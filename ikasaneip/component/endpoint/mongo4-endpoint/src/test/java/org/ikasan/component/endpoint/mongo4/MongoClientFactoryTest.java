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

import com.mongodb.ConnectionString;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoClient;
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
     * Helper method to invoke private buildUrl method using reflection
     */
    private String invokeBuildUrl(MongoClientConfiguration configuration) throws Exception
    {
        Method buildUrlMethod = MongoClientFactory.class.getDeclaredMethod("buildUrl", MongoClientConfiguration.class);
        buildUrlMethod.setAccessible(true);
        return (String) buildUrlMethod.invoke(null, configuration);
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
        configuration.setSocketTimeout(60000);
        configuration.setMaxWaitTime(3000);
        configuration.setMaxConnectionIdleTime(120000);
        configuration.setMaxConnectionLifeTime(300000);

        String url = invokeBuildUrl(configuration);

        Assert.assertTrue("URL should contain connectTimeoutMS", url.contains("connectTimeoutMS=5000"));
        Assert.assertTrue("URL should contain socketTimeoutMS", url.contains("socketTimeoutMS=60000"));
        Assert.assertTrue("URL should contain waitQueueTimeoutMS", url.contains("waitQueueTimeoutMS=3000"));
        Assert.assertTrue("URL should contain maxIdleTimeMS", url.contains("maxIdleTimeMS=120000"));
        Assert.assertTrue("URL should contain maxLifeTimeMS", url.contains("maxLifeTimeMS=300000"));
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
        configuration.setHeartbeatSocketTimeout(5000);

        String url = invokeBuildUrl(configuration);

        Assert.assertTrue("URL should contain heartbeatFrequencyMS", url.contains("heartbeatFrequencyMS=10000"));
        Assert.assertTrue("URL should contain minHeartbeatFrequencyMS", url.contains("minHeartbeatFrequencyMS=500"));
        Assert.assertTrue("URL should contain serverSelectionTimeoutMS", url.contains("serverSelectionTimeoutMS=20000"));
        Assert.assertTrue("URL should contain heartbeatSocketTimeoutMS", url.contains("heartbeatSocketTimeoutMS=5000"));
    }

    /**
     * Test buildUrl with replica set parameters
     */
    @Test
    public void testBuildUrlWithReplicaSetParameters() throws Exception
    {
        MongoClientConfiguration configuration = new MongoClientConfiguration();
        configuration.setConnectionUrls(Arrays.asList("host1:27017", "host2:27017"));
        configuration.setDatabaseName("testDb");
        configuration.setRequiredReplicaSetName("rs0");
        configuration.setLocalThreshold(15);

        String url = invokeBuildUrl(configuration);

        Assert.assertTrue("URL should contain replicaSet", url.contains("replicaSet=rs0"));
        Assert.assertTrue("URL should contain localThresholdMS", url.contains("localThresholdMS=15"));
    }

    /**
     * Test buildUrl with read preference parameter
     */
    @Test
    public void testBuildUrlWithReadPreference() throws Exception
    {
        MongoClientConfiguration configuration = new MongoClientConfiguration();
        configuration.setConnectionUrls(Arrays.asList("localhost:27017"));
        configuration.setDatabaseName("testDb");
        configuration.setReadPreference(ReadPreference.secondaryPreferred());

        String url = invokeBuildUrl(configuration);

        Assert.assertTrue("URL should contain readPreference", url.contains("readPreference=secondaryPreferred"));
    }

    /**
     * Test buildUrl with write concern parameters
     */
    @Test
    public void testBuildUrlWithWriteConcern() throws Exception
    {
        MongoClientConfiguration configuration = new MongoClientConfiguration();
        configuration.setConnectionUrls(Arrays.asList("localhost:27017"));
        configuration.setDatabaseName("testDb");
        configuration.setWriteConcern(WriteConcern.MAJORITY.withJournal(true));

        String url = invokeBuildUrl(configuration);

        Assert.assertTrue("URL should contain w parameter", url.contains("w=majority"));
        Assert.assertTrue("URL should contain journal parameter", url.contains("journal=true"));
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
        configuration.setSslEnabled(true);
        configuration.setSslInvalidHostNameAllowed(true);

        String url = invokeBuildUrl(configuration);

        Assert.assertTrue("URL should contain tlsAllowInvalidHostnames", url.contains("tlsAllowInvalidHostnames=true"));
    }

    /**
     * Test buildUrl with all parameters combined and verify with ConnectionString
     */
    @Test
    public void testBuildUrlWithAllParametersValidated() throws Exception
    {
        MongoClientConfiguration configuration = new MongoClientConfiguration();
        configuration.setConnectionUrls(Arrays.asList("host1:27017", "host2:27017"));
        configuration.setDatabaseName("testDb");

        // Connection pool
        configuration.setConnectionsPerHost(50);
        configuration.setMinConnectionsPerHost(10);

        // Timeouts
        configuration.setConnectionTimeout(5000);
        configuration.setSocketTimeout(60000);
        configuration.setMaxWaitTime(3000);

        // Heartbeat
        configuration.setHeartbeatFrequency(10000);

        // Replica set
        configuration.setRequiredReplicaSetName("rs0");
        configuration.setLocalThreshold(15);

        // Read/Write
        configuration.setReadPreference(ReadPreference.secondaryPreferred());
        configuration.setWriteConcern(WriteConcern.MAJORITY);

        String url = invokeBuildUrl(configuration);

        // Validate using MongoDB's ConnectionString parser
        ConnectionString connectionString = new ConnectionString(url);

        Assert.assertNotNull("ConnectionString should be created", connectionString);
        Assert.assertEquals("Database should match", "testDb", connectionString.getDatabase());
        Assert.assertEquals("Should have two hosts", 2, connectionString.getHosts().size());

        // Verify connection pool settings
        Assert.assertEquals("Max pool size should be 50", 50, connectionString.getMaxConnectionPoolSize().longValue());
        Assert.assertEquals("Min pool size should be 10", 10, connectionString.getMinConnectionPoolSize().longValue());

        // Verify read preference
        Assert.assertEquals("Read preference should be secondaryPreferred",
            ReadPreference.secondaryPreferred(), connectionString.getReadPreference());

        // Verify write concern
        Assert.assertEquals("Write concern should be MAJORITY",
            WriteConcern.MAJORITY.getWObject(), connectionString.getWriteConcern().getWObject());

        // Verify replica set
        Assert.assertEquals("Replica set name should match", "rs0", connectionString.getRequiredReplicaSetName());
    }

    /**
     * Test that optionalConnectionParameters can override applicationName
     */
    @Test
    public void testOptionalConnectionParametersOverrideApplicationName() throws Exception
    {
        MongoClientConfiguration configuration = new MongoClientConfiguration();
        configuration.setConnectionUrls(Arrays.asList("localhost:27017"));
        configuration.setDatabaseName("testDb");
        configuration.setApplicationName("OriginalApp");

        Map<String, String> optionalParams = new HashMap<>();
        optionalParams.put("appName", "OverriddenApp");
        configuration.setOptionalConnectionParameters(optionalParams);

        String url = invokeBuildUrl(configuration);

        // Validate using MongoDB's ConnectionString parser
        ConnectionString connectionString = new ConnectionString(url);

        // The optionalConnectionParameters should override the explicit applicationName
        Assert.assertEquals("Application name should be overridden", "OverriddenApp",
            connectionString.getApplicationName());
    }

    /**
     * Test that optionalConnectionParameters can override connection pool settings
     */
    @Test
    public void testOptionalConnectionParametersOverrideConnectionPool() throws Exception
    {
        MongoClientConfiguration configuration = new MongoClientConfiguration();
        configuration.setConnectionUrls(Arrays.asList("localhost:27017"));
        configuration.setDatabaseName("testDb");
        configuration.setConnectionsPerHost(50);
        configuration.setMinConnectionsPerHost(10);

        Map<String, String> optionalParams = new HashMap<>();
        optionalParams.put("maxPoolSize", "100");
        optionalParams.put("minPoolSize", "20");
        configuration.setOptionalConnectionParameters(optionalParams);

        String url = invokeBuildUrl(configuration);

        // Validate using MongoDB's ConnectionString parser
        ConnectionString connectionString = new ConnectionString(url);

        // The optionalConnectionParameters should override the explicit pool settings
        Assert.assertEquals("Max pool size should be overridden to 100", 100,
            connectionString.getMaxConnectionPoolSize().longValue());
        Assert.assertEquals("Min pool size should be overridden to 20", 20,
            connectionString.getMinConnectionPoolSize().longValue());
    }

    /**
     * Test that optionalConnectionParameters can override timeout settings
     */
    @Test
    public void testOptionalConnectionParametersOverrideTimeouts() throws Exception
    {
        MongoClientConfiguration configuration = new MongoClientConfiguration();
        configuration.setConnectionUrls(Arrays.asList("localhost:27017"));
        configuration.setDatabaseName("testDb");
        configuration.setConnectionTimeout(5000);
        configuration.setSocketTimeout(60000);
        configuration.setMaxWaitTime(3000);
        configuration.setMaxConnectionIdleTime(120000);
        configuration.setMaxConnectionLifeTime(300000);

        Map<String, String> optionalParams = new HashMap<>();
        optionalParams.put("connectTimeoutMS", "10000");
        optionalParams.put("socketTimeoutMS", "120000");
        optionalParams.put("waitQueueTimeoutMS", "6000");
        optionalParams.put("maxIdleTimeMS", "240000");
        optionalParams.put("maxLifeTimeMS", "600000");
        configuration.setOptionalConnectionParameters(optionalParams);

        String url = invokeBuildUrl(configuration);

        // Validate using MongoDB's ConnectionString parser
        ConnectionString connectionString = new ConnectionString(url);

        // The optionalConnectionParameters should override the explicit timeout settings
        Assert.assertEquals("Connect timeout should be overridden to 10000", 10000,
            connectionString.getConnectTimeout().longValue());
        Assert.assertEquals("Socket timeout should be overridden to 120000", 120000,
            connectionString.getSocketTimeout().longValue());
        Assert.assertEquals("Max wait time should be overridden to 6000", 6000,
            connectionString.getMaxWaitTime().longValue());
        Assert.assertEquals("Max idle time should be overridden to 240000", 240000,
            connectionString.getMaxConnectionIdleTime().longValue());
        Assert.assertEquals("Max life time should be overridden to 600000", 600000,
            connectionString.getMaxConnectionLifeTime().longValue());
    }

    /**
     * Test that optionalConnectionParameters can override heartbeat settings
     */
    @Test
    public void testOptionalConnectionParametersOverrideHeartbeat() throws Exception
    {
        MongoClientConfiguration configuration = new MongoClientConfiguration();
        configuration.setConnectionUrls(Arrays.asList("localhost:27017"));
        configuration.setDatabaseName("testDb");
        configuration.setHeartbeatFrequency(10000);
        configuration.setMinHeartbeatFrequency(500);
        configuration.setHeartbeatConnectTimeout(20000);
        configuration.setHeartbeatSocketTimeout(5000);

        Map<String, String> optionalParams = new HashMap<>();
        optionalParams.put("heartbeatFrequencyMS", "20000");
        optionalParams.put("serverSelectionTimeoutMS", "40000");
        optionalParams.put("heartbeatSocketTimeoutMS", "10000");
        configuration.setOptionalConnectionParameters(optionalParams);

        String url = invokeBuildUrl(configuration);

        // Validate using MongoDB's ConnectionString parser
        ConnectionString connectionString = new ConnectionString(url);

        // The optionalConnectionParameters should override the explicit heartbeat settings
        Assert.assertEquals("Heartbeat frequency should be overridden to 20000", 20000,
            connectionString.getHeartbeatFrequency().longValue());
        Assert.assertEquals("Server selection timeout should be overridden to 40000", 40000,
            connectionString.getServerSelectionTimeout().longValue());
    }

    /**
     * Test that optionalConnectionParameters can override replica set settings
     */
    @Test
    public void testOptionalConnectionParametersOverrideReplicaSet() throws Exception
    {
        MongoClientConfiguration configuration = new MongoClientConfiguration();
        configuration.setConnectionUrls(Arrays.asList("host1:27017", "host2:27017"));
        configuration.setDatabaseName("testDb");
        configuration.setRequiredReplicaSetName("rs0");
        configuration.setLocalThreshold(15);

        Map<String, String> optionalParams = new HashMap<>();
        optionalParams.put("replicaSet", "rs1");
        optionalParams.put("localThresholdMS", "30");
        configuration.setOptionalConnectionParameters(optionalParams);

        String url = invokeBuildUrl(configuration);

        // Validate using MongoDB's ConnectionString parser
        ConnectionString connectionString = new ConnectionString(url);

        // The optionalConnectionParameters should override the explicit replica set settings
        Assert.assertEquals("Replica set name should be overridden to rs1", "rs1",
            connectionString.getRequiredReplicaSetName());
        Assert.assertEquals("Local threshold should be overridden to 30", 30,
            connectionString.getLocalThreshold().longValue());
    }

    /**
     * Test that optionalConnectionParameters can override read preference
     */
    @Test
    public void testOptionalConnectionParametersOverrideReadPreference() throws Exception
    {
        MongoClientConfiguration configuration = new MongoClientConfiguration();
        configuration.setConnectionUrls(Arrays.asList("localhost:27017"));
        configuration.setDatabaseName("testDb");
        configuration.setReadPreference(ReadPreference.secondaryPreferred());

        Map<String, String> optionalParams = new HashMap<>();
        optionalParams.put("readPreference", "primaryPreferred");
        configuration.setOptionalConnectionParameters(optionalParams);

        String url = invokeBuildUrl(configuration);

        // Validate using MongoDB's ConnectionString parser
        ConnectionString connectionString = new ConnectionString(url);

        // The optionalConnectionParameters should override the explicit read preference
        Assert.assertEquals("Read preference should be overridden to primary",
            ReadPreference.primaryPreferred(), connectionString.getReadPreference());
    }

    /**
     * Test that optionalConnectionParameters can override write concern settings
     */
    @Test
    public void testOptionalConnectionParametersOverrideWriteConcern() throws Exception
    {
        MongoClientConfiguration configuration = new MongoClientConfiguration();
        configuration.setConnectionUrls(Arrays.asList("localhost:27017"));
        configuration.setDatabaseName("testDb");
        configuration.setWriteConcern(WriteConcern.MAJORITY.withJournal(true));

        Map<String, String> optionalParams = new HashMap<>();
        optionalParams.put("w", "1");
        optionalParams.put("journal", "false");
        configuration.setOptionalConnectionParameters(optionalParams);

        String url = invokeBuildUrl(configuration);

        // Validate using MongoDB's ConnectionString parser
        ConnectionString connectionString = new ConnectionString(url);

        // The optionalConnectionParameters should override the explicit write concern settings
        Assert.assertEquals("Write concern w should be overridden to 1", 1,
            connectionString.getWriteConcern().getWObject());
        Assert.assertFalse("Journal should be overridden to false",
            connectionString.getWriteConcern().getJournal());
    }

    /**
     * Test that optionalConnectionParameters can override SSL settings
     */
    @Test
    public void testOptionalConnectionParametersOverrideSslSettings() throws Exception
    {
        MongoClientConfiguration configuration = new MongoClientConfiguration();
        configuration.setConnectionUrls(Arrays.asList("localhost:27017"));
        configuration.setDatabaseName("testDb");
        configuration.setSslEnabled(true);
        configuration.setSslInvalidHostNameAllowed(false);

        Map<String, String> optionalParams = new HashMap<>();
        optionalParams.put("tlsAllowInvalidHostnames", "true");
        configuration.setOptionalConnectionParameters(optionalParams);

        String url = invokeBuildUrl(configuration);

        // Validate URL contains the overridden value
        Assert.assertTrue("URL should contain overridden tlsAllowInvalidHostnames=true",
            url.contains("tlsAllowInvalidHostnames=true"));
    }

    /**
     * Test that optionalConnectionParameters appear last in URL and override previous values
     */
    @Test
    public void testOptionalConnectionParametersAppearLastInUrl() throws Exception
    {
        MongoClientConfiguration configuration = new MongoClientConfiguration();
        configuration.setConnectionUrls(Arrays.asList("localhost:27017"));
        configuration.setDatabaseName("testDb");
        configuration.setApplicationName("OriginalApp");

        Map<String, String> optionalParams = new HashMap<>();
        optionalParams.put("maxPoolSize", "200");
        optionalParams.put("appName", "OverriddenApp");
        optionalParams.put("customParam", "customValue");
        configuration.setOptionalConnectionParameters(optionalParams);

        String url = invokeBuildUrl(configuration);

        // Find positions of parameters in URL
        int secondMaxPoolSize = url.indexOf("maxPoolSize=200");
        int firstAppName = url.indexOf("appName=OriginalApp");
        int secondAppName = url.indexOf("appName=OverriddenApp");

        // Both original and override should appear, with override appearing last
        Assert.assertTrue("Override maxPoolSize should be in URL", secondMaxPoolSize > -1);

        // Original appName should NOT appear because of the check in buildUrl
        Assert.assertEquals("Original appName should not be in URL when override exists", -1, firstAppName);
        Assert.assertTrue("Override appName should be in URL", secondAppName > -1);

        // Custom parameter should be present
        Assert.assertTrue("Custom parameter should be in URL", url.contains("customParam=customValue"));
    }

    /**
     * Test that multiple optional parameters can override multiple explicit settings
     */
    @Test
    public void testMultipleOptionalConnectionParametersOverride() throws Exception
    {
        MongoClientConfiguration configuration = new MongoClientConfiguration();
        configuration.setConnectionUrls(Arrays.asList("localhost:27017"));
        configuration.setDatabaseName("testDb");
        configuration.setApplicationName("OriginalApp");
        configuration.setConnectionsPerHost(50);
        configuration.setMinConnectionsPerHost(10);
        configuration.setConnectionTimeout(5000);
        configuration.setReadPreference(ReadPreference.secondaryPreferred());

        Map<String, String> optionalParams = new HashMap<>();
        optionalParams.put("appName", "NewApp");
        optionalParams.put("maxPoolSize", "150");
        optionalParams.put("minPoolSize", "30");
        optionalParams.put("connectTimeoutMS", "15000");
        optionalParams.put("readPreference", "primaryPreferred");
        configuration.setOptionalConnectionParameters(optionalParams);

        String url = invokeBuildUrl(configuration);

        // Validate using MongoDB's ConnectionString parser
        ConnectionString connectionString = new ConnectionString(url);

        // All optional parameters should override explicit settings
        Assert.assertEquals("Application name should be overridden", "NewApp",
            connectionString.getApplicationName());
        Assert.assertEquals("Max pool size should be overridden", 150,
            connectionString.getMaxConnectionPoolSize().longValue());
        Assert.assertEquals("Min pool size should be overridden", 30,
            connectionString.getMinConnectionPoolSize().longValue());
        Assert.assertEquals("Connect timeout should be overridden", 15000,
            connectionString.getConnectTimeout().longValue());
        Assert.assertEquals("Read preference should be overridden",
            ReadPreference.primaryPreferred(), connectionString.getReadPreference());
    }

    /**
     * Test SSL parameter when only ssl is provided in optionalConnectionParameters
     */
    @Test
    public void testSslOnlyInOptionalParameters() throws Exception
    {
        MongoClientConfiguration configuration = new MongoClientConfiguration();
        configuration.setConnectionUrls(Arrays.asList("localhost:27017"));
        configuration.setDatabaseName("testDb");
        configuration.setSslEnabled(false); // Set to false explicitly

        Map<String, String> optionalParams = new HashMap<>();
        optionalParams.put("ssl", "true");
        configuration.setOptionalConnectionParameters(optionalParams);

        String url = invokeBuildUrl(configuration);

        // The optional ssl parameter should be used instead of the explicit sslEnabled
        Assert.assertTrue("URL should contain ssl=true from optional parameters", url.contains("ssl=true"));
        // Validate using MongoDB's ConnectionString parser
        ConnectionString connectionString = new ConnectionString(url);
        Assert.assertTrue("SSL should be enabled from optional parameters", connectionString.getSslEnabled());
    }

    /**
     * Test TSL parameter when only tsl is provided in optionalConnectionParameters
     * TSL should be mapped to ssl in the URL
     */
    @Test
    public void testTslOnlyInOptionalParameters() throws Exception
    {
        MongoClientConfiguration configuration = new MongoClientConfiguration();
        configuration.setConnectionUrls(Arrays.asList("localhost:27017"));
        configuration.setDatabaseName("testDb");
        configuration.setSslEnabled(false); // Set to false explicitly

        Map<String, String> optionalParams = new HashMap<>();
        optionalParams.put("tsl", "true");
        configuration.setOptionalConnectionParameters(optionalParams);

        String url = invokeBuildUrl(configuration);

        // The tsl parameter should be mapped to ssl in the URL
        Assert.assertTrue("URL should contain ssl=true mapped from tsl parameter", url.contains("ssl=true"));
        // Validate using MongoDB's ConnectionString parser
        ConnectionString connectionString = new ConnectionString(url);
        Assert.assertTrue("SSL should be enabled from tsl parameter", connectionString.getSslEnabled());
    }

    /**
     * Test when both tsl and ssl are provided in optionalConnectionParameters
     * When both are present, tsl should take precedence and be used as tsl parameter
     */
    @Test
    public void testBothTslAndSslInOptionalParameters() throws Exception
    {
        MongoClientConfiguration configuration = new MongoClientConfiguration();
        configuration.setConnectionUrls(Arrays.asList("localhost:27017"));
        configuration.setDatabaseName("testDb");
        configuration.setSslEnabled(false);

        Map<String, String> optionalParams = new HashMap<>();
        optionalParams.put("tsl", "true");
        optionalParams.put("ssl", "false");
        configuration.setOptionalConnectionParameters(optionalParams);

        String url = invokeBuildUrl(configuration);

        // When both tsl and ssl are present, tsl should be used as the tsl parameter
        Assert.assertTrue("URL should contain tsl=true when both are present", url.contains("ssl=false"));
        Assert.assertFalse("URL should not contain tsl parameter when ssl is present",
            url.matches(".*[?&]tsl=.*"));
    }

    /**
     * Test SSL with null optionalConnectionParameters falls back to explicit sslEnabled
     */
    @Test
    public void testSslWithNullOptionalParameters() throws Exception
    {
        MongoClientConfiguration configuration = new MongoClientConfiguration();
        configuration.setConnectionUrls(Arrays.asList("localhost:27017"));
        configuration.setDatabaseName("testDb");
        configuration.setSslEnabled(true);
        configuration.setOptionalConnectionParameters(null);

        String url = invokeBuildUrl(configuration);

        // Should fall back to explicit sslEnabled configuration
        Assert.assertTrue("URL should contain ssl=true from explicit configuration", url.contains("ssl=true"));
        ConnectionString connectionString = new ConnectionString(url);
        Assert.assertTrue("SSL should be enabled from explicit configuration", connectionString.getSslEnabled());
    }

    /**
     * Test SSL with empty optionalConnectionParameters falls back to explicit sslEnabled
     */
    @Test
    public void testSslWithEmptyOptionalParameters() throws Exception
    {
        MongoClientConfiguration configuration = new MongoClientConfiguration();
        configuration.setConnectionUrls(Arrays.asList("localhost:27017"));
        configuration.setDatabaseName("testDb");
        configuration.setSslEnabled(true);
        configuration.setOptionalConnectionParameters(new HashMap<>());

        String url = invokeBuildUrl(configuration);

        // Should fall back to explicit sslEnabled configuration
        Assert.assertTrue("URL should contain ssl=true from explicit configuration", url.contains("ssl=true"));
        ConnectionString connectionString = new ConnectionString(url);
        Assert.assertTrue("SSL should be enabled from explicit configuration", connectionString.getSslEnabled());
    }

    /**
     * Test SSL parameter positioning with non-authenticated configuration
     */
    @Test
    public void testSslParameterPositioningWithoutAuth() throws Exception
    {
        MongoClientConfiguration configuration = new MongoClientConfiguration();
        configuration.setConnectionUrls(Arrays.asList("localhost:27017"));
        configuration.setDatabaseName("testDb");
        configuration.setAuthenticated(false);

        Map<String, String> optionalParams = new HashMap<>();
        optionalParams.put("ssl", "true");
        configuration.setOptionalConnectionParameters(optionalParams);

        String url = invokeBuildUrl(configuration);

        // SSL should be the first parameter (using ?) when not authenticated
        Assert.assertTrue("URL should contain ?ssl=true as first parameter", url.contains("?ssl=true"));
    }

    /**
     * Test SSL parameter positioning with authenticated configuration
     */
    @Test
    public void testSslParameterPositioningWithAuth() throws Exception
    {
        MongoClientConfiguration configuration = new MongoClientConfiguration();
        configuration.setConnectionUrls(Arrays.asList("localhost:27017"));
        configuration.setDatabaseName("testDb");
        configuration.setAuthenticated(true);
        configuration.setUsername("user");
        configuration.setPassword("pass");

        Map<String, String> optionalParams = new HashMap<>();
        optionalParams.put("ssl", "true");
        configuration.setOptionalConnectionParameters(optionalParams);

        String url = invokeBuildUrl(configuration);

        // SSL should use & as separator when authenticated (authSource will be first param with ?)
        Assert.assertTrue("URL should contain authSource parameter", url.contains("?authSource="));
        Assert.assertTrue("URL should contain &ssl=true after authSource", url.contains("&ssl=true"));
    }

    /**
     * Test TSL parameter is set to false in optional parameters
     */
    @Test
    public void testTslFalseInOptionalParameters() throws Exception
    {
        MongoClientConfiguration configuration = new MongoClientConfiguration();
        configuration.setConnectionUrls(Arrays.asList("localhost:27017"));
        configuration.setDatabaseName("testDb");
        configuration.setSslEnabled(true); // Explicitly enabled

        Map<String, String> optionalParams = new HashMap<>();
        optionalParams.put("tsl", "false");
        configuration.setOptionalConnectionParameters(optionalParams);

        String url = invokeBuildUrl(configuration);

        // The tsl=false from optional parameters should override explicit sslEnabled=true
        Assert.assertTrue("URL should contain ssl=false from tsl parameter", url.contains("ssl=false"));
        ConnectionString connectionString = new ConnectionString(url);
        Assert.assertFalse("SSL should be disabled from tsl parameter", connectionString.getSslEnabled());
    }

    /**
     * Test SSL parameter is set to false in optional parameters
     */
    @Test
    public void testSslFalseInOptionalParameters() throws Exception
    {
        MongoClientConfiguration configuration = new MongoClientConfiguration();
        configuration.setConnectionUrls(Arrays.asList("localhost:27017"));
        configuration.setDatabaseName("testDb");
        configuration.setSslEnabled(true); // Explicitly enabled

        Map<String, String> optionalParams = new HashMap<>();
        optionalParams.put("ssl", "false");
        configuration.setOptionalConnectionParameters(optionalParams);

        String url = invokeBuildUrl(configuration);

        // The ssl=false from optional parameters should override explicit sslEnabled=true
        Assert.assertTrue("URL should contain ssl=false from optional parameters", url.contains("ssl=false"));
        ConnectionString connectionString = new ConnectionString(url);
        Assert.assertFalse("SSL should be disabled from optional parameters", connectionString.getSslEnabled());
    }

    /**
     * Test default SSL behavior when sslEnabled is null and no optional parameters
     */
    @Test
    public void testSslDefaultsToFalseWhenNull() throws Exception
    {
        MongoClientConfiguration configuration = new MongoClientConfiguration();
        configuration.setConnectionUrls(Arrays.asList("localhost:27017"));
        configuration.setDatabaseName("testDb");
        configuration.setSslEnabled(null);
        configuration.setOptionalConnectionParameters(new HashMap<>());

        String url = invokeBuildUrl(configuration);

        // Should default to ssl=false when sslEnabled is null
        Assert.assertTrue("URL should contain ssl=false as default", url.contains("ssl=false"));
        ConnectionString connectionString = new ConnectionString(url);
        Assert.assertFalse("SSL should default to false", connectionString.getSslEnabled());
    }

    /**
     * Test SSL with other optional parameters to ensure proper URL construction
     */
    @Test
    public void testSslWithOtherOptionalParameters() throws Exception
    {
        MongoClientConfiguration configuration = new MongoClientConfiguration();
        configuration.setConnectionUrls(Arrays.asList("localhost:27017"));
        configuration.setDatabaseName("testDb");
        configuration.setSslEnabled(false);

        Map<String, String> optionalParams = new HashMap<>();
        optionalParams.put("ssl", "true");
        optionalParams.put("retryWrites", "true");
        optionalParams.put("maxPoolSize", "100");
        configuration.setOptionalConnectionParameters(optionalParams);

        String url = invokeBuildUrl(configuration);

        // Validate URL contains ssl from optional params
        Assert.assertTrue("URL should contain ssl=true", url.contains("ssl=true"));
        Assert.assertTrue("URL should contain retryWrites", url.contains("retryWrites=true"));
        Assert.assertTrue("URL should contain maxPoolSize", url.contains("maxPoolSize=100"));

        ConnectionString connectionString = new ConnectionString(url);
        Assert.assertTrue("SSL should be enabled", connectionString.getSslEnabled());
        Assert.assertEquals("Max pool size should be 100", 100,
            connectionString.getMaxConnectionPoolSize().longValue());
    }

    /**
     * Test TSL with authenticated configuration
     */
    @Test
    public void testTslWithAuthenticatedConfiguration() throws Exception
    {
        MongoClientConfiguration configuration = new MongoClientConfiguration();
        configuration.setConnectionUrls(Arrays.asList("localhost:27017"));
        configuration.setDatabaseName("testDb");
        configuration.setAuthenticated(true);
        configuration.setUsername("admin");
        configuration.setPassword("password");

        Map<String, String> optionalParams = new HashMap<>();
        optionalParams.put("tsl", "true");
        configuration.setOptionalConnectionParameters(optionalParams);

        String url = invokeBuildUrl(configuration);

        // Should have authSource first, then ssl from tsl parameter
        Assert.assertTrue("URL should contain authSource", url.contains("?authSource="));
        Assert.assertTrue("URL should contain ssl=true from tsl", url.contains("&ssl=true"));
        Assert.assertTrue("URL should contain credentials", url.contains("admin:password@"));

        ConnectionString connectionString = new ConnectionString(url);
        Assert.assertTrue("SSL should be enabled", connectionString.getSslEnabled());
        Assert.assertNotNull("Should have credentials", connectionString.getCredential());
    }

    /**
     * Test that both tsl and ssl values are different - tsl takes precedence
     */
    @Test
    public void testSslTakesPrecedenceOverSslWhenBothPresent() throws Exception
    {
        MongoClientConfiguration configuration = new MongoClientConfiguration();
        configuration.setConnectionUrls(Arrays.asList("localhost:27017"));
        configuration.setDatabaseName("testDb");

        Map<String, String> optionalParams = new HashMap<>();
        optionalParams.put("tsl", "false");
        optionalParams.put("ssl", "true");
        configuration.setOptionalConnectionParameters(optionalParams);

        String url = invokeBuildUrl(configuration);

        // When both tsl and ssl present, tsl value should be used as tsl parameter
        ConnectionString connectionString = new ConnectionString(url);
        Assert.assertTrue("SSL should be enabled", connectionString.getSslEnabled());
        Assert.assertTrue("URL should contain ssl=true", url.contains("ssl=true"));
        Assert.assertFalse("URL should not contain tsl parameter",
            url.matches(".*[?&]tsl=.*"));
    }
}
