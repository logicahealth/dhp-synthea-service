Building the Synthea Docker Image
=================================

This folder contains the necessary Dockerfile which is used to create
the Synthea image that is used in the creation of the DHP Synthea
Service container for OSEHRA's Synthea compose scripts.

Building the Synthea Image
+++++++++++++++++++++++++++

The steps to build the image are quite simple.
1. First, acquire the Synthea source code from GitHub using Git:

..parsed-literal::

  $ git clone https://github.com/synthetichealth/synthea.git
  
Or via download:

  https://github.com/synthetichealth/synthea/archive/master.zip
  
2. Place the ``Dockerfile`` from this directory into the Synthea
   directory
3. Execute the Docker build command:

..parsed-literal::

  $ docker build -t osehra/synthea .
  
