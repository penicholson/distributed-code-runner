:: fulle2e
docker-compose -f e2e/full-e2e/docker-compose.yml up -d
timeout 10
call mvn clean install -am -pl pl.petergood.dcr:full-e2e -DskipTests
call mvn clean install -DargLine="-Ddcr.e2e.kafka.bootstrap.urls=192.168.99.100:9092 -Ddcr.e2e.configurationservice.url=http://192.168.99.100:8080" -pl pl.petergood.dcr:full-e2e
docker-compose -f e2e/full-e2e/docker-compose.yml down