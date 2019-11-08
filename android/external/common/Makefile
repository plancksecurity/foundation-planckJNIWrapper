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

all:  download-iconv download-uuid download-sequoia-deps #build-etpan download sequoia

download-iconv: ../libiconv-1.15.tar.gz

download-uuid: ../ossp-uuid_1.6.2.orig.tar.gz

download-sequoia-deps: ../openssl-$(OPENSSL_VERSION).tar.gz ../gmp-$(GMP_VERSION).tar.bz2 ../nettle-$(NETTLE_VERSION).tar.gz

# Download source and patch it
../libiconv-1.15.tar.gz:
	wget https://ftp.gnu.org/pub/gnu/libiconv/libiconv-1.15.tar.gz -P ../

../ossp-uuid_1.6.2.orig.tar.gz:
	wget http://http.debian.net/debian/pool/main/o/ossp-uuid/ossp-uuid_1.6.2.orig.tar.gz -P ../
	cd .. && md5sum -c ossp-uuid_1.6.2.orig.tar.gz.md5

../openssl-$(OPENSSL_VERSION).tar.gz:
	wget -nc https://www.openssl.org/source/openssl-$(OPENSSL_VERSION).tar.gz -P ../
	cd .. && md5sum -c openssl-$(OPENSSL_VERSION).tar.gz.md5

../gmp-$(GMP_VERSION).tar.bz2:
	wget -nc https://gmplib.org/download/gmp/gmp-$(GMP_VERSION).tar.bz2 -P ../
	cd .. && md5sum -c gmp-$(GMP_VERSION).tar.bz2.md5

../nettle-$(NETTLE_VERSION).tar.gz:
	wget -nc https://ftp.gnu.org/gnu/nettle/nettle-$(NETTLE_VERSION).tar.gz -P ../
	cd .. && md5sum -c nettle-$(NETTLE_VERSION).tar.gz.md5



clean:
	rm -rf ../libiconv-1.15.tar.gz
	rm -rf ../ossp-uuid_1.6.2.orig.tar.gz
	rm -rf ../openssl-$(OPENSSL_VERSION).tar.gz
	rm -rf ../gmp-$(GMP_VERSION).tar.bz2
	rm -rf ../nettle-$(NETTLE_VERSION).tar.gz

.PHONY: clean download-iconv download-uuid