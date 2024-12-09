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

import com.mongodb.MongoClientSettings;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.connection.ClusterConnectionMode;
import com.mongodb.connection.ClusterType;
import de.flapdoodle.embed.mongo.client.ClientActions;
import de.flapdoodle.embed.mongo.client.SyncClientAdapter;
import de.flapdoodle.embed.mongo.commands.MongodArguments;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.config.Storage;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.mongo.transitions.ImmutableMongod;
import de.flapdoodle.embed.mongo.transitions.Mongod;
import de.flapdoodle.embed.mongo.transitions.RunningMongodProcess;
import de.flapdoodle.reverse.Listener;
import de.flapdoodle.reverse.TransitionWalker;
import de.flapdoodle.reverse.transitions.Start;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

/**
 * Allows the port and download location of the mongo distribution to be configured Also ensures that only a single
 * embedded mongo instance is started for all tests
 *
 * @author Kieron Edwards
 */
public class EmbeddedMongo
{
    private final static Logger logger = LoggerFactory.getLogger(EmbeddedMongo.class);

    private static EmbeddedMongo staticReference;

    private TransitionWalker.ReachedState<RunningMongodProcess> runningMongod;

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
        overrideConfigurationWithEnvironmentVariables();
        overrideConfigurationWithSystemProperties();
    }

    /**
     * This will automatically allocated a free port to the mongo instance
     */
    public EmbeddedMongo()
    {
        configuration = new EmbeddedMongoConfiguration();
        overrideConfigurationWithEnvironmentVariables();
        overrideConfigurationWithSystemProperties();
    }

    /**
     * If you need to configure embedded mongo distribution and db settings use this constructor
     */
    public EmbeddedMongo(EmbeddedMongoConfiguration configuration)
    {
        this.configuration = configuration;
        overrideConfigurationWithEnvironmentVariables();
        overrideConfigurationWithSystemProperties();
    }

    private void overrideConfigurationWithEnvironmentVariables()
    {
        final String envHttpProxyHost = System.getenv(EmbeddedMongoConfiguration.HTTP_PROXY_HOST);
        final String envHttpProxyPort = System.getenv(EmbeddedMongoConfiguration.HTTP_PROXY_PORT);
        if ( envHttpProxyHost != null )
        {
            configuration.setHttpProxyHost(envHttpProxyHost);
        }
        if ( envHttpProxyPort != null )
        {
            configuration.setHttpProxyPort(envHttpProxyPort);
        }
    }

    private void overrideConfigurationWithSystemProperties()
    {
        final String sysCustomMongoDatabaseDir = System.getProperty(
            EmbeddedMongoConfiguration.CUSTOM_MONGO_DATABASE_DIRECTORY);
        final String sysLocalMongoDistDir = System.getProperty(EmbeddedMongoConfiguration.LOCAL_MONGO_DIST_DIR_PROP);
        final String sysCustomMongoVersion = System.getProperty(EmbeddedMongoConfiguration.CUSTOM_MONGO_VERSION);
        final String sysCustomMongoArchiveStorageDir = System.getProperty(
            EmbeddedMongoConfiguration.CUSTOM_MONGO_ARCHIVE_STORAGE_DIRECTORY);
        final String sysCustomMongoPort = System.getProperty(EmbeddedMongoConfiguration.CUSTOM_MONGO_PORT);
        final String sysHttpProxyHost = System.getProperty(EmbeddedMongoConfiguration.HTTP_PROXY_HOST);
        final String sysHttpProxyPort = System.getProperty(EmbeddedMongoConfiguration.HTTP_PROXY_PORT);
        if ( sysCustomMongoDatabaseDir != null )
        {
            configuration.setDatabaseDirectory(sysCustomMongoDatabaseDir);
        }
        if ( sysLocalMongoDistDir != null )
        {
            configuration.setDistributionDirectory(sysLocalMongoDistDir);
        }
        if ( sysCustomMongoVersion != null )
        {
            configuration.setVersion(sysCustomMongoVersion);
        }
        if ( sysCustomMongoArchiveStorageDir != null )
        {
            configuration.setArchiveStorageDirectory((sysCustomMongoArchiveStorageDir));
        }
        if ( sysCustomMongoPort != null )
        {
            configuration.setPort(Integer.valueOf(sysCustomMongoPort));
        }
        if ( sysHttpProxyHost != null )
        {
            configuration.setHttpProxyHost(sysHttpProxyHost);
        }
        if ( sysHttpProxyPort != null )
        {
            configuration.setHttpProxyPort(sysHttpProxyPort);
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
            if ( staticReference == null )
            {
                Version.Main version = Version.Main.V6_0;
                Storage storage = Storage.of("rs0", 5000);

                Listener withRunningMongod = ClientActions.initReplicaSet(new SyncClientAdapter(), version, storage);

                ImmutableMongod.Builder mongodBuilder = Mongod.builder().mongodArguments(Start.to(MongodArguments.class)
                                                                                              .initializedWith(
                                                                                                  MongodArguments.defaults()
                                                                                                                 .withUseNoPrealloc(
                                                                                                                     false)
                                                                                                                 .withUseSmallFiles(
                                                                                                                     false)
                                                                                                                 .withUseNoJournal(
                                                                                                                     false)
                                                                                                                 .withEnableTextSearch(
                                                                                                                     false)
                                                                                                                 .withIsConfigServer(
                                                                                                                     false)
                                                                                                                 .withAuth(
                                                                                                                     false)
                                                                                                                 .withReplication(
                                                                                                                     storage)))
                                                              .net(Start.to(Net.class).initializedWith(
                                                                  Net.defaults().withPort(configuration.getPort())));

                runningMongod = mongodBuilder.build().start(version, withRunningMongod);
                String host = runningMongod.current().getServerAddress().getHost();
                int port = runningMongod.current().getServerAddress().getPort();
                logger.info("Embedded Mongo started on [{}:{}]", host, port);
                staticReference = this;

                try (com.mongodb.client.MongoClient client = MongoClients.create("mongodb://" + host + ":" + port))
                {
                    await().atMost(10, TimeUnit.SECONDS).pollInterval(1, TimeUnit.SECONDS)
                           .until(() -> isReplicaSetReady(client));
                }
                mongoClient = MongoClients.create(MongoClientSettings.builder().applyToClusterSettings(
                    builder -> builder.requiredClusterType(ClusterType.REPLICA_SET)
                                      .hosts(List.of(new ServerAddress(host, port))).requiredReplicaSetName("rs0")
                                      .mode(ClusterConnectionMode.MULTIPLE)).build());

            }
            return staticReference.mongoClient;
        }
    }

    private Boolean isReplicaSetReady(MongoClient mongoClient)
    {
        final String adminDb = "admin";

        double replSetStatusOk = (double) mongoClient.getDatabase(adminDb)
                                                     .runCommand(new Document("replSetGetStatus", 1)).get("ok");
        if ( replSetStatusOk == 1.0 )
        {
            logger.debug("ReplStatusOK is 1.0");
            boolean currentIsMaster = (boolean) mongoClient.getDatabase(adminDb).runCommand(new Document("isMaster", 1))
                                                           .get("ismaster");
            if ( !currentIsMaster )
            {
                logger.debug("Replica set is not ready. Waiting for node to become master.");
            }
            else
            {
                logger.debug("Replica set is ready. Node is now master.");
            }
            return currentIsMaster;
        }
        else
        {
            logger.debug("Replica set is not ready. Waiting for replStatusOK to be 1.0. Currently {}", replSetStatusOk);
            return false;
        }
    }

    public static void stop()
    {
        synchronized (EmbeddedMongo.class)
        {
            if ( staticReference != null )
            {
                staticReference.runningMongod.current().stop();
                staticReference = null;
            }
        }
    }

    public EmbeddedMongoConfiguration getConfiguration()
    {
        return configuration;
    }

}
