# Copyright 2022, pEp Foundation
# This file is part of pEpJNIAdapter for Android  - MultiArch  build
# This file may be used under the terms of the GNU General Public License version 3
# see LICENSE.txt

include ../Makefile.conf

all: build

build: showsetup boost-install

#------------------------------------------------------------------------------#
# Manage paths for PREFIX, DESTDIR, LOCAL and PATH

EXTERNAL_ROOT := $(shell pwd)

# install root for built files
DESTDIR = $(EXTERNAL_ROOT)/..
LOCAL := $(DESTDIR)/output

# debugging stuff

showsetup:
	@echo "ANDROID_NDK: $(ANDROID_NDK)"
	@echo "PREFIX: $(LOCAL)"
	@echo "BOOST_VERSION: $(BOOST_VERSION)"
	@echo "NCPU: $(NCPU)"

#------------------------------------------------------------------------------#
# boost
boost.src.stamp:../downloads/boost-for-android.tar.gz
	mkdir -p boost
	cd boost && tar xvf ../$<
	touch $@


boost.stamp: boost.src.stamp
	mkdir -p $(LOCAL)
	cd boost; NCPU=$(NCPU) bash ./build-android.sh $(ANDROID_NDK) \
		--boost=$(BOOST_VERSION) \
		--layout=system \
		--with-libraries=regex,iostreams,system \
		--prefix=$(LOCAL)
	touch $@

boost-install: boost.stamp

boost-clean:
	rm -rf boost.stamp

clean-all: clean
	rm -rf boost*
	rm -rf *.git
	rm -rf *.stamp
#------------------------------------------------------------------------------#

clean: boost-clean

.PHONY = clean clean-all boost-clean boost-install showsetup
