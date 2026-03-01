#!/usr/bin/env bash
set -euo pipefail

if [[ $# -lt 1 ]]; then
  echo "Usage: $0 <BASE_URL>"
  echo "Example: $0 https://shop-service.onrender.com"
  exit 1
fi

BASE_URL="${1%/}"

echo "[1/6] Health"
curl -fsS "${BASE_URL}/health" >/dev/null

echo "[2/6] OpenAPI"
curl -fsS "${BASE_URL}/openapi" >/dev/null

echo "[3/6] Swagger"
curl -fsS "${BASE_URL}/swagger" >/dev/null

echo "[4/6] Register smoke user"
curl -fsS -X POST "${BASE_URL}/auth/register" \
  -H "Content-Type: application/json" \
  -d '{"email":"deploy-smoke@example.com","password":"strongPass123"}' >/dev/null || true

echo "[5/6] Login smoke user"
LOGIN_RESPONSE="$(curl -fsS -X POST "${BASE_URL}/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"email":"deploy-smoke@example.com","password":"strongPass123"}')"
echo "${LOGIN_RESPONSE}" | grep -q "accessToken"

echo "[6/6] Public products"
curl -fsS "${BASE_URL}/products" >/dev/null

echo "Post-deploy checks passed for ${BASE_URL}"
