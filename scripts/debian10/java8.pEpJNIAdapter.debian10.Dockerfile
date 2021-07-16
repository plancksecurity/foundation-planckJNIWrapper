ARG DOCKER_REGISTRY_HOST
ARG CURRENT_DISTRO
ARG PEPENGINE_VERSION

FROM ${DOCKER_REGISTRY_HOST}/pep-${CURRENT_DISTRO}-engine:${PEPENGINE_VERSION} AS builder

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
RUN sh ./scripts/common/build_pEpJNIAdapter.sh && \
    install -m 644 -t ${INSTPREFIX}/lib dist/libpEpJNI.a && \
    install -m 755 -t ${INSTPREFIX}/lib dist/libpEpJNI.so && \
    install -m 644 -t ${INSTPREFIX}/lib dist/pEp.jar

### Cleanup
RUN find /install -name '*.a' -delete && \
    rm  -rf /install/lib/sequoia/ && \
    rm -f /install/lib/libsequoia_ffi.so*

## Switch to final image
FROM openjdk:8-slim-buster@sha256:cf05a507973843bd6a1ded846c94a5e655e0a3d4796ae17de54545403252901a

ENV LD_LIBRARY_PATH=/install/lib:/install/libetpan/lib:$LD_LIBRARY_PATH

COPY --from=builder /install/include /install/include
COPY --from=builder /install/lib /install/lib
COPY --from=builder /install/libetpan /install/libetpan
COPY --from=builder /share/pEp /share/pEp
COPY --from=builder /build /build

RUN apt-get update -yqq && \
    apt-get install -yqq --no-install-recommends sqlite3 && \
    rm -rf /var/lib/apt/lists/*
