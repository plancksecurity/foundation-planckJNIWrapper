#!/bin/zsh
#emulate -LR bash
#cd ~code/android/pEpJNIAdapter/external

ARCH_DEST="$1"
mkdir "$ARCH_DEST"
cp MakefileTemplate "$ARCH_DEST"/Makefile
FILE_DEST=$ARCH_DEST/Makefile

################################################################################
#                                Select TEMPLATE FIELDS FOR ARCH               #
################################################################################

case $ARCH_DEST in
 	x86)
 		ANDROID_API=18
 		HOST=i686-linux-android
 		COMPILER_PREFIX="$HOST"
 		NDK_TOOLCHAIN_TARGET="APP_ABI"
 		ARCH_DEBUG_CFLAGS=TARGET_x86_debug_CFLAGS
 		OPENSSL_ARCHITECTURE=android-x86
 		SEQUOIA_ARCH=i686-linux-android
 	;;
 	x86_64)
 		ANDROID_API=21
 		HOST=x86_64-linux-android
 		COMPILER_PREFIX="$HOST"
 		NDK_TOOLCHAIN_TARGET="APP_ABI"
 		ARCH_DEBUG_CFLAGS=TARGET_x86_64_debug_CFLAGS
 		OPENSSL_ARCHITECTURE=android-x86_64
 		SEQUOIA_ARCH=x86_64-linux-android
 	;;
 	armeabi-v7a)
 		ANDROID_API=18
 		HOST=arm-linux-androideabi
 		COMPILER_PREFIX=armv7a-linux-androideabi
 		NDK_TOOLCHAIN_TARGET="HOST"
 		ARCH_DEBUG_CFLAGS=TARGET_arm_debug_CFLAGS
 		OPENSSL_ARCHITECTURE=android-arm
 		SEQUOIA_ARCH=armv7-linux-androideabi
 	;;
 	arm64-v8a)
 		ANDROID_API=21
 		HOST=aarch64-linux-android
 		COMPILER_PREFIX="$HOST"
 		NDK_TOOLCHAIN_TARGET="HOST"
 		ARCH_DEBUG_CFLAGS=TARGET_arm64_debug_CFLAGS
 		OPENSSL_ARCHITECTURE=android-arm64
 		GMP_MAKEFILE_EXTRA=' MPN_PATH=\"arm64 generic\"'
 		SEQUOIA_ARCH=aarch64-linux-android
 	;;
 esac

################################################################################
#                                Select GNU SED                                #
################################################################################

OS="$(uname -s)"

case "${OS}" in
    Linux*)     SED=sed;;
    Darwin*)    SED=gsed;;
    CYGWIN*)    echo "UNSUPORTED YET" && exit;;
    MINGW*)     echo "UNSUPORTED YET" && exit;;
    *)          echo "UNKNOWN:${OS}" && exit;;
esac

################################################################################
#              REPLACE FIELDS IN TEMPLATE                                      #
################################################################################

$SED -i 's/\[ARCH\]/'"$ARCH_DEST"'/g' "$FILE_DEST"
$SED -i 's/\[ANDROID_API\]/'"$ANDROID_API"'/g' "$FILE_DEST"
$SED -i 's/\[HOST\]/'"$HOST"'/g' "$FILE_DEST"
$SED -i 's/\[COMPILER_PREFIX\]/'"$COMPILER_PREFIX"'/g' "$FILE_DEST"
$SED -i 's/\[NDK_TOOLCHAIN_TARGET\]/'"$NDK_TOOLCHAIN_TARGET"'/g' "$FILE_DEST"
$SED -i 's/\[ARCH_DEBUG_CFLAGS\]/'"$ARCH_DEBUG_CFLAGS"'/g' "$FILE_DEST"
$SED -i 's/\[OPENSSL_ARCHITECTURE\]/'"$OPENSSL_ARCHITECTURE"'/g' "$FILE_DEST"
$SED -i 's/\[GMP_MAKEFILE_EXTRA\]/'"$GMP_MAKEFILE_EXTRA"'/g' "$FILE_DEST"
$SED -i 's/\[SEQUOIA_ARCH\]/'"$SEQUOIA_ARCH"'/g' "$FILE_DEST"

cat "$FILE_DEST"
