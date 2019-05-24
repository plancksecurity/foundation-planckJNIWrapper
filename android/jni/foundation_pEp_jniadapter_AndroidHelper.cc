
#include "foundation_pEp_jniadapter_AndroidHelper.h"

#include <stdlib.h>

extern "C" {

JNIEXPORT jint JNICALL Java_foundation_pEp_jniadapter_AndroidHelper_setenv
  (JNIEnv* env, jclass clazz, jstring key, jstring value, jboolean overwrite)
{
    char* k = (char *) env->GetStringUTFChars(key, NULL);
    char* v = (char *) env->GetStringUTFChars(value, NULL);
    int err = setenv(k, v, overwrite);
    env->ReleaseStringUTFChars(key, k);
    env->ReleaseStringUTFChars(value, v);
    return err;
}

} // extern "C"

