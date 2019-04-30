![IKASAN](../../../developer/docs/quickstart-images/Ikasan-title-transparent.png)
# Scheduled Consumer

<img src="../../../developer/docs/quickstart-images/polling-consumer.png" width="200px" align="left">This is a &quot;time event&quot; based consumer configured to be either an absolute or relative time schedule.
Read more about EIP [Polling Consumer](http://www.enterpriseintegrationpatterns.com/patterns/messaging/PollingConsumer.html)
<br/>
<br/>
<br/>
<br/>
##### Configuration Options

| Option | Type | Purpose |
| --- | --- | --- |
| cronExpression | String | Cron based expression dictating the callback schedule for this component. Example, \* \* \* \* ?? |
| ignoreMisfire | boolean |   |
| isEager | boolean |  Flag indicating whether if scheduled consumer should trigger(run) again, immediately after first(previous) timely run was successful   |
| timezone | String | Timezone used by quartz scheduler |

##### Sample Usage

##### Sample Usage - builder pattern

```java
public class ModuleConfig {

  @Resource
  private BuilderFactory builderFactory;

  public  Consumer getFileConsumer() {
      return builderFactory.getComponentBuilder().scheduledConsumer()
              .setCronExpression(cronExpression)
              .setScheduledJobGroupName(scheduledGroupName)
              .setScheduledJobName(scheduledName)
              .setConfiguredResourceId(scheduledConsumerConfiguredResourceId)
              .build();
  }
}

```

# Document Info

| Authors | Ikasan Development Team |
| --- | --- |
| Contributors | n/a |
| Date | April 2019 |
| Email | info@ikasan.org |
| WebSite | [http://www.ikasan.org](http://www.ikasan.org/) |
