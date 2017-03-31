#!/bin/bash

VERSION=1.2
NAME=cdmi-hpss

mvn clean package
mvn -DdescriptorId=jar-with-dependencies assembly:single

mkdir -p debian/var/lib/$NAME
mkdir -p debian/usr/lib/cdmi-server/plugins
mkdir -p debian/etc/cdmi-server/plugins
cp target/$NAME-$VERSION-jar-with-dependencies.jar debian/var/lib/$NAME/
cp target/$NAME-$VERSION-jar-with-dependencies.jar debian/usr/lib/cdmi-server/plugins/
cp config/capabilities.json debian/etc/cdmi-server/plugins
cp config/configuration.json debian/etc/cdmi-server/plugins

dpkg --build debian

mv debian.deb $NAME-${VERSION}_all.deb
