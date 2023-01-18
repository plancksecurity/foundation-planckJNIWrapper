#include <pEp/keymanagement.h>
#include <pEp/Adapter.hh>
#include <pEp/pEpLog.hh>

#ifndef ANDROID
#include <string.h>
#endif

#include "throw_pEp_exception.hh"
#include "jniutils.hh"
#include "passphrase_callback.hh"

extern "C" {

using namespace pEp::JNIAdapter;
using pEp::Adapter::session;
using pEp::passphrase_cache;

JNIEXPORT jbyteArray JNICALL Java_foundation_pEp_jniadapter_Engine__1trustwords(JNIEnv *env,
        jobject obj,
        jobject ident)
{
    std::mutex *mutex_local = nullptr;
    {
        std::lock_guard<std::mutex> l(global_mutex);
        pEpLog("called with lock_guard");
        mutex_local = get_engine_java_object_mutex(env, obj);
    }
    std::lock_guard<std::mutex> l(*mutex_local);

    pEp_identity *_ident = to_identity(env, ident);
    char *words;
    size_t wsize;

    PEP_STATUS status = PEP_STATUS_OK;

    if (_ident->fpr == NULL || _ident->fpr[0] == 0) {
        if (_ident->me) {
            status = passphraseWrap(::myself, session(), _ident);
        } else {
            status = passphraseWrap(::update_identity, session(), _ident);
        }
    }

    if (status != PEP_STATUS_OK) {
        throw_pEp_Exception(env, status);
        return NULL;
    }

    if (_ident->fpr == NULL || _ident->fpr[0] == 0) {
        throw_pEp_Exception(env, PEP_CANNOT_FIND_IDENTITY);
        return NULL;
    }

    const char *lang;
    if (_ident->lang[0]) {
        lang = _ident->lang;
    } else {
        lang = "en";
    }

    status = passphraseWrap(::trustwords, session(), static_cast<const char *>(_ident->fpr), lang, &words, &wsize, 10);

    if (status != PEP_STATUS_OK) {
        throw_pEp_Exception(env, status);
        return NULL;
    }

    return from_string(env, words);
}

JNIEXPORT jobject JNICALL Java_foundation_pEp_jniadapter_Engine__1myself(JNIEnv *env,
        jobject obj,
        jobject ident)
{
    std::mutex *mutex_local = nullptr;
    {
        std::lock_guard<std::mutex> l(global_mutex);
        pEpLog("called with lock_guard");
        mutex_local = get_engine_java_object_mutex(env, obj);
    }
    std::lock_guard<std::mutex> l(*mutex_local);

    pEp_identity *_ident = to_identity(env, ident);

    PEP_STATUS status = passphraseWrap(::myself, session(), _ident);

    if (status != PEP_STATUS_OK) {
        throw_pEp_Exception(env, status);
        return NULL;
    }
    return from_identity(env, _ident);
}

JNIEXPORT jobject JNICALL Java_foundation_pEp_jniadapter_Engine__1updateIdentity(JNIEnv *env,
        jobject obj,
        jobject ident)
{
    std::mutex *mutex_local = nullptr;
    {
        std::lock_guard<std::mutex> l(global_mutex);
        pEpLog("called with lock_guard");
        mutex_local = get_engine_java_object_mutex(env, obj);
    }
    std::lock_guard<std::mutex> l(*mutex_local);

    pEp_identity *_ident = to_identity(env, ident);
    passphraseWrap(::update_identity, session(), _ident);

    return from_identity(env, _ident);
}

JNIEXPORT jobject JNICALL Java_foundation_pEp_jniadapter_Engine__1setOwnKey(JNIEnv *env,
        jobject obj,
        jobject ident,
        jbyteArray fpr)
{
    std::mutex *mutex_local = nullptr;
    {
        std::lock_guard<std::mutex> l(global_mutex);
        pEpLog("called with lock_guard");
        mutex_local = get_engine_java_object_mutex(env, obj);
    }
    std::lock_guard<std::mutex> l(*mutex_local);

    pEp_identity *_ident = to_identity(env, ident);
    const char *_fpr = to_string(env, fpr);

    PEP_STATUS status = passphraseWrap(::set_own_key, session(), _ident, _fpr);

    if (status != PEP_STATUS_OK) {
        throw_pEp_Exception(env, status);
        return NULL;
    }

    return from_identity(env, _ident);
}

JNIEXPORT void JNICALL Java_foundation_pEp_jniadapter_Engine__1keyMistrusted(JNIEnv *env,
        jobject obj,
        jobject ident)
{
    std::mutex *mutex_local = nullptr;
    {
        std::lock_guard<std::mutex> l(global_mutex);
        pEpLog("called with lock_guard");
        mutex_local = get_engine_java_object_mutex(env, obj);
    }
    std::lock_guard<std::mutex> l(*mutex_local);

    pEp_identity *_ident = to_identity(env, ident);

    PEP_STATUS status = PEP_STATUS_OK;

    if (_ident->fpr == NULL || _ident->fpr[0] == 0) {
        if (_ident->me)
            status = passphraseWrap(::myself, session(), _ident);
        else
            status = passphraseWrap(::update_identity, session(), _ident);
    }

    if (status != PEP_STATUS_OK) {
        throw_pEp_Exception(env, status);
        return;
    }

    if (_ident->fpr == NULL || _ident->fpr[0] == 0) {
        throw_pEp_Exception(env, PEP_CANNOT_FIND_IDENTITY);
        return;
    }

    passphraseWrap(::key_mistrusted, session(), _ident);
}

JNIEXPORT void JNICALL Java_foundation_pEp_jniadapter_Engine__1keyResetTrust(JNIEnv *env,
        jobject obj,
        jobject ident)
{
    std::mutex *mutex_local = nullptr;
    {
        std::lock_guard<std::mutex> l(global_mutex);
        pEpLog("called with lock_guard");
        mutex_local = get_engine_java_object_mutex(env, obj);
    }
    std::lock_guard<std::mutex> l(*mutex_local);

    pEp_identity *_ident = to_identity(env, ident);

    PEP_STATUS status = PEP_STATUS_OK;

    if (_ident->fpr == NULL || _ident->fpr[0] == 0) {
        if (_ident->me) {
            status = passphraseWrap(::myself, session(), _ident);
        } else {
            status = passphraseWrap(::update_identity, session(), _ident);
        }
    }

    if (status != PEP_STATUS_OK) {
        throw_pEp_Exception(env, status);
        return;
    }

    if (_ident->fpr == NULL || _ident->fpr[0] == 0) {
        throw_pEp_Exception(env, PEP_CANNOT_FIND_IDENTITY);
        return;
    }

    passphraseWrap(::key_reset_trust, session(), _ident);
}

JNIEXPORT void JNICALL Java_foundation_pEp_jniadapter_Engine__1trustPersonalKey(JNIEnv *env,
        jobject obj,
        jobject ident)
{
    std::mutex *mutex_local = nullptr;
    {
        std::lock_guard<std::mutex> l(global_mutex);
        pEpLog("called with lock_guard");
        mutex_local = get_engine_java_object_mutex(env, obj);
    }
    std::lock_guard<std::mutex> l(*mutex_local);

    pEp_identity *_ident = to_identity(env, ident);

    PEP_STATUS status = PEP_STATUS_OK;

    if (_ident->fpr == NULL || _ident->fpr[0] == 0) {
        if (_ident->me) {
            status = passphraseWrap(::myself, session(), _ident);
        } else {
            status = passphraseWrap(::update_identity, session(), _ident);
        }
    }

    if (status != PEP_STATUS_OK) {
        throw_pEp_Exception(env, status);
        return;
    }

    if (_ident->fpr == NULL || _ident->fpr[0] == 0) {
        throw_pEp_Exception(env, PEP_CANNOT_FIND_IDENTITY);
        return;
    }

    passphraseWrap(::trust_personal_key, session(), _ident);
}

JNIEXPORT void JNICALL Java_foundation_pEp_jniadapter_Engine__1trustOwnKey(JNIEnv *env,
        jobject obj,
        jobject ident)
{
    std::mutex *mutex_local = nullptr;
    {
        std::lock_guard<std::mutex> l(global_mutex);
        pEpLog("called with lock_guard");
        mutex_local = get_engine_java_object_mutex(env, obj);
    }
    std::lock_guard<std::mutex> l(*mutex_local);

    pEp_identity *_ident = to_identity(env, ident);

    if (_ident->fpr == NULL || _ident->fpr[0] == 0) {
        throw_pEp_Exception(env, PEP_CANNOT_FIND_IDENTITY);
        return;
    }

    passphraseWrap(::trust_own_key, session(), _ident);
}

JNIEXPORT jobject JNICALL Java_foundation_pEp_jniadapter_Engine__1importKey(JNIEnv *env,
        jobject obj,
        jbyteArray key)
{
    std::mutex *mutex_local = nullptr;
    {
        std::lock_guard<std::mutex> l(global_mutex);
        pEpLog("called with lock_guard");
        mutex_local = get_engine_java_object_mutex(env, obj);
    }
    std::lock_guard<std::mutex> l(*mutex_local);

    size_t _size = static_cast<size_t>(env->GetArrayLength(key));
    jbyte* _key = env->GetByteArrayElements(key, NULL);

    if(_key == NULL) {
        throw_pEp_Exception(env, PEP_OUT_OF_MEMORY);
        return NULL;
    }

    identity_list *_identities = nullptr;

    PEP_STATUS status =  passphraseWrap(::import_key, session(), reinterpret_cast<const char*>(_key), _size, &_identities);
    if (status != PEP_STATUS_OK && status != PEP_KEY_IMPORTED) {
        throw_pEp_Exception(env, status);
        return NULL;
    }

    jobject identities_ = NULL;
    if (_identities) {
        identities_ = from_identitylist(env, _identities);
    }

    env->ReleaseByteArrayElements(key, _key, JNI_ABORT);
    return identities_;
}


JNIEXPORT void JNICALL Java_foundation_pEp_jniadapter_Engine__1config_1passive_1mode(JNIEnv *env,
        jobject obj,
        jboolean enable)
{
    std::mutex *mutex_local = nullptr;
    {
        std::lock_guard<std::mutex> l(global_mutex);
        pEpLog("called with lock_guard");
        mutex_local = get_engine_java_object_mutex(env, obj);
    }
    std::lock_guard<std::mutex> l(*mutex_local);

    ::config_passive_mode(session(), static_cast<bool>(enable));
}


JNIEXPORT void JNICALL Java_foundation_pEp_jniadapter_Engine__1config_1unencrypted_1subject(JNIEnv *env,
        jobject obj,
        jboolean enable)
{
    std::mutex *mutex_local = nullptr;
    {
        std::lock_guard<std::mutex> l(global_mutex);
        pEpLog("called with lock_guard");
        mutex_local = get_engine_java_object_mutex(env, obj);
    }
    std::lock_guard<std::mutex> l(*mutex_local);

    ::config_unencrypted_subject(session(), static_cast<bool>(enable));
}



JNIEXPORT jbyteArray JNICALL Java_foundation_pEp_jniadapter_Engine__1getCrashdumpLog(JNIEnv *env,
        jobject obj,
        jint dummy,
        jint maxlines)
{
    std::mutex *mutex_local = nullptr;
    {
        std::lock_guard<std::mutex> l(global_mutex);
        pEpLog("called with lock_guard");
        mutex_local = get_engine_java_object_mutex(env, obj);
    }
    std::lock_guard<std::mutex> l(*mutex_local);

    int _maxlines = static_cast<int>(maxlines);
    char *_logdata;

    PEP_STATUS status = passphraseWrap(::get_crashdump_log, session(), _maxlines, &_logdata);
    if ((status > PEP_STATUS_OK && status < PEP_UNENCRYPTED) ||
            status < PEP_STATUS_OK ||
            status >= PEP_TRUSTWORD_NOT_FOUND) {
        throw_pEp_Exception(env, status);
        return NULL;
    }

    return from_string(env, _logdata);
}

JNIEXPORT jbyteArray JNICALL Java_foundation_pEp_jniadapter_Engine__1getUserDirectory(JNIEnv *env,
        jobject obj)
{
    pEpLog("called");
    return from_string(env, ::per_user_directory());
}

JNIEXPORT jbyteArray JNICALL Java_foundation_pEp_jniadapter_Engine__1getMachineDirectory(JNIEnv *env,
        jobject obj)
{
    pEpLog("called");
    return from_string(env, ::per_machine_directory());
}


JNIEXPORT void JNICALL Java_foundation_pEp_jniadapter_Engine__1config_1passphrase(JNIEnv * env,
       jobject obj,
       jbyteArray passphrase)
{
    std::mutex *mutex_local = nullptr;
    {
        std::lock_guard<std::mutex> l(global_mutex);
        pEpLog("called with lock_guard");
        mutex_local = get_engine_java_object_mutex(env, obj);
    }
    std::lock_guard<std::mutex> l(*mutex_local);

    char* _passphrase = to_string(env, passphrase);

    PEP_STATUS status = ::config_passphrase(session(),passphrase_cache.add(_passphrase));
    if (status != 0) {
        throw_pEp_Exception(env, status);
        return;
    }
}


JNIEXPORT void JNICALL Java_foundation_pEp_jniadapter_Engine__1config_1passphrase_1for_1new_1keys(JNIEnv *env,
        jobject obj,
        jboolean enable,
        jbyteArray passphrase)
{
    std::mutex *mutex_local = nullptr;
    {
        std::lock_guard<std::mutex> l(global_mutex);
        pEpLog("called with lock_guard");
        mutex_local = get_engine_java_object_mutex(env, obj);
    }
    std::lock_guard<std::mutex> l(*mutex_local);

    bool _enable = static_cast<bool>(enable);
    const char *_passphrase = to_string(env, passphrase);

    PEP_STATUS status = ::config_passphrase_for_new_keys(session(),_enable,passphrase_cache.add_stored(_passphrase));
    if ((status > PEP_STATUS_OK && status < PEP_UNENCRYPTED) ||
            status < PEP_STATUS_OK ||
            status >= PEP_TRUSTWORD_NOT_FOUND) {
        throw_pEp_Exception(env, status);
        return ;
    }
}

JNIEXPORT jbyteArray JNICALL Java_foundation_pEp_jniadapter_Engine__1export_1key (JNIEnv *env,
        jobject obj,
        jbyteArray fpr)
{
    std::mutex *mutex_local = nullptr;

    {
        std::lock_guard<std::mutex> l(global_mutex);
        pEpLog("called with lock_guard");
        mutex_local = get_engine_java_object_mutex(env, obj);
    }
    std::lock_guard<std::mutex> l(*mutex_local);

    const char *_fpr = to_string(env, fpr);
    char *buff = nullptr;
    size_t size = 0;

    PEP_STATUS status = passphraseWrap(::export_key, session(), _fpr, &buff, &size);

    if (status != PEP_STATUS_OK) {
        throw_pEp_Exception(env, status);
        return NULL;
    }

    return from_string(env, buff);
}

JNIEXPORT void JNICALL Java_foundation_pEp_jniadapter_Engine__1config_1enable_1echo_1protocol(JNIEnv *env,
        jobject obj,
        jboolean enable)
{
    std::mutex *mutex_local = nullptr;
    {
        std::lock_guard<std::mutex> l(global_mutex);
        pEpLog("called with lock_guard");
        mutex_local = get_engine_java_object_mutex(env, obj);
    }
    std::lock_guard<std::mutex> l(*mutex_local);
    ::config_enable_echo_protocol(session(),static_cast<bool>(enable));
}

JNIEXPORT void JNICALL Java_foundation_pEp_jniadapter_Engine__1config_1enable_1echo_1in_1outgoing_1message_1rating_1preview(JNIEnv *env,
        jobject obj,
        jboolean enable)
{
    std::mutex *mutex_local = nullptr;
    {
        std::lock_guard<std::mutex> l(global_mutex);
        pEpLog("called with lock_guard");
        mutex_local = get_engine_java_object_mutex(env, obj);
    }
    std::lock_guard<std::mutex> l(*mutex_local);
    ::config_enable_echo_in_outgoing_message_rating_preview(session(),static_cast<bool>(enable));
}

} // extern "C"

