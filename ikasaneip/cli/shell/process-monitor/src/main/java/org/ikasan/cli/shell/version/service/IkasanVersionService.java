package org.ikasan.cli.shell.version.service;

import org.ikasan.cli.shell.version.dao.IkasanVersionPersistenceDaoImpl;
import org.ikasan.cli.shell.version.model.IkasanVersion;

public interface IkasanVersionService {

    /**
     * Returns an instance of IkasanVersionService.
     *
     * @return an instance of IkasanVersionService
     */
    static IkasanVersionService instance(String persistenceDir) {
        return new IkasanVersionServiceImpl(new IkasanVersionPersistenceDaoImpl(persistenceDir+"/version_manifest"));
    }

    /**
     * Writes the Ikasan version to the persistent file system.
     *
     * @param version to be saved.
     */
    void writeVersion(String version);

    /**
     * Finds the IkasanVersion object.
     *
     * @return the found IkasanVersion object, or null if not found
     */
    IkasanVersion find();


    /**
     * Deletes IkasanVersion.
     */
    void delete();
}
