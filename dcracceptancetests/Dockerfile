FROM nsjail:latest

RUN apt-get -y update && apt-get install -y \
    maven \
    && rm -rf /var/lib/apt/lists/*

COPY . /dcr
RUN cp /dcr/dcracceptancetests/test-nsjail.cfg /
RUN chmod +x /dcr/dcracceptancetests/run.sh

ENTRYPOINT ["/bin/bash", "/dcr/dcracceptancetests/run.sh"]