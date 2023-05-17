LOCAL_PATH:= $(call my-dir)
SRC_PATH := $(LOCAL_PATH)/../../../
ENGINE_PATH := $(LOCAL_PATH)/../../../pEpEngine
LIB_PEP_ADAPTER_PATH:=$(SRC_PATH)/libPlanckWrapper
LIB_PEP_CXX11_PATH:=$(SRC_PATH)/libPlanckCxx11
GPGBUILD:= $(LOCAL_PATH)/../external/output/
include $(LOCAL_PATH)/../external/Makefile.conf

include $(CLEAR_VARS)
LOCAL_MODULE := libiconv
LOCAL_SRC_FILES := $(GPGBUILD)/$(TARGET_ARCH_ABI)/lib/libiconv.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libpep_engine_sequoia_backend
LOCAL_SRC_FILES := $(GPGBUILD)/$(TARGET_ARCH_ABI)/lib/libpep_engine_sequoia_backend.a
include $(PREBUILT_STATIC_LIBRARY)
#Crypto lib switch, as we can use Sequoia with multiple crypto backends a switch to see which libs are loaded is required, we assume botan is the alternative, botan  the 'default' temporarily, but having nothing defined will output an error
ifeq ($(CRYPTO_LIB_NAME), botan)
    $(warning ==== JNIADAPTER android.mk using BOTAN for pEpEngineSequoiaBackend)

    include $(CLEAR_VARS)
    LOCAL_MODULE := botan
    LOCAL_SRC_FILES := $(GPGBUILD)/$(TARGET_ARCH_ABI)/lib/libbotan-2.a
    include $(PREBUILT_STATIC_LIBRARY)

else ifeq ($(CRYPTO_LIB_NAME), nettle)
    $(warning ==== JNIADAPTER android.mk using NETTLE for pEpEngineSequoiaBackend)
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
    LOCAL_SHARED_LIBRARIES := libnettle libhogweed libgmp

else
    $(error No crypto backend given!)
endif

include $(CLEAR_VARS)
LOCAL_MODULE := libetpan
LOCAL_SRC_FILES :=  $(GPGBUILD)/$(TARGET_ARCH_ABI)/lib/libetpan.a
LOCAL_EXPORT_C_INCLUDES += $(GPGBUILD)/$(TARGET_ARCH_ABI)/include
LOCAL_EXPORT_LDLIBS := -lz
include $(PREBUILT_STATIC_LIBRARY)


#Take out Engine Headers
$(shell sh $(ENGINE_PATH)/build-android/takeOutHeaderFiles.sh $(ENGINE_PATH))

include $(CLEAR_VARS)
LOCAL_MODULE     := pEpJNI
LOCAL_STATIC_LIBRARIES := pEpEngine libetpan libuuid libiconv pEpAdapter pEpCxx11 botan libpep_engine_sequoia_backend
LOCAL_CPP_FEATURES += exceptions
LOCAL_CPPFLAGS += -std=c++11 -DANDROID_STL=c++_shared -DHAVE_PTHREADS -DDISABLE_SYNC -fuse-ld=lld
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
LOCAL_C_INCLUDES += $(LIB_PEP_ADAPTER_PATH)/build-android/include $(SRC_PATH)/libPlanckWrapper
LOCAL_C_INCLUDES += $(LIB_PEP_CXX11_PATH)/build-android/include $(SRC_PATH)/libPlanckCxx11
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

$(warning ==== CURRENT LOCAL BUILT MODULE: $(LOCAL_BUILT_MODULE))
## uuid
$(info $(LOCAL_PATH))
$(warning ==== JNIADAPTER android.mk CALLING import-module uuid)
$(call import-module, pEpJNIAdapter/android/external/$(TARGET_ARCH_ABI)/uuid/jni)
$(warning ==== CURRENT LOCAL BUILT MODULE: $(LOCAL_BUILT_MODULE))
#$(call import-module, libPlanckTransport/build-android/jni/)
$(warning ==== CURRENT NDK LIBS OUT: $(NDK_LIBS_OUT))
$(warning ==== CURRENT NDK OUT: $(NDK_OUT))
$(warning ==== CURRENT TARGET OUT: $(TARGET_OUT))

#$(shell sleep 2)
#$(warning ==== after sleeping 2 seconds)
#$(shell sleep 2)
#$(warning ==== after sleeping 4 seconds)
#$(shell sleep 2)
#$(warning ==== after sleeping 6 seconds)
## pEpEngine
$(warning ==== JNIADAPTER android.mk CALLING import-module pEpEngine)
$(call import-module, pEpEngine/build-android/jni/)
$(warning ==== CURRENT LOCAL BUILT MODULE: $(LOCAL_BUILT_MODULE))
#echo "==== INSIDE RECIPE: JNIADAPTER android.mk CALLING import-module pEpEngine"
$(warning ==== JNIADAPTER android.mk CALLING import-module pEpEngine)
$(call import-module, pEpEngine/build-android/jni/)
$(warning ==== CURRENT LOCAL BUILT MODULE: $(LOCAL_BUILT_MODULE))
## libPlanckWrapper
$(warning ==== JNIADAPTER android.mk CALLING import-module libPlanckWrapper
$(call import-module, libPlanckWrapper/build-android/jni/)
$(warning ==== CURRENT LOCAL BUILT MODULE: $(LOCAL_BUILT_MODULE))
## libPlanckCxx11
$(warning ==== JNIADAPTER android.mk CALLING import-module libPlanckCxx11)
$(call import-module, libPlanckCxx11/build-android/jni/)
$(warning ==== CURRENT LOCAL BUILT MODULE: $(LOCAL_BUILT_MODULE))
