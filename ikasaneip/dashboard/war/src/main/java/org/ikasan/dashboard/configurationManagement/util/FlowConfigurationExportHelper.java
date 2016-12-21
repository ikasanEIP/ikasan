package org.ikasan.dashboard.configurationManagement.util;

import org.ikasan.spec.configuration.Configuration;
import org.ikasan.topology.model.Flow;

import java.util.List;

/**
 * Created by stewmi on 20/12/2016.
 */
public class FlowConfigurationExportHelper
{
    private Flow flow;
    private List<Configuration> configurationList;

    public FlowConfigurationExportHelper(Flow flow, List<Configuration> configurationList)
    {
        this.flow = flow;
        this.configurationList = configurationList;
    }

    public String getFlowConfigurationExportXml()
    {
        return "";
    }
}
