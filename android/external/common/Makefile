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

all:  download-iconv #download-uuid build-etpan download sequoia

download-iconv: ../libiconv-1.15.tar.gz


# Download source and patch it
../libiconv-1.15.tar.gz:
	wget https://ftp.gnu.org/pub/gnu/libiconv/libiconv-1.15.tar.gz -P ../

clean:
	rm -rf ../libiconv-1.15.tar.gz

.PHONY: clean download-iconv