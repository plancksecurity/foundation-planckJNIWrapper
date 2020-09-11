#include <cassert>
#include "jniutils.hh"
#include <pEp/pEpLog.hh>
#ifndef __LP64__
#include <time64.h>
#define time_t time64_t
#define timegm timegm64
#define gmtime_r gmtime64_r
#else
#include <string.h>
#endif

namespace pEp {
    namespace JNIAdapter {
        std::mutex global_mutex;
        std::unordered_map<long, std::mutex*> engine_objid_mutex;

        std::mutex* get_engine_java_object_mutex(
                JNIEnv *env,
                jobject obj
            )
        {
            long engine_obj_id = (long)callLongMethod(env, obj, "getId");
            assert(engine_obj_id);
            pEpLog("for java object id: " << engine_obj_id);
            std::mutex *engine_obj_mutex = engine_objid_mutex.at(engine_obj_id);
            pEpLog("found mutex: " << engine_obj_mutex << " with native_handle: " << engine_obj_mutex->native_handle());
            assert(engine_obj_mutex);
            return engine_obj_mutex;
        }

        void create_engine_java_object_mutex(
                JNIEnv *env,
                jobject obj
            )
        {
            long engine_obj_id = (long)callLongMethod(env, obj, "getId");
            assert(engine_obj_id);
            std::mutex *engine_obj_mutex = new std::mutex();
            pEpLog(engine_obj_mutex << " with native_handle: " << engine_obj_mutex->native_handle() << " for java object id: " << engine_obj_id);
            assert(engine_obj_mutex);
            if(engine_objid_mutex.count(engine_obj_id) > 0) {
                pEpLog("Fatal: mutex already existing for object id: " << engine_obj_id);
                assert(0);
            }
            engine_objid_mutex.insert(std::make_pair(engine_obj_id, engine_obj_mutex ));
        }

        void release_engine_java_object_mutex(
                JNIEnv *env,
                jobject obj
            )
        {
            long engine_obj_id = (long)callLongMethod(env, obj, "getId");
            assert(engine_obj_id);
            std::mutex *engine_obj_mutex = engine_objid_mutex.at(engine_obj_id);
            pEpLog(engine_obj_mutex << " with native_handle: " << engine_obj_mutex->native_handle() << " for java object id: " << engine_obj_id);
            assert(engine_obj_mutex);
            engine_objid_mutex.erase(engine_obj_id);
            delete engine_obj_mutex;
        }

        jclass findClass(JNIEnv *env, const char *classname)
        {
            jclass clazz = env->FindClass(classname);
            if (!clazz)
                fprintf(stderr, "class not found: %s\n", classname);
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
            assert(clazz);

            jfieldID field = env->GetFieldID(clazz, fieldname, signature);
            assert(field);

            if (field == NULL) {
                jclass ex = env->FindClass("java/lang/NoSuchFieldError");
                assert(ex);
                env->ThrowNew(ex, fieldname);
                throw std::invalid_argument(std::string(fieldname));
            }

            env->DeleteLocalRef(clazz);

            return field;
        }

        //TODO: fix/generalize/clean patch added to make keysync work using globalref to class
        jfieldID getFieldID(
                JNIEnv *env,
                const char *classname,
                const char *fieldname,
                const char *signature,
                const jclass clazz
            )
        {

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

            env->DeleteLocalRef(clazz);

            jint result = env->CallIntMethod(obj, method);
            env->ExceptionCheck(); // handle exception in Java
            return result;
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

            env->DeleteLocalRef(clazz);

            jlong result = env->CallLongMethod(obj, method);
            env->ExceptionCheck(); // handle exception in Java
            return result;
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

            env->DeleteLocalRef(clazz);

            jobject result = env->CallObjectMethod(obj, method, index);
            env->ExceptionCheck(); // handle exception in Java
            return result;
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

            env->DeleteLocalRef(clazz);

            jboolean result = env->CallBooleanMethod(obj, method, o);
            env->ExceptionCheck(); // handle exception in Java
            return result;
        }

        jint outOfMemory(JNIEnv *env)
        {
            jclass ex;
            const char *ex_name = "java/lang/OutOfMemoryError";

            ex = env->FindClass(ex_name);
            assert(ex);
            return env->ThrowNew(ex, ex_name);
        }

        jobject from_Integer(JNIEnv *env, int val)
        {
            assert(env);
            jclass clazz = findClass(env, "java/lang/Integer");

            jmethodID constructor = env->GetMethodID(clazz, "<init>", "(I)V");
            assert(constructor);

            jobject obj = env->NewObject(clazz, constructor, val);
            assert(obj);
            return obj;
        }

        int to_Integer(JNIEnv *env, jobject obj)
        {
            assert(env && obj);
            int _val = callIntMethod(env, obj, "intValue");
            return _val;
        }

        jbyteArray from_string(JNIEnv *env, const char *str)
        {
            if (str && str[0]) {
                jboolean isCopy;
                size_t l = strlen(str);
                jbyteArray _str = env->NewByteArray(l);
                env->SetByteArrayRegion(_str, 0, l, (jbyte*)str);
                return _str;
            }
            else if (str) {
                return env->NewByteArray(0);
            } else {
                return (jbyteArray) NULL;
            }
        }

        char *to_string(JNIEnv *env, jbyteArray str)
        {
            if (str == NULL)
                return NULL;

            size_t l = env->GetArrayLength(str);
            char *_str = (char *) calloc(1,l+1);
            assert(_str);
            env->GetByteArrayRegion(str, 0, l, (jbyte*)_str);
            return _str;
        }

        jobject from_stringlist(JNIEnv *env, stringlist_t *sl)
        {
            if (!sl)
                return (jobject) NULL;

            jclass clazz = findClass(env, "java/util/Vector");
            jmethodID constructor = env->GetMethodID(clazz, "<init>", "()V");
            assert(constructor);
            jobject obj = env->NewObject(clazz, constructor);
            assert(obj);

            stringlist_t *_sl;
            for (_sl = sl; _sl && _sl->value; _sl = _sl->next) {
                jobject o = from_string(env, _sl->value);
                callBooleanMethod(env, obj, "add", o);
            }

            env->DeleteLocalRef(clazz);

            return obj;
        }

        stringlist_t *to_stringlist(JNIEnv *env, jobject obj)
        {
            if (!obj)
                return NULL;

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
                env->DeleteLocalRef(o);
                free(str);
            }

            return sl;
        }

        jobject from_stringpairlist(JNIEnv *env, stringpair_list_t *sl)
        {
            if (!sl)
                return (jobject) NULL;

            jclass clazz = findClass(env, "java/util/ArrayList");
            jclass clazz_pair = findClass(env, "foundation/pEp/jniadapter/Pair");
            jmethodID constructor = env->GetMethodID(clazz, "<init>", "()V");
            assert(constructor);
            jmethodID constructor_pair = env->GetMethodID(clazz_pair, "<init>",
                    "(Ljava/lang/Object;Ljava/lang/Object;)V");
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

                env->DeleteLocalRef(first);
                env->DeleteLocalRef(second);
                env->DeleteLocalRef(pair);


            }

            env->DeleteLocalRef(clazz);
            env->DeleteLocalRef(clazz_pair);

            return obj;
        }

        stringpair_list_t *to_stringpairlist(JNIEnv *env, jobject obj)
        {
            if (!obj)
                return NULL;

            jint size = callIntMethod(env, obj, "size");
            if (size == 0)
                return NULL;

            jfieldID first_id = getFieldID(env, "foundation/pEp/jniadapter/Pair",
                    "first", "Ljava/lang/Object;");
            jfieldID second_id = getFieldID(env, "foundation/pEp/jniadapter/Pair",
                    "second", "Ljava/lang/Object;");

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
                env->DeleteLocalRef(pair);
                free(first_str);
                free(second_str);

                _sl = stringpair_list_add(_sl, sp);
            }

            return sl;
        }

        jobject from_timestamp(JNIEnv *env, timestamp *ts)
        {
            if (!ts)
                return (jobject) NULL;

            //LOGD("/* Seconds (0-60) */  FROM   :%d", ts->tm_sec);
            //LOGD("/* Minutes (0-59) */         :%d", ts->tm_min);    
            //LOGD("/* Hours (0-23) */           :%d", ts->tm_hour);   
            //LOGD("/* Day of the month (1-31) */:%d", ts->tm_mday);   
            //LOGD("/* Month (0-11) */           :%d", ts->tm_mon);    
            //LOGD("/* Year - 1900 */            :%d", ts->tm_year);   

            time_t t = timegm(ts)*1000;
            //LOGD( "TimeGM returns : %lld", t);
            jclass clazz = findClass(env, "java/util/Date");
            jmethodID constructor = env->GetMethodID(clazz, "<init>", "(J)V");
            assert(constructor);

            jobject result =  env->NewObject(clazz, constructor, (jlong) t);

            env->DeleteLocalRef(clazz);

            return result;

        }

        timestamp *to_timestamp(JNIEnv *env, jobject date)
        {
            if (!date)
                return NULL;

            jlong t = callLongMethod(env, date, "getTime");
            //LOGD( "Set Time to : %lld", t);
            timestamp *ts = (timestamp*)calloc(1, sizeof(timestamp));
            assert(ts);
            if (ts == NULL)
                return NULL;

            if (t){
                time_t clock = t/1000;
                gmtime_r(&clock, ts);

                //LOGD("/* Seconds (0-60) */  TO     :%d", ts->tm_sec);    
                //LOGD("/* Minutes (0-59) */         :%d", ts->tm_min);    
                //LOGD("/* Hours (0-23) */           :%d", ts->tm_hour);   
                //LOGD("/* Day of the month (1-31) */:%d", ts->tm_mday);   
                //LOGD("/* Month (0-11) */           :%d", ts->tm_mon);    
                //LOGD("/* Year - 1900 */            :%d", ts->tm_year);   
            }

            return ts;
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

        //TODO: fix/generalize/clean patch added to make keysync work using globalref to class
        static void _setStringField(JNIEnv *env, const char *classname,
                jobject obj, const char *name, const char *value, const jclass clazz)
        {
            if (value) {
                jfieldID fieldID = getFieldID(env, classname, name, "[B", clazz);
                env->SetObjectField(obj, fieldID,
                        reinterpret_cast<jobject>(from_string(env, value)));

            }
        }

        jobject from_identity(JNIEnv *env, pEp_identity *ident)
        {
            if (!ident)
                return (jobject) NULL;

            static const char *classname = "foundation/pEp/jniadapter/_Identity";
            jclass clazz = findClass(env, classname);
            jmethodID constructor = env->GetMethodID(clazz, "<init>", "()V");
            assert(constructor);
            jobject obj = env->NewObject(clazz, constructor);

            env->DeleteLocalRef(clazz);
            
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

                jfieldID flags_id = getFieldID(env, classname, "flags", "I");
                env->SetIntField(obj, flags_id, (jint) (int) ident->flags);
            }

            return obj;
        }

        //TODO: fix/generalize/clean patch added to make keysync work using globalref to class
        jobject from_identity(JNIEnv *env, pEp_identity *ident, jclass identityClass)
        {
            if (!ident)
                return (jobject) NULL;

            static const char *classname = "foundation/pEp/jniadapter/_Identity";
            jmethodID constructor = env->GetMethodID(identityClass, "<init>", "()V");
            assert(constructor);
            jobject obj = env->NewObject(identityClass, constructor);

            if (ident) {
                _setStringField(env, classname, obj, "address", ident->address, identityClass);
                _setStringField(env, classname, obj, "fpr", ident->fpr, identityClass);
                _setStringField(env, classname, obj, "user_id", ident->user_id, identityClass);
                _setStringField(env, classname, obj, "username", ident->username, identityClass);

                jfieldID comm_type_id = getFieldID(env, classname, "comm_type", "I", identityClass);
                env->SetIntField(obj, comm_type_id, (jint) (int) ident->comm_type);

                _setStringField(env, classname, obj, "lang", ident->lang, identityClass);

                jfieldID me_id = getFieldID(env, classname, "me", "Z", identityClass);
                env->SetBooleanField(obj, me_id, (jboolean) ident->me);

                jfieldID flags_id = getFieldID(env, classname, "flags", "I", identityClass);
                env->SetIntField(obj, flags_id, (jint) (int) ident->flags);
            }

            return obj;
        }

        char *_getStringField(JNIEnv *env, const char *classname, jobject obj,
                const char *name)
        {
            jfieldID fieldID = getFieldID(env, classname, name, "[B");
            jobject fobj = env->GetObjectField(obj, fieldID);

            char *res =  to_string(env, reinterpret_cast<jbyteArray>(fobj));

            env->DeleteLocalRef(fobj);
            return res;
        }

        pEp_identity *to_identity(JNIEnv *env, jobject obj)
        {
            if (!obj)
                return NULL;

            static const char *classname = "foundation/pEp/jniadapter/_Identity";
            pEp_identity *ident = new_identity(NULL, NULL, NULL, NULL);

            ident->address = _getStringField(env, classname, obj, "address");
            ident->fpr = _getStringField(env, classname, obj, "fpr");
            ident->user_id = _getStringField(env, classname, obj, "user_id");
            ident->username = _getStringField(env, classname, obj, "username");

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
            
            jfieldID flags_id = getFieldID(env, classname, "flags", "I");
            ident->flags = (identity_flags_t) (int) env->GetIntField(obj, flags_id);

            return ident;
        }

        jobject from_identitylist(JNIEnv *env, identity_list *il)
        {
            if (!il)
                return (jobject) NULL;

            jclass clazz = findClass(env, "java/util/Vector");
            jmethodID constructor = env->GetMethodID(clazz, "<init>", "()V");
            assert(constructor);
            jobject obj = env->NewObject(clazz, constructor);
            assert(obj);

            identity_list *_il;
            for (_il = il; _il && _il->ident; _il = _il->next) {
                jobject o = from_identity(env, _il->ident);
                callBooleanMethod(env, obj, "add", o);
            }

            env->DeleteLocalRef(clazz);

            return obj;
        }

        identity_list *to_identitylist(JNIEnv *env, jobject obj)
        {
            if (!obj)
                return NULL;

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
                env->DeleteLocalRef(o);
            }

            return il;
        }

        jobject _from_blob(JNIEnv *env, bloblist_t *b)
        {
            if (!b)
                return (jobject) NULL;

            static const char *classname = "foundation/pEp/jniadapter/_Blob";
            jclass clazz = findClass(env, classname);
            jmethodID constructor = env->GetMethodID(clazz, "<init>", "()V");
            assert(constructor);
            jobject obj = env->NewObject(clazz, constructor);
            
            env->DeleteLocalRef(clazz);

            jfieldID fieldID = getFieldID(env, classname, "data", "[B");
            jbyteArray _data = env->NewByteArray((jsize) b->size);
            env->SetByteArrayRegion(_data, 0, b->size, (jbyte*)b->value);
            env->SetObjectField(obj, fieldID, reinterpret_cast<jobject>(_data));
            _setStringField(env, classname, obj, "mime_type", b->mime_type);
            _setStringField(env, classname, obj, "filename", b->filename);

            return obj;
        }

        jobject from_bloblist(JNIEnv *env, bloblist_t *bl)
        {
            if (!bl)
                return (jobject) NULL;

            jclass clazz = findClass(env, "java/util/Vector");
            jmethodID constructor = env->GetMethodID(clazz, "<init>", "()V");
            assert(constructor);
            jobject obj = env->NewObject(clazz, constructor);
            assert(obj);

            env->DeleteLocalRef(clazz);

            bloblist_t *_bl;
            for (_bl = bl; _bl && _bl->value; _bl = _bl->next) {
                jobject o = _from_blob(env, _bl);
                if(o)
                    callBooleanMethod(env, obj, "add", o);
            }

            return obj;
        }

        bloblist_t *to_blob(JNIEnv *env, jobject obj)
        {
            if (!obj)
                return NULL;

            static const char *classname = "foundation/pEp/jniadapter/_Blob";
            jclass clazz = findClass(env, classname);

            char *mime_type = _getStringField(env, classname, obj, "mime_type");
            char *filename = _getStringField(env, classname, obj, "filename");

            jfieldID data_id = getFieldID(env, classname, "data", "[B");
            jbyteArray _data = reinterpret_cast<jbyteArray>(env->GetObjectField(obj, data_id));
            size_t size = (size_t) env->GetArrayLength(_data);
            char *b = (char *) malloc(size);
            assert(b);

            env->GetByteArrayRegion(_data, 0, size, (jbyte*)b);
            bloblist_t *bl = new_bloblist( b, size, mime_type, filename);

            free(mime_type);
            free(filename);
            return bl;
        }


        bloblist_t *to_bloblist(JNIEnv *env, jobject obj)
        {
            if (!obj)
                return NULL;

            jint size = callIntMethod(env, obj, "size");
            if (size == 0)
                return NULL;

            bloblist_t *bl = new_bloblist(NULL, 0, NULL, NULL);
            bloblist_t *_bl;
            _bl = bl;
            jint i;
            for (i = 0; i < (int) size; i++) {
                jobject o = callObjectMethod(env, obj, "get", i);
                bloblist_t *b = to_blob(env, o);
                _bl = bloblist_add(_bl, b->value, b->size, b->mime_type, b->filename);
                env->DeleteLocalRef(o);
            }

            return bl;
        }

        PEP_enc_format to_EncFormat(JNIEnv *env, jobject obj)
        {
            static const char *classname = "foundation/pEp/jniadapter/Message$EncFormat";
            jclass clazz_enc_format = findClass(env, classname);
            jfieldID field_value = env->GetFieldID(clazz_enc_format, "value", "I");
            assert(field_value);

            env->DeleteLocalRef(clazz_enc_format);
            return (PEP_enc_format) env->GetIntField(obj, field_value);
        }

        PEP_CIPHER_SUITE to_CipherSuite(JNIEnv *env, jobject obj)
        {
            static const char *classname = "foundation/pEp/jniadapter/CipherSuite";
            jclass clazz_enc_format = findClass(env, classname);
            jfieldID field_value = env->GetFieldID(clazz_enc_format, "value", "I");
            assert(field_value);

            env->DeleteLocalRef(clazz_enc_format);
            return (PEP_CIPHER_SUITE) env->GetIntField(obj, field_value);
        }

        sync_handshake_result to_SyncHandshakeResult(JNIEnv *env, jobject obj)
        {
            static const char *classname = "foundation/pEp/jniadapter/SyncHandshakeResult";
            jclass clazz_enc_format = findClass(env, classname);
            jfieldID field_value = env->GetFieldID(clazz_enc_format, "value", "I");
            assert(field_value);

            env->DeleteLocalRef(clazz_enc_format);
            return (sync_handshake_result) env->GetIntField(obj, field_value);
        }
    };
};
