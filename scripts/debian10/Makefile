include ../../DEPENDENCIES
export
LIBPEPADAPTER_VERSION=${libpEpAdapter}
PEPENGINE_VERSION=${pEpEngine}
SEQUOIA_VERSION=${sequoia}
CURRENT_DISTRO=$(shell basename $(shell pwd))
IMAGE_NAME=${DOCKER_REGISTRY_HOST}/pep-$(CURRENT_DISTRO)-jniadapter
DOCKERFILE=pEpJNIAdapter.$(CURRENT_DISTRO).Dockerfile
IS_TAGGED=${TAGGED_BUILD}
ifeq ($(IS_TAGGED), true)
# $CI_COMMIT_TAG is a predefined environment variable from Gitlab
	PEPJNIADAPTER_VERSION=${CI_COMMIT_TAG}
else
	PEPJNIADAPTER_VERSION=$(shell git rev-parse --short=8 HEAD)
endif
all:
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
