package org.ikasan.ootb.scheduler.agent.module.component.endpoint.consumer;

import com.google.common.util.concurrent.ListenableFuture;
import com.leansoft.bigqueue.IBigQueue;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.component.endpoint.EndpointListener;
import org.ikasan.spec.component.endpoint.MultiThreadedCapable;
import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.component.transformation.TransformationException;
import org.ikasan.spec.event.EventFactory;
import org.ikasan.spec.event.EventListener;
import org.ikasan.spec.event.ManagedRelatedEventIdentifierService;
import org.ikasan.spec.event.Resubmission;
import org.ikasan.spec.exclusion.ExclusionService;
import org.ikasan.spec.exclusion.IsExclusionServiceAware;
import org.ikasan.spec.flow.FlowEvent;
import org.ikasan.spec.management.ManagedIdentifierService;
import org.ikasan.spec.resubmission.ResubmissionEventFactory;
import org.ikasan.spec.resubmission.ResubmissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Implementation of a Kafka client consumer.
 *
 * @author Ikasan Development Team
 */
public class BigQueueConsumer
    implements Consumer<EventListener<?>,EventFactory>,
    ManagedIdentifierService<ManagedRelatedEventIdentifierService>, EndpointListener<String, Throwable>,
    ResubmissionService<String>, Converter<String,Object>,
    MultiThreadedCapable, IsExclusionServiceAware
{
    /** class logger */
    private static Logger logger = LoggerFactory.getLogger(BigQueueConsumer.class);

    private String configurationId;

    private boolean isRunning;

    /** consumer event factory */
    protected EventFactory<FlowEvent<?,?>> flowEventFactory;

    /** resubmission event factory */
    protected ResubmissionEventFactory<Resubmission<?>> resubmissionEventFactory;

    /** consumer event listener */
    protected EventListener eventListener;

    protected IBigQueue inboundQueue;

    protected ExecutorService bigQueueListenerExecutor;

    protected ListenableFuture<byte[]> listenableFuture;


    public BigQueueConsumer(IBigQueue inboundQueue) {
        this.inboundQueue = inboundQueue;
    }


    /**
     * Invoke the eventListener with the given flowEvent.
     * @param flowEvent
     */
    protected void invoke(FlowEvent flowEvent)
    {
        if(this.eventListener == null)
        {
            throw new RuntimeException("No active eventListeners registered for flowEvent!");
        }

        this.eventListener.invoke(flowEvent);
    }

    /**
     * Invoke the eventListener with the given resubmission.
     * @param resubmission
     */
    protected void invoke(Resubmission resubmission)
    {
        if(this.eventListener == null)
        {
            throw new RuntimeException("No active eventListeners registered for resubmission event!");
        }

        this.eventListener.invoke(resubmission);
    }

    @Override
    public void setListener(EventListener<?> eventListener) {
        this.eventListener = eventListener;
    }

    @Override
    public void setEventFactory(EventFactory eventFactory) {
        this.flowEventFactory = eventFactory;
    }

    @Override
    public EventFactory getEventFactory() {
        return this.flowEventFactory;
    }

    @Override
    public void start() {
            this.bigQueueListenerExecutor = Executors.newSingleThreadExecutor();
            this.addInboundListener();
            this.isRunning = true;
    }

    private void addInboundListener() {
        this.listenableFuture = this.inboundQueue.peekAsync();
        this.listenableFuture.addListener(new InboundQueueMessageRunner()
            , bigQueueListenerExecutor);
    }

    @Override
    public boolean isRunning() {
        return this.isRunning;
    }

    @Override
    public void stop() {
        this.isRunning = false;
        this.listenableFuture.cancel(false);
        this.listenableFuture = null;
        this.bigQueueListenerExecutor.shutdownNow();
        this.bigQueueListenerExecutor = null;
    }

    @Override
    public void onMessage(String event) {
        logger.info("Received message " + new String(event));

        try {
            FlowEvent<?, ?> flowEvent = flowEventFactory.newEvent("", "", new String(event));
            invoke(flowEvent);
        }
        catch (Exception e) {
            this.eventListener.invoke(e);
        }
    }

    @Override
    public void onException(Throwable throwable) {
        this.eventListener.invoke(throwable);
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public Object convert(String payload) throws TransformationException {
        return payload;
    }

    @Override
    public void setManagedIdentifierService(ManagedRelatedEventIdentifierService managedRelatedEventIdentifierService) {

    }

    @Override
    public void onResubmission(String event) {
        logger.info("Resubmission message " + event);

        Resubmission flowEvent = resubmissionEventFactory.newResubmissionEvent(event);
        invoke(flowEvent);
    }

    @Override
    public void setResubmissionEventFactory(ResubmissionEventFactory resubmissionEventFactory) {
        this.resubmissionEventFactory = resubmissionEventFactory;
    }

    @Override
    public void setExclusionService(ExclusionService exclusionService) {

    }

    private class InboundQueueMessageRunner implements Runnable {

        @Override
        public void run() {
            try {
                byte[] event = inboundQueue.peek();
                if(event == null) {
                    return;
                }

                onMessage(new String(event));

                inboundQueue.dequeue();
                inboundQueue.gc();
            }
            catch (Exception e) {
                // do something
                e.printStackTrace();
            }
            finally {
                addInboundListener();
            }
        }
    }
}
