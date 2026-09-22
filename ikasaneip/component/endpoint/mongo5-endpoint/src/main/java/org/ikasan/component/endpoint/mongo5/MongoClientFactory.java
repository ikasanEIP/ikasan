package org.ikasan.component.endpoint.mongo5;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import java.util.List;
import java.util.Map;

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
        if (configuration == null)
        {
            throw new RuntimeException("Configuration is null!");
        }

        configuration.validate();

        return MongoClients.create(buildUrl(configuration));
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

        String sslPrefix = "&";
        if(!url.contains("?authSource=")) {
            sslPrefix = "?";
        }

        if(configuration.getOptionalConnectionParameters() != null
            && configuration.getOptionalConnectionParameters().containsKey("tsl")
            && configuration.getOptionalConnectionParameters().containsKey("ssl")) {
            url += sslPrefix+"tsl=" + configuration.getOptionalConnectionParameters().get("tsl");
        }
        else if(configuration.getOptionalConnectionParameters() != null
            && !configuration.getOptionalConnectionParameters().containsKey("tsl")
            && configuration.getOptionalConnectionParameters().containsKey("ssl")) {
            url += sslPrefix+"ssl=" + configuration.getOptionalConnectionParameters().get("ssl");
        }
        else if(configuration.getOptionalConnectionParameters() != null
            && configuration.getOptionalConnectionParameters().containsKey("tsl")
            && !configuration.getOptionalConnectionParameters().containsKey("ssl")) {
            url += sslPrefix+"ssl=" + configuration.getOptionalConnectionParameters().get("tsl");
        }
        else {
            url += sslPrefix+"ssl=" + falseIfNull(configuration.getSslEnabled());
        }

        if (configuration.getApplicationName() != null
            && configuration.getOptionalConnectionParameters() != null
            && !configuration.getOptionalConnectionParameters().containsKey("appName"))
        {
            url += "&appName=" + configuration.getApplicationName();
        }

        // Connection pool settings
        if (configuration.getConnectionsPerHost() != null
            && configuration.getOptionalConnectionParameters() != null
            && !configuration.getOptionalConnectionParameters().containsKey("maxPoolSize"))
        {
            url += "&maxPoolSize=" + configuration.getConnectionsPerHost();
        }

        if (configuration.getMinConnectionsPerHost() != null
            && configuration.getOptionalConnectionParameters() != null
            && !configuration.getOptionalConnectionParameters().containsKey("minPoolSize"))
        {
            url += "&minPoolSize=" + configuration.getMinConnectionsPerHost();
        }

        // Timeout settings
        if (configuration.getConnectionTimeout() != null
            && configuration.getOptionalConnectionParameters() != null
            && !configuration.getOptionalConnectionParameters().containsKey("connectTimeoutMS"))
        {
            url += "&connectTimeoutMS=" + configuration.getConnectionTimeout();
        }

        if (configuration.getSocketTimeout() != null
            && configuration.getOptionalConnectionParameters() != null
            && !configuration.getOptionalConnectionParameters().containsKey("socketTimeoutMS"))
        {
            url += "&socketTimeoutMS=" + configuration.getSocketTimeout();
        }

        if (configuration.getMaxWaitTime() != null
            && configuration.getOptionalConnectionParameters() != null
            && !configuration.getOptionalConnectionParameters().containsKey("waitQueueTimeoutMS"))
        {
            url += "&waitQueueTimeoutMS=" + configuration.getMaxWaitTime();
        }

        if (configuration.getMaxConnectionIdleTime() != null
            && configuration.getOptionalConnectionParameters() != null
            && !configuration.getOptionalConnectionParameters().containsKey("maxIdleTimeMS"))
        {
            url += "&maxIdleTimeMS=" + configuration.getMaxConnectionIdleTime();
        }

        if (configuration.getMaxConnectionLifeTime() != null
            && configuration.getOptionalConnectionParameters() != null
            && !configuration.getOptionalConnectionParameters().containsKey("maxLifeTimeMS"))
        {
            url += "&maxLifeTimeMS=" + configuration.getMaxConnectionLifeTime();
        }

        // Heartbeat settings
        if (configuration.getHeartbeatFrequency() != null
            && configuration.getOptionalConnectionParameters() != null
            && !configuration.getOptionalConnectionParameters().containsKey("heartbeatFrequencyMS"))
        {
            url += "&heartbeatFrequencyMS=" + configuration.getHeartbeatFrequency();
        }

        if (configuration.getMinHeartbeatFrequency() != null
            && configuration.getOptionalConnectionParameters() != null
            && !configuration.getOptionalConnectionParameters().containsKey("minHeartbeatFrequencyMS"))
        {
            url += "&minHeartbeatFrequencyMS=" + configuration.getMinHeartbeatFrequency();
        }

        if (configuration.getHeartbeatConnectTimeout() != null
            && configuration.getOptionalConnectionParameters() != null
            && !configuration.getOptionalConnectionParameters().containsKey("serverSelectionTimeoutMS"))
        {
            url += "&serverSelectionTimeoutMS=" + configuration.getHeartbeatConnectTimeout();
        }

        if (configuration.getHeartbeatSocketTimeout() != null
            && configuration.getOptionalConnectionParameters() != null
            && !configuration.getOptionalConnectionParameters().containsKey("heartbeatSocketTimeoutMS"))
        {
            url += "&heartbeatSocketTimeoutMS=" + configuration.getHeartbeatSocketTimeout();
        }

        // Replica set settings
        if (configuration.getRequiredReplicaSetName() != null
            && configuration.getOptionalConnectionParameters() != null
            && !configuration.getOptionalConnectionParameters().containsKey("replicaSet"))
        {
            url += "&replicaSet=" + configuration.getRequiredReplicaSetName();
        }

        if (configuration.getLocalThreshold() != null
            && configuration.getOptionalConnectionParameters() != null
            && !configuration.getOptionalConnectionParameters().containsKey("localThresholdMS"))
        {
            url += "&localThresholdMS=" + configuration.getLocalThreshold();
        }

        // Read/Write concern settings
        if (configuration.getReadPreference() != null
            && configuration.getOptionalConnectionParameters() != null
            && !configuration.getOptionalConnectionParameters().containsKey("readPreference"))
        {
            url += "&readPreference=" + configuration.getReadPreference().getName();
        }

        if (configuration.getWriteConcern() != null)
        {
            if (configuration.getWriteConcern().getWObject() != null
                && configuration.getOptionalConnectionParameters() != null
                && !configuration.getOptionalConnectionParameters().containsKey("w"))
            {
                url += "&w=" + configuration.getWriteConcern().getWObject();
            }
            if (configuration.getWriteConcern().getJournal() != null
                && configuration.getOptionalConnectionParameters() != null
                && !configuration.getOptionalConnectionParameters().containsKey("journal"))
            {
                url += "&journal=" + configuration.getWriteConcern().getJournal();
            }
        }

        // SSL/TLS settings
        if (configuration.getSslInvalidHostNameAllowed() != null && configuration.getSslInvalidHostNameAllowed()
            && configuration.getOptionalConnectionParameters() != null
            && !configuration.getOptionalConnectionParameters().containsKey("tlsAllowInvalidHostnames"))
        {
            url += "&tlsAllowInvalidHostnames=" + configuration.getSslInvalidHostNameAllowed();
        }

        // Append optional connection parameters (these override any previously set values)
        if (configuration.getOptionalConnectionParameters() != null)
        {
            for (Map.Entry<String, String> entry : configuration.getOptionalConnectionParameters().entrySet())
            {
                if(entry.getKey().equals("ssl") || entry.getKey().equals("tsl")) continue;
                url += "&" + entry.getKey() + "=" + entry.getValue();
            }
        }

        return url;
    }

    private static boolean falseIfNull(Boolean bool) {
        if (bool == null) {
            return false;
        }

        return bool;
    }

    private static String getAuthDatabase(MongoClientConfiguration configuration)
    {
        return configuration.getAuthDatabaseName() != null ?
            configuration.getAuthDatabaseName() :
            configuration.getDatabaseName();
    }
}
