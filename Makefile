.PHONY: setup build run dev test test-integration lint fmt

setup:
	npm install
	@command -v go       > /dev/null || echo "warning: go not found       — brew install go"
	@command -v node     > /dev/null || echo "warning: node not found     — brew install node"
	@command -v rustc    > /dev/null || echo "warning: rustc not found    — brew install rust"
	@command -v kubectl  > /dev/null || echo "warning: kubectl not found  — brew install kubectl"
	@command -v helm     > /dev/null || echo "warning: helm not found     — brew install helm"
	@command -v skaffold > /dev/null || echo "warning: skaffold not found — brew install skaffold"
	@command -v gh       > /dev/null || echo "warning: gh not found       — brew install gh"
	@command -v jq       > /dev/null || echo "warning: jq not found       — brew install jq"
	@git rev-parse --git-dir > /dev/null 2>&1 && git config core.hooksPath .githooks || true

build:
	# TODO: add build command

run:
	# TODO: add run command

dev:
	skaffold dev

test:
	# TODO: add unit test command

test-integration:
	# TODO: add integration test command

lint:
	# TODO: add lint command

fmt:
	# TODO: add format command
