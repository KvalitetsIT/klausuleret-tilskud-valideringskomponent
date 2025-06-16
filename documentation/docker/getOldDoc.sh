#!/usr/bin/env bash

if docker pull kvalitetsit/klausuleret-tilskud-valideringskomponent-documentation:latest; then
    echo "Copy from old documentation image."
    docker cp $(docker create kvalitetsit/klausuleret-tilskud-valideringskomponent-documentation:latest):/usr/share/nginx/html target/old
fi