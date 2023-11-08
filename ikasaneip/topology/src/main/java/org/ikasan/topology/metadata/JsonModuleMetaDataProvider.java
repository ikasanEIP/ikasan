package org.ikasan.topology.metadata;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.metadata.*;
import org.ikasan.spec.module.Module;
import org.ikasan.spec.module.StartupControl;
import org.ikasan.topology.metadata.model.*;

import java.util.Map;

public class JsonModuleMetaDataProvider implements ModuleMetaDataProvider<String>
{
    private JsonFlowMetaDataProvider flowMetaDataProvider;
    private ObjectMapper mapper;

    public JsonModuleMetaDataProvider(JsonFlowMetaDataProvider flowMetaDataProvider)
    {
        this.flowMetaDataProvider = flowMetaDataProvider;
        if(this.flowMetaDataProvider == null)
        {
            throw new IllegalArgumentException("flowMetaDataProvider cannot be null!");
        }

        this.mapper = new ObjectMapper();

        SimpleModule m = new SimpleModule();
        m.addAbstractTypeMapping(ModuleMetaData.class, ModuleMetaDataImpl.class);
        m.addAbstractTypeMapping(FlowMetaData.class, FlowMetaDataImpl.class);
        m.addAbstractTypeMapping(FlowElementMetaData.class, FlowElementMetaDataImpl.class);
        m.addAbstractTypeMapping(Transition.class, TransitionImpl.class);
        m.addAbstractTypeMapping(DecoratorMetaData.class, DecoratorMetaDataImpl.class);

        this.mapper.registerModule(m);
    }

    @Override
    public String describeModule(Module<Flow> module, Map<String, StartupControl> stringStartupControlMap)
    {
        String result;

        try
        {
            ModuleMetaData moduleMetaData = new ModuleMetaDataImpl();
            moduleMetaData.setUrl(module.getUrl());
            moduleMetaData.setName(module.getName());
            moduleMetaData.setDescription(module.getDescription());
            moduleMetaData.setVersion(module.getVersion());
            moduleMetaData.setType(module.getType());
            moduleMetaData.setPort(module.getPort());
            moduleMetaData.setProtocol(module.getProtocol());
            moduleMetaData.setContext(module.getContext());
            moduleMetaData.setHost(module.getHost());

            if(module instanceof ConfiguredResource resource) {
                moduleMetaData.setConfiguredResourceId(resource.getConfiguredResourceId());
            }

            for(Flow flow: module.getFlows())
            {
                moduleMetaData.getFlows().add(flowMetaDataProvider
                    .deserialiseFlow(flowMetaDataProvider.describeFlow(flow, stringStartupControlMap.get(flow.getName()))));
            }

            result = this.mapper.writerWithDefaultPrettyPrinter().writeValueAsString(moduleMetaData);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Exception has occurred creating flow meta data json!", e);
        }

        return result;
    }

    @Override
    public ModuleMetaData deserialiseModule(String module)
    {
        ModuleMetaDataImpl result;

        try
        {
            //JSON file to Java object
            result = this.mapper.readValue(module, ModuleMetaDataImpl.class);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Exception has occurred creating module meta data object!", e);
        }

        return result;
    }
}
