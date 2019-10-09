FROM openjdk:11-jdk-stretch
RUN apt-get update
RUN apt-get install -y git
RUN mkdir synthea
COPY . /synthea
ENV GRADLE_OPTS=-Xmx512m
WORKDIR /synthea
RUN ./gradlew build -x test
WORKDIR /
