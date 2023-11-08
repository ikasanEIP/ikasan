/*
 * $Id$
 * $URL$
 *
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 *
 * Distributed under the Modified BSD License.
 * Copyright notice: The copyright for this software and a full listing
 * of individual contributors are as shown in the packaged copyright.txt
 * file.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  - Neither the name of the ORGANIZATION nor the names of its contributors may
 *    be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 */
package org.ikasan.builder;

import org.ikasan.builder.component.Builder;
import org.ikasan.builder.conditional.Otherwise;
import org.ikasan.builder.conditional.When;
import org.ikasan.builder.invoker.*;
import org.ikasan.builder.sequential.SequenceName;
import org.ikasan.builder.sequential.SequentialOrder;
import org.ikasan.configurationService.service.ConfiguredResourceConfigurationService;
import org.ikasan.error.reporting.service.ErrorReportingServiceDefaultImpl;
import org.ikasan.exceptionResolver.ExceptionResolver;
import org.ikasan.exclusion.service.ExclusionServiceFactory;
import org.ikasan.flow.configuration.FlowPersistentConfiguration;
import org.ikasan.flow.event.DefaultReplicationFactory;
import org.ikasan.flow.event.ResubmissionEventFactoryImpl;
import org.ikasan.flow.visitorPattern.*;
import org.ikasan.flow.visitorPattern.invoker.*;
import org.ikasan.history.listener.MessageHistoryContextListener;
import org.ikasan.spec.component.IsConsumerAware;
import org.ikasan.spec.component.endpoint.Broker;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.component.filter.Filter;
import org.ikasan.spec.component.routing.MultiRecipientRouter;
import org.ikasan.spec.component.routing.SingleRecipientRouter;
import org.ikasan.spec.component.sequencing.Sequencer;
import org.ikasan.spec.component.splitting.Splitter;
import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.component.transformation.Translator;
import org.ikasan.spec.configuration.ConfigurationService;
import org.ikasan.spec.configuration.Configured;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.error.reporting.ErrorReportingService;
import org.ikasan.spec.error.reporting.ErrorReportingServiceFactory;
import org.ikasan.spec.error.reporting.IsErrorReportingServiceAware;
import org.ikasan.spec.event.EventFactory;
import org.ikasan.spec.exclusion.ExclusionService;
import org.ikasan.spec.exclusion.IsExclusionServiceAware;
import org.ikasan.spec.flow.*;
import org.ikasan.spec.history.MessageHistoryService;
import org.ikasan.spec.monitor.FlowMonitor;
import org.ikasan.spec.monitor.Monitor;
import org.ikasan.spec.monitor.MonitorSubject;
import org.ikasan.spec.recovery.RecoveryManager;
import org.ikasan.spec.recovery.RecoveryManagerFactory;
import org.ikasan.spec.replay.ReplayRecordService;
import org.ikasan.spec.resubmission.ResubmissionEventFactory;
import org.ikasan.spec.resubmission.ResubmissionService;
import org.ikasan.spec.serialiser.SerialiserFactory;
import org.ikasan.spec.trigger.TriggerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A simple Flow builder.
 *
 * @author Ikasan Development Team
 */
public class FlowBuilder implements ApplicationContextAware
{
    /** logger */
    private static Logger logger = LoggerFactory.getLogger(FlowBuilder.class);

    // constants used to generate identifiers for configuration service if ids not provided
    private static String INVOKER = "_I";
    private static String COMPONENT = "_C";

    /** name of the flow module owner */
    String moduleName;

    /** name of the flow being instantiated */
    String flowName;

    /** optional module description */
    String description;

    /** flow event listener */
    @Autowired
    FlowEventListener flowEventListener;

    /** trigger service */
    @Autowired
    TriggerService triggerService;

    /** flow recovery manager factory instance */
    @Autowired
    RecoveryManagerFactory recoveryManagerFactory;

    /** flow recovery manager instance */
    RecoveryManager recoveryManager;

    /** exception resolver to be registered with recovery manager */
    @Autowired
    ExceptionResolver exceptionResolver;

    /** configuration service */
    @Autowired
    ConfigurationService configurationService;

    /** exclusion service factory */
    @Autowired
    ExclusionServiceFactory exclusionServiceFactory;

    /** exclusion service */
    ExclusionService exclusionService;

    /** error reporting service factory */
    @Autowired
    ErrorReportingServiceFactory errorReportingServiceFactory;

    /** error reporting service */
    @Autowired
    ErrorReportingService errorReportingService;

    /** error reporting service time to live */
    Long errorReportingServiceTimeToLive;

    /** message history service */
    @Autowired
    MessageHistoryService messageHistoryService;

    /** flow monitor */
    FlowMonitor monitor;

    /** default event factory */
    EventFactory eventFactory;

    /** head flow element of the exclusion flow */
    FlowElement<?> exclusionFlowHeadElement;

    /** handle to the re-submission service */
    ResubmissionService resubmissionService;

    /** the serialiser factory */
    @Autowired
    SerialiserFactory serialiserFactory;

    /** the replayRecordService **/
    @Autowired
    ReplayRecordService replayRecordService;

    /** List of FlowInvocationListener */
    List<FlowInvocationContextListener> flowInvocationContextListeners;

    ApplicationContext context;

    /** resubmission event factory */
    ResubmissionEventFactory resubmissionEventFactory = new ResubmissionEventFactoryImpl();

    /** Aop Proxy Provider for applying pointcuts */
    @Autowired
    AopProxyProvider aopProxyProvider;

    boolean isRecording = false;

    /**
     * Constructor
     *
     * @param name
     */
    public FlowBuilder(String name, String moduleName, EventFactory eventFactory)
    {
        this.flowName = name;
        if(name == null)
        {
            throw new IllegalArgumentException("flow name cannot be 'null'");
        }

        this.moduleName = moduleName;
        if(moduleName == null)
        {
            throw new IllegalArgumentException("module name cannot be 'null'");
        }

        this.eventFactory = eventFactory;
        if(eventFactory == null)
        {
            throw new IllegalArgumentException("eventFactory name cannot be 'null'");
        }
    }

    /**
     * Default constructor
     *
     */
    public FlowBuilder()
    {
    }

    /**
     * Add a flow name
     *
     * @param name
     * @return
     */
    public FlowBuilder withName(String name)
    {
        this.flowName = name;
        return this;
    }

    /**
     * Add a module name
     *
     * @param moduleName
     * @return
     */
    public FlowBuilder withModuleName(String moduleName)
    {
        this.moduleName = moduleName;
        return this;
    }

    /**
     * Add a flow description
     *
     * @param description
     * @return
     */
    public FlowBuilder withDescription(String description)
    {
        this.description = description;
        return this;
    }

    /**
     * Add a Flow Event Listener
     *
     * @param flowEventListener
     * @return
     */
    public FlowBuilder withFlowListener(FlowEventListener flowEventListener)
    {
        this.flowEventListener = flowEventListener;
        return this;
    }

    /**
     * Add a trigger service
     *
     * @param triggerService
     * @return
     */
    public FlowBuilder withTriggerService(TriggerService triggerService)
    {
        this.triggerService = triggerService;
        return this;
    }


    /**
     * Allow override of default configuration service
     *
     * @param configurationService
     * @return
     */
    public FlowBuilder withConfigurationService(ConfigurationService configurationService)
    {
        this.configurationService = configurationService;
        return this;
    }

    /**
     * Override the default recovery manager
     *
     * @param recoveryManagerFactory
     * @return
     */
    public FlowBuilder withRecoveryManagerFactory(RecoveryManagerFactory recoveryManagerFactory)
    {
        this.recoveryManagerFactory = recoveryManagerFactory;
        return this;
    }

    /**
     * Set the error reporting service factory
     *
     * @param errorReportingServiceFactory
     * @return
     */
    public FlowBuilder withErrorReportingServiceFactory(ErrorReportingServiceFactory errorReportingServiceFactory)
    {
        this.errorReportingServiceFactory = errorReportingServiceFactory;
        return this;
    }

    /**
     * Override the default exclusion service
     *
     * @param exclusionService
     * @return
     */
    public FlowBuilder withExclusionService(ExclusionService exclusionService)
    {
        this.exclusionService = exclusionService;
        return this;
    }

    /**
     * Override the default exclusion service factory
     *
     * @param exclusionServiceFactory
     * @return
     */
    public FlowBuilder withExclusionServiceFactory(ExclusionServiceFactory exclusionServiceFactory)
    {
        this.exclusionServiceFactory = exclusionServiceFactory;
        return this;
    }

    /**
     * Override the default error reporting service
     *
     * @param errorReportingService
     * @return
     */
    public FlowBuilder withErrorReportingService(ErrorReportingService errorReportingService)
    {
        this.errorReportingService = errorReportingService;
        return this;
    }

    /**
     * Override the default error reporting service time to live.
     *
     * @param errorReportingServiceTimeToLive
     * @return
     */
    public FlowBuilder withErrorReportingServiceTimeToLive(Long errorReportingServiceTimeToLive)
    {
        this.errorReportingServiceTimeToLive = errorReportingServiceTimeToLive;
        return this;
    }

    /**
     * Setter for exclusion flow head flow element
     * @param exclusionFlowHeadElement
     */
    public FlowBuilder withExclusionFlowHeadElement(FlowElement<?> exclusionFlowHeadElement)
    {
        this.exclusionFlowHeadElement = exclusionFlowHeadElement;
        return this;
    }

    /**
     * @param serialiserFactory the ikasanSerialiserFactory to set
     */
    public FlowBuilder withSerialiserFactory(SerialiserFactory serialiserFactory)
    {
        this.serialiserFactory = serialiserFactory;
        return this;
    }


    /**
     * Add in a MessageHistoryService
     * @param messageHistoryService
     * @return the current builder
     */
    public FlowBuilder withMessageHistoryService(MessageHistoryService messageHistoryService)
    {
        this.messageHistoryService = messageHistoryService;
        return this;
    }

    /**
     * Add in a FlowInvocationContextListener
     * @param flowInvocationContextListener
     * @return the current builder
     */
    public FlowBuilder withFlowInvocationContextListener(FlowInvocationContextListener flowInvocationContextListener)
    {
        if (flowInvocationContextListeners == null)
        {
            flowInvocationContextListeners = new ArrayList<>();
        }
        flowInvocationContextListeners.add(flowInvocationContextListener);
        return this;
    }

    /**
     * Set the full list of FlowInvocationContextListeners
     * @param flowInvocationContextListeners
     * @return the current builder
     */
    public FlowBuilder withFlowInvocationContextListeners(List<FlowInvocationContextListener> flowInvocationContextListeners)
    {
        this.flowInvocationContextListeners = flowInvocationContextListeners;
        return this;
    }

    /**
     * Setter for monitor
     * @param monitor
     */
    public FlowBuilder withMonitor(FlowMonitor monitor)
    {
        this.monitor = monitor;
        return this;
    }

    /**
     * Setter for monitor
     * @param notifierBuilder
     */
    public FlowBuilder withMonitor(MonitorBuilder.NotifierBuilder notifierBuilder)
    {
        this.withMonitor( notifierBuilder.build() );
        return this;
    }

    /**
     * Setter for re-submission service
     * @param resubmissionService
     */
    public FlowBuilder withResubmissionService(ResubmissionService resubmissionService)
    {
        this.resubmissionService = resubmissionService;
        return this;
    }

    /**
     * Setter for recoveryManager service
     * @param recoveryManager
     */
    public FlowBuilder withRecoveryManager(RecoveryManager recoveryManager)
    {
        this.recoveryManager = recoveryManager;
        return this;
    }

    /**
     * @param replayRecordService the replayRecordService to set
     */
    public FlowBuilder withReplayRecordService(ReplayRecordService replayRecordService)
    {
        this.replayRecordService = replayRecordService;
        return this;
    }

    /**
     * Setter for exception resolver to be registered with the recovery manager.
     * @param exceptionResolver
     */
    public FlowBuilder withExceptionResolver(ExceptionResolver exceptionResolver)
    {
        this.exceptionResolver = exceptionResolver;
        return this;
    }

    /**
     * Setter for exception resolver to be registered with the recovery manager.
     * @param exceptionResolverBuilder
     */
    public FlowBuilder withExceptionResolver(ExceptionResolverBuilder exceptionResolverBuilder)
    {
        return this.withExceptionResolver(exceptionResolverBuilder.build());
    }

    /**
     * Override the default event factory
     *
     * @param eventFactory
     * @return
     */
    public FlowBuilder withEventFactory(EventFactory eventFactory)
    {
        this.eventFactory = eventFactory;
        return this;
    }

    /**
     * Configure if the flow is recording.
     *
     * @param isRecording
     * @return
     */
    public FlowBuilder isRecording(boolean isRecording)
    {
        this.isRecording = isRecording;
        return this;
    }

    protected EventFactory getEventFactory()
    {
        return this.eventFactory;
    }

    /**
     * Add a consumer
     *
     * @param name
     * @param consumer
     * @return
     */
    public PrimaryRouteBuilder consumer(String name, Consumer consumer)
    {
        ConsumerFlowElementInvoker invoker = new ConsumerFlowElementInvoker();
        return new PrimaryRouteBuilder( newPrimaryRoute( new FlowElementImpl(name, this.aopProxyProvider.applyPointcut(name, consumer), invoker) ));
    }

    /**
     * Add a consumer
     *
     * @param name
     * @param consumerBuilder
     * @return
     */
    public PrimaryRouteBuilder consumer(String name, Builder<Consumer> consumerBuilder)
    {
        return consumer(name, consumerBuilder.build());
    }

    /**
     * Add a consumer
     *
     * @param name
     * @param consumerBuilder
     * @return
     */
    public PrimaryRouteBuilder consumer(String name, Builder<Consumer> consumerBuilder, VanillaInvokerConfigurationBuilder consumerInvokerConfigurationBuilder)
    {
        return consumer(name, consumerBuilder.build(), consumerInvokerConfigurationBuilder.build());
    }

    /**
     * Add a consumer
     *
     * @param name
     * @param consumerBuilder
     * @return
     */
    public PrimaryRouteBuilder consumer(String name, Builder<Consumer> consumerBuilder, InvokerConfiguration consumerInvokerConfiguration)
    {
        return consumer(name, consumerBuilder.build(), consumerInvokerConfiguration);
    }

    /**
     * Add a consumer
     *
     * @param name
     * @param consumer
     * @param consumerInvokerConfigurationBuilder
     * @return
     */
    public PrimaryRouteBuilder consumer(String name, Consumer consumer, VanillaInvokerConfigurationBuilder consumerInvokerConfigurationBuilder)
    {
        return consumer(name, consumer, consumerInvokerConfigurationBuilder.build());
    }

    /**
     * Add a consumer
     *
     * @param name
     * @param consumer
     * @return
     */
    public PrimaryRouteBuilder consumer(String name, Consumer consumer, InvokerConfiguration consumerInvokerConfiguration)
    {
        ConsumerFlowElementInvoker invoker = new ConsumerFlowElementInvoker();
        invoker.setConfiguration(consumerInvokerConfiguration);
        return new PrimaryRouteBuilder( newPrimaryRoute( new FlowElementImpl(name, this.aopProxyProvider.applyPointcut(name, consumer), invoker) ));
    }


    protected Route newPrimaryRoute(FlowElement<Consumer> flowElement)
    {
        List<FlowElement> flowElements = new ArrayList<FlowElement>();
        flowElements.add(flowElement);
        return new RouteImpl(flowElements);
    }

    protected FlowElement connectElements(List<FlowElement> flowElements, Map<String, FlowElement> transitions)
    {
        int count = flowElements.size();
        FlowElement nextFlowElement = null;

        while (count > 0)
        {
            FlowElement flowElement = flowElements.get(--count);

            // if the invoker is a configured resource
            // set the configuredResourceId if not already set
            if(flowElement.getFlowElementInvoker() instanceof ConfiguredResource)
            {
                ConfiguredResource configuredResource = ((ConfiguredResource)flowElement.getFlowElementInvoker());
                Object configuration = configuredResource.getConfiguration();
                if(configuredResource.getConfiguredResourceId() == null)
                {
                    String id = generateIdentifier(moduleName, flowName, flowElement.getComponentName(), configuration.getClass().getName(), INVOKER);
                    configuredResource.setConfiguredResourceId(id);
                }
            }

            // if the POJO is a configured resource
            // set the configuredResourceId if not already set
            if(flowElement.getFlowComponent() instanceof ConfiguredResource)
            {
                ConfiguredResource configuredResource = ((ConfiguredResource)flowElement.getFlowComponent());
                Object configuration = configuredResource.getConfiguration();
                if(configuredResource.getConfiguredResourceId() == null && configuration != null)
                {
                    String id = generateIdentifier(moduleName, flowName, flowElement.getComponentName(), configuration.getClass().getName(), COMPONENT);
                    configuredResource.setConfiguredResourceId(id);
                }
            }

            // if the POJO is a configured then check to ensure the configuration class has been set
            // if not then create and set it
            if(flowElement.getFlowComponent() instanceof Configured)
            {
                Configured configured = ((Configured)flowElement.getFlowComponent());
                if(configured.getConfiguration() == null)
                {
                    logger.info(flowElement.getComponentName() + " configured component without a configuration instance set. Will try to create...");
                    Object conf = generateConfiguredInstance(configured);
                    if(conf != null)
                    {
                        configured.setConfiguration(conf);
                        logger.info(conf.getClass().getName() + " configuration created and set on component " + flowElement.getComponentName());
                    }
                    else
                    {
                        configured.setConfiguration(conf);
                        logger.info("Failed to create configuration for component " + flowElement.getComponentName());
                    }
                }
            }

            if (flowElement.getFlowComponent() instanceof Consumer)
            {
                Consumer consumer = (Consumer) flowElement.getFlowComponent();
                consumer.setEventFactory(eventFactory);
                nextFlowElement = new FlowElementImpl(
                    flowElement.getComponentName(),
                    consumer,
                    flowElement.getFlowElementInvoker(), nextFlowElement);
            }
            else if (flowElement.getFlowComponent() instanceof MultiRecipientRouter)
            {
                nextFlowElement = new FlowElementImpl(
                    flowElement.getComponentName(),
                    this.aopProxyProvider.applyPointcut(flowElement.getComponentName(), flowElement.getFlowComponent()),
                    flowElement.getFlowElementInvoker(), new LinkedHashMap<>(transitions) );
            }
            else if (flowElement.getFlowComponent() instanceof SingleRecipientRouter)
            {
                nextFlowElement = new FlowElementImpl(
                    flowElement.getComponentName(),
                    this.aopProxyProvider.applyPointcut(flowElement.getComponentName(), flowElement.getFlowComponent()),
                    flowElement.getFlowElementInvoker(), new LinkedHashMap<>(transitions) );
            }
            else if (flowElement.getFlowComponent() instanceof Sequencer)
            {
                nextFlowElement = new FlowElementImpl(
                    flowElement.getComponentName(),
                    this.aopProxyProvider.applyPointcut(flowElement.getComponentName(), flowElement.getFlowComponent()),
                    flowElement.getFlowElementInvoker(), new LinkedHashMap<>(transitions) );
            }
            else if (flowElement.getFlowComponent() instanceof Producer)
            {
                nextFlowElement = new FlowElementImpl(
                    flowElement.getComponentName(),
                    this.aopProxyProvider.applyPointcut(flowElement.getComponentName(), flowElement.getFlowComponent()),
                    flowElement.getFlowElementInvoker());
            }
            else if (flowElement.getFlowComponent() instanceof When
                || flowElement.getFlowComponent() instanceof Otherwise
                || flowElement.getFlowComponent() instanceof SequenceName
            )
            {
                nextFlowElement = new FlowElementImpl(
                    flowElement.getComponentName(),
                    flowElement.getFlowComponent(),
                    flowElement.getFlowElementInvoker(),
                    nextFlowElement);
            }
            else
            {
                nextFlowElement = new FlowElementImpl(
                    flowElement.getComponentName(),
                    this.aopProxyProvider.applyPointcut(flowElement.getComponentName(), flowElement.getFlowComponent()),
                    flowElement.getFlowElementInvoker(),
                    nextFlowElement);
            }
        }

        return nextFlowElement;
    }

    protected Object generateConfiguredInstance(Configured configured)
    {
        logger.warn("Component [{}] is configured but has no configuration instance set will attempt to instantiate",
            configured.toString());
        Type[] types = configured.getClass().getGenericInterfaces();
        for(Type type:types)
        {
            if (type instanceof ParameterizedType parameterizedType && Configured.class.isAssignableFrom(
                (Class)parameterizedType.getRawType())) {
                Type configurationType = parameterizedType.getActualTypeArguments()[0];
                try {
                    if (configurationType instanceof Class class1)
                        return class1.getConstructor().newInstance();
                } catch (InvocationTargetException | InstantiationException | IllegalAccessException
                    | NoSuchMethodException e) {
                }

            }
        }

        logger.warn("Unable to instantiate configuration for component [{}]", configured);
        return null;
    }

    protected String generateIdentifier(String moduleName, String flowName, String componentName, String fqClassName, String type)
    {
        String id = moduleName + "_" + flowName + "_" + componentName + "_" + fqClassName.hashCode() + type;
        if(id.length() > 255)
        {
            id = moduleName + "_" + flowName + "_" + componentName.hashCode() + "_" + fqClassName.hashCode() + type;
            if(id.length() > 255)
            {
                id = moduleName + "_" + flowName.hashCode() + "_" + componentName.hashCode() + "_" + fqClassName.hashCode() + type;
                if(id.length() > 255)
                {
                    id = moduleName.hashCode() + "_" + flowName.hashCode() + "_" + componentName.hashCode() + "_" + fqClassName.hashCode() + type;
                }
                if(id.length() > 255)
                {
                    id = id.substring(0,253) + type;
                    logger.warn("Generated Identifier exceeds 255 characters for moduleName[" + moduleName + "] flowName[" + flowName
                        + "] componentName[" + componentName + "] fq classname[" + fqClassName + "] type [" + type + "]. Truncated to 255 -> [" + id + "]");
                }
            }
        }

        return id;
    }

    protected FlowElement connectElements(Route route)
    {
        List<FlowElement> flowElements = route.getFlowElements();

        if(route.getNestedRoutes().size() > 0)
        {
            Map<String,FlowElement> transitions = new LinkedHashMap<>();

            for(Route nestedRoute:route.getNestedRoutes())
            {
                FlowElement nestedHead = connectElements(nestedRoute);


                if (nestedHead.getFlowComponent() instanceof When)
                {
                    transitions.put(((When) nestedHead.getFlowComponent()).getResult(), nestedHead.getTransition(FlowElement.DEFAULT_TRANSITION_NAME));
                }
                else if (nestedHead.getFlowComponent() instanceof Otherwise)
                {
                    transitions.put(((Otherwise) nestedHead.getFlowComponent()).getResult(), nestedHead.getTransition(FlowElement.DEFAULT_TRANSITION_NAME));
                }
                else if (nestedHead.getFlowComponent() instanceof SequenceName)
                {
                    transitions.put(((SequenceName) nestedHead.getFlowComponent()).getName(), nestedHead.getTransition(FlowElement.DEFAULT_TRANSITION_NAME));
                }
                else
                {
                    throw new IllegalStateException("Unsupported FlowElement encountered in the builder [" + nestedHead.getFlowComponent().getClass() + "]");
                }
            }

            return connectElements(flowElements, transitions);
        }

        return connectElements(flowElements, null); // TODO - better way of managing this?
    }

    protected Flow _build(Route _route)
    {
        return _build( connectElements(_route) );
    }

    Flow _build(FlowElement headFlowElement)
    {
        if (configurationService == null)
        {
            configurationService = ConfiguredResourceConfigurationService
                .getDefaultConfigurationService();
        }
        // if resubmissionService not specifically set then check to see if consumer supports ResubmissionService, if so then set it
        if (resubmissionService != null)
        {
            if (!(AopUtils.isJdkDynamicProxy(resubmissionService)))
            {
                Consumer unwrappedConsumer = getTargetObject(headFlowElement.getFlowComponent(), Consumer.class);
                if (unwrappedConsumer == resubmissionService)
                {
                    logger.info(
                        "ResubmissionService is equal to Proxied Consumer. Setting resubmissionService to Proxy object.");
                    resubmissionService = (ResubmissionService) headFlowElement.getFlowComponent();
                }
                else
                {
                    logger.info(
                        "ResubmissionService is not instance of JdkDynamicProxy. Trying to proxy resubmissionService.");
                    resubmissionService = this.aopProxyProvider
                        .applyPointcut(flowName + "resubmissionService", resubmissionService);
                }
            }
        }
        else
        {
            if (headFlowElement.getFlowComponent() instanceof ResubmissionService)
            {
                resubmissionService = (ResubmissionService) headFlowElement.getFlowComponent();
            }
        }

        // replayRecordService
        if(replayRecordService == null)
        {
            logger.info("Record/Replay is not supported for ModuleName[" + moduleName + "] Flowname[" + flowName + "]");
        }

        if(resubmissionService == null)
        {
            logger.info("Resubmission is not supported for ModuleName[" + moduleName + "] Flowname[" + flowName + "]");
        }
        else
        {
            resubmissionService.setResubmissionEventFactory(resubmissionEventFactory);
        }

        if (exclusionService == null)
        {
            if(exclusionServiceFactory == null)
            {
                throw new IllegalArgumentException("exclusionServiceFactory cannot be 'null'");
            }

            exclusionService = exclusionServiceFactory.getExclusionService(moduleName, flowName);
        }

        if (errorReportingService == null)
        {
            errorReportingService = errorReportingServiceFactory.getErrorReportingService();
        }

        if(errorReportingService instanceof ErrorReportingServiceDefaultImpl impl)
        {
            impl.setModuleName(moduleName);
            impl.setFlowName(flowName);
        }

        if(this.errorReportingServiceTimeToLive != null)
        {
            errorReportingService.setTimeToLive(errorReportingServiceTimeToLive);
        }

        if (recoveryManager == null)
        {
            recoveryManager = this.recoveryManagerFactory.getRecoveryManager(flowName, moduleName);
        }

        if(recoveryManager instanceof IsConsumerAware aware)
        {
            aware.setConsumer(((FlowElement<Consumer>) headFlowElement).getFlowComponent());
        }

        if(recoveryManager instanceof IsExclusionServiceAware aware)
        {
            aware.setExclusionService(exclusionService);
        }

        if(recoveryManager instanceof IsErrorReportingServiceAware aware)
        {
            aware.setErrorReportingService(errorReportingService);
        }

        if(exceptionResolver != null)
        {
            recoveryManager.setResolver(exceptionResolver);
        }

        FlowConfiguration flowConfiguration = new DefaultFlowConfiguration(headFlowElement, configurationService, resubmissionService, replayRecordService);

        ExclusionFlowConfiguration exclusionFlowConfiguration = null;
        if(exclusionFlowHeadElement != null)
        {
            exclusionFlowConfiguration = new DefaultExclusionFlowConfiguration(exclusionFlowHeadElement, configurationService, resubmissionService, replayRecordService);
        }

        if(flowName == null)
        {
            throw new IllegalArgumentException("flow name cannot be 'null'");
        }

        if(moduleName == null)
        {
            throw new IllegalArgumentException("module name cannot be 'null'");
        }

        Flow flow = new VisitingInvokerFlow(flowName, moduleName, flowConfiguration, exclusionFlowConfiguration, recoveryManager, exclusionService, serialiserFactory);
        flow.setFlowListener(flowEventListener);
        flow.setTriggerService(triggerService);

        if(flow instanceof ConfiguredResource resource)
        {
            FlowPersistentConfiguration flowPersistentConfiguration = (FlowPersistentConfiguration)resource.getConfiguration();
            flowPersistentConfiguration.setIsRecording(isRecording);
        }

        // pass handle to the error reporting service if flow needs to be aware of this
        if(flow instanceof IsErrorReportingServiceAware aware)
        {
            aware.setErrorReportingService(errorReportingService);
        }

        if(monitor == null)
        {
            monitor = context.getBean(FlowMonitor.class);
        }

        if(monitor != null && flow instanceof MonitorSubject subject)
        {
            if(monitor.getEnvironment() == null)
            {
                monitor.setEnvironment("Undefined Environment");
            }

            if(monitor.getModuleName() == null)
            {
                monitor.setModuleName(moduleName);
            }

            if(monitor.getFlowName() == null)
            {
                monitor.setFlowName(flowName);
            }

            subject.setMonitor(monitor);
        }

        // add a default MessageHistoryContextListener if necessary.
        if(this.flowInvocationContextListeners == null)
        {
            this.flowInvocationContextListeners = new ArrayList<>();
            MessageHistoryContextListener listener = new MessageHistoryContextListener(this.messageHistoryService,
                moduleName, flowName);

            this.flowInvocationContextListeners.add(listener);
        }

        flow.setFlowInvocationContextListeners(flowInvocationContextListeners);

        logger.info("Instantiated flow - name[" + flowName + "] module[" + moduleName
            + "] with RecoveryManager[" + ((recoveryManager != null) ? recoveryManager.getClass().getSimpleName() : "none")
            + "] with ErrorReportingService[" + ((errorReportingService != null) ? errorReportingService.getClass().getSimpleName() : "none")
            + "] with ResubmissionService[" + ((resubmissionService != null) ? resubmissionService.getClass().getSimpleName() : "none")
            + "] with ExceptionResolver[" + ((exceptionResolver != null) ? exceptionResolver.getClass().getSimpleName() : "none")
            + "] with ExclusionService[" + ((exclusionService != null) ? exclusionService.getClass().getSimpleName() : "none")
            + "] with ConfigurationService[" + ((configurationService != null) ? configurationService.getClass().getSimpleName() : "none")
            + "] with RecordReplayService[" + ((replayRecordService != null) ? replayRecordService.getClass().getSimpleName() : "none")
            + "] with FlowEventListener[" + ((flowEventListener != null) ? flowEventListener.getClass().getSimpleName() : "none")
            + "] with Monitor[" + ((monitor != null && flow instanceof MonitorSubject) ? monitor.getClass().getSimpleName() : "none")
            + "]");

        return flow;
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.context = context;
    }

    private <T> T getTargetObject(Object proxy, Class<T> targetClass) {
        try
        {
            if (AopUtils.isJdkDynamicProxy(proxy))
            {
                return (T) ((Advised) proxy).getTargetSource().getTarget();
            }
            else
            {
                return (T) proxy; // expected to be cglib proxy then, which is simply a specialized class
            }
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }


    public class PrimaryRouteBuilder
    {
        Route route;

        public PrimaryRouteBuilder(Route route)
        {
            this.route = route;
            if (route == null)
            {
                throw new IllegalArgumentException("route cannot be 'null'");
            }
        }

        public PrimaryRouteBuilder converter(String name, Converter converter)
        {
            this.route.addFlowElement(new FlowElementImpl(name, converter, new ConverterFlowElementInvoker()));
            return this;
        }

        public PrimaryRouteBuilder converter(String name, Builder<Converter> converterBuilder)
        {
            return this.converter(name, converterBuilder.build());
        }

        public PrimaryRouteBuilder converter(String name, Builder<Converter> converterBuilder, InvokerConfiguration converterInvokerConfiguration)
        {
            return this.converter(name, converterBuilder.build(), converterInvokerConfiguration);
        }

        public PrimaryRouteBuilder converter(String name, Builder<Converter> converterBuilder, VanillaInvokerConfigurationBuilder converterInvokerConfigurationBuilder)
        {
            return this.converter(name, converterBuilder.build(), converterInvokerConfigurationBuilder.build());
        }

        public PrimaryRouteBuilder converter(String name, Converter converter, InvokerConfiguration converterInvokerConfiguration)
        {
            ConverterFlowElementInvoker converterFlowElementInvoker = new ConverterFlowElementInvoker();
            converterFlowElementInvoker.setConfiguration(converterInvokerConfiguration);
            this.route.addFlowElement(new FlowElementImpl(name, converter, converterFlowElementInvoker));
            return this;
        }

        public PrimaryRouteBuilder converter(String name, Converter converter, VanillaInvokerConfigurationBuilder converterInvokerConfigurationBuilder)
        {
            return this.converter(name, converter, converterInvokerConfigurationBuilder.build());
        }

        public PrimaryRouteBuilder translator(String name, Translator translator)
        {
            this.route.addFlowElement(new FlowElementImpl(name, translator, new TranslatorFlowElementInvoker()));
            return this;
        }

        public PrimaryRouteBuilder translator(String name, Builder<Translator> translatorBuilder)
        {
            return this.translator(name, translatorBuilder.build());
        }

        public PrimaryRouteBuilder translator(String name, Translator translator, TranslatorInvokerConfiguration translatorInvokerConfiguration)
        {
            TranslatorFlowElementInvoker translatorFlowElementInvoker = new TranslatorFlowElementInvoker();
            translatorFlowElementInvoker.setConfiguration(translatorInvokerConfiguration);
            this.route.addFlowElement(new FlowElementImpl(name, translator, translatorFlowElementInvoker));
            return this;
        }

        public PrimaryRouteBuilder translator(String name, Builder<Translator> translatorBuilder, TranslatorInvokerConfigurationBuilder translatorInvokerConfigurationBuilder)
        {
            return this.translator(name, translatorBuilder.build(), translatorInvokerConfigurationBuilder.build());
        }

        public PrimaryRouteBuilder translator(String name, Translator translator, TranslatorInvokerConfigurationBuilder translatorInvokerConfigurationBuilder)
        {
            return this.translator(name, translator, translatorInvokerConfigurationBuilder.build());
        }

        public PrimaryRouteBuilder translator(String name, Builder<Translator> translatorBuilder, TranslatorInvokerConfiguration translatorInvokerConfiguration)
        {
            return this.translator(name, translatorBuilder.build(), translatorInvokerConfiguration);
        }

        public PrimaryRouteBuilder splitter(String name, Splitter splitter)
        {
            this.route.addFlowElement(new FlowElementImpl(name, splitter, new SplitterFlowElementInvoker(eventFactory)));
            return this;
        }

        public PrimaryRouteBuilder splitter(String name, Builder<Splitter> splitterBuilder)
        {
            return this.splitter(name, splitterBuilder.build());
        }

        public PrimaryRouteBuilder splitter(String name, Splitter splitter, SplitterInvokerConfigurationBuilder splitterInvokerConfigurationBuilder)
        {
            return this.splitter(name, splitter, splitterInvokerConfigurationBuilder.build());
        }

        public PrimaryRouteBuilder splitter(String name, Splitter splitter, SplitterInvokerConfiguration splitterInvokerConfiguration)
        {
            SplitterFlowElementInvoker splitterFlowElementInvoker = new SplitterFlowElementInvoker(eventFactory);
            splitterFlowElementInvoker.setConfiguration(splitterInvokerConfiguration);
            this.route.addFlowElement(new FlowElementImpl(name, splitter, splitterFlowElementInvoker));
            return this;
        }

        public PrimaryRouteBuilder splitter(String name, Builder<Splitter> splitterBuilder, SplitterInvokerConfiguration splitterInvokerConfiguration)
        {
            return this.splitter(name, splitterBuilder.build(), splitterInvokerConfiguration);
        }

        public PrimaryRouteBuilder splitter(String name, Builder<Splitter> splitterBuilder, SplitterInvokerConfigurationBuilder splitterInvokerConfigurationBuilder)
        {
            return this.splitter(name, splitterBuilder.build(), splitterInvokerConfigurationBuilder.build());
        }

        public PrimaryRouteBuilder concurrentSplitter(String name, Splitter splitter)
        {
            ConcurrentSplitterInvokerConfiguration concurrentSplitterInvokerConfiguration = new ConcurrentSplitterInvokerConfiguration();
            ExecutorService executorService = Executors.newFixedThreadPool(concurrentSplitterInvokerConfiguration.getConcurrentThreads());
            this.route.addFlowElement(new FlowElementImpl(name, splitter, new ConcurrentSplitterFlowElementInvoker(executorService)));
            return this;
        }

        public PrimaryRouteBuilder concurrentSplitter(String name, Builder<Splitter> concurrentSplitterBuilder)
        {
            return this.concurrentSplitter(name, concurrentSplitterBuilder.build());
        }

        public PrimaryRouteBuilder concurrentSplitter(String name, Splitter splitter, ConcurrentSplitterInvokerConfigurationBuilder concurrentSplitterInvokerConfigurationBuilder)
        {
            return this.concurrentSplitter(name, splitter, concurrentSplitterInvokerConfigurationBuilder.build());
        }

        public PrimaryRouteBuilder concurrentSplitter(String name, Splitter splitter, ConcurrentSplitterInvokerConfiguration concurrentSplitterInvokerConfiguration)
        {
            ExecutorService executorService = Executors.newFixedThreadPool(concurrentSplitterInvokerConfiguration.getConcurrentThreads());
            ConcurrentSplitterFlowElementInvoker concurrentSplitterFlowElementInvoker = new ConcurrentSplitterFlowElementInvoker(executorService);
            concurrentSplitterFlowElementInvoker.setConfiguration(concurrentSplitterInvokerConfiguration);
            this.route.addFlowElement(new FlowElementImpl(name, splitter, concurrentSplitterFlowElementInvoker));
            return this;
        }

        public PrimaryRouteBuilder concurrentSplitter(String name, Builder<Splitter> concurrentSplitterBuilder, ConcurrentSplitterInvokerConfiguration concurrentSplitterInvokerConfiguration)
        {
            return this.concurrentSplitter(name, concurrentSplitterBuilder.build(), concurrentSplitterInvokerConfiguration);
        }

        public PrimaryRouteBuilder concurrentSplitter(String name, Builder<Splitter> concurrentSplitterBuilder, ConcurrentSplitterInvokerConfigurationBuilder concurrentSplitterInvokerConfigurationBuilder)
        {
            return this.concurrentSplitter(name, concurrentSplitterBuilder.build(), concurrentSplitterInvokerConfigurationBuilder.build());
        }

        public PrimaryRouteBuilder filter(String name, Filter filter)
        {
            this.route.addFlowElement(new FlowElementImpl(name, filter, new FilterFlowElementInvoker()));
            return this;
        }

        public PrimaryRouteBuilder filter(String name, Builder<Filter> filterBuilder)
        {
            return this.filter(name, filterBuilder.build());
        }

        public PrimaryRouteBuilder filter(String name, Filter filter, FilterInvokerConfiguration filterInvokerConfiguration)
        {
            FilterFlowElementInvoker filterFlowElementInvoker = new  FilterFlowElementInvoker();
            filterFlowElementInvoker.setConfiguration(filterInvokerConfiguration);
            this.route.addFlowElement(new FlowElementImpl(name, filter, filterFlowElementInvoker));
            return this;
        }

        public PrimaryRouteBuilder filter(String name, Filter filter, FilterInvokerConfigurationBuilder filterInvokerConfigurationBuilder)
        {
            return this.filter(name, filter, filterInvokerConfigurationBuilder.build());
        }

        public PrimaryRouteBuilder filter(String name, Builder<Filter> filterBuilder, FilterInvokerConfiguration filterInvokerConfiguration)
        {
            return this.filter(name, filterBuilder.build(), filterInvokerConfiguration);
        }

        public PrimaryRouteBuilder filter(String name, Builder<Filter> filterBuilder, FilterInvokerConfigurationBuilder filterInvokerConfigurationBuilder)
        {
            return this.filter(name, filterBuilder.build(), filterInvokerConfigurationBuilder.build());
        }

        public Sequence<Flow> sequencer(String name, Sequencer sequencer)
        {
            this.route.addFlowElement(new FlowElementImpl(name, sequencer, new SequencerFlowElementInvoker()));
            return new PrimarySequenceImpl(route);
        }

        public Sequence<Flow> sequencer(String name, Builder<Sequencer> sequencerBuilder)
        {
            return this.sequencer(name, sequencerBuilder.build());
        }

        public Sequence<Flow> sequencer(String name, Sequencer sequencer, InvokerConfiguration sequencerInvokerConfiguration)
        {
            SequencerFlowElementInvoker sequencerFlowElementInvoker = new SequencerFlowElementInvoker();
            sequencerFlowElementInvoker.setConfiguration(sequencerInvokerConfiguration);
            this.route.addFlowElement(new FlowElementImpl(name, sequencer, sequencerFlowElementInvoker));
            return new PrimarySequenceImpl(route);
        }

        public Sequence<Flow> sequencer(String name, Builder<Sequencer> sequencerBuilder, InvokerConfiguration sequencerInvokerConfiguration)
        {
            return this.sequencer(name, sequencerBuilder.build(), sequencerInvokerConfiguration);
        }

        public Sequence<Flow> sequencer(String name, Builder<Sequencer> sequencerBuilder, VanillaInvokerConfigurationBuilder sequencerInvokerConfigurationBuilder)
        {
            return this.sequencer(name, sequencerBuilder.build(), sequencerInvokerConfigurationBuilder.build());
        }

        public Sequence<Flow> sequencer(String name, Sequencer sequencer, VanillaInvokerConfigurationBuilder sequencerInvokerConfigurationBuilder)
        {
            return this.sequencer(name, sequencer, sequencerInvokerConfigurationBuilder.build());
        }

        public PrimaryRouteBuilder broker(String name, Broker broker)
        {
            this.route.addFlowElement(new FlowElementImpl(name, broker, new BrokerFlowElementInvoker()));
            return this;
        }

        public PrimaryRouteBuilder broker(String name, Builder<Broker> brokerBuilder)
        {
            return this.broker(name, brokerBuilder.build());
        }

        public PrimaryRouteBuilder broker(String name, Broker broker, InvokerConfiguration brokerInvokerConfiguration)
        {
            BrokerFlowElementInvoker brokerFlowElementInvoker = new BrokerFlowElementInvoker();
            brokerFlowElementInvoker.setConfiguration(brokerInvokerConfiguration);
            this.route.addFlowElement(new FlowElementImpl(name, broker, brokerFlowElementInvoker));
            return this;
        }

        public PrimaryRouteBuilder broker(String name, Builder<Broker> brokerBuilder, InvokerConfiguration brokerInvokerConfiguration)
        {
            return this.broker(name, brokerBuilder.build(), brokerInvokerConfiguration);
        }

        public PrimaryRouteBuilder broker(String name, Builder<Broker> brokerBuilder, VanillaInvokerConfigurationBuilder brokerInvokerConfigurationBuilder)
        {
            return this.broker(name, brokerBuilder.build(), brokerInvokerConfigurationBuilder.build());
        }

        public PrimaryRouteBuilder broker(String name, Broker broker, VanillaInvokerConfigurationBuilder brokerInvokerConfigurationBuilder)
        {
            return this.broker(name, broker, brokerInvokerConfigurationBuilder.build());
        }

        public EvaluationOtherwise<Flow> singleRecipientRouter(String name, SingleRecipientRouter singleRecipientRouter)
        {
            this.route.addFlowElement(new FlowElementImpl(name, singleRecipientRouter, new SingleRecipientRouterFlowElementInvoker()));
            return new PrimaryEvaluationOtherwiseImpl(route);
        }

        public EvaluationOtherwise<Flow> singleRecipientRouter(String name, SingleRecipientRouter singleRecipientRouter, InvokerConfiguration invokerConfiguration)
        {
            SingleRecipientRouterFlowElementInvoker singleRecipientRouterFlowElementInvoker = new SingleRecipientRouterFlowElementInvoker();
            singleRecipientRouterFlowElementInvoker.setConfiguration(invokerConfiguration);
            this.route.addFlowElement(new FlowElementImpl(name, singleRecipientRouter, singleRecipientRouterFlowElementInvoker));
            return new PrimaryEvaluationOtherwiseImpl(route);
        }

        public EvaluationOtherwise<Flow> singleRecipientRouter(String name, Builder<SingleRecipientRouter> singleRecipientRouterBuilder)
        {
            return this.singleRecipientRouter(name, singleRecipientRouterBuilder.build());
        }

        public EvaluationOtherwise<Flow> singleRecipientRouter(String name, Builder<SingleRecipientRouter> singleRecipientRouterBuilder, InvokerConfiguration invokerConfiguration)
        {
            return this.singleRecipientRouter(name, singleRecipientRouterBuilder.build(), invokerConfiguration);
        }

        public EvaluationOtherwise<Flow> singleRecipientRouter(String name, Builder<SingleRecipientRouter> singleRecipientRouterBuilder, VanillaInvokerConfigurationBuilder invokerConfigurationBuilder)
        {
            return this.singleRecipientRouter(name, singleRecipientRouterBuilder.build(), invokerConfigurationBuilder.build());
        }

        public EvaluationOtherwise<Flow> singleRecipientRouter(String name, SingleRecipientRouter singleRecipientRouter, VanillaInvokerConfigurationBuilder invokerConfigurationBuilder)
        {
            return this.singleRecipientRouter(name, singleRecipientRouter, invokerConfigurationBuilder.build());
        }

        public EvaluationWhen<Flow> multiRecipientRouter(String name, MultiRecipientRouter multiRecipientRouter) {
            this.route.addFlowElement(new FlowElementImpl(name, multiRecipientRouter, new MultiRecipientRouterFlowElementInvoker(DefaultReplicationFactory.getInstance(), new MultiRecipientRouterInvokerConfiguration())));
            return new PrimaryEvaluationWhenImpl(route);
        }

        public EvaluationWhen<Flow> multiRecipientRouter(String name, Builder<MultiRecipientRouter> multiRecipientRouterBuilder) {
            return this.multiRecipientRouter(name, multiRecipientRouterBuilder.build());
        }

        public EvaluationWhen<Flow> multiRecipientRouter(String name, MultiRecipientRouter multiRecipientRouter, MultiRecipientRouterInvokerConfiguration invokerConfiguration)
        {
            MultiRecipientRouterFlowElementInvoker multiRecipientRouterFlowElementInvoker =
                new MultiRecipientRouterFlowElementInvoker(DefaultReplicationFactory.getInstance(), invokerConfiguration);
            this.route.addFlowElement( new FlowElementImpl(name, multiRecipientRouter, multiRecipientRouterFlowElementInvoker) );
            return new PrimaryEvaluationWhenImpl(route);
        }

        public EvaluationWhen<Flow> multiRecipientRouter(String name, Builder<MultiRecipientRouter> multiRecipientRouterBuilder, MultiRecipientRouterInvokerConfiguration invokerConfiguration)
        {
            return this.multiRecipientRouter(name, multiRecipientRouterBuilder.build(), invokerConfiguration);
        }

        public EvaluationWhen<Flow> multiRecipientRouter(String name, MultiRecipientRouter multiRecipientRouter, MultiRecipientRouterInvokerConfigurationBuilder invokerConfigurationBuilder)
        {
            return this.multiRecipientRouter(name, multiRecipientRouter, invokerConfigurationBuilder.build());
        }

        public EvaluationWhen<Flow> multiRecipientRouter(String name, Builder<MultiRecipientRouter> multiRecipientRouterBuilder, MultiRecipientRouterInvokerConfigurationBuilder invokerConfigurationBuilder)
        {
            return this.multiRecipientRouter(name, multiRecipientRouterBuilder.build(), invokerConfigurationBuilder.build());
        }

        public Endpoint<Flow> producer(String name, Producer producer) {
            this.route.addFlowElement(new FlowElementImpl(name, producer, new ProducerFlowElementInvoker()));
            return new EndpointImpl();
        }

        public Endpoint<Flow> producer(String name, Builder<Producer> producerBuilder)
        {
            return producer(name, producerBuilder.build());
        }

        public Endpoint<Flow> producer(String name, Producer producer, InvokerConfiguration producerInvokerConfiguration)
        {
            ProducerFlowElementInvoker producerFlowElementInvoker = new ProducerFlowElementInvoker();
            producerFlowElementInvoker.setConfiguration(producerInvokerConfiguration);
            this.route.addFlowElement( new FlowElementImpl(name, producer, producerFlowElementInvoker) );
            return new EndpointImpl();
        }

        public Endpoint<Flow> producer(String name, Builder<Producer> producerBuilder, InvokerConfiguration producerInvokerConfiguration)
        {
            return producer(name, producerBuilder.build(), producerInvokerConfiguration);
        }

        public Endpoint<Flow> producer(String name, Producer producer, VanillaInvokerConfigurationBuilder producerInvokerConfigurationBuilder)
        {
            return producer(name, producer, producerInvokerConfigurationBuilder.build());
        }

        public Endpoint<Flow> producer(String name, Builder<Producer> producerBuilder, VanillaInvokerConfigurationBuilder producerInvokerConfigurationBuilder)
        {
            return producer(name, producerBuilder.build(), producerInvokerConfigurationBuilder.build());
        }

        class EndpointImpl implements Endpoint<Flow>
        {
            public Flow build()
            {
                return _build(route);
            }
        }
    }

    public class PrimaryEvaluationWhenImpl implements EvaluationWhen<Flow>
    {
        Route route;

        public PrimaryEvaluationWhenImpl(Route route)
        {
            this.route = route;
            if(route == null)
            {
                throw new IllegalArgumentException("route cannot be 'null'");
            }
        }

        public EvaluationWhen<Flow> when(String name, Route evaluatedRoute)
        {
            // create shallow copy of Route before adding When joining
            Route shallowCopy = new RouteImpl(evaluatedRoute);
            shallowCopy.addFlowElementAsFirst(new FlowElementImpl(this.getClass().getName(), new When(name), null));
            this.route.addNestedRoute(shallowCopy);
            return new PrimaryEvaluationWhenImpl(route);
        }

        public EvaluationWhen<Flow> otherwise(Route evaluatedRoute)
        {
            // create shallow copy of Route before adding Otherwise joining
            Route shallowCopy = new RouteImpl(evaluatedRoute);
            shallowCopy.addFlowElementAsFirst(new FlowElementImpl(this.getClass().getName(), new Otherwise(), null));
            this.route.addNestedRoute(shallowCopy);
            return new PrimaryEvaluationWhenImpl(route);
        }

        public Flow build()
        {
            return _build(route);
        }

    }

    public class PrimaryEvaluationOtherwiseImpl implements EvaluationOtherwise<Flow>
    {
        Route route;

        public PrimaryEvaluationOtherwiseImpl(Route route)
        {
            this.route = route;
            if(route == null)
            {
                throw new IllegalArgumentException("route cannot be 'null'");
            }
        }

        public EvaluationOtherwise<Flow> when(String name, Route evaluatedRoute)
        {
            // create shallow copy of Route before adding When joining
            Route shallowCopy = new RouteImpl(evaluatedRoute);
            shallowCopy.addFlowElementAsFirst(new FlowElementImpl(this.getClass().getName(), new When(name), null));
            this.route.addNestedRoute(shallowCopy);
            return new PrimaryEvaluationOtherwiseImpl(route);
        }

        public Flow otherwise(Route evaluatedRoute)
        {
            // create shallow copy of Route before adding Otherwise joining
            Route shallowCopy = new RouteImpl(evaluatedRoute);
            shallowCopy.addFlowElementAsFirst(new FlowElementImpl(this.getClass().getName(), new Otherwise(), null));
            this.route.addNestedRoute(shallowCopy);
            return this.build();
        }

        public Flow build()
        {
            return _build(route);
        }

    }

    public class PrimarySequenceImpl implements Sequence<Flow>
    {
        Route route;

        public PrimarySequenceImpl(Route route)
        {
            this.route = route;
            if(route == null)
            {
                throw new IllegalArgumentException("route cannot be 'null'");
            }
        }

        public Sequence<Flow> route(String name, Route sequencedRoute)
        {
            // create shallow copy of Route before adding SequentialOrder joining
            Route shallowCopy = new RouteImpl(sequencedRoute);
            shallowCopy.addFlowElementAsFirst(new FlowElementImpl(this.getClass().getName(), SequentialOrder.to(name), null));
            this.route.addNestedRoute(shallowCopy);
            return new PrimarySequenceImpl(route);
        }

        public Flow build()
        {
            return _build(route);
        }
    }

}




