#!/bin/bash
cp /dcr/dcracceptancetests/test-nsjail.cfg /test-nsjail.cfg
mvn -f /dcr clean install -pl "$1" -am -DskipTests
mvn -f /dcr clean install -pl "$1"