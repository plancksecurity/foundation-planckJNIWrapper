#include "org_pEp_jniadapter_Engine.h"

#include <stdexcept>
#include <typeinfo>
#include <assert.h>
#include <pEp/pEpEngine.h>

namespace pEp {
    namespace JNIAdapter {
        jint throw_pEp_Exception(JNIEnv *env, PEP_STATUS status)
        {
            jclass ex;
            const char *ex_name;

            switch (status) {
                case PEP_INIT_CANNOT_LOAD_GPGME:
                    ex_name = "org/pEp/jniadapter/InitCannotLoadGPGME";
                    break;
                case PEP_INIT_GPGME_INIT_FAILED:
                    ex_name = "org/pEp/jniadapter/GPGMEInitFailed";
                    break;
                case PEP_INIT_SQLITE3_WITHOUT_MUTEX:
                    ex_name = "org/pEp/jniadapter/SQLite3WithoutMutex";
                    break;
                case PEP_INIT_CANNOT_OPEN_DB:
                    ex_name = "org/pEp/jniadapter/InitCannotOpenDB";
                    break;
                case PEP_INIT_CANNOT_OPEN_SYSTEM_DB:
                    ex_name = "org/pEp/jniadapter/InitCannotOpenSystemDB";
                    break;
                default:
                    assert(0);
                    ex_name = "Exception";
            }

            ex = env->FindClass(ex_name);
            assert(ex);

            if (ex == NULL) {
                ex = env->FindClass("java/lang/NoClassDefFoundError");
                assert(ex);
            }

            return env->ThrowNew(ex, ex_name);
        }

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
                throw std::invalid_argument(fieldname);
            }

            return field;
        }
    };
};

extern "C" {
    using namespace pEp::JNIAdapter;

    JNIEXPORT void JNICALL Java_org_pEp_jniadapter_Engine_init(
            JNIEnv *env,
            jobject me
        )
    {
        PEP_SESSION session = NULL;
        jfieldID handle;

        PEP_STATUS status = init(&session);
        assert(status == PEP_STATUS_OK);

        if (status != PEP_STATUS_OK) {
            throw_pEp_Exception(env, status);
            return;
        }

        assert(session);
        
        try {
            handle = getFieldID(env, "org/pEp/jniadapter/Engine", "handle", "J");
        }
        catch (std::exception& ex) {
            assert(0);
            return;
        }

        jlong _session = (jlong) session;
        env->SetLongField(me, handle, _session);
    }

    JNIEXPORT void JNICALL Java_org_pEp_jniadapter_Engine_release(
            JNIEnv *env,
            jobject me
        )
    {
        PEP_SESSION session = NULL;
        jfieldID handle;

        try {
            handle = getFieldID(env, "org/pEp/jniadapter/Engine", "handle", "J");
        }
        catch (std::exception& ex) {
            assert(0);
            return;
        }

        session = (PEP_SESSION) env->GetLongField(me, handle);
        if (session)
            release(session);
        else
            env->SetLongField(me, handle, jlong(0));
    }
}

