#include <keymanagement.h>
#include <blacklist.h>
#include <sync.h>

#ifndef ANDROID
#include <string.h>
#endif

#include "throw_pEp_exception.hh"
#include "jniutils.hh"

extern "C" {
    using namespace pEp::JNIAdapter;

JNIEXPORT jobject JNICALL Java_org_pEp_jniadapter_Engine_trustwords(
        JNIEnv *env,
        jobject obj,
        jobject ident
    )
{
    PEP_SESSION session = (PEP_SESSION) callLongMethod(env, obj, "getHandle");
    pEp_identity *_ident = to_identity(env, ident);
    char *words;
    size_t wsize;

    if (_ident->fpr == NULL || _ident->fpr[0] == 0) {
        ::update_identity(session, _ident);
    }

    if (_ident->fpr == NULL || _ident->fpr[0] == 0) {
        throw_pEp_Exception(env, PEP_CANNOT_FIND_IDENTITY);
        return NULL;
    }

    const char *lang;
    if (_ident->lang[0])
        lang = _ident->lang;
    else
        lang = "en";

    PEP_STATUS status = ::trustwords(session, _ident->fpr, lang, &words, &wsize, 10);
    if (status != PEP_STATUS_OK) {
        throw_pEp_Exception(env, status);
        return NULL;
    }

    return from_string(env, words);
}

JNIEXPORT jobject JNICALL Java_org_pEp_jniadapter_Engine_myself(
        JNIEnv *env,
        jobject obj,
        jobject ident
    )
{
    PEP_SESSION session = (PEP_SESSION) callLongMethod(env, obj, "getHandle");
    pEp_identity *_ident = to_identity(env, ident);

    ::myself(session, _ident);

    return from_identity(env, _ident);
}

JNIEXPORT jobject JNICALL Java_org_pEp_jniadapter_Engine_updateIdentity(
        JNIEnv *env,
        jobject obj,
        jobject ident
    )
{
    PEP_SESSION session = (PEP_SESSION) callLongMethod(env, obj, "getHandle");
    pEp_identity *_ident = to_identity(env, ident);

    ::update_identity(session, _ident);

    return from_identity(env, _ident);
}

JNIEXPORT void JNICALL Java_org_pEp_jniadapter_Engine_keyMistrusted(
        JNIEnv *env,
        jobject obj,
        jobject ident
    )
{
    PEP_SESSION session = (PEP_SESSION) callLongMethod(env, obj, "getHandle");
    pEp_identity *_ident = to_identity(env, ident);

    if (_ident->fpr == NULL || _ident->fpr[0] == 0) {
        ::update_identity(session, _ident);
    }

    if (_ident->fpr == NULL || _ident->fpr[0] == 0) {
        throw_pEp_Exception(env, PEP_CANNOT_FIND_IDENTITY);
        return;
    }

    ::key_mistrusted(session, _ident);
}

JNIEXPORT void JNICALL Java_org_pEp_jniadapter_Engine_keyResetTrust(
        JNIEnv *env,
        jobject obj,
        jobject ident
    )
{
    PEP_SESSION session = (PEP_SESSION) callLongMethod(env, obj, "getHandle");
    pEp_identity *_ident = to_identity(env, ident);

    if (_ident->fpr == NULL || _ident->fpr[0] == 0) {
        ::update_identity(session, _ident);
    }

    if (_ident->fpr == NULL || _ident->fpr[0] == 0) {
        throw_pEp_Exception(env, PEP_CANNOT_FIND_IDENTITY);
        return;
    }

    ::key_reset_trust(session, _ident);
}

JNIEXPORT void JNICALL Java_org_pEp_jniadapter_Engine_trustPersonalKey(
        JNIEnv *env,
        jobject obj,
        jobject ident
    )
{
    PEP_SESSION session = (PEP_SESSION) callLongMethod(env, obj, "getHandle");
    pEp_identity *_ident = to_identity(env, ident);

    if (_ident->fpr == NULL || _ident->fpr[0] == 0) {
        ::update_identity(session, _ident);
    }

    if (_ident->fpr == NULL || _ident->fpr[0] == 0) {
        throw_pEp_Exception(env, PEP_CANNOT_FIND_IDENTITY);
        return;
    }

    ::trust_personal_key(session, _ident);
}

JNIEXPORT void JNICALL Java_org_pEp_jniadapter_Engine_importKey(
        JNIEnv *env,
        jobject obj,
        jbyteArray key
    )
{
    PEP_SESSION session = (PEP_SESSION) callLongMethod(env, obj, "getHandle");
    char *_key = to_string(env, key);

    if(_key == NULL){
        throw_pEp_Exception(env, PEP_OUT_OF_MEMORY);
        return;
    }

    
    PEP_STATUS status = ::import_key(session, _key, strlen(_key), NULL);
    if (status != PEP_STATUS_OK) {
        throw_pEp_Exception(env, status);
        return;
    }

}

JNIEXPORT void JNICALL Java_org_pEp_jniadapter_Engine_config_1passive_1mode(
        JNIEnv *env,
        jobject obj,
        jboolean enable
    )
{
    PEP_SESSION session = (PEP_SESSION) callLongMethod(env, obj, "getHandle");

    ::config_passive_mode(session, (bool)enable);
}


JNIEXPORT void JNICALL Java_org_pEp_jniadapter_Engine_config_1unencrypted_1subject(
        JNIEnv *env,
        jobject obj,
        jboolean enable
    )
{
    PEP_SESSION session = (PEP_SESSION) callLongMethod(env, obj, "getHandle");

    ::config_unencrypted_subject(session, (bool)enable);
}

JNIEXPORT void JNICALL Java_org_pEp_jniadapter_Engine_blacklist_1add(
        JNIEnv *env,
        jobject obj,
        jbyteArray fpr
    )
{
    PEP_SESSION session = (PEP_SESSION) callLongMethod(env, obj, "getHandle");
    char *_fpr = to_string(env, fpr);

    if(_fpr == NULL){
        throw_pEp_Exception(env, PEP_OUT_OF_MEMORY);
        return;
    }
    
    PEP_STATUS status = ::blacklist_add(session, _fpr);
    if (status != PEP_STATUS_OK) {
        throw_pEp_Exception(env, status);
        return;
    }

}

JNIEXPORT void JNICALL Java_org_pEp_jniadapter_Engine_blacklist_1delete(
        JNIEnv *env,
        jobject obj,
        jbyteArray fpr
    )
{
    PEP_SESSION session = (PEP_SESSION) callLongMethod(env, obj, "getHandle");
    char *_fpr = to_string(env, fpr);

    if(_fpr == NULL){
        throw_pEp_Exception(env, PEP_OUT_OF_MEMORY);
        return;
    }
    
    PEP_STATUS status = ::blacklist_delete(session, _fpr);
    if (status != PEP_STATUS_OK) {
        throw_pEp_Exception(env, status);
        return;
    }

}

JNIEXPORT jboolean JNICALL Java_org_pEp_jniadapter_Engine_blacklist_1is_1listed(
        JNIEnv *env,
        jobject obj,
        jbyteArray fpr
    )
{
    PEP_SESSION session = (PEP_SESSION) callLongMethod(env, obj, "getHandle");
    char *_fpr = to_string(env, fpr);
    bool _listed = 0;

    if(_fpr == NULL){
        throw_pEp_Exception(env, PEP_OUT_OF_MEMORY);
        return 0;
    }
    
    PEP_STATUS status = ::blacklist_is_listed(session, _fpr, &_listed);
    if (status != PEP_STATUS_OK) {
        throw_pEp_Exception(env, status);
        return 0;
    }

    return (jboolean)_listed;
}

JNIEXPORT void JNICALL Java_org_pEp_jniadapter_Engine_accept_1sync_1handshake(
        JNIEnv *env,
        jobject obj,
        jobject ident
    )

{
    PEP_SESSION session = (PEP_SESSION) callLongMethod(env, obj, "getHandle");
    pEp_identity *_ident = to_identity(env, ident);

    PEP_STATUS status =
        ::deliverHandshakeResult(session, _ident, SYNC_HANDSHAKE_ACCEPTED);

    if (status != PEP_STATUS_OK) {
        throw_pEp_Exception(env, status);
        return;
    }
}


JNIEXPORT void JNICALL Java_org_pEp_jniadapter_Engine_reject_1sync_1handshake(
        JNIEnv *env,
        jobject obj,
        jobject ident
    )
{
    PEP_SESSION session = (PEP_SESSION) callLongMethod(env, obj, "getHandle");
    pEp_identity *_ident = to_identity(env, ident);
    
    PEP_STATUS status = 
        ::deliverHandshakeResult(session, _ident, SYNC_HANDSHAKE_REJECTED);

    if (status != PEP_STATUS_OK) {
        throw_pEp_Exception(env, status);
        return;
    }
}

JNIEXPORT void JNICALL Java_org_pEp_jniadapter_Engine_cancel_1sync_1handshake(
        JNIEnv *env,
        jobject obj,
        jobject ident
    )
{
    PEP_SESSION session = (PEP_SESSION) callLongMethod(env, obj, "getHandle");
    pEp_identity *_ident = to_identity(env, ident);
    
    PEP_STATUS status = 
        ::deliverHandshakeResult(session, _ident, SYNC_HANDSHAKE_CANCEL);

    if (status != PEP_STATUS_OK) {
        throw_pEp_Exception(env, status);
        return;
    }
}

JNIEXPORT jbyteArray JNICALL Java_org_pEp_jniadapter_Engine_getCrashdumpLog(
        JNIEnv *env,
        jobject obj,
        jint maxlines
    )
{
    PEP_SESSION session = (PEP_SESSION) callLongMethod(env, obj, "getHandle");

    int _maxlines = (int) maxlines;
    char *_logdata;

    PEP_STATUS status = ::get_crashdump_log(session, _maxlines, &_logdata);
    if ((status > PEP_STATUS_OK && status < PEP_UNENCRYPTED) ||
            status < PEP_STATUS_OK ||
            status >= PEP_TRUSTWORD_NOT_FOUND) {
        throw_pEp_Exception(env, status);
        return NULL;
    }

    return from_string(env, _logdata);
}

} // extern "C"

