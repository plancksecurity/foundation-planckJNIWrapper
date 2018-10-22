
#include "org_pEp_jniadapter_AndroidHelper.h"

#include <stdlib.h>

#include <gpgme.h>

extern "C" {

JNIEXPORT jint JNICALL Java_org_pEp_jniadapter_AndroidHelper_setenv
  (JNIEnv* env, jclass clazz, jstring key, jstring value, jboolean overwrite)
{
    char* k = (char *) env->GetStringUTFChars(key, NULL);
    char* v = (char *) env->GetStringUTFChars(value, NULL);
    int err = setenv(k, v, overwrite);
    env->ReleaseStringUTFChars(key, k);
    env->ReleaseStringUTFChars(value, v);
    return err;
}

JNIEXPORT jint JNICALL Java_org_pEp_jniadapter_AndroidHelper_nativeSetup
  (JNIEnv* env, jclass clazz, jstring debugflag)
{
    char* cdebugflag = (char *) env->GetStringUTFChars(debugflag, NULL);
    gpgme_set_global_flag("debug", cdebugflag);
    env->ReleaseStringUTFChars(debugflag, cdebugflag);
    gpgme_set_global_flag ("disable-gpgconf", "");
    gpgme_set_global_flag ("gpg-name", "gpg2");

    return 0;
}

} // extern "C"

