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
package org.ikasan.component.endpoint.mongo;

import com.mongodb.*;

import org.apache.log4j.Logger;
import org.bson.BSON;
import org.bson.Transformer;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.management.ManagedResource;
import org.ikasan.spec.management.ManagedResourceRecoveryManager;

import java.util.*;

/**
 * Abstract Mongo component which can be managed and configured.
 * 
 * @author Ikasan Development Team
 */
public abstract class MongoComponent implements ManagedResource, ConfiguredResource<MongoClientConfiguration>
{
    /** logger instance */
    private static Logger logger = Logger.getLogger(MongoComponent.class);

    /** configured resource id */
    private String configuredResourceId;

    /** configuration */
    protected MongoClientConfiguration configuration;

    /** is this a critical resource for startup of the flow */
    private boolean isCriticalOnStartup;

    /** instantiated mongoclient */
    protected MongoClient mongoClient;

    /** optional proxy for sharing a Mongo client*/
    protected MongoClientProxy mongoClientProxy;

    /** handle to all referenced collections */
    protected Map<String, DBCollection> collections;

    /** handle to mongoDB instance */
    protected DB mongoDatabase;

    /** set if the setter on MongoClient is used (so we don't close it) */
    protected boolean mongoClientSet = false;

    /**
     * bson transformers that will be used when encoding from java types to bson
     **/
    protected Map<Class<?>, List<Transformer>> bsonEncodingTransformerMap;

    public static void setLogger(Logger logger)
    {
        MongoComponent.logger = logger;
    }

    public void setCollections(Map<String, DBCollection> collections)
    {
        this.collections = collections;
    }

    public void setMongoDatabase(DB mongoDatabase)
    {
        this.mongoDatabase = mongoDatabase;
    }

    public void setBsonEncodingTransformerMap(Map<Class<?>, List<Transformer>> bsonEncodingTransformerMap)
    {
        this.bsonEncodingTransformerMap = bsonEncodingTransformerMap;
    }

    @Override
    public void startManagedResource()
    {
        // if we have a proxy, use it, register interest in its lifecycle until Ikasan framework allows ManagedResource at the flow or module level
        if (mongoClientProxy != null)
        {
            if (mongoClientProxy.getConfiguration() == null)
            {
                mongoClientProxy.setConfiguration(configuration);
                mongoClientProxy.setConfiguredResourceId(configuredResourceId);
            }
            mongoClientProxy.start(this);
            mongoClient = mongoClientProxy.getMongoClient();
        }
        else if (mongoClient == null)
        {
            mongoClient = MongoClientFactory.getMongoClient(configuration);
        }

        mongoDatabase = mongoClient.getDB(configuration.getDatabaseName());
        collections = new HashMap<>();
        for (Map.Entry<String, String> entry : configuration.getCollectionNames().entrySet())
        {
            DBCollection dbCollection = mongoDatabase.getCollection(entry.getValue());
            if (dbCollection == null)
            {
                throw new RuntimeException("DBCollection[" + entry.getValue() + "] not found in database["
                        + configuration.getDatabaseName() + "]");
            }
            collections.put(entry.getKey(), dbCollection);
        }
        addEncodingHooks();
    }

    @Override
    public void stopManagedResource()
    {
        if (mongoClientProxy != null)
        {
            // use the proxy to manage the lifecycle
            mongoClientProxy.stop(this);
            mongoClient = null;
        }
        else
        {
            if (this.mongoClient != null && !mongoClientSet)
            {
                this.mongoClient.close();
                this.mongoClient = null;
            }
        }
    }

    /**
     * Customise java to BSON encoding.
     */
    private void addEncodingHooks()
    {
        if (bsonEncodingTransformerMap != null)
        {
            for (Class<?> c : bsonEncodingTransformerMap.keySet())
            {
                List<Transformer> transformers = bsonEncodingTransformerMap.get(c);
                for(Transformer transformer : transformers){
                    logger.debug(String.format("Adding bsonEncodingTransfomer [%1$s] for class [%2$s]", transformer, c));
                    BSON.addEncodingHook(c, transformer);
                }
            }
        }
    }


    @Override
    public void setManagedResourceRecoveryManager(ManagedResourceRecoveryManager managedResourceRecoveryManager)
    {
        // do we need this ?
    }

    @Override
    public boolean isCriticalOnStartup()
    {
        return this.isCriticalOnStartup;
    }

    @Override
    public void setCriticalOnStartup(boolean isCriticalOnStartup)
    {
        this.isCriticalOnStartup = isCriticalOnStartup;
    }

    @Override
    public String getConfiguredResourceId()
    {
        return this.configuredResourceId;
    }

    @Override
    public void setConfiguredResourceId(String configuredResourceId)
    {
        this.configuredResourceId = configuredResourceId;
    }

    @Override
    public MongoClientConfiguration getConfiguration()
    {
        return this.configuration;
    }

    @Override
    public void setConfiguration(MongoClientConfiguration configuration)
    {
        this.configuration = configuration;
    }

    public MongoClientProxy getMongoClientProxy()
    {
        return mongoClientProxy;
    }

    public void setMongoClientProxy(MongoClientProxy mongoClientProxy)
    {
        this.mongoClientProxy = mongoClientProxy;
    }

    public MongoClient getMongoClient()
    {
        return mongoClient;
    }

    public void setMongoClient(MongoClient mongoClient)
    {
        this.mongoClient = mongoClient;
        this.mongoClientSet = true;
    }
}
