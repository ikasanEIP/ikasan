[../](../../Readme.md)
![IKASAN](../developer/docs/quickstart-images/Ikasan-title-transparent.png)
# Ikasan Solr Integration
Solr Reference Guide

Solr Online Documentation: [Solr Reference Guide](https://lucene.apache.org/solr/guide/)

### Solr Installation Location

````text 
/opt/<env>/solr-8.2.0
````
 
````text 
# There is also a symbolic link created pointing at the above installation location.
/opt/runtime/solr
````

### Controlling Solr

````text 
cd /opt/runtime/solr/bin
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
/opt/runtime/solr/server/logs/
````

### Solr Index Location

The solr index can be found at the following location.

````text
#Solr index location
/opt/<env>index/solr/ikasan/
````

### Creating a new Ikasan core

````txt
./solr create_core -c ikasan
````
### Auto Schema Creation

For the Ikasan use case, auto schema creation has been disabled. This prevents documents of any shape from being added to the index.

````text
curl http://localhost:8983/solr/ikasan/config -d '{"set-user-property": {"update.autoCreateFields":"false"}}'
````

### Configuring Solr to point at the index location

````text
vi /opt/runtime/solr/server/solr/ikasan/conf/solrconfig.xml
# Update the Solr data directory to point at the relevant mount by modifying the relevant line in the solrconfig.xml file to 
<dataDir>/opt/<env>index/solr/${solr.core.name}</dataDir> 
````

### Solr Security

Solr is constrained by HTTP basic authentication. In order to initialise basic authentication a file called /opt/runtime/solr/server/solr/security.json needs to be created.
This file contains the authentication details along with the security roles. 

Add the following json to this file and stop and start the solr instance.
````json
{
  "authentication":{
    "blockUnknown":true,
    "class":"solr.BasicAuthPlugin",
    "credentials":{"solr":"IV0EHq1OnNrj6gvRCwvFwTrZ1+z1oBbnQdiVC3otuq0= Ndd7LKvVBAaZIF0QAVi1ekCfAJXr1GGfLtRUXhgrF8c="},
    "":{"v":0}},
  "authorization":{
    "class":"solr.RuleBasedAuthorizationPlugin",
    "permissions":[{
        "name":"security-edit",
        "role":"admin"}],
    "user-role":{"solr":"admin"}}
````
The default **solr:SolrRocks** credentials will be created.

All administration of Solr security is performed via the exposed http services as seen below. See the Solr security reference for further information.

````text
# Sample command to add a new user to Solr
curl --user solr:SolrRocks http://localhost:8983/solr/admin/authentication -H 'Content-type:application/json' -d '{"set-user": {"<new-user>":"<new-password"}}’
 
# Give the new user the admin role
curl -u solr:SolrRocks -H 'Content-type:application/json' -d '{
   "set-user-role" : {"<new-username>": ["admin"]}
}' http://localhost:8983/solr/admin/authorization

# Delete the default solr user
curl --user solr:SolrRocks http://localhost:8983/solr/admin/authentication -H 'Content-type:application/json' -d  '{"delete-user": ["solr"]}'
````

### Schema Definition

The following schema definition has been defined to support all Ikasan related data. Each curl command is required to 
be run against the Ikasan solr index that has been created.

````text
curl --user <new-user>:<password> -X POST -H 'Content-type:application/json' --data-binary '{
  "delete-field" : { "name":"payload" }
}' http://localhost:8983/solr/ikasan/schema
 
curl --user <new-user>:<password> -X POST -H 'Content-type:application/json' --data-binary '{
  "add-field":{
     "name":"payload",
     "type":"text_general",
     "stored":true,
         "multiValued":false}
}' http://localhost:8983/solr/ikasan/schema
````

````text
curl --user <new-user>:<password> -X POST -H 'Content-type:application/json' --data-binary '{
  "delete-field" : { "name":"timestamp" }
}' http://localhost:8983/solr/ikasan/schema
 
curl --user <new-user>:<password> -X POST -H 'Content-type:application/json' --data-binary '{
  "add-field":{
     "name":"timestamp",
     "type":"plongs",
     "stored":true,
         "multiValued":false}
}' http://localhost:8983/solr/ikasan/schema
````
````text
curl --user <new-user>:<password> -X POST -H 'Content-type:application/json' --data-binary '{
  "delete-field" : { "name":"componentName" }
}' http://localhost:8983/solr/ikasan/schema
 
curl --user <new-user>:<password> -X POST -H 'Content-type:application/json' --data-binary '{
  "add-field":{
     "name":"componentName",
     "type":"strings",
     "stored":true,
         "multiValued":false}
}' http://localhost:8983/solr/ikasan/schema
````

````text
curl --user <new-user>:<password> -X POST -H 'Content-type:application/json' --data-binary '{
  "delete-field" : { "name":"flowName" }
}' http://localhost:8983/solr/ikasan/schema
 
curl --user <new-user>:<password> -X POST -H 'Content-type:application/json' --data-binary '{
  "add-field":{
     "name":"flowName",
     "type":"strings",
     "stored":true,
         "multiValued":false}
}' http://localhost:8983/solr/ikasan/schema
````

````text
curl --user <new-user>:<password> -X POST -H 'Content-type:application/json' --data-binary '{
  "delete-field" : { "name":"moduleName" }
}' http://localhost:8983/solr/ikasan/schema
 
curl --user <new-user>:<password> -X POST -H 'Content-type:application/json' --data-binary '{
  "add-field":{
     "name":"moduleName",
     "type":"strings",
     "stored":true,
         "multiValued":false}
}' http://localhost:8983/solr/ikasan/schema
````
````text
curl --user <new-user>:<password> -X POST -H 'Content-type:application/json' --data-binary '{
  "delete-field" : { "name":"type" }
}' http://localhost:8983/solr/ikasan/schema
 
curl --user <new-user>:<password> -X POST -H 'Content-type:application/json' --data-binary '{
  "add-field":{
     "name":"type",
     "type":"strings",
     "stored":true,
         "multiValued":false}
}' http://localhost:8983/solr/ikasan/schema
````
````text
curl --user <new-user>:<password> -X POST -H 'Content-type:application/json' --data-binary '{
  "delete-field" : { "name":"event" }
}' http://localhost:8983/solr/ikasan/schema
 
curl --user <new-user>:<password> -X POST -H 'Content-type:application/json' --data-binary '{
  "add-field":{
     "name":"event",
     "type":"strings",
     "stored":true,
         "multiValued":false}
}' http://localhost:8983/solr/ikasan/schema
````

````text
curl --user <new-user>:<password> -X POST -H 'Content-type:application/json' --data-binary '{
  "delete-field" : { "name":"expiry" }
}' http://localhost:8983/solr/ikasan/schema
 
curl --user <new-user>:<password> -X POST -H 'Content-type:application/json' --data-binary '{
  "add-field":{
     "name":"expiry",
     "type":"plongs",
     "stored":true,
         "multiValued":false}
}' http://localhost:8983/solr/ikasan/schema
````

````text
curl --user <new-user>:<password> -X POST -H 'Content-type:application/json' --data-binary '{
  "delete-field" : { "name":"errorUri" }
}' http://localhost:8983/solr/ikasan/schema
 
curl --user <new-user>:<password> -X POST -H 'Content-type:application/json' --data-binary '{
  "add-field":{
     "name":"errorUri",
     "type":"strings",
     "stored":true,
         "multiValued":false}
}' http://localhost:8983/solr/ikasan/schema
````

````text
curl --user <new-user>:<password> -X POST -H 'Content-type:application/json' --data-binary '{
  "delete-field" : { "name":"errorDetail" }
}' http://localhost:8983/solr/ikasan/schema
 
curl --user <new-user>:<password> -X POST -H 'Content-type:application/json' --data-binary '{
  "add-field":{
     "name":"errorDetail",
     "type":"text_general",
     "stored":true,
         "multiValued":false}
}' http://localhost:8983/solr/ikasan/schema
````

````text
curl --user <new-user>:<password> -X POST -H 'Content-type:application/json' --data-binary '{
  "delete-field" : { "name":"errorMessage" }
}' http://localhost:8983/solr/ikasan/schema
 
curl --user <new-user>:<password> -X POST -H 'Content-type:application/json' --data-binary '{
  "add-field":{
     "name":"errorMessage",
     "type":"text_general",
     "stored":true,
         "multiValued":false}
}' http://localhost:8983/solr/ikasan/schema
````

````text
curl --user <new-user>:<password> -X POST -H 'Content-type:application/json' --data-binary '{
  "delete-field" : { "name":"exceptionClass" }
}' http://localhost:8983/solr/ikasan/schema
 
curl --user <new-user>:<password> -X POST -H 'Content-type:application/json' --data-binary '{
  "add-field":{
     "name":"exceptionClass",
     "type":"strings",
     "stored":true,
         "multiValued":false}
}' http://localhost:8983/solr/ikasan/schema
````

````text
curl --user <new-user>:<password> -X POST -H 'Content-type:application/json' --data-binary '{
  "delete-field" : { "name":"relatedEventId" }
}' http://localhost:8983/solr/ikasan/schema
 
curl --user <new-user>:<password> -X POST -H 'Content-type:application/json' --data-binary '{
  "add-field":{
     "name":"relatedEventId",
     "type":"strings",
     "stored":true,
         "multiValued":false}
}' http://localhost:8983/solr/ikasan/schema
````

````text
curl --user <new-user>:<password> -X POST -H 'Content-type:application/json' --data-binary '{
  "delete-field" : { "name":"payloadRaw" }
}' http://localhost:8983/solr/ikasan/schema
 
curl --user <new-user>:<password> -X POST -H 'Content-type:application/json' --data-binary '{
  "add-field":{
     "name":"payloadRaw",
     "type":"binary",
     "stored":true,
         "indexed":false,
     "multiValued":false }
}' http://localhost:8983/solr/ikasan/schema
````
