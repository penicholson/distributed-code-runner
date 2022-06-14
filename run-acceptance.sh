#!/bin/bash
docker run -v /home/peternicholson/m2_docker/:root/.m2 -v /home/peternicholson/Documents/github/petergood/distributed-code-runner:/dcr -it --privileged=true dcracceptancetests $1