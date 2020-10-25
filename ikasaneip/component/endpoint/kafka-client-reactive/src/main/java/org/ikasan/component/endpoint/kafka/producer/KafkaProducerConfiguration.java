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
package org.ikasan.component.endpoint.kafka.producer;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.ikasan.spec.configuration.Masked;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of a producer configuration for a Kafka producer.
 *
 * @author Ikasan Development Team
 */
public class KafkaProducerConfiguration
{
    /**
     * The name of the topic to subscribe to.
     */
    private String topicName;

    /**
     * Deserializer class for key that implements the org.apache.kafka.common.serialization.Deserializer interface.
     */
    private String keySerializer;


    /**
     * Deserializer class for value that implements the org.apache.kafka.common.serialization.Deserializer interface.
     */
    private String valueSerializer;

    /**
     * The number of acknowledgments the producer requires the leader to have received before considering a request complete.
     * This controls the durability of records that are sent. The following settings are allowed:
     *
     * acks=0 If set to zero then the producer will not wait for any acknowledgment from the server at all. The record
     * will be immediately added to the socket buffer and considered sent. No guarantee can be made that the server has
     * received the record in this case, and the retries configuration will not take effect (as the client won't generally
     * know of any failures). The offset given back for each record will always be set to -1.
     *
     * acks=1 This will mean the leader will write the record to its local log but will respond without awaiting full
     * acknowledgement from all followers. In this case should the leader fail immediately after acknowledging the record
     * but before the followers have replicated it then the record will be lost.
     *
     * acks=all This means the leader will wait for the full set of in-sync replicas to acknowledge the record. This guarantees
     * that the record will not be lost as long as at least one in-sync replica remains alive. This is the strongest available
     * guarantee. This is equivalent to the acks=-1 setting.
     *
     * Default:	1
     * Valid Values:	[all, -1, 0, 1]
     */
    private String acks;

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
     * The total bytes of memory the producer can use to buffer records waiting to be sent to the server. If records are
     * sent faster than they can be delivered to the server the producer will block for max.block.ms after which it will
     * throw an exception.
     *
     * This setting should correspond roughly to the total memory the producer will use, but is not a hard bound since not
     * all memory the producer uses is used for buffering. Some additional memory will be used for compression (if compression
     * is enabled) as well as for maintaining in-flight requests.
     *
     * Default:	33554432
     */
    private Long bufferMemory;

    /**
     * The compression type for all data generated by the producer. The default is none (i.e. no compression). Valid values are
     * none, gzip, snappy, lz4, or zstd. Compression is of full batches of data, so the efficacy of batching will also impact
     * the compression ratio (more batching means better compression).
     *
     * Default:	none
     */
    private String compressionType;

    /**
     * Setting a value greater than zero will cause the client to resend any record whose send fails with a potentially
     * transient error. Note that this retry is no different than if the client resent the record upon receiving the error.
     * Allowing retries without setting max.in.flight.requests.per.connection to 1 will potentially change the ordering
     * of records because if two batches are sent to a single partition, and the first fails and is retried but the second
     * succeeds, then the records in the second batch may appear first. Note additionally that produce requests will be
     * failed before the number of retries has been exhausted if the timeout configured by delivery.timeout.ms expires
     * first before successful acknowledgement. Users should generally prefer to leave this config unset and instead use
     * delivery.timeout.ms to control retry behavior.
     *
     * Default:	2147483647
     * Valid Values:	[0,...,2147483647]
     */
    private Integer retries;

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
     * The producer will attempt to batch records together into fewer requests whenever multiple records are being sent
     * to the same partition. This helps performance on both the client and the server. This configuration controls the
     * default batch size in bytes.
     *
     * No attempt will be made to batch records larger than this size.
     *
     * Requests sent to brokers will contain multiple batches, one for each partition with data available to be sent.
     *
     * A small batch size will make batching less common and may reduce throughput (a batch size of zero will disable
     * batching entirely). A very large batch size may use memory a bit more wastefully as we will always allocate a
     * buffer of the specified batch size in anticipation of additional records.
     *
     * Default:	16384
     */
    private Integer batchSize;

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
     * An id string to pass to the server when making requests. The purpose of this is to be able to track the source of requests beyond just
     * ip/port by allowing a logical application name to be included in server-side request logging.
     *
     * Default:	""
     */
    private String clientId;

    /**
     * Close idle connections after the number of milliseconds specified by this config.
     *
     * Default:	540000 (9 minutes)
     */
    private Long connectionsMaxIdleMillis;

    /**
     * An upper bound on the time to report success or failure after a call to send() returns. This limits the total time that
     * a record will be delayed prior to sending, the time to await acknowledgement from the broker (if expected), and the
     * time allowed for retriable send failures. The producer may report failure to send a record earlier than this config if
     * either an unrecoverable error is encountered, the retries have been exhausted, or the record is added to a batch which
     * reached an earlier delivery expiration deadline. The value of this config should be greater than or equal to the sum of
     * request.timeout.ms and linger.ms.
     *
     * Default:	120000 (2 minutes)
     */
    private Long deliveryTimeoutMillis;

    /**
     * The producer groups together any records that arrive in between request transmissions into a single batched request.
     * Normally this occurs only under load when records arrive faster than they can be sent out. However in some circumstances
     * the client may want to reduce the number of requests even under moderate load. This setting accomplishes this by adding
     * a small amount of artificial delay—that is, rather than immediately sending out a record the producer will wait for
     * up to the given delay to allow other records to be sent so that the sends can be batched together. This can be thought
     * of as analogous to Nagle's algorithm in TCP. This setting gives the upper bound on the delay for batching: once we get
     * batch.size worth of records for a partition it will be sent immediately regardless of this setting, however if we have
     * fewer than this many bytes accumulated for this partition we will 'linger' for the specified time waiting for more records
     * to show up. This setting defaults to 0 (i.e. no delay). Setting linger.ms=5, for example, would have the effect of reducing
     * the number of requests sent but would add up to 5ms of latency to records sent in the absence of load.
     *
     * Default:	0
     */
    private Long lingerMillis;

    /**
     * The configuration controls how long KafkaProducer.send() and KafkaProducer.partitionsFor() will block.These methods can
     * be blocked either because the buffer is full or metadata unavailable.Blocking in the user-supplied serializers or
     * partitioner will not be counted against this timeout.
     *
     * Default:	60000 (1 minute)
     */
    private Long maxBlockMillis;

    /**
     * The maximum size of a request in bytes. This setting will limit the number of record batches the producer will send
     * in a single request to avoid sending huge requests. This is also effectively a cap on the maximum uncompressed record
     * batch size. Note that the server has its own cap on the record batch size (after compression if compression is enabled)
     * which may be different from this.
     *
     * Default:	1048576
     */
    private Integer maxRequestSize;

    /**
     * Partitioner class that implements the org.apache.kafka.clients.producer.Partitioner interface.
     *
     * Default:	org.apache.kafka.clients.producer.internals.DefaultPartitioner
     */
    private String partitionerClass;

    /**
     * The size of the TCP receive buffer (SO_RCVBUF) to use when reading data. If the value is -1, the OS default will be used.
     *
     * Default:	32768 (32 kibibytes)
     */
    private Integer receiveBufferBytes;

    /**
     * The configuration controls the maximum amount of time the client will wait for the response of a request. If the response
     * is not received before the timeout elapses the client will resend the request if necessary or fail the request if retries
     * are exhausted. This should be larger than replica.lag.time.max.ms (a broker configuration) to reduce the possibility of
     * message duplication due to unnecessary producer retries.
     *
     * Default:	30000 (30 seconds)
     */
    private Long requestTimeoutMillis;

    /**
     * The fully qualified name of a SASL client callback handler class that implements the AuthenticateCallbackHandler interface.
     */
    private String saslClientCallbackHandlerClass;

    /**
     * JAAS login context parameters for SASL connections in the format used by JAAS configuration files. JAAS configuration
     * file format is described here. The format for the value is: 'loginModuleClass controlFlag (optionName=optionValue)*;'.
     * For brokers, the config must be prefixed with listener prefix and SASL mechanism name in lower-case. For example,
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
     * For example, listener.name.sasl_ssl.scram-sha-256.sasl.login.callback.handler.class=com.example.CustomScramLoginCallbackHandler
     */
    private String saslLoginCallbackHandlerClass;

    /**
     * The fully qualified name of a class that implements the Login interface. For brokers, login config must be prefixed with
     * listener prefix and SASL mechanism name in lower-case. For example,
     * listener.name.sasl_ssl.scram-sha-256.sasl.login.class=com.example.CustomScramLogin
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
     * When set to 'true', the producer will ensure that exactly one copy of each message is written in the stream.
     * If 'false', producer retries due to broker failures, etc., may write duplicates of the retried message in the stream.
     * Note that enabling idempotence requires max.in.flight.requests.per.connection to be less than or equal to 5, retries
     * to be greater than 0 and acks must be 'all'. If these values are not explicitly set by the user, suitable values
     * will be chosen. If incompatible values are set, a ConfigException will be thrown.
     *
     * Default:	false
     */
    private Boolean enableIdempotence;

    /**
     * A list of classes to use as interceptors. Implementing the org.apache.kafka.clients.producer.ProducerInterceptor
     * interface allows you to intercept (and possibly mutate) the records received by the producer before they are
     * published to the Kafka cluster. By default, there are no interceptors.
     */
    private List<String> interceptorClasses;

    /**
     * The maximum number of unacknowledged requests the client will send on a single connection before blocking. Note that
     * if this setting is set to be greater than 1 and there are failed sends, there is a risk of message re-ordering due to
     * retries (i.e., if retries are enabled).
     *
     * Default:	5
     */
    private Integer maxInFlightRequestsPerConnection;

    /**
     * The period of time in milliseconds after which we force a refresh of metadata even if we haven't seen any partition
     * leadership changes to proactively discover any new brokers or partitions.
     *
     * Default:	300000 (5 minutes)
     */
    private Long metadataMaxAgeMillis;

    /**
     * Controls how long the producer will cache metadata for a topic that's idle. If the elapsed time since a topic was
     * last produced to exceeds the metadata idle duration, then the topic's metadata is forgotten and the next access to
     * it will force a metadata fetch request.
     *
     * Default:	300000 (5 minutes)
     */
    private List metadataMaxIdleMillis;

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

    /**
     * The maximum amount of time in ms that the transaction coordinator will wait for a transaction status update from the producer
     * before proactively aborting the ongoing transaction.If this value is larger than the transaction.max.timeout.ms setting in the
     * broker, the request will fail with a InvalidTransactionTimeout error.
     *
     * Default:	60000 (1 minute)
     */
    private Long transactionTimeoutMillis;

    /**
     * The TransactionalId to use for transactional delivery. This enables reliability semantics which span multiple producer
     * sessions since it allows the client to guarantee that transactions using the same TransactionalId have been completed
     * prior to starting any new transactions. If no TransactionalId is provided, then the producer is limited to idempotent
     * delivery. If a TransactionalId is configured, enable.idempotence is implied. By default the TransactionId is not configured,
     * which means transactions cannot be used. Note that, by default, transactions require a cluster of at least three brokers
     * which is the recommended setting for production; for development you can change this, by adjusting broker setting
     * transaction.state.log.replication.factor.
     */
    private String transactionalId;

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getKeySerializer() {
        return keySerializer;
    }

    public void setKeySerializer(String keySerializer) {
        this.keySerializer = keySerializer;
    }

    public String getValueSerializer() {
        return valueSerializer;
    }

    public void setValueSerializer(String valueSerializer) {
        this.valueSerializer = valueSerializer;
    }

    public String getAcks() {
        return acks;
    }

    public void setAcks(String acks) {
        this.acks = acks;
    }

    public List<String> getBootstrapServers() {
        return bootstrapServers;
    }

    public void setBootstrapServers(List<String> bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public Long getBufferMemory() {
        return bufferMemory;
    }

    public void setBufferMemory(Long bufferMemory) {
        this.bufferMemory = bufferMemory;
    }

    public String getCompressionType() {
        return compressionType;
    }

    public void setCompressionType(String compressionType) {
        this.compressionType = compressionType;
    }

    public Integer getRetries() {
        return retries;
    }

    public void setRetries(Integer retries) {
        this.retries = retries;
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

    public Integer getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(Integer batchSize) {
        this.batchSize = batchSize;
    }

    public String getClientDnsLookup() {
        return clientDnsLookup;
    }

    public void setClientDnsLookup(String clientDnsLookup) {
        this.clientDnsLookup = clientDnsLookup;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public Long getConnectionsMaxIdleMillis() {
        return connectionsMaxIdleMillis;
    }

    public void setConnectionsMaxIdleMillis(Long connectionsMaxIdleMillis) {
        this.connectionsMaxIdleMillis = connectionsMaxIdleMillis;
    }

    public Long getDeliveryTimeoutMillis() {
        return deliveryTimeoutMillis;
    }

    public void setDeliveryTimeoutMillis(Long deliveryTimeoutMillis) {
        this.deliveryTimeoutMillis = deliveryTimeoutMillis;
    }

    public Long getLingerMillis() {
        return lingerMillis;
    }

    public void setLingerMillis(Long lingerMillis) {
        this.lingerMillis = lingerMillis;
    }

    public Long getMaxBlockMillis() {
        return maxBlockMillis;
    }

    public void setMaxBlockMillis(Long maxBlockMillis) {
        this.maxBlockMillis = maxBlockMillis;
    }

    public Integer getMaxRequestSize() {
        return maxRequestSize;
    }

    public void setMaxRequestSize(Integer maxRequestSize) {
        this.maxRequestSize = maxRequestSize;
    }

    public String getPartitionerClass() {
        return partitionerClass;
    }

    public void setPartitionerClass(String partitionerClass) {
        this.partitionerClass = partitionerClass;
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

    public Boolean getEnableIdempotence() {
        return enableIdempotence;
    }

    public void setEnableIdempotence(Boolean enableIdempotence) {
        this.enableIdempotence = enableIdempotence;
    }

    public List<String> getInterceptorClasses() {
        return interceptorClasses;
    }

    public void setInterceptorClasses(List<String> interceptorClasses) {
        this.interceptorClasses = interceptorClasses;
    }

    public Integer getMaxInFlightRequestsPerConnection() {
        return maxInFlightRequestsPerConnection;
    }

    public void setMaxInFlightRequestsPerConnection(Integer maxInFlightRequestsPerConnection) {
        this.maxInFlightRequestsPerConnection = maxInFlightRequestsPerConnection;
    }

    public Long getMetadataMaxAgeMillis() {
        return metadataMaxAgeMillis;
    }

    public void setMetadataMaxAgeMillis(Long metadataMaxAgeMillis) {
        this.metadataMaxAgeMillis = metadataMaxAgeMillis;
    }

    public List getMetadataMaxIdleMillis() {
        return metadataMaxIdleMillis;
    }

    public void setMetadataMaxIdleMillis(List metadataMaxIdleMillis) {
        this.metadataMaxIdleMillis = metadataMaxIdleMillis;
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

    public Long getTransactionTimeoutMillis() {
        return transactionTimeoutMillis;
    }

    public void setTransactionTimeoutMillis(Long transactionTimeoutMillis) {
        this.transactionTimeoutMillis = transactionTimeoutMillis;
    }

    public String getTransactionalId() {
        return transactionalId;
    }

    public void setTransactionalId(String transactionalId) {
        this.transactionalId = transactionalId;
    }

    public Map<String, Object> getProducerProps() throws ClassNotFoundException {
        Map<String, Object> props = new HashMap<>();

        if(this.getKeySerializer() != null && !this.getKeySerializer().isEmpty()) {
            props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, Class.forName(this.getKeySerializer()));
        }

        if(this.getValueSerializer() != null && !this.getValueSerializer().isEmpty()) {
            props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, Class.forName(this.getValueSerializer()));
        }

        if(this.getAcks() != null && !this.getAcks().isEmpty()) {
            props.put(ProducerConfig.ACKS_CONFIG, this.getAcks());
        }

        if(this.getTransactionalId() != null && !this.getTransactionalId().isEmpty()) {
            props.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, this.getTransactionalId());
        }

        if(this.getBootstrapServers() != null && !this.getBootstrapServers().isEmpty()) {
            props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, this.getBootstrapServers());
        }

        if(this.getClientDnsLookup() != null && !this.getClientDnsLookup().isEmpty()) {
            props.put(ProducerConfig.CLIENT_DNS_LOOKUP_CONFIG, this.getClientDnsLookup());
        }

        if(this.getClientId() != null && !this.getClientId().isEmpty()) {
            props.put(ProducerConfig.CLIENT_ID_CONFIG, this.getClientId());
        }

        if(this.getConnectionsMaxIdleMillis() != null) {
            props.put(ProducerConfig.CONNECTIONS_MAX_IDLE_MS_CONFIG, this.getConnectionsMaxIdleMillis());
        }

        if(this.getMetadataMaxAgeMillis() != null) {
            props.put(ProducerConfig.METADATA_MAX_AGE_CONFIG, this.getMetadataMaxAgeMillis());
        }

        if(this.getMetricReporters() != null && !this.getMetricReporters().isEmpty()) {
            props.put(ProducerConfig.METRIC_REPORTER_CLASSES_CONFIG, this.getMetricReporters());
        }

        if(this.getMetricsNumSamples() != null) {
            props.put(ProducerConfig.METRICS_NUM_SAMPLES_CONFIG, this.getMetricsNumSamples());
        }

        if(this.getMetricsRecordingLevel() != null && !this.getMetricsRecordingLevel().isEmpty()) {
            props.put(ProducerConfig.METRICS_RECORDING_LEVEL_CONFIG, this.getMetricsRecordingLevel());
        }

        if(this.getMetricsSampleWindowMillis() != null) {
            props.put(ProducerConfig.METRICS_SAMPLE_WINDOW_MS_CONFIG, this.getMetricsSampleWindowMillis());
        }

        if(this.getReceiveBufferBytes() != null) {
            props.put(ProducerConfig.RECEIVE_BUFFER_CONFIG, this.getReceiveBufferBytes());
        }

        if(this.getReconnectBackoffMaxMillis() != null) {
            props.put(ProducerConfig.RECONNECT_BACKOFF_MAX_MS_CONFIG, this.getReconnectBackoffMaxMillis());
        }

        if(this.getReconnectBackoffMillis() != null) {
            props.put(ProducerConfig.RECONNECT_BACKOFF_MS_CONFIG, this.getReconnectBackoffMillis());
        }

        if(this.getRequestTimeoutMillis() != null) {
            props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, this.getRequestTimeoutMillis());
        }

        if(this.getRetryBackoffMillis() != null) {
            props.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, this.getRetryBackoffMillis());
        }

        if(this.getSecurityProviders() != null && !this.getSecurityProviders().isEmpty()) {
            props.put(ProducerConfig.SECURITY_PROVIDERS_CONFIG, this.getSecurityProviders());
        }

        if(this.getSendBufferBytes() != null) {
            props.put(ProducerConfig.SEND_BUFFER_CONFIG, this.getSendBufferBytes());
        }

        return props;
    }
}

