package org.ikasan.configurationService.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

public class XAConfigurationDaoHibernateImpl extends AbstractConfigurationDaoHibernateImpl {
    @PersistenceContext(unitName = "xa-configuration-service")
    private EntityManager entityManager;

    @Override
    public EntityManager getEntityManager() {
        return this.entityManager;
    }
}
