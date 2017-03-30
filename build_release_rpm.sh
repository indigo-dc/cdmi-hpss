#!/bin/bash

VERSION=0.1
NAME=cdmi-hpss
TOPDIR=`pwd`/rpm

mvn clean package
mvn -DdescriptorId=jar-with-dependencies assembly:single

cp target/$NAME-$VERSION-jar-with-dependencies.jar $TOPDIR/SOURCES

rpmbuild --define "_topdir ${TOPDIR}" -ba $TOPDIR/SPECS/$NAME.spec

cp ${TOPDIR}/RPMS/x86_64/*.rpm .
