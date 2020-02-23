FROM nsjail:latest

RUN apt-get -y update && apt-get install -y \
    maven \
    && rm -rf /var/lib/apt/lists/*

COPY acceptance-tests/run.sh /
RUN chmod +x /run.sh

ENTRYPOINT ["/bin/bash", "/run.sh"]