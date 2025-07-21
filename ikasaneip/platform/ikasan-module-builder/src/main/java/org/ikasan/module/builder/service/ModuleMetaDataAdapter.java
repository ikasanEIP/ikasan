package org.ikasan.module.builder.service;

import org.apache.commons.text.WordUtils;
import org.ikasan.module.builder.model.*;
import org.ikasan.spec.component.endpoint.Broker;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.component.filter.Filter;
import org.ikasan.spec.component.routing.MultiRecipientRouter;
import org.ikasan.spec.component.routing.SingleRecipientRouter;
import org.ikasan.spec.component.splitting.Splitter;
import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.component.transformation.Translator;
import org.ikasan.spec.metadata.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ModuleMetaDataAdapter {

    private HashMap<String, String> fromTransitionLabelMap = new HashMap<>();
    private HashMap<String, String> toTransitionLabelMap = new HashMap<>();

    /**
     * Adapts the provided ModuleManifestMetaData into a ModuleModel.
     *
     * @param moduleManifestMetaData The metadata of the module to adapt.
     * @return The adapted ModuleModel containing the name and flows from the input metadata.
     */
    public ModuleModel adapt(ModuleManifestMetaData moduleManifestMetaData, String moduleBasePackage)
    {
        Map<String, ConfigurationMetaData> configurationMetaDataMap = new HashMap<>();

        if(moduleManifestMetaData.getConfigurationMetaData() != null) {
            configurationMetaDataMap = moduleManifestMetaData.getConfigurationMetaData().stream().
                collect(Collectors.toMap(metaData -> metaData.getConfigurationId(), metaData -> metaData));
        }

        ModuleModel model = new ModuleModel(moduleManifestMetaData.getModuleMetaData().getName(), moduleBasePackage);
        for(FlowMetaData flowMetaData: moduleManifestMetaData.getModuleMetaData().getFlows())
        {
            model.addFlow(this.manageFlow(flowMetaData, configurationMetaDataMap, moduleBasePackage));
        }

        return model;
    }


    /**
     * Manages the flow based on the provided metadata and configuration.
     *
     * @param flowMetaData The metadata of the flow being managed.
     * @param configurationMetaDataMap Map containing configuration metadata.
     * @param moduleBasePackage The base package of the module.
     * @return The FlowModel representing the managed flow.
     */
    public FlowModel manageFlow(FlowMetaData flowMetaData, Map<String, ConfigurationMetaData> configurationMetaDataMap,
                                String moduleBasePackage) {
        Map<String, FlowElementMetaData> flowElements = flowMetaData.getFlowElements().stream().collect(
            Collectors.toMap(FlowElementMetaData::getComponentName, flowElementMetaData -> flowElementMetaData, (key1, key2) -> key1));

        List<Transition> uniqueTransitions = distinctList(flowMetaData.getTransitions(), Transition::getFrom, Transition::getTo);
        this.buildFromTransitionLabelMap(flowMetaData.getTransitions());
        this.buildToTransitionLabelMap(flowMetaData.getTransitions());

        ConsumerComponent consumer = (ConsumerComponent) manageFlowElement(flowMetaData.getConsumer(), uniqueTransitions
            , flowElements, configurationMetaDataMap);

        return new FlowModel(flowMetaData.getName(), moduleBasePackage, consumer);
    }

    /**
     * Manages a flow element based on the provided inputs.
     *
     * @param flowElement The metadata of the flow element being managed.
     * @param transitions The list of transitions available.
     * @param flowElements Map of all flow elements keyed by name.
     * @param configurationMetaDataMap Map of configuration metadata keyed by name.
     * @return The managed Component based on the flow element's component type.
     */
    protected Component manageFlowElement(FlowElementMetaData flowElement, List<Transition> transitions,
                                                    Map<String, FlowElementMetaData> flowElements, Map<String, ConfigurationMetaData> configurationMetaDataMap)
    {
        if (flowElement.getComponentType().equals(org.ikasan.spec.component.endpoint.Consumer.class.getName())
            || flowElement.getComponentType().equals(Converter.class.getName())
            || flowElement.getComponentType().equals(Translator.class.getName())
            || flowElement.getComponentType().equals(Splitter.class.getName())
            || flowElement.getComponentType().equals(Filter.class.getName())
            || flowElement.getComponentType().equals(Broker.class.getName())
            || flowElement.getComponentType().equals(Producer.class.getName()))
        {
            Component node =  manageSingleTransition(flowElement, transitions, flowElements, configurationMetaDataMap);
            return node;
        }
        else if (flowElement.getComponentType().equals(SingleRecipientRouter.class.getName())||
            flowElement.getComponentType().equals(MultiRecipientRouter.class.getName()))
        {
            Component node = manageMultiTransition(flowElement, transitions, flowElements, configurationMetaDataMap);
            return node;
        }
        else
        {
            throw new IllegalArgumentException("Unknown component type encountered");
        }
    }

    /**
     * Manages a single transition based on the provided inputs.
     *
     * @param flowElement The metadata of the flow element being managed.
     * @param transitions The list of transitions available.
     * @param flowElements Map of all flow elements keyed by name.
     * @param configurationMetaDataMap Map of configuration metadata keyed by name.
     * @return The managed Component based on the flow element's component type.
     */
    protected Component manageSingleTransition(FlowElementMetaData flowElement, List<Transition> transitions,
                                                         Map<String, FlowElementMetaData> flowElements, Map<String, ConfigurationMetaData> configurationMetaDataMap)
    {

        if (flowElement.getComponentType().equals(Producer.class.getName()))
        {
            return this.manageProducers(flowElement, configurationMetaDataMap);
        }

        // As the name of this method implies, this method only deals with components that have a single transition so get the first.
        FlowElementMetaData flowElementMetaData = this.getTransitions(flowElement, transitions, flowElements).get(0);

        if (flowElement.getComponentType().equals(org.ikasan.spec.component.endpoint.Consumer.class.getName()))
        {
            return this.manageConsumers(flowElement, flowElementMetaData, transitions, flowElements, configurationMetaDataMap);
        }
        else if (flowElement.getComponentType().equals(Converter.class.getName()))
        {
            return this.manageConverter(flowElement, flowElementMetaData, transitions, flowElements, configurationMetaDataMap);
        }
        else if (flowElement.getComponentType().equals(Translator.class.getName()))
        {
            return this.manageTranslator(flowElement, flowElementMetaData, transitions, flowElements, configurationMetaDataMap);
        }
        else if (flowElement.getComponentType().equals(Splitter.class.getName()))
        {
            return this.manageSplitter(flowElement, flowElementMetaData, transitions, flowElements, configurationMetaDataMap);
        }
        else if (flowElement.getComponentType().equals(Filter.class.getName()))
        {
            return this.manageFilter(flowElement, flowElementMetaData, transitions, flowElements, configurationMetaDataMap);
        }
        else if (flowElement.getComponentType().equals(Broker.class.getName()))
        {
            return this.manageBroker(flowElement, flowElementMetaData, transitions, flowElements, configurationMetaDataMap);
        }
        else
        {
            throw new IllegalArgumentException("Unknown component type encountered");
        }

    }

    /**
     * Manage multi transition flow elements.
     *
     * @param flowElement
     * @param transitions
     * @param flowElements
     * @return
     */
    protected Component manageMultiTransition(FlowElementMetaData flowElement, List<Transition> transitions,
                                                        Map<String, FlowElementMetaData> flowElements, Map<String, ConfigurationMetaData> configurationMetaDataMap)
    {
        List<FlowElementMetaData> flowElementMetaDataTransitions
            = this.getTransitions(flowElement, transitions, flowElements);

        if (flowElement.getComponentType().equals(SingleRecipientRouter.class.getName()))
        {
            return this.manageSingleRecipientRouter(flowElement, transitions, flowElements, configurationMetaDataMap, flowElementMetaDataTransitions);
        }
        else if (flowElement.getComponentType().equals(MultiRecipientRouter.class.getName()))
        {
            return this.manageMultiRecipientRouter(flowElement, transitions, flowElements, configurationMetaDataMap, flowElementMetaDataTransitions);
        }
        else
        {
            throw new IllegalArgumentException("Unknown component type encountered");
        }
    }

    /**
     * Helper method to manage producers.
     *
     * @param flowElement
     * @param configurationMetaDataMap
     * @return
     */
    private Component manageProducers(FlowElementMetaData flowElement, Map<String, ConfigurationMetaData> configurationMetaDataMap)
    {
        return new ProducerComponent(flowElement.getComponentName(), flowElement.getImplementingClass(), null);
    }

    /**
     * Helper method to manage consumers.
     *
     * @param flowElement
     * @param flowElementMetaData
     * @param transitions
     * @param flowElements
     * @param configurationMetaDataMap
     * @return
     */
    private Component manageConsumers(FlowElementMetaData flowElement, FlowElementMetaData flowElementMetaData, List<Transition> transitions,
                                                Map<String, FlowElementMetaData> flowElements, Map<String, ConfigurationMetaData> configurationMetaDataMap)
    {
        return new ConsumerComponent(flowElement.getComponentName(), flowElement.getImplementingClass()
            , manageFlowElement(flowElementMetaData, transitions, flowElements, configurationMetaDataMap));
    }

    /**
     * Helper method to manage converters.
     *
     * @param flowElement
     * @param flowElementMetaData
     * @param transitions
     * @param flowElements
     * @param configurationMetaDataMap
     * @return
     */
    private Component manageConverter(FlowElementMetaData flowElement, FlowElementMetaData flowElementMetaData, List<Transition> transitions, Map<String, FlowElementMetaData> flowElements, Map<String, ConfigurationMetaData> configurationMetaDataMap)
    {
        return new ConverterComponent(flowElement.getComponentName(), flowElement.getImplementingClass()
            , manageFlowElement(flowElementMetaData, transitions, flowElements, configurationMetaDataMap));
    }

    /**
     * Helper method to manage translators.
     *
     * @param flowElement
     * @param flowElementMetaData
     * @param transitions
     * @param flowElements
     * @param configurationMetaDataMap
     * @return
     */
    private Component manageTranslator(FlowElementMetaData flowElement, FlowElementMetaData flowElementMetaData, List<Transition> transitions,
                                                 Map<String, FlowElementMetaData> flowElements, Map<String, ConfigurationMetaData> configurationMetaDataMap)
    {
        return new TranslatorComponent(flowElement.getComponentName(), flowElement.getImplementingClass()
            , manageFlowElement(flowElementMetaData, transitions, flowElements, configurationMetaDataMap));
    }

    /**
     * Helper method to manage splitters.
     *
     * @param flowElement
     * @param flowElementMetaData
     * @param transitions
     * @param flowElements
     * @param configurationMetaDataMap
     * @return
     */
    private Component manageSplitter(FlowElementMetaData flowElement, FlowElementMetaData flowElementMetaData, List<Transition> transitions,
                                               Map<String, FlowElementMetaData> flowElements, Map<String, ConfigurationMetaData> configurationMetaDataMap)
    {
        return new SplitterComponent(flowElement.getComponentName(), flowElement.getImplementingClass()
            , manageFlowElement(flowElementMetaData, transitions, flowElements, configurationMetaDataMap));
    }

    /**
     * Helper method to manage filters.
     *
     * @param flowElement
     * @param flowElementMetaData
     * @param transitions
     * @param flowElements
     * @param configurationMetaDataMap
     * @return
     */
    private Component manageFilter(FlowElementMetaData flowElement, FlowElementMetaData flowElementMetaData, List<Transition> transitions,
                                             Map<String, FlowElementMetaData> flowElements, Map<String, ConfigurationMetaData> configurationMetaDataMap) {
        return new FilterComponent(flowElement.getComponentName(), flowElement.getImplementingClass()
            , manageFlowElement(flowElementMetaData, transitions, flowElements, configurationMetaDataMap));
    }

    /**
     * Helper method to manage brokers.
     *
     * @param flowElement
     * @param flowElementMetaData
     * @param transitions
     * @param flowElements
     * @param configurationMetaDataMap
     * @return
     */
    private Component manageBroker(FlowElementMetaData flowElement, FlowElementMetaData flowElementMetaData, List<Transition> transitions,
                                             Map<String, FlowElementMetaData> flowElements, Map<String, ConfigurationMetaData> configurationMetaDataMap) {
        return new BrokerComponent(flowElement.getComponentName(), flowElement.getImplementingClass()
            , manageFlowElement(flowElementMetaData, transitions, flowElements, configurationMetaDataMap));
    }

    /**
     * Manage single recipient routers.
     *
     * @param flowElement
     * @param transitions
     * @param flowElements
     * @param configurationMetaDataMap
     * @param flowElementMetaDataTransitions
     * @return
     */
    private Component manageSingleRecipientRouter(FlowElementMetaData flowElement, List<Transition> transitions,
                                                            Map<String, FlowElementMetaData> flowElements, Map<String, ConfigurationMetaData> configurationMetaDataMap,
                                                            List<FlowElementMetaData> flowElementMetaDataTransitions)
    {
        SingleRecipientRouterComponent router = new SingleRecipientRouterComponent(flowElement.getComponentName(), flowElement.getImplementingClass());

        flowElementMetaDataTransitions.stream().forEach(flowElementMetaData ->
            router.addTransition(Optional.ofNullable(this.toTransitionLabelMap.get(flowElementMetaData.getComponentName())).orElse(flowElementMetaData.getComponentName())
                , manageFlowElement(flowElementMetaData, transitions, flowElements, configurationMetaDataMap)));

        return router;
    }

    /**
     * Manage single recipient routers.
     *
     * @param flowElement
     * @param transitions
     * @param flowElements
     * @param configurationMetaDataMap
     * @param flowElementMetaDataTransitions
     * @return
     */
    private Component manageMultiRecipientRouter(FlowElementMetaData flowElement, List<Transition> transitions,
                                                 Map<String, FlowElementMetaData> flowElements, Map<String, ConfigurationMetaData> configurationMetaDataMap,
                                                 List<FlowElementMetaData> flowElementMetaDataTransitions)
    {
        MultiRecipientRouterComponent router = new MultiRecipientRouterComponent(flowElement.getComponentName(), flowElement.getImplementingClass());

        flowElementMetaDataTransitions.stream().forEach(flowElementMetaData ->
            router.addTransition(Optional.ofNullable(this.toTransitionLabelMap.get(flowElementMetaData.getComponentName())).orElse(flowElementMetaData.getComponentName())
                , manageFlowElement(flowElementMetaData, transitions, flowElements, configurationMetaDataMap)));

        return router;
    }

    /**
     * Build the from transition label map.
     *
     * @param transitions
     */
    protected void buildFromTransitionLabelMap(List<Transition> transitions)
    {
        for(Transition transition: transitions)
        {
            if (this.fromTransitionLabelMap.containsKey(transition.getFrom()))
            {
                String label = fromTransitionLabelMap.get(transition.getFrom());

                if(!label.contains(transition.getName()))
                {
                    label = label + ", " + transition.getName();
                    fromTransitionLabelMap.put(transition.getFrom(), label);
                }
            }
            else
            {
                String label = transition.getName();
                fromTransitionLabelMap.put(transition.getFrom(), label);
            }
        }
    }

    /**
     * Build the to transition label map.
     *
     * @param transitions
     */
    protected void buildToTransitionLabelMap(List<Transition> transitions)
    {
        for(Transition transition: transitions)
        {
            if (this.toTransitionLabelMap.containsKey(transition.getTo()))
            {
                String label = toTransitionLabelMap.get(transition.getTo());

                if(!label.contains(transition.getName()))
                {
                    label = label + ", " + transition.getName();
                    toTransitionLabelMap.put(transition.getTo(), WordUtils.wrap(label, 15));
                }
            }
            else
            {
                String label = transition.getName();
                toTransitionLabelMap.put(transition.getTo(), label);
            }
        }
    }

    /**
     * Get a list of transitions in the form of FlowElementMetaData.
     *
     * @param flowElement
     * @param transitions
     * @param flowElements
     * @return
     */
    protected List<FlowElementMetaData> getTransitions(FlowElementMetaData flowElement, List<Transition> transitions,
                                                       Map<String, FlowElementMetaData> flowElements)
    {
        return transitions.stream()
            .filter(transition -> transition.getFrom().equals(flowElement.getComponentName()))
            .map(transition -> flowElements.get(transition.getTo()))
            .collect(Collectors.toList());
    }

    /**
     * Help us narrow a distinct list
     *
     * @param list
     * @param keyExtractors
     * @param <T>
     * @return
     */
    private static <T> List<T> distinctList(List<T> list, Function<? super T, ?>... keyExtractors)
    {
        return list
            .stream()
            .filter(distinctByKeys(keyExtractors))
            .collect(Collectors.toList());
    }

    /**
     * Predicate to help narrow a distinct list.
     *
     * @param keyExtractors
     * @param <T>
     * @return
     */
    private static <T> Predicate<T> distinctByKeys(Function<? super T, ?>... keyExtractors)
    {
        final Map<List<?>, Boolean> seen = new ConcurrentHashMap<>();

        return t ->
        {

            final List<?> keys = Arrays.stream(keyExtractors)
                .map(ke -> ke.apply(t))
                .collect(Collectors.toList());

            return seen.putIfAbsent(keys, Boolean.TRUE) == null;
        };
    }
}
