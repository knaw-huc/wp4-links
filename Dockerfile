FROM alpine:3.13.1 AS build

RUN apk update && apk upgrade && \
    apk add --no-cache bash git openssh \
    openjdk8 maven

RUN mkdir -p /app

WORKDIR /app

COPY . /app/wp4-links


RUN if [ ! -f /app/wp4-links/pom.xml ]; then rm -rf wp4-links; git clone https://github.com/knaw-huc/wp4-links.git; fi
RUN cd /app/wp4-links && \
    mvn install

FROM alpine:3.13.1 

RUN apk update && apk upgrade && \
    apk add --no-cache openjdk8-jre

RUN mkdir -p /app

WORKDIR /app

COPY --from=build /app/wp4-links/target/wp4-links-0.0.1-SNAPSHOT-jar-with-dependencies.jar /app/

ENTRYPOINT ["java","-jar", "wp4-links-0.0.1-SNAPSHOT-jar-with-dependencies.jar"] 