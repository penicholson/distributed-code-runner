#!/bin/bash
cp /dcr/acceptance-tests/test-nsjail.cfg /test-nsjail.cfg
mvn -f /dcr clean install -pl !e2e,!pl.petergood.dcr:compilation-worker-e2e,!pl.petergood.dcr:simple-runner-worker-e2e,!pl.petergood.dcr:full-e2e