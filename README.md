# INDIGO-DataCloud CDMI HPSS plugin
Java Service Provider implementation for CDMI HPSS storage back-end

## Maven
### Latest Release (Stable)
TBA

## Requirements

* JDK 1.8+
* [Maven 3+](https://maven.apache.org/)

## Build & Usage
The project uses the Maven build automation tool.

You can build the project with
```
mvn clean package
```

To use the cdmi-hpss SPI implementation you need to included this module at runtime of the CDMI server, e.g.

```
java -classpath .:cdmi-hpss.jar -jar cdmi-server.jar
```
or
```
java -Djava.ext.dirs=cdmi-hpss/target -jar cdmi-server.jar
```
