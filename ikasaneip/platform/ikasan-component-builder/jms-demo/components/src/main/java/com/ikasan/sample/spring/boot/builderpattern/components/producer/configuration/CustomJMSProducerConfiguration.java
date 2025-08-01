package com.ikasan.sample.spring.boot.builderpattern.components.producer.configuration;

    import java.util.HashMap;
    import java.lang.String;
    import java.lang.String;
    import java.lang.String;
    import java.lang.Integer;
    import java.lang.Boolean;
    import java.lang.String;
    import java.util.HashMap;
    import java.lang.Boolean;
    import java.lang.Boolean;
    import java.lang.Boolean;
    import java.lang.Integer;
    import java.lang.Boolean;
    import java.lang.Boolean;
    import java.lang.Long;
    import java.lang.Integer;
    import java.lang.String;
    import java.lang.Boolean;
    import java.lang.Long;

public class CustomJMSProducerConfiguration {
    private HashMap<String, String> connectionFactoryJndiProperties;
    private String connectionFactoryName;
    private String connectionFactoryPassword;
    private String connectionFactoryUsername;
    private Integer deliveryMode;
    private Boolean deliveryPersistent;
    private String destinationJndiName;
    private HashMap<String, String> destinationJndiProperties;
    private Boolean explicitQosEnabled;
    private Boolean messageIdEnabled;
    private Boolean messageTimestampEnabled;
    private Integer priority;
    private Boolean pubSubDomain;
    private Boolean pubSubNoLocal;
    private Long receiveTimeout;
    private Integer sessionAcknowledgeMode;
    private String sessionAcknowledgeModeName;
    private Boolean sessionTransacted;
    private Long timeToLive;

    /**
    * Set the connectionFactoryJndiProperties configuration value.
    *
    * @param connectionFactoryJndiProperties the configuration value to set.
    */
    public HashMap<String, String> getConnectionFactoryJndiProperties() {
        return connectionFactoryJndiProperties;
    }

    /**
    * Get the connectionFactoryJndiProperties configuration value.
    *
    * @return connectionFactoryJndiProperties configuration value.
    */
    public void setConnectionFactoryJndiProperties(HashMap<String, String> connectionFactoryJndiProperties) {
        this.connectionFactoryJndiProperties = connectionFactoryJndiProperties;
    }
    /**
    * Set the connectionFactoryName configuration value.
    *
    * @param connectionFactoryName the configuration value to set.
    */
    public String getConnectionFactoryName() {
        return connectionFactoryName;
    }

    /**
    * Get the connectionFactoryName configuration value.
    *
    * @return connectionFactoryName configuration value.
    */
    public void setConnectionFactoryName(String connectionFactoryName) {
        this.connectionFactoryName = connectionFactoryName;
    }
    /**
    * Set the connectionFactoryPassword configuration value.
    *
    * @param connectionFactoryPassword the configuration value to set.
    */
    public String getConnectionFactoryPassword() {
        return connectionFactoryPassword;
    }

    /**
    * Get the connectionFactoryPassword configuration value.
    *
    * @return connectionFactoryPassword configuration value.
    */
    public void setConnectionFactoryPassword(String connectionFactoryPassword) {
        this.connectionFactoryPassword = connectionFactoryPassword;
    }
    /**
    * Set the connectionFactoryUsername configuration value.
    *
    * @param connectionFactoryUsername the configuration value to set.
    */
    public String getConnectionFactoryUsername() {
        return connectionFactoryUsername;
    }

    /**
    * Get the connectionFactoryUsername configuration value.
    *
    * @return connectionFactoryUsername configuration value.
    */
    public void setConnectionFactoryUsername(String connectionFactoryUsername) {
        this.connectionFactoryUsername = connectionFactoryUsername;
    }
    /**
    * Set the deliveryMode configuration value.
    *
    * @param deliveryMode the configuration value to set.
    */
    public Integer getDeliveryMode() {
        return deliveryMode;
    }

    /**
    * Get the deliveryMode configuration value.
    *
    * @return deliveryMode configuration value.
    */
    public void setDeliveryMode(Integer deliveryMode) {
        this.deliveryMode = deliveryMode;
    }
    /**
    * Set the deliveryPersistent configuration value.
    *
    * @param deliveryPersistent the configuration value to set.
    */
    public Boolean getDeliveryPersistent() {
        return deliveryPersistent;
    }

    /**
    * Get the deliveryPersistent configuration value.
    *
    * @return deliveryPersistent configuration value.
    */
    public void setDeliveryPersistent(Boolean deliveryPersistent) {
        this.deliveryPersistent = deliveryPersistent;
    }
    /**
    * Set the destinationJndiName configuration value.
    *
    * @param destinationJndiName the configuration value to set.
    */
    public String getDestinationJndiName() {
        return destinationJndiName;
    }

    /**
    * Get the destinationJndiName configuration value.
    *
    * @return destinationJndiName configuration value.
    */
    public void setDestinationJndiName(String destinationJndiName) {
        this.destinationJndiName = destinationJndiName;
    }
    /**
    * Set the destinationJndiProperties configuration value.
    *
    * @param destinationJndiProperties the configuration value to set.
    */
    public HashMap<String, String> getDestinationJndiProperties() {
        return destinationJndiProperties;
    }

    /**
    * Get the destinationJndiProperties configuration value.
    *
    * @return destinationJndiProperties configuration value.
    */
    public void setDestinationJndiProperties(HashMap<String, String> destinationJndiProperties) {
        this.destinationJndiProperties = destinationJndiProperties;
    }
    /**
    * Set the explicitQosEnabled configuration value.
    *
    * @param explicitQosEnabled the configuration value to set.
    */
    public Boolean getExplicitQosEnabled() {
        return explicitQosEnabled;
    }

    /**
    * Get the explicitQosEnabled configuration value.
    *
    * @return explicitQosEnabled configuration value.
    */
    public void setExplicitQosEnabled(Boolean explicitQosEnabled) {
        this.explicitQosEnabled = explicitQosEnabled;
    }
    /**
    * Set the messageIdEnabled configuration value.
    *
    * @param messageIdEnabled the configuration value to set.
    */
    public Boolean getMessageIdEnabled() {
        return messageIdEnabled;
    }

    /**
    * Get the messageIdEnabled configuration value.
    *
    * @return messageIdEnabled configuration value.
    */
    public void setMessageIdEnabled(Boolean messageIdEnabled) {
        this.messageIdEnabled = messageIdEnabled;
    }
    /**
    * Set the messageTimestampEnabled configuration value.
    *
    * @param messageTimestampEnabled the configuration value to set.
    */
    public Boolean getMessageTimestampEnabled() {
        return messageTimestampEnabled;
    }

    /**
    * Get the messageTimestampEnabled configuration value.
    *
    * @return messageTimestampEnabled configuration value.
    */
    public void setMessageTimestampEnabled(Boolean messageTimestampEnabled) {
        this.messageTimestampEnabled = messageTimestampEnabled;
    }
    /**
    * Set the priority configuration value.
    *
    * @param priority the configuration value to set.
    */
    public Integer getPriority() {
        return priority;
    }

    /**
    * Get the priority configuration value.
    *
    * @return priority configuration value.
    */
    public void setPriority(Integer priority) {
        this.priority = priority;
    }
    /**
    * Set the pubSubDomain configuration value.
    *
    * @param pubSubDomain the configuration value to set.
    */
    public Boolean getPubSubDomain() {
        return pubSubDomain;
    }

    /**
    * Get the pubSubDomain configuration value.
    *
    * @return pubSubDomain configuration value.
    */
    public void setPubSubDomain(Boolean pubSubDomain) {
        this.pubSubDomain = pubSubDomain;
    }
    /**
    * Set the pubSubNoLocal configuration value.
    *
    * @param pubSubNoLocal the configuration value to set.
    */
    public Boolean getPubSubNoLocal() {
        return pubSubNoLocal;
    }

    /**
    * Get the pubSubNoLocal configuration value.
    *
    * @return pubSubNoLocal configuration value.
    */
    public void setPubSubNoLocal(Boolean pubSubNoLocal) {
        this.pubSubNoLocal = pubSubNoLocal;
    }
    /**
    * Set the receiveTimeout configuration value.
    *
    * @param receiveTimeout the configuration value to set.
    */
    public Long getReceiveTimeout() {
        return receiveTimeout;
    }

    /**
    * Get the receiveTimeout configuration value.
    *
    * @return receiveTimeout configuration value.
    */
    public void setReceiveTimeout(Long receiveTimeout) {
        this.receiveTimeout = receiveTimeout;
    }
    /**
    * Set the sessionAcknowledgeMode configuration value.
    *
    * @param sessionAcknowledgeMode the configuration value to set.
    */
    public Integer getSessionAcknowledgeMode() {
        return sessionAcknowledgeMode;
    }

    /**
    * Get the sessionAcknowledgeMode configuration value.
    *
    * @return sessionAcknowledgeMode configuration value.
    */
    public void setSessionAcknowledgeMode(Integer sessionAcknowledgeMode) {
        this.sessionAcknowledgeMode = sessionAcknowledgeMode;
    }
    /**
    * Set the sessionAcknowledgeModeName configuration value.
    *
    * @param sessionAcknowledgeModeName the configuration value to set.
    */
    public String getSessionAcknowledgeModeName() {
        return sessionAcknowledgeModeName;
    }

    /**
    * Get the sessionAcknowledgeModeName configuration value.
    *
    * @return sessionAcknowledgeModeName configuration value.
    */
    public void setSessionAcknowledgeModeName(String sessionAcknowledgeModeName) {
        this.sessionAcknowledgeModeName = sessionAcknowledgeModeName;
    }
    /**
    * Set the sessionTransacted configuration value.
    *
    * @param sessionTransacted the configuration value to set.
    */
    public Boolean getSessionTransacted() {
        return sessionTransacted;
    }

    /**
    * Get the sessionTransacted configuration value.
    *
    * @return sessionTransacted configuration value.
    */
    public void setSessionTransacted(Boolean sessionTransacted) {
        this.sessionTransacted = sessionTransacted;
    }
    /**
    * Set the timeToLive configuration value.
    *
    * @param timeToLive the configuration value to set.
    */
    public Long getTimeToLive() {
        return timeToLive;
    }

    /**
    * Get the timeToLive configuration value.
    *
    * @return timeToLive configuration value.
    */
    public void setTimeToLive(Long timeToLive) {
        this.timeToLive = timeToLive;
    }
}
