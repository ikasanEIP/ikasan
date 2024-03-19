package org.ikasan.cli.shell.version.service;

import org.ikasan.cli.shell.version.dao.IkasanVersionPersistenceDao;
import org.ikasan.cli.shell.version.model.IkasanVersion;

public class IkasanVersionServiceImpl implements IkasanVersionService {

    private IkasanVersionPersistenceDao ikasanVersionPersistenceDao;

    public IkasanVersionServiceImpl(IkasanVersionPersistenceDao ikasanVersionPersistenceDao) {
        this.ikasanVersionPersistenceDao = ikasanVersionPersistenceDao;
    }

    @Override
    public void writeVersion(String version) {
        IkasanVersion ikasanVersion = new IkasanVersion();
        ikasanVersion.setVersion(version);
        this.ikasanVersionPersistenceDao.save(ikasanVersion);
    }

    @Override
    public IkasanVersion find() {
        return this.ikasanVersionPersistenceDao.find();
    }

    @Override
    public void delete() {
        this.ikasanVersionPersistenceDao.delete();
    }
}
