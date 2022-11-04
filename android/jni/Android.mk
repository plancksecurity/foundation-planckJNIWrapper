LOCAL_PATH:= $(call my-dir)
SRC_PATH := $(LOCAL_PATH)/../../../
ENGINE_PATH := $(LOCAL_PATH)/../../../pEpEngine
LIB_PEP_ADAPTER_PATH:=$(SRC_PATH)/libpEpAdapter
LIB_PEP_CXX11_PATH:=$(SRC_PATH)/libpEpCxx11
#LIB_PEP_TRANSPORT_PATH:=$(SRC_PATH)/libpEpTransport
GPGBUILD:= $(LOCAL_PATH)/../external/output/

include $(CLEAR_VARS)
LOCAL_MODULE := libiconv
LOCAL_SRC_FILES := $(GPGBUILD)/$(TARGET_ARCH_ABI)/lib/libiconv.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libpep_engine_sequoia_backend
LOCAL_SRC_FILES := $(GPGBUILD)/$(TARGET_ARCH_ABI)/lib/libpep_engine_sequoia_backend.a
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

#Take out Engine Headers
$(shell sh $(ENGINE_PATH)/build-android/takeOutHeaderFiles.sh $(ENGINE_PATH))

include $(CLEAR_VARS)
LOCAL_MODULE     := pEpJNI
#TODO PEMA-103 we cna move seq to static
LOCAL_SHARED_LIBRARIES :=  libnettle libhogweed libgmp
LOCAL_STATIC_LIBRARIES := pEpEngine libetpan libuuid libiconv pEpAdapter pEpCxx11 libpep_engine_sequoia_backend
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
#LOCAL_C_INCLUDES += $(ENGINE_PATH)/build-android/include/pEp
LOCAL_C_INCLUDES += $(LIB_PEP_ADAPTER_PATH)/build-android/include $(SRC_PATH)/libpEpAdapter
LOCAL_C_INCLUDES += $(LIB_PEP_CXX11_PATH)/build-android/include $(SRC_PATH)/libpEpCxx11
#LOCAL_C_INCLUDES += $(LIB_PEP_TRANSPORT_PATH)/build-android/include $(SRC_PATH)/libpEpTransport
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
MY_UUID_BUILD := $(LOCAL_BUILT_MODULE)
#$(call import-module, libpEpTransport/build-android/jni/)
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
#pEpEngine.ndkBuild.stamp: $(MY_UUID_BUILD)
#echo "==== INSIDE RECIPE: JNIADAPTER android.mk CALLING import-module pEpEngine"
$(warning ==== JNIADAPTER android.mk CALLING import-module pEpEngine)
$(call import-module, pEpEngine/build-android/jni/)
$(warning ==== CURRENT LOCAL BUILT MODULE: $(LOCAL_BUILT_MODULE))
## libpEpAdapter
$(warning ==== JNIADAPTER android.mk CALLING import-module libpEpAdapter)
$(call import-module, libpEpAdapter/build-android/jni/)
$(warning ==== CURRENT LOCAL BUILT MODULE: $(LOCAL_BUILT_MODULE))
## libpEpCxx11
$(warning ==== JNIADAPTER android.mk CALLING import-module libpEpCxx11)
$(call import-module, libpEpCxx11/build-android/jni/)
$(warning ==== CURRENT LOCAL BUILT MODULE: $(LOCAL_BUILT_MODULE))
