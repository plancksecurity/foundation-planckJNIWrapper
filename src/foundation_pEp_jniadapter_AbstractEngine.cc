#include "foundation_pEp_jniadapter_AbstractEngine.h"
#include <unistd.h>
#include <pEp/keymanagement.h>
#include <pEp/message_api.h>
#include <pEp/sync_api.h>
#include <pEp/Adapter.hh>
#include <pEp/utils.hh>
#include "throw_pEp_exception.hh"
#include "jniutils.hh"

namespace pEp {
using namespace pEp::JNIAdapter;
using namespace utility;   // for libpEpAdapter locked queue impl.  TODO:rename

bool first = true;

JavaVM *jvm= nullptr;

std::mutex mutex_obj;

jfieldID field_value = nullptr;
jmethodID messageConstructorMethodID = nullptr;
jmethodID messageToSendMethodID = nullptr;
jmethodID notifyHandShakeMethodID = nullptr;
jmethodID needsFastPollMethodID = nullptr;
jmethodID method_values = nullptr;

jobject objj = nullptr;

jclass messageClass = nullptr;
jclass identityClass = nullptr;;
jclass signalClass = nullptr;
jclass engineClass = nullptr;

class JNISync {
public:
    JNIEnv * env()
    {
        pEpLog("called");
        JNIEnv *thread_env = nullptr;
        int status = jvm->GetEnv((void**)&thread_env, JNI_VERSION_1_6);
        if (status < 0) {
#ifdef ANDROID
            status = jvm->AttachCurrentThread(&thread_env, nullptr);
#else
            status = jvm->AttachCurrentThread((void **) &thread_env, nullptr);
#endif
        }
        assert(status >= 0);
        return thread_env;
    }

    void onSyncStartup()
    {
        pEpLog("called");
        env();
    }

    void onSyncShutdown()
    {
        pEpLog("called");
        jvm->DetachCurrentThread();
    }
} o;

void jni_init() {
    pEpLog("called");
    JNIEnv *_env = o.env();

    messageClass = reinterpret_cast<jclass>(
            _env->NewGlobalRef(findClass(_env, "foundation/pEp/jniadapter/Message")));
    identityClass = reinterpret_cast<jclass>(
        _env->NewGlobalRef(findClass(_env, "foundation/pEp/jniadapter/_Identity")));
    signalClass = reinterpret_cast<jclass>(
            _env->NewGlobalRef(findClass(_env, "foundation/pEp/jniadapter/SyncHandshakeSignal")));
    engineClass = reinterpret_cast<jclass>(_env->NewGlobalRef(findClass(_env, "foundation/pEp/jniadapter/Engine")));

    messageConstructorMethodID = _env->GetMethodID(messageClass, "<init>", "(J)V");
    messageToSendMethodID = _env->GetMethodID(
        engineClass,
        "messageToSendCallFromC",
        "(Lfoundation/pEp/jniadapter/Message;)I");
    needsFastPollMethodID = _env->GetMethodID(
        engineClass,
        "needsFastPollCallFromC",
        "(Z)I");
    notifyHandShakeMethodID = _env->GetMethodID(
        engineClass,
        "notifyHandshakeCallFromC",
        "(Lfoundation/pEp/jniadapter/_Identity;Lfoundation/pEp/jniadapter/_Identity;Lfoundation/pEp/jniadapter/SyncHandshakeSignal;)I");

    method_values = o.env()->GetStaticMethodID(signalClass, "values",
                "()[Lfoundation/pEp/jniadapter/SyncHandshakeSignal;");
    field_value = o.env()->GetFieldID(signalClass, "value", "I");
}

PEP_STATUS messageToSend(message *msg)
{
    pEpLog("called");
    std::lock_guard<std::mutex> l(mutex_obj);

    pEpLog("############### messageToSend() called");
    jobject msg_ = nullptr;

    assert(messageClass && messageConstructorMethodID && objj && messageToSendMethodID);

    msg_ = o.env()->NewObject(messageClass, messageConstructorMethodID, (jlong) msg);

    PEP_STATUS status = (PEP_STATUS) o.env()->CallIntMethod(objj, messageToSendMethodID, msg_);
    if (o.env()->ExceptionCheck()) {
        o.env()->ExceptionDescribe();
        status = PEP_UNKNOWN_ERROR;
        o.env()->ExceptionClear();
    }


    return status;
}

PEP_STATUS notifyHandshake(pEp_identity *me, pEp_identity *partner, sync_handshake_signal signal)
{
    pEpLog("called");
    std::lock_guard<std::mutex> l(mutex_obj);

    pEpLog("############### notifyHandshake() called");
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
            o.env() -> DeleteLocalRef(element);
        }
    }

    assert(objj && notifyHandShakeMethodID);

    PEP_STATUS status = (PEP_STATUS) o.env()->CallIntMethod(objj, notifyHandShakeMethodID, me_, partner_, signal_);
    if (o.env()->ExceptionCheck()) {
        o.env()->ExceptionClear();
        return PEP_UNKNOWN_ERROR;
    }

    return status;
}
}

extern "C" {
using namespace pEp;

JNIEXPORT void JNICALL Java_foundation_pEp_jniadapter_AbstractEngine_init(
        JNIEnv *env,
        jobject me
    )
{
    pEpLog("called");
    std::lock_guard<std::mutex> l(global_mutex); // global mutex for write access to <unordered_map>

    if (first) {
        pEpLog("first Engine instance");
        first = false;
        env->GetJavaVM(&jvm);
        jni_init();
        objj = env->NewGlobalRef(me);
        Adapter::_messageToSend = messageToSend;
    }

    create_engine_java_object_mutex(env, me);  // Create a mutex per java object
    Adapter::session();
}

JNIEXPORT void JNICALL Java_foundation_pEp_jniadapter_AbstractEngine_release(
        JNIEnv *env,
        jobject me
    )
{
    pEpLog("called");
    std::lock_guard<std::mutex> l(global_mutex);  // global mutex for write access to <unordered_map>
    release_engine_java_object_mutex(env, me);
    Adapter::session(pEp::Adapter::release);
}

JNIEXPORT jstring JNICALL Java_foundation_pEp_jniadapter_AbstractEngine_getVersion(
        JNIEnv *env,
        jobject me
    )
{
    pEpLog("called with lock_guard");
    std::lock_guard<std::mutex> l(*get_engine_java_object_mutex(env, me));

    return env->NewStringUTF(::get_engine_version());
}

JNIEXPORT jstring JNICALL Java_foundation_pEp_jniadapter_AbstractEngine_getProtocolVersion(
        JNIEnv *env,
        jobject me
    )
{
    pEpLog("called with lock_guard");
    std::lock_guard<std::mutex> l(*get_engine_java_object_mutex(env, me));

    return env->NewStringUTF(::get_protocol_version());
}

int examine_identity(pEp_identity *ident, void *arg)
{
    pEpLog("called");
    locked_queue< pEp_identity * > *queue = (locked_queue< pEp_identity * > *) arg;
    queue->push_back(identity_dup(ident));
    return 0;
}

pEp_identity *retrieve_next_identity(void *arg)
{
    pEpLog("called");
    locked_queue< pEp_identity * > *queue = (locked_queue< pEp_identity * > *) arg;

    while (!queue->size())
        usleep(100000);

    pEp_identity *ident = queue->front();
    queue->pop_front();
    return ident;
}

static void *keyserver_thread_routine(void *arg)
{
    pEpLog("called");
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

JNIEXPORT void JNICALL Java_foundation_pEp_jniadapter_AbstractEngine_startKeyserverLookup(
        JNIEnv *env,
        jobject me
    )
{
    pEpLog("called with lock_guard");
    std::lock_guard<std::mutex> l(*get_engine_java_object_mutex(env, me));

    pthread_t *thread = nullptr;
    locked_queue< pEp_identity * > *queue = nullptr;

    jfieldID thread_handle;
    jfieldID queue_handle;

    try {
        thread_handle = getFieldID(env, "foundation/pEp/jniadapter/Engine", "keyserverThread", "J");
        queue_handle = getFieldID(env, "foundation/pEp/jniadapter/Engine", "keyserverQueue", "J");
    }
    catch (std::exception& ex) {
        assert(0);
        return;
    }

    thread = (pthread_t *) env->GetLongField(me, thread_handle);
    if (thread)
        return;

    thread = (pthread_t *) calloc(1, sizeof(pthread_t));
    assert(thread);
    env->SetLongField(me, thread_handle, (jlong) thread);

    queue = new locked_queue< pEp_identity * >();
    env->SetLongField(me, queue_handle, (jlong) queue);

    register_examine_function(Adapter::session(), examine_identity, (void *) queue);

    pthread_create(thread, nullptr, keyserver_thread_routine, (void *) queue);
}

JNIEXPORT void JNICALL Java_foundation_pEp_jniadapter_AbstractEngine_stopKeyserverLookup(
        JNIEnv *env,
        jobject me
    )
{
    pEpLog("called with lock_guard");
    std::lock_guard<std::mutex> l(*get_engine_java_object_mutex(env, me));

    pthread_t *thread = nullptr;
    locked_queue< pEp_identity * > *queue = nullptr;

    jfieldID thread_handle;
    jfieldID queue_handle;

    try {
        thread_handle = getFieldID(env, "foundation/pEp/jniadapter/Engine", "keyserverThread", "J");
        queue_handle = getFieldID(env, "foundation/pEp/jniadapter/Engine", "keyserverQueue", "J");
    }
    catch (std::exception& ex) {
        assert(0);
        return;
    }

    thread = (pthread_t *) env->GetLongField(me, thread_handle);
    if (!thread)
        return;

    queue = (locked_queue< pEp_identity * > *) env->GetLongField(me, queue_handle);

    env->SetLongField(me, queue_handle, (jlong) 0);
    env->SetLongField(me, thread_handle, (jlong) 0);

    register_examine_function(Adapter::session(), nullptr, nullptr);

    queue->push_front(nullptr);
    pthread_join(*thread, nullptr);
    free(thread);
}

JNIEXPORT void JNICALL Java_foundation_pEp_jniadapter_AbstractEngine_startSync(
        JNIEnv *env,
        jobject me
    )
{
    pEpLog("called with lock_guard");
    std::lock_guard<std::mutex> l(*get_engine_java_object_mutex(env, me));

    pEpLog("######## starting sync");
    try {
        Adapter::startup<JNISync>(messageToSend, notifyHandshake, &o, &JNISync::onSyncStartup, &JNISync::onSyncShutdown);
    }
    catch (RuntimeError& ex) {
        throw_pEp_Exception(env, ex.status);
        return;
    }
}

JNIEXPORT void JNICALL Java_foundation_pEp_jniadapter_AbstractEngine_stopSync(
        JNIEnv *env,
        jobject me
    )
{
    pEpLog("called with lock_guard");
    std::lock_guard<std::mutex> l(*get_engine_java_object_mutex(env, me));

    Adapter::shutdown();
}

JNIEXPORT jboolean JNICALL Java_foundation_pEp_jniadapter_AbstractEngine_isSyncRunning(
        JNIEnv *env,
        jobject me
    )
{
    pEpLog("called with lock_guard");
    std::lock_guard<std::mutex> l(*get_engine_java_object_mutex(env, me));

    return (jboolean) Adapter::is_sync_running();
}

} // extern "C"

