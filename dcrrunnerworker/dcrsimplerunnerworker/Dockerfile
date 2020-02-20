FROM openjdk:11-jdk

RUN apt-get -y update && apt-get -y install \
    maven \
    && rm -rf /var/lib/apt/lists/*

COPY . /dcr

RUN cd /dcr && mvn clean install -am -pl pl.petergood:dcrsimplerunnerworker


FROM petergood/nsjail:latest

COPY --from=0 /dcr/dcrrunnerworker/dcrsimplerunnerworker/target/dcrsimplerunnerworker-1.0-SNAPSHOT-exec.jar /
COPY --from=0 /dcr/dcrrunnerworker/dcrsimplerunnerworker/nsjail.cfg /

ENV DCR_SIMPLERUNNERWORKER_JAIL_ROOT_PATH=/
ENV DCR_SIMPLERUNNERWORKER_JAIL_CONFIGURATION_PATH=/nsjail.cfg

ENTRYPOINT ["java", "-jar", "/dcrsimplerunnerworker-1.0-SNAPSHOT-exec.jar"]