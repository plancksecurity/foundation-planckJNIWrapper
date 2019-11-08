# Copyright 2019, pEp Foundation
# This file is part of pEpJNIAdapter for Android  - ARM64 v8a build
# This file may be used under the terms of the GNU General Public License version 3
# see LICENSE.txt

include ../Makefile.conf

#------------------------------------------------------------------------------#
# Makefile to build deps for use with pEpEngine
#  based on gnupg-for-android/external/Makefile
#------------------------------------------------------------------------------#

#------------------------------------------------------------------------------#
# Build parameters

APP_ABI ?= arm64-v8a
ANDROID_API ?= 21

all: build

build: generate-ndk-toolchain showsetup uuid-install sequoia-ffi-install libetpan-build

#------------------------------------------------------------------------------#
# Generate standalone toolchain
generate-ndk-toolchain: ndk-standalone-toolchain/AndroidVersion.txt

ndk-standalone-toolchain/AndroidVersion.txt:
	bash generate-standalone-ndk-toolchain.sh

#------------------------------------------------------------------------------#
# Manage paths for PREFIX, DESTDIR, LOCAL and PATH

EXTERNAL_ROOT := $(shell pwd)

# install root for built files
DESTDIR = $(EXTERNAL_ROOT)/..
prefix = /output/$(APP_ABI)
LOCAL := $(DESTDIR)$(prefix)

PATH := ${PATH}:$(NDK_TOOLCHAIN)/bin:$(LOCAL)/bin

NDK_SYSROOT=$(ANDROID_NDK_HOME)/sysroot

HOST = aarch64-linux-android
NDK_TOOLCHAIN = $(HOST)-$(NDK_TOOLCHAIN_COMPILER)

# include Android's build flags
TARGET_ARCH_ABI = $(APP_ABI)
include $(ANDROID_NDK)/build/core/toolchains/$(NDK_TOOLCHAIN)/setup.mk

ANDROID_NDK_HOME=$(EXTERNAL_ROOT)/ndk-standalone-toolchain

LD := $(ANDROID_NDK_HOME)/bin/$(HOST)-ld
AR := $(ANDROID_NDK_HOME)/bin/$(HOST)-ar
AS := $(ANDROID_NDK_HOME)/bin/$(HOST)-clang
CC := $(ANDROID_NDK_HOME)/bin/$(HOST)-clang
CC := $(ANDROID_NDK_HOME)/bin/$(HOST)-clang
CXX := $(ANDROID_NDK_HOME)/bin/$(HOST)-clang++
RANLIB := $(ANDROID_NDK_HOME)/bin/$(HOST)-ranlib
STRIP := $(ANDROID_NDK_HOME)/bin/$(HOST)-strip

CFLAGS += -DANDROID -I$(LOCAL)/include $(TARGET_CFLAGS) -fPIE -fPIC -std=c99
LDFLAGS += -llog -L$(LOCAL)/lib $(TARGET_LDFLAGS) -pie

# change 'release' to 'debug' for unoptimized debug builds
CFLAGS += $(TARGET_arm64_debug_CFLAGS)

#------------------------------------------------------------------------------#
# GNU Tools trickery

# point pkg-config to the .pc files generated from these builds
export PKG_CONFIG_PATH=$(LOCAL)/lib/pkgconfig

# workaround for cross-compiling bug in autoconf
export ac_cv_func_malloc_0_nonnull=yes

#------------------------------------------------------------------------------#
# debugging stuff

showsetup:
	@echo "NDK_TOOLCHAIN: $(NDK_TOOLCHAIN)"
	@echo "NDK_SYSROOT: $(NDK_SYSROOT)"
	@echo "APP_ABI: $(APP_ABI)"
	@echo "HOST: $(HOST)"
	@echo "CC: $(CC)"
	@echo "LD: $(LD)"
	@echo "CFLAGS: $(CFLAGS)"
	@echo "LDFLAGS: $(LDFLAGS)"

#------------------------------------------------------------------------------#
# libiconv

libiconv.src.stamp: ../libiconv-1.15.tar.gz
	tar xvfz ../libiconv-1.15.tar.gz
	mv libiconv-1.15 libiconv
	touch $@

libiconv-src: libiconv.src.stamp

libiconv-clean:
	rm -rf libiconv
	rm -rf libiconv.src.stamp

libiconv/Makefile: libiconv.src.stamp
	cd libiconv && \
		CC="$(CC)" LD="$(LD)" AR="$(AR)" AS="$(AS)" RANLIB=$(RANLIB) STRIP="$(STRIP)" CFLAGS="$(CFLAGS)" LDFLAGS="$(LDFLAGS)" \
			./configure \
				--with-lib-path=$(LOCAL)/lib \
                --with-include-path=$(LOCAL)/include \
				--host=$(HOST) \
				--enable-static \
				--disable-shared \
				--prefix=$(LOCAL)

libiconv/lib/.libs/libiconv.a: libiconv/Makefile
	$(MAKE) -C libiconv

$(LOCAL)/lib/libiconv.a: libiconv/lib/.libs/libiconv.a
	$(MAKE) -C libiconv DESTDIR=$(DESTDIR) prefix=$(prefix) install
	ls -l $(LOCAL)/lib/libiconv.a

libiconv-build: libiconv/lib/.libs/libiconv.a

libiconv-install: $(LOCAL)/lib/libiconv.a

#------------------------------------------------------------------------------#
# libetpan

libetpan/Makefile: libetpan.src.stamp | libiconv-install
	cd libetpan/build-android; ICONV_PREFIX=$(LOCAL) bash ./build.sh
	mv libetpan/build-android/libetpan-android-1.zip ../
	touch $@

libetpan-build: libetpan/Makefile

libetpan-cleanzip:
	rm -f libetpan/build-android/libetpan-android-1.zip

#------------------------------------------------------------------------------#
# uuid

uuid.src.stamp: ossp-uuid_1.6.2.orig.tar.gz
	tar xvfz ../ossp-uuid_1.6.2.orig.tar.gz
	mv uuid-1.6.2 uuid
	touch $@

uuid-src: uuid.src.stamp

uuid-clean:
	rm -rf uuid
	rm -rf uuid.src.stamp

uuid/Makefile: uuid.src.stamp
	$(SED) -i 's,AC_CHECK_VA_COPY(),,' uuid/uuid.ac
	cd uuid && autoconf
	-patch -N -p1 --reject-file=- uuid/libtool.m4 ../libtool-Add-Android-Linux-support-iconv.patch
	cp ../config.sub uuid
	cp ../config.guess uuid
	cd uuid && \
		CC="$(CC)" AR="$(AR)" RANLIB=$(RANLIB) CFLAGS="$(CFLAGS)" LDFLAGS="$(LDFLAGS)" \
			./configure \
				--enable-static \
				--host=$(HOST) \
				--with-gnu-ld \
				--prefix=$(LOCAL)

uuid/.libs/libuuid.so: uuid/Makefile
	$(MAKE) -C uuid

$(LOCAL)/lib/libuuid.so: uuid/.libs/libuuid.so
	# install fails copying uuid cmdline tool, but libs are copied ...
	-$(MAKE) -C uuid DESTDIR=$(DESTDIR) prefix=$(prefix) install
	echo "****** THIS ERROR WAS WILLINGLY IGNORED ******"
	ls -l $(LOCAL)/lib/libuuid.so

uuid-build: uuid/.libs/libuuid.so

uuid-prebuild: uuid/jni/Android.mk uuid/Makefile

uuid-install: $(LOCAL)/lib/libuuid.so | uuid-prebuild

uuid/jni/Android.mk: uuid.src.stamp
	mkdir -p uuid/jni
	cp ../libuuid.Android.mk uuid/jni/Android.mk


#------------------------------------------------------------------------------#
# Sequoia

sequoia-deps-build: gmp-install nettle-install openssl-install

#------------------------------------------------------------------------------#
# OpenSSL

openssl-$(OPENSSL_VERSION).tar.gz:
	wget -nc https://www.openssl.org/source/openssl-$(OPENSSL_VERSION).tar.gz
	md5sum -c ../openssl-$(OPENSSL_VERSION).tar.gz.md5
	touch $@

openssl.src.stamp: openssl-$(OPENSSL_VERSION).tar.gz
	tar xvf openssl-$(OPENSSL_VERSION).tar.gz
	mv openssl-$(OPENSSL_VERSION) openssl
	touch $@

openssl-src: openssl.src.stamp

openssl-clean:
	rm -rf openssl
	rm -rf openssl.src.stamp

OPENSSL_ARCHITECTURE:=android-arm64

openssl/Makefile: openssl.src.stamp
	cd openssl && \
	    ../../configure_openssl.sh ${OPENSSL_ARCHITECTURE} $(ANDROID_API) $(LOCAL)
openssl/libssl.so: | openssl/Makefile
	PATH="$(ANDROID_NDK_HOME)/bin:$(PATH)" $(MAKE) -C openssl

$(LOCAL)/lib/libssl.so: openssl/libssl.so
	cd openssl && \
		cp libcrypto.so $(LOCAL)/lib && \
		cp libcrypto.a $(LOCAL)/lib && \
		cp libssl.so $(LOCAL)/lib && \
		cp libssl.a $(LOCAL)/lib 
	ls -l $(LOCAL)/lib/libssl.so

openssl-build: openssl/libssl.so

openssl-install: $(LOCAL)/lib/libssl.so


#------------------------------------------------------------------------------#
# GMP

gmp-$(GMP_VERSION).tar.bz2:
	wget -nc https://gmplib.org/download/gmp/gmp-$(GMP_VERSION).tar.bz2
	md5sum -c ../gmp-$(GMP_VERSION).tar.bz2.md5
	touch $@

gmp.src.stamp: gmp-$(GMP_VERSION).tar.bz2
	tar xvf gmp-$(GMP_VERSION).tar.bz2
	mv gmp-$(GMP_VERSION) gmp
	touch $@

gmp-src: gmp.src.stamp

gmp-clean:
	rm -rf gmp
	rm -rf gmp.src.stamp

gmp/Makefile: gmp.src.stamp
	cd gmp && \
		CC="$(CC)" LD="$(LD)" AR="$(AR)" AS="$(AS)" RANLIB=$(RANLIB) STRIP="$(STRIP)" CFLAGS="$(CFLAGS)" LDFLAGS="$(LDFLAGS)" \
			./configure \
				--host=$(HOST) \
				--prefix=$(LOCAL) \
			    --disable-static \
			    MPN_PATH="arm64 generic"


gmp/.libs/libgmp.so: gmp/Makefile
	$(MAKE) -C gmp

$(LOCAL)/lib/libgmp.so: gmp/.libs/libgmp.so
	$(MAKE) -C gmp DESTDIR=$(DESTDIR) prefix=$(prefix) install
	#gmp/libtool --finish $(LOCAL)/lib/
	ls -l $(LOCAL)/lib/libgmp.so
	touch $@

gmp-build: gmp/.libs/libgmp.so

gmp-install: $(LOCAL)/lib/libgmp.so


#------------------------------------------------------------------------------#
# NETTLE

nettle-$(NETTLE_VERSION).tar.gz: $(LOCAL)/lib/libgmp.so
	wget -nc https://ftp.gnu.org/gnu/nettle/nettle-$(NETTLE_VERSION).tar.gz
	md5sum -c ../nettle-$(NETTLE_VERSION).tar.gz.md5
	touch $@

nettle.src.stamp: nettle-$(NETTLE_VERSION).tar.gz
	tar xvf nettle-$(NETTLE_VERSION).tar.gz
	mv nettle-$(NETTLE_VERSION) nettle
	touch $@

nettle-src: nettle.src.stamp

nettle-clean:
	rm -rf nettle
	rm -rf nettle.src.stamp

nettle/Makefile: nettle.src.stamp
	-patch -N -p1 --reject-file=- nettle/configure.ac  ../nettle-3.4.1-remove-so-versioning.patch
	-patch -N -p1 --reject-file=- nettle/Makefile.in  ../nettle-3.4.1-remove-so-versioning-link.patch
	cd nettle && \
		CC="$(CC)" LD="$(LD)" AR="$(AR)" AS="$(AS)" RANLIB=$(RANLIB) STRIP="$(STRIP)" CFLAGS="$(CFLAGS)" LDFLAGS="$(LDFLAGS)" \
			./configure \
				--with-lib-path=$(LOCAL)/lib \
                --with-include-path=$(LOCAL)/include \
				--host=$(HOST) \
				--prefix=$(LOCAL)

nettle/libnettle.so: nettle/Makefile
	$(MAKE) -C nettle

$(LOCAL)/lib/libnettle.so: nettle/libnettle.so nettle/libhogweed.so
	$(MAKE) -C nettle DESTDIR=$(DESTDIR) prefix=$(prefix) install

nettle-build: nettle/libnettle.so nettle/libhogweed.so

nettle-install: $(LOCAL)/lib/libnettle.so $(LOCAL)/lib/libhogweed.so


#------------------------------------------------------------------------------#
# Sequoia-ffi

CARGO_TARGET_DIR=$(EXTERNAL_ROOT)/build/$(APP_ABI)

sequoia-ffi-clean:
	rm -rf sequoia
	rm -rf sequoia.src.stamp

$(CARGO_TARGET_DIR)/aarch64-linux-android/release/libsequoia_openpgp_ffi.so:  sequoia.src.stamp sequoia-deps-build
	cd sequoia && PATH="$(ANDROID_NDK_HOME)/bin:$(PATH)" \
	LD_LIBRARY_PATH=$(LOCAL)/lib/ \
	PKG_CONFIG_PATH=$(LOCAL)/lib/pkgconfig \
	PKG_CONFIG_ALLOW_CROSS=1 \
	ARMV7_LINUX_ANDROIDEABI_OPENSSL_LIB_DIR="$(LOCAL)/lib" \
	ARMV7_LINUX_ANDROIDEABI_OPENSSL_INCLUDE_DIR="$(LOCAL)/include" \
	ARMV7_LINUX_ANDROIDEABI_OPENSSL_DIR="$(LOCAL)/bin" \
	CARGO_TARGET_DIR=$(CARGO_TARGET_DIR) cargo build --target aarch64-linux-android -p sequoia-openpgp-ffi --release


$(LOCAL)/lib/libsequoia_openpgp_ffi.so: $(CARGO_TARGET_DIR)/aarch64-linux-android/release/libsequoia_openpgp_ffi.so
	cp $(CARGO_TARGET_DIR)/aarch64-linux-android/release/libsequoia_openpgp_ffi.* $(LOCAL)/lib/
	cp -r sequoia/openpgp-ffi/include/* $(LOCAL)/include


sequoia-ffi-build: $(CARGO_TARGET_DIR)/aarch64-linux-android/release/libsequoia_openpgp_ffi.so

sequoia-ffi-install: sequoia-deps-build $(LOCAL)/lib/libsequoia_openpgp_ffi.so

#------------------------------------------------------------------------------#
# assets for Android app

#ASSETS := $(EXTERNAL_ROOT)/assets
#
#assets: clean-assets
#	# add the new stuff
#	install -d $(ASSETS)
#	cp -a $(LOCAL)/* $(ASSETS)
#	# remove all the stuff we don't need
#	rm -f $(ASSETS)/bin/*-static
#	rm -f $(ASSETS)/bin/curl*
#	rm -f $(ASSETS)/lib/*.a $(ASSETS)/lib/*.la
#	# remove lib symlinks since Android AssetManager copies them as files
#	rm -f $(ASSETS)/lib/*.so
#	# remove .so.0 symlink and rename the .so.0.12.0 file to it
#	for f in $(ASSETS)/lib/*.so.[0-9]*; do \
#		echo $$f; \
#		test ! -L $$f || \
#			(rm $$f && mv $$f.[0-9]* $$f); \
#	done
#	rm -rf $(ASSETS)/include
#	rm -rf $(ASSETS)/share/man $(ASSETS)/share/info $(ASSETS)/share/doc
#	rm -rf $(ASSETS)/tests
#

#------------------------------------------------------------------------------#
# Clone update and archive external projects GIT repos
# Local clone is in external/$project.git while
# selected commit is archived in external/$project

define per_repo_targets
$(1).git.stamp:
	git clone $(2) $(1).git
	touch $(1).git.stamp

$(1).git_update: $(1).git.stamp
	cd $(1).git; git pull
	touch $(1).git.stamp

$(1).src.stamp: $(1).git.stamp
	rm -rf $(1)
	mkdir $(1)
	(cd $(1).git; git archive --format=tar $(3)) | tar -C $(1) -x
	touch $(1).src.stamp

$(1)-src: $(1).src.stamp

$(1)-clean:
	rm -rf $(1)
	rm -f $(1).src.stamp

EXTERNAL_LOCAL_GITS += $(1).git.stamp
EXTERNAL_LOCAL_GITS_UPDATE += $(1).git_update
EXTERNAL_SRCS += $(1)-src
EXTERNAL_SRCS_CLEAN += $(1)-clean
endef

define per_repo
$(call per_repo_targets,\
    $(1),\
    $(word 1,$(subst ?, ,$(2))),\
    $(word 2,$(subst ?, ,$(2))))
endef

$(foreach repo, $(EXTERNAL_GIT_REPOS), $(eval $(call per_repo,\
    $(word 1,$(subst |, ,$(repo))),\
    $(word 2,$(subst |, ,$(repo))))))

git_clones: $(EXTERNAL_LOCAL_GITS)

git_update: $(EXTERNAL_LOCAL_GITS_UPDATE)

#------------------------------------------------------------------------------#
# clean

clean-assets:
	rm -rf $(ASSETS)

clean-install:
	rm -rf $(DESTDIR)/data

clean: $(EXTERNAL_SRCS_CLEAN) clean-assets clean-install libetpan-cleanzip

clean-all: clean
	rm -rf *.git
	rm -rf *.stamp
#------------------------------------------------------------------------------#

.PHONY = clean clean-install clean-assets libetpan-cleanzip\
	libiconv-build libiconv-install \
	libetpan-build \
	uuid-build uuid-prebuild uuid-install \
	gmp-build  gmp-install \
	sequoia-deps-build \
	openssl-build  openssl-install \
	nettle-build  nettle-install \
	sequoia-ffi-build sequoia-ffi-install\
	assets \
    $(EXTERNAL_LOCAL_GITS_UPDATE) $(EXTERNAL_SRCS) $(EXTERNAL_SRCS_CLEAN)\
	showsetup
