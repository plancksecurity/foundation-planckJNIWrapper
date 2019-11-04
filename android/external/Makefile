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

.PHONY: all build clean

build:
	$(MAKE) -C armeabi-v7a

all: build

clean:
	$(MAKE) -C armeabi-v7a clean

assets:
