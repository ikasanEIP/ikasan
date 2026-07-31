/*
 * $Id$
 * $URL$
 *
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 *
 * Distributed under the Modified BSD License.
 * Copyright notice: The copyright for this software and a full listing
 * of individual contributors are as shown in the packaged copyright.txt
 * file.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  - Neither the name of the ORGANIZATION nor the names of its contributors may
 *    be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 */
package org.ikasan.component.endpoint.pulsar.consumer;

import jakarta.transaction.RollbackException;
import jakarta.transaction.SystemException;
import jakarta.transaction.TransactionManager;
import org.apache.pulsar.client.api.*;
import org.ikasan.component.endpoint.pulsar.consumer.configuration.PulsarConsumerConfiguration;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.component.endpoint.EndpointListener;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.event.*;
import org.ikasan.spec.event.MessageListener;
import org.ikasan.spec.flow.FlowEvent;
import org.ikasan.spec.management.ManagedIdentifierService;
import org.ikasan.spec.resubmission.ResubmissionEventFactory;
import org.ikasan.spec.resubmission.ResubmissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of an Apache Pulsar consumer.
 *
 * @author Ikasan Development Team
 */
public class PulsarConsumer
    implements Consumer<EventListener<?>, EventFactory>,
    ManagedIdentifierService<ManagedRelatedEventIdentifierService>, EndpointListener<byte[], Throwable>, MessageListener<byte[]>,
    ConfiguredResource<PulsarConsumerConfiguration>,
    ResubmissionService<byte[]>, XAResource {

    /** class logger */
    private static Logger logger = LoggerFactory.getLogger(PulsarConsumer.class);

    private boolean isRunning;

    /** consumer event factory */
    protected EventFactory<FlowEvent<?, ?>> flowEventFactory;

    /** resubmission event factory */
    protected ResubmissionEventFactory<Resubmission<?>> resubmissionEventFactory;

    /** consumer event listener */
    protected EventListener eventListener;

    /** Pulsar client */
    protected PulsarClient pulsarClient;

    /** Pulsar consumer */
    protected org.apache.pulsar.client.api.Consumer<byte[]> consumer;

    /** Executor service for message processing */
    protected ExecutorService messageListenerExecutor;

    protected ManagedRelatedEventIdentifierService managedRelatedEventIdentifierService;

    private TransactionManager transactionManager;

    private PulsarConsumerConfiguration configuration;

    private String configurationId;

    private InboundQueueMessageListener inboundQueueMessageListener;

//    /** Current message being processed */
//    private Message<byte[]> currentMessage;

    /**
     * Constructor
     *
     * @param transactionManager Transaction manager for XA support
     */
    public PulsarConsumer(TransactionManager transactionManager, InboundQueueMessageListener inboundQueueMessageListener) {
        this.transactionManager = transactionManager;
        if (this.transactionManager == null) {
            throw new IllegalArgumentException("transactionManager cannot be null!");
        }
        this.inboundQueueMessageListener = inboundQueueMessageListener;
        if (this.inboundQueueMessageListener == null) {
            throw new IllegalArgumentException("inboundQueueMessageListener cannot be null!");
        }
    }

    /**
     * Invoke the eventListener with the given flowEvent.
     *
     * @param flowEvent Flow event to process
     */
    protected void invoke(FlowEvent flowEvent) {
        if (this.eventListener == null) {
            throw new RuntimeException("No active eventListeners registered for flowEvent!");
        }

        this.eventListener.invoke(flowEvent);
    }

    /**
     * Invoke the eventListener with the given resubmission.
     *
     * @param resubmission Resubmission event
     */
    protected void invoke(Resubmission resubmission) {
        if (this.eventListener == null) {
            throw new RuntimeException("No active eventListeners registered for resubmission event!");
        }

        FlowEvent<?, ?> flowEvent;

        if (this.managedRelatedEventIdentifierService != null) {
            flowEvent = flowEventFactory.newEvent(
                managedRelatedEventIdentifierService.getEventIdentifier(resubmission.getEvent()),
                managedRelatedEventIdentifierService.getRelatedEventIdentifier(resubmission.getEvent()),
                resubmission);
        } else {
            flowEvent = flowEventFactory.newEvent(
                String.valueOf(resubmission.getEvent().hashCode()),
                String.valueOf(resubmission.getEvent().hashCode()),
                resubmission.getEvent());
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
        logger.info("Starting PulsarConsumer - " + this.configurationId);
        try {
            // Create Pulsar client
            ClientBuilder clientBuilder = PulsarClient.builder()
                .serviceUrl(configuration.getServiceUrl());

            // Configure authentication if enabled
            if (configuration.isAuthenticationEnabled()) {
                clientBuilder.authentication(
                    configuration.getAuthPluginClassName(),
                    configuration.getAuthParams()
                );
            }

            // Configure TLS if enabled
            if (configuration.isTlsEnabled()) {
                clientBuilder.tlsTrustCertsFilePath(configuration.getTlsTrustCertsFilePath());
            }

            this.pulsarClient = clientBuilder.build();

            // Create consumer builder
            ConsumerBuilder<byte[]> consumerBuilder = pulsarClient.newConsumer(Schema.BYTES)
                .topics(Arrays.asList(configuration.getTopics()))
                .subscriptionName(configuration.getSubscriptionName())
                .subscriptionType(SubscriptionType.valueOf(configuration.getSubscriptionType()))
                .receiverQueueSize(configuration.getReceiverQueueSize())
                .negativeAckRedeliveryDelay(this.configuration.getNegativeAckRedeliveryDelay(), TimeUnit.MILLISECONDS);

            // Set consumer name if provided
            if (configuration.getConsumerName() != null && !configuration.getConsumerName().isEmpty()) {
                consumerBuilder.consumerName(configuration.getConsumerName());
            }

            // Set acknowledgment timeout if configured
            if (configuration.getAckTimeoutMillis() > 0) {
                consumerBuilder.ackTimeout(configuration.getAckTimeoutMillis(), TimeUnit.MILLISECONDS);
            }

            // Enable batch index acknowledgment if configured
            if (configuration.isBatchIndexAckEnabled()) {
                consumerBuilder.enableBatchIndexAcknowledgment(true);
            }

            // Set priority level
            if (configuration.getPriorityLevel() > 0) {
                consumerBuilder.priorityLevel(configuration.getPriorityLevel());
            }

            // Set max total receiver queue size across partitions
            if (configuration.getMaxTotalReceiverQueueSizeAcrossPartitions() > 0) {
                consumerBuilder.maxTotalReceiverQueueSizeAcrossPartitions(configuration.getMaxTotalReceiverQueueSizeAcrossPartitions());
            }

            // Set read compacted
            if (configuration.isReadCompacted()) {
                consumerBuilder.readCompacted(true);
            }

            // Set subscription initial position
            if (configuration.getSubscriptionInitialPosition() != null) {
                switch (configuration.getSubscriptionInitialPosition()) {
                    case "Latest":
                        consumerBuilder.subscriptionInitialPosition(org.apache.pulsar.client.api.SubscriptionInitialPosition.Latest);
                        break;
                    case "Earliest":
                        consumerBuilder.subscriptionInitialPosition(org.apache.pulsar.client.api.SubscriptionInitialPosition.Earliest);
                        break;
                }
            }

            // Set pattern auto discovery period
            if (configuration.isPatternAutoDiscoveryPeriod() && configuration.getAutoDiscoveryPeriodMinutes() > 0) {
                consumerBuilder.patternAutoDiscoveryPeriod(configuration.getAutoDiscoveryPeriodMinutes(), TimeUnit.MINUTES);
            }

            // Configure dead letter policy if enabled
            if (configuration.isRetryEnable() && configuration.getMaxRedeliverCount() > 0) {
                org.apache.pulsar.client.api.DeadLetterPolicy.DeadLetterPolicyBuilder dlpBuilder =
                    org.apache.pulsar.client.api.DeadLetterPolicy.builder()
                        .maxRedeliverCount(configuration.getMaxRedeliverCount());

                if (configuration.getDeadLetterTopic() != null && !configuration.getDeadLetterTopic().isEmpty()) {
                    dlpBuilder.deadLetterTopic(configuration.getDeadLetterTopic());
                }

                consumerBuilder.deadLetterPolicy(dlpBuilder.build());
                consumerBuilder.enableRetry(true);
            }

            // Set start message ID inclusive
            if (configuration.isStartMessageIdInclusive()) {
                consumerBuilder.startMessageIdInclusive();
            }

            // Enable batch receive
            if (configuration.isBatchReceiveEnabled()) {
                consumerBuilder.enableBatchIndexAcknowledgment(true);
            }

            // Set ack receipt enabled
            if (configuration.isAckReceiptEnabled()) {
                consumerBuilder.isAckReceiptEnabled(true);
            }

            // Set pool messages
            consumerBuilder.poolMessages(configuration.isPoolMessages());

            // Set replicate subscription state
            if (configuration.isReplicateSubscriptionState()) {
                consumerBuilder.replicateSubscriptionState(true);
            }

            // Set ack timeout tick time
            if (configuration.getAckTimeoutTickTimeMillis() > 0) {
                consumerBuilder.ackTimeoutTickTime(configuration.getAckTimeoutTickTimeMillis(), TimeUnit.MILLISECONDS);
            }

            // Set auto ack oldest chunked message on queue full
            if (configuration.isAutoAckOldestChunkedMessageOnQueueFull()) {
                consumerBuilder.autoAckOldestChunkedMessageOnQueueFull(true);
            }

            // Set max pending chunked messages
            if (configuration.getMaxPendingChunkedMessage() > 0) {
                consumerBuilder.maxPendingChunkedMessage(configuration.getMaxPendingChunkedMessage());
            }

            // Set expire time of incomplete chunked messages
            if (configuration.getExpireTimeOfIncompleteChunkedMessageMillis() > 0) {
                consumerBuilder.expireTimeOfIncompleteChunkedMessage(
                    configuration.getExpireTimeOfIncompleteChunkedMessageMillis(),
                    TimeUnit.MILLISECONDS
                );
            }

            // Set acknowledgement group time
            if (configuration.getAcknowledgementGroupTimeMillis() > 0) {
                consumerBuilder.acknowledgmentGroupTime(
                    configuration.getAcknowledgementGroupTimeMillis(),
                    TimeUnit.MILLISECONDS
                );
            }

            // Set auto scale receiver queue size
            consumerBuilder.autoScaledReceiverQueueSizeEnabled(configuration.isAutoScaleReceiverQueueSizeEnabled());


            // Create message listener executor
            this.messageListenerExecutor = Executors.newFixedThreadPool(configuration.getMessageListenerThreads());

            // Set message listener
            consumerBuilder.messageListener(this.inboundQueueMessageListener);

            this.consumer = consumerBuilder.subscribe();

            this.isRunning = true;
            logger.info("Started PulsarConsumer - " + this.configurationId);
        } catch (PulsarClientException e) {
            logger.error("Failed to start PulsarConsumer", e);
            throw new RuntimeException("Failed to start PulsarConsumer", e);
        }
    }

    @Override
    public boolean isRunning() {
        return this.isRunning;
    }

    @Override
    public synchronized void stop() {
        logger.info("Stopping PulsarConsumer - " + this.configurationId);
        this.isRunning = false;

        if (this.messageListenerExecutor != null) {
            try {
                logger.info("Shutting down executor - " + this.messageListenerExecutor);
                this.shutdownExecutor(this.messageListenerExecutor);
            } catch (Exception e) {
                logger.warn("Unable to shut down message listener executor!", e);
            }
            this.messageListenerExecutor = null;
        }

        if (this.consumer != null) {
            this.consumer.closeAsync();
            this.consumer = null;
        }

        if (this.pulsarClient != null) {
            this.pulsarClient.closeAsync();
            this.pulsarClient = null;
        }

        logger.info("Stopped PulsarConsumer - " + this.configurationId);
    }

    @Override
    public void onMessage(byte[] event) {
        logger.debug("Received message " + event);

        try {
            this.transactionManager.getTransaction().enlistResource(this);
            FlowEvent<?, ?> flowEvent;
            if (this.managedRelatedEventIdentifierService != null) {
                flowEvent = flowEventFactory.newEvent(
                    managedRelatedEventIdentifierService.getEventIdentifier(event),
                    managedRelatedEventIdentifierService.getRelatedEventIdentifier(event),
                    event);
            } else {
                flowEvent = flowEventFactory.newEvent(
                    String.valueOf(new String(event).hashCode()),
                    String.valueOf(new String(event).hashCode()),
                    event);
            }
            invoke(flowEvent);
        } catch (RollbackException | SystemException e) {
            logger.debug("An exception has occurred attempting to process event!", e);
            this.onException(e);
        }
    }

    @Override
    public void onException(Throwable throwable) {
        if (throwable instanceof ForceTransactionRollbackException) {
            logger.info("Ignoring rethrown ForceTransactionRollbackException");
        } else if (this.eventListener != null) {
            try {
                this.eventListener.invoke(throwable);
            } catch (ForceTransactionRollbackException e) {
                this.stop();
            }
        } else {
            logger.error(throwable.getMessage(), throwable);
        }
    }

    @Override
    public boolean isActive() {
        return this.isRunning;
    }

    @Override
    public void setManagedIdentifierService(ManagedRelatedEventIdentifierService managedRelatedEventIdentifierService) {
        this.managedRelatedEventIdentifierService = managedRelatedEventIdentifierService;
    }

    @Override
    public void onResubmission(byte[] event) {
        logger.info("Resubmission message " + event);

        Resubmission flowEvent = resubmissionEventFactory.newResubmissionEvent(event);
        invoke(flowEvent);
    }

    @Override
    public void setResubmissionEventFactory(ResubmissionEventFactory resubmissionEventFactory) {
        this.resubmissionEventFactory = resubmissionEventFactory;
    }

    @Override
    public PulsarConsumerConfiguration getConfiguration() {
        return this.configuration;
    }

    @Override
    public void setConfiguration(PulsarConsumerConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public String getConfiguredResourceId() {
        return this.configurationId;
    }

    @Override
    public void setConfiguredResourceId(String id) {
        this.configurationId = id;
    }

    // XAResource implementation

    @Override
    public void commit(Xid xid, boolean onePhase) throws XAException {
        logger.debug("commit " + xid);
        try {
            if (this.inboundQueueMessageListener.getCurrentMessage() != null && !configuration.isAutoAcknowledge()) {
                logger.debug("acknowledging message due to commit");
                this.consumer.acknowledge(this.inboundQueueMessageListener.getCurrentMessage());
                this.inboundQueueMessageListener.reset();
            }
        } catch (PulsarClientException e) {
            logger.error("An exception has occurred committing transaction!", e);
            throw new XAException(e.getMessage());
        }
        logger.debug("commit complete " + xid);
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
        return XA_OK;
    }

    @Override
    public Xid[] recover(int flag) throws XAException {
        return new Xid[0];
    }

    @Override
    public void rollback(Xid xid) throws XAException {
        logger.debug("rollback " + xid);
        try {
            if (this.consumer != null && this.inboundQueueMessageListener.getCurrentMessage() != null
                && !configuration.isAutoAcknowledge()) {
                logger.debug("negatively acknowledging message due to rollback");
                this.consumer.negativeAcknowledge(this.inboundQueueMessageListener.getCurrentMessage());
                this.inboundQueueMessageListener.reset();
            }
        } catch (Exception e) {
            logger.error("An exception has occurred rolling back transaction!", e);
            throw new XAException(e.getMessage());
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
     *
     * @param executor Executor to shutdown
     */
    private void shutdownExecutor(ExecutorService executor) {
        executor.shutdownNow();
        try {
            if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                logger.warn("Executor did not terminate in the specified time.");
            }
        } catch (InterruptedException e) {
            logger.warn("Interrupted while waiting for executor to terminate", e);
            Thread.currentThread().interrupt();
        }
    }
}
