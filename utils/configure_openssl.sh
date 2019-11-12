#!/bin/bash

### 
# DONT USE IT
### 
CC=clang
TOOLCHAINS_PATH=$(python ../../../utils/ndk_toolchains_path.py --ndk ${ANDROID_NDK})
PATH=$TOOLCHAINS_PATH/bin:$PATH

ARCHITECTURE=$1
ANDROID_API=$2
echo $3

./Configure ${ARCHITECTURE} -D__ANDROID_API__=$ANDROID_API --prefix=$3
