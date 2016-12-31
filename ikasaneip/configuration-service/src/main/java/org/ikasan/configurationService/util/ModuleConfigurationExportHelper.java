package org.ikasan.configurationService.util;

import org.apache.log4j.Logger;
import org.ikasan.spec.configuration.Configuration;
import org.ikasan.spec.configuration.ConfigurationManagement;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.topology.model.Flow;
import org.ikasan.topology.model.Module;

/**
 * Created by Ikasan Development Team on 20/12/2016.
 */
public class ModuleConfigurationExportHelper extends ConfigurationHelper
{
    private Logger logger = Logger.getLogger(ModuleConfigurationExportHelper.class);

    private static final String XML_TAG = "<?xml version=\"1.0\"?>";
    private static final String NON_EMBEDED_START_TAG = "<moduleConfiguration xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
            "xsi:noNamespaceSchemaLocation=\"{$schemaLocation}\">";

    private static final String EMBEDED_START_TAG = "<moduleConfiguration>";
    private static final String END_TAG = "</moduleConfiguration>";

    private static final String FLOW_CONFIGURATIONS_START_TAG = "<flowConfigurations>";
    private static final String FLOW_CONFIGURATIONS_END_TAG = "</flowConfigurations>";

    private static final String NAME_START_TAG = "<name>";
    private static final String NAME_END_TAG = "</name>";

    private static final String ID_START_TAG = "<id>";
    private static final String ID_END_TAG = "</id>";

    private static final String MODULE_NAME_START_TAG = "<module>";
    private static final String MODULE_NAME_END_TAG = "</module>";

    private String schemaLocation = "schemaLocation";

    public ModuleConfigurationExportHelper(ConfigurationManagement<ConfiguredResource, Configuration> configurationService,
                                           ConfigurationCreationHelper helper)
    {
        super(configurationService, helper);
    }

    public String getModuleConfigurationExportXml(Module module)
    {
        StringBuffer xml = new StringBuffer("");

        String startTag = EMBEDED_START_TAG;

        xml.append(XML_TAG);
        startTag = NON_EMBEDED_START_TAG;
        xml.append(startTag.replace("{$schemaLocation}", schemaLocation));

        xml.append(NAME_START_TAG).append(module.getName()).append(NAME_END_TAG);

        xml.append(FLOW_CONFIGURATIONS_START_TAG);

        if(module.getFlows() != null)
        {
            for (Flow flow : module.getFlows())
            {
                if (flow != null)
                {
                    FlowConfigurationExportHelper flowConfigurationExportHelper
                            = new FlowConfigurationExportHelper(super.configurationService, helper);

                    flowConfigurationExportHelper.setEmbeded(true);

                    xml.append(flowConfigurationExportHelper.getFlowConfigurationExportXml(flow));
                }
            }
        }

        xml.append(FLOW_CONFIGURATIONS_END_TAG);
        xml.append(END_TAG);

        logger.info(xml.toString());
        return xml.toString().trim();
    }
}
