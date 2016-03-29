#! /bin/sh

set -x

a=0

curl http://localhost:8111/clear

while [ $a -lt 100 ]
do
   echo $a
   sleep 2
   curl -X POST http://localhost:8111/entry -H "Content-Type: application/json" -d '{"subject": "data data data '$a'", "adjective": "String  data  data  data  data '$a'", "timestamp": "2016-03-01T12:00:00.000Z", "language": "spanish", "donation": 20}'
   sleep 2
   a=`expr $a + 1`
   curl -X POST http://localhost:8111/entry -H "Content-Type: application/json" -d '{"subject": "data data data '$a'", "adjective": "String  data  data  data  data '$a'", "timestamp": "2016-03-01T12:00:00.000Z", "language": "spanish", "donation": 50}'
   sleep 2
   a=`expr $a + 1`
   curl -X POST http://localhost:8111/entry -H "Content-Type: application/json" -d '{"subject": "data data data '$a'", "adjective": "String  data  data  data  data '$a'", "timestamp": "2016-03-01T12:00:00.000Z", "language": "spanish", "donation": 100}'
done