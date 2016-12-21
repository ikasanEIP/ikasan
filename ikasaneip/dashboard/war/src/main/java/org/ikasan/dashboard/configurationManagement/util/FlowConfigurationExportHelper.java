package org.ikasan.dashboard.configurationManagement.util;

import org.apache.log4j.Logger;
import org.ikasan.dashboard.ui.framework.util.XmlFormatter;
import org.ikasan.spec.configuration.Configuration;
import org.ikasan.spec.configuration.ConfigurationManagement;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.topology.model.Flow;


import java.util.List;

/**
 * Created by stewmi on 20/12/2016.
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

    private Flow flow;

    public FlowConfigurationExportHelper(Flow flow, ConfigurationManagement<ConfiguredResource, Configuration> configurationService)
    {
        super(configurationService);
        this.flow = flow;
    }

    public String getFlowConfigurationExportXml()
    {
        StringBuffer xml = new StringBuffer("");

        String startTag = EMBEDED_START_TAG;

        if(!isEmbeded)
        {
            xml.append(XML_TAG);
            startTag = NON_EMBEDED_START_TAG;
            xml.append(startTag.replace("{$schemaLocation}", schemaLocation));
        }
        else
        {
            xml.append(startTag);
        }

        xml.append(ID_START_TAG).append(flow.getConfigurationId()).append(ID_END_TAG);
        xml.append(MODULE_NAME_START_TAG).append(flow.getModule().getName()).append(MODULE_NAME_END_TAG);
        xml.append(NAME_START_TAG).append(flow.getName()).append(NAME_END_TAG);

        if(flow.isConfigurable())
        {
            //todo deal with the actual flow configuration
        }

        xml.append(COMPONENT_CONFIGURATIONS_START_TAG);

        List<Configuration> configurationList = super.getFlowConfigurations(this.flow);

        logger.info("Number of configurations: " + configurationList.size());
        for(Configuration configuration: configurationList)
        {
            logger.info("Setting configuration to: " + configuration);

            if(configuration != null)
            {
                ComponentConfigurationExportHelper componentConfigurationExportHelper
                        = new ComponentConfigurationExportHelper(configuration);

                componentConfigurationExportHelper.setEmbeded(true);

                xml.append(componentConfigurationExportHelper.getComponentConfigurationExportXml());
                xml.append("\r\n");
            }
        }

        xml.append(COMPONENT_CONFIGURATIONS_END_TAG);
        xml.append(END_TAG);

        logger.info(xml.toString());

        if(!this.isEmbeded)
        {
            return XmlFormatter.format(xml.toString().trim());
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
