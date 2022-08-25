#include "foundation_pEp_jniadapter_AbstractEngine.h"
#include <pEp/keymanagement.h>
#include <pEp/message_api.h>
#include <pEp/sync_api.h>
#include <pEp/media_key.h>
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

jfieldID signal_field_value = nullptr;
jfieldID passphrase_type_field_value = nullptr;
jmethodID messageConstructorMethodID = nullptr;
jmethodID messageToSendMethodID = nullptr;
jmethodID notifyHandShakeMethodID = nullptr;
jmethodID needsFastPollMethodID = nullptr;
jmethodID passphraseRequiredMethodID = nullptr;
jmethodID sync_handshake_signal_values = nullptr;
jmethodID passphrase_status_values = nullptr;
jmethodID passphrase_callback_values = nullptr;

jobject objj = nullptr;

jclass messageClass = nullptr;
jclass identityClass = nullptr;
jclass signalClass = nullptr;
jclass abstractEngineClass = nullptr;
jclass passphraseTypeClass = nullptr;

namespace JNISync {
    JNIEnv* env() {
        JNIEnv *thread_env = nullptr;
        int status = jvm->GetEnv(reinterpret_cast<void**>(&thread_env), JNI_VERSION_1_6);
        if (status < 0) {
#ifdef ANDROID
            status = jvm->AttachCurrentThread(&thread_env, nullptr);
#else
            status = jvm->AttachCurrentThread(reinterpret_cast<void**>(&thread_env), nullptr);
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
    JNIEnv * _env = JNISync::env();

    messageClass = static_cast<jclass>(_env->NewGlobalRef(findClass(_env, "foundation/pEp/jniadapter/Message")));
    identityClass = static_cast<jclass>(_env->NewGlobalRef(findClass(_env, "foundation/pEp/jniadapter/_Identity")));
    signalClass = static_cast<jclass>(_env->NewGlobalRef(findClass(_env, "foundation/pEp/jniadapter/SyncHandshakeSignal")));
    passphraseTypeClass = static_cast<jclass>(_env->NewGlobalRef(findClass(_env, "foundation/pEp/jniadapter/PassphraseType")));
    abstractEngineClass = static_cast<jclass>(_env->NewGlobalRef(findClass(_env, "foundation/pEp/jniadapter/AbstractEngine")));

    messageConstructorMethodID = _env->GetMethodID(
            messageClass,
            "<init>",
            "(J)V");

    messageToSendMethodID = _env->GetMethodID(
            abstractEngineClass,
            "messageToSendCallFromC",
            "(Lfoundation/pEp/jniadapter/Message;)I");

    needsFastPollMethodID = _env->GetMethodID(
            abstractEngineClass,
            "needsFastPollCallFromC",
            "(Z)I");

    notifyHandShakeMethodID = _env->GetMethodID(
            abstractEngineClass,
            "notifyHandshakeCallFromC",
            "(Lfoundation/pEp/jniadapter/_Identity;Lfoundation/pEp/jniadapter/_Identity;Lfoundation/pEp/jniadapter/SyncHandshakeSignal;)I");

    passphraseRequiredMethodID = _env->GetMethodID(
            abstractEngineClass,
            "passphraseRequiredFromC",
            "(Lfoundation/pEp/jniadapter/PassphraseType;)[B");

    sync_handshake_signal_values = JNISync::env()->GetStaticMethodID(
            signalClass,
            "values",
            "()[Lfoundation/pEp/jniadapter/SyncHandshakeSignal;");

    passphrase_status_values = JNISync::env()->GetStaticMethodID(
            passphraseTypeClass,
            "values",
            "()[Lfoundation/pEp/jniadapter/PassphraseType;");

    signal_field_value = JNISync::env()->GetFieldID(
            signalClass,
            "value",
            "I");

    passphrase_type_field_value = JNISync::env()->GetFieldID(passphraseTypeClass,    "value", "I");
}

char* JNIAdapter::passphraseRequiredCallback(
    const PEP_STATUS status)
{
    pEpLog("called");
    jobject status_ = nullptr;
    {
        assert(passphraseTypeClass);
        assert(passphrase_status_values);
        assert(passphrase_type_field_value);

        jobjectArray values = static_cast<jobjectArray>(JNISync::env()->CallStaticObjectMethod(passphraseTypeClass, passphrase_status_values));

        if (JNISync::env()->ExceptionCheck()) {
            JNISync::env()->ExceptionClear();
            throw_pEp_Exception(JNISync::env(), PEP_UNKNOWN_ERROR);
        }

        jsize values_size = JNISync::env()->GetArrayLength(values);
        for (jsize i = 0; i < values_size; i++) {
            jobject element = JNISync::env()->GetObjectArrayElement(values, i);
            assert(element);
            jint value = JNISync::env()->GetIntField(element, passphrase_type_field_value);
            if (value == static_cast<jint>(status)) {
                status_ = element;
                break;
            }
            JNISync::env()->DeleteLocalRef(element);
        }
    }
    assert(objj && passphraseRequiredMethodID);

    jobject ppJO = JNISync::env()->CallObjectMethod(objj, passphraseRequiredMethodID, status_);
    if (JNISync::env()->ExceptionCheck()) {
        JNISync::env()->ExceptionDescribe();
        JNISync::env()->ExceptionClear();
    }

    jbyteArray ppJBA = static_cast<jbyteArray>(ppJO);
    char* passphrase_ = to_string( JNISync::env(), ppJBA);

    return passphrase_;
}

PEP_STATUS messageToSend(message *msg)
{
    std::lock_guard <std::mutex> l(mutex_obj);
    pEpLog("called");

    // Passphrase
    // When a protocol implementation of the p≡p engine using messageToSend() cannot sign or encrypt with an
    // empty passphrase and not with the configured passphrase it is calling messageToSend() with a NULL instead
    // of a struct _message object.
    if (Adapter::on_sync_thread() && !msg) {
        return pEp::PassphraseCache::config_next_passphrase();
    }

    // reset passphrase iterator
    if (Adapter::on_sync_thread()) {
        pEp::PassphraseCache::config_next_passphrase(true);
    }

    jobject msg_ = nullptr;
    assert(messageClass && messageConstructorMethodID && objj && messageToSendMethodID);

    msg_ = JNISync::env()->NewObject(messageClass, messageConstructorMethodID, reinterpret_cast<jlong>(msg));

    PEP_STATUS status = (PEP_STATUS) JNISync::env()->CallIntMethod(objj, messageToSendMethodID, msg_);
    if (JNISync::env()->ExceptionCheck()) {
        JNISync::env()->ExceptionDescribe();
        status = PEP_UNKNOWN_ERROR;
        JNISync::env()->ExceptionClear();
    }

    return status;
}

PEP_STATUS notifyHandshake(pEp_identity *me,
        pEp_identity *partner,
        sync_handshake_signal signal)
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
        assert(sync_handshake_signal_values);
        assert(signal_field_value);

        jobjectArray values = static_cast<jobjectArray>(JNISync::env()->CallStaticObjectMethod(signalClass, sync_handshake_signal_values));
        if (JNISync::env()->ExceptionCheck()) {
            JNISync::env()->ExceptionClear();
            return PEP_UNKNOWN_ERROR;
        }

        jsize values_size = JNISync::env()->GetArrayLength(values);
        for (jsize i = 0; i < values_size; i++) {
            jobject element = JNISync::env()->GetObjectArrayElement(values, i);
            assert(element);
            jint value = JNISync::env()->GetIntField(element, signal_field_value);
            if (value == static_cast<jint>(signal)) {
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

JNIEXPORT void JNICALL Java_foundation_pEp_jniadapter_AbstractEngine_init(JNIEnv *env,
        jobject obj)
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

JNIEXPORT void JNICALL Java_foundation_pEp_jniadapter_AbstractEngine_release(JNIEnv *env,
        jobject obj)
{
    std::lock_guard<std::mutex> l(global_mutex);  // global mutex for write access to <unordered_map>
    pEpLog("called");
    release_engine_java_object_mutex(env, obj);
    Adapter::session(pEp::Adapter::release);
}

JNIEXPORT void JNICALL Java_foundation_pEp_jniadapter_AbstractEngine__1setDebugLogEnabled(
        JNIEnv *env,
        jclass clazz,
        jboolean enabled)
{
    Adapter::pEpLog::set_enabled(static_cast<bool>(enabled));
}

JNIEXPORT jboolean JNICALL Java_foundation_pEp_jniadapter_AbstractEngine__1getDebugLogEnabled(
        JNIEnv *env,
        jclass clazz
    )
{
    return static_cast<jboolean>(Adapter::pEpLog::get_enabled());
}

JNIEXPORT jstring JNICALL Java_foundation_pEp_jniadapter_AbstractEngine__1getVersion(JNIEnv *env,
        jobject obj)
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

JNIEXPORT jstring JNICALL Java_foundation_pEp_jniadapter_AbstractEngine__1getProtocolVersion(JNIEnv *env,
        jobject obj)
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

int examine_identity(pEp_identity *ident,
        void *arg)
{
    locked_queue < pEp_identity * > *queue = static_cast<locked_queue < pEp_identity * > * > (arg);
    queue->push_back(identity_dup(ident));
    return 0;
}

JNIEXPORT void JNICALL Java_foundation_pEp_jniadapter_AbstractEngine__1startSync(JNIEnv *env,
        jobject obj)
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
    } catch (RuntimeError& ex) {
        throw_pEp_Exception(env, ex.status);
        return;
    }
}

JNIEXPORT void JNICALL Java_foundation_pEp_jniadapter_AbstractEngine__1stopSync(JNIEnv *env,
        jobject obj)
{
    std::mutex *mutex_local = nullptr;
    {
        std::lock_guard<std::mutex> l(global_mutex);
        pEpLog("called with lock_guard");
        mutex_local = get_engine_java_object_mutex(env, obj);
    }
    std::lock_guard<std::mutex> l(*mutex_local);

    CallbackDispatcher::stop_sync();
}

JNIEXPORT void JNICALL Java_foundation_pEp_jniadapter_AbstractEngine__1config_1media_1keys(JNIEnv *env,
        jobject obj,
        jobject value)
{
    std::mutex *mutex_local = nullptr;
    {
        std::lock_guard<std::mutex> l(global_mutex);
        pEpLog("called with lock_guard");
        mutex_local = get_engine_java_object_mutex(env, obj);
    }
    std::lock_guard<std::mutex> l(*mutex_local);

    PEP_STATUS status = ::config_media_keys(Adapter::session(),to_stringpairlist(env, value));
    if (status) {
        throw_pEp_Exception(env, status);
    }

}


JNIEXPORT jboolean JNICALL Java_foundation_pEp_jniadapter_AbstractEngine__1isSyncRunning(JNIEnv *env,
        jobject obj)
{
    std::mutex *mutex_local = nullptr;
    {
        std::lock_guard<std::mutex> l(global_mutex);
        pEpLog("called with lock_guard");
        mutex_local = get_engine_java_object_mutex(env, obj);
    }
    std::lock_guard<std::mutex> l(*mutex_local);

    return static_cast<jboolean>(Adapter::is_sync_running());
}

} // extern "C"

