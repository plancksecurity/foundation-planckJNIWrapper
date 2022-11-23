mkfileName = $(firstword $(MAKEFILE_LIST))
helpHeader = $(notdir $(abspath $(dir $(mkfileName))))/$(mkfileName)

TARGET_MAX_CHAR_NUM=21
## Show help
help:
	@echo ''
	@echo '${BOLD}${CYAN}============= $(helpHeader) =============${RESET}'
	@echo ''
	@echo '${BOLD}Purpose:${RESET}'
	@echo '  $(helpPurpose)'
	@echo ''
	@echo '${BOLD}Usage:${RESET}'
	@echo '  ${YELLOW}make${RESET} ${GREEN}<target>${RESET}'
	@echo ''
	@echo '${BOLD}Targets:${RESET}'
	@awk '/^[a-zA-Z\-\_0-9]+:/ { \
		helpMessage = match(lastLine, /^## (.*)/); \
		if (helpMessage) { \
			helpCommand = substr($$1, 0, index($$1, ":")-1); \
			helpMessage = substr(lastLine, RSTART + 3, RLENGTH); \
			printf "  ${YELLOW}%-$(TARGET_MAX_CHAR_NUM)s${RESET} ${GREEN}%s${RESET}\n", helpCommand, helpMessage; \
		} \
	} \
	{ lastLine = $$0 }' $(MAKEFILE_LIST)

.PHONY: help-base
