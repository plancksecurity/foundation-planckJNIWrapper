LOCAL_PATH:= $(call my-dir)
SRC_PATH := $(LOCAL_PATH)/../../../
ENGINE_PATH := $(LOCAL_PATH)/../../../pEpEngine
LIB_PEP_ADAPTER_PATH:=$(SRC_PATH)/libpEpAdapter
GPGBUILD:= $(LOCAL_PATH)/../external/output/$(TARGET_ARCH_ABI)

include $(CLEAR_VARS)
LOCAL_MODULE := libiconv
LOCAL_SRC_FILES := $(GPGBUILD)/lib/libiconv.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libsequoia_openpgp_ffi
LOCAL_SRC_FILES := $(GPGBUILD)/lib/libsequoia_openpgp_ffi.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libhogweed
LOCAL_SRC_FILES := $(GPGBUILD)/lib/libhogweed.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libgmp
LOCAL_SRC_FILES := $(GPGBUILD)/lib/libgmp.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libnettle
LOCAL_SRC_FILES := $(GPGBUILD)/lib/libnettle.so
include $(PREBUILT_SHARED_LIBRARY)


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
LOCAL_SHARED_LIBRARIES :=  libnettle libhogweed libgmp
LOCAL_STATIC_LIBRARIES := pEpEngine libetpan libiconv libuuid pEpAdapter libsequoia_openpgp_ffi
LOCAL_CPP_FEATURES += exceptions
LOCAL_CPPFLAGS += -std=c++14 -DANDROID_STL=c++_shared -DHAVE_PTHREADS -DDISABLE_SYNC -fuse-ld=lld
LOCAL_SRC_FILES  := \
		  ../../src/foundation_pEp_jniadapter_AbstractEngine.cc \
		  ../../src/foundation_pEp_jniadapter_Engine.cc \
		  ../../src/foundation_pEp_jniadapter_Message.cc \
		  ../../src/throw_pEp_exception.cc \
		  ../../src/basic_api.cc \
		  ../../src/identity_api.cc \
		  ../../src/jniutils.cc

LOCAL_C_INCLUDES += $(GPGBUILD)/include
LOCAL_C_INCLUDES += $(LIB_PEP_ADAPTER_PATH)/build-android/include $(SRC_PATH)/libpEpAdapter

LOCAL_LDFLAGS = -Wl,--allow-multiple-definition
LOCAL_LDLIBS    += -llog

include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE     := pEpJNIAndroidHelper
LOCAL_CFLAGS += -DANDROID_STL=c++_shared
LOCAL_SRC_FILES  := foundation_pEp_jniadapter_AndroidHelper.cc

include $(BUILD_SHARED_LIBRARY)
$(call import-add-path,$(SRC_PATH))
$(call import-module, pEpEngine/build-android/jni/)
$(call import-module, libpEpAdapter/build-android/jni/)
$(info $(LOCAL_PATH))
$(call import-module, pEpJNIAdapter/android/external/$(TARGET_ARCH_ABI)/uuid/jni)
