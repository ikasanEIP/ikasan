package org.ikasan.configurationService.metadata.module;

import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.ikasan.spec.module.ModuleType;

import java.util.ArrayList;
import java.util.List;

public class TestModule implements Module<Flow>
{
    private List<Flow> flows;

    @Override
    public void setType(ModuleType moduleType) {

    }

    @Override
    public ModuleType getType() {
        return ModuleType.INTEGRATION_MODULE;
    }

    @Override
    public String getVersion()
    {
        return "module version";
    }

    @Override
    public String getName()
    {
        return "module name";
    }

    @Override
    public List<Flow> getFlows()
    {
        if(flows == null)
        {
            flows = new ArrayList<>();
        }

        return flows;
    }

    @Override
    public Flow getFlow(String name)
    {
        return null;
    }

    @Override
    public String getDescription()
    {
        return "module description";
    }

    @Override
    public void setDescription(String description)
    {

    }

    @Override
    public String getUrl()
    {
        return "url";
    }

    @Override
    public void setUrl(String url)
    {

    }
}
