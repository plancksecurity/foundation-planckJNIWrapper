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

## Download all dependencies
all: downloads.stamp

downloads.stamp: download-iconv download-uuid download-sequoia-deps
	touch $@

## Download iconv
download-iconv: libiconv-1.15.tar.gz

## Download and patch uuid
download-uuid: ossp-uuid_1.6.2.orig-patched.tar.gz

## Download sequoia dependencies

download-sequoia-deps: botan.src.stamp

# Download source and patch it
libiconv-1.15.tar.gz:
	wget https://ftp.gnu.org/pub/gnu/libiconv/libiconv-1.15.tar.gz

ossp-uuid_1.6.2.orig-patched.tar.gz: ossp-uuid_1.6.2.orig.tar.gz
	tar -xvf $<
	-patch -N -p1 --reject-file=- uuid-1.6.2/libtool.m4 ../libtool-Add-Android-Linux-support-iconv.patch
	tar -czvf $@ uuid-1.6.2
	rm -rf uuid-1.6.2

ossp-uuid_1.6.2.orig.tar.gz:
	wget http://http.debian.net/debian/pool/main/o/ossp-uuid/ossp-uuid_1.6.2.orig.tar.gz
	md5sum -c ossp-uuid_1.6.2.orig.tar.gz.md5

openssl-$(OPENSSL_VERSION).tar.gz:
	wget -nc https://www.openssl.org/source/openssl-$(OPENSSL_VERSION).tar.gz
	md5sum -c openssl-$(OPENSSL_VERSION).tar.gz.md5

gmp-$(GMP_VERSION).tar.bz2:
	wget -nc https://gmplib.org/download/gmp/gmp-$(GMP_VERSION).tar.bz2
	md5sum -c gmp-$(GMP_VERSION).tar.bz2.md5


botan.src.stamp:
# just check the folder exist for the moment, so we use a submodule.
	@if [ -d $(BOTAN_SRC) ]; then \
		touch $@; \
	else \
		echo "Error: botan folder does not exist."; \
		exit 1; \
	fi

#------------------------------------------------------------------------------#
clean:
	-rm -rf *.git
	-rm -rf *.stamp
	-rm -rf *.tar.gz
	-rm -rf *.tar.bz2
	-rm -rf *.tar.xz

.PHONY: all clean download-iconv \
		download-uuid \
		download-sequoia-deps

include ../help.mk
helpPurpose='Download all files and packages needed to compile external dependencies for pEpEngine'
