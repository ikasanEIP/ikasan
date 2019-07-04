package org.ikasan.dashboard.ui.visualisation.adapter.service;

import org.ikasan.dashboard.ui.visualisation.model.flow.Module;
import org.ikasan.dashboard.ui.visualisation.model.flow.MultiTransition;
import org.ikasan.dashboard.ui.visualisation.model.flow.SingleTransition;
import org.ikasan.spec.component.endpoint.Broker;
import org.ikasan.spec.component.endpoint.Consumer;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModuleVisjsAdapter
{
    public Module adapt(ModuleMetaData moduleMetaData)
    {

        for(FlowMetaData flowMetaData: moduleMetaData.getFlows())
        {
        }

        return null;
    }

    protected void manageFlow(FlowMetaData flow)
    {
        Map<String, FlowElementMetaData> flowElements = new HashMap<>();

        for (FlowElementMetaData flowElement : flow.getFlowElements())
        {
            flowElements.put(flowElement.getComponentName(), flowElement);
        }

        for (FlowElementMetaData flowElement : flow.getFlowElements())
        {
            if (flowElement.getComponentType().equals(Consumer.class.getName()))
            {

            }
            else if (flowElement.getComponentType().equals(Converter.class.getName()))
            {

            }
            else if (flowElement.getComponentType().equals(Translator.class.getName()))
            {

            }
            else if (flowElement.getComponentType().equals(Splitter.class.getName()))
            {

            }
            else if (flowElement.getComponentType().equals(Filter.class.getName()))
            {

            }
            else if (flowElement.getComponentType().equals(Broker.class.getName()))
            {

            }
            else if (flowElement.getComponentType().equals(SingleRecipientRouter.class.getName()))
            {

            }
            else if (flowElement.getComponentType().equals(MultiRecipientRouter.class.getName()))
            {

            }
            else if (flowElement.getComponentType().equals(Producer.class.getName()))
            {

            }
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

    protected SingleTransition manageSingleTransition(FlowElementMetaData flowElement, List<Transition> transition,
                                                      Map<String, FlowElementMetaData> flowElements)
    {
        return null;
    }

    protected MultiTransition manageMultiTransition(FlowElementMetaData flowElement, List<Transition> transition,
                                                    Map<String, FlowElementMetaData> flowElements)
    {
        return null;
    }
}
