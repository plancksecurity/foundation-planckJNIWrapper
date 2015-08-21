#include "org_pEp_jniadapter_AbstractEngine.h"

#include <stdexcept>
#include <unistd.h>
#include <assert.h>
#include <pthread.h>
#include <pEp/keymanagement.h>

#include "throw_pEp_exception.hh"
#include "jniutils.hh"

extern "C" {
    using namespace pEp::JNIAdapter;
    using namespace pEp::utility;

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

    int examine_identity(pEp_identity *ident, void *arg)
    {
        locked_queue< pEp_identity * > *queue = (locked_queue< pEp_identity * > *) arg;
        queue->push_back(ident);
        return 0;
    }

    pEp_identity *retrieve_next_identity(void *arg)
    {
        locked_queue< pEp_identity * > *queue = (locked_queue< pEp_identity * > *) arg;

        while (!queue->size())
            usleep(100000);

        pEp_identity *ident = queue->front();
        queue->pop_front();
        return ident;
    }

    static void *start_routine(void *arg)
    {
        PEP_STATUS status = do_keymanagement(retrieve_next_identity, arg);

        locked_queue< pEp_identity * > *queue = (locked_queue< pEp_identity * > *) arg;
        delete queue;

        return (void *) status;
    }

    JNIEXPORT void JNICALL Java_org_pEp_jniadapter_AbstractEngine_startKeyserverLookup(
            JNIEnv *env,
            jobject obj
        )
    {
        PEP_SESSION session = (PEP_SESSION) callLongMethod(env, obj, "getHandle");

        pthread_t *thread = NULL;
        locked_queue< pEp_identity * > *queue = NULL;

        jfieldID thread_handle;
        jfieldID queue_handle;

        try {
            thread_handle = getFieldID(env, "org/pEp/jniadapter/Engine", "keyserverThread", "J");
            queue_handle = getFieldID(env, "org/pEp/jniadapter/Engine", "queueThread", "J");
        }
        catch (std::exception& ex) {
            assert(0);
            return;
        }

        thread = (pthread_t *) env->GetLongField(obj, thread_handle);
        if (thread)
            return;
 
        thread = (pthread_t *) calloc(1, sizeof(pthread_t));
        assert(thread);
        env->SetLongField(obj, thread_handle, (jlong) thread);

        queue = new locked_queue< pEp_identity * >();
        env->SetLongField(obj, queue_handle, (jlong) queue);

        pthread_create(thread, NULL, start_routine, (void *) queue);
        register_examine_function(session, examine_identity, (void *) queue);
    }

    JNIEXPORT void JNICALL Java_org_pEp_jniadapter_AbstractEngine_stopKeyserverLookup(
            JNIEnv *env,
            jobject obj
        )
    {
        PEP_SESSION session = (PEP_SESSION) callLongMethod(env, obj, "getHandle");

        pthread_t *thread = NULL;
        locked_queue< pEp_identity * > *queue = NULL;

        jfieldID thread_handle;
        jfieldID queue_handle;

        try {
            thread_handle = getFieldID(env, "org/pEp/jniadapter/Engine", "keyserverThread", "J");
            queue_handle = getFieldID(env, "org/pEp/jniadapter/Engine", "queueThread", "J");
        }
        catch (std::exception& ex) {
            assert(0);
            return;
        }

        thread = (pthread_t *) env->GetLongField(obj, thread_handle);
        if (!thread)
            return;
 
        queue = (locked_queue< pEp_identity * > *) env->GetLongField(obj, queue_handle);

        env->SetLongField(obj, queue_handle, (jlong) 0);
        env->SetLongField(obj, thread_handle, (jlong) 0);

        register_examine_function(session, NULL, NULL);

        queue->push_front(NULL);
        pthread_join(*thread, NULL);
        free(thread);
    }

} // extern "C"


