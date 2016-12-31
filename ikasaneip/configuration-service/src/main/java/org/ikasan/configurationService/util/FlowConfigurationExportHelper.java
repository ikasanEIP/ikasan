package org.ikasan.configurationService.util;

import org.apache.log4j.Logger;
import org.ikasan.spec.configuration.Configuration;
import org.ikasan.spec.configuration.ConfigurationManagement;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.topology.model.Flow;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ikasan Development Team on 20/12/2016.
 */
public class FlowConfigurationExportHelper extends ConfigurationHelper
{
    private Logger logger = Logger.getLogger(FlowConfigurationExportHelper.class);

    private static final String XML_TAG = "<?xml version=\"1.0\"?>";
    private static final String NON_EMBEDED_START_TAG = "<flowConfiguration xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
            "xsi:noNamespaceSchemaLocation=\"{$schemaLocation}\">";

    private static final String EMBEDED_START_TAG = "<flowConfiguration>";
    private static final String END_TAG = "</flowConfiguration>";

    private static final String COMPONENT_CONFIGURATIONS_START_TAG = "<componentConfigurations>";
    private static final String COMPONENT_CONFIGURATIONS_END_TAG = "</componentConfigurations>";

    private static final String NAME_START_TAG = "<name>";
    private static final String NAME_END_TAG = "</name>";

    private static final String ID_START_TAG = "<id>";
    private static final String ID_END_TAG = "</id>";

    private static final String MODULE_NAME_START_TAG = "<module>";
    private static final String MODULE_NAME_END_TAG = "</module>";

    private String schemaLocation = "schemaLocation";

    private Boolean isEmbeded = false;

    public FlowConfigurationExportHelper(ConfigurationManagement<ConfiguredResource, Configuration> configurationService,
                                         ConfigurationCreationHelper helper)
    {
        super(configurationService, helper);
    }

    public String getFlowConfigurationExportXml(Flow flow)
    {
        StringBuffer xml = new StringBuffer("");

        String startTag = EMBEDED_START_TAG;

        if(!isEmbeded)
        {
            xml.append(XML_TAG);
            startTag = NON_EMBEDED_START_TAG;
            xml.append(startTag.replace("{$schemaLocation}", schemaLocation));

            xml.append(MODULE_NAME_START_TAG).append(flow.getModule().getName()).append(MODULE_NAME_END_TAG);
        }
        else
        {
            xml.append(startTag);
        }

        xml.append(NAME_START_TAG).append(flow.getName()).append(NAME_END_TAG);

        xml.append(COMPONENT_CONFIGURATIONS_START_TAG);

        List<Configuration> configurationList = super.getFlowConfigurations(flow);

        logger.info("Number of configurations: " + configurationList.size());

        ArrayList<String> configuredResourceIds = new ArrayList<String>();

        for(Configuration configuration: configurationList)
        {
            logger.info("Setting configuration to: " + configuration);

            if(configuration != null && !configuredResourceIds.contains(configuration.getConfigurationId()))
            {
                ComponentConfigurationExportHelper componentConfigurationExportHelper
                        = new ComponentConfigurationExportHelper();

                componentConfigurationExportHelper.setEmbeded(true);

                xml.append(componentConfigurationExportHelper.getComponentConfigurationExportXml(configuration));

                configuredResourceIds.add(configuration.getConfigurationId());
            }
        }

        xml.append(COMPONENT_CONFIGURATIONS_END_TAG);
        xml.append(END_TAG);

        logger.info(xml.toString());

        if(!this.isEmbeded)
        {
            return xml.toString().trim();
        }
        else
        {
            return xml.toString();
        }
    }

    public Boolean getEmbeded()
    {
        return isEmbeded;
    }

    public void setEmbeded(Boolean embeded)
    {
        isEmbeded = embeded;
    }
}
