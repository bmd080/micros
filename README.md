# Micro Services Of Spring Cloud

## License
Apache License 2.0.

## Local config path
https://github.com/bmd080/micros/tree/master/config

## Run maven command
mvn clean install

## Startup docker container
cd ./docker
docker-compose up

## Access services
$ open $(echo \"$(echo $DOCKER_HOST)\"|
  \sed 's/tcp:\/\//http:\/\//g'|
  \sed 's/[0-9]\{4,\}/8761/g'|
  \sed 's/\"//g')
