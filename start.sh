#!/bin/bash

user_specified_image_name=$1
source docker-env.sh

echo "Starting container $app_name"
docker start ${app_name}

if [ $? -eq 0 ]
then
        echo "Successfully started container ${app_name}."
        exit 0
else
        echo "Errors encountered while starting container ${app_name}."
        exit 1
fi
