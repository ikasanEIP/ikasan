package org.ikasan.persistence.service;

import org.ikasan.spec.persistence.dao.GeneralDatabaseDao;
import org.ikasan.spec.persistence.service.GeneralDatabaseService;

public class GeneralDatabaseServiceImpl implements GeneralDatabaseService {

    private GeneralDatabaseDao generalDatabaseDao;


    /**
     * Constructs a new instance of the GeneralDatabaseServiceImpl class.
     *
     * @param generalDatabaseDao the GeneralDatabaseDao implementation used for database operations.
     */
    public GeneralDatabaseServiceImpl(GeneralDatabaseDao generalDatabaseDao) {
        this.generalDatabaseDao = generalDatabaseDao;
        if(this.generalDatabaseDao == null) {
            throw new IllegalArgumentException("generalDatabaseDao cannot be null!");
        }
    }

    @Override
    public int getRecordCountForDatabaseTable(String tableName) {
        return this.generalDatabaseDao.getRecordCountForDatabaseTable(tableName);
    }
}
