docker build -t dcr-acceptance-tests -f dcracceptancetests/Dockerfile .
docker run -v /home/docker/.m2:/root/.m2 -it --privileged=true dcr-acceptance-tests