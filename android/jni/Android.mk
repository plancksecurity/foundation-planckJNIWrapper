LOCAL_PATH:= $(call my-dir)
ENGINE_PATH := $(LOCAL_PATH)/../../../pEpEngine
GPGBUILD:= $(LOCAL_PATH)/../external/data/data/pep.android.k9/app_opt

include $(CLEAR_VARS)
LOCAL_MODULE := libassuan
LOCAL_SRC_FILES := $(GPGBUILD)/lib/libassuan.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libcurl
LOCAL_SRC_FILES := $(GPGBUILD)/lib/libcurl.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libgcrypt
LOCAL_SRC_FILES := $(GPGBUILD)/lib/libgcrypt.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libgpg-error
LOCAL_SRC_FILES := $(GPGBUILD)/lib/libgpg-error.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libgpgme
LOCAL_SRC_FILES := $(GPGBUILD)/lib/libgpgme.so
LOCAL_EXPORT_C_INCLUDES := $(GPGBUILD)/include
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libksba
LOCAL_SRC_FILES := $(GPGBUILD)/lib/libksba.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libiconv
LOCAL_SRC_FILES := $(GPGBUILD)/lib/libiconv.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libuuid
LOCAL_SRC_FILES := $(GPGBUILD)/lib/libuuid.a
include $(PREBUILT_STATIC_LIBRARY)


include $(CLEAR_VARS)
LOCAL_MODULE := libetpan
LOCAL_SRC_FILES := $(LOCAL_PATH)/../build/libetpan-android-1/libs/$(TARGET_ARCH_ABI)/libetpan.a
LOCAL_EXPORT_C_INCLUDES += $(LOCAL_PATH)/../build/libetpan-android-1/include
LOCAL_EXPORT_LDLIBS := -lz
include $(PREBUILT_STATIC_LIBRARY)

#Take out Engine Headers
$(shell sh $(ENGINE_PATH)/build-android/takeOutHeaderFiles.sh $(ENGINE_PATH))

include $(CLEAR_VARS)
LOCAL_MODULE     := pEpJNI
LOCAL_SHARED_LIBRARIES := libgpgme libassuan libcurl libgcrypt libgpg-error
LOCAL_STATIC_LIBRARIES := pEpEngine libetpan libiconv libuuid
LOCAL_CPP_FEATURES += exceptions
LOCAL_SRC_FILES  := \
		  ../../src/org_pEp_jniadapter_AbstractEngine.cc \
		  ../../src/org_pEp_jniadapter_Engine.cc \
		  ../../src/org_pEp_jniadapter_Message.cc \
		  ../../src/throw_pEp_exception.cc \
		  ../../src/basic_api.cc \
		  ../../src/jniutils.cc

LOCAL_C_INCLUDES += $(GPGBUILD)/include
LOCAL_LDLIBS    := -llog
include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE     := pEpJNIAndroidHelper
LOCAL_SHARED_LIBRARIES := libgpgme
LOCAL_SRC_FILES  := org_pEp_jniadapter_AndroidHelper.cc

include $(BUILD_SHARED_LIBRARY)
$(call import-add-path,$(ENGINE_PATH))
$(call import-module, build-android/jni/)
