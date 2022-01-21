package org.ikasan.component.endpoint.mongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;

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
        configuration.validate();

        String url = buildUrl(configuration);

        MongoClientOptions.Builder mongoClientOptionsBuilder = mongoClientOptionsBuilder(configuration);

        return new MongoClient(new MongoClientURI(url, mongoClientOptionsBuilder));
    }

    private static String buildUrl(MongoClientConfiguration configuration)
    {
        List<String> connectionUrls = configuration.getConnectionUrls();
        if (connectionUrls.size() == 0)
        {
            throw new RuntimeException("No Mongo server addresses specified!");
        }

        String url = "mongodb";

        if (configuration.getSrvRecord() != null && configuration.getSrvRecord())
        {
            url += "+srv";
        }

        url += "://";

        if (configuration.isAuthenticated() != null && configuration.isAuthenticated())
        {
            url += configuration.getUsername() + ":" + configuration.getPassword() + "@";
        }

        url += String.join(",", connectionUrls) + "/" + configuration.getDatabaseName();

        if (configuration.isAuthenticated() != null && configuration.isAuthenticated())
        {
            url += "?authSource=" + getAuthDatabase(configuration);
        }

        return url;
    }

    private static String getAuthDatabase(MongoClientConfiguration configuration)
    {
        return configuration.getAuthDatabaseName() != null ?
                configuration.getAuthDatabaseName() :
                configuration.getDatabaseName();
    }

    /**
     * Mongo option builder method
     */
    private static MongoClientOptions.Builder mongoClientOptionsBuilder(MongoClientConfiguration configuration)
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
        if (configuration.getSslEnabled() != null)
        {
            builder.sslEnabled(configuration.getSslEnabled());
        }
        if (configuration.getSslInvalidHostNameAllowed() != null)
        {
            builder.sslInvalidHostNameAllowed(configuration.getSslInvalidHostNameAllowed());
        }

        return builder;
    }
}
