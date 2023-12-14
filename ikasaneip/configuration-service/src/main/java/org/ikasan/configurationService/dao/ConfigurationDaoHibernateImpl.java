package org.ikasan.configurationService.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

public class ConfigurationDaoHibernateImpl extends AbstractConfigurationDaoHibernateImpl {
    @PersistenceContext(unitName = "configuration-service")
    private EntityManager entityManager;

    @Override
    public EntityManager getEntityManager() {
        return this.entityManager;
    }

}
