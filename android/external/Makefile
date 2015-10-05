#------------------------------------------------------------------------------#
# Makefile to build GPGME, GnuPG and deps for use with pEpEngine 
#  based on gnupg-for-android/external/Makefile
#------------------------------------------------------------------------------#

#------------------------------------------------------------------------------#
# Build parameters

# TODO: get params from the outside for multiarch build

NDK_ABI ?= arm
NDK_TOOLCHAIN_VERSION ?= 4.8
APP_ABI ?= armeabi-v7a
APP_PLATFORM ?= android-14
PEP_PACKAGE_NAME ?= com.pep.pepjniaaractivity

#------------------------------------------------------------------------------#
# Clone update and archive external projects GIT repos
# Local clone is in external/$project.git while 
# slected commit is archived in external/$project

EXTERNAL_GIT_REPOS = \
libetpan|https://github.com/fdik/libetpan.git?HEAD\
gnupg|git://git.gnupg.org/gnupg.git?08c00cd\
libassuan|git://git.gnupg.org/libassuan.git?261498d\
libgcrypt|git://git.gnupg.org/libgcrypt.git?b3936b6\
libgpg-error|git://git.gnupg.org/libgpg-error.git?bcd9295\
libksba|git://git.gnupg.org/libksba.git?02079b5\
curl|https://github.com/bagder/curl?f77e89c\
npth|git://git.gnupg.org/npth.git?79fbdce\
gpgme|git://git.gnupg.org/gpgme.git?37d927a

define per_repo_targets
$(1).git:
	git clone $(2) $(1).git

$(1).git_update: $(1).git
	cd $(1).git; git pull

$(1): |$(1).git
	mkdir $(1)
	(cd $(1).git; git archive --format=tar $(3)) | tar -C $(1) -x

$(1)_clean: 
	rm -rf $(1)

EXTERNAL_LOCAL_GITS += $(1).git
EXTERNAL_LOCAL_GITS_UPDATE += $(1).git_update
EXTERNAL_SRCS += $(1)
EXTERNAL_SRCS_CLEAN += $(1)_clean
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

git_update: $(EXTERNAL_LOCAL_GITS_UPDATE)

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

NDK_SYSROOT=$(ANDROID_NDK_HOME)/platforms/$(APP_PLATFORM)/arch-$(NDK_ABI)
NDK_UNAME := $(shell uname -s | tr '[A-Z]' '[a-z]')
ifeq ($(NDK_ABI),x86)
 HOST = i686-linux-android
 NDK_TOOLCHAIN = $(NDK_ABI)-$(NDK_TOOLCHAIN_VERSION)
else
 HOST = $(NDK_ABI)-linux-androideabi
 NDK_TOOLCHAIN = $(HOST)-$(NDK_TOOLCHAIN_VERSION)
endif
NDK_TOOLCHAIN_BASE=$(ANDROID_NDK_HOME)/toolchains/$(NDK_TOOLCHAIN)/prebuilt/$(NDK_UNAME)-$(NDK_PROCESSOR)

# include Android's build flags
TARGET_ARCH_ABI = $(APP_ABI)
include $(ANDROID_NDK_HOME)/toolchains/$(NDK_TOOLCHAIN)/setup.mk

CC := $(NDK_TOOLCHAIN_BASE)/bin/$(HOST)-gcc --sysroot=$(NDK_SYSROOT)
CXX := $(NDK_TOOLCHAIN_BASE)/bin/$(HOST)-g++
CPP := $(NDK_TOOLCHAIN_BASE)/bin/$(HOST)-cpp
LD := $(NDK_TOOLCHAIN_BASE)/bin/$(HOST)-ld
AR := $(NDK_TOOLCHAIN_BASE)/bin/$(HOST)-ar
RANLIB := $(NDK_TOOLCHAIN_BASE)/bin/$(HOST)-ranlib
STRIP := $(NDK_TOOLCHAIN_BASE)/bin/$(HOST)-strip \
	--strip-unneeded -R .note -R .comment

CFLAGS = -DANDROID -I$(LOCAL)/include $(TARGET_CFLAGS)
LDFLAGS = -llog -L$(LOCAL)/lib $(TARGET_LDFLAGS)

# change 'release' to 'debug' for unoptimized debug builds
ifeq ($(APP_ABI),armeabi-v7a)
	CFLAGS += $(TARGET_arm_release_CFLAGS)
endif
ifeq ($(APP_ABI),armeabi)
	CFLAGS += $(TARGET_thumb_release_CFLAGS)
endif

#------------------------------------------------------------------------------#
# GNU Tools trickery

# point pkg-config to the .pc files generated from these builds
export PKG_CONFIG_PATH=$(LOCAL)/lib/pkgconfig
# workaround for cross-compiling bug in autoconf
export ac_cv_func_malloc_0_nonnull=yes

# GnuPG does not work with automake 1.14, it has proven reliable on 1.11, and
# that's widely available, so we'll add it to the env here, and the
# ./autogen.sh scripts will pick it up.
# http://lists.gnupg.org/pipermail/gnupg-devel/2013-August/027857.html
export AUTOMAKE=automake-1.11


.PHONY = clean distclean install-clean \
	libgpg-error-build libgpg-error-clean libgpg-error-install \
	libgcrypt-build libgcrypt-clean libgcrypt-install \
	libassuan-build libassuan-clean libassuan-install \
	libksba-build libksba-clean libksba-install \
	gnupg-build gnupg-clean \
	curl-build curl-clean curl-install \
	npth-build npth-clean npth-install \
	assets clean-assets \
    $(EXTERNAL_LOCAL_GITS_UPDATE) $(EXTERNAL_SRCS_CLEAN) \
	showsetup

all: libetpan-build gnupg-install gpgme-install assets


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

libgpg-error/configure: libgpg-error libgpg-error/configure.ac
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

libgpg-error-clean:
	-$(MAKE) -C libgpg-error clean
	rm -rf libgpg-error/configure libgpg-error/Makefile


#------------------------------------------------------------------------------#
# libgcrypt

libgcrypt/configure: libgcrypt libgcrypt/configure.ac
	cd libgcrypt && ./autogen.sh

libgcrypt/Makefile: libgcrypt/configure
	cd libgcrypt && \
		CC="$(CC)" AR="$(AR)" RANLIB=$(RANLIB) CFLAGS="$(CFLAGS)" LDFLAGS="$(LDFLAGS)" \
			./configure \
				--enable-maintainer-mode \
				--host=$(HOST) \
				--with-gpg-error-prefix=$(LOCAL) \
				--prefix=$(LOCAL)
	-patch -N -p1 --reject-file=- libgcrypt/tests/random.c libgcrypt-disable-hanging-random-test.patch

libgcrypt/src/.libs/libgcrypt.so: $(LOCAL)/lib/libgpg-error.so libgcrypt/Makefile
	$(MAKE) -C libgcrypt

$(LOCAL)/lib/libgcrypt.so: libgcrypt/src/.libs/libgcrypt.so
	$(MAKE) -C libgcrypt prefix=$(LOCAL) install
	ls -l $(LOCAL)/lib/libgcrypt.so

libgcrypt-build: libgcrypt/src/.libs/libgcrypt.so

libgcrypt-install: $(LOCAL)/lib/libgcrypt.so

libgcrypt-clean:
	-$(MAKE) -C libgcrypt clean
	rm -rf libgcrypt/configure libgcrypt/Makefile


#------------------------------------------------------------------------------#
# libassuan

libassuan/configure: libassuan libassuan/configure.ac
	cd libassuan && ./autogen.sh && autoreconf --install --force --verbose

libassuan/Makefile: libassuan/configure
	-patch -N -p1 --reject-file=- libassuan/m4/libtool.m4 libtool-Add-Android-Linux-support.patch
	cd libassuan && \
		CC="$(CC)" AR="$(AR)" RANLIB=$(RANLIB) CFLAGS="$(CFLAGS)" LDFLAGS="$(LDFLAGS)" \
			./configure \
				--enable-maintainer-mode \
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

libassuan-clean:
	-$(MAKE) -C libassuan clean
	rm -rf libassuan/configure libassuan/Makefile


#------------------------------------------------------------------------------#
# npth

npth/configure: npth npth/configure.ac
	cd npth && ./autogen.sh && autoreconf --install --force --verbose

npth/Makefile: npth/configure
	-patch -N -p1 --reject-file=- npth/m4/libtool.m4 libtool-Add-Android-Linux-support.patch
	cd npth && \
		CC="$(CC)" AR="$(AR)" RANLIB=$(RANLIB) CFLAGS="$(CFLAGS)" LDFLAGS="$(LDFLAGS)" \
			./configure \
				--enable-maintainer-mode \
				--host=$(HOST) \
				--with-gnu-ld \
				--prefix=$(LOCAL)

npth/src/.libs/libnpth.so: $(LOCAL)/lib/libgpg-error.so npth/Makefile
	$(MAKE) -C npth

$(LOCAL)/lib/libnpth.so: npth/src/.libs/libnpth.so
	$(MAKE) -C npth prefix=$(LOCAL) install
	ls -l $(LOCAL)/lib/libnpth.so*

npth-build: npth/src/.libs/libnpth.so

npth-install: $(LOCAL)/lib/libnpth.so

npth-clean:
	-$(MAKE) -C npth clean
	rm -rf npth/Makefile npth/configure


#------------------------------------------------------------------------------#
# libksba

libksba/configure: libksba libksba/configure.ac
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

libksba-clean:
	-$(MAKE) -C libksba clean
	rm -rf libksba/configure libksba/Makefile


#------------------------------------------------------------------------------#
# curl

curl/configure: curl curl/configure.ac
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

curl-clean:
	-$(MAKE) -C curl clean
	rm -f curl/Makefile

#------------------------------------------------------------------------------#
# gnupg

gnupg/configure: gnupg
	cd gnupg && ./autogen.sh

gnupg/Makefile: gnupg/configure
	cd gnupg && \
		CC="$(CC)" AR="$(AR)" RANLIB=$(RANLIB) CFLAGS="$(CFLAGS)" LDFLAGS="$(LDFLAGS)" \
			./configure \
				--enable-maintainer-mode \
				--host=$(HOST) \
				--with-gpg-error-prefix=$(LOCAL) \
				--with-libgcrypt-prefix=$(LOCAL) \
				--with-libassuan-prefix=$(LOCAL) \
				--with-ksba-prefix=$(LOCAL) \
				--with-npth-prefix=$(LOCAL) \
				--with-libcurl=$(LOCAL) \
				--disable-ldap \
				--without-libiconv-prefix \
				--disable-doc \
				--disable-g13 \
				--disable-ntbtls \
				--disable-gnutls \
				--enable-dirmngr-auto-start \
				--with-agent-pgm=$(prefix)/bin/gpg-agent \
				--with-pinentry-pgm=$(prefix)/bin/pinentry.sh \
				--with-dirmngr-pgm=$(prefix)/bin/dirmngr \
				--with-protect-tool-pgm=$(prefix)/libexec/gpg-protect-tool \
				--with-scdaemon-pgm=$(prefix)/bin/scdaemon \
				--prefix=$(prefix)

gnupg/g10/gpg2: $(LOCAL)/lib/libgpg-error.so $(LOCAL)/lib/libgcrypt.so $(LOCAL)/lib/libksba.so $(LOCAL)/lib/libassuan.so $(LOCAL)/lib/libnpth.so $(LOCAL)/lib/libcurl.so gnupg/Makefile
	$(MAKE) -C gnupg

$(LOCAL)/bin/gpg2: gnupg/g10/gpg2 gnupg/configure
	$(MAKE) -C gnupg prefix=$(LOCAL) install
	ls -l $(LOCAL)/bin/gpg2

$(LOCAL)/bin/pinentry.sh: pinentry.sh
	install $< $(LOCAL)/bin 

gnupg-build: gnupg/g10/gpg2

gnupg-install: $(LOCAL)/bin/gpg2 $(LOCAL)/bin/pinentry.sh
	install -d $(LOCAL)/etc/gnupg
	install -d $(LOCAL)/var/run/gnupg
	install -d $(LOCAL)/var/cache/gnupg

gnupg-clean:
	-$(MAKE) -C gnupg

#------------------------------------------------------------------------------#
# gpgme

gpgme/configure: gpgme gpgme/configure.ac
	cd gpgme && ./autogen.sh

gpgme/Makefile: gpgme/configure
	-patch -N -p1 --reject-file=- gpgme/m4/libtool.m4 libtool-Add-Android-Linux-support.patch
	cd gpgme && \
		CC="$(CC)" AR="$(AR)" RANLIB=$(RANLIB) CFLAGS="$(CFLAGS)" LDFLAGS="$(LDFLAGS)" \
			./configure \
				--host=$(HOST) \
				--enable-maintainer-mode \
				--with-gpg-error-prefix=$(LOCAL) \
				--with-libassuan-prefix=$(LOCAL) \
				--enable-fixed-path=$(prefix)/bin \
				--without-g13 \
				--disable-glibtest \
				--disable-gpg-test \
				--disable-gpgsm-test \
				--disable-gpgconf-test \
				--disable-g13-test \
				--prefix=$(prefix)


gpgme/src/.libs/libgpgme.so: gpgme/Makefile
	$(MAKE) -C gpgme

$(LOCAL)/lib/libgpgme.so: gpgme/src/.libs/libgpgme.so
	$(MAKE) -C gpgme DESTDIR=$(DESTDIR) prefix=$(prefix) install

gpgme-build: gpgme/src/.libs/libgpgme.so

gpgme-install: $(LOCAL)/bin/gpg2 $(LOCAL)/lib/libgpgme.so

gpgme-clean:
	-$(MAKE) -C gpgme clean
	-rm -f gpgme/Makefile gpgme/configure

#------------------------------------------------------------------------------#
# libetpan

libetpan/libetpan-android-1.zip: libetpan
	cd libetpan/build-android; ./build.sh

libetpan-build: libetpan/libetpan-android-1.zip

#------------------------------------------------------------------------------#
# assets for Android app

clean-assets:
	rm -rf $(ASSETS)

ASSETS := $(EXTERNAL_ROOT)/../assets
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
# clean

install-clean:
	rm -rf -- $(LOCAL)

clean: install-clean gnupg-clean curl-clean libksba-clean libassuan-clean npth-clean libgcrypt-clean libgpg-error-clean 

distclean: clean install-clean
	-$(MAKE) -C gnupg distclean
	-$(MAKE) -C curl distclean
	-rm -f curl/configure # their distclean fails to rm this
	-$(MAKE) -C libksba distclean
	-$(MAKE) -C npth distclean
	-$(MAKE) -C libassuan distclean
	-$(MAKE) -C libgcrypt distclean
	-$(MAKE) -C libgpg-error distclean

gitclean: $(EXTERNAL_SRCS_CLEAN)
	rm -rf $(ASSETS)
	rm -rf $(EXTERNAL_ROOT)/data

