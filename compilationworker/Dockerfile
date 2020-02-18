FROM openjdk:11-jdk

RUN apt-get -y update && apt-get -y install \
    maven \
    && rm -rf /var/lib/apt/lists/*

COPY . /dcr

RUN cd /dcr && mvn clean install -am -pl compilationworker


FROM nsjail:latest

COPY --from=0 /dcr/compilationworker/target/compilationworker-1.0-SNAPSHOT-exec.jar /
COPY --from=0 /dcr/compilationworker/nsjail.cfg /

ENV DCR_COMPILATIONWORKER_JAIL_CONFIGURATION_PATH=/nsjail.cfg
ENV DCR_COMPILATIONWORKER_JAIL_ROOT_PATH=/jail

ENTRYPOINT ["java", "-jar", "/compilationworker-1.0-SNAPSHOT-exec.jar"]