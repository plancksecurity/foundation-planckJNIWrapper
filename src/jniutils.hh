#pragma once

#include <list>
#include <pthread.h>
#include <jni.h>
#include <pEp/stringpair.h>
#include <pEp/identity_list.h>
#include <pEp/bloblist.h>

namespace pEp {
    namespace utility {
        using namespace std;

        class mutex {
            pthread_mutex_t _mutex;

        public:
            mutex() {
                pthread_mutex_init(&_mutex, NULL);
            }
            ~mutex() {
                pthread_mutex_destroy(&_mutex);
            }
            void lock() {
                pthread_mutex_lock(&_mutex);
            }
            void unlock() {
                pthread_mutex_unlock(&_mutex);
            }
        };

        template<class T> class lock_guard {
            T& _mtx;

        public:
            lock_guard(T& mtx) : _mtx(mtx) {
                _mtx.lock();
            }
            ~lock_guard() {
                _mtx.unlock();
            }
        };

        template<class T> class locked_queue
        {
            mutex _mtx;
            list<T> _q;

        public:
            T& back()
            {
                lock_guard<mutex> lg(_mtx);
                return _q.back();
            }
            T& front()
            {
                lock_guard<mutex> lg(_mtx);
                return _q.front();
            }
            void pop_back()
            {
                lock_guard<mutex> lg(_mtx);
                _q.pop_back();
            }
            void pop_front()
            {
                lock_guard<mutex> lg(_mtx);
                _q.pop_front();
            }
            void push_back(const T& data)
            {
                lock_guard<mutex> lg(_mtx);
                _q.push_back(data);
            }
            void push_front(const T& data)
            {
                lock_guard<mutex> lg(_mtx);
                _q.push_front(data);
            }
            size_t size()
            {
                lock_guard<mutex> lg(_mtx);
                return _q.size();
            }
        };
    }

    namespace JNIAdapter {
        jclass findClass(JNIEnv *env, const char *classname);

        jfieldID getFieldID(
                JNIEnv *env,
                const char *classname,
                const char *fieldname,
                const char *signature
            );

        jint callIntMethod(
                JNIEnv *env,
                jobject obj,
                const char *methodname
            );

        jlong callLongMethod(
                JNIEnv *env,
                jobject obj,
                const char *methodname
            );

        jobject callObjectMethod(
                JNIEnv *env,
                jobject obj,
                const char *methodname,
                jint index
            );

        jboolean callBooleanMethod(
                JNIEnv *env,
                jobject obj,
                const char *methodname,
                jobject o
            );

        jint outOfMemory(JNIEnv *env);

        jbyteArray from_string(JNIEnv *env, const char *str);
        char *to_string(JNIEnv *env, jbyteArray str);

        jobject from_stringlist(JNIEnv *env, stringlist_t *sl);
        stringlist_t *to_stringlist(JNIEnv *env, jobject obj);

        jobject from_stringpairlist(JNIEnv *env, stringpair_list_t *sl);
        stringpair_list_t *to_stringpairlist(JNIEnv *env, jobject obj);

        jobject from_timestamp(JNIEnv *env, timestamp *ts);
        timestamp *to_timestamp(JNIEnv *env, jobject date);

        jobject from_identity(JNIEnv *env, pEp_identity *ident);
        pEp_identity *to_identity(JNIEnv *env, jobject obj);

        jobject from_identitylist(JNIEnv *env, identity_list *il);
        identity_list *to_identitylist(JNIEnv *env, jobject obj);

        jobject from_bloblist(JNIEnv *env, bloblist_t *bl);
        bloblist_t *to_bloblist(JNIEnv *env, jobject obj);
    };
};

