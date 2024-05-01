package org.ikasan.backup.h2.service;

import org.ikasan.backup.h2.exception.InvalidH2ConnectionUrlException;
import org.ikasan.backup.h2.model.H2DatabaseBackup;
import org.ikasan.backup.h2.persistence.model.H2DatabaseBackupManifest;
import org.ikasan.backup.h2.persistence.service.H2DatabaseBackupManifestService;
import org.ikasan.backup.h2.util.H2BackupUtils;
import org.ikasan.backup.h2.util.H2ConnectionUrlUtils;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.housekeeping.HousekeepService;
import org.ikasan.spec.module.Module;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.SocketUtils;

import java.io.File;
import java.io.IOException;
import java.net.BindException;
import java.net.URISyntaxException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class H2BackupServiceImpl implements HousekeepService {

    private static Logger logger = LoggerFactory.getLogger(H2BackupServiceImpl.class);

    private static final String CREATE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS %s ( \n" +
        " IDENTIFIER  VARCHAR NOT NULL, \n" +
        "   CONSTRAINT %s \n" +
        "     PRIMARY KEY (IDENTIFIER))";

    public static final String BACKUP_DIRECTORY = "db-backup";

    private static final String BACKUP_QUERY = "BACKUP TO '";

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd-HH-mm-ss");

    private H2DatabaseBackup h2DatabaseBackup;

    private Module<Flow> module;

    private boolean stopFlowsOnCorruptDatabaseDetection;

    private int maxRetriesPortClash;
    private int h2BackupNumberOfInvalidBackupsBeforeStoppingFlows;
    private String databaseName;

    private H2DatabaseValidator h2DatabaseValidator;

    private H2DatabaseBackupManifestService h2DatabaseBackupManifestService;

    /**
     * Constructs an instance of H2BackupServiceImpl.
     *
     * @param h2DatabaseBackup the H2 database backup details
     * @param module the module we are managing flows on behalf of
     * @param stopFlowsOnCorruptDatabaseDetection flag to indicate we stop flows if corrupted db is encountered
     * @param maxRetriesPortClash max retries for a port clash when testing a db backup
     * @param h2BackupNumberOfInvalidBackupsBeforeStoppingFlows the number of failed backups before stopping flows in the module
     * @param h2DatabaseValidator the validator used to test the backed up database
     * @param h2DatabaseBackupManifestService the manifest service used to track db backup failures
     *
     * @throws IllegalArgumentException    if h2DatabaseBackupDetails or persistenceDirectory is null
     */
    public H2BackupServiceImpl(H2DatabaseBackup h2DatabaseBackup, Module<Flow> module,
                               boolean stopFlowsOnCorruptDatabaseDetection, int maxRetriesPortClash,
                               int h2BackupNumberOfInvalidBackupsBeforeStoppingFlows,
                               H2DatabaseValidator h2DatabaseValidator,
                               H2DatabaseBackupManifestService h2DatabaseBackupManifestService) {
        this.h2DatabaseBackup = h2DatabaseBackup;
        if(this.h2DatabaseBackup == null) {
            throw new IllegalArgumentException("h2DatabaseBackupDetails cannot be null!");
        }
        this.module = module;
        if(this.module == null) {
            throw new IllegalArgumentException("module cannot be null!");
        }
        this.h2DatabaseValidator = h2DatabaseValidator;
        if(this.h2DatabaseValidator == null) {
            throw new IllegalArgumentException("h2DatabaseValidator cannot be null!");
        }

        this.stopFlowsOnCorruptDatabaseDetection = stopFlowsOnCorruptDatabaseDetection;
        this.maxRetriesPortClash = maxRetriesPortClash;
        this.h2BackupNumberOfInvalidBackupsBeforeStoppingFlows
                = h2BackupNumberOfInvalidBackupsBeforeStoppingFlows;

        this.h2DatabaseBackupManifestService = h2DatabaseBackupManifestService;
        if(this.h2DatabaseBackupManifestService == null) {
            throw new IllegalArgumentException("h2DatabaseBackupManifestService cannot be null!");
        }

        try {
            this.databaseName = H2ConnectionUrlUtils.getDatabaseName(h2DatabaseBackup.getDbUrl());
        }
        catch (InvalidH2ConnectionUrlException e) {
            throw new IllegalArgumentException(h2DatabaseBackup.getDbUrl() + " is an invalid H2 URL", e);
        }
    }

    @Override
    public void housekeep() {
        this.backup();
    }

    @Override
    public boolean housekeepablesExist() {
        return true;
    }

    @Override
    public void setHousekeepingBatchSize(Integer housekeepingBatchSize) {
        // not needed
    }

    @Override
    public void setTransactionBatchSize(Integer transactionBatchSize) {
        // not needed
    }

    /**
     * Performs a backup of the H2 database.
     */
    public void backup() {
        H2DatabaseBackupManifest h2DatabaseBackupManifest = this.h2DatabaseBackupManifestService.find();
        if(h2DatabaseBackupManifest != null &&
                h2DatabaseBackupManifest.getRetryCount() >= this.h2BackupNumberOfInvalidBackupsBeforeStoppingFlows) {
            logger.info(String.format("H2 database backup[%s] has detected database corruption [%s] times which exceeds the" +
                    "configured corruption tolerance. There will be no further attempts to back up this database."
                    , this.h2DatabaseBackup.toString(), h2DatabaseBackupManifest.getRetryCount()));
            return;
        }

        this.createBackupDirectoryIfItDoesNotExist();

        String backupFilePath = null;

        try (Connection con = DriverManager.getConnection(h2DatabaseBackup.getDbUrl()
            , h2DatabaseBackup.getUsername(), h2DatabaseBackup.getPassword())) {
            backupFilePath = this.getBackupFilePath(h2DatabaseBackup.getDbUrl());

            con.prepareStatement(this.buildDatabaseBackupQuery(backupFilePath)).executeUpdate();

            int retry = 0;

            while(retry < this.maxRetriesPortClash) {
                try {
                    this.h2DatabaseValidator.runDatabaseValidationTest(backupFilePath
                            , retry == 0 ? this.h2DatabaseBackup.getTestH2Port() : SocketUtils.findAvailableTcpPort());
                    // We do not want to retry if no exception thrown so set retry to max.
                    retry = this.maxRetriesPortClash;
                }
                catch (SQLException sqlException) {
                    if(sqlException.getCause() instanceof BindException) {
                        logger.info(String.format("Encountered a bind exception attempting to test backed up H2 database[%s]. " +
                                "Message[%s], Retry[%s]", this.h2DatabaseBackup.toString(), sqlException.getCause().getMessage(), retry));
                        retry++;
                    }
                    else {
                        throw sqlException;
                    }

                    if(retry == this.maxRetriesPortClash) logger.info(String.format("Could not test backed up database due to the number" +
                            " of port clashes exceeding [%s]! The number of retries can be configured using property " +
                            "h2.backup.database.test.port.bind.retries if the retry number needs to be increased beyond the default of 5 fr" +
                             " database backup [%s].", this.maxRetriesPortClash, this.h2DatabaseBackup));
                }
            }

            this.rollRetainedBackupFiles();
            // we remove the manifest off the file system if there is one if the backup was successful.
            this.h2DatabaseBackupManifestService.delete();
        } catch(Exception e) {
            if(backupFilePath != null) {
                deleteFile(backupFilePath);
            }

            h2DatabaseBackupManifest = this.getH2DatabaseBackupManifest();
            h2DatabaseBackupManifest.setFailureMessage(e.getMessage());
            h2DatabaseBackupManifest.setRetryCount(h2DatabaseBackupManifest.getRetryCount()+1);
            h2DatabaseBackupManifest.setFailureTimestamp(System.currentTimeMillis());

            this.h2DatabaseBackupManifestService.save(h2DatabaseBackupManifest);

            if(this.stopFlowsOnCorruptDatabaseDetection
                    &&  h2DatabaseBackupManifest.getRetryCount() >= this.h2BackupNumberOfInvalidBackupsBeforeStoppingFlows) {
                logger.warn(String.format("All flows are stopping due to an error validating the backed up H2 database [%s]! " +
                    "This indicates that the database is corrupt!", this.h2DatabaseBackup.toString()));
                this.module.getFlows().forEach(flow -> flow.stop());
                logger.error(String.format("An error has occurred validating a H2 database backup! As a preventative action" +
                    " all flows have been stopped for backup [%s]!", this.h2DatabaseBackup.toString()), e);
            }
            else if (this.stopFlowsOnCorruptDatabaseDetection == false
                    &&  h2DatabaseBackupManifest.getRetryCount() >= this.h2BackupNumberOfInvalidBackupsBeforeStoppingFlows){
                logger.warn(String.format("An error has occurred validating a H2 database backup! The flows will NOT be stopped as " +
                    "property [h2.backup.stop.flows.on.corrupt.database.detection=false]. " +
                    "This indicates that the database is corrupt! The service is configured to re-attempt [%s] times before," +
                        "discontinuing taking backups for [%s].", this.h2BackupNumberOfInvalidBackupsBeforeStoppingFlows
                        , this.h2DatabaseBackup.toString()));
            }
            else {
                logger.info(String.format("An error has occurred validating a H2 database backup! " +
                                "This indicates that the database is corrupt! The service is configured to re-attempt [%s] times before," +
                                " discontinuing taking backups for %s.", this.h2BackupNumberOfInvalidBackupsBeforeStoppingFlows
                        , this.h2DatabaseBackup.toString()));
            }
        }
    }

    private H2DatabaseBackupManifest getH2DatabaseBackupManifest() {
        H2DatabaseBackupManifest h2DatabaseBackupManifest = this.h2DatabaseBackupManifestService.find();
        if(h2DatabaseBackupManifest == null) {
            h2DatabaseBackupManifest = new H2DatabaseBackupManifest();
            h2DatabaseBackupManifest.setBackupName(this.databaseName);
            h2DatabaseBackupManifest.setRetryCount(0);
        }

        return h2DatabaseBackupManifest;
    }

    /**
     * Rolls the retained backup files based on the number of backup files to keep.
     *
     * @throws IOException if an I/O exception occurs
     */
    private void rollRetainedBackupFiles() throws IOException {
        Path dir = Paths.get(this.h2DatabaseBackup.getDbBackupBaseDirectory()
            + FileSystems.getDefault().getSeparator() + BACKUP_DIRECTORY);

        List<Path> files = Files
            .walk(dir, 1)
            .map(path -> path.toFile())
            .sorted(Comparator.comparingLong(File::lastModified))
            .filter(file -> file.isFile() && file.getAbsolutePath().endsWith(".zip"))
            .map(file -> file.toPath())
            .collect(Collectors.toList());

        if(files.size() > this.h2DatabaseBackup.getNumOfBackupsToRetain()) {
            for(int i = 0; i<files.size()-this.h2DatabaseBackup.getNumOfBackupsToRetain(); i++) {
                Files.delete(files.get(i));
            }
        }
    }

    /**
     * Builds the query for database backup.
     * The backup query consists of the backup query prefix followed by the backup file name.
     *
     * @param backupFileName the name of the backup file
     *
     * @return the database backup query
     */
    private String buildDatabaseBackupQuery(String backupFileName) {
        return BACKUP_QUERY + backupFileName + "'";
    }

    /**
     * Creates a backup directory if it does not exist. The backup directory path is constructed by appending the backup
     * directory name to the persistence directory. It also creates a directory safe guard file within the backup directory
     * to ensure that the directory is not accidentally deleted.
     */
    private void createBackupDirectoryIfItDoesNotExist() {
        File persistenceDirFile = new File(this.getBackupDirectory());
        if(!persistenceDirFile.exists()) {
            persistenceDirFile.mkdirs();
            File directorySafeGuardFile = new File(this.getBackupDirectory() +
                FileSystems.getDefault().getSeparator() + "DO_NOT_DELETE_ANY_FILES_IN_THIS_DIRECTORY");
            if(!directorySafeGuardFile.exists()) {
                try {
                    directorySafeGuardFile.createNewFile();
                }
                catch (IOException e) {
                    logger.warn("An error has occurred creating the directory safe guard file!", e);
                }
            }
        }
    }

    /**
     * Returns the file path for the backup file.
     *
     * @param connectionUrl the URL of the database connection
     *
     * @return the file path for the backup file
     *
     * @throws URISyntaxException if a URI syntax exception occurs
     */
    private String getBackupFilePath(String connectionUrl) throws InvalidH2ConnectionUrlException {
        return this.getBackupDirectory() + FileSystems.getDefault().getSeparator()
            + H2ConnectionUrlUtils.getDatabaseName(connectionUrl)
            + "-backup-" + DATE_FORMAT.format(new Date()) + ".zip";
    }

    /**
     * Returns the backup directory path.
     *
     * @return the backup directory path
     */
    private String getBackupDirectory() {
        return this.h2DatabaseBackup.getDbBackupBaseDirectory() +
            FileSystems.getDefault().getSeparator() + BACKUP_DIRECTORY;
    }

    /**
     * Deletes a file at the given file path.
     *
     * @param filePath the path of the file to be deleted
     */
    private void deleteFile(String filePath) {
        H2BackupUtils.deleteFile(filePath);
    }

    /**
     * Retrieves the H2 database backup details.
     *
     * @return the H2 database backup details
     */
    public H2DatabaseBackup getH2DatabaseBackup() {
        return h2DatabaseBackup;
    }

    /**
     * Get the H2 Database Backup Manifest Service
     *
     * @return the H2 Database Backup Manifest Service
     */
    protected H2DatabaseBackupManifestService getH2DatabaseBackupManifestService() {
        return h2DatabaseBackupManifestService;
    }
}
