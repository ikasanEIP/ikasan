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
package org.ikasan.component.endpoint.kafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.ikasan.spec.configuration.Masked;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of a consumer configuration for a Kafka consumer.
 * 
 * @author Ikasan Development Team
 */
public class KafkaConsumerConfiguration
{
    private Long offset = 0L;

    /**
     * The name of the topic to subscribe to.
     */
    private String topicName;

    /**
     * Deserializer class for key that implements the org.apache.kafka.common.serialization.Deserializer interface.
     */
    private String keyDeserializer;


    /**
     * Deserializer class for value that implements the org.apache.kafka.common.serialization.Deserializer interface.
     */
    private String valueDeserializer;

    /**
     * A list of host/port pairs to use for establishing the initial connection to the Kafka cluster. The client will
     * make use of all servers irrespective of which servers are specified here for bootstrapping—this list only
     * impacts the initial hosts used to discover the full set of servers. This list should be in the form
     * host1:port1,host2:port2,.... Since these servers are just used for the initial connection to discover
     * the full cluster membership (which may change dynamically), this list need not contain the full set of
     * servers (you may want more than one, though, in case a server is down).
     */
    private List<String> bootstrapServers;

    /**
     * The minimum amount of data the server should return for a fetch request. If insufficient data is available the
     * request will wait for that much data to accumulate before answering the request. The default setting of 1 byte
     * means that fetch requests are answered as soon as a single byte of data is available or the fetch request times
     * out waiting for data to arrive. Setting this to something greater than 1 will cause the server to wait for
     * larger amounts of data to accumulate which can improve server throughput a bit at the cost of some additional
     * latency.
     */
    private Integer fetchMinBytes;

    /**
     * A unique string that identifies the consumer group this consumer belongs to. This property is required if the
     * consumer uses either the group management functionality by using subscribe(topic) or the Kafka-based offset
     * management strategy.
     */
    private String groupId;

    /**
     * The expected time between heartbeats to the consumer coordinator when using Kafka's group management facilities.
     * Heartbeats are used to ensure that the consumer's session stays active and to facilitate rebalancing when new
     * consumers join or leave the group. The value must be set lower than session.timeout.ms, but typically should be
     * set no higher than 1/3 of that value. It can be adjusted even lower to control the expected time for normal
     * re-balances.
     *
     * Default:	3000 (3 seconds)
     */
    private Integer heartbeatIntervalMillis;

    /**
     * The maximum amount of data per-partition the server will return. Records are fetched in batches by the consumer.
     * If the first record batch in the first non-empty partition of the fetch is larger than this limit, the batch will
     * still be returned to ensure that the consumer can make progress. The maximum record batch size accepted by the
     * broker is defined via message.max.bytes (broker config) or max.message.bytes (topic config). See fetch.max.bytes
     * for limiting the consumer request size.
     *
     * Default:	1048576 (1 mebibyte)
     */
    private Integer maxPartitionFetchBytes;

    /**
     * The timeout used to detect client failures when using Kafka's group management facility. The client sends periodic
     * heartbeats to indicate its liveness to the broker. If no heartbeats are received by the broker before the expiration
     * of this session timeout, then the broker will remove this client from the group and initiate a rebalance. Note that
     * the value must be in the allowable range as configured in the broker configuration by group.min.session.timeout.ms
     * and group.max.session.timeout.ms.
     *
     * Default:	10000 (10 seconds)
     */
    private Integer sessionTimeoutMillis;

    /**
     * The password of the private key in the key store file. This is optional for client.
     */
    @Masked
    private String sslKeyPassword;

    /**
     * The location of the key store file. This is optional for client and can be used for two-way authentication for client.
     */
    private String sslKeystoreLocation;

    /**
     * The store password for the key store file. This is optional for client and only needed if ssl.keystore.location
     * is configured.
     */
    @Masked
    private String sslKeystorePassword;

    /**
     * The location of the trust store file.
     */
    private String sslTruststoreLocation;

    /**
     * The password for the trust store file. If a password is not set access to the truststore is still available, but
     * integrity checking is disabled.
     */
    @Masked
    private String sslTruststorePassword;

    /**
     * Allow automatic topic creation on the broker when subscribing to or assigning a topic. A topic being subscribed to
     * will be automatically created only if the broker allows for it using `auto.create.topics.enable` broker configuration.
     * This configuration must be set to `false` when using brokers older than 0.11.0
     *
     * Default:	true
     */
    private Boolean allowAutoCreateTopics;

    /**
     * What to do when there is no initial offset in Kafka or if the current offset does not exist any more on the server (e.g. because that data has been deleted):
     *
     * earliest: automatically reset the offset to the earliest offset
     * latest: automatically reset the offset to the latest offset
     * none: throw exception to the consumer if no previous offset is found for the consumer's group
     * anything else: throw exception to the consumer.
     *
     * Default:	latest
     */
    private String autoOffsetReset;

    /**
     * Controls how the client uses DNS lookups. If set to use_all_dns_ips, connect to each returned IP address in sequence until a successful connection is
     * established. After a disconnection, the next IP is used. Once all IPs have been used once, the client resolves the IP(s) from the hostname again
     * (both the JVM and the OS cache DNS name lookups, however). If set to resolve_canonical_bootstrap_servers_only, resolve each bootstrap address
     * into a list of canonical names. After the bootstrap phase, this behaves the same as use_all_dns_ips. If set to default (deprecated), attempt
     * to connect to the first IP address returned by the lookup, even if the lookup returns multiple IP addresses.
     *
     * Default:	use_all_dns_ips
     * Valid Values:	[default, use_all_dns_ips, resolve_canonical_bootstrap_servers_only]
     */
    private String clientDnsLookup;

    /**
     * Close idle connections after the number of milliseconds specified by this config.
     *
     * Default:	540000 (9 minutes)
     */
    private Long connectionsMaxIdleMillis;

    /**
     * Specifies the timeout (in milliseconds) for client APIs. This configuration is used as the default timeout for all client operations that do not
     * specify a timeout parameter.
     *
     * Default:	60000 (1 minute)
     */
    private Long defaultApiTimeoutMillis;

    /**
     * If true the consumer's offset will be periodically committed in the background.
     *
     * Default:	true
     */
    private Boolean enableAutoCommit;

    /**
     * Whether internal topics matching a subscribed pattern should be excluded from the subscription. It is always possible to explicitly subscribe
     * to an internal topic.
     *
     * Default:	true
     */
    private Boolean excludeInternalTopics;

    /**
     * The maximum amount of data the server should return for a fetch request. Records are fetched in batches by the consumer, and if the first record
     * batch in the first non-empty partition of the fetch is larger than this value, the record batch will still be returned to ensure that the
     * consumer can make progress. As such, this is not a absolute maximum. The maximum record batch size accepted by the broker is defined via
     * message.max.bytes (broker config) or max.message.bytes (topic config). Note that the consumer performs multiple fetches in parallel.
     *
     * Default:	52428800 (50 mebibytes)
     */
    private Integer fetchMaxBytes;

    /**
     * A unique identifier of the consumer instance provided by the end user. Only non-empty strings are permitted. If set, the consumer is treated as a static member,
     * which means that only one instance with this ID is allowed in the consumer group at any time. This can be used in combination with a larger session timeout
     * to avoid group rebalances caused by transient unavailability (e.g. process restarts). If not set, the consumer will join the group as a dynamic member,
     * which is the traditional behavior.
     */
    private String groupInstanceId;

    /**
     * Controls how to read messages written transactionally. If set to read_committed, consumer.poll() will only return transactional messages which have been
     * committed. If set to read_uncommitted' (the default), consumer.poll() will return all messages, even transactional messages which have been aborted.
     * Non-transactional messages will be returned unconditionally in either mode.
     *
     * Messages will always be returned in offset order. Hence, in read_committed mode, consumer.poll() will only return messages up to the last stable offset
     * (LSO), which is the one less than the offset of the first open transaction. In particular any messages appearing after messages belonging to ongoing
     * transactions will be withheld until the relevant transaction has been completed. As a result, read_committed consumers will not be able to read up
     * to the high watermark when there are in flight transactions.
     *
     * Further, when in read_committed the seekToEnd method will return the LSO
     *
     * Default:	read_uncommitted
     * Valid Values:	[read_committed, read_uncommitted]
     */
    private String isolationLevel;

    /**
     * The maximum delay between invocations of poll() when using consumer group management. This places an upper bound on the amount of time that the consumer
     * can be idle before fetching more records. If poll() is not called before expiration of this timeout, then the consumer is considered failed and the group
     * will rebalance in order to reassign the partitions to another member. For consumers using a non-null group.instance.id which reach this timeout,
     * partitions will not be immediately reassigned. Instead, the consumer will stop sending heartbeats and partitions will be reassigned after expiration
     * of session.timeout.ms. This mirrors the behavior of a static consumer which has shutdown.
     *
     * Default:	300000 (5 minutes)
     */
    private Long maxPollIntervalMillis;

    /**
     * The maximum number of records returned in a single call to poll().
     *
     * Default:	500
     */
    private Integer maxPollRecords;

    /**
     * A list of class names or class types, ordered by preference, of supported partition assignment strategies that the client will use to distribute
     * partition ownership amongst consumer instances when group management is used.
     *
     * In addition to the default class specified below, you can use the org.apache.kafka.clients.consumer.RoundRobinAssignorclass for round robin
     * assignments of partitions to consumers.
     *
     * Implementing the org.apache.kafka.clients.consumer.ConsumerPartitionAssignor interface allows you to plug in a custom assignmentstrategy.
     *
     * Default:	class org.apache.kafka.clients.consumer.RangeAssignor
     */
    private List<String> partitionAssignmentStrategy;

    /**
     * The size of the TCP receive buffer (SO_RCVBUF) to use when reading data. If the value is -1, the OS default will be used.
     *
     * Default:	65536 (64 kibibytes)
     */
    private Integer receiveBufferBytes;

    /**
     * The configuration controls the maximum amount of time the client will wait for the response of a request. If the response
     * is not received before the timeout elapses the client will resend the request if necessary or fail the request if retries
     * are exhausted.
     *
     * Default:	30000 (30 seconds)
     */
    private Long requestTimeoutMillis;

    /**
     * The fully qualified name of a SASL client callback handler class that implements the AuthenticateCallbackHandler interface.
     */
    private String saslClientCallbackHandlerClass;

    /**
     * JAAS login context parameters for SASL connections in the format used by JAAS configuration files. JAAS configuration file
     * format is described here. The format for the value is: 'loginModuleClass controlFlag (optionName=optionValue)*;'. For brokers,
     * the config must be prefixed with listener prefix and SASL mechanism name in lower-case. For example,
     * listener.name.sasl_ssl.scram-sha-256.sasl.jaas.config=com.example.ScramLoginModule required;
     */
    @Masked
    private String saslJaasConfig;

    /**
     * The Kerberos principal name that Kafka runs as. This can be defined either in Kafka's JAAS config or in Kafka's config.
     */
    private String saslKerberosServiceName;

    /**
     * The fully qualified name of a SASL login callback handler class that implements the AuthenticateCallbackHandler interface.
     * For brokers, login callback handler config must be prefixed with listener prefix and SASL mechanism name in lower-case.
     *
     * For example, listener.name.sasl_ssl.scram-sha-256.sasl.login.callback.handler.class=com.example.CustomScramLoginCallbackHandler
     */
    private String saslLoginCallbackHandlerClass;

    /**
     * The fully qualified name of a class that implements the Login interface. For brokers, login config must be prefixed with
     * listener prefix and SASL mechanism name in lower-case.
     *
     * For example, listener.name.sasl_ssl.scram-sha-256.sasl.login.class=com.example.CustomScramLogin
     */
    private String saslLoginClass;

    /**
     * SASL mechanism used for client connections. This may be any mechanism for which a security provider is available.
     * GSSAPI is the default mechanism.
     *
     * Default:	GSSAPI
     */
    private String saslMechanism;

    /**
     * Protocol used to communicate with brokers. Valid values are: PLAINTEXT, SSL, SASL_PLAINTEXT, SASL_SSL.
     *
     * Default:	PLAINTEXT
     */
    private String securityProtocol;

    /**
     * The size of the TCP send buffer (SO_SNDBUF) to use when sending data. If the value is -1, the OS default will be used.
     *
     * Default:	131072 (128 kibibytes)
     */
    private Integer sendBufferBytes;

    /**
     * The list of protocols enabled for SSL connections. The default is 'TLSv1.2,TLSv1.3' when running with Java 11 or newer, 'TLSv1.2' otherwise.
     * With the default value for Java 11, clients and servers will prefer TLSv1.3 if both support it and fallback to TLSv1.2 otherwise
     * (assuming both support at least TLSv1.2). This default should be fine for most cases. Also see the config documentation for `ssl.protocol`.
     *
     * Default:	TLSv1.2
     */
    private List<String> sslEnabledProtocols;

    /**
     * The file format of the key store file. This is optional for client.
     *
     * Default:	JKS
     */
    private String sslKeystoreType;

    /**
     * The SSL protocol used to generate the SSLContext. The default is 'TLSv1.3' when running with Java 11 or newer, 'TLSv1.2' otherwise. This value
     * should be fine for most use cases. Allowed values in recent JVMs are 'TLSv1.2' and 'TLSv1.3'. 'TLS', 'TLSv1.1', 'SSL', 'SSLv2' and 'SSLv3'
     * may be supported in older JVMs, but their usage is discouraged due to known security vulnerabilities. With the default value for this config
     * and 'ssl.enabled.protocols', clients will downgrade to 'TLSv1.2' if the server does not support 'TLSv1.3'. If this config is set to 'TLSv1.2',
     * clients will not use 'TLSv1.3' even if it is one of the values in ssl.enabled.protocols and the server only supports 'TLSv1.3'.
     *
     * Default:	TLSv1.2
     */
    private String sslProtocol;


    /**
     * The name of the security provider used for SSL connections. Default value is the default security provider of the JVM.
     */
    private String sslProvider;

    /**
     * The file format of the trust store file.
     *
     * Default:	JKS
     */
    private String sslTruststoreType;

    /**
     * The frequency in milliseconds that the consumer offsets are auto-committed to Kafka if enable.auto.commit is set to true.
     *
     * Default:	5000 (5 seconds)
     */
    private Integer autoCommitIntervalMillis;

    /**
     * Automatically check the CRC32 of the records consumed. This ensures no on-the-wire or on-disk corruption to the messages occurred.
     * This check adds some overhead, so it may be disabled in cases seeking extreme performance.
     *
     * Default:	true
     */
    private Boolean checkCrcs;

    /**
     * An id string to pass to the server when making requests. The purpose of this is to be able to track the source of requests beyond just
     * ip/port by allowing a logical application name to be included in server-side request logging.
     *
     * Default:	""
     */
    private String clientId;

    /**
     * A rack identifier for this client. This can be any string value which indicates where this client is physically located. It corresponds
     * with the broker config 'broker.rack'
     *
     * Default:	""
     */
    private String clientRack;

    /**
     * The maximum amount of time the server will block before answering the fetch request if there isn't sufficient data to immediately
     * satisfy the requirement given by fetch.min.bytes.
     *
     * Default:	500
     */
    private Long fetchMaxWaitMillis;

    /**
     * A list of classes to use as interceptors. Implementing the org.apache.kafka.clients.consumer.ConsumerInterceptor interface allows you to
     * intercept (and possibly mutate) records received by the consumer. By default, there are no interceptors.
     */
    private List<String> interceptorClasses;

    /**
     * The period of time in milliseconds after which we force a refresh of metadata even if we haven't seen any partition leadership changes
     * to proactively discover any new brokers or partitions.
     *
     * Default:	300000 (5 minutes)
     */
    private Long metadataMaxAgeMillis;

    /**
     * A list of classes to use as metrics reporters. Implementing the org.apache.kafka.common.metrics.MetricsReporter interface allows plugging in classes
     * that will be notified of new metric creation. The JmxReporter is always included to register JMX statistics.
     */
    private List<String> metricReporters;

    /**
     * The number of samples maintained to compute metrics.
     *
     * Default:	2
     */
    private Integer metricsNumSamples;

    /**
     * The highest recording level for metrics.
     *
     * Default:	INFO
     * Valid Values:	[INFO, DEBUG]
     */
    private String metricsRecordingLevel;

    /**
     * The window of time a metrics sample is computed over.
     *
     * Default:	30000 (30 seconds)
     */
    private Long metricsSampleWindowMillis;

    /**
     * The maximum amount of time in milliseconds to wait when reconnecting to a broker that has repeatedly failed to connect. If provided, the backoff per host
     * will increase exponentially for each consecutive connection failure, up to this maximum. After calculating the backoff increase, 20% random jitter is added
     * to avoid connection storms.
     *
     * Default:	1000 (1 second)
     */
    private Long reconnectBackoffMaxMillis;

    /**
     * The base amount of time to wait before attempting to reconnect to a given host. This avoids repeatedly connecting to a host in a tight loop. This backoff applies
     * to all connection attempts by the client to a broker.
     *
     * Default:	50
     */
    private Long reconnectBackoffMillis;

    /**
     * The amount of time to wait before attempting to retry a failed request to a given topic partition. This avoids repeatedly sending requests in a tight loop under
     * some failure scenarios.
     *
     * Default:	100
     */
    private Long retryBackoffMillis;

    /**
     * Kerberos kinit command path.
     *
     * Default:	/usr/bin/kinit
     */
    private String saslKerberosKinitCmd;

    /**
     * Login thread sleep time between refresh attempts.
     *
     * Default:	60000
     */
    private Long saslKerberosMinTimeBeforeRelogin;

    /**
     * Percentage of random jitter added to the renewal time.
     *
     * Default:	0.05
     */
    private Double saslKerberosTicketRenewJitter;

    /**
     * Login thread will sleep until the specified window factor of time from last refresh to ticket's expiry has been reached,
     * at which time it will try to renew the ticket.
     *
     * Default:	0.8
     */
    private Double saslKerberosTicketRenewWindowFactor;

    /**
     * The amount of buffer time before credential expiration to maintain when refreshing a credential, in seconds. If a refresh would
     * otherwise occur closer to expiration than the number of buffer seconds then the refresh will be moved up to maintain as much of
     * the buffer time as possible. Legal values are between 0 and 3600 (1 hour); a default value of 300 (5 minutes) is used if no
     * value is specified. This value and sasl.login.refresh.min.period.seconds are both ignored if their sum exceeds the remaining
     * lifetime of a credential. Currently applies only to OAUTHBEARER.
     *
     * Default:	300
     * Valid Values:	[0,...,3600]
     */
    private Integer saslLoginRefreshBufferSeconds;

    /**
     * The desired minimum time for the login refresh thread to wait before refreshing a credential, in seconds. Legal values are between
     * 0 and 900 (15 minutes); a default value of 60 (1 minute) is used if no value is specified. This value and sasl.login.refresh.buffer.seconds
     * are both ignored if their sum exceeds the remaining lifetime of a credential. Currently applies only to OAUTHBEARER.
     *
     * Default:	60
     * Valid Values:	[0,...,900]
     */
    private Integer saslLoginRefreshMinPeriodSeconds;

    /**
     * Login refresh thread will sleep until the specified window factor relative to the credential's lifetime has been reached, at which time it
     * will try to refresh the credential. Legal values are between 0.5 (50%) and 1.0 (100%) inclusive; a default value of 0.8 (80%) is used if no
     * value is specified. Currently applies only to OAUTHBEARER.
     *
     * Default:	0.8
     * Valid Values:	[0.5,...,1.0]
     */
    private Double saslLoginRefreshWindowFactor;

    /**
     * The maximum amount of random jitter relative to the credential's lifetime that is added to the login refresh thread's sleep time. Legal values are
     * between 0 and 0.25 (25%) inclusive; a default value of 0.05 (5%) is used if no value is specified. Currently applies only to OAUTHBEARER.
     *
     * Default:	0.05
     * Valid Values:	[0.0,...,0.25]
     */
    private Double saslLoginRefreshWindowJitter;

    /**
     * A list of configurable creator classes each returning a provider implementing security algorithms. These classes should implement the
     * org.apache.kafka.common.security.auth.SecurityProviderCreator interface.
     */
    private String securityProviders;

    /**
     * A list of cipher suites. This is a named combination of authentication, encryption, MAC and key exchange algorithm used to negotiate the security settings for
     * a network connection using TLS or SSL network protocol. By default all the available cipher suites are supported.
     */
    private List<String> sslCipherSuites;

    /**
     * The endpoint identification algorithm to validate server hostname using server certificate.
     *
     * Default:	https
     */
    private String sslEndpointIdentificationAlgorithm;

    /**
     * The class of type org.apache.kafka.common.security.auth.SslEngineFactory to provide SSLEngine objects. Default value is
     * org.apache.kafka.common.security.ssl.DefaultSslEngineFactory
     */
    private String sslEngineFactoryClass;

    /**
     * The algorithm used by key manager factory for SSL connections. Default value is the key manager factory algorithm configured for the
     * Java Virtual Machine.
     *
     * Default:	SunX509
     */
    private String sslKeymanagerAlgorithm;

    /**
     * The SecureRandom PRNG implementation to use for SSL cryptography operations.
     */
    private String sslSecureRandomImplementation;

    /**
     * The algorithm used by trust manager factory for SSL connections. Default value is the trust manager factory algorithm configured
     * for the Java Virtual Machine.
     *
     * Default:	PKIX
     */
    private String sslTrustmanagerAlgorithm;

    public Long getOffset() {
        return offset;
    }

    public void setOffset(Long offset) {
        this.offset = offset;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getKeyDeserializer() {
        return keyDeserializer;
    }

    public void setKeyDeserializer(String keyDeserializer) {
        this.keyDeserializer = keyDeserializer;
    }

    public String getValueDeserializer() {
        return valueDeserializer;
    }

    public void setValueDeserializer(String valueDeserializer) {
        this.valueDeserializer = valueDeserializer;
    }

    public List<String> getBootstrapServers() {
        return bootstrapServers;
    }

    public void setBootstrapServers(List<String> bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public Integer getFetchMinBytes() {
        return fetchMinBytes;
    }

    public void setFetchMinBytes(Integer fetchMinBytes) {
        this.fetchMinBytes = fetchMinBytes;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public Integer getHeartbeatIntervalMillis() {
        return heartbeatIntervalMillis;
    }

    public void setHeartbeatIntervalMillis(Integer heartbeatIntervalMillis) {
        this.heartbeatIntervalMillis = heartbeatIntervalMillis;
    }

    public Integer getMaxPartitionFetchBytes() {
        return maxPartitionFetchBytes;
    }

    public void setMaxPartitionFetchBytes(Integer maxPartitionFetchBytes) {
        this.maxPartitionFetchBytes = maxPartitionFetchBytes;
    }

    public Integer getSessionTimeoutMillis() {
        return sessionTimeoutMillis;
    }

    public void setSessionTimeoutMillis(Integer sessionTimeoutMillis) {
        this.sessionTimeoutMillis = sessionTimeoutMillis;
    }

    public String getSslKeyPassword() {
        return sslKeyPassword;
    }

    public void setSslKeyPassword(String sslKeyPassword) {
        this.sslKeyPassword = sslKeyPassword;
    }

    public String getSslKeystoreLocation() {
        return sslKeystoreLocation;
    }

    public void setSslKeystoreLocation(String sslKeystoreLocation) {
        this.sslKeystoreLocation = sslKeystoreLocation;
    }

    public String getSslKeystorePassword() {
        return sslKeystorePassword;
    }

    public void setSslKeystorePassword(String sslKeystorePassword) {
        this.sslKeystorePassword = sslKeystorePassword;
    }

    public String getSslTruststoreLocation() {
        return sslTruststoreLocation;
    }

    public void setSslTruststoreLocation(String sslTruststoreLocation) {
        this.sslTruststoreLocation = sslTruststoreLocation;
    }

    public String getSslTruststorePassword() {
        return sslTruststorePassword;
    }

    public void setSslTruststorePassword(String sslTruststorePassword) {
        this.sslTruststorePassword = sslTruststorePassword;
    }

    public Boolean getAllowAutoCreateTopics() {
        return allowAutoCreateTopics;
    }

    public void setAllowAutoCreateTopics(Boolean allowAutoCreateTopics) {
        this.allowAutoCreateTopics = allowAutoCreateTopics;
    }

    public String getAutoOffsetReset() {
        return autoOffsetReset;
    }

    public void setAutoOffsetReset(String autoOffsetReset) {
        this.autoOffsetReset = autoOffsetReset;
    }

    public String getClientDnsLookup() {
        return clientDnsLookup;
    }

    public void setClientDnsLookup(String clientDnsLookup) {
        this.clientDnsLookup = clientDnsLookup;
    }

    public Long getConnectionsMaxIdleMillis() {
        return connectionsMaxIdleMillis;
    }

    public void setConnectionsMaxIdleMillis(Long connectionsMaxIdleMillis) {
        this.connectionsMaxIdleMillis = connectionsMaxIdleMillis;
    }

    public Long getDefaultApiTimeoutMillis() {
        return defaultApiTimeoutMillis;
    }

    public void setDefaultApiTimeoutMillis(Long defaultApiTimeoutMillis) {
        this.defaultApiTimeoutMillis = defaultApiTimeoutMillis;
    }

    public Boolean getEnableAutoCommit() {
        return enableAutoCommit;
    }

    public void setEnableAutoCommit(Boolean enableAutoCommit) {
        this.enableAutoCommit = enableAutoCommit;
    }

    public Boolean getExcludeInternalTopics() {
        return excludeInternalTopics;
    }

    public void setExcludeInternalTopics(Boolean excludeInternalTopics) {
        this.excludeInternalTopics = excludeInternalTopics;
    }

    public Integer getFetchMaxBytes() {
        return fetchMaxBytes;
    }

    public void setFetchMaxBytes(Integer fetchMaxBytes) {
        this.fetchMaxBytes = fetchMaxBytes;
    }

    public String getGroupInstanceId() {
        return groupInstanceId;
    }

    public void setGroupInstanceId(String groupInstanceId) {
        this.groupInstanceId = groupInstanceId;
    }

    public String getIsolationLevel() {
        return isolationLevel;
    }

    public void setIsolationLevel(String isolationLevel) {
        this.isolationLevel = isolationLevel;
    }

    public Long getMaxPollIntervalMillis() {
        return maxPollIntervalMillis;
    }

    public void setMaxPollIntervalMillis(Long maxPollIntervalMillis) {
        this.maxPollIntervalMillis = maxPollIntervalMillis;
    }

    public Integer getMaxPollRecords() {
        return maxPollRecords;
    }

    public void setMaxPollRecords(Integer maxPollRecords) {
        this.maxPollRecords = maxPollRecords;
    }

    public List<String> getPartitionAssignmentStrategy() {
        return partitionAssignmentStrategy;
    }

    public void setPartitionAssignmentStrategy(List<String> partitionAssignmentStrategy) {
        this.partitionAssignmentStrategy = partitionAssignmentStrategy;
    }

    public Integer getReceiveBufferBytes() {
        return receiveBufferBytes;
    }

    public void setReceiveBufferBytes(Integer receiveBufferBytes) {
        this.receiveBufferBytes = receiveBufferBytes;
    }

    public Long getRequestTimeoutMillis() {
        return requestTimeoutMillis;
    }

    public void setRequestTimeoutMillis(Long requestTimeoutMillis) {
        this.requestTimeoutMillis = requestTimeoutMillis;
    }

    public String getSaslClientCallbackHandlerClass() {
        return saslClientCallbackHandlerClass;
    }

    public void setSaslClientCallbackHandlerClass(String saslClientCallbackHandlerClass) {
        this.saslClientCallbackHandlerClass = saslClientCallbackHandlerClass;
    }

    public String getSaslJaasConfig() {
        return saslJaasConfig;
    }

    public void setSaslJaasConfig(String saslJaasConfig) {
        this.saslJaasConfig = saslJaasConfig;
    }

    public String getSaslKerberosServiceName() {
        return saslKerberosServiceName;
    }

    public void setSaslKerberosServiceName(String saslKerberosServiceName) {
        this.saslKerberosServiceName = saslKerberosServiceName;
    }

    public String getSaslLoginCallbackHandlerClass() {
        return saslLoginCallbackHandlerClass;
    }

    public void setSaslLoginCallbackHandlerClass(String saslLoginCallbackHandlerClass) {
        this.saslLoginCallbackHandlerClass = saslLoginCallbackHandlerClass;
    }

    public String getSaslLoginClass() {
        return saslLoginClass;
    }

    public void setSaslLoginClass(String saslLoginClass) {
        this.saslLoginClass = saslLoginClass;
    }

    public String getSaslMechanism() {
        return saslMechanism;
    }

    public void setSaslMechanism(String saslMechanism) {
        this.saslMechanism = saslMechanism;
    }

    public String getSecurityProtocol() {
        return securityProtocol;
    }

    public void setSecurityProtocol(String securityProtocol) {
        this.securityProtocol = securityProtocol;
    }

    public Integer getSendBufferBytes() {
        return sendBufferBytes;
    }

    public void setSendBufferBytes(Integer sendBufferBytes) {
        this.sendBufferBytes = sendBufferBytes;
    }

    public List<String> getSslEnabledProtocols() {
        return sslEnabledProtocols;
    }

    public void setSslEnabledProtocols(List<String> sslEnabledProtocols) {
        this.sslEnabledProtocols = sslEnabledProtocols;
    }

    public String getSslKeystoreType() {
        return sslKeystoreType;
    }

    public void setSslKeystoreType(String sslKeystoreType) {
        this.sslKeystoreType = sslKeystoreType;
    }

    public String getSslProtocol() {
        return sslProtocol;
    }

    public void setSslProtocol(String sslProtocol) {
        this.sslProtocol = sslProtocol;
    }

    public String getSslProvider() {
        return sslProvider;
    }

    public void setSslProvider(String sslProvider) {
        this.sslProvider = sslProvider;
    }

    public String getSslTruststoreType() {
        return sslTruststoreType;
    }

    public void setSslTruststoreType(String sslTruststoreType) {
        this.sslTruststoreType = sslTruststoreType;
    }

    public Integer getAutoCommitIntervalMillis() {
        return autoCommitIntervalMillis;
    }

    public void setAutoCommitIntervalMillis(Integer autoCommitIntervalMillis) {
        this.autoCommitIntervalMillis = autoCommitIntervalMillis;
    }

    public Boolean getCheckCrcs() {
        return checkCrcs;
    }

    public void setCheckCrcs(Boolean checkCrcs) {
        this.checkCrcs = checkCrcs;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientRack() {
        return clientRack;
    }

    public void setClientRack(String clientRack) {
        this.clientRack = clientRack;
    }

    public Long getFetchMaxWaitMillis() {
        return fetchMaxWaitMillis;
    }

    public void setFetchMaxWaitMillis(Long fetchMaxWaitMillis) {
        this.fetchMaxWaitMillis = fetchMaxWaitMillis;
    }

    public List<String> getInterceptorClasses() {
        return interceptorClasses;
    }

    public void setInterceptorClasses(List<String> interceptorClasses) {
        this.interceptorClasses = interceptorClasses;
    }

    public Long getMetadataMaxAgeMillis() {
        return metadataMaxAgeMillis;
    }

    public void setMetadataMaxAgeMillis(Long metadataMaxAgeMillis) {
        this.metadataMaxAgeMillis = metadataMaxAgeMillis;
    }

    public List<String> getMetricReporters() {
        return metricReporters;
    }

    public void setMetricReporters(List<String> metricReporters) {
        this.metricReporters = metricReporters;
    }

    public Integer getMetricsNumSamples() {
        return metricsNumSamples;
    }

    public void setMetricsNumSamples(Integer metricsNumSamples) {
        this.metricsNumSamples = metricsNumSamples;
    }

    public String getMetricsRecordingLevel() {
        return metricsRecordingLevel;
    }

    public void setMetricsRecordingLevel(String metricsRecordingLevel) {
        this.metricsRecordingLevel = metricsRecordingLevel;
    }

    public Long getMetricsSampleWindowMillis() {
        return metricsSampleWindowMillis;
    }

    public void setMetricsSampleWindowMillis(Long metricsSampleWindowMillis) {
        this.metricsSampleWindowMillis = metricsSampleWindowMillis;
    }

    public Long getReconnectBackoffMaxMillis() {
        return reconnectBackoffMaxMillis;
    }

    public void setReconnectBackoffMaxMillis(Long reconnectBackoffMaxMillis) {
        this.reconnectBackoffMaxMillis = reconnectBackoffMaxMillis;
    }

    public Long getReconnectBackoffMillis() {
        return reconnectBackoffMillis;
    }

    public void setReconnectBackoffMillis(Long reconnectBackoffMillis) {
        this.reconnectBackoffMillis = reconnectBackoffMillis;
    }

    public Long getRetryBackoffMillis() {
        return retryBackoffMillis;
    }

    public void setRetryBackoffMillis(Long retryBackoffMillis) {
        this.retryBackoffMillis = retryBackoffMillis;
    }

    public String getSaslKerberosKinitCmd() {
        return saslKerberosKinitCmd;
    }

    public void setSaslKerberosKinitCmd(String saslKerberosKinitCmd) {
        this.saslKerberosKinitCmd = saslKerberosKinitCmd;
    }

    public Long getSaslKerberosMinTimeBeforeRelogin() {
        return saslKerberosMinTimeBeforeRelogin;
    }

    public void setSaslKerberosMinTimeBeforeRelogin(Long saslKerberosMinTimeBeforeRelogin) {
        this.saslKerberosMinTimeBeforeRelogin = saslKerberosMinTimeBeforeRelogin;
    }

    public Double getSaslKerberosTicketRenewJitter() {
        return saslKerberosTicketRenewJitter;
    }

    public void setSaslKerberosTicketRenewJitter(Double saslKerberosTicketRenewJitter) {
        this.saslKerberosTicketRenewJitter = saslKerberosTicketRenewJitter;
    }

    public Double getSaslKerberosTicketRenewWindowFactor() {
        return saslKerberosTicketRenewWindowFactor;
    }

    public void setSaslKerberosTicketRenewWindowFactor(Double saslKerberosTicketRenewWindowFactor) {
        this.saslKerberosTicketRenewWindowFactor = saslKerberosTicketRenewWindowFactor;
    }

    public Integer getSaslLoginRefreshBufferSeconds() {
        return saslLoginRefreshBufferSeconds;
    }

    public void setSaslLoginRefreshBufferSeconds(Integer saslLoginRefreshBufferSeconds) {
        this.saslLoginRefreshBufferSeconds = saslLoginRefreshBufferSeconds;
    }

    public Integer getSaslLoginRefreshMinPeriodSeconds() {
        return saslLoginRefreshMinPeriodSeconds;
    }

    public void setSaslLoginRefreshMinPeriodSeconds(Integer saslLoginRefreshMinPeriodSeconds) {
        this.saslLoginRefreshMinPeriodSeconds = saslLoginRefreshMinPeriodSeconds;
    }

    public Double getSaslLoginRefreshWindowFactor() {
        return saslLoginRefreshWindowFactor;
    }

    public void setSaslLoginRefreshWindowFactor(Double saslLoginRefreshWindowFactor) {
        this.saslLoginRefreshWindowFactor = saslLoginRefreshWindowFactor;
    }

    public Double getSaslLoginRefreshWindowJitter() {
        return saslLoginRefreshWindowJitter;
    }

    public void setSaslLoginRefreshWindowJitter(Double saslLoginRefreshWindowJitter) {
        this.saslLoginRefreshWindowJitter = saslLoginRefreshWindowJitter;
    }

    public String getSecurityProviders() {
        return securityProviders;
    }

    public void setSecurityProviders(String securityProviders) {
        this.securityProviders = securityProviders;
    }

    public List<String> getSslCipherSuites() {
        return sslCipherSuites;
    }

    public void setSslCipherSuites(List<String> sslCipherSuites) {
        this.sslCipherSuites = sslCipherSuites;
    }

    public String getSslEndpointIdentificationAlgorithm() {
        return sslEndpointIdentificationAlgorithm;
    }

    public void setSslEndpointIdentificationAlgorithm(String sslEndpointIdentificationAlgorithm) {
        this.sslEndpointIdentificationAlgorithm = sslEndpointIdentificationAlgorithm;
    }

    public String getSslEngineFactoryClass() {
        return sslEngineFactoryClass;
    }

    public void setSslEngineFactoryClass(String sslEngineFactoryClass) {
        this.sslEngineFactoryClass = sslEngineFactoryClass;
    }

    public String getSslKeymanagerAlgorithm() {
        return sslKeymanagerAlgorithm;
    }

    public void setSslKeymanagerAlgorithm(String sslKeymanagerAlgorithm) {
        this.sslKeymanagerAlgorithm = sslKeymanagerAlgorithm;
    }

    public String getSslSecureRandomImplementation() {
        return sslSecureRandomImplementation;
    }

    public void setSslSecureRandomImplementation(String sslSecureRandomImplementation) {
        this.sslSecureRandomImplementation = sslSecureRandomImplementation;
    }

    public String getSslTrustmanagerAlgorithm() {
        return sslTrustmanagerAlgorithm;
    }

    public void setSslTrustmanagerAlgorithm(String sslTrustmanagerAlgorithm) {
        this.sslTrustmanagerAlgorithm = sslTrustmanagerAlgorithm;
    }

    public Map<String, Object> getConsumerProps() throws ClassNotFoundException {
        Map<String, Object> props = new HashMap<>();

        if(this.getKeyDeserializer() != null && !this.getKeyDeserializer().isEmpty()) {
            props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, Class.forName(this.getKeyDeserializer()));
        }

        if(this.getAllowAutoCreateTopics() != null) {
            props.put(ConsumerConfig.ALLOW_AUTO_CREATE_TOPICS_CONFIG, this.getAllowAutoCreateTopics());
        }

        if(this.getAutoCommitIntervalMillis() != null) {
            props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, this.getAutoCommitIntervalMillis());
        }

        if(this.getAutoOffsetReset() != null && !this.getAutoOffsetReset().isEmpty()) {
            props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, this.getAutoOffsetReset());
        }

        if(this.getBootstrapServers() != null && !this.getBootstrapServers().isEmpty()) {
            props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, this.getBootstrapServers());
        }

        if(this.getCheckCrcs() != null) {
            props.put(ConsumerConfig.CHECK_CRCS_CONFIG, this.getCheckCrcs());
        }

        if(this.getClientDnsLookup() != null && !this.getClientDnsLookup().isEmpty()) {
            props.put(ConsumerConfig.CLIENT_DNS_LOOKUP_CONFIG, this.getClientDnsLookup());
        }

        if(this.getClientId() != null && !this.getClientId().isEmpty()) {
            props.put(ConsumerConfig.CLIENT_ID_CONFIG, this.getClientId());
        }

        if(this.getClientRack() != null && !this.getClientRack().isEmpty()) {
            props.put(ConsumerConfig.CLIENT_RACK_CONFIG, this.getClientRack());
        }

        if(this.getAutoOffsetReset() != null && !this.getAutoOffsetReset().isEmpty()) {
            props.put(ConsumerConfig.AUTO_OFFSET_RESET_DOC, this.getAutoOffsetReset());
        }

        if(this.getConnectionsMaxIdleMillis() != null) {
            props.put(ConsumerConfig.CONNECTIONS_MAX_IDLE_MS_CONFIG, this.getConnectionsMaxIdleMillis());
        }

        if(this.getDefaultApiTimeoutMillis() != null) {
            props.put(ConsumerConfig.DEFAULT_API_TIMEOUT_MS_CONFIG, this.getDefaultApiTimeoutMillis());
        }

        // todo look at default isolation level config
//        if(this.getAutoOffsetReset() != null && !this.getAutoOffsetReset().isEmpty()) {
//            props.put(ConsumerConfig.DEFAULT_ISOLATION_LEVEL, this.getD());
//        }

        if(this.getEnableAutoCommit() != null) {
            props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, this.getEnableAutoCommit());
        }

        if(this.getExcludeInternalTopics() != null) {
            props.put(ConsumerConfig.EXCLUDE_INTERNAL_TOPICS_CONFIG, this.getExcludeInternalTopics());
        }

        if(this.getFetchMaxBytes() != null) {
            props.put(ConsumerConfig.FETCH_MAX_BYTES_CONFIG, this.getFetchMaxBytes());
        }

        if(this.getFetchMaxWaitMillis() != null) {
            props.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, this.getFetchMaxWaitMillis());
        }

        if(this.getFetchMinBytes() != null) {
            props.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, this.getFetchMinBytes());
        }

        if(this.getGroupId() != null && !this.getGroupId().isEmpty()) {
            props.put(ConsumerConfig.GROUP_ID_CONFIG, this.getGroupId());
        }

        if(this.getGroupInstanceId() != null && !this.getGroupInstanceId().isEmpty()) {
            props.put(ConsumerConfig.GROUP_INSTANCE_ID_CONFIG, this.getGroupInstanceId());
        }

        if(this.getHeartbeatIntervalMillis() != null) {
            props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, this.getHeartbeatIntervalMillis());
        }

        if(this.getInterceptorClasses() != null && !this.getInterceptorClasses().isEmpty()) {
            props.put(ConsumerConfig.INTERCEPTOR_CLASSES_CONFIG, this.getInterceptorClasses());
        }

        if(this.getIsolationLevel() != null && !this.getIsolationLevel().isEmpty()) {
            props.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, this.getIsolationLevel());
        }

        if(this.getMaxPartitionFetchBytes() != null) {
            props.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, this.getMaxPartitionFetchBytes());
        }

        if(this.getMaxPollIntervalMillis() != null) {
            props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, this.getMaxPollIntervalMillis());
        }

        if(this.getMaxPollRecords() != null) {
            props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, this.getMaxPollRecords());
        }

        if(this.getMetadataMaxAgeMillis() != null) {
            props.put(ConsumerConfig.METADATA_MAX_AGE_CONFIG, this.getMetadataMaxAgeMillis());
        }

        if(this.getMetricReporters() != null && !this.getMetricReporters().isEmpty()) {
            props.put(ConsumerConfig.METRIC_REPORTER_CLASSES_CONFIG, this.getMetricReporters());
        }

        if(this.getMetricsNumSamples() != null) {
            props.put(ConsumerConfig.METRICS_NUM_SAMPLES_CONFIG, this.getMetricsNumSamples());
        }

        if(this.getMetricsRecordingLevel() != null && !this.getMetricsRecordingLevel().isEmpty()) {
            props.put(ConsumerConfig.METRICS_RECORDING_LEVEL_CONFIG, this.getMetricsRecordingLevel());
        }

        if(this.getMetricsSampleWindowMillis() != null) {
            props.put(ConsumerConfig.METRICS_SAMPLE_WINDOW_MS_CONFIG, this.getMetricsSampleWindowMillis());
        }

        if(this.getPartitionAssignmentStrategy() != null && !this.getPartitionAssignmentStrategy().isEmpty()) {
            props.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG, this.getPartitionAssignmentStrategy());
        }

        if(this.getReceiveBufferBytes() != null) {
            props.put(ConsumerConfig.RECEIVE_BUFFER_CONFIG, this.getReceiveBufferBytes());
        }

        if(this.getReconnectBackoffMaxMillis() != null) {
            props.put(ConsumerConfig.RECONNECT_BACKOFF_MAX_MS_CONFIG, this.getReconnectBackoffMaxMillis());
        }

        if(this.getReconnectBackoffMillis() != null) {
            props.put(ConsumerConfig.RECONNECT_BACKOFF_MS_CONFIG, this.getReconnectBackoffMillis());
        }

        if(this.getRequestTimeoutMillis() != null) {
            props.put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, this.getRequestTimeoutMillis());
        }

        if(this.getRetryBackoffMillis() != null) {
            props.put(ConsumerConfig.RETRY_BACKOFF_MS_CONFIG, this.getRetryBackoffMillis());
        }

        if(this.getSecurityProviders() != null && !this.getSecurityProviders().isEmpty()) {
            props.put(ConsumerConfig.SECURITY_PROVIDERS_CONFIG, this.getSecurityProviders());
        }

        if(this.getSendBufferBytes() != null) {
            props.put(ConsumerConfig.SEND_BUFFER_CONFIG, this.getSendBufferBytes());
        }

        if(this.getSessionTimeoutMillis() != null) {
            props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, this.getSessionTimeoutMillis());
        }

        if(this.getValueDeserializer() != null && !this.getValueDeserializer().isEmpty()) {
            props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, Class.forName(this.getValueDeserializer()));
        }

        return props;
    }
}
