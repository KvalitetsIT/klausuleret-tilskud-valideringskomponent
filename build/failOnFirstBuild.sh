#!/bin/sh

echo "${GITHUB_REPOSITORY}"
echo "${DOCKER_SERVICE}"
if [ "${GITHUB_REPOSITORY}" != "KvalitetsIT/klausuleret-tilskud-valideringskomponent" ] && [ "${DOCKER_SERVICE}" = "kvalitetsit/klausuleret-tilskud-valideringskomponent" ]; then
  echo "Please run setup.sh REPOSITORY_NAME"
  exit 1
fi
