/*
 * $Id: MappingConfigurationExportHelper.java 40648 2014-11-07 11:12:53Z stewmi $
 * $URL: https://svc-vcs-prd.uk.mizuho-sc.com:18080/svn/architecture/cmi2/trunk/projects/mappingConfigurationUI/war/src/main/java/org/ikasan/mapping/configuration/ui/util/MappingConfigurationExportHelper.java $
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
import java.util.Date;
import java.util.List;

import org.ikasan.dashboard.ui.framework.util.XmlFormatter;
import org.ikasan.mapping.model.KeyLocationQuery;
import org.ikasan.mapping.model.MappingConfiguration;

/**
 * @author CMI2 Development Team
 *
 */
public class MappingConfigurationExportHelper
{
    private static final String XML_TAG = "<?xml version=\"1.0\"?>";
    private static final String START_TAG = "<mappingConfiguration xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
    		"xsi:noNamespaceSchemaLocation=\"{$schemaLocation}\">";
    private static final String END_TAG = "</mappingConfiguration>";
    private static final String CLIENT_START_TAG = "<client>";
    private static final String CLIENT_END_TAG = "</client>";
    private static final String TYPE_START_TAG = "<type>";
    private static final String TYPE_END_TAG = "</type>";
    private static final String SOURCE_CONTEXT_START_TAG = "<sourceContext>";
    private static final String SOURCE_CONTEXT_END_TAG = "</sourceContext>";
    private static final String TARGET_CONTEXT_START_TAG = "<targetContext>";
    private static final String TARGET_CONTEXT_END_TAG = "</targetContext>";
    private static final String DESCRIPTION_START_TAG = "<description>";
    private static final String DESCRIPTION_END_TAG = "</description>";
    private static final String NUMBER_OF_SOURCE_PARAMS_START_TAG = "<numberOfSourceParams>";
    private static final String NUMBER_OF_SOURCE_PARAMS_END_TAG = "</numberOfSourceParams>";
    private static final String SOURCE_CONFIGURATION_VALUE_QUERIES_START_TAG = "<sourceConfigurationValueQueries>";
    private static final String SOURCE_CONFIGURATION_VALUE_QUERIES_END_TAG = "</sourceConfigurationValueQueries>";
    private static final String SOURCE_CONFIGURATION_VALUE_QUERY_START_TAG = "<sourceConfigurationValueQuery>";
    private static final String SOURCE_CONFIGURATION_VALUE_QUERY_END_TAG = "</sourceConfigurationValueQuery>";
    private static final String EXPORT_DATE_TIME_START_TAG = "<exportDateTime>";
    private static final String EXPORT_DATE_TIME_END_TAG = "</exportDateTime>";

    private MappingConfigurationValuesExportHelper mappingConfigurationValuesExportHelper;

    /**
     * @param schemaLocation
     */
    public MappingConfigurationExportHelper(MappingConfigurationValuesExportHelper mappingConfigurationValuesExportHelper)
    {
        super();
        this.mappingConfigurationValuesExportHelper = mappingConfigurationValuesExportHelper;
    }

    /**
     * Helper method to create the XML export document.
     * 
     * @param mappingConfiguration
     * @return
     */
    public String getMappingConfigurationExportXml(MappingConfiguration mappingConfiguration, 
            List<KeyLocationQuery> keyLocationQueries, String schemaLocation)
    {
        StringBuffer exportString = new StringBuffer();

        exportString.append(XML_TAG);
        String startTag = START_TAG;
        exportString.append(startTag.replace("{$schemaLocation}", schemaLocation));

        exportString.append(EXPORT_DATE_TIME_START_TAG);
        exportString.append(DateFormat.getDateTimeInstance
            (DateFormat.LONG, DateFormat.LONG).format(new Date()));
        exportString.append(EXPORT_DATE_TIME_END_TAG);

        exportString.append(CLIENT_START_TAG);
        exportString.append(mappingConfiguration.getConfigurationServiceClient().getName());
        exportString.append(CLIENT_END_TAG);

        exportString.append(TYPE_START_TAG);
        exportString.append(mappingConfiguration.getConfigurationType().getName());
        exportString.append(TYPE_END_TAG);

        exportString.append(SOURCE_CONTEXT_START_TAG);
        exportString.append(mappingConfiguration.getSourceContext().getName());
        exportString.append(SOURCE_CONTEXT_END_TAG);

        exportString.append(TARGET_CONTEXT_START_TAG);
        exportString.append(mappingConfiguration.getTargetContext().getName());
        exportString.append(TARGET_CONTEXT_END_TAG);

        exportString.append(DESCRIPTION_START_TAG);
        exportString.append(mappingConfiguration.getDescription());
        exportString.append(DESCRIPTION_END_TAG);

        exportString.append(NUMBER_OF_SOURCE_PARAMS_START_TAG);
        exportString.append(mappingConfiguration.getNumberOfParams());
        exportString.append(NUMBER_OF_SOURCE_PARAMS_END_TAG);

        exportString.append(SOURCE_CONFIGURATION_VALUE_QUERIES_START_TAG);

        for(KeyLocationQuery query: keyLocationQueries)
        {
            exportString.append(SOURCE_CONFIGURATION_VALUE_QUERY_START_TAG);
            exportString.append(query.getValue());
            exportString.append(SOURCE_CONFIGURATION_VALUE_QUERY_END_TAG);
        }

        exportString.append(SOURCE_CONFIGURATION_VALUE_QUERIES_END_TAG);

        exportString.append(this.mappingConfigurationValuesExportHelper
            .getMappingConfigurationExportXml(mappingConfiguration, false, schemaLocation));

        exportString.append(END_TAG);

        return XmlFormatter.format(exportString.toString().trim());
    }

}
