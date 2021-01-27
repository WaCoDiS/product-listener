# WaCoDiS Product Listener
![Java CI](https://github.com/WaCoDiS/metadata-connector/workflows/Java%20CI/badge.svg)
  
The **WaCoDiS Product Listener** requets processing result (EO-Products) from the processing environment and ingest those result data sets into a data backend.

**Table of Content**  

1. [WaCoDiS Project Information](#wacodis-project-information)
  * [Architecture Overview](#architecture-overview)
2. [Overview](#overview)
  * [Modules](#modules)
  * [Utilized Technologies](#utilized-technologies)
3. [Installation / Building Information](#installation--building-information)
  * [Build from Source](#build-from-source)
  * [Build using Docker](#build-using-docker)
  * [Configuration](#configuration)
    * [Parameters](#parameters)
  * [Deployment](#deployment)
    * [Preconditions](#preconditions)
4. [User Guide](#user-guide)
  * [Run Product Listener](#run-product-listener)
    * [Using Docker](#using-docker)
5. [Contribution - Developer Information](#contribution---developer-information)
  * [Branching](#branching) 
  * [Pending Developments](#pending-developments)
  * [License and Third Party Lib POM Plugins](#license-and-third-party-lib-pom-plugins)
6. [Contact](#contact)
7. [Credits and Contributing Organizations](#credits-and-contributing-organizations)


## WaCoDiS Project Information
<p align="center">
  <img src="https://raw.githubusercontent.com/WaCoDiS/apis-and-workflows/master/misc/logos/wacodis.png" width="200">
</p>
Climate changes and the ongoing intensification of agriculture effect in increased material inputs in watercourses and dams.
Thus, water industry associations, suppliers and municipalities face new challenges. To ensure an efficient and environmentally
friendly water supply for the future, adjustments on changing conditions are necessary. Hence, the research project WaCoDiS
aims to geo-locate and quantify material outputs from agricultural areas and to optimize models for sediment and material
inputs (nutrient contamination) into watercourses and dams. Therefore, approaches for combining heterogeneous data sources,
existing interoperable web based information systems and innovative domain oriented models will be explored.

### Architecture Overview

For a detailed overview about the WaCoDiS system architecture please visit the 
**[WaCoDiS Core Engine](https://github.com/WaCoDiS/core-engine)** repository.  

## Overview  
The **WaCoDiS Job Status Listener** subscribes to messages about finished processing jobs (published by the WaCoDiS Core Engine via the system's message broker). The Product Listener request the generated results (EO-Products) from the processing environment and ingests the received data into a configured data backend. Finally the Product Listener compiles metadata for the ingested data ([WacodisProductDataEnvelope](https://github.com/WaCoDiS/apis-and-workflows/blob/master/openapi/src/main/definitions/wacodis-schemas.yml)) and publishes this metadata via the systems message broker. 
### Modules
The WaCoDiS Product Listener is a stand-alone Spring Boot application comprisiung only a single module.
### Utilized Technologies
* Java  
WaCoDiS Product Listener uses (as most of the WaCoDiS components) the java programming language. WaCoDiS Product Listener is tested with Oracle JDK 8 and OpenJDK 8. Unless stated otherwise later Java versions can be used as well.
* Maven  
The project WaCoDiS Product Listener uses the build-management tool Apache [maven](https://maven.apache.org/)
* Spring Boot  
WaCoDiS Product Listener is a standalone application built with the [Spring Boot](https://spring.io/projects/spring-boot) framework.
* Spring Cloud  
[Spring Cloud](https://spring.io/projects/spring-cloud) is used for exploiting some ready-to-use features in order to implement
an event-driven workflow. In particular, [Spring Cloud Stream](https://spring.io/projects/spring-cloud-stream) is used
for subscribing to asynchronous messages within thw WaCoDiS system.
* RabbitMQ  
For communication with other WaCoDiS components of the WaCoDiS system the message broker [RabbitMQ](https://www.rabbitmq.com/) is utilized. RabbitMQ is not part of WaCoDiS Product Listener and therefore [must be deployed separately](#preconditions) if WaCoDIS Product Listener is deployed as part of the whole WaCoDiS system. 
* OpenAPI  
OpenAPI is used for the specification of core WaCoDiS data model and APIs.
* OGC Web Processing Service  
The [OGC Web Processing Service](https://www.ogc.org/standards/wps) (WPS) is a standardized web interface for processing (spatial) data utilizing abitrary processing algorithms. The processing environment of the whole WaCoDiS system implements the WPS standard.


## Installation / Building Information
### Build from Source
In order to build the WaCoDiS Product Listener from source _Java Development Kit_ (JDK) must be available. Product Listener
is tested with Oracle JDK 8 and OpenJDK 8. Unless stated otherwise later JDK versions can be used.  

Since this is a Maven project, [Apache Maven](https://maven.apache.org/) must be available for building it. Then, you
can build the project by running `mvn clean install` from root directory

### Build using Docker
The project contains a Dockerfile for building a Docker image. Simply run `docker build -t wacodis/product-listener:latest .`
in order to build the image. You will find some detailed information about running the Product Listener as Docker container
within the [run section](#using-docker) .

### Configuration
Configuration is fetched from [WaCoDiS Config Server](https://github.com/WaCoDiS/config-server). If config server is not
available, configuration values located at *src/main/resources/application.yml*.   
#### Parameters
The following section contains descriptions for configuration parameters structured by configuration section.

##### spring/cloud/stream/bindings/wacodis.test.new-process-result
parameters related to message on successfully finished proessing jobs

| value     | description       | note  |
| ------------- |-------------| -----|
| destination     | topic used to receive message about successfully executed WaCoDiS jobs | e.g. *wacodis.test.tools.finished* |
| binder      | defines the binder (message broker)   |  |
| content-type      | content type of  DataEnvelope acknowledgement messages (mime type)   | should always be *application/json*  |

##### spring/cloud/stream/bindings/wacodis.new-product
parameters related to messages on failed processing jobs

| value     | description       | note  |
| ------------- |-------------| -----|
| destination     | topic used to publish metadata (WacodisProductDataEnvelooe) for ingested data sets| e.g. *wacodis.test.data.available* |
| binder      | defines the binder (message broker)   |  |
| content-type      | content type of  DataEnvelope acknowledgement messages (mime type)   | should always be *application/json*  |

##### spring/rabbitmq
parameters related to WaCoDis message broker

| value     | description       | note  |
| ------------- |-------------| -----|
| host | RabbitMQ host (WaCoDiS message broker) | e.g. *localhost* |
| port | RabbitMQ port (WaCoDiS message broker)   | e.g. *5672*|
| username | RabbitMQ username (WaCoDiS message broker)   | |
| password | RabbitMQ password (WaCoDiS message broker)   | |

##### spring/product-listener
parameters to configure base settings and connections

| value     | description       | note  |
| ------------- |-------------| -----|
| wps-base-url| service url of the processing environment (OGC WPS) | e.g. *http://localhost:8080/wacodis-wps/service* |
| file-storage-directory | directory to store received result data sets before ingesting data into the configured backend   | e.g. */media/user/daten/wacodis_data/*|
| dataAccessGetDataEnvelopeEndpoint | url to dataenvelopes endpoint of [Wacodis Data Access API](https://github.com/WaCoDiS/data-access-api)   | e.g. *localhost:8081/dataAccess/dataenvelopes*, access to Data Access API is necessary for compiling product metadata |
| useOutputIDinFileName | should be true to ensure unique filenames when process has multiple outputs (except METADATA)   | boolean |

##### spring/product-listener/geoserver
parameters to configure geoserver backend

| value     | description       | note  |
| ------------- |-------------| -----|
| enable| true if ingestion of result data into geoserver should be enabled | boolean |
| url | url of geoserver instance  | e.g. *http://localhost:8080/geoserver*|
| service type | OGC Web Service   | e.g. *WMS* for Web Mapping Service|
| username | username for geoserver instance   ||
| password | password for geoserver user  ||

##### spring/product-listener/arcgis-image-server
parameters to configure ARCGis Image Server backend

| value     | description       | note  |
| ------------- |-------------| -----|
| enable| true if ingestion of result data into Image Server should be enabled | boolean |
| python-script-location | path to python script that handles data ingestion into Image Server  | e.g. */home/dummy-ingestion.py* [see](https://github.com/WaCoDiS/Tools/tree/master/imageServicePublisher)|
| service type | service type   | e.g. *ImageServer*|
| url | url of the Image Server instance  | e.g. *arcgis.server.url/arcgis/admin* |

##### spring/product-listener/product-collection-mapping
Extendable list of collection mappings: Each mapping maps the productCollection parameter in [WacodisJobDefinition](https://github.com/WaCoDiS/apis-and-workflows/blob/master/openapi/src/main/definitions/wacodis-schemas.yml) to a collection within the the service backend. This way different product type can be ingested into differenct collections within the service backend.  
  
**Example:**  
```
  product-collection-mapping:
    - product-collection: sentinel-2_level-1c # matches the productCollection parameter from WacodisJobDefinition
      product-type: EO:WACODIS:DAT:S2A_RAW # matches the resulting collection within the service backend
    - product-collection: forest-vitality-change
      product-type: EO:WACODIS:DAT:FOREST_VITALITY_CHANGE # beware of weird ArcGIS special character handling ('-' no, '_' yes)
```

### Deployment
This section describes deployment scenarios, options and preconditions.
#### Preconditions
* (without using Docker) In order to run Product Listener Java Runtime Environment (JRE) (version >= 8) must be available. In order to [build Product Listener from source](#installation--building-information) Java Development Kit (JDK) version >= 8) must be abailable. Product Listener is tested with Oracle JDK 8 and OpenJDK 8.
* In order to receive and publish messages a running instance a running instance of [RabbitMQ message broker](https://www.rabbitmq.com/) must be available.  
* When running Product Listener as part of the WaCoDiS system, messages are published by [WaCoDiS Core Engine](https://github.com/WaCoDiS/core-engine). For testing purposes other tools can be used to publish/mock messages about processing progress of a WaCoDiS Job.
* In order to compile product metadata for result data (WacodisProductDataEnvelope) Product Listener needs access to the REST interface of  [WaCoDiS Data Access](https://github.com/WaCoDiS/data-access-api). 

The WaCoDiS Product Listener must be deployed in an envriomnent that allows access to the configured data backend and allows receiving and publishing messages via the message broket (RabbitMQ).
  
The server addresses are [configurable](#configuration).  
  
 * If [configuration](#configuration) should be fetched from Configuration Server a running instance of [WaCoDiS Config Server](https://github.com/WaCoDiS/config-server) must be available.

## User Guide
### Run Product Listener
Currently there are no pre-compiled binaries available for WaCoDiS Product Listener. Product Listener must be [built from source](#installation--building-information). Alternatively Docker can be used to (build and) run WaCoDiS Product Listener.

Product Listener is a Spring Boot application. Execute the compiled jar (`java -jar  product-listener.jar`) or run *de.wacodis.productlistener.ProductListenerApplication.java* in IDE to start the Product Listener.

#### Using Docker
1. Build Docker Image from [Dockerfile](https://github.com/WaCoDiS/job-status-listener/blob/master/Dockerfile) that resides in the project's root folder.
2. Run created Docker Image.

Alternatively, latest available docker image (automatically built from master branch) can be pulled from [Docker Hub](https://hub.docker.com/r/wacodis/product-listener). See [WaCoDiS Docker repository](https://github.com/WaCoDiS/wacodis-docker) for pre-configured Docker Compose files to run WaCoDiS system components and backend services (RabbitMQ and Elasticsearch).

## Contribution - Developer Information
This section contains information for developers.

### Branching
The master branch provides sources for stable builds. The develop branch represents the latest (maybe unstable) state of development.

### Pending Developments
Currently product listener is capable of handling (OGC) Wps jobs with multiple outputs (different output identifier) but cannot handle job outputs with cardinality higher than 1.

### License and Third Party Lib POM Plugins
[optional]

## Contact
|    Name   |   Organization    |    Mail    |
| :-------------: |:-------------:| :-----:|
| Sebastian Drost | Bochum University of Applied Sciences | sebastian.drost@hs-bochum.de |
| Arne Vogt | Bochum University of Applied Sciences | arne.vogt@hs-bochum.de |
| Andreas Wytzisk  | Bochum University of Applied Sciences | andreas.wytzisk@hs-bochum.de |
| Matthes Rieke | 52° North GmbH | m.rieke@52north.org |

## Credits and Contributing Organizations
- Department of Geodesy, Bochum University of Applied Sciences, Bochum
- 52° North Initiative for Geospatial Open Source Software GmbH, Münster
- Wupperverband, Wuppertal
- EFTAS Fernerkundung Technologietransfer GmbH, Münster

The research project WaCoDiS is funded by the BMVI as part of the [mFund programme](https://www.bmvi.de/DE/Themen/Digitales/mFund/Ueberblick/ueberblick.html)  
<p align="center">
  <img src="https://raw.githubusercontent.com/WaCoDiS/apis-and-workflows/master/misc/logos/mfund.jpg" height="100">
  <img src="https://raw.githubusercontent.com/WaCoDiS/apis-and-workflows/master/misc/logos/bmvi.jpg" height="100">
</p>

