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
                bool isCopy;
                jbyteArray _str = NewByteArray(env, strlen(str));
                jbyte *b = GetByteArrayElements(env, str, &isCopy);
                strcpy((char *)b, str);
                ReleaseByteArrayElements(env, str, b, 0);
                return _str;
            }
            else {
                return NewByteArray(env, 0);
            }
        }

        char *to_string(JNIEnv *env, jbyteArray str)
        {
            bool isCopy;
            jbyte *b = GetByteArrayElements(env, str, &isCopy);
            char *_str = strndup((char *)b, (size_t)GetArrayLength(env, str));
            ReleaseByteArrayElements(env, str, b, JNI_ABORT);
            return _str;
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

