#!/bin/bash

ENDPOINT="http://localhost:8000"

echo "Deletando tabelas..."

aws dynamodb delete-table \
  --table-name pokemon-api-capture-local \
  --endpoint-url $ENDPOINT || true

aws dynamodb delete-table \
  --table-name pokemon-api-users-local \
  --endpoint-url $ENDPOINT || true

echo "Criando tabelas..."

aws dynamodb create-table \
--cli-input-json file://docker/dynamodb/tables/capture-pokemon.json \
--endpoint-url $ENDPOINT || true \
--region us-east-1

aws dynamodb create-table \
--cli-input-json file://docker/dynamodb/tables/users.json \
--endpoint-url $ENDPOINT || true \
--region us-east-1

echo "Pronto."