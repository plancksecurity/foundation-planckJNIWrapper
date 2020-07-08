#include "foundation_pEp_jniadapter_AbstractEngine.h"
#include <unistd.h>
#include <pEp/keymanagement.h>
#include <pEp/message_api.h>
#include <pEp/sync_api.h>
#include <pEp/Adapter.hh>
#include <pEp/pEpLog.hh>
#include <pEp/passphrase_cache.hh>
#include <pEp/callback_dispatcher.hh>
#include "throw_pEp_exception.hh"
#include "jniutils.hh"
#include "passphrase_callback.hh"

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
jmethodID passphraseRequiredMethodID = nullptr;
jmethodID method_values = nullptr;

jobject objj = nullptr;

jclass messageClass = nullptr;
jclass identityClass = nullptr;;
jclass signalClass = nullptr;
jclass engineClass = nullptr;

namespace JNISync {
    JNIEnv* env() {
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

    void onSyncStartup() {
        pEpLog("called");
        env();
    }

    void onSyncShutdown() {
        pEpLog("called");
        jvm->DetachCurrentThread();
    }
};


void jni_init() {
    JNIEnv *_env = JNISync::env();

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
    passphraseRequiredMethodID = _env->GetMethodID(
        engineClass,
        "passphraseRequiredFromC",
        "()[B");

    method_values = JNISync::env()->GetStaticMethodID(signalClass, "values",
                "()[Lfoundation/pEp/jniadapter/SyncHandshakeSignal;");
    field_value = JNISync::env()->GetFieldID(signalClass, "value", "I");
}

char* JNIAdapter::passphraseRequiredCallback() {
    pEpLog("called");

    assert(objj && passphraseRequiredMethodID);

    jobject ppJO = JNISync::env()->CallObjectMethod(objj, passphraseRequiredMethodID);
    if (JNISync::env()->ExceptionCheck()) {
        JNISync::env()->ExceptionDescribe();
        JNISync::env()->ExceptionClear();
    }

    jbyteArray ppJBA = reinterpret_cast<jbyteArray>(ppJO);
    char* passphrase_ = to_string( JNISync::env(), ppJBA);

    pEpLog("fromJava: " << passphrase_);
    JNISync::env()->DeleteLocalRef(ppJO);

    return passphrase_;
}

PEP_STATUS messageToSend(message *msg)
{
    std::lock_guard<std::mutex> l(mutex_obj);
    pEpLog("called");

    // Passphrase
    // When a protocol implementation of the p≡p engine using messageToSend() cannot sign or encrypt with an
    // empty passphrase and not with the configured passphrase it is calling messageToSend() with a NULL instead
    // of a struct _message object.
    if (Adapter::on_sync_thread() && !msg)
        return pEp::PassphraseCache::config_next_passphrase();

    // reset passphrase iterator
    if (Adapter::on_sync_thread())
        pEp::PassphraseCache::config_next_passphrase(true);

    jobject msg_ = nullptr;
    assert(messageClass && messageConstructorMethodID && objj && messageToSendMethodID);

    msg_ = JNISync::env()->NewObject(messageClass, messageConstructorMethodID, (jlong) msg);

    PEP_STATUS status = (PEP_STATUS) JNISync::env()->CallIntMethod(objj, messageToSendMethodID, msg_);
    if (JNISync::env()->ExceptionCheck()) {
        JNISync::env()->ExceptionDescribe();
        status = PEP_UNKNOWN_ERROR;
        JNISync::env()->ExceptionClear();
    }


    return status;
}

PEP_STATUS notifyHandshake(pEp_identity *me, pEp_identity *partner, sync_handshake_signal signal)
{
    std::lock_guard<std::mutex> l(mutex_obj);
    pEpLog("called");

    jobject me_ = nullptr;
    jobject partner_ = nullptr;

    me_ = from_identity(JNISync::env(), me, identityClass);
    partner_ = from_identity(JNISync::env(), partner, identityClass);

    jobject signal_ = nullptr;
    {
        assert(signalClass);
        assert(method_values);
        assert(field_value);

        jobjectArray values = (jobjectArray) JNISync::env()->CallStaticObjectMethod(signalClass,
                method_values);
        if (JNISync::env()->ExceptionCheck()) {
            JNISync::env()->ExceptionClear();
            return PEP_UNKNOWN_ERROR;
        }

        jsize values_size = JNISync::env()->GetArrayLength(values);
        for (jsize i = 0; i < values_size; i++) {
            jobject element = JNISync::env()->GetObjectArrayElement(values, i);
            assert(element);
            jint value = JNISync::env()->GetIntField(element, field_value);
            if (value == (jint) signal) {
                signal_ = element;
                break;
            }
            JNISync::env() -> DeleteLocalRef(element);
        }
    }

    assert(objj && notifyHandShakeMethodID);

    PEP_STATUS status = (PEP_STATUS) JNISync::env()->CallIntMethod(objj, notifyHandShakeMethodID, me_, partner_, signal_);
    if (JNISync::env()->ExceptionCheck()) {
        JNISync::env()->ExceptionClear();
        return PEP_UNKNOWN_ERROR;
    }

    return status;
}
}

extern "C" {
using namespace pEp;

JNIEXPORT void JNICALL Java_foundation_pEp_jniadapter_AbstractEngine_init(
        JNIEnv *env,
        jobject obj
    )
{
    std::lock_guard<std::mutex> l(global_mutex); // global mutex for write access to <unordered_map>
    pEpLog("called");

    if (first) {
        pEpLog("first Engine instance");
        first = false;
        env->GetJavaVM(&jvm);
        jni_init();
        objj = env->NewGlobalRef(obj);
        callback_dispatcher.add(messageToSend, notifyHandshake, JNISync::onSyncStartup, JNISync::onSyncShutdown);
        Adapter::_messageToSend = CallbackDispatcher::messageToSend;
    }

    create_engine_java_object_mutex(env, obj);  // Create a mutex per java object
    Adapter::session();
}

JNIEXPORT void JNICALL Java_foundation_pEp_jniadapter_AbstractEngine_release(
        JNIEnv *env,
        jobject obj
    )
{
    std::lock_guard<std::mutex> l(global_mutex);  // global mutex for write access to <unordered_map>
    pEpLog("called");
    release_engine_java_object_mutex(env, obj);
    Adapter::session(pEp::Adapter::release);
}

JNIEXPORT jstring JNICALL Java_foundation_pEp_jniadapter_AbstractEngine_getVersion(
        JNIEnv *env,
        jobject obj
    )
{
    std::mutex *mutex_local = nullptr;
    {
        std::lock_guard<std::mutex> l(global_mutex);
        pEpLog("called with lock_guard");
        mutex_local = get_engine_java_object_mutex(env, obj);
    }
    std::lock_guard<std::mutex> l(*mutex_local);

    return env->NewStringUTF(::get_engine_version());
}

JNIEXPORT jstring JNICALL Java_foundation_pEp_jniadapter_AbstractEngine_getProtocolVersion(
        JNIEnv *env,
        jobject obj
    )
{
    std::mutex *mutex_local = nullptr;
    {
        std::lock_guard<std::mutex> l(global_mutex);
        pEpLog("called with lock_guard");
        mutex_local = get_engine_java_object_mutex(env, obj);
    }
    std::lock_guard<std::mutex> l(*mutex_local);

    return env->NewStringUTF(::get_protocol_version());
}

int examine_identity(pEp_identity *ident, void *arg)
{
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
        jobject obj
    )
{
    std::mutex *mutex_local = nullptr;
    {
        std::lock_guard<std::mutex> l(global_mutex);
        pEpLog("called with lock_guard");
        mutex_local = get_engine_java_object_mutex(env, obj);
    }
    std::lock_guard<std::mutex> l(*mutex_local);

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

    thread = (pthread_t *) env->GetLongField(obj, thread_handle);
    if (thread)
        return;

    thread = (pthread_t *) calloc(1, sizeof(pthread_t));
    assert(thread);
    env->SetLongField(obj, thread_handle, (jlong) thread);

    queue = new locked_queue< pEp_identity * >();
    env->SetLongField(obj, queue_handle, (jlong) queue);

    register_examine_function(Adapter::session(), examine_identity, (void *) queue);

    pthread_create(thread, nullptr, keyserver_thread_routine, (void *) queue);
}

JNIEXPORT void JNICALL Java_foundation_pEp_jniadapter_AbstractEngine_stopKeyserverLookup(
        JNIEnv *env,
        jobject obj
    )
{
    std::mutex *mutex_local = nullptr;
    {
        std::lock_guard<std::mutex> l(global_mutex);
        pEpLog("called with lock_guard");
        mutex_local = get_engine_java_object_mutex(env, obj);
    }
    std::lock_guard<std::mutex> l(*mutex_local);

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

    thread = (pthread_t *) env->GetLongField(obj, thread_handle);
    if (!thread)
        return;

    queue = (locked_queue< pEp_identity * > *) env->GetLongField(obj, queue_handle);

    env->SetLongField(obj, queue_handle, (jlong) 0);
    env->SetLongField(obj, thread_handle, (jlong) 0);

    register_examine_function(Adapter::session(), nullptr, nullptr);

    queue->push_front(nullptr);
    pthread_join(*thread, nullptr);
    free(thread);
}

JNIEXPORT void JNICALL Java_foundation_pEp_jniadapter_AbstractEngine_startSync(
        JNIEnv *env,
        jobject obj
    )
{
    std::mutex *mutex_local = nullptr;
    {
        std::lock_guard<std::mutex> l(global_mutex);
        pEpLog("called with lock_guard");
        mutex_local = get_engine_java_object_mutex(env, obj);
    }
    std::lock_guard<std::mutex> l(*mutex_local);

    try {
        CallbackDispatcher::start_sync();
//        Adapter::startup<JNISync>(messageToSend, notifyHandshake, &o, &JNISync::onSyncStartup, &JNISync::onSyncShutdown);
    } catch (RuntimeError& ex) {
        throw_pEp_Exception(env, ex.status);
        return;
    }
}

JNIEXPORT void JNICALL Java_foundation_pEp_jniadapter_AbstractEngine_stopSync(
        JNIEnv *env,
        jobject obj
    )
{
    std::mutex *mutex_local = nullptr;
    {
        std::lock_guard<std::mutex> l(global_mutex);
        pEpLog("called with lock_guard");
        mutex_local = get_engine_java_object_mutex(env, obj);
    }
    std::lock_guard<std::mutex> l(*mutex_local);

    CallbackDispatcher::stop_sync();
//    Adapter::shutdown();
}

JNIEXPORT jboolean JNICALL Java_foundation_pEp_jniadapter_AbstractEngine_isSyncRunning(
        JNIEnv *env,
        jobject obj
    )
{
    std::mutex *mutex_local = nullptr;
    {
        std::lock_guard<std::mutex> l(global_mutex);
        pEpLog("called with lock_guard");
        mutex_local = get_engine_java_object_mutex(env, obj);
    }
    std::lock_guard<std::mutex> l(*mutex_local);

    return (jboolean) Adapter::is_sync_running();
}

} // extern "C"

