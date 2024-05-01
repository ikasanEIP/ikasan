package org.ikasan.backup.h2.persistence.service;


import org.ikasan.backup.h2.persistence.dao.H2DatabaseBackupManifestPersistenceDao;
import org.ikasan.backup.h2.persistence.model.H2DatabaseBackupManifest;

public class H2DatabaseBackupManifestServiceImpl implements H2DatabaseBackupManifestService {

    private H2DatabaseBackupManifestPersistenceDao h2DatabaseBackupManifestPersistenceDao;

    public H2DatabaseBackupManifestServiceImpl(H2DatabaseBackupManifestPersistenceDao h2DatabaseBackupManifestPersistenceDao) {
        this.h2DatabaseBackupManifestPersistenceDao = h2DatabaseBackupManifestPersistenceDao;
    }

    @Override
    public void save(H2DatabaseBackupManifest h2DatabaseBackupManifest) {
        this.h2DatabaseBackupManifestPersistenceDao.save(h2DatabaseBackupManifest);
    }

    @Override
    public H2DatabaseBackupManifest find() {
        return this.h2DatabaseBackupManifestPersistenceDao.find();
    }

    @Override
    public void delete() {
        this.h2DatabaseBackupManifestPersistenceDao.delete();
    }
}
