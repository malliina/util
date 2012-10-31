#!/bin/sh
#
# chkconfig: 345 80 20
# description: App
# processname: wicket
# pidfile: /var/run/wicket.pid
#
### BEGIN INIT INFO
# Provides:          wicket
# Required-Start:    $remote_fs $syslog $network
# Required-Stop:     $remote_fs $syslog $network
# Default-Start:     2 3 4 5
# Default-Stop:      0 1 6
# Short-Description: Start wicket at boot time
# Description:       Manages the services needed to run wicket
### END INIT INFO
#
# Startup script for wicket under *nix systems (it works under NT/cygwin too).
# Adapted from artifactory's start/stop script
# Should be LSB compliant and therefore usable with pacemaker for HA configurations



usage() {
    echo "Usage: $0 {start|stop|restart|status}"
    exit 1
}
pid_exists(){
    if [ -f ${PID_FILE} ]; then
       return 0
    else
       return 1
    fi
}
is_running(){
    if pid_exists; then
        if [ "$(ps -p `cat ${PID_FILE}` | wc -l)" -gt 1 ]; then
            return 0
        else
            # not running, but PID file exists
            echo "The app was not stopped correctly. Removing old pid file."
            rm ${PID_FILE}
            return 1
        fi
    else
        return 1
    fi
}
# needed for HA configuration so we can use this as an LSB resource agent
meta_data() {
    cat <<END
    <?xml version="1.0"?>
    <!DOCTYPE resource-agent SYSTEM "ra-api-1.dtd">
    <resource-agent name="wicket" version="0.9">
        <version>1.0</version>

        <longdesc lang="en">
            This is a Dummy Resource Agent. It does absolutely nothing except
            keep track of whether its running or not.
        </longdesc>
        <shortdesc lang="en">Example stateless resource agent</shortdesc>

        <parameters>
            <parameter name="state" unique="1">
                <longdesc lang="en">
                Location to store the resource state in.
                </longdesc>
                <shortdesc lang="en">State file</shortdesc>
                <content type="string" default="${HA_RSCTMP}/wicket-${OCF_RESOURCE_INSTANCE}.state" />
            </parameter>
            <parameter name="fake" unique="0">
                <longdesc lang="en">Fake attribute that can be changed to cause a reload</longdesc>
                <shortdesc lang="en">Fake attribute that can be changed to cause a reload</shortdesc>
                <content type="string" default="wicket" />
            </parameter>
        </parameters>

        <actions>
            <action name="start"        timeout="20" />
            <action name="stop"         timeout="20" />
            <action name="monitor"      timeout="20" interval="10" depth="0" />
            <action name="reload"       timeout="20" />
            <action name="migrate_to"   timeout="20" />
            <action name="migrate_from" timeout="20" />
            <action name="meta-data"    timeout="5" />
            <action name="validate-all"   timeout="20" />
        </actions>
    </resource-agent>
    END
}

# Script starts here
APP_NAME=wicket
if [ -f /etc/default/${APP_NAME} ] ; then
  . /etc/default/${APP_NAME}
fi

case "$1" in
    start)
        if is_running; then
            echo "Already running"
            # LSB says: return 0 when starting an already started service
            exit 0
        fi
        COMMAND="exec ${JAVA_CMD} ${JAVA_OPTS} -cp ${APP_HOME}/lib/*:${APP_HOME}/${APP_NAME}.jar ${MAIN_CLASS} >> ${APP_HOME}/logs/console.out 2>&1"
        if [ -z "${APP_USER}" ]; then
            nohup sh -c "${COMMAND}" >/dev/null 2>&1 &
        else
            nohup su - ${APP_USER} --shell=/bin/sh -c "${COMMAND}" >/dev/null 2>&1 &
        fi
        echo $! > ${PID_FILE}
        sleep 1
        if is_running; then
            echo "Started"
        else
            echo "Startup failed"
            exit 1
        fi
        ;;
    stop)
        if pid_exists; then
            # TODO: use rmi call to stop service
            PID=`cat ${PID_FILE} 2>/dev/null`
            kill $PID 2>/dev/null
            rm -f ${PID_FILE}
            # Wait for the service to die; remove if RMI in use
            sleep 2
            echo "Stopped"
        else
            echo "Unable to find PID file; already stopped?"
        fi
        ;;
    restart)
        $0 stop $*
        $0 start $*
        ;;
    status)
        if is_running; then
            echo "Running"
        else
            echo "Not running"
            # LSB says exit status 3 is "program is not running"
            # http://refspecs.linux-foundation.org/LSB_3.2.0/LSB-Core-generic/LSB-Core-generic/iniscrptact.html
            exit 3
        fi
        ;;
    *)
        usage
        ;;
esac
exit 0



