#include "org_pEp_jniadapter_AbstractEngine.h"

#ifndef NDEBUG
#include <iostream>
auto& debug_log = std::cerr;
#else
// the compiler should optimize this away
static struct _debug_log {
    _debug_log& operator<<(const char*) { return *this; }
    _debug_log& operator<<(int) { return *this; }
    _debug_log& operator<<(double) { return *this; }
} debug_log;
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

#ifdef ANDROID
#define ATTACH_CURRENT_THREAD(env, args) jvm->AttachCurrentThread(&env, args);
#else
#define ATTACH_CURRENT_THREAD(env, args) jvm->AttachCurrentThread((void **) &env, args);
#endif

namespace pEp {
    using namespace pEp::JNIAdapter;
    using namespace pEp::Adapter;
    using namespace utility;

    bool first = true;

    JavaVM *jvm= nullptr;

    std::mutex mutex_obj;

    jfieldID field_value = nullptr;
    jmethodID messageConstructorMethodID = nullptr;
    jmethodID messageToSendMethodID = nullptr;
    jmethodID notifyHandShakeMethodID = nullptr;
    jmethodID needsFastPollMethodID = nullptr; 
    jmethodID method_values = nullptr;

    jobject obj = nullptr;

    jclass messageClass = nullptr;
    jclass identityClass = nullptr;;
    jclass signalClass = nullptr;
    jclass engineClass = nullptr;

    class JNISync {
    public:
        JNIEnv * env()
        {
            JNIEnv *thread_env = nullptr;
            int status = jvm->GetEnv((void**)&thread_env, JNI_VERSION_1_6);
            if (status < 0)
                status = ATTACH_CURRENT_THREAD(thread_env, nullptr);
            assert(status >= 0);
            return thread_env;
        }

        void startup_sync()
        {

        }

        void shutdown_sync()
        {
            jvm->DetachCurrentThread();
        }
    } o;

    void jni_init() {
        JNIEnv *_env = o.env();

        messageClass = reinterpret_cast<jclass>(
                _env->NewGlobalRef(findClass(_env, "org/pEp/jniadapter/Message")));
        identityClass = reinterpret_cast<jclass>(
            _env->NewGlobalRef(findClass(_env, "org/pEp/jniadapter/_Identity")));
        signalClass = reinterpret_cast<jclass>(
                _env->NewGlobalRef(findClass(_env, "org/pEp/jniadapter/SyncHandshakeSignal")));
        engineClass = reinterpret_cast<jclass>(_env->NewGlobalRef(findClass(_env, "org/pEp/jniadapter/Engine")));

        messageConstructorMethodID = _env->GetMethodID(messageClass, "<init>", "(J)V");
        messageToSendMethodID = _env->GetMethodID(
            engineClass,
            "messageToSendCallFromC", 
            "(Lorg/pEp/jniadapter/Message;)I");
        needsFastPollMethodID = _env->GetMethodID(
            engineClass,
            "needsFastPollCallFromC",
            "(Z)I");
        notifyHandShakeMethodID = _env->GetMethodID(
            engineClass,
            "notifyHandshakeCallFromC",
            "(Lorg/pEp/jniadapter/_Identity;Lorg/pEp/jniadapter/_Identity;Lorg/pEp/jniadapter/SyncHandshakeSignal;)I");

        method_values = o.env()->GetStaticMethodID(signalClass, "values",
                    "()[Lorg/pEp/jniadapter/SyncHandshakeSignal;");
        field_value = o.env()->GetFieldID(signalClass, "value", "I");
    }

    PEP_STATUS messageToSend(message *msg)
    {
        std::lock_guard<std::mutex> l(mutex_obj);

        debug_log << "\n############### messageToSend() called\n";
        jobject msg_ = nullptr;

        assert(messageClass && messageConstructorMethodID && obj && messageToSendMethodID);

        msg_ = o.env()->NewObject(messageClass, messageConstructorMethodID, (jlong) msg);

        PEP_STATUS status = (PEP_STATUS) o.env()->CallIntMethod(obj, messageToSendMethodID, msg_);
        if (o.env()->ExceptionCheck()) {
            status = PEP_UNKNOWN_ERROR;
            o.env()->ExceptionClear();
        }

        return status;
    }

    PEP_STATUS notifyHandshake(pEp_identity *me, pEp_identity *partner, sync_handshake_signal signal)
    {
        std::lock_guard<std::mutex> l(mutex_obj);

        debug_log << "\n############### notifyHandshake() called\n";
        jobject me_ = nullptr;
        jobject partner_ = nullptr;

        me_ = from_identity(o.env(), me, identityClass);
        partner_ = from_identity(o.env(), partner, identityClass);

        jobject signal_ = nullptr;
        {
            assert(signalClass);
            assert(method_values);
            assert(field_value);
        
            jobjectArray values = (jobjectArray) o.env()->CallStaticObjectMethod(signalClass,
                    method_values);
            if (o.env()->ExceptionCheck()) {
                o.env()->ExceptionClear();
                return PEP_UNKNOWN_ERROR;
            }
        
            jsize values_size = o.env()->GetArrayLength(values);
            for (jsize i = 0; i < values_size; i++) {
                jobject element = o.env()->GetObjectArrayElement(values, i);
                assert(element);
                jint value = o.env()->GetIntField(element, field_value);
                if (value == (jint) signal) {
                    signal_ = element;
                    break;
                }
            }
        }

        assert(obj && notifyHandShakeMethodID);

        PEP_STATUS status = (PEP_STATUS) o.env()->CallIntMethod(obj, notifyHandShakeMethodID, me_, partner_, signal_);
        if (o.env()->ExceptionCheck()) {
            o.env()->ExceptionClear();
            return PEP_UNKNOWN_ERROR;
        }

        return status;
    }
}

extern "C" {
    using namespace pEp;

    JNIEXPORT void JNICALL Java_org_pEp_jniadapter_AbstractEngine_init(
            JNIEnv *env,
            jobject me
        )
    {
        if (first) {
            env->GetJavaVM(&jvm);
            jni_init();
            obj = env->NewGlobalRef(me);
        }

#ifdef DISABLE_SYNC
        _messageToSend = messageToSend;
        session();
#else 
        if (first) {
            debug_log << "######## starting sync\n";
            startup<JNISync>(messageToSend, notifyHandshake, &o, &JNISync::startup_sync, &JNISync::shutdown_sync);
        }
#endif
        first = false;
    }

    JNIEXPORT void JNICALL Java_org_pEp_jniadapter_AbstractEngine_release(
            JNIEnv *env,
            jobject me
        )
    {
        session(pEp::Adapter::release);
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

