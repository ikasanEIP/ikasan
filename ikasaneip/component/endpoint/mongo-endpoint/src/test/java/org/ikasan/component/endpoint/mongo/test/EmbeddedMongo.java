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
 * @author Ikasan Development Team
 */
public class EmbeddedMongo
{
    private final static Logger logger = LoggerFactory
			.getLogger(EmbeddedMongo.class);
    
    private MongodExecutable mongodExecutable;

    private boolean useLocalMongoDistribution;

    private static EmbeddedMongo staticReference;

    private MongoClient mongoClient;

    private final int port;

    private String mongoDistributionDirectory;
    


	public int getPort() {
        return port;
    }

    public String getMongoDistributionDirectory() {
        return mongoDistributionDirectory;
    }

    /** CHECK SYSTEM PROPERTIES FOR A LOCAL DIRECTORY TO GET MONGO DB DIST FROM **/
    public static final String LOCAL_MONGO_DIST_DIR_PROP="ikasan.localMongoDistDirProperty";
    
    public static final String CUSTOM_MONGO_DATABASE_DIRECTORY="ikasan.flapdoodle.customMongoDatabaseDir";
    
    public static final String CUSTOM_MONGO_ARCHIVE_STORAGE_DIRECTORY="ikasan.flapdoodle.customMongoArchiveStorageDir";
    
    public static final String CUSTOM_MONGO_VERSION="ikasan.flapdoodle.customMongoVersion";
    /**
     * 
     * @param port
     * @param mongoDistributionDirectory
     */
    
    public EmbeddedMongo(int port)
    {
        super();
        this.port = port;
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
                
                DownloadConfigBuilder downloadConfigBuilder=setupDownloadConfigBuilder(command);
                IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder()
                        .defaults(command)
                        .artifactStore(
                            new ArtifactStoreBuilder().defaults(command).
                            download(downloadConfigBuilder.build()))
                                    .build();
                    runtime = MongodStarter.getInstance(runtimeConfig);
                    ;
                try
                {
                    MongodConfigBuilder mongodConfigBuilder=setupMongodConfigBuilder();
                	mongodExecutable = runtime.prepare(mongodConfigBuilder.build());
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

    private MongodConfigBuilder setupMongodConfigBuilder() throws UnknownHostException, IOException {
    	MongodConfigBuilder builder = new MongodConfigBuilder();
        builder=builder.net(new Net(port, Network.localhostIsIPv6()));
        String customMongoDatabaseDirectory=System.getProperty(CUSTOM_MONGO_DATABASE_DIRECTORY);
        String customMongoVersion=System.getProperty(CUSTOM_MONGO_VERSION);

        if (customMongoDatabaseDirectory!=null){
            logger.info("Custom mongo database dir set to [{}]",customMongoDatabaseDirectory);
            builder.replication(new Storage(customMongoDatabaseDirectory,null,0));
        }
        if (customMongoVersion!= null){
            logger.info("Custom mongo version set to [{}]",customMongoVersion);
        	builder.version(Versions.withFeatures(new GenericVersion(customMongoVersion),Feature.SYNC_DELAY));
        } else {
        	builder.version(Version.Main.PRODUCTION);
        }
      
        return builder;
    }

	private DownloadConfigBuilder setupDownloadConfigBuilder(Command command) {
    	DownloadConfigBuilder builder = new DownloadConfigBuilder();
        builder=builder.defaultsForCommand(command);
        String localMongoDistDir=System.getProperty(LOCAL_MONGO_DIST_DIR_PROP);
        String customMongoArchiveDownloadDirectory=System.getProperty(CUSTOM_MONGO_ARCHIVE_STORAGE_DIRECTORY);
        if (localMongoDistDir!=null){
            logger.info("Custom local mongo dist dir set to [{}]",localMongoDistDir);
        	builder.downloadPath(
                getLocalMongoDistributionPath(localMongoDistDir));
        }
        if (customMongoArchiveDownloadDirectory != null){
            logger.info("Custom mongo artifact storage dir set to [{}]",customMongoArchiveDownloadDirectory);
        	builder.artifactStorePath(getIDirectory(customMongoArchiveDownloadDirectory));
        }
        return builder;
    }

	private IDirectory getIDirectory(final String customMongoArchiveDownloadDirectory) {
		return new IDirectory(){

			@Override
			public File asFile() {
				return new File(customMongoArchiveDownloadDirectory);
			}

			@Override
			public boolean isGenerated() {
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
        catch (MalformedURLException e)
        {
            logger.error("Could not get local mongo distribution path", e);
        }
        return resource.toString();
    }
}

