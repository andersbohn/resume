#!/bin/bash
set -euo pipefail
sbt "runMain dk.bohnjespersen.anders.resume.util.StaticExporter"
rsync -av --delete-after src/main/resources/ target/dist/assets/

BRANCH_FLAG=""
if [[ "${1:-}" == "prod" ]]; then
  echo "🚀 DEPLOYING TO PRODUCTION..."
  BRANCH_FLAG="--branch=main"
else
  echo "🧪 DEPLOYING PREVIEW..."
fi

npx wrangler pages deploy target/dist --project-name=cv-zio-site $BRANCH_FLAG