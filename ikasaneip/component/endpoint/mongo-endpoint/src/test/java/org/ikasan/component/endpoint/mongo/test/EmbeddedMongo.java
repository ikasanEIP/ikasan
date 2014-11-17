package org.ikasan.component.endpoint.mongo.test;


import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

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
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.runtime.Network;

/**
 * Allows the port and download location of the mongo distribution to be configured Also ensures that only a single
 * embedded mongo instance is started for all tests
 * 
 * @author Ikasan Development Team
 */
public class EmbeddedMongo
{
    private final Logger logger = Logger.getLogger(EmbeddedMongo.class);

    private MongodExecutable mongodExecutable;

    private boolean useLocalMongoDistribution;

    private static EmbeddedMongo staticReference;

    private MongoClient mongoClient;

    private final int port;

    private final String mongoDistributionDirectory;

    public int getPort() {
		return port;
	}

	public String getMongoDistributionDirectory() {
		return mongoDistributionDirectory;
	}

	/** CHECK SYSTEM PROPERTIES FOR A LOCAL DIRECTORY TO GET MONGO DB DIST FROM **/
    public static final String LOCAL_MONGO_DIST_DIR_PROP="ikasan.localMongoDistDirProperty";
    
    public EmbeddedMongo(int port, String mongoDistributionDirectory)
    {
        super();
        this.port = port;
        this.mongoDistributionDirectory = mongoDistributionDirectory;
        if (mongoDistributionDirectory != null){
        	useLocalMongoDistribution = true;
        }
    }
    
    /**
     * Check to see if the local mongo distribution directory is
     * defined as a system property
     * 
     * @param port
     */
    public EmbeddedMongo(int port)
    {
        this(port, System.getProperty(LOCAL_MONGO_DIST_DIR_PROP));
    }

    /**
     * This will check for the singleton instance and wont instantiate a new mongo database each
     * time start is called by a test. 
     * 
     * @return
     */
    public MongoClient start()
    {
        synchronized (EmbeddedMongo.class)
        {
            if (staticReference == null)
            {
                Command command = Command.MongoD;
                MongodStarter runtime = null;
                if (useLocalMongoDistribution)
                {
                    IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder()
                        .defaults(command)
                        .artifactStore(
                            new ArtifactStoreBuilder().defaults(command).download(
                                new DownloadConfigBuilder().defaultsForCommand(command).downloadPath(
                                    getLocalMongoDistributionPath(mongoDistributionDirectory)))).build();
                    logger.info("ArtifactStore = " + runtimeConfig.getArtifactStore());
                    runtime = MongodStarter.getInstance(runtimeConfig);
                    ;
                }
                else
                {
                    runtime = MongodStarter.getDefaultInstance();
                }
                try
                {
                    mongodExecutable = runtime.prepare(new MongodConfigBuilder().version(Version.Main.PRODUCTION)
                        .net(new Net(port, Network.localhostIsIPv6())).build());
                    MongodProcess mongodProcess = mongodExecutable.start();
                    staticReference = this;
                    mongoClient = new MongoClient(new ServerAddress(mongodProcess.getConfig().net().getServerAddress(),
                        mongodProcess.getConfig().net().getPort()));
                }
                catch (IOException e)
                {
                    throw new RuntimeException("Unable to start embeddedMongo", e);
                }
            }
            return staticReference.mongoClient;
        }
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
        catch (MalformedURLException e)
        {
            logger.error("Could not get local mongo distribution path", e);
        }
        logger.info(String.format("Using the local mongo distribution [%1$s] ", resource));
        return resource.toString();
    }
}

