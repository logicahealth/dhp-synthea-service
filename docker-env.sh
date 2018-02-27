#!/bin/bash

export app_name=dhp-synthea-service

#Coordinate host port with the reverse proxy config on server where containers are run
export host_port="8021"

#Container port is what your app is listening on inside your container.  Spring Boot default = 8080, nginx = 80, tomcat = 8080
export container_port="8021"

#Volume string or strings, should contain -v <volume> [-v <volume> ...]
#volume_string="-v /data/dhp-consent/logs:/data/dhp/logs:rw"

#other docker options custom to this app.
export docker_opts=


