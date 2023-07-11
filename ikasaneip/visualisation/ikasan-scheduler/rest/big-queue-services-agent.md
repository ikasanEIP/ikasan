![IKASAN](../../../developer/docs/quickstart-images/Ikasan-title-transparent.png)

# Ikasan Enterprise Scheduler Agent Big Queue Services
Big Queue is the underlying queuing mechanism that Ikasan employs to provide reliable messaging between agents and the dashboard.
An Ikasan Enterprise Scheduler Agent exposes a number of services that allows clients to inspect and manage biq queues associated with the agent.

[Managing queues through the Scheduler Agent](#managing-queues-through-the-scheduler-agent)

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
