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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

import de.flapdoodle.embed.mongo.Command;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.ArtifactStoreBuilder;
import de.flapdoodle.embed.mongo.config.DownloadConfigBuilder;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.config.RuntimeConfigBuilder;
import de.flapdoodle.embed.mongo.config.Storage;
import de.flapdoodle.embed.mongo.distribution.Feature;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.mongo.distribution.Versions;
import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.distribution.GenericVersion;
import de.flapdoodle.embed.process.io.directories.IDirectory;
import de.flapdoodle.embed.process.runtime.Network;

/**
 * Allows the port and download location of the mongo distribution to be configured Also ensures that only a single
 * embedded mongo instance is started for all tests
 * 
 * @author Kieron Edwards
 */
public class EmbeddedMongo
{
    private final static Logger logger = LoggerFactory.getLogger(EmbeddedMongo.class);

    private MongodExecutable mongodExecutable;

    private static EmbeddedMongo staticReference;

    private MongoClient mongoClient;

    private final EmbeddedMongoConfiguration configuration;


    /**
     * @deprecated - use the configuration object constructor or the empty constructor instead
     */
    @Deprecated
    public EmbeddedMongo(int port)
    {
        super();
        configuration = new EmbeddedMongoConfiguration();
        configuration.setPort(port);
        overrideConfigurationWithSystemProperties();
    }

    /**
     * This will automatically allocated a free port to the mongo instance
     */
    public EmbeddedMongo()
    {
        configuration = new EmbeddedMongoConfiguration();
        overrideConfigurationWithSystemProperties();
    }

    /**
     * If you need to configure embedded mongo distribution and db settings use this constructor
     */
    public EmbeddedMongo(EmbeddedMongoConfiguration configuration)
    {
        this.configuration = configuration;
        overrideConfigurationWithSystemProperties();
    }

    private void overrideConfigurationWithSystemProperties()
    {
        final String sysCustomMongoDatabaseDir=System.getProperty(EmbeddedMongoConfiguration.CUSTOM_MONGO_DATABASE_DIRECTORY);
        final String sysLocalMongoDistDir=System.getProperty(EmbeddedMongoConfiguration.LOCAL_MONGO_DIST_DIR_PROP);
        final String sysCustomMongoVersion=System.getProperty(EmbeddedMongoConfiguration.CUSTOM_MONGO_VERSION);
        final String sysCustomMongoArchiveStorageDir=System.getProperty(EmbeddedMongoConfiguration.CUSTOM_MONGO_ARCHIVE_STORAGE_DIRECTORY);
        final String sysCustomMongoPort=System.getProperty(EmbeddedMongoConfiguration.CUSTOM_MONGO_PORT);
        if (sysCustomMongoDatabaseDir != null){
            configuration.setDatabaseDirectory(sysCustomMongoDatabaseDir); 
        }
        if (sysLocalMongoDistDir != null){
            configuration.setDistributionDirectory(sysLocalMongoDistDir); 
        }
        if (sysCustomMongoVersion != null){
            configuration.setVersion(sysCustomMongoVersion); 
        }
        if (sysCustomMongoArchiveStorageDir != null){
            configuration.setArchiveStorageDirectory((sysCustomMongoArchiveStorageDir));
        }
        if (sysCustomMongoPort != null){
            configuration.setPort(new Integer(sysCustomMongoPort)); 
        }
    }

    /**
     * This will check for the singleton instance and wont instantiate a new mongo database each time start is called by
     * a test.
     * 
     * @return
     */
    public MongoClient start()
    {
        synchronized (EmbeddedMongo.class)
        {
            if (staticReference == null)
            {
                final Command command = Command.MongoD;
                MongodStarter runtime = null;
                final DownloadConfigBuilder downloadConfigBuilder = setupDownloadConfigBuilder(command);
                final IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder()
                .defaults(command)
                .artifactStore(new ArtifactStoreBuilder().defaults(command).download(downloadConfigBuilder.build()))
                .build();
                runtime = MongodStarter.getInstance(runtimeConfig);
                ;
                try
                {
                    final MongodConfigBuilder mongodConfigBuilder = setupMongodConfigBuilder();
                    mongodExecutable = runtime.prepare(mongodConfigBuilder.build());
                    final MongodProcess mongodProcess = mongodExecutable.start();
                    staticReference = this;
                    mongoClient = new MongoClient(new ServerAddress(mongodProcess.getConfig().net().getServerAddress(),
                        mongodProcess.getConfig().net().getPort()));
                }
                catch (final IOException e)
                {
                    throw new RuntimeException("Unable to start embeddedMongo", e);
                }
            }
            return staticReference.mongoClient;
        }
    }

    private MongodConfigBuilder setupMongodConfigBuilder() throws UnknownHostException, IOException
    {
        MongodConfigBuilder builder = new MongodConfigBuilder();
        builder = configuration.getPort() != null ? builder.net(new Net(configuration.getPort(), Network
            .localhostIsIPv6())) : builder.net(new Net());
        final String customMongoDatabaseDirectory = configuration.getDatabaseDirectory();
        final String customMongoVersion = configuration.getVersion();
        if (customMongoDatabaseDirectory != null)
        {
            logger.info("Custom mongo database dir set to [{}]", customMongoDatabaseDirectory);
            builder.replication(new Storage(customMongoDatabaseDirectory, null, 0));
        }
        if (customMongoVersion != null)
        {
            logger.info("Custom mongo version set to [{}]", customMongoVersion);
            builder.version(Versions.withFeatures(new GenericVersion(customMongoVersion), Feature.SYNC_DELAY));
        }
        else
        {
            builder.version(Version.Main.PRODUCTION);
        }
        return builder;
    }

    private DownloadConfigBuilder setupDownloadConfigBuilder(Command command)
    {
        DownloadConfigBuilder builder = new DownloadConfigBuilder();
        builder = builder.defaultsForCommand(command);
        final String localMongoDistDir = configuration.getDistributionDirectory();
        final String customMongoArchiveDownloadDirectory = configuration.getArchiveStorageDirectory();
        if (localMongoDistDir != null)
        {
            logger.info("Custom local mongo dist dir set to [{}]", localMongoDistDir);
            builder.downloadPath(getLocalMongoDistributionPath(localMongoDistDir));
        }
        if (customMongoArchiveDownloadDirectory != null)
        {
            logger.info("Custom mongo artifact storage dir set to [{}]", customMongoArchiveDownloadDirectory);
            builder.artifactStorePath(getIDirectory(customMongoArchiveDownloadDirectory));
        }
        return builder;
    }

    private IDirectory getIDirectory(final String customMongoArchiveDownloadDirectory)
    {
        return new IDirectory()
        {
            @Override
            public File asFile()
            {
                return new File(customMongoArchiveDownloadDirectory);
            }

            @Override
            public boolean isGenerated()
            {
                return false;
            }
        };
    }

    public static void stop()
    {
        synchronized (EmbeddedMongo.class)
        {
            if (staticReference != null)
            {
                staticReference.mongodExecutable.stop();
                staticReference = null;
            }
        }
    }

    private String getLocalMongoDistributionPath(String mongoDistributionDirectory)
    {
        URL resource = null;
        try
        {
            resource = new File(mongoDistributionDirectory).toURI().toURL();
        }
        catch (final MalformedURLException e)
        {
            logger.error("Could not get local mongo distribution path", e);
        }
        return resource.toString();
    }

    public EmbeddedMongoConfiguration getConfiguration()
    {
        return configuration;
    }

    public String getMongoDistributionDirectory()
    {
        return configuration.getDistributionDirectory();
    }
}
