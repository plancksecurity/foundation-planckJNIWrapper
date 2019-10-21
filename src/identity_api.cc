#include <pEp/message_api.h>

#include "jniutils.hh"


extern "C" {

JNIEXPORT jint JNICALL
Java_foundation_pEp_jniadapter_Identity__1getRating(JNIEnv *env, jobject thiz, jint comm_type) {
    return ::rating_from_comm_type((PEP_comm_type) comm_type);

}
} // extern "C"

