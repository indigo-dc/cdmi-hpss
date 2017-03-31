#!/bin/bash

VERSION=1.2
NAME=cdmi-hpss
TOPDIR=`pwd`/rpm

mvn clean package
mvn -DdescriptorId=jar-with-dependencies assembly:single

mkdir $TOPDIR/SOURCES
cp target/$NAME-$VERSION-jar-with-dependencies.jar $TOPDIR/SOURCES
cp config/capabilities.json $TOPDIR/SOURCES
cp config/configuration.json $TOPDIR/SOURCES

rpmbuild --define "_topdir ${TOPDIR}" -ba $TOPDIR/SPECS/$NAME.spec

cp ${TOPDIR}/RPMS/x86_64/*.rpm .
