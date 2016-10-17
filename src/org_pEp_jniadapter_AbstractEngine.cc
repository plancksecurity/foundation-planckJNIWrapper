#include "org_pEp_jniadapter_AbstractEngine.h"

#include <stdexcept>
#include <unistd.h>
#include <assert.h>
#include <pthread.h>
#include <pEp/keymanagement.h>
#include <pEp/message_api.h>
#include <pEp/sync.h>

#include "throw_pEp_exception.hh"
#include "jniutils.hh"

extern "C" {
    using namespace pEp::JNIAdapter;
    using namespace pEp::utility;

    int inject_sync_msg(void *msg, void *arg);
    static PEP_SESSION sync_session = NULL;

    JNIEXPORT void JNICALL Java_org_pEp_jniadapter_AbstractEngine_init(
            JNIEnv *env,
            jobject me
        )
    {
        PEP_SESSION session = NULL;
        jfieldID handle;

        PEP_STATUS status = init(&session);
        assert(status == PEP_STATUS_OK);

        if (status != PEP_STATUS_OK) {
            throw_pEp_Exception(env, status);
            return;
        }

        assert(session);

        if(sync_session != NULL){
            status = attach_sync_session(session, sync_session);
            if (status != PEP_STATUS_OK) {
                throw_pEp_Exception(env, status);
                return;
            }
        }
        
        try {
            handle = getFieldID(env, "org/pEp/jniadapter/Engine", "handle", "J");
        }
        catch (std::exception& ex) {
            assert(0);
            return;
        }

        jlong _session = (jlong) session;
        env->SetLongField(me, handle, _session);
    }

    JNIEXPORT void JNICALL Java_org_pEp_jniadapter_AbstractEngine_release(
            JNIEnv *env,
            jobject me
        )
    {
        PEP_SESSION session = NULL;
        jfieldID handle;

        try {
            handle = getFieldID(env, "org/pEp/jniadapter/Engine", "handle", "J");
        }
        catch (std::exception& ex) {
            assert(0);
            return;
        }

        session = (PEP_SESSION) env->GetLongField(me, handle);
        if (session){
            release(session);
            detach_sync_session(session);
        }
        else
            env->SetLongField(me, handle, jlong(0));
    }

    int examine_identity(pEp_identity *ident, void *arg)
    {
        locked_queue< pEp_identity * > *queue = (locked_queue< pEp_identity * > *) arg;
        queue->push_back(identity_dup(ident));
        return 0;
    }

    pEp_identity *retrieve_next_identity(void *arg)
    {
        locked_queue< pEp_identity * > *queue = (locked_queue< pEp_identity * > *) arg;

        while (!queue->size())
            usleep(100000);

        pEp_identity *ident = queue->front();
        queue->pop_front();
        return ident;
    }

    static void *keyserver_thread_routine(void *arg)
    {
        PEP_STATUS status = do_keymanagement(retrieve_next_identity, arg);

        locked_queue< pEp_identity * > *queue = (locked_queue< pEp_identity * > *) arg;

        while (queue->size()) {
            pEp_identity *ident = queue->front();
            queue->pop_front();
            free_identity(ident);
        }

        delete queue;

        return (void *) status;
    }

    JNIEXPORT void JNICALL Java_org_pEp_jniadapter_AbstractEngine_startKeyserverLookup(
            JNIEnv *env,
            jobject obj
        )
    {
        PEP_SESSION session = (PEP_SESSION) callLongMethod(env, obj, "getHandle");

        pthread_t *thread = NULL;
        locked_queue< pEp_identity * > *queue = NULL;

        jfieldID thread_handle;
        jfieldID queue_handle;

        try {
            thread_handle = getFieldID(env, "org/pEp/jniadapter/Engine", "keyserverThread", "J");
            queue_handle = getFieldID(env, "org/pEp/jniadapter/Engine", "keyserverQueue", "J");
        }
        catch (std::exception& ex) {
            assert(0);
            return;
        }

        thread = (pthread_t *) env->GetLongField(obj, thread_handle);
        if (thread)
            return;
 
        thread = (pthread_t *) calloc(1, sizeof(pthread_t));
        assert(thread);
        env->SetLongField(obj, thread_handle, (jlong) thread);

        queue = new locked_queue< pEp_identity * >();
        env->SetLongField(obj, queue_handle, (jlong) queue);

        register_examine_function(session, examine_identity, (void *) queue);

        pthread_create(thread, NULL, keyserver_thread_routine, (void *) queue);
    }

    JNIEXPORT void JNICALL Java_org_pEp_jniadapter_AbstractEngine_stopKeyserverLookup(
            JNIEnv *env,
            jobject obj
        )
    {
        PEP_SESSION session = (PEP_SESSION) callLongMethod(env, obj, "getHandle");

        pthread_t *thread = NULL;
        locked_queue< pEp_identity * > *queue = NULL;

        jfieldID thread_handle;
        jfieldID queue_handle;

        try {
            thread_handle = getFieldID(env, "org/pEp/jniadapter/Engine", "keyserverThread", "J");
            queue_handle = getFieldID(env, "org/pEp/jniadapter/Engine", "keyserverQueue", "J");
        }
        catch (std::exception& ex) {
            assert(0);
            return;
        }

        thread = (pthread_t *) env->GetLongField(obj, thread_handle);
        if (!thread)
            return;
 
        queue = (locked_queue< pEp_identity * > *) env->GetLongField(obj, queue_handle);

        env->SetLongField(obj, queue_handle, (jlong) 0);
        env->SetLongField(obj, thread_handle, (jlong) 0);

        register_examine_function(session, NULL, NULL);

        queue->push_front(NULL);
        pthread_join(*thread, NULL);
        free(thread);
    }

    /////////////////////////////////////////////////////////////////////////
    // Sync message callbacks, queue, and thread
    /////////////////////////////////////////////////////////////////////////

    static jobject sync_obj = NULL;
    static JNIEnv* sync_env = NULL;
    static jmethodID showHandShakeMethodID = NULL; 
    static jmethodID messageToSendMethodID = NULL;
    static jclass messageClass = NULL;
    static jmethodID messageConstructorMethodID = NULL;

    // Called by sync thread only
    PEP_STATUS show_handshake(void *obj, pEp_identity *me, pEp_identity *partner)
    {
        jobject me_ = NULL;
        jobject partner_ = NULL;

        me_ = from_identity(sync_env, me);
        partner_ = from_identity(sync_env, partner);

        jint result = sync_env->CallIntMethod(sync_obj, showHandShakeMethodID, me_, partner_);

        return (PEP_STATUS) result;
    }

    // Called by sync thread only
    PEP_STATUS message_to_send(void *obj, message *msg)
    {
        jobject msg_ = NULL;

        msg_ = sync_env->NewObject(messageClass, messageConstructorMethodID, (jlong) msg);

        jint result = sync_env->CallIntMethod(sync_obj, messageToSendMethodID, msg_);

        return (PEP_STATUS) result;
    }

    // called indirectly by decrypt message 
    int inject_sync_msg(void *msg, void *arg)
    {
        if(arg == NULL)
            return 1;

        locked_queue< sync_msg_t * > *queue = (locked_queue< sync_msg_t * > *) arg;

        queue->push_back((sync_msg_t *)msg);
        return 0;
    }

    void *retrieve_next_sync_msg(void *arg)
    {
        locked_queue< sync_msg_t * > *queue = (locked_queue< sync_msg_t * > *) arg;

        while (!queue->size())
            // TODO: add blocking dequeue 
            usleep(100000);

        void *msg = queue->front();
        queue->pop_front();
        return msg;
    }

    typedef struct _sync_thread_arg_t {
        PEP_SESSION session;
        locked_queue< sync_msg_t * > *queue;
        JavaVM* sync_jvm;
    } sync_thread_arg_t;


    static void *sync_thread_routine(void *arg)
    {
        sync_thread_arg_t *a = (sync_thread_arg_t*)arg;
        PEP_SESSION session = (PEP_SESSION) a->session;

        a->sync_jvm->AttachCurrentThread(&sync_env, NULL);

        jclass clazz = sync_env->GetObjectClass(sync_obj);

        showHandShakeMethodID = sync_env->GetMethodID(
            clazz, 
            "showHandshakeCallFromC", 
            "(Lorg/pEp/jniadapter/_Identity;Lorg/pEp/jniadapter/_Identity;)I");
        assert(showHandShakeMethodID);

        messageToSendMethodID = sync_env->GetMethodID(
            clazz, 
            "messageToSendCallFromC", 
            "(Lorg/pEp/jniadapter/Message;)I");
        assert(messageToSendMethodID);

        sync_env->DeleteLocalRef(clazz);

        messageClass = findClass(sync_env, "org/pEp/jniadapter/Message");
        assert(messageClass);

        messageConstructorMethodID = sync_env->GetMethodID(messageClass, "<init>", "(J)V");
        assert(messageConstructorMethodID);

        PEP_STATUS status = do_sync_protocol(session, a->queue);

        locked_queue< sync_msg_t * > *queue = (locked_queue< sync_msg_t * > *) arg;

        while (queue->size()) {
            sync_msg_t *msg = queue->front();
            queue->pop_front();
            free_sync_msg(msg);
        }

        sync_env->DeleteLocalRef(messageClass);

        a->sync_jvm->DetachCurrentThread();

        delete queue;
        free(a);

        return (void *) status;
    }

    JNIEXPORT void JNICALL Java_org_pEp_jniadapter_AbstractEngine_startSync(
            JNIEnv *env,
            jobject obj
        )
    {
        PEP_SESSION session = (PEP_SESSION) callLongMethod(env, obj, "getHandle");

        pthread_t *thread = NULL;
        locked_queue< sync_msg_t * > *queue = NULL;

        jfieldID thread_handle;
        jfieldID queue_handle;

        try {
            thread_handle = getFieldID(env, "org/pEp/jniadapter/Engine", "syncThread", "J");
            queue_handle = getFieldID(env, "org/pEp/jniadapter/Engine", "syncQueue", "J");
        }
        catch (std::exception& ex) {
            assert(0);
            return;
        }

        thread = (pthread_t *) env->GetLongField(obj, thread_handle);
        if (thread)
            return;
 
        thread = (pthread_t *) calloc(1, sizeof(pthread_t));
        assert(thread);
        env->SetLongField(obj, thread_handle, (jlong) thread);

        queue = new locked_queue< sync_msg_t * >();
        env->SetLongField(obj, queue_handle, (jlong) queue);

        // for callbacks
        sync_obj = env->NewGlobalRef(obj);
        sync_thread_arg_t *a = (sync_thread_arg_t*) malloc(sizeof(sync_thread_arg_t));
        assert(a);
        a->session = session;
        a->queue = queue; 
        env->GetJavaVM(&a->sync_jvm);
        
        sync_session = session;

        register_sync_callbacks(session,
                                (void *) queue,
                                message_to_send,
                                show_handshake, 
                                inject_sync_msg,
                                retrieve_next_sync_msg);


        pthread_create(thread, NULL, sync_thread_routine, (void *) a);
    }

    JNIEXPORT void JNICALL Java_org_pEp_jniadapter_AbstractEngine_stopSync(
            JNIEnv *env,
            jobject obj
        )
    {
        PEP_SESSION session = (PEP_SESSION) callLongMethod(env, obj, "getHandle");

        pthread_t *thread = NULL;
        locked_queue< sync_msg_t * > *queue = NULL;

        jfieldID thread_handle;
        jfieldID queue_handle;

        try {
            thread_handle = getFieldID(env, "org/pEp/jniadapter/Engine", "syncThread", "J");
            queue_handle = getFieldID(env, "org/pEp/jniadapter/Engine", "syncQueue", "J");
        }
        catch (std::exception& ex) {
            assert(0);
            return;
        }

        thread = (pthread_t *) env->GetLongField(obj, thread_handle);
        if (!thread)
            return;
 
        queue = (locked_queue< sync_msg_t * > *) env->GetLongField(obj, queue_handle);

        env->SetLongField(obj, queue_handle, (jlong) 0);
        env->SetLongField(obj, thread_handle, (jlong) 0);

        sync_session = NULL;

        unregister_sync_callbacks(session);

        sync_obj = NULL;

        queue->push_front(NULL);
        pthread_join(*thread, NULL);
        free(thread);
    }
} // extern "C"


