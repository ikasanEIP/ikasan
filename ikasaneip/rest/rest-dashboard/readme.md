![Problem Domain](../../developer/docs/quickstart-images/Ikasan-title-transparent.png)
# Rest Dashboard
The Ikasan Dashboard exposes a number of REST service endpoints that allow for integration modules
to push both transient data to the dashboard as well as data that describes the runtime details of the 
module along with the runtime state. The dashboard acts as an aggregator for the transient data and pushes
the transient data to a Solr text index or a database depending on the manner in which Ikasan is configured. 
The runtime metadata is also pushed to a data store and is used to build visual representations of the underlying topology,
while the runtime state can be used for monitoring and control purposes.  

## Error Harvesting Service

<details>
    <summary>Click to expand!</summary>
<p>

````json
[
  {
    "uri": "errorUri",
    "moduleName": "moduleName",
    "flowName": "flowName",
    "flowElementName": "componentName",
    "errorDetail": "errorDetail",
    "errorMessage": "failed error occurrence text",
    "exceptionClass": "exception.class",
    "eventLifeIdentifier": "lifeId",
    "eventRelatedIdentifier": "relatedLifeId",
    "action": "action",
    "event": "ZXZlbnQ=",
    "eventAsString": "event",
    "timestamp": 1000,
    "expiry": 0,
    "userAction": "userAction",
    "actionedBy": "actionedBy",
    "userActionTimestamp": 0
  },
  {
    "uri": "errorUri",
    "moduleName": "moduleName",
    "flowName": "flowName",
    "flowElementName": "componentName",
    "errorDetail": "errorDetail",
    "errorMessage": "failed error occurrence text",
    "exceptionClass": "exception.class",
    "eventLifeIdentifier": "lifeId",
    "eventRelatedIdentifier": "relatedLifeId",
    "action": "action",
    "event": "ZXZlbnQ=",
    "eventAsString": "event",
    "timestamp": 1000,
    "expiry": 0,
    "userAction": "userAction",
    "actionedBy": "actionedBy",
    "userActionTimestamp": 0
  },
  {
    "uri": "errorUri",
    "moduleName": "moduleName",
    "flowName": "flowName",
    "flowElementName": "componentName",
    "errorDetail": "errorDetail",
    "errorMessage": "failed error occurrence text",
    "exceptionClass": "exception.class",
    "eventLifeIdentifier": "lifeId",
    "eventRelatedIdentifier": "relatedLifeId",
    "action": "action",
    "event": "ZXZlbnQ=",
    "eventAsString": "event",
    "timestamp": 1000,
    "expiry": 0,
    "userAction": "userAction",
    "actionedBy": "actionedBy",
    "userActionTimestamp": 0
  }
]
````
 
</p>
</details>