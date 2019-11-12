#!/bin/bash
usage="Usage: $(basename "$0") [-h][--force] -- Script to generate android standalone toolchain to build pEp for Android.

where:
    -h  Show this help text
    --force  Force generating the toolchain"


if [ "$1" == "-h" ]; then
echo "$usage"
    exit 0
fi

if [ -z "$ANDROID_NDK" ]; then
    echo "Please define \$ANDROID_NDK"
    exit 1
fi

$ANDROID_NDK/build/tools/make_standalone_toolchain.py $1 --arch x86_64 --api 21 --install-dir=ndk-standalone-toolchain
exit 0

