package org.ikasan.topology.metadata;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.ikasan.spec.component.endpoint.Broker;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.component.filter.Filter;
import org.ikasan.spec.component.routing.MultiRecipientRouter;
import org.ikasan.spec.component.routing.SingleRecipientRouter;
import org.ikasan.spec.component.splitting.Splitter;
import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.component.transformation.Translator;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.flow.FlowConfiguration;
import org.ikasan.spec.flow.FlowElement;
import org.ikasan.spec.metadata.FlowElementMetaData;
import org.ikasan.spec.metadata.FlowMetaData;
import org.ikasan.spec.metadata.FlowMetaDataProvider;
import org.ikasan.spec.metadata.Transition;
import org.ikasan.topology.metadata.model.FlowElementMetaDataImpl;
import org.ikasan.topology.metadata.model.FlowMetaDataImpl;
import org.ikasan.topology.metadata.model.TransitionImpl;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;

import java.util.Map;

/**
 * Implementation of FlowMetaDataProvider that provides a JSON representation of a flow.
 *
 * @author Ikasan Development Team
 */
public class JsonFlowMetaDataProvider implements FlowMetaDataProvider<String>
{

    private ObjectMapper mapper;

    /**
     * Constructor
     */
    public JsonFlowMetaDataProvider()
    {
        mapper = new ObjectMapper();

        SimpleModule m = new SimpleModule();
        m.addAbstractTypeMapping(FlowMetaData.class, FlowMetaDataImpl.class);
        m.addAbstractTypeMapping(FlowElementMetaData.class, FlowElementMetaDataImpl.class);
        m.addAbstractTypeMapping(Transition.class, TransitionImpl.class);

        this.mapper.registerModule(m);
    }

    @Override
    public String describeFlow(Flow flow)
    {
        String result;

        try
        {
            FlowConfiguration configuration = flow.getFlowConfiguration();

            FlowMetaDataImpl flowMetaData = new FlowMetaDataImpl();
            flowMetaData.setName(flow.getName());
            flowMetaData.setConsumer(describeFlowElement(flowMetaData, configuration.getConsumerFlowElement()));

            if(flow instanceof ConfiguredResource)
            {
                flowMetaData.setConfigurationId(((ConfiguredResource) flow).getConfiguredResourceId());
            }

            result = this.mapper.writerWithDefaultPrettyPrinter().writeValueAsString(flowMetaData);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Exception has occurred creating flow meta data json!", e);
        }

        return result;
    }

    @Override
    public FlowMetaData deserialiseFlow(String metaData)
    {
        FlowMetaDataImpl result;

        try
        {
            //JSON file to Java object
            result = this.mapper.readValue(metaData, FlowMetaDataImpl.class);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Exception has occurred creating flow meta data object!", e);
        }

        return result;
    }

    /**
     * Recursive method to work it's way through a flow and it's components and the
     * associated routes.
     *
     * @param flowElement
     * @return
     */
    protected FlowElementMetaData describeFlowElement(FlowMetaData flowMetaData, FlowElement flowElement) throws Exception
    {

        if(flowElement.getFlowComponent() instanceof Consumer)
        {
            return manageSingleTransitionComponent(flowMetaData, flowElement, Consumer.class.getName());
        }
        else if(flowElement.getFlowComponent() instanceof Converter)
        {
            return manageSingleTransitionComponent(flowMetaData, flowElement, Converter.class.getName());
        }
        else if(flowElement.getFlowComponent() instanceof Translator)
        {
            return manageSingleTransitionComponent(flowMetaData, flowElement, Translator.class.getName());
        }
        else if(flowElement.getFlowComponent() instanceof Splitter)
        {
            return manageSingleTransitionComponent(flowMetaData, flowElement, Splitter.class.getName());
        }
        else if(flowElement.getFlowComponent() instanceof Filter)
        {
            return manageSingleTransitionComponent(flowMetaData, flowElement, Filter.class.getName());
        }
        else if(flowElement.getFlowComponent() instanceof Broker)
        {
            return manageSingleTransitionComponent(flowMetaData, flowElement, Broker.class.getName());
        }
        else if(flowElement.getFlowComponent() instanceof SingleRecipientRouter)
        {
            return manageMultiTransitionComponent(flowMetaData, flowElement, SingleRecipientRouter.class.getName());
        }
        else if(flowElement.getFlowComponent() instanceof MultiRecipientRouter)
        {
            return manageMultiTransitionComponent(flowMetaData, flowElement, MultiRecipientRouter.class.getName());
        }
        else if(flowElement.getFlowComponent() instanceof Producer)
        {
            FlowElementMetaData flowElementMetaData = createFlowElementMetaData(flowElement, Producer.class.getName());

            flowMetaData.getFlowElements().add(flowElementMetaData);
            return flowElementMetaData;
        }

        throw new UnsupportedOperationException("Unknown component type!");
    }

    /**
     * Deal with a component flow element whose component is single transition.
     *
     * @param flowMetaData the flow meta data.
     * @param flowElement the flow element.
     * @param componentType the component type we are referencing.
     *
     * @return the flow element meta data.
     */
    protected FlowElementMetaData manageSingleTransitionComponent(FlowMetaData flowMetaData, FlowElement flowElement, String componentType)
        throws Exception
    {
        FlowElementMetaData flowElementMetaData = createFlowElementMetaData(flowElement, componentType);

        FlowElementMetaData transitionMetaData = describeFlowElement(flowMetaData, flowElement.getTransition(FlowElement.DEFAULT_TRANSITION_NAME));

        Transition transition = new TransitionImpl();
        transition.setFrom(flowElementMetaData.getComponentName());
        transition.setTo(transitionMetaData.getComponentName());
        transition.setName(FlowElement.DEFAULT_TRANSITION_NAME);

        flowMetaData.getTransitions().add(transition);
        flowMetaData.getFlowElements().add(flowElementMetaData);

        return flowElementMetaData;
    }

    /**
     * Deal with a component flow element whose component is multi transition.
     *
     * @param flowMetaData the flow meta data.
     * @param flowElement the flow element.
     * @param componentType the component type we are referencing.
     *
     * @return the flow element meta data.
     */
    protected FlowElementMetaData manageMultiTransitionComponent(FlowMetaData flowMetaData, FlowElement flowElement, String componentType)
        throws Exception
    {
        FlowElementMetaData flowElementMetaData = createFlowElementMetaData(flowElement, componentType);

        Map<String, FlowElement> flowElementTransitions = flowElement.getTransitions();

        for(String route: flowElementTransitions.keySet())
        {
            FlowElementMetaData transitionMetaData = describeFlowElement(flowMetaData, flowElement.getTransition(route));

            Transition transition = new TransitionImpl();
            transition.setFrom(flowElementMetaData.getComponentName());
            transition.setTo(transitionMetaData.getComponentName());
            transition.setName(route);

            flowMetaData.getTransitions().add(transition);
        }

        flowMetaData.getFlowElements().add(flowElementMetaData);
        return flowElementMetaData;
    }

    /**
     * Create flow element meta data.
     *
     * @param flowElement the flow element
     * @param componentType the component type associated with the flow element.
     * @return
     */
    protected FlowElementMetaData createFlowElementMetaData(FlowElement flowElement, String componentType) throws Exception
    {
        FlowElementMetaData flowElementMetaData = new FlowElementMetaDataImpl();
        flowElementMetaData.setComponentName(flowElement.getComponentName());
        flowElementMetaData.setDescription(flowElement.getDescription());
        flowElementMetaData.setComponentType(componentType);

        if (AopUtils.isJdkDynamicProxy(flowElement.getFlowComponent()))
        {
            flowElementMetaData.setImplementingClass(AopProxyUtils.ultimateTargetClass(flowElement.getFlowComponent()).getName());
        }
        else
        {
            flowElementMetaData.setImplementingClass(flowElement.getFlowComponent().getClass().getName());
        }

        if(flowElement.getFlowComponent() instanceof ConfiguredResource)
        {
            flowElementMetaData.setConfigurable(true);
            flowElementMetaData.setConfigurationId(((ConfiguredResource)flowElement.getFlowComponent()).getConfiguredResourceId());
        }

        if(flowElement.getFlowElementInvoker() instanceof ConfiguredResource)
        {
            flowElementMetaData.setInvokerConfigurationId(((ConfiguredResource) flowElement.getFlowElementInvoker())
                .getConfiguredResourceId());
        }

        return flowElementMetaData;
    }
}
