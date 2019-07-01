package org.ikasan.topology.metadata.model;

import org.ikasan.spec.metadata.FlowMetaData;
import org.ikasan.spec.metadata.ModuleMetaData;

import java.util.ArrayList;
import java.util.List;

public class ModuleMetaDataImpl implements ModuleMetaData
{
    private String name;
    private String description;
    private String version;
    private List<FlowMetaData> flows;

    @Override
    public void setName(String name)
    {
        this.name = name;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public void setDescription(String description)
    {
        this.description = description;
    }

    @Override
    public String getDescription()
    {
        return this.description;
    }

    @Override
    public void setVersion(String version)
    {
        this.version = version;
    }

    @Override
    public String getVersion()
    {
        return this.version;
    }

    @Override
    public void setFlows(List<FlowMetaData> flows)
    {
        this.flows = flows;
    }

    @Override
    public List<FlowMetaData> getFlows()
    {
        if(flows == null)
        {
            flows = new ArrayList<>();
        }

        return flows;
    }
}
