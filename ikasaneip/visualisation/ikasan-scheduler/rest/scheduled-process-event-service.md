![IKASAN](../../../developer/docs/quickstart-images/Ikasan-title-transparent.png)

# Ikasan Enterprise Scheduled Process Event Services

The Ikasan Enterprise Scheduler Dashboard exposes a number of rest endpoints that consume events relating to the processing of
scheduler jobs.

- [Harvest Scheduled Events Service](#harvest-scheduled-events-service)
- [Scheduled Event Service](#scheduled-event-service)
- [Global Event Service](#global-event-service)

## Harvest Scheduled Events Service
This service accepts a json String representation of a List of [ScheduledProcessEvent](../../../spec/service/scheduled/src/main/java/org/ikasan/spec/scheduled/event/model/ScheduledProcessEvent.java)
and writes the events to the underlying document store.

| Parameter                            | Value                                                                                                                                                                                         | 
|--------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Request Method                       | PUT                                                                                                                                                                                           |
| Service Name                         | Harvest Scheduled Events                                                                                                                                                                      |
| Service Description                  | This service accepts a json String representation of a List of [ScheduledProcessEvent](../../../spec/service/scheduled/src/main/java/org/ikasan/spec/scheduled/event/model/ScheduledProcessEvent.java). |
| Service Context                      | /rest/harvest/scheduled                                                                                                                                                                       |
| Sample                               | https://localhost:9090/rest/harvest/scheduled                                                                                                                                                 |
| Requires 'Authorization' HTTP Header | Basic {TOKEN}                                                                                                                                                                                 |
| Returns                              | HTTP 200 status                                                                                                                                                                               |

### Sample Payload
<details>
    <summary>Click to view the sample JSON payload.</summary>
<p>

```json
[
    {
        "id": "f82fa805-7f2e-4abb-8fff-9196fb75126e",
        "agentName": "agentName",
        "jobName": "jobName",
        "jobGroup": "jobGroup",
        "jobDescription": "jodDescription",
        "commandLine": "commandLine",
        "result": 1,
        "resultOutput": "output",
        "resultError": null,
        "pid": 111111,
        "user": "user",
        "fireTime": 1000,
        "nextFireTime": 2000,
        "harvested": false,
        "harvestedDateTime": 0
    },
    {
        "id": "f82fa805-7f2e-4abb-8fff-9196fb87436a",
        "agentName": "agentName",
        "jobName": "jobName",
        "jobGroup": "jobGroup",
        "jobDescription": "jodDescription",
        "commandLine": "commandLine",
        "result": 1,
        "resultOutput": "output",
        "resultError": null,
        "pid": 111112,
        "user": "user",
        "fireTime": 1000,
        "nextFireTime": 2000,
        "harvested": false,
        "harvestedDateTime": 0
    },
    {
        "id": "f82fa805-7f2e-4abb-8fff-9196fb73456c",
        "agentName": "agentName",
        "jobName": "jobName",
        "jobGroup": "jobGroup",
        "jobDescription": "jodDescription",
        "commandLine": "commandLine",
        "result": 1,
        "resultOutput": "output",
        "resultError": null,
        "pid": 111113,
        "user": "user",
        "fireTime": 1000,
        "nextFireTime": 2000,
        "harvested": false,
        "harvestedDateTime": 0
    },
    {
        "id": "f82fa805-7f2e-4abb-8fff-9196fb3678b",
        "agentName": "agentName",
        "jobName": "jobName",
        "jobGroup": "jobGroup",
        "jobDescription": "jodDescription",
        "commandLine": "commandLine",
        "result": 1,
        "resultOutput": "output",
        "resultError": null,
        "pid": 111114,
        "user": "user",
        "fireTime": 1000,
        "nextFireTime": 2000,
        "harvested": false,
        "harvestedDateTime": 0
    },
    {
        "id": "f82fa805-7f2e-4abb-8fff-9196fb4102e",
        "agentName": "agentName",
        "jobName": "jobName",
        "jobGroup": "jobGroup",
        "jobDescription": "jodDescription",
        "commandLine": "commandLine",
        "result": 1,
        "resultOutput": "output",
        "resultError": null,
        "pid": 111115,
        "user": "user",
        "fireTime": 1000,
        "nextFireTime": 2000,
        "harvested": false,
        "harvestedDateTime": 0
    },
    {
        "id": "f82fa805-7f2e-4abb-8fff-9196fb78191d",
        "agentName": "agentName",
        "jobName": "jobName",
        "jobGroup": "jobGroup",
        "jobDescription": "jodDescription",
        "commandLine": "commandLine",
        "result": 1,
        "resultOutput": "output",
        "resultError": null,
        "pid": 111116,
        "user": "user",
        "fireTime": 1000,
        "nextFireTime": 2000,
        "harvested": false,
        "harvestedDateTime": 0
    },
    {
        "id": "f82fa805-7f2e-4abb-8fff-9196fb72214a",
        "agentName": "agentName",
        "jobName": "jobName",
        "jobGroup": "jobGroup",
        "jobDescription": "jodDescription",
        "commandLine": "commandLine",
        "result": 1,
        "resultOutput": "output",
        "resultError": null,
        "pid": 111117,
        "user": "user",
        "fireTime": 1000,
        "nextFireTime": 2000,
        "harvested": false,
        "harvestedDateTime": 0
    },
    {
        "id": "f82fa805-7f2e-4abb-8fff-9196fb11267b",
        "agentName": "agentName",
        "jobName": "jobName",
        "jobGroup": "jobGroup",
        "jobDescription": "jodDescription",
        "commandLine": "commandLine",
        "result": 1,
        "resultOutput": "output",
        "resultError": null,
        "pid": 111118,
        "user": "user",
        "fireTime": 1000,
        "nextFireTime": 2000,
        "harvested": false,
        "harvestedDateTime": 0
    }
]

```
</p>
</details>

## Scheduled Event Service
This service accepts a json String representation of [ContextualisedScheduledProcessEvent](../../../spec/service/scheduled/src/main/java/org/ikasan/spec/scheduled/event/model/ContextualisedScheduledProcessEvent.java)
and enqueues it onto the inbound BiqQueue in order for it to be processed by the relevant ContextMachine.

| Parameter                            | Value                                                                                                                                                                                                                     | 
|--------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Request Method                       | PUT                                                                                                                                                                                                                       |
| Service Name                         | Scheduled Event                                                                                                                                                                                                           |
| Service Description                  | This service accepts a json String representation of [ContextualisedScheduledProcessEvent](../../../spec/service/scheduled/src/main/java/org/ikasan/spec/scheduled/event/model/ContextualisedScheduledProcessEvent.java). |
| Service Context                      | /rest/event/scheduled                                                                                                                                                                                                     |
| Sample                               | https://localhost:9090/rest/event/scheduled                                                                                                                                                                               |
| Requires 'Authorization' HTTP Header | Basic {TOKEN}                                                                                                                                                                                                             |
| Returns                              | HTTP 200 status and zipped job plan                                                                                                                                                                                       |

### Sample Payload
<details>
    <summary>Click to view the sample JSON payload.</summary>
<p>

```json
{
    "id" : null,
    "agentName" : "scheduler-agent",
    "agentHostname" : null,
    "jobName" : "SampleCommandExecutionJob1",
    "jobGroup" : null,
    "jobDescription" : null,
    "commandLine" : null,
    "returnCode" : 0,
    "successful" : false,
    "outcome" : "EXECUTION_INVOKED",
    "resultOutput" : "/opt/platform/runtime/scheduler-agent-3.3.0-SNAPSHOT/./process-logs/MyFirstJobPlan-07427ffd-1dc9-412e-9611-3ed3f164cbb3-scheduler-agent-SampleCommandExecutionJob1-1689228002160-out.log",
    "resultError" : "/opt/platform/runtime/scheduler-agent-3.3.0-SNAPSHOT/./process-logs/MyFirstJobPlan-07427ffd-1dc9-412e-9611-3ed3f164cbb3-scheduler-agent-SampleCommandExecutionJob1-1689228002160-err.log",
    "pid" : 2936,
    "user" : null,
    "fireTime" : 1689228002160,
    "nextFireTime" : 0,
    "completionTime" : 0,
    "dryRun" : false,
    "contextName" : "MyFirstJobPlan",
    "childContextNames" : [ "MyFirstJobPlan", "ScriptProcessingContext" ],
    "contextInstanceId" : "07427ffd-1dc9-412e-9611-3ed3f164cbb3",
    "jobStarting" : true,
    "dryRunParameters" : null,
    "skipped" : false,
    "internalEventDrivenJob" : {
        "agentName" : "scheduler-agent",
        "jobName" : "SampleCommandExecutionJob1",
        "jobDescription" : "This is a simple script to demonstrate how command execution jobs work.",
        "contextName" : "MyFirstJobPlan",
        "childContextNames" : [ "MyFirstJobPlan", "ScriptProcessingContext" ],
        "startupControlType" : "AUTOMATIC",
        "skippedContexts" : { },
        "heldContexts" : { },
        "contextInstanceId" : "07427ffd-1dc9-412e-9611-3ed3f164cbb3",
        "childContextName" : "MyFirstJobPlan",
        "held" : false,
        "initiationEventRaised" : false,
        "status" : "WAITING",
        "scheduledProcessEvent" : null,
        "successfulReturnCodes" : null,
        "workingDirectory" : null,
        "commandLine" : "echo \"Running Job :- SampleCommandExecutionJob1\"\nls -la\nsleep 10\necho $sample_param",
        "minExecutionTime" : 10,
        "maxExecutionTime" : 1000000,
        "contextParameters" : [ {
            "name" : "sample_param",
            "defaultValue" : "sample default value"
        } ],
        "daysOfWeekToRun" : null,
        "targetResidingContextOnly" : false,
        "executionEnvironmentProperties" : null,
        "participatesInLock" : false,
        "jobRepeatable" : false,
        "identifier" : null
    },
    "raisedDueToFailureResubmission" : false,
    "executionDetails" : "\nExecuting Job -> Context Name[MyFirstJobPlan] Job Name[SampleCommandExecutionJob1]\n\nJob Parameters -> \nName[sample_param] Value[sample default value]\nName[LOG_FILE_PATH] Value[/opt/platform/runtime/scheduler-agent-3.3.0-SNAPSHOT/./process-logs/MyFirstJobPlan-07427ffd-1dc9-412e-9611-3ed3f164cbb3-scheduler-agent-SampleCommandExecutionJob1-1689228002160-out.log]\nName[ERROR_LOG_FILE_PATH] Value[/opt/platform/runtime/scheduler-agent-3.3.0-SNAPSHOT/./process-logs/MyFirstJobPlan-07427ffd-1dc9-412e-9611-3ed3f164cbb3-scheduler-agent-SampleCommandExecutionJob1-1689228002160-err.log]\n\nProcess Command -> \n/bin/bash\n-c\necho \"Running Job :- SampleCommandExecutionJob1\"\nls -la\nsleep 10\necho $sample_param\n",
    "catalystEvent" : {
        "id" : null,
        "agentName" : "scheduler-agent",
        "agentHostname" : null,
        "jobName" : "7amScheduledEvent",
        "jobGroup" : "1315391353-1689175992670",
        "jobDescription" : "This event fires at 7am Monday through Friday.",
        "commandLine" : null,
        "returnCode" : 0,
        "successful" : true,
        "outcome" : null,
        "resultOutput" : null,
        "resultError" : null,
        "pid" : 0,
        "user" : null,
        "fireTime" : 1689228000010,
        "nextFireTime" : 1689314400000,
        "completionTime" : 0,
        "dryRun" : false,
        "contextName" : "MyFirstJobPlan",
        "childContextNames" : [ "MyFirstJobPlan" ],
        "contextInstanceId" : "07427ffd-1dc9-412e-9611-3ed3f164cbb3",
        "jobStarting" : false,
        "dryRunParameters" : null,
        "skipped" : false,
        "internalEventDrivenJob" : null,
        "raisedDueToFailureResubmission" : false,
        "executionDetails" : null,
        "catalystEvent" : null
    }
}
```
</p>
</details>

## Global Event Service
This service accepts a global event name. That global events in then raised against all running ContextMachines.

| Parameter                            | Value                                                                       | 
|--------------------------------------|-----------------------------------------------------------------------------|
| Request Method                       | PUT                                                                         |
| Service Name                         | Global Event                                                                |
| Service Description                  | This service accepts a json String representation of the global event name. |
| Service Context                      | /rest/event/globalScheduled                                                 |
| Sample                               | https://localhost:9090/rest/harvest/globalScheduled                         |
| Requires 'Authorization' HTTP Header | Basic {TOKEN}                                                               |
| Returns                              | HTTP 200 status and zipped job plan                                         |
