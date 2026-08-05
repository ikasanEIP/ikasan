package org.ikasan.builder.component.endpoint;

import jakarta.transaction.TransactionManager;
import org.ikasan.component.endpoint.pulsar.producer.PulsarProducerLRCO;
import org.ikasan.component.endpoint.pulsar.producer.configuration.PulsarProducerConfiguration;
import org.ikasan.spec.component.endpoint.Producer;

/**
 * Implementation of PulsarProducerLRCOBuilder.
 *
 * @author Ikasan Development Team
 */
public class PulsarProducerLRCOBuilderImpl implements PulsarProducerLRCOBuilder {

    private TransactionManager transactionManager;
    private PulsarProducerConfiguration configuration;
    private String configurationId;

    /**
     * Constructor
     *
     * @param transactionManager
     */
    public PulsarProducerLRCOBuilderImpl(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
        if (this.transactionManager == null) {
            throw new IllegalArgumentException("transactionManager cannot be null!");
        }
        this.configuration = new PulsarProducerConfiguration();
    }

    @Override
    public PulsarProducerLRCOBuilder setConfiguration(PulsarProducerConfiguration configuration) {
        this.configuration = configuration;
        return this;
    }

    @Override
    public PulsarProducerLRCOBuilder setServiceUrl(String serviceUrl) {
        this.configuration.setServiceUrl(serviceUrl);
        return this;
    }

    @Override
    public PulsarProducerLRCOBuilder setTopic(String topic) {
        this.configuration.setTopic(topic);
        return this;
    }

    @Override
    public PulsarProducerLRCOBuilder setProducerName(String producerName) {
        this.configuration.setProducerName(producerName);
        return this;
    }

    @Override
    public PulsarProducerLRCOBuilder setAuthenticationEnabled(boolean authenticationEnabled) {
        this.configuration.setAuthenticationEnabled(authenticationEnabled);
        return this;
    }

    @Override
    public PulsarProducerLRCOBuilder setAuthPluginClassName(String authPluginClassName) {
        this.configuration.setAuthPluginClassName(authPluginClassName);
        return this;
    }

    @Override
    public PulsarProducerLRCOBuilder setAuthParams(String authParams) {
        this.configuration.setAuthParams(authParams);
        return this;
    }

    @Override
    public PulsarProducerLRCOBuilder setTlsEnabled(boolean tlsEnabled) {
        this.configuration.setTlsEnabled(tlsEnabled);
        return this;
    }

    @Override
    public PulsarProducerLRCOBuilder setTlsTrustCertsFilePath(String tlsTrustCertsFilePath) {
        this.configuration.setTlsTrustCertsFilePath(tlsTrustCertsFilePath);
        return this;
    }

    @Override
    public PulsarProducerLRCOBuilder setBatchingEnabled(boolean batchingEnabled) {
        this.configuration.setBatchingEnabled(batchingEnabled);
        return this;
    }

    @Override
    public PulsarProducerLRCOBuilder setCompressionType(String compressionType) {
        this.configuration.setCompressionType(compressionType);
        return this;
    }

    @Override
    public PulsarProducerLRCOBuilder setConfigurationId(String configurationId) {
        this.configurationId = configurationId;
        return this;
    }

    @Override
    public PulsarProducerLRCOBuilder setSchemaType(String schemaType) {
        this.configuration.setSchemaType(schemaType);
        return this;
    }

    @Override
    public PulsarProducerLRCOBuilder setMessageClassName(String messageClassName) {
        this.configuration.setSchemaMessageClassName(messageClassName);
        return this;
    }

    @Override
    public Producer build() {
        PulsarProducerLRCO producer = new PulsarProducerLRCO(transactionManager, configuration);

        if (configurationId != null) {
            producer.setConfiguredResourceId(configurationId);
        }

        return producer;
    }
}
