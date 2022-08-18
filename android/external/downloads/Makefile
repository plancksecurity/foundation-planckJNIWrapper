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

all: download-iconv download-uuid download-sequoia-deps download-etpan download-sequoia download-boost \
	download-cryptopp download-archive

download-iconv: libiconv-1.15.tar.gz

download-uuid: ossp-uuid_1.6.2.orig.tar.gz

download-sequoia-deps: openssl-$(OPENSSL_VERSION).tar.gz gmp-$(GMP_VERSION).tar.bz2 \
						nettle-$(NETTLE_VERSION).tar.gz

download-etpan: libetpan.tar.gz

download-sequoia: sequoia.tar.gz

download-boost: boost-for-android.tar.gz

download-cryptopp: cryptopp.tar.gz

download-archive: libarchive-$(LIB_ARCHIVE_VERSION).tar.gz

# Download source and patch it
libiconv-1.15.tar.gz:
	wget https://ftp.gnu.org/pub/gnu/libiconv/libiconv-1.15.tar.gz

ossp-uuid_1.6.2.orig.tar.gz:
	wget http://http.debian.net/debian/pool/main/o/ossp-uuid/ossp-uuid_1.6.2.orig.tar.gz
	md5sum -c ossp-uuid_1.6.2.orig.tar.gz.md5

openssl-$(OPENSSL_VERSION).tar.gz:
	wget -nc https://www.openssl.org/source/openssl-$(OPENSSL_VERSION).tar.gz
	md5sum -c openssl-$(OPENSSL_VERSION).tar.gz.md5

gmp-$(GMP_VERSION).tar.bz2:
	wget -nc https://gmplib.org/download/gmp/gmp-$(GMP_VERSION).tar.bz2
	md5sum -c gmp-$(GMP_VERSION).tar.bz2.md5

nettle-$(NETTLE_VERSION).tar.gz:
	wget -nc https://ftp.gnu.org/gnu/nettle/nettle-$(NETTLE_VERSION).tar.gz
	md5sum -c nettle-$(NETTLE_VERSION).tar.gz.md5

libarchive-$(LIB_ARCHIVE_VERSION).tar.gz:
	wget -nc https://libarchive.org/downloads/libarchive-$(LIB_ARCHIVE_VERSION).tar.gz
	md5sum -c libarchive-$(LIB_ARCHIVE_VERSION).tar.gz.md5

#------------------------------------------------------------------------------#
# Clone update and archive external projects GIT repos
# Local clone is in external/$project.git while
# selected commit is archived in external/$project

define per_repo_targets
$(1).tar.gz:
	git clone $(2) $(1).git
	(cd $(1).git; git archive --format=tar $(3)) > $(1).tar.gz

$(1).git_update: $(1).tar.gz
	cd $(1).git; git pull
	rm -rf $(1).tar.gz
	(cd $(1).git; git archive --format=tar $(3)) > $(1).tar.gz

$(1)-clean:
	rm -rf $(1)
	rm -f $(1).src.stamp

EXTERNAL_LOCAL_GITS += $(1).tar.gz
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
clean:
	rm -rf *.git
	rm -rf *.stamp
	rm -rf *.tar.gz
	rm -rf *.tar.bz2
	rm -rf ../arm64-v8a/ndk-standalone-toolchain
	rm -rf ../armeabi-v7a/ndk-standalone-toolchain
	rm -rf ../x86/ndk-standalone-toolchain
	rm -rf ../x86_64/ndk-standalone-toolchain

.PHONY: all clean download-iconv \
		download-uuid download-etpan \
		download-sequoia-deps download-sequoia
