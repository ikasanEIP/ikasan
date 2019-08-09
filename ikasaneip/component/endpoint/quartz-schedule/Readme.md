[<< Component Quick Start](../../Readme.md)
![IKASAN](../../../developer/docs/quickstart-images/Ikasan-title-transparent.png)
# Scheduled Consumer

<img src="../../../developer/docs/quickstart-images/polling-consumer.png" width="200px" align="left">

This is a &quot;time event&quot; based consumer configured to be either an absolute or relative time schedule.
Read more about EIP [Polling Consumer](http://www.enterpriseintegrationpatterns.com/patterns/messaging/PollingConsumer.html)
<br/>
<br/>
<br/>
<br/>
<br/>

##### Operation
This consumer is invoked on a Quartz based schedule with the ```MessageProvider``` returning the Quartz ```JobExecutionContext```.

```java
org.quartz.JobExecutionContext invoke(org.quartz.JobExecutionContext context)
```

##### Supported Features
The following Ikasan features are supported by this component.

||| 
| :----- | :------: | 
| **Feature**| **Support** | 
| Managed Lifecycle| Yes | 
| Component Configuration| Yes | 
| Event Resubmission| No | 
| Event Record/Replay| No | 

##### Mandatory Configuration Options

| Option | Type | Purpose |
| --- | --- | --- |
| cronExpression | String | Cron based expression dictating the invocation schedule for this component. Example, "\*/5 \* \* \* \* ?" will fire every 5 seconds.|

###### Sample Usage - Ikasan Java FluentAPI
```java
public class ModuleConfig 
{
  @Resource
  private BuilderFactory builderFactory;

  public Consumer getScheduledConsumer() 
  {
      return builderFactory.getComponentBuilder().scheduledConsumer()
                .setCronExpression("*/5 * * * * ?")
                .build();
  }
}
```

##### Optional Configuration Options

| Option | Type | Purpose |
| --- | --- | --- |
| ignoreMisfire | boolean | Default is false. When false the scheduler will try to fire any misfires as soon as possible. When true this will tell the scheduler to ignore misfires and wait for the next scheduled time.|
| eager | boolean | Tells the consumer to immediately re-schedule itself to invoke again, regardless of the initial cron schedule. This is useful for consumers dealing with lots of data which can be continuously consumed until exhausted, at which point it returns to its initial cron schedule.|
| maxEagerCallbacks | integer | Override the maximum number of consecutive eager invocations. Default is unlimited until no more data available.|
| timezone | String | Optional timezone used by Quartz scheduler. |
| configuration | ScheduledConsumerConfiguration | Override configuration with a complete instance of the ScheduledConsumerConfiguration. See [Configuration Service](../../../configuration-service/Readme.md).|
| configuredResourceId | boolean | Override default generated configuredResource identifier. This is useful if you want to shared configuredResource instances across components. See [Configuration Service](../../../configuration-service/Readme.md).|
| criticalOnStartup | boolean | Default false. Override to specify whether the failure of starting this consumer component should also fail the flow starting. |
| messageProvider | MessageProvider | Data provider for this consumer. Default is a simple ScheduledConsumer which is invoked with a Quartz JobExecutionContext instance. Default messageProvider will simply transition to the next component in the flow. MessageProvider can be overridden with a CallbackScheduledConsumer which is invoked with a Quartz JobExecutionContext, but leaves control with the developer to consume source system events as required. This is recommended when consuming large amounts of data within a single schedule as the developer can control how much data is read and processed by the flow on each callback. All consumption within the CallbackScheduledConsumer is within a single transaction. |
| managedEventIdentifierService | ManagedEventIdentifierService | Default null. Override to provide your own ManagedEventIdentifierService. See [Managed Event Identifier Service](../../../spec/event/Readme.md#Managed-Event-Identifier-Service) for details. |
| managedResourceRecoveryManager | ManagedResourceRecoveryManager | Default null. Override to provide your own ManagedResourceRecoveryManager. See [Managed Event Identifier Service](../../../spec/event/Readme.md#Managed-Event-Identifier-Service) for details. |
| eventFactory | EventFactory | Default null. Override to provide your own EventFactory. Only override this if you are an Ikasan Ninja. |
| scheduledJobName | String | Default generated. Override to provide your own Quartz ScheduledJobName. |
| scheduledJobGroupName | String | Default generated. Override to provide your own Quartz ScheduledJobGroupName. |


###### Sample Usage - Ikasan Java FluentAPI

```java
public class ModuleConfig 
{
  @Resource
  private BuilderFactory builderFactory;

  public Consumer getScheduledConsumer() 
  {
        return builderFactory.getComponentBuilder().scheduledConsumer()
                .setCronExpression("*/5 * * * * ?")
                .setIgnoreMisfire(true)
                .setEager(true)
                .setMaxEagerCallbacks(10)
                .setTimezone("GMT")
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

```
