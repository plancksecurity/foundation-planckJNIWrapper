#!/usr/bin/env sh
set -exo

export LC_ALL=en_US.UTF-8 && \

# JAVA_HOME (only for the pEpJNIAdapter)
if [ $(uname) == "Linux" ]; then {
  export JAVA_HOME=$(dirname $(dirname $(readlink -f /usr/bin/javac)));
} fi
if [ $(uname) == "Darwin" ]; then {
  export JAVA_HOME=$(dirname $(dirname $(readlink /usr/bin/javac)));
} fi
echo $JAVA_HOME

cat >local.conf <<__LOCAL__
PREFIX=${INSTPREFIX}
DEBUG=0
JAVA_HOME=${JAVA_HOME}
YML2_PATH=${INSTPREFIX}/yml2
YML2_PROC=${INSTPREFIX}/yml2/yml2proc --encoding=utf8
#YML2_OPTS=--encoding=utf8
ENGINE_INC_PATH=${INSTPREFIX}/include
ENGINE_LIB_PATH=${INSTPREFIX}/lib
AD_INC_PATH=${INSTPREFIX}/include
AD_LIB_PATH=${INSTPREFIX}/lib
__LOCAL__

make WARN= DEBUG=
