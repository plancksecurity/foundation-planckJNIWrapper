ARG DOCKER_REGISTRY_HOST
ARG CURRENT_DISTRO
ARG PEPENGINE_VERSION

FROM ${DOCKER_REGISTRY_HOST}/pep-${CURRENT_DISTRO}-engine:${PEPENGINE_VERSION}

ENV BUILDROOT /build
ENV INSTPREFIX /install
ENV OUTDIR /out

ARG LIBPEPADAPTER_VERSION
ARG CURRENT_DISTRO

## Build and install libpEpAdapter
### Setup working directory
RUN git clone --depth=1 \
    https://gitea.pep.foundation/pEp.foundation/libpEpAdapter.git \
    -b ${LIBPEPADAPTER_VERSION} \
    ${BUILDROOT}/libpEpAdapter
WORKDIR ${BUILDROOT}/libpEpAdapter

### Build libpEpAdapter
RUN sh ./scripts/${CURRENT_DISTRO}/build_libpEpAdapter.sh && \
    rm -rf ${BUILDROOT}/*


## Build and install pEpJNIAdapter
### Install Java 8
USER root
RUN apt-get update -yqq && \
    apt-get install -yqq apt-transport-https ca-certificates wget dirmngr gnupg2 software-properties-common && \
    wget -qO - https://adoptopenjdk.jfrog.io/adoptopenjdk/api/gpg/key/public | apt-key add - && \
    add-apt-repository --yes https://adoptopenjdk.jfrog.io/adoptopenjdk/deb/ && \
    apt-get update -yqq && \
    apt-get install -yqq adoptopenjdk-8-hotspot && \
    rm -rf /var/lib/apt/lists/*

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

### Build pEpJNIAdapter
RUN sh ./scripts/${CURRENT_DISTRO}/build_pEpJNIAdapter.sh && \
    install -m 644 -t ${INSTPREFIX}/lib dist/libpEpJNI.a && \
    install -m 755 -t ${INSTPREFIX}/lib dist/libpEpJNI.so && \
    install -m 644 -t ${INSTPREFIX}/lib dist/pEp.jar && \
    rm -rf ${BUILDROOT}/*
