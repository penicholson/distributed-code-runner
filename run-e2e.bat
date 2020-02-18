docker-compose -f dcre2e/docker-compose.yml up -d
timeout 10
call mvn clean install -DargLine="-Ddcr.e2e.kafka.bootstrap.urls=192.168.99.100:9092" -am -pl dcre2e
docker-compose -f dcre2e/docker-compose.yml down