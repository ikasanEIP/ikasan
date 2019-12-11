package org.ikasan.dashboard.ui.visualisation.adapter.service;

import org.apache.commons.text.WordUtils;
import org.ikasan.dashboard.ui.visualisation.model.flow.*;
import org.ikasan.spec.component.endpoint.Broker;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.component.filter.Filter;
import org.ikasan.spec.component.routing.MultiRecipientRouter;
import org.ikasan.spec.component.routing.SingleRecipientRouter;
import org.ikasan.spec.component.splitting.Splitter;
import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.component.transformation.Translator;
import org.ikasan.spec.flow.FlowElement;
import org.ikasan.spec.metadata.*;
import org.ikasan.topology.model.Component;
import org.ikasan.vaadin.visjs.network.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ModuleVisjsAdapter
{
    Logger logger = LoggerFactory.getLogger(ModuleVisjsAdapter.class);

    private int identifier;
    private HashMap<String, String> fromTransitionLabelMap = new HashMap<>();
    private HashMap<String, String> toTransitionLabelMap = new HashMap<>();
    private HashMap<String, ConfigurationMetaData> configurationMetaDataHashMap;
    private HashMap<String, FlowElementMetaData> componentMap;

    /**
     * Adapt module meta data into a Module structure suitable for rendering to a VisJs visualisation.
     *
     * @param moduleMetaData
     * @param configurationMetaData
     * @return
     */
    public Module adapt(ModuleMetaData moduleMetaData, List<ConfigurationMetaData> configurationMetaData)
    {
        this.configurationMetaDataHashMap = new HashMap<>();
        this.componentMap = new HashMap<>();
        Module module = new Module(moduleMetaData.getUrl(), moduleMetaData.getName(), moduleMetaData.getDescription()
            , moduleMetaData.getVersion(), configurationMetaDataHashMap, componentMap);

        Map<String, ConfigurationMetaData> configurationMetaDataMap = configurationMetaData.stream().
            collect(Collectors.toMap(metaData -> metaData.getConfigurationId(), metaData -> metaData));

        identifier = 0;

        for(FlowMetaData flowMetaData: moduleMetaData.getFlows())
        {
            module.addFlow(this.manageFlow(flowMetaData, configurationMetaDataMap));
        }

        return module;
    }

    /**
     * Helper method to manage flows.
     *
     * @param flowMetaData
     * @return
     */
    protected Flow manageFlow(FlowMetaData flowMetaData, Map<String, ConfigurationMetaData> configurationMetaDataMap)
    {
        Map<String, FlowElementMetaData> flowElements = flowMetaData.getFlowElements().stream().collect(
            Collectors.toMap(FlowElementMetaData::getComponentName, flowElementMetaData -> flowElementMetaData, (key1, key2) -> key1));

        this.buildFromTransitionLabelMap(flowMetaData.getTransitions());
        this.buildToTransitionLabelMap(flowMetaData.getTransitions());

        List<Transition> uniqueTransitions = distinctList(flowMetaData.getTransitions(), Transition::getFrom, Transition::getTo);

        Consumer consumer = (Consumer) manageFlowElement(flowMetaData.getConsumer(), uniqueTransitions, flowElements, configurationMetaDataMap);

        return new Flow(flowMetaData.getName(), flowMetaData.getConfigurationId(), consumer);
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

    /**
     * Helper method to manage flow elements
     *
     * @param flowElement
     * @param transitions
     * @param flowElements
     * @return
     */
    protected Node manageFlowElement(FlowElementMetaData flowElement, List<Transition> transitions,
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
            return manageSingleTransition(flowElement, transitions, flowElements, configurationMetaDataMap);
        }
        else if (flowElement.getComponentType().equals(SingleRecipientRouter.class.getName())||
            flowElement.getComponentType().equals(MultiRecipientRouter.class.getName()))
        {
            return manageMultiTransition(flowElement, transitions, flowElements, configurationMetaDataMap);
        }
        else
        {
            throw new IllegalArgumentException("Unknown component type encountered");
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
     * Manage single transition flow elements.
     *
     * @param flowElement
     * @param transitions
     * @param flowElements
     * @return
     */
    protected Node manageSingleTransition(FlowElementMetaData flowElement, List<Transition> transitions,
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
    protected Node manageMultiTransition(FlowElementMetaData flowElement, List<Transition> transitions,
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
    private Node manageProducers(FlowElementMetaData flowElement, Map<String, ConfigurationMetaData> configurationMetaDataMap)
    {
        String nodeId = flowElement.getComponentName() + identifier++;
        this.manageModuleMaps(nodeId, configurationMetaDataMap, flowElement);

        if(flowElement.getImplementingClass().equals("org.ikasan.component.endpoint.util.producer.DevNull"))
        {
            return DeadEndPoint.deadEndPointBuilder()
                .withId(nodeId)
                .withName(WordUtils.wrap(flowElement.getComponentName(), 25))
                .withTransitionLabel(this.fromTransitionLabelMap.get(flowElement.getComponentName()))
                .build();
        }
        else if(flowElement.getImplementingClass().equals("org.ikasan.component.endpoint.jms.spring.producer.ArjunaJmsTemplateProducer"))
        {
            ConfigurationMetaData configurationMetaData = configurationMetaDataMap.get(flowElement.getConfigurationId());
            String destinationName = this.getConfigurationParameterMetaData("destinationJndiName", configurationMetaData);

            return MessageProducer.messageProducerBuilder()
                .withId(nodeId)
                .withName(WordUtils.wrap(flowElement.getComponentName(), 25))
                .withTransitionLabel(this.fromTransitionLabelMap.get(flowElement.getComponentName()))
                .withTransition(new MessageChannel("channel"+identifier++, WordUtils.wrap(destinationName, 25, "\n", true, "\\."), false))
                .build();
        }
        else if(flowElement.getImplementingClass().equals("org.ikasan.endpoint.ftp.producer.FtpProducer"))
        {
            ConfigurationMetaData configurationMetaData = configurationMetaDataMap.get(flowElement.getConfigurationId());
            String remoteHost = this.getConfigurationParameterMetaData("remoteHost", configurationMetaData);

            return MessageEndPoint.messageEndPointBuilder()
                .withId(nodeId)
                .withName(WordUtils.wrap(flowElement.getComponentName(), 25))
                .withTransitionLabel(this.fromTransitionLabelMap.get(flowElement.getComponentName()))
                .withTransition(new FtpLocation("channel"+identifier++, remoteHost))
                .build();
        }
        else if(flowElement.getImplementingClass().equals("org.ikasan.endpoint.sftp.producer.SftpProducer"))
        {
            ConfigurationMetaData configurationMetaData = configurationMetaDataMap.get(flowElement.getConfigurationId());
            String remoteHost = this.getConfigurationParameterMetaData("remoteHost", configurationMetaData);

            return MessageEndPoint.messageEndPointBuilder()
                .withId(nodeId)
                .withName(WordUtils.wrap(flowElement.getComponentName(), 25))
                .withTransitionLabel(this.fromTransitionLabelMap.get(flowElement.getComponentName()))
                .withTransition(new SftpLocation("channel"+identifier++, remoteHost))
                .build();
        }

        return MessageEndPoint.messageEndPointBuilder()
            .withId(nodeId)
            .withName(WordUtils.wrap(flowElement.getComponentName(), 25))
            .withTransitionLabel(this.fromTransitionLabelMap.get(flowElement.getComponentName()))
            .withTransition(new FileLocation("fileLocation"+ identifier++, ""))
            .build();
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
    private Node manageConsumers(FlowElementMetaData flowElement, FlowElementMetaData flowElementMetaData, List<Transition> transitions,
                                 Map<String, FlowElementMetaData> flowElements, Map<String, ConfigurationMetaData> configurationMetaDataMap)
    {
        ConfigurationMetaData configurationMetaData = configurationMetaDataMap.get(flowElement.getConfigurationId());

        String nodeId = flowElement.getComponentName() + identifier++;
        this.manageModuleMaps(nodeId, configurationMetaDataMap, flowElement);

        if(flowElement.getImplementingClass().equals("org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumer"))
        {
            if(configurationMetaData != null && configurationMetaData.getImplementingClass().equals("org.ikasan.endpoint.ftp.consumer.FtpConsumerConfiguration"))
            {
                String remoteHost = this.getConfigurationParameterMetaData("remoteHost", configurationMetaData);

                return FtpConsumer.ftpConsumerBuilder()
                    .withId(nodeId)
                    .withName(WordUtils.wrap(flowElement.getComponentName(), 25))
                    .withTransitionLabel(this.fromTransitionLabelMap.get(flowElement.getComponentName()))
                    .withTransition(manageFlowElement(flowElementMetaData, transitions, flowElements, configurationMetaDataMap))
                    .withSource(new FtpLocation("ftpLocation"+ identifier++, remoteHost))
                    .build();

            }
            else if(configurationMetaData != null && configurationMetaData.getImplementingClass().equals("org.ikasan.endpoint.sftp.consumer.SftpConsumerConfiguration"))
            {
                String remoteHost = this.getConfigurationParameterMetaData("remoteHost", configurationMetaData);

                return SftpConsumer.sftpConsumerBuilder()
                    .withId(nodeId)
                    .withName(WordUtils.wrap(flowElement.getComponentName(), 25))
                    .withTransitionLabel(this.fromTransitionLabelMap.get(flowElement.getComponentName()))
                    .withTransition(manageFlowElement(flowElementMetaData, transitions, flowElements, configurationMetaDataMap))
                    .withSource(new SftpLocation("sftpLocation"+ identifier++, remoteHost))
                    .build();
            }
            else
            {
                return PollingConsumer.pollingConsumerBuilder()
                    .withId(nodeId)
                    .withName(WordUtils.wrap(flowElement.getComponentName(), 25))
                    .withTransitionLabel(this.fromTransitionLabelMap.get(flowElement.getComponentName()))
                    .withTransition(manageFlowElement(flowElementMetaData, transitions, flowElements, configurationMetaDataMap))
                    .withSource(new FileLocation("fileLocation"+ identifier++, ""))
                    .build();
            }
        }

        String destinationName = this.getConfigurationParameterMetaData("destinationJndiName", configurationMetaData);


        return EventDrivenConsumer.sftpConsumerBuilder()
            .withId(nodeId)
            .withName(WordUtils.wrap(flowElement.getComponentName(), 25))
            .withTransitionLabel(this.fromTransitionLabelMap.get(flowElement.getComponentName()))
            .withTransition(manageFlowElement(flowElementMetaData, transitions, flowElements, configurationMetaDataMap))
            .withSource(new MessageChannel("messageChannel"+ identifier++, WordUtils.wrap(destinationName, 25, "\n", true, "\\."), false))
            .build();
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
    private Node manageConverter(FlowElementMetaData flowElement, FlowElementMetaData flowElementMetaData, List<Transition> transitions, Map<String, FlowElementMetaData> flowElements, Map<String, ConfigurationMetaData> configurationMetaDataMap)
    {
        String nodeId = flowElement.getComponentName() + identifier++;
        this.manageModuleMaps(nodeId, configurationMetaDataMap, flowElement);

        return MessageConverter.messageConverterBuilder()
            .withId(nodeId)
            .withName(WordUtils.wrap(flowElement.getComponentName(), 25))
            .withTransitionLabel(this.fromTransitionLabelMap.get(flowElement.getComponentName()))
            .withTransition(manageFlowElement(flowElementMetaData, transitions, flowElements, configurationMetaDataMap))
            .build();
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
    private Node manageTranslator(FlowElementMetaData flowElement, FlowElementMetaData flowElementMetaData, List<Transition> transitions,
                                 Map<String, FlowElementMetaData> flowElements, Map<String, ConfigurationMetaData> configurationMetaDataMap)
    {
        String nodeId = flowElement.getComponentName() + identifier++;
        this.manageModuleMaps(nodeId, configurationMetaDataMap, flowElement);

        return MessageTranslator.messageConverterBuilder()
            .withId(nodeId)
            .withName(WordUtils.wrap(flowElement.getComponentName(), 25))
            .withTransitionLabel(this.fromTransitionLabelMap.get(flowElement.getComponentName()))
            .withTransition(manageFlowElement(flowElementMetaData, transitions, flowElements, configurationMetaDataMap))
            .build();
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
    private Node manageSplitter(FlowElementMetaData flowElement, FlowElementMetaData flowElementMetaData, List<Transition> transitions,
                                  Map<String, FlowElementMetaData> flowElements, Map<String, ConfigurationMetaData> configurationMetaDataMap)
    {
        String nodeId = flowElement.getComponentName() + identifier++;
        this.manageModuleMaps(nodeId, configurationMetaDataMap, flowElement);

        return org.ikasan.dashboard.ui.visualisation.model.flow.Splitter.splitterBuilder()
            .withId(nodeId)
            .withName(WordUtils.wrap(flowElement.getComponentName(), 25))
            .withTransitionLabel(this.fromTransitionLabelMap.get(flowElement.getComponentName()))
            .withTransition(manageFlowElement(flowElementMetaData, transitions, flowElements, configurationMetaDataMap))
            .build();
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
    private Node manageFilter(FlowElementMetaData flowElement, FlowElementMetaData flowElementMetaData, List<Transition> transitions,
                                Map<String, FlowElementMetaData> flowElements, Map<String, ConfigurationMetaData> configurationMetaDataMap)
    {
        String nodeId = flowElement.getComponentName() + identifier++;
        this.manageModuleMaps(nodeId, configurationMetaDataMap, flowElement);

        return org.ikasan.dashboard.ui.visualisation.model.flow.Filter.filterBuilder()
            .withId(nodeId)
            .withName(WordUtils.wrap(flowElement.getComponentName(), 25))
            .withTransitionLabel(this.fromTransitionLabelMap.get(flowElement.getComponentName()))
            .withTransition(manageFlowElement(flowElementMetaData, transitions, flowElements, configurationMetaDataMap))
            .build();
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
    private Node manageBroker(FlowElementMetaData flowElement, FlowElementMetaData flowElementMetaData, List<Transition> transitions,
                              Map<String, FlowElementMetaData> flowElements, Map<String, ConfigurationMetaData> configurationMetaDataMap)
    {
        String nodeId = flowElement.getComponentName() + identifier++;
        this.manageModuleMaps(nodeId, configurationMetaDataMap, flowElement);

        return org.ikasan.dashboard.ui.visualisation.model.flow.Broker.brokerBuilder()
            .withId(nodeId)
            .withName(WordUtils.wrap(flowElement.getComponentName(), 25))
            .withTransitionLabel(this.fromTransitionLabelMap.get(flowElement.getComponentName()))
            .withTransition(manageFlowElement(flowElementMetaData, transitions, flowElements, configurationMetaDataMap))
            .build();
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
    private Node manageSingleRecipientRouter(FlowElementMetaData flowElement, List<Transition> transitions,
                                             Map<String, FlowElementMetaData> flowElements, Map<String, ConfigurationMetaData> configurationMetaDataMap,
                                             List<FlowElementMetaData> flowElementMetaDataTransitions)
    {
        String nodeId = flowElement.getComponentName() + identifier++;
        this.manageModuleMaps(nodeId, configurationMetaDataMap, flowElement);

        org.ikasan.dashboard.ui.visualisation.model.flow.SingleRecipientRouter router =
            org.ikasan.dashboard.ui.visualisation.model.flow.SingleRecipientRouter.singleRecipientRouterBuilder()
                .withId(nodeId)
                .withName(WordUtils.wrap(flowElement.getComponentName(), 25))
                .build();

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
    private Node manageMultiRecipientRouter(FlowElementMetaData flowElement, List<Transition> transitions,
                                             Map<String, FlowElementMetaData> flowElements, Map<String, ConfigurationMetaData> configurationMetaDataMap,
                                             List<FlowElementMetaData> flowElementMetaDataTransitions)
    {
        String nodeId = flowElement.getComponentName() + identifier++;
        this.manageModuleMaps(nodeId, configurationMetaDataMap, flowElement);

        org.ikasan.dashboard.ui.visualisation.model.flow.RecipientListRouter router =
            org.ikasan.dashboard.ui.visualisation.model.flow.RecipientListRouter.recipientRouterBuilder()
                .withId(nodeId)
                .withName(WordUtils.wrap(flowElement.getComponentName(), 25))
                .build();

        flowElementMetaDataTransitions.stream().forEach(flowElementMetaData ->
            router.addTransition(Optional.ofNullable(this.toTransitionLabelMap.get(flowElementMetaData.getComponentName())).orElse(flowElementMetaData.getComponentName())
                , manageFlowElement(flowElementMetaData, transitions, flowElements, configurationMetaDataMap)));

        return router;
    }

    /**
     * Get a parameter value in the form of a String.
     *
     * @param parameter
     * @param configurationMetaData
     * @return
     */
    protected String getConfigurationParameterMetaData(String parameter, ConfigurationMetaData configurationMetaData)
    {
        if(configurationMetaData == null)
        {
            return "";
        }

        ConfigurationParameterMetaData parameterMetaData = ((List<ConfigurationParameterMetaData>)configurationMetaData.getParameters()).stream()
            .filter(configurationParameterMetaData -> parameter.equals(configurationParameterMetaData.getName()))
            .findAny()
            .orElse(null);

        if(parameterMetaData == null || parameterMetaData.getValue() == null)
        {
            return "";
        }

        return String.valueOf(parameterMetaData.getValue());
    }

    /**
     * Helper method to associate configuration meta data with the node identifier.
     *
     * @param nodeId
     * @param configurationMetaDataMap
     * @param flowElement
     */
    private void manageModuleMaps(String nodeId, Map<String, ConfigurationMetaData> configurationMetaDataMap, FlowElementMetaData flowElement)
    {
        this.componentMap.put(nodeId, flowElement);

        ConfigurationMetaData configurationMetaData = configurationMetaDataMap.get(flowElement.getConfigurationId());

        if(configurationMetaData != null)
        {
            this.configurationMetaDataHashMap.put(nodeId, configurationMetaData);
        }
    }
}
