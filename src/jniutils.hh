#pragma once

#include <jni.h>

namespace pEp {
    namespace JNIAdapter {
        jclass findClass(JNIEnv *env, const char *classname);

        jfieldID getFieldID(
                JNIEnv *env,
                const char *classname,
                const char *fieldname,
                const char *signature
            );

        jlong callLongMethod(
                JNIEnv *env,
                jobject obj,
                const char *methodname
            );

        jint outOfMemory(JNIEnv *env);

        jbyteArray from_string(JNIEnv *env, const char *str);
        char *to_string(JNIEnv *env, jbyteArray str);

        jobject from_stringlist(JNIEnv *env, stringlist_t *sl);
        stringlist_t *to_stringlist(JNIEnv *env, jobject sl);

        jobject from_stringpairlist(JNIEnv *env, stringpair_list_t *sl);
        stringpair_list_t *to_stringpairlist(JNIEnv *env, jobject sl);

        jobject from_timestamp(JNIEnv *env, timestamp *ts);
        timestamp *to_timestamp(JNIEnv *env, jobject date);

        jobject from_identity(JNIEnv *env, pEp_identity *ident);
        pEp_identity *to_identity(JNIEnv *env, jobject ident);

        jobject from_identitylist(JNIEnv *env, identity_list *il);
        identity_list *to_identitylist(JNIEnv *env, jobject il);

        jobject from_bloblist(JNIEnv *env, bloblist_t *bl);
        bloblist_t *to_bloblist(JNIEnv *env, jobject bl);
    };
};

