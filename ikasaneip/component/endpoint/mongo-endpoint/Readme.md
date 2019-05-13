[<< Component Quick Start](../../Readme.md)
![IKASAN](../../../developer/docs/quickstart-images/Ikasan-title-transparent.png)
# MongoDB Client Consumer

<img src="../../../developer/docs/quickstart-images/mongo-consumer.png" width="200px" align="left">This consumer is variation of Scheduled Consumer which is a &quot;time event&quot; based consumer configured to be either an absolute or relative time schedule, backed by Mongo Message provider.
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
| collectionNames | Map<String,String> | Names of the MongoDB collections. sThis is represented as a key-name followed by the value of the actual collection name.  |
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
