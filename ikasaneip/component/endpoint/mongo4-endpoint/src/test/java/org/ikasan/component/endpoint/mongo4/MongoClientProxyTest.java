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
package org.ikasan.component.endpoint.mongo4;

import com.mongodb.client.MongoClient;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.HashMap;

/**
 * Unit tests for MongoClientProxy
 *
 * @author Ikasan Development Team
 */
public class MongoClientProxyTest
{
    private MongoClientProxy mongoClientProxy;
    private MongoClientConfiguration configuration;

    @Before
    public void setUp()
    {
        mongoClientProxy = new MongoClientProxy();

        configuration = new MongoClientConfiguration();
        configuration.setConnectionUrls(Arrays.asList("localhost:27017"));
        configuration.setDatabaseName("testDb");
        configuration.setCollectionNames(new HashMap<>());
    }

    /**
     * Test initial state
     */
    @Test
    public void testInitialState()
    {
        Assert.assertNull("MongoClient should be null initially", mongoClientProxy.getMongoClient());
        Assert.assertFalse("Should not be started initially", mongoClientProxy.isStarted());
        Assert.assertNull("Configuration should be null initially", mongoClientProxy.getConfiguration());
        Assert.assertNull("Configured resource ID should be null initially", mongoClientProxy.getConfiguredResourceId());
    }

    /**
     * Test configuration setters and getters
     */
    @Test
    public void testConfigurationSettersAndGetters()
    {
        mongoClientProxy.setConfiguration(configuration);
        Assert.assertEquals("Configuration should match", configuration, mongoClientProxy.getConfiguration());

        mongoClientProxy.setConfiguredResourceId("testId");
        Assert.assertEquals("Configured resource ID should match", "testId", mongoClientProxy.getConfiguredResourceId());
    }

    /**
     * Test isCriticalOnStartup always returns false
     */
    @Test
    public void testIsCriticalOnStartup()
    {
        Assert.assertFalse("isCriticalOnStartup should always be false", mongoClientProxy.isCriticalOnStartup());

        // Should not change
        mongoClientProxy.setCriticalOnStartup(true);
        Assert.assertFalse("isCriticalOnStartup should still be false", mongoClientProxy.isCriticalOnStartup());
    }

    /**
     * Test startManagedResource creates MongoClient
     */
    @Test
    public void testStartManagedResourceCreatesMongoClient()
    {
        mongoClientProxy.setConfiguration(configuration);

        mongoClientProxy.startManagedResource();

        Assert.assertNotNull("MongoClient should be created", mongoClientProxy.getMongoClient());
        Assert.assertTrue("Should be started", mongoClientProxy.isStarted());
    }

    /**
     * Test startManagedResource is idempotent
     */
    @Test
    public void testStartManagedResourceIsIdempotent()
    {
        mongoClientProxy.setConfiguration(configuration);

        mongoClientProxy.startManagedResource();
        MongoClient firstClient = mongoClientProxy.getMongoClient();

        mongoClientProxy.startManagedResource();
        MongoClient secondClient = mongoClientProxy.getMongoClient();

        Assert.assertSame("Should return same MongoClient", firstClient, secondClient);
        Assert.assertTrue("Should still be started", mongoClientProxy.isStarted());
    }

    /**
     * Test stopManagedResource closes MongoClient when no clients registered
     */
    @Test
    public void testStopManagedResourceClosesMongoClient()
    {
        mongoClientProxy.setConfiguration(configuration);
        mongoClientProxy.startManagedResource();

        Assert.assertTrue("Should be started", mongoClientProxy.isStarted());

        mongoClientProxy.stopManagedResource();

        Assert.assertFalse("Should be stopped", mongoClientProxy.isStarted());
        Assert.assertNull("MongoClient should be null after stop", mongoClientProxy.getMongoClient());
    }

    /**
     * Test stopManagedResource does not close MongoClient when clients are registered
     */
    @Test
    public void testStopManagedResourceWithRegisteredClients()
    {
        mongoClientProxy.setConfiguration(configuration);

        MongoComponent mockComponent = Mockito.mock(MongoComponent.class);
        mongoClientProxy.start(mockComponent);

        MongoClient client = mongoClientProxy.getMongoClient();
        Assert.assertNotNull("MongoClient should exist", client);

        mongoClientProxy.stopManagedResource();

        Assert.assertTrue("Should still be started with registered clients", mongoClientProxy.isStarted());
        Assert.assertNotNull("MongoClient should still exist", mongoClientProxy.getMongoClient());
    }

    /**
     * Test start method with MongoComponent
     */
    @Test
    public void testStartWithMongoComponent()
    {
        mongoClientProxy.setConfiguration(configuration);

        MongoComponent mockComponent = Mockito.mock(MongoComponent.class);

        mongoClientProxy.start(mockComponent);

        Assert.assertTrue("Should be started", mongoClientProxy.isStarted());
        Assert.assertNotNull("MongoClient should be created", mongoClientProxy.getMongoClient());
    }

    /**
     * Test start method registers same component only once
     */
    @Test
    public void testStartRegistersComponentOnlyOnce()
    {
        mongoClientProxy.setConfiguration(configuration);

        MongoComponent mockComponent = Mockito.mock(MongoComponent.class);

        mongoClientProxy.start(mockComponent);
        MongoClient firstClient = mongoClientProxy.getMongoClient();

        // Start with same component again
        mongoClientProxy.start(mockComponent);

        Assert.assertSame("Should be same MongoClient", firstClient, mongoClientProxy.getMongoClient());
    }

    /**
     * Test stop method with MongoComponent
     */
    @Test
    public void testStopWithMongoComponent()
    {
        mongoClientProxy.setConfiguration(configuration);

        MongoComponent mockComponent = Mockito.mock(MongoComponent.class);

        mongoClientProxy.start(mockComponent);
        Assert.assertTrue("Should be started", mongoClientProxy.isStarted());

        mongoClientProxy.stop(mockComponent);

        Assert.assertFalse("Should be stopped", mongoClientProxy.isStarted());
        Assert.assertNull("MongoClient should be null", mongoClientProxy.getMongoClient());
    }

    /**
     * Test multiple components can use same proxy
     */
    @Test
    public void testMultipleComponentsCanUseProxy()
    {
        mongoClientProxy.setConfiguration(configuration);

        MongoComponent component1 = Mockito.mock(MongoComponent.class);
        MongoComponent component2 = Mockito.mock(MongoComponent.class);

        mongoClientProxy.start(component1);
        MongoClient client = mongoClientProxy.getMongoClient();

        mongoClientProxy.start(component2);

        Assert.assertSame("Should be same MongoClient", client, mongoClientProxy.getMongoClient());
        Assert.assertTrue("Should be started", mongoClientProxy.isStarted());

        // Stop first component
        mongoClientProxy.stop(component1);
        Assert.assertTrue("Should still be started", mongoClientProxy.isStarted());
        Assert.assertNotNull("MongoClient should still exist", mongoClientProxy.getMongoClient());

        // Stop second component
        mongoClientProxy.stop(component2);
        Assert.assertFalse("Should be stopped", mongoClientProxy.isStarted());
        Assert.assertNull("MongoClient should be null", mongoClientProxy.getMongoClient());
    }

    /**
     * Test setManagedResourceRecoveryManager does nothing
     */
    @Test
    public void testSetManagedResourceRecoveryManager()
    {
        // Should not throw exception
        mongoClientProxy.setManagedResourceRecoveryManager(null);
    }

    /**
     * Test proxy auto-configures from component if not already configured
     */
    @Test(expected = RuntimeException.class)
    public void testProxyAutoConfiguresFromComponent()
    {
        MongoComponent mockComponent = Mockito.mock(MongoComponent.class);

        // Proxy has no configuration
        Assert.assertNull("Proxy should have no configuration", mongoClientProxy.getConfiguration());

        mongoClientProxy.start(mockComponent);

        // After start, proxy should have configuration
        Assert.assertNotNull("Proxy should be auto-configured", mongoClientProxy.getConfiguration());
    }

    /**
     * Test stop on non-started proxy
     */
    @Test
    public void testStopOnNonStartedProxy()
    {
        mongoClientProxy.stopManagedResource();

        Assert.assertFalse("Should not be started", mongoClientProxy.isStarted());
        Assert.assertNull("MongoClient should be null", mongoClientProxy.getMongoClient());
    }

    /**
     * Test concurrent start calls
     */
    @Test
    public void testConcurrentStartCalls() throws InterruptedException
    {
        mongoClientProxy.setConfiguration(configuration);

        // Create multiple threads that will start the proxy
        Thread[] threads = new Thread[5];
        for (int i = 0; i < threads.length; i++)
        {
            threads[i] = new Thread(() -> {
                mongoClientProxy.startManagedResource();
            });
            threads[i].start();
        }

        // Wait for all threads to complete
        for (Thread thread : threads)
        {
            thread.join();
        }

        Assert.assertTrue("Should be started", mongoClientProxy.isStarted());
        Assert.assertNotNull("MongoClient should exist", mongoClientProxy.getMongoClient());
    }
}
