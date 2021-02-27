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

FROM python:3.9.1-alpine3.13
#alpine:3.13.1 

RUN apk update && apk upgrade && \
    apk add --no-cache openjdk8-jre g++
    #python3 py3-pip

RUN mkdir -p /app

WORKDIR /app

COPY --from=build /app/wp4-links/target/wp4-links-0.0.1-SNAPSHOT-jar-with-dependencies.jar /app/

COPY --from=build /app/wp4-links/assets/docker/entrypoint.sh /app/
RUN chmod +x /app/entrypoint.sh

RUN pip install wheel pandas

COPY --from=build /app/wp4-links/assets/csv-to-rdf/convert-zeeland-to-RDF.py /app/convert-to-RDF.py

ENTRYPOINT ["/app/entrypoint.sh"] 