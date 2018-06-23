#!/bin/bash

source docker-env.sh

echo "Stopping $app_name container"
docker stop $app_name 
