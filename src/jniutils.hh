#pragma once

#include <list>
#include <pthread.h>
#include <errno.h>
#include <jni.h>
#include <pEp/stringpair.h>
#include <pEp/identity_list.h>
#include <pEp/bloblist.h>
#include <pEp/message.h>
#include <pEp/sync_api.h>

#if 0 // Enable if log needed
#include <android/log.h>
#define  LOG_TAG    "pEpJNIAdapter"
#define  LOGD(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)
#else
#define  LOGD(...)
#endif

namespace pEp {
    namespace JNIAdapter {
        jclass findClass(JNIEnv *env, const char *classname);

        jfieldID getFieldID(
                JNIEnv *env,
                const char *classname,
                const char *fieldname,
                const char *signature
            );

            jfieldID getFieldID(
                JNIEnv *env,
                const char *classname,
                const char *fieldname,
                const char *signature,
                const jclass clazz
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

        jobject from_Integer(JNIEnv *env, int val);
        int to_Integer(JNIEnv *env, jobject obj);

        jbyteArray from_string(JNIEnv *env, const char *str);
        char *to_string(JNIEnv *env, jbyteArray str);

        jobject from_stringlist(JNIEnv *env, stringlist_t *sl);
        stringlist_t *to_stringlist(JNIEnv *env, jobject obj);

        jobject from_stringpairlist(JNIEnv *env, stringpair_list_t *sl);
        stringpair_list_t *to_stringpairlist(JNIEnv *env, jobject obj);

        jobject from_timestamp(JNIEnv *env, timestamp *ts);
        timestamp *to_timestamp(JNIEnv *env, jobject date);

        jobject from_identity(JNIEnv *env, pEp_identity *ident);
        jobject from_identity(JNIEnv *env, pEp_identity *ident, jclass identityClass);
        pEp_identity *to_identity(JNIEnv *env, jobject obj);

        jobject from_identitylist(JNIEnv *env, identity_list *il);
        identity_list *to_identitylist(JNIEnv *env, jobject obj);

        jobject from_bloblist(JNIEnv *env, bloblist_t *bl);
        bloblist_t *to_bloblist(JNIEnv *env, jobject obj);

        PEP_enc_format to_EncFormat(JNIEnv *env, jobject obj);

        sync_handshake_result to_SyncHandshakeResult(JNIEnv *env, jobject obj);
    };
};

