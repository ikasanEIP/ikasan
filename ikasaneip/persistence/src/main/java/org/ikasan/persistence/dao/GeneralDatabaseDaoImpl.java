package org.ikasan.persistence.dao;

import org.ikasan.spec.persistence.dao.GeneralDatabaseDao;

import javax.sql.DataSource;
import java.sql.*;

public class GeneralDatabaseDaoImpl implements GeneralDatabaseDao {
    public static final String TABLE_COUNT_QUERY = "SELECT COUNT(*) AS COUNT FROM %s";
    private DataSource dataSource;

    /**
     * Constructs a GeneralDatabaseDaoImpl object with the specified DataSource.
     *
     * @param dataSource the DataSource to be used by the GeneralDatabaseDaoImpl object
     * @throws IllegalArgumentException if the dataSource is null
     */
    public GeneralDatabaseDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
        if(this.dataSource == null) {
            throw new IllegalArgumentException("dataSource cannot be null!");
        }
    }

    @Override
    public int getRecordCountForDatabaseTable(String tableName) {
        Connection connection = null;
        int count = 0;
        try {
            connection = dataSource.getConnection();
            DatabaseMetaData databaseMetaData = connection.getMetaData();

            ResultSet resultSet = databaseMetaData.getTables(null, null
                , null, new String[] {"TABLE"});

            boolean tableNameExists = false;
            while (resultSet.next()) {
                String name = resultSet.getString("TABLE_NAME");
                String schema = resultSet.getString("TABLE_SCHEM");
                String fullyQualifiedName = schema + "." + name;
                if(name.equalsIgnoreCase(tableName) || fullyQualifiedName.equalsIgnoreCase(tableName)) {
                    tableNameExists=true;
                    break;
                }
            }

            if(!tableNameExists) {
                throw new RuntimeException(String.format("An exception has occurred querying count for table[%s]! The " +
                    "table does not exist in the database", tableName));
            }

            Statement statement = connection.createStatement();

            resultSet = statement.executeQuery(String.format(TABLE_COUNT_QUERY, tableName));

            resultSet.next();
            count = resultSet.getInt("COUNT");
        }
        catch (SQLException e) {
            throw new RuntimeException(String.format("An exception has occurred querying count for table[%s]!", tableName), e);
        }
        finally {
            try {
                connection.close();
            }
            catch (SQLException e) {
                throw new RuntimeException("An exception has occurred closing database connection!", e);
            }
        }

        return count;
    }
}
