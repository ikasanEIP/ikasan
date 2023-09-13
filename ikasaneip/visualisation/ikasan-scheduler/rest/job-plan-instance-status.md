![IKASAN](../../../developer/docs/quickstart-images/Ikasan-title-transparent.png)

# Job Plan and Job Instance Status Services
Ikasan exposes a number of services that allows client to inspect the state of running job plans and job instances associated with those plans.

- [Job Plan Status Service](#job-plan-status-service)
- [Job Instance Status Service](#job-instance-status-service)
- [All Job Plan Instance Status](#all-job-plan-instance-status-service)
- [Job Status Service](#job-status-service)

## Job Plan Status Service
| Parameter                            | Value                                                                                                                                                              | 
|--------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Request Method                       | GET                                                                                                                                                                |
| Service Name                         | Context Instance Status                                                                                                                                            |
| Service Description                  | This service returns a mapped json payload containing the detailed state of a Job Plan or a nested context within it for each running instances.                   |
| Service Context                      | /rest/context/status/json/{job-plan-name}/{context-name}                                                                                                           |
| Sample                               | https://localhost:9090/rest/context/status/json/-1793100514/CONTEXT-1799117601                                                                                     |
| Requires Path parameter job-plan-name   | The parent job plan name.                                                                                                                                          |
| Requires Path parameter context-name   | The nested context name. If this is the same as the job-plan-name, then the entire job plan will be return, otherwise the child will be.                           |
| Requires 'Authorization' HTTP Header | Basic {TOKEN}                                                                                                                                                      |
| Returns                              | HTTP 200 status and json serialised [ContextInstance](../../../spec/service/scheduled/src/main/java/org/ikasan/spec/scheduled/instance/model/ContextInstance.java) |

### Sample Curl Command
``` text
curl -u <username>:<password> http://localhost:9090/rest/context/status/json/-1793100514/CONTEXT-1799117601
```

### Sample Response
<details>
    <summary>Click to view the sample JSON payload response.</summary>
<p>

```json
{
  "ee05dd29-0733-4c51-a2e8-caf621534bed": {
    "contexts": [],
    "scheduledJobs": [
      {
        "agentName": "scheduler-agent",
        "jobName": "685048954",
        "startupControlType": "AUTOMATIC",
        "skip": false,
        "skippedContexts": {},
        "heldContexts": {},
        "held": false,
        "initiationEventRaised": false,
        "status": "WAITING",
        "identifier": "scheduler-agent-685048954"
      },
      {
        "agentName": "scheduler-agent",
        "jobName": "1799117601_ScheduledJob_13:45:00",
        "startupControlType": "AUTOMATIC",
        "skip": false,
        "skippedContexts": {},
        "heldContexts": {},
        "contextInstanceId": "8d49315b-6066-41aa-9908-a47bde55eaaf",
        "childContextName": "CONTEXT-1799117601",
        "held": false,
        "initiationEventRaised": false,
        "status": "COMPLETE",
        "scheduledProcessEvent": {
          "agentName": "scheduler-agent",
          "jobName": "1799117601_ScheduledJob_13:45:00",
          "jobGroup": "2082041823-1666007206447",
          "jobDescription": "Quartz scheduled job - 1799117601_ScheduledJob_13:45:00 firing on cron schedule (0 45 13 ? * MON,TUE,WED,THU,FRI *)",
          "returnCode": 0,
          "successful": true,
          "pid": 0,
          "fireTime": 1666010700296,
          "nextFireTime": 1666097100000,
          "completionTime": 0,
          "dryRun": false,
          "contextName": "-1793100514",
          "childContextNames": [
            "CONTEXT-1799117601"
          ],
          "contextInstanceId": "8d49315b-6066-41aa-9908-a47bde55eaaf",
          "jobStarting": false,
          "skipped": false,
          "raisedDueToFailureResubmission": false
        },
        "identifier": "scheduler-agent-1799117601_ScheduledJob_13:45:00"
      },
      {
        "agentName": "scheduler-agent",
        "jobName": "-1581215935",
        "startupControlType": "AUTOMATIC",
        "skip": false,
        "skippedContexts": {},
        "heldContexts": {},
        "held": false,
        "initiationEventRaised": false,
        "status": "WAITING",
        "identifier": "scheduler-agent--1581215935"
      }
    ],
    "jobLocks": [],
    "name": "CONTEXT-1799117601",
    "jobDependencies": [
      {
        "jobIdentifier": "scheduler-agent-685048954",
        "logicalGrouping": {
          "and": [
            {
              "identifier": "scheduler-agent-1799117601_ScheduledJob_13:45:00"
            },
            {
              "identifier": "scheduler-agent--1581215935"
            }
          ]
        }
      }
    ],
    "contextDependencies": [],
    "contextParameters": [],
    "id": "ee05dd29-0733-4c51-a2e8-caf621534bed",
    "createdDateTime": 1666007251406,
    "updatedDateTime": 1666010824635,
    "startTime": 0,
    "endTime": 0,
    "status": "RUNNING",
    "heldJobs": {}
  }
}
```

</p>
</details>

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
Map<String, ContextInstance> contextInstanceMap = ObjectMapperFactory.newInstance().readValue(json, new TypeReference<>() {});
```

## Job Instance Status Service
| Parameter                             | Value                                                                                                                                                                        | 
|---------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Request Method                        | GET                                                                                                                                                                          |
| Service Name                          | Job Instance Status                                                                                                                                                          |
| Service Description                   | This service returns a mapped json payload containing the detailed state of a job for all running instances.                                                                 |
| Service Context                       | /rest/context/status/json/{job-plan-name}/{context-name}//{job-name}                                                                                                         |
| Sample                                | https://localhost:9090/rest/context/status/json/-1793100514/CONTEXT-1799117601                                                                                               |
| Requires Path parameter job-plan-name | The parent job plan name.                                                                                                                                                    |
| Requires Path parameter context-name  | The nested context name. If this is the same as the job-plan-name, then the entire job plan will be return, otherwise the child will be.                                     |
| Requires Path parameter job-name      | The name of the job whose status will be returned.                                                                                                                           |
| Requires 'Authorization' HTTP Header  | Basic {TOKEN}                                                                                                                                                                |
| Returns                               | HTTP 200 status and json serialised [SchedulerJobInstance](../../../spec/service/scheduled/src/main/java/org/ikasan/spec/scheduled/instance/model/SchedulerJobInstance.java) |

### Sample Curl Command
``` text
curl -u <username>:<password> http://localhost:9090/rest/context/status/json/-1793100514/CONTEXT-1799117601/1799117601_ScheduledJob_13:45:00
```

### Sample Response
<details>
    <summary>Click to view the sample JSON payload response.</summary>
<p>

```json
{
  "8d49315b-6066-41aa-9908-a47bde55eaaf": {
    "agentName": "scheduler-agent",
    "jobName": "1799117601_ScheduledJob_13:45:00",
    "startupControlType": "AUTOMATIC",
    "skip": false,
    "skippedContexts": {},
    "heldContexts": {},
    "contextInstanceId": "8d49315b-6066-41aa-9908-a47bde55eaaf",
    "childContextName": "CONTEXT-1799117601",
    "held": false,
    "initiationEventRaised": false,
    "status": "COMPLETE",
    "scheduledProcessEvent": {
      "agentName": "scheduler-agent",
      "jobName": "1799117601_ScheduledJob_13:45:00",
      "jobGroup": "2106643654-1666087804609",
      "jobDescription": "Quartz scheduled job - 1799117601_ScheduledJob_13:45:00 firing on cron schedule (0 45 13 ? * MON,TUE,WED,THU,FRI *)",
      "returnCode": 0,
      "successful": true,
      "pid": 0,
      "fireTime": 1666097100049,
      "nextFireTime": 1666183500000,
      "completionTime": 0,
      "dryRun": false,
      "contextName": "-1793100514",
      "childContextNames": [
        "CONTEXT-1799117601"
      ],
      "contextInstanceId": "8d49315b-6066-41aa-9908-a47bde55eaaf",
      "jobStarting": false,
      "skipped": false,
      "raisedDueToFailureResubmission": false
    },
    "identifier": "scheduler-agent-1799117601_ScheduledJob_13:45:00"
  }
}
```
</p>
</details>

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
Map<String, SchedulerJobInstance> schedulerJobInstanceMap = ObjectMapperFactory.newInstance().readValue(json, new TypeReference<>() {});
```

## All Job Plan Instance Status Service
| Parameter                                  | Value                                                                                                                                                                                    | 
|--------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Request Method                             | GET                                                                                                                                                                                      |
| Service Name                               | All Job Plan Instance Status                                                                                                                                                             |
| Service Description                        | A simple response service to bring back a list of all instances, with its overall status and its context instance id value                                                               |
| Service Context                            | /rest/context/status/json/allInstance <br />/rest/context/status/json/allInstance?includePrepared={boolean}                                                                              |
| Sample                                     | https://localhost:9090/rest/context/status/json/allInstance?includePrepared=false                                                                                                        |
| Optional Request parameter includePrepared | boolean value - set to true to see prepared instance - default to false if not specified                                                                                                 |
| Requires 'Authorization' HTTP Header       | Basic {TOKEN}                                                                                                                                                                            |
| Returns                                    | HTTP 200 status and json serialised [ContextMachineStatusWrapper](../../../spec/service/scheduled/src/main/java/org/ikasan/spec/scheduled/status/model/ContextMachineStatusWrapper.java) |

### Sample Curl Command
``` text
curl -u <username>:<password> http://localhost:9090/rest/context/status/json/allInstance
```

### Sample Response
<details>
    <summary>Click to view the sample JSON payload response.</summary>
<p>

```json
{
    "contextMachineStatusList": [
        {
            "contextName": "PLAN_NAME_1",
            "contextInstanceId": "46366d95-a7a2-40c5-8f6e-db5f9a7c4240",
            "instanceStatus": "WAITING"
        },
        {
            "contextName": "PLAN_NAME_2",
            "contextInstanceId": "13f02153-11ea-4270-8ab8-0bb09bad8202",
            "instanceStatus": "COMPLETE"
        }
    ]
}
```

</p>
</details>

### Context Instance Implementation
If your consuming client runs on the Java ecosystem, you can import an implementation of a [ContextMachineStatusWrapper](../../../spec/service/scheduled/src/main/java/org/ikasan/spec/scheduled/status/model/ContextMachineStatusWrapper.java) into your project
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
ContextMachineStatusWrapperImpl status = ObjectMapperFactory.newInstance().readValue(json, ContextMachineStatusWrapperImpl.class);
```

## Job Status Service
| Parameter                                 | Value                                                                                                                                                                                                                                                                            | 
|-------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Request Method                            | GET                                                                                                                                                                                                                                                                              |
| Service Name                              | Job Status Service                                                                                                                                                                                                                                                               |
| Service Description                       | Get the job status for all job plans currently running in the scheduler, with an optional setting to target a specific plan or specific status                                                                                                                                   |
| Service Context                           | /rest/context/status/json/jobStatus <br/>/rest/context/status/json/jobStatus?instanceStatus={instanceStatus} <br />/rest/context/status/json/jobStatus/{job-plan-name} <br /> /rest/context/status/json/jobStatus/{job-plan-name}?instanceStatus=instanceStatus={instanceStatus} |
| Sample                                    | https://localhost:9090/rest/context/status/json/jobStatus                                                                                                                                                                                                                        |
| Optional Path parameter job-plan-name     | The parent job plan name.                                                                                                                                                                                                                                                        |
| Optional Request parameter instanceStatus | Allowed string value from [InstanceStatus](../../../spec/service/scheduled/src/main/java/org/ikasan/spec/scheduled/instance/model/InstanceStatus.java)                                                                                                                           |
| Requires 'Authorization' HTTP Header      | Basic {TOKEN}                                                                                                                                                                                                                                                                    |
| Returns                                   | HTTP 200 status and json serialised [ContextJobInstanceStatusWrapper](../../../spec/service/scheduled/src/main/java/org/ikasan/spec/scheduled/status/model/ContextJobInstanceStatusWrapper.java)                                                                                 |

### Sample Curl Command
``` text
curl -u <username>:<password> http://localhost:9090/rest/context/status/json/jobStatus
```

### Sample Response
<details>
    <summary>Click to view the sample JSON payload response.</summary>
<p>

```json
{
    "jobPlans": [
        {
            "contextName": "CONTEXT-125233534",
            "contextInstanceId": "13f02153-11ea-4270-8ab8-0bb09bad8202",
            "instanceStatus": "WAITING",
            "jobDetails": [
                {
                    "jobName": "JOB1",
                    "childContextName": [
                        "CONTEXT-125233535"
                    ],
                    "instanceStatus": "COMPLETE",
                    "targetResidingContextOnly": false,
                    "startTime": 1694594220025,
                    "endTime": 1694594220025
                },
                {
                    "jobName": "JOB2",
                    "childContextName": [
                        "CONTEXT-125233535"
                    ],
                    "instanceStatus": "ERROR",
                    "targetResidingContextOnly": false,
                    "startTime": 1694593577221,
                    "endTime": 1694593577259
                }
            ]
        }
    ]
}
```

</p>
</details>

### Context Instance Implementation
If your consuming client runs on the Java ecosystem, you can import an implementation of a [ContextJobInstanceStatusWrapper](../../../spec/service/scheduled/src/main/java/org/ikasan/spec/scheduled/status/model/ContextJobInstanceStatusWrapper.java) into your project
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
ContextJobInstanceStatusWrapper jobStatus = ObjectMapperFactory.newInstance().readValue(json, ContextJobInstanceStatusWrapperImpl.class);
```
