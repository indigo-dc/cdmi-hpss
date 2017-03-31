# INDIGO-DataCloud CDMI HPSS plugin
Java Service Provider implementation for CDMI HPSS storage back-end

## Maven
### Latest Release (Stable)
1.2

## Requirements

* JDK 1.8+
* [Maven 3+](https://maven.apache.org/)

## Build & Usage
The project uses the Maven build automation tool.

You can build the project with
```
mvn clean package
mvn -DdescriptorId=jar-with-dependencies assembly:single
```

To use the cdmi-hpss SPI implementation you need to included this module at runtime of the CDMI server, e.g.

```
JAVA_OPTS="-Djava.ext.dirs=target -Dcdmi.hpss.config=config" ./cdmi-server-1.2.jar
```
