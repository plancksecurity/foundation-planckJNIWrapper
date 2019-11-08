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

.PHONY: all build clean clean-all

all: build

build: downloads/downloads.stamp
	$(MAKE) -C armeabi-v7a
	$(MAKE) -C arm64-v8a

clean:
	$(MAKE) -C armeabi-v7a clean
	$(MAKE) -C arm64-v8a clean

clean-all:
	$(MAKE) -C downloads clean
	$(MAKE) -C armeabi-v7a clean-all
	$(MAKE) -C arm64-v8a clean-all

downloads/downloads.stamp:
	$(MAKE) -C downloads

assets:
