#!/bin/sh -x

DATA=/data

if [ -z $1 ]; then
    echo "!ERR: no dataset name provided!"
    exit 1
fi

DS="$1"

if [ ! -d ${DATA}/${DS} ]; then
    echo "!ERR: dataset directory doesn't exist!"
    exit 2
fi

if [ ! -f ${DATA}/${DS}/${DS}.hdt ]; then
    echo "?INF: HDT doesn't exist yet"

    if [ ! -f ${DATA}/${DS}/${DS}.nq ]; then
        echo "?INF: NQ doesn't exist yet"

        if [ ! -d ${DATA}/${DS}/CSV ]; then
            echo "!ERR: dataset CSV directory doesn't exist!"
            exit 3
        fi

        # turn CSV into NQ
        echo "?INF: generate NQ"
        python3 /app/convert-to-RDF.py ${DATA}/${DS}/CSV
        if [ $? ]; then
            exit $?
        fi
    fi

    # turn NQ into HDT
    echo "?INF: generate HDT"

fi

# get rid of $DS in $1
shift

java -jar wp4-links-0.0.1-SNAPSHOT-jar-with-dependencies.jar --inputData ${DATA}/${DS}/${DS}.hdt --outputDir ${DATA}/${DS} $*