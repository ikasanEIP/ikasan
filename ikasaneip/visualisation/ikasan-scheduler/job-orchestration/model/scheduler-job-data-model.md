![IKASAN](../../../../developer/docs/quickstart-images/Ikasan-title-transparent.png)

# Ikasan Enterprise Scheduler - Scheduler Job Data Model

The Ikasan Scheduler support 4 different job types.

## File Event Driven Jobs

These jobs raise events upon receipt of a file in a location that the job is configured to look for a file within a
configurable time window. See [Scheduler Job Builder](../builder/scheduler-job-builder.md) for details of how to easily
build File Event Driven Jobs using the builder implementation.

### Configuration

|Field Name|	Type|	Description|	Mandatory| 	Default Value |	Allowed Values|	Comments|
|-----|-----|-----|----|----------------|-----|-----|
|agentName|	String|	The name of the agent that the job will run on.|yes|                | | |
|jobName|	String|	The name of the job itself.|	yes	|                | | |
|contextId| String |The id of the parent context that this job belongs to.| yes|                | | |
|childContextNames| List<String>| A list containing the child context names of all of the contexts that the job is relevant in, within the parent context.| yes|                | | |
|startupControlType| String| The startup control of the Ikasan flow that manages this job.| yes| MANUAL         |AUTOMATIC, MANUAL, DISABLED| |
|cronExpression| String |The cron expression polling interval that the file will be polled for arrival by the Ikasan Scheduler.| yes|                |A valid Quartz cron expression.||
|jobGroup |String |The quartz job group that the associated polling schedule will belong to.| yes|                | | |
|timeZone| String| The timezone within that the above polling schedule is relevant.| no|                | | |
|ignoreMisfire| boolean| TBD| no| true           | | Probably not relevant in the Ikasan Scheduler but is available on the FileConsumer interface.|
|eager| boolean| TBD |no| false          ||Probably not relevant in the Ikasan Scheduler but is available on the FileConsumer interface.|
|maxEagerCallbacks| Integer| TBD| no| 0| |Probably not relevant in the Ikasan Scheduler but is available on the FileConsumer interface.|
|passthroughProperties| Map<String, String>|    A map of name value pairs that are passed to the scheduler context when the file polling event is fired.| no| | | |
|persistentRecovery| boolean| TBD| no|false| |Probably not relevant in the Ikasan Scheduler but is available on the FileConsumer interface.|
|recoveryTolerance| Integer| TBD| no |1800000| |Probably not relevant in the Ikasan Scheduler but is available on the FileConsumer interface.|
|filePath| String| The file path location that the Ikasan Scheduler will look for file arrivals.| yes| | | |
|filenames| List<String> |   A list of filename patterns that the Ikasan Scheduler will raise a event for upon receipt.| yes | | | |
|encoding| String| The encoding of the files that the Ikasan Scheduler expects to receive.| no | | | |
|includeHeader| boolean| TBD| yes| true| | Probably not relevant in the Ikasan Scheduler but is available on the FileConsumer interface.|
|includeTrailer| boolean |TBD| yes| true| |Probably not relevant in the Ikasan Scheduler but is available on the FileConsumer interface.|
|sortByModifiedDateTime| boolean |TBD| yes| true | |Probably not relevant in the Ikasan Scheduler but is available on the FileConsumer interface.|
|sortAscending| boolean| TBD| yes| false| |Probably not relevant in the Ikasan Scheduler but is available on the FileConsumer interface.|
|directoryDepth| Integer| TBD| yes| 5| | | |
|logMatchedFilenames| boolean| TBD| yes| true| | | |
|ignoreFileRenameWhilstScanning| boolean| TBD| yes| false| | | |
|identifier |String The unique job identifier within the Ikasan Scheduler.| yes | | | | |
|jobDescription| String The description of the job.| yes | | | | |

### Sample
```json
{
    "agentName" : "agentName",
    "jobName" : "jobName",
    "contextName" : "contextId",
    "childContextNames" : [ "childContextId" ],
    "startupControlType" : "MANUAL",
    "cronExpression" : "cronExpression",
    "jobGroup" : "jobGroup",
    "timeZone" : "timezone",
    "ignoreMisfire" : true,
    "eager" : false,
    "maxEagerCallbacks" : 0,
    "passthroughProperties" : {
        "key" : "value"
    },
    "persistentRecovery" : true,
    "recoveryTolerance" : 1800000,
    "blackoutWindowCronExpressions" : [ ],
    "blackoutWindowDateTimeRanges" : { },
    "filePath" : "filePath",
    "moveDirectory" : "moveDirectory",
    "filenames" : [ "file1", "file2" ],
    "encoding" : "encoding",
    "includeHeader" : true,
    "includeTrailer" : true,
    "sortByModifiedDateTime" : true,
    "sortAscending" : false,
    "directoryDepth" : 5,
    "logMatchedFilenames" : true,
    "ignoreFileRenameWhilstScanning" : false,
    "minFileAgeSeconds" : 180,
    "dropEventOnBlackout" : false,
    "identifier" : "agentName-jobName",
    "jobDescription" : "description"
}
```

## Quartz Scheduled Event Driven Jobs

These jobs raise events purely upon a time schedule.

### Configuration
|Field Name|	Type|	Description|	Mandatory| 	Default Value |	Allowed Values|	Comments|
|-----|-----|-----|----|----------------|-----|-----|
|agentName| String The name of the agent that the job will run on.| yes| | | |
|jobName |String| The name of the job itself.| yes| | | |
|contextId| String| The id of the parent context that this job belongs to.| yes| | | |
|childContextNames| List<String>|    A list containing the child context names of all of the contexts that the job is relevant in, within the parent context.| yes| | | |
|startupControlType| String| The startup control of the Ikasan flow that manages this job.| yes| MANUAL |AUTOMATIC, MANUAL, DISABLED | |
|cronExpression| String The cron expression polling interval that the file will be polled for arrival by the Ikasan Scheduler. |yes| | A valid Quartz cron expression.| |
|jobGroup| String| The quartz job group that the assocaited polling schedule will belong to.| yes| | | |
|timeZone| String| The timezone within that the above polling schedule is relevant.| no|                | | |
|ignoreMisfire| boolean| TBD| no| true           | | Probably not relevant in the Ikasan Scheduler but is available on the FileConsumer interface.|
|eager| boolean| TBD |no| false          ||Probably not relevant in the Ikasan Scheduler but is available on the FileConsumer interface.|
|maxEagerCallbacks| Integer| TBD| no| 0| |Probably not relevant in the Ikasan Scheduler but is available on the FileConsumer interface.|
|passthroughProperties| Map<String, String>|    A map of name value pairs that are passed to the scheduler context when the file polling event is fired.| no| | | |
|persistentRecovery| boolean| TBD| no|false| |Probably not relevant in the Ikasan Scheduler but is available on the FileConsumer interface.|
|recoveryTolerance| Integer| TBD| no |1800000| |Probably not relevant in the Ikasan Scheduler but is available on the FileConsumer interface.|
|identifier |String The unique job identifier within the Ikasan Scheduler.| yes | | | | |
|jobDescription| String The description of the job.| yes | | | | |

### Sample
```json
{
    "agentName" : "agentName",
    "jobName" : "jobName",
    "contextName" : "contextId",
    "childContextNames" : [ "childContextId" ],
    "startupControlType" : "MANUAL",
    "cronExpression" : "cronExpression",
    "jobGroup" : "jobGroup",
    "timeZone" : "timezone",
    "ignoreMisfire" : true,
    "eager" : true,
    "maxEagerCallbacks" : 10,
    "passthroughProperties" : {
        "key" : "value"
    },
    "persistentRecovery" : true,
    "recoveryTolerance" : 100,
    "blackoutWindowCronExpressions" : [ ],
    "blackoutWindowDateTimeRanges" : { },
    "dropEventOnBlackout" : false,
    "identifier" : "agentName-jobName",
    "jobDescription" : "description"
}
```

## Internal Event Driven Jobs

The Internal Event Driven Job is responsible for executing a job by creating an external process and managing it. The
job that is executed is generally a bash or some kind of shell script within a Windows or Linux environment.

### Configuration
|Field Name|	Type|	Description|	Mandatory| 	Default Value |	Allowed Values|	Comments|
|-----|-----|-----|----|----------------|-----|-----|
|agentName| String The name of the agent that the job will run on.| yes| | | |
|jobName |String| The name of the job itself.| yes| | | |
|contextId| String| The id of the parent context that this job belongs to.| yes| | | |
|childContextNames| List<String>|    A list containing the child context names of all of the contexts that the job is relevant in, within the parent context.| yes| | | |
|startupControlType| String| The startup control of the Ikasan flow that manages this job.| yes| MANUAL |AUTOMATIC, MANUAL, DISABLED | |
|successfulReturnCodes|List<String>|The list of return codes that indicate that a job executed successfully.|	yes	| | | |
|workingDirectory| String| The directory from where the command is execiuted.| yes| | | |
|commandLine| String| The command that is executed.| yes| | | |
|minExecutionTime |long| The minimum amount of time that a job can execute for. |yes| | | |
|maxExecutionTime| long| The maximum amoiunt of time that a job can execute for.| yes| | | |
|contextParameters|List<ContextParameter>|The parameters that are contextualised and used by the job.|	no	| | | |
|identifier| String |The unique job identifier within the Ikasan Scheduler.| yes| | | |
|jobDescription | String |The description of the job. |yes| | | |

### Sample
```json
{
    "agentName" : "agentName",
    "jobName" : "jobName",
    "contextName" : "contextId",
    "childContextNames" : [ "childContextId" ],
    "startupControlType" : "AUTOMATIC",
    "successfulReturnCodes" : [ "0" ],
    "workingDirectory" : "working directory",
    "commandLine" : "commandLine",
    "minExecutionTime" : 1000,
    "maxExecutionTime" : 100000,
    "contextParameters" : [ {
        "name" : "name1",
        "defaultValue" : "value"
    }, {
        "name" : "name2",
        "defaultValue" : "value"
    } ],
    "daysOfWeekToRun" : [ 2, 3 ],
    "targetResidingContextOnly" : true,
    "participatesInLock" : true,
    "jobRepeatable" : false,
    "identifier" : "agentName-jobName",
    "jobDescription" : "description"
}
```

## Global Event Driven Jobs

TBD

### Configuration
|Field Name|	Type|	Description|	Mandatory| 	Default Value |	Allowed Values|	Comments|
|-----|-----|-----|----|----------------|-----|-----|
|agentName| String The name of the agent that the job will run on.| yes| | | |
|jobName |String| The name of the job itself.| yes| | | |
|contextId| String| The id of the parent context that this job belongs to.| yes| | | |
|childContextNames| List<String>|    A list containing the child context names of all of the contexts that the job is relevant in, within the parent context.| yes| | | |
|startupControlType| String| The startup control of the Ikasan flow that manages this job.| yes| MANUAL |AUTOMATIC, MANUAL, DISABLED | |
|successfulReturnCodes|List<String>|The list of return codes that indicate that a job executed successfully.|	yes	| | | |
|workingDirectory| String| The directory from where the command is execiuted.| yes| | | |
|commandLine| String| The command that is executed.| yes| | | |
|minExecutionTime |long| The minimum amount of time that a job can execute for. |yes| | | |
|maxExecutionTime| long| The maximum amoiunt of time that a job can execute for.| yes| | | |
|contextParameters|List<ContextParameter>|The parameters that are contextualised and used by the job.|	no	| | | |
|identifier| String |The unique job identifier within the Ikasan Scheduler.| yes| | | |
|jobDescription | String |The description of the job. |yes| | | |

### Sample
```json
{
  "agentName" : "GLOBAL_EVENT",
  "jobName" : "jobName",
  "contextName" : "contextId",
  "childContextNames" : [ "childContextId" ],
  "startupControlType" : "MANUAL",
  "identifier" : "GLOBAL_EVENT-jobName",
  "jobDescription" : "description"
}
```

