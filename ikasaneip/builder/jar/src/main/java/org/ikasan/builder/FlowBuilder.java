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

import org.apache.log4j.Logger;
import org.ikasan.builder.conditional.Otherwise;
import org.ikasan.builder.conditional.When;
import org.ikasan.builder.sequential.SequenceName;
import org.ikasan.builder.sequential.SequentialOrder;
import org.ikasan.configurationService.service.ConfiguredResourceConfigurationService;
import org.ikasan.error.reporting.service.ErrorReportingServiceDefaultImpl;
import org.ikasan.error.reporting.service.ErrorReportingServiceFactoryDefaultImpl;
import org.ikasan.exceptionResolver.ExceptionResolver;
import org.ikasan.exclusion.service.ExclusionServiceFactory;
import org.ikasan.flow.event.FlowEventFactory;
import org.ikasan.flow.visitorPattern.*;
import org.ikasan.flow.visitorPattern.invoker.*;
import org.ikasan.recovery.RecoveryManagerFactory;
import org.ikasan.spec.component.endpoint.Broker;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.component.filter.Filter;
import org.ikasan.spec.component.routing.MultiRecipientRouter;
import org.ikasan.spec.component.routing.Router;
import org.ikasan.spec.component.routing.SingleRecipientRouter;
import org.ikasan.spec.component.sequencing.Sequencer;
import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.component.transformation.Translator;
import org.ikasan.spec.configuration.ConfigurationService;
import org.ikasan.spec.error.reporting.ErrorReportingService;
import org.ikasan.spec.error.reporting.ErrorReportingServiceFactory;
import org.ikasan.spec.error.reporting.IsErrorReportingServiceAware;
import org.ikasan.spec.event.EventFactory;
import org.ikasan.spec.exclusion.ExclusionService;
import org.ikasan.spec.flow.*;
import org.ikasan.spec.monitor.Monitor;
import org.ikasan.spec.monitor.MonitorSubject;
import org.ikasan.spec.recovery.RecoveryManager;
import org.ikasan.spec.replay.ReplayRecordService;
import org.ikasan.spec.resubmission.ResubmissionService;
import org.ikasan.spec.serialiser.SerialiserFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple Flow builder.
 * 
 * @author Ikasan Development Team
 */
public class FlowBuilder
{
    /** logger */
    private static Logger logger = Logger.getLogger(FlowBuilder.class);

	/** name of the flow module owner */
	String moduleName;

	/** name of the flow being instantiated */
	String name;

	/** optional module description */
	String description;

	/** flow event listener */
    @Autowired
    FlowEventListener flowEventListener;

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
    ErrorReportingServiceFactory errorReportingServiceFactory = new ErrorReportingServiceFactoryDefaultImpl();

    /** error reporting service */

    ErrorReportingService errorReportingService;

    /** flow monitor */
    Monitor monitor;

	/** default event factory */
	EventFactory eventFactory = new FlowEventFactory();

    /** head flow element of the exclusion flow */
    FlowElement<?> exclusionFlowHeadElement;
    
    /** handle to the re-submission service */
    //@Autowired
    ResubmissionService resubmissionService;
    
    /** the serialiser factory */
    @Autowired
    SerialiserFactory serialiserFactory;
    
    /** the replayRecordService **/
    ReplayRecordService replayRecordService;

	/** List of FlowInvocationListener */
    List<FlowInvocationContextListener> flowInvocationContextListeners;

    /**
	 * Constructor
	 *
	 * @param name
	 */
	public FlowBuilder(String name, String moduleName)
	{
        this.name = name;
        if(name == null)
        {
            throw new IllegalArgumentException("flow name cannot be 'null'");
        }

        this.moduleName = moduleName;
        if(moduleName == null)
        {
            throw new IllegalArgumentException("module name cannot be 'null'");
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
        this.name = name;
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
	 * Add a flow description
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
    public void setMonitor(Monitor monitor)
    {
        this.monitor = monitor;
    }
    
    /**
     * Setter for re-submission service
     * @param resubmissionService
     */
    public void setResubmissionService(ResubmissionService resubmissionService)
    {
        this.resubmissionService = resubmissionService;
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
    public void setExceptionResolver(ExceptionResolver exceptionResolver)
    {
        this.exceptionResolver = exceptionResolver;
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
        BuilderFactory builderFactory = BuilderFactory.getInstance();
        return new PrimaryRouteBuilder( builderFactory.newPrimaryRoute( new FlowElementImpl(name, consumer, invoker) ));
    }

    protected FlowElement connectElements(List<FlowElement> flowElements, Map<String, FlowElement> transitions)
    {
        int count = flowElements.size();
        FlowElement nextFlowElement = null;

        while (count > 0)
        {
            FlowElement flowElement = flowElements.get(--count);
            if (flowElement.getFlowComponent() instanceof Consumer)
            {
                ((Consumer)flowElement.getFlowComponent()).setEventFactory(eventFactory);
                nextFlowElement = new FlowElementImpl(
                        flowElement.getComponentName(),
                        flowElement.getFlowComponent(),
                        flowElement.getFlowElementInvoker(), nextFlowElement);
            }
            else if (flowElement.getFlowComponent() instanceof Router)
            {
                nextFlowElement = new FlowElementImpl(
                        flowElement.getComponentName(),
                        flowElement.getFlowComponent(),
                        flowElement.getFlowElementInvoker(), new LinkedHashMap<>(transitions) );
            }
            else if (flowElement.getFlowComponent() instanceof MultiRecipientRouter)
            {
                nextFlowElement = new FlowElementImpl(
                        flowElement.getComponentName(),
                        flowElement.getFlowComponent(),
                        flowElement.getFlowElementInvoker(), new LinkedHashMap<>(transitions) );
            }
            else if (flowElement.getFlowComponent() instanceof SingleRecipientRouter)
            {
                nextFlowElement = new FlowElementImpl(
                        flowElement.getComponentName(),
                        flowElement.getFlowComponent(),
                        flowElement.getFlowElementInvoker(), new LinkedHashMap<>(transitions) );
            }
            else if (flowElement.getFlowComponent() instanceof Sequencer)
            {
                nextFlowElement = new FlowElementImpl(
                        flowElement.getComponentName(),
                        flowElement.getFlowComponent(),
                        flowElement.getFlowElementInvoker(), new LinkedHashMap<>(transitions) );
            }
            else if (flowElement.getFlowComponent() instanceof Producer)
            {
                nextFlowElement = new FlowElementImpl(
                        flowElement.getComponentName(),
                        flowElement.getFlowComponent(),
                        flowElement.getFlowElementInvoker());
            }
            else
            {
                nextFlowElement = new FlowElementImpl(
                        flowElement.getComponentName(),
                        flowElement.getFlowComponent(),
                        flowElement.getFlowElementInvoker(),
                        nextFlowElement);
            }
        }

        return nextFlowElement;
    }

    protected FlowElement connectElements(Route<Flow> route)
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

    protected Flow _build(Route<Flow> _route)
    {
        FlowElement headFlowElement = connectElements(_route);

        if (configurationService == null)
        {
            configurationService = ConfiguredResourceConfigurationService
                    .getDefaultConfigurationService();
        }

        // if resubmissionService not specifically set then check to see if consumer supports ResubmissionService, if so then set it
        if(resubmissionService == null && headFlowElement instanceof ResubmissionService)
        {
            resubmissionService = (ResubmissionService)headFlowElement;
        }

        if (exclusionService == null)
        {
            if(exclusionServiceFactory == null)
            {
                throw new IllegalArgumentException("exclusionServiceFactory cannot be 'null'");
            }

            exclusionService = exclusionServiceFactory.getExclusionService(moduleName, name);
        }

        if (errorReportingService == null)
        {
            errorReportingService = errorReportingServiceFactory.getErrorReportingService();
        }

        if(errorReportingService instanceof ErrorReportingServiceDefaultImpl)
        {
            ((ErrorReportingServiceDefaultImpl)errorReportingService).setModuleName(moduleName);
            ((ErrorReportingServiceDefaultImpl)errorReportingService).setFlowName(name);
        }

        if (recoveryManager == null)
        {
            recoveryManager = this.recoveryManagerFactory.getInstance()
                    .getRecoveryManager(
                            name,
                            moduleName,
                            ((FlowElement<Consumer>) headFlowElement).getFlowComponent(),
                            exclusionService, errorReportingService);
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

        if(name == null)
        {
            throw new IllegalArgumentException("flow name cannot be 'null'");
        }

        if(moduleName == null)
        {
            throw new IllegalArgumentException("module name cannot be 'null'");
        }

        Flow flow = new VisitingInvokerFlow(name, moduleName, flowConfiguration, exclusionFlowConfiguration, recoveryManager, exclusionService, serialiserFactory);
        flow.setFlowListener(flowEventListener);

        // pass handle to the error reporting service if flow needs to be aware of this
        if(flow instanceof IsErrorReportingServiceAware)
        {
            ((IsErrorReportingServiceAware)flow).setErrorReportingService(errorReportingService);
        }

        if(monitor != null && flow instanceof MonitorSubject)
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
                monitor.setFlowName(name);
            }

            ((MonitorSubject)flow).setMonitor(monitor);
        }

        flow.setFlowInvocationContextListeners(flowInvocationContextListeners);

        logger.info("Instantiated flow - name[" + name + "] module[" + moduleName
                + "] with ResubmissionService[" + ((resubmissionService != null) ? resubmissionService.getClass().getSimpleName() : "none")
                + "] with ExclusionService[" + exclusionService.getClass().getSimpleName()
                + "] with ErrorReportingService[" + errorReportingService.getClass().getSimpleName()
                + "] with RecoveryManager[" + recoveryManager.getClass().getSimpleName()
                + "]; ExceptionResolver[" + ((exceptionResolver != null) ? exceptionResolver.getClass().getSimpleName() : "none")
                + "]; Monitor[" + ((monitor != null && flow instanceof MonitorSubject) ? monitor.getClass().getSimpleName() : "none")
                + "]");

        return flow;

    }

    public class PrimaryRouteBuilder {
        Route<Flow> route;

        public PrimaryRouteBuilder(Route route) {
            this.route = route;
            if (route == null) {
                throw new IllegalArgumentException("route cannot be 'null'");
            }
        }

        public PrimaryRouteBuilder converter(String name, Converter converter) {
            this.route.addFlowElement(new FlowElementImpl(name, converter, new ConverterFlowElementInvoker()));
            return this;
        }

        public PrimaryRouteBuilder translator(String name, Translator translator) {
            this.route.addFlowElement(new FlowElementImpl(name, translator, new TranslatorFlowElementInvoker()));
            return this;
        }

        public PrimaryRouteBuilder filter(String name, Filter filter) {
            this.route.addFlowElement(new FlowElementImpl(name, filter, new FilterFlowElementInvoker()));
            return this;
        }

        public Sequence<Flow> sequencer(String name, Sequencer sequencer) {
            this.route.addFlowElement(new FlowElementImpl(name, sequencer, new SequencerFlowElementInvoker()));
            return new PrimarySequenceImpl(route);
        }

        public PrimaryRouteBuilder broker(String name, Broker broker) {
            this.route.addFlowElement(new FlowElementImpl(name, broker, new BrokerFlowElementInvoker()));
            return this;
        }

        public Evaluation<Flow> singleRecipientRouter(String name, SingleRecipientRouter singleRecipientRouter) {
            this.route.addFlowElement(new FlowElementImpl(name, singleRecipientRouter, new SingleRecipientRouterFlowElementInvoker()));
            return new PrimaryEvaluationImpl(route);
        }

        public Evaluation<Flow> multiRecipientRouter(String name, MultiRecipientRouter multiRecipientRouter) {
            this.route.addFlowElement(new FlowElementImpl(name, multiRecipientRouter, new MultiRecipientRouterFlowElementInvoker()));
            return new PrimaryEvaluationImpl(route);
        }

        public Endpoint<Flow> producer(String name, Producer producer) {
            this.route.addFlowElement(new FlowElementImpl(name, producer, new ProducerFlowElementInvoker()));
            return new EndpointImpl();
        }

        class EndpointImpl implements Endpoint<Flow>
        {
            public Flow build()
            {
                return _build(route);
            }
        }
    }

    public class PrimaryEvaluationImpl implements Evaluation<Flow>
    {
        Route route;

        public PrimaryEvaluationImpl(Route route)
        {
            this.route = route;
            if(route == null)
            {
                throw new IllegalArgumentException("route cannot be 'null'");
            }
        }

        public Evaluation<Flow> when(String name, Route evaluatedRoute)
        {
            List<FlowElement> fes = evaluatedRoute.getFlowElements();
            fes.add(0, new FlowElementImpl(this.getClass().getName(), new When(name), null));
            this.route.addNestedRoute(evaluatedRoute);
            return new PrimaryEvaluationImpl(route);
        }

        public Evaluation<Flow> otherwise(Route evaluatedRoute)
        {
            List<FlowElement> fes = evaluatedRoute.getFlowElements();
            fes.add(0, new FlowElementImpl(this.getClass().getName(), new Otherwise(), null));
            this.route.addNestedRoute(evaluatedRoute);
            return new PrimaryEvaluationImpl(route);
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
            List<FlowElement> fes = sequencedRoute.getFlowElements();
            fes.add(0, new FlowElementImpl(this.getClass().getName(), SequentialOrder.to(name), null));
            this.route.addNestedRoute(sequencedRoute);
            return new PrimarySequenceImpl(route);
        }

        public Flow build()
        {
            return _build(route);
        }
    }

}




