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
}
