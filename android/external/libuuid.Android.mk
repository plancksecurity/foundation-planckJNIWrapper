# Copyleft 2019 pEp foundation
#
# This file is under GNU General Public License 3.0
# see LICENSE.txt
$(warning ==== UUID android.mk START)
LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := libuuid

ENGINE_SRC_FILES := $(wildcard $(LOCAL_PATH)/../*.c)
LOCAL_SRC_FILES := $(ENGINE_SRC_FILES:%=%) 

$(warning ==== UUID android.mk BUILD STATIC LIBRARY UUID.A)
include $(BUILD_STATIC_LIBRARY)
