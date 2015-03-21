/*
 * $Id: MappingConfigurationDaoConstants.java 44074 2015-03-17 10:43:23Z stewmi $
 * $URL: https://svc-vcs-prd.uk.mizuho-sc.com:18080/svn/architecture/cmi2/trunk/projects/mappingConfigurationService/api/src/main/java/com/mizuho/cmi2/mappingConfiguration/dao/constants/MappingConfigurationDaoConstants.java $
 *
 * ====================================================================
 *
 * Copyright (c) 2000-2011 by Mizuho International plc.
 * All Rights Reserved.
 *
 * ====================================================================
 *
 */
package org.ikasan.mapping.dao.constants;

/**
 * @author CMI2 Development Team
 *
 */
public interface MappingConfigurationDaoConstants
{
    /** Static strings representing parameter values to set on the query */
    public static final String CONFIGURATION_TYPE = "configurationType";
    public static final String SOURCE_CONTEXT = "sourceContext";
    public static final String TARGET_CONTEXT = "targetContext";
    public static final String SOURCE_SYSTEM_VALUE = "sourceSystemValue";
    public static final String NUMBER_OF_PARAMS = "numberOfParams";
    public static final String CONFIGURATION_SERVICE_CLIENT_NAME = "configurationServiceClientName";
    public static final String TARGET_CONFIGURATION_VALUE_ID = "targetConfigurationValueId";

    /** The base HQL query used to access the mapping configurations. */
    public static final String MAPPING_CONFIGURATION_QUERY = "select distinct tcv.targetSystemValue from ConfigurationType as ct," +
            " MappingConfiguration as mc, TargetConfigurationValue as tcv, SourceConfigurationValue as scv, ConfigurationServiceClient as csc" +
             " where ct.name = :" + CONFIGURATION_TYPE +
             " and ct.id = mc.configurationType" +
             " and mc.sourceContext = (select cc.id from ConfigurationContext as cc where cc.name = :" + SOURCE_CONTEXT  + ")" +
             " and mc.targetContext = (select cc.id from ConfigurationContext as cc where cc.name = :" + TARGET_CONTEXT  + ")" +
             " and mc.numberOfParams = :" + NUMBER_OF_PARAMS +
             " and mc.configurationServiceClient = csc.id" +
             " and csc.name = :" + CONFIGURATION_SERVICE_CLIENT_NAME + 
             " and mc.id = scv.mappingConfigurationId" +
             " and tcv.id = scv.targetConfigurationValue";

    /** This fragment of HQL is used to narrow the results based on n number of source system values. */
    public static final String NARROW_SOURCE_SYSTEM_FRAGMENT = " and scv.targetConfigurationValue in (select scv2.targetConfigurationValue " +
            "from SourceConfigurationValue as scv2 where scv2.sourceSystemValue = :" + SOURCE_SYSTEM_VALUE;

    public static final String KEY_LOCATION_QUERY_QUERY = "select klq.value from ConfigurationType as ct," +
            " MappingConfiguration as mc, KeyLocationQuery as klq, ConfigurationServiceClient as csc" +
            " where ct.name = :" + CONFIGURATION_TYPE +
            " and ct.id = mc.configurationType" +
            " and mc.sourceContext = (select cc.id from ConfigurationContext as cc where cc.name = :" + SOURCE_CONTEXT  + ")" +
            " and mc.targetContext = (select cc.id from ConfigurationContext as cc where cc.name = :" + TARGET_CONTEXT  + ")" +
            " and mc.configurationServiceClient = csc.id" +
            " and csc.name = :" + CONFIGURATION_SERVICE_CLIENT_NAME + 
            " and mc.id = klq.mappingConfigurationId";

    public static final String MAPPING_CONFIGURATION_BY_CLIENT_TYPE_AND_CONTEXT_QUERY = "select mc from ConfigurationType as ct," +
             " MappingConfiguration as mc, ConfigurationServiceClient as csc" +
             " where ct.name = :" + CONFIGURATION_TYPE +
             " and ct.id = mc.configurationType" +
             " and mc.configurationServiceClient = csc.id" +
             " and csc.name = :" + CONFIGURATION_SERVICE_CLIENT_NAME + 
             " and mc.sourceContext = (select cc.id from ConfigurationContext as cc where cc.name = :" + SOURCE_CONTEXT  + ")" +
             " and mc.targetContext = (select cc.id from ConfigurationContext as cc where cc.name = :" + TARGET_CONTEXT  + ")";

    public static final String NUMBER_OF_SOURCE_CONFIGURATION_VALUES_REFERENCING_TARGET_CONFIGURATION_VALUE = "select count(*) from SourceConfigurationValue as scv," +
    		"TargetConfigurationValue as tcv where scv.targetConfigurationValue = tcv.id and tcv.id =:" + TARGET_CONFIGURATION_VALUE_ID;

    public static final String MAPPING_CONFIGURATION_EXISTS_QUERY = "select count(*) from ConfigurationType as ct," +
            " MappingConfiguration as mc, ConfigurationServiceClient as csc" +
            " where ct.name = :" + CONFIGURATION_TYPE +
            " and ct.id = mc.configurationType" +
            " and mc.configurationServiceClient = csc.id" +
            " and csc.name = :" + CONFIGURATION_SERVICE_CLIENT_NAME + 
            " and mc.sourceContext = (select cc.id from ConfigurationContext as cc where cc.name = :" + SOURCE_CONTEXT  + ")" +
            " and mc.targetContext = (select cc.id from ConfigurationContext as cc where cc.name = :" + TARGET_CONTEXT  + ")";

    public static final String NARROW_CONFIGURATION_TYPE_BASE_QUERY =
            "select DISTINCT t from ConfigurationType as t, " +
            "ConfigurationServiceClient as c, MappingConfiguration as mc " +
            "where t.id = mc.configurationType " +
            "and c.id = mc.configurationServiceClient ";

    public static final String NARROW_SOURCE_CONFIGURATION_BASE_QUERY =
            "select DISTINCT s from ConfigurationContext as s, ConfigurationType as t, " +
            "ConfigurationServiceClient as c, MappingConfiguration as mc " +
            "where t.id = mc.configurationType " +
            "and c.id = mc.configurationServiceClient " +
            "and s.id = mc.sourceContext ";

    public static final String NARROW_TARGET_CONFIGURATION_BASE_QUERY =
            "select DISTINCT tg from ConfigurationContext as tg, ConfigurationContext as s, ConfigurationType as t, " +
            "ConfigurationServiceClient as c, MappingConfiguration as mc " +
            "where t.id = mc.configurationType " +
            "and c.id = mc.configurationServiceClient " +
            "and s.id = mc.sourceContext " +
            "and tg.id = mc.targetContext ";

    public static final String MAPPING_CONFIGURATION_BASE_QUERY = "select mc from ConfigurationType as t," +
            " MappingConfiguration as mc, ConfigurationServiceClient as c" +
            " where t.id = mc.configurationType" +
            " and mc.configurationServiceClient = c.id";

    public static final String MAPPING_CONFIGURATION_LITE_BASE_QUERY = "select mc from ConfigurationType as t," +
            " MappingConfigurationLite as mc, ConfigurationServiceClient as c" +
            " where t.id = mc.configurationType" +
            " and mc.configurationServiceClient = c.id";

    public static final String CONFIGURATION_TYPE_PREDICATE = " and t.name = :" + CONFIGURATION_TYPE;
    public static final String SOURCE_SYSTEM_PREDICATE = " and s.name = :" + SOURCE_SYSTEM_VALUE;
    public static final String CONFIGURATION_CLIENT_PREDICATE =" and c.name = :" + CONFIGURATION_SERVICE_CLIENT_NAME;
    public static final String SOURCE_CONTEXT_PREDICATE = " and mc.sourceContext = (select cc.id from " +
    		"ConfigurationContext as cc where cc.name = :" + SOURCE_CONTEXT  + ")";
    public static final String TARGET_CONTEXT_PREDICATE = " and mc.targetContext = (select cc.id from " +
    		"ConfigurationContext as cc where cc.name = :" + TARGET_CONTEXT  + ")";

}
