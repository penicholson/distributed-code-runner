#!/bin/bash
cp -R /dcr /dcr_build
cp /dcr_build/acceptance-tests/test-nsjail.cfg /test-nsjail.cfg
mvn -f /dcr_build clean install -pl !e2e,!pl.petergood.dcr:compilation-worker-e2e,!pl.petergood.dcr:simple-runner-worker-e2e,!pl.petergood.dcr:configuration-service-e2e,!pl.petergood.dcr:full-e2e