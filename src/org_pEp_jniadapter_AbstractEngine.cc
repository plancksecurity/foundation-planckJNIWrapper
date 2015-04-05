#include "org_pEp_jniadapter_AbstractEngine.h"

#include <stdexcept>
#include <assert.h>
#include <pEp/pEpEngine.h>

#include "throw_pEp_exception.hh"
#include "jniutils.hh"

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

