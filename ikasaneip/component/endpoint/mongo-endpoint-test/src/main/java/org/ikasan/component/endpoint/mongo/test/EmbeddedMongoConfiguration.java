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

/**
 * Used to keep the configuration in one place for EmbeddedMongo
 * 
 * @author Kieron Edwards
 */
public class EmbeddedMongoConfiguration
{
    public static final String LOCAL_MONGO_DIST_DIR_PROP = "ikasan.localMongoDistDirProperty";

    public static final String CUSTOM_MONGO_DATABASE_DIRECTORY = "ikasan.flapdoodle.customMongoDatabaseDir";

    public static final String CUSTOM_MONGO_ARCHIVE_STORAGE_DIRECTORY = "ikasan.flapdoodle.customMongoArchiveStorageDir";

    public static final String CUSTOM_MONGO_VERSION = "ikasan.flapdoodle.customMongoVersion";

    public static final String CUSTOM_MONGO_PORT = "ikasan.flapdoodle.customMongoPort";

    private String distributionDirectory;

    private String databaseDirectory;

    private String archiveStorageDirectory;

    private String version;

    private Integer port;

    public String getDistributionDirectory()
    {
        return distributionDirectory;
    }

    /**
     * Set the directory where embedded mongo will look for a mongo .zip distribution to download Use this if you are
     * behind a firewall and cant easily access the mongo distribution site
     */
    public void setDistributionDirectory(String distributionDirectory)
    {
        this.distributionDirectory = distributionDirectory;
    }

    public String getDatabaseDirectory()
    {
        return databaseDirectory;
    }

    /**
     * Set the directory for the database files
     */
    public void setDatabaseDirectory(String databaseDirectory)
    {
        this.databaseDirectory = databaseDirectory;
    }

    public String getArchiveStorageDirectory()
    {
        return archiveStorageDirectory;
    }

    /**
     * Set the directory where the distribution zip is downloaded to
     */
    public void setArchiveStorageDirectory(String archiveStorageDirectory)
    {
        this.archiveStorageDirectory = archiveStorageDirectory;
    }

    public String getVersion()
    {
        return version;
    }

    /**
     * Set mongo version ensuring you are using a sufficient version of the mongo-java-driver for this and that you can
     * access the distribution zip associated with this version
     */
    public void setVersion(String version)
    {
        this.version = version;
    }

    public Integer getPort()
    {
        return port;
    }

    public void setPort(Integer port)
    {
        this.port = port;
    }

    @Override
    public String toString()
    {
        return "EmbeddedMongoConfiguration [distributionDirectory=" + distributionDirectory + ", databaseDirectory="
                + databaseDirectory + ", archiveStorageDirectory=" + archiveStorageDirectory + ", version=" + version
                + ", port=" + port + "]";
    }
}
