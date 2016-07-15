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
package org.ikasan.dashboard.ui.mappingconfiguration.util;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.ikasan.dashboard.ui.framework.util.XmlFormatter;
import org.ikasan.mapping.model.KeyLocationQuery;
import org.ikasan.mapping.model.MappingConfiguration;

/**
 * @author Ikasan Development Team
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
        exportString.append(StringEscapeUtils.escapeXml(mappingConfiguration.getConfigurationServiceClient().getName()));
        exportString.append(CLIENT_END_TAG);

        exportString.append(TYPE_START_TAG);
        exportString.append(StringEscapeUtils.escapeXml(mappingConfiguration.getConfigurationType().getName()));
        exportString.append(TYPE_END_TAG);

        exportString.append(SOURCE_CONTEXT_START_TAG);
        exportString.append(StringEscapeUtils.escapeXml(mappingConfiguration.getSourceContext().getName()));
        exportString.append(SOURCE_CONTEXT_END_TAG);

        exportString.append(TARGET_CONTEXT_START_TAG);
        exportString.append(StringEscapeUtils.escapeXml(mappingConfiguration.getTargetContext().getName()));
        exportString.append(TARGET_CONTEXT_END_TAG);

        exportString.append(DESCRIPTION_START_TAG);
        exportString.append(StringEscapeUtils.escapeXml(mappingConfiguration.getDescription()));
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
