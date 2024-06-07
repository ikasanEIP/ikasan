package org.ikasan.cli.shell.migration.service;

import org.ikasan.cli.shell.migration.dao.MigrationPersistenceDao;
import org.ikasan.cli.shell.migration.model.IkasanMigration;

public class DefaultMigrationServiceImpl implements MigrationService{

    private MigrationPersistenceDao migrationPersistenceDao;

    /**
     * Implementation of the MigrationService interface.
     */
    public DefaultMigrationServiceImpl(MigrationPersistenceDao migrationPersistenceDao) {
        this.migrationPersistenceDao = migrationPersistenceDao;
        if(this.migrationPersistenceDao == null) {
            throw new IllegalArgumentException("processPersistenceDao cannot be 'null'");
        }
    }

    @Override
    public void save(IkasanMigration ikasanMigration) {
        this.migrationPersistenceDao.save(ikasanMigration);
    }

    @Override
    public IkasanMigration find(String type, String sourceVersion, String targetVersion, String label) {
        return this.migrationPersistenceDao.find(type, sourceVersion, targetVersion, label);
    }

    @Override
    public void delete(String type, String sourceVersion, String targetVersion, String label) {
        if(type == null || sourceVersion == null || targetVersion == null) {
            throw new IllegalArgumentException("type, sourceVersion or targetVersion cannot be null!");
        }
        this.migrationPersistenceDao.delete(type, sourceVersion, targetVersion, label);
    }
}
