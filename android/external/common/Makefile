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

all:  download-iconv download-uuid #build-etpan download sequoia

download-iconv: ../libiconv-1.15.tar.gz

download-uuid: ../ossp-uuid_1.6.2.orig.tar.gz

# Download source and patch it
../libiconv-1.15.tar.gz:
	wget https://ftp.gnu.org/pub/gnu/libiconv/libiconv-1.15.tar.gz -P ../

../ossp-uuid_1.6.2.orig.tar.gz:
	wget http://http.debian.net/debian/pool/main/o/ossp-uuid/ossp-uuid_1.6.2.orig.tar.gz -P ../
	cd .. && md5sum -c ossp-uuid_1.6.2.orig.tar.gz.md5

clean:
	rm -rf ../libiconv-1.15.tar.gz
	rm -rf ../ossp-uuid_1.6.2.orig.tar.gz

.PHONY: clean download-iconv download-uuid