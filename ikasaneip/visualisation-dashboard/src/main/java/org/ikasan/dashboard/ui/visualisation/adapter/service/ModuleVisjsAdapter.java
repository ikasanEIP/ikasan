package org.ikasan.dashboard.ui.visualisation.adapter.service;

import org.ikasan.dashboard.ui.visualisation.model.flow.*;
import org.ikasan.spec.component.endpoint.Broker;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.component.filter.Filter;
import org.ikasan.spec.component.routing.MultiRecipientRouter;
import org.ikasan.spec.component.routing.SingleRecipientRouter;
import org.ikasan.spec.component.splitting.Splitter;
import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.component.transformation.Translator;
import org.ikasan.spec.metadata.FlowElementMetaData;
import org.ikasan.spec.metadata.FlowMetaData;
import org.ikasan.spec.metadata.ModuleMetaData;
import org.ikasan.spec.metadata.Transition;
import org.ikasan.vaadin.visjs.network.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ModuleVisjsAdapter
{
    private int identifier;

    public Module adapt(ModuleMetaData moduleMetaData)
    {
        Module module = new Module(moduleMetaData.getName());

        identifier = 0;

        for(FlowMetaData flowMetaData: moduleMetaData.getFlows())
        {
            module.addFlow(this.manageFlow(flowMetaData));
        }

        return module;
    }

    protected Flow manageFlow(FlowMetaData flowMetaData)
    {
        System.out.println("Sorting flow: " + flowMetaData.getName());
        Map<String, FlowElementMetaData> flowElements = flowMetaData.getFlowElements().stream().collect(
            Collectors.toMap(FlowElementMetaData::getComponentName, flowElementMetaData -> flowElementMetaData, (key1, key2) -> key1));

        EventDrivenConsumer consumer = (EventDrivenConsumer) manageFlowElement(flowMetaData.getConsumer(), flowMetaData.getTransitions(), flowElements);

        return new Flow(flowMetaData.getName(), consumer);
    }

    protected Node manageFlowElement(FlowElementMetaData flowElement, List<Transition> transitions,
                                     Map<String, FlowElementMetaData> flowElements)
    {
        if (flowElement.getComponentType().equals(org.ikasan.spec.component.endpoint.Consumer.class.getName())
            || flowElement.getComponentType().equals(Converter.class.getName())
            || flowElement.getComponentType().equals(Translator.class.getName())
            || flowElement.getComponentType().equals(Splitter.class.getName())
            || flowElement.getComponentType().equals(Filter.class.getName())
            || flowElement.getComponentType().equals(Broker.class.getName())
            || flowElement.getComponentType().equals(Producer.class.getName()))
        {
            return manageSingleTransition(flowElement, transitions, flowElements);
        }
        else if (flowElement.getComponentType().equals(SingleRecipientRouter.class.getName())||
            flowElement.getComponentType().equals(MultiRecipientRouter.class.getName()))
        {
            return manageMultiTransition(flowElement, transitions, flowElements);
        }
        else
        {
            throw new IllegalArgumentException("Unknown component type encountered");
        }
    }

    protected List<FlowElementMetaData> getTransitions(FlowElementMetaData flowElement, List<Transition> transitions,
                                                       Map<String, FlowElementMetaData> flowElements)
    {
        List<FlowElementMetaData> transitionFlowElements = new ArrayList<>();

        for(Transition transition: transitions)
        {
            if(transition.getFrom().equals(flowElement.getComponentName()))
            {
                transitionFlowElements.add(flowElements.get(transition.getTo()));
            }
        }

        return transitionFlowElements;
    }

    protected Node manageSingleTransition(FlowElementMetaData flowElement, List<Transition> transitions,
                                                      Map<String, FlowElementMetaData> flowElements)
    {
        if (flowElement.getComponentType().equals(Producer.class.getName()))
        {
            if(flowElement.getImplementingClass().equals("org.ikasan.component.endpoint.util.producer.DevNull"))
            {
                return new DeadEndPoint(flowElement.getComponentName() + identifier++, flowElement.getComponentName(), null);
            }

            return new MessageProducer(flowElement.getComponentName() + identifier++, flowElement.getComponentName()
                , new MessageChannel("channel"+identifier++, "esb.com.some.channel", false));
        }

        FlowElementMetaData flowElementMetaData = this.getTransitions(flowElement, transitions, flowElements).get(0);
        if (flowElement.getComponentType().equals(org.ikasan.spec.component.endpoint.Consumer.class.getName()))
        {
            return new EventDrivenConsumer(flowElement.getComponentName() + identifier++,
                flowElement.getComponentName(), manageFlowElement(flowElementMetaData, transitions, flowElements));
        }
        else if (flowElement.getComponentType().equals(Converter.class.getName()))
        {
            return new MessageTranslator(flowElement.getComponentName() + identifier++,
                flowElement.getComponentName(), manageFlowElement(flowElementMetaData, transitions, flowElements));
        }
        else if (flowElement.getComponentType().equals(Translator.class.getName()))
        {
            return new MessageTranslator(flowElement.getComponentName() + identifier++,
                flowElement.getComponentName(), manageFlowElement(flowElementMetaData, transitions, flowElements));
        }
        else if (flowElement.getComponentType().equals(Splitter.class.getName()))
        {
            return new org.ikasan.dashboard.ui.visualisation.model.flow.Splitter(flowElement.getComponentName() + identifier++,
                flowElement.getComponentName(), manageFlowElement(flowElementMetaData, transitions, flowElements));
        }
        else if (flowElement.getComponentType().equals(Filter.class.getName()))
        {
            return new org.ikasan.dashboard.ui.visualisation.model.flow.Filter(flowElement.getComponentName() + identifier++,
                flowElement.getComponentName(), manageFlowElement(flowElementMetaData, transitions, flowElements));
        }
        else if (flowElement.getComponentType().equals(Broker.class.getName()))
        {
            return new org.ikasan.dashboard.ui.visualisation.model.flow.Broker(flowElement.getComponentName() + identifier++,
                flowElement.getComponentName(), manageFlowElement(flowElementMetaData, transitions, flowElements));
        }
        else
        {
            throw new IllegalArgumentException("Unknown component type encountered");
        }

    }

    protected Node manageMultiTransition(FlowElementMetaData flowElement, List<Transition> transitions,
                                                    Map<String, FlowElementMetaData> flowElements)
    {
        List<FlowElementMetaData> flowElementMetaDataTransitions
            = this.getTransitions(flowElement, transitions, flowElements);

        if (flowElement.getComponentType().equals(SingleRecipientRouter.class.getName()))
        {
            org.ikasan.dashboard.ui.visualisation.model.flow.SingleRecipientRouter router
                = new org.ikasan.dashboard.ui.visualisation.model.flow.SingleRecipientRouter(flowElement.getComponentName() + identifier++,
                    flowElement.getComponentName());

            flowElementMetaDataTransitions.stream().forEach(flowElementMetaData ->
                router.addTransition(flowElementMetaData.getComponentName(), manageFlowElement(flowElementMetaData, transitions, flowElements)));

            return router;
        }
        else if (flowElement.getComponentType().equals(MultiRecipientRouter.class.getName()))
        {
            org.ikasan.dashboard.ui.visualisation.model.flow.RecipientListRouter router
                = new org.ikasan.dashboard.ui.visualisation.model.flow.RecipientListRouter(flowElement.getComponentName() + identifier++,
                flowElement.getComponentName());

            flowElementMetaDataTransitions.stream().forEach(flowElementMetaData ->
                router.addTransition(manageFlowElement(flowElementMetaData, transitions, flowElements)));

            return router;
        }
        else
        {
            throw new IllegalArgumentException("Unknown component type encountered");
        }
    }
}
