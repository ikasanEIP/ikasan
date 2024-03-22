package org.ikasan.backup.h2.service;

import org.h2.tools.Server;
import org.ikasan.backup.h2.exception.H2DatabaseValidationException;
import org.ikasan.backup.h2.exception.InvalidH2ConnectionUrlException;
import org.ikasan.backup.h2.model.H2DatabaseBackup;
import org.ikasan.backup.h2.util.H2BackupUtils;
import org.ikasan.backup.h2.util.H2ConnectionUrlUtils;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.housekeeping.HousekeepService;
import org.ikasan.spec.module.Module;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.util.TestSocketUtils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
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
    public static final String TEST_DIRECTORY = BACKUP_DIRECTORY
        + FileSystems.getDefault().getSeparator() + "test-directory";
    private static final String BACKUP_QUERY = "BACKUP TO '";

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd-HH-mm-ss");

    private H2DatabaseBackup h2DatabaseBackup;

    private Module<Flow> module;

    private boolean stopFlowsOnCorruptDatabaseDetection;

    /**
     * Constructs an instance of H2BackupServiceImpl.
     *
     * @param h2DatabaseBackup the H2 database backup details
     *
     * @throws IllegalArgumentException    if h2DatabaseBackupDetails or persistenceDirectory is null
     */
    public H2BackupServiceImpl(H2DatabaseBackup h2DatabaseBackup, Module<Flow> module,
                               boolean stopFlowsOnCorruptDatabaseDetection) {
        this.h2DatabaseBackup = h2DatabaseBackup;
        if(this.h2DatabaseBackup == null) {
            throw new IllegalArgumentException("h2DatabaseBackupDetails cannot be null!");
        }
        this.module = module;
        if(this.module == null) {
            throw new IllegalArgumentException("module cannot be null!");
        }

        this.stopFlowsOnCorruptDatabaseDetection = stopFlowsOnCorruptDatabaseDetection;
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
        this.createBackupDirectoryIfItDoesNotExist();

        String backupFilePath = null;

        try (Connection con = DriverManager.getConnection(h2DatabaseBackup.getDbUrl()
            , h2DatabaseBackup.getUsername(), h2DatabaseBackup.getPassword())) {
            backupFilePath = this.getBackupFilePath(h2DatabaseBackup.getDbUrl());

            con.prepareStatement(this.buildDatabaseBackupQuery(backupFilePath)).executeUpdate();

            this.runDatabaseValidationTest(backupFilePath);

            this.rollRetainedBackupFiles();
        } catch(Exception e) {
            if(backupFilePath != null) {
                deleteFile(backupFilePath);
            }

            if(this.stopFlowsOnCorruptDatabaseDetection) {
                logger.warn("All flows are stopping due to an error validating the backed up H2 database! " +
                    "This indicates that the database is corrupt!");
                this.module.getFlows().forEach(flow -> flow.stop());
                logger.error("An error has occurred validating a H2 database backup! As a preventative action" +
                    " all flows have been stopped!", e);
            }
            else {
                logger.error("An error has occurred validating a H2 database backup! The flows will NOT be stopped as " +
                    "property [h2.backup.stop.flows.on.corrupt.database.detection=false]. " +
                    "This indicates that the database is corrupt!", e);
            }
        }
    }



    /**
     * Runs a database validation test using the provided backup file.
     *
     * @param backupFileName the name of the backup file to use for the test
     *
     * @throws URISyntaxException if a URI syntax exception occurs
     * @throws SQLException if an SQL exception occurs
     * @throws IOException if an I/O exception occurs
     * @throws H2DatabaseValidationException if a database validation exception occurs
     */
    protected void runDatabaseValidationTest(String backupFileName) throws InvalidH2ConnectionUrlException, SQLException
        , IOException, H2DatabaseValidationException {
        String testDbFilePath = this.h2DatabaseBackup.getDbBackupBaseDirectory() + FileSystems.getDefault().getSeparator()
            + TEST_DIRECTORY;
        int port = TestSocketUtils.findAvailableTcpPort();
        String testDbUrl = this.getTestDbUrl(h2DatabaseBackup.getDbUrl(), Integer.toString(port), testDbFilePath);
        Server server = Server.createTcpServer("-tcpPort", Integer.toString(port), "-tcpAllowOthers");
        this.unzipBackedUpDatabaseFile(backupFileName);
        server.start();
        this.testDb(testDbUrl, h2DatabaseBackup);
        server.stop();
        this.cleanTestDirectory();
    }

    /**
     * Tests the provided database connection by creating a test table, executing a select count(*) query on it,
     * and then dropping the test table.
     *
     * @param testDbUrl the URL of the test database
     * @param h2DatabaseBackupDetails the details of the H2 database backup
     *
     * @throws H2DatabaseValidationException if a database validation exception occurs
     */
    private void testDb(String testDbUrl, H2DatabaseBackup h2DatabaseBackupDetails) throws H2DatabaseValidationException {
        try (Connection con = DriverManager.getConnection(testDbUrl
            , h2DatabaseBackupDetails.getUsername(), h2DatabaseBackupDetails.getPassword())) {
            Statement statement = con.createStatement();
            this.createDatabaseTable("TestTable", con);
            ResultSet resultSet = statement.executeQuery("select count(*) as count from TestTable");
            resultSet.next();
            con.prepareStatement("drop TABLE TestTable").executeUpdate();
        } catch(SQLException ex) {
            throw new H2DatabaseValidationException("An error has occurred validating the backed up H2 database!", ex);
        }
    }

    /**
     * Creates a database table with the provided name using the given connection.
     *
     * @param tableName   the name of the table to be created
     * @param connection  the database connection
     *
     * @throws SQLException if a SQL exception occurs
     */
    private void createDatabaseTable(String tableName, Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute(String.format(CREATE_TABLE_SQL, tableName, tableName + "_PK"));
    }

    /**
     * Unzips a backed up database file.
     *
     * @param backupFileName the name of the backup file to unzip
     *
     * @throws IOException if an I/O error occurs during unzipping
     */
    private void unzipBackedUpDatabaseFile(String backupFileName) throws IOException {
        H2BackupUtils.unzipFile(backupFileName, this.h2DatabaseBackup.getDbBackupBaseDirectory()
            + FileSystems.getDefault().getSeparator() + TEST_DIRECTORY);
    }

    /**
     * Cleans the test directory by deleting all files and directories within it.
     *
     * @throws IOException if an I/O error occurs during file deletion.
     */
    private void cleanTestDirectory() throws IOException {
        H2BackupUtils.cleanDirectory(this.h2DatabaseBackup.getDbBackupBaseDirectory()
            + FileSystems.getDefault().getSeparator() + TEST_DIRECTORY);
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
     * Returns the URL for testing a backed up database.
     *
     * @param url the original connection URL
     * @param port the port to be used for the test
     * @param testDbFilePath the file path for the test database
     *
     * @return the URL for testing the backed up database
     *
     * @throws URISyntaxException if a URI syntax exception occurs
     */
    private String getTestDbUrl(String url, String port, String testDbFilePath) throws InvalidH2ConnectionUrlException {
        return H2ConnectionUrlUtils.createTestUrl(url, port, testDbFilePath);
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
}
