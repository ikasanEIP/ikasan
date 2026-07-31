package org.ikasan.builder.component.endpoint;

import org.ikasan.builder.component.Builder;
import org.ikasan.component.endpoint.pulsar.producer.configuration.PulsarProducerConfiguration;
import org.ikasan.spec.component.endpoint.Producer;

/**
 * Contract for a Pulsar producer LRCO builder.
 *
 * @author Ikasan Development Team.
 */
public interface PulsarProducerLRCOBuilder extends Builder<Producer>
{
    /**
     * Set the Pulsar producer configuration.
     *
     * @param configuration
     * @return this builder
     */
    PulsarProducerLRCOBuilder setConfiguration(PulsarProducerConfiguration configuration);

    /**
     * Set the Pulsar service URL (e.g., pulsar://localhost:6650).
     *
     * @param serviceUrl
     * @return this builder
     */
    PulsarProducerLRCOBuilder setServiceUrl(String serviceUrl);

    /**
     * Set the topic to publish to.
     *
     * @param topic
     * @return this builder
     */
    PulsarProducerLRCOBuilder setTopic(String topic);

    /**
     * Set the producer name.
     *
     * @param producerName
     * @return this builder
     */
    PulsarProducerLRCOBuilder setProducerName(String producerName);

    /**
     * Set authentication enabled.
     *
     * @param authenticationEnabled
     * @return this builder
     */
    PulsarProducerLRCOBuilder setAuthenticationEnabled(boolean authenticationEnabled);

    /**
     * Set the authentication plugin class name.
     *
     * @param authPluginClassName
     * @return this builder
     */
    PulsarProducerLRCOBuilder setAuthPluginClassName(String authPluginClassName);

    /**
     * Set the authentication parameters.
     *
     * @param authParams
     * @return this builder
     */
    PulsarProducerLRCOBuilder setAuthParams(String authParams);

    /**
     * Set TLS enabled.
     *
     * @param tlsEnabled
     * @return this builder
     */
    PulsarProducerLRCOBuilder setTlsEnabled(boolean tlsEnabled);

    /**
     * Set the TLS trust certificates file path.
     *
     * @param tlsTrustCertsFilePath
     * @return this builder
     */
    PulsarProducerLRCOBuilder setTlsTrustCertsFilePath(String tlsTrustCertsFilePath);

    /**
     * Set batching enabled.
     *
     * @param batchingEnabled
     * @return this builder
     */
    PulsarProducerLRCOBuilder setBatchingEnabled(boolean batchingEnabled);

    /**
     * Set the compression type (NONE, LZ4, ZLIB, ZSTD, SNAPPY).
     *
     * @param compressionType
     * @return this builder
     */
    PulsarProducerLRCOBuilder setCompressionType(String compressionType);

    /**
     * Set the configuration id on the producer.
     *
     * @param configurationId
     * @return this builder
     */
    PulsarProducerLRCOBuilder setConfigurationId(String configurationId);
}
