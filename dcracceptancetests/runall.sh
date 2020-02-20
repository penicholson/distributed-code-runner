#!/bin/bash
cp /dcr/dcracceptancetests/test-nsjail.cfg /test-nsjail.cfg
mvn -f /dcr clean install -pl !dcre2e,!pl.petergood:dcrcompilationworkere2e,!pl.petergood:dcrsimplerunnerworkere2e,!pl.petergood:fulle2e