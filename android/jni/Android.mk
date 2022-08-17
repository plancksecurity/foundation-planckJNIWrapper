LOCAL_PATH:= $(call my-dir)
SRC_PATH := $(LOCAL_PATH)/../../../
ENGINE_PATH := $(LOCAL_PATH)/../../../pEpEngine
LIB_PEP_ADAPTER_PATH:=$(SRC_PATH)/libpEpAdapter
GPGBUILD:= $(LOCAL_PATH)/../external/output/

include $(CLEAR_VARS)
LOCAL_MODULE := libiconv
LOCAL_SRC_FILES := $(GPGBUILD)/$(TARGET_ARCH_ABI)/lib/libiconv.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libsequoia_openpgp_ffi
LOCAL_SRC_FILES := $(GPGBUILD)/$(TARGET_ARCH_ABI)/lib/libsequoia_openpgp_ffi.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libhogweed
LOCAL_SRC_FILES := $(GPGBUILD)/$(TARGET_ARCH_ABI)/lib/libhogweed.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libgmp
LOCAL_SRC_FILES := $(GPGBUILD)/$(TARGET_ARCH_ABI)/lib/libgmp.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libnettle
LOCAL_SRC_FILES := $(GPGBUILD)/$(TARGET_ARCH_ABI)/lib/libnettle.so
include $(PREBUILT_SHARED_LIBRARY)


include $(CLEAR_VARS)
LOCAL_MODULE := libetpan
LOCAL_SRC_FILES :=  $(GPGBUILD)/$(TARGET_ARCH_ABI)/lib/libetpan.a
LOCAL_EXPORT_C_INCLUDES += $(GPGBUILD)/$(TARGET_ARCH_ABI)/include
LOCAL_EXPORT_LDLIBS := -lz
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libssl
LOCAL_SRC_FILES := $(GPGBUILD)/$(TARGET_ARCH_ABI)/lib/libssl.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libcrypto
LOCAL_SRC_FILES := $(GPGBUILD)/$(TARGET_ARCH_ABI)/lib/libcrypto.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libboost_iostreams
LOCAL_SRC_FILES := $(GPGBUILD)/$(TARGET_ARCH_ABI)/lib/libboost_iostreams.a
LOCAL_EXPORT_C_INCLUDES += $(GPGBUILD)/$(TARGET_ARCH_ABI)/include
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libboost_regex
LOCAL_SRC_FILES := $(GPGBUILD)/$(TARGET_ARCH_ABI)/lib/libboost_regex.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libboost_system
LOCAL_SRC_FILES := $(GPGBUILD)/$(TARGET_ARCH_ABI)/lib/libboost_system.a
include $(PREBUILT_STATIC_LIBRARY)



#Take out Engine Headers
$(shell sh $(ENGINE_PATH)/build-android/takeOutHeaderFiles.sh $(ENGINE_PATH))

include $(CLEAR_VARS)
LOCAL_MODULE     := pEpJNI
LOCAL_SHARED_LIBRARIES := libnettle libhogweed libgmp libcryptopp
LOCAL_STATIC_LIBRARIES := pEpEngine libetpan libiconv libuuid pEpAdapter libsequoia_openpgp_ffi downloadclient signedpkg
LOCAL_STATIC_LIBRARIES += libssl libcrypto libboost_system  libboost_regex libboost_iostreams

LOCAL_CPPFLAGS += -fexceptions
LOCAL_CPPFLAGS += -frtti

LOCAL_CPP_FEATURES += exceptiovns
LOCAL_CPPFLAGS += -std=c++17 -DANDROID_STL=c++_shared -DHAVE_PTHREADS -DDISABLE_SYNC -fuse-ld=lld
LOCAL_SRC_FILES  := \
		  ../../src/cxx/foundation_pEp_jniadapter_AbstractEngine.cc \
		  ../../src/cxx/foundation_pEp_jniadapter_Engine.cc \
		  ../../src/cxx/foundation_pEp_jniadapter_Message.cc \
		  ../../src/cxx/foundation_pEp_jniadapter__Blob.cc \
		  ../../src/cxx/throw_pEp_exception.cc \
		  ../../src/cxx/basic_api.cc \
		  ../../src/cxx/identity_api.cc \
		  ../../src/cxx/jniutils.cc

LOCAL_C_INCLUDES += $(GPGBUILD)/$(TARGET_ARCH_ABI)/include
LOCAL_C_INCLUDES += $(LIB_PEP_ADAPTER_PATH)/build-android/include $(SRC_PATH)/libpEpAdapter

LOCAL_LDFLAGS = -Wl,--allow-multiple-definition
LOCAL_LDLIBS    += -llog -lz

include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE     := pEpJNIAndroidHelper
LOCAL_CFLAGS += -DANDROID_STL=c++_shared
LOCAL_SRC_FILES  := foundation_pEp_jniadapter_AndroidHelper.cc

include $(BUILD_SHARED_LIBRARY)
$(call import-add-path,$(SRC_PATH))
$(call import-module, pEpEngine/build-android/jni/)
$(call import-module, libpEpAdapter/build-android/jni/)
$(call import-module, signedpkg/build-android/jni)
$(call import-module, downloadclient/build-android/jni)
$(info $(LOCAL_PATH))
$(call import-module, pEpJNIAdapter/android/external/$(TARGET_ARCH_ABI)/uuid/jni)
