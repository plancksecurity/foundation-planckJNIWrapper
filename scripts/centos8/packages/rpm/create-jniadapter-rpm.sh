#!/bin/bash -ex
# we should always set proper ownership before exiting, otherwise
# the created packages will have root:root ownership and we'll be unable
# to delete them from our host.
trap 'chown -R --reference /usr/bin/create-rpm.sh /out/' EXIT

# the source directory is mounted read-only to prevent issues where the build
# could alter the source; we should copy it somewhere inside the container
cd /source/out
ls -alh
tree
INSTALL_TOP=/package
mkdir -p ${INSTALL_TOP}/lib
cp -ar lib/* ${INSTALL_TOP}/lib/.


cd /out

#this would be the no-signature command line
fpm -t rpm -s dir \
	-n ${PKG_NAME} \
	--version ${PKG_VERSION} \
	--description "${PKG_DESCRIPTION}" \
	--depends ${PKG_DEPENDS} \
	-C ${PKG_INSTALL_PATH_STRING}
