#!/bin/bash

set -e

SERVER=medrecord.dev.medvision360.org

gradle -a install

sleep 2
scp build/libs/medrecord-*.war root@${SERVER}:/tmp/medrecord.war
ssh root@${SERVER} rm -r /var/lib/tomcat7/webapps/{medrecord.war,medrecord}
ssh root@${SERVER} mv /tmp/medrecord.war /var/lib/tomcat7/webapps/medrecord.war

ssh -L 8000:localhost:8000 root@${SERVER} tail -f /var/log/medvision360/medrecord/errors.log
