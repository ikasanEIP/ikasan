
# Component Quick Start

TODO: Add picture Module/flow/component view
 
## Consumers

Starting component of the flow for which only one consumer may exist in any given flow.

### Purpose

Consumers provide the &quot;glue&quot; between the entry into the flow and the underlying technology generating he event.

### Pattern


### Types

#### Scheduled Consumer

This is a &quot;time event&quot; based consumer configured to be either an absolute or relative time schedule.
Read more about EIP [Polling Consumer](http://www.enterpriseintegrationpatterns.com/patterns/messaging/PollingConsumer.html)


##### Configuration Options

| Option | Type | Purpose |
| --- | --- | --- |
| cronExpression | String | Cron based expression dictating the callback schedule for this component. Example, \* \* \* \* ?? |
| ignoreMisfire | boolean |   |
| isEager | boolean |  Flag indicating whether if scheduled consumer should trigger(run) again, immediately after first(previous) timely run was successful   |
| timezone | String | Timezone used by quartz scheduler |

##### Sample Usage


#### Generic JMS Consumer

The JMS consumer is a event driven consumer, used to connect to Legacy JBoss 4.3 and JBoss 5.1 Jboss Messaging.
Read more about EIP [Event Driven Consumer](http://www.enterpriseintegrationpatterns.com/patterns/messaging/EventDrivenConsumer.html)


##### Configuration Options

| Option | Type | Purpose |
| --- | --- | --- |
| providerURL | String | Optional JNDI provider URL |
| initialContextFactory | String | Optional JNDI initial context factory |
| urlPackagePrefixes | String | Optional JNDI URL packages prefix |
| connectionFactoryName | String | ConnectionFactoryName is required if not already passed on the constructor |
| destinationName | String | Name of destination to connect to source messages. |
| clientId | String | Id set on the JMS connection for durable subscribers |
| subscriberId | String | Id set on the JMS session for durable subscribers |
| durable | boolean | Create a durable subscription (true) on the destination which will ensure messages aren&#39;t missed when the flow is stopped or paused.If not durable (false) messages on the destination will be missed when the flow is stopped or paused. This is only applicable to topics. |
| username | String | Authentication principal |
| password | String | Authentication credential.This value is masked on all GUI views. |
| transacted | boolean | Sets whether the session should be part of a transaction. |
| acknowledgement | int | JMS session acknowledgement level. |
| selector | String | Optional JMS message selector. |
| noLocal | boolean | Optional determines whether this consuming client connection can be used for delivery of messages back on the destination. |
| remoteJNDILookup | boolean | Are you using a local (false) or remote (true) JNDI. |
| autoContentConversion | boolean | Extract the content of the JMS message and present this to the next component (true); or leave as a JMS message payload (false). This option can save extracting the JMS message body in subsequent components if it is only the JMS content that is of interest. For instance, you are not interested in the JMS headers. |

##### Sample Usage

#### SpringTemplate JMS Consumer

The JMS consumer is Event Driven Consumer, used to connect to any Vendor specific JMS Broker(ActiveMQ, HornetQ, IBM MQ etc). However one need to include the related vendor specific libraries in the IM. 
Read more about EIP [Event Driven Consumer](http://www.enterpriseintegrationpatterns.com/patterns/messaging/EventDrivenConsumer.html)


##### Configuration Options

| Option | Type | Purpose |
| --- | --- | --- |
| destinationJNDIProperties | Map<String,String> | Optional JNDI parameters map. Typical map would include following keys :<ul><li>java.naming.provider.url</li><li> java.naming.factory.initial</li><li>java.naming.factory.url.pkgs </li><li>java.naming.security.credentials</li><li>java.naming.security.principle</li></ul> |
| destinationJNDIName | String | Destination(Topic/Queue) name, which could refer as well to JNDI name |
| connectionFactoryJNDIProperties | Map<String,String> | Optional JNDI parameters map. Typical map would include following keys : <ul><li>java.naming.provider.url</li><li> java.naming.factory.initial</li><li>java.naming.factory.url.pkgs </li><li>java.naming.security.credentials</li><li>java.naming.security.principle</li></ul> |
| connectionFactoryName | String | ConnectionFactoryName is required if not already passed on the constructor |
| connectionFactoryUsername | String | Authentication principal  |
| connectionFactoryPassword | String | Id set on the JMS connection for durable subscribers |
| pubSubDomain | boolean | set to true to indicate that destination is a topic, otherwise destina|
| durableSubscriptionName | String | Id set on the JMS session for durable subscribers  |
| durable | boolean | Create a durable subscription (true) on the destination which will ensure messages aren&#39;t missed when the flow is stopped or paused.If not durable (false) messages on the destination will be missed when the flow is stopped or paused. This is only applicable to topics. |
| sessionTransacted | boolean | Sets whether the session should be part of a transaction. |
| autoContentConversion | boolean | Extract the content of the JMS message and present this to the next component (true); or leave as a JMS message payload (false). This option can save extracting the JMS message body in subsequent components if it is only the JMS content that is of interest. For instance, you are not interested in the JMS headers. |
| batchMode | boolean  | Use message batching. |
| batchSize |  integer  | Batching consumer maximum messages per batch limit. |
| autoSplitBatch | boolean  | When batchMode is true this option determines whether to automatically split the batch of messages into individual messages to fire downstream (true); or to simply pass them as a list of messages downstream (false). |
| maxConcurrentConsumers |  integer | Maximum number of concurrent consumers within this message listener. WARN: Using concurrent concurrency on the consumer requires downstream components within this flow to be thread safe.  |
| concurrentConsumers |  integer | Initial number of concurrent consumers within this message listener. WARN: Using concurrent concurrency on the consumer requires downstream components within this flow to be thread safe. |
| cacheLevel | integer  | Caching level of the underlying message listener container. <ul><li>CACHE\_NONE = 0</li><li>CACHE\_CONNECTION = 1</li><li>CACHE\_SESSION = 2</li><li>CACHE\_CONSUMER = 3</li><li>CACHE\_AUTO = 4</li></ui>  |
| sessionAcknowledgeMode | integer | |

##### Sample Usage

#### Local File Consumer

This consumer is variation of Scheduled Consumer which is a &quot;time event&quot; based consumer configured to be either an absolute or relative time schedule, backed by Local File Message provider.
Read more about EIP [Polling Consumer](http://www.enterpriseintegrationpatterns.com/patterns/messaging/PollingConsumer.html)


##### Configuration Options

| Option | Type | Purpose |
| --- | --- | --- |
| filenames | List<String> | Filenames to be processed. |
| encoding | String | Encoding of the files |
| includeHeader | boolean | Assume first line of the file is a header and include it as a header within the message payload. |
| sortByModifiedDateTime | boolean | Sort the received file list by last modified date time. |
| sortAscending | boolean | Sort the list in ascending order (true) or descending order (false) when a sort method is used. |
| directoryDepth | int | How deep down the directory tree to go to find matching filenames. |
| logMatchedFilenames | boolean | Write any matching filenames found to the log files as additional information. |
| ignoreFileRenameWhilstScanning | boolean | Ignore cases where the file has been renamed between scanning and retrieval. |

##### Sample Usage

#### MongoDB Client Consumer

This consumer is variation of Scheduled Consumer which is a &quot;time event&quot; based consumer configured to be either an absolute or relative time schedule, backed by Mongo Message provider.
Read more about EIP [Polling Consumer](http://www.enterpriseintegrationpatterns.com/patterns/messaging/PollingConsumer.html)


##### Configuration Options

NOTE: All options specified below will override any associated options in the driver if also specified.

| Option | Type | Purpose |
| --- | --- | --- |
| connectionUrls | List<String> | Connection URLs to try to connect to MongoDB |
| authenticated | boolean | Is the connection authenticated. |
| username | String | Principal for simple authentication |
| password | Masked String | Password credential for simple authentication. |
| databaseName | String | Name of the MongoDB database. |
| collectionNames | Map<String,String> | Names of the MongoDB collections.
This is represented as a key-name followed by the value of the actual collection name.  |
| readPreference | ReadPreference | Replicate set members to which any query may be sent. |
| writeConcern | WriteConcern | Sets acknowledgement of write operations. |
| localThreshold | Integer | Local threshold |
| alwaysUseMBeans | Boolean | Whether JMX beans registered by the driver should always be MBeans. |
| connectionsPerHost | Integer | Sets the maximum allowed connections per host. |
| connectionTimeout | Integer | Connection timeout. |
| cursorFinalizerEnabled | Boolean | Sets whether cursor finalizers are enabled. |
| description | String |   |
| minHeartbeatFrequency | Integer |   |
| heartbeatConnectTimeout | Integer |   |
| heartbeatConnectFrequency | Integer |   |
| heartbeatSocketTimeout | Integer |   |
| legacyDefaults | Boolean |   |
| maxConnectionIdleTime | Integer |   |
| maxConnectionLifeTIme | Integer |   |
| maxWaitTime | Integer |   |
| minConnectionsPerHost | Integer |   |
| requiredReplicaSetName | String |   |
| keepSocketAlive | Boolean |   |
| socketTimeout | Integer | Socket timeout. |
| threadsAllowedToBlockForConnectionMultiplier | Integer | multiplier for number of threads allowed to block waiting for a connection. |

##### Sample Usage

#### Database Consumer

#### (S)FTP Consumer

This consumer is variation of Scheduled Consumer which is a &quot;time event&quot; based consumer configured to be either an absolute or relative time schedule, backed by (S)FTP Message provider. The (S)FTP Message provider is under pined with persistent store which allow us to store meta information about the files we are processing.
Read more about EIP [Polling Consumer](http://www.enterpriseintegrationpatterns.com/patterns/messaging/PollingConsumer.html)


##### Configuration Options

| Option | Type | Purpose |
| --- | --- | --- |
| cronExpression | String | Cron based expression dictating the callback schedule for this component. Example, \* \* \* \* ?? |
| ignoreMisfire | boolean |   |
| isEager | boolean |  Flag indicating whether if scheduled consumer should trigger(run) again, immediately after first(previous) timely run was successful   |
| timezone | String | Timezone used by quartz scheduler |
| sourceDirectory | String | Remote directory from which to discover files |
| filenamePattern | String | Regular expression for matching file names |
| sourceDirectoryURLFactory | DirectoryURLFactory | Classname for source directories URLs factory. The factory provides more flexible way of defining source directory. Most common use case would be when source directory changes names for instance based on date|
| filterDuplicates | boolean | Default(True) Flag indicating whether to filter out duplicates files based on previously persisted meta information. When value set to false no meta data is persisted hence same file could be processed over and over again.  |
| filterOnFilename | boolean | Default(True) Flag indicating whether to include file name when persisting meta information about processed file.  |
| filterOnLastModifiedDate | boolean | Default(True) Flag indicating whether to include last modified date of the file when persisting meta information about processed file and whether to use the last modified date for filtering. If filterOnFilename=true and filterOnLastModifiedDate=false any modifications to the files would not be detected and file wouldn’t be reprocessed.   |
| renameOnSuccess | boolean | Default(False) Flag indicating whether to rename the processed file after successful consumption |
| renameOnSuccessExtension | String | Optional only applicable when renameOnSuccess=true, renameOnSuccessExtension is suffixed to the processed fileName |
| moveOnSuccess | boolean | Default(False) Flag indicating whether to move the processed file after successful consumption to different location defined by moveOnSuccessNewPath  configuration. |
| moveOnSuccessNewPath | String | Optional only applicable when moveOnSuccess=true, it provides new directory path when the processed file is moved to. |
| destructive | boolean | Default(False) Flag indicating whether the processed file should be deleted after successful consumption |
| chronological | boolean | Default(False) Flag indicating whether the file processing should be based on chronological order of file latest updates. |
| chunking | boolean | Default(False) Flag indicating whether the file download should be performed in smaller distinguished data chunks of size defined by chunkSize configuration. |
| chunkSize | integer | Optional only applicable when chunking=true. Default(1048576) 1MB. |
| checksum | boolean | Default(False) Flag indicating whether to verify integrity of retrieved file by comparing with a checksum supplied by the remote system. |
| isRecursive | boolean | Default(False) Flag indicating whether the sourceDirectory file read should be performed in recursive manner. The option can be useful if once consumes files from top level directory without knowing the lower lever dir structure. |
| minAge | integer | Default(120) file filter related option, expressed in seconds, used to indicate minimum age of the file on the remote filesystem before file can be processed. This setting is in place to prevent (S)FTP consumer from picking up file which is still being written to.|
| maxRows | integer | Default(-1) file filter related option. Given that meta data of processed files is being collected on every successful file consumptions, the maxRows option relates to housekeeping of the meta information. On every successful file consumption as part of post commit process file (S)FTP consumer will attempt to delete maxRows records from file filter persistence table. The operation is skipped when maxRows=-1 |
| ageOfFiles | integer | Default(-1) file filter related option expressed in days. Given that meta data of processed files is being collected on every successful file consumptions, the ageOfFiles option relates to housekeeping of the meta information. On every successful file consumption as part of post commit process file (S)FTP consumer will attempt to delete records older than ageOfFiles records from file filter persistence table. The operation is skipped when ageOfFiles=-1 |
| clientId | String | file filter related option identifying consumer. clientId is stored as part of the meta information persisted about the processed file.  |
| cleanupJournalOnComplete | boolean | Default(true) Existing (S)FTP consumer is using DB persistence tables to establish different operations it is performing as part of the usage of command pattern (FileDiscovery, FileRename, FileRetrive). That persistent information is be default cleaned up when cleanupJournalOnComplete=true. It can be occasionally useful to cleanupJournalOnComplete=false when performing some debugging. |
| remoteHost | String | Default(‘localhost’) host name of the remote (S)FTP server where consumer needs to connect.|
| remotePort | integer | Default(22) port of the remote (S)FTP server where consumer needs to connect.|
| username | String | User name used to login to (S)FTP server where consumer needs to connect.|
| password | String | password used to login to (S)FTP server where consumer needs to connect. Takes precedences over privateKeyFilename. If both provided user/password combination will be used to login rather then user/privateKeyFilename. |
| maxRetryAttempts | integer | Default(3) internal (S)FTP connector retry count. |
| connectionTimeout | integer | Default(60000) expressed in milliseconds. Internal (S)FTP connector connection timeout value. |
| privateKeyFilename | String | Optional only available on SFTP consumer. Allows authentication to remote server with private/public key set given the exchange of the keys and connectivity setup were performed upfront. |
| knownHostsFilename | String | Optional only available on SFTP consumer. Works in combination with private/public key set. |
| preferredKeyExchangeAlgorithm | String | Optional only available on SFTP consumer. Allowing to explicitly provide KeyExchange Algorithm used by the remote server. |
| active | boolean | Optional only available on FTP consumer. Default(False) Flag indicating whether the FTP connection is active or passive | 
| dataTimeout | integer | Optional only available on FTP consumer. Default(300000) expressed in milliseconds. Internal FTP connector data connection timeout value. |
| socketTimeout | integer | Optional only available on FTP consumer. Default(300000) expressed in milliseconds. Internal FTP connector socket connection timeout value. |
| systemKey | String | Optional only available on FTP consumer.  |
| passwordFilePath | String | Optional only available on FTP consumer. The path of the file that contains the password. |
| isFTPS | boolean | Optional only available on FTP consumer. Default(false) used to determine if connection is using FTPs |
| ftpsPort | integer | Optional only available on FTP consumer. Default(21) only applicable when isFTPS=true. The remote port of FTPs server where consumer needs to connect. |
| ftpsProtocol | String | Optional only available on FTP consumer. Default(‘SSL’) only applicable when isFTPS=true. The protocol used for remote FTPs connection. |
| ftpsIsImplicit | booleans | Optional only available on FTP consumer. Default(false) only applicable when isFTPS=true. |
| ftpsKeyStoreFilePath | String | Optional only available on FTP consumer. Only applicable when isFTPS=true. |
| ftpsKeyStoreFilePassword | String | Optional only available on FTP consumer. Only applicable when isFTPS=true. |


#### File System Consumer

#### Event Generating Consumer

Utility consumer for the generation of ad-hoc events for demonstration or test of flows.

##### Configuration Options

| Option | Type | Purpose |
| --- | --- | --- |
| identifier | String | Identifier to be set on the generated FlowEvent |
| payload | String | Payload to be set on the generated FlowEvent |
| eventGenerationInterval | long | Event generation inrerval in milliseconds.0 = immediate and continuous event generation. This is the default value. |
| batchSize | int | Used in conjunction with the eventGenerationInterval to determine number of generated events per interval. |
| eventLimit | String | Allow a limit to be set on the total number of events generated. Default -1 = unlimited. |

##### Sample Usage

## Translators

### Purpose
Read more about EIP [Translators](http://www.enterpriseintegrationpatterns.com/patterns/messaging/MessageTranslator.html)

### Pattern

### Types

#### TODO

## Converters

### Purpose
Read more about EIP [Translators](http://www.enterpriseintegrationpatterns.com/patterns/messaging/MessageTranslator.html)

### Pattern

### Types

## Brokers

### Purpose

### Pattern

### Types

## Splitters

### Purpose
Read more about EIP [Translators](http://www.enterpriseintegrationpatterns.com/patterns/messaging/Sequencer.html)

### Pattern

### Types

## Routers

### Purpose
Read more about EIP [Message Router](http://www.enterpriseintegrationpatterns.com/patterns/messaging/MessageRouter.html)


### Pattern

### Types
 
## Producers

Ending component of the flow, the only component in the flow which have no transition.

### Purpose


Read more about EIP [Message Endpoint](http://www.enterpriseintegrationpatterns.com/patterns/messaging/MessageEndpoint.html)


### Pattern


### Types

#### Dev Null Producer

This type of producer discards all data passed to it and does not perform any processing. 

#### JMS Template Producer

The JMS producer is based on Spring template and is used to connect to any Vendor specific JMS Broker(ActiveMQ, HornetQ, IBM MQ etc). However one need to include the related vendor specific libraries in the IM. 

##### Configuration Options

| Option | Type | Purpose |
| --- | --- | --- |
| destinationJNDIProperties | Map<String,String> | Optional JNDI parameters map. Typical map would include following keys : <ul><li>java.naming.provider.url</li><li> java.naming.factory.initial</li><li>java.naming.factory.url.pkgs </li><li>java.naming.security.credentials</li><li>java.naming.security.principle</li></ul> |
| destinationJNDIName | String | Destination(Topic/Queue) name, which could refer as well to JNDI name |
| connectionFactoryJNDIProperties | Map<String,String> | Optional JNDI parameters map. Typical map would include following keys : <ul><li>java.naming.provider.url</li><li> java.naming.factory.initial</li><li>java.naming.factory.url.pkgs </li><li>java.naming.security.credentials</li><li>java.naming.security.principle</li></ul> |
| connectionFactoryName | String | ConnectionFactoryName is required if not already passed on the constructor |
| connectionFactoryUsername | String | Authentication principal  |
| connectionFactoryPassword | String | Id set on the JMS connection for durable subscribers |
| pubSubDomain | boolean | set to true to indicate that destination is a topic, otherwise destination is a queue|
| deliveryPersistent | boolean | Default(false) flag indicating whether publishing messages are persisted on the broker or hold in memory   |
| deliveryMode | integer |  |
| sessionTransacted | boolean | Sets whether the session should be part of a transaction. |
| explicitQosEnabled | boolean | Default(false) |
| messageIdEnabled | boolean | Default(false) |
| messageTimestampEnabled | boolean | Default(false) |
| priority | Integer | Optional allows to set message priority on the message. This option will only work if the broker or/and consumer are configured to use the priority when dispatching/consuming messages. |
| pubSubNoLocal | boolean | Default(false) flag indicating whether to inhibit the delivery of messages published by its own connection. |
| receiveTimeout | long | Optional the timeout to use for receive calls (in milliseconds). |
| sessionAcknowledgeMode | integer | Optional the JMS acknowledgement mode that is used when creating a JMS Session to send a message   <ul><li>AUTO_ACKNOWLEDGE = 1</li><li>CLIENT_ACKNOWLEDGE = 2</li><li>DUPS_OK_ACKNOWLEDGE = 3</li><li>SESSION_TRANSACTED = 0</li></ul> |
| sessionAcknowledgeModeName | String | Optional |
| timeToLive | long | Optional the time-to-live of the message when sending. |

##### Sample Usage

#### (S)FTP Producer

This producer allows delivery of a file to remote (S)FTP server. The producer is under pined with persistent store which saves meta information about the deliver files.

##### Configuration Options

| Option | Type | Purpose |
| --- | --- | --- |
| clientId | String | clientId is stored as part of the meta information persisted about the processed file. The clientId should be a unique value identifing producer or consumer connecting to remote server.|
| cleanupJournalOnComplete | boolean | Default(true) Existing (S)FTP producer is using DB persistence tables to establish different operations it is performing as part of the command pattern (FileDiscovery, FileRename, FileDelivery). That persistent information is be default cleaned up when cleanupJournalOnComplete=true. It can be occasionally useful to cleanupJournalOnComplete=false when performing some debugging. |
| remoteHost | String | Default(‘localhost’) host name of the remote (S)FTP server where producer needs to connect.|
| remotePort | integer | Default(22) port of the remote (S)FTP server where producer needs to connect.|
| username | String | User name used to login to (S)FTP server where producer needs to connect.|
| password | String | password used to login to (S)FTP server where producer needs to connect. Takes precedences over privateKeyFilename. If both provided user/password combination will be used to login rather then user/privateKeyFilename. |
| maxRetryAttempts | integer | Default(3) internal (S)FTP connector retry count. |
| connectionTimeout | integer | Default(60000) expressed in milliseconds. Internal (S)FTP connector connection timeout value. |
| outputDirectory | String | Remote directory where to deliver the file |
| renameExtension | String | Default(‘tmp’) file delivery to remote location takes place in two stages, first a file is delivered with suffix equal to renameExtension, further file is renamed with suffix being removed.|
| tempFileName | String ||
| overwrite | boolean | Default(false) Flag indicating whether the remote file can be overwritten on remote server.| 
| unzip | boolean | Default(false) Flag indicating whether the file should be unzipped on remote server as post delivery task. This makes an assumption that delivered stream is zipped.| 
| checksumDelivered | boolean | Default(false) Flag indicating whether producer should generate and deliver a checksum md5 hash file to remote server.| 
| createParentDirectory | boolean | Default(false) Flag indicating whether outputDirectory should be created if it does not exist. | 
| cleanUpChunks | boolean | Default(trunk) Flag indicating whether chunk file information persisted in DB should be removed by producer after successful file delivery aka. successful file assembling from chunks.| 
| privateKeyFilename | String | Optional only available on SFTP producer. Allows authentication to remote server with private/public key set given the exchange of the keys and connectivity setup were performed upfront. |
| knownHostsFilename | String | Optional only available on SFTP producer. Works in combination with private/public key set. |
| preferredKeyExchangeAlgorithm | String | Optional only available on SFTP producer. Allowing to explicitly provide KeyExchange Algorithm used by the remote server. |
| active | boolean | Optional only available on FTP producer. Default(False) Flag indicating whether the FTP connection is active or passive | 
| dataTimeout | integer | Optional only available on FTP producer. Default(300000) expressed in milliseconds. Internal FTP connector data connection timeout value. |
| socketTimeout | integer | Optional only available on FTP producer. Default(300000) expressed in milliseconds. Internal FTP connector socket connection timeout value. |
| systemKey | String | Optional only available on FTP producer.  |


# Document Info

| Authors | Ikasan Development Team |
| --- | --- |
| Contributors | n/a |
| Date | November 2017 |
| Email | info@ikasan.org |
| WebSite | [http://www.ikasan.org](http://www.ikasan.org/) |
