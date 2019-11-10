![IKASAN](../developer/docs/quickstart-images/Ikasan-title-transparent.png)

# Ikasan Visualisation


### Pre

- Create  directory dashboard
```$xslt
mkdir dashboard
cd dashboard
mkdir solr
cd solr
```
- Get Ikasan Solar running 
```
curl -fSL https://oss.sonatype.org/content/repositories/snapshots/org/ikasan/ikasan-solr-distribution/2.2.0-SNAPSHOT/ikasan-solr-distribution-2.2.0-20191109.231104-94.zip -o ikasan-solr-distribution.zip
unzip ikasan-solr-distribution.zip
cd bin
./solr start
```
- Get dashboard
```
curl -fSL   https://oss.sonatype.org/content/repositories/snapshots/org/ikasan/visualisation-dashboard/2.2.0-SNAPSHOT/visualisation-dashboard-2.2.0-20191109.234523-21.jar -o visualisation-dashboard-2.2.0-SNAPSHOT.jar
```
- Get h2 lib 
```
curl -fSL  https://repo1.maven.org/maven2/com/h2database/h2/1.4.199/h2-1.4.199.jar -o h2-1.4.199.jar
```
- Create local property files as a copy of  [application.properties](src/main/resources/application.properties) in config directory
```
## Make sure following properties exist

# Logging levels across packages (optional)
logging.level.root=WARN
logging.level.org.ikasan=INFO
logging.file=logs/application.log

module.name=visualisation-dashboard
server.port=9090    
# This is a workaround for https://github.com/vaadin/spring/issues/381
h2.db.port=9091
spring.servlet.multipart.enabled = false

solr.url=http://localhost:8983/solr
solr.username=ikasan
solr.password=1ka5an

error.notification.duration=5000

# Ikasan persistence store
datasource.username=sa
datasource.password=sa
datasource.driver-class-name=org.h2.Driver
datasource.xadriver-class-name=org.h2.jdbcx.JdbcDataSource
#datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1
datasource.url=jdbc:h2:tcp://localhost:${h2.db.port}/./${module.name}-db/esb;IFEXISTS=FALSE

datasource.dialect=org.hibernate.dialect.H2Dialect
datasource.show-sql=false
datasource.hbm2ddl.auto=none
datasource.validationQuery=select 1
datasource.min.pool.size=5
datasource.max.pool.size=20

spring.liquibase.change-log=classpath:db-changelog.xml
spring.liquibase.enabled=true

spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration,org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration,org.springframework.boot.autoconfigure.quartz.QuartzAutoConfiguration,org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration,org.ikasan.harvesting.HarvestingAutoConfiguration,org.ikasan.housekeeping.HousekeepingAutoConfiguration,org.ikasan.module.IkasanModuleAutoConfiguration

jwt.secret=javainuse

vaadin.compatibilityMode=true
vaadin.original.frontend.resources=true

vaadin.i18n.provider=org.ikasan.dashboard.internationalisation.IkasanI18NProvider

render.search.images=true

rest.module.username=admin
rest.module.password=admin


```
- Set JAVA_HOME to JDK8
- Add runscript 
```$xslt

#!/bin/bash
#set -u

SCRIPT_DIR=$(pwd)


# Ikasan Module settings

MODULE_NAME=`cat config/application.properties|grep "module.name"|head -1|cut -d'=' -f2`
MODULE_JVM_OPTS="-server -Xms256m -Xmx256m -XX:MaxMetaspaceSize=128m -Dorg.apache.activemq.SERIALIZABLE_PACKAGES=*"
MODULE_OTHER_OPTS=""
MODULE_JAVA_OPTS="$MODULE_JVM_OPTS  $MODULE_OTHER_OPTS"

APPLICATION_JAR=$MODULE_NAME-2.2.0-SNAPSHOT.jar

# H2 Persistence settings
H2_VERSION=1.4.199
H2_MODULE_NAME=h2-$MODULE_NAME
H2_JVM_OPTS="-server -Xms256m -Xmx256m -XX:MaxMetaspaceSize=128m"
H2_PORT=`cat config/application.properties|grep "h2.db.port"|head -1|cut -d'=' -f2`
# check the port was parsed
[ ${#H2_PORT} -lt 1 ] && echo "Cannot locate h2.db.port in config/application.properties" && exit 1

JAVA=$JAVA_HOME/bin/java

cd $SCRIPT_DIR
mkdir -p logs

#echo "Print MODULE_JAVA_OPTS $MODULE_JAVA_OPTS"
#echo "Print JAVA $JAVA"
#echo "RUN $JAVA $MODULE_JAVA_OPTS -jar $APPLICATION_JAR"


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
      nohup $JAVA $MODULE_JAVA_OPTS -Dmodule.name=$MODULE_NAME -jar $APPLICATION_JAR >/dev/null 2>&1 &
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
      nohup $JAVA -cp h2-$H2_VERSION.jar $H2_JVM_OPTS -Dmodule.name=$H2_MODULE_NAME org.h2.tools.Server -ifNotExists -tcp -tcpAllowOthers -tcpPort $H2_PORT &
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
      echo "Module $MODULE_NAME not running"
    fi
}

function check_h2
{
    h2pid=`ps aux|grep module.name=$H2_MODULE_NAME|grep -v grep| awk '{print $2}'`
    if [[ ${#h2pid} -gt 0 ]];then
      echo "H2 $H2_MODULE_NAME running on PID $h2pid"
    else
      echo "H2 $H2_MODULE_NAME not running"
    fi
}

function stop_module
{
    check_module
    if [[ ${#modulepid} -gt 0 ]];then
      echo "Stopping Module $MODULE_NAME on PID $modulepid"
      kill $modulepid
    fi
}

function stop_h2
{
    check_h2
    if [[ ${#h2pid} -gt 0 ]];then
      echo "Stopping H2 $H2_MODULE_NAME on PID $h2pid"
      kill $h2pid
    fi
}

ACTION=$1
case "$ACTION" in
    start) # starts both H2 and Module
        start_h2
        start_module
        ;;
    start-h2) # starts H2 only
        start_h2
        ;;
    stop) # stops both Module and H2
        stop_module
        while [[ ${#modulepid} -gt 0 ]];do
          echo "Waiting for module to shut down before stopping H2"
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

```
- start dashboard 
```
./run.sh start

tail -f log/application.log
```


### Update IM Client in order

ikasan.dashboard.extract.enabled=true
ikasan.dashboard.extract.base.url=http://localhost:9090
ikasan.dashboard.extract.username=admin
ikasan.dashboard.extract.password=admin