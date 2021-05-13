# Copyright 2019, pEp Foundation
# This file is part of pEpJNIAdapter Android Build
# This file may be used under the terms of the GNU General Public License version 3
# see LICENSE.txt

HERE_REL := $(notdir $(CURDIR))

include Makefile.conf

ifneq ($(wildcard local.conf),)
    $(info ================================================)
    $(info Overrides in `local.conf` are used.)
    $(info ================================================)
endif

ifdef BUILD_CONFIG
    $(info ================================================)
    $(info Overrides in `$(BUILD_CONFIG)` are used.)
    $(info ================================================)
endif

.PHONY: all build clean clean-all build-arm build-arm64 build-x86 build-x86_64

build: all

all: build-arm build-arm64 build-x86 build-x86_64

build-arm: downloads/downloads.stamp
	$(MAKE) -C armeabi-v7a

build-arm64: downloads/downloads.stamp
	$(MAKE) -C arm64-v8a

build-x86: downloads/downloads.stamp
	$(MAKE) -C x86

build-x86_64: downloads/downloads.stamp
	$(MAKE) -C x86_64

clean:
	$(MAKE) -C armeabi-v7a clean
	$(MAKE) -C arm64-v8a clean
	$(MAKE) -C x86 clean
	$(MAKE) -C x86_64 clean

clean-all:
	$(MAKE) -C downloads clean
	$(MAKE) -C armeabi-v7a clean-all
	$(MAKE) -C arm64-v8a clean-all
	$(MAKE) -C x86 clean-all
	$(MAKE) -C x86_64 clean-all
	rm -rf build

downloads/downloads.stamp:
	$(MAKE) -C downloads

assets:
