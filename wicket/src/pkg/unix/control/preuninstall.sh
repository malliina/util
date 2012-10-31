#!/bin/sh
echo "Executing preuninstall..."
APP_NAME=wicket
if [ -f /etc/default/${APP_NAME} ] ; then
  . /etc/default/${APP_NAME}
fi
echo "Stopping ${APP_NAME}..."
service ${APP_NAME} stop
userdel ${APP_USER}