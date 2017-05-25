package org.ikasan.mapping.service;

import org.apache.log4j.Logger;
import org.ikasan.mapping.dao.MappingConfigurationDao;
import org.ikasan.mapping.service.configuration.MappingConfigurationServiceConfiguration;
import org.ikasan.mapping.util.SetProducer;
import org.ikasan.spec.mapping.MappingService;
import org.ikasan.spec.mapping.NamedResult;
import org.ikasan.spec.mapping.QueryParameter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stewmi on 16/05/2017.
 */
public class MappingServiceImpl implements MappingService<MappingConfigurationServiceConfiguration>
{
    private Logger logger = Logger.getLogger(MappingServiceImpl.class);

    protected final MappingConfigurationDao dao;
    protected MappingConfigurationServiceConfiguration configuration;

    /**
     * Constructor
     *
     * @param dao
     */
    public MappingServiceImpl(final MappingConfigurationDao dao)
    {
        this.dao = dao;
        if (this.dao == null)
        {
            throw new IllegalArgumentException("The MappingConfigurationDao cannot be null.");
        }
    }

    /*
     * (non-Javadoc)
     * @see org.ikasan.mapping.service.MappingConfigurationService#getTargetConfigurationValue(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.util.List)
     */
    @Override
    public String getTargetConfigurationValue(final String clientName, final String configurationType, final String sourceSystem,
                                              final String targetSystem, final List<String> sourceSystemValues)
    {
        if(sourceSystemValues == null || sourceSystemValues.size() == 0)
        {
            throw new RuntimeException("Null or empty source paramaters cannot be supplied to a mapping configuration look up.");
        }

        if(this.configuration != null && this.configuration.isReverseMapping())
        {
            if(sourceSystemValues.size() > 1)
            {
                throw new RuntimeException("The mapping configuration is configured for reverse mappings. Only one source parameter can be" +
                        " provided for a reverse mapping as only one to one reverse mappings are supported. You have provided " + sourceSystemValues.size()
                        + " source parameters.");
            }

            return this.dao.getReverseMapping(clientName, configurationType, sourceSystem, targetSystem, sourceSystemValues.get(0));
        }
        else
        {
            return this.dao.getTargetConfigurationValue(clientName, configurationType, sourceSystem, targetSystem, sourceSystemValues);
        }
    }

    /* (non-Javadoc)
     * @see org.ikasan.mapping.service.MappingConfigurationService#getTargetConfigurationValue(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public String getTargetConfigurationValue(final String clientName, String configurationType, String sourceSystem, String targetSystem,
                                              String sourceSystemValue)
    {
        if(sourceSystemValue == null)
        {
            throw new RuntimeException("A null source paramater cannot be supplied to a mapping configuration look up.");
        }

        List<String> sourceSystemValues = new ArrayList<String>();
        sourceSystemValues.add(sourceSystemValue);

        return getTargetConfigurationValue(clientName, configurationType, sourceSystem, targetSystem, sourceSystemValues);
    }


    /* (non-Javadoc)
	 * @see com.mizuho.cmi2.mappingConfiguration.service.MappingConfigurationService#getTargetConfigurationValueWithIgnores(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.util.List)
	 */
    public String getTargetConfigurationValueWithIgnores(String clientName,
                                                         String configurationTypeName, String sourceContext,
                                                         String targetContext, List<String> sourceSystemValues)
    {
        String returnValue = this.dao.getTargetConfigurationValueWithIgnores(clientName, configurationTypeName, sourceContext,
                targetContext, sourceSystemValues, sourceSystemValues.size());

        boolean resultFound = false;

        SetProducer<String> setProducer = new SetProducer<String>();

        if(returnValue == null)
        {
            for(int i=sourceSystemValues.size() - 1; i>0; i--)
            {
                List<List<String>> subSets = setProducer.combinations(sourceSystemValues, i);

                String result = null;

                for(List<String> subSet: subSets)
                {
                    ArrayList<String> subList = new ArrayList<String>();
                    subList.addAll(subSet);

                    returnValue = this.dao.getTargetConfigurationValueWithIgnores(clientName, configurationTypeName, sourceContext,
                            targetContext, subList, sourceSystemValues.size());

                    if(returnValue != null && resultFound)
                    {
                        StringBuffer sourceSystemValuesSB = new StringBuffer();

                        sourceSystemValuesSB.append("[SourceSystemValues = ");
                        for(String sourceSystemValue: sourceSystemValues)
                        {
                            sourceSystemValuesSB.append(sourceSystemValue).append(" ");
                        }
                        sourceSystemValuesSB.append("]");

                        String errorMessage = "Multiple sub results returned from the mapping configuration service. " +
                                "[Client = " + clientName + "] [MappingConfigurationType = " + configurationTypeName + "] [SourceContext = " + sourceContext + "] " +
                                "[TargetContext = " + targetContext + "] " + sourceSystemValuesSB.toString();

                        logger.error(errorMessage);

                        throw new RuntimeException(errorMessage);
                    }

                    if(returnValue != null)
                    {
                        resultFound = true;
                        result = returnValue;
                    }
                }

                if(result != null)
                {
                    return result;
                }
            }
        }

        return returnValue;
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.mappingConfiguration.service.MappingConfigurationService#getTargetConfigurationValueWithIgnores(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.util.List)
     */
    public String getTargetConfigurationValueWithIgnoresWithOrdinality(String clientName,
                                                                       String configurationTypeName, String sourceContext,
                                                                       String targetContext, List<QueryParameter> sourceSystemValues)
    {
        String returnValue = this.dao.getTargetConfigurationValueWithIgnoresWithOrdinality(clientName, configurationTypeName, sourceContext,
                targetContext, sourceSystemValues, sourceSystemValues.size());

        boolean resultFound = false;

        SetProducer<QueryParameter> setProducer = new SetProducer<QueryParameter>();

        if(returnValue == null)
        {
            for(int i=sourceSystemValues.size() - 1; i>0; i--)
            {
                List<List<QueryParameter>> subSets = setProducer.combinations(sourceSystemValues, i);

                String result = null;

                for(List<QueryParameter> subSet: subSets)
                {
                    ArrayList<QueryParameter> subList = new ArrayList<QueryParameter>();
                    subList.addAll(subSet);

                    returnValue = this.dao.getTargetConfigurationValueWithIgnoresWithOrdinality(clientName, configurationTypeName, sourceContext,
                            targetContext, subList, sourceSystemValues.size());

                    if(returnValue != null && resultFound)
                    {
                        StringBuffer sourceSystemValuesSB = new StringBuffer();

                        sourceSystemValuesSB.append("[SourceSystemValues = ");
                        for(QueryParameter sourceSystemValue: sourceSystemValues)
                        {
                            sourceSystemValuesSB.append(sourceSystemValue).append(" ");
                        }
                        sourceSystemValuesSB.append("]");

                        String errorMessage = "Multiple sub results returned from the mapping configuration service. " +
                                "[Client = " + clientName + "] [MappingConfigurationType = " + configurationTypeName + "] [SourceContext = " + sourceContext + "] " +
                                "[TargetContext = " + targetContext + "] " + sourceSystemValuesSB.toString();

                        logger.error(errorMessage);

                        throw new RuntimeException(errorMessage);
                    }

                    if(returnValue != null)
                    {
                        resultFound = true;
                        result = returnValue;
                    }
                }

                if(result != null)
                {
                    return result;
                }
            }
        }

        return returnValue;
    }

    @Override
    public List<String> getTargetConfigurationValues(String clientName, String configurationType, String sourceContext, String targetContext, List<String> sourceSystemValues)
    {
        return this.dao.getTargetConfigurationValues(clientName, configurationType, sourceContext, targetContext, sourceSystemValues);
    }

    @Override
    public List<NamedResult> getTargetConfigurationValuesWithOrdinality(String clientName, String configurationType, String sourceContext, String targetContext, List<QueryParameter> sourceSystemValues)
    {
        return this.dao.getTargetConfigurationValuesWithOrdinality(clientName, configurationType, sourceContext, targetContext, sourceSystemValues);
    }

    @Override
    public void setConfiguration(MappingConfigurationServiceConfiguration configuration)
    {
        this.configuration = configuration;
    }
}
