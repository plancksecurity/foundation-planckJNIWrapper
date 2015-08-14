#pragma once

#include <jni.h>
#include <pEp/stringpair.h>
#include <pEp/identity_list.h>
#include <pEp/bloblist.h>

namespace pEp {
    namespace JNIAdapter {
        jclass findClass(JNIEnv *env, const char *classname);

        jfieldID getFieldID(
                JNIEnv *env,
                const char *classname,
                const char *fieldname,
                const char *signature
            );

        jint callIntMethod(
                JNIEnv *env,
                jobject obj,
                const char *methodname
            );

        jlong callLongMethod(
                JNIEnv *env,
                jobject obj,
                const char *methodname
            );

        jobject callObjectMethod(
                JNIEnv *env,
                jobject obj,
                const char *methodname,
                jint index
            );

        jboolean callBooleanMethod(
                JNIEnv *env,
                jobject obj,
                const char *methodname,
                jobject o
            );

        jint outOfMemory(JNIEnv *env);

        jbyteArray from_string(JNIEnv *env, const char *str);
        char *to_string(JNIEnv *env, jbyteArray str);

        jobject from_stringlist(JNIEnv *env, stringlist_t *sl);
        stringlist_t *to_stringlist(JNIEnv *env, jobject obj);

        jobject from_stringpairlist(JNIEnv *env, stringpair_list_t *sl);
        stringpair_list_t *to_stringpairlist(JNIEnv *env, jobject obj);

        jobject from_timestamp(JNIEnv *env, timestamp *ts);
        timestamp *to_timestamp(JNIEnv *env, jobject date);

        jobject from_identity(JNIEnv *env, pEp_identity *ident);
        pEp_identity *to_identity(JNIEnv *env, jobject ident);

        jobject from_identitylist(JNIEnv *env, identity_list *il);
        identity_list *to_identitylist(JNIEnv *env, jobject obj);

        jobject from_bloblist(JNIEnv *env, bloblist_t *bl);
        bloblist_t *to_bloblist(JNIEnv *env, jobject obj);
    };
};

