# dhp-synthea-service

=======
This project allows the [Synthea](https://github.com/synthetichealth/synthea "Synthea's GitHub") synthetic data creation
 tool be called through an web API.  Synthea is a multi-threaded java application so a quartz scheduler is used
  to kick off the process.

Right now Synthea will output to a single directory.  Each time you create synthetic data with the API the output
directory will be deleted and new files created.  This means that you cannot have multiple users calling the API at the
same time.

## Getting Started



### Prerequisites

The Synthea code must be available on your machine. If you are running in a container the Synthea code must be part of
the base image.
You will add the location of the output directories to the dhp-synthea-service properties file.

This application only works on a *nix based operating system.  It will not run on Windows.

### Installing

Manual install
Install Synthea on your local system
update the following properties in the dhp-synteha-service application

```
//the root folder for your synthea install
synthea.root=../synthea
//output directory for Synthea.  This will be {synthea.root}/output
synthea.root.output=../synthea/output
//The FHIR directory where the patient files are output to. This will be {synthea.root.output}/fhir
synthea.root.output.fhir=../synthea/output/fhir
vista.url=https://someurl/addpatient
server.port=8021
origins.url=http://localhost:8080
```

run the following command to start the server
```
gradle clean bootRun
```

## Running the tests

There are still not that many tests for this since it was started as a prototype.

```
gradle test
```

## Deployment with Docker

There is a different application.properties file for each environment.  They are named application-{env].properties.
The appropriate env must be passed on the command line or it will default to dev.  The docker-env.sh has the Docker port
 mappings in it. As of this writing both the internal and external ports are 8021.

 Note: The base image used in the Docker file is a custom image with Synthea installed on it.  You will need access to
 the internet to download it.

```
deploy.sh dev
```

