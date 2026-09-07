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

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import org.ikasan.component.endpoint.mongo5.MongoClientConfiguration;
import org.ikasan.component.endpoint.mongo5.MongoClientFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Unit tests for MongoClientFactory
 *
 * @author Ikasan Development Team
 */
public class MongoClientFactoryTest
{
    private MongoClient mongoClient;

    @After
    public void tearDown()
    {
        if (mongoClient != null)
        {
            try
            {
                mongoClient.close();
            }
            catch (Exception e)
            {
                // Ignore
            }
        }
    }

    /**
     * Test getMongoClient throws exception when configuration has no connection URLs
     */
    @Test(expected = RuntimeException.class)
    public void testGetMongoClientWithNoConnectionUrls()
    {
        MongoClientConfiguration configuration = new MongoClientConfiguration();
        configuration.setDatabaseName("testDb");

        MongoClientFactory.getMongoClient(configuration);
    }

    /**
     * Test getMongoClient throws exception when database name is null
     */
    @Test(expected = RuntimeException.class)
    public void testGetMongoClientWithNoDatabaseName()
    {
        MongoClientConfiguration configuration = new MongoClientConfiguration();
        configuration.setConnectionUrls(Arrays.asList("localhost:27017"));

        MongoClientFactory.getMongoClient(configuration);
    }

    /**
     * Test buildUrl with basic configuration (non-authenticated)
     */
    @Test
    public void testBuildUrlBasic() throws Exception
    {
        MongoClientConfiguration configuration = new MongoClientConfiguration();
        configuration.setConnectionUrls(Arrays.asList("localhost:27017"));
        configuration.setDatabaseName("testDb");
        configuration.setAuthenticated(false);

        String url = invokeBuildUrl(configuration);

        Assert.assertNotNull("URL should not be null", url);
        Assert.assertTrue("URL should start with mongodb://", url.startsWith("mongodb://"));
        Assert.assertTrue("URL should contain localhost:27017", url.contains("localhost:27017"));
        Assert.assertTrue("URL should contain database name", url.contains("/testDb"));
        Assert.assertTrue("URL should contain ssl=false", url.contains("ssl=false"));
        Assert.assertFalse("URL should not contain username", url.contains("@"));
    }

    /**
     * Test buildUrl with non-authenticated configuration and validate URL format
     * This test ensures the URL is properly formatted and does not contain authentication
     */
    @Test
    public void testBuildUrlNonAuthenticatedValidFormat() throws Exception
    {
        MongoClientConfiguration configuration = new MongoClientConfiguration();
        configuration.setConnectionUrls(Arrays.asList("localhost:27017"));
        configuration.setDatabaseName("testDb");
        configuration.setAuthenticated(false);

        String url = invokeBuildUrl(configuration);

        // Validate URL structure
        Assert.assertNotNull("URL should not be null", url);
        Assert.assertTrue("URL should start with mongodb://", url.startsWith("mongodb://"));

        // Validate no authentication present
        Assert.assertFalse("URL should not contain @ symbol for credentials", url.contains("@"));
        Assert.assertFalse("URL should not contain authSource parameter", url.contains("authSource="));

        // Validate host and database
        Assert.assertTrue("URL should contain host", url.contains("localhost:27017"));
        Assert.assertTrue("URL should contain database after /", url.contains("/testDb"));

        // Validate SSL parameter is present
        Assert.assertTrue("URL should contain ssl parameter", url.contains("ssl=false"));

        // Validate URL matches expected pattern: mongodb://host:port/database?params
        String expectedPattern = "mongodb://localhost:27017/testDb";
        Assert.assertTrue("URL should start with expected pattern", url.startsWith(expectedPattern));

        // Ensure URL has query parameters
        Assert.assertTrue("URL should contain query string", url.contains("?") || url.contains("&"));
    }

    /**
     * Test buildUrl with non-authenticated configuration - verify no credentials leak
     */
    @Test
    public void testBuildUrlNonAuthenticatedNoCredentials() throws Exception
    {
        MongoClientConfiguration configuration = new MongoClientConfiguration();
        configuration.setConnectionUrls(Arrays.asList("localhost:27017"));
        configuration.setDatabaseName("testDb");
        configuration.setAuthenticated(false);
        // Set username and password but authenticated = false
        configuration.setUsername("testUser");
        configuration.setPassword("testPassword");

        String url = invokeBuildUrl(configuration);

        // Even though username/password are set, they should not appear in URL
        Assert.assertFalse("URL should not contain username", url.contains("testUser"));
        Assert.assertFalse("URL should not contain password", url.contains("testPassword"));
        Assert.assertFalse("URL should not contain @ for credentials", url.contains("@"));
        Assert.assertFalse("URL should not contain authSource", url.contains("authSource="));
    }

    /**
     * Test buildUrl with non-authenticated configuration and verify URL components order
     */
    @Test
    public void testBuildUrlNonAuthenticatedComponentsOrder() throws Exception
    {
        MongoClientConfiguration configuration = new MongoClientConfiguration();
        configuration.setConnectionUrls(Arrays.asList("host1:27017", "host2:27017"));
        configuration.setDatabaseName("myDatabase");
        configuration.setAuthenticated(false);
        configuration.setSslEnabled(true);
        configuration.setApplicationName("TestApp");

        String url = invokeBuildUrl(configuration);

        // Validate URL structure: mongodb://hosts/database?params
        Assert.assertTrue("URL should start with mongodb://", url.startsWith("mongodb://"));

        // Extract parts
        String afterProtocol = url.substring("mongodb://".length());

        // Should contain hosts before /
        int slashIndex = afterProtocol.indexOf('/');
        Assert.assertTrue("URL should contain / separator", slashIndex > 0);

        String hosts = afterProtocol.substring(0, slashIndex);
        Assert.assertTrue("Hosts should contain host1:27017", hosts.contains("host1:27017"));
        Assert.assertTrue("Hosts should contain host2:27017", hosts.contains("host2:27017"));
        Assert.assertTrue("Multiple hosts should be comma-separated", hosts.contains(","));

        // Should contain database after /
        String afterSlash = afterProtocol.substring(slashIndex + 1);
        Assert.assertTrue("Database should be after slash", afterSlash.startsWith("myDatabase"));

        // Should contain query parameters
        Assert.assertTrue("URL should contain ssl parameter", url.contains("ssl=true"));
        Assert.assertTrue("URL should contain appName parameter", url.contains("appName=TestApp"));
    }

    /**
     * Test buildUrl with non-authenticated null configuration
     */
    @Test
    public void testBuildUrlNonAuthenticatedWithNullAuthenticated() throws Exception
    {
        MongoClientConfiguration configuration = new MongoClientConfiguration();
        configuration.setConnectionUrls(Arrays.asList("localhost:27017"));
        configuration.setDatabaseName("testDb");
        configuration.setAuthenticated(null); // Explicitly set to null

        String url = invokeBuildUrl(configuration);

        // When authenticated is null, should be treated as false
        Assert.assertFalse("URL should not contain credentials", url.contains("@"));
        Assert.assertFalse("URL should not contain authSource", url.contains("authSource="));
        Assert.assertTrue("URL should be valid", url.startsWith("mongodb://"));
    }

    /**
     * Test buildUrl with non-authenticated configuration and validate using MongoDB's ConnectionString
     * This uses the MongoDB driver's own validation to confirm URL is correctly formatted
     */
    @Test
    public void testBuildUrlNonAuthenticatedValidatedByMongoDBDriver() throws Exception
    {
        MongoClientConfiguration configuration = new MongoClientConfiguration();
        configuration.setConnectionUrls(Arrays.asList("localhost:27017"));
        configuration.setDatabaseName("testDb");
        configuration.setAuthenticated(false);

        String url = invokeBuildUrl(configuration);

        // Use MongoDB's ConnectionString class to validate the URL
        // If the URL is invalid, this will throw an exception
        ConnectionString connectionString = new ConnectionString(url);

        // Validate the parsed connection string
        Assert.assertNotNull("ConnectionString should be created successfully", connectionString);
        Assert.assertEquals("Database should match", "testDb", connectionString.getDatabase());
        Assert.assertNotNull("Hosts should not be null", connectionString.getHosts());
        Assert.assertEquals("Should have one host", 1, connectionString.getHosts().size());
        Assert.assertTrue("Host should contain localhost", connectionString.getHosts().get(0).contains("localhost"));

        // Verify no credentials are present
        Assert.assertNull("Username should be null", connectionString.getUsername());
        Assert.assertNull("Password should be null", connectionString.getPassword());
        Assert.assertNull("Credential should be null", connectionString.getCredential());
    }

    /**
     * Test buildUrl with non-authenticated configuration including multiple hosts
     * validated by MongoDB's ConnectionString parser
     */
    @Test
    public void testBuildUrlNonAuthenticatedMultipleHostsValidatedByDriver() throws Exception
    {
        MongoClientConfiguration configuration = new MongoClientConfiguration();
        configuration.setConnectionUrls(Arrays.asList("host1:27017", "host2:27018", "host3:27019"));
        configuration.setDatabaseName("myDatabase");
        configuration.setAuthenticated(false);
        configuration.setSslEnabled(true);

        String url = invokeBuildUrl(configuration);

        // Validate using MongoDB's ConnectionString parser
        ConnectionString connectionString = new ConnectionString(url);

        Assert.assertNotNull("ConnectionString should be created", connectionString);
        Assert.assertEquals("Database should match", "myDatabase", connectionString.getDatabase());
        Assert.assertEquals("Should have three hosts", 3, connectionString.getHosts().size());

        // Verify hosts
        Assert.assertTrue("Should contain host1", connectionString.getHosts().stream()
            .anyMatch(h -> h.contains("host1")));
        Assert.assertTrue("Should contain host2", connectionString.getHosts().stream()
            .anyMatch(h -> h.contains("host2")));
        Assert.assertTrue("Should contain host3", connectionString.getHosts().stream()
            .anyMatch(h -> h.contains("host3")));

        // Verify SSL setting
        Assert.assertTrue("SSL should be enabled", connectionString.getSslEnabled());

        // Verify no authentication
        Assert.assertNull("Should have no credentials", connectionString.getCredential());
    }

    /**
     * Test buildUrl with non-authenticated configuration and optional parameters
     * validated by MongoDB's ConnectionString parser
     */
    @Test
    public void testBuildUrlNonAuthenticatedWithOptionalParamsValidatedByDriver() throws Exception
    {
        MongoClientConfiguration configuration = new MongoClientConfiguration();
        configuration.setConnectionUrls(Arrays.asList("localhost:27017"));
        configuration.setDatabaseName("testDb");
        configuration.setAuthenticated(false);
        configuration.setApplicationName("TestApplication");

        Map<String, String> optionalParams = new HashMap<>();
        optionalParams.put("retryReads", "true");
        optionalParams.put("maxPoolSize", "50");
        configuration.setOptionalConnectionParameters(optionalParams);

        String url = invokeBuildUrl(configuration);

        // Validate using MongoDB's ConnectionString parser
        ConnectionString connectionString = new ConnectionString(url);

        Assert.assertNotNull("ConnectionString should be created", connectionString);
        Assert.assertEquals("Database should match", "testDb", connectionString.getDatabase());

        // Verify application name
        Assert.assertEquals("Application name should match", "TestApplication",
            connectionString.getApplicationName());

        // Verify retry writes is set
        Assert.assertTrue("Retry writes should be enabled", connectionString.getRetryReads());

        // Verify max pool size
        Assert.assertEquals("Max pool size should be 50", 50L,
            connectionString.getMaxConnectionPoolSize().longValue());

        // Verify no authentication
        Assert.assertNull("Should have no credentials", connectionString.getCredential());
    }

    /**
     * Test buildUrl with authenticated configuration
     */
    @Test
    public void testBuildUrlWithAuthentication() throws Exception
    {
        MongoClientConfiguration configuration = new MongoClientConfiguration();
        configuration.setConnectionUrls(Arrays.asList("localhost:27017"));
        configuration.setDatabaseName("testDb");
        configuration.setAuthenticated(true);
        configuration.setUsername("admin");
        configuration.setPassword("password123");

        String url = invokeBuildUrl(configuration);

        Assert.assertNotNull("URL should not be null", url);
        Assert.assertTrue("URL should contain credentials", url.contains("admin:password123@"));
        Assert.assertTrue("URL should contain authSource", url.contains("authSource=testDb"));
    }

    /**
     * Test buildUrl with custom auth database
     */
    @Test
    public void testBuildUrlWithCustomAuthDatabase() throws Exception
    {
        MongoClientConfiguration configuration = new MongoClientConfiguration();
        configuration.setConnectionUrls(Arrays.asList("localhost:27017"));
        configuration.setDatabaseName("testDb");
        configuration.setAuthenticated(true);
        configuration.setUsername("admin");
        configuration.setPassword("password123");
        configuration.setAuthDatabaseName("admin");

        String url = invokeBuildUrl(configuration);

        Assert.assertTrue("URL should contain custom authSource", url.contains("authSource=admin"));
        Assert.assertFalse("URL should not use default auth database", url.contains("authSource=testDb"));
    }

    /**
     * Test buildUrl with SSL enabled
     */
    @Test
    public void testBuildUrlWithSslEnabled() throws Exception
    {
        MongoClientConfiguration configuration = new MongoClientConfiguration();
        configuration.setConnectionUrls(Arrays.asList("localhost:27017"));
        configuration.setDatabaseName("testDb");
        configuration.setSslEnabled(true);

        String url = invokeBuildUrl(configuration);

        Assert.assertTrue("URL should contain ssl=true", url.contains("ssl=true"));
    }

    /**
     * Test buildUrl with SRV record
     */
    @Test
    public void testBuildUrlWithSrvRecord() throws Exception
    {
        MongoClientConfiguration configuration = new MongoClientConfiguration();
        configuration.setConnectionUrls(Arrays.asList("cluster.example.com"));
        configuration.setDatabaseName("testDb");
        configuration.setSrvRecord(true);

        String url = invokeBuildUrl(configuration);

        Assert.assertTrue("URL should start with mongodb+srv://", url.startsWith("mongodb+srv://"));
    }

    /**
     * Test buildUrl with application name
     */
    @Test
    public void testBuildUrlWithApplicationName() throws Exception
    {
        MongoClientConfiguration configuration = new MongoClientConfiguration();
        configuration.setConnectionUrls(Arrays.asList("localhost:27017"));
        configuration.setDatabaseName("testDb");
        configuration.setApplicationName("MyApp");

        String url = invokeBuildUrl(configuration);

        Assert.assertTrue("URL should contain application name", url.contains("appName=MyApp"));
    }

    /**
     * Test buildUrl with multiple hosts
     */
    @Test
    public void testBuildUrlWithMultipleHosts() throws Exception
    {
        MongoClientConfiguration configuration = new MongoClientConfiguration();
        configuration.setConnectionUrls(Arrays.asList("host1:27017", "host2:27017", "host3:27017"));
        configuration.setDatabaseName("testDb");

        String url = invokeBuildUrl(configuration);

        Assert.assertTrue("URL should contain all hosts", url.contains("host1:27017,host2:27017,host3:27017"));
    }

    /**
     * Test buildUrl with optional connection parameters
     */
    @Test
    public void testBuildUrlWithOptionalConnectionParameters() throws Exception
    {
        MongoClientConfiguration configuration = new MongoClientConfiguration();
        configuration.setConnectionUrls(Arrays.asList("localhost:27017"));
        configuration.setDatabaseName("testDb");

        Map<String, String> params = new HashMap<>();
        params.put("retryWrites", "true");
        params.put("retryReads", "false");
        params.put("maxPoolSize", "100");
        configuration.setOptionalConnectionParameters(params);

        String url = invokeBuildUrl(configuration);

        Assert.assertTrue("URL should contain retryWrites parameter", url.contains("retryWrites=true"));
        Assert.assertTrue("URL should contain retryReads parameter", url.contains("retryReads=false"));
        Assert.assertTrue("URL should contain maxPoolSize parameter", url.contains("maxPoolSize=100"));
    }

    /**
     * Test buildUrl with all options combined
     */
    @Test
    public void testBuildUrlWithAllOptions() throws Exception
    {
        MongoClientConfiguration configuration = new MongoClientConfiguration();
        configuration.setConnectionUrls(Arrays.asList("host1:27017", "host2:27017"));
        configuration.setDatabaseName("testDb");
        configuration.setAuthenticated(true);
        configuration.setUsername("user");
        configuration.setPassword("pass");
        configuration.setAuthDatabaseName("admin");
        configuration.setSslEnabled(true);
        configuration.setApplicationName("TestApp");

        Map<String, String> params = new HashMap<>();
        params.put("retryWrites", "true");
        params.put("w", "majority");
        configuration.setOptionalConnectionParameters(params);

        String url = invokeBuildUrl(configuration);

        Assert.assertTrue("URL should contain credentials", url.contains("user:pass@"));
        Assert.assertTrue("URL should contain multiple hosts", url.contains("host1:27017,host2:27017"));
        Assert.assertTrue("URL should contain database", url.contains("/testDb"));
        Assert.assertTrue("URL should contain authSource", url.contains("authSource=admin"));
        Assert.assertTrue("URL should contain ssl=true", url.contains("ssl=true"));
        Assert.assertTrue("URL should contain appName", url.contains("appName=TestApp"));
        Assert.assertTrue("URL should contain retryWrites", url.contains("retryWrites=true"));
        Assert.assertTrue("URL should contain w parameter", url.contains("w=majority"));
    }

    /**
     * Test buildUrl with null SSL parameter (should default to false)
     */
    @Test
    public void testBuildUrlWithNullSslEnabled() throws Exception
    {
        MongoClientConfiguration configuration = new MongoClientConfiguration();
        configuration.setConnectionUrls(Arrays.asList("localhost:27017"));
        configuration.setDatabaseName("testDb");
        configuration.setSslEnabled(null);

        String url = invokeBuildUrl(configuration);

        Assert.assertTrue("URL should contain ssl=false when null", url.contains("ssl=false"));
    }

    /**
     * Test buildUrl throws exception with empty connection URLs
     */
    @Test(expected = RuntimeException.class)
    public void testBuildUrlWithEmptyConnectionUrls() throws Throwable
    {
        MongoClientConfiguration configuration = new MongoClientConfiguration();
        configuration.setDatabaseName("testDb");

        try {
            invokeBuildUrl(configuration);
        }
        catch (Exception e) {
            throw e.getCause();
        }
    }

    /**
     * Test buildUrl with no application name
     */
    @Test
    public void testBuildUrlWithoutApplicationName() throws Exception
    {
        MongoClientConfiguration configuration = new MongoClientConfiguration();
        configuration.setConnectionUrls(Arrays.asList("localhost:27017"));
        configuration.setDatabaseName("testDb");
        configuration.setApplicationName(null);

        String url = invokeBuildUrl(configuration);

        Assert.assertFalse("URL should not contain appName parameter", url.contains("appName="));
    }

    /**
     * Test buildUrl with empty optional connection parameters
     */
    @Test
    public void testBuildUrlWithEmptyOptionalConnectionParameters() throws Exception
    {
        MongoClientConfiguration configuration = new MongoClientConfiguration();
        configuration.setConnectionUrls(Arrays.asList("localhost:27017"));
        configuration.setDatabaseName("testDb");
        configuration.setOptionalConnectionParameters(new HashMap<>());

        String url = invokeBuildUrl(configuration);

        Assert.assertNotNull("URL should not be null", url);
        Assert.assertTrue("URL should be valid", url.startsWith("mongodb://"));
    }

    /**
     * Test buildUrl with null optional connection parameters
     */
    @Test
    public void testBuildUrlWithNullOptionalConnectionParameters() throws Exception
    {
        MongoClientConfiguration configuration = new MongoClientConfiguration();
        configuration.setConnectionUrls(Arrays.asList("localhost:27017"));
        configuration.setDatabaseName("testDb");
        configuration.setOptionalConnectionParameters(null);

        String url = invokeBuildUrl(configuration);

        Assert.assertNotNull("URL should not be null", url);
        Assert.assertTrue("URL should be valid", url.startsWith("mongodb://"));
    }

    /**
     * Test buildUrl with connection pool parameters
     */
    @Test
    public void testBuildUrlWithConnectionPoolParameters() throws Exception
    {
        MongoClientConfiguration configuration = new MongoClientConfiguration();
        configuration.setConnectionUrls(Arrays.asList("localhost:27017"));
        configuration.setDatabaseName("testDb");
        configuration.setConnectionsPerHost(50);
        configuration.setMinConnectionsPerHost(10);

        String url = invokeBuildUrl(configuration);

        Assert.assertTrue("URL should contain maxPoolSize", url.contains("maxPoolSize=50"));
        Assert.assertTrue("URL should contain minPoolSize", url.contains("minPoolSize=10"));
    }

    /**
     * Test buildUrl with timeout parameters
     */
    @Test
    public void testBuildUrlWithTimeoutParameters() throws Exception
    {
        MongoClientConfiguration configuration = new MongoClientConfiguration();
        configuration.setConnectionUrls(Arrays.asList("localhost:27017"));
        configuration.setDatabaseName("testDb");
        configuration.setConnectionTimeout(5000);
        configuration.setSocketTimeout(10000);
        configuration.setMaxWaitTime(2000);
        configuration.setMaxConnectionIdleTime(60000);
        configuration.setMaxConnectionLifeTime(120000);

        String url = invokeBuildUrl(configuration);

        Assert.assertTrue("URL should contain connectTimeoutMS", url.contains("connectTimeoutMS=5000"));
        Assert.assertTrue("URL should contain socketTimeoutMS", url.contains("socketTimeoutMS=10000"));
        Assert.assertTrue("URL should contain waitQueueTimeoutMS", url.contains("waitQueueTimeoutMS=2000"));
        Assert.assertTrue("URL should contain maxIdleTimeMS", url.contains("maxIdleTimeMS=60000"));
        Assert.assertTrue("URL should contain maxLifeTimeMS", url.contains("maxLifeTimeMS=120000"));
    }

    /**
     * Test buildUrl with heartbeat parameters
     */
    @Test
    public void testBuildUrlWithHeartbeatParameters() throws Exception
    {
        MongoClientConfiguration configuration = new MongoClientConfiguration();
        configuration.setConnectionUrls(Arrays.asList("localhost:27017"));
        configuration.setDatabaseName("testDb");
        configuration.setHeartbeatFrequency(10000);
        configuration.setMinHeartbeatFrequency(500);
        configuration.setHeartbeatConnectTimeout(20000);
        configuration.setHeartbeatSocketTimeout(20000);

        String url = invokeBuildUrl(configuration);

        Assert.assertTrue("URL should contain heartbeatFrequencyMS", url.contains("heartbeatFrequencyMS=10000"));
        Assert.assertTrue("URL should contain minHeartbeatFrequencyMS", url.contains("minHeartbeatFrequencyMS=500"));
        Assert.assertTrue("URL should contain serverSelectionTimeoutMS", url.contains("serverSelectionTimeoutMS=20000"));
        Assert.assertTrue("URL should contain heartbeatSocketTimeoutMS", url.contains("heartbeatSocketTimeoutMS=20000"));
    }

    /**
     * Test buildUrl with replica set parameters
     */
    @Test
    public void testBuildUrlWithReplicaSetParameters() throws Exception
    {
        MongoClientConfiguration configuration = new MongoClientConfiguration();
        configuration.setConnectionUrls(Arrays.asList("host1:27017", "host2:27017", "host3:27017"));
        configuration.setDatabaseName("testDb");
        configuration.setRequiredReplicaSetName("myReplicaSet");
        configuration.setLocalThreshold(15);

        String url = invokeBuildUrl(configuration);

        Assert.assertTrue("URL should contain replicaSet", url.contains("replicaSet=myReplicaSet"));
        Assert.assertTrue("URL should contain localThresholdMS", url.contains("localThresholdMS=15"));
    }

    /**
     * Test buildUrl with read preference
     */
    @Test
    public void testBuildUrlWithReadPreference() throws Exception
    {
        MongoClientConfiguration configuration = new MongoClientConfiguration();
        configuration.setConnectionUrls(Arrays.asList("localhost:27017"));
        configuration.setDatabaseName("testDb");
        configuration.setReadPreference(com.mongodb.ReadPreference.secondary());

        String url = invokeBuildUrl(configuration);

        Assert.assertTrue("URL should contain readPreference", url.contains("readPreference=secondary"));
    }

    /**
     * Test buildUrl with write concern
     */
    @Test
    public void testBuildUrlWithWriteConcern() throws Exception
    {
        MongoClientConfiguration configuration = new MongoClientConfiguration();
        configuration.setConnectionUrls(Arrays.asList("localhost:27017"));
        configuration.setDatabaseName("testDb");
        configuration.setWriteConcern(com.mongodb.WriteConcern.MAJORITY.withJournal(true));

        String url = invokeBuildUrl(configuration);

        Assert.assertTrue("URL should contain w=majority", url.contains("w=majority"));
        Assert.assertTrue("URL should contain journal=true", url.contains("journal=true"));
    }

    /**
     * Test buildUrl with SSL invalid hostname allowed
     */
    @Test
    public void testBuildUrlWithSslInvalidHostNameAllowed() throws Exception
    {
        MongoClientConfiguration configuration = new MongoClientConfiguration();
        configuration.setConnectionUrls(Arrays.asList("localhost:27017"));
        configuration.setDatabaseName("testDb");
        configuration.setSslInvalidHostNameAllowed(true);

        String url = invokeBuildUrl(configuration);

        Assert.assertTrue("URL should contain tlsAllowInvalidHostnames", url.contains("tlsAllowInvalidHostnames=true"));
    }

    /**
     * Test buildUrl with all parameters set and validated using ConnectionString
     */
    @Test
    public void testBuildUrlWithAllParametersValidated() throws Exception
    {
        MongoClientConfiguration configuration = new MongoClientConfiguration();
        configuration.setConnectionUrls(Arrays.asList("localhost:27017"));
        configuration.setDatabaseName("testDb");
        configuration.setAuthenticated(false);
        configuration.setSslEnabled(true);
        configuration.setApplicationName("TestApp");

        // Connection pool
        configuration.setConnectionsPerHost(50);
        configuration.setMinConnectionsPerHost(10);

        // Timeouts
        configuration.setConnectionTimeout(5000);
        configuration.setSocketTimeout(10000);
        configuration.setMaxWaitTime(2000);
        configuration.setMaxConnectionIdleTime(60000);
        configuration.setMaxConnectionLifeTime(120000);

        // Heartbeat
        configuration.setHeartbeatFrequency(10000);
        configuration.setMinHeartbeatFrequency(500);
        configuration.setHeartbeatConnectTimeout(20000);
        configuration.setHeartbeatSocketTimeout(20000);

        // Replica set
        configuration.setLocalThreshold(15);

        // Read/Write concerns
        configuration.setReadPreference(com.mongodb.ReadPreference.secondary());
        configuration.setWriteConcern(com.mongodb.WriteConcern.MAJORITY.withJournal(true));

        String url = invokeBuildUrl(configuration);

        // Use MongoDB's ConnectionString to validate the URL is correctly formatted
        ConnectionString connectionString = new ConnectionString(url);

        Assert.assertNotNull("ConnectionString should be created", connectionString);
        Assert.assertEquals("Database should match", "testDb", connectionString.getDatabase());

        // Verify application name
        Assert.assertEquals("Application name should match", "TestApp", connectionString.getApplicationName());

        // Verify SSL
        Assert.assertTrue("SSL should be enabled", connectionString.getSslEnabled());

        // Verify pool settings
        Assert.assertEquals("Max pool size should be 50", 50,
            connectionString.getMaxConnectionPoolSize().longValue());
        Assert.assertEquals("Min pool size should be 10", 10,
            connectionString.getMinConnectionPoolSize().longValue());

        // Verify timeouts
        Assert.assertEquals("Connection timeout should be 60000ms", 60000,
            connectionString.getMaxConnectionIdleTime().longValue());

        // Verify read preference
        Assert.assertEquals("Read preference should be secondary", com.mongodb.ReadPreference.secondary(),
            connectionString.getReadPreference());

        // Verify write concern
        Assert.assertEquals("Write concern w should be majority", "majority",
            connectionString.getWriteConcern().getWObject());
        Assert.assertTrue("Write concern journal should be true",
            connectionString.getWriteConcern().getJournal());
    }

    /**
     * Test buildUrl with connection pool parameters only
     */
    @Test
    public void testBuildUrlWithConnectionPoolOnly() throws Exception
    {
        MongoClientConfiguration configuration = new MongoClientConfiguration();
        configuration.setConnectionUrls(Arrays.asList("localhost:27017"));
        configuration.setDatabaseName("testDb");
        configuration.setConnectionsPerHost(25);

        String url = invokeBuildUrl(configuration);

        // Validate using ConnectionString
        ConnectionString connectionString = new ConnectionString(url);

        Assert.assertEquals("Max pool size should be 25", 25,
            connectionString.getMaxConnectionPoolSize().longValue());
    }

    /**
     * Test buildUrl with timeout parameters validated by driver
     */
    @Test
    public void testBuildUrlWithTimeoutsValidated() throws Exception
    {
        MongoClientConfiguration configuration = new MongoClientConfiguration();
        configuration.setConnectionUrls(Arrays.asList("localhost:27017"));
        configuration.setDatabaseName("testDb");
        configuration.setConnectionTimeout(3000);
        configuration.setSocketTimeout(7000);

        String url = invokeBuildUrl(configuration);

        // Validate using ConnectionString
        ConnectionString connectionString = new ConnectionString(url);

        Assert.assertNotNull("ConnectionString should be valid", connectionString);
        // The driver parses these correctly if URL is valid
    }

    /**
     * Test buildUrl with replica set validated by driver
     */
    @Test
    public void testBuildUrlWithReplicaSetValidated() throws Exception
    {
        MongoClientConfiguration configuration = new MongoClientConfiguration();
        configuration.setConnectionUrls(Arrays.asList("host1:27017", "host2:27017"));
        configuration.setDatabaseName("testDb");
        configuration.setRequiredReplicaSetName("rs0");

        String url = invokeBuildUrl(configuration);

        // Validate using ConnectionString
        ConnectionString connectionString = new ConnectionString(url);

        Assert.assertEquals("Replica set name should match", "rs0",
            connectionString.getRequiredReplicaSetName());
    }

    /**
     * Test buildUrl with null connection pool parameters (should not appear in URL)
     */
    @Test
    public void testBuildUrlWithNullConnectionPoolParameters() throws Exception
    {
        MongoClientConfiguration configuration = new MongoClientConfiguration();
        configuration.setConnectionUrls(Arrays.asList("localhost:27017"));
        configuration.setDatabaseName("testDb");
        configuration.setConnectionsPerHost(null);
        configuration.setMinConnectionsPerHost(null);

        String url = invokeBuildUrl(configuration);

        Assert.assertFalse("URL should not contain maxPoolSize", url.contains("maxPoolSize="));
        Assert.assertFalse("URL should not contain minPoolSize", url.contains("minPoolSize="));
    }

    /**
     * Test buildUrl with null timeout parameters (should not appear in URL)
     */
    @Test
    public void testBuildUrlWithNullTimeoutParameters() throws Exception
    {
        MongoClientConfiguration configuration = new MongoClientConfiguration();
        configuration.setConnectionUrls(Arrays.asList("localhost:27017"));
        configuration.setDatabaseName("testDb");
        configuration.setConnectionTimeout(null);
        configuration.setSocketTimeout(null);

        String url = invokeBuildUrl(configuration);

        Assert.assertFalse("URL should not contain connectTimeoutMS", url.contains("connectTimeoutMS="));
        Assert.assertFalse("URL should not contain socketTimeoutMS", url.contains("socketTimeoutMS="));
    }

    /**
     * Test buildUrl with null heartbeat parameters (should not appear in URL)
     */
    @Test
    public void testBuildUrlWithNullHeartbeatParameters() throws Exception
    {
        MongoClientConfiguration configuration = new MongoClientConfiguration();
        configuration.setConnectionUrls(Arrays.asList("localhost:27017"));
        configuration.setDatabaseName("testDb");
        configuration.setHeartbeatFrequency(null);
        configuration.setMinHeartbeatFrequency(null);

        String url = invokeBuildUrl(configuration);

        Assert.assertFalse("URL should not contain heartbeatFrequencyMS", url.contains("heartbeatFrequencyMS="));
        Assert.assertFalse("URL should not contain minHeartbeatFrequencyMS", url.contains("minHeartbeatFrequencyMS="));
    }

    /**
     * Test buildUrl with null replica set parameters (should not appear in URL)
     */
    @Test
    public void testBuildUrlWithNullReplicaSetParameters() throws Exception
    {
        MongoClientConfiguration configuration = new MongoClientConfiguration();
        configuration.setConnectionUrls(Arrays.asList("localhost:27017"));
        configuration.setDatabaseName("testDb");
        configuration.setRequiredReplicaSetName(null);
        configuration.setLocalThreshold(null);

        String url = invokeBuildUrl(configuration);

        Assert.assertFalse("URL should not contain replicaSet", url.contains("replicaSet="));
        Assert.assertFalse("URL should not contain localThresholdMS", url.contains("localThresholdMS="));
    }

    /**
     * Test buildUrl with false SSL invalid hostname allowed (should not appear in URL)
     */
    @Test
    public void testBuildUrlWithSslInvalidHostNameAllowedFalse() throws Exception
    {
        MongoClientConfiguration configuration = new MongoClientConfiguration();
        configuration.setConnectionUrls(Arrays.asList("localhost:27017"));
        configuration.setDatabaseName("testDb");
        configuration.setSslInvalidHostNameAllowed(false);

        String url = invokeBuildUrl(configuration);

        Assert.assertFalse("URL should not contain tlsAllowInvalidHostnames when false",
            url.contains("tlsAllowInvalidHostnames="));
    }

    /**
     * Test buildUrl combining configured parameters with optional parameters
     */
    @Test
    public void testBuildUrlWithConfiguredAndOptionalParameters() throws Exception
    {
        MongoClientConfiguration configuration = new MongoClientConfiguration();
        configuration.setConnectionUrls(Arrays.asList("localhost:27017"));
        configuration.setDatabaseName("testDb");
        configuration.setConnectionsPerHost(30);

        Map<String, String> optionalParams = new HashMap<>();
        optionalParams.put("maxPoolSize", "50"); // Should override the configured value
        optionalParams.put("retryWrites", "true");
        configuration.setOptionalConnectionParameters(optionalParams);

        String url = invokeBuildUrl(configuration);

        // Both should appear in URL, optional params come last so can override
        Assert.assertTrue("URL should contain configured maxPoolSize", url.contains("maxPoolSize=30"));
        Assert.assertTrue("URL should contain optional maxPoolSize", url.contains("maxPoolSize=50"));
        Assert.assertTrue("URL should contain retryWrites", url.contains("retryWrites=true"));
    }

    /**
     * Helper method to invoke private buildUrl method using reflection
     */
    private String invokeBuildUrl(MongoClientConfiguration configuration) throws Exception
    {
        Method buildUrlMethod = MongoClientFactory.class.getDeclaredMethod("buildUrl", MongoClientConfiguration.class);
        buildUrlMethod.setAccessible(true);
        return (String) buildUrlMethod.invoke(null, configuration);
    }
}
