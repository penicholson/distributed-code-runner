# compilationworker
docker-compose -f e2e/compilation-worker-e2e/docker-compose.yml up -d
sleep 10
mvn clean install -am -pl pl.petergood.dcr:compilation-worker-e2e -DskipTests
mvn clean install -DargLine="-Ddcr.e2e.kafka.bootstrap.urls=172.18.0.11:9092" -pl pl.petergood.dcr:compilation-worker-e2e
docker-compose -f e2e/compilation-worker-e2e/docker-compose.yml down