FROM maven:3.6.3-openjdk-14-slim AS build

COPY settings.xml /usr/share/maven/conf/

COPY pom.xml pom.xml
COPY iis-api/pom.xml iis-api/pom.xml
COPY iis-model/pom.xml iis-model/pom.xml
COPY iis-base/pom.xml iis-base/pom.xml

RUN mvn dependency:go-offline package -B

COPY iis-api/src iis-api/src
COPY iis-model/src iis-model/src
COPY iis-base/src iis-base/src

RUN mvn install

FROM openjdk:14-ea-jdk-alpine
USER root

RUN mkdir service

COPY --from=build /iis-base/target/ /service/
COPY /wz /service/wz

ADD https://github.com/ufoscout/docker-compose-wait/releases/download/2.5.0/wait /wait

RUN chmod +x /wait

ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005

EXPOSE 5005

CMD /wait && java --enable-preview -jar /service/iis-base-1.0-SNAPSHOT.jar -Xdebug