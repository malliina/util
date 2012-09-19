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
# Startup script for wicket under *nix systems (it works under NT/cygwin too).
# Adapted from artifactory's start/stop script
APP_NAME=wicket
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
if [ -f /etc/default/${APP_NAME} ] ; then
  . /etc/default/${APP_NAME}
fi

case "$1" in
    start)
        if [ -f ${PID_FILE} ]; then
            if [ "$(ps -p `cat ${PID_FILE}` | wc -l)" -gt 1 ]; then
                echo "Already running"
                exit 1
            else
                # not running, but PID file exists
                echo "The app was not stopped correctly. Removing old pid file."
                rm ${PID_FILE}
            fi
        fi
        COMMAND="exec ${JAVA_CMD} -cp ${APP_HOME}/lib/*:${APP_HOME}/${APP_NAME}.jar ${MAIN_CLASS} >> ${APP_HOME}/logs/console.out 2>&1"
        if [ -z "${APP_USER}" ]; then
            nohup sh -c ${COMMAND} >/dev/null &
        else
            nohup su - ${APP_USER} --shell=/bin/sh -c ${COMMAND} >/dev/null &
        fi
        echo $! > ${PID_FILE}
        echo "Started"
        ;;

    stop)
        PID=`cat ${PID_FILE} 2>/dev/null`
        kill $PID 2>/dev/null
        sleep 1
        kill -9 $PID 2>/dev/null
        rm -f ${PID_FILE}
        echo "Stopped"
        ;;

    restart)
        $0 stop $*
        sleep 5
        $0 start $*
        ;;
    *)
        usage
        ;;
esac
exit 0



