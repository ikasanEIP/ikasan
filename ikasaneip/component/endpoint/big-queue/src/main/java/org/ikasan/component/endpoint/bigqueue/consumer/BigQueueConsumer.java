package org.ikasan.component.endpoint.bigqueue.consumer;

import com.google.common.util.concurrent.ListenableFuture;
import org.ikasan.bigqueue.IBigQueue;
import org.ikasan.component.endpoint.bigqueue.consumer.configuration.BigQueueConsumerConfiguration;
import org.ikasan.component.endpoint.bigqueue.serialiser.BigQueueMessageJsonSerialiser;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.component.endpoint.EndpointListener;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.event.*;
import org.ikasan.spec.flow.FlowEvent;
import org.ikasan.spec.management.ManagedIdentifierService;
import org.ikasan.spec.resubmission.ResubmissionEventFactory;
import org.ikasan.spec.resubmission.ResubmissionService;
import org.ikasan.spec.serialiser.Serialiser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of a BigQueue consumer.
 *
 * @author Ikasan Development Team
 */
public class BigQueueConsumer<T>
    implements Consumer<EventListener<?>,EventFactory>,
    ManagedIdentifierService<ManagedRelatedEventIdentifierService>, EndpointListener<T, Throwable>, MessageListener<T>,
    ConfiguredResource<BigQueueConsumerConfiguration>,
    ResubmissionService<T>, XAResource {
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

    protected Serialiser<T,byte[]> serialiser = new BigQueueMessageJsonSerialiser();;

    private InboundQueueMessageRunner inboundQueueMessageRunner;

    private TransactionManager transactionManager;

    private BigQueueConsumerConfiguration bigQueueConsumerConfiguration;

    private String configurationId;

    /**
     * Constructor
     *
     * @param inboundQueue
     * @param inboundQueueMessageRunner
     * @param transactionManager
     */
    public BigQueueConsumer(IBigQueue inboundQueue, InboundQueueMessageRunner inboundQueueMessageRunner
        , TransactionManager transactionManager) {
        this.inboundQueue = inboundQueue;
        if(this.inboundQueue == null) {
            throw new IllegalArgumentException("inboundQueue cannot bee null!");
        }
        this.inboundQueueMessageRunner = inboundQueueMessageRunner;
        if(this.inboundQueueMessageRunner == null) {
            throw new IllegalArgumentException("inboundQueueMessageRunner cannot bee null!");
        }
        this.transactionManager = transactionManager;
        if(this.transactionManager == null) {
            throw new IllegalArgumentException("transactionManager cannot bee null!");
        }
    }

    /**
     * Override the default implementation of the serialiser.
     * The default serialiser of for big queue messages.
     * @param serialiser
     */
    public void setSerialiser(Serialiser<T, byte[]> serialiser) {
        this.serialiser = serialiser;
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

        FlowEvent<?,?> flowEvent;

        if(this.managedRelatedEventIdentifierService != null) {
            flowEvent = flowEventFactory.newEvent(managedRelatedEventIdentifierService.getEventIdentifier(resubmission.getEvent())
                , managedRelatedEventIdentifierService.getRelatedEventIdentifier(resubmission.getEvent()), resubmission);
        }
        else {
            flowEvent = flowEventFactory.newEvent(String.valueOf(resubmission.getEvent().hashCode())
                , String.valueOf(resubmission.getEvent().hashCode()), resubmission.getEvent());
        }

        Resubmission resubmissionEvent = this.resubmissionEventFactory.newResubmissionEvent(flowEvent);


        this.eventListener.invoke(resubmissionEvent);
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
        logger.info("Starting BigQueueConsumer - " + this.configurationId);
        this.bigQueueListenerExecutor = Executors.newSingleThreadExecutor();
        this.addInboundListener();
        this.isRunning = true;
        logger.info("Started BigQueueConsumer - " + this.configurationId);
    }

    private void addInboundListener() {
        logger.debug("Adding inbound message listener - bigQueueListenerExecutor: " + bigQueueListenerExecutor);
        if(this.bigQueueListenerExecutor != null) {
            this.listenableFuture = this.inboundQueue.peekAsync();
            logger.debug("Peeking onto the inbound queue. listenableFuture: " + listenableFuture);
            this.listenableFuture.addListener(this.inboundQueueMessageRunner
                , bigQueueListenerExecutor);
            logger.debug("Sucessfully added inbound listener!");
        }
    }

    @Override
    public boolean isRunning() {
        return this.isRunning;
    }

    @Override
    public void stop() {
        logger.info("Stopping BigQueueConsumer - " + this.configurationId);
        this.isRunning = false;
        if(this.listenableFuture != null) {
            this.listenableFuture.cancel(true);
            logger.debug("future is cancelled" + this.listenableFuture.isCancelled());
            logger.debug("future is done" + this.listenableFuture.isDone());
            this.listenableFuture = null;
        }
        if(this.bigQueueListenerExecutor != null) {
            try {
                this.shutdownExecutor(this.bigQueueListenerExecutor);
            }
            catch (Exception e) {
                logger.warn("Unable to shut down big queue executor!", e);
            }
            this.bigQueueListenerExecutor = null;
        }
        this.isRunning = false;
        logger.info("Stopped BigQueueConsumer - " + this.configurationId);
    }

    @Override
    public void onMessage(T event) {
        logger.debug("Received message " + event);

        try {
            this.transactionManager.getTransaction().enlistResource(this);
            FlowEvent<?, ?> flowEvent;
            if(this.managedRelatedEventIdentifierService != null) {
                flowEvent = flowEventFactory.newEvent(managedRelatedEventIdentifierService.getEventIdentifier(event)
                    , managedRelatedEventIdentifierService.getRelatedEventIdentifier(event), event);
            }
            else {
                flowEvent = flowEventFactory.newEvent(String.valueOf(event.hashCode()), String.valueOf(event.hashCode()), event);
            }
            invoke(flowEvent);
        }
        catch (RollbackException | SystemException e) {
            logger.debug("An exception has occurred attemping to process event!", e);
            this.onException(e);
        }
    }

    @Override
    public void onException(Throwable throwable) {
        if (throwable instanceof ForceTransactionRollbackException)
        {
            logger.info("Ignoring rethrown ForceTransactionRollbackException");
        }
        else if(this.eventListener != null) {
            this.eventListener.invoke(throwable);
        }
        else {
            logger.error(throwable.getMessage(), throwable);
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

    @Override
    public BigQueueConsumerConfiguration getConfiguration() {
        return this.bigQueueConsumerConfiguration;
    }

    @Override
    public void setConfiguration(BigQueueConsumerConfiguration configuration) {
        this.bigQueueConsumerConfiguration = configuration;
    }

    @Override
    public String getConfiguredResourceId() {
        return this.configurationId;
    }

    @Override
    public void setConfiguredResourceId(String id) {
        this.configurationId = id;
    }

    @Override
    public void commit(Xid xid, boolean onePhase) throws XAException {
        logger.debug("commit " + xid);
        try {
            logger.debug("dequeuing message due to commit");
            inboundQueue.dequeue();
            logger.debug("garbage collecting queue due to commit");
            inboundQueue.gc();
        } catch (IOException e) {
            logger.debug("An exception has occurred commiting transaction!", e);
            throw new XAException(e.getMessage());
        }
        finally {
            this.addInboundListener();
        }
        logger.debug("commit complete" + xid);
    }

    @Override
    public void end(Xid xid, int flags) throws XAException {
        logger.debug("end " + xid);
    }

    @Override
    public void forget(Xid xid) throws XAException {
        logger.debug("forget " + xid);
    }

    @Override
    public int getTransactionTimeout() throws XAException {
        return 0;
    }

    @Override
    public boolean isSameRM(XAResource xares) throws XAException {
        return false;
    }

    @Override
    public int prepare(Xid xid) throws XAException {
        logger.debug("prepare " + xid);
        return 0;
    }

    @Override
    public Xid[] recover(int flag) throws XAException {
        return new Xid[0];
    }

    @Override
    public void rollback(Xid xid) throws XAException {
        logger.debug("rollback " + xid);
        try {
            if(this.bigQueueConsumerConfiguration.isPutErrorsToBackOfQueue()) {
                inboundQueue.enqueue(inboundQueue.dequeue());
            }
        } catch (IOException e) {
            logger.debug("An exception has occurred rolling back transaction!", e);
            throw new XAException(e.getMessage());
        }
        finally {
            this.addInboundListener();
        }
    }

    @Override
    public boolean setTransactionTimeout(int seconds) throws XAException {
        return false;
    }

    @Override
    public void start(Xid xid, int flags) throws XAException {
        logger.debug("start " + xid);
    }

    /**
     * Helper method to shut down the executor.
     * @param executor
     */
    private void shutdownExecutor(ExecutorService executor) {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(2000, TimeUnit.MILLISECONDS)) {
                executor.shutdownNow();
            }
        }
        catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }
}
