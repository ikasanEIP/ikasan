![IKASAN](../developer/docs/quickstart-images/Ikasan-title-transparent.png)
# Ikasan Solr
Solr Reference Guide

Solr Online Documentation: [Solr Reference Guide](https://lucene.apache.org/solr/guide/)

### Building the Solr Installation
The solr-distribution module is responsible for creating an Ikasan ready version of 
a solr distribution. In order to build the distribution:

````text 
cd solr-distribution
mvn clean install
````

A zip file will be created under the /target directory in the form ikasan-solr-<solr.version>.zip and
a zip file will also be pushed to your local maven repository with pom reference:

````text 
<groupId>org.ikasan</groupId>
<artifactId>ikasan-solr-distribution</artifactId>
<version>${version.ikasan}</version>
````

### Installing Solr

````text 
cd to <solr-installation-directory> location and copy <ikasan-solr-distribution>.zip to this location
unzip -d solr <kasan-solr-distribution>.zip
````

### Controlling Solr

````text 
cd <solr-installation-directory>/solr/bin
````

````text 
# Start solr
bin/solr start
````

````text 
# Stop solr
bin/solr stop
````
 
````text 
# Check solr status
bin/solr status
````

For more information on please see the solr control script documentation.

### Solr Logging

Solr logs to the following location.

````text
#Solr logging directory
<solr-installation-directory>/solr/server/logs/
````

### Solr Index Location

The solr index can be found at the following location.

````text
#Solr index location
<solr-installation-directory>/solr/server/solr/ikasan/data
````

### Configuring Solr to point at the index location
It is possible to configure solr to point at another location to store the data index. 

````text
vi <solr-installation-directory>/solr/server/solr/ikasan/conf/solrconfig.xml
# Update the Solr data directory to point at the relevant mount by modifying the relevant line in the solrconfig.xml file to 
<dataDir>/opt/<env>index/solr/${solr.core.name}</dataDir> 
````

### Solr Security

Solr is constrained by HTTP basic authentication. In order to initialise basic authentication a file called <solr-installation-directory>/solr/server/solr/security.json has been included in the Ikasan distribution.
This file contains the authentication details along with the security roles. 

The default **ikasan:1ka5an** credentials are created as part of the Ikasan distribution.

All administration of Solr security is performed via the exposed http services as seen below. See the Solr security reference for further information.

````text
# Sample command to add a new user to Solr
curl --user ikasan:1ka5an http://localhost:8983/solr/admin/authentication -H 'Content-type:application/json' -d '{"set-user": {"<new-user>":"<new-password>"}}'
 
# Give the new user the admin role
curl -u ikasan:1ka5an -H 'Content-type:application/json' -d '{
   "set-user-role" : {"<new-username>": ["admin"]}
}' http://localhost:8983/solr/admin/authorization

# You may wish to delete the default ikasan solr user once you have created your own admin user.
curl --user ikasan:1ka5an http://localhost:8983/solr/admin/authentication -H 'Content-type:application/json' -d  '{"delete-user": ["ikasan"]}'
````
