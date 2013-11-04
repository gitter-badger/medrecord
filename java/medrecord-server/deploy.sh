#!/bin/sh

# this is going to be replaced with a gradle script in the near future, 
# then you can do something like 'gradle deployDev'

set -e
(cd medrecord-server-deb && gradle buildDebianPackage)

#ssh root@profileserver2.dev.medvision360.org /usr/local/bin/ddeploy < deb/build/mv-profileserver2-2.0.0.deb 
