![Problem Domain](../../developer/docs/quickstart-images/Ikasan-title-transparent.png)
# Ikasan ESB Dashboard Rest Services
- [Authorization Endpoint](#authorization-endpoint)
- [Error Harvesting Service](#error-harvesting-service)
- [Exclusions Harvesting Service](#exclusions-harvesting-service)
- [Metrics Harvesting Service](#metrics-harvesting-service)
- [Replay Events Harvesting Service](#replay-events-harvesting-service)
- [Wiretap Events Harvesting Service](#wiretap-events-harvesting-service)
- [Metadata Service](#metadata-service)
- [Configuration Service](#configuration-service)
- [Metrics Retrieval Services](#metrics-retrieval-services)
  - [Metrics by Start and End Time Service](#metrics-by-start-and-end-time-service) 
  - [Metrics by Module Name, Start and End Time Service](#metrics-by-module-name-start-and-end-time-service)
  - [Metrics by Module Name, Flow Name, Start and End Time Service](#metrics-by-module-name-flow-name-start-and-end-time-service)
  - [Metrics Count by Start and End Time Service](#metrics-count-by-start-and-end-time-service)
  - [Metrics Count by Module Name, Start and End Time Service](#metrics-count-by-module-name-start-and-end-time-service)
  - [Metrics Count by Module Name, Flow Name, Start and End Time Service](#metrics-count-by-module-name-flow-name-start-and-end-time-service)
  - [Paged Metrics by Start and End Time Service](#paged-metrics-by-start-and-end-time-service)
  - [Paged Metrics by Module Name, Start and End Time Service](#paged-metrics-by-module-name-start-and-end-time-service)
  - [Paged Metrics by Module Name, Flow Name, Start and End Time Service](#paged-metrics-by-module-name-flow-name-start-and-end-time-service)

The Ikasan Dashboard exposes a number of REST service endpoints that allow for integration modules
to push both transient data to the dashboard as well as data that describes the runtime details of the 
module along with the runtime state. The dashboard acts as an aggregator for the transient data and pushes
the transient data to a Solr text index or a database depending on the manner in which Ikasan is configured. 
The runtime metadata is also pushed to a data store and is used to build visual representations of the underlying topology,
while the runtime state can be used for monitoring and control purposes. 

All Ikasan Dashboard REST service endpoints require Authorisation HTTP header to be send along with data payload. 
Authorisation Header has a form of "Bearer {JWT TOKEN}". The {JWT TOKEN} can be obtained from Authorisation Endpoint by
providing user credentials.

## Authorization Endpoint
Authentication and Authorization Service.

| Parameter       | Value                                                                                          | 
|-----------------|------------------------------------------------------------------------------------------------|
| Request Method  | POST                                                                                           |
| Service Context | {dashboard-root-context}/authenticate                                                          |
| Payload         | A json serialised  [JwtRequest](src/main/java/org/ikasan/rest/dashboard/model/JwtRequest.java) |

<details>
    <summary>Click to view the sample JSON payload expected by the service.</summary>
<p>

````json
{
     "username":"testUsername",
     "password":"SecretPassword"
}
````
 
</p>



</details>

<details>
    <summary>Provided response.</summary>
<p>

````json
{
  "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImV4cCI6MTU2NzI1NzYwOCwiaWF0IjoxNTY3MjM5NjA4fQ.9v2AolonpxP2E6jl-PJVNK-A3oHrHTR1YNM9MCQMkSTLtbreO9vAWlh6dsN3NeWgRipXWVcG3TZp1HO0gQnndw"
}
````

</p>


</details>

## Error Harvesting Service
Aggregation service for errors produced by the Ikasan Hospital service.

| Parameter                            | Value                                                                                                                                             | 
|--------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------|
| Request Method                       | PUT                                                                                                                                               |
| Service Context                      | {dashboard-root-context}/rest/harvest/errors                                                                                                      |
| Requires 'Authorization' HTTP Header | Bearer {JWT TOKEN}                                                                                                                                |
| Payload                              | A json serialised List of [ErrorOccurence](../../spec/service/error-reporting/src/main/java/org/ikasan/spec/error/reporting/ErrorOccurrence.java) |

<details>
    <summary>Click to view the sample JSON payload expected by the service.</summary>
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

## Exclusions Harvesting Service
Aggregation service for exclusions produced by the Ikasan Hospital service.

| Parameter                            | Value                                                                                                                                | 
|--------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------|
| Request Method                       | PUT                                                                                                                                  |
| Service Context                      | {dashboard-root-context}/rest/harvest/exclusions                                                                                     |
| Requires 'Authorization' HTTP Header | Bearer {JWT TOKEN}                                                                                                                   |
| Payload                              | A json serialised List of [ExclusionEvent](../../spec/service/exclusion/src/main/java/org/ikasan/spec/exclusion/ExclusionEvent.java) |


<details>
    <summary>Click to view the sample JSON payload expected by the service.</summary>
<p>

````json
[
  {
    "id": 1230,
    "moduleName": "moduleName",
    "flowName": "flowName",
    "identifier": "identifier",
    "event": "ZXZlbnQ=",
    "timestamp": 1234,
    "errorUri": "errorUri",
    "harvested": false
  },
  {
    "id": 1230,
    "moduleName": "moduleName",
    "flowName": "flowName",
    "identifier": "identifier",
    "event": "ZXZlbnQ=",
    "timestamp": 1234,
    "errorUri": "errorUri",
    "harvested": false
  },
  {
    "id": 1230,
    "moduleName": "moduleName",
    "flowName": "flowName",
    "identifier": "identifier",
    "event": "ZXZlbnQ=",
    "timestamp": 1234,
    "errorUri": "errorUri",
    "harvested": false
  }
]
````
 
</p>
</details>

## Metrics Harvesting Service
Aggregation service for metrics produced by the Ikasan Metrics service.

| Parameter                            | Value                                                                                                                                        | 
|--------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------|
| Request Method                       | PUT                                                                                                                                          |
| Service Context                      | {dashboard-root-context}/rest/harvest/metrics                                                                                                |
| Requires 'Authorization' HTTP Header | Bearer {JWT TOKEN}                                                                                                                           |
| Payload                              | A json serialised List of [FlowInvocationMetric](../../spec/service/history/src/main/java/org/ikasan/spec/history/FlowInvocationMetric.java) |


<details>
    <summary>Click to view the sample JSON payload expected by the service.</summary>
<p>

````json
[
  {
    "id": 1,
    "moduleName": "moduleName",
    "flowName": "flowName",
    "invocationStartTime": 1564929295578,
    "invocationEndTime": 1564929296078,
    "finalAction": "ACTION",
    "componentInvocationMetricImpls": [
      {
        "componentName": "componentName",
        "beforeEventIdentifier": "lifeId0",
        "beforeRelatedEventIdentifier": "relatedLifeId0",
        "afterEventIdentifier": "lifeId0",
        "afterRelatedEventIdentifier": "relatedLifeId0",
        "startTimeMillis": 1564929295576,
        "endTimeMillis": 1564929296076,
        "id": 3,
        "flowInvocation": null,
        "metrics": [
          {
            "id": 3,
            "componentInvocationMetricImpl": null,
            "name": "name",
            "value": "value"
          }
        ],
        "wiretapFlowEvent": {
          "identifier": 5,
          "timestamp": 1564929296076,
          "moduleName": "moduleName",
          "flowName": "flowName",
          "componentName": "componentName",
          "event": "payload",
          "expiry": 30,
          "nextByEventId": null,
          "previousByEventId": null,
          "eventId": "lifeId0",
          "relatedEventId": null,
          "eventTimestamp": 0
        }
      },
      {
        "componentName": "componentName",
        "beforeEventIdentifier": "lifeId0",
        "beforeRelatedEventIdentifier": "relatedLifeId0",
        "afterEventIdentifier": "lifeId0",
        "afterRelatedEventIdentifier": "relatedLifeId0",
        "startTimeMillis": 1564929295570,
        "endTimeMillis": 1564929296070,
        "id": 2,
        "flowInvocation": null,
        "metrics": [
          {
            "id": 2,
            "componentInvocationMetricImpl": null,
            "name": "name",
            "value": "value"
          }
        ],
        "wiretapFlowEvent": {
          "identifier": 5,
          "timestamp": 1564929296076,
          "moduleName": "moduleName",
          "flowName": "flowName",
          "componentName": "componentName",
          "event": "payload",
          "expiry": 30,
          "nextByEventId": null,
          "previousByEventId": null,
          "eventId": "lifeId0",
          "relatedEventId": null,
          "eventTimestamp": 0
        }
      },
      {
        "componentName": "componentName",
        "beforeEventIdentifier": "lifeId0",
        "beforeRelatedEventIdentifier": "relatedLifeId0",
        "afterEventIdentifier": "lifeId0",
        "afterRelatedEventIdentifier": "relatedLifeId0",
        "startTimeMillis": 1564929295400,
        "endTimeMillis": 1564929295900,
        "id": 1,
        "flowInvocation": null,
        "metrics": [
          {
            "id": 1,
            "componentInvocationMetricImpl": null,
            "name": "name",
            "value": "value"
          }
        ],
        "wiretapFlowEvent": {
          "identifier": 5,
          "timestamp": 1564929296076,
          "moduleName": "moduleName",
          "flowName": "flowName",
          "componentName": "componentName",
          "event": "payload",
          "expiry": 30,
          "nextByEventId": null,
          "previousByEventId": null,
          "eventId": "lifeId0",
          "relatedEventId": null,
          "eventTimestamp": 0
        }
      },
      {
        "componentName": "componentName",
        "beforeEventIdentifier": "lifeId0",
        "beforeRelatedEventIdentifier": "relatedLifeId0",
        "afterEventIdentifier": "lifeId0",
        "afterRelatedEventIdentifier": "relatedLifeId0",
        "startTimeMillis": 1564929295572,
        "endTimeMillis": 1564929296072,
        "id": 4,
        "flowInvocation": null,
        "metrics": [
          {
            "id": 4,
            "componentInvocationMetricImpl": null,
            "name": "name",
            "value": "value"
          }
        ],
        "wiretapFlowEvent": {
          "identifier": 5,
          "timestamp": 1564929296076,
          "moduleName": "moduleName",
          "flowName": "flowName",
          "componentName": "componentName",
          "event": "payload",
          "expiry": 30,
          "nextByEventId": null,
          "previousByEventId": null,
          "eventId": "lifeId0",
          "relatedEventId": null,
          "eventTimestamp": 0
        }
      },
      {
        "componentName": "componentName",
        "beforeEventIdentifier": "lifeId0",
        "beforeRelatedEventIdentifier": "relatedLifeId0",
        "afterEventIdentifier": "lifeId0",
        "afterRelatedEventIdentifier": "relatedLifeId0",
        "startTimeMillis": 1564929295574,
        "endTimeMillis": 1564929296074,
        "id": 5,
        "flowInvocation": null,
        "metrics": [
          {
            "id": 5,
            "componentInvocationMetricImpl": null,
            "name": "name",
            "value": "value"
          }
        ],
        "wiretapFlowEvent": {
          "identifier": 5,
          "timestamp": 1564929296076,
          "moduleName": "moduleName",
          "flowName": "flowName",
          "componentName": "componentName",
          "event": "payload",
          "expiry": 30,
          "nextByEventId": null,
          "previousByEventId": null,
          "eventId": "lifeId0",
          "relatedEventId": null,
          "eventTimestamp": 0
        }
      }
    ],
    "harvested": true,
    "expiry": 0,
    "errorUri": null,
    "harvestedDateTime": 1564929297534,
    "flowInvocationEvents": [
      {
        "componentName": "componentName",
        "beforeEventIdentifier": "lifeId0",
        "beforeRelatedEventIdentifier": "relatedLifeId0",
        "afterEventIdentifier": "lifeId0",
        "afterRelatedEventIdentifier": "relatedLifeId0",
        "startTimeMillis": 1564929295576,
        "endTimeMillis": 1564929296076,
        "id": 3,
        "flowInvocation": null,
        "metrics": [
          {
            "id": 3,
            "componentInvocationMetricImpl": null,
            "name": "name",
            "value": "value"
          }
        ],
        "wiretapFlowEvent": {
          "identifier": 5,
          "timestamp": 1564929296076,
          "moduleName": "moduleName",
          "flowName": "flowName",
          "componentName": "componentName",
          "event": "payload",
          "expiry": 30,
          "nextByEventId": null,
          "previousByEventId": null,
          "eventId": "lifeId0",
          "relatedEventId": null,
          "eventTimestamp": 0
        }
      },
      {
        "componentName": "componentName",
        "beforeEventIdentifier": "lifeId0",
        "beforeRelatedEventIdentifier": "relatedLifeId0",
        "afterEventIdentifier": "lifeId0",
        "afterRelatedEventIdentifier": "relatedLifeId0",
        "startTimeMillis": 1564929295570,
        "endTimeMillis": 1564929296070,
        "id": 2,
        "flowInvocation": null,
        "metrics": [
          {
            "id": 2,
            "componentInvocationMetricImpl": null,
            "name": "name",
            "value": "value"
          }
        ],
        "wiretapFlowEvent": {
          "identifier": 5,
          "timestamp": 1564929296076,
          "moduleName": "moduleName",
          "flowName": "flowName",
          "componentName": "componentName",
          "event": "payload",
          "expiry": 30,
          "nextByEventId": null,
          "previousByEventId": null,
          "eventId": "lifeId0",
          "relatedEventId": null,
          "eventTimestamp": 0
        }
      },
      {
        "componentName": "componentName",
        "beforeEventIdentifier": "lifeId0",
        "beforeRelatedEventIdentifier": "relatedLifeId0",
        "afterEventIdentifier": "lifeId0",
        "afterRelatedEventIdentifier": "relatedLifeId0",
        "startTimeMillis": 1564929295400,
        "endTimeMillis": 1564929295900,
        "id": 1,
        "flowInvocation": null,
        "metrics": [
          {
            "id": 1,
            "componentInvocationMetricImpl": null,
            "name": "name",
            "value": "value"
          }
        ],
        "wiretapFlowEvent": {
          "identifier": 5,
          "timestamp": 1564929296076,
          "moduleName": "moduleName",
          "flowName": "flowName",
          "componentName": "componentName",
          "event": "payload",
          "expiry": 30,
          "nextByEventId": null,
          "previousByEventId": null,
          "eventId": "lifeId0",
          "relatedEventId": null,
          "eventTimestamp": 0
        }
      },
      {
        "componentName": "componentName",
        "beforeEventIdentifier": "lifeId0",
        "beforeRelatedEventIdentifier": "relatedLifeId0",
        "afterEventIdentifier": "lifeId0",
        "afterRelatedEventIdentifier": "relatedLifeId0",
        "startTimeMillis": 1564929295572,
        "endTimeMillis": 1564929296072,
        "id": 4,
        "flowInvocation": null,
        "metrics": [
          {
            "id": 4,
            "componentInvocationMetricImpl": null,
            "name": "name",
            "value": "value"
          }
        ],
        "wiretapFlowEvent": {
          "identifier": 5,
          "timestamp": 1564929296076,
          "moduleName": "moduleName",
          "flowName": "flowName",
          "componentName": "componentName",
          "event": "payload",
          "expiry": 30,
          "nextByEventId": null,
          "previousByEventId": null,
          "eventId": "lifeId0",
          "relatedEventId": null,
          "eventTimestamp": 0
        }
      },
      {
        "componentName": "componentName",
        "beforeEventIdentifier": "lifeId0",
        "beforeRelatedEventIdentifier": "relatedLifeId0",
        "afterEventIdentifier": "lifeId0",
        "afterRelatedEventIdentifier": "relatedLifeId0",
        "startTimeMillis": 1564929295574,
        "endTimeMillis": 1564929296074,
        "id": 5,
        "flowInvocation": null,
        "metrics": [
          {
            "id": 5,
            "componentInvocationMetricImpl": null,
            "name": "name",
            "value": "value"
          }
        ],
        "wiretapFlowEvent": {
          "identifier": 5,
          "timestamp": 1564929296076,
          "moduleName": "moduleName",
          "flowName": "flowName",
          "componentName": "componentName",
          "event": "payload",
          "expiry": 30,
          "nextByEventId": null,
          "previousByEventId": null,
          "eventId": "lifeId0",
          "relatedEventId": null,
          "eventTimestamp": 0
        }
      }
    ]
  },
  {
    "id": 2,
    "moduleName": "moduleName",
    "flowName": "flowName",
    "invocationStartTime": 1564929295612,
    "invocationEndTime": 1564929296112,
    "finalAction": "ACTION",
    "componentInvocationMetricImpls": [
      {
        "componentName": "componentName",
        "beforeEventIdentifier": "lifeId1",
        "beforeRelatedEventIdentifier": "relatedLifeId1",
        "afterEventIdentifier": "lifeId1",
        "afterRelatedEventIdentifier": "relatedLifeId1",
        "startTimeMillis": 1564929295598,
        "endTimeMillis": 1564929296098,
        "id": 8,
        "flowInvocation": null,
        "metrics": [
          {
            "id": 8,
            "componentInvocationMetricImpl": null,
            "name": "name",
            "value": "value"
          }
        ],
        "wiretapFlowEvent": {
          "identifier": 10,
          "timestamp": 1564929296111,
          "moduleName": "moduleName",
          "flowName": "flowName",
          "componentName": "componentName",
          "event": "payload",
          "expiry": 30,
          "nextByEventId": null,
          "previousByEventId": null,
          "eventId": "lifeId1",
          "relatedEventId": null,
          "eventTimestamp": 0
        }
      },
      {
        "componentName": "componentName",
        "beforeEventIdentifier": "lifeId1",
        "beforeRelatedEventIdentifier": "relatedLifeId1",
        "afterEventIdentifier": "lifeId1",
        "afterRelatedEventIdentifier": "relatedLifeId1",
        "startTimeMillis": 1564929295602,
        "endTimeMillis": 1564929296102,
        "id": 9,
        "flowInvocation": null,
        "metrics": [
          {
            "id": 9,
            "componentInvocationMetricImpl": null,
            "name": "name",
            "value": "value"
          }
        ],
        "wiretapFlowEvent": {
          "identifier": 10,
          "timestamp": 1564929296111,
          "moduleName": "moduleName",
          "flowName": "flowName",
          "componentName": "componentName",
          "event": "payload",
          "expiry": 30,
          "nextByEventId": null,
          "previousByEventId": null,
          "eventId": "lifeId1",
          "relatedEventId": null,
          "eventTimestamp": 0
        }
      },
      {
        "componentName": "componentName",
        "beforeEventIdentifier": "lifeId1",
        "beforeRelatedEventIdentifier": "relatedLifeId1",
        "afterEventIdentifier": "lifeId1",
        "afterRelatedEventIdentifier": "relatedLifeId1",
        "startTimeMillis": 1564929295611,
        "endTimeMillis": 1564929296111,
        "id": 6,
        "flowInvocation": null,
        "metrics": [
          {
            "id": 6,
            "componentInvocationMetricImpl": null,
            "name": "name",
            "value": "value"
          }
        ],
        "wiretapFlowEvent": {
          "identifier": 10,
          "timestamp": 1564929296111,
          "moduleName": "moduleName",
          "flowName": "flowName",
          "componentName": "componentName",
          "event": "payload",
          "expiry": 30,
          "nextByEventId": null,
          "previousByEventId": null,
          "eventId": "lifeId1",
          "relatedEventId": null,
          "eventTimestamp": 0
        }
      },
      {
        "componentName": "componentName",
        "beforeEventIdentifier": "lifeId1",
        "beforeRelatedEventIdentifier": "relatedLifeId1",
        "afterEventIdentifier": "lifeId1",
        "afterRelatedEventIdentifier": "relatedLifeId1",
        "startTimeMillis": 1564929295600,
        "endTimeMillis": 1564929296100,
        "id": 7,
        "flowInvocation": null,
        "metrics": [
          {
            "id": 7,
            "componentInvocationMetricImpl": null,
            "name": "name",
            "value": "value"
          }
        ],
        "wiretapFlowEvent": {
          "identifier": 10,
          "timestamp": 1564929296111,
          "moduleName": "moduleName",
          "flowName": "flowName",
          "componentName": "componentName",
          "event": "payload",
          "expiry": 30,
          "nextByEventId": null,
          "previousByEventId": null,
          "eventId": "lifeId1",
          "relatedEventId": null,
          "eventTimestamp": 0
        }
      },
      {
        "componentName": "componentName",
        "beforeEventIdentifier": "lifeId1",
        "beforeRelatedEventIdentifier": "relatedLifeId1",
        "afterEventIdentifier": "lifeId1",
        "afterRelatedEventIdentifier": "relatedLifeId1",
        "startTimeMillis": 1564929295608,
        "endTimeMillis": 1564929296108,
        "id": 10,
        "flowInvocation": null,
        "metrics": [
          {
            "id": 10,
            "componentInvocationMetricImpl": null,
            "name": "name",
            "value": "value"
          }
        ],
        "wiretapFlowEvent": {
          "identifier": 10,
          "timestamp": 1564929296111,
          "moduleName": "moduleName",
          "flowName": "flowName",
          "componentName": "componentName",
          "event": "payload",
          "expiry": 30,
          "nextByEventId": null,
          "previousByEventId": null,
          "eventId": "lifeId1",
          "relatedEventId": null,
          "eventTimestamp": 0
        }
      }
    ],
    "harvested": true,
    "expiry": 0,
    "errorUri": null,
    "harvestedDateTime": 1564929297546,
    "flowInvocationEvents": [
      {
        "componentName": "componentName",
        "beforeEventIdentifier": "lifeId1",
        "beforeRelatedEventIdentifier": "relatedLifeId1",
        "afterEventIdentifier": "lifeId1",
        "afterRelatedEventIdentifier": "relatedLifeId1",
        "startTimeMillis": 1564929295598,
        "endTimeMillis": 1564929296098,
        "id": 8,
        "flowInvocation": null,
        "metrics": [
          {
            "id": 8,
            "componentInvocationMetricImpl": null,
            "name": "name",
            "value": "value"
          }
        ],
        "wiretapFlowEvent": {
          "identifier": 10,
          "timestamp": 1564929296111,
          "moduleName": "moduleName",
          "flowName": "flowName",
          "componentName": "componentName",
          "event": "payload",
          "expiry": 30,
          "nextByEventId": null,
          "previousByEventId": null,
          "eventId": "lifeId1",
          "relatedEventId": null,
          "eventTimestamp": 0
        }
      },
      {
        "componentName": "componentName",
        "beforeEventIdentifier": "lifeId1",
        "beforeRelatedEventIdentifier": "relatedLifeId1",
        "afterEventIdentifier": "lifeId1",
        "afterRelatedEventIdentifier": "relatedLifeId1",
        "startTimeMillis": 1564929295602,
        "endTimeMillis": 1564929296102,
        "id": 9,
        "flowInvocation": null,
        "metrics": [
          {
            "id": 9,
            "componentInvocationMetricImpl": null,
            "name": "name",
            "value": "value"
          }
        ],
        "wiretapFlowEvent": {
          "identifier": 10,
          "timestamp": 1564929296111,
          "moduleName": "moduleName",
          "flowName": "flowName",
          "componentName": "componentName",
          "event": "payload",
          "expiry": 30,
          "nextByEventId": null,
          "previousByEventId": null,
          "eventId": "lifeId1",
          "relatedEventId": null,
          "eventTimestamp": 0
        }
      },
      {
        "componentName": "componentName",
        "beforeEventIdentifier": "lifeId1",
        "beforeRelatedEventIdentifier": "relatedLifeId1",
        "afterEventIdentifier": "lifeId1",
        "afterRelatedEventIdentifier": "relatedLifeId1",
        "startTimeMillis": 1564929295611,
        "endTimeMillis": 1564929296111,
        "id": 6,
        "flowInvocation": null,
        "metrics": [
          {
            "id": 6,
            "componentInvocationMetricImpl": null,
            "name": "name",
            "value": "value"
          }
        ],
        "wiretapFlowEvent": {
          "identifier": 10,
          "timestamp": 1564929296111,
          "moduleName": "moduleName",
          "flowName": "flowName",
          "componentName": "componentName",
          "event": "payload",
          "expiry": 30,
          "nextByEventId": null,
          "previousByEventId": null,
          "eventId": "lifeId1",
          "relatedEventId": null,
          "eventTimestamp": 0
        }
      },
      {
        "componentName": "componentName",
        "beforeEventIdentifier": "lifeId1",
        "beforeRelatedEventIdentifier": "relatedLifeId1",
        "afterEventIdentifier": "lifeId1",
        "afterRelatedEventIdentifier": "relatedLifeId1",
        "startTimeMillis": 1564929295600,
        "endTimeMillis": 1564929296100,
        "id": 7,
        "flowInvocation": null,
        "metrics": [
          {
            "id": 7,
            "componentInvocationMetricImpl": null,
            "name": "name",
            "value": "value"
          }
        ],
        "wiretapFlowEvent": {
          "identifier": 10,
          "timestamp": 1564929296111,
          "moduleName": "moduleName",
          "flowName": "flowName",
          "componentName": "componentName",
          "event": "payload",
          "expiry": 30,
          "nextByEventId": null,
          "previousByEventId": null,
          "eventId": "lifeId1",
          "relatedEventId": null,
          "eventTimestamp": 0
        }
      },
      {
        "componentName": "componentName",
        "beforeEventIdentifier": "lifeId1",
        "beforeRelatedEventIdentifier": "relatedLifeId1",
        "afterEventIdentifier": "lifeId1",
        "afterRelatedEventIdentifier": "relatedLifeId1",
        "startTimeMillis": 1564929295608,
        "endTimeMillis": 1564929296108,
        "id": 10,
        "flowInvocation": null,
        "metrics": [
          {
            "id": 10,
            "componentInvocationMetricImpl": null,
            "name": "name",
            "value": "value"
          }
        ],
        "wiretapFlowEvent": {
          "identifier": 10,
          "timestamp": 1564929296111,
          "moduleName": "moduleName",
          "flowName": "flowName",
          "componentName": "componentName",
          "event": "payload",
          "expiry": 30,
          "nextByEventId": null,
          "previousByEventId": null,
          "eventId": "lifeId1",
          "relatedEventId": null,
          "eventTimestamp": 0
        }
      }
    ]
  },
  {
    "id": 3,
    "moduleName": "moduleName",
    "flowName": "flowName",
    "invocationStartTime": 1564929295628,
    "invocationEndTime": 1564929296128,
    "finalAction": "ACTION",
    "componentInvocationMetricImpls": [
      {
        "componentName": "componentName",
        "beforeEventIdentifier": "lifeId2",
        "beforeRelatedEventIdentifier": "relatedLifeId2",
        "afterEventIdentifier": "lifeId2",
        "afterRelatedEventIdentifier": "relatedLifeId2",
        "startTimeMillis": 1564929295621,
        "endTimeMillis": 1564929296121,
        "id": 11,
        "flowInvocation": null,
        "metrics": [
          {
            "id": 11,
            "componentInvocationMetricImpl": null,
            "name": "name",
            "value": "value"
          }
        ],
        "wiretapFlowEvent": {
          "identifier": 15,
          "timestamp": 1564929296127,
          "moduleName": "moduleName",
          "flowName": "flowName",
          "componentName": "componentName",
          "event": "payload",
          "expiry": 30,
          "nextByEventId": null,
          "previousByEventId": null,
          "eventId": "lifeId2",
          "relatedEventId": null,
          "eventTimestamp": 0
        }
      },
      {
        "componentName": "componentName",
        "beforeEventIdentifier": "lifeId2",
        "beforeRelatedEventIdentifier": "relatedLifeId2",
        "afterEventIdentifier": "lifeId2",
        "afterRelatedEventIdentifier": "relatedLifeId2",
        "startTimeMillis": 1564929295625,
        "endTimeMillis": 1564929296125,
        "id": 13,
        "flowInvocation": null,
        "metrics": [
          {
            "id": 13,
            "componentInvocationMetricImpl": null,
            "name": "name",
            "value": "value"
          }
        ],
        "wiretapFlowEvent": {
          "identifier": 15,
          "timestamp": 1564929296127,
          "moduleName": "moduleName",
          "flowName": "flowName",
          "componentName": "componentName",
          "event": "payload",
          "expiry": 30,
          "nextByEventId": null,
          "previousByEventId": null,
          "eventId": "lifeId2",
          "relatedEventId": null,
          "eventTimestamp": 0
        }
      },
      {
        "componentName": "componentName",
        "beforeEventIdentifier": "lifeId2",
        "beforeRelatedEventIdentifier": "relatedLifeId2",
        "afterEventIdentifier": "lifeId2",
        "afterRelatedEventIdentifier": "relatedLifeId2",
        "startTimeMillis": 1564929295623,
        "endTimeMillis": 1564929296123,
        "id": 15,
        "flowInvocation": null,
        "metrics": [
          {
            "id": 15,
            "componentInvocationMetricImpl": null,
            "name": "name",
            "value": "value"
          }
        ],
        "wiretapFlowEvent": {
          "identifier": 15,
          "timestamp": 1564929296127,
          "moduleName": "moduleName",
          "flowName": "flowName",
          "componentName": "componentName",
          "event": "payload",
          "expiry": 30,
          "nextByEventId": null,
          "previousByEventId": null,
          "eventId": "lifeId2",
          "relatedEventId": null,
          "eventTimestamp": 0
        }
      },
      {
        "componentName": "componentName",
        "beforeEventIdentifier": "lifeId2",
        "beforeRelatedEventIdentifier": "relatedLifeId2",
        "afterEventIdentifier": "lifeId2",
        "afterRelatedEventIdentifier": "relatedLifeId2",
        "startTimeMillis": 1564929295619,
        "endTimeMillis": 1564929296119,
        "id": 14,
        "flowInvocation": null,
        "metrics": [
          {
            "id": 14,
            "componentInvocationMetricImpl": null,
            "name": "name",
            "value": "value"
          }
        ],
        "wiretapFlowEvent": {
          "identifier": 15,
          "timestamp": 1564929296127,
          "moduleName": "moduleName",
          "flowName": "flowName",
          "componentName": "componentName",
          "event": "payload",
          "expiry": 30,
          "nextByEventId": null,
          "previousByEventId": null,
          "eventId": "lifeId2",
          "relatedEventId": null,
          "eventTimestamp": 0
        }
      },
      {
        "componentName": "componentName",
        "beforeEventIdentifier": "lifeId2",
        "beforeRelatedEventIdentifier": "relatedLifeId2",
        "afterEventIdentifier": "lifeId2",
        "afterRelatedEventIdentifier": "relatedLifeId2",
        "startTimeMillis": 1564929295627,
        "endTimeMillis": 1564929296127,
        "id": 12,
        "flowInvocation": null,
        "metrics": [
          {
            "id": 12,
            "componentInvocationMetricImpl": null,
            "name": "name",
            "value": "value"
          }
        ],
        "wiretapFlowEvent": {
          "identifier": 15,
          "timestamp": 1564929296127,
          "moduleName": "moduleName",
          "flowName": "flowName",
          "componentName": "componentName",
          "event": "payload",
          "expiry": 30,
          "nextByEventId": null,
          "previousByEventId": null,
          "eventId": "lifeId2",
          "relatedEventId": null,
          "eventTimestamp": 0
        }
      }
    ],
    "harvested": true,
    "expiry": 0,
    "errorUri": null,
    "harvestedDateTime": 1564929297548,
    "flowInvocationEvents": [
      {
        "componentName": "componentName",
        "beforeEventIdentifier": "lifeId2",
        "beforeRelatedEventIdentifier": "relatedLifeId2",
        "afterEventIdentifier": "lifeId2",
        "afterRelatedEventIdentifier": "relatedLifeId2",
        "startTimeMillis": 1564929295621,
        "endTimeMillis": 1564929296121,
        "id": 11,
        "flowInvocation": null,
        "metrics": [
          {
            "id": 11,
            "componentInvocationMetricImpl": null,
            "name": "name",
            "value": "value"
          }
        ],
        "wiretapFlowEvent": {
          "identifier": 15,
          "timestamp": 1564929296127,
          "moduleName": "moduleName",
          "flowName": "flowName",
          "componentName": "componentName",
          "event": "payload",
          "expiry": 30,
          "nextByEventId": null,
          "previousByEventId": null,
          "eventId": "lifeId2",
          "relatedEventId": null,
          "eventTimestamp": 0
        }
      },
      {
        "componentName": "componentName",
        "beforeEventIdentifier": "lifeId2",
        "beforeRelatedEventIdentifier": "relatedLifeId2",
        "afterEventIdentifier": "lifeId2",
        "afterRelatedEventIdentifier": "relatedLifeId2",
        "startTimeMillis": 1564929295625,
        "endTimeMillis": 1564929296125,
        "id": 13,
        "flowInvocation": null,
        "metrics": [
          {
            "id": 13,
            "componentInvocationMetricImpl": null,
            "name": "name",
            "value": "value"
          }
        ],
        "wiretapFlowEvent": {
          "identifier": 15,
          "timestamp": 1564929296127,
          "moduleName": "moduleName",
          "flowName": "flowName",
          "componentName": "componentName",
          "event": "payload",
          "expiry": 30,
          "nextByEventId": null,
          "previousByEventId": null,
          "eventId": "lifeId2",
          "relatedEventId": null,
          "eventTimestamp": 0
        }
      },
      {
        "componentName": "componentName",
        "beforeEventIdentifier": "lifeId2",
        "beforeRelatedEventIdentifier": "relatedLifeId2",
        "afterEventIdentifier": "lifeId2",
        "afterRelatedEventIdentifier": "relatedLifeId2",
        "startTimeMillis": 1564929295623,
        "endTimeMillis": 1564929296123,
        "id": 15,
        "flowInvocation": null,
        "metrics": [
          {
            "id": 15,
            "componentInvocationMetricImpl": null,
            "name": "name",
            "value": "value"
          }
        ],
        "wiretapFlowEvent": {
          "identifier": 15,
          "timestamp": 1564929296127,
          "moduleName": "moduleName",
          "flowName": "flowName",
          "componentName": "componentName",
          "event": "payload",
          "expiry": 30,
          "nextByEventId": null,
          "previousByEventId": null,
          "eventId": "lifeId2",
          "relatedEventId": null,
          "eventTimestamp": 0
        }
      },
      {
        "componentName": "componentName",
        "beforeEventIdentifier": "lifeId2",
        "beforeRelatedEventIdentifier": "relatedLifeId2",
        "afterEventIdentifier": "lifeId2",
        "afterRelatedEventIdentifier": "relatedLifeId2",
        "startTimeMillis": 1564929295619,
        "endTimeMillis": 1564929296119,
        "id": 14,
        "flowInvocation": null,
        "metrics": [
          {
            "id": 14,
            "componentInvocationMetricImpl": null,
            "name": "name",
            "value": "value"
          }
        ],
        "wiretapFlowEvent": {
          "identifier": 15,
          "timestamp": 1564929296127,
          "moduleName": "moduleName",
          "flowName": "flowName",
          "componentName": "componentName",
          "event": "payload",
          "expiry": 30,
          "nextByEventId": null,
          "previousByEventId": null,
          "eventId": "lifeId2",
          "relatedEventId": null,
          "eventTimestamp": 0
        }
      },
      {
        "componentName": "componentName",
        "beforeEventIdentifier": "lifeId2",
        "beforeRelatedEventIdentifier": "relatedLifeId2",
        "afterEventIdentifier": "lifeId2",
        "afterRelatedEventIdentifier": "relatedLifeId2",
        "startTimeMillis": 1564929295627,
        "endTimeMillis": 1564929296127,
        "id": 12,
        "flowInvocation": null,
        "metrics": [
          {
            "id": 12,
            "componentInvocationMetricImpl": null,
            "name": "name",
            "value": "value"
          }
        ],
        "wiretapFlowEvent": {
          "identifier": 15,
          "timestamp": 1564929296127,
          "moduleName": "moduleName",
          "flowName": "flowName",
          "componentName": "componentName",
          "event": "payload",
          "expiry": 30,
          "nextByEventId": null,
          "previousByEventId": null,
          "eventId": "lifeId2",
          "relatedEventId": null,
          "eventTimestamp": 0
        }
      }
    ]
  }
]
````
 
</p>
</details>

## Replay Events Harvesting Service
Aggregation service for replay events produced by the Ikasan Replay service.

| Parameter                            | Value                                                                                                                    | 
|--------------------------------------|--------------------------------------------------------------------------------------------------------------------------|
| Request Method                       | PUT                                                                                                                      |
| Service Context                      | {dashboard-root-context}/rest/harvest/replay                                                                             |
| Requires 'Authorization' HTTP Header | Bearer {JWT TOKEN}                                                                                                       |
| Payload                              | A json serialised List of [ReplayEvent](../../spec/service/replay/src/main/java/org/ikasan/spec/replay/ReplayEvent.java) |


<details>
    <summary>Click to view the sample JSON payload expected by the service.</summary>
<p>

````json
[ {
  "id" : 1,
  "moduleName" : "moduleName",
  "flowName" : "flowName",
  "eventId" : "errorUri",
  "event" : "ZXZlbnQ=",
  "eventAsString" : "event",
  "timestamp" : 1564931198117,
  "expiry" : 1567523198117,
  "harvested" : false,
  "harvestedDateTime" : 0
}, {
  "id" : 2,
  "moduleName" : "moduleName",
  "flowName" : "flowName",
  "eventId" : "errorUri",
  "event" : "ZXZlbnQ=",
  "eventAsString" : "event",
  "timestamp" : 1564931198248,
  "expiry" : 1567523198248,
  "harvested" : false,
  "harvestedDateTime" : 0
}, {
  "id" : 3,
  "moduleName" : "moduleName",
  "flowName" : "flowName",
  "eventId" : "errorUri",
  "event" : "ZXZlbnQ=",
  "eventAsString" : "event",
  "timestamp" : 1564931198253,
  "expiry" : 1567523198253,
  "harvested" : false,
  "harvestedDateTime" : 0
}]
````
 
</p>
</details>

## Wiretap Events Harvesting Service
Aggregation service for wiretap events produced by the Ikasan Wiretap service.

| Parameter       | Value                                                                                                                        | 
|-----------------|------------------------------------------------------------------------------------------------------------------------------|
| Request Method  | PUT                                                                                                                          |
| Service Context | {dashboard-root-context}/rest/harvest/wiretaps                                                                               |
| Payload         | A json serialised List of [WiretapEvent](../../spec/service/wiretap/src/main/java/org/ikasan/spec/wiretap/WiretapEvent.java) |

<details>
    <summary>Click to view the sample JSON payload expected by the service.</summary>
<p>

````json
[
  {
    "moduleName": "My Module Name",
    "flowName": "My Flow Name",
    "event": "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><note><to>Tove</to><from>Jani</from><heading>Reminder</heading><body>Don't forget me this weekend!</body></note>",
    "componentName": "My Component Name",
    "expiry": 1234567,
    "eventId": "event identifier",
    "identifier": 678910,
    "timestamp": 99999999999999
  },
  {
    "moduleName": "My Module Name",
    "flowName": "My Flow Name",
    "event": "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><note><to>Tove</to><from>Jani</from><heading>Reminder</heading><body>Don't forget me this weekend!</body></note>",
    "componentName": "My Component Name",
    "expiry": 1234567,
    "eventId": "event identifier",
    "identifier": 678910,
    "timestamp": 99999999999999
  },
  {
    "moduleName": "My Module Name",
    "flowName": "My Flow Name",
    "event": "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><note><to>Tove</to><from>Jani</from><heading>Reminder</heading><body>Don't forget me this weekend!</body></note>",
    "componentName": "My Component Name",
    "expiry": 1234567,
    "eventId": "event identifier",
    "identifier": 678910,
    "timestamp": 99999999999999
  }
]
````
 
</p>
</details>

## Metadata Service
Aggregation service for module metadata produced by the Ikasan Topology service.

| Parameter                            | Value                                                                                                              | 
|--------------------------------------|--------------------------------------------------------------------------------------------------------------------|
| Request Method                       | PUT                                                                                                                |
| Service Context                      | {dashboard-root-context}/rest/module/metadata                                                                      |
| Requires 'Authorization' HTTP Header | Bearer {JWT TOKEN}                                                                                                 |
| Payload                              | A json serialised [ModuleMetaData](../../spec/metadata/src/main/java/org/ikasan/spec/metadata/ModuleMetaData.java) |


<details>
    <summary>Click to view the sample JSON payload expected by the service.</summary>
<p>

````json
{
  "name" : "module name",
  "description" : "module description",
  "version" : "module version",
  "flows" : [ {
    "name" : "Simple Flow 1",
    "consumer" : {
      "componentName" : "Test Consumer",
      "description" : "Test Consumer Description",
      "componentType" : "org.ikasan.spec.component.endpoint.Consumer",
      "implementingClass" : "org.ikasan.metadata.components.TestConsumer",
      "configurationId" : null,
      "invokerConfigurationId" : "FLOW_INVOKER_CONFIGURATION_ID",
      "configurable" : false
    },
    "transitions" : [ {
      "from" : "Test Converter",
      "to" : "Test Producer",
      "name" : "default"
    }, {
      "from" : "Test Broker",
      "to" : "Test Converter",
      "name" : "default"
    }, {
      "from" : "Test Splitter",
      "to" : "Test Broker",
      "name" : "default"
    }, {
      "from" : "Test Filter",
      "to" : "Test Splitter",
      "name" : "default"
    }, {
      "from" : "Test Consumer",
      "to" : "Test Filter",
      "name" : "default"
    } ],
    "flowElements" : [ {
      "componentName" : "Test Producer",
      "description" : "Test Producer Description",
      "componentType" : "org.ikasan.spec.component.endpoint.Producer",
      "implementingClass" : "org.ikasan.metadata.components.TestProducer",
      "configurationId" : null,
      "invokerConfigurationId" : "FLOW_INVOKER_CONFIGURATION_ID",
      "configurable" : false
    }, {
      "componentName" : "Test Converter",
      "description" : "Test Converter Description",
      "componentType" : "org.ikasan.spec.component.transformation.Converter",
      "implementingClass" : "org.ikasan.metadata.components.TestConverter",
      "configurationId" : null,
      "invokerConfigurationId" : "FLOW_INVOKER_CONFIGURATION_ID",
      "configurable" : false
    }, {
      "componentName" : "Test Broker",
      "description" : "Test Broker Description",
      "componentType" : "org.ikasan.spec.component.endpoint.Broker",
      "implementingClass" : "org.ikasan.metadata.components.TestBroker",
      "configurationId" : null,
      "invokerConfigurationId" : "FLOW_INVOKER_CONFIGURATION_ID",
      "configurable" : false
    }, {
      "componentName" : "Test Splitter",
      "description" : "Test Splitter Description",
      "componentType" : "org.ikasan.spec.component.splitting.Splitter",
      "implementingClass" : "org.ikasan.metadata.components.TestSplitter",
      "configurationId" : null,
      "invokerConfigurationId" : "FLOW_INVOKER_CONFIGURATION_ID",
      "configurable" : false
    }, {
      "componentName" : "Test Filter",
      "description" : "Test Filter Description",
      "componentType" : "org.ikasan.spec.component.filter.Filter",
      "implementingClass" : "org.ikasan.metadata.components.TestFilter",
      "configurationId" : null,
      "invokerConfigurationId" : "FLOW_INVOKER_CONFIGURATION_ID",
      "configurable" : false
    }, {
      "componentName" : "Test Consumer",
      "description" : "Test Consumer Description",
      "componentType" : "org.ikasan.spec.component.endpoint.Consumer",
      "implementingClass" : "org.ikasan.metadata.components.TestConsumer",
      "configurationId" : null,
      "invokerConfigurationId" : "FLOW_INVOKER_CONFIGURATION_ID",
      "configurable" : false
    } ],
    "configurationId" : "FLOW_CONFIGURATION_ID"
  }, {
    "name" : "Simple Flow 2",
    "consumer" : {
      "componentName" : "Test Consumer",
      "description" : "Test Consumer Description",
      "componentType" : "org.ikasan.spec.component.endpoint.Consumer",
      "implementingClass" : "org.ikasan.metadata.components.TestConsumer",
      "configurationId" : null,
      "invokerConfigurationId" : "FLOW_INVOKER_CONFIGURATION_ID",
      "configurable" : false
    },
    "transitions" : [ {
      "from" : "Test Converter",
      "to" : "Test Producer",
      "name" : "default"
    }, {
      "from" : "Test Broker",
      "to" : "Test Converter",
      "name" : "default"
    }, {
      "from" : "Test Splitter",
      "to" : "Test Broker",
      "name" : "default"
    }, {
      "from" : "Test Filter",
      "to" : "Test Splitter",
      "name" : "default"
    }, {
      "from" : "Test Consumer",
      "to" : "Test Filter",
      "name" : "default"
    } ],
    "flowElements" : [ {
      "componentName" : "Test Producer",
      "description" : "Test Producer Description",
      "componentType" : "org.ikasan.spec.component.endpoint.Producer",
      "implementingClass" : "org.ikasan.metadata.components.TestProducer",
      "configurationId" : null,
      "invokerConfigurationId" : "FLOW_INVOKER_CONFIGURATION_ID",
      "configurable" : false
    }, {
      "componentName" : "Test Converter",
      "description" : "Test Converter Description",
      "componentType" : "org.ikasan.spec.component.transformation.Converter",
      "implementingClass" : "org.ikasan.metadata.components.TestConverter",
      "configurationId" : null,
      "invokerConfigurationId" : "FLOW_INVOKER_CONFIGURATION_ID",
      "configurable" : false
    }, {
      "componentName" : "Test Broker",
      "description" : "Test Broker Description",
      "componentType" : "org.ikasan.spec.component.endpoint.Broker",
      "implementingClass" : "org.ikasan.metadata.components.TestBroker",
      "configurationId" : null,
      "invokerConfigurationId" : "FLOW_INVOKER_CONFIGURATION_ID",
      "configurable" : false
    }, {
      "componentName" : "Test Splitter",
      "description" : "Test Splitter Description",
      "componentType" : "org.ikasan.spec.component.splitting.Splitter",
      "implementingClass" : "org.ikasan.metadata.components.TestSplitter",
      "configurationId" : null,
      "invokerConfigurationId" : "FLOW_INVOKER_CONFIGURATION_ID",
      "configurable" : false
    }, {
      "componentName" : "Test Filter",
      "description" : "Test Filter Description",
      "componentType" : "org.ikasan.spec.component.filter.Filter",
      "implementingClass" : "org.ikasan.metadata.components.TestFilter",
      "configurationId" : null,
      "invokerConfigurationId" : "FLOW_INVOKER_CONFIGURATION_ID",
      "configurable" : false
    }, {
      "componentName" : "Test Consumer",
      "description" : "Test Consumer Description",
      "componentType" : "org.ikasan.spec.component.endpoint.Consumer",
      "implementingClass" : "org.ikasan.metadata.components.TestConsumer",
      "configurationId" : null,
      "invokerConfigurationId" : "FLOW_INVOKER_CONFIGURATION_ID",
      "configurable" : false
    } ],
    "configurationId" : "FLOW_CONFIGURATION_ID"
  }
  ]
}
````
 
</p>
</details>

## Configuration Service
Aggregation service for configuration metadata produced by the Ikasan Topology service.

| Parameter                            | Value                                                                       | 
|--------------------------------------|-----------------------------------------------------------------------------|
| Request Method                       | PUT                                                                         |
| Service Context                      | {dashboard-root-context}/rest/module/configuration                          |
| Requires 'Authorization' HTTP Header | Bearer {JWT TOKEN}                                                          |
| Payload                              | See [Configuration Service metadata](../../configuration-service/Readme.md) |


<details>
    <summary>Click to view the sample JSON payload expected by the service.</summary>
<p>

````json
[ {
  "configurationId" : "consumerConfiguredResourceId",
  "description" : "desc",
  "implementingClass" : "org.ikasan.configurationService.model.DefaultConfiguration",
  "parameters" : [ {
    "id" : null,
    "name" : "name",
    "value" : "value",
    "description" : "desc",
    "implementingClass": "org.ikasan.configurationService.model.ConfigurationParameterStringImpl"
  }, {
    "id" : null,
    "name" : "name",
    "value" : 10,
    "description" : "desc",
    "implementingClass": "org.ikasan.configurationService.model.ConfigurationParameterIntegerImpl"
  }, {
    "id" : null,
    "name" : "name",
    "value" : 10,
    "description" : "desc",
    "implementingClass": "org.ikasan.configurationService.model.ConfigurationParameterLongImpl"
  }, {
    "id" : null,
    "name" : "name",
    "value" : [ "one", "two", "three" ],
    "description" : "desc",
    "implementingClass": "org.ikasan.configurationService.model.ConfigurationParameterListImpl"
  }, {
    "id" : null,
    "name" : "name",
    "value" : {
      "one" : "1",
      "two" : "2",
      "three" : "3"
    },
    "description" : "desc",
    "implementingClass": "org.ikasan.configurationService.model.ConfigurationParameterMapImpl"
  } ]
},
  {
    "configurationId" : "producerConfiguredResourceId",
    "description" : "desc",
    "implementingClass" : "org.ikasan.configurationService.model.DefaultConfiguration",
    "parameters" : [ {
      "id" : null,
      "name" : "name",
      "value" : "value",
      "description" : "desc",
      "implementingClass": "org.ikasan.configurationService.model.ConfigurationParameterStringImpl"
    }, {
      "id" : null,
      "name" : "name",
      "value" : 10,
      "description" : "desc",
      "implementingClass": "org.ikasan.configurationService.model.ConfigurationParameterIntegerImpl"
    }, {
      "id" : null,
      "name" : "name",
      "value" : 10,
      "description" : "desc",
      "implementingClass": "org.ikasan.configurationService.model.ConfigurationParameterLongImpl"
    }, {
      "id" : null,
      "name" : "name",
      "value" : [ "one", "two", "three" ],
      "description" : "desc",
      "implementingClass": "org.ikasan.configurationService.model.ConfigurationParameterListImpl"
    }, {
      "id" : null,
      "name" : "name",
      "value" : {
        "one" : "1",
        "two" : "2",
        "three" : "3"
      },
      "description" : "desc",
      "implementingClass": "org.ikasan.configurationService.model.ConfigurationParameterMapImpl"
    } ]
  }]
````
 
</p>
</details>

## Metrics Retrieval Services

All metrics retrieval services return a json serialised List of [FlowInvocationMetric](../../spec/service/history/src/main/java/org/ikasan/spec/history/FlowInvocationMetric.java).

All services will return the results based on the filtering criteria provided in the REST GET request url parameters.

<details>
    <summary>Click to view the sample JSON payload returned by the services.</summary>
<p>

````json
[
  {
    "id": 1,
    "moduleName": "moduleName",
    "flowName": "flowName",
    "invocationStartTime": 1564929295578,
    "invocationEndTime": 1564929296078,
    "finalAction": "ACTION",
    "componentInvocationMetricImpls": [
      {
        "componentName": "componentName",
        "beforeEventIdentifier": "lifeId0",
        "beforeRelatedEventIdentifier": "relatedLifeId0",
        "afterEventIdentifier": "lifeId0",
        "afterRelatedEventIdentifier": "relatedLifeId0",
        "startTimeMillis": 1564929295576,
        "endTimeMillis": 1564929296076,
        "id": 3,
        "flowInvocation": null,
        "metrics": [
          {
            "id": 3,
            "componentInvocationMetricImpl": null,
            "name": "name",
            "value": "value"
          }
        ],
        "wiretapFlowEvent": {
          "identifier": 5,
          "timestamp": 1564929296076,
          "moduleName": "moduleName",
          "flowName": "flowName",
          "componentName": "componentName",
          "event": "payload",
          "expiry": 30,
          "nextByEventId": null,
          "previousByEventId": null,
          "eventId": "lifeId0",
          "relatedEventId": null,
          "eventTimestamp": 0
        }
      },
      {
        "componentName": "componentName",
        "beforeEventIdentifier": "lifeId0",
        "beforeRelatedEventIdentifier": "relatedLifeId0",
        "afterEventIdentifier": "lifeId0",
        "afterRelatedEventIdentifier": "relatedLifeId0",
        "startTimeMillis": 1564929295570,
        "endTimeMillis": 1564929296070,
        "id": 2,
        "flowInvocation": null,
        "metrics": [
          {
            "id": 2,
            "componentInvocationMetricImpl": null,
            "name": "name",
            "value": "value"
          }
        ],
        "wiretapFlowEvent": {
          "identifier": 5,
          "timestamp": 1564929296076,
          "moduleName": "moduleName",
          "flowName": "flowName",
          "componentName": "componentName",
          "event": "payload",
          "expiry": 30,
          "nextByEventId": null,
          "previousByEventId": null,
          "eventId": "lifeId0",
          "relatedEventId": null,
          "eventTimestamp": 0
        }
      },
      {
        "componentName": "componentName",
        "beforeEventIdentifier": "lifeId0",
        "beforeRelatedEventIdentifier": "relatedLifeId0",
        "afterEventIdentifier": "lifeId0",
        "afterRelatedEventIdentifier": "relatedLifeId0",
        "startTimeMillis": 1564929295400,
        "endTimeMillis": 1564929295900,
        "id": 1,
        "flowInvocation": null,
        "metrics": [
          {
            "id": 1,
            "componentInvocationMetricImpl": null,
            "name": "name",
            "value": "value"
          }
        ],
        "wiretapFlowEvent": {
          "identifier": 5,
          "timestamp": 1564929296076,
          "moduleName": "moduleName",
          "flowName": "flowName",
          "componentName": "componentName",
          "event": "payload",
          "expiry": 30,
          "nextByEventId": null,
          "previousByEventId": null,
          "eventId": "lifeId0",
          "relatedEventId": null,
          "eventTimestamp": 0
        }
      },
      {
        "componentName": "componentName",
        "beforeEventIdentifier": "lifeId0",
        "beforeRelatedEventIdentifier": "relatedLifeId0",
        "afterEventIdentifier": "lifeId0",
        "afterRelatedEventIdentifier": "relatedLifeId0",
        "startTimeMillis": 1564929295572,
        "endTimeMillis": 1564929296072,
        "id": 4,
        "flowInvocation": null,
        "metrics": [
          {
            "id": 4,
            "componentInvocationMetricImpl": null,
            "name": "name",
            "value": "value"
          }
        ],
        "wiretapFlowEvent": {
          "identifier": 5,
          "timestamp": 1564929296076,
          "moduleName": "moduleName",
          "flowName": "flowName",
          "componentName": "componentName",
          "event": "payload",
          "expiry": 30,
          "nextByEventId": null,
          "previousByEventId": null,
          "eventId": "lifeId0",
          "relatedEventId": null,
          "eventTimestamp": 0
        }
      },
      {
        "componentName": "componentName",
        "beforeEventIdentifier": "lifeId0",
        "beforeRelatedEventIdentifier": "relatedLifeId0",
        "afterEventIdentifier": "lifeId0",
        "afterRelatedEventIdentifier": "relatedLifeId0",
        "startTimeMillis": 1564929295574,
        "endTimeMillis": 1564929296074,
        "id": 5,
        "flowInvocation": null,
        "metrics": [
          {
            "id": 5,
            "componentInvocationMetricImpl": null,
            "name": "name",
            "value": "value"
          }
        ],
        "wiretapFlowEvent": {
          "identifier": 5,
          "timestamp": 1564929296076,
          "moduleName": "moduleName",
          "flowName": "flowName",
          "componentName": "componentName",
          "event": "payload",
          "expiry": 30,
          "nextByEventId": null,
          "previousByEventId": null,
          "eventId": "lifeId0",
          "relatedEventId": null,
          "eventTimestamp": 0
        }
      }
    ],
    "harvested": true,
    "expiry": 0,
    "errorUri": null,
    "harvestedDateTime": 1564929297534,
    "flowInvocationEvents": [
      {
        "componentName": "componentName",
        "beforeEventIdentifier": "lifeId0",
        "beforeRelatedEventIdentifier": "relatedLifeId0",
        "afterEventIdentifier": "lifeId0",
        "afterRelatedEventIdentifier": "relatedLifeId0",
        "startTimeMillis": 1564929295576,
        "endTimeMillis": 1564929296076,
        "id": 3,
        "flowInvocation": null,
        "metrics": [
          {
            "id": 3,
            "componentInvocationMetricImpl": null,
            "name": "name",
            "value": "value"
          }
        ],
        "wiretapFlowEvent": {
          "identifier": 5,
          "timestamp": 1564929296076,
          "moduleName": "moduleName",
          "flowName": "flowName",
          "componentName": "componentName",
          "event": "payload",
          "expiry": 30,
          "nextByEventId": null,
          "previousByEventId": null,
          "eventId": "lifeId0",
          "relatedEventId": null,
          "eventTimestamp": 0
        }
      },
      {
        "componentName": "componentName",
        "beforeEventIdentifier": "lifeId0",
        "beforeRelatedEventIdentifier": "relatedLifeId0",
        "afterEventIdentifier": "lifeId0",
        "afterRelatedEventIdentifier": "relatedLifeId0",
        "startTimeMillis": 1564929295570,
        "endTimeMillis": 1564929296070,
        "id": 2,
        "flowInvocation": null,
        "metrics": [
          {
            "id": 2,
            "componentInvocationMetricImpl": null,
            "name": "name",
            "value": "value"
          }
        ],
        "wiretapFlowEvent": {
          "identifier": 5,
          "timestamp": 1564929296076,
          "moduleName": "moduleName",
          "flowName": "flowName",
          "componentName": "componentName",
          "event": "payload",
          "expiry": 30,
          "nextByEventId": null,
          "previousByEventId": null,
          "eventId": "lifeId0",
          "relatedEventId": null,
          "eventTimestamp": 0
        }
      },
      {
        "componentName": "componentName",
        "beforeEventIdentifier": "lifeId0",
        "beforeRelatedEventIdentifier": "relatedLifeId0",
        "afterEventIdentifier": "lifeId0",
        "afterRelatedEventIdentifier": "relatedLifeId0",
        "startTimeMillis": 1564929295400,
        "endTimeMillis": 1564929295900,
        "id": 1,
        "flowInvocation": null,
        "metrics": [
          {
            "id": 1,
            "componentInvocationMetricImpl": null,
            "name": "name",
            "value": "value"
          }
        ],
        "wiretapFlowEvent": {
          "identifier": 5,
          "timestamp": 1564929296076,
          "moduleName": "moduleName",
          "flowName": "flowName",
          "componentName": "componentName",
          "event": "payload",
          "expiry": 30,
          "nextByEventId": null,
          "previousByEventId": null,
          "eventId": "lifeId0",
          "relatedEventId": null,
          "eventTimestamp": 0
        }
      },
      {
        "componentName": "componentName",
        "beforeEventIdentifier": "lifeId0",
        "beforeRelatedEventIdentifier": "relatedLifeId0",
        "afterEventIdentifier": "lifeId0",
        "afterRelatedEventIdentifier": "relatedLifeId0",
        "startTimeMillis": 1564929295572,
        "endTimeMillis": 1564929296072,
        "id": 4,
        "flowInvocation": null,
        "metrics": [
          {
            "id": 4,
            "componentInvocationMetricImpl": null,
            "name": "name",
            "value": "value"
          }
        ],
        "wiretapFlowEvent": {
          "identifier": 5,
          "timestamp": 1564929296076,
          "moduleName": "moduleName",
          "flowName": "flowName",
          "componentName": "componentName",
          "event": "payload",
          "expiry": 30,
          "nextByEventId": null,
          "previousByEventId": null,
          "eventId": "lifeId0",
          "relatedEventId": null,
          "eventTimestamp": 0
        }
      },
      {
        "componentName": "componentName",
        "beforeEventIdentifier": "lifeId0",
        "beforeRelatedEventIdentifier": "relatedLifeId0",
        "afterEventIdentifier": "lifeId0",
        "afterRelatedEventIdentifier": "relatedLifeId0",
        "startTimeMillis": 1564929295574,
        "endTimeMillis": 1564929296074,
        "id": 5,
        "flowInvocation": null,
        "metrics": [
          {
            "id": 5,
            "componentInvocationMetricImpl": null,
            "name": "name",
            "value": "value"
          }
        ],
        "wiretapFlowEvent": {
          "identifier": 5,
          "timestamp": 1564929296076,
          "moduleName": "moduleName",
          "flowName": "flowName",
          "componentName": "componentName",
          "event": "payload",
          "expiry": 30,
          "nextByEventId": null,
          "previousByEventId": null,
          "eventId": "lifeId0",
          "relatedEventId": null,
          "eventTimestamp": 0
        }
      }
    ]
  },
  {
    "id": 2,
    "moduleName": "moduleName",
    "flowName": "flowName",
    "invocationStartTime": 1564929295612,
    "invocationEndTime": 1564929296112,
    "finalAction": "ACTION",
    "componentInvocationMetricImpls": [
      {
        "componentName": "componentName",
        "beforeEventIdentifier": "lifeId1",
        "beforeRelatedEventIdentifier": "relatedLifeId1",
        "afterEventIdentifier": "lifeId1",
        "afterRelatedEventIdentifier": "relatedLifeId1",
        "startTimeMillis": 1564929295598,
        "endTimeMillis": 1564929296098,
        "id": 8,
        "flowInvocation": null,
        "metrics": [
          {
            "id": 8,
            "componentInvocationMetricImpl": null,
            "name": "name",
            "value": "value"
          }
        ],
        "wiretapFlowEvent": {
          "identifier": 10,
          "timestamp": 1564929296111,
          "moduleName": "moduleName",
          "flowName": "flowName",
          "componentName": "componentName",
          "event": "payload",
          "expiry": 30,
          "nextByEventId": null,
          "previousByEventId": null,
          "eventId": "lifeId1",
          "relatedEventId": null,
          "eventTimestamp": 0
        }
      },
      {
        "componentName": "componentName",
        "beforeEventIdentifier": "lifeId1",
        "beforeRelatedEventIdentifier": "relatedLifeId1",
        "afterEventIdentifier": "lifeId1",
        "afterRelatedEventIdentifier": "relatedLifeId1",
        "startTimeMillis": 1564929295602,
        "endTimeMillis": 1564929296102,
        "id": 9,
        "flowInvocation": null,
        "metrics": [
          {
            "id": 9,
            "componentInvocationMetricImpl": null,
            "name": "name",
            "value": "value"
          }
        ],
        "wiretapFlowEvent": {
          "identifier": 10,
          "timestamp": 1564929296111,
          "moduleName": "moduleName",
          "flowName": "flowName",
          "componentName": "componentName",
          "event": "payload",
          "expiry": 30,
          "nextByEventId": null,
          "previousByEventId": null,
          "eventId": "lifeId1",
          "relatedEventId": null,
          "eventTimestamp": 0
        }
      },
      {
        "componentName": "componentName",
        "beforeEventIdentifier": "lifeId1",
        "beforeRelatedEventIdentifier": "relatedLifeId1",
        "afterEventIdentifier": "lifeId1",
        "afterRelatedEventIdentifier": "relatedLifeId1",
        "startTimeMillis": 1564929295611,
        "endTimeMillis": 1564929296111,
        "id": 6,
        "flowInvocation": null,
        "metrics": [
          {
            "id": 6,
            "componentInvocationMetricImpl": null,
            "name": "name",
            "value": "value"
          }
        ],
        "wiretapFlowEvent": {
          "identifier": 10,
          "timestamp": 1564929296111,
          "moduleName": "moduleName",
          "flowName": "flowName",
          "componentName": "componentName",
          "event": "payload",
          "expiry": 30,
          "nextByEventId": null,
          "previousByEventId": null,
          "eventId": "lifeId1",
          "relatedEventId": null,
          "eventTimestamp": 0
        }
      },
      {
        "componentName": "componentName",
        "beforeEventIdentifier": "lifeId1",
        "beforeRelatedEventIdentifier": "relatedLifeId1",
        "afterEventIdentifier": "lifeId1",
        "afterRelatedEventIdentifier": "relatedLifeId1",
        "startTimeMillis": 1564929295600,
        "endTimeMillis": 1564929296100,
        "id": 7,
        "flowInvocation": null,
        "metrics": [
          {
            "id": 7,
            "componentInvocationMetricImpl": null,
            "name": "name",
            "value": "value"
          }
        ],
        "wiretapFlowEvent": {
          "identifier": 10,
          "timestamp": 1564929296111,
          "moduleName": "moduleName",
          "flowName": "flowName",
          "componentName": "componentName",
          "event": "payload",
          "expiry": 30,
          "nextByEventId": null,
          "previousByEventId": null,
          "eventId": "lifeId1",
          "relatedEventId": null,
          "eventTimestamp": 0
        }
      },
      {
        "componentName": "componentName",
        "beforeEventIdentifier": "lifeId1",
        "beforeRelatedEventIdentifier": "relatedLifeId1",
        "afterEventIdentifier": "lifeId1",
        "afterRelatedEventIdentifier": "relatedLifeId1",
        "startTimeMillis": 1564929295608,
        "endTimeMillis": 1564929296108,
        "id": 10,
        "flowInvocation": null,
        "metrics": [
          {
            "id": 10,
            "componentInvocationMetricImpl": null,
            "name": "name",
            "value": "value"
          }
        ],
        "wiretapFlowEvent": {
          "identifier": 10,
          "timestamp": 1564929296111,
          "moduleName": "moduleName",
          "flowName": "flowName",
          "componentName": "componentName",
          "event": "payload",
          "expiry": 30,
          "nextByEventId": null,
          "previousByEventId": null,
          "eventId": "lifeId1",
          "relatedEventId": null,
          "eventTimestamp": 0
        }
      }
    ],
    "harvested": true,
    "expiry": 0,
    "errorUri": null,
    "harvestedDateTime": 1564929297546,
    "flowInvocationEvents": [
      {
        "componentName": "componentName",
        "beforeEventIdentifier": "lifeId1",
        "beforeRelatedEventIdentifier": "relatedLifeId1",
        "afterEventIdentifier": "lifeId1",
        "afterRelatedEventIdentifier": "relatedLifeId1",
        "startTimeMillis": 1564929295598,
        "endTimeMillis": 1564929296098,
        "id": 8,
        "flowInvocation": null,
        "metrics": [
          {
            "id": 8,
            "componentInvocationMetricImpl": null,
            "name": "name",
            "value": "value"
          }
        ],
        "wiretapFlowEvent": {
          "identifier": 10,
          "timestamp": 1564929296111,
          "moduleName": "moduleName",
          "flowName": "flowName",
          "componentName": "componentName",
          "event": "payload",
          "expiry": 30,
          "nextByEventId": null,
          "previousByEventId": null,
          "eventId": "lifeId1",
          "relatedEventId": null,
          "eventTimestamp": 0
        }
      },
      {
        "componentName": "componentName",
        "beforeEventIdentifier": "lifeId1",
        "beforeRelatedEventIdentifier": "relatedLifeId1",
        "afterEventIdentifier": "lifeId1",
        "afterRelatedEventIdentifier": "relatedLifeId1",
        "startTimeMillis": 1564929295602,
        "endTimeMillis": 1564929296102,
        "id": 9,
        "flowInvocation": null,
        "metrics": [
          {
            "id": 9,
            "componentInvocationMetricImpl": null,
            "name": "name",
            "value": "value"
          }
        ],
        "wiretapFlowEvent": {
          "identifier": 10,
          "timestamp": 1564929296111,
          "moduleName": "moduleName",
          "flowName": "flowName",
          "componentName": "componentName",
          "event": "payload",
          "expiry": 30,
          "nextByEventId": null,
          "previousByEventId": null,
          "eventId": "lifeId1",
          "relatedEventId": null,
          "eventTimestamp": 0
        }
      },
      {
        "componentName": "componentName",
        "beforeEventIdentifier": "lifeId1",
        "beforeRelatedEventIdentifier": "relatedLifeId1",
        "afterEventIdentifier": "lifeId1",
        "afterRelatedEventIdentifier": "relatedLifeId1",
        "startTimeMillis": 1564929295611,
        "endTimeMillis": 1564929296111,
        "id": 6,
        "flowInvocation": null,
        "metrics": [
          {
            "id": 6,
            "componentInvocationMetricImpl": null,
            "name": "name",
            "value": "value"
          }
        ],
        "wiretapFlowEvent": {
          "identifier": 10,
          "timestamp": 1564929296111,
          "moduleName": "moduleName",
          "flowName": "flowName",
          "componentName": "componentName",
          "event": "payload",
          "expiry": 30,
          "nextByEventId": null,
          "previousByEventId": null,
          "eventId": "lifeId1",
          "relatedEventId": null,
          "eventTimestamp": 0
        }
      },
      {
        "componentName": "componentName",
        "beforeEventIdentifier": "lifeId1",
        "beforeRelatedEventIdentifier": "relatedLifeId1",
        "afterEventIdentifier": "lifeId1",
        "afterRelatedEventIdentifier": "relatedLifeId1",
        "startTimeMillis": 1564929295600,
        "endTimeMillis": 1564929296100,
        "id": 7,
        "flowInvocation": null,
        "metrics": [
          {
            "id": 7,
            "componentInvocationMetricImpl": null,
            "name": "name",
            "value": "value"
          }
        ],
        "wiretapFlowEvent": {
          "identifier": 10,
          "timestamp": 1564929296111,
          "moduleName": "moduleName",
          "flowName": "flowName",
          "componentName": "componentName",
          "event": "payload",
          "expiry": 30,
          "nextByEventId": null,
          "previousByEventId": null,
          "eventId": "lifeId1",
          "relatedEventId": null,
          "eventTimestamp": 0
        }
      },
      {
        "componentName": "componentName",
        "beforeEventIdentifier": "lifeId1",
        "beforeRelatedEventIdentifier": "relatedLifeId1",
        "afterEventIdentifier": "lifeId1",
        "afterRelatedEventIdentifier": "relatedLifeId1",
        "startTimeMillis": 1564929295608,
        "endTimeMillis": 1564929296108,
        "id": 10,
        "flowInvocation": null,
        "metrics": [
          {
            "id": 10,
            "componentInvocationMetricImpl": null,
            "name": "name",
            "value": "value"
          }
        ],
        "wiretapFlowEvent": {
          "identifier": 10,
          "timestamp": 1564929296111,
          "moduleName": "moduleName",
          "flowName": "flowName",
          "componentName": "componentName",
          "event": "payload",
          "expiry": 30,
          "nextByEventId": null,
          "previousByEventId": null,
          "eventId": "lifeId1",
          "relatedEventId": null,
          "eventTimestamp": 0
        }
      }
    ]
  },
  {
    "id": 3,
    "moduleName": "moduleName",
    "flowName": "flowName",
    "invocationStartTime": 1564929295628,
    "invocationEndTime": 1564929296128,
    "finalAction": "ACTION",
    "componentInvocationMetricImpls": [
      {
        "componentName": "componentName",
        "beforeEventIdentifier": "lifeId2",
        "beforeRelatedEventIdentifier": "relatedLifeId2",
        "afterEventIdentifier": "lifeId2",
        "afterRelatedEventIdentifier": "relatedLifeId2",
        "startTimeMillis": 1564929295621,
        "endTimeMillis": 1564929296121,
        "id": 11,
        "flowInvocation": null,
        "metrics": [
          {
            "id": 11,
            "componentInvocationMetricImpl": null,
            "name": "name",
            "value": "value"
          }
        ],
        "wiretapFlowEvent": {
          "identifier": 15,
          "timestamp": 1564929296127,
          "moduleName": "moduleName",
          "flowName": "flowName",
          "componentName": "componentName",
          "event": "payload",
          "expiry": 30,
          "nextByEventId": null,
          "previousByEventId": null,
          "eventId": "lifeId2",
          "relatedEventId": null,
          "eventTimestamp": 0
        }
      },
      {
        "componentName": "componentName",
        "beforeEventIdentifier": "lifeId2",
        "beforeRelatedEventIdentifier": "relatedLifeId2",
        "afterEventIdentifier": "lifeId2",
        "afterRelatedEventIdentifier": "relatedLifeId2",
        "startTimeMillis": 1564929295625,
        "endTimeMillis": 1564929296125,
        "id": 13,
        "flowInvocation": null,
        "metrics": [
          {
            "id": 13,
            "componentInvocationMetricImpl": null,
            "name": "name",
            "value": "value"
          }
        ],
        "wiretapFlowEvent": {
          "identifier": 15,
          "timestamp": 1564929296127,
          "moduleName": "moduleName",
          "flowName": "flowName",
          "componentName": "componentName",
          "event": "payload",
          "expiry": 30,
          "nextByEventId": null,
          "previousByEventId": null,
          "eventId": "lifeId2",
          "relatedEventId": null,
          "eventTimestamp": 0
        }
      },
      {
        "componentName": "componentName",
        "beforeEventIdentifier": "lifeId2",
        "beforeRelatedEventIdentifier": "relatedLifeId2",
        "afterEventIdentifier": "lifeId2",
        "afterRelatedEventIdentifier": "relatedLifeId2",
        "startTimeMillis": 1564929295623,
        "endTimeMillis": 1564929296123,
        "id": 15,
        "flowInvocation": null,
        "metrics": [
          {
            "id": 15,
            "componentInvocationMetricImpl": null,
            "name": "name",
            "value": "value"
          }
        ],
        "wiretapFlowEvent": {
          "identifier": 15,
          "timestamp": 1564929296127,
          "moduleName": "moduleName",
          "flowName": "flowName",
          "componentName": "componentName",
          "event": "payload",
          "expiry": 30,
          "nextByEventId": null,
          "previousByEventId": null,
          "eventId": "lifeId2",
          "relatedEventId": null,
          "eventTimestamp": 0
        }
      },
      {
        "componentName": "componentName",
        "beforeEventIdentifier": "lifeId2",
        "beforeRelatedEventIdentifier": "relatedLifeId2",
        "afterEventIdentifier": "lifeId2",
        "afterRelatedEventIdentifier": "relatedLifeId2",
        "startTimeMillis": 1564929295619,
        "endTimeMillis": 1564929296119,
        "id": 14,
        "flowInvocation": null,
        "metrics": [
          {
            "id": 14,
            "componentInvocationMetricImpl": null,
            "name": "name",
            "value": "value"
          }
        ],
        "wiretapFlowEvent": {
          "identifier": 15,
          "timestamp": 1564929296127,
          "moduleName": "moduleName",
          "flowName": "flowName",
          "componentName": "componentName",
          "event": "payload",
          "expiry": 30,
          "nextByEventId": null,
          "previousByEventId": null,
          "eventId": "lifeId2",
          "relatedEventId": null,
          "eventTimestamp": 0
        }
      },
      {
        "componentName": "componentName",
        "beforeEventIdentifier": "lifeId2",
        "beforeRelatedEventIdentifier": "relatedLifeId2",
        "afterEventIdentifier": "lifeId2",
        "afterRelatedEventIdentifier": "relatedLifeId2",
        "startTimeMillis": 1564929295627,
        "endTimeMillis": 1564929296127,
        "id": 12,
        "flowInvocation": null,
        "metrics": [
          {
            "id": 12,
            "componentInvocationMetricImpl": null,
            "name": "name",
            "value": "value"
          }
        ],
        "wiretapFlowEvent": {
          "identifier": 15,
          "timestamp": 1564929296127,
          "moduleName": "moduleName",
          "flowName": "flowName",
          "componentName": "componentName",
          "event": "payload",
          "expiry": 30,
          "nextByEventId": null,
          "previousByEventId": null,
          "eventId": "lifeId2",
          "relatedEventId": null,
          "eventTimestamp": 0
        }
      }
    ],
    "harvested": true,
    "expiry": 0,
    "errorUri": null,
    "harvestedDateTime": 1564929297548,
    "flowInvocationEvents": [
      {
        "componentName": "componentName",
        "beforeEventIdentifier": "lifeId2",
        "beforeRelatedEventIdentifier": "relatedLifeId2",
        "afterEventIdentifier": "lifeId2",
        "afterRelatedEventIdentifier": "relatedLifeId2",
        "startTimeMillis": 1564929295621,
        "endTimeMillis": 1564929296121,
        "id": 11,
        "flowInvocation": null,
        "metrics": [
          {
            "id": 11,
            "componentInvocationMetricImpl": null,
            "name": "name",
            "value": "value"
          }
        ],
        "wiretapFlowEvent": {
          "identifier": 15,
          "timestamp": 1564929296127,
          "moduleName": "moduleName",
          "flowName": "flowName",
          "componentName": "componentName",
          "event": "payload",
          "expiry": 30,
          "nextByEventId": null,
          "previousByEventId": null,
          "eventId": "lifeId2",
          "relatedEventId": null,
          "eventTimestamp": 0
        }
      },
      {
        "componentName": "componentName",
        "beforeEventIdentifier": "lifeId2",
        "beforeRelatedEventIdentifier": "relatedLifeId2",
        "afterEventIdentifier": "lifeId2",
        "afterRelatedEventIdentifier": "relatedLifeId2",
        "startTimeMillis": 1564929295625,
        "endTimeMillis": 1564929296125,
        "id": 13,
        "flowInvocation": null,
        "metrics": [
          {
            "id": 13,
            "componentInvocationMetricImpl": null,
            "name": "name",
            "value": "value"
          }
        ],
        "wiretapFlowEvent": {
          "identifier": 15,
          "timestamp": 1564929296127,
          "moduleName": "moduleName",
          "flowName": "flowName",
          "componentName": "componentName",
          "event": "payload",
          "expiry": 30,
          "nextByEventId": null,
          "previousByEventId": null,
          "eventId": "lifeId2",
          "relatedEventId": null,
          "eventTimestamp": 0
        }
      },
      {
        "componentName": "componentName",
        "beforeEventIdentifier": "lifeId2",
        "beforeRelatedEventIdentifier": "relatedLifeId2",
        "afterEventIdentifier": "lifeId2",
        "afterRelatedEventIdentifier": "relatedLifeId2",
        "startTimeMillis": 1564929295623,
        "endTimeMillis": 1564929296123,
        "id": 15,
        "flowInvocation": null,
        "metrics": [
          {
            "id": 15,
            "componentInvocationMetricImpl": null,
            "name": "name",
            "value": "value"
          }
        ],
        "wiretapFlowEvent": {
          "identifier": 15,
          "timestamp": 1564929296127,
          "moduleName": "moduleName",
          "flowName": "flowName",
          "componentName": "componentName",
          "event": "payload",
          "expiry": 30,
          "nextByEventId": null,
          "previousByEventId": null,
          "eventId": "lifeId2",
          "relatedEventId": null,
          "eventTimestamp": 0
        }
      },
      {
        "componentName": "componentName",
        "beforeEventIdentifier": "lifeId2",
        "beforeRelatedEventIdentifier": "relatedLifeId2",
        "afterEventIdentifier": "lifeId2",
        "afterRelatedEventIdentifier": "relatedLifeId2",
        "startTimeMillis": 1564929295619,
        "endTimeMillis": 1564929296119,
        "id": 14,
        "flowInvocation": null,
        "metrics": [
          {
            "id": 14,
            "componentInvocationMetricImpl": null,
            "name": "name",
            "value": "value"
          }
        ],
        "wiretapFlowEvent": {
          "identifier": 15,
          "timestamp": 1564929296127,
          "moduleName": "moduleName",
          "flowName": "flowName",
          "componentName": "componentName",
          "event": "payload",
          "expiry": 30,
          "nextByEventId": null,
          "previousByEventId": null,
          "eventId": "lifeId2",
          "relatedEventId": null,
          "eventTimestamp": 0
        }
      },
      {
        "componentName": "componentName",
        "beforeEventIdentifier": "lifeId2",
        "beforeRelatedEventIdentifier": "relatedLifeId2",
        "afterEventIdentifier": "lifeId2",
        "afterRelatedEventIdentifier": "relatedLifeId2",
        "startTimeMillis": 1564929295627,
        "endTimeMillis": 1564929296127,
        "id": 12,
        "flowInvocation": null,
        "metrics": [
          {
            "id": 12,
            "componentInvocationMetricImpl": null,
            "name": "name",
            "value": "value"
          }
        ],
        "wiretapFlowEvent": {
          "identifier": 15,
          "timestamp": 1564929296127,
          "moduleName": "moduleName",
          "flowName": "flowName",
          "componentName": "componentName",
          "event": "payload",
          "expiry": 30,
          "nextByEventId": null,
          "previousByEventId": null,
          "eventId": "lifeId2",
          "relatedEventId": null,
          "eventTimestamp": 0
        }
      }
    ]
  }
]
````

</p>
</details>

### Metrics by Start and End Time Service
> **Note**
> The Ikasan Dashboard has a configuration value exposed `solr.metrics.query.limit` which defaults to 200 records (unless explicitly set) for this service 
> even if there are more records available. It is recommended to use the count and paging services to ensure records are not missed.

| Parameter                            | Value                                                                                                                                               | 
|--------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------|
| Request Method                       | GET                                                                                                                                                 |
| Service Name                         | Metrics by Start and End Time Service                                                                                                               |
| Service Description                  | This service returns list of metrics for a given time period.                                                                                       |
| Service Context                      | {dashboard-root-context}/rest/metrics/{startTime}/{endTime}                                                                                         |
| Service Parameter {startTime}        | The start time in milliseconds since epoch to retrieve metrics for.                                                                                 |
| Service Parameter {endTime}          | The end time in milliseconds since epoch to retrieve metrics for.                                                                                   |
| Requires 'Authorization' HTTP Header | Bearer {JWT TOKEN}                                                                                                                                  |
| Returns                              | HTTP 200 status and json List of [FlowInvocationMetric](../../spec/service/history/src/main/java/org/ikasan/spec/history/FlowInvocationMetric.java) |                                                                                                                            

### Metrics by Module Name, Start and End Time Service
> **Note**
> The Ikasan Dashboard has a configuration value exposed `solr.metrics.query.limit` which defaults to 200 records (unless explicitly set) for this service 
> even if there are more records available. It is recommended to use the count and paging services to ensure records are not missed.


| Parameter                            | Value                                                                                                                                               | 
|--------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------|
| Request Method                       | GET                                                                                                                                                 |
| Service Name                         | Metrics by Module Name, Start and End Time Service                                                                                                  |
| Service Description                  | This service returns list of metrics for a given time period and module.                                                                            |
| Service Context                      | {dashboard-root-context}/rest/metrics/{moduleName}/{startTime}/{endTime}                                                                            |
| Service Parameter {moduleName}       | The module name to retrieve metrics for.                                                                                                            |
| Service Parameter {startTime}        | The start time in milliseconds since epoch to retrieve metrics for.                                                                                 |
| Service Parameter {endTime}          | The end time in milliseconds since epoch to retrieve metrics for.                                                                                   |
| Requires 'Authorization' HTTP Header | Bearer {JWT TOKEN}                                                                                                                                  |
| Returns                              | HTTP 200 status and json List of [FlowInvocationMetric](../../spec/service/history/src/main/java/org/ikasan/spec/history/FlowInvocationMetric.java) |                                                                                                                            

### Metrics by Module Name, Flow Name, Start and End Time Service
> **Note**
> The Ikasan Dashboard has a configuration value exposed `solr.metrics.query.limit` which defaults to 200 records (unless explicitly set) for this service 
> even if there are more records available. It is recommended to use the count and paging services to ensure records are not missed.


| Parameter                            | Value                                                                                                                                               | 
|--------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------|
| Request Method                       | GET                                                                                                                                                 |
| Service Name                         | Metrics by Module Name, Flow Name, Start and End Time Service                                                                                       |
| Service Description                  | This service returns list of metrics for a given time period, module and flow.                                                                      |
| Service Context                      | {dashboard-root-context}/rest/metrics/{moduleName}/{flowName}/{startTime}/{endTime}                                                                 |
| Service Parameter {moduleName}       | The module name to retrieve metrics for.                                                                                                            |
| Service Parameter {flowName}         | The flow name to retrieve metrics for.                                                                                                              |
| Service Parameter {startTime}        | The start time in milliseconds since epoch to retrieve metrics for.                                                                                 |
| Service Parameter {endTime}          | The end time in milliseconds since epoch to retrieve metrics for.                                                                                   |
| Requires 'Authorization' HTTP Header | Bearer {JWT TOKEN}                                                                                                                                  |
| Returns                              | HTTP 200 status and json List of [FlowInvocationMetric](../../spec/service/history/src/main/java/org/ikasan/spec/history/FlowInvocationMetric.java) |                                                                                                                            

### Metrics Count by Start and End Time Service

| Parameter                            | Value                                                                       | 
|--------------------------------------|-----------------------------------------------------------------------------|
| Request Method                       | GET                                                                         |
| Service Name                         | Metrics Count by Start and End Time Service                                 |
| Service Description                  | This service returns the number of metrics records for a given time period. |
| Service Context                      | {dashboard-root-context}/rest/metrics/count/{startTime}/{endTime}           |
| Service Parameter {startTime}        | The start time in milliseconds since epoch to retrieve metrics for.         |
| Service Parameter {endTime}          | The end time in milliseconds since epoch to retrieve metrics for.           |
| Requires 'Authorization' HTTP Header | Bearer {JWT TOKEN}                                                          |
| Returns                              | HTTP 200 status and Long representing the number of records available.      |                                                                                                                            

### Metrics Count by Module Name, Start and End Time Service
| Parameter                            | Value                                                                                  | 
|--------------------------------------|----------------------------------------------------------------------------------------|
| Request Method                       | GET                                                                                    |
| Service Name                         | Metrics Count by Module Name, Start and End Time Service                               |
| Service Description                  | This service returns the number of metrics records for a given time period and module. |
| Service Context                      | {dashboard-root-context}/rest/metrics/count/{moduleName}/{startTime}/{endTime}         |
| Service Parameter {moduleName}       | The module name to retrieve metrics count for.                                         |
| Service Parameter {startTime}        | The start time in milliseconds since epoch to retrieve metrics for.                    |
| Service Parameter {endTime}          | The end time in milliseconds since epoch to retrieve metrics for.                      |
| Requires 'Authorization' HTTP Header | Bearer {JWT TOKEN}                                                                     |
| Returns                              | HTTP 200 status and Long representing the number of records available.                 |                                                                                                                            

### Metrics Count by Module Name, Flow Name, Start and End Time Service
| Parameter                            | Value                                                                                        | 
|--------------------------------------|----------------------------------------------------------------------------------------------|
| Request Method                       | GET                                                                                          |
| Service Name                         | Metrics Count by Module Name, Flow Name, Start and End Time Service                          |
| Service Description                  | This service returns the number of metrics records for a given time period, module and flow. |
| Service Context                      | {dashboard-root-context}/rest/metrics/count/{moduleName}/{flowName}/{startTime}/{endTime}    |
| Service Parameter {moduleName}       | The module name to retrieve metrics count for.                                               |
| Service Parameter {flowName}         | The flow name to retrieve metrics count for.                                                 |
| Service Parameter {startTime}        | The start time in milliseconds since epoch to retrieve metrics for.                          |
| Service Parameter {endTime}          | The end time in milliseconds since epoch to retrieve metrics for.                            |
| Requires 'Authorization' HTTP Header | Bearer {JWT TOKEN}                                                                           |
| Returns                              | HTTP 200 status and Long representing the number of records available.                       |                                                                                                                            

### Paged Metrics by Start and End Time Service

| Parameter                            | Value                                                                                                                                                                                                                               | 
|--------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Request Method                       | GET                                                                                                                                                                                                                                 |
| Service Name                         | Paged Metrics by Start and End Time Service                                                                                                                                                                                         |
| Service Description                  | This service returns paged list of metrics for a given time period with an offset and limit. It is intended to be used with the equivalent count service so that the client can iterate and retrieve all records in a paged manner. |
| Service Context                      | {dashboard-root-context}/rest/metrics/paged/{moduleName}/{flowName}/{startTime}/{endTime}/{offset}/{limit}                                                                                                                          |
| Service Parameter {startTime}        | The start time in milliseconds since epoch to retrieve metrics for.                                                                                                                                                                 |
| Service Parameter {endTime}          | The end time in milliseconds since epoch to retrieve metrics for.                                                                                                                                                                   |
| Service Parameter {offset}           | The zero based offset to retrieve metrics from.                                                                                                                                                                                     |
| Service Parameter {limit}            | The limit of the number of metrics records that will be received.                                                                                                                                                                   |
| Requires 'Authorization' HTTP Header | Bearer {JWT TOKEN}                                                                                                                                                                                                                  |
| Returns                              | HTTP 200 status and List of [FlowInvocationMetric](../../spec/service/history/src/main/java/org/ikasan/spec/history/FlowInvocationMetric.java)                                                                                      |                                                                                                                            

### Paged Metrics by Module Name, Start and End Time Service
| Parameter                            | Value                                                                                                                                                                                                                                          | 
|--------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Request Method                       | GET                                                                                                                                                                                                                                            |
| Service Name                         | Paged Metrics by Module Name, Start and End Time Service                                                                                                                                                                                       |
| Service Description                  | This service returns paged list of metrics for a given time period and module with an offset and limit. It is intended to be used with the equivalent count service so that the client can iterate and retrieve all records in a paged manner. |
| Service Context                      | {dashboard-root-context}/rest/metrics/paged/{moduleName}/{flowName}/{startTime}/{endTime}/{offset}/{limit}                                                                                                                                     |
| Service Parameter {moduleName}       | The module name to retrieve metrics for.                                                                                                                                                                                                       |
| Service Parameter {startTime}        | The start time in milliseconds since epoch to retrieve metrics for.                                                                                                                                                                            |
| Service Parameter {endTime}          | The end time in milliseconds since epoch to retrieve metrics for.                                                                                                                                                                              |
| Service Parameter {offset}           | The zero based offset to retrieve metrics from.                                                                                                                                                                                                |
| Service Parameter {limit}            | The limit of the number of metrics records that will be received.                                                                                                                                                                              |
| Requires 'Authorization' HTTP Header | Bearer {JWT TOKEN}                                                                                                                                                                                                                             |
| Returns                              | HTTP 200 status and List of [FlowInvocationMetric](../../spec/service/history/src/main/java/org/ikasan/spec/history/FlowInvocationMetric.java)                                                                                                 |                                                                                                                            

### Paged Metrics by Module Name, Flow Name, Start and End Time Service
| Parameter                            | Value                                                                                                                                                                                                                                                | 
|--------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Request Method                       | GET                                                                                                                                                                                                                                                  |
| Service Name                         | Paged Metrics by Module Name, Flow Name, Start and End Time Service                                                                                                                                                                                  |
| Service Description                  | This service returns paged list of metrics for a given time period, flow and module with an offset and limit. It is intended to be used with the equivalent count service so that the client can iterate and retrieve all records in a paged manner. |
| Service Context                      | {dashboard-root-context}/rest/metrics/paged/{moduleName}/{flowName}/{startTime}/{endTime}/{offset}/{limit}                                                                                                                                           |
| Service Parameter {moduleName}       | The module name to retrieve metrics for.                                                                                                                                                                                                             |
| Service Parameter {flowName}         | The flow name to retrieve metrics for.                                                                                                                                                                                                               |
| Service Parameter {startTime}        | The start time in milliseconds since epoch to retrieve metrics for.                                                                                                                                                                                  |
| Service Parameter {endTime}          | The end time in milliseconds since epoch to retrieve metrics for.                                                                                                                                                                                    |
| Service Parameter {offset}           | The zero based offset to retrieve metrics from.                                                                                                                                                                                                      |
| Service Parameter {limit}            | The limit of the number of metrics records that will be received.                                                                                                                                                                                    |
| Requires 'Authorization' HTTP Header | Bearer {JWT TOKEN}                                                                                                                                                                                                                                   |
| Returns                              | HTTP 200 status and List of [FlowInvocationMetric](../../spec/service/history/src/main/java/org/ikasan/spec/history/FlowInvocationMetric.java)                                                                                                       |                                                                                                                            

