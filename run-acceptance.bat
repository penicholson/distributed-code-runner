docker build -t dcr-acceptance-tests -f acceptancetests.Dockerfile .
docker run -v /home/docker/.m2:/root/.m2 -it --privileged=true dcr-acceptance-tests