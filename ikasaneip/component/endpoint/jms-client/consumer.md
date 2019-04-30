![IKASAN](../../../developer/docs/quickstart-images/Ikasan-title-transparent.png)
# Generic JMS Consumer

<img src="../../../developer/docs/quickstart-images/event-driven-consumer.png" width="200px" align="left">The JMS consumer is a event driven consumer, used to connect to Legacy JBoss 4.3 and JBoss 5.1 Jboss Messaging.
Read more about EIP [Event Driven Consumer](http://www.enterpriseintegrationpatterns.com/patterns/messaging/EventDrivenConsumer.html)
<br/>
<br/>
<br/>
<br/>
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

# Document Info

| Authors | Ikasan Development Team |
| --- | --- |
| Contributors | n/a |
| Date | April 2019 |
| Email | info@ikasan.org |
| WebSite | [http://www.ikasan.org](http://www.ikasan.org/) |
