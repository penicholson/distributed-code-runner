FROM openjdk:11-jdk

RUN apt-get -y update && apt-get -y install \
    maven \
    && rm -rf /var/lib/apt/lists/*

COPY . /dcr

RUN cd /dcr && mvn clean install -am -pl compilation-worker


FROM nsjail:latest

COPY --from=0 /dcr/compilation-worker/target/compilation-worker-1.0-SNAPSHOT-exec.jar /
COPY --from=0 /dcr/compilation-worker/nsjail.cfg /

ENV DCR_COMPILATIONWORKER_JAIL_CONFIGURATION_PATH=/nsjail.cfg
ENV DCR_COMPILATIONWORKER_JAIL_ROOT_PATH=/jail

ENTRYPOINT ["java", "-jar", "/compilation-worker-1.0-SNAPSHOT-exec.jar"]