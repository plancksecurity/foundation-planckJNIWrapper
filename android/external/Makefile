#------------------------------------------------------------------------------#
# Makefile to build GPGME, GnuPG and deps for use with pEpEngine 
#  based on gnupg-for-android/external/Makefile
#------------------------------------------------------------------------------#

#------------------------------------------------------------------------------#
# Build parameters

# TODO: get params from the outside for multiarch build

NDK_ABI ?= arm
NDK_TOOLCHAIN_VERSION ?= clang
APP_ABI ?= armeabi-v7a
APP_PLATFORM ?= android-18
PEP_PACKAGE_NAME ?= security.pEp

all: build assets

build: showsetup libetpan-build gnupg-install gpgme-install uuid-install uuid-prebuild sequoia-build

#------------------------------------------------------------------------------#
# Manage paths for PREFIX, DESTDIR, LOCAL and PATH

EXTERNAL_ROOT := $(shell pwd)

# install root for built files
DESTDIR = $(EXTERNAL_ROOT)
prefix = /data/data/$(PEP_PACKAGE_NAME)/app_opt
LOCAL := $(DESTDIR)$(prefix)

PATH := ${PATH}:$(NDK_TOOLCHAIN)/bin:$(LOCAL)/bin

#------------------------------------------------------------------------------#
# NDK toolchain integration
# TODO: cleanup.

# Android now has 64-bit and 32-bit versions of the NDK for GNU/Linux.  We
# assume that the build platform uses the appropriate version, otherwise the
# user building this will have to manually set NDK_PROCESSOR or NDK_TOOLCHAIN.
CPU := $(shell uname -m)
ifeq ($(CPU),x86_64)
 NDK_PROCESSOR=x86_64
else
 NDK_PROCESSOR=x86
endif

NDK_SYSROOT=$(ANDROID_NDK_HOME)/sysroot
NDK_UNAME := $(shell uname -s | tr '[A-Z]' '[a-z]')

ifeq ($(NDK_ABI),x86)
 HOST = i686-linux-android
 NDK_TOOLCHAIN = $(NDK_ABI)-$(NDK_TOOLCHAIN_VERSION)
else
 HOST = $(NDK_ABI)-linux-androideabi
 NDK_TOOLCHAIN = $(HOST)-$(NDK_TOOLCHAIN_VERSION)
endif
# include Android's build flags
TARGET_ARCH_ABI = $(APP_ABI)
#include $(ANDROID_NDK)/build/core/toolchains/$(NDK_TOOLCHAIN)/setup.mk
#include $(ANDROID_NDK_HOME)/toolchains/$(NDK_TOOLCHAIN)/setup.mk
ANDROID_NDK_HOME=$(EXTERNAL_ROOT)/ndk-18-arm
LD := $(ANDROID_NDK_HOME)/bin/$(HOST)-ld
AR := $(ANDROID_NDK_HOME)/bin/$(HOST)-ar
AS := $(ANDROID_NDK_HOME)/bin/$(HOST)-clang
CC := $(ANDROID_NDK_HOME)/bin/$(HOST)-clang
CXX := $(ANDROID_NDK_HOME)/bin/$(HOST)-clang++
RANLIB := $(ANDROID_NDK_HOME)/bin/$(HOST)-ranlib
STRIP := $(ANDROID_NDK_HOME)/bin/$(HOST)-strip

#-D_FILE_OFFSET_BITS=64 -DLARGEFILE_SOURCE=1
CFLAGS += -DANDROID -I$(LOCAL)/include $(TARGET_CFLAGS) -fPIE -fPIC -std=c99
LDFLAGS += -llog -L$(LOCAL)/lib $(TARGET_LDFLAGS) -pie

# change 'release' to 'debug' for unoptimized debug builds
ifeq ($(APP_ABI),armeabi-v7a)
	CFLAGS += $(TARGET_arm_debug_CFLAGS)
endif
ifeq ($(APP_ABI),armeabi)
	CFLAGS += $(TARGET_thumb_debug_CFLAGS)
endif

#------------------------------------------------------------------------------#
# GNU Tools trickery

# point pkg-config to the .pc files generated from these builds
export PKG_CONFIG_PATH=$(LOCAL)/lib/pkgconfig
# workaround for cross-compiling bug in autoconf
export ac_cv_func_malloc_0_nonnull=yes

#------------------------------------------------------------------------------#
# debugging stuff

showsetup:
	@echo "NDK_TOOLCHAIN_VERSION: $(NDK_TOOLCHAIN_VERSION)"
	@echo "NDK_TOOLCHAIN: $(NDK_TOOLCHAIN)"
	@echo "NDK_SYSROOT: $(NDK_SYSROOT)"
	@echo "APP_PLATFORM: $(APP_PLATFORM)"
	@echo "APP_ABI: $(APP_ABI)"
	@echo "HOST: $(HOST)"
	@echo "CC: $(CC)"
	@echo "LD: $(LD)"
	@echo "CFLAGS: $(CFLAGS)"
	@echo "LDFLAGS: $(LDFLAGS)"


#------------------------------------------------------------------------------#
# libgpg-error

EXTERNAL_GIT_REPOS += libgpg-error|git://git.gnupg.org/libgpg-error.git?libgpg-error-1.20

libgpg-error/configure: libgpg-error.src.stamp libgpg-error/configure.ac 
	cd libgpg-error && ./autogen.sh

libgpg-error/Makefile: libgpg-error/configure 
	cd libgpg-error && \
		./configure \
			CC="$(CC)" \
			AR=$(AR) \
			RANLIB=$(RANLIB) \
			CFLAGS="$(CFLAGS)" \
			LDFLAGS="$(LDFLAGS)" \
			--disable-doc \
			--disable-languages \
			--host=$(HOST) \
			--prefix=$(LOCAL)
	ls -l libgpg-error/libtool
	# brute force and ignorance to make libtool comply with android style
	sed -i 's,^fast_install=.*,fast_install=needless,' libgpg-error/libtool
	sed -i 's,^version_type=.*,version_type=none,' libgpg-error/libtool
	sed -i 's,^shlibpath_overrides_runpath=.*,shlibpath_overrides_runpath=yes,' libgpg-error/libtool
	sed -i 's,^library_names_spec=.*,library_names_spec="\\$$libname\\$$release\\$$shared_ext",' libgpg-error/libtool
	sed -i 's,^soname_spec=.*,soname_spec="\\$$libname\\$$release\\$$shared_ext",' libgpg-error/libtool
	sed -i 's,^finish_cmds=.*,finish_cmds="",' libgpg-error/libtool
	sed -i 's,^sys_lib_dlsearch_path_spec=.*,sys_lib_dlsearch_path_spec="/lib /usr/lib",' libgpg-error/libtool

libgpg-error/src/.libs/libgpg-error.so: libgpg-error/Makefile 
	$(MAKE) -C libgpg-error

libgpg-error-build: libgpg-error/src/.libs/libgpg-error.so

$(LOCAL)/lib/libgpg-error.so: libgpg-error/src/.libs/libgpg-error.so
	$(MAKE) -C libgpg-error prefix=$(LOCAL) install
	ls -l $(LOCAL)/lib/libgpg-error.so*

libgpg-error-install: $(LOCAL)/lib/libgpg-error.so

#------------------------------------------------------------------------------#
# libgcrypt

EXTERNAL_GIT_REPOS += libgcrypt|git://git.gnupg.org/libgcrypt.git?libgcrypt-1.6.4

libgcrypt/configure: libgcrypt.src.stamp libgcrypt/configure.ac 
	cd libgcrypt && ./autogen.sh

libgcrypt/Makefile: libgcrypt/configure 
	-patch -N -p1 --reject-file=- libgcrypt/Makefile.am libgcrypt-disable-docs.patch
	cd libgcrypt && \
		CC="$(CC)" AR="$(AR)" RANLIB=$(RANLIB) CFLAGS="$(CFLAGS)" LDFLAGS="$(LDFLAGS)" \
			./configure \
				--enable-maintainer-mode \
				--host=$(HOST) \
				--with-gpg-error-prefix=$(LOCAL) \
				--prefix=$(LOCAL)
	-patch -N -p1 --reject-file=- libgcrypt/tests/random.c libgcrypt-disable-hanging-random-test.patch
	# force use of /dev/urandom
	-sed -i 's/NAME_OF_DEV_RANDOM/NAME_OF_DEV_URANDOM/' libgcrypt/random/rndlinux.c

libgcrypt/src/.libs/libgcrypt.so: $(LOCAL)/lib/libgpg-error.so libgcrypt/Makefile
	$(MAKE) -C libgcrypt

$(LOCAL)/lib/libgcrypt.so: libgcrypt/src/.libs/libgcrypt.so
	$(MAKE) -C libgcrypt prefix=$(LOCAL) install
	ls -l $(LOCAL)/lib/libgcrypt.so

libgcrypt-build: libgcrypt/src/.libs/libgcrypt.so

libgcrypt-install: $(LOCAL)/lib/libgcrypt.so

#------------------------------------------------------------------------------#
# libassuan

EXTERNAL_GIT_REPOS += libassuan|git://git.gnupg.org/libassuan.git?libassuan-2.3.0

libassuan/configure: libassuan.src.stamp libassuan/configure.ac 
	cd libassuan && ./autogen.sh && autoreconf --install --force --verbose

libassuan/Makefile: libassuan/configure
	-patch -N -p1 --reject-file=- libassuan/m4/libtool.m4 libtool-Add-Android-Linux-support.patch
	cd libassuan && \
		CC="$(CC)" AR="$(AR)" RANLIB=$(RANLIB) CFLAGS="$(CFLAGS)" LDFLAGS="$(LDFLAGS)" \
			./configure \
				--enable-maintainer-mode \
				--disable-largefile \
				--host=$(HOST) \
				--with-gpg-error-prefix=$(LOCAL) \
				--prefix=$(LOCAL)

libassuan/src/.libs/libassuan.so: $(LOCAL)/lib/libgpg-error.so libassuan/Makefile
	$(MAKE) -C libassuan

$(LOCAL)/lib/libassuan.so: libassuan/src/.libs/libassuan.so
	$(MAKE) -C libassuan prefix=$(LOCAL) install
	ls -l $(LOCAL)/lib/libassuan.so

libassuan-build: libassuan/src/.libs/libassuan.so

libassuan-install: $(LOCAL)/lib/libassuan.so

#------------------------------------------------------------------------------#
# libksba

EXTERNAL_GIT_REPOS += libksba|git://git.gnupg.org/libksba.git?libksba-1.3.4

libksba/configure: libksba.src.stamp libksba/configure.ac 
	cd libksba && ./autogen.sh

libksba/Makefile: $(LOCAL)/lib/libgpg-error.so libksba/configure
	-patch -N -p1 --reject-file=- libksba/m4/libtool.m4 libtool-Add-Android-Linux-support.patch
	cd libksba && \
		CC="$(CC)" AR="$(AR)" RANLIB=$(RANLIB) CFLAGS="$(CFLAGS)" LDFLAGS="$(LDFLAGS)" \
			./configure \
				--enable-maintainer-mode \
				--host=$(HOST) \
				--with-gpg-error-prefix=$(LOCAL) \
				--prefix=$(LOCAL)

libksba/src/.libs/libksba.so: libksba/Makefile
	$(MAKE) -C libksba

$(LOCAL)/lib/libksba.so: libksba/src/.libs/libksba.so
	$(MAKE) -C libksba prefix=$(LOCAL) install
	ls -l $(LOCAL)/lib/libksba.so

libksba-build: libksba/src/.libs/libksba.so

libksba-install: $(LOCAL)/lib/libksba.so


#------------------------------------------------------------------------------#
# npth

EXTERNAL_GIT_REPOS += npth|git://git.gnupg.org/npth.git?npth-1.2

npth/configure: npth.src.stamp npth/configure.ac
	cd npth && ./autogen.sh

npth/Makefile: $(LOCAL)/lib/libgpg-error.so npth/configure
	cd npth && \
		CC="$(CC)" AR="$(AR)" RANLIB=$(RANLIB) CFLAGS="$(CFLAGS)" LDFLAGS="$(LDFLAGS)" \
		./configure $(CONFIGURE_ARGS) \
				--enable-maintainer-mode \
        		--host=$(HOST) \
				--with-gpg-error-prefix=$(LOCAL) \
				--prefix=$(LOCAL)

npth/src/.libs/libnpth.so: npth/Makefile
	$(MAKE) -C npth

$(LOCAL)/lib/libnpth.so: npth/src/.libs/libnpth.so
	$(MAKE) -C npth prefix=$(LOCAL) install
	ls -l $(LOCAL)/lib/libnpth.so

npth-build: npth/src/.libs/libnpth.so

npth-install: $(LOCAL)/lib/libnpth.so

#------------------------------------------------------------------------------#
# curl

EXTERNAL_GIT_REPOS += curl|https://github.com/bagder/curl?curl-7_45_0

curl/configure: curl.src.stamp curl/configure.ac 
	cd curl && ./buildconf

curl/Makefile: curl/configure
	-patch -N -p1 --reject-file=- curl/m4/libtool.m4 libtool-Add-Android-Linux-support.patch
	cd curl && \
		CC="$(CC)" AR="$(AR)" RANLIB=$(RANLIB) CFLAGS="$(CFLAGS)" LDFLAGS="$(LDFLAGS)" \
			./configure \
				--host=$(HOST) \
				--prefix=$(prefix) \
				--with-gnu-ld \
				--disable-imap \
				--disable-ldap \
				--disable-pop3 \
				--disable-rtsp \
				--disable-smtp
	# brute force and ignorance to make libtool comply with android style
	sed -i 's,^fast_install=.*,fast_install=needless,' curl/libtool
	sed -i 's,^version_type=.*,version_type=none,' curl/libtool
	sed -i 's,^shlibpath_overrides_runpath=.*,shlibpath_overrides_runpath=yes,' curl/libtool
	sed -i 's,^library_names_spec=.*,library_names_spec="\\$$libname\\$$release\\$$shared_ext",' curl/libtool
	sed -i 's,^soname_spec=.*,soname_spec="\\$$libname\\$$release\\$$shared_ext",' curl/libtool
	sed -i 's,^finish_cmds=.*,finish_cmds="",' curl/libtool
	sed -i 's,^sys_lib_dlsearch_path_spec=.*,sys_lib_dlsearch_path_spec="/lib /usr/lib",' curl/libtool

curl/lib/.libs/libcurl.so: curl/Makefile
	$(MAKE) -C curl

$(LOCAL)/lib/libcurl.so: curl/lib/.libs/libcurl.so
	$(MAKE) -C curl DESTDIR=$(DESTDIR) prefix=$(prefix) install
	ls -l $(LOCAL)/lib/libcurl.so

curl-build: curl/lib/.libs/libcurl.so

curl-install: $(LOCAL)/lib/libcurl.so

#------------------------------------------------------------------------------#
# libiconv

# libiconv from git can't autogen with today's debian packages.
#EXTERNAL_GIT_REPOS += libiconv|git://git.savannah.gnu.org/libiconv.git?5365cc8

# using released package instead
libiconv-1.15.tar.gz:
	wget https://ftp.gnu.org/pub/gnu/libiconv/libiconv-1.15.tar.gz

libiconv.src.stamp: libiconv-1.15.tar.gz
	tar xvfz libiconv-1.15.tar.gz
	mv libiconv-1.15 libiconv
	touch $@

libiconv-src: libiconv.src.stamp

libiconv-clean:
	rm -rf libiconv
	rm -rf libiconv.src.stamp

EXTERNAL_SRCS += libiconv-src
EXTERNAL_SRCS_CLEAN += libiconv-clean

libiconv/Makefile: libiconv.src.stamp
	-patch -N -p1 --reject-file=- libiconv/m4/libtool.m4 libtool-Add-Android-Linux-support-iconv.patch
	cp config.sub libiconv/build-aux
	cp config.guess libiconv/build-aux
	cp config.sub libiconv/libcharset/build-aux
	cp config.guess libiconv/libcharset/build-aux
	cd libiconv && \
		CC="$(CC)" AR="$(AR)" RANLIB=$(RANLIB) CFLAGS="$(CFLAGS)" LDFLAGS="$(LDFLAGS)" \
			./configure \
				--enable-static \
				--enable-maintainer-mode \
				--host=$(HOST) \
				--with-gnu-ld \
				--prefix=$(LOCAL)

libiconv/lib/.libs/libiconv.so: libiconv/Makefile
	$(MAKE) -C libiconv

$(LOCAL)/lib/libiconv.so: libiconv/lib/.libs/libiconv.so
	$(MAKE) -C libiconv DESTDIR=$(DESTDIR) prefix=$(prefix) install
	ls -l $(LOCAL)/lib/libiconv.so

libiconv-build: libiconv/lib/.libs/libiconv.so

libiconv-install: $(LOCAL)/lib/libiconv.so

#------------------------------------------------------------------------------#
# gnupg

EXTERNAL_GIT_REPOS += gnupg|git://git.gnupg.org/gnupg.git?gnupg-2.0.30

gnupg/configure: gnupg.src.stamp gnupg/configure.ac 
	cd gnupg && ./autogen.sh

gnupg/Makefile: gnupg/configure
	-patch -N -p1 --reject-file=- gnupg/gl/stdint_.h gnupg_fix_gmulib_stdint_with_bionic.patch
	-patch -N -p1 --reject-file=- gnupg/jnlib/dotlock.c gnupg_use_rename_for_dotlock.patch
	cd gnupg && \
		CC="$(CC)" AR="$(AR)" RANLIB=$(RANLIB) CFLAGS="$(CFLAGS)" LDFLAGS="$(LDFLAGS)" \
			./configure \
				--enable-maintainer-mode \
				--host=$(HOST) \
				--with-gpg-error-prefix=$(LOCAL) \
				--with-libgcrypt-prefix=$(LOCAL) \
				--with-libassuan-prefix=$(LOCAL) \
				--with-ksba-prefix=$(LOCAL) \
				--with-libcurl=$(LOCAL) \
				--disable-agent \
				--disable-scdaemon \
				--disable-ldap \
				--disable-doc \
				--disable-gpgsm \
				--prefix=$(prefix)

gnupg/g10/gpg2: $(LOCAL)/lib/libgpg-error.so $(LOCAL)/lib/libgcrypt.so $(LOCAL)/lib/libksba.so $(LOCAL)/lib/libnpth.so $(LOCAL)/lib/libassuan.so $(LOCAL)/lib/libcurl.so $(LOCAL)/lib/libiconv.so gnupg/Makefile
	$(MAKE) -C gnupg

$(LOCAL)/bin/gpg2: gnupg/g10/gpg2 gnupg/configure
	$(MAKE) -C gnupg prefix=$(LOCAL) install
	ls -l $(LOCAL)/bin/gpg2

gnupg-build: gnupg/g10/gpg2

gnupg-install: $(LOCAL)/bin/gpg2
	install -d $(LOCAL)/etc/gnupg
	install -d $(LOCAL)/var/run/gnupg
	install -d $(LOCAL)/var/cache/gnupg

#------------------------------------------------------------------------------#
# gpgme

EXTERNAL_GIT_REPOS += gpgme|git://git.gnupg.org/gpgme.git?gpgme-1.8.0

gpgme/configure: gpgme.src.stamp gpgme/configure.ac
	cd gpgme && ./autogen.sh

ifneq ("x$(findstring '64',$(HOST))x","xx")
    gpgme_32b_force_no_largefile =
else
    gpgme_32b_force_no_largefile = --disable-largefile
endif

gpgme/Makefile: gpgme/configure
	-patch -N -p1 --reject-file=- gpgme/m4/libtool.m4 libtool-Add-Android-Linux-support.patch
	cd gpgme && \
		CC="$(CC)" AR="$(AR)" RANLIB=$(RANLIB) CFLAGS="$(CFLAGS)" LDFLAGS="$(LDFLAGS)" \
			./configure \
				--host=$(HOST) \
				$(gpgme_32b_force_no_largefile) \
				--enable-maintainer-mode \
				--with-gpg-error-prefix=$(LOCAL) \
				--with-libassuan-prefix=$(LOCAL) \
				--enable-fixed-path=$(prefix)/bin \
				--disable-glibtest \
				--disable-gpg-test \
				--disable-gpgsm-test \
				--disable-gpgconf-test \
				--disable-g13-test \
				--prefix=$(prefix) \
				--enable-languages=cl


gpgme/src/.libs/libgpgme.so: gpgme/Makefile
	$(MAKE) -C gpgme

$(LOCAL)/lib/libgpgme.so: gpgme/src/.libs/libgpgme.so
	$(MAKE) -C gpgme DESTDIR=$(DESTDIR) prefix=$(prefix) install

gpgme-build: gpgme/src/.libs/libgpgme.so

gpgme-install: $(LOCAL)/bin/gpg2 $(LOCAL)/lib/libgpgme.so

#------------------------------------------------------------------------------#
# libetpan

EXTERNAL_GIT_REPOS += libetpan|https://github.com/fdik/libetpan.git?HEAD

libetpan/build-android/libetpan-android-1.zip: libiconv-install libetpan.src.stamp
	cd libetpan/build-android; ICONV_PREFIX=$(LOCAL) bash ./build.sh

libetpan-build: libetpan/build-android/libetpan-android-1.zip

libetpan-cleanzip:
	rm -f libetpan/build-android/libetpan-android-1.zip

#------------------------------------------------------------------------------#
# uuid

# using released package from debian. official ftp wasn't available today
ossp-uuid_1.6.2.orig.tar.gz:
	wget http://http.debian.net/debian/pool/main/o/ossp-uuid/ossp-uuid_1.6.2.orig.tar.gz
	md5sum -c ossp-uuid_1.6.2.orig.tar.gz.md5

uuid.src.stamp: ossp-uuid_1.6.2.orig.tar.gz
	tar xvfz ossp-uuid_1.6.2.orig.tar.gz
	mv uuid-1.6.2 uuid
	touch $@

uuid-src: uuid.src.stamp

uuid-clean:
	rm -rf uuid
	rm -rf uuid.src.stamp

EXTERNAL_SRCS += uuid-src
EXTERNAL_SRCS_CLEAN += uuid-clean

uuid/Makefile: uuid.src.stamp
	sed -i 's,AC_CHECK_VA_COPY(),,' uuid/uuid.ac
	cd uuid && autoconf
	-patch -N -p1 --reject-file=- uuid/libtool.m4 libtool-Add-Android-Linux-support-iconv.patch
	cp config.sub uuid
	cp config.guess uuid
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

uuid-install: $(LOCAL)/lib/libuuid.so

uuid/jni/Android.mk: uuid.src.stamp
	mkdir -p uuid/jni
	cp libuuid.Android.mk uuid/jni/Android.mk


#------------------------------------------------------------------------------#
# Sequoia
OPENSSL_VERSION=1.1.1a
GMP_VERSION=6.1.2
NETTLE_VERSION=3.4.1

sequoia-build: gmp-install nettle-install
#------------------------------------------------------------------------------#
# GMP

gmp-$(GMP_VERSION).tar.bz2:
	wget -nc https://gmplib.org/download/gmp/gmp-$(GMP_VERSION).tar.bz2
	md5sum -c gmp-$(GMP_VERSION).tar.bz2.md5

gmp.src.stamp: gmp-$(GMP_VERSION).tar.bz2
	tar xvf gmp-$(GMP_VERSION).tar.bz2
	mv gmp-$(GMP_VERSION) gmp
	touch $@

gmp-src: gmp.src.stamp

gmp-clean:
	rm -rf gmp
	rm -rf gmp.src.stamp

EXTERNAL_SRCS += gmp-src
EXTERNAL_SRCS_CLEAN += gmp-clean

gmp/Makefile: gmp.src.stamp
	cd gmp && \
		CC="$(CC)" LD="$(LD)" AR="$(AR)" AS="$(AS)" RANLIB=$(RANLIB) STRIP="$(STRIP)" CFLAGS="$(CFLAGS)" LDFLAGS="$(LDFLAGS)" \
			./configure \
				--host=$(HOST) \
				--prefix=$(LOCAL)

gmp/.libs/libgmp.so: gmp/Makefile
	$(MAKE) -C gmp

$(LOCAL)/lib/libgmp.so: gmp/.libs/libgmp.so
	$(MAKE) -C gmp DESTDIR=$(DESTDIR) prefix=$(prefix) install
	ls -l $(LOCAL)/lib/libgmp.so

gmp-build: gmp/.libs/libgmp.so

gmp-install: $(LOCAL)/lib/libgmp.so


#------------------------------------------------------------------------------#
# NETTLE

nettle-$(NETTLE_VERSION).tar.bz2: $(LOCAL)/lib/libgmp.so
	wget -nc https://ftp.gnu.org/gnu/nettle/nettle-$(NETTLE_VERSION).tar.gz
	md5sum -c nettle-$(NETTLE_VERSION).tar.gz.md5

nettle.src.stamp: nettle-$(NETTLE_VERSION).tar.gz
	tar xvf nettle-$(NETTLE_VERSION).tar.gz
	mv nettle-$(NETTLE_VERSION) nettle
	touch $@

nettle-src: nettle.src.stamp

nettle-clean:
	rm -rf nettle
	rm -rf nettle.src.stamp

EXTERNAL_SRCS += nettle-src
EXTERNAL_SRCS_CLEAN += nettle-clean

nettle/Makefile: nettle.src.stamp
	cd nettle && \
		CC="$(CC)" LD="$(LD)" AR="$(AR)" AS="$(AS)" RANLIB=$(RANLIB) STRIP="$(STRIP)" CFLAGS="$(CFLAGS)" LDFLAGS="$(LDFLAGS)" \
			./configure \
				--with-lib-path=$(LOCAL)/lib \
                --with-include-path=$(LOCAL)/include \
				--host=$(HOST) \
				--prefix=$(LOCAL)

nettle/libnettle.so: nettle/Makefile
	$(MAKE) -C nettle

$(LOCAL)/lib/libnettle.so: nettle/libnettle.so
	$(MAKE) -C nettle DESTDIR=$(DESTDIR) prefix=$(prefix) install
	ls -l $(LOCAL)/lib/libnettle.so

nettle-build: nettle/libnettle.so

nettle-install: $(LOCAL)/lib/libnettle.so

#------------------------------------------------------------------------------#
# assets for Android app

ASSETS := $(EXTERNAL_ROOT)/assets
assets: clean-assets
	# add the new stuff
	install -d $(ASSETS)
	cp -a $(LOCAL)/* $(ASSETS)
	# remove all the stuff we don't need
	rm -f $(ASSETS)/bin/*-static
	rm -f $(ASSETS)/bin/curl*
	rm -f $(ASSETS)/lib/*.a $(ASSETS)/lib/*.la
	# remove lib symlinks since Android AssetManager copies them as files
	rm -f $(ASSETS)/lib/*.so
	# remove .so.0 symlink and rename the .so.0.12.0 file to it
	for f in $(ASSETS)/lib/*.so.[0-9]*; do \
		echo $$f; \
		test ! -L $$f || \
			(rm $$f && mv $$f.[0-9]* $$f); \
	done
	rm -rf $(ASSETS)/include
	rm -rf $(ASSETS)/share/man $(ASSETS)/share/info $(ASSETS)/share/doc
	rm -rf $(ASSETS)/tests


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
	libgpg-error-build libgpg-error-install \
	libgcrypt-build libgcrypt-install \
	libassuan-build libassuan-install \
	libksba-build libksba-install \
	libnpth-build libnpth-install \
	libiconv-build libiconv-install \
	uuid-build uuid-prebuild uuid-install \
	gnupg-build  gnupg-install\
	gmp-build  gmp-install \
	sequoia-build \
	nettle-build  nettle-install \
	gpgme-build  gpgme-install\
	curl-build curl-install \
	assets \
    $(EXTERNAL_LOCAL_GITS_UPDATE) $(EXTERNAL_SRCS) \
	showsetup

