![IKASAN](../../../developer/docs/quickstart-images/Ikasan-title-transparent.png)

# Job Plan and Job Instance Status Services
Ikasan exposes a number of services that allows client to inspect the state of running job plans and job instances associated with those plans.

## Job Plan Status Service
| Parameter                            | Value                                                                                                                                                              | 
|--------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Request Method                       | GET                                                                                                                                                                |
| Service Name                         | Context Instance Status                                                                                                                                            |
| Service Description                  | This service returns a json payload containing the detailed state of a Job Plan or a nested context within it.                                                     |
| Service Context                      | /rest/context/status/json/{job-plan-name}/{context-name}                                                                                                           |
| Sample                               | https://localhost:9090/rest/context/status/json/-1793100514/CONTEXT-1799117601                                                                                     |
| Requires Path parameter job-plan-name   | The parent job plan name.                                                                                                                                          |
| Requires Path parameter context-name   | The nested context name. If this is the same as the job-plan-name, then the entire job plan will be return, otherwise the child will be.                           |
| Requires 'Authorization' HTTP Header | Basic {TOKEN}                                                                                                                                                      |
| Returns                              | HTTP 200 status and json serialised [ContextInstance](../../../spec/service/scheduled/src/main/java/org/ikasan/spec/scheduled/instance/model/ContextInstance.java)       |

### Sample Curl Command
``` text
curl -u <username>:<password> http://localhost:9090/rest/context/status/json/-1793100514/CONTEXT-1799117601
```

### Sample Response
```json
{
    "contexts" : [ ],
    "scheduledJobs" : [ {
        "agentName" : "scheduler-agent",
        "jobName" : "685048954",
        "startupControlType" : "AUTOMATIC",
        "skip" : false,
        "skippedContexts" : { },
        "heldContexts" : { },
        "held" : false,
        "initiationEventRaised" : false,
        "status" : "WAITING",
        "identifier" : "scheduler-agent-685048954"
    }, {
        "agentName" : "scheduler-agent",
        "jobName" : "1799117601_ScheduledJob_13:45:00",
        "startupControlType" : "AUTOMATIC",
        "skip" : false,
        "skippedContexts" : { },
        "heldContexts" : { },
        "contextInstanceId" : "8d49315b-6066-41aa-9908-a47bde55eaaf",
        "childContextName" : "CONTEXT-1799117601",
        "held" : false,
        "initiationEventRaised" : false,
        "status" : "COMPLETE",
        "scheduledProcessEvent" : {
            "agentName" : "scheduler-agent",
            "jobName" : "1799117601_ScheduledJob_13:45:00",
            "jobGroup" : "2082041823-1666007206447",
            "jobDescription" : "Quartz scheduled job - 1799117601_ScheduledJob_13:45:00 firing on cron schedule (0 45 13 ? * MON,TUE,WED,THU,FRI *)",
            "returnCode" : 0,
            "successful" : true,
            "pid" : 0,
            "fireTime" : 1666010700296,
            "nextFireTime" : 1666097100000,
            "completionTime" : 0,
            "dryRun" : false,
            "contextName" : "-1793100514",
            "childContextNames" : [ "CONTEXT-1799117601" ],
            "contextInstanceId" : "8d49315b-6066-41aa-9908-a47bde55eaaf",
            "jobStarting" : false,
            "skipped" : false,
            "raisedDueToFailureResubmission" : false
        },
        "identifier" : "scheduler-agent-1799117601_ScheduledJob_13:45:00"
    }, {
        "agentName" : "scheduler-agent",
        "jobName" : "-1581215935",
        "startupControlType" : "AUTOMATIC",
        "skip" : false,
        "skippedContexts" : { },
        "heldContexts" : { },
        "held" : false,
        "initiationEventRaised" : false,
        "status" : "WAITING",
        "identifier" : "scheduler-agent--1581215935"
    } ],
    "jobLocks" : [ ],
    "name" : "CONTEXT-1799117601",
    "jobDependencies" : [ {
        "jobIdentifier" : "scheduler-agent-685048954",
        "logicalGrouping" : {
            "and" : [ {
                "identifier" : "scheduler-agent-1799117601_ScheduledJob_13:45:00"
            }, {
                "identifier" : "scheduler-agent--1581215935"
            } ]
        }
    } ],
    "contextDependencies" : [ ],
    "contextParameters" : [ ],
    "id" : "ee05dd29-0733-4c51-a2e8-caf621534bed",
    "createdDateTime" : 1666007251406,
    "updatedDateTime" : 1666010824635,
    "startTime" : 0,
    "endTime" : 0,
    "status" : "RUNNING",
    "heldJobs" : { }
}
```
### Context Instance Implementation
If your consuming client runs on the Java ecosystem, you can import an implementation of a [ContextInstance](../../../spec/service/scheduled/src/main/java/org/ikasan/spec/scheduled/instance/model/ContextInstance.java) into your project
using the following pom.
``` xml
<parent>
    <groupId>org.ikasan</groupId>
    <artifactId>ikasan-job-orchestration-model</artifactId>
    <version>${ikasan-version}</version>
</parent>
```

The following snippet of code will de-serialise the response json.
``` java
ContextInstance contextInstance = ObjectMapperFactory.newInstance().readValue(json, ContextInstanceImpl.class);
```

## Job Instance Status Service
| Parameter                             | Value                                                                                                                                                              | 
|---------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Request Method                        | GET                                                                                                                                                                |
| Service Name                          | Job Instance Status                                                                                                                                                |
| Service Description                   | This service returns a json payload containing the detailed state of a job instance.                                                                               |
| Service Context                       | /rest/context/status/json/{job-plan-name}/{context-name}//{job-name}                                                                                               |
| Sample                                | https://localhost:9090/rest/context/status/json/-1793100514/CONTEXT-1799117601                                                                                     |
| Requires Path parameter job-plan-name | The parent job plan name.                                                                                                                                          |
| Requires Path parameter context-name  | The nested context name. If this is the same as the job-plan-name, then the entire job plan will be return, otherwise the child will be.                           |
| Requires Path parameter job-name      | The name of the job whose status will be returned.                                                                                                                 |
| Requires 'Authorization' HTTP Header  | Basic {TOKEN}                                                                                                                                                      |
| Returns                               | HTTP 200 status and json serialised [SchedulerJobInstance](../../../spec/service/scheduled/src/main/java/org/ikasan/spec/scheduled/instance/model/SchedulerJobInstance.java) |

### Sample Curl Command
``` text
curl -u <username>:<password> http://localhost:9090/rest/context/status/json/-1793100514/CONTEXT-1799117601/1799117601_ScheduledJob_13:45:00
```

### Sample Response
```json
{
    "agentName" : "scheduler-agent",
    "jobName" : "1799117601_ScheduledJob_13:45:00",
    "startupControlType" : "AUTOMATIC",
    "skip" : false,
    "skippedContexts" : { },
    "heldContexts" : { },
    "contextInstanceId" : "8d49315b-6066-41aa-9908-a47bde55eaaf",
    "childContextName" : "CONTEXT-1799117601",
    "held" : false,
    "initiationEventRaised" : false,
    "status" : "COMPLETE",
    "scheduledProcessEvent" : {
        "agentName" : "scheduler-agent",
        "jobName" : "1799117601_ScheduledJob_13:45:00",
        "jobGroup" : "2106643654-1666087804609",
        "jobDescription" : "Quartz scheduled job - 1799117601_ScheduledJob_13:45:00 firing on cron schedule (0 45 13 ? * MON,TUE,WED,THU,FRI *)",
        "returnCode" : 0,
        "successful" : true,
        "pid" : 0,
        "fireTime" : 1666097100049,
        "nextFireTime" : 1666183500000,
        "completionTime" : 0,
        "dryRun" : false,
        "contextName" : "-1793100514",
        "childContextNames" : [ "CONTEXT-1799117601" ],
        "contextInstanceId" : "8d49315b-6066-41aa-9908-a47bde55eaaf",
        "jobStarting" : false,
        "skipped" : false,
        "raisedDueToFailureResubmission" : false
    },
    "identifier" : "scheduler-agent-1799117601_ScheduledJob_13:45:00"
}
```
### Context Instance Implementation
If your consuming client runs on the Java ecosystem, you can import an implementation of a [SchedulerJobInstance](../../../spec/service/scheduled/src/main/java/org/ikasan/spec/scheduled/instance/model/SchedulerJobInstance.java) into your project
using the following pom.
``` xml
<parent>
    <groupId>org.ikasan</groupId>
    <artifactId>ikasan-job-orchestration-model</artifactId>
    <version>${ikasan-version}</version>
</parent>
```

The following snippet of code will de-serialise the response json.
``` java
SchedulerJobInstance schedulerJobInstance = ObjectMapperFactory.newInstance().readValue(json, SchedulerJobInstanceImpl.class);
```
