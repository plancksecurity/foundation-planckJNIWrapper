#pragma once

#include <unordered_map>
#include <thread>
#include <mutex>
#include <jni.h>
#include <pEp/stringpair.h>
#include <pEp/identity_list.h>
#include <pEp/bloblist.h>
#include <pEp/message.h>
#include <pEp/sync_api.h>
#include <pEp/passphrase_cache.hh>
#include <pEp/platform.h>

namespace pEp {
namespace JNIAdapter {
// Global mutex needs to be locked in all constructors which insert their own mutex object
// into the unordered_map (which is thread safe for read, but not for write)
extern std::mutex global_mutex;

// Stores mutex per java object
extern std::unordered_map<long, std::mutex *> engine_objid_mutex;

// needs to be called after create_engine_java_object_mutex()
// and before release_engine_java_object_mutex()
// Thread safe
std::mutex *get_engine_java_object_mutex(JNIEnv *env,
        jobject me);

// Needs to be called exactly once per obj, in the constructor of the obj
// You need to lock a global mutex before calling this function (write to unordered_map)
void create_engine_java_object_mutex(JNIEnv *env,
        jobject me);

// Needs to be called exactly once per obj, in the destructor of this obj
// You need to lock a global mutex before calling this function (write to unordered_map)
void release_engine_java_object_mutex(JNIEnv *env,
        jobject me);


jclass findClass(JNIEnv *env,
        const char *classname);

jfieldID getFieldID(JNIEnv *env,
        const char *classname,
        const char *fieldname,
        const char *signature);

jfieldID getFieldID(JNIEnv *env,
        const char *classname,
        const char *fieldname,
        const char *signature,
        const jclass clazz);

jint callIntMethod(JNIEnv *env,
        jobject obj,
        const char *methodname);

jlong callLongMethod(JNIEnv *env,
        jobject obj,
        const char *methodname);

jobject callObjectMethod(JNIEnv *env,
        jobject obj,
        const char *methodname,
        jint index);

jboolean callBooleanMethod(JNIEnv *env,
        jobject obj,
        const char *methodname,
        jobject o);

jint outOfMemory(JNIEnv *env);

jobject from_Integer(JNIEnv *env,
        int val);

int to_Integer(JNIEnv *env,
        jobject obj);

jbyteArray from_string(JNIEnv *env,
        const char *str);

char *to_string(JNIEnv *env,
        jbyteArray str);

jobject from_stringlist(JNIEnv *env,
        stringlist_t *sl);

stringlist_t *to_stringlist(JNIEnv *env,
        jobject obj);

jobject from_stringpairlist(JNIEnv *env,
        stringpair_list_t *sl);

stringpair_list_t *to_stringpairlist(JNIEnv *env,
        jobject obj);

jobject from_timestamp(JNIEnv *env,
        timestamp *ts);

timestamp *to_timestamp(JNIEnv *env,
        jobject date);

jobject from_identity(JNIEnv *env,
        pEp_identity *ident);

jobject from_identity(JNIEnv *env,
        pEp_identity *ident,
        jclass identityClass);

pEp_identity *to_identity(JNIEnv *env,
        jobject obj);

jobject from_identitylist(JNIEnv *env,
        identity_list *il);

identity_list *to_identitylist(JNIEnv *env,
        jobject obj);

jobject from_bloblist(JNIEnv *env,
        bloblist_t *bl);

bloblist_t *to_blob(JNIEnv *env,
        jobject obj);

bloblist_t *to_bloblist(JNIEnv *env,
        jobject obj);

PEP_enc_format to_EncFormat(JNIEnv *env,
        jobject obj);

PEP_CIPHER_SUITE to_CipherSuite(JNIEnv *env,
        jobject obj);

sync_handshake_result to_SyncHandshakeResult(JNIEnv *env,
        jobject obj);
};
};

