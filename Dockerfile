FROM openjdk:8-jre-alpine
MAINTAINER dnsh

RUN apt-get update
RUN apt-get upgrade -y

RUN mkdir -p /apps
WORKDIR /apps

RUN git clone https://github.com/dinsaw/valuestore.git
WORKDIR /apps/valuestore

EXPOSE 8094

RUN ./gradlew clean build fatjar
RUN java -jar launcher/build/libs/launcher-0.0.1-SNAPSHOT-all.jar
