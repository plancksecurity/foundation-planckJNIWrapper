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
    };
};

