#!/bin/bash

##
## This script takes one paramater - the target DHP environment.  If not specified, dev is the default.

[[ -n "$1" ]] && env=$1 || env=dev

echo "Deploying for $env"

source docker-env.sh

if [ -z "$BUILD_NUMBER" ] ;
then 
    build_number=0 
else 
    build_number=$BUILD_NUMBER
fi

# Find the running container, if there is one, and save off the container id and image

running_container=`docker ps --filter name=${app_name} --format {{.ID}}`

if [ -n "${running_container}" ] ;
then
    running_image=`docker ps -f name=${app_name} --format {{.Image}}`
    echo "DOCKER: Container ${running_container} based on image ${running_image} was running." 
    docker stop ${running_container} 
fi

current_container=`docker ps -a --filter name=${app_name} --format {{.ID}}`

if [ -n "${current_container}" ] ;
then
    echo "DOCKER: Removing Container ${current_container}." 
    docker rm ${current_container} 
fi

# Remove old containers and images
old_images=`docker images ${app_name} --format "{{.Repository}}:{{.Tag}}\t{{.Tag}}" |sort -n -k2 -r |tail -n +4`
echo "Old images: $old_images"
# set linefeed as the field separator IFS in the next two for loops
(
IFS='
'
    for i in $old_images;
    do
        old_image_name=`echo $i | awk '{print $1}'`
        old_containers=`docker ps -a -q --filter "ancestor=${old_image_name}"`
        for c in $old_containers;
        do
            echo "DOCKER: Removing Old Docker Container $c"
            docker rm $c
        done

        echo "DOCKER: Removing Old Docker Image ${old_image_name}"
        docker rmi ${old_image_name}
    done
)
#Create the new image
image_name=${app_name}:${build_number}
echo "DOCKER: Creating new image ${image_name} in env $env"
docker build --build-arg env=$env -t ${image_name} .

echo "DOCKER: Starting Container ${app_name}"
#Start a container with this image

./create-container.sh $image_name

if [ $? -eq 0 ]
then
        echo "Successfully deployed."
        exit 0
else
        echo "Errors encountered while deploying the app."
        exit 1
fi
