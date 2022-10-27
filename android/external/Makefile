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

build-arm: armeabi-v7a/Makefile
	$(MAKE) -C armeabi-v7a

armeabi-v7a/Makefile: downloads/downloads.stamp
	sh createMakefile.sh armeabi-v7a

build-arm64: arm64-v8a/Makefile
	$(MAKE) -C arm64-v8a

arm64-v8a/Makefile: downloads/downloads.stamp
	sh createMakefile.sh arm64-v8a

build-x86: x86/Makefile
	$(MAKE) -C x86

x86/Makefile: downloads/downloads.stamp
	sh createMakefile.sh x86

build-x86_64: x86_64/Makefile
	$(MAKE) -C x86_64

x86_64/Makefile: downloads/downloads.stamp
	sh createMakefile.sh x86_64

clean:
	-$(MAKE) -C armeabi-v7a clean
	-$(MAKE) -C arm64-v8a clean
	-$(MAKE) -C x86 clean
	-$(MAKE) -C x86_64 clean

clean-all: clean
	$(MAKE) -C downloads clean
	rm -rf build
	rm -rf arm64-v8a
	rm -rf armeabi-v7a
	rm -rf x86
	rm -rf x86_64

downloads/downloads.stamp:
	$(MAKE) -C downloads

assets:

showAllSetups: showx86Setup showx86_64Setup showArmSetup showArm64Setup

showx86Setup:
	$(MAKE) -C x86 showsetup

showx86_64Setup:
	$(MAKE) -C x86_64 showsetup

showArmSetup:
	$(MAKE) -C armeabi-v7a showsetup

showArm64Setup:
	$(MAKE) -C arm64-v8a showsetup

createMakefiles: armeabi-v7a/Makefile arm64-v8a/Makefile x86/Makefile x86_64/Makefile
