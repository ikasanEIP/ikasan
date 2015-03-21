/*
 * $Id: HibernateMappingConfigurationDao.java 44074 2015-03-17 10:43:23Z stewmi $
 * $URL: https://svc-vcs-prd.uk.mizuho-sc.com:18080/svn/architecture/cmi2/trunk/projects/mappingConfigurationService/api/src/main/java/com/mizuho/cmi2/mappingConfiguration/dao/HibernateMappingConfigurationDao.java $
 *
 * ====================================================================
 *
 * Copyright (c) 2000-2012 by Mizuho International plc.
 * All Rights Reserved.
 *
 * ====================================================================
 *
 */
package org.ikasan.mapping.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.ikasan.mapping.dao.constants.MappingConfigurationDaoConstants;
import org.ikasan.mapping.model.ConfigurationContext;
import org.ikasan.mapping.model.ConfigurationServiceClient;
import org.ikasan.mapping.model.ConfigurationType;
import org.ikasan.mapping.model.KeyLocationQuery;
import org.ikasan.mapping.model.MappingConfiguration;
import org.ikasan.mapping.model.MappingConfigurationLite;
import org.ikasan.mapping.model.SourceConfigurationGroupSequence;
import org.ikasan.mapping.model.SourceConfigurationValue;
import org.ikasan.mapping.model.TargetConfigurationValue;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;


/**
 * @author CMI2 Development Team
 *
 */
public class HibernateMappingConfigurationDao extends HibernateDaoSupport implements MappingConfigurationDao
{
    private static final Long ID = new Long(1);

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.stateModel.dao.MappingConfigurationDao#getTargetConfigurationValue(java.lang.String, java.lang.String, java.lang.String, java.util.List)
     */
    @Override
    public String getTargetConfigurationValue(final String clientName, final String configurationType, final String sourceSystem
            , final String targetSystem, final List<String> sourceSystemValues)
    {
        return (String)this.getHibernateTemplate().execute(new HibernateCallback()
        {
            @SuppressWarnings("unchecked")
            public Object doInHibernate(Session session) throws HibernateException
            {
                Query query = session.createQuery(buildQueryString(sourceSystemValues));
                query.setParameter(MappingConfigurationDaoConstants.CONFIGURATION_TYPE, configurationType);
                query.setParameter(MappingConfigurationDaoConstants.SOURCE_CONTEXT, sourceSystem);
                query.setParameter(MappingConfigurationDaoConstants.TARGET_CONTEXT, targetSystem);
                query.setParameter(MappingConfigurationDaoConstants.NUMBER_OF_PARAMS, new Long(sourceSystemValues.size()));
                query.setParameter(MappingConfigurationDaoConstants.CONFIGURATION_SERVICE_CLIENT_NAME, clientName);

                int i=0;
                for(String sourceSystemValue: sourceSystemValues)
                {
                    query.setParameter(MappingConfigurationDaoConstants.SOURCE_SYSTEM_VALUE + i, sourceSystemValue);
                    i++;
                }

                List<String> results = (List<String>)query.list();

                if(results.size() == 0)
                {
                    return null;
                }
                else
                {
                    return results.get(0);
                }
            }
        });
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.stateModel.dao.MappingConfigurationDao#getTargetConfigurationValue(java.lang.String, java.lang.String, java.lang.String, java.util.List)
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<String> getKeyLocationQuery(final String configurationType, final String sourceSystem, final String targetSystem,
            final String configurationServiceClientName)
    {
        return (List<String>)this.getHibernateTemplate().execute(new HibernateCallback()
        {
            public Object doInHibernate(Session session) throws HibernateException
            {
                Query query = session.createQuery(MappingConfigurationDaoConstants.KEY_LOCATION_QUERY_QUERY);
                query.setParameter(MappingConfigurationDaoConstants.CONFIGURATION_TYPE, configurationType);
                query.setParameter(MappingConfigurationDaoConstants.SOURCE_CONTEXT, sourceSystem);
                query.setParameter(MappingConfigurationDaoConstants.TARGET_CONTEXT, targetSystem);
                query.setParameter(MappingConfigurationDaoConstants.CONFIGURATION_SERVICE_CLIENT_NAME, configurationServiceClientName);

                return (List<String>)query.list();
            }
        });
    }

    /**
     * Helper method to build the query string used to query the mapping
     * configuration data. 
     * 
     * @param sourceSystemValues
     * @return
     */
    private String buildQueryString(List<String> sourceSystemValues)
    {
        StringBuffer query = new StringBuffer(MappingConfigurationDaoConstants.MAPPING_CONFIGURATION_QUERY);

        for(int i=0; i<sourceSystemValues.size(); i++)
        {
            query.append(MappingConfigurationDaoConstants.NARROW_SOURCE_SYSTEM_FRAGMENT).append(i).append(") ");
        }

        return query.toString();
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.mappingConfiguration.dao.MappingConfigurationDao#getConfigurationServiceClientByName(java.lang.String)
     */
    @Override
    public ConfigurationServiceClient getConfigurationServiceClientByName(String configurationServiceClientName)
    {
        DetachedCriteria criteria = DetachedCriteria.forClass(ConfigurationServiceClient.class);
        criteria.add(Restrictions.eq("name", configurationServiceClientName));
        return (ConfigurationServiceClient) DataAccessUtils.uniqueResult(this.getHibernateTemplate().findByCriteria(criteria));
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.mappingConfiguration.dao.MappingConfigurationDao#storeConfigurationType(com.mizuho.cmi2.mappingConfiguration.model.ConfigurationType)
     */
    @Override
    public Long storeConfigurationType(ConfigurationType configurationType)
    {
        configurationType.setUpdatedDateTime(new Date());
        this.getHibernateTemplate().saveOrUpdate(configurationType);

        return configurationType.getId();
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.mappingConfiguration.dao.MappingConfigurationDao#storeConfigurationContext(com.mizuho.cmi2.mappingConfiguration.model.ConfigurationContext)
     */
    @Override
    public Long storeMappingConfiguration(MappingConfiguration configurationContext) throws DataAccessException
    {
        configurationContext.setUpdatedDateTime(new Date());
        this.getHibernateTemplate().saveOrUpdate(configurationContext);

        return configurationContext.getId();
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.mappingConfiguration.dao.MappingConfigurationDao#storeSourceConfigurationValue(com.mizuho.cmi2.mappingConfiguration.model.SourceConfigurationValue)
     */
    @Override
    public Long storeSourceConfigurationValue(SourceConfigurationValue sourceConfigurationValue)
    {
        sourceConfigurationValue.setUpdatedDateTime(new Date());
        this.getHibernateTemplate().saveOrUpdate(sourceConfigurationValue);

        return sourceConfigurationValue.getId();
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.mappingConfiguration.dao.MappingConfigurationDao#storeTargetConfigurationValue(com.mizuho.cmi2.mappingConfiguration.model.TargetConfigurationValue)
     */
    @Override
    public Long storeTargetConfigurationValue(TargetConfigurationValue targetConfigurationValue)
    {
        targetConfigurationValue.setUpdatedDateTime(new Date());
        this.getHibernateTemplate().saveOrUpdate(targetConfigurationValue);

        return targetConfigurationValue.getId();
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.mappingConfiguration.dao.MappingConfigurationDao#storeConfigurationServiceClient(com.mizuho.cmi2.mappingConfiguration.model.ConfigurationServiceClient)
     */
    @Override
    public Long storeConfigurationServiceClient(ConfigurationServiceClient configurationServiceClient)
    {
        configurationServiceClient.setUpdatedDateTime(new Date());
        this.getHibernateTemplate().saveOrUpdate(configurationServiceClient);

        return configurationServiceClient.getId();
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.mappingConfiguration.dao.MappingConfigurationDao#storeKeyLocationQuery(com.mizuho.cmi2.mappingConfiguration.model.KeyLocationQuery)
     */
    @Override
    public Long storeKeyLocationQuery(KeyLocationQuery keyLocationQuery)
    {
        keyLocationQuery.setUpdatedDateTime(new Date());
        this.getHibernateTemplate().saveOrUpdate(keyLocationQuery);

        return keyLocationQuery.getId();
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.mappingConfiguration.dao.MappingConfigurationDao#storeConfigurationContext(com.mizuho.cmi2.mappingConfiguration.model.ConfigurationContext)
     */
    @Override
    public Long storeConfigurationContext(ConfigurationContext configurationContext)
    {
        configurationContext.setUpdatedDateTime(new Date());
        this.getHibernateTemplate().saveOrUpdate(configurationContext);

        return configurationContext.getId();
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.mappingConfiguration.dao.MappingConfigurationDao#getAllConfigurationTypes()
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<ConfigurationType> getAllConfigurationTypes()
    {
        DetachedCriteria criteria = DetachedCriteria.forClass(ConfigurationType.class);

        return (List<ConfigurationType>) this.getHibernateTemplate().findByCriteria(criteria);
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.mappingConfiguration.dao.MappingConfigurationDao#getAllConfigurationContexts()
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<ConfigurationContext> getAllConfigurationContexts()
    {
        DetachedCriteria criteria = DetachedCriteria.forClass(ConfigurationContext.class);

        return (List<ConfigurationContext>) this.getHibernateTemplate().findByCriteria(criteria);
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.mappingConfiguration.dao.MappingConfigurationDao#getAllConfigurationServiceClients()
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<ConfigurationServiceClient> getAllConfigurationServiceClients()
    {
        DetachedCriteria criteria = DetachedCriteria.forClass(ConfigurationServiceClient.class);

        return (List<ConfigurationServiceClient>) this.getHibernateTemplate().findByCriteria(criteria);
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.mappingConfiguration.dao.MappingConfigurationDao#getMappingConfigurationById(java.lang.Long)
     */
    @Override
    public MappingConfiguration getMappingConfigurationById(Long id)
    {
        DetachedCriteria criteria = DetachedCriteria.forClass(MappingConfiguration.class);
        criteria.add(Restrictions.eq("id", id));

        return (MappingConfiguration)DataAccessUtils.uniqueResult(this.getHibernateTemplate().findByCriteria(criteria));
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.mappingConfiguration.dao.MappingConfigurationDao#getMappingConfiguration(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public MappingConfiguration getMappingConfiguration(final String clientName, final String mappingConfigurationType,
            final String sourceContextName, final String targetContextName)
    {
        return (MappingConfiguration)this.getHibernateTemplate().execute(new HibernateCallback()
        {
            @SuppressWarnings("unchecked")
            public Object doInHibernate(Session session) throws HibernateException
            {
                Query query = session.createQuery(MappingConfigurationDaoConstants.MAPPING_CONFIGURATION_BY_CLIENT_TYPE_AND_CONTEXT_QUERY);
                query.setParameter(MappingConfigurationDaoConstants.CONFIGURATION_TYPE, mappingConfigurationType);
                query.setParameter(MappingConfigurationDaoConstants.SOURCE_CONTEXT, sourceContextName);
                query.setParameter(MappingConfigurationDaoConstants.TARGET_CONTEXT, targetContextName);
                query.setParameter(MappingConfigurationDaoConstants.CONFIGURATION_SERVICE_CLIENT_NAME, clientName);

                List<MappingConfiguration> mappingConfigurations =  (List<MappingConfiguration>)query.list();

                if(mappingConfigurations.size() == 0)
                {
                    return null;
                }
                else
                {
                    return mappingConfigurations.get(0);
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
	@Override
    public List<MappingConfiguration> getMappingConfigurations(final String clientName, final String mappingConfigurationType,
            final String sourceContextName, final String targetContextName)
    {
        logger.info("Using new query for mapping configuration search.");
        return (List<MappingConfiguration>)this.getHibernateTemplate().execute(new HibernateCallback()
        {
            @SuppressWarnings("unchecked")
            public Object doInHibernate(Session session) throws HibernateException
            {
                StringBuffer queryString = new StringBuffer(MappingConfigurationDaoConstants.MAPPING_CONFIGURATION_BASE_QUERY);

                if(clientName != null && clientName.trim().length() > 0)
                {
                    queryString.append(MappingConfigurationDaoConstants.CONFIGURATION_CLIENT_PREDICATE);
                }
                
                if(mappingConfigurationType != null && mappingConfigurationType.trim().length() > 0)
                {
                    queryString.append(MappingConfigurationDaoConstants.CONFIGURATION_TYPE_PREDICATE);
                }
                
                if(sourceContextName != null && sourceContextName.trim().length() > 0)
                {
                    queryString.append(MappingConfigurationDaoConstants.SOURCE_CONTEXT_PREDICATE);
                }
                
                if(targetContextName != null && targetContextName.trim().length() > 0)
                {
                    queryString.append(MappingConfigurationDaoConstants.TARGET_CONTEXT_PREDICATE);
                }

                Query query = session.createQuery(queryString.toString());

                if(clientName != null && clientName.trim().length() > 0)
                {
                    query.setParameter(MappingConfigurationDaoConstants.CONFIGURATION_SERVICE_CLIENT_NAME, clientName);
                }
                
                if(mappingConfigurationType != null && mappingConfigurationType.trim().length() > 0)
                {
                    query.setParameter(MappingConfigurationDaoConstants.CONFIGURATION_TYPE, mappingConfigurationType);
                }
                
                if(sourceContextName != null && sourceContextName.trim().length() > 0)
                {
                    query.setParameter(MappingConfigurationDaoConstants.SOURCE_CONTEXT, sourceContextName);
                }
                
                if(targetContextName != null && targetContextName.trim().length() > 0)
                {
                    query.setParameter(MappingConfigurationDaoConstants.TARGET_CONTEXT, targetContextName);
                }

                return (List<MappingConfiguration>)query.list();
            }
        });
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<MappingConfigurationLite> getMappingConfigurationLites(final String clientName, final String mappingConfigurationType,
            final String sourceContextName, final String targetContextName)
    {
        logger.info("Using new query for mapping configuration search.");
        return (List<MappingConfigurationLite>)this.getHibernateTemplate().execute(new HibernateCallback()
        {
            @SuppressWarnings("unchecked")
            public Object doInHibernate(Session session) throws HibernateException
            {
                StringBuffer queryString = new StringBuffer(MappingConfigurationDaoConstants.MAPPING_CONFIGURATION_LITE_BASE_QUERY);

                if(clientName != null && clientName.trim().length() > 0)
                {
                    queryString.append(MappingConfigurationDaoConstants.CONFIGURATION_CLIENT_PREDICATE);
                }
                
                if(mappingConfigurationType != null && mappingConfigurationType.trim().length() > 0)
                {
                    queryString.append(MappingConfigurationDaoConstants.CONFIGURATION_TYPE_PREDICATE);
                }
                
                if(sourceContextName != null && sourceContextName.trim().length() > 0)
                {
                    queryString.append(MappingConfigurationDaoConstants.SOURCE_CONTEXT_PREDICATE);
                }
                
                if(targetContextName != null && targetContextName.trim().length() > 0)
                {
                    queryString.append(MappingConfigurationDaoConstants.TARGET_CONTEXT_PREDICATE);
                }

                Query query = session.createQuery(queryString.toString());

                if(clientName != null && clientName.trim().length() > 0)
                {
                    query.setParameter(MappingConfigurationDaoConstants.CONFIGURATION_SERVICE_CLIENT_NAME, clientName);
                }
                
                if(mappingConfigurationType != null && mappingConfigurationType.trim().length() > 0)
                {
                    query.setParameter(MappingConfigurationDaoConstants.CONFIGURATION_TYPE, mappingConfigurationType);
                }
                
                if(sourceContextName != null && sourceContextName.trim().length() > 0)
                {
                    query.setParameter(MappingConfigurationDaoConstants.SOURCE_CONTEXT, sourceContextName);
                }
                
                if(targetContextName != null && targetContextName.trim().length() > 0)
                {
                    query.setParameter(MappingConfigurationDaoConstants.TARGET_CONTEXT, targetContextName);
                }

                return (List<MappingConfiguration>)query.list();
            }
        });
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.mappingConfiguration.dao.MappingConfigurationDao#getMappingConfigurationsByConfigurationServiceClientId(java.lang.Long)
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<MappingConfiguration> getMappingConfigurationsByConfigurationServiceClientId(
            Long configurationServiceClientId)
    {
        DetachedCriteria criteria = DetachedCriteria.forClass(MappingConfiguration.class);
        criteria.add(Restrictions.eq("configurationServiceClient.id", configurationServiceClientId));

        return (List<MappingConfiguration>)this.getHibernateTemplate().findByCriteria(criteria);
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.mappingConfiguration.dao.MappingConfigurationDao#getMappingConfigurationsByConfigurationTypeId(java.lang.Long)
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<MappingConfiguration> getMappingConfigurationsByConfigurationTypeId(Long configurationTypeId)
    {
        DetachedCriteria criteria = DetachedCriteria.forClass(MappingConfiguration.class);
        criteria.add(Restrictions.eq("configurationType.id", configurationTypeId));

        return (List<MappingConfiguration>)this.getHibernateTemplate().findByCriteria(criteria);
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.mappingConfiguration.dao.MappingConfigurationDao#getMappingConfigurationsBySourceContextId(java.lang.Long)
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<MappingConfiguration> getMappingConfigurationsBySourceContextId(Long sourceContextId)
    {
        DetachedCriteria criteria = DetachedCriteria.forClass(MappingConfiguration.class);
        criteria.add(Restrictions.eq("sourceContext.id", sourceContextId));

        return (List<MappingConfiguration>)this.getHibernateTemplate().findByCriteria(criteria);
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.mappingConfiguration.dao.MappingConfigurationDao#getMappingConfigurationsByTargetContextId(java.lang.Long)
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<MappingConfiguration> getMappingConfigurationsByTargetContextId(Long targetContextId)
    {
        DetachedCriteria criteria = DetachedCriteria.forClass(MappingConfiguration.class);
        criteria.add(Restrictions.eq("targetContext.id", targetContextId));

        return (List<MappingConfiguration>)this.getHibernateTemplate().findByCriteria(criteria);
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.mappingConfiguration.dao.MappingConfigurationDao#getConfigurationContextById(java.lang.Long)
     */
    @Override
    public ConfigurationContext getConfigurationContextById(Long id)
    {
        DetachedCriteria criteria = DetachedCriteria.forClass(ConfigurationContext.class);
        criteria.add(Restrictions.eq("id", id));

        return (ConfigurationContext)DataAccessUtils.uniqueResult(this.getHibernateTemplate().findByCriteria(criteria));
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.mappingConfiguration.dao.MappingConfigurationDao#getConfigurationServiceClientById(java.lang.Long)
     */
    @Override
    public ConfigurationServiceClient getConfigurationServiceClientById(Long id)
    {
        DetachedCriteria criteria = DetachedCriteria.forClass(ConfigurationServiceClient.class);
        criteria.add(Restrictions.eq("id", id));

        return (ConfigurationServiceClient)DataAccessUtils.uniqueResult(this.getHibernateTemplate().findByCriteria(criteria));
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.mappingConfiguration.dao.MappingConfigurationDao#getConfigurationTypeById(java.lang.Long)
     */
    @Override
    public ConfigurationType getConfigurationTypeById(Long id)
    {
        DetachedCriteria criteria = DetachedCriteria.forClass(ConfigurationType.class);
        criteria.add(Restrictions.eq("id", id));

        return (ConfigurationType)DataAccessUtils.uniqueResult(this.getHibernateTemplate().findByCriteria(criteria));
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.mappingConfiguration.dao.MappingConfigurationDao#getKeyLocationQueriesByMappingConfigurationId(java.lang.Long)
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<KeyLocationQuery> getKeyLocationQueriesByMappingConfigurationId(Long mappingConfigurationId)
    {
        DetachedCriteria criteria = DetachedCriteria.forClass(KeyLocationQuery.class);
        criteria.add(Restrictions.eq("mappingConfigurationId", mappingConfigurationId));

        return (List<KeyLocationQuery>)this.getHibernateTemplate().findByCriteria(criteria);
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.mappingConfiguration.dao.MappingConfigurationDao#getSourceConfigurationValueByMappingConfigurationId(java.lang.Long)
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<SourceConfigurationValue> getSourceConfigurationValueByMappingConfigurationId(
            Long mappingConfigurationId)
    {
        DetachedCriteria criteria = DetachedCriteria.forClass(SourceConfigurationValue.class);
        criteria.add(Restrictions.eq("mappingConfigurationId", mappingConfigurationId));

        return (List<SourceConfigurationValue>)this.getHibernateTemplate().findByCriteria(criteria);
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.mappingConfiguration.dao.MappingConfigurationDao#getTargetConfigurationValueById(java.lang.Long)
     */
    @Override
    public TargetConfigurationValue getTargetConfigurationValueById(Long id)
    {
        DetachedCriteria criteria = DetachedCriteria.forClass(TargetConfigurationValue.class);
        criteria.add(Restrictions.eq("id", id));

        return (TargetConfigurationValue)DataAccessUtils.uniqueResult(this.getHibernateTemplate().findByCriteria(criteria));
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.mappingConfiguration.dao.MappingConfigurationDao#getAllMappingCongigurations()
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<MappingConfiguration> getAllMappingConfigurations()
    {
        DetachedCriteria criteria = DetachedCriteria.forClass(MappingConfiguration.class);

        return (List<MappingConfiguration>) this.getHibernateTemplate().findByCriteria(criteria);
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.mappingConfiguration.dao.MappingConfigurationDao#getTargetConfigurationValueByMappingConfigurationId(java.lang.Long)
     */
    @Override
    public List<SourceConfigurationValue> getSourceConfigurationValuesByTargetConfigurationValueId(
            Long targetConfigurationValueId)
    {
        DetachedCriteria criteria = DetachedCriteria.forClass(SourceConfigurationValue.class);
        criteria.add(Restrictions.eq("targetConfigurationValue.id", targetConfigurationValueId));

        return (List<SourceConfigurationValue>)this.getHibernateTemplate().findByCriteria(criteria);
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.mappingConfiguration.dao.MappingConfigurationDao#deleteSourceConfigurationValue(com.mizuho.cmi2.mappingConfiguration.model.SourceConfigurationValue)
     */
    @Override
    public void deleteSourceConfigurationValue(SourceConfigurationValue sourceConfigurationValue)
    {
        this.getHibernateTemplate().delete(sourceConfigurationValue);
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.mappingConfiguration.dao.MappingConfigurationDao#deleteTargetConfigurationValue(com.mizuho.cmi2.mappingConfiguration.model.TargetConfigurationValue)
     */
    @Override
    public void deleteTargetConfigurationValue(TargetConfigurationValue targetConfigurationValue)
    {
        this.getHibernateTemplate().delete(targetConfigurationValue);
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.swiftAlliance.swiftGateway.dao.LauSequenceDao#getLauSequence()
     */
    @Override
    public SourceConfigurationGroupSequence getSourceConfigurationGroupSequence()
    {
        DetachedCriteria criteria = DetachedCriteria.forClass(SourceConfigurationGroupSequence.class);
        criteria.add(Restrictions.eq("id", ID));

        return (SourceConfigurationGroupSequence) DataAccessUtils.uniqueResult(this.getHibernateTemplate().findByCriteria(criteria));
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.swiftAlliance.swiftGateway.dao.LauSequenceDao#saveLauSequence(com.mizuho.cmi2.swiftAlliance.swiftGateway.model.LauSequence)
     */
    @Override
    public void saveSourceConfigurationGroupSequence(SourceConfigurationGroupSequence sequence)
    {
            sequence.setId(ID);
            this.getHibernateTemplate().saveOrUpdate(sequence);
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.mappingConfiguration.dao.MappingConfigurationDao#getNumberOfSourceConfigurationValuesReferencingTargetConfigurationValue(com.mizuho.cmi2.mappingConfiguration.model.TargetConfigurationValue)
     */
    @Override
    public Long getNumberOfSourceConfigurationValuesReferencingTargetConfigurationValue(
            final TargetConfigurationValue targetConfigurationValue)
    {
        return (Long)this.getHibernateTemplate().execute(new HibernateCallback()
        {
            @SuppressWarnings("unchecked")
            public Object doInHibernate(Session session) throws HibernateException
            {
                Query query = session.createQuery(MappingConfigurationDaoConstants.NUMBER_OF_SOURCE_CONFIGURATION_VALUES_REFERENCING_TARGET_CONFIGURATION_VALUE);
                query.setParameter(MappingConfigurationDaoConstants.TARGET_CONFIGURATION_VALUE_ID, targetConfigurationValue.getId());

                return (Long)query.uniqueResult();
            }
        });
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.mappingConfiguration.dao.MappingConfigurationDao#mappingConfigurationExists(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public Long getNumberOfMappingConfigurations(final String clientName, final String mappingConfigurationType,
            final String sourceContextName, final String targetContextName)
    {
        return (Long)this.getHibernateTemplate().execute(new HibernateCallback()
        {
            @SuppressWarnings("unchecked")
            public Object doInHibernate(Session session) throws HibernateException
            {
                Query query = session.createQuery(MappingConfigurationDaoConstants.MAPPING_CONFIGURATION_EXISTS_QUERY);
                query.setParameter(MappingConfigurationDaoConstants.CONFIGURATION_TYPE, mappingConfigurationType);
                query.setParameter(MappingConfigurationDaoConstants.SOURCE_CONTEXT, sourceContextName);
                query.setParameter(MappingConfigurationDaoConstants.TARGET_CONTEXT, targetContextName);
                query.setParameter(MappingConfigurationDaoConstants.CONFIGURATION_SERVICE_CLIENT_NAME, clientName);

                return (Long)query.uniqueResult();
            }
        });
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.mappingConfiguration.dao.MappingConfigurationDao#deleteMappingConfiguration(com.mizuho.cmi2.mappingConfiguration.model.MappingConfiguration)
     */
    @Override
    public void deleteMappingConfiguration(MappingConfiguration mappingConfiguration)
    {
        List<KeyLocationQuery> keyLocationQueries = this.getKeyLocationQueriesByMappingConfigurationId(mappingConfiguration.getId());

        for(KeyLocationQuery query: keyLocationQueries)
        {
            this.getHibernateTemplate().delete(query);
        }

        this.getHibernateTemplate().delete(mappingConfiguration);
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.mappingConfiguration.dao.MappingConfigurationDao#getConfigurationTypeByName(java.lang.String)
     */
    @Override
    public ConfigurationType getConfigurationTypeByName(String name)
    {
        DetachedCriteria criteria = DetachedCriteria.forClass(ConfigurationType.class);
        criteria.add(Restrictions.eq("name", name));
        return (ConfigurationType) DataAccessUtils.uniqueResult(this.getHibernateTemplate().findByCriteria(criteria));

    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.mappingConfiguration.dao.MappingConfigurationDao#getConfigurationContextByName(java.lang.String)
     */
    @Override
    public ConfigurationContext getConfigurationContextByName(String name)
    {
        DetachedCriteria criteria = DetachedCriteria.forClass(ConfigurationContext.class);
        criteria.add(Restrictions.eq("name", name));
        return (ConfigurationContext) DataAccessUtils.uniqueResult(this.getHibernateTemplate().findByCriteria(criteria));
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.mappingConfiguration.dao.MappingConfigurationDao#getConfigurationTypesByClientName(java.lang.String)
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<ConfigurationType> getConfigurationTypesByClientName(final String clientName)
    {
        return (List<ConfigurationType>)this.getHibernateTemplate().execute(new HibernateCallback()
        {
            public Object doInHibernate(Session session) throws HibernateException
            {
                Query query = session.createQuery(MappingConfigurationDaoConstants.NARROW_CONFIGURATION_TYPE_BASE_QUERY
                    + MappingConfigurationDaoConstants.CONFIGURATION_CLIENT_PREDICATE);
                query.setParameter(MappingConfigurationDaoConstants.CONFIGURATION_SERVICE_CLIENT_NAME, clientName);

                return (List<ConfigurationType>)query.list();
            }
        });
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.mappingConfiguration.dao.MappingConfigurationDao#getSourceConfigurationContextByClientNameAndType(java.lang.String, java.lang.String)
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<ConfigurationContext> getSourceConfigurationContextByClientNameAndType(final String clientName, final String type)
    {
        return (List<ConfigurationContext>)this.getHibernateTemplate().execute(new HibernateCallback()
        {
            public Object doInHibernate(Session session) throws HibernateException
            {
                StringBuffer queryString = new StringBuffer(MappingConfigurationDaoConstants.NARROW_SOURCE_CONFIGURATION_BASE_QUERY);

                if(clientName != null && clientName.length() > 0)
                {
                    queryString.append(MappingConfigurationDaoConstants.CONFIGURATION_CLIENT_PREDICATE);
                }

                if(type != null && type.length() > 0)
                {
                    queryString.append(MappingConfigurationDaoConstants.CONFIGURATION_TYPE_PREDICATE);
                }

                Query query = session.createQuery(queryString.toString());

                if(clientName != null && clientName.length() > 0)
                {
                    query.setParameter(MappingConfigurationDaoConstants.CONFIGURATION_SERVICE_CLIENT_NAME, clientName);
                }

                if(type != null && type.length() > 0)
                {
                    query.setParameter(MappingConfigurationDaoConstants.CONFIGURATION_TYPE, type);
                }

                return (List<ConfigurationContext>)query.list();
            }
        });
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.mappingConfiguration.dao.MappingConfigurationDao#getTargetConfigurationContextByClientNameTypeAndSourceContext(java.lang.String, java.lang.String, java.lang.String)
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<ConfigurationContext> getTargetConfigurationContextByClientNameTypeAndSourceContext(final String clientName,
            final String type, final String sourceContext)
    {
        return (List<ConfigurationContext>)this.getHibernateTemplate().execute(new HibernateCallback()
        {
            public Object doInHibernate(Session session) throws HibernateException
            {
                StringBuffer queryString = new StringBuffer(MappingConfigurationDaoConstants.NARROW_TARGET_CONFIGURATION_BASE_QUERY);

                if(clientName != null && clientName.length() > 0)
                {
                    queryString.append(MappingConfigurationDaoConstants.CONFIGURATION_CLIENT_PREDICATE);
                }

                if(type != null && type.length() > 0)
                {
                    queryString.append(MappingConfigurationDaoConstants.CONFIGURATION_TYPE_PREDICATE);
                }

                if(sourceContext != null && sourceContext.length() > 0)
                {
                    queryString.append(MappingConfigurationDaoConstants.SOURCE_SYSTEM_PREDICATE);
                }

                Query query = session.createQuery(queryString.toString());

                if(clientName != null && clientName.length() > 0)
                {
                    query.setParameter(MappingConfigurationDaoConstants.CONFIGURATION_SERVICE_CLIENT_NAME, clientName);
                }

                if(type != null && type.length() > 0)
                {
                    query.setParameter(MappingConfigurationDaoConstants.CONFIGURATION_TYPE, type);
                }

                if(sourceContext != null && sourceContext.length() > 0)
                {
                    query.setParameter(MappingConfigurationDaoConstants.SOURCE_SYSTEM_VALUE, sourceContext);
                }

                return (List<ConfigurationContext>)query.list();
            }
        });
    }

}
