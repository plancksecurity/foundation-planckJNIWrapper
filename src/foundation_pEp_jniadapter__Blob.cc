#include <pEp/bloblist.h>
#include <pEp/sync_codec.h>
#include <pEp/distribution_codec.h>
#include <iostream>

#include "jniutils.hh"
#include "throw_pEp_exception.hh"
#include "foundation_pEp_jniadapter__Blob.h"

namespace pEp {
    namespace JNIAdapter {
        static ::bloblist_t *bloblist_ptr(JNIEnv *env, jobject me) {
            jfieldID handle;

            try {
                handle = getFieldID(env, "foundation/pEp/jniadapter/Blob", "mime_type", "Ljava/lang/String");
            }
            catch (std::exception& ex) {
                assert(0);
                return NULL;
            }

            return (::bloblist_t *) (intptr_t) (int64_t) env->GetLongField(me, handle);
        }
    };
};

extern "C" {

using namespace std;
using namespace pEp::JNIAdapter;
JNIEXPORT jbyteArray JNICALL Java_foundation_pEp_jniadapter__1Blob__1dataToXER
  (JNIEnv *env, jobject obj)
{
    bloblist_t *b = to_blob(env, obj);
    char *out = nullptr;

    if(strcasecmp(b->mime_type, "application/pEp.sync") == 0) {
        PEP_STATUS status = ::PER_to_XER_Sync_msg(b->value, (size_t) b->size, &out);
        if (status)
            throw_pEp_Exception(env, status);

        jbyteArray result = from_string(env, out);
        free(out);
        return result;
    }

    if(strcasecmp(b->mime_type, "application/pEp.keyreset") == 0) {
        PEP_STATUS status = ::PER_to_XER_Distribution_msg(b->value, (size_t) b->size, &out);
        if (status)
            throw_pEp_Exception(env, status);

        jbyteArray result = from_string(env, out);
        free(out);
        return result;
    }

    return from_string(env,b->value);
}

}; // extern "C"
