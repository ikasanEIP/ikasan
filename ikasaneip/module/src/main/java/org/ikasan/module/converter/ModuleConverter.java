package org.ikasan.module.converter;

import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.component.transformation.TransformationException;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.flow.FlowElement;
import org.ikasan.spec.module.Module;
import org.ikasan.spec.flow.Flow;
import org.ikasan.topology.model.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * ModuleConverter class is utility class converting Runtime Module object to DB(DTO) type object.
 *
 * @author Ikasan Development Team
 */
public class ModuleConverter implements Converter<Module<Flow>, org.ikasan.topology.model.Module>
{
    /**
     * Converts runtime module view to DTO type module view.
     *
     * @param moduleRuntime runtime module representation
     * @return topology DTO module representation
     * @throws TransformationException could be thrown
     */
    @Override
    public org.ikasan.topology.model.Module convert(Module<Flow> moduleRuntime) throws TransformationException
    {

        Set<org.ikasan.topology.model.Flow> flows = new HashSet<>();

        int flowOrder = 0;
        for (org.ikasan.spec.flow.Flow flow : moduleRuntime.getFlows()) {

            org.ikasan.topology.model.Flow topologyFlow = new org.ikasan.topology.model.Flow(flow.getName(), "description", null);
            topologyFlow.setOrder(flowOrder++);

            if (flow instanceof ConfiguredResource) {
                topologyFlow.setConfigurationId(((ConfiguredResource) flow).getConfiguredResourceId());
                topologyFlow.setConfigurable(true);
            } else {
                topologyFlow.setConfigurable(false);
            }

            flows.add(topologyFlow);

            Set<Component> components
                    = new HashSet<Component>();

            int order = 0;

            for (FlowElement<?> flowElement : flow.getFlowElements()) {

                org.ikasan.topology.model.Component component = new org.ikasan.topology.model.Component();
                component.setName(flowElement.getComponentName());
                if (flowElement.getDescription() != null) {
                    component.setDescription(flowElement.getDescription());
                } else {
                    component.setDescription("No description.");
                }

                if (flowElement.getFlowComponent() instanceof ConfiguredResource) {
                    component.setConfigurationId(((ConfiguredResource) flowElement.getFlowComponent()).getConfiguredResourceId());
                    component.setConfigurable(true);
                } else {
                    component.setConfigurable(false);
                }

                if(flowElement.getFlowElementInvoker() instanceof  ConfiguredResource) {
                    component.setInvokerConfigurationId(((ConfiguredResource)flowElement.getFlowElementInvoker()).getConfiguredResourceId());
                    component.setInvokerConfigurable(true);
                }
                else {
                    component.setInvokerConfigurable(false);
                }

                component.setOrder(order++);
                components.add(component);
            }

            topologyFlow.setComponents(components);
        }

        org.ikasan.topology.model.Module module = new org.ikasan.topology.model.Module(moduleRuntime.getName(),null,null,null,null,null);
        module.setFlows(flows);
        return module;
    }
}
