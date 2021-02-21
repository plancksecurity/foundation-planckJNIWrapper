ARG DOCKER_REGISTRY_HOST
ARG CURRENT_DISTRO
ARG LIBPEPADAPTER_VERSION
ARG PEPENGINE_VERSION
FROM ${DOCKER_REGISTRY_HOST}/pep-${CURRENT_DISTRO}-libpepadapter:${LIBPEPADAPTER_VERSION}_engine-${PEPENGINE_VERSION}

ENV BUILDROOT /build
ENV INSTPREFIX /install
ENV OUTDIR /out

### Install system dependencies
USER root
RUN apt-get update -yqq && \
    apt-get install -yqq default-jdk-headless
USER pep-builder

### Setup working directory
RUN mkdir ${BUILDROOT}/pEpJNIAdapter
COPY . ${BUILDROOT}/pEpJNIAdapter
USER root
RUN chown -R pep-builder:pep-builder ${BUILDROOT}/pEpJNIAdapter
USER pep-builder
WORKDIR ${BUILDROOT}/pEpJNIAdapter

ARG PEPJNIADAPTER_VERSION
ARG CURRENT_DISTRO

### Build libpEpAdapter
RUN sh ./scripts/${CURRENT_DISTRO}/build_pEpJNIAdapter.sh && \
    rm -rf ${BUILDROOT}/*
