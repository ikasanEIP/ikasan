package org.ikasan.backup.h2.persistence.service;

import org.ikasan.backup.h2.persistence.dao.H2DatabaseBackupManifestPersistenceDaoImpl;
import org.ikasan.backup.h2.persistence.model.H2DatabaseBackupManifest;

import java.nio.file.FileSystems;

public interface H2DatabaseBackupManifestService {

    /**
     * Returns an instance of H2DatabaseBackupManifestService.
     * @param persistenceDir the persistence directory path
     * @param databaseName the database name
     *
     * @return an instance of H2DatabaseBackupManifestService
     */
    static H2DatabaseBackupManifestService instance(String persistenceDir, String databaseName) {
        return new H2DatabaseBackupManifestServiceImpl(new H2DatabaseBackupManifestPersistenceDaoImpl
                (persistenceDir+FileSystems.getDefault().getSeparator()+"manifest"
                        , databaseName+"_h2_database_backup.mf"));
    }

    /**
     * Saves the given H2DatabaseBackupManifest object.
     *
     * @param h2DatabaseBackupManifest the H2DatabaseBackupManifest object to be saved
     */
    void save(H2DatabaseBackupManifest h2DatabaseBackupManifest);

    /**
     * Finds the H2DatabaseBackupManifest object.
     *
     * @return the found H2DatabaseBackupManifest object, or null if not found
     */
    H2DatabaseBackupManifest find();


    /**
     * Deletes H2DatabaseBackupManifest for the given database.
     */
    void delete();
}
