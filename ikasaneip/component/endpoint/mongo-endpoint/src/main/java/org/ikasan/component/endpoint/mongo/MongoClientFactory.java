package org.ikasan.component.endpoint.mongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

import java.util.Arrays;
import java.util.List;

/**
 * Factory for creating MongoClients, useful when creating and injecting objects through Spring
 */
public class MongoClientFactory
{
    private MongoClientFactory()
    {
        // no instances
    }

    /**
     * Creates and returns a MongoClient based on a configuration.
     * Callers of this method are responsible for the lifecycle management
     * of the returned MongoClient, for example, calling close() when it is no
     * longer required
     * @param configuration the configuration
     * @return a MongoClient
     */
    public static MongoClient getMongoClient(MongoClientConfiguration configuration)
    {
        MongoClient mongoClient;
        configuration.validate();
        List<ServerAddress> addresses = configuration.getServerAddresses();
        if (addresses.size() == 0)
        {
            throw new RuntimeException("No Mongo server addresses specified!");
        }
        MongoClientOptions mongoClientOptions = buildMongoClientOptions(configuration);
        if (configuration.isAuthenticated())
        {
            MongoCredential mongoCredential = MongoCredential.createMongoCRCredential(configuration.getUsername(),
                    configuration.getDatabaseName(), (configuration.getPassword() != null) ? configuration.getPassword()
                            .toCharArray() : null);
            mongoClient = new MongoClient(addresses, Arrays.asList(mongoCredential), mongoClientOptions);
        }
        else
        {
            mongoClient = new MongoClient(addresses, mongoClientOptions);
        }
        return mongoClient;
    }

    /**
     * Mongo option builder method
     */
    private static MongoClientOptions buildMongoClientOptions(MongoClientConfiguration configuration)
    {
        MongoClientOptions.Builder builder = MongoClientOptions.builder();
        if (configuration.getLocalThreshold() != null)
        {
            builder.localThreshold(configuration.getLocalThreshold());
        }
        if (configuration.getAlwaysUseMBeans() != null)
        {
            builder.alwaysUseMBeans(configuration.getAlwaysUseMBeans());
        }
        if (configuration.getConnectionsPerHost() != null)
        {
            builder.connectionsPerHost(configuration.getConnectionsPerHost());
        }
        if (configuration.getConnectionTimeout() != null)
        {
            builder.connectTimeout(configuration.getConnectionTimeout());
        }
        if (configuration.getCursorFinalizerEnabled() != null)
        {
            builder.cursorFinalizerEnabled(configuration.getCursorFinalizerEnabled());
        }
        if (configuration.getDescription() != null)
        {
            builder.description(configuration.getDescription());
        }
        if (configuration.getMinHeartbeatFrequency() != null)
        {
            builder.minHeartbeatFrequency(configuration.getMinHeartbeatFrequency());
        }
        if (configuration.getHeartbeatConnectTimeout() != null)
        {
            builder.heartbeatConnectTimeout(configuration.getHeartbeatConnectTimeout());
        }
        if (configuration.getHeartbeatFrequency() != null)
        {
            builder.heartbeatFrequency(configuration.getHeartbeatFrequency());
        }
        if (configuration.getHeartbeatSocketTimeout() != null)
        {
            builder.heartbeatSocketTimeout(configuration.getHeartbeatSocketTimeout());
        }
        if (configuration.getLegacyDefaults() != null && configuration.getLegacyDefaults())
        {
            builder.legacyDefaults();
        }
        if (configuration.getMaxConnectionIdleTime() != null)
        {
            builder.maxConnectionIdleTime(configuration.getMaxConnectionIdleTime());
        }
        if (configuration.getMaxConnectionLifeTime() != null)
        {
            builder.maxConnectionLifeTime(configuration.getMaxConnectionLifeTime());
        }
        if (configuration.getMaxWaitTime() != null)
        {
            builder.maxWaitTime(configuration.getMaxWaitTime());
        }
        if (configuration.getSocketTimeout() != null)
        {
            builder.socketTimeout(configuration.getSocketTimeout());
        }
        if (configuration.getMinConnectionsPerHost() != null)
        {
            builder.minConnectionsPerHost(configuration.getMinConnectionsPerHost());
        }
        if (configuration.getRequiredReplicaSetName() != null)
        {
            builder.requiredReplicaSetName(configuration.getRequiredReplicaSetName());
        }
        if (configuration.getSocketKeepAlive() != null)
        {
            builder.socketKeepAlive(configuration.getSocketKeepAlive());
        }
        if (configuration.getThreadsAllowedToBlockForConnectionMultiplier() != null)
        {
            builder.threadsAllowedToBlockForConnectionMultiplier(configuration
                    .getThreadsAllowedToBlockForConnectionMultiplier());
        }
        if (configuration.getReadPreference() != null)
        {
            builder.readPreference(configuration.getReadPreference());
        }
        if (configuration.getWriteConcern() != null)
        {
            builder.writeConcern(configuration.getWriteConcern());
        }
        return builder.build();
    }

}
