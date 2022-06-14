# simplerunnerworker
docker-compose -f e2e/full-e2e/docker-compose.yml up -d
sleep 10
mvn clean install -am -pl pl.petergood.dcr:full-e2e -DskipTests
mvn clean install -DargLine="-Ddcr.e2e.kafka.bootstrap.urls=172.18.0.11:9092 -Ddcr.e2e.configurationservice.url=http://172.18.0.15:8080" -pl pl.petergood.dcr:full-e2e
docker-compose -f e2e/full-e2e/docker-compose.yml down