![IKASAN](../../../../developer/docs/quickstart-images/Ikasan-title-transparent.png)

# Ikasan Enterprise Scheduler - Builder

## FileEventDrivenJobBuilder
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