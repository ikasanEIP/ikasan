
# Dashboard
 
The Ikasan Dashboard provides a management web front end for searching and tracking events passing through Ikasan Integration Modules. Ikasan dashboard functionality covers:
- Event Searching and Tracking
- User Administration and Management
- Ikasan Support and Resources
- Security and access management
- Data mapping
- Error and event exclusion lifecycle
- Business stream control
- Monitoring
  

## Step-by-step guide how run ikasan-dashboard as standalone 

Download ikasan-dashboard from offical mvn repo [ikasan-dashboard-boot/2.0.0-SNAPSHOT](https://oss.sonatype.org/service/local/repositories/snapshots/content/org/ikasan/ikasan-dashboard-boot/2.0.0-SNAPSHOT/)

Start Ikasan-dashboard with h2 in memory database.
- Out of the box ikasan-dashboard is shipped with build in H2 driver which allows you to exlore the product.
- Run           
```
$JAVA_HOME/bin/java -jar ikasan-dashboard-boot-2.0.0-SNAPSHOT.jar
```           

Start ikasan-dashboard with different DB driver              
- download desired driver 
  - sybase [jconn4-7.0.jar](http://mvn.sonner.com.br/~maven/com/sybase/jdbc4/jdbc/jconn4/7.0/jconn4-7.0.jar)
  - sql [mssql-jdbc-6.2.1.jre8.jar](http://central.maven.org/maven2/com/microsoft/sqlserver/mssql-jdbc/6.2.1.jre8/mssql-jdbc-6.2.1.jre8.jar)
  - mysql [mysql-connector-java-5.1.44.jar](http://central.maven.org/maven2/mysql/mysql-connector-java/5.1.44/mysql-connector-java-5.1.44.jar)  
- create sub dir lib, and place new driver in lib        
- create sub dir config
- create new config/application.properties file based on [application.properties](boot/src/main/resources/application.properties)
   - Sybase 
```
datasource.username=ikasan01xxx
datasource.password=XXXXXXXXXXXXXXXXXXXXXXX
datasource.driver-class-name=com.sybase.jdbc4.jdbc.SybDataSource
datasource.xadriver-class-name=com.sybase.jdbc4.jdbc.SybXADataSource
datasource.dialect=org.ikasan.persistence.hibernate.IkasanSybaseASE157Dialect
datasource.url=jdbc:sybase:Tds:hostname:50100/Ikasan01
datasource.db.name=Ikasan01
datasource.port=50100
datasource.host=hostname
```         
   - mssql   
```
datasource.username=ikasan01xxx
datasource.password=XXXXXXXXXXXXXXXXXXXXXXX
datasource.dialect=org.hibernate.dialect.SQLServer2012Dialect
datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver
datasource.xa-driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerXADataSource 
datasource.url=jdbc:sybase:Tds:hostname:50100/Ikasan01
datasource.db.name=Ikasan01
datasource.port=50100
datasource.host=hostname
```                          
- Start dashboard with custome driver 
```
$JAVA_HOME/bin/java -Dloader.path=lib,config,ikasan-dashboard-boot-2.0.0-SNAPSHOT.jar -jar ikasan-dashboard-boot-2.0.0-SNAPSHOT.jar

```    

Navigate to Frontend [http://localhost:8080/ikasan-dashboard](http://localhost:8080/ikasan-dashboard)
- you can modify binding IP, port and application context in  [application.properties](boot/src/main/resources/application.properties)
```
server.port=8080
server.address=0.0.0.0
server.contextPath=/ikasan-dashboard
```      


