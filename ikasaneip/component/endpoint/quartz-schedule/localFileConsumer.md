[<< Component Quick Start](../../Readme.md)
![IKASAN](../../../developer/docs/quickstart-images/Ikasan-title-transparent.png)
# Local File Consumer

<img src="../../../developer/docs/quickstart-images/local-file-consumer.png" width="200px" align="left">This consumer is a variation of Scheduled Consumer which is a &quot;time event&quot; based consumer configured to be either an absolute or relative time schedule, backed by Local File ```MessageProvider```.
<br/>
<br/>
<br/>
<br/>

##### Operation
This consumer is invoked on a Quartz based schedule with the ```MessageProvider``` returning ```List<File>``` for matched files.

```java
List<File> invoke(org.quartz.JobExecutionContext context)
```

##### Supported Features
The following Ikasan features are supported by this component.

| **Feature**| **Support** | 
| :----- | :------: | 
| Managed Lifecycle| Yes | 
| Component Configuration| Yes | 
| Event Resubmission| Yes | 
| Event Record/Replay| Yes | 

##### Mandatory Configuration Options

| Option | Type | Purpose |
| --- | --- | --- |
| cronExpression | String | Cron based expression dictating the invocation schedule for this component. Example, "\*/5 \* \* \* \* ?" will fire every 5 seconds.|
| filenames | List\<String> | Filenames to be processed. |

##### Sample Usage - Ikasan Java FluentAPI

```java
public class ModuleConfig {


  @Resource
  private BuilderFactory builderFactory;

  public Consumer getFileConsumer()
  {
      return builderFactory.getComponentBuilder().fileConsumer()
              .setCronExpression("*/5 * * * * ?")
              .setFilenames(sourceFilenames)
              .build();
  }
}
```

##### Optional Configuration Options

| Option | Type | Purpose |
| --- | --- | --- |
| encoding | String | Encoding of the files ie. UTF-8 |
| directoryDepth | integer | How deep down the directory tree to go to find matching filenames. |
| ignoreFileRenameWhilstScanning | boolean | Ignore cases where the file has been renamed between scanning and retrieval. |
| includeHeader | boolean | Assume first line of the file is a header and include it as a header within the message payload. |
| includeTrailer | boolean | Assume last line of the file is a trailer and include it as a trailer within the message payload. |
| sortAscending | boolean | Sort the list in ascending order (true) or descending order (false) when a sort method is used. |
| sortByModifiedDateTime | boolean | Sort the received file list by last modified date time. |
| logMatchedFilenames | boolean | Write any matching filenames found to the log files as additional information. |
| messageProviderPostProcessor | MessageProviderPostProcessor | Provide additional functionality to be applied to the payload as a post processor before returning for next component invocation.|
| configuration | ScheduledConsumerConfiguration | Override configuration with a complete instance of the ScheduledConsumerConfiguration. See [Configuration Service](../../../configuration-service/Readme.md).|
| configuredResourceId | boolean | Override default generated configuredResource identifier. This is useful if you want to shared configuredResource instances across components. See [Configuration Service](../../../configuration-service/Readme.md).|
| criticalOnStartup | boolean | Default false. Override to specify whether the failure of starting this consumer component should also fail the flow starting. |
| messageProvider | MessageProvider | Data provider for this consumer. Default is a simple ScheduledConsumer which is invoked with a Quartz JobExecutionContext instance. Default messageProvider will simply transition to the next component in the flow. MessageProvider can be overridden with a CallbackScheduledConsumer which is invoked with a Quartz JobExecutionContext, but leaves control with the developer to consume source system events as required. This is recommended when consuming large amounts of data within a single schedule as the developer can control how much data is read and processed by the flow on each callback. All consumption within the CallbackScheduledConsumer is within a single transaction. |
| managedEventIdentifierService | ManagedEventIdentifierService | Default null. Override to provide your own ManagedEventIdentifierService. See [Managed Event Identifier Service](../../../spec/event/Readme.md#Managed-Event-Identifier-Service) for details. |
| managedResourceRecoveryManager | ManagedResourceRecoveryManager | Default null. Override to provide your own ManagedResourceRecoveryManager. See [Managed Event Identifier Service](../../../spec/event/Readme.md#Managed-Event-Identifier-Service) for details. |
| eventFactory | EventFactory | Default null. Override to provide your own EventFactory. Only override this if you are an Ikasan Ninja. |
| scheduledJobName | String | Default generated. Override to provide your own Quartz ScheduledJobName. |
| scheduledJobGroupName | String | Default generated. Override to provide your own Quartz ScheduledJobGroupName. |

##### Sample Usage - Ikasan Java FluentAPI

```java
public class ModuleConfig {


  @Resource
  private BuilderFactory builderFactory;

  public Consumer getFileConsumer()
  {
      return builderFactory.getComponentBuilder().fileConsumer()
                    .setCronExpression("*/5 * * * * ?")
                    .setFilenames(filenames)
                    .setEncoding("UTF-8")
                    .setDirectoryDepth(1)
                    .setIgnoreFileRenameWhilstScanning(true)
                    .setIncludeHeader(true)
                    .setIncludeTrailer(true)
                    .setSortAscending(true)
                    .setSortByModifiedDateTime(true)
                    .setLogMatchedFilenames(true)
                    .setMessageProviderPostProcessor(messageProviderPostProcessor)
                    .setConfiguration(scheduledConsumerConfiguration)
                    .setConfiguredResourceId("moduleName-flowName-component")
                    .setCriticalOnStartup(true)
                    .setManagedResourceRecoveryManager(managedResourceRecoveryManager)
                    .setManagedEventIdentifierService(managedEventIdentifierService)
                    .setEventFactory(eventFactory)
                    .setMessageProvider(messageProvider)
                    .setScheduledJobName("moduleName-flowName-componentName")
                    .setScheduledJobGroupName("moduleName-flowName")
                    .build();
  }
}