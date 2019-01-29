FROM openjdk:8-jre-alpine
MAINTAINER dnsh

RUN mkdir -p /apps
WORKDIR /apps

EXPOSE 8094

COPY conf /apps/
ADD launcher/build/libs/launcher-0.0.1-SNAPSHOT-all.jar /apps/
# RUN java -jar /apps/launcher-0.0.1-SNAPSHOT-all.jar
RUN ls /apps