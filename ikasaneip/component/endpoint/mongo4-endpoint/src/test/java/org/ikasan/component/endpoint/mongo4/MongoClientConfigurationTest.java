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

import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

/**
 * Unit tests for MongoClientConfiguration
 *
 * @author Ikasan Development Team
 */
public class MongoClientConfigurationTest
{
    /**
     * Test default values are set correctly
     */
    @Test
    public void testDefaultValues()
    {
        MongoClientConfiguration configuration = new MongoClientConfiguration();

        Assert.assertNotNull("connectionUrls should not be null", configuration.getConnectionUrls());
        Assert.assertEquals("connectionUrls should be empty by default", 0, configuration.getConnectionUrls().size());
        Assert.assertEquals("authenticated should default to false", Boolean.FALSE, configuration.getAuthenticated());
        Assert.assertEquals("cursorFinalizerEnabled should default to true", Boolean.TRUE, configuration.getCursorFinalizerEnabled());
        Assert.assertEquals("legacyDefaults should default to false", Boolean.FALSE, configuration.getLegacyDefaults());
        Assert.assertEquals("sslEnabled should default to false", Boolean.FALSE, configuration.getSslEnabled());
        Assert.assertEquals("sslInvalidHostNameAllowed should default to false", Boolean.FALSE, configuration.getSslInvalidHostNameAllowed());
        Assert.assertEquals("srvRecord should default to false", Boolean.FALSE, configuration.getSrvRecord());
        Assert.assertNotNull("collectionNames should not be null", configuration.getCollectionNames());
        Assert.assertNotNull("readPreference should not be null", configuration.getReadPreference());
        Assert.assertEquals("readPreference should default to primary", ReadPreference.primary(), configuration.getReadPreference());
        Assert.assertNotNull("writeConcern should not be null", configuration.getWriteConcern());
        Assert.assertEquals("writeConcern should default to ACKNOWLEDGED", WriteConcern.ACKNOWLEDGED, configuration.getWriteConcern());
        Assert.assertNotNull("optionalConnectionParameters should not be null", configuration.getOptionalConnectionParameters());
        Assert.assertTrue("optionalConnectionParameters should be empty by default", configuration.getOptionalConnectionParameters().isEmpty());
    }

    /**
     * Test setters and getters for basic properties
     */
    @Test
    public void testBasicProperties()
    {
        MongoClientConfiguration configuration = new MongoClientConfiguration();

        // Test connectionUrls
        List<String> urls = Arrays.asList("localhost:27017", "localhost:27018");
        configuration.setConnectionUrls(urls);
        Assert.assertEquals("connectionUrls should match", urls, configuration.getConnectionUrls());

        // Test authenticated
        configuration.setAuthenticated(true);
        Assert.assertTrue("authenticated should be true", configuration.isAuthenticated());
        Assert.assertTrue("getAuthenticated should be true", configuration.getAuthenticated());

        // Test username
        configuration.setUsername("testUser");
        Assert.assertEquals("username should match", "testUser", configuration.getUsername());

        // Test password
        configuration.setPassword("testPassword");
        Assert.assertEquals("password should match", "testPassword", configuration.getPassword());

        // Test databaseName
        configuration.setDatabaseName("testDb");
        Assert.assertEquals("databaseName should match", "testDb", configuration.getDatabaseName());

        // Test applicationName
        configuration.setApplicationName("testApp");
        Assert.assertEquals("applicationName should match", "testApp", configuration.getApplicationName());

        // Test authDatabaseName
        configuration.setAuthDatabaseName("admin");
        Assert.assertEquals("authDatabaseName should match", "admin", configuration.getAuthDatabaseName());
    }

    /**
     * Test SSL and SRV properties
     */
    @Test
    public void testSslAndSrvProperties()
    {
        MongoClientConfiguration configuration = new MongoClientConfiguration();

        configuration.setSslEnabled(true);
        Assert.assertTrue("sslEnabled should be true", configuration.getSslEnabled());

        configuration.setSslInvalidHostNameAllowed(true);
        Assert.assertTrue("sslInvalidHostNameAllowed should be true", configuration.getSslInvalidHostNameAllowed());

        configuration.setSrvRecord(true);
        Assert.assertTrue("srvRecord should be true", configuration.getSrvRecord());
    }

    /**
     * Test collection names
     */
    @Test
    public void testCollectionNames()
    {
        MongoClientConfiguration configuration = new MongoClientConfiguration();

        Map<String, String> collections = new HashMap<>();
        collections.put("messages", "message_collection");
        collections.put("errors", "error_collection");

        configuration.setCollectionNames(collections);
        Assert.assertEquals("collectionNames should match", collections, configuration.getCollectionNames());
        Assert.assertEquals("messages collection should match", "message_collection", configuration.getCollectionNames().get("messages"));
    }

    /**
     * Test read preference and write concern
     */
    @Test
    public void testReadPreferenceAndWriteConcern()
    {
        MongoClientConfiguration configuration = new MongoClientConfiguration();

        configuration.setReadPreference(ReadPreference.secondary());
        Assert.assertEquals("readPreference should be secondary", ReadPreference.secondary(), configuration.getReadPreference());

        configuration.setWriteConcern(WriteConcern.MAJORITY);
        Assert.assertEquals("writeConcern should be MAJORITY", WriteConcern.MAJORITY, configuration.getWriteConcern());
    }

    /**
     * Test timeout and connection properties
     */
    @Test
    public void testTimeoutAndConnectionProperties()
    {
        MongoClientConfiguration configuration = new MongoClientConfiguration();

        configuration.setLocalThreshold(100);
        Assert.assertEquals("localThreshold should be 100", Integer.valueOf(100), configuration.getLocalThreshold());

        configuration.setConnectionsPerHost(50);
        Assert.assertEquals("connectionsPerHost should be 50", Integer.valueOf(50), configuration.getConnectionsPerHost());

        configuration.setConnectionTimeout(5000);
        Assert.assertEquals("connectionTimeout should be 5000", Integer.valueOf(5000), configuration.getConnectionTimeout());

        configuration.setSocketTimeout(10000);
        Assert.assertEquals("socketTimeout should be 10000", Integer.valueOf(10000), configuration.getSocketTimeout());

        configuration.setMaxWaitTime(3000);
        Assert.assertEquals("maxWaitTime should be 3000", Integer.valueOf(3000), configuration.getMaxWaitTime());

        configuration.setMinConnectionsPerHost(5);
        Assert.assertEquals("minConnectionsPerHost should be 5", Integer.valueOf(5), configuration.getMinConnectionsPerHost());

        configuration.setMaxConnectionIdleTime(60000);
        Assert.assertEquals("maxConnectionIdleTime should be 60000", Integer.valueOf(60000), configuration.getMaxConnectionIdleTime());

        configuration.setMaxConnectionLifeTime(120000);
        Assert.assertEquals("maxConnectionLifeTime should be 120000", Integer.valueOf(120000), configuration.getMaxConnectionLifeTime());

        configuration.setThreadsAllowedToBlockForConnectionMultiplier(10);
        Assert.assertEquals("threadsAllowedToBlockForConnectionMultiplier should be 10", Integer.valueOf(10), configuration.getThreadsAllowedToBlockForConnectionMultiplier());
    }

    /**
     * Test heartbeat properties
     */
    @Test
    public void testHeartbeatProperties()
    {
        MongoClientConfiguration configuration = new MongoClientConfiguration();

        configuration.setMinHeartbeatFrequency(500);
        Assert.assertEquals("minHeartbeatFrequency should be 500", Integer.valueOf(500), configuration.getMinHeartbeatFrequency());

        configuration.setHeartbeatConnectTimeout(2000);
        Assert.assertEquals("heartbeatConnectTimeout should be 2000", Integer.valueOf(2000), configuration.getHeartbeatConnectTimeout());

        configuration.setHeartbeatFrequency(10000);
        Assert.assertEquals("heartbeatFrequency should be 10000", Integer.valueOf(10000), configuration.getHeartbeatFrequency());

        configuration.setHeartbeatSocketTimeout(3000);
        Assert.assertEquals("heartbeatSocketTimeout should be 3000", Integer.valueOf(3000), configuration.getHeartbeatSocketTimeout());
    }

    /**
     * Test other boolean properties
     */
    @Test
    public void testOtherBooleanProperties()
    {
        MongoClientConfiguration configuration = new MongoClientConfiguration();

        configuration.setAlwaysUseMBeans(true);
        Assert.assertTrue("alwaysUseMBeans should be true", configuration.getAlwaysUseMBeans());

        configuration.setCursorFinalizerEnabled(false);
        Assert.assertFalse("cursorFinalizerEnabled should be false", configuration.getCursorFinalizerEnabled());

        configuration.setLegacyDefaults(true);
        Assert.assertTrue("legacyDefaults should be true", configuration.getLegacyDefaults());

        configuration.setSocketKeepAlive(true);
        Assert.assertTrue("socketKeepAlive should be true", configuration.getSocketKeepAlive());
    }

    /**
     * Test description and replica set name
     */
    @Test
    public void testDescriptionAndReplicaSet()
    {
        MongoClientConfiguration configuration = new MongoClientConfiguration();

        configuration.setDescription("Test MongoDB Client");
        Assert.assertEquals("description should match", "Test MongoDB Client", configuration.getDescription());

        configuration.setRequiredReplicaSetName("rs0");
        Assert.assertEquals("requiredReplicaSetName should match", "rs0", configuration.getRequiredReplicaSetName());
    }

    /**
     * Test optional connection parameters
     */
    @Test
    public void testOptionalConnectionParameters()
    {
        MongoClientConfiguration configuration = new MongoClientConfiguration();

        Map<String, String> params = new HashMap<>();
        params.put("retryWrites", "true");
        params.put("retryReads", "false");
        params.put("maxPoolSize", "100");

        configuration.setOptionalConnectionParameters(params);
        Assert.assertEquals("optionalConnectionParameters should match", params, configuration.getOptionalConnectionParameters());
        Assert.assertEquals("retryWrites should be true", "true", configuration.getOptionalConnectionParameters().get("retryWrites"));
        Assert.assertEquals("optionalConnectionParameters should have 3 entries", 3, configuration.getOptionalConnectionParameters().size());
    }

    /**
     * Test validation with valid configuration
     */
    @Test
    public void testValidationSuccess()
    {
        MongoClientConfiguration configuration = new MongoClientConfiguration();
        configuration.setConnectionUrls(Arrays.asList("localhost:27017"));
        configuration.setDatabaseName("testDb");

        // Should not throw exception
        configuration.validate();
    }

    /**
     * Test validation fails with no connection URLs
     */
    @Test(expected = RuntimeException.class)
    public void testValidationFailsWithNoConnectionUrls()
    {
        MongoClientConfiguration configuration = new MongoClientConfiguration();
        configuration.setDatabaseName("testDb");

        configuration.validate();
    }

    /**
     * Test validation fails with empty connection URLs
     */
    @Test(expected = RuntimeException.class)
    public void testValidationFailsWithEmptyConnectionUrls()
    {
        MongoClientConfiguration configuration = new MongoClientConfiguration();
        configuration.setConnectionUrls(Arrays.asList("", null));
        configuration.setDatabaseName("testDb");

        configuration.validate();
    }

    /**
     * Test validation fails with no database name
     */
    @Test(expected = RuntimeException.class)
    public void testValidationFailsWithNoDatabaseName()
    {
        MongoClientConfiguration configuration = new MongoClientConfiguration();
        configuration.setConnectionUrls(Arrays.asList("localhost:27017"));

        configuration.validate();
    }

    /**
     * Test getServerAddresses with valid connection URLs
     */
    @Test
    public void testGetServerAddresses()
    {
        MongoClientConfiguration configuration = new MongoClientConfiguration();
        configuration.setConnectionUrls(Arrays.asList("localhost:27017", "localhost:27018", "localhost:27019"));

        List<ServerAddress> addresses = configuration.getServerAddresses();

        Assert.assertEquals("Should have 3 server addresses", 3, addresses.size());
        Assert.assertEquals("First address host should be localhost", "localhost", addresses.get(0).getHost());
        Assert.assertEquals("First address port should be 27017", 27017, addresses.get(0).getPort());
        Assert.assertEquals("Second address port should be 27018", 27018, addresses.get(1).getPort());
        Assert.assertEquals("Third address port should be 27019", 27019, addresses.get(2).getPort());
    }

    /**
     * Test getServerAddresses with invalid connection URLs
     */
    @Test
    public void testGetServerAddressesWithInvalidUrls()
    {
        MongoClientConfiguration configuration = new MongoClientConfiguration();
        configuration.setConnectionUrls(Arrays.asList("localhost:27017", "invalid-url", "localhost:not-a-port", "localhost:27018"));

        List<ServerAddress> addresses = configuration.getServerAddresses();

        // Should only return valid addresses
        Assert.assertEquals("Should have 2 valid server addresses", 2, addresses.size());
        Assert.assertEquals("First address port should be 27017", 27017, addresses.get(0).getPort());
        Assert.assertEquals("Second address port should be 27018", 27018, addresses.get(1).getPort());
    }

    /**
     * Test toString method
     */
    @Test
    public void testToString()
    {
        MongoClientConfiguration configuration = new MongoClientConfiguration();
        configuration.setConnectionUrls(Arrays.asList("localhost:27017"));
        configuration.setDatabaseName("testDb");
        configuration.setUsername("testUser");
        configuration.setAuthenticated(true);

        Map<String, String> params = new HashMap<>();
        params.put("retryWrites", "true");
        configuration.setOptionalConnectionParameters(params);

        String result = configuration.toString();

        Assert.assertNotNull("toString should not be null", result);
        Assert.assertTrue("toString should contain connectionUrls", result.contains("connectionUrls"));
        Assert.assertTrue("toString should contain databaseName", result.contains("databaseName=testDb"));
        Assert.assertTrue("toString should contain username", result.contains("username=testUser"));
        Assert.assertTrue("toString should contain authenticated", result.contains("authenticated=true"));
        Assert.assertTrue("toString should contain optionalConnectionParameters", result.contains("optionalConnectionParameters"));
    }
}
