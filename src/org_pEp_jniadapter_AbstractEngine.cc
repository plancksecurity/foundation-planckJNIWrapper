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
    static JavaVM* sync_jvm = NULL;

    // Called by sync thread only
    PEP_STATUS show_handshake(void *obj, pEp_identity *me, pEp_identity *partner)
    {
        JNIEnv* env;
        jobject me_ = NULL;
        jobject partner_ = NULL;

        sync_jvm->AttachCurrentThread(&env, NULL);

        jclass clazz = env->GetObjectClass(sync_obj);
        jmethodID methodID = env->GetMethodID(
            clazz, 
            "showHandshakeCallFromC", 
            "(Lorg/pEp/jniadapter/_Identity;Lorg/pEp/jniadapter/_Identity;)I");

        env->DeleteLocalRef(clazz);

        me_ = from_identity(env, me);
        partner_ = from_identity(env, partner);

        jint result = env->CallIntMethod(sync_obj, methodID, me_, partner_);

        return (PEP_STATUS) result;
    }

    // Called by sync thread only
    PEP_STATUS message_to_send(void *obj, message *msg)
    {
        JNIEnv* env;
        jobject msg_ = NULL;

        sync_jvm->AttachCurrentThread(&env, NULL);

        jclass clazz = env->GetObjectClass(sync_obj);
        jmethodID methodID = env->GetMethodID(
            clazz, 
            "messageToSendCallFromC", 
            "(Lorg/pEp/jniadapter/Message;)I");

        env->DeleteLocalRef(clazz);

        jclass clazz_msg_ = findClass(env, "org/pEp/jniadapter/Message");
        assert(clazz_msg_);
        jmethodID constructor_msg_ = env->GetMethodID(clazz_msg_, "<init>", "(J)V");
        assert(constructor_msg_);
        msg_ = env->NewObject(clazz_msg_, constructor_msg_, (jlong) msg);

        jint result = env->CallIntMethod(sync_obj, methodID, msg_);

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

    typedef struct _sync_thread_arg {
        PEP_SESSION session;
        locked_queue< sync_msg_t * > *queue;
    } sync_thread_arg;


    static void *sync_thread_routine(void *arg)
    {
        sync_thread_arg *a = (sync_thread_arg*)arg;
        PEP_SESSION session = (PEP_SESSION) a->session;

        PEP_STATUS status = do_sync_protocol(session, a->queue);

        locked_queue< sync_msg_t * > *queue = (locked_queue< sync_msg_t * > *) arg;

        while (queue->size()) {
            sync_msg_t *msg = queue->front();
            queue->pop_front();
            free_sync_msg(msg);
        }

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
        env->GetJavaVM(&sync_jvm);
        sync_obj = env->NewGlobalRef(obj);

        register_sync_callbacks(session,
                                (void *) queue,
                                message_to_send,
                                show_handshake, 
                                inject_sync_msg,
                                retrieve_next_sync_msg);

        sync_thread_arg *a = (sync_thread_arg*) malloc(sizeof(sync_thread_arg));
        assert(a);
        a->session = session;
        a->queue = queue; 
        
        sync_session = session;

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


