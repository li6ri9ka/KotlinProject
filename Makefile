SHELL := /bin/zsh

.PHONY: test up down smoke deploy-check ci-local

test:
	./gradlew clean test

up:
	docker compose --env-file .env up -d --build

down:
	docker compose down -v

smoke:
	./scripts/smoke_api.sh

deploy-check:
	./scripts/post_deploy_check.sh $(BASE_URL)

ci-local:
	./gradlew clean test assemble
	docker build -t shop-service:local .
	docker build -f Dockerfile.worker -t shop-worker:local .
