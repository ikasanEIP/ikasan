package org.ikasan.component.endpoint.pulsar.consumer.configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration for Apache Pulsar Consumer
 *
 * @author Ikasan Development Team
 */
public class PulsarConsumerConfiguration {

    /** Pulsar service URL (e.g., pulsar://localhost:6650) */
    private String serviceUrl = "pulsar://localhost:6650";

    /** Topics to subscribe to */
    private String[] topics;

    /** Subscription name */
    private String subscriptionName;

    /** Subscription type: Exclusive, Shared, Failover, Key_Shared */
    private String subscriptionType = "Exclusive";

    /** Consumer name */
    private String consumerName;

    /** Number of threads for message listener executor */
    private int messageListenerThreads = 1;

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

    /** Acknowledge timeout in milliseconds */
    private long ackTimeoutMillis = 0;

    /** Enable batch index acknowledgment */
    private boolean batchIndexAckEnabled = false;

    /** Additional consumer properties */
    private Map<String, Object> consumerProperties = new HashMap<>();

    /** Enable auto-acknowledgment */
    private boolean autoAcknowledge = false;

    /** Receiver queue size */
    private int receiverQueueSize = 1000;

    /** Delay in how long it takes for a nacked message to be redelivered */
    private long negativeAckRedeliveryDelay = 1000;

    /** Priority level for the shared subscription consumer */
    private int priorityLevel = 0;

    /** Max total receiver queue size across partitions */
    private int maxTotalReceiverQueueSizeAcrossPartitions = 50000;

    /** Read compacted topics */
    private boolean readCompacted = false;

    /** Pattern for auto discovery of topics */
    private String topicsPattern;

    /** Subscription initial position: Latest, Earliest */
    private String subscriptionInitialPosition = "Latest";

    /** RegEx pattern for subscription */
    private boolean patternAutoDiscoveryPeriod = false;

    /** Auto discovery period in minutes */
    private int autoDiscoveryPeriodMinutes = 1;

    /** Crypto key reader class name */
    private String cryptoKeyReaderClassName;

    /** Enable retry on failure */
    private boolean retryEnable = false;

    /** Dead letter topic */
    private String deadLetterTopic;

    /** Max redelivery count */
    private int maxRedeliverCount = 0;

    /** Start message ID inclusive */
    private boolean startMessageIdInclusive = false;

    /** Enable batch receive */
    private boolean batchReceiveEnabled = false;

    /** Ack receipt enabled */
    private boolean ackReceiptEnabled = false;

    /** Pool messages enabled */
    private boolean poolMessages = true;

    /** Replicate subscription state */
    private boolean replicateSubscriptionState = false;

    /** Ack timeout tick time in milliseconds */
    private long ackTimeoutTickTimeMillis = 1000;

    /** Auto ack oldest chunked message on queue full */
    private boolean autoAckOldestChunkedMessageOnQueueFull = false;

    /** Max pending chunked messages */
    private int maxPendingChunkedMessage = 10;

    /** Expire time of incomplete chunked messages in milliseconds */
    private long expireTimeOfIncompleteChunkedMessageMillis = 60000;

    /** Consumer event listener */
    private String consumerEventListener;

    /** Properties for the consumer */
    private String properties;

    /** Subscription properties */
    private String subscriptionProperties;

    /** Acknowledgement group time in milliseconds */
    private long acknowledgementGroupTimeMillis = 100;

    /** Enable auto scale receiver queue size */
    private boolean autoScaleReceiverQueueSizeEnabled = false;

    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public String[] getTopics() {
        return topics;
    }

    public void setTopics(String[] topics) {
        this.topics = topics;
    }

    public String getSubscriptionName() {
        return subscriptionName;
    }

    public void setSubscriptionName(String subscriptionName) {
        this.subscriptionName = subscriptionName;
    }

    public String getSubscriptionType() {
        return subscriptionType;
    }

    public void setSubscriptionType(String subscriptionType) {
        this.subscriptionType = subscriptionType;
    }

    public String getConsumerName() {
        return consumerName;
    }

    public void setConsumerName(String consumerName) {
        this.consumerName = consumerName;
    }

    public int getMessageListenerThreads() {
        return messageListenerThreads;
    }

    public void setMessageListenerThreads(int messageListenerThreads) {
        this.messageListenerThreads = messageListenerThreads;
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

    public long getAckTimeoutMillis() {
        return ackTimeoutMillis;
    }

    public void setAckTimeoutMillis(long ackTimeoutMillis) {
        this.ackTimeoutMillis = ackTimeoutMillis;
    }

    public boolean isBatchIndexAckEnabled() {
        return batchIndexAckEnabled;
    }

    public void setBatchIndexAckEnabled(boolean batchIndexAckEnabled) {
        this.batchIndexAckEnabled = batchIndexAckEnabled;
    }

    public Map<String, Object> getConsumerProperties() {
        return consumerProperties;
    }

    public void setConsumerProperties(Map<String, Object> consumerProperties) {
        this.consumerProperties = consumerProperties;
    }

    public boolean isAutoAcknowledge() {
        return autoAcknowledge;
    }

    public void setAutoAcknowledge(boolean autoAcknowledge) {
        this.autoAcknowledge = autoAcknowledge;
    }

    public int getReceiverQueueSize() {
        return receiverQueueSize;
    }

    public void setReceiverQueueSize(int receiverQueueSize) {
        this.receiverQueueSize = receiverQueueSize;
    }

    public long getNegativeAckRedeliveryDelay() {
        return negativeAckRedeliveryDelay;
    }

    public void setNegativeAckRedeliveryDelay(long negativeAckRedeliveryDelay) {
        this.negativeAckRedeliveryDelay = negativeAckRedeliveryDelay;
    }

    public int getPriorityLevel() {
        return priorityLevel;
    }

    public void setPriorityLevel(int priorityLevel) {
        this.priorityLevel = priorityLevel;
    }

    public int getMaxTotalReceiverQueueSizeAcrossPartitions() {
        return maxTotalReceiverQueueSizeAcrossPartitions;
    }

    public void setMaxTotalReceiverQueueSizeAcrossPartitions(int maxTotalReceiverQueueSizeAcrossPartitions) {
        this.maxTotalReceiverQueueSizeAcrossPartitions = maxTotalReceiverQueueSizeAcrossPartitions;
    }

    public boolean isReadCompacted() {
        return readCompacted;
    }

    public void setReadCompacted(boolean readCompacted) {
        this.readCompacted = readCompacted;
    }

    public String getTopicsPattern() {
        return topicsPattern;
    }

    public void setTopicsPattern(String topicsPattern) {
        this.topicsPattern = topicsPattern;
    }

    public String getSubscriptionInitialPosition() {
        return subscriptionInitialPosition;
    }

    public void setSubscriptionInitialPosition(String subscriptionInitialPosition) {
        this.subscriptionInitialPosition = subscriptionInitialPosition;
    }

    public boolean isPatternAutoDiscoveryPeriod() {
        return patternAutoDiscoveryPeriod;
    }

    public void setPatternAutoDiscoveryPeriod(boolean patternAutoDiscoveryPeriod) {
        this.patternAutoDiscoveryPeriod = patternAutoDiscoveryPeriod;
    }

    public int getAutoDiscoveryPeriodMinutes() {
        return autoDiscoveryPeriodMinutes;
    }

    public void setAutoDiscoveryPeriodMinutes(int autoDiscoveryPeriodMinutes) {
        this.autoDiscoveryPeriodMinutes = autoDiscoveryPeriodMinutes;
    }

    public String getCryptoKeyReaderClassName() {
        return cryptoKeyReaderClassName;
    }

    public void setCryptoKeyReaderClassName(String cryptoKeyReaderClassName) {
        this.cryptoKeyReaderClassName = cryptoKeyReaderClassName;
    }

    public boolean isRetryEnable() {
        return retryEnable;
    }

    public void setRetryEnable(boolean retryEnable) {
        this.retryEnable = retryEnable;
    }

    public String getDeadLetterTopic() {
        return deadLetterTopic;
    }

    public void setDeadLetterTopic(String deadLetterTopic) {
        this.deadLetterTopic = deadLetterTopic;
    }

    public int getMaxRedeliverCount() {
        return maxRedeliverCount;
    }

    public void setMaxRedeliverCount(int maxRedeliverCount) {
        this.maxRedeliverCount = maxRedeliverCount;
    }

    public boolean isStartMessageIdInclusive() {
        return startMessageIdInclusive;
    }

    public void setStartMessageIdInclusive(boolean startMessageIdInclusive) {
        this.startMessageIdInclusive = startMessageIdInclusive;
    }

    public boolean isBatchReceiveEnabled() {
        return batchReceiveEnabled;
    }

    public void setBatchReceiveEnabled(boolean batchReceiveEnabled) {
        this.batchReceiveEnabled = batchReceiveEnabled;
    }

    public boolean isAckReceiptEnabled() {
        return ackReceiptEnabled;
    }

    public void setAckReceiptEnabled(boolean ackReceiptEnabled) {
        this.ackReceiptEnabled = ackReceiptEnabled;
    }

    public boolean isPoolMessages() {
        return poolMessages;
    }

    public void setPoolMessages(boolean poolMessages) {
        this.poolMessages = poolMessages;
    }

    public boolean isReplicateSubscriptionState() {
        return replicateSubscriptionState;
    }

    public void setReplicateSubscriptionState(boolean replicateSubscriptionState) {
        this.replicateSubscriptionState = replicateSubscriptionState;
    }

    public long getAckTimeoutTickTimeMillis() {
        return ackTimeoutTickTimeMillis;
    }

    public void setAckTimeoutTickTimeMillis(long ackTimeoutTickTimeMillis) {
        this.ackTimeoutTickTimeMillis = ackTimeoutTickTimeMillis;
    }

    public boolean isAutoAckOldestChunkedMessageOnQueueFull() {
        return autoAckOldestChunkedMessageOnQueueFull;
    }

    public void setAutoAckOldestChunkedMessageOnQueueFull(boolean autoAckOldestChunkedMessageOnQueueFull) {
        this.autoAckOldestChunkedMessageOnQueueFull = autoAckOldestChunkedMessageOnQueueFull;
    }

    public int getMaxPendingChunkedMessage() {
        return maxPendingChunkedMessage;
    }

    public void setMaxPendingChunkedMessage(int maxPendingChunkedMessage) {
        this.maxPendingChunkedMessage = maxPendingChunkedMessage;
    }

    public long getExpireTimeOfIncompleteChunkedMessageMillis() {
        return expireTimeOfIncompleteChunkedMessageMillis;
    }

    public void setExpireTimeOfIncompleteChunkedMessageMillis(long expireTimeOfIncompleteChunkedMessageMillis) {
        this.expireTimeOfIncompleteChunkedMessageMillis = expireTimeOfIncompleteChunkedMessageMillis;
    }

    public String getConsumerEventListener() {
        return consumerEventListener;
    }

    public void setConsumerEventListener(String consumerEventListener) {
        this.consumerEventListener = consumerEventListener;
    }

    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }

    public String getSubscriptionProperties() {
        return subscriptionProperties;
    }

    public void setSubscriptionProperties(String subscriptionProperties) {
        this.subscriptionProperties = subscriptionProperties;
    }

    public long getAcknowledgementGroupTimeMillis() {
        return acknowledgementGroupTimeMillis;
    }

    public void setAcknowledgementGroupTimeMillis(long acknowledgementGroupTimeMillis) {
        this.acknowledgementGroupTimeMillis = acknowledgementGroupTimeMillis;
    }

    public boolean isAutoScaleReceiverQueueSizeEnabled() {
        return autoScaleReceiverQueueSizeEnabled;
    }

    public void setAutoScaleReceiverQueueSizeEnabled(boolean autoScaleReceiverQueueSizeEnabled) {
        this.autoScaleReceiverQueueSizeEnabled = autoScaleReceiverQueueSizeEnabled;
    }
}
