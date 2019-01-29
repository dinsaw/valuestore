FROM openjdk:8-jre-alpine
MAINTAINER dnsh

RUN apt-get update
RUN apt-get upgrade -y

RUN mkdir -p /apps
WORKDIR /apps

EXPOSE 8094

ADD launcher/build/libs/launcher-0.0.1-SNAPSHOT-all.jar /apps/
RUN java -jar /apps/launcher-0.0.1-SNAPSHOT-all.jar
