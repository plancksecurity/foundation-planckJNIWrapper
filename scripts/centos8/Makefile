include ../../DEPENDENCIES
export
LIBPEPADAPTER_VERSION=${libpEpAdapter}
PEPENGINE_VERSION=${pEpEngine}
SEQUOIA_VERSION=${sequoia}
CURRENT_DISTRO=${CI_DISTRO_TARGET}
IMAGE_NAME=${DOCKER_REGISTRY_HOST}/pep-$(CURRENT_DISTRO)-jniadapter
DOCKERFILE=JNIAdapter.$(CURRENT_DISTRO).Dockerfile
PKG_BUILD_IMAGE=${DOCKER_REGISTRY_HOST}/fpm-$(CURRENT_DISTRO)
PKG_INSTALL_PATH=/opt/pEp
PKG_NAME=pEpJNIAdapter
PKG_DESCRIPTION="p≡p JNI Adapter Binary RPM Package"
PKG_DEPENDS="libpEpAdapter --depends java-1.8.0-openjdk"
PKG_INSTALL_PATH_STRING="/ /package/lib=${PKG_INSTALL_PATH}"
IS_TAGGED=${TAGGED_BUILD}
ifeq ($(IS_TAGGED), true)
# $CI_COMMIT_TAG is a predefined environment variable from Gitlab
	PEPJNIADAPTER_VERSION=${CI_COMMIT_TAG}
else
	PEPJNIADAPTER_VERSION=$(shell git rev-parse --short=8 HEAD)
endif
all: build

build:
	-docker pull $(IMAGE_NAME):latest
	cd ../../ && docker build --build-arg CURRENT_DISTRO=$(CURRENT_DISTRO) \
		      --build-arg DOCKER_REGISTRY_HOST=${DOCKER_REGISTRY_HOST} \
		      --build-arg PEPJNIADAPTER_VERSION=$(PEPJNIADAPTER_VERSION) \
		      --build-arg LIBPEPADAPTER_VERSION=$(LIBPEPADAPTER_VERSION) \
		      --build-arg PEPENGINE_VERSION=$(PEPENGINE_VERSION) \
		      --build-arg SEQUOIA_VERSION=$(SEQUOIA_VERSION) \
		      --cache-from $(IMAGE_NAME):latest \
		      --tag=$(IMAGE_NAME):$(PEPJNIADAPTER_VERSION) \
		      --tag=$(IMAGE_NAME):$(PEPJNIADAPTER_VERSION)_libpepadapter-$(LIBPEPADAPTER_VERSION)_engine-$(PEPENGINE_VERSION) \
		      --tag=$(IMAGE_NAME):latest \
		      -f scripts/${CURRENT_DISTRO}/$(DOCKERFILE) .
	docker push $(IMAGE_NAME):$(PEPJNIADAPTER_VERSION)
	docker push $(IMAGE_NAME):$(PEPJNIADAPTER_VERSION)_libpepadapter-$(LIBPEPADAPTER_VERSION)_engine-$(PEPENGINE_VERSION)
	docker push $(IMAGE_NAME):latest

java8:
	-docker pull $(IMAGE_NAME)-java8:latest
	cd ../../ && docker build --build-arg CURRENT_DISTRO=$(CURRENT_DISTRO) \
		      --build-arg DOCKER_REGISTRY_HOST=${DOCKER_REGISTRY_HOST} \
		      --build-arg PEPJNIADAPTER_VERSION=$(PEPJNIADAPTER_VERSION) \
		      --build-arg LIBPEPADAPTER_VERSION=$(LIBPEPADAPTER_VERSION) \
		      --build-arg PEPENGINE_VERSION=$(PEPENGINE_VERSION) \
		      --build-arg SEQUOIA_VERSION=$(SEQUOIA_VERSION) \
		      --cache-from $(IMAGE_NAME):latest \
		      --tag=$(IMAGE_NAME)-java8:$(PEPJNIADAPTER_VERSION) \
		      --tag=$(IMAGE_NAME)-java8:$(PEPJNIADAPTER_VERSION)_libpepadapter-$(LIBPEPADAPTER_VERSION)_engine-$(PEPENGINE_VERSION) \
		      --tag=$(IMAGE_NAME)-java8:latest \
		      -f scripts/${CURRENT_DISTRO}/java8.$(DOCKERFILE) .
	docker push $(IMAGE_NAME)-java8:$(PEPJNIADAPTER_VERSION)
	docker push $(IMAGE_NAME)-java8:$(PEPJNIADAPTER_VERSION)_libpepadapter-$(LIBPEPADAPTER_VERSION)_engine-$(PEPENGINE_VERSION)
	docker push $(IMAGE_NAME)-java8:latest
rpm:
	-docker pull $(PKG_BUILD_IMAGE)-jniadapter:latest
	@docker build --build-arg CURRENT_DISTRO=$(CURRENT_DISTRO) \
		--build-arg PEPJNIADAPTER_VERSION=$(PEPJNIADAPTER_VERSION) \
		--build-arg DOCKER_REGISTRY_HOST=${DOCKER_REGISTRY_HOST} \
		--build-arg PKG_INSTALL_PATH=$(PKG_INSTALL_PATH) \
		--cache-from $(PKG_BUILD_IMAGE)-jniadapter:latest \
		--tag=$(PKG_BUILD_IMAGE)-jniadapter:$(PEPJNIADAPTER_VERSION) \
		--tag=$(PKG_BUILD_IMAGE)-jniadapter:latest \
		packages/rpm
	@docker push $(PKG_BUILD_IMAGE)-jniadapter:$(PEPJNIADAPTER_VERSION)
	@docker push $(PKG_BUILD_IMAGE)-jniadapter:latest
	@docker run -e PEPJNIADAPTER_VERSION=$(PEPJNIADAPTER_VERSION) \
		-e PKG_VERSION=$(PEPJNIADAPTER_VERSION) \
		-e PKG_INSTALL_PATH=$(PKG_INSTALL_PATH) \
		-e PKG_NAME=$(PKG_NAME) \
		-e PKG_DESCRIPTION=$(PKG_DESCRIPTION) \
		-e PKG_DEPENDS=$(PKG_DEPENDS) \
		-e PKG_INSTALL_PATH_STRING=$(PKG_INSTALL_PATH_STRING) \
		--rm -v $(shell pwd)/packages/rpm/create-jniadapter-rpm.sh:/usr/bin/create-rpm.sh:ro  \
		-v $(shell pwd)/out:/out \
		-w / $(PKG_BUILD_IMAGE)-jniadapter:${PEPJNIADAPTER_VERSION} \
		/usr/bin/create-rpm.sh
