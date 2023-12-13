package org.ikasan.configurationService.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.ikasan.spec.configuration.Configuration;
import org.ikasan.spec.configuration.ConfigurationParameter;

import java.util.List;

public class XAConfigurationDaoHibernateImpl extends AbstractConfigurationDaoHibernateImpl {
    @PersistenceContext(unitName = "xa-configuration-service")
    private EntityManager entityManager;

    @Override
    public EntityManager getEntityManager() {
        return this.entityManager;
    }

//    /* (non-Javadoc)
//     * @see org.ikasan.framework.configuration.dao.ConfigurationDao#saveConfiguration(org.ikasan.framework.configuration.window.Configuration)
//     */
//    public void save(Configuration<List<ConfigurationParameter>> configuration)
//    {
//        // work-around for Sybase issue where it converts empty strings to single spaces.
//        // See http://open.jira.com/browse/IKASAN-520
//        // Where we would have persisted "" change this to a null to stop Sybase
//        // inserting a single space character.
//        if("".equals(configuration.getDescription()))
//        {
//            configuration.setDescription(null);
//        }
//
//        configuration.getParameters().forEach(configurationParameter->
//        {
//            if("".equals(configurationParameter.getValue()))
//            {
//                configurationParameter.setValue(null);
//            }
//
//
//            if("".equals(configurationParameter.getDescription()))
//            {
//                configurationParameter.setDescription(null);
//            }
//        });
//
//        // hibernate mutates the object and amends configurations Params with Id
//        this.getEntityManager().persist(this.getEntityManager().contains(configuration)
//            ? configuration : this.getEntityManager().merge(configuration));
//    }
//
//    /* (non-Javadoc)
//     * @see org.ikasan.framework.configuration.dao.ConfigurationDao#deleteConfiguration(org.ikasan.framework.configuration.window.Configuration)
//     */
//    public void delete(Configuration<List<ConfigurationParameter>> configuration)
//    {
//        this.getEntityManager().remove(this.getEntityManager().contains(configuration)
//            ? configuration : this.getEntityManager().merge(configuration));
//    }
}
