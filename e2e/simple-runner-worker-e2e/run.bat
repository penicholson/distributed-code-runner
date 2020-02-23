:: simplerunnerworker
docker-compose -f e2e/simple-runner-worker-e2e/docker-compose.yml up -d
timeout 10
call mvn clean install -am -pl pl.petergood.dcr:simple-runner-worker-e2e -DskipTests
call mvn clean install -DargLine="-Ddcr.e2e.kafka.bootstrap.urls=192.168.99.100:9092" -pl pl.petergood.dcr:simple-runner-worker-e2e
docker-compose -f e2e/simple-runner-worker-e2e/docker-compose.yml down