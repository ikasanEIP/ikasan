![IKASAN](../../../developer/docs/quickstart-images/Ikasan-title-transparent.png)
# Local File Consumer

<img src="../../../developer/docs/quickstart-images/local-file-consumer.png" width="200px" align="left">This consumer is variation of Scheduled Consumer which is a &quot;time event&quot; based consumer configured to be either an absolute or relative time schedule, backed by Local File Message provider.
Read more about EIP [Polling Consumer](http://www.enterpriseintegrationpatterns.com/patterns/messaging/PollingConsumer.html)


##### Configuration Options

| Option | Type | Purpose |
| --- | --- | --- |
| filenames | List<String> | Filenames to be processed. |
| encoding | String | Encoding of the files |
| includeHeader | boolean | Assume first line of the file is a header and include it as a header within the message payload. |
| sortByModifiedDateTime | boolean | Sort the received file list by last modified date time. |
| sortAscending | boolean | Sort the list in ascending order (true) or descending order (false) when a sort method is used. |
| directoryDepth | int | How deep down the directory tree to go to find matching filenames. |
| logMatchedFilenames | boolean | Write any matching filenames found to the log files as additional information. |
| ignoreFileRenameWhilstScanning | boolean | Ignore cases where the file has been renamed between scanning and retrieval. |


##### Sample Usage - Spring XML

```xml
<!-- fileConsumer is a bean definition of component -->
<bean id="fileConsumer" class="org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumer">
        <constructor-arg ref="scheduler"/>
        <property name="messageProvider" ref="fileMessageProvider"/>
        <property name="jobDetail">
            <bean class="org.springframework.beans.factory.config.MethodInvokingFactoryBean">
                <property name="targetObject" ref="scheduledJobFactory"/>
                <property name="targetMethod" value="createJobDetail"/>
                <property name="arguments">
                    <list>
                        <ref bean="fileConsumer"/>
                        <value type="java.lang.Class">
                            org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumer
                        </value>
                        <value>fileConsumer</value>
                        <value>localfile</value>
                    </list>
                </property>
            </bean>
        </property>
        <property name="configuration" ref="fileConsumerConfiguration"/>
        <property name="configuredResourceId" value="fileConsumer"/>
    </bean>

    <bean id="fileConsumerConfiguration" class="org.ikasan.component.endpoint.filesystem.messageprovider.FileConsumerConfiguration">
        <property name="cronExpression" value="0 0/1 * * * ?"/>
        <property name="filenames" value="*.txt"/>
     </bean>
    
<bean id="fileMessageProvider" class="org.ikasan.component.endpoint.filesystem.messageprovider.FileMessageProvider">
</bean>

<!-- fileConsumerFlowElement is a bean definition of flow elements which uses fileConsumer as a component -->
<bean id="fileConsumerFlowElement" class="org.ikasan.builder.FlowElementFactory">
    <property name="name" value="FILE Consumer"/>
    <property name="component"  ref="fileConsumer"/>
    <property name="transition" ref="converterFlowElement"/>
</bean>

```

##### Sample Usage - builder pattern

```java
public class ModuleConfig {


  @Resource
  private BuilderFactory builderFactory;

  public Consumer getFileConsumer()
  {
      return builderFactory.getComponentBuilder().fileConsumer()
              .setCronExpression("0 0/1 * * * ?")
              .setScheduledJobGroupName(scheduledGroupName)
              .setScheduledJobName(scheduledName)
              .setFilenames(sourceFilenames)
              .setLogMatchedFilenames(true)
              .setConfiguredResourceId(fileConsumerConfiguredResourceId)
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
