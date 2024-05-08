![Problem Domain](../../developer/docs/quickstart-images/Ikasan-title-transparent.png)
# Rest Module
Every Ikasan Integration Module exposes a number of REST service endpoints that allow any user or client to interact with it.
 
All Ikasan Module REST service endpoints require  Authorisation HTTP header to be send along with data payload. 
Authorisation Header has a form of "Basic {TOKEN}". The {TOKEN} can be obtained from Login Endpoint by
providing user credentials.


## Module Control Service
The set of REST endpoints which allows you to:
 - inquire runtime state of module flows
 - change flow state
 - change flow startup type
 
 
### GET Flows state

| Parameter                            | Value                                                                                                     | 
|--------------------------------------|-----------------------------------------------------------------------------------------------------------|
| Request Method                       | GET                                                                                                       |
| Service Context                      | {module-root-context}/rest/moduleControl/{moduleName}                                                     |
| Requires Path parameter moduleName   | Module Name                                                                                               |
| Requires 'Authorization' HTTP Header | Basic {TOKEN}                                                                                             |
| Returns                              | HTTP 200 status and  json serialised [ModuleDto](src/main/java/org/ikasan/rest/module/dto/ModuleDto.java) |
 
 <details>
    <summary>Click to view the sample JSON payload provided by the service.</summary>
<p>

````json
{
  "name": "sampleFileIntegrationModule",
  "flows": [
    {
      "name": "sourceFileFlow",
      "state": "stopped"
    },
    {
      "name": "targetFileFlow",
      "state": "stopped"
    }
  ]
}
````
</p>
</details>


### GET Flow state

| Parameter                            | Value                                                                                                                  | 
|--------------------------------------|------------------------------------------------------------------------------------------------------------------------|
| Request Method                       | GET                                                                                                                    |
| Service Context                      | {module-root-context}/rest/moduleControl/{moduleName}/{flowName}                                                       |
| Requires Path parameter moduleName   | Module Name                                                                                                            |
| Requires Path parameter flowName     | Flow Name                                                                                                              |
| Requires 'Authorization' HTTP Header | Basic {TOKEN}                                                                                                          |
| Returns                              | HTTP 200 status with below payload. A json serialised [FlowDto](src/main/java/org/ikasan/rest/module/dto/FlowDto.java) |

<details>
    <summary>Click to view the sample JSON payload provided by the service.</summary>
<p>

````json
    {
      "name": "sourceFileFlow",
      "state": "stopped"
    }
````
 
</p>
</details>


### Change Flow state

| Parameter                            | Value                                                                                                    | 
|--------------------------------------|----------------------------------------------------------------------------------------------------------|
| Request Method                       | PUT                                                                                                      |
| Service Context                      | {module-root-context}/rest/moduleControl                                                                 |
| Requires 'Authorization' HTTP Header | Basic {TOKEN}                                                                                            |
| Payload                              | A json serialised [ChangeFlowStateDto](src/main/java/org/ikasan/rest/module/dto/ChangeFlowStateDto.java) |
| Returns                              | HTTP 200 status if state was changed successfully.                                                       |

<details>
    <summary>Click to view the sample JSON payload expected by the service.</summary>
<p>

````json
    {
      "action": "start|startPause|pause|resume|stop",
      "flowName": "string",
      "moduleName": "string"
    }
````
 
</p>
</details>

### Change Flow startup type

| Parameter                            | Value                                                                                                                | 
|--------------------------------------|----------------------------------------------------------------------------------------------------------------------|
| Request Method                       | PUT                                                                                                                  |
| Service Context                      | {module-root-context}/rest/moduleControl/startupMode                                                                 |
| Requires 'Authorization' HTTP Header | Basic {TOKEN}                                                                                                        |
| Payload                              | A json serialised [ChangeFlowStartupModeDto](src/main/java/org/ikasan/rest/module/dto/ChangeFlowStartupModeDto.java) |
| Returns                              | HTTP 200 status if startup type was changed successfully.                                                            |

<details>
    <summary>Click to view the sample JSON payload expected by the service.</summary>
<p>

````json
    {
      "comment": "This filed is mandatory when startupType=disabled",
      "flowName": "string",
      "moduleName": "string",
      "startupType": "manual|automatic|disabled"
    }
````
 
</p>
</details> 

## Resubmission Service
REST endpoints which allows user to resubmit or ignore excluded events.

### Resubmission Endpoint

| Parameter                            | Value                                                                                                            | 
|--------------------------------------|------------------------------------------------------------------------------------------------------------------|
| Request Method                       | PUT                                                                                                              |
| Service Context                      | {module-root-context}/rest/resubmission                                                                          |
| Requires 'Authorization' HTTP Header | Basic {TOKEN}                                                                                                    |
| Payload                              | A json serialised [ResubmissionRequestDto](src/main/java/org/ikasan/rest/module/dto/ResubmissionRequestDto.java) |
| Returns                              | HTTP 200 status if startup type was changed successfully.                                                        |

<details>
    <summary>Click to view the sample JSON payload expected by the service.</summary>
<p>

````json
{
  "action": "resubmit|ignore",
  "errorUri": "string",
  "flowName": "string",
  "moduleName": "string"
}
````
 
</p>
</details>


## Replay Service
REST endpoints which allows user to replay given recorded event.

### Replay Endpoint

| Parameter                            | Value                                                                                                | 
|--------------------------------------|------------------------------------------------------------------------------------------------------|
| Request Method                       | PUT                                                                                                  |
| Service Context                      | {module-root-context}/rest/replay                                                                    |
| Requires 'Authorization' HTTP Header | Basic {TOKEN}                                                                                        |
| Payload                              | A json serialised [ReplayRequestDto](src/main/java/org/ikasan/rest/module/dto/ReplayRequestDto.java) |
| Returns                              | HTTP 200 status if startup type was changed successfully.                                            |

<details>
    <summary>Click to view the sample JSON payload expected by the service.</summary>
<p>

````json
{
  "action": "resubmit|ignore",
  "errorUri": "string",
  "event": "byteArray"
}
````
 
</p>
</details>

## Persistence Service
REST endpoints which allows users to obtain the row count for database tables.
> [!IMPORTANT]
> If the `org.ikasan.persistence.PersistenceAutoConfiguration` has been excluded, then the Persistence Service rest services
> will not be available. This is likely to be the case if a DBMS platform other than H2 has been used by Ikasan. In which
> case an implementation of the `org.ikasan.spec.persistence.service.GeneralDatabaseService` will need to be provided for the
> relevant RDMS.

### GET Table Row Count

| Parameter                            | Value                                                                                                                                    | 
|--------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------|
| Request Method                       | GET                                                                                                                                      |
| Service Context                      | {module-root-context}/rest/persistence/rowCount/{tableName}                                                                              |
| Requires Path parameter tableName    | The name of the table to the row count for.                                                                                              |
| Requires 'Authorization' HTTP Header | Basic {TOKEN}                                                                                                                            |
| Returns                              | HTTP 200 status with below payload. A json serialised [TableRowCountDto](src/main/java/org/ikasan/rest/module/dto/TableRowCountDto.java) |

<details>
    <summary>Click to view the sample JSON payload provided by the service.</summary>
<p>

````json
    {
      "tableName":"TableName",
      "rowCount":5
    }
````

</p>
</details>

## In Doubt Transaction Service
REST endpoints which allow for the discovery and management of IN_DOUBT database transactions.

> [!IMPORTANT]
> If the `org.ikasan.persistence.InDoubtTransactionAutoConfiguration` has been excluded, then the In Doubt Transaction Service rest services
> will not be available. This is likely to be the case if a DBMS platform other than H2 has been used by Ikasan. In which
> case an implementation of the `org.ikasan.spec.persistence.service.InDoubtTransactionService` will need to be provided for the
> relevant RDMS.

### Get All In Doubt Transactions

| Parameter                            | Value                                                                                                                                                      | 
|--------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Request Method                       | GET                                                                                                                                                        |
| Service Context                      | {module-root-context}/rest/transaction/inDoubt/all                                                                                                         |
| Requires 'Authorization' HTTP Header | Basic {TOKEN}                                                                                                                                              |
| Returns                              | HTTP 200 status with below payload. A json serialised List of [InDoubtTransactionDto](src/main/java/org/ikasan/rest/module/dto/InDoubtTransactionDto.java) |

<details>
    <summary>Click to view the sample JSON payload provided by the service.</summary>
<p>

````json
[
      {
          "transactionName":"TRANS_1",
          "transactionState":"IN_DOUBT"
      },
      {
          "transactionName":"TRANS_2",
          "transactionState":"IN_DOUBT"
      }
]
````

</p>
</details>

### Get In Doubt Transaction By Name

| Parameter                               | Value                                                                                                                                              | 
|-----------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------|
| Request Method                          | GET                                                                                                                                                |
| Service Context                         | {module-root-context}/rest/transaction/inDoubt/get/{transactionName}                                                                               |
| Requires Path parameter transactionName | The name of the transaction we are requesting details of.                                                                                          |
| Requires 'Authorization' HTTP Header    | Basic {TOKEN}                                                                                                                                      |
| Returns                                 | HTTP 200 status with below payload. A json serialised [InDoubtTransactionDto](src/main/java/org/ikasan/rest/module/dto/InDoubtTransactionDto.java) |

<details>
    <summary>Click to view the sample JSON payload provided by the service.</summary>
<p>

````json
{
  "transactionName":"TRANS_1",
  "transactionState":"IN_DOUBT"
}
````

</p>
</details>

### Commit In Doubt Transaction

| Parameter                               | Value                                                                   | 
|-----------------------------------------|-------------------------------------------------------------------------|
| Request Method                          | PUT                                                                     |
| Service Context                         | {module-root-context}/rest/transaction/inDoubt/commit/{transactionName} |
| Requires Path parameter transactionName | The name of the transaction we are committing.                          |
| Requires 'Authorization' HTTP Header    | Basic {TOKEN}                                                           |
| Returns                                 | HTTP 200 status                                                         |


### Rollback In Doubt Transaction

| Parameter                               | Value                                                                     | 
|-----------------------------------------|---------------------------------------------------------------------------|
| Request Method                          | PUT                                                                       |
| Service Context                         | {module-root-context}/rest/transaction/inDoubt/rollback/{transactionName} |
| Requires Path parameter transactionName | The name of the transaction we are rolling back.                          |
| Requires 'Authorization' HTTP Header    | Basic {TOKEN}                                                             |
| Returns                                 | HTTP 200 status                                                           |
