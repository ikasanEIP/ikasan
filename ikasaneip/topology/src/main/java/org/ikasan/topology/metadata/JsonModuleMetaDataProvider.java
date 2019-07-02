package org.ikasan.topology.metadata;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.metadata.*;
import org.ikasan.spec.module.Module;
import org.ikasan.topology.metadata.model.FlowElementMetaDataImpl;
import org.ikasan.topology.metadata.model.FlowMetaDataImpl;
import org.ikasan.topology.metadata.model.ModuleMetaDataImpl;
import org.ikasan.topology.metadata.model.TransitionImpl;

public class JsonModuleMetaDataProvider implements ModuleMetaDataProvider<String>
{
    JsonFlowMetaDataProvider flowMetaDataProvider;

    public JsonModuleMetaDataProvider(JsonFlowMetaDataProvider flowMetaDataProvider)
    {
        this.flowMetaDataProvider = flowMetaDataProvider;
        if(this.flowMetaDataProvider == null)
        {
            throw new IllegalArgumentException("flowMetaDataProvider cannot be null!");
        }
    }

    @Override
    public String describeModule(Module<Flow> module)
    {
        String result;

        try
        {
            ModuleMetaData moduleMetaData = new ModuleMetaDataImpl();
            moduleMetaData.setName(module.getName());
            moduleMetaData.setDescription(module.getDescription());
            moduleMetaData.setVersion(module.getVersion());

            for(Flow flow: module.getFlows())
            {
                moduleMetaData.getFlows().add(flowMetaDataProvider
                    .deserialiseFlow(flowMetaDataProvider.describeFlow(flow)));
            }

            ObjectMapper mapper = new ObjectMapper();

            result = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(moduleMetaData);
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
            ObjectMapper mapper = new ObjectMapper();
            SimpleModule m = new SimpleModule();
            m.addAbstractTypeMapping(ModuleMetaData.class, ModuleMetaDataImpl.class);
            m.addAbstractTypeMapping(FlowMetaData.class, FlowMetaDataImpl.class);
            m.addAbstractTypeMapping(FlowElementMetaData.class, FlowElementMetaDataImpl.class);
            m.addAbstractTypeMapping(Transition.class, TransitionImpl.class);

            mapper.registerModule(m);

            //JSON file to Java object
            result = mapper.readValue(module, ModuleMetaDataImpl.class);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Exception has occurred creating module meta data object!", e);
        }

        return result;
    }
}
