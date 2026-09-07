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
package org.ikasan.component.endpoint.mongo5;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.ikasan.component.endpoint.mongo5.MongoClientConfiguration;
import org.ikasan.component.endpoint.mongo5.MongoClientProxy;
import org.ikasan.component.endpoint.mongo5.MongoComponent;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Unit tests for MongoComponent (abstract class tested via concrete implementation)
 *
 * @author Ikasan Development Team
 */
public class MongoComponentTest
{
    private TestMongoComponent mongoComponent;
    private MongoClientConfiguration configuration;

    /**
     * Concrete implementation of MongoComponent for testing
     */
    private static class TestMongoComponent extends MongoComponent<MongoClientConfiguration>
    {
        // Empty implementation for testing
    }

    @Before
    public void setUp()
    {
        mongoComponent = new TestMongoComponent();

        configuration = new MongoClientConfiguration();
        configuration.setConnectionUrls(Arrays.asList("localhost:27017"));
        configuration.setDatabaseName("testDb");

        Map<String, String> collections = new HashMap<>();
        collections.put("messages", "message_collection");
        configuration.setCollectionNames(collections);

        mongoComponent.setConfiguration(configuration);
    }

    /**
     * Test initial state
     */
    @Test
    public void testInitialState()
    {
        TestMongoComponent component = new TestMongoComponent();

        Assert.assertNull("MongoClient should be null initially", component.getMongoClient());
        Assert.assertNull("Configuration should be null initially", component.getConfiguration());
        Assert.assertNull("Configured resource ID should be null initially", component.getConfiguredResourceId());
        Assert.assertFalse("Should not be critical on startup by default", component.isCriticalOnStartup());
    }

    /**
     * Test configuration setters and getters
     */
    @Test
    public void testConfigurationSettersAndGetters()
    {
        mongoComponent.setConfiguration(configuration);
        Assert.assertEquals("Configuration should match", configuration, mongoComponent.getConfiguration());

        mongoComponent.setConfiguredResourceId("testId");
        Assert.assertEquals("Configured resource ID should match", "testId", mongoComponent.getConfiguredResourceId());
    }

    /**
     * Test criticalOnStartup flag
     */
    @Test
    public void testCriticalOnStartup()
    {
        Assert.assertFalse("Should not be critical by default", mongoComponent.isCriticalOnStartup());

        mongoComponent.setCriticalOnStartup(true);
        Assert.assertTrue("Should be critical", mongoComponent.isCriticalOnStartup());

        mongoComponent.setCriticalOnStartup(false);
        Assert.assertFalse("Should not be critical", mongoComponent.isCriticalOnStartup());
    }

    /**
     * Test setMongoClient marks client as externally set
     */
    @Test
    public void testSetMongoClient()
    {
        MongoClient mockClient = Mockito.mock(MongoClient.class);
        MongoDatabase mockDatabase = Mockito.mock(MongoDatabase.class);
        MongoCollection<Document> mockCollection = Mockito.mock(MongoCollection.class);

        Mockito.when(mockClient.getDatabase("testDb")).thenReturn(mockDatabase);
        Mockito.when(mockDatabase.getCollection("message_collection")).thenReturn(mockCollection);

        mongoComponent.setMongoClient(mockClient);

        Assert.assertEquals("MongoClient should match", mockClient, mongoComponent.getMongoClient());
    }

    /**
     * Test startManagedResource creates MongoClient when not set
     */
    @Test
    public void testStartManagedResourceCreatesMongoClient()
    {
        mongoComponent.startManagedResource();

        Assert.assertNotNull("MongoClient should be created", mongoComponent.getMongoClient());
    }

    /**
     * Test startManagedResource with proxy
     */
    @Test
    public void testStartManagedResourceWithProxy()
    {
        MongoClientProxy mockProxy = Mockito.mock(MongoClientProxy.class);
        MongoClient mockClient = Mockito.mock(MongoClient.class);
        MongoDatabase mockDatabase = Mockito.mock(MongoDatabase.class);
        MongoCollection<Document> mockCollection = Mockito.mock(MongoCollection.class);

        Mockito.when(mockProxy.getConfiguration()).thenReturn(null);
        Mockito.when(mockProxy.getMongoClient()).thenReturn(mockClient);
        Mockito.when(mockClient.getDatabase("testDb")).thenReturn(mockDatabase);
        Mockito.when(mockDatabase.getCollection("message_collection")).thenReturn(mockCollection);

        mongoComponent.setMongoClientProxy(mockProxy);
        mongoComponent.startManagedResource();

        Mockito.verify(mockProxy).start(mongoComponent);
        Mockito.verify(mockProxy).setConfiguration(configuration);
        Assert.assertEquals("MongoClient should come from proxy", mockClient, mongoComponent.getMongoClient());
    }

    /**
     * Test startManagedResource with already configured proxy
     */
    @Test
    public void testStartManagedResourceWithConfiguredProxy()
    {
        MongoClientProxy mockProxy = Mockito.mock(MongoClientProxy.class);
        MongoClient mockClient = Mockito.mock(MongoClient.class);
        MongoDatabase mockDatabase = Mockito.mock(MongoDatabase.class);
        MongoCollection<Document> mockCollection = Mockito.mock(MongoCollection.class);

        Mockito.when(mockProxy.getConfiguration()).thenReturn(configuration);
        Mockito.when(mockProxy.getMongoClient()).thenReturn(mockClient);
        Mockito.when(mockClient.getDatabase("testDb")).thenReturn(mockDatabase);
        Mockito.when(mockDatabase.getCollection("message_collection")).thenReturn(mockCollection);

        mongoComponent.setMongoClientProxy(mockProxy);
        mongoComponent.startManagedResource();

        Mockito.verify(mockProxy).start(mongoComponent);
        Mockito.verify(mockProxy, Mockito.never()).setConfiguration(Mockito.any());
        Assert.assertEquals("MongoClient should come from proxy", mockClient, mongoComponent.getMongoClient());
    }

    /**
     * Test startManagedResource initializes collections
     */
    @Test
    public void testStartManagedResourceInitializesCollections()
    {
        MongoClient mockClient = Mockito.mock(MongoClient.class);
        MongoDatabase mockDatabase = Mockito.mock(MongoDatabase.class);
        MongoCollection<Document> mockCollection = Mockito.mock(MongoCollection.class);

        Mockito.when(mockClient.getDatabase("testDb")).thenReturn(mockDatabase);
        Mockito.when(mockDatabase.getCollection("message_collection")).thenReturn(mockCollection);

        mongoComponent.setMongoClient(mockClient);
        mongoComponent.startManagedResource();

        Mockito.verify(mockClient).getDatabase("testDb");
        Mockito.verify(mockDatabase).getCollection("message_collection");
    }

    /**
     * Test startManagedResource throws exception when collection not found
     */
    @Test(expected = RuntimeException.class)
    public void testStartManagedResourceThrowsExceptionWhenCollectionNotFound()
    {
        MongoClient mockClient = Mockito.mock(MongoClient.class);
        MongoDatabase mockDatabase = Mockito.mock(MongoDatabase.class);

        Mockito.when(mockClient.getDatabase("testDb")).thenReturn(mockDatabase);
        Mockito.when(mockDatabase.getCollection("message_collection")).thenReturn(null);

        mongoComponent.setMongoClient(mockClient);
        mongoComponent.startManagedResource();
    }

    /**
     * Test stopManagedResource closes client when set internally
     */
    @Test
    public void testStopManagedResourceClosesInternalClient()
    {
        mongoComponent.startManagedResource();
        MongoClient client = mongoComponent.getMongoClient();
        Assert.assertNotNull("Client should exist", client);

        mongoComponent.stopManagedResource();

        Assert.assertNull("MongoClient should be null after stop", mongoComponent.getMongoClient());
    }

    /**
     * Test stopManagedResource does not close externally set client
     */
    @Test
    public void testStopManagedResourceDoesNotCloseExternalClient()
    {
        MongoClient mockClient = Mockito.mock(MongoClient.class);
        MongoDatabase mockDatabase = Mockito.mock(MongoDatabase.class);
        MongoCollection<Document> mockCollection = Mockito.mock(MongoCollection.class);

        Mockito.when(mockClient.getDatabase("testDb")).thenReturn(mockDatabase);
        Mockito.when(mockDatabase.getCollection("message_collection")).thenReturn(mockCollection);

        mongoComponent.setMongoClient(mockClient);
        mongoComponent.startManagedResource();

        mongoComponent.stopManagedResource();

        Mockito.verify(mockClient, Mockito.never()).close();
    }

    /**
     * Test stopManagedResource with proxy
     */
    @Test
    public void testStopManagedResourceWithProxy()
    {
        MongoClientProxy mockProxy = Mockito.mock(MongoClientProxy.class);
        MongoClient mockClient = Mockito.mock(MongoClient.class);
        MongoDatabase mockDatabase = Mockito.mock(MongoDatabase.class);
        MongoCollection<Document> mockCollection = Mockito.mock(MongoCollection.class);

        Mockito.when(mockProxy.getConfiguration()).thenReturn(configuration);
        Mockito.when(mockProxy.getMongoClient()).thenReturn(mockClient);
        Mockito.when(mockClient.getDatabase("testDb")).thenReturn(mockDatabase);
        Mockito.when(mockDatabase.getCollection("message_collection")).thenReturn(mockCollection);

        mongoComponent.setMongoClientProxy(mockProxy);
        mongoComponent.startManagedResource();

        mongoComponent.stopManagedResource();

        Mockito.verify(mockProxy).stop(mongoComponent);
        Assert.assertNull("MongoClient should be null after stop", mongoComponent.getMongoClient());
    }

    /**
     * Test setManagedResourceRecoveryManager does nothing
     */
    @Test
    public void testSetManagedResourceRecoveryManager()
    {
        // Should not throw exception
        mongoComponent.setManagedResourceRecoveryManager(null);
    }

    /**
     * Test proxy getter and setter
     */
    @Test
    public void testMongoClientProxyGetterAndSetter()
    {
        MongoClientProxy mockProxy = Mockito.mock(MongoClientProxy.class);

        mongoComponent.setMongoClientProxy(mockProxy);
        Assert.assertEquals("Proxy should match", mockProxy, mongoComponent.getMongoClientProxy());
    }

    /**
     * Test setCollections
     */
    @Test
    public void testSetCollections()
    {
        Map<String, MongoCollection<Document>> collections = new HashMap<>();
        MongoCollection<Document> mockCollection = Mockito.mock(MongoCollection.class);
        collections.put("test", mockCollection);

        mongoComponent.setCollections(collections);

        // Cannot directly verify as collections is protected, but method should not throw
    }

    /**
     * Test setMongoDatabase
     */
    @Test
    public void testSetMongoDatabase()
    {
        MongoDatabase mockDatabase = Mockito.mock(MongoDatabase.class);

        mongoComponent.setMongoDatabase(mockDatabase);

        // Cannot directly verify as mongoDatabase is protected, but method should not throw
    }

    /**
     * Test setBsonEncodingTransformerMap
     */
    @Test
    public void testSetBsonEncodingTransformerMap()
    {
        Map mockMap = Mockito.mock(Map.class);

        mongoComponent.setBsonEncodingTransformerMap(mockMap);

        // Cannot directly verify as bsonEncodingTransformerMap is protected, but method should not throw
    }

    /**
     * Test startManagedResource with multiple collections
     */
    @Test
    public void testStartManagedResourceWithMultipleCollections()
    {
        Map<String, String> collections = new HashMap<>();
        collections.put("messages", "message_collection");
        collections.put("errors", "error_collection");
        collections.put("audit", "audit_collection");
        configuration.setCollectionNames(collections);

        MongoClient mockClient = Mockito.mock(MongoClient.class);
        MongoDatabase mockDatabase = Mockito.mock(MongoDatabase.class);
        MongoCollection<Document> mockCollection1 = Mockito.mock(MongoCollection.class);
        MongoCollection<Document> mockCollection2 = Mockito.mock(MongoCollection.class);
        MongoCollection<Document> mockCollection3 = Mockito.mock(MongoCollection.class);

        Mockito.when(mockClient.getDatabase("testDb")).thenReturn(mockDatabase);
        Mockito.when(mockDatabase.getCollection("message_collection")).thenReturn(mockCollection1);
        Mockito.when(mockDatabase.getCollection("error_collection")).thenReturn(mockCollection2);
        Mockito.when(mockDatabase.getCollection("audit_collection")).thenReturn(mockCollection3);

        mongoComponent.setMongoClient(mockClient);
        mongoComponent.startManagedResource();

        Mockito.verify(mockDatabase).getCollection("message_collection");
        Mockito.verify(mockDatabase).getCollection("error_collection");
        Mockito.verify(mockDatabase).getCollection("audit_collection");
    }

    /**
     * Test start and stop lifecycle
     */
    @Test
    public void testStartStopLifecycle()
    {
        mongoComponent.startManagedResource();
        Assert.assertNotNull("MongoClient should be created", mongoComponent.getMongoClient());

        mongoComponent.stopManagedResource();
        Assert.assertNull("MongoClient should be null after stop", mongoComponent.getMongoClient());

        // Should be able to start again
        mongoComponent.startManagedResource();
        Assert.assertNotNull("MongoClient should be created again", mongoComponent.getMongoClient());
    }
}
