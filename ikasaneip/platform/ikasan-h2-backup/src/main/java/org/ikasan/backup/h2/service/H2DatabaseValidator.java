package org.ikasan.backup.h2.service;

import org.ikasan.backup.h2.exception.H2DatabaseValidationException;
import org.ikasan.backup.h2.exception.InvalidH2ConnectionUrlException;
import org.ikasan.backup.h2.model.H2DatabaseBackup;
import org.ikasan.backup.h2.util.H2BackupUtils;
import org.ikasan.backup.h2.util.H2ConnectionUrlUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.FileSystems;
import java.sql.*;

public class H2DatabaseValidator {

    private static final String CREATE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS %s ( \n" +
            " IDENTIFIER  VARCHAR NOT NULL, \n" +
            "   CONSTRAINT %s \n" +
            "     PRIMARY KEY (IDENTIFIER))";

    public static final String TEST_DIRECTORY = "db-backup"
            + FileSystems.getDefault().getSeparator() + "test-directory";

    private H2DatabaseBackup h2DatabaseBackup;

    public H2DatabaseValidator(H2DatabaseBackup h2DatabaseBackup) {
        this.h2DatabaseBackup = h2DatabaseBackup;
        if(this.h2DatabaseBackup == null) {
            throw new IllegalArgumentException("h2DatabaseBackup cannot be null!");
        }
    }

    /**
     * Runs a database validation test using the provided backup file.
     *
     * @param backupFileName the name of the backup file to use for the test
     * @param portNumber the port number to run the db validation test on
     *
     * @throws InvalidH2ConnectionUrlException if the database URL is invalid
     * @throws SQLException if an SQL exception occurs
     * @throws IOException if an I/O exception occurs
     * @throws H2DatabaseValidationException if a database validation exception occurs
     */
    protected void runDatabaseValidationTest(String backupFileName, int portNumber) throws InvalidH2ConnectionUrlException, SQLException
            , IOException, H2DatabaseValidationException {
        String testDbFilePath = this.h2DatabaseBackup.getDbBackupBaseDirectory() + FileSystems.getDefault().getSeparator()
                + TEST_DIRECTORY;
        String testDbUrl = this.getTestDbUrl(h2DatabaseBackup.getDbUrl(), Integer.toString(portNumber), testDbFilePath);
        this.unzipBackedUpDatabaseFile(backupFileName);
        this.testDb(testDbUrl, h2DatabaseBackup);
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

}
