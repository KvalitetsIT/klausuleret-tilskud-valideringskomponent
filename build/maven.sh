#!/bin/sh

apt-get update
apt-get install -y docker.io

# Parent directory of parent directory of this script
SRC_FOLDER=$(dirname "$(dirname "$(readlink -f "$0")")")

cd $SRC_FOLDER
mvn clean install -Pdocker-test
