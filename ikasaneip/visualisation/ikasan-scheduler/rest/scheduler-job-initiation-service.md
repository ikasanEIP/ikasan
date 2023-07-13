![IKASAN](../../../developer/docs/quickstart-images/Ikasan-title-transparent.png)

# Ikasan Enterprise Scheduler Job Initiation Services
- [Scheduler Job Initiation Service](#scheduler-job-initiation-service)

## Scheduler Job Initiation Service
This service accepts a json String representation of [SchedulerJobInitiationEvent](../../../spec/service/scheduled/src/main/java/org/ikasan/spec/scheduled/event/model/SchedulerJobInitiationEvent.java)
and publishes it to an inbound BigQueue before forwarding it to the relevant job in order initiate the job.

| Parameter                            | Value                                                                                                                                                                                                     | 
|--------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Request Method                       | PUT                                                                                                                                                                                                       |
| Service Name                         | Scheduler Job Initiation Service                                                                                                                                                                          |
| Service Description                  | This service accepts a json String representation of [SchedulerJobInitiationEvent](../../../spec/service/scheduled/src/main/java/org/ikasan/spec/scheduled/event/model/SchedulerJobInitiationEvent.java). |
| Service Context                      | /rest/schedulerJobInitiation                                                                                                                                                                              |
| Sample                               | https://localhost:9090/rest/schedulerJobInitiation                                                                                                                                                        |
| Requires 'Authorization' HTTP Header | Basic {TOKEN}                                                                                                                                                                                             |
| Returns                              | HTTP 200 status                                                                                                                                                                                           |

### Sample Payload

```json
{
    "agentName":"agentName",
    "agentUrl":null,
    "jobName":"TEST",
    "contextParameters":null,
    "internalEventDrivenJob":
    {
        "successfulReturnCodes":null,
        "workingDirectory":null,
        "commandLine":null,
        "minExecutionTime":0,
        "maxExecutionTime":0,
        "contextParameters":[
            {
                "name":"name",
                "defaultValue":null,
                "value":"value"
            }
        ],
        "identifier":null,
        "agentName":null,
        "jobName":null,
        "jobDescription":null,
        "startupControlType":null,
        "daysOfWeekToRun":null,
        "contextInstanceId":null,
        "childContextName":null,
        "held":false,
        "skip":false,
        "initiationEventRaised":false,
        "status":null,
        "scheduledProcessEvent":
        {
            "id":null,
            "agentName":null,
            "agentHostname":null,
            "jobName":null,
            "jobGroup":null,
            "jobDescription":null,
            "commandLine":null,
            "returnCode":0,
            "successful":false,
            "outcome":null,
            "resultOutput":null,
            "resultError":null,
            "pid":0,
            "user":null,
            "fireTime":0,
            "nextFireTime":0,
            "completionTime":0,
            "dryRun":false,
            "contextName":null,
            "childContextNames":null,
            "contextInstanceId":null,
            "jobStarting":false,
            "dryRunParameters":null,
            "skipped":false,
            "internalEventDrivenJob":
            {
                "successfulReturnCodes":null,
                "workingDirectory":null,
                "commandLine":null,
                "minExecutionTime":0,
                "maxExecutionTime":0,
                "contextParameters":null,
                "identifier":null,
                "agentName":null,
                "jobName":null,
                "jobDescription":null,
                "startupControlType":null,
                "daysOfWeekToRun":null,
                "contextInstanceId":null,
                "childContextName":null,
                "held":false,
                "skip":false,
                "initiationEventRaised":false,
                "status":null,
                "scheduledProcessEvent":null,
                "targetResidingContextOnly":false,
                "participatesInLock":false,
                "executionEnvironmentProperties":null,
                "skippedContexts":null,
                "heldContexts":null,
                "jobRepeatable":false,
                "contextName":null,
                "childContextNames":null
            },
            "raisedDueToFailureResubmission":false,
            "executionDetails":null,
            "catalystEvent":null
        },
        "targetResidingContextOnly":false,
        "participatesInLock":false,
        "executionEnvironmentProperties":null,
        "skippedContexts":null,
        "heldContexts":null,
        "jobRepeatable":false,
        "contextName":null,
        "childContextNames":null
    },
    "contextName":null,
    "childContextNames":null,
    "contextInstanceId":null,
    "dryRun":false,
    "skipped":false,
    "catalystEvent":null,
    "dryRunParameters":null
}
```