/*
 * $Id$
 * $URL$
 *
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 *
 * Distributed under the Modified BSD License.
 * Copyright notice: The copyright for this software and a full listing
 * of individual contributors are as shown in the packaged copyright.txt
 * file.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  - Neither the name of the ORGANIZATION nor the names of its contributors may
 *    be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 */
package org.ikasan.mapping.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.*;
import org.ikasan.mapping.dao.constants.MappingConfigurationDaoConstants;
import org.ikasan.mapping.model.*;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate4.HibernateCallback;
import org.springframework.orm.hibernate4.support.HibernateDaoSupport;





/**
 * @author Ikasan Development Team
 *
 */
public class HibernateMappingConfigurationDao extends HibernateDaoSupport implements MappingConfigurationDao
{
	private static final Long ID = new Long(1);

    /* (non-Javadoc)
         * @see com.mizuho.cmi2.stateModel.dao.MappingConfigurationDao#getTargetConfigurationValue(java.lang.String, java.lang.String, java.lang.String, java.util.List)
         */
    @SuppressWarnings("unchecked")
	@Override
    public String getTargetConfigurationValueWithIgnores(final String clientName, final String configurationType, final String sourceSystem
            , final String targetSystem, final List<String> sourceSystemValues, final int numParams)
    {
    	// We don't want to search on an empty string if we have a mapping with more than 1 source value
    	if(sourceSystemValues == null || (sourceSystemValues.size() == 1 && numParams > 1 && sourceSystemValues.get(0).equals("")))
    	{
    		return null;
    	}
    	
        return (String)this.getHibernateTemplate().execute(new HibernateCallback()
        {
 
            @SuppressWarnings("unchecked")
            public Object doInHibernate(Session session) throws HibernateException
            {
                Query query = session.createQuery(buildWithIgnoresQueryString(sourceSystemValues));
                query.setParameter(MappingConfigurationDaoConstants.CONFIGURATION_TYPE, configurationType);
                query.setParameter(MappingConfigurationDaoConstants.SOURCE_CONTEXT, sourceSystem);
                query.setParameter(MappingConfigurationDaoConstants.TARGET_CONTEXT, targetSystem);
                query.setParameter(MappingConfigurationDaoConstants.NUMBER_OF_PARAMS, new Long(numParams));
                query.setParameter(MappingConfigurationDaoConstants.CONFIGURATION_SERVICE_CLIENT_NAME, clientName);
                query.setParameter(MappingConfigurationDaoConstants.SIZE, new Long(sourceSystemValues.size()));

                int i=0;
                for(String sourceSystemValue: sourceSystemValues)
                {
                	if(sourceSystemValue == null || sourceSystemValue.equals(""))
                	{
                		sourceSystemValue = "ignore";
                	}
                    query.setParameter(MappingConfigurationDaoConstants.SOURCE_SYSTEM_VALUE + i, sourceSystemValue);
                    query.setParameter(MappingConfigurationDaoConstants.SOURCE_SYSTEM_VALUE_SIZE_CONFIRM + i, sourceSystemValue);
                    i++;
                }

                List<String> results = (List<String>)query.list();

                if(results.size() == 0)
                {
                    return null;
                }
                else if(results.size() > 1)
                {
                	StringBuffer sourceSystemValuesSB = new StringBuffer();

                    sourceSystemValuesSB.append("[SourceSystemValues = ");
                    for(String sourceSystemValue: sourceSystemValues)
                    {
                        sourceSystemValuesSB.append(sourceSystemValue).append(" ");
                    }
                    sourceSystemValuesSB.append("]");

                    String errorMessage = "Multiple results returned from the mapping configuration service. " +
                            "[Client = " + clientName + "] [MappingConfigurationType = " + configurationType + "] [SourceContext = " + sourceSystem + "] " +
                            "[TargetContext = " + targetSystem + "] " + sourceSystemValuesSB.toString();
                    
                    logger.error(errorMessage);
                    
                    throw new RuntimeException(errorMessage);
                }
                else
                {
                    return results.get(0);
                }
            }
        });
    }

    @Override
    public String getTargetConfigurationValueWithIgnoresWithOrdinality(final String clientName, final String configurationType, final String sourceSystem, final String targetSystem, final List<QueryParameter> sourceSystemValues, final int numParams)
    {
        // We don't want to search on an empty string if we have a mapping with more than 1 source value
        if(sourceSystemValues == null || (sourceSystemValues.size() == 1 && numParams > 1 && sourceSystemValues.get(0).getValue().equals("")))
        {
            return null;
        }

        return (String)this.getHibernateTemplate().execute(new HibernateCallback()
        {

            @SuppressWarnings("unchecked")
            public Object doInHibernate(Session session) throws HibernateException
            {
                Query query = session.createQuery(buildWithIgnoresQueryStringWithNamedParams(sourceSystemValues));
                query.setParameter(MappingConfigurationDaoConstants.CONFIGURATION_TYPE, configurationType);
                query.setParameter(MappingConfigurationDaoConstants.SOURCE_CONTEXT, sourceSystem);
                query.setParameter(MappingConfigurationDaoConstants.TARGET_CONTEXT, targetSystem);
                query.setParameter(MappingConfigurationDaoConstants.NUMBER_OF_PARAMS, new Long(numParams));
                query.setParameter(MappingConfigurationDaoConstants.CONFIGURATION_SERVICE_CLIENT_NAME, clientName);
                query.setParameter(MappingConfigurationDaoConstants.SIZE, new Long(sourceSystemValues.size()));

                int i=0;
                for(QueryParameter sourceSystemValue: sourceSystemValues)
                {
                    if(sourceSystemValue.getValue() == null || sourceSystemValue.getValue().equals(""))
                    {
                        sourceSystemValue.setValue("ignore");
                    }
                    query.setParameter(MappingConfigurationDaoConstants.SOURCE_SYSTEM_VALUE + i, sourceSystemValue.getValue());
                    query.setParameter(MappingConfigurationDaoConstants.SOURCE_SYSTEM_VALUE_NAME + i, sourceSystemValue.getName());
                    query.setParameter(MappingConfigurationDaoConstants.SOURCE_SYSTEM_VALUE_SIZE_CONFIRM + i, sourceSystemValue.getValue());
                    i++;
                }

                List<String> results = (List<String>)query.list();

                if(results.size() == 0)
                {
                    return null;
                }
                else if(results.size() > 1)
                {
                    StringBuffer sourceSystemValuesSB = new StringBuffer();

                    sourceSystemValuesSB.append("[SourceSystemValues = ");
                    for(QueryParameter sourceSystemValue: sourceSystemValues)
                    {
                        sourceSystemValuesSB.append(sourceSystemValue).append(" ");
                    }
                    sourceSystemValuesSB.append("]");

                    String errorMessage = "Multiple results returned from the mapping configuration service. " +
                            "[Client = " + clientName + "] [MappingConfigurationType = " + configurationType + "] [SourceContext = " + sourceSystem + "] " +
                            "[TargetContext = " + targetSystem + "] " + sourceSystemValuesSB.toString();

                    logger.error(errorMessage);

                    throw new RuntimeException(errorMessage);
                }
                else
                {
                    return results.get(0);
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
	@Override
    public String getReverseMapping(final String clientName, final String configurationType, final String sourceSystem
            , final String targetSystem, final String targetSystemValue)
    {
    	return (String)this.getHibernateTemplate().execute(new HibernateCallback()
        {
 
            @SuppressWarnings("unchecked")
            public Object doInHibernate(Session session) throws HibernateException
            {
            	Query query = session.createQuery(MappingConfigurationDaoConstants.REVERSE_MAPPING_CONFIGURATION_QUERY);
                query.setParameter(MappingConfigurationDaoConstants.TARGET_SYSTEM_VALUE, targetSystemValue);
                query.setParameter(MappingConfigurationDaoConstants.CONFIGURATION_TYPE, configurationType);
                query.setParameter(MappingConfigurationDaoConstants.SOURCE_CONTEXT, sourceSystem);
                query.setParameter(MappingConfigurationDaoConstants.TARGET_CONTEXT, targetSystem);
                query.setParameter(MappingConfigurationDaoConstants.CONFIGURATION_SERVICE_CLIENT_NAME, clientName);

                List<String> results = (List<String>)query.list();

                if(results.size() == 0)
                {
                    return null;
                }
                else if(results.size() > 1)
                {                    
                	String errorMessage = "Multiple results returned from the mapping configuration service. " +
                            "[Client = " + clientName + "] [MappingConfigurationType = " + configurationType + "] [SourceContext = " + sourceSystem + "] " +
                            "[TargetContext = " + targetSystem + "] " + "[TargetSystemValue = " + targetSystemValue + "]";
                    
                    logger.error(errorMessage);
                    
                    throw new RuntimeException(errorMessage);
                }
                else
                {
                    return results.get(0);
                }
            }
        });
    }

    /*
         * (non-Javadoc)
         * @see org.ikasan.mapping.dao.MappingConfigurationDao#getTargetConfigurationValue(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.util.List)
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

    @Override
    public String getTargetConfigurationValueWithOrdinality(final String clientName, final String configurationType, final String sourceContext, final String targetContext, final List<QueryParameter> sourceSystemValues)
    {
        return (String)this.getHibernateTemplate().execute(new HibernateCallback()
        {
            @SuppressWarnings("unchecked")
            public Object doInHibernate(Session session) throws HibernateException
            {
                Query query = session.createQuery(buildQueryStringWithNamedParams(sourceSystemValues));
                query.setParameter(MappingConfigurationDaoConstants.CONFIGURATION_TYPE, configurationType);
                query.setParameter(MappingConfigurationDaoConstants.SOURCE_CONTEXT, sourceContext);
                query.setParameter(MappingConfigurationDaoConstants.TARGET_CONTEXT, targetContext);
                query.setParameter(MappingConfigurationDaoConstants.NUMBER_OF_PARAMS, new Long(sourceSystemValues.size()));
                query.setParameter(MappingConfigurationDaoConstants.CONFIGURATION_SERVICE_CLIENT_NAME, clientName);

                int i=0;
                for(QueryParameter sourceSystemValue: sourceSystemValues)
                {
                    query.setParameter(MappingConfigurationDaoConstants.SOURCE_SYSTEM_VALUE + i, sourceSystemValue.getValue());
                    query.setParameter(MappingConfigurationDaoConstants.SOURCE_SYSTEM_VALUE_NAME + i, sourceSystemValue.getName());
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

    /*
     * (non-Javadoc)
     * @see org.ikasan.mapping.dao.MappingConfigurationDao#getTargetConfigurationValue(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.util.List)
     */
    @Override
    public List<String> getTargetConfigurationValues(final String clientName, final String configurationType, final String sourceSystem
            , final String targetSystem, final List<String> sourceSystemValues)
    {
        MappingConfiguration mappingConfiguration = this.getMappingConfiguration(clientName, configurationType, sourceSystem, targetSystem);

        if(mappingConfiguration == null)
        {
            return new ArrayList<String>();
        }

        final List<SourceConfigurationValue> manyToManySourceConfigurationValues
                    = this.getSourceConfigurationValues(mappingConfiguration.getId(), sourceSystemValues);

        if(manyToManySourceConfigurationValues.size() == 0)
        {
            return new ArrayList<String>();
        }

        Long groupingId = -1l;

        HashMap<Long, List<SourceConfigurationValue>> groups = new HashMap<Long, List<SourceConfigurationValue>>();

        for(SourceConfigurationValue value: manyToManySourceConfigurationValues)
        {
            List<SourceConfigurationValue> sourceValues = groups.get(value.getSourceConfigGroupId());

            if(sourceValues == null)
            {
                sourceValues = new ArrayList<SourceConfigurationValue>();
                sourceValues.add(value);

                groups.put(value.getSourceConfigGroupId(), sourceValues);
            }
            else
            {
                groups.get(value.getSourceConfigGroupId()).add(value);
            }
        }

        for(Long groupId: groups.keySet())
        {
            Long expectedSourceValues = getNumberOfSourceValuesForGroupId(groupId);

            if(groups.get(groupId).size() == expectedSourceValues)
            {
                groupingId = groupId;
            }
        }

        if(groupingId == -1l)
        {
            return new ArrayList<String>();
        }

        final Long groupingIdParam = groupingId;

        return (List<String>)this.getHibernateTemplate().execute(new HibernateCallback()
        {
            @SuppressWarnings("unchecked")
            public Object doInHibernate(Session session) throws HibernateException
            {
                Query query = session.createQuery(MappingConfigurationDaoConstants.MANY_TO_MAPPING_CONFIGURATION_QUERY);
                query.setParameter(MappingConfigurationDaoConstants.GROUPING_ID, groupingIdParam);

                List<String> results = (List<String>)query.list();

                if(results.size() == 0)
                {
                    return null;
                }
                else
                {
                    return results;
                }
            }
        });
    }

    @Override
    public List<String> getTargetConfigurationValuesWithOrdinality(String clientName, String configurationType, String sourceContext, String targetContext, List<QueryParameter> sourceSystemValues)
    {
        MappingConfiguration mappingConfiguration = this.getMappingConfiguration(clientName, configurationType, sourceContext, targetContext);

        if(mappingConfiguration == null)
        {
            return new ArrayList<String>();
        }

        final List<SourceConfigurationValue> manyToManySourceConfigurationValues
                = this.getSourceConfigurationValuesWithName(mappingConfiguration.getId(), sourceSystemValues);

        if(manyToManySourceConfigurationValues.size() == 0)
        {
            return new ArrayList<String>();
        }

        Long groupingId = -1l;

        HashMap<Long, List<SourceConfigurationValue>> groups = new HashMap<Long, List<SourceConfigurationValue>>();

        for(SourceConfigurationValue value: manyToManySourceConfigurationValues)
        {
            List<SourceConfigurationValue> sourceValues = groups.get(value.getSourceConfigGroupId());

            if(sourceValues == null)
            {
                sourceValues = new ArrayList<SourceConfigurationValue>();
                sourceValues.add(value);

                groups.put(value.getSourceConfigGroupId(), sourceValues);
            }
            else
            {
                groups.get(value.getSourceConfigGroupId()).add(value);
            }
        }

        for(Long groupId: groups.keySet())
        {
            Long expectedSourceValues = getNumberOfSourceValuesForGroupId(groupId);

            if(groups.get(groupId).size() == expectedSourceValues)
            {
                groupingId = groupId;
            }
        }

        if(groupingId == -1l)
        {
            return new ArrayList<String>();
        }

        final Long groupingIdParam = groupingId;

        return (List<String>)this.getHibernateTemplate().execute(new HibernateCallback()
        {
            @SuppressWarnings("unchecked")
            public Object doInHibernate(Session session) throws HibernateException
            {
                Query query = session.createQuery(MappingConfigurationDaoConstants.MANY_TO_MAPPING_CONFIGURATION_QUERY);
                query.setParameter(MappingConfigurationDaoConstants.GROUPING_ID, groupingIdParam);

                List<String> results = (List<String>)query.list();

                if(results.size() == 0)
                {
                    return null;
                }
                else
                {
                    return results;
                }
            }
        });
    }

    /**
     * (non-Javadoc)
     * @see org.ikasan.mapping.dao.MappingConfigurationDao#getKeyLocationQuery(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
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

    /**
     * Helper method to build the query string used to query the mapping
     * configuration data.
     *
     * @param sourceSystemValues
     * @return
     */
    private String buildQueryStringWithNamedParams(List<QueryParameter> sourceSystemValues)
    {
        StringBuffer query = new StringBuffer(MappingConfigurationDaoConstants.MAPPING_CONFIGURATION_QUERY);

        for(int i=0; i<sourceSystemValues.size(); i++)
        {
            query.append(MappingConfigurationDaoConstants.NARROW_SOURCE_SYSTEM_WITH_NAME_FRAGMENT.replaceAll("index", Integer.toString(i)));
        }

        return query.toString();
    }
    
    /**
     * Helper method to build the query string used to query the mapping
     * configuration data. 
     * 
     * @param sourceSystemValues
     * @return
     */
    private String buildWithIgnoresQueryString(List<String> sourceSystemValues)
    {
        StringBuffer query = new StringBuffer(MappingConfigurationDaoConstants.MAPPING_CONFIGURATION_QUERY);

        for(int i=0; i<sourceSystemValues.size(); i++)
        {
            query.append(MappingConfigurationDaoConstants.NARROW_SOURCE_SYSTEM_FRAGMENT).append(i).append(") ");
        }
        
        query.append(MappingConfigurationDaoConstants.CONFIRM_RESULT_SIZE_PREDICATE_START);
        
        for(int i=0; i<sourceSystemValues.size(); i++)
        {
            query.append(MappingConfigurationDaoConstants.CONFIRM_RESULT_NARROW_BY_SOURCE_SYSTEM).append(i);
            
            if(i < sourceSystemValues.size()-1)
            {
            	query.append(" or ");
            }
        }
        
        query.append(MappingConfigurationDaoConstants.CONFIRM_RESULT_SIZE_PREDICATE_END);

        return query.toString();
    }

    /**
     * Helper method to build the query string used to query the mapping
     * configuration data.
     *
     * @param sourceSystemValues
     * @return
     */
    private String buildWithIgnoresQueryStringWithNamedParams(List<QueryParameter> sourceSystemValues)
    {
        StringBuffer query = new StringBuffer(MappingConfigurationDaoConstants.MAPPING_CONFIGURATION_QUERY);

        for(int i=0; i<sourceSystemValues.size(); i++)
        {
            query.append(MappingConfigurationDaoConstants.NARROW_SOURCE_SYSTEM_WITH_NAME_FRAGMENT.replaceAll("index", Integer.toString(i)));
        }

        query.append(MappingConfigurationDaoConstants.CONFIRM_RESULT_SIZE_PREDICATE_START);

        for(int i=0; i<sourceSystemValues.size(); i++)
        {
            query.append(MappingConfigurationDaoConstants.CONFIRM_RESULT_NARROW_BY_SOURCE_SYSTEM).append(i);

            if(i < sourceSystemValues.size()-1)
            {
                query.append(" or ");
            }
        }

        query.append(MappingConfigurationDaoConstants.CONFIRM_RESULT_SIZE_PREDICATE_END);

        return query.toString();
    }

    /*
     * (non-Javadoc)
     * @see org.ikasan.mapping.dao.MappingConfigurationDao#getConfigurationServiceClientByName(java.lang.String)
     */
    @Override
    public ConfigurationServiceClient getConfigurationServiceClientByName(String configurationServiceClientName)
    {
        DetachedCriteria criteria = DetachedCriteria.forClass(ConfigurationServiceClient.class);
        criteria.add(Restrictions.eq("name", configurationServiceClientName));
        return (ConfigurationServiceClient) DataAccessUtils.uniqueResult(this.getHibernateTemplate().findByCriteria(criteria));
    }

   /*
    * /(non-Javadoc)
    * @see org.ikasan.mapping.dao.MappingConfigurationDao#storeConfigurationType(org.ikasan.mapping.window.ConfigurationType)
    */
    @Override
    public Long storeConfigurationType(ConfigurationType configurationType)
    {
        configurationType.setUpdatedDateTime(new Date());
        this.getHibernateTemplate().saveOrUpdate(configurationType);

        return configurationType.getId();
    }

    /*
     * (non-Javadoc)
     * @see org.ikasan.mapping.dao.MappingConfigurationDao#storeMappingConfiguration(org.ikasan.mapping.window.MappingConfiguration)
     */
    @Override
    public Long storeMappingConfiguration(MappingConfiguration configurationContext) throws DataAccessException
    {
        configurationContext.setUpdatedDateTime(new Date());
        this.getHibernateTemplate().saveOrUpdate(configurationContext);

        return configurationContext.getId();
    }

    /*
     * (non-Javadoc)
     * @see org.ikasan.mapping.dao.MappingConfigurationDao#storeSourceConfigurationValue(org.ikasan.mapping.window.SourceConfigurationValue)
     */
    @Override
    public Long storeSourceConfigurationValue(SourceConfigurationValue sourceConfigurationValue)
    {
        sourceConfigurationValue.setUpdatedDateTime(new Date());
        this.getHibernateTemplate().saveOrUpdate(sourceConfigurationValue);

        return sourceConfigurationValue.getId();
    }

    /*
     * (non-Javadoc)
     * @see org.ikasan.mapping.dao.MappingConfigurationDao#storeTargetConfigurationValue(org.ikasan.mapping.window.TargetConfigurationValue)
     */
    @Override
    public Long storeTargetConfigurationValue(TargetConfigurationValue targetConfigurationValue)
    {
        targetConfigurationValue.setUpdatedDateTime(new Date());
        this.getHibernateTemplate().saveOrUpdate(targetConfigurationValue);

        return targetConfigurationValue.getId();
    }


    @Override
    public Long storeManyToManyTargetConfigurationValue(ManyToManyTargetConfigurationValue targetConfigurationValue)
    {
        targetConfigurationValue.setUpdatedDateTime(new Date());
        this.getHibernateTemplate().saveOrUpdate(targetConfigurationValue);

        return targetConfigurationValue.getId();
    }

    @Override
    public void storeSourceValueTargetValueGrouping(SourceValueTargetValueGrouping sourceValueTargetValueGrouping)
    {
        this.getHibernateTemplate().saveOrUpdate(sourceValueTargetValueGrouping);
    }

    @Override
    public List<SourceConfigurationValue> getSourceConfigurationValues(Long mappingConfigurationId, List<String> values)
    {
        DetachedCriteria criteria = DetachedCriteria.forClass(SourceConfigurationValue.class);
        criteria.add(Restrictions.in("sourceSystemValue", values));
        criteria.add(Restrictions.eq("mappingConfigurationId", mappingConfigurationId));

        return (List<SourceConfigurationValue>)this.getHibernateTemplate().findByCriteria(criteria);
    }


    protected List<SourceConfigurationValue> getSourceConfigurationValuesWithName(Long mappingConfigurationId, List<QueryParameter> values)
    {
        DetachedCriteria criteria = DetachedCriteria.forClass(SourceConfigurationValue.class);
        criteria.add(Restrictions.eq("mappingConfigurationId", mappingConfigurationId));

        Disjunction or = Restrictions.disjunction();

        for(QueryParameter param: values)
        {
            Conjunction and = Restrictions.conjunction();

            and.add(Restrictions.eq("sourceSystemValue", param.getValue()));
            and.add(Restrictions.eq("name", param.getName()));

            or.add(and);
        }

        criteria.add(or);

        return (List<SourceConfigurationValue>)this.getHibernateTemplate().findByCriteria(criteria);
    }

    /*
             * (non-Javadoc)
             * @see org.ikasan.mapping.dao.MappingConfigurationDao#storeConfigurationServiceClient(org.ikasan.mapping.window.ConfigurationServiceClient)
             */
    @Override
    public Long storeConfigurationServiceClient(ConfigurationServiceClient configurationServiceClient)
    {
        configurationServiceClient.setUpdatedDateTime(new Date());
        this.getHibernateTemplate().saveOrUpdate(configurationServiceClient);

        return configurationServiceClient.getId();
    }

    /* (non-Javadoc)
     * @see org.ikasan.mapping.dao.MappingConfigurationDao#storeKeyLocationQuery(org.ikasan.mapping.window.KeyLocationQuery)
     */
    @Override
    public Long storeKeyLocationQuery(KeyLocationQuery keyLocationQuery)
    {
        keyLocationQuery.setUpdatedDateTime(new Date());
        this.getHibernateTemplate().saveOrUpdate(keyLocationQuery);

        return keyLocationQuery.getId();
    }

    /* (non-Javadoc)
     * @see org.ikasan.mapping.dao.MappingConfigurationDao#storeConfigurationContext(org.ikasan.mapping.window.ConfigurationContext)
     */
    @Override
    public Long storeConfigurationContext(ConfigurationContext configurationContext)
    {
        configurationContext.setUpdatedDateTime(new Date());
        this.getHibernateTemplate().saveOrUpdate(configurationContext);

        return configurationContext.getId();
    }

    /* (non-Javadoc)
     * @see org.ikasan.mapping.dao.MappingConfigurationDao#getAllConfigurationTypes()
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<ConfigurationType> getAllConfigurationTypes()
    {
        DetachedCriteria criteria = DetachedCriteria.forClass(ConfigurationType.class);

        return (List<ConfigurationType>) this.getHibernateTemplate().findByCriteria(criteria);
    }

    /* (non-Javadoc)
     * @see org.ikasan.mapping.dao.MappingConfigurationDao#getAllConfigurationContexts()
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<ConfigurationContext> getAllConfigurationContexts()
    {
        DetachedCriteria criteria = DetachedCriteria.forClass(ConfigurationContext.class);

        return (List<ConfigurationContext>) this.getHibernateTemplate().findByCriteria(criteria);
    }

    /* (non-Javadoc)
     * @see org.ikasan.mapping.dao.MappingConfigurationDao#getAllConfigurationServiceClients()
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<ConfigurationServiceClient> getAllConfigurationServiceClients()
    {
        DetachedCriteria criteria = DetachedCriteria.forClass(ConfigurationServiceClient.class);

        return (List<ConfigurationServiceClient>) this.getHibernateTemplate().findByCriteria(criteria);
    }

    /* (non-Javadoc)
     * @see org.ikasan.mapping.dao.MappingConfigurationDao#getMappingConfigurationById(java.lang.Long)
     */
    @Override
    public MappingConfiguration getMappingConfigurationById(Long id)
    {
        DetachedCriteria criteria = DetachedCriteria.forClass(MappingConfiguration.class);
        criteria.add(Restrictions.eq("id", id));

        return (MappingConfiguration)DataAccessUtils.uniqueResult(this.getHibernateTemplate().findByCriteria(criteria));
    }

    /* (non-Javadoc)
     * @see org.ikasan.mapping.dao.MappingConfigurationDao#getMappingConfiguration(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
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
     * @see org.ikasan.mapping.dao.MappingConfigurationDao#getMappingConfigurationsByConfigurationServiceClientId(java.lang.Long)
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
     * @see org.ikasan.mapping.dao.MappingConfigurationDao#getMappingConfigurationsByConfigurationTypeId(java.lang.Long)
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
     * @see org.ikasan.mapping.dao.MappingConfigurationDao#getMappingConfigurationsBySourceContextId(java.lang.Long)
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
     * @see org.ikasan.mapping.dao.MappingConfigurationDao#getMappingConfigurationsByTargetContextId(java.lang.Long)
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
     * @see org.ikasan.mapping.dao.MappingConfigurationDao#getConfigurationContextById(java.lang.Long)
     */
    @Override
    public ConfigurationContext getConfigurationContextById(Long id)
    {
        DetachedCriteria criteria = DetachedCriteria.forClass(ConfigurationContext.class);
        criteria.add(Restrictions.eq("id", id));

        return (ConfigurationContext)DataAccessUtils.uniqueResult(this.getHibernateTemplate().findByCriteria(criteria));
    }

    /* (non-Javadoc)
     * @see org.ikasan.mapping.dao.MappingConfigurationDao#getConfigurationServiceClientById(java.lang.Long)
     */
    @Override
    public ConfigurationServiceClient getConfigurationServiceClientById(Long id)
    {
        DetachedCriteria criteria = DetachedCriteria.forClass(ConfigurationServiceClient.class);
        criteria.add(Restrictions.eq("id", id));

        return (ConfigurationServiceClient)DataAccessUtils.uniqueResult(this.getHibernateTemplate().findByCriteria(criteria));
    }

    /* (non-Javadoc)
     * @see org.ikasan.mapping.dao.MappingConfigurationDao#getConfigurationTypeById(java.lang.Long)
     */
    @Override
    public ConfigurationType getConfigurationTypeById(Long id)
    {
        DetachedCriteria criteria = DetachedCriteria.forClass(ConfigurationType.class);
        criteria.add(Restrictions.eq("id", id));

        return (ConfigurationType)DataAccessUtils.uniqueResult(this.getHibernateTemplate().findByCriteria(criteria));
    }

    /* (non-Javadoc)
     * @see org.ikasan.mapping.dao.MappingConfigurationDao#getKeyLocationQueriesByMappingConfigurationId(java.lang.Long)
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
     * @see org.ikasan.mapping.dao.MappingConfigurationDao#getSourceConfigurationValueByMappingConfigurationId(java.lang.Long)
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
     * @see org.ikasan.mapping.dao.MappingConfigurationDao#getTargetConfigurationValueById(java.lang.Long)
     */
    @Override
    public TargetConfigurationValue getTargetConfigurationValueById(Long id)
    {
        DetachedCriteria criteria = DetachedCriteria.forClass(TargetConfigurationValue.class);
        criteria.add(Restrictions.eq("id", id));

        return (TargetConfigurationValue)DataAccessUtils.uniqueResult(this.getHibernateTemplate().findByCriteria(criteria));
    }

    /* (non-Javadoc)
     * @see org.ikasan.mapping.dao.MappingConfigurationDao#getAllMappingCongigurations()
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<MappingConfiguration> getAllMappingConfigurations()
    {
        DetachedCriteria criteria = DetachedCriteria.forClass(MappingConfiguration.class);

        return (List<MappingConfiguration>) this.getHibernateTemplate().findByCriteria(criteria);
    }

    /* (non-Javadoc)
     * @see org.ikasan.mapping.dao.MappingConfigurationDao#getTargetConfigurationValueByMappingConfigurationId(java.lang.Long)
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
     * @see org.ikasan.mapping.dao.MappingConfigurationDao#deleteSourceConfigurationValue(org.ikasan.mapping.window.SourceConfigurationValue)
     */
    @Override
    public void deleteSourceConfigurationValue(SourceConfigurationValue sourceConfigurationValue)
    {
        this.getHibernateTemplate().delete(sourceConfigurationValue);
    }

    /* (non-Javadoc)
     * @see org.ikasan.mapping.dao.MappingConfigurationDao#deleteTargetConfigurationValue(org.ikasan.mapping.window.TargetConfigurationValue)
     */
    @Override
    public void deleteTargetConfigurationValue(TargetConfigurationValue targetConfigurationValue)
    {
        this.getHibernateTemplate().delete(targetConfigurationValue);
    }

    /*
     * (non-Javadoc)
     * @see org.ikasan.mapping.dao.MappingConfigurationDao#getSourceConfigurationGroupSequence()
     */
    @Override
    public SourceConfigurationGroupSequence getSourceConfigurationGroupSequence()
    {
        DetachedCriteria criteria = DetachedCriteria.forClass(SourceConfigurationGroupSequence.class);
        criteria.add(Restrictions.eq("id", ID));

        return (SourceConfigurationGroupSequence) DataAccessUtils.uniqueResult(this.getHibernateTemplate().findByCriteria(criteria));
    }

    /*
     * (non-Javadoc)
     * @see org.ikasan.mapping.dao.MappingConfigurationDao#saveSourceConfigurationGroupSequence(org.ikasan.mapping.window.SourceConfigurationGroupSequence)
     */
    @Override
    public void saveSourceConfigurationGroupSequence(SourceConfigurationGroupSequence sequence)
    {
            sequence.setId(ID);
            this.getHibernateTemplate().saveOrUpdate(sequence);
    }

    /* (non-Javadoc)
     * @see org.ikasan.mapping.dao.MappingConfigurationDao#getNumberOfSourceConfigurationValuesReferencingTargetConfigurationValue(org.ikasan.mapping.window.TargetConfigurationValue)
     */
    @Override
    public Long getNumberOfSourceConfigurationValuesReferencingTargetConfigurationValue(
            final TargetConfigurationValue targetConfigurationValue)
    {
        if(targetConfigurationValue == null)
        {
            return 0l;
        }

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
     * @see org.ikasan.mapping.dao.MappingConfigurationDao#mappingConfigurationExists(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
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
     * @see org.ikasan.mapping.dao.MappingConfigurationDao#deleteMappingConfiguration(org.ikasan.mapping.window.MappingConfiguration)
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
     * @see org.ikasan.mapping.dao.MappingConfigurationDao#getConfigurationTypeByName(java.lang.String)
     */
    @Override
    public ConfigurationType getConfigurationTypeByName(String name)
    {
        DetachedCriteria criteria = DetachedCriteria.forClass(ConfigurationType.class);
        criteria.add(Restrictions.eq("name", name));
        return (ConfigurationType) DataAccessUtils.uniqueResult(this.getHibernateTemplate().findByCriteria(criteria));

    }

    /* (non-Javadoc)
     * @see org.ikasan.mapping.dao.MappingConfigurationDao#getConfigurationContextByName(java.lang.String)
     */
    @Override
    public ConfigurationContext getConfigurationContextByName(String name)
    {
        DetachedCriteria criteria = DetachedCriteria.forClass(ConfigurationContext.class);
        criteria.add(Restrictions.eq("name", name));
        return (ConfigurationContext) DataAccessUtils.uniqueResult(this.getHibernateTemplate().findByCriteria(criteria));
    }

    /* (non-Javadoc)
     * @see org.ikasan.mapping.dao.MappingConfigurationDao#getConfigurationTypesByClientName(java.lang.String)
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
     * @see org.ikasan.mapping.dao.MappingConfigurationDao#getSourceConfigurationContextByClientNameAndType(java.lang.String, java.lang.String)
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
     * @see org.ikasan.mapping.dao.MappingConfigurationDao#getTargetConfigurationContextByClientNameTypeAndSourceContext(java.lang.String, java.lang.String, java.lang.String)
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

    @Override
    public List<ManyToManyTargetConfigurationValue> getManyToManyTargetConfigurationValues(Long groupId)
    {
        DetachedCriteria criteria = DetachedCriteria.forClass(ManyToManyTargetConfigurationValue.class);
        criteria.add(Restrictions.eq("groupId", groupId));
        return (List<ManyToManyTargetConfigurationValue>)this.getHibernateTemplate().findByCriteria(criteria);
    }

    @Override
    public void deleteManyToManyTargetConfigurationValue(ManyToManyTargetConfigurationValue targetConfigurationValue)
    {
        this.getHibernateTemplate().delete(targetConfigurationValue);
    }

    @Override
    public Long getNumberOfSourceValuesForGroupId(Long groupId)
    {
        DetachedCriteria criteria = DetachedCriteria.forClass(SourceConfigurationValue.class);
        criteria.add(Restrictions.eq("sourceConfigGroupId", groupId));
        criteria.setProjection(Projections.rowCount());
        criteria.setProjection(Projections.projectionList()
                .add(Projections.count("sourceConfigGroupId")));

        return (Long) DataAccessUtils.uniqueResult(this.getHibernateTemplate().findByCriteria(criteria));
    }
}
