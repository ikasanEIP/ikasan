![IKASAN](../../../../developer/docs/quickstart-images/Ikasan-title-transparent.png)

# Ikasan Enterprise Scheduler - Scheduler Job Builder
In order to use the Ikasan Enterprise Scheduler Job Plan Builder include the following POM in your project:

```xml
<dependency>
    <groupId>org.ikasan</groupId>
    <artifactId>ikasan-job-orchestration-builder</artifactId>
    <version>${version.ikasan}</version>
</dependency>
```

The various job builder implementations allow for jobs to be easily created in Java code. These builder implementations assist in the 
construction of jobs defined in the [Scheduler Job Data Model](../model/scheduler-job-data-model.md).

## InternalEventDrivenJobBuilder
The InternalEventDrivenJobBuilder assists in building an instance of [InternalEventDrivenJob](../model/scheduler-job-data-model.md#internal-event-driven-jobs)
otherwise known as a command execution job.
```java
InternalEventDrivenJobBuilder internalEventDrivenJobBuilder = new InternalEventDrivenJobBuilder();
    internalEventDrivenJobBuilder
    .withCommandLine("commandLine")
    .withMaxExecutionTime(100000L)
    .withMinExecutionTime(1000L)
    .withWorkingDirectory("working directory")
    .withParticipatesInLock(true)
    .withTargetResidingContextOnly(true)
    .addDayOfWeekToRun(Calendar.MONDAY).addDayOfWeekToRun(Calendar.TUESDAY)
    .addContextParameter(internalEventDrivenJobBuilder.getContextParameterBuilder()
    .withName("name1")
    .withDefaultValue("value")
    .build())
    .addContextParameter(internalEventDrivenJobBuilder.getContextParameterBuilder()
    .withName("name2")
    .withDefaultValue("value")
    .build())
    .addSuccessfulReturnCode("0")
    .withContextName("contextId")
    .withAgentName("agentName")
    .withContextName("contextId")
    .addChildContextId("childContextId")
    .withDescription("description")
    .withJobName("jobName");
```

```json
{
  "agentName" : "agentName",
  "jobName" : "jobName",
  "contextName" : "contextId",
  "childContextNames" : [ "childContextId" ],
  "startupControlType" : "AUTOMATIC",
  "skippedContexts" : { },
  "heldContexts" : { },
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
## QuartzScheduleDrivenJobBuilder
The QuartzScheduleDrivenJobBuilder assists in building an instance of [QuartzScheduleDrivenJob](../model/scheduler-job-data-model.md#quartz-scheduled-driven-jobs).
```java
QuartzScheduleDrivenJobBuilder quartzScheduleDrivenJobBuilder = new QuartzScheduleDrivenJobBuilder();

Map<String, String> passthroughProperties = new HashMap<>();
passthroughProperties.put("key", "value");

quartzScheduleDrivenJobBuilder.withCronExpression("cronExpression")
    .withJobGroup("jobGroup")
    .withTimeZone("timezone")
    .withEager(true)
    .withIgnoreMisfire(true)
    .withMaxEagerCallbacks(10)
    .withPassthroughProperties(passthroughProperties)
    .withPersistentRecovery(true)
    .withRecoveryTolerance(100L)
    .withAgentName("agentName")
    .withContextName("contextId")
    .addChildContextId("childContextId")
    .withDescription("description")
    .withJobName("jobName")
    .withStartupControlType("MANUAL");
```

```json
{
  "agentName" : "agentName",
  "jobName" : "jobName",
  "contextName" : "contextId",
  "childContextNames" : [ "childContextId" ],
  "startupControlType" : "MANUAL",
  "skippedContexts" : { },
  "heldContexts" : { },
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

## FileEventDrivenJobBuilder
The FileEventDrivenJobBuilder assists in building an instance of [FileEventDrivenJob](../model/scheduler-job-data-model.md#file-event-driven-jobs)
otherwise known as a file watcher job.
```java
FileEventDrivenJobBuilder fileEventDrivenJobBuilder = new FileEventDrivenJobBuilder();
Map<String, String> passthroughProperties = new HashMap<>();
passthroughProperties.put("key", "value");

List<String> filenames = new ArrayList<>();
filenames.add("file1");
filenames.add("file2");

fileEventDrivenJobBuilder.withFilePath("filePath")
    .withMoveDirectory("moveDirectory")
    .withFilenames(filenames)
    .withDirectoryDepth(5)
    .withEncoding("encoding")
    .withMinFileAgeSeconds(180)
    .withIgnoreFileRenameWhilstScanning(false)
    .withIncludeHeader(true)
    .withIncludeTrailer(true)
    .withLogMatchedFilenames(true)
    .withSortAscending(false)
    .withSortByModifiedDateTime(true)
    .withCronExpression("cronExpression")
    .withJobGroup("jobGroup")
    .withTimeZone("timezone")
    .withEager(true)
    .withIgnoreMisfire(true)
    .withMaxEagerCallbacks(10)
    .withPassthroughProperties(passthroughProperties)
    .withPersistentRecovery(true)
    .withRecoveryTolerance(100L)
    .withAgentName("agentName")
    .withContextName("contextId")
    .addChildContextId("childContextId")
    .withDescription("description")
    .withJobName("jobName")
    .withStartupControlType("MANUAL");
```

```json
{
  "agentName" : "agentName",
  "jobName" : "jobName",
  "contextName" : "contextId",
  "childContextNames" : [ "childContextId" ],
  "startupControlType" : "MANUAL",
  "skippedContexts" : { },
  "heldContexts" : { },
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

## GlobalEventJobBuilder
The GlobalEventJobBuilder assists in building an instance of [GlobalEventJob](../model/scheduler-job-data-model.md#global-event-jobs).
```java
GlobalEventJobBuilder globalEventJobBuilder = new GlobalEventJobBuilder();

globalEventJobBuilder
    .withAgentName(JobConstants.GLOBAL_EVENT)
    .withContextName("contextId")
    .addChildContextId("childContextId")
    .withDescription("description")
    .withJobName("jobName")
    .withStartupControlType("MANUAL");
```

```json
{
  "agentName" : "GLOBAL_EVENT",
  "jobName" : "jobName",
  "contextName" : "contextId",
  "childContextNames" : [ "childContextId" ],
  "startupControlType" : "MANUAL",
  "skippedContexts" : { },
  "heldContexts" : { },
  "identifier" : "GLOBAL_EVENT-jobName",
  "jobDescription" : "description"
}
```