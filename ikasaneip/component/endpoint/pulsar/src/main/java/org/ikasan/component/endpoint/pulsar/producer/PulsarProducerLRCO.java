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
package org.ikasan.component.endpoint.pulsar.producer;

import jakarta.transaction.TransactionManager;
import org.apache.pulsar.client.api.*;
import org.ikasan.component.endpoint.pulsar.producer.configuration.PulsarProducerConfiguration;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.flow.FlowEvent;
import org.ikasan.spec.management.ManagedResource;
import org.ikasan.spec.management.ManagedResourceRecoveryManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Apache Pulsar Producer implementation with Last Resource Commit Optimisation (LRCO).
 * This producer sends messages to Pulsar topics and participates in XA transactions.
 *
 * @author Ikasan Development Team
 */
public class PulsarProducerLRCO implements Producer<FlowEvent>,
    ConfiguredResource<PulsarProducerConfiguration>,
    ManagedResource {

    private static Logger logger = LoggerFactory.getLogger(PulsarProducerLRCO.class);

    private TransactionManager transactionManager;
    private PulsarClient pulsarClient;
    private org.apache.pulsar.client.api.Producer<byte[]> producer;
    private PulsarProducerConfiguration configuration;
    private String configuredResourceId;
    private PulsarConnection connection = null;

    private boolean isRecovering = false;
    private String criticalFailure = null;

    /**
     * Constructor
     *
     * @param transactionManager the transaction manager for XA support
     * @param configuration the producer configuration
     */
    public PulsarProducerLRCO(TransactionManager transactionManager, PulsarProducerConfiguration configuration) {
        this.transactionManager = transactionManager;
        if (this.transactionManager == null) {
            throw new IllegalArgumentException("transactionManager cannot be null!");
        }
        this.configuration = configuration;
        if (this.configuration == null) {
            throw new IllegalArgumentException("configuration cannot be null!");
        }
        if (this.configuration.getTopic() == null || this.configuration.getTopic().isEmpty()) {
            throw new IllegalArgumentException("configuration.topic cannot be null or empty!");
        }
    }

    @Override
    public void invoke(FlowEvent payload) throws EndpointException {
        try {
            if (producer == null) {
                throw new EndpointException("Pulsar producer is not started. Call startManagedResource() first.");
            }

            PulsarConnectionCallback callback = new PulsarConnectionCallbackImpl<>(payload, producer);
            connection = new PulsarConnection(callback);
            this.transactionManager.getTransaction().enlistResource(connection);
        } catch (Exception e) {
            throw new EndpointException(e);
        }
    }

    @Override
    public void startManagedResource() {
        logger.info("Starting Pulsar Producer - {}", configuredResourceId);
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

            // Create producer builder
            ProducerBuilder<byte[]> producerBuilder = pulsarClient.newProducer(Schema.BYTES)
                .topic(configuration.getTopic());

            // Set producer name if provided
            if (configuration.getProducerName() != null && !configuration.getProducerName().isEmpty()) {
                producerBuilder.producerName(configuration.getProducerName());
            }

            // Configure batching
            if (configuration.isBatchingEnabled()) {
                producerBuilder.enableBatching(true)
                    .batchingMaxMessages(configuration.getBatchingMaxMessages())
                    .batchingMaxPublishDelay(configuration.getBatchingMaxPublishDelayMillis(), TimeUnit.MILLISECONDS)
                    .batchingMaxBytes(configuration.getBatchingMaxBytes());
            } else {
                producerBuilder.enableBatching(false);
            }

            // Configure compression
            if (configuration.getCompressionType() != null && !configuration.getCompressionType().equals("NONE")) {
                producerBuilder.compressionType(CompressionType.valueOf(configuration.getCompressionType()));
            }

            // Configure send timeout
            producerBuilder.sendTimeout(configuration.getSendTimeoutMillis(), TimeUnit.MILLISECONDS);

            // Configure queue behavior
            producerBuilder.blockIfQueueFull(configuration.isBlockIfQueueFull())
                .maxPendingMessages(configuration.getMaxPendingMessages());

            // Configure message routing
            if (configuration.getMessageRoutingMode() != null) {
                switch (configuration.getMessageRoutingMode()) {
                    case "SinglePartition":
                        producerBuilder.messageRoutingMode(MessageRoutingMode.SinglePartition);
                        break;
                    case "RoundRobinPartition":
                        producerBuilder.messageRoutingMode(MessageRoutingMode.RoundRobinPartition);
                        break;
                    case "CustomPartition":
                        producerBuilder.messageRoutingMode(MessageRoutingMode.CustomPartition);
                        break;
                }
            }

            // Configure hashing scheme
            if (configuration.getHashingScheme() != null) {
                switch (configuration.getHashingScheme()) {
                    case "JavaStringHash":
                        producerBuilder.hashingScheme(HashingScheme.JavaStringHash);
                        break;
                    case "Murmur3_32Hash":
                        producerBuilder.hashingScheme(HashingScheme.Murmur3_32Hash);
                        break;
                }
            }

            // Configure chunking
            if (configuration.isChunkingEnabled()) {
                producerBuilder.enableChunking(true);
            }

            // Configure auto update partitions
            if (configuration.isAutoUpdatePartitions()) {
                producerBuilder.autoUpdatePartitions(true)
                    .autoUpdatePartitionsInterval(configuration.getAutoUpdatePartitionsIntervalSeconds(), TimeUnit.SECONDS);
            }

            // Configure max pending messages across partitions
            if (configuration.getMaxPendingMessagesAcrossPartitions() > 0) {
                producerBuilder.maxPendingMessagesAcrossPartitions(configuration.getMaxPendingMessagesAcrossPartitions());
            }

            // Configure multi-schema
            if (!configuration.isMultiSchema()) {
                producerBuilder.enableMultiSchema(false);
            }

            // Configure access mode
            if (configuration.getAccessMode() != null) {
                switch (configuration.getAccessMode()) {
                    case "Shared":
                        producerBuilder.accessMode(ProducerAccessMode.Shared);
                        break;
                    case "Exclusive":
                        producerBuilder.accessMode(ProducerAccessMode.Exclusive);
                        break;
                    case "WaitForExclusive":
                        producerBuilder.accessMode(ProducerAccessMode.WaitForExclusive);
                        break;
                }
            }

            // Configure lazy start of partitioned producers
            if (configuration.isLazyStartPartitionedProducers()) {
                producerBuilder.enableLazyStartPartitionedProducers(true);
            }

            // Configure initial sequence ID
            if (configuration.getInitialSequenceId() != null) {
                producerBuilder.initialSequenceId(configuration.getInitialSequenceId());
            }

            // Configure round robin router batching partition switch frequency
            if (configuration.getRoundRobinRouterBatchingPartitionSwitchFrequency() > 0) {
                producerBuilder.roundRobinRouterBatchingPartitionSwitchFrequency(
                    configuration.getRoundRobinRouterBatchingPartitionSwitchFrequency()
                );
            }

            // Create the producer
            this.producer = producerBuilder.create();

            this.isRecovering = false;
            this.criticalFailure = null;

            logger.info("Started Pulsar Producer - {}", configuredResourceId);
        } catch (PulsarClientException e) {
            logger.error("Failed to start Pulsar Producer - {}", configuredResourceId, e);
            this.criticalFailure = "Failed to start Pulsar Producer: " + e.getMessage();
            throw new RuntimeException("Failed to start Pulsar Producer", e);
        }
    }

    @Override
    public void stopManagedResource() {
        logger.info("Stopping Pulsar Producer - {}", configuredResourceId);

        if (this.producer != null) {
            this.producer.closeAsync();
            this.producer = null;
        }

        if (this.pulsarClient != null) {
            this.pulsarClient.closeAsync();
            this.pulsarClient = null;
        }

        logger.info("Stopped Pulsar Producer - {}", configuredResourceId);
    }

    @Override
    public void setManagedResourceRecoveryManager(ManagedResourceRecoveryManager managedResourceRecoveryManager) {
        // Not implemented for this producer
    }

    @Override
    public boolean isCriticalOnStartup() {
        return true;
    }

    @Override
    public void setCriticalOnStartup(boolean criticalOnStartup) {
        // Not configurable for this producer
    }

    @Override
    public PulsarProducerConfiguration getConfiguration() {
        return this.configuration;
    }

    @Override
    public void setConfiguration(PulsarProducerConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public String getConfiguredResourceId() {
        return this.configuredResourceId;
    }

    @Override
    public void setConfiguredResourceId(String id) {
        this.configuredResourceId = id;
    }

    /**
     * Get the Pulsar producer instance.
     *
     * @return the Pulsar producer
     */
    public org.apache.pulsar.client.api.Producer<byte[]> getProducer() {
        return this.producer;
    }

    /**
     * Check if the producer is currently recovering.
     *
     * @return true if recovering, false otherwise
     */
    public boolean isRecovering() {
        return this.isRecovering;
    }

    /**
     * Get the critical failure message if any.
     *
     * @return the critical failure message or null
     */
    public String getCriticalFailure() {
        return this.criticalFailure;
    }
}
