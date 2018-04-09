# dhp-synthea-service

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



### Installing

A step by step series of examples that tell you have to get a development env running

To run on your local machine's file system.

```
Give the example
```

And repeat

```
until finished
```

End with an example of getting some data out of the system or using it for a little demo

## Running the tests

Explain how to run the automated tests for this system

### Break down into end to end tests

Explain what these tests test and why

```
Give an example
```

### And coding style tests

Explain what these tests test and why

```
Give an example
```

## Deployment

Add additional notes about how to deploy this on a live system

## Built With

* [Dropwizard](http://www.dropwizard.io/1.0.2/docs/) - The web framework used
* [Maven](https://maven.apache.org/) - Dependency Management
* [ROME](https://rometools.github.io/rome/) - Used to generate RSS Feeds

## Contributing

Please read [CONTRIBUTING.md](https://gist.github.com/PurpleBooth/b24679402957c63ec426) for details on our code of conduct, and the process for submitting pull requests to us.

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://github.com/your/project/tags).

## Authors

* **Billie Thompson** - *Initial work* - [PurpleBooth](https://github.com/PurpleBooth)

See also the list of [contributors](https://github.com/your/project/contributors) who participated in this project.

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

## Acknowledgments

* Hat tip to anyone who's code was used
* Inspiration
* etc
