#!/bin/bash
cp -R /dcr /dcr_build
cp /dcr_build/acceptance-tests/test-nsjail.cfg /test-nsjail.cfg
mvn -f /dcr_build clean install -pl "$1" -am -DskipTests
mvn -f /dcr_build clean install -pl "$1"