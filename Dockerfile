FROM openjdk:8-jre-alpine
MAINTAINER dnsh

RUN mkdir -p /apps
WORKDIR /apps

EXPOSE 8094

COPY conf /apps/conf
COPY launcher/build/libs/launcher-0.0.1-SNAPSHOT-all.jar /apps/
CMD java -jar /apps/launcher-0.0.1-SNAPSHOT-all.jar vertx.cacheDirBase=/tmp/vertx-cache