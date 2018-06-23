#!/bin/bash

## Generic master build script that chains together other scripts to build this app.
## other scripts needed:
## * compile.sh - should compile and package the application
## * deploy.sh - generic script that deploys the app to a docker container
##
## This script takes one parameter - the target DHP environment.  If not specified, dev is the default.


[[ -n "$1" ]] && env=$1 || env=dev

echo "Building for $env"

./compile.sh $env

if [ $? -ne 0 ]
then
	echo "compile step failed."
	exit 1
fi

./deploy.sh $env

if [ $? -ne 0 ]
then
	echo "deploy step failed."
	exit 1
fi

exit 0
