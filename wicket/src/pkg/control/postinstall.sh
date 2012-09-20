#!/bin/sh
echo "Executing postinstall..."
APP_NAME=wicket
if [ -f /etc/default/${APP_NAME} ] ; then
  . /etc/default/${APP_NAME}
fi
# Create user
user=`id -nu ${APP_USER}`
if [ "${user}" = "${APP_USER}" ]; then
    echo -n "User already exists..."
else
    echo -n "Creating user ${APP_USER}..."
    useradd -d /nonexistent -s /bin/false ${APP_USER}
    if [ ! $? ]; then
        echo "Unable to create user"
        exit 1
    fi
fi
# Permissions
mkdir -p ${APP_HOME}/logs
chown -R ${APP_USER}:${APP_USER} ${APP_HOME}
# Install as service
# Try update-rc.d for debian/ubuntu else use chkconfig
if [ -x /usr/sbin/update-rc.d ]; then
    echo -n "Adding as service with update-rc.d..."
    update-rc.d ${APP_NAME} defaults && serviceOK=true
else
    echo -n "Initializing service with chkconfig..."
    chkconfig --add ${APP_NAME} && chkconfig ${APP_NAME} on && chkconfig --list ${APP_NAME} &&  serviceOK=true
fi
if [ ! $serviceOK ]; then
    echo "Error adding service"
    exit 1
fi
echo "Done. You can now use 'service ${APP_NAME} start/stop/restart/status'."


