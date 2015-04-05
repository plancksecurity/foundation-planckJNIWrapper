#pragma once

#include <jni.h>

namespace pEp {
    namespace JNIAdapter {
        jfieldID getFieldID(
                JNIEnv *env,
                const char *classname,
                const char *fieldname,
                const char *signature
            );
    };
};

