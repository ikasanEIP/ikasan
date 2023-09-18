![IKASAN](../../../../developer/docs/quickstart-images/Ikasan-title-transparent.png)

# Ikasan Enterprise Scheduler - Job Plan Instance Data Model

## Introduction
This page describes how the job plan instance data model is defined.

## Job Plan Instance
A job instance is an active job plan. It has a temporal nature to it that is governed by the cron expression that tells it when 
to start, as well as a time to live value that defines how long the job plan instance will remain active. Users can interact 
with a job plan instance via the Ikasan Enterprise Scheduler Dashboard and can perorm actions such as skipping or holding 
jobs as well as monitor the status of the job plan instance as jobs are executed within the job plan instance.
A job plan instance is modelled as a[ContextInstance](../../../../spec/service/scheduled/src/main/java/org/ikasan/spec/scheduled/instance/model/ContextInstance.java) which is an extension
of a [ContextTemplate](../../../../spec/service/scheduled/src/main/java/org/ikasan/spec/scheduled/context/model/ContextTemplate.java) with the following
additional fields.

| Field            | Type     | Description                                                                                                       |
|------------------|----------|-------------------------------------------------------------------------------------------------------------------|
| id               | 	String	 | The unique identifier of the context instance.                                                                    |
| createdTime      | 	Long    | 	Time that the instance was created in milliseconds since epoch.                                                  |
| updatedTime      | 	Long    | 	Last time that the instance updated in milliseconds since epoch.                                                 |
| startTime        | 	Long    | 	Start time of the instance in milliseconds since epoch.                                                          |
| endTime          | 	Long    | 	End time of the instance in milliseconds since epoch.                                                            |
| projectedEndTime | Long     | The project end time calculated from the timeWindowStart cron expression and the job plan duration.               |
| timezone         | 	String  | 	Timezone in which the context instance was run.                                                                  |
| isRunContextUntilManuallyEnded                 | boolean  | Flag to indicate that the context instance will ignore the projectedEndTime and is required to be manually ended. |
| status	          | String	  | The overall status of the instance.                                                                               |

## ContextParameterInstance
A [ContextParameterInstance](../../../../spec/service/scheduled/src/main/java/org/ikasan/spec/scheduled/instance/model/ContextParameterInstance.java) is an extension
of a [ContextParameter](../../../../spec/service/scheduled/src/main/java/org/ikasan/spec/scheduled/context/model/ContextParameter.java) with the following
additional fields.

| Field            | Type     | Description                                                                                                       |
|------------------|----------|-------------------------------------------------------------------------------------------------------------------|
|value|	String|	The value of the contextParameter instance.	|

## SchedulerJobInstance
A [SchedulerJobInstance](../../../../spec/service/scheduled/src/main/java/org/ikasan/spec/scheduled/instance/model/SchedulerJobInstance.java) is an extension
of a [SchedulerJob](../../../../spec/service/scheduled/src/main/java/org/ikasan/spec/scheduled/job/model/SchedulerJob.java) with the following
additional fields.

| Field                 | Type                                                                                                                            | Description                                                                      |
|-----------------------|---------------------------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------|
| contextInstanceId     | String                                                                                                                          | The unique identifier context instance that the job instance is associated with. |
| childContextName      | String                                                                                                                          | The child context name that the job resides within in the context instance.      |
| held                  | 	Boolean                                                                                                                        | 	Boolean flag to indicate if the job is held.                                    |
| skip                  | 	Boolean                                                                                                                        | 	Boolean flag to indicate if the job is skipped.                                 |
| initiationEventRaised | Boolean                                                                                                                         | Boolean flag to indicate if an initiation event has been raised by the job.      |
| status                | [InstanceStatus](../../../../spec/service/scheduled/src/main/java/org/ikasan/spec/scheduled/instance/model/InstanceStatus.java) | The status of the job.                                                           |
| scheduledProcessEvent |	scheduledProcessEvent| 	The event representing the state of the job after it has been executed.         |

## Putting it All Together
```json
{
    "contexts" : [ {
        "contexts" : [ ],
        "scheduledJobs" : [ {
            "agentName" : "scheduler-agent",
            "jobName" : "SampleCommandExecutionJob2",
            "jobDescription" : "This is a simple script to demonstrate how command execution jobs work.",
            "contextName" : "MyFirstJobPlan",
            "childContextNames" : [ ],
            "startupControlType" : "AUTOMATIC",
            "skippedContexts" : { },
            "heldContexts" : { },
            "childContextName" : "ScriptProcessingContext",
            "held" : false,
            "initiationEventRaised" : false,
            "status" : "WAITING",
            "identifier" : "scheduler-agent-SampleCommandExecutionJob2"
        }, {
            "agentName" : "scheduler-agent",
            "jobName" : "SampleCommandExecutionJob3",
            "jobDescription" : "This is a simple script to demonstrate how command execution jobs work.",
            "contextName" : "MyFirstJobPlan",
            "childContextNames" : [ ],
            "startupControlType" : "AUTOMATIC",
            "skippedContexts" : { },
            "heldContexts" : { },
            "childContextName" : "ScriptProcessingContext",
            "held" : false,
            "initiationEventRaised" : false,
            "status" : "WAITING",
            "identifier" : "scheduler-agent-SampleCommandExecutionJob3"
        }, {
            "agentName" : "scheduler-agent",
            "jobName" : "SampleCommandExecutionJob4",
            "jobDescription" : "This is a simple script to demonstrate how command execution jobs work.",
            "contextName" : "MyFirstJobPlan",
            "childContextNames" : [ ],
            "startupControlType" : "AUTOMATIC",
            "skippedContexts" : { },
            "heldContexts" : { },
            "childContextName" : "ScriptProcessingContext",
            "held" : false,
            "initiationEventRaised" : false,
            "status" : "WAITING",
            "identifier" : "scheduler-agent-SampleCommandExecutionJob4"
        }, {
            "agentName" : "scheduler-agent",
            "jobName" : "SampleCommandExecutionJob6",
            "jobDescription" : "This is a simple script to demonstrate how command execution jobs work.",
            "contextName" : "MyFirstJobPlan",
            "childContextNames" : [ ],
            "startupControlType" : "AUTOMATIC",
            "skippedContexts" : { },
            "heldContexts" : { },
            "childContextName" : "ScriptProcessingContext",
            "held" : false,
            "initiationEventRaised" : false,
            "status" : "WAITING",
            "identifier" : "scheduler-agent-SampleCommandExecutionJob6"
        }, {
            "agentName" : "scheduler-agent",
            "jobName" : "SampleCommandExecutionJob7",
            "jobDescription" : "This is a simple script to demonstrate how command execution jobs work.",
            "contextName" : "MyFirstJobPlan",
            "childContextNames" : [ ],
            "startupControlType" : "AUTOMATIC",
            "skippedContexts" : { },
            "heldContexts" : { },
            "childContextName" : "ScriptProcessingContext",
            "held" : false,
            "initiationEventRaised" : false,
            "status" : "WAITING",
            "identifier" : "scheduler-agent-SampleCommandExecutionJob7"
        }, {
            "agentName" : "scheduler-agent",
            "jobName" : "FileWatcherJob1",
            "jobDescription" : "This is a file watcher job looking for a file on the file system.",
            "contextName" : "MyFirstJobPlan",
            "childContextNames" : [ ],
            "startupControlType" : "AUTOMATIC",
            "skippedContexts" : { },
            "heldContexts" : { },
            "childContextName" : "ScriptProcessingContext",
            "held" : false,
            "initiationEventRaised" : false,
            "status" : "WAITING",
            "identifier" : "scheduler-agent-FileWatcherJob1"
        }, {
            "agentName" : "scheduler-agent",
            "jobName" : "SampleCommandExecutionJob1",
            "jobDescription" : "This is a simple script to demonstrate how command execution jobs work.",
            "contextName" : "MyFirstJobPlan",
            "childContextNames" : [ ],
            "startupControlType" : "AUTOMATIC",
            "skippedContexts" : { },
            "heldContexts" : { },
            "childContextName" : "ScriptProcessingContext",
            "held" : false,
            "initiationEventRaised" : false,
            "status" : "WAITING",
            "identifier" : "scheduler-agent-SampleCommandExecutionJob1"
        } ],
        "jobLocks" : [ ],
        "name" : "ScriptProcessingContext",
        "description" : "This context demonstrates executing some command execution jobs along with a file job",
        "jobDependencies" : [ {
            "jobIdentifier" : "scheduler-agent-SampleCommandExecutionJob2",
            "logicalGrouping" : {
                "and" : [ {
                    "identifier" : "scheduler-agent-SampleCommandExecutionJob1"
                } ]
            }
        }, {
            "jobIdentifier" : "scheduler-agent-SampleCommandExecutionJob7",
            "logicalGrouping" : {
                "and" : [ {
                    "identifier" : "scheduler-agent-SampleCommandExecutionJob1"
                } ]
            }
        }, {
            "jobIdentifier" : "scheduler-agent-SampleCommandExecutionJob3",
            "logicalGrouping" : {
                "and" : [ {
                    "identifier" : "scheduler-agent-SampleCommandExecutionJob2"
                }, {
                    "identifier" : "scheduler-agent-SampleCommandExecutionJob7"
                }, {
                    "identifier" : "scheduler-agent-FileWatcherJob1"
                } ]
            }
        }, {
            "jobIdentifier" : "scheduler-agent-SampleCommandExecutionJob4",
            "logicalGrouping" : {
                "and" : [ {
                    "identifier" : "scheduler-agent-SampleCommandExecutionJob3"
                } ]
            }
        }, {
            "jobIdentifier" : "scheduler-agent-SampleCommandExecutionJob4",
            "logicalGrouping" : {
                "and" : [ {
                    "identifier" : "scheduler-agent-SampleCommandExecutionJob3"
                } ]
            }
        }, {
            "jobIdentifier" : "scheduler-agent-SampleCommandExecutionJob6",
            "logicalGrouping" : {
                "and" : [ {
                    "identifier" : "scheduler-agent-SampleCommandExecutionJob4"
                } ]
            }
        } ],
        "contextDependencies" : [ ],
        "contextParameters" : [ ],
        "contextTtlMilliseconds" : 0,
        "treeViewExpandLevel" : 1,
        "ableToRunConcurrently" : true,
        "id" : "cd255927-f729-469b-b26f-059ae1bf1446",
        "createdDateTime" : 1688716294254,
        "updatedDateTime" : 1688716294254,
        "startTime" : 0,
        "endTime" : 0,
        "projectedEndTime" : 0,
        "status" : "WAITING",
        "heldJobs" : { },
        "runContextUntilManuallyEnded" : false,
        "quartzScheduleDrivenJobsDisabledForContext" : false
    }, {
        "contexts" : [ ],
        "scheduledJobs" : [ {
            "agentName" : "GLOBAL_EVENT",
            "jobName" : "GlobalEventJob1",
            "jobDescription" : "This is a sample global job!",
            "contextName" : "MyFirstJobPlan",
            "childContextNames" : [ ],
            "startupControlType" : "AUTOMATIC",
            "skippedContexts" : { },
            "heldContexts" : { },
            "childContextName" : "MyGlobalEventContext",
            "held" : false,
            "initiationEventRaised" : false,
            "status" : "WAITING",
            "identifier" : "GLOBAL_EVENT-GlobalEventJob1"
        }, {
            "agentName" : "scheduler-agent",
            "jobName" : "SampleCommandExecutionJob6",
            "jobDescription" : "This is a simple script to demonstrate how command execution jobs work.",
            "contextName" : "MyFirstJobPlan",
            "childContextNames" : [ ],
            "startupControlType" : "AUTOMATIC",
            "skippedContexts" : { },
            "heldContexts" : { },
            "childContextName" : "MyGlobalEventContext",
            "held" : false,
            "initiationEventRaised" : false,
            "status" : "WAITING",
            "identifier" : "scheduler-agent-SampleCommandExecutionJob6"
        } ],
        "jobLocks" : [ ],
        "name" : "MyGlobalEventContext",
        "description" : "This is a sample context template that executes a Global Event Job after all upstream jobs are complete.",
        "jobDependencies" : [ {
            "jobIdentifier" : "GLOBAL_EVENT-GlobalEventJob1",
            "logicalGrouping" : {
                "and" : [ {
                    "identifier" : "scheduler-agent-SampleCommandExecutionJob6"
                } ]
            }
        } ],
        "contextDependencies" : [ ],
        "contextParameters" : [ ],
        "timeWindowStart" : "* * 1 ? * * *",
        "contextTtlMilliseconds" : 82800000,
        "treeViewExpandLevel" : 1,
        "ableToRunConcurrently" : true,
        "id" : "15bca063-aeb0-4403-a7f9-4b9810fc2e82",
        "createdDateTime" : 1688716294256,
        "updatedDateTime" : 1688716294256,
        "startTime" : 0,
        "endTime" : 0,
        "projectedEndTime" : 0,
        "status" : "WAITING",
        "heldJobs" : { },
        "runContextUntilManuallyEnded" : false,
        "quartzScheduleDrivenJobsDisabledForContext" : false
    } ],
    "scheduledJobs" : [ {
        "agentName" : "scheduler-agent",
        "jobName" : "7amScheduledEvent",
        "jobDescription" : "This event fires at 7am Monday through Friday.",
        "contextName" : "MyFirstJobPlan",
        "childContextNames" : [ ],
        "startupControlType" : "AUTOMATIC",
        "skippedContexts" : { },
        "heldContexts" : { },
        "childContextName" : "MyFirstJobPlan",
        "held" : false,
        "initiationEventRaised" : false,
        "status" : "WAITING",
        "identifier" : "scheduler-agent-7amScheduledEvent"
    }, {
        "agentName" : "scheduler-agent",
        "jobName" : "SampleCommandExecutionJob1",
        "jobDescription" : "This is a simple script to demonstrate how command execution jobs work.",
        "contextName" : "MyFirstJobPlan",
        "childContextNames" : [ ],
        "startupControlType" : "AUTOMATIC",
        "skippedContexts" : { },
        "heldContexts" : { },
        "childContextName" : "MyFirstJobPlan",
        "held" : false,
        "initiationEventRaised" : false,
        "status" : "WAITING",
        "identifier" : "scheduler-agent-SampleCommandExecutionJob1"
    } ],
    "jobLocks" : [ {
        "name" : "MyFirstLock",
        "lockCount" : 1,
        "jobs" : {
            "ScriptProcessingContext" : [ {
                "agentName" : "scheduler-agent",
                "jobName" : "SampleCommandExecutionJob7",
                "contextName" : "MyFirstJobPlan",
                "startupControlType" : "AUTOMATIC",
                "lockCount" : 1,
                "jobDescription" : "This is a simple script to demonstrate how command execution jobs work.",
                "identifier" : "scheduler-agent-SampleCommandExecutionJob7"
            }, {
                "agentName" : "scheduler-agent",
                "jobName" : "SampleCommandExecutionJob2",
                "contextName" : "MyFirstJobPlan",
                "startupControlType" : "AUTOMATIC",
                "lockCount" : 1,
                "jobDescription" : "This is a simple script to demonstrate how command execution jobs work.",
                "identifier" : "scheduler-agent-SampleCommandExecutionJob2"
            } ]
        },
        "exclusiveJobLock" : false
    } ],
    "name" : "MyFirstJobPlan",
    "description" : "This is a sample context template used to demonstrate the builder classes associated with the Ikasan Enterprise Scheduler.",
    "jobDependencies" : [ {
        "jobIdentifier" : "scheduler-agent-7amScheduledEvent"
    }, {
        "jobIdentifier" : "scheduler-agent-SampleCommandExecutionJob1",
        "logicalGrouping" : {
            "and" : [ {
                "identifier" : "scheduler-agent-7amScheduledEvent"
            } ]
        }
    } ],
    "contextDependencies" : [ ],
    "contextParameters" : [ {
        "name" : "sample_param",
        "defaultValue" : "sample default value",
        "value" : "sample default value"
    } ],
    "timeWindowStart" : "0 0 1 ? * * *",
    "contextTtlMilliseconds" : 82800000,
    "treeViewExpandLevel" : 1,
    "ableToRunConcurrently" : true,
    "id" : "34fad2f3-d506-4f6d-a636-973e151f7d58",
    "createdDateTime" : 1688716294254,
    "updatedDateTime" : 1688716294254,
    "startTime" : 1688716296276,
    "endTime" : 0,
    "projectedEndTime" : 1688799096276,
    "status" : "WAITING",
    "heldJobs" : { },
    "runContextUntilManuallyEnded" : false,
    "quartzScheduleDrivenJobsDisabledForContext" : false
}  
```
