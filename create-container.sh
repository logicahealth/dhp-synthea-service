#!/bin/bash

user_specified_image_name=$1
source docker-env.sh

if [ -n "$user_specified_image_name" ] ;
then 
    image_name=$user_specified_image_name 
    valid=`docker images ${image_name} --format "{{.Repository}}:{{.Tag}}\t{{.Tag}}" |sort -n -k2 -r |head -n 1`
    [[ -z "$valid" ]] && echo "image name specified is not valid." && exit 1
else 
    #find the highest version image
    image_name=`docker images ${app_name} --format "{{.Repository}}:{{.Tag}}\t{{.Tag}}" |sort -n -k2 -r |head -n 1 | awk '{print $1}'`
    [[ -z "$image_name" ]] && echo "No image name specified and could not find latest image for ${app_name}." && exit 1
fi

echo "Starting $app_name using image $image_name"
docker run --name $app_name --restart always -p $host_port:$container_port $volume_string -d $image_name

if [ $? -eq 0 ]
then
        echo "Successfully started container ${app_name}."
        exit 0
else
        echo "Errors encountered while starting container ${app_name}."
        exit 1
fi
