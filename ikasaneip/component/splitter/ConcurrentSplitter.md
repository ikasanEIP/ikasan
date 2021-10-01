![IKASAN](../../developer/docs/quickstart-images/Ikasan-title-transparent.png)
# Concurrent Splitters

### Purpose

<img src="../../developer/docs/quickstart-images/splitter.png" width="200px" align="left">Splits incoming event into a multiple events returned as a single list. Each event in the list can be processed concurrently by the downstream components.
NOTE: At present it is not possible to wiretap components executed post concurrent splitter. This is fixed in IkasanESB 3.3.0.


### Pattern

### Invoker Configuration

#### Configuration Options
| Option | Type | Purpose |
| --- | --- | --- |
| sendSplitsAsSinglePayload | boolean | Whether the split events shold be processed as individual events or as a single List of events |
| concurrentThreads | int | Number of concurrent threads available to process the split events  |

#### Sample Usage
The sample below defines a simple module with one flow that translates an attribute on a map message to an Object. 
````java
     public Flow getFlow(ModuleBuilder moduleBuilder, ComponentBuilder componentBuilder)
     {
         FlowBuilder flowBuilder = moduleBuilder.getFlowBuilder("Flow");
         return flowBuilder.withDescription("Flow description")
             .consumer("consumer", componentBuilder.scheduledConsumer().setCronExpression("0/5 * * * * ?").setConfiguredResourceId("configuredResourceId")
                 .setScheduledJobGroupName("scheduledJobGroupName").setScheduledJobName("scheduledJobName").build())
             .concurrentSplitter("splitterName", componentBuilder.listSplitter(), 
                 org.ikasan.builder.invoker.Configuration.concurrentSplitterInvoker().setConcurrentThreads(5))
             .producer("producer", new MyProducer()).build();
     }

 ````


