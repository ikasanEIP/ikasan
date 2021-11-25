package org.ikasan.component.endpoint.bigqueue.consumer;

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
import org.ikasan.spec.serialiser.Serialiser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Implementation of a Kafka client consumer.
 *
 * @author Ikasan Development Team
 */
public class BigQueueConsumer<T>
    implements Consumer<EventListener<?>,EventFactory>,
    ManagedIdentifierService<ManagedRelatedEventIdentifierService>, EndpointListener<T, Throwable>,
    ResubmissionService<T> {
    /** class logger */
    private static Logger logger = LoggerFactory.getLogger(BigQueueConsumer.class);

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

    protected ManagedRelatedEventIdentifierService managedRelatedEventIdentifierService;

    protected Serialiser<T,byte[]> serialiser;

    /**
     * Constructor
     *
     * @param inboundQueue
     */
    public BigQueueConsumer(IBigQueue inboundQueue, Serialiser<T,byte[]> serialiser) {
        this.inboundQueue = inboundQueue;
        if(this.inboundQueue == null) {
            throw new IllegalArgumentException("inboundQueue cannot bee null!");
        }
        this.serialiser = serialiser;
        if(this.serialiser == null) {
            throw new IllegalArgumentException("serialiser cannot bee null!");
        }
    }


    /**
     * Invoke the eventListener with the given flowEvent.
     * @param flowEvent
     */
    protected void invoke(FlowEvent flowEvent)
    {
        if(this.eventListener == null) {
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
        if(this.bigQueueListenerExecutor != null) {
            this.listenableFuture = this.inboundQueue.peekAsync();
            this.listenableFuture.addListener(new InboundQueueMessageRunner()
                , bigQueueListenerExecutor);
        }
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
        this.isRunning = false;
    }

    @Override
    public void onMessage(T event) {
        logger.info("Received message " + event);

        try {
            FlowEvent<?, ?> flowEvent;
            if(this.managedRelatedEventIdentifierService != null) {
                flowEvent = flowEventFactory.newEvent(managedRelatedEventIdentifierService.getEventIdentifier(event)
                    , managedRelatedEventIdentifierService.getRelatedEventIdentifier(event), event);
            }
            else {
                flowEvent = flowEventFactory.newEvent(event.hashCode(), event.hashCode(), event);
            }
            invoke(flowEvent);
        }
        catch (Exception e) {
            this.onException(e);
        }
    }

    @Override
    public void onException(Throwable throwable) {
        if(this.eventListener != null) {
            this.eventListener.invoke(throwable);
        }
        else {
            logger.error(throwable.getMessage());
            throwable.printStackTrace();
        }
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public void setManagedIdentifierService(ManagedRelatedEventIdentifierService managedRelatedEventIdentifierService) {
        this.managedRelatedEventIdentifierService = managedRelatedEventIdentifierService;
    }

    @Override
    public void onResubmission(T event) {
        logger.info("Resubmission message " + event);

        Resubmission flowEvent = resubmissionEventFactory.newResubmissionEvent(event);
        invoke(flowEvent);
    }

    @Override
    public void setResubmissionEventFactory(ResubmissionEventFactory resubmissionEventFactory) {
        this.resubmissionEventFactory = resubmissionEventFactory;
    }

    /**
     * Inner class to allow for us to listen asynchronously to the BigQueue.
     */
    private class InboundQueueMessageRunner implements Runnable {

        @Override
        public void run() {
            try {
                byte[] event = inboundQueue.peek();
                if(event == null) {
                    return;
                }

                onMessage(serialiser.deserialise(event));

                inboundQueue.dequeue();
                inboundQueue.gc();
            }
            catch (Exception e) {
                onException(e);
            }
            finally {
                addInboundListener();
            }
        }
    }
}
