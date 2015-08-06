#include "jniutils.hh"

#include <stdexcept>
#include <typeinfo>
#include <assert.h>
#include <string>

namespace pEp {
    namespace JNIAdapter {
        jfieldID getFieldID(
                JNIEnv *env,
                const char *classname,
                const char *fieldname,
                const char *signature
            )
        {
            jclass engine = env->FindClass(classname);
            assert(engine);

            if (engine == NULL) {
                jclass ex = env->FindClass("java/lang/NoClassDefFoundError");
                assert(ex);
                env->ThrowNew(ex, classname);
                throw std::bad_cast();
            }

            jfieldID field = env->GetFieldID(engine, fieldname, signature);
            assert(field);

            if (field == NULL) {
                jclass ex = env->FindClass("java/lang/NoSuchFieldError");
                assert(ex);
                env->ThrowNew(ex, fieldname);
                throw std::invalid_argument(std::string(fieldname));
            }

            return field;
        }
    };
};

