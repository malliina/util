#!/bin/sh
echo "Executing preuninstall..."
APP_NAME=wicket
if [ -f /etc/default/${APP_NAME} ] ; then
  . /etc/default/${APP_NAME}
fi
service ${APP_NAME} stop
userdel ${APP_USER}