/* 
 * =============================================================================
 * Ikasan Enterprise Integration Platform
 * 
 * Distributed under the Modified BSD License.
 * Copyright 2015 notice: The copyright for this software and a full listing 
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
 * =============================================================================
 */
package org.ikasan.component.endpoint.mongo.test;

import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class EmbeddedMongoTest {


    @Test
    public void testEmbeddedMongoOverrideConfigurationWithSystemProperties() throws Exception {
        Map<String, String> envVars = new HashMap<>();
        envVars.put("http.proxy.host", "httpProxyHostFromEnv");
        envVars.put("http.proxy.port", "httpProxyPortFromEnv");
        setEnvironmentVariables(envVars);

        final EmbeddedMongoConfiguration configuration = new EmbeddedMongoConfiguration();
        configuration.setArchiveStorageDirectory("archiveStorageDir");
        configuration.setDatabaseDirectory("databaseDirectory");
        configuration.setDistributionDirectory("distributionDirectory");
        configuration.setPort(100);
        configuration.setVersion("3.0.3");
        System.setProperty("ikasan.localMongoDistDirProperty", "sysLocalStorageDir");
        System.setProperty("ikasan.flapdoodle.customMongoDatabaseDir", "sysDatabaseDir");
        System.setProperty("ikasan.flapdoodle.customMongoArchiveStorageDir", "sysArchiveStorageDir");
        System.setProperty("ikasan.flapdoodle.customMongoVersion", "3.0.0");
        System.setProperty("ikasan.flapdoodle.customMongoPort", "200");
        System.setProperty("http.proxy.host", "httpProxyHostFromSys");
        System.setProperty("http.proxy.port", "httpProxyPortFromSys");
        new EmbeddedMongo(configuration);
        assertEquals("sysLocalStorageDir", configuration.getDistributionDirectory());
        assertEquals("sysDatabaseDir", configuration.getDatabaseDirectory());
        assertEquals("sysArchiveStorageDir", configuration.getArchiveStorageDirectory());
        assertEquals("3.0.0", configuration.getVersion());
        assertEquals(200, configuration.getPort().intValue());
        assertEquals("httpProxyHostFromSys", configuration.getHttpProxyHost());
        assertEquals("httpProxyPortFromSys", configuration.getHttpProxyPort());
    }

    @Test
    public void testEmbeddedMongoOverrideConfigurationWithEnvironmentVariables() throws Exception {
        Map<String, String> envVars = new HashMap<>();
        envVars.put("http.proxy.host", "httpProxyHostFromEnv");
        envVars.put("http.proxy.port", "httpProxyPortFromEnv");
        setEnvironmentVariables(envVars);

        final EmbeddedMongoConfiguration configuration = new EmbeddedMongoConfiguration();
        new EmbeddedMongo(configuration);
        assertEquals("httpProxyHostFromEnv", configuration.getHttpProxyHost());
        assertEquals("httpProxyPortFromEnv", configuration.getHttpProxyPort());
    }

    @Test
    public void testEmbeddedMongoWithPortConstructor()
    {
        final EmbeddedMongo embeddedMongo = new EmbeddedMongo(200);
        final EmbeddedMongoConfiguration configuration = embeddedMongo.getConfiguration();
        assertEquals(200, configuration.getPort().intValue());
    }

    protected static void setEnvironmentVariables(Map<String, String> newenv) throws Exception {
        try {
            Class<?> processEnvironmentClass = Class.forName("java.lang.ProcessEnvironment");
            Field theEnvironmentField = processEnvironmentClass.getDeclaredField("theEnvironment");
            theEnvironmentField.setAccessible(true);
            Map<String, String> env = (Map<String, String>) theEnvironmentField.get(null);
            env.putAll(newenv);
            Field theCaseInsensitiveEnvironmentField = processEnvironmentClass.getDeclaredField("theCaseInsensitiveEnvironment");
            theCaseInsensitiveEnvironmentField.setAccessible(true);
            Map<String, String> cienv = (Map<String, String>) theCaseInsensitiveEnvironmentField.get(null);
            cienv.putAll(newenv);
        } catch (NoSuchFieldException e) {
            Class[] classes = Collections.class.getDeclaredClasses();
            Map<String, String> env = System.getenv();
            for (Class cl : classes) {
                if ("java.util.Collections$UnmodifiableMap".equals(cl.getName())) {
                    Field field = cl.getDeclaredField("m");
                    field.setAccessible(true);
                    Object obj = field.get(env);
                    Map<String, String> map = (Map<String, String>) obj;
                    map.clear();
                    map.putAll(newenv);
                }
            }
        }
    }

}
