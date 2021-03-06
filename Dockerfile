FROM openjdk:8-jre-alpine
MAINTAINER dnsh

RUN mkdir -p /apps
WORKDIR /apps

EXPOSE 8094

COPY launcher/build/libs/launcher-0.0.1-SNAPSHOT-all.jar /apps/
CMD java -Dvertx.cacheDirBase=/tmp/vertx-cache -jar /apps/launcher-0.0.1-SNAPSHOT-all.jar