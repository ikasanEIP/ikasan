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
package org.ikasan.configurationService.util;

 import org.apache.commons.lang3.StringEscapeUtils;
 import org.ikasan.configurationService.model.*;
 import org.ikasan.spec.configuration.Configuration;
 import org.ikasan.spec.configuration.ConfigurationParameter;

 import java.util.List;
 import java.util.Map;

 /**
  * @author Ikasan Development Team
  *
  */
 public class ComponentConfigurationExportHelper
 {
     private static final String XML_TAG = "<?xml version=\"1.0\"?>";
     private static final String NON_EMBEDED_START_TAG = "<componentConfiguration xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
             "xsi:noNamespaceSchemaLocation=\"{$schemaLocation}\">";
     private static final String EMBEDED_START_TAG = "<componentConfiguration>";
     private static final String END_TAG = "</componentConfiguration>";
     private static final String CONFIGURATION_ID_START_TAG = "<id>";
     private static final String CONFIGURATION_ID_END_TAG = "</id>";
     private static final String CONFIGURATION_DESCRIPTION_START_TAG = "<description>";
     private static final String CONFIGURATION_DESCRIPTION_END_TAG = "</description>";
     private static final String CONFIGURATION_PARAMETERS_START_TAG = "<parameters>";
     private static final String CONFIGURATION_PARAMETERS_END_TAG = "</parameters>";
     private static final String CONFIGURATION_PARAMETER_INTEGER_START_TAG = "<integerParameter>";
     private static final String CONFIGURATION_PARAMETERS_INTEGER_END_TAG = "</integerParameter>";
     private static final String CONFIGURATION_PARAMETER_MASKED_STRING_START_TAG = "<maskedStringParameter>";
     private static final String CONFIGURATION_PARAMETERS_MASKED_STRING_END_TAG = "</maskedStringParameter>";
     private static final String CONFIGURATION_PARAMETER_STRING_START_TAG = "<stringParameter>";
     private static final String CONFIGURATION_PARAMETERS_STRING_END_TAG = "</stringParameter>";
     private static final String CONFIGURATION_PARAMETER_BOOLEAN_START_TAG = "<booleanParameter>";
     private static final String CONFIGURATION_PARAMETERS_BOOLEAN_END_TAG = "</booleanParameter>";
     private static final String CONFIGURATION_PARAMETER_LONG_START_TAG = "<longParameter>";
     private static final String CONFIGURATION_PARAMETERS_LONG_END_TAG = "</longParameter>";
     private static final String CONFIGURATION_PARAMETER_MAP_START_TAG = "<mapParameter>";
     private static final String CONFIGURATION_PARAMETERS_MAP_END_TAG = "</mapParameter>";
     private static final String CONFIGURATION_PARAMETER_LIST_START_TAG = "<listParameter>";
     private static final String CONFIGURATION_PARAMETERS_LIST_END_TAG = "</listParameter>";

     private static final String ITEM_START_TAG = "<item>";
     private static final String ITEM_END_TAG = "</item>";
     private static final String NAME_START_TAG = "<name>";
     private static final String NAME_END_TAG = "</name>";
     private static final String VALUE_START_TAG = "<value>";
     private static final String VALUE_END_TAG = "</value>";

     private String schemaLocation = "schemaLocation";
     private Boolean isEmbeded = false;

     public ComponentConfigurationExportHelper()
     {
         super();
     }

     public String getComponentConfigurationExportXml(Configuration configuration)
     {
         StringBuffer exportString = new StringBuffer();

         String startTag = EMBEDED_START_TAG;

         if(!isEmbeded)
         {
             exportString.append(XML_TAG);
             startTag = NON_EMBEDED_START_TAG;
             exportString.append(startTag.replace("{$schemaLocation}", schemaLocation));
         }
         else
         {
             exportString.append(startTag);
         }

         List<ConfigurationParameter> parameters = (List<ConfigurationParameter>)configuration.getParameters();

         exportString.append(CONFIGURATION_ID_START_TAG)
                 .append(StringEscapeUtils.escapeXml(configuration.getConfigurationId()))
                 .append(CONFIGURATION_ID_END_TAG);

         exportString.append(CONFIGURATION_DESCRIPTION_START_TAG)
                 .append((configuration.getDescription() == null) ? "" : StringEscapeUtils.escapeXml(configuration.getDescription()))
                 .append(CONFIGURATION_DESCRIPTION_END_TAG);

         exportString.append(CONFIGURATION_PARAMETERS_START_TAG);

         for(ConfigurationParameter parameter: parameters)
         {
             if(parameter instanceof ConfigurationParameterIntegerImpl)
             {
                exportString.append(CONFIGURATION_PARAMETER_INTEGER_START_TAG)
                        .append(NAME_START_TAG)
                        .append(StringEscapeUtils.escapeXml(parameter.getName()))
                        .append(NAME_END_TAG)
                        .append(VALUE_START_TAG)
                        .append((parameter.getValue() == null) ? "" : (Integer)parameter.getValue())
                        .append(VALUE_END_TAG)
                        .append(CONFIGURATION_DESCRIPTION_START_TAG)
                        .append((parameter.getDescription() == null) ? "" : StringEscapeUtils.escapeXml(parameter.getDescription()))
                        .append(CONFIGURATION_DESCRIPTION_END_TAG)
                        .append(CONFIGURATION_PARAMETERS_INTEGER_END_TAG);
             }
             else if(parameter instanceof ConfigurationParameterMaskedStringImpl)
             {
                 exportString.append(CONFIGURATION_PARAMETER_MASKED_STRING_START_TAG)
                         .append(NAME_START_TAG)
                         .append(StringEscapeUtils.escapeXml(parameter.getName()))
                         .append(NAME_END_TAG)
                         .append(VALUE_START_TAG)
                         .append((parameter.getValue() == null) ? "" : StringEscapeUtils.escapeXml((String)parameter.getValue()))
                         .append(VALUE_END_TAG)
                         .append(CONFIGURATION_DESCRIPTION_START_TAG)
                         .append((parameter.getDescription() == null) ? "" : StringEscapeUtils.escapeXml( parameter.getDescription()))
                         .append(CONFIGURATION_DESCRIPTION_END_TAG)
                         .append(CONFIGURATION_PARAMETERS_MASKED_STRING_END_TAG);
             }
             else if(parameter instanceof ConfigurationParameterStringImpl)
             {
                 exportString.append(CONFIGURATION_PARAMETER_STRING_START_TAG)
                         .append(NAME_START_TAG)
                         .append(StringEscapeUtils.escapeXml(parameter.getName()))
                         .append(NAME_END_TAG)
                         .append(VALUE_START_TAG)
                         .append((parameter.getValue() == null) ? "" : StringEscapeUtils.escapeXml((String)parameter.getValue()))
                         .append(VALUE_END_TAG)
                         .append(CONFIGURATION_DESCRIPTION_START_TAG)
                         .append((parameter.getDescription() == null) ? "" : StringEscapeUtils.escapeXml(parameter.getDescription()))
                         .append(CONFIGURATION_DESCRIPTION_END_TAG)
                         .append(CONFIGURATION_PARAMETERS_STRING_END_TAG);
             }
             else if(parameter instanceof ConfigurationParameterBooleanImpl)
             {
                 exportString.append(CONFIGURATION_PARAMETER_BOOLEAN_START_TAG)
                         .append(NAME_START_TAG)
                         .append(StringEscapeUtils.escapeXml(parameter.getName()))
                         .append(NAME_END_TAG)
                         .append(VALUE_START_TAG)
                         .append((parameter.getValue() == null) ? "" : (Boolean)parameter.getValue())
                         .append(VALUE_END_TAG)
                         .append(CONFIGURATION_DESCRIPTION_START_TAG)
                         .append((parameter.getDescription() == null) ? "" : StringEscapeUtils.escapeXml(parameter.getDescription()))
                         .append(CONFIGURATION_DESCRIPTION_END_TAG)
                         .append(CONFIGURATION_PARAMETERS_BOOLEAN_END_TAG);
             }
             else if(parameter instanceof ConfigurationParameterLongImpl)
             {
                 exportString.append(CONFIGURATION_PARAMETER_LONG_START_TAG)
                         .append(NAME_START_TAG)
                         .append(StringEscapeUtils.escapeXml(parameter.getName()))
                         .append(NAME_END_TAG)
                         .append(VALUE_START_TAG)
                         .append((parameter.getValue() == null) ? "" : (Long)parameter.getValue())
                         .append(VALUE_END_TAG)
                         .append(CONFIGURATION_DESCRIPTION_START_TAG)
                         .append((parameter.getDescription() == null) ? "" : StringEscapeUtils.escapeXml(parameter.getDescription()))
                         .append(CONFIGURATION_DESCRIPTION_END_TAG)
                         .append(CONFIGURATION_PARAMETERS_LONG_END_TAG);
             }
             else if(parameter instanceof ConfigurationParameterMapImpl)
             {
                 Map<String, String> map = (Map<String, String>)parameter.getValue();

                 exportString.append(CONFIGURATION_PARAMETER_MAP_START_TAG)
                    .append(NAME_START_TAG)
                    .append(StringEscapeUtils.escapeXml(parameter.getName()))
                    .append(NAME_END_TAG)
                    .append(CONFIGURATION_DESCRIPTION_START_TAG)
                    .append((parameter.getDescription() == null) ? "" : StringEscapeUtils.escapeXml(parameter.getDescription()))
                    .append(CONFIGURATION_DESCRIPTION_END_TAG);

                 for(String key: map.keySet())
                 {
                     exportString.append(ITEM_START_TAG)
                             .append(NAME_START_TAG)
                             .append(StringEscapeUtils.escapeXml(key))
                             .append(NAME_END_TAG)
                             .append(VALUE_START_TAG)
                             .append(StringEscapeUtils.escapeXml(map.get(key)))
                             .append(VALUE_END_TAG)
                             .append(ITEM_END_TAG);
                 }

                 exportString.append(CONFIGURATION_PARAMETERS_MAP_END_TAG);
             }
             else if(parameter instanceof ConfigurationParameterListImpl)
             {
                 List<String> list = (List<String>)parameter.getValue();

                 exportString.append(CONFIGURATION_PARAMETER_LIST_START_TAG)
                     .append(NAME_START_TAG)
                     .append(StringEscapeUtils.escapeXml(parameter.getName()))
                     .append(NAME_END_TAG)
                     .append(CONFIGURATION_DESCRIPTION_START_TAG)
                     .append((parameter.getDescription() == null) ? "" : StringEscapeUtils.escapeXml(parameter.getDescription()))
                     .append(CONFIGURATION_DESCRIPTION_END_TAG);

                 for (String value: list)
                 {
                     exportString.append(VALUE_START_TAG)
                         .append(StringEscapeUtils.escapeXml(value))
                         .append(VALUE_END_TAG);
                 }

                 exportString.append(CONFIGURATION_PARAMETERS_LIST_END_TAG);
             }
         }

         exportString.append(CONFIGURATION_PARAMETERS_END_TAG)
            .append(END_TAG);

         if(this.isEmbeded)
         {
             return exportString.toString();
         }
         else
         {
             return exportString.toString().trim();
         }
     }


     public void setEmbeded(Boolean embeded)
     {
         isEmbeded = embeded;
     }
 }
