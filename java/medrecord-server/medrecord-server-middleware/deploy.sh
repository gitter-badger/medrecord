#!/bin/bash bash

set -e
set -x

# create a .war and copy it to a server, for example:
#     ENVIRONMENT=dev SERVER=svc01.local ./deploy.sh

export GRADLE_OPTS="-Xmx1024m -XX:MaxPermSize=512m"
export ENVIRONMENT=${ENVIRONMENT:-dev}
export SERVER=${SERVER:-middleware.${ENVIRONMENT}.medvision360.org}

if [[ "${ENVIRONMENT}" == "test" ]]; then
  export LOG_LEVEL=${LOG_LEVEL:-INFO}
  export JUL_LOG_LEVEL=${JUL_LOG_LEVEL:-FINE}
elif [[ "${ENVIRONMENT}" == "dev" ]]; then
  export LOG_LEVEL=${LOG_LEVEL:-DEBUG}
  export JUL_LOG_LEVEL=${JUL_LOG_LEVEL:-FINER}
else
  export LOG_LEVEL=${LOG_LEVEL:-ERROR}
  export JUL_LOG_LEVEL=${JUL_LOG_LEVEL:-WARNING}
fi

export REMOTE_USER=${REMOTE_USER:-root}
export TOMCAT_WEBAPP_DIR=${TOMCAT_WEBAPP_DIR:-/var/lib/tomcat7/webapps}
export TOMCAT_ENDORSED_DIR=${TOMCAT_ENDORSED_DIR:-/usr/share/tomcat7/endorsed}

export WEB_NAME=${WEB_NAME:-middleware}
export LOG_APPENDER=${LOG_APPENDER:-SYSLOG}

GRADLE_ARGS="${GRADLE_ARGS} -Dweb.name=${WEB_NAME}"
GRADLE_ARGS="${GRADLE_ARGS} -Dlog.level=${LOG_LEVEL} -Dlog.appender=${LOG_APPENDER}"
GRADLE_ARGS="${GRADLE_ARGS} -Djul.log.level=${JUL_LOG_LEVEL}"

START_DIRECTORY=`pwd`
WEBAPP_LOCATION=${START_DIRECTORY}/build/lib/${WEB_NAME}.war

gradle clean install ${GRADLE_ARGS}

if [[ ! -f ${WEBAPP_LOCATION} ]]; then
  echo "No webapp file ${WEBAPP_LOCATION}"
  echo "Is something wrong with the build?"
  exit 1
fi

ssh ${REMOTE_USER}@${SERVER} /etc/init.d/tomcat7 stop

WEBAPP_FILENAME=${WEBAPP_LOCATION##*/}
WEBAPP_DIR_NAME=${WEBAPP_FILENAME%.*}

ssh ${REMOTE_USER}@${SERVER} rm -rf ${TOMCAT_WEBAPP_DIR}/{${WEBAPP_FILENAME},${WEBAPP_DIR_NAME}}
scp ${WEBAPP_LOCATION} ${REMOTE_USER}@${SERVER}:${TOMCAT_WEBAPP_DIR}/

ssh ${REMOTE_USER}@${SERVER} /etc/init.d/tomcat7 start
