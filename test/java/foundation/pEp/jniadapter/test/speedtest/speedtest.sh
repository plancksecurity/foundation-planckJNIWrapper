#!/usr/bin/env bash

PREFIX=$HOME/local

export HOME=../resources/per-user-dirs/alice
export LD_LIBRARY_PATH=$PREFIX/lib
export DYLD_LIBRARY_PATH=$PREFIX/lib

cd ../../../../../
java -enableassertions -Xcheck:jni -cp .:../../src -Djava.library.path=.:../../src foundation.pEp.jniadapter.test.speedtest.SpeedTest $@
