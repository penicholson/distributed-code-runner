# configurationservice
docker-compose -f e2e/configuration-service-e2e/docker-compose.yml up -d
sleep 10
mvn clean install -am -pl pl.petergood.dcr:configuration-service-e2e -DskipTests
mvn clean install -DargLine="-Ddcr.e2e.configurationservice.url=http://172.18.0.11:8080" -pl pl.petergood.dcr:configuration-service-e2e
docker-compose -f e2e/configuration-service-e2e/docker-compose.yml down