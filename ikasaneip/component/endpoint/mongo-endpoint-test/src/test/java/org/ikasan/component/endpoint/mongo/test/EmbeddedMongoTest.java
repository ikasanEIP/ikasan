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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class EmbeddedMongoTest {


    @Test
    public void testEmbeddedMongoOverrideConfiguration()
    {
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
        new EmbeddedMongo(configuration);
        assertEquals("sysLocalStorageDir", configuration.getDistributionDirectory());
        assertEquals("sysDatabaseDir", configuration.getDatabaseDirectory());
        assertEquals("sysArchiveStorageDir", configuration.getArchiveStorageDirectory());
        assertEquals("3.0.0", configuration.getVersion());
        assertEquals(200, configuration.getPort().intValue());
    }


    @Test
    public void testEmbeddedMongoWithPortConstructor()
    {
        final EmbeddedMongo embeddedMongo = new EmbeddedMongo(200);
        final EmbeddedMongoConfiguration configuration = embeddedMongo.getConfiguration();
        assertEquals(200, configuration.getPort().intValue());
    }


}
