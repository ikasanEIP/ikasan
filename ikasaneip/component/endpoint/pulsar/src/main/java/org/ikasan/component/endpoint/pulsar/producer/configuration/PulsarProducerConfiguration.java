package org.ikasan.component.endpoint.pulsar.producer.configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration for Apache Pulsar Producer
 *
 * @author Ikasan Development Team
 */
public class PulsarProducerConfiguration {

    /** Pulsar service URL (e.g., pulsar://localhost:6650) */
    private String serviceUrl = "pulsar://localhost:6650";

    /** Topic to publish to */
    private String topic;

    /** Producer name */
    private String producerName;

    /** Enable batching */
    private boolean batchingEnabled = true;

    /** Maximum number of messages permitted in a batch */
    private int batchingMaxMessages = 1000;

    /** Maximum delay for batching in milliseconds */
    private long batchingMaxPublishDelayMillis = 10;

    /** Maximum batch size in bytes */
    private int batchingMaxBytes = 128 * 1024;

    /** Compression type: NONE, LZ4, ZLIB, ZSTD, SNAPPY */
    private String compressionType = "NONE";

    /** Send timeout in milliseconds */
    private int sendTimeoutMillis = 30000;

    /** Enable block if queue full */
    private boolean blockIfQueueFull = false;

    /** Maximum pending messages */
    private int maxPendingMessages = 1000;

    /** Enable authentication */
    private boolean authenticationEnabled = false;

    /** Authentication plugin class name */
    private String authPluginClassName;

    /** Authentication parameters */
    private String authParams;

    /** Enable TLS */
    private boolean tlsEnabled = false;

    /** TLS trust certificate file path */
    private String tlsTrustCertsFilePath;

    /** Message routing mode: SinglePartition, RoundRobinPartition, CustomPartition */
    private String messageRoutingMode = "RoundRobinPartition";

    /** Hashing scheme: JavaStringHash, Murmur3_32Hash */
    private String hashingScheme = "JavaStringHash";

    /** Enable chunking for large messages */
    private boolean chunkingEnabled = false;

    /** Additional producer properties */
    private Map<String, Object> producerProperties = new HashMap<>();

    /** Auto update partitions */
    private boolean autoUpdatePartitions = true;

    /** Auto update partitions interval in seconds */
    private int autoUpdatePartitionsIntervalSeconds = 60;

    /** Maximum pending messages across partitions */
    private int maxPendingMessagesAcrossPartitions = 50000;

    /** Enable multi-schema */
    private boolean multiSchema = true;

    /** Access mode: Shared, Exclusive, WaitForExclusive */
    private String accessMode = "Shared";

    /** Enable lazy start of partitioned producers */
    private boolean lazyStartPartitionedProducers = false;

    /** Initial sequence ID */
    private Long initialSequenceId;

    /** Round robin router batching partition switch frequency */
    private int roundRobinRouterBatchingPartitionSwitchFrequency = 10;

    // ========================================
    // Schema Configuration Properties
    // ========================================

    /**
     * Schema type - supported values:
     * BYTES, STRING, JSON, AVRO, PROTOBUF, PROTOBUF_NATIVE, KEY_VALUE, AUTO_CONSUME, AUTO_PRODUCE_BYTES,
     * INT8, INT16, INT32, INT64, BOOL, FLOAT, DOUBLE,
     * DATE, TIME, TIMESTAMP, INSTANT, LOCAL_DATE, LOCAL_TIME, LOCAL_DATE_TIME
     */
    private String schemaType = "BYTES";

    /**
     * For JSON, AVRO, and PROTOBUF schemas - fully qualified class name of the message type
     * Example: "com.example.MyMessage"
     */
    private String schemaMessageClassName;

    /**
     * For AVRO schemas - AVRO schema definition string
     */
    private String schemaAvroDefinition;

    /**
     * For KEY_VALUE schemas - schema type for keys
     */
    private String schemaKeyType;

    /**
     * For KEY_VALUE schemas - schema type for values
     */
    private String schemaValueType;

    /**
     * For KEY_VALUE schemas - key class name
     */
    private String schemaKeyClassName;

    /**
     * For KEY_VALUE schemas - value class name
     */
    private String schemaValueClassName;

    /**
     * For KEY_VALUE schemas - encoding type: INLINE or SEPARATED
     */
    private String schemaKeyValueEncodingType = "INLINE";

    /**
     * Schema properties - additional schema metadata as Map
     */
    private Map<String, String> schemaProperties = new HashMap<>();

    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getProducerName() {
        return producerName;
    }

    public void setProducerName(String producerName) {
        this.producerName = producerName;
    }

    public boolean isBatchingEnabled() {
        return batchingEnabled;
    }

    public void setBatchingEnabled(boolean batchingEnabled) {
        this.batchingEnabled = batchingEnabled;
    }

    public int getBatchingMaxMessages() {
        return batchingMaxMessages;
    }

    public void setBatchingMaxMessages(int batchingMaxMessages) {
        this.batchingMaxMessages = batchingMaxMessages;
    }

    public long getBatchingMaxPublishDelayMillis() {
        return batchingMaxPublishDelayMillis;
    }

    public void setBatchingMaxPublishDelayMillis(long batchingMaxPublishDelayMillis) {
        this.batchingMaxPublishDelayMillis = batchingMaxPublishDelayMillis;
    }

    public int getBatchingMaxBytes() {
        return batchingMaxBytes;
    }

    public void setBatchingMaxBytes(int batchingMaxBytes) {
        this.batchingMaxBytes = batchingMaxBytes;
    }

    public String getCompressionType() {
        return compressionType;
    }

    public void setCompressionType(String compressionType) {
        this.compressionType = compressionType;
    }

    public int getSendTimeoutMillis() {
        return sendTimeoutMillis;
    }

    public void setSendTimeoutMillis(int sendTimeoutMillis) {
        this.sendTimeoutMillis = sendTimeoutMillis;
    }

    public boolean isBlockIfQueueFull() {
        return blockIfQueueFull;
    }

    public void setBlockIfQueueFull(boolean blockIfQueueFull) {
        this.blockIfQueueFull = blockIfQueueFull;
    }

    public int getMaxPendingMessages() {
        return maxPendingMessages;
    }

    public void setMaxPendingMessages(int maxPendingMessages) {
        this.maxPendingMessages = maxPendingMessages;
    }

    public boolean isAuthenticationEnabled() {
        return authenticationEnabled;
    }

    public void setAuthenticationEnabled(boolean authenticationEnabled) {
        this.authenticationEnabled = authenticationEnabled;
    }

    public String getAuthPluginClassName() {
        return authPluginClassName;
    }

    public void setAuthPluginClassName(String authPluginClassName) {
        this.authPluginClassName = authPluginClassName;
    }

    public String getAuthParams() {
        return authParams;
    }

    public void setAuthParams(String authParams) {
        this.authParams = authParams;
    }

    public boolean isTlsEnabled() {
        return tlsEnabled;
    }

    public void setTlsEnabled(boolean tlsEnabled) {
        this.tlsEnabled = tlsEnabled;
    }

    public String getTlsTrustCertsFilePath() {
        return tlsTrustCertsFilePath;
    }

    public void setTlsTrustCertsFilePath(String tlsTrustCertsFilePath) {
        this.tlsTrustCertsFilePath = tlsTrustCertsFilePath;
    }

    public String getMessageRoutingMode() {
        return messageRoutingMode;
    }

    public void setMessageRoutingMode(String messageRoutingMode) {
        this.messageRoutingMode = messageRoutingMode;
    }

    public String getHashingScheme() {
        return hashingScheme;
    }

    public void setHashingScheme(String hashingScheme) {
        this.hashingScheme = hashingScheme;
    }

    public boolean isChunkingEnabled() {
        return chunkingEnabled;
    }

    public void setChunkingEnabled(boolean chunkingEnabled) {
        this.chunkingEnabled = chunkingEnabled;
    }

    public Map<String, Object> getProducerProperties() {
        return producerProperties;
    }

    public void setProducerProperties(Map<String, Object> producerProperties) {
        this.producerProperties = producerProperties;
    }

    public boolean isAutoUpdatePartitions() {
        return autoUpdatePartitions;
    }

    public void setAutoUpdatePartitions(boolean autoUpdatePartitions) {
        this.autoUpdatePartitions = autoUpdatePartitions;
    }

    public int getAutoUpdatePartitionsIntervalSeconds() {
        return autoUpdatePartitionsIntervalSeconds;
    }

    public void setAutoUpdatePartitionsIntervalSeconds(int autoUpdatePartitionsIntervalSeconds) {
        this.autoUpdatePartitionsIntervalSeconds = autoUpdatePartitionsIntervalSeconds;
    }

    public int getMaxPendingMessagesAcrossPartitions() {
        return maxPendingMessagesAcrossPartitions;
    }

    public void setMaxPendingMessagesAcrossPartitions(int maxPendingMessagesAcrossPartitions) {
        this.maxPendingMessagesAcrossPartitions = maxPendingMessagesAcrossPartitions;
    }

    public boolean isMultiSchema() {
        return multiSchema;
    }

    public void setMultiSchema(boolean multiSchema) {
        this.multiSchema = multiSchema;
    }

    public String getAccessMode() {
        return accessMode;
    }

    public void setAccessMode(String accessMode) {
        this.accessMode = accessMode;
    }

    public boolean isLazyStartPartitionedProducers() {
        return lazyStartPartitionedProducers;
    }

    public void setLazyStartPartitionedProducers(boolean lazyStartPartitionedProducers) {
        this.lazyStartPartitionedProducers = lazyStartPartitionedProducers;
    }

    public Long getInitialSequenceId() {
        return initialSequenceId;
    }

    public void setInitialSequenceId(Long initialSequenceId) {
        this.initialSequenceId = initialSequenceId;
    }

    public int getRoundRobinRouterBatchingPartitionSwitchFrequency() {
        return roundRobinRouterBatchingPartitionSwitchFrequency;
    }

    public void setRoundRobinRouterBatchingPartitionSwitchFrequency(int roundRobinRouterBatchingPartitionSwitchFrequency) {
        this.roundRobinRouterBatchingPartitionSwitchFrequency = roundRobinRouterBatchingPartitionSwitchFrequency;
    }

    // Schema configuration getters and setters

    public String getSchemaType() {
        return schemaType;
    }

    public void setSchemaType(String schemaType) {
        this.schemaType = schemaType;
    }

    public String getSchemaMessageClassName() {
        return schemaMessageClassName;
    }

    public void setSchemaMessageClassName(String schemaMessageClassName) {
        this.schemaMessageClassName = schemaMessageClassName;
    }

    public String getSchemaAvroDefinition() {
        return schemaAvroDefinition;
    }

    public void setSchemaAvroDefinition(String schemaAvroDefinition) {
        this.schemaAvroDefinition = schemaAvroDefinition;
    }

    public String getSchemaKeyType() {
        return schemaKeyType;
    }

    public void setSchemaKeyType(String schemaKeyType) {
        this.schemaKeyType = schemaKeyType;
    }

    public String getSchemaValueType() {
        return schemaValueType;
    }

    public void setSchemaValueType(String schemaValueType) {
        this.schemaValueType = schemaValueType;
    }

    public String getSchemaKeyClassName() {
        return schemaKeyClassName;
    }

    public void setSchemaKeyClassName(String schemaKeyClassName) {
        this.schemaKeyClassName = schemaKeyClassName;
    }

    public String getSchemaValueClassName() {
        return schemaValueClassName;
    }

    public void setSchemaValueClassName(String schemaValueClassName) {
        this.schemaValueClassName = schemaValueClassName;
    }

    public String getSchemaKeyValueEncodingType() {
        return schemaKeyValueEncodingType;
    }

    public void setSchemaKeyValueEncodingType(String schemaKeyValueEncodingType) {
        this.schemaKeyValueEncodingType = schemaKeyValueEncodingType;
    }

    public Map<String, String> getSchemaProperties() {
        return schemaProperties;
    }

    public void setSchemaProperties(Map<String, String> schemaProperties) {
        this.schemaProperties = schemaProperties;
    }
}
