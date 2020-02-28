FROM openjdk:11-jdk

RUN apt-get -y update && apt-get -y install \
    maven \
    && rm -rf /var/lib/apt/lists/*

COPY . /dcr

RUN cd /dcr && mvn clean install -am -pl pl.petergood.dcr:configuration-service-backend


FROM openjdk:11-jdk

COPY --from=0 /dcr/configuration-service/configuration-service-backend/target/configuration-service-backend-1.0-SNAPSHOT-exec.jar /

ENTRYPOINT ["java", "-jar", "/configuration-service-backend-1.0-SNAPSHOT-exec.jar"]