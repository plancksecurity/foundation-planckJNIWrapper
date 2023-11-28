LOCAL_PATH:= $(call my-dir)
SRC_PATH := $(LOCAL_PATH)/../../../
SUBMODULES_PATH := $(LOCAL_PATH)/../../submodules/
ENGINE_PATH := $(LOCAL_PATH)/../../submodules/planckCoreV3
NESTED_SUBMODULES_PATH := $(ENGINE_PATH)/submodules/
LIB_PEP_ADAPTER_PATH:=$(LOCAL_PATH)/../../submodules/libPlanckWrapper
LIB_PEP_CXX11_PATH:=$(LOCAL_PATH)/../../submodules/libPlanckCxx11
BUILD_PATH:= $(LOCAL_PATH)/../external/output
PEP_LOG ?=
include $(LOCAL_PATH)/../external/Makefile.conf

include $(CLEAR_VARS)
LOCAL_MODULE := libiconv
LOCAL_SRC_FILES := $(BUILD_PATH)/$(TARGET_ARCH_ABI)/lib/libiconv.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libpep_engine_sequoia_backend
LOCAL_SRC_FILES := $(BUILD_PATH)/$(TARGET_ARCH_ABI)/lib/libpep_engine_sequoia_backend.a
include $(PREBUILT_STATIC_LIBRARY)
#Crypto lib switch, as we can use Sequoia with multiple crypto backends a switch to see which libs are loaded is required, we assume botan is the alternative, botan  the 'default' temporarily, but having nothing defined will output an error
ifeq ($(CRYPTO_LIB_NAME), botan2)
    $(warning ==== JNIADAPTER android.mk using BOTAN2 for pEpEngineSequoiaBackend)

    include $(CLEAR_VARS)
    LOCAL_MODULE := botan
    LOCAL_SRC_FILES := $(BUILD_PATH)/$(TARGET_ARCH_ABI)/lib/libbotan-2.a
    include $(PREBUILT_STATIC_LIBRARY)

else ifeq ($(CRYPTO_LIB_NAME), nettle)
    $(warning ==== JNIADAPTER android.mk using NETTLE for pEpEngineSequoiaBackend)
    include $(CLEAR_VARS)
    LOCAL_MODULE := libhogweed
    LOCAL_SRC_FILES := $(BUILD_PATH)/$(TARGET_ARCH_ABI)/lib/libhogweed.so
    include $(PREBUILT_SHARED_LIBRARY)

    include $(CLEAR_VARS)
    LOCAL_MODULE := libgmp
    LOCAL_SRC_FILES := $(BUILD_PATH)/$(TARGET_ARCH_ABI)/lib/libgmp.so
    include $(PREBUILT_SHARED_LIBRARY)

    include $(CLEAR_VARS)
    LOCAL_MODULE := libnettle
    LOCAL_SRC_FILES := $(BUILD_PATH)/$(TARGET_ARCH_ABI)/lib/libnettle.so
    include $(PREBUILT_SHARED_LIBRARY)
    LOCAL_SHARED_LIBRARIES := libnettle libhogweed libgmp

else
    $(error No crypto backend given!)
endif

include $(CLEAR_VARS)
LOCAL_MODULE := libetpan
LOCAL_SRC_FILES :=  $(BUILD_PATH)/$(TARGET_ARCH_ABI)/lib/libetpan.a
LOCAL_EXPORT_C_INCLUDES += $(BUILD_PATH)/$(TARGET_ARCH_ABI)/include
LOCAL_EXPORT_LDLIBS := -lz
include $(PREBUILT_STATIC_LIBRARY)


#Take out Engine Headers
$(shell sh $(ENGINE_PATH)/build-android/takeOutHeaderFiles.sh $(ENGINE_PATH))

include $(CLEAR_VARS)
LOCAL_MODULE     := pEpJNI
LOCAL_STATIC_LIBRARIES := pEpEngine libetpan libuuid libiconv pEpAdapter pEpCxx11 botan libpep_engine_sequoia_backend
LOCAL_CPP_FEATURES += exceptions
LOCAL_CPPFLAGS += -std=c++14 -DANDROID_STL=c++_shared -DHAVE_PTHREADS -DDISABLE_SYNC -fuse-ld=lld -frtti

LOCAL_SRC_FILES := $(wildcard ../../src/cxx/*.cc)

LOCAL_C_INCLUDES += $(BUILD_PATH)/$(TARGET_ARCH_ABI)/include
LOCAL_C_INCLUDES += $(SUBMODULES_PATH)/libPlanckWrapper
LOCAL_C_INCLUDES += $(SUBMODULES_PATH)/libPlanckCxx11
LOCAL_C_INCLUDES += $(ENGINE_PATH)/asn.1

LOCAL_LDFLAGS = -Wl,--allow-multiple-definition
LOCAL_LDLIBS    += -llog

include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE     := pEpJNIAndroidHelper
LOCAL_CFLAGS += -DANDROID_STL=c++_shared
LOCAL_SRC_FILES  := foundation_pEp_jniadapter_AndroidHelper.cc

include $(BUILD_SHARED_LIBRARY)

$(call import-add-path,$(SRC_PATH))
$(call import-add-path,$(SUBMODULES_PATH))
$(call import-add-path,$(NESTED_SUBMODULES_PATH))

$(warning ==== CURRENT LOCAL BUILT MODULE: $(LOCAL_BUILT_MODULE))
## uuid
$(info $(LOCAL_PATH))
$(warning ==== JNIADAPTER android.mk CALLING import-module uuid)
$(call import-module, planckJNIWrapper/android/external/$(TARGET_ARCH_ABI)/uuid/jni)
$(warning ==== CURRENT LOCAL BUILT MODULE: $(LOCAL_BUILT_MODULE))

$(warning ==== CURRENT NDK LIBS OUT: $(NDK_LIBS_OUT))
$(warning ==== CURRENT NDK OUT: $(NDK_OUT))
$(warning ==== CURRENT TARGET OUT: $(TARGET_OUT))

## planckCore
$(warning ==== JNIADAPTER android.mk CALLING import-module pEpEngine)
$(call import-module, planckCoreV3/build-android/jni/)
$(warning ==== CURRENT LOCAL BUILT MODULE: $(LOCAL_BUILT_MODULE))
## libPlanckWrapper
$(warning ==== JNIADAPTER android.mk CALLING import-module libPlanckWrapper)
$(call import-module, libPlanckWrapper/build-android/jni/)
$(warning ==== CURRENT LOCAL BUILT MODULE: $(LOCAL_BUILT_MODULE))
## libPlanckCxx11
$(warning ==== JNIADAPTER android.mk CALLING import-module libPlanckCxx11)
$(call import-module, libPlanckCxx11/build-android/jni/)
$(warning ==== CURRENT LOCAL BUILT MODULE: $(LOCAL_BUILT_MODULE))
