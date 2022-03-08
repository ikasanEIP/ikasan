package org.ikasan.component.endpoint.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

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

        url += "&ssl=" + falseIfNull(configuration.getSslEnabled());

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
