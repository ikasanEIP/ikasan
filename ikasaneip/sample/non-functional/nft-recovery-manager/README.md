# nft-recovery-manager
This module has been created to provide an integration test framework in order to test the behaviour of the Ikasan 
[ScheduledRecoveryManager](../../../recovery-manager/src/main/java/org/ikasan/recovery/ScheduledRecoveryManager.java). 
The approach taken has been to identify various [Consumer](../../../spec/component/src/main/java/org/ikasan/spec/component/endpoint/Consumer.java) 
types that are the entry point into a [Flow](../../../spec/flow/src/main/java/org/ikasan/spec/flow/Flow.java). `Consumers` are the main
Ikasan component type that interacts with the scheduled recovery manager.

The following `Consumer` implementations have been chosen to exercise the full range of use cases relevant to the `ScheduledRecoveryManager`.
- [JmsContainerConsumer](../../../component/endpoint/jms-spring/src/main/java/org/ikasan/component/endpoint/jms/spring/consumer/JmsContainerConsumer.java) chosen because because it is multi thread capable.
- [BigQueueConsumer](../../../component/endpoint/big-queue/src/main/java/org/ikasan/component/endpoint/bigqueue/consumer/BigQueueConsumer.java) chosen because it is like a bespoke implementation and can throw a recovery exception from its start method.
- [ScheduledConsumer](../../../component/endpoint/quartz-schedule/src/main/java/org/ikasan/component/endpoint/quartz/consumer/ScheduledConsumer.java) chosen because it runs on its own business schedule and also because it is a [ManagedResource](../../../spec/service/management/src/main/java/org/ikasan/spec/management/ManagedResource.java).

In order to exercise the recovery manager the following configuration has been applied that are relevant to the recovery manager.
````properties
ikasan.exceptions.retry-configs.[0].className=org.ikasan.spec.component.endpoint.EndpointException
ikasan.exceptions.retry-configs.[0].delayInMillis=100
ikasan.exceptions.retry-configs.[0].maxRetries=20

ikasan.exceptions.scheduled-retry-configs.[0].className=com.ikasan.sample.spring.boot.builderpattern.SampleScheduledRecoveryGeneratedException
ikasan.exceptions.scheduled-retry-configs.[0].cronExpression=0/1 * * * * ?
ikasan.exceptions.scheduled-retry-configs.[0].maxRetries=10

ikasan.exceptions.excludedClasses[0]=org.ikasan.spec.component.transformation.TransformationException
ikasan.exceptions.excludedClasses[1]=com.ikasan.sample.spring.boot.builderpattern.SampleGeneratedException

ikasan.exceptions.stopClasses[0]=java.lang.RuntimeException
````
In layman terms:
- if a component within a flow encounters a `org.ikasan.spec.component.endpoint.EndpointException`, the flow will go into recovery and retry every `100` milliseconds for a maximum of `20` times, at which point the flow will go into stopped in error.
- if a component within a flow encounters a `com.ikasan.sample.spring.boot.builderpattern.SampleScheduledRecoveryGeneratedException`, the flow will go into recovery and retry on the following cron schedule `0/1 * * * * ?` (once every second) for a maximum of `10` times, at which point the flow will go into stopped in error.
- if a component within a flow encounters a `org.ikasan.spec.component.transformation.TransformationException` or `com.ikasan.sample.spring.boot.builderpattern.SampleGeneratedException` the associated event will be excluded.
- if a component within a flow encounters a `java.lang.RuntimeException` the flow will immediately go into stopped in error.

For each of above consumer types the following integration tests have been built.
- Flow goes into delayed recovery and subsequently into stopped in error due to the retries being exceeded.
- Flow goes into delayed recovery and subsequently recovers due to the retry error being transient.
- Flow goes into scheduled recovery and subsequently into stopped in error due to the retries being exceeded.
- Flow goes into scheduled recovery and subsequently recovers due to the retry error being transient.
- Flow excludes a message and continues to remain in a running state.
- Flow goes into stopped in error when appropriate exception encountered.

The above tests are also applied across a multi-threaded flow along with the following test:
- A multi-threaded flow with 5 threads has a transient error raised on one thread only, and once the transient error no longer occurs, the flow recovers successfully.

Lastly all of the above tests are applied to a flow with a bespoke consumer that throws various exceptions in its start method.
- The `Consumer` start method throws a retry exception that causes the flow to go into recovery, and the flow goes into `stoppedInError` are retry attempts expire.
- The `Consumer` start method throws a transient retry exception that recovers and the flow goes into a `running` state.

The following integration tests have been implemented in order test the above use cases:
- [JmsSampleFlowTest](src/test/java/com/ikasan/sample/spring/boot/builderpattern/JmsSampleFlowTest.java)
- [MultiThreadedJmsSampleFlowTest](src/test/java/com/ikasan/sample/spring/boot/builderpattern/MultiThreadedJmsSampleFlowTest.java)
- [BigQueueSampleFlowTest](src/test/java/com/ikasan/sample/spring/boot/builderpattern/BigQueueSampleFlowTest.java)
- [ScheduledToJmsFlowTest](src/test/java/com/ikasan/sample/spring/boot/builderpattern/ScheduledToJmsFlowTest.java)
