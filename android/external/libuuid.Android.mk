# Copyleft 2019 pEp foundation
#
# This file is under GNU General Public License 3.0
# see LICENSE.txt
LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := libuuid

ENGINE_SRC_FILES := $(wildcard $(LOCAL_PATH)/../*.c)
LOCAL_SRC_FILES := $(ENGINE_SRC_FILES:%=%) 

include $(BUILD_STATIC_LIBRARY)
