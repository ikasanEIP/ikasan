package org.ikasan.spec.persistence.dao;

public interface GeneralDatabaseDao {

    /**
     * Retrieves the total record count for a given database table.
     *
     * @param tableName the name of the database table.
     * @return the total record count for the specified database table.
     */
    public int getRecordCountForDatabaseTable(String tableName);
}
