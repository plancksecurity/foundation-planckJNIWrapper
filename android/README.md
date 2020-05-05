# Build pEpJNIAdapter for Android

** Asuming machine is already set up to build pEp Engine and Sequoia! **
## Install dependencies

```bash 
sudo port -N install gsed wget autoconf automake libtool md5sha1sum openjdk8

## optionaly (not needed if using Android Studio)
sudo port -N install gradle

```

## Install NDK
It can be done with the SDK and the sdkmanager cli tool, or using Android Studio.

Android studio -> Configure  -> SDK Manager -> SDK Tools (tab) -> Select NDK and install

Tip: To install other versions click on "Show packages details" and select the desired NDK version (Currently using: 21.0.6113669)

## Required env\_vars (for MacOS):

``` bash
export JAVA_HOME=$(/usr/libexec/java_home -v 1.8)
export ANDROID_NDK=$ANDROID_SDK/ndk/21.0.6113669
export HOST_TAG=darwin-x86_64
export ANDROID_MIN_SDK_32=18
export ANDROID_MIN_SDK_64=21
export NDK_TOOLCHAIN=$ANDROID_NDK/toolchains/llvm/prebuilt/$HOST_TAG

export PATH=$PATH:ANDROID_NDK/bin
```

## Add Rust android targets

```bash
rustup target add aarch64-linux-android armv7-linux-androideabi i686-linux-android x86_64-linux-android
```

Tell rust the location of the new target linker and AR

```bash
echo "
[target.aarch64-linux-android]
ar = \"$NDK_TOOLCHAIN/bin/aarch64-linux-android-ar\"
linker = \"$NDK_TOOLCHAIN/bin/aarch64-linux-android$ANDROID_MIN_SDK_64-clang\"

[target.armv7-linux-androideabi]
ar = \"$NDK_TOOLCHAIN/bin/arm-linux-androideabi-ar\"
linker = \"$NDK_TOOLCHAIN/bin/armv7a-linux-androideabi$ANDROID_MIN_SDK_32-clang\"

[target.x86_64-linux-android]
ar = \"$NDK_TOOLCHAIN/bin/x86_64-linux-android-ar\"
linker = \"$NDK_TOOLCHAIN/bin/x86_64-linux-android$ANDROID_MIN_SDK_64-clang\"

[target.i686-linux-android]
ar = \"$NDK_TOOLCHAIN/bin/i686-linux-android-ar\"
linker = \"$NDK_TOOLCHAIN/bin/i686-linux-android$ANDROID_MIN_SDK_32-clang\"
" >> $HOME/.cargo/config

```

## Build it

Open the android project with Android Studio, this will generate the file local.properties with the sdk location.

To build can be done form the Gradle menu (on the right in android studio) and select the build task, or just calling gradle build form terminal.

``` Bash
gradle build #Run inside JNIAdapter/android
```

