[<< Component Quick Start](../../Readme.md)
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
| cronExpression | String | Mandatory cron based expression dictating the callback schedule for this component. Example, \* \* \* \* ?? |
| ignoreMisfire | boolean | Optional  |
| isEager | boolean |  Optional flag indicating whether if scheduled consumer should trigger(run) again, immediately after first(previous) timely run was successful   |
| timezone | String | Optional timezone used by quartz scheduler |

##### Sample Usage - Ikasan Java FluentAPI

```java
public class ModuleConfig 
{
  @Resource
  private BuilderFactory builderFactory;
  String cronExpression = "*/5 * * * * ?";

  public  Consumer getScheduledConsumer() 
  {
      return builderFactory.getComponentBuilder().scheduledConsumer()
                .setCronExpression(cronExpression)
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
