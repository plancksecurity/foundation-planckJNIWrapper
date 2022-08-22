#!/bin/bash

### 
# DONT USE IT
### 
PARENT_COMMAND=$(ps -o args= $PPID)
echo 
#CC=clang
TOOLCHAINS_PATH=$(python ../../../../utils/ndk_toolchains_path.py --ndk ${ANDROID_NDK})
export OLD_PATH=$PATH
export PATH=$TOOLCHAINS_PATH/bin:$PATH

echo "OLDPATH: $OLD_PATH"
echo ""
echo "PATH: $PATH"
echo ""
echo "TOOLCHAINS_PATH: $TOOLCHAINS_PATH"

ARCHITECTURE=$1
ANDROID_API=$2
echo $3

#./Configure ${ARCHITECTURE} -D__ANDROID_API__=$ANDROID_API -D__ARM_MAX_ARCH__=8 --prefix=$3
./Configure ${ARCHITECTURE} -D__ANDROID_API__=$ANDROID_API --prefix=$3

export PATH=$OLD_PATH
