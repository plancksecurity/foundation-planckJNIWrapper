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

.PHONY: all build clean clean-all build-arm build-arm64 build-x86 build-x86_64 showAllSetups \
	showx86Setup showx86_64Setup showArmSetup showArm64Setup createMakefiles cleanMakefiles \
	refreshMakefiles help

## Build dependencies for the archs passed. Example: make build archs=x86 arm64. If no archs passed, build for all 4 archs.
build: $(if $(archs),$(foreach wrd, $(archs), build-$(wrd)), all)

## Build dependencies for all 4 archs
all: build-arm build-arm64 build-x86 build-x86_64

## Call armeabi-v7a Makefile (build dependencies for arm arch)
build-arm: armeabi-v7a/Makefile downloads/downloads.stamp
	$(MAKE) -C armeabi-v7a

## Create Makefile for arm (armeabi-v7a) arch
armeabi-v7a/Makefile:
	sh createMakefile.sh armeabi-v7a

## Call arm64-v8a Makefile (build dependencies for arm64 arch)
build-arm64: arm64-v8a/Makefile downloads/downloads.stamp
	$(MAKE) -C arm64-v8a

## Create Makefile for arm64 (arm64-v8a) arch
arm64-v8a/Makefile:
	sh createMakefile.sh arm64-v8a

## Call x86 Makefile (build dependencies for x86 arch)
build-x86: x86/Makefile downloads/downloads.stamp
	$(MAKE) -C x86

## Create Makefile for x86 arch
x86/Makefile:
	sh createMakefile.sh x86

## Call x86_64 Makefile (build dependencies for x86_64 arch)
build-x86_64: x86_64/Makefile downloads/downloads.stamp
	$(MAKE) -C x86_64

## Create Makefile for x86_64 arch
x86_64/Makefile:
	sh createMakefile.sh x86_64

## Clean generated artifacts for all archs
clean:
	-$(MAKE) -C armeabi-v7a clean
	-$(MAKE) -C arm64-v8a clean
	-$(MAKE) -C x86 clean
	-$(MAKE) -C x86_64 clean

## Clean generated artifacts for all archs and remove downloaded files
clean-all: clean
	$(MAKE) -C downloads clean
	-rm -rf build
	-rm -rf arm64-v8a
	-rm -rf armeabi-v7a
	-rm -rf x86
	-rm -rf x86_64

## Download dependency files
downloads/downloads.stamp:
	$(MAKE) -C downloads

assets:

## Show setup values for all Makefiles (Needs for Makefiles to be already created first)
showAllSetups: showx86Setup showx86_64Setup showArmSetup showArm64Setup

## Show setup values for x86 Makefile
showx86Setup:
	-$(MAKE) -C x86 showsetup

## Show setup values for x86_64 Makefile
showx86_64Setup:
	-$(MAKE) -C x86_64 showsetup

## Show setup values for arm (armeabi-v7a) Makefile
showArmSetup:
	-$(MAKE) -C armeabi-v7a showsetup

## Show setup values for arm64 (arm64-v8a) Makefile
showArm64Setup:
	-$(MAKE) -C arm64-v8a showsetup

## Create all arch Makefiles
createMakefiles: armeabi-v7a/Makefile arm64-v8a/Makefile x86/Makefile x86_64/Makefile

## Delete all arch Makefiles
cleanMakefiles:
	-rm armeabi-v7a/Makefile
	-rm arm64-v8a/Makefile
	-rm x86/Makefile
	-rm x86_64/Makefile

## Refresh all arch Makefiles (delete and re-create all arch Makefiles)
refreshMakefiles: cleanMakefiles createMakefiles

include help.mk
helpHeader='pEpJNIAdapter/android/external Makefile'
helpPurpose='Cross-compile external dependencies of pEpEngine for the 4 Android archs \(x86, x86_64, arm, arm64\)'
