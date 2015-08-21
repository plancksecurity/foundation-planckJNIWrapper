#include <pEp/keymanagement.h>

#include "throw_pEp_exception.hh"
#include "jniutils.hh"

using namespace pEp::JNIAdapter;

extern "C" {
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
        if (_ident->me)
            ::myself(session, _ident);
        else
            ::update_identity(session, _ident);
    }

    if (_ident->fpr == NULL || _ident->fpr[0] == 0) {
        throw_pEp_Exception(env, PEP_CANNOT_FIND_IDENTITY);
        return NULL;
    }

    PEP_STATUS status = ::trustwords(session, _ident->fpr, _ident->lang, &words, &wsize, 10);
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

JNIEXPORT void JNICALL Java_org_pEp_jniadapter_Engine_keyCompromized(
        JNIEnv *env,
        jobject obj,
        jobject ident
    )
{
    PEP_SESSION session = (PEP_SESSION) callLongMethod(env, obj, "getHandle");
    pEp_identity *_ident = to_identity(env, ident);

    if (_ident->fpr == NULL || _ident->fpr[0] == 0) {
        if (_ident->me)
            ::myself(session, _ident);
        else
            ::update_identity(session, _ident);
    }

    if (_ident->fpr == NULL || _ident->fpr[0] == 0) {
        throw_pEp_Exception(env, PEP_CANNOT_FIND_IDENTITY);
        return;
    }

    ::key_compromized(session, _ident->fpr);
}

} // extern "C"

