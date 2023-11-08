package org.ikasan.topology.metadata;

import com.fasterxml.jackson.annotation.JsonInclude;
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
import org.ikasan.spec.metadata.*;
import org.ikasan.spec.module.StartupControl;
import org.ikasan.spec.module.StartupType;
import org.ikasan.spec.trigger.Trigger;
import org.ikasan.spec.trigger.TriggerRelationship;
import org.ikasan.topology.metadata.model.DecoratorMetaDataImpl;
import org.ikasan.topology.metadata.model.FlowElementMetaDataImpl;
import org.ikasan.topology.metadata.model.FlowMetaDataImpl;
import org.ikasan.topology.metadata.model.TransitionImpl;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;

import java.util.ArrayList;
import java.util.List;
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
        m.addAbstractTypeMapping(DecoratorMetaData.class, DecoratorMetaDataImpl.class);

        this.mapper.registerModule(m);

        this.mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    @Override
    public String describeFlow(Flow flow, StartupControl startupControl)
    {
        String result;

        try
        {
            FlowConfiguration configuration = flow.getFlowConfiguration();

            FlowMetaDataImpl flowMetaData = new FlowMetaDataImpl();
            flowMetaData.setName(flow.getName());
            Map<String, List<Trigger>> triggers = flow.getTriggerService()
                                                      .getTriggers(flow.getModuleName(), flow.getName());

            flowMetaData
                .setConsumer(describeFlowElement(flowMetaData, configuration.getConsumerFlowElement(), triggers));

            if ( flow instanceof ConfiguredResource resource )
            {
                flowMetaData.setConfigurationId(resource.getConfiguredResourceId());
            }

            if(startupControl != null) {
                flowMetaData.setFlowStartupType(startupControl.getStartupType().name());
                flowMetaData.setFlowStartupComment(startupControl.getComment());
            } else {
                flowMetaData.setFlowStartupType(StartupType.MANUAL.name());
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
    protected FlowElementMetaData describeFlowElement(FlowMetaData flowMetaData, FlowElement flowElement,
                                                      Map<String, List<Trigger>> triggers) throws Exception
    {

        if ( flowElement.getFlowComponent() instanceof Consumer )
        {
            return manageSingleTransitionComponent(flowMetaData, flowElement, Consumer.class.getName(), triggers);
        }
        else if ( flowElement.getFlowComponent() instanceof Converter )
        {
            return manageSingleTransitionComponent(flowMetaData, flowElement, Converter.class.getName(), triggers);
        }
        else if ( flowElement.getFlowComponent() instanceof Translator )
        {
            return manageSingleTransitionComponent(flowMetaData, flowElement, Translator.class.getName(), triggers);
        }
        else if ( flowElement.getFlowComponent() instanceof Splitter )
        {
            return manageSingleTransitionComponent(flowMetaData, flowElement, Splitter.class.getName(), triggers);
        }
        else if ( flowElement.getFlowComponent() instanceof Filter )
        {
            return manageSingleTransitionComponent(flowMetaData, flowElement, Filter.class.getName(), triggers);
        }
        else if ( flowElement.getFlowComponent() instanceof Broker )
        {
            return manageSingleTransitionComponent(flowMetaData, flowElement, Broker.class.getName(), triggers);
        }
        else if ( flowElement.getFlowComponent() instanceof SingleRecipientRouter )
        {
            return manageMultiTransitionComponent(flowMetaData, flowElement, SingleRecipientRouter.class.getName(),
                triggers
                                                 );
        }
        else if ( flowElement.getFlowComponent() instanceof MultiRecipientRouter )
        {
            return manageMultiTransitionComponent(flowMetaData, flowElement, MultiRecipientRouter.class.getName(),
                triggers
                                                 );
        }
        else if ( flowElement.getFlowComponent() instanceof Producer )
        {
            FlowElementMetaData flowElementMetaData = createFlowElementMetaData(flowElement, Producer.class.getName(),
                triggers
                                                                               );

            flowMetaData.getFlowElements().add(flowElementMetaData);
            return flowElementMetaData;
        }

        throw new UnsupportedOperationException("Unknown component type!");
    }

    /**
     * Deal with a component flow element whose component is single transition.
     *
     * @param flowMetaData  the flow meta data.
     * @param flowElement   the flow element.
     * @param componentType the component type we are referencing.
     * @return the flow element meta data.
     */
    protected FlowElementMetaData manageSingleTransitionComponent(FlowMetaData flowMetaData, FlowElement flowElement,
                                                                  String componentType,
                                                                  Map<String, List<Trigger>> triggers) throws Exception
    {
        FlowElementMetaData flowElementMetaData = createFlowElementMetaData(flowElement, componentType, triggers);

        FlowElementMetaData transitionMetaData = describeFlowElement(flowMetaData,
            flowElement.getTransition(FlowElement.DEFAULT_TRANSITION_NAME), triggers
                                                                    );

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
     * @param flowMetaData  the flow meta data.
     * @param flowElement   the flow element.
     * @param componentType the component type we are referencing.
     * @return the flow element meta data.
     */
    protected FlowElementMetaData manageMultiTransitionComponent(FlowMetaData flowMetaData, FlowElement flowElement,
                                                                 String componentType,
                                                                 Map<String, List<Trigger>> triggers) throws Exception
    {
        FlowElementMetaData flowElementMetaData = createFlowElementMetaData(flowElement, componentType, triggers);

        Map<String, FlowElement> flowElementTransitions = flowElement.getTransitions();

        for (String route : flowElementTransitions.keySet())
        {
            FlowElementMetaData transitionMetaData = describeFlowElement(flowMetaData, flowElement.getTransition(route),
                triggers
                                                                        );
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
     * @param flowElement   the flow element
     * @param componentType the component type associated with the flow element.
     * @return
     */
    protected FlowElementMetaData createFlowElementMetaData(FlowElement flowElement, String componentType,
                                                            Map<String, List<Trigger>> triggers)
    {
        FlowElementMetaData flowElementMetaData = new FlowElementMetaDataImpl();
        flowElementMetaData.setComponentName(flowElement.getComponentName());
        flowElementMetaData.setDescription(flowElement.getDescription());
        flowElementMetaData.setComponentType(componentType);

        if ( AopUtils.isJdkDynamicProxy(flowElement.getFlowComponent()) )
        {
            flowElementMetaData
                .setImplementingClass(AopProxyUtils.ultimateTargetClass(flowElement.getFlowComponent()).getName());
        }
        else
        {
            flowElementMetaData.setImplementingClass(flowElement.getFlowComponent().getClass().getName());
        }

        if ( flowElement.getFlowComponent() instanceof ConfiguredResource )
        {
            flowElementMetaData.setConfigurable(true);
            flowElementMetaData
                .setConfigurationId(((ConfiguredResource) flowElement.getFlowComponent()).getConfiguredResourceId());
        }

        if ( flowElement.getFlowElementInvoker() instanceof ConfiguredResource )
        {
            flowElementMetaData.setInvokerConfigurationId(
                ((ConfiguredResource) flowElement.getFlowElementInvoker()).getConfiguredResourceId());
        }
        List<DecoratorMetaData> decoratorMetaDataList = new ArrayList<>();
        if(hasBeforeWiretap(triggers,flowElement.getComponentName()))
        {
            getBeforeWiretaps(triggers, flowElement.getComponentName()).stream().map(t -> getDecoratorMetaData(t)).forEach(d -> decoratorMetaDataList.add(d));
        }

        if(hasAfterWiretap(triggers,flowElement.getComponentName()))
        {
            getAfterWiretaps(triggers, flowElement.getComponentName()).stream().map(t -> getDecoratorMetaData(t)).forEach(d -> decoratorMetaDataList.add(d));
        }
        if(!decoratorMetaDataList.isEmpty())
        {
            flowElementMetaData.setDecorators(decoratorMetaDataList);
        }

        return flowElementMetaData;
    }

    private DecoratorMetaData getDecoratorMetaData(Trigger t)
    {
        DecoratorMetaData metaData = new DecoratorMetaDataImpl();
        metaData.setName(t.getRelationship().name() + " " + t.getFlowElementName());
        if ( t.getParams() != null && !t.getParams().isEmpty() )
        {
            metaData.setType("Wiretap");
            metaData.setConfigurable(true);
        }
        else
        {
            metaData.setType("LogWiretap");
        }
        metaData.setConfigurationId(t.getId() != null ? t.getId().toString() : null);

        return metaData;
    }

    protected boolean hasBeforeWiretap(Map<String, List<Trigger>> triggers, String componentName)
    {
        return triggers.entrySet().stream().anyMatch(e -> e.getKey().equals(TriggerRelationship.BEFORE.getDescription() + componentName));
    }

    protected List<Trigger> getBeforeWiretaps(Map<String, List<Trigger>> triggers, String componentName)
    {
        return triggers.entrySet().stream().filter(e -> e.getKey().equals(TriggerRelationship.BEFORE.getDescription() + componentName))
                       .map(e -> e.getValue()).findAny().get();
    }

    protected boolean hasAfterWiretap(Map<String, List<Trigger>> triggers, String componentName)
    {
        return triggers.entrySet().stream().anyMatch(e -> e.getKey().equals(TriggerRelationship.AFTER.getDescription() + componentName));
    }

    protected List<Trigger> getAfterWiretaps(Map<String, List<Trigger>> triggers, String componentName)
    {
        //
        return triggers.entrySet().stream().filter(e -> e.getKey().equals(TriggerRelationship.AFTER.getDescription() + componentName))
                       .map(e -> e.getValue()).findAny().get();
    }
}
