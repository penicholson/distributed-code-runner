#!/bin/bash
cp /dcr/acceptance-tests/test-nsjail.cfg /test-nsjail.cfg
mvn -f /dcr clean install -pl "$1" -am -DskipTests
mvn -f /dcr clean install -pl "$1"