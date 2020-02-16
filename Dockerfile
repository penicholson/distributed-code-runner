# TODO: build nsjail binary in seperate build

FROM openjdk:11-jdk
RUN apt-get -y update && apt-get install -y \
    autoconf \
    bison \
    flex \
    gcc \
    g++ \
    git \
    libprotobuf-dev \
    libnl-route-3-dev \
    libtool \
    make \
    pkg-config \
    protobuf-compiler \
    maven \
    && rm -rf /var/lib/apt/lists/*

RUN git clone https://github.com/google/nsjail.git
RUN cd nsjail && make && mv /nsjail/nsjail /bin

COPY . /dcr
RUN chmod +x /dcr/tmp.sh

#ENTRYPOINT ["mvn", "-f", "/dcr", "clean", "install", "-pl", "dcracceptancetests", "-am"]
ENTRYPOINT ["/bin/bash", "/dcr/tmp.sh"]