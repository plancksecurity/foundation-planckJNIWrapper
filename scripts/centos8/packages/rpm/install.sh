#!/bin/bash
set -exuo pipefail

# ===========================
# Distro
# ===========================

echo 7 >"${INSTPREFIX}/D_REVISION"

D_REV=$(cat ${INSTPREFIX}/D_REVISION)
D=""

D=${INSTPREFIX}/out

mkdir -p ${INSTPREFIX}/out
rm -rf ${INSTPREFIX}/out/*
mkdir -p "$D"/{bin,ld,lib/pEp,share/pEp,include/pEp}

tree ${INSTPREFIX}

# pEpJNIAdapter
cp -a ${INSTPREFIX}/lib/libpEpJNI.a "$D"/lib
cp -a ${INSTPREFIX}/lib/libpEpJNI.so "$D"/lib
cp -a ${INSTPREFIX}/lib/pEp.jar "$D"/lib

# versions
cp -a ${INSTPREFIX}/*.ver "$D"

find "$D"/lib -maxdepth 1 -type f -print -exec patchelf --set-rpath '$ORIGIN/pEp:$ORIGIN' {} \;
find "$D"/lib/pEp         -type f -print -exec patchelf --set-rpath '$ORIGIN' {} \;
find "$D"/bin -type f -print -exec patchelf --set-rpath '$ORIGIN/../lib/pEp:$ORIGIN/../lib' {} \;

ls -lh "$D"/*
du -sch "$D"
