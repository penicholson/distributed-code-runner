FROM petergood/nsjail:latest

RUN apt-get -y update && apt-get install -y \
    maven \
    && rm -rf /var/lib/apt/lists/*