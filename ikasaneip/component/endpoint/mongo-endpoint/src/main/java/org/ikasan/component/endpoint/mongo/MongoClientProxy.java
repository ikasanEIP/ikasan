package org.ikasan.component.endpoint.mongo;

import com.mongodb.MongoClient;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.management.ManagedResource;
import org.ikasan.spec.management.ManagedResourceRecoveryManager;

import java.util.HashSet;
import java.util.Set;

/**
 * MongoClient proxy for injecting into a MongoComponent
 * Allows for a single MongoClient to be shared among components, but
 * have multiple MongoComponents safely manage its lifecycle
 */
public class MongoClientProxy implements ManagedResource, ConfiguredResource<MongoClientConfiguration>
{
    private MongoClient mongoClient;

    private MongoClientConfiguration mongoClientConfiguration;

    private String configurationId;

    private volatile boolean isStarted = false;

    private static final Object lock = new Object();

    private final Set<MongoComponent> clients = new HashSet<>();

    @Override
    public void startManagedResource()
    {
        synchronized (lock)
        {
            if (isStarted)
            {
                // dont keep starting the same resource
                return;
            }
            if (mongoClient == null)
            {
                mongoClient = MongoClientFactory.getMongoClient(mongoClientConfiguration);
            }
            isStarted = true;
        }
    }

    @Override
    public void stopManagedResource()
    {
        synchronized (lock)
        {
            if (!isStarted || !clients.isEmpty())
            {
                return;
            }
            if (mongoClient != null)
            {
                mongoClient.close();
                mongoClient = null;
            }
            isStarted = false;
        }
    }

    /**
     * Called by MongoComponent instances when using the proxy to register their
     * interest in the underlying MongoClient.
     * @param mongoComponent the interested client
     */
    void start(MongoComponent mongoComponent)
    {
        synchronized (clients)
        {
            if (!clients.contains(mongoComponent))
            {
                clients.add(mongoComponent);
            }
        }
        startManagedResource();
    }

    /**
     * Called by MongoComponent instances when using the proxy to stop the underlying MongoClient.
     * @param mongoComponent the interested client
     */
    void stop(MongoComponent mongoComponent)
    {
        synchronized (clients)
        {
            clients.remove(mongoComponent);
        }
        if (clients.isEmpty())
        {
            stopManagedResource();
        }
    }

    @Override
    public void setManagedResourceRecoveryManager(ManagedResourceRecoveryManager managedResourceRecoveryManager)
    {
        // do nothing
    }

    @Override
    public boolean isCriticalOnStartup()
    {
        return false;
    }

    @Override
    public void setCriticalOnStartup(boolean criticalOnStartup)
    {
    }


    @Override
    public String getConfiguredResourceId()
    {
        return configurationId;
    }

    @Override
    public void setConfiguredResourceId(String id)
    {
        configurationId = id;
    }

    @Override
    public MongoClientConfiguration getConfiguration()
    {
        return mongoClientConfiguration;
    }

    @Override
    public void setConfiguration(MongoClientConfiguration configuration)
    {
        mongoClientConfiguration = configuration;
    }

    public MongoClient getMongoClient()
    {
        return mongoClient;
    }

    public boolean isStarted()
    {
        return isStarted;
    }


}
