#include "org_pEp_jniadapter_AbstractEngine.h"

#include <stdexcept>
#include <assert.h>
#include <pthread.h>
#include <pEp/keymanagement.h>

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

    static void *start_routine(void *arg)
    {
        return NULL;
    }

    JNIEXPORT void JNICALL Java_org_pEp_jniadapter_AbstractEngine_startKeyserverLookup(
            JNIEnv *env,
            jobject obj
        )
    {
        pthread_t *thread = NULL;
        jfieldID handle;

        try {
            handle = getFieldID(env, "org/pEp/jniadapter/Engine", "keyserverThread", "J");
        }
        catch (std::exception& ex) {
            assert(0);
            return;
        }

        thread = (pthread_t *) env->GetLongField(obj, handle);
        if (thread)
            return;
 
        thread = (pthread_t *) calloc(1, sizeof(pthread_t));
        assert(thread);
        env->SetLongField(obj, handle, (jlong) thread);

        pthread_create(thread, NULL, start_routine, NULL);
    }

    JNIEXPORT void JNICALL Java_org_pEp_jniadapter_AbstractEngine_stopKeyserverLookup(
            JNIEnv *env,
            jobject obj
        )
    {
        pthread_t *thread = NULL;
        jfieldID handle;

        try {
            handle = getFieldID(env, "org/pEp/jniadapter/Engine", "keyserverThread", "J");
        }
        catch (std::exception& ex) {
            assert(0);
            return;
        }

        thread = (pthread_t *) env->GetLongField(obj, handle);
        if (!thread)
            return;
 
        // stop thread
    }

} // extern "C"


