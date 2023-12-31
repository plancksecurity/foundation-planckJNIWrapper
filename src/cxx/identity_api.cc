#include <pEp/message_api.h>
#include <pEp/pEpLog.hh>
#include "foundation_pEp_jniadapter_Identity.h"

#include "jniutils.hh"

extern "C" {

JNIEXPORT jint JNICALL Java_foundation_pEp_jniadapter_Identity__1getRating(
        JNIEnv *env,
        jobject thiz,
        jint comm_type)
{
    pEpLog("called");
    return ::rating_from_comm_type(static_cast<PEP_comm_type>(comm_type));
}

} // extern "C"

