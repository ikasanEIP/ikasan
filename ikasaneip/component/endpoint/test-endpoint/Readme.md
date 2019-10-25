[<< Component Quick Start](../../Readme.md)
![IKASAN](../../../developer/docs/quickstart-images/Ikasan-title-transparent.png)
# Event Generating Consumer

Utility consumer for the generation of ad-hoc events for demonstration or testing of flows.

<br/>

##### Operation
Default behaviour is the generation of real-time "Test Message ###" String events to this consumer.
This behaviour can be overridden by the ```setEndpointEventProvider(EndpointEventProvider endpointEventProvider)``` where you can provide your own event provider to this consumer.

```java
org.ikasan.spec.event.MessageListener.onMessage(T);
org.ikasan.spec.event.ExceptionListener.onException(Throwable);
```

##### Supported Features
The following Ikasan features are supported by this component.

||| 
| :----- | :------: | 
| **Feature**| **Support** | 
| Managed Lifecycle| Yes | 
| Component Configuration| No | 
| Event Resubmission| Yes | 
| Event Record/Replay| Yes | 

##### Mandatory Configuration Options
No mandatory configuration required.

##### Optional Configuration Options

| Option | Type | Purpose |
| --- | --- | --- |
| eventEndpointProvider | EventEndpointProvider | Custom implementation of the EventEndpointProvider contract.|

###### Sample Usage - Ikasan Java FluentAPI
Example below shows how easy it is to provide your own custom event endpoint provider.

```java
public class ModuleConfig 
{
  @Resource
  private BuilderFactory builderFactory;

  public Consumer getEventGeneratingConsumer() 
  {
        return builderFactory.getComponentBuilder().eventGeneratingConsumer()
                            .setEndpointEventProvider(new MyTechEndpointProvider());
  }
  
  /**
  * Custom endpointEventProvider implementation.
  */
  class MyTechEndpointProvider implements EndpointEventProvider<String>
  {
      long count = 0;

      /**
      * Method to return the required event data to be passed into the Consumer.
      * @return String
      */
      @Override
      public String getEvent()
      {
          return "Hello " + ++count;
      }

      /**
      * Method called to rollback the last delivered event as part of the transaction contract.
      */
      @Override
      public void rollback()
      {
          --count;
      }
  }

}

```
You can optionally set the ManagedEventIdentifierService specific to this event provider if the default which simply creates a hashcode of the entire event is not sufficient.

```java
public class ModuleConfig 
{
  @Resource
  private BuilderFactory builderFactory;

  public Consumer getEventGeneratingConsumer() 
  {
        return builderFactory.getComponentBuilder().eventGeneratingConsumer()
                            .setEndpointEventProvider(new MyTechEndpointProvider())
                            .setManagedEventIdentifierService(new MyIdentifierService());
  }
  
    /**
     * Custom ManagedEventIdentifierService implementation.
     */
    class MyIdentifierService<T> implements ManagedEventIdentifierService<String,T>
    {
        @Override
        public void setEventIdentifier(String s, T t) throws ManagedEventIdentifierException
        {
            // not used for consumers so can ignore
        }
    
        @Override
        public String getEventIdentifier(T t) throws ManagedEventIdentifierException
        {
            // return whatever you need from the incoming event as long as it is consistent for the same event instance
            return String.valueOf(t.hashCode() + whatever);
        }
    }
}

```
