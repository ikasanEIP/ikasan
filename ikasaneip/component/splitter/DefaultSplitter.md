![IKASAN](../../developer/docs/quickstart-images/Ikasan-title-transparent.png)
# Default Splitters

### Purpose

<img src="../../developer/docs/quickstart-images/splitter.png" width="200px" align="left">Splits incoming event into a multiple events returned as a single list.


### Pattern

### Invoker Configuration

#### Configuration Options
| Option | Type | Purpose |
| --- | --- | --- |
| sendSplitsAsSinglePayload | boolean | Whether the split events shold be processed as individual events or as a single List of events |

#### Sample Usage
The sample below defines a simple module with one flow that translates an attribute on a map message to an Object. 
````java
     public Flow getFlow(ModuleBuilder moduleBuilder, ComponentBuilder componentBuilder)
     {
         FlowBuilder flowBuilder = moduleBuilder.getFlowBuilder("Flow");
         return flowBuilder.withDescription("Flow description")
             .consumer("consumer", componentBuilder.scheduledConsumer().setCronExpression("0/5 * * * * ?").setConfiguredResourceId("configuredResourceId")
                 .setScheduledJobGroupName("scheduledJobGroupName").setScheduledJobName("scheduledJobName").build())
             .splitter("splitterName", componentBuilder.listSplitter())
             .producer("producer", new MyProducer()).build();
     }

 ````


