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
    && rm -rf /var/lib/apt/lists/*

RUN git clone https://github.com/google/nsjail.git
RUN cd nsjail && make && mv /nsjail/nsjail /bin
RUN rm -r -f /nsjail

RUN apt-get remove -y \
    git