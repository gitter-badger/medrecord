#!/bin/sh

# this is going to be replaced with a gradle script in the near future, 
# then you can do something like 'gradle deployDev'

SERVER=medrecord.dev.medvision360.org
CONF_DIR=/etc/medvision360/medrecord

set -e
echo \(cd medrecord-server-deb && gradle buildDebianPackage\)

echo ssh root@${SERVER} /usr/local/bin/ddeploy \< deb/build/mv-medrecord-2.0.0.deb 
echo scp configuration-server.properties root@${SERVER}:${CONF_DIR}/configuration.properties
echo scp logback-server.xml root@$SERVER:${CONF_DIR}/logback.xml
