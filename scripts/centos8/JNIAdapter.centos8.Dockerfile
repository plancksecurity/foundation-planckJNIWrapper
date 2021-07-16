ARG DOCKER_REGISTRY_HOST
ARG CURRENT_DISTRO
ARG LIBPEPADAPTER_VERSION
FROM ${DOCKER_REGISTRY_HOST}/pep-${CURRENT_DISTRO}-libpepadapter:${LIBPEPADAPTER_VERSION}

ENV BUILDROOT /build
ENV INSTPREFIX /install
ENV OUTDIR /out

ARG JNIADAPTER_VERSION

## Install system dependencies
USER root
RUN yum -y install time java-1.8.0-openjdk java-1.8.0-openjdk-devel && \
    yum clean all

## Build and install pEpJNIAdapter
### Setup working directory
RUN mkdir ${BUILDROOT}/pEpJNIAdapter
COPY . ${BUILDROOT}/pEpJNIAdapter
RUN chown -R pep-builder:pep-builder ${BUILDROOT}/pEpJNIAdapter
USER pep-builder
WORKDIR ${BUILDROOT}/pEpJNIAdapter

RUN sh ./scripts/common/build_pEpJNIAdapter.sh && \
    install -m 644 -t ${INSTPREFIX}/lib dist/libpEpJNI.a && \
    install -m 755 -t ${INSTPREFIX}/lib dist/libpEpJNI.so && \
    install -m 644 -t ${INSTPREFIX}/lib dist/pEp.jar && \
    echo "${pepjni_ver}">${INSTPREFIX}/pEp_JNI.ver && \
    rm -rf ${BUILDROOT}/*
