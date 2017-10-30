
# Component Quick Start

## Consumers

Starting component of the flow for which only one consumer may exist in any given flow.

### Purpose

Consumers provide the &quot;glue&quot; between the entry into the flow and the underlying technology generating he event.

### Pattern



### Types

#### Scheduled Consumer

This is a &quot;time event&quot; based consumer configured to be either an absolute or relative time schedule.

##### Configuration Options

| Option | Type | Purpose |
| --- | --- | --- |
| cronExpression | String | Cron based expression dictating the callback schedule for this component. Example, \* \* \* \* ?? |
| ignoreMisfire | boolean |   |
| isEager | boolean |   |
| timezone | String |   |

##### Sample Usage


#### Generic JMS Consumer

The JMS consumer is used to connect to Legacy JBoss 4.3 and JBoss 5.1 Jboss Messaging.

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

##### Configuration Options

| Option | Type | Purpose |
| --- | --- | --- |
| destinationJNDIProperties | String | Optional JNDI provider URL |
| destinationJNDIName | String | Optional JNDI initial context factory |
| connectionFactoryJNDIProperties | String | Optional JNDI URL packages prefix |
| connectionFactoryName | String | ConnectionFactoryName is required if not already passed on the constructor |
| connectionFactoryUsername | String | Name of destination to connect to source messages. |
| connectionFactoryPassword | String | Id set on the JMS connection for durable subscribers |
| pubSubDomain | String | Id set on the JMS session for durable subscribers |
| durableSubscriptionName | boolean | Create a durable subscription (true) on the destination which will ensure messages aren&#39;t missed when the flow is stopped or paused.If not durable (false) messages on the destination will be missed when the flow is stopped or paused. This is only applicable to topics. |
| durable | String | Authentication principal |
| sessionTransacted | boolean | Sets whether the session should be part of a transaction. |
| autoContentConversion | boolean | Extract the content of the JMS message and present this to the next component (true); or leave as a JMS message payload (false). This option can save extracting the JMS message body in subsequent components if it is only the JMS content that is of interest. For instance, you are not interested in the JMS headers. |
| batchMode |   | Use message batching. |
| batchSize |   | Batching consumer maximum messages per batch limit. |
| autoSplitBatch |   | When batchMode is true this option determines whether to automatically split the batch of messages into individual messages to fire downstream (true); or to simply pass them as a list of messages downstream (false). |
| maxConcurrentConsumers |   | Maximum number of concurrent consumers within this message listener. WARN: Using concurrent concurrency on the consumer requires downstream components within this flow to be thread safe.  |
| concurrentConsumers |   | Initial number of concurrent consumers within this message listener. WARN: Using concurrent concurrency on the consumer requires downstream components within this flow to be thread safe. |
| cacheLevel |   | Caching level of the underlying message listener container. CACHE\_NONE = 0CACHE\_CONNECTION = 1CACHE\_SESSION = 2CACHE\_CONSUMER = 3CACHE\_AUTO = 4  |

##### Sample Usage

#### Local File Consumer

This is a &quot;time event&quot; based consumer configured to be either an absolute or relative time schedule.

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

This is a &quot;time event&quot; based consumer configured to be either an absolute or relative time schedule.

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
| threadsAllowedToBlockFor
CnnectionMultiplier | Integer | multiplier for number of threads allowed to block waiting for a connection. |

##### Sample Usage

#### Database Consumer

#### (S)FTP Consumer

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

### Pattern

### Types

#### TODO

## Converters

### Purpose

### Pattern

### Types

#### TODO



# Document Info

| Authors | Ikasan Development Team |
| --- | --- |
| Contributors | n/a |
| Date | October 2015 |
| Email | info@ikasan.org |
| WebSite | [http://www.ikasan.org](http://www.ikasan.org/) |
