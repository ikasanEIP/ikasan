#!/bin/bash
#set -u

SCRIPT_DIR=$(pwd)


# Ikasan Module settings

MODULE_NAME=`cat config/application.properties|grep "module.name"|head -1|cut -d'=' -f2`
OBJECT_STORE_PROP=com.arjuna.ats.arjuna.objectstore.objectStoreDir
MODULE_OBJECT_STORE=`cat config/application.properties|grep ${OBJECT_STORE_PROP}|head -1|cut -d'=' -f2`
MODULE_JVM_OPTS="-server -Xms256m -Xmx256m -XX:MaxMetaspaceSize=128m -D${OBJECT_STORE_PROP}=${MODULE_OBJECT_STORE} -Dorg.apache.activemq.SERIALIZABLE_PACKAGES=*"
MODULE_OTHER_OPTS=""
MODULE_JAVA_OPTS="$MODULE_JVM_OPTS  $MODULE_OTHER_OPTS"

APPLICATION_JAR=$MODULE_NAME-${project.version}.jar

# H2 Persistence settings
H2_VERSION=${version.com.h2database}
H2_MODULE_NAME=h2-$MODULE_NAME
H2_JVM_OPTS="-server -Xms256m -Xmx256m -XX:MaxMetaspaceSize=128m"
H2_PORT=`cat config/application.properties|grep "h2.db.port"|head -1|cut -d'=' -f2`
# check the port was parsed
[ ${#H2_PORT} -lt 1 ] && echo "Cannot locate h2.db.port in config/application.properties" && exit 1

JAVA=$JAVA_HOME/bin/java

cd $SCRIPT_DIR
mkdir -p logs

# Prints command usage.
function usage
{
    /bin/cat <<-_BASIC_INFO_
    Usage: run.sh <action>
        <action>  Specify action name,
              'start|start-h2|stop|ps'.
_BASIC_INFO_
}

# start the Ikasan module
function start_module
{
    check_module
    if [[ ${#modulepid} -lt 1 ]];then
      echo "Starting Module"
      nohup $JAVA $MODULE_JAVA_OPTS -Dmodule.name=$MODULE_NAME -jar ${SCRIPT_DIR}/lib/$APPLICATION_JAR > ${SCRIPT_DIR}/logs/application.log 2>&1 &
    else
      echo "Module already running on PID $modulepid, will not start"
    fi
}

# start the standalone H2 DB
function start_h2
{
    check_h2
    if [[ ${#h2pid} -lt 1 ]];then
      echo "Starting H2"
      nohup $JAVA -cp ${SCRIPT_DIR}/lib/h2-$H2_VERSION.jar $H2_JVM_OPTS -Dmodule.name=$H2_MODULE_NAME org.h2.tools.Server -ifNotExists -tcp -tcpAllowOthers -tcpPort $H2_PORT > ${SCRIPT_DIR}/logs/h2-server.log 2>&1 &
    else
      echo "H2 already running on PID $h2pid, will not start"
    fi
}

function check_module
{
    modulepid=`ps aux|grep module.name=$MODULE_NAME|grep -v grep| awk '{print $2}'`
    if [[ ${#modulepid} -gt 0 ]];then
      echo "$MODULE_NAME running on PID $modulepid"
    else
      echo "$MODULE_NAME not running"
    fi
}

function check_h2
{
    h2pid=`ps aux|grep module.name=$H2_MODULE_NAME|grep -v grep| awk '{print $2}'`
    if [[ ${#h2pid} -gt 0 ]];then
      echo "$H2_MODULE_NAME running on PID $h2pid"
    else
      echo "$H2_MODULE_NAME not running"
    fi
}

function stop_module
{
    check_module
    if [[ ${#modulepid} -gt 0 ]];then
      echo "Stopping $MODULE_NAME on PID $modulepid"
      kill $modulepid
    fi
}

function stop_h2
{
    check_h2
    if [[ ${#h2pid} -gt 0 ]];then
      echo "Stopping $H2_MODULE_NAME on PID $h2pid"
      kill $h2pid
    fi
}

ACTION=$1
case "$ACTION" in
    start) # starts both H2 and Module
        start_h2
        check_h2
        start_module
        ;;
    start-h2) # starts H2 only
        start_h2
        ;;
    stop) # stops both Module and H2
        stop_module
        while [[ ${#modulepid} -gt 0 ]];do
          echo "Waiting for $MODULE_NAME to shut down before stopping $H2_MODULE_NAME"
          sleep 5
          check_module
        done
        stop_h2
        ;;
    ps)
        check_module
        check_h2
        ;;
    *)
        usage
        exit 1
        ;;
esac