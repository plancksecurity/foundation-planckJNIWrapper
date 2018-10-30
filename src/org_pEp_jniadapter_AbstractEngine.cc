#include "org_pEp_jniadapter_AbstractEngine.h"

#ifndef ANDROID
#ifndef NDEBUG
#include <fstream>
std::ofstream debug_log("debug.log");
#endif
#endif

#include <stdexcept>
#include <unistd.h>
#include <assert.h>
#include <pthread.h>
#include <pEp/keymanagement.h>
#include <pEp/message_api.h>
#include <pEp/sync_api.h>
#include <pEp/Adapter.hh>

#include "throw_pEp_exception.hh"
#include "jniutils.hh"

namespace pEp {
    using namespace pEp::JNIAdapter;
    using namespace pEp::Adapter;
    using namespace utility;

    JavaVM *jvm= nullptr;
    thread_local JNIEnv *thread_env = nullptr;
    JNIEnv *first_env = nullptr;
    jobject obj = nullptr;
    jclass _clazz = nullptr;

    jclass messageClass = nullptr;
    jclass identityClass = nullptr;
    jclass signalClass = nullptr;
    jclass engineClass = nullptr;

    jmethodID messageConstructorMethodID = nullptr;
    jmethodID messageToSendMethodID = nullptr;
    jmethodID notifyHandShakeMethodID = nullptr;
    jmethodID needsFastPollMethodID = nullptr; 

    class JNISync {
    public:
        JNIEnv * env()
        {
            JNIEnv *_env;

            if (on_sync_thread()) {
                _env = first_env;
            }
            else {
                if (!thread_env) {
                    #ifdef ANDROID
                    jvm->AttachCurrentThread(&thread_env, nullptr);
                    #else
                    jvm->AttachCurrentThread((void **) &thread_env, nullptr);
                    #endif
                }
                _env = thread_env;
            }

            return _env;
        }

        void startup_sync()
        {
            needsFastPollMethodID = env()->GetMethodID(
                _clazz,
                "needsFastPollCallFromC",
                "(Z)I");
            assert(needsFastPollMethodID);

            notifyHandShakeMethodID = env()->GetMethodID(
                _clazz,
                "notifyHandshakeCallFromC",
                "(Lorg/pEp/jniadapter/_Identity;Lorg/pEp/jniadapter/_Identity;Lorg/pEp/jniadapter/SyncHandshakeSignal;)I");
            assert(notifyHandShakeMethodID);
        }

        void shutdown_sync()
        {
            env()->DeleteLocalRef(messageClass);
            jvm->DetachCurrentThread();
        }
    };

    JNISync *o = nullptr;

    PEP_STATUS messageToSend(message *msg)
    {
        jobject msg_ = nullptr;
        jint result = 0;

        msg_ = o->env()->NewObject(messageClass, messageConstructorMethodID, (jlong) msg);
        result = o->env()->CallIntMethod(obj, messageToSendMethodID, msg_);

        return (PEP_STATUS) result;
    }

    PEP_STATUS notifyHandshake(pEp_identity *me, pEp_identity *partner, sync_handshake_signal signal)
    {
        jobject me_ = nullptr;
        jobject partner_ = nullptr;

        me_ = from_identity(o->env(), me, identityClass);
        partner_ = from_identity(o->env(), partner, identityClass);

        jobject signal_ = nullptr;
        {
            assert(signalClass);
            jmethodID method_values = o->env()->GetStaticMethodID(signalClass, "values",
                    "()[Lorg/pEp/jniadapter/SyncHandshakeSignal;");
            assert(method_values);
            jfieldID field_value = o->env()->GetFieldID(signalClass, "value", "I");
            assert(field_value);
        
            jobjectArray values = (jobjectArray) o->env()->CallStaticObjectMethod(signalClass,
                    method_values);
            assert(values);
        
            jsize values_size = o->env()->GetArrayLength(values);
            for (jsize i = 0; i < values_size; i++) {
                jobject element = o->env()->GetObjectArrayElement(values, i);
                assert(element);
                jint value = o->env()->GetIntField(element, field_value);
                if (value == (jint) signal) {
                    signal_ = element;
                    break;
                }
            }
        }

        jint result = o->env()->CallIntMethod(obj, notifyHandShakeMethodID, me_, partner_, signal_);

        return (PEP_STATUS) result;
    }
}

extern "C" {
    using namespace pEp;

    JNIEXPORT void JNICALL Java_org_pEp_jniadapter_AbstractEngine_init(
            JNIEnv *env,
            jobject me
        )
    {
        if (!first_env)
            first_env = env;

        env->GetJavaVM(&jvm);
        thread_env = env;
        obj = me;
        _clazz = env->GetObjectClass(obj);

        if (!o)
            o = new JNISync();

        if (!messageClass)
            messageClass = reinterpret_cast<jclass>(env->NewGlobalRef(findClass(env, "org/pEp/jniadapter/Message")));

        if (!identityClass)
            identityClass = reinterpret_cast<jclass>(env->NewGlobalRef(findClass(env, "org/pEp/jniadapter/_Identity")));

        if (!signalClass)
            signalClass = reinterpret_cast<jclass>(env->NewGlobalRef(findClass(env, "org/pEp/jniadapter/SyncHandshakeSignal")));

        if (!engineClass)
            engineClass = reinterpret_cast<jclass>(env->NewGlobalRef(findClass(env, "org/pEp/jniadapter/Engine")));

        if (!messageConstructorMethodID)
            messageConstructorMethodID = env->GetMethodID(messageClass, "<init>", "(J)V");

        if (!messageToSendMethodID) {
            messageToSendMethodID = env->GetMethodID(
                engineClass,
                "messageToSendCallFromC", 
                "(Lorg/pEp/jniadapter/Message;)I");
            assert(messageToSendMethodID);
        }

        startup<JNISync>(messageToSend, notifyHandshake, o, &JNISync::startup_sync, &JNISync::shutdown_sync);
    }

    JNIEXPORT void JNICALL Java_org_pEp_jniadapter_AbstractEngine_release(
            JNIEnv *env,
            jobject me
        )
    {
        shutdown();
        env->DeleteLocalRef(_clazz);
        session(pEp::Adapter::release);
        delete o;
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
        pthread_t *thread = nullptr;
        locked_queue< pEp_identity * > *queue = nullptr;

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

        register_examine_function(session(), examine_identity, (void *) queue);

        pthread_create(thread, nullptr, keyserver_thread_routine, (void *) queue);
    }

    JNIEXPORT void JNICALL Java_org_pEp_jniadapter_AbstractEngine_stopKeyserverLookup(
            JNIEnv *env,
            jobject obj
        )
    {
        pthread_t *thread = nullptr;
        locked_queue< pEp_identity * > *queue = nullptr;

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

        register_examine_function(session(), nullptr, nullptr);

        queue->push_front(nullptr);
        pthread_join(*thread, nullptr);
        free(thread);
    }

} // extern "C"

