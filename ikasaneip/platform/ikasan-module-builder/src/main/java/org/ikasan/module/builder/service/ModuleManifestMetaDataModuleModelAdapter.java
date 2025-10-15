package org.ikasan.module.builder.service;

import org.ikasan.module.builder.model.module.*;
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

public class ModuleManifestMetaDataModuleModelAdapter {
    private HashMap<String, String> toTransitionLabelMap = new HashMap<>();

    /**
     * Adapts the given ModuleManifestMetaData and moduleBasePackage to create a ModuleModel object.
     * This method maps configuration metadata to a map based on the configuration ID, then iterates over the flows
     * to add them to the ModuleModel using the manageFlow method.
     *
     * @param moduleManifestMetaData The metadata of the module to adapt.
     * @param moduleBasePackage The base package of the module.
     * @return A ModuleModel object representing the adapted module.
     */
    public ModuleModel adapt(ModuleManifestMetaData moduleManifestMetaData, String moduleBasePackage)
    {
        Map<String, ConfigurationMetaData> configurationMetaDataMap = new HashMap<>();

        if(moduleManifestMetaData.getConfigurationMetaData() != null) {
            configurationMetaDataMap = moduleManifestMetaData.getConfigurationMetaData().stream().
                collect(Collectors.toMap(metaData -> metaData.getConfigurationId(), metaData -> metaData));
        }

        ModuleManifestMetaDataImportedResourcesAdapter importedResourcesAdapter = new ModuleManifestMetaDataImportedResourcesAdapter();
        List<ImportedResourceMetaData> importedConfigurationResources = importedResourcesAdapter.adapt(moduleManifestMetaData, moduleBasePackage,
            ImportedResourceMetaData.IMPORTED_CONFIGURATION_CLASS);
        List<ImportedResourceMetaData> importedXmlResources = importedResourcesAdapter.adapt(moduleManifestMetaData, moduleBasePackage,
            ImportedResourceMetaData.IMPORTED_XML_RESOURCE);

        ModuleModel model = new ModuleModel(moduleManifestMetaData.getModuleMetaData().getName(), moduleBasePackage,
            importedConfigurationResources, importedXmlResources);
        for(FlowMetaData flowMetaData: moduleManifestMetaData.getModuleMetaData().getFlows())
        {
            model.addFlow(this.manageFlow(flowMetaData, configurationMetaDataMap, moduleBasePackage));
        }

        return model;
    }


    /**
     * Manages the flow based on the provided flow metadata, configuration metadata map, and module base package.
     *
     * @param flowMetaData The metadata describing the flow elements and transitions.
     * @param configurationMetaDataMap A map of configuration metadata keyed by configuration name.
     * @param moduleBasePackage The base package of the module.
     * @return The constructed FlowModel representing the managed flow.
     */
    public FlowModel manageFlow(FlowMetaData flowMetaData, Map<String, ConfigurationMetaData> configurationMetaDataMap,
                                String moduleBasePackage) {
        Map<String, FlowElementMetaData> flowElements = flowMetaData.getFlowElements().stream().collect(
            Collectors.toMap(FlowElementMetaData::getComponentName, flowElementMetaData -> flowElementMetaData, (key1, key2) -> key1));

        List<Transition> uniqueTransitions = distinctList(flowMetaData.getTransitions(), Transition::getFrom, Transition::getTo);
        this.buildToTransitionLabelMap(flowMetaData.getTransitions());

        ConsumerComponent consumer = (ConsumerComponent) manageFlowElement(flowMetaData.getConsumer(), uniqueTransitions
            , flowElements, configurationMetaDataMap);

        return new FlowModel(flowMetaData.getName(), moduleBasePackage, consumer);
    }

    /**
     * Manages the flow element based on its component type.
     *
     * @param flowElement the metadata of the flow element to manage
     * @param transitions the list of transitions associated with the flow element
     * @param flowElements a map of all flow elements in the flow
     * @param configurationMetaDataMap a map of configuration metadata
     * @return the Component object representing the managed flow element
     * @throws IllegalArgumentException if an unknown component type is encountered
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
            || flowElement.getComponentType().equals(Producer.class.getName())) {
            Component node =  manageSingleTransition(flowElement, transitions, flowElements, configurationMetaDataMap);
            return node;
        }
        else if (flowElement.getComponentType().equals(SingleRecipientRouter.class.getName())||
            flowElement.getComponentType().equals(MultiRecipientRouter.class.getName())) {
            Component node = manageMultiTransition(flowElement, transitions, flowElements, configurationMetaDataMap);
            return node;
        }
        else {
            throw new IllegalArgumentException("Unknown component type encountered");
        }
    }

    /**
     * Manages a single transition for a given flow element.
     *
     * @param flowElement The metadata of the flow element to process.
     * @param transitions The list of available transitions for the flow element.
     * @param flowElements A map containing all flow elements in the current context.
     * @param configurationMetaDataMap A map containing configuration metadata for components.
     * @return The managed Component based on the specific type of flow element.
     */
    protected Component manageSingleTransition(FlowElementMetaData flowElement, List<Transition> transitions,
                                                         Map<String, FlowElementMetaData> flowElements, Map<String, ConfigurationMetaData> configurationMetaDataMap)
    {

        if (flowElement.getComponentType().equals(Producer.class.getName())) {
            return this.manageProducers(flowElement, configurationMetaDataMap);
        }

        // As the name of this method implies, this method only deals with components that have a single transition so get the first.
        FlowElementMetaData flowElementMetaData = this.getTransitions(flowElement, transitions, flowElements).get(0);

        if (flowElement.getComponentType().equals(org.ikasan.spec.component.endpoint.Consumer.class.getName())) {
            return this.manageConsumers(flowElement, flowElementMetaData, transitions, flowElements, configurationMetaDataMap);
        }
        else if (flowElement.getComponentType().equals(Converter.class.getName())) {
            return this.manageConverter(flowElement, flowElementMetaData, transitions, flowElements, configurationMetaDataMap);
        }
        else if (flowElement.getComponentType().equals(Translator.class.getName())) {
            return this.manageTranslator(flowElement, flowElementMetaData, transitions, flowElements, configurationMetaDataMap);
        }
        else if (flowElement.getComponentType().equals(Splitter.class.getName())) {
            return this.manageSplitter(flowElement, flowElementMetaData, transitions, flowElements, configurationMetaDataMap);
        }
        else if (flowElement.getComponentType().equals(Filter.class.getName())) {
            return this.manageFilter(flowElement, flowElementMetaData, transitions, flowElements, configurationMetaDataMap);
        }
        else if (flowElement.getComponentType().equals(Broker.class.getName())) {
            return this.manageBroker(flowElement, flowElementMetaData, transitions, flowElements, configurationMetaDataMap);
        }
        else {
            throw new IllegalArgumentException("Unknown component type encountered");
        }

    }


    /**
     * Manages the transition of a FlowElement based on its type and configuration.
     *
     * @param flowElement The FlowElement to manage the transition for.
     * @param transitions List of transitions available in the flow.
     * @param flowElements Mapping of FlowElement names to FlowElementMetaData objects.
     * @param configurationMetaDataMap Mapping of ComponentConfiguration ID to ConfigurationMetaData objects.
     * @return The Component representing the transition managed based on the FlowElement's type.
     */
    protected Component manageMultiTransition(FlowElementMetaData flowElement, List<Transition> transitions,
                                                        Map<String, FlowElementMetaData> flowElements, Map<String, ConfigurationMetaData> configurationMetaDataMap) {
        List<FlowElementMetaData> flowElementMetaDataTransitions
            = this.getTransitions(flowElement, transitions, flowElements);

        if (flowElement.getComponentType().equals(SingleRecipientRouter.class.getName())) {
            return this.manageSingleRecipientRouter(flowElement, transitions, flowElements, configurationMetaDataMap, flowElementMetaDataTransitions);
        }
        else if (flowElement.getComponentType().equals(MultiRecipientRouter.class.getName())) {
            return this.manageMultiRecipientRouter(flowElement, transitions, flowElements, configurationMetaDataMap, flowElementMetaDataTransitions);
        }
        else {
            throw new IllegalArgumentException("Unknown component type encountered");
        }
    }


    /**
     * Manages the producers for a given flow element.
     *
     * @param flowElement The metadata of the flow element for which producers are being managed.
     * @param configurationMetaDataMap The map containing configuration metadata for the flow.
     * @return A new ProducerComponent object representing the managed producer.
     */
    private Component manageProducers(FlowElementMetaData flowElement, Map<String, ConfigurationMetaData> configurationMetaDataMap) {
        return new ProducerComponent(flowElement.getComponentName(), flowElement.getImplementingClass(), null);
    }

    /**
     * Manages consumers for a given flow element.
     *
     * @param flowElement The main flow element to create a consumer for.
     * @param flowElementMetaData The metadata of the flow element.
     * @param transitions List of transitions associated with the flow element.
     * @param flowElements Map of all flow elements in the flow.
     * @param configurationMetaDataMap Map of all configuration metadata.
     * @return A new ConsumerComponent for the given flow element.
     */
    private Component manageConsumers(FlowElementMetaData flowElement, FlowElementMetaData flowElementMetaData, List<Transition> transitions,
                                                Map<String, FlowElementMetaData> flowElements, Map<String, ConfigurationMetaData> configurationMetaDataMap) {
        return new ConsumerComponent(flowElement.getComponentName(), flowElement.getImplementingClass()
            , manageFlowElement(flowElementMetaData, transitions, flowElements, configurationMetaDataMap));
    }

    /**
     * Creates a ConverterComponent based on the provided flow element metadata and transitions.
     *
     * @param flowElement the main flow element metadata
     * @param flowElementMetaData the flow element metadata to manage
     * @param transitions the list of transitions for the flow element
     * @param flowElements map of flow element metadata by name
     * @param configurationMetaDataMap map of configuration metadata by name
     * @return a new ConverterComponent based on the input parameters
     */
    private Component manageConverter(FlowElementMetaData flowElement, FlowElementMetaData flowElementMetaData, List<Transition> transitions
        , Map<String, FlowElementMetaData> flowElements, Map<String, ConfigurationMetaData> configurationMetaDataMap) {
        return new ConverterComponent(flowElement.getComponentName(), flowElement.getImplementingClass()
            , manageFlowElement(flowElementMetaData, transitions, flowElements, configurationMetaDataMap));
    }

    /**
     * Creates a TranslatorComponent based on the provided FlowElement metadata, transitions, and other necessary information.
     *
     * @param flowElement the main FlowElement metadata
     * @param flowElementMetaData additional FlowElement metadata
     * @param transitions list of transitions
     * @param flowElements map of flow elements
     * @param configurationMetaDataMap map of configuration metadata
     * @return a new TranslatorComponent instance
     */
    private Component manageTranslator(FlowElementMetaData flowElement, FlowElementMetaData flowElementMetaData, List<Transition> transitions,
                                                 Map<String, FlowElementMetaData> flowElements, Map<String, ConfigurationMetaData> configurationMetaDataMap) {
        return new TranslatorComponent(flowElement.getComponentName(), flowElement.getImplementingClass()
            , manageFlowElement(flowElementMetaData, transitions, flowElements, configurationMetaDataMap));
    }

    /**
     * Manages the creation of a SplitterComponent based on the provided metadata.
     *
     * @param flowElement The metadata of the main flow element.
     * @param flowElementMetaData The metadata of the flow element.
     * @param transitions List of transitions associated with the flow element.
     * @param flowElements Map of flow elements in the flow.
     * @param configurationMetaDataMap Map of configuration metadata.
     * @return A new SplitterComponent instance based on the provided metadata.
     */
    private Component manageSplitter(FlowElementMetaData flowElement, FlowElementMetaData flowElementMetaData, List<Transition> transitions,
                                               Map<String, FlowElementMetaData> flowElements, Map<String, ConfigurationMetaData> configurationMetaDataMap) {
        return new SplitterComponent(flowElement.getComponentName(), flowElement.getImplementingClass()
            , manageFlowElement(flowElementMetaData, transitions, flowElements, configurationMetaDataMap));
    }

    /**
     * Manages a filter component based on the provided inputs.
     *
     * @param flowElement the primary flow element
     * @param flowElementMetaData the metadata of the flow element
     * @param transitions the list of transitions for the flow element
     * @param flowElements the map of flow elements
     * @param configurationMetaDataMap the map of configuration metadata
     * @return a new FilterComponent instance with the necessary details
     */
    private Component manageFilter(FlowElementMetaData flowElement, FlowElementMetaData flowElementMetaData, List<Transition> transitions,
                                             Map<String, FlowElementMetaData> flowElements, Map<String, ConfigurationMetaData> configurationMetaDataMap) {
        return new FilterComponent(flowElement.getComponentName(), flowElement.getImplementingClass()
            , manageFlowElement(flowElementMetaData, transitions, flowElements, configurationMetaDataMap));
    }

    /**
     * Manages a BrokerComponent based on the provided input parameters.
     *
     * @param flowElement the flow element to create the BrokerComponent from
     * @param flowElementMetaData the metadata of the flow element
     * @param transitions the list of transitions for the flow element
     * @param flowElements map of flow elements by their names
     * @param configurationMetaDataMap map of configuration metadata by configuration ID
     * @return a new BrokerComponent based on the input parameters
     */
    private Component manageBroker(FlowElementMetaData flowElement, FlowElementMetaData flowElementMetaData, List<Transition> transitions,
                                             Map<String, FlowElementMetaData> flowElements, Map<String, ConfigurationMetaData> configurationMetaDataMap) {
        return new BrokerComponent(flowElement.getComponentName(), flowElement.getImplementingClass()
            , manageFlowElement(flowElementMetaData, transitions, flowElements, configurationMetaDataMap));
    }

    /**
     * Manages a Single Recipient Router component based on the provided flow element and associated data.
     *
     * @param flowElement the metadata of the flow element
     * @param transitions list of transitions
     * @param flowElements map of flow element metadata
     * @param configurationMetaDataMap map of configuration metadata
     * @param flowElementMetaDataTransitions list of flow element metadata transitions
     * @return a Component representing the Single Recipient Router component
     */
    private Component manageSingleRecipientRouter(FlowElementMetaData flowElement, List<Transition> transitions,
                                                            Map<String, FlowElementMetaData> flowElements, Map<String, ConfigurationMetaData> configurationMetaDataMap,
                                                            List<FlowElementMetaData> flowElementMetaDataTransitions) {
        SingleRecipientRouterComponent router = new SingleRecipientRouterComponent(flowElement.getComponentName(), flowElement.getImplementingClass());

        flowElementMetaDataTransitions.stream().forEach(flowElementMetaData ->
            router.addTransition(Optional.ofNullable(this.toTransitionLabelMap.get(flowElementMetaData.getComponentName())).orElse(flowElementMetaData.getComponentName())
                , manageFlowElement(flowElementMetaData, transitions, flowElements, configurationMetaDataMap)));

        return router;
    }

    /**
     * Creates a MultiRecipientRouterComponent based on the provided information.
     *
     * @param flowElement The metadata of the flow element.
     * @param transitions List of transitions in the flow.
     * @param flowElements Map of all flow elements in the flow.
     * @param configurationMetaDataMap Map of configuration metadata.
     * @param flowElementMetaDataTransitions List of flow elements metadata transitions.
     * @return A MultiRecipientRouterComponent instance representing a multi-recipient router component in the flow.
     */
    private Component manageMultiRecipientRouter(FlowElementMetaData flowElement, List<Transition> transitions,
                                                 Map<String, FlowElementMetaData> flowElements, Map<String, ConfigurationMetaData> configurationMetaDataMap,
                                                 List<FlowElementMetaData> flowElementMetaDataTransitions) {
        MultiRecipientRouterComponent router = new MultiRecipientRouterComponent(flowElement.getComponentName(), flowElement.getImplementingClass());

        flowElementMetaDataTransitions.stream().forEach(flowElementMetaData ->
            router.addTransition(Optional.ofNullable(this.toTransitionLabelMap.get(flowElementMetaData.getComponentName())).orElse(flowElementMetaData.getComponentName())
                , manageFlowElement(flowElementMetaData, transitions, flowElements, configurationMetaDataMap)));

        return router;
    }

    /**
     * Builds a map of transition labels based on the provided list of transitions. Each transition is checked against
     * the existing map to determine if the 'to' component already has a label. If so, the transition name is appended to
     * the existing label. If not, a new label is created using the transition name. The labels are word-wrapped at 15
     * characters for display.
     *
     * @param transitions the list of transitions to build the label map from
     */
    protected void buildToTransitionLabelMap(List<Transition> transitions) {
        for(Transition transition: transitions)
        {
            if (this.toTransitionLabelMap.containsKey(transition.getTo()))
            {
                String label = toTransitionLabelMap.get(transition.getTo());

                if(!label.contains(transition.getName()))
                {
                    label = label + "," + transition.getName();
                    toTransitionLabelMap.put(transition.getTo(), label);
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
     * Retrieves a list of FlowElementMetaData representing transitions for a given FlowElementMetaData.
     *
     * @param flowElement the FlowElementMetaData for which transitions are to be retrieved
     * @param transitions the list of Transition objects to filter from
     * @param flowElements a map of component names to FlowElementMetaData objects for lookup
     * @return a List of FlowElementMetaData representing transitions for the input FlowElementMetaData
     */
    protected List<FlowElementMetaData> getTransitions(FlowElementMetaData flowElement, List<Transition> transitions,
                                                       Map<String, FlowElementMetaData> flowElements) {
        return transitions.stream()
            .filter(transition -> transition.getFrom().equals(flowElement.getComponentName()))
            .map(transition -> flowElements.get(transition.getTo()))
            .collect(Collectors.toList());
    }

    /**
     * Returns a new list containing only distinct elements based on the provided key extractors.
     *
     * @param list The list of elements to filter.
     * @param keyExtractors The functions used to extract keys for distinct filtering.
     * @param <T> The type of elements in the list.
     * @return A new list containing distinct elements.
     */
    private static <T> List<T> distinctList(List<T> list, Function<? super T, ?>... keyExtractors) {
        return list
            .stream()
            .filter(distinctByKeys(keyExtractors))
            .collect(Collectors.toList());
    }

    /**
     * Returns a predicate that filters elements based on unique key values extracted by the provided key extractors.
     *
     * @param <T> the type of elements to filter
     * @param keyExtractors the key extractors to extract unique keys from elements
     * @return the predicate to filter elements based on unique keys
     */
    private static <T> Predicate<T> distinctByKeys(Function<? super T, ?>... keyExtractors) {
        final Map<List<?>, Boolean> seen = new ConcurrentHashMap<>();

        return t -> {

            final List<?> keys = Arrays.stream(keyExtractors)
                .map(ke -> ke.apply(t))
                .collect(Collectors.toList());

            return seen.putIfAbsent(keys, Boolean.TRUE) == null;
        };
    }
}
