/*
 * $Id: MappingConfigurationValuesExportHelper.java 40648 2014-11-07 11:12:53Z stewmi $
 * $URL: https://svc-vcs-prd.uk.mizuho-sc.com:18080/svn/architecture/cmi2/trunk/projects/mappingConfigurationUI/war/src/main/java/org/ikasan/mapping/configuration/ui/util/MappingConfigurationValuesExportHelper.java $
 *
 * ====================================================================
 *
 * Copyright (c) 2000-2011 by Mizuho International plc.
 * All Rights Reserved.
 *
 * ====================================================================
 *
 */
package org.ikasan.dashboard.ui.mappingconfiguration.util;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.ikasan.dashboard.ui.framework.util.XmlFormatter;
import org.ikasan.dashboard.ui.mappingconfiguration.model.MappingConfigurationValue;
import org.ikasan.mapping.model.MappingConfiguration;
import org.ikasan.mapping.model.SourceConfigurationValue;

/**
 * @author CMI2 Development Team
 *
 */
public class MappingConfigurationValuesExportHelper
{
    private static final String XML_TAG = "<?xml version=\"1.0\"?>";
    private static final String START_TAG = "<mappingConfigurationValues xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" +
    		" xsi:noNamespaceSchemaLocation=\"{$schemaLocation}\">";
    private static final String START_TAG_WITHOUT_SCHEMA = "<mappingConfigurationValues>";
    private static final String END_TAG = "</mappingConfigurationValues>";
    private static final String MAPPING_CONFIGURATION_START_TAG = "<mappingConfigurationValue>";
    private static final String MAPPING_CONFIGURATION_END_TAG = "</mappingConfigurationValue>";
    private static final String SOURCE_CONFIGURATION_VALUES_START_TAG = "<sourceConfigurationValues>";
    private static final String SOURCE_CONFIGURATION_VALUES_END_TAG = "</sourceConfigurationValues>";
    private static final String SOURCE_CONFIGURATION_VALUE_START_TAG = "<sourceConfigurationValue>";
    private static final String SOURCE_CONFIGURATION_VALUE_END_TAG = "</sourceConfigurationValue>";
    private static final String TARGET_CONFIGURATION_VALUE_START_TAG = "<targetConfigurationValue>";
    private static final String TARGET_CONFIGURATION_VALUE_END_TAG = "</targetConfigurationValue>";
    private static final String EXPORT_DATE_TIME_START_TAG = "<exportDateTime>";
    private static final String EXPORT_DATE_TIME_END_TAG = "</exportDateTime>";

    /**
     * @param schemaLocation
     */
    public MappingConfigurationValuesExportHelper()
    {
        super();
    }

    /**
     * Helper method to create the XML export document.
     * 
     * @param mappingConfiguration
     * @return
     */
    public String getMappingConfigurationExportXml(MappingConfiguration mappingConfiguration, boolean includeXmlTag,
    		String schemaLocation)
    {
        StringBuffer exportString = new StringBuffer();

        if(includeXmlTag)
        {
            exportString.append(XML_TAG);
            String startTag = START_TAG;
            exportString.append(startTag.replace("{$schemaLocation}", schemaLocation));
            
            exportString.append(EXPORT_DATE_TIME_START_TAG);
            exportString.append(DateFormat.getDateTimeInstance
                (DateFormat.LONG, DateFormat.LONG).format(new Date()));
            exportString.append(EXPORT_DATE_TIME_END_TAG);
        }
        else
        {
            exportString.append(START_TAG_WITHOUT_SCHEMA);
        }

        List<MappingConfigurationValue> mappingConfigurationValues
            = getMappingConfigurationValues(mappingConfiguration.getSourceConfigurationValues());

        for(MappingConfigurationValue mappingConfigurationValue: mappingConfigurationValues)
        {
            exportString.append(MAPPING_CONFIGURATION_START_TAG).append(SOURCE_CONFIGURATION_VALUES_START_TAG);

            for(SourceConfigurationValue value: mappingConfigurationValue.getSourceConfigurationValues())
            {
                exportString.append(SOURCE_CONFIGURATION_VALUE_START_TAG).append(value.getSourceSystemValue())
                .append(SOURCE_CONFIGURATION_VALUE_END_TAG);
            }

            exportString.append(SOURCE_CONFIGURATION_VALUES_END_TAG).append(TARGET_CONFIGURATION_VALUE_START_TAG)
            .append(mappingConfigurationValue.getTargetConfigurationValue().getTargetSystemValue())
            .append(TARGET_CONFIGURATION_VALUE_END_TAG).append(MAPPING_CONFIGURATION_END_TAG);
        }

        exportString.append(END_TAG);

        if(includeXmlTag)
        {
            return XmlFormatter.format(exportString.toString().trim());
        }
        else
        {
            return exportString.toString().trim();
        }
    }

    protected List<MappingConfigurationValue> getMappingConfigurationValues(Set<SourceConfigurationValue> sourceConfigurationValues)
    {
        HashMap<Long, MappingConfigurationValue> map = new HashMap<Long, MappingConfigurationValue>();

        ArrayList<MappingConfigurationValue> oneToOneMappingConfigurationValues = null;

        for(SourceConfigurationValue value: sourceConfigurationValues)
        {
            if(value.getSourceConfigGroupId() == null)
            {
                if(oneToOneMappingConfigurationValues == null)
                {
                    oneToOneMappingConfigurationValues = new ArrayList<MappingConfigurationValue>();
                }
                MappingConfigurationValue mappingConfigurationValue = new MappingConfigurationValue();
                mappingConfigurationValue.addSourceConfigurationValue(value);
                mappingConfigurationValue.setTargetConfigurationValue(value.getTargetConfigurationValue());
                oneToOneMappingConfigurationValues.add(mappingConfigurationValue);
            }
            else
            {
                MappingConfigurationValue mappingConfigurationValue = map.get(value.getSourceConfigGroupId());
    
                if(mappingConfigurationValue == null)
                {
                    mappingConfigurationValue = new MappingConfigurationValue();
                    mappingConfigurationValue.addSourceConfigurationValue(value);
                    mappingConfigurationValue.setTargetConfigurationValue(value.getTargetConfigurationValue());
                    map.put(value.getSourceConfigGroupId(), mappingConfigurationValue);
                }
                else
                {
                    mappingConfigurationValue.addSourceConfigurationValue(value);
                }
            }
        }

        if(oneToOneMappingConfigurationValues != null)
        {
            return oneToOneMappingConfigurationValues;
        }
        else
        {
            return new ArrayList<MappingConfigurationValue>(map.values());
        }
    }
}
