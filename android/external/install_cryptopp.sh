#!/bin/bash
###
### Script to isolate ENV required by crytptoPP to build.
###
ANDROID_CPU=$1
ANDROID_API=$2
PREFIX=$3
MAKE=$4
echo $3
export ANDROID_NDK_ROOT=$ANDROID_NDK
export ANDROID_SDK_ROOT=$ANDROID_SDK

source TestScripts/setenv-android.sh ANDROID_API=$ANDROID_API ANDROID_CPU=$ANDROID_CPU

PREFIX=$PREFIX $MAKE -f GNUmakefile-cross shared install