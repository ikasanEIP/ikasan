![IKASAN](../../../developer/docs/quickstart-images/Ikasan-title-transparent.png)

# Big Queue Services
Big Queue is the underlying queuing mechanism that Ikasan employs to provide reliable messaging between agents and the dashboard.
Ikasan exposes a number of services that allows clients to inspect and manage biq queues associated with Scheduler Agents and the Scheduler Dashboard.
Management can be done via the Scheduler Agent and the Scheduler Dashboard.

[Managing queues through the Scheduler Dashboard](#managing-queues-through-the-scheduler-dashboard)

[Managing queues through the Scheduler Agent](#managing-queues-through-the-scheduler-agent)

[Managing Scheduler Agent queues through the Scheduler Dashboard](#managing-scheduler-agent-queues-through-the-scheduler-dashboard)

[Managing queues through curl](#managing-queues-through-curl)

## Managing queues through the Scheduler Dashboard
The scheduler dashboard comes with its own rest services to manage its queues. The following services are available once authenticated through the scheduler dashboard.

### Get all queue names
| Parameter                            | Value                                                                                                                                                              | 
|--------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Request Method                       | GET                                                                                                                                                                |
| Service Name                         | All Queue Name                                                                                                                                                     |
| Service Description                  | This service returns a list of all the queues that are set up for this agent                                                                                       |
| Service Context                      |  /rest/dashboard/bigQueue/                                                                                                                                         |
| Sample                               | https://localhost:9090/rest/dashboard/bigQueue/                                                                                                                    |
| Requires 'Authorization' HTTP Header | Basic {TOKEN}                                                                                                                                                      |
| Returns                              | HTTP 200 status and json array list    |                                                                                                                            

**Sample Response** 
```json 
[
   "dashboard-inbound-queue",
   "inbound-349a63c4-b003-4395-b0a8-cb8fc60b0f30-queue",
   "outbound-349a63c4-b003-4395-b0a8-cb8fc60b0f30-queue"
]
```

### Get size of all queue
| Parameter                            | Value                                                                                                                                                              | 
|--------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Request Method                       | GET                                                                                                                                                                |
| Service Name                         | Size of all queues                                                                                                                                                 |
| Service Description                  | This service returns a map of all queues and the number of message yet to be taken off the queue. Option to just return queues with size > 0                       |
| Service Context                      | /rest/dashboard/bigQueue/size <br>/rest/dashboard/bigQueue/size?includeZeros={true/false}                                                                          |
| Optional parameter includeZeros      | Boolean value _true_ or *false*. Set to _true_ to include zero queue size, set to _false_ to only include sizes greater than 0. <br> Defaults to true if not specified. |
| Sample                               | https://localhost:9090/rest/dashboard/bigQueue/size <br>https://localhost:9090/rest/dashboard/bigQueue/size?includeZeros=false                                     |
| Requires 'Authorization' HTTP Header | Basic {TOKEN}                                                                                                                                                      |
| Returns                              | HTTP 200 status and json map value pairs           |                                                                                                                

**Sample Response**
```json 
{
  "dashboard-inbound-queue": 0,
  "inbound-349a63c4-b003-4395-b0a8-cb8fc60b0f30-queue": 0,
  "outbound-349a63c4-b003-4395-b0a8-cb8fc60b0f30-queue": 1
}
```

### Get size of specific queue
| Parameter                            | Value                                                                                                                                                              | 
|--------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Request Method                       | GET                                                                                                                                                                |
| Service Name                         | Size of specific queues                                                                                                                                            |
| Service Description                  | This service returns a numeric value of the amount of messages currently on the queue.                                                                             |
| Service Context                      | /rest/dashboard/bigQueue/size/{queueName}                                                                                                                          |
| Required parameter queueName         | Name of the queue                                                                                                                                                  |
| Sample                               | https://localhost:9090/rest/dashboard/bigQueue/size/dashboard-inbound-queue                                                                                        |
| Requires 'Authorization' HTTP Header | Basic {TOKEN}                                                                                                                                                      |
| Returns                              | HTTP 200 status and a numeric value        |                                                                                                                        

**Sample Response**
```json 
5
```

### Peek a specific queue
| Parameter                            | Value                                                                                                                                                              | 
|--------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Request Method                       | GET                                                                                                                                                                |
| Service Name                         | Peek a specific queues                                                                                                                                             |
| Service Description                  | This service returns a json payload containing the details of the first message on the queue                                                                       |
| Service Context                      | /rest/dashboard/bigQueue/peek/{queueName}                                                                                                                          |
| Required parameter queueName         | Name of the queue                                                                                                                                                  |
| Sample                               | https://localhost:9090/rest/dashboard/bigQueue/peek/dashboard-inbound-queue                                                                                        |
| Requires 'Authorization' HTTP Header | Basic {TOKEN}                                                                                                                                                      |
| Returns                              | HTTP 200 status and json serialised [BigQueueMessage](../../../spec/service/big-queue/src/main/java/org/ikasan/spec/bigqueue/message/BigQueueMessage.java)         |

**Sample Response**
```json 
{
  "messageId": "73f15215-cf1f-4e01-943d-80a011efc89c",
  "createdTime": 1666174020053,
  "message": {
    "agentName": "scheduler-agent",
    "jobName": "-801284430_ScheduledJob_06:00:00",
    "jobGroup": "604791405-1666112601627",
    "jobDescription": "Quartz scheduled job - -801284430_ScheduledJob_06:00:00 firing on cron schedule (0 0 6 ? * * *)",
    "returnCode": 0,
    "successful": true,
    "pid": 0,
    "fireTime": 1666174020053,
    "nextFireTime": 1666174080000,
    "completionTime": 0,
    "harvested": false,
    "harvestedDateTime": 0,
    "dryRun": false,
    "jobStarting": false,
    "contextInstanceId": "c2ef539a-36bb-4669-808d-5e1edd9994c8",
    "skipped": false,
    "raisedDueToFailureResubmission": false,
    "childContextNames": [
      "CONTEXT--801284430"
    ],
    "contextName": "-1793100514"
  },
  "messageProperties": {
    "contextName": "-1793100514",
    "contextInstanceId": "c2ef539a-36bb-4669-808d-5e1edd9994c8"
  }
}
```

### Get all messages on a specific queue
| Parameter                            | Value                                                                                                                                                              | 
|--------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Request Method                       | GET                                                                                                                                                                |
| Service Name                         | Get messages from a specific queues                                                                                                                                |
| Service Description                  | This service returns an array of json payload containing the details of all messages on the queue                                                                  |
| Service Context                      | /rest/dashboard/bigQueue/messages/{queueName}                                                                                                                      |
| Required parameter queueName         | Name of the queue                                                                                                                                                  |
| Sample                               | https://localhost:9090/rest/dashboard/bigQueue/messages/dashboard-inbound-queue                                                                                    |
| Requires 'Authorization' HTTP Header | Basic {TOKEN}                                                                                                                                                      |
| Returns                              | HTTP 200 status and array of json serialised [BigQueueMessage](../../../spec/service/big-queue/src/main/java/org/ikasan/spec/bigqueue/message/BigQueueMessage.java)|

**Sample Response**
```json 
[
  {
    "messageId": "73f15215-cf1f-4e01-943d-80a011efc89c",
    "createdTime": 1666174020053,
    "message": {
      "agentName": "scheduler-agent",
      "jobName": "-801284430_ScheduledJob_06:00:00",
      "jobGroup": "604791405-1666112601627",
      "jobDescription": "Quartz scheduled job - -801284430_ScheduledJob_06:00:00 firing on cron schedule (0 0 6 ? * * *)",
      "returnCode": 0,
      "successful": true,
      "pid": 0,
      "fireTime": 1666174020053,
      "nextFireTime": 1666174080000,
      "completionTime": 0,
      "harvested": false,
      "harvestedDateTime": 0,
      "dryRun": false,
      "jobStarting": false,
      "contextInstanceId": "c2ef539a-36bb-4669-808d-5e1edd9994c8",
      "skipped": false,
      "raisedDueToFailureResubmission": false,
      "childContextNames": [
        "CONTEXT--801284430"
      ],
      "contextName": "-1793100514"
    },
    "messageProperties": {
      "contextName": "-1793100514",
      "contextInstanceId": "c2ef539a-36bb-4669-808d-5e1edd9994c8"
    }
  },
  {
    "messageId": "ddd74498-4ec2-4de5-aadf-791235155411",
    "createdTime": 1666174140065,
    "message": {
      "agentName": "scheduler-agent",
      "jobName": "-801284430_ScheduledJob_06:00:00",
      "jobGroup": "604791405-1666112601627",
      "jobDescription": "Quartz scheduled job - -801284430_ScheduledJob_06:00:00 firing on cron schedule (0 0 6 ? * * *)",
      "returnCode": 0,
      "successful": true,
      "pid": 0,
      "fireTime": 1666174140064,
      "nextFireTime": 1666174200000,
      "completionTime": 0,
      "harvested": false,
      "harvestedDateTime": 0,
      "dryRun": false,
      "jobStarting": false,
      "contextInstanceId": "c2ef539a-36bb-4669-808d-5e1edd9994c8",
      "skipped": false,
      "raisedDueToFailureResubmission": false,
      "childContextNames": [
        "CONTEXT--801284430"
      ],
      "contextName": "-1793100514"
    },
    "messageProperties": {
      "contextName": "-1793100514",
      "contextInstanceId": "c2ef539a-36bb-4669-808d-5e1edd9994c8"
    }
  }
]
```

### Delete message from a queue
| Parameter                            | Value                                                                                                                                                              | 
|--------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Request Method                       | DELETE                                                                                                                                                             |
| Service Name                         | Delete message from a queue                                                                                                                                        |
| Service Description                  | Delete a message from a specific queue by a messageId from the [BigQueueMessage](../../../spec/service/big-queue/src/main/java/org/ikasan/spec/bigqueue/message/BigQueueMessage.java) |
| Service Context                      | /rest/dashboard/bigQueue/delete/{queueName}/{messageId}                                                                                                            |
| Required parameter queueName         | Name of the queue                                                                                                                                                  |
| Required parameter messageId         | MessageId of the message to be removed                                                                                                                             |
| Sample                               | https://localhost:9090/rest/dashboard/bigQueue/delete/dashboard-inbound-queue/ddd74498-4ec2-4de5-aadf-791235155411                                                 |
| Requires 'Authorization' HTTP Header | Basic {TOKEN}                                                                                                                                                      |
| Returns                              | HTTP 200 status                                                                                                                                                    |

### Delete all messages from a queue
| Parameter                            | Value                                                                                                                                                              | 
|--------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Request Method                       | DELETE                                                                                                                                                             |
| Service Name                         | Delete all message from a queue                                                                                                                                    |
| Service Description                  | Delete all messages from a specific queue                                                                                                                          |
| Service Context                      | /rest/dashboard/bigQueue/delete/allMessages/{queueName}                                                                                                            |
| Required parameter queueName         | Name of the queue                                                                                                                                                  |
| Sample                               | https://localhost:9090/rest/dashboard/bigQueue/delete/allMessages/dashboard-inbound-queue                                                                          |
| Requires 'Authorization' HTTP Header | Basic {TOKEN}                                                                                                                                                      |
| Returns                              | HTTP 200 status                                                                                                                                                    |

### Delete a queue
| Parameter                            | Value                                                                                                                                                              | 
|--------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Request Method                       | DELETE                                                                                                                                                             |
| Service Name                         | Delete a queue                                                                                                                                                     |
| Service Description                  | Delete a queue. This included removing the underlying files and directories relating to the queue.                                                                 |
| Service Context                      | /rest/dashboard/bigQueue/delete/{queueName}                                                                                                                        |
| Required parameter queueName         | Name of the queue                                                                                                                                                  |
| Sample                               | https://localhost:9090/rest/dashboard/bigQueue/delete/dashboard-inbound-queue                                                                                      |
| Requires 'Authorization' HTTP Header | Basic {TOKEN}                                                                                                                                                      |
| Returns                              | HTTP 200 status        |                                                                                                                                        


## Managing queues through the Scheduler Agent
The scheduler agents comes with its own rest services to manage its queues. The following services are available once authenticated through the scheduler agent.

### Get all queue names
| Parameter                            | Value                                                                                                                                                              | 
|--------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Request Method                       | GET                                                                                                                                                                |
| Service Name                         | All Queue Name                                                                                                                                                     |
| Service Description                  | This service returns a list of all the queues that are set up for this agent                                                                                       |
| Service Context                      | /rest/big/queue/                                                                                                                                                   |
| Sample                               | https://localhost:9090/agent-module-name/rest/big/queue/                                                                                                           |
| Requires 'Authorization' HTTP Header | Basic {TOKEN}                                                                                                                                                      |
| Returns                              | HTTP 200 status and json array list                                                                                                 |
**Sample Response** 
```json 
[
   "scheduler-agent--958075417-inbound-queue",
   "scheduler-agent-509550682-inbound-queue",
   "scheduler-agent-153847922-inbound-queue"
]
```

### Get size of all queue
| Parameter                            | Value                                                                                                                                                              | 
|--------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Request Method                       | GET                                                                                                                                                                |
| Service Name                         | Size of all queues                                                                                                                                                 |
| Service Description                  | This service returns a map of all queues and the number of message yet to be taken off the queue. Option to just return queues with size > 0                       |
| Service Context                      | /rest/big/queue/size <br>/rest/big/queue/size?includeZeros={true/false}                                                                                            |
| Optional parameter includeZeros      | Boolean value _true_ or *false*. Set to _true_ to include zero queue size, set to _false_ to only include sizes greater than 0. <br> Defaults to true if not specified. |
| Sample                               | https://localhost:9090/agent-module-name/rest/big/queue/size <br>https://localhost:9090/agent-module-name/rest/big/queue/size?includeZeros=false                   |
| Requires 'Authorization' HTTP Header | Basic {TOKEN}                                                                                                                                                      |
| Returns                              | HTTP 200 status and json map value pairs                                                                                                                           |
**Sample Response**
```json 
{
  "scheduler-agent--2050730871-inbound-queue": 0,
  "scheduler-agent-744167903-inbound-queue": 0,
  "scheduler-agent--66035914-inbound-queue": 1
}
```

### Get size of specific queue
| Parameter                            | Value                                                                                                                                                              | 
|--------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Request Method                       | GET                                                                                                                                                                |
| Service Name                         | Size of specific queues                                                                                                                                            |
| Service Description                  | This service returns a numeric value of the amount of messages currently on the queue.                                                                             |
| Service Context                      | /rest/big/queue/size/{queueName}                                                                                                                                   |
| Required parameter queueName         | Name of the queue                                                                                                                                                  |
| Sample                               | https://localhost:9090/agent-module-name/rest/big/queue/size/scheduler-agent--2050730871-inbound-queue                                                             |
| Requires 'Authorization' HTTP Header | Basic {TOKEN}                                                                                                                                                      |
| Returns                              | HTTP 200 status and a numeric value                                                                                                                                |
**Sample Response**
```json 
5
```

### Peek a specific queue
| Parameter                            | Value                                                                                                                                                              | 
|--------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Request Method                       | GET                                                                                                                                                                |
| Service Name                         | Peek a specific queues                                                                                                                                             |
| Service Description                  | This service returns a json payload containing the details of the first message on the queue                                                                       |
| Service Context                      | /rest/big/queue/peek/{queueName}                                                                                                                                   |
| Required parameter queueName         | Name of the queue                                                                                                                                                  |
| Sample                               | https://localhost:9090/agent-module-name/rest/big/queue/peek/scheduler-agent--2050730871-inbound-queue                                                             |
| Requires 'Authorization' HTTP Header | Basic {TOKEN}                                                                                                                                                      |
| Returns                              | HTTP 200 status and json serialised [BigQueueMessage](../../../spec/service/big-queue/src/main/java/org/ikasan/spec/bigqueue/message/BigQueueMessage.java)         |
**Sample Response**
```json 
{
  "messageId": "73f15215-cf1f-4e01-943d-80a011efc89c",
  "createdTime": 1666174020053,
  "message": {
    "agentName": "scheduler-agent",
    "jobName": "-801284430_ScheduledJob_06:00:00",
    "jobGroup": "604791405-1666112601627",
    "jobDescription": "Quartz scheduled job - -801284430_ScheduledJob_06:00:00 firing on cron schedule (0 0 6 ? * * *)",
    "returnCode": 0,
    "successful": true,
    "pid": 0,
    "fireTime": 1666174020053,
    "nextFireTime": 1666174080000,
    "completionTime": 0,
    "harvested": false,
    "harvestedDateTime": 0,
    "dryRun": false,
    "jobStarting": false,
    "contextInstanceId": "c2ef539a-36bb-4669-808d-5e1edd9994c8",
    "skipped": false,
    "raisedDueToFailureResubmission": false,
    "childContextNames": [
      "CONTEXT--801284430"
    ],
    "contextName": "-1793100514"
  },
  "messageProperties": {
    "contextName": "-1793100514",
    "contextInstanceId": "c2ef539a-36bb-4669-808d-5e1edd9994c8"
  }
}
```

### Get all messages on a specific queue
| Parameter                            | Value                                                                                                                                                              | 
|--------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Request Method                       | GET                                                                                                                                                                |
| Service Name                         | Get messages from a specific queues                                                                                                                                |
| Service Description                  | This service returns an array of json payload containing the details of all messages on the queue                                                                  |
| Service Context                      | /rest/big/queue/messages/{queueName}                                                                                                                               |
| Required parameter queueName         | Name of the queue                                                                                                                                                  |
| Sample                               | https://localhost:9090/agent-module-name/rest/big/queue/messages/scheduler-agent--2050730871-inbound-queue                                                         |
| Requires 'Authorization' HTTP Header | Basic {TOKEN}                                                                                                                                                      |
| Returns                              | HTTP 200 status and array of json serialised [BigQueueMessage](../../../spec/service/big-queue/src/main/java/org/ikasan/spec/bigqueue/message/BigQueueMessage.java)|
**Sample Response**
```json 
[
  {
    "messageId": "73f15215-cf1f-4e01-943d-80a011efc89c",
    "createdTime": 1666174020053,
    "message": {
      "agentName": "scheduler-agent",
      "jobName": "-801284430_ScheduledJob_06:00:00",
      "jobGroup": "604791405-1666112601627",
      "jobDescription": "Quartz scheduled job - -801284430_ScheduledJob_06:00:00 firing on cron schedule (0 0 6 ? * * *)",
      "returnCode": 0,
      "successful": true,
      "pid": 0,
      "fireTime": 1666174020053,
      "nextFireTime": 1666174080000,
      "completionTime": 0,
      "harvested": false,
      "harvestedDateTime": 0,
      "dryRun": false,
      "jobStarting": false,
      "contextInstanceId": "c2ef539a-36bb-4669-808d-5e1edd9994c8",
      "skipped": false,
      "raisedDueToFailureResubmission": false,
      "childContextNames": [
        "CONTEXT--801284430"
      ],
      "contextName": "-1793100514"
    },
    "messageProperties": {
      "contextName": "-1793100514",
      "contextInstanceId": "c2ef539a-36bb-4669-808d-5e1edd9994c8"
    }
  },
  {
    "messageId": "ddd74498-4ec2-4de5-aadf-791235155411",
    "createdTime": 1666174140065,
    "message": {
      "agentName": "scheduler-agent",
      "jobName": "-801284430_ScheduledJob_06:00:00",
      "jobGroup": "604791405-1666112601627",
      "jobDescription": "Quartz scheduled job - -801284430_ScheduledJob_06:00:00 firing on cron schedule (0 0 6 ? * * *)",
      "returnCode": 0,
      "successful": true,
      "pid": 0,
      "fireTime": 1666174140064,
      "nextFireTime": 1666174200000,
      "completionTime": 0,
      "harvested": false,
      "harvestedDateTime": 0,
      "dryRun": false,
      "jobStarting": false,
      "contextInstanceId": "c2ef539a-36bb-4669-808d-5e1edd9994c8",
      "skipped": false,
      "raisedDueToFailureResubmission": false,
      "childContextNames": [
        "CONTEXT--801284430"
      ],
      "contextName": "-1793100514"
    },
    "messageProperties": {
      "contextName": "-1793100514",
      "contextInstanceId": "c2ef539a-36bb-4669-808d-5e1edd9994c8"
    }
  }
]
```

### Delete message from a queue
| Parameter                            | Value                                                                                                                                                              | 
|--------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Request Method                       | DELETE                                                                                                                                                             |
| Service Name                         | Delete message from a queue                                                                                                                                        |
| Service Description                  | Delete a message from a specific queue by a messageId from the [BigQueueMessage](../../../spec/service/big-queue/src/main/java/org/ikasan/spec/bigqueue/message/BigQueueMessage.java) |
| Service Context                      | /rest/big/queue/delete/{queueName}/{messageId}                                                                                                                     |
| Required parameter queueName         | Name of the queue                                                                                                                                                  |
| Required parameter messageId         | MessageId of the message to be removed                                                                                                                             |
| Sample                               | https://localhost:9090/agent-module-name/rest/big/queue/delete/scheduler-agent-111-inbound-queue/ddd74498-4ec2-4de5-aadf-791235155411                              |
| Requires 'Authorization' HTTP Header | Basic {TOKEN}                                                                                                                                                      |
| Returns                              | HTTP 200 status                                                                                                                                                    |

### Delete all messages from a queue
| Parameter                            | Value                                                                                                                                                              | 
|--------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Request Method                       | DELETE                                                                                                                                                             |
| Service Name                         | Delete all message from a queue                                                                                                                                    |
| Service Description                  | Delete all messages from a specific queue                                                                                                                          |
| Service Context                      | /rest/big/queue/delete/allMessages/{queueName}                                                                                                                     |
| Required parameter queueName         | Name of the queue                                                                                                                                                  |
| Sample                               | https://localhost:9090/agent-module-name/rest/big/queue/delete/allMessages/scheduler-agent-111-inbound-queue                                                       |
| Requires 'Authorization' HTTP Header | Basic {TOKEN}                                                                                                                                                      |
| Returns                              | HTTP 200 status                                                                                                                                                    |

### Delete a queue
| Parameter                            | Value                                                                                                                                                              | 
|--------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Request Method                       | DELETE                                                                                                                                                             |
| Service Name                         | Delete a queue                                                                                                                                                     |
| Service Description                  | Delete a queue. This included removing the underlying files and directories relating to the queue.                                                                 |
| Service Context                      | /rest/big/queue/delete/{queueName}                                                                                                                                 |
| Required parameter queueName         | Name of the queue                                                                                                                                                  |
| Sample                               | https://localhost:9090/agent-module-name/rest/big/queue/delete/scheduler-agent-111-inbound-queue                                                                   |
| Requires 'Authorization' HTTP Header | Basic {TOKEN}                                                                                                                                                      |
| Returns                              | HTTP 200 status                                                                                                                                                    |


## Managing Scheduler Agent queues through the Scheduler Dashboard
The scheduler dashboard exposes rest services to manage its connected scheduler agents big queues. The following are the services it exposes once authenticated through the scheduler dashboard. 

### Get all queue names for a scheduler agent
| Parameter                            | Value                                                                                                                                                              | 
|--------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Request Method                       | GET                                                                                                                                                                |
| Service Name                         | All Queue Name for a scheduler agent                                                                                                                               |
| Service Description                  | This service returns a list of all the queues that are set up for the agent                                                                                        |
| Service Context                      | /rest/module/bigQueue/{moduleName}                                                                                                                                 |
| Required parameter moduleName        | Name of the Scheduler agent you are looking to manage                                                                                                              |
| Sample                               | https://localhost:9090/rest/module/bigQueue/agent-module-name                                                                                                      |
| Requires 'Authorization' HTTP Header | Basic {TOKEN}                                                                                                                                                      |
| Returns                              | HTTP 200 status and json array list                                                                                                                                |
**Sample Response** 
```json 
[
   "scheduler-agent--958075417-inbound-queue",
   "scheduler-agent-509550682-inbound-queue",
   "scheduler-agent-153847922-inbound-queue"
]
```

### Get size of all queue from all modules registered to the scheduler dashboard
| Parameter                            | Value                                                                                                                                                              | 
|--------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Request Method                       | GET                                                                                                                                                                |
| Service Name                         | Get all modules queues sizes                                                                                                                                       |
| Service Description                  | This service returns a map of all queues and the number of message yet to be taken off the queue for all modules that are registered to the scheduler dashboard.   |
| Service Context                      | /rest/module/bigQueue/size/all/{includeZeros}/{moduleType}                                                                                                         |
| Required parameter includeZeros      | Boolean value _true_ or *false*. Set to _true_ to include zero queue size, set to _false_ to only include sizes greater than 0.                                    |
| Required parameter moduleType        | INTEGRATION_MODULE - search for integration modules only<br>SCHEDULER_AGENT - search for scheduler agents only<br>ALL - Search everything regardless of [ModuleType](../../../spec/module/src/main/java/org/ikasan/spec/module/ModuleType.java) |
| Sample                               | https://localhost:9090/rest/module/bigQueue/size/all/true/SCHEDULER_AGENT                                                                                          |
| Requires 'Authorization' HTTP Header | Basic {TOKEN}                                                                                                                                                      |
| Returns                              | HTTP 200 status and array of json serialised BigQueueModuleDto                                                                                                     |
**Sample Response**
```json 
[
  {
    "moduleName": "agent-module-name",
    "queueSizeMap": {
      "scheduler-agent--2050730871-inbound-queue": 0,
      "scheduler-agent-744167903-inbound-queue": 0,
      "scheduler-agent--66035914-inbound-queue": 0
    }
  }
]
```

### Get size of all queue for a scheduler agent
| Parameter                            | Value                                                                                                                                                              | 
|--------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Request Method                       | GET                                                                                                                                                                |
| Service Name                         | Size of all queues for a scheduler agent                                                                                                                           |
| Service Description                  | This service returns a map of all queues and the number of message yet to be taken off the queue. Option to just return queues with size > 0                       |
| Service Context                      | /rest/module/bigQueue/size/module/{includeZeros}/{moduleName}                                                                                                      |
| Required parameter moduleName        | Name of the Scheduler agent you are looking to manage                                                                                                              |
| Required parameter includeZeros      | Boolean value _true_ or *false*. Set to _true_ to include zero queue size, set to _false_ to only include sizes greater than 0.                                    |
| Sample                               | https://localhost:9090/rest/module/bigQueue/size/module/true/agent-module-name                                                                                     |
| Requires 'Authorization' HTTP Header | Basic {TOKEN}                                                                                                                                                      |
| Returns                              | HTTP 200 status and json map value pairs   |                                                                     


**Sample Response**
```json 
{
  "scheduler-agent--2050730871-inbound-queue": 0,
  "scheduler-agent-744167903-inbound-queue": 0,
  "scheduler-agent--66035914-inbound-queue": 1
}
```

### Get size of specific queue for a scheduler agent
| Parameter                            | Value                                                                                                                                                              | 
|--------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Request Method                       | GET                                                                                                                                                                |
| Service Name                         | Size of specific queues for a scheduler agent                                                                                                                      |
| Service Description                  | This service returns a numeric value of the amount of messages currently on the queue.                                                                             |
| Service Context                      | /rest/module/bigQueue/size/{queueName}/{moduleName}                                                                                                                |
| Required parameter moduleName        | Name of the Scheduler agent you are looking to manage                                                                                                              |
| Required parameter queueName         | Name of the queue                                                                                                                                                  |
| Sample                               | https://localhost:9090/rest/module/bigQueue/size/queue-inbound-name/agent-module-name                                                                              |
| Requires 'Authorization' HTTP Header | Basic {TOKEN}                                                                                                                                                      |
| Returns                              | HTTP 200 status and a numeric value                                                                                                                                |

**Sample Response**
```json 
5
```

### Peek a specific queue for a scheduler agent
| Parameter                            | Value                                                                                                                                                              | 
|--------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Request Method                       | GET                                                                                                                                                                |
| Service Name                         | Peek a specific queues for a scheduler agent                                                                                                                       |
| Service Description                  | This service returns a json payload containing the details of the first message on the queue                                                                       |
| Service Context                      | /rest/module/bigQueue/peek/{queueName}/{moduleName}                                                                                                                |
| Required parameter moduleName        | Name of the Scheduler agent you are looking to manage                                                                                                              |
| Required parameter queueName         | Name of the queue                                                                                                                                                  |
| Sample                               | https://localhost:9090/rest/module/bigQueue/peek/queue-inbound-name/agent-module-name                                                                              |
| Requires 'Authorization' HTTP Header | Basic {TOKEN}                                                                                                                                                      |
| Returns                              | HTTP 200 status and json serialised [BigQueueMessage](../../../spec/service/big-queue/src/main/java/org/ikasan/spec/bigqueue/message/BigQueueMessage.java)         |

**Sample Response**
```json 
{
  "messageId": "73f15215-cf1f-4e01-943d-80a011efc89c",
  "createdTime": 1666174020053,
  "message": {
    "agentName": "scheduler-agent",
    "jobName": "-801284430_ScheduledJob_06:00:00",
    "jobGroup": "604791405-1666112601627",
    "jobDescription": "Quartz scheduled job - -801284430_ScheduledJob_06:00:00 firing on cron schedule (0 0 6 ? * * *)",
    "returnCode": 0,
    "successful": true,
    "pid": 0,
    "fireTime": 1666174020053,
    "nextFireTime": 1666174080000,
    "completionTime": 0,
    "harvested": false,
    "harvestedDateTime": 0,
    "dryRun": false,
    "jobStarting": false,
    "contextInstanceId": "c2ef539a-36bb-4669-808d-5e1edd9994c8",
    "skipped": false,
    "raisedDueToFailureResubmission": false,
    "childContextNames": [
      "CONTEXT--801284430"
    ],
    "contextName": "-1793100514"
  },
  "messageProperties": {
    "contextName": "-1793100514",
    "contextInstanceId": "c2ef539a-36bb-4669-808d-5e1edd9994c8"
  }
}
```

### Get all messages on a specific queue for a scheduler agent
| Parameter                            | Value                                                                                                                                                              | 
|--------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Request Method                       | GET                                                                                                                                                                |
| Service Name                         | Get messages from a specific queues for a scheduler agent                                                                                                          |
| Service Description                  | This service returns an array of json payload containing the details of all messages on the queue                                                                  |
| Service Context                      | /rest/module/bigQueue/messages/{queueName}/{moduleName}                                                                                                            |
| Required parameter moduleName        | Name of the Scheduler agent you are looking to manage                                                                                                              |
| Required parameter queueName         | Name of the queue                                                                                                                                                  |
| Sample                               | https://localhost:9090/rest/dashboard/bigQueue/messages/queue-inbound-name/agent-module-name                                                                       |
| Requires 'Authorization' HTTP Header | Basic {TOKEN}                                                                                                                                                      |
| Returns                              | HTTP 200 status and array of json serialised [BigQueueMessage](../../../spec/service/big-queue/src/main/java/org/ikasan/spec/bigqueue/message/BigQueueMessage.java)|

**Sample Response**
```json 
[
  {
    "messageId": "73f15215-cf1f-4e01-943d-80a011efc89c",
    "createdTime": 1666174020053,
    "message": {
      "agentName": "scheduler-agent",
      "jobName": "-801284430_ScheduledJob_06:00:00",
      "jobGroup": "604791405-1666112601627",
      "jobDescription": "Quartz scheduled job - -801284430_ScheduledJob_06:00:00 firing on cron schedule (0 0 6 ? * * *)",
      "returnCode": 0,
      "successful": true,
      "pid": 0,
      "fireTime": 1666174020053,
      "nextFireTime": 1666174080000,
      "completionTime": 0,
      "harvested": false,
      "harvestedDateTime": 0,
      "dryRun": false,
      "jobStarting": false,
      "contextInstanceId": "c2ef539a-36bb-4669-808d-5e1edd9994c8",
      "skipped": false,
      "raisedDueToFailureResubmission": false,
      "childContextNames": [
        "CONTEXT--801284430"
      ],
      "contextName": "-1793100514"
    },
    "messageProperties": {
      "contextName": "-1793100514",
      "contextInstanceId": "c2ef539a-36bb-4669-808d-5e1edd9994c8"
    }
  },
  {
    "messageId": "ddd74498-4ec2-4de5-aadf-791235155411",
    "createdTime": 1666174140065,
    "message": {
      "agentName": "scheduler-agent",
      "jobName": "-801284430_ScheduledJob_06:00:00",
      "jobGroup": "604791405-1666112601627",
      "jobDescription": "Quartz scheduled job - -801284430_ScheduledJob_06:00:00 firing on cron schedule (0 0 6 ? * * *)",
      "returnCode": 0,
      "successful": true,
      "pid": 0,
      "fireTime": 1666174140064,
      "nextFireTime": 1666174200000,
      "completionTime": 0,
      "harvested": false,
      "harvestedDateTime": 0,
      "dryRun": false,
      "jobStarting": false,
      "contextInstanceId": "c2ef539a-36bb-4669-808d-5e1edd9994c8",
      "skipped": false,
      "raisedDueToFailureResubmission": false,
      "childContextNames": [
        "CONTEXT--801284430"
      ],
      "contextName": "-1793100514"
    },
    "messageProperties": {
      "contextName": "-1793100514",
      "contextInstanceId": "c2ef539a-36bb-4669-808d-5e1edd9994c8"
    }
  }
]
```

### Delete message from a queue for a scheduler agent
| Parameter                            | Value                                                                                                                                                              | 
|--------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Request Method                       | DELETE                                                                                                                                                             |
| Service Name                         | Delete message from a queue for a scheduler agent                                                                                                                  |
| Service Description                  | Delete a message from a specific queue by a messageId from the [BigQueueMessage](../../../spec/service/big-queue/src/main/java/org/ikasan/spec/bigqueue/message/BigQueueMessage.java) |
| Service Context                      | /rest/module/bigQueue/delete/{queueName}/{messageId}/{moduleName}                                                                                                  |
| Required parameter moduleName        | Name of the Scheduler agent you are looking to manage                                                                                                              |
| Required parameter queueName         | Name of the queue                                                                                                                                                  |
| Required parameter messageId         | MessageId of the message to be removed                                                                                                                             |
| Sample                               | https://localhost:9090/rest/module/bigQueue/delete/agent-inbound-queue/ddd74498-4ec2-4de5-aadf-791235155411/agent-module-name                                      |
| Requires 'Authorization' HTTP Header | Basic {TOKEN}                                                                                                                                                      |
| Returns                              | HTTP 200 status                                                                                                                                                    |

### Delete all messages from a queue for a scheduler agent
| Parameter                            | Value                                                                                                                                                              | 
|--------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Request Method                       | DELETE                                                                                                                                                             |
| Service Name                         | Delete all message from a queue for a scheduler agent                                                                                                              |
| Service Description                  | Delete all messages from a specific queue                                                                                                                          |
| Service Context                      | /rest/module/bigQueue/delete/allMessages/{queueName}/{moduleName}                                                                                                  |
| Required parameter moduleName        | Name of the Scheduler agent you are looking to manage                                                                                                              |
| Required parameter queueName         | Name of the queue                                                                                                                                                  |
| Sample                               | https://localhost:9090/rest/module/bigQueue/delete/allMessages/agent-inbound-queue/agent-module-name                                                               |
| Requires 'Authorization' HTTP Header | Basic {TOKEN}                                                                                                                                                      |
| Returns                              | HTTP 200 status                                                                                                                                                    |

## Managing queues through curl
A few services that are described above are accessible via curl

The following are available:

[Get size of all queue from all modules registered to the scheduler dashboard](#get-size-of-all-queue-from-all-modules-registered-to-the-scheduler-dashboard)

An example curl command that can be used is:
``` text
curl -u <username>:<password> http://dashboard-host:9090/rest/module/bigQueue/size/all/true/SCHEDULER_AGENT
curl -u <username>:<password> http://dashboard-host:9090/rest/module/bigQueue/size/all/true/INTEGRATION_MODULE
curl -u <username>:<password> http://dashboard-host:9090/rest/module/bigQueue/size/all/true/ALL   
```
