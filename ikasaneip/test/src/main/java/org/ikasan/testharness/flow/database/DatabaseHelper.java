package org.ikasan.testharness.flow.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseHelper {

    private static Logger logger = LoggerFactory.getLogger(DatabaseHelper.class);

    private final DataSource ikasanDataSource;

    public DatabaseHelper(DataSource ikasanDataSource) {
        this.ikasanDataSource = ikasanDataSource;
    }

    public static final String[] MAIN_TABLES_TO_CLEAR_BETWEEN_TESTS = new String[]{
        "CONFPARAMMAPSTRING", "CONFPARAMSTRING",
        "CONFPARAMMASKEDSTRING", "CONFPARAMMAP",
        "CONFPARAMMAP", "CONFPARAMLONG", "CONFPARAMLISTSTRING", "CONFPARAMLIST", "CONFPARAMINTEGER", "CONFPARAMBOOLEAN",
        "CONFIGURATIONPARAMETER", "CONFIGURATION",
        "ERROROCCURRENCE", "EXCLUSIONEVENT", "MESSAGEFILTER", "IKASANWIRETAP"
    };

    public static final String[] EXTENDED_TABLES_TO_CLEAR_BETWEEN_TESTS = new String[]{
        "CONFPARAMMAPSTRING", "CONFPARAMSTRING",
        "CONFPARAMMASKEDSTRING", "CONFPARAMMAP",
        "CONFPARAMMAP", "CONFPARAMLONG", "CONFPARAMLISTSTRING", "CONFPARAMLIST", "CONFPARAMINTEGER", "CONFPARAMBOOLEAN",
        "CONFIGURATIONPARAMETER", "CONFIGURATION",
        "ERROROCCURRENCE", "EXCLUSIONEVENT", "MESSAGEFILTER", "IKASANWIRETAP","FTFileChunk","FTFileChunkHeader"
    };


    /**
     * Clears all data from tables that may have been added between tests
     */
    public void clearDatabase() throws SQLException {
        clearDatabase(Arrays.asList(MAIN_TABLES_TO_CLEAR_BETWEEN_TESTS));
    }

    /**
     * Clears all data from tables that may have been added between tests
     */
    public void clearExtendedDatabaseTables() throws SQLException {
        clearDatabase(Arrays.asList(EXTENDED_TABLES_TO_CLEAR_BETWEEN_TESTS));
    }


    public void clearDatabase(List<String> tablesToClear) throws SQLException {
        StopWatch clearDbStopWatch = new StopWatch("Clear database timer");
        clearDbStopWatch.start();

        try (Connection connection = ikasanDataSource.getConnection()) {
            Map<String, Integer> deletedCountsMap = new HashMap<>();
            for (String table : tablesToClear) {
                PreparedStatement deleteStatement = connection.prepareStatement("DELETE FROM PUBLIC."
                    + table.toUpperCase());
                Integer deletedNumber = deleteStatement.executeUpdate();
                deletedCountsMap.put(table, deletedNumber);
            }
            connection.commit();
            clearDbStopWatch.stop();
            logger.info("Time taken to clear ikasan db [{}] millis - deleted following [{}]",
                clearDbStopWatch.getLastTaskTimeMillis(), deletedCountsMap);
        }
    }


}
