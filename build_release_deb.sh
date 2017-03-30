#!/bin/bash

VERSION=0.1
NAME=cdmi-hpss

mvn clean package
mvn -DdescriptorId=jar-with-dependencies assembly:single

mkdir -p debian/var/lib/$NAME
cp target/$NAME-$VERSION-jar-with-dependencies.jar debian/var/lib/$NAME/

dpkg --build debian

mv debian.deb $NAME-${VERSION}_all.deb
