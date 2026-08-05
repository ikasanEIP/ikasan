package org.ikasan.builder.component.endpoint;

import org.ikasan.builder.component.Builder;
import org.ikasan.component.endpoint.pulsar.consumer.configuration.PulsarConsumerConfiguration;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.event.EventFactory;
import org.ikasan.spec.event.EventListener;
import org.ikasan.spec.event.ManagedRelatedEventIdentifierService;
import org.ikasan.spec.resubmission.ResubmissionEventFactory;

/**
 * Contract for a Pulsar consumer builder.
 *
 * @author Ikasan Development Team.
 */
public interface PulsarConsumerBuilder extends Builder<Consumer>
{
    /**
     * Set the Pulsar consumer configuration.
     *
     * @param configuration
     * @return this builder
     */
    PulsarConsumerBuilder setConfiguration(PulsarConsumerConfiguration configuration);

    /**
     * Set the Pulsar service URL (e.g., pulsar://localhost:6650).
     *
     * @param serviceUrl
     * @return this builder
     */
    PulsarConsumerBuilder setServiceUrl(String serviceUrl);

    /**
     * Set the topics to subscribe to.
     *
     * @param topics
     * @return this builder
     */
    PulsarConsumerBuilder setTopics(String... topics);

    /**
     * Set the subscription name.
     *
     * @param subscriptionName
     * @return this builder
     */
    PulsarConsumerBuilder setSubscriptionName(String subscriptionName);

    /**
     * Set the subscription type (Exclusive, Shared, Failover, Key_Shared).
     *
     * @param subscriptionType
     * @return this builder
     */
    PulsarConsumerBuilder setSubscriptionType(String subscriptionType);

    /**
     * Set the consumer name.
     *
     * @param consumerName
     * @return this builder
     */
    PulsarConsumerBuilder setConsumerName(String consumerName);

    /**
     * Set authentication enabled.
     *
     * @param authenticationEnabled
     * @return this builder
     */
    PulsarConsumerBuilder setAuthenticationEnabled(boolean authenticationEnabled);

    /**
     * Set the authentication plugin class name.
     *
     * @param authPluginClassName
     * @return this builder
     */
    PulsarConsumerBuilder setAuthPluginClassName(String authPluginClassName);

    /**
     * Set the authentication parameters.
     *
     * @param authParams
     * @return this builder
     */
    PulsarConsumerBuilder setAuthParams(String authParams);

    /**
     * Set TLS enabled.
     *
     * @param tlsEnabled
     * @return this builder
     */
    PulsarConsumerBuilder setTlsEnabled(boolean tlsEnabled);

    /**
     * Set the TLS trust certificates file path.
     *
     * @param tlsTrustCertsFilePath
     * @return this builder
     */
    PulsarConsumerBuilder setTlsTrustCertsFilePath(String tlsTrustCertsFilePath);

    /**
     * Set the managed event identifier service.
     *
     * @param managedEventIdentifierService
     * @return this builder
     */
    PulsarConsumerBuilder setManagedEventIdentifierService(ManagedRelatedEventIdentifierService managedEventIdentifierService);

    /**
     * Set the event listener on the consumer.
     *
     * @param eventListener
     * @return this builder
     */
    PulsarConsumerBuilder setListener(EventListener<?> eventListener);

    /**
     * Set the event factory on the consumer.
     *
     * @param eventFactory
     * @return this builder
     */
    PulsarConsumerBuilder setEventFactory(EventFactory eventFactory);

    /**
     * Set the managed identifier service on the consumer.
     *
     * @param managedRelatedEventIdentifierService
     * @return this builder
     */
    PulsarConsumerBuilder setManagedIdentifierService(ManagedRelatedEventIdentifierService managedRelatedEventIdentifierService);

    /**
     * Set the resubmission event factory on the consumer.
     *
     * @param resubmissionEventFactory
     * @return this builder
     */
    PulsarConsumerBuilder setResubmissionEventFactory(ResubmissionEventFactory resubmissionEventFactory);

    /**
     * Set the configuration id on the consumer.
     *
     * @param configurationId
     * @return this builder
     */
    PulsarConsumerBuilder setConfigurationId(String configurationId);

    /**
     * Set the schema type (BYTES, STRING, JSON, AVRO, PROTOBUF, etc.).
     *
     * @param schemaType
     * @return this builder
     */
    PulsarConsumerBuilder setSchemaType(String schemaType);

    /**
     * Set the message class name for JSON, AVRO, or PROTOBUF schemas.
     *
     * @param messageClassName
     * @return this builder
     */
    PulsarConsumerBuilder setMessageClassName(String messageClassName);

    /**
     * Set the AVRO schema definition string.
     *
     * @param avroDefinition
     * @return this builder
     */
    PulsarConsumerBuilder setSchemaAvroDefinition(String avroDefinition);

    /**
     * Set the schema type for keys in KEY_VALUE schemas.
     *
     * @param keyType
     * @return this builder
     */
    PulsarConsumerBuilder setSchemaKeyType(String keyType);

    /**
     * Set the schema type for values in KEY_VALUE schemas.
     *
     * @param valueType
     * @return this builder
     */
    PulsarConsumerBuilder setSchemaValueType(String valueType);

    /**
     * Set the key class name for KEY_VALUE schemas.
     *
     * @param keyClassName
     * @return this builder
     */
    PulsarConsumerBuilder setSchemaKeyClassName(String keyClassName);

    /**
     * Set the value class name for KEY_VALUE schemas.
     *
     * @param valueClassName
     * @return this builder
     */
    PulsarConsumerBuilder setSchemaValueClassName(String valueClassName);

    /**
     * Set the encoding type for KEY_VALUE schemas (INLINE or SEPARATED).
     *
     * @param encodingType
     * @return this builder
     */
    PulsarConsumerBuilder setSchemaKeyValueEncodingType(String encodingType);

    /**
     * Set additional schema properties.
     *
     * @param schemaProperties
     * @return this builder
     */
    PulsarConsumerBuilder setSchemaProperties(java.util.Map<String, String> schemaProperties);
}
