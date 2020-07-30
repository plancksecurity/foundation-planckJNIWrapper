#!/usr/bin/env bash

ENGINE_LIB_PATH=$HOME/local-default/lib

export HOME=../resources/per-user-dirs/alice
export LD_LIBRARY_PATH=$ENGINE_LIB_PATH
export DYLD_LIBRARY_PATH=$ENGINE_LIB_PATH

cd ../../../../../
java -enableassertions -Xcheck:jni -cp .:../../src -Djava.library.path=.:../../src foundation.pEp.jniadapter.test.speedtest.SpeedTest $@
