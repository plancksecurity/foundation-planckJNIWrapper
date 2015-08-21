#include "jniutils.hh"

#include <stdexcept>
#include <typeinfo>
#include <assert.h>
#include <string>

namespace pEp {
    namespace JNIAdapter {
        jclass findClass(JNIEnv *env, const char *classname)
        {
            jclass clazz = env->FindClass(classname);
            assert(clazz);

            if (clazz == NULL) {
                jclass ex = env->FindClass("java/lang/NoClassDefFoundError");
                assert(ex);
                env->ThrowNew(ex, classname);
                throw std::bad_cast();
            }
            
            return clazz;
        }

        jfieldID getFieldID(
                JNIEnv *env,
                const char *classname,
                const char *fieldname,
                const char *signature
            )
        {
            jclass clazz = findClass(env, classname);

            jfieldID field = env->GetFieldID(clazz, fieldname, signature);
            assert(field);

            if (field == NULL) {
                jclass ex = env->FindClass("java/lang/NoSuchFieldError");
                assert(ex);
                env->ThrowNew(ex, fieldname);
                throw std::invalid_argument(std::string(fieldname));
            }

            return field;
        }

        jint callIntMethod(
                JNIEnv *env,
                jobject obj,
                const char *methodname
            )
        {
            jclass clazz = env->GetObjectClass(obj);
            assert(clazz);

            jmethodID method = env->GetMethodID(clazz, methodname, "()I");
            if (method == NULL) {
                jclass ex = env->FindClass("java/lang/NoSuchMethodError");
                assert(ex);
                env->ThrowNew(ex, methodname);
                throw std::invalid_argument(std::string(methodname));
            }

            return env->CallIntMethod(obj, method);
        }

        jlong callLongMethod(
                JNIEnv *env,
                jobject obj,
                const char *methodname
            )
        {
            jclass clazz = env->GetObjectClass(obj);
            assert(clazz);

            jmethodID method = env->GetMethodID(clazz, methodname, "()J");
            if (method == NULL) {
                jclass ex = env->FindClass("java/lang/NoSuchMethodError");
                assert(ex);
                env->ThrowNew(ex, methodname);
                throw std::invalid_argument(std::string(methodname));
            }

            return env->CallLongMethod(obj, method);
        }

        jobject callObjectMethod(
                JNIEnv *env,
                jobject obj,
                const char *methodname,
                jint index
            )
        {
            jclass clazz = env->GetObjectClass(obj);
            assert(clazz);

            jmethodID method = env->GetMethodID(clazz, methodname,
                    "(I)Ljava/lang/Object;");
            if (method == NULL) {
                jclass ex = env->FindClass("java/lang/NoSuchMethodError");
                assert(ex);
                env->ThrowNew(ex, methodname);
                throw std::invalid_argument(std::string(methodname));
            }

            return env->CallObjectMethod(obj, method, index);
        }

        jboolean callBooleanMethod(
                JNIEnv *env,
                jobject obj,
                const char *methodname,
                jobject o
            )
        {
            jclass clazz = env->GetObjectClass(obj);
            assert(clazz);

            jmethodID method = env->GetMethodID(clazz, methodname,
                    "(Ljava/lang/Object;)Z");
            if (method == NULL) {
                jclass ex = env->FindClass("java/lang/NoSuchMethodError");
                assert(ex);
                env->ThrowNew(ex, methodname);
                throw std::invalid_argument(std::string(methodname));
            }

            return env->CallLongMethod(obj, method, o);
        }

        jint outOfMemory(JNIEnv *env)
        {
            jclass ex;
            const char *ex_name = "java/lang/OutOfMemoryError";

            ex = env->FindClass(ex_name);
            assert(ex);
            return env->ThrowNew(ex, ex_name);
        }

        jbyteArray from_string(JNIEnv *env, const char *str)
        {
            if (str && str[0]) {
                jboolean isCopy;
                jbyteArray _str = env->NewByteArray(strlen(str));
                jbyte *b = env->GetByteArrayElements(_str, &isCopy);
                strcpy((char *)b, str);
                env->ReleaseByteArrayElements(_str, b, 0);
                return _str;
            }
            else {
                return env->NewByteArray(0);
            }
        }

        char *to_string(JNIEnv *env, jbyteArray str)
        {
            jboolean isCopy;
            jbyte *b = env->GetByteArrayElements(str, &isCopy);
            char *_str = strndup((char *)b, (size_t)env->GetArrayLength(str));
            env->ReleaseByteArrayElements(str, b, JNI_ABORT);
            return _str;
        }

        jobject from_stringlist(JNIEnv *env, stringlist_t *sl)
        {
            jclass clazz = findClass(env, "java/util/ArrayList");
            jmethodID constructor = env->GetMethodID(clazz, "<init>", "()V");
            assert(constructor);
            jobject obj = env->NewObject(clazz, constructor);
            assert(obj);

            stringlist_t *_sl;
            for (_sl = sl; _sl && _sl->value; _sl = _sl->next) {
                jobject o = from_string(env, _sl->value);
                callBooleanMethod(env, obj, "add", o);
            }

            return obj;
        }

        stringlist_t *to_stringlist(JNIEnv *env, jobject obj)
        {
            jint size = callIntMethod(env, obj, "size");
            if (size == 0)
                return NULL;

            stringlist_t *sl = new_stringlist(NULL);
            stringlist_t *_sl;
            jint i;
            for (_sl = sl, i = 0; i < (int) size; i++) {
                jobject o = callObjectMethod(env, obj, "get", i);
                jbyteArray a = reinterpret_cast<jbyteArray>(o);
                char * str = to_string(env, a);
                _sl = stringlist_add(_sl, str);
                free(str);
            }

            return sl;
        }

        jobject from_stringpairlist(JNIEnv *env, stringpair_list_t *sl)
        {
            jclass clazz = findClass(env, "java/util/Vector");
            jclass clazz_pair = findClass(env, "org/pEp/jniadapter/Pair");
            jmethodID constructor = env->GetMethodID(clazz, "<init>", "()V");
            assert(constructor);
            jmethodID constructor_pair = env->GetMethodID(clazz_pair, "<init>",
                    "([B[B)V");
            assert(constructor_pair);

            jobject obj = env->NewObject(clazz, constructor);
            assert(obj);

            stringpair_list_t *_sl;
            for (_sl = sl; _sl && _sl->value; _sl = _sl->next) {
                assert(_sl->value->key);
                assert(_sl->value->value);

                jbyteArray first = from_string(env, _sl->value->key);
                jbyteArray second = from_string(env, _sl->value->value);
                jobject pair = env->NewObject(clazz_pair, constructor_pair,
                        first, second);
                callBooleanMethod(env, obj, "add", pair);
            }

            return obj;
        }

        stringpair_list_t *to_stringpairlist(JNIEnv *env, jobject obj)
        {
            jint size = callIntMethod(env, obj, "size");
            if (size == 0)
                return NULL;

            jfieldID first_id = getFieldID(env, "org/pEp/jniadapter/Pair",
                    "first", "Ljava/lang/Object");
            jfieldID second_id = getFieldID(env, "org/pEp/jniadapter/Pair",
                    "second", "Ljava/lang/Object");

            stringpair_list_t *sl = new_stringpair_list(NULL);
            stringpair_list_t *_sl;
            jint i;
            for (_sl = sl, i = 0; i < (int) size; i++) {
                jobject pair = callObjectMethod(env, obj, "get", i);
                jbyteArray first =
                    reinterpret_cast<jbyteArray>(env->GetObjectField(pair,
                                first_id));
                jbyteArray second =
                    reinterpret_cast<jbyteArray>(env->GetObjectField(pair,
                                second_id));

                char *first_str = to_string(env, first);
                char *second_str = to_string(env, second);
                stringpair_t *sp = new_stringpair(first_str, second_str);
                free(first_str);
                free(second_str);

                _sl = stringpair_list_add(_sl, sp);
            }

            return sl;
        }

        jobject from_timestamp(JNIEnv *env, timestamp *ts)
        {
            time_t t = timegm(ts);
            jclass clazz = findClass(env, "java/util/Date");
            jmethodID constructor = env->GetMethodID(clazz, "<init>", "(J)V");
            assert(constructor);
            return env->NewObject(clazz, constructor, (jlong) t);
        }

        timestamp *to_timestamp(JNIEnv *env, jobject date)
        {
            time_t t = (time_t) callLongMethod(env, date, "getTime");
            return new_timestamp(t);
        }

        static void _setStringField(JNIEnv *env, const char *classname,
                jobject obj, const char *name, const char *value)
        {
            if (value) {
                jfieldID fieldID = getFieldID(env, classname, name, "[B");
                env->SetObjectField(obj, fieldID,
                        reinterpret_cast<jobject>(from_string(env, value)));
            }
        }

        jobject from_identity(JNIEnv *env, pEp_identity *ident)
        {
            static const char *classname = "org/pEp/jniadapter/_Identity";
            jclass clazz = findClass(env, classname);
            jmethodID constructor = env->GetMethodID(clazz, "<init>", "()V");
            assert(constructor);
            jobject obj = env->NewObject(clazz, constructor);
            
            if (ident) {
                _setStringField(env, classname, obj, "address", ident->address);
                _setStringField(env, classname, obj, "fpr", ident->fpr);
                _setStringField(env, classname, obj, "user_id", ident->user_id);
                _setStringField(env, classname, obj, "username", ident->username);

                jfieldID comm_type_id = getFieldID(env, classname, "comm_type", "I");
                env->SetIntField(obj, comm_type_id, (jint) (int) ident->comm_type);

                _setStringField(env, classname, obj, "lang", ident->lang);

                jfieldID me_id = getFieldID(env, classname, "me", "Z");
                env->SetBooleanField(obj, me_id, (jboolean) ident->me);
            }

            return obj;
        }

        char *_getStringField(JNIEnv *env, const char *classname, jobject obj,
                const char *name)
        {
            jfieldID fieldID = getFieldID(env, classname, name, "[B");
            jbyteArray field =
                reinterpret_cast<jbyteArray>(env->GetObjectField(obj,
                            fieldID));
            return to_string(env, field);
        }

        pEp_identity *to_identity(JNIEnv *env, jobject obj)
        {
            static const char *classname = "org/pEp/jniadapter/_Identity";
            pEp_identity *ident = new_identity(NULL, NULL, NULL, NULL);

            ident->address = _getStringField(env, classname, obj, "address");
            ident->address_size = strlen(ident->address);
            ident->fpr = _getStringField(env, classname, obj, "fpr");
            ident->fpr_size = strlen(ident->fpr);
            ident->user_id = _getStringField(env, classname, obj, "user_id");
            ident->user_id_size = strlen(ident->user_id);
            ident->username = _getStringField(env, classname, obj, "username");
            ident->username_size = strlen(ident->username);

            jfieldID comm_type_id = getFieldID(env, classname, "comm_type", "I");
            ident->comm_type = (PEP_comm_type) (int) env->GetIntField(obj, comm_type_id);

            char *lang = _getStringField(env, classname, obj, "lang");
            if (lang && lang[0]) {
                ident->lang[0] = lang[0];
                ident->lang[1] = lang[1];
            }
            free(lang);

            jfieldID me_id = getFieldID(env, classname, "me", "Z");
            ident->me = (bool) env->GetBooleanField(obj, me_id);

            return ident;
        }

        jobject from_identitylist(JNIEnv *env, identity_list *il)
        {
            jclass clazz = findClass(env, "java/util/ArrayList");
            jmethodID constructor = env->GetMethodID(clazz, "<init>", "()V");
            assert(constructor);
            jobject obj = env->NewObject(clazz, constructor);
            assert(obj);

            identity_list *_il;
            for (_il = il; _il && _il->ident; _il = _il->next) {
                jobject o = from_identity(env, _il->ident);
                callBooleanMethod(env, obj, "add", o);
            }

            return obj;
        }

        identity_list *to_identitylist(JNIEnv *env, jobject obj)
        {
            jint size = callIntMethod(env, obj, "size");
            if (size == 0)
                return NULL;

            identity_list *il = new_identity_list(NULL);
            identity_list *_il;
            jint i;
            for (_il = il, i = 0; i < (int) size; i++) {
                jobject o = callObjectMethod(env, obj, "get", i);
                pEp_identity* ident = to_identity(env, o);
                _il = identity_list_add(_il, ident);
            }

            return il;
        }

        jobject _from_blob(JNIEnv *env, bloblist_t *b)
        {
            static const char *classname = "org/pEp/jniadapter/_Blob";
            jclass clazz = findClass(env, classname);
            jmethodID constructor = env->GetMethodID(clazz, "<init>", "()V");
            assert(constructor);
            jobject obj = env->NewObject(clazz, constructor);
            
            jfieldID fieldID = getFieldID(env, classname, "data", "[B");
            jboolean isCopy;
            jbyteArray _data = env->NewByteArray((jsize) b->size);
            jbyte *_b = env->GetByteArrayElements(_data, &isCopy);
            memcpy((char *)_b, b->value, b->size);
            env->ReleaseByteArrayElements(_data, _b, 0);

            _setStringField(env, classname, obj, "mime_type", b->mime_type);
            _setStringField(env, classname, obj, "filename", b->filename);

            return obj;
        }

        jobject from_bloblist(JNIEnv *env, bloblist_t *bl)
        {
            jclass clazz = findClass(env, "java/util/ArrayList");
            jmethodID constructor = env->GetMethodID(clazz, "<init>", "()V");
            assert(constructor);
            jobject obj = env->NewObject(clazz, constructor);
            assert(obj);

            bloblist_t *_bl;
            for (_bl = bl; _bl && _bl->value; _bl = _bl->next) {
                jobject o = _from_blob(env, _bl);
                callBooleanMethod(env, obj, "add", o);
            }

            return obj;
        }

        bloblist_t *to_bloblist(JNIEnv *env, jobject obj)
        {
            jint size = callIntMethod(env, obj, "size");
            if (size == 0)
                return NULL;

            static const char *classname = "org/pEp/jniadapter/_Blob";
            jclass clazz = findClass(env, classname);
            jfieldID data_id = getFieldID(env, classname, "data", "[B");

            bloblist_t *bl = new_bloblist(NULL, 0, NULL, NULL);
            bloblist_t *_bl;
            jint i;
            for (_bl = bl, i = 0; i < (int) size; i++) {
                jobject o = callObjectMethod(env, obj, "get", i);
                char *mime_type = _getStringField(env, classname, o,
                        "mime_type");
                char *filename = _getStringField(env, classname, o,
                        "filename");

                jbyteArray _data =
                    reinterpret_cast<jbyteArray>(env->GetObjectField(o,
                                data_id));
                
                size_t size = (size_t) env->GetArrayLength(_data);
                char *b = (char *) malloc(size);
                assert(b);

                jboolean isCopy;
                jbyte *_b = env->GetByteArrayElements(_data, &isCopy);
                memcpy(b, _b, size);
                env->ReleaseByteArrayElements(_data, _b, JNI_ABORT);

                _bl = bloblist_add(_bl, b, size, mime_type, filename);

                free(mime_type);
                free(filename);
            }

            return bl;
        }
    };
};

