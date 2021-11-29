[../](../../Readme.md)
![IKASAN](../developer/docs/quickstart-images/Ikasan-title-transparent.png)
# REST Module Client
 
The REST Module Client is able to interact with Module REST endpoints.

- resubmission of exclusions
- replay recorded events 
- get and update module/flow/component/flowInvoker configuration 
- get and update module runtime information like state

Http connection time outs can be set using the following properties:

```properties
module.rest.connection.readTimeout=5000
module.rest.connection.connectTimeout=5000
module.rest.connection.connectionRequestTimeout=5000
```