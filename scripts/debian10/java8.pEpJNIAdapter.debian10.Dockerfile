ARG DOCKER_REGISTRY_HOST
ARG CURRENT_DISTRO
ARG LIBPEPADAPTER_VERSION
ARG PEPENGINE_VERSION
FROM ${DOCKER_REGISTRY_HOST}/pep-${CURRENT_DISTRO}-libpepadapter:${LIBPEPADAPTER_VERSION}_engine-${PEPENGINE_VERSION}

ENV BUILDROOT /build
ENV INSTPREFIX /install
ENV OUTDIR /out

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

### Build libpEpAdapter
RUN sh ./scripts/${CURRENT_DISTRO}/build_pEpJNIAdapter.sh && \
    install -m 644 -t ${INSTPREFIX}/lib dist/libpEpJNI.a && \
    install -m 755 -t ${INSTPREFIX}/lib dist/libpEpJNI.so && \
    install -m 644 -t ${INSTPREFIX}/lib dist/pEp.jar && \
    rm -rf ${BUILDROOT}/*
