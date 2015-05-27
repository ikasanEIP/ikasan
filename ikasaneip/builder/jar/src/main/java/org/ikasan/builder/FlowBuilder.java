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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.ikasan.builder.FlowBuilder.FlowConfigurationBuilder.RouterRootConfigurationBuilder.Otherwise;
import org.ikasan.builder.FlowBuilder.FlowConfigurationBuilder.RouterRootConfigurationBuilder.When;
import org.ikasan.builder.FlowBuilder.FlowConfigurationBuilder.SequencerRootConfigurationBuilder.Sequence;
import org.ikasan.component.endpoint.util.producer.LogProducer;
import org.ikasan.configurationService.service.ConfiguredResourceConfigurationService;
import org.ikasan.error.reporting.service.ErrorReportingServiceDefaultImpl;
import org.ikasan.error.reporting.service.ErrorReportingServiceFactoryDefaultImpl;
import org.ikasan.exceptionResolver.ExceptionResolver;
import org.ikasan.flow.visitorPattern.DefaultExclusionFlowConfiguration;
import org.ikasan.exclusion.service.ExclusionServiceFactory;
import org.ikasan.flow.event.DefaultReplicationFactory;
import org.ikasan.flow.event.FlowEventFactory;
import org.ikasan.flow.visitorPattern.*;
import org.ikasan.flow.visitorPattern.invoker.*;
import org.ikasan.recovery.RecoveryManagerFactory;
import org.ikasan.spec.component.endpoint.Broker;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.component.routing.MultiRecipientRouter;
import org.ikasan.spec.component.routing.Router;
import org.ikasan.spec.component.routing.SingleRecipientRouter;
import org.ikasan.spec.component.sequencing.Sequencer;
import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.component.transformation.Translator;
import org.ikasan.spec.configuration.ConfigurationService;
import org.ikasan.spec.error.reporting.ErrorReportingService;
import org.ikasan.spec.error.reporting.ErrorReportingServiceFactory;
import org.ikasan.spec.event.EventFactory;
import org.ikasan.spec.exclusion.ExclusionService;
import org.ikasan.spec.flow.*;
import org.ikasan.spec.monitor.Monitor;
import org.ikasan.spec.monitor.MonitorSubject;
import org.ikasan.spec.recovery.RecoveryManager;

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
	FlowEventListener flowEventListener;

	/** flow recovery manager instance */
	RecoveryManager recoveryManager;

    /** exception resolver to be registered with recovery manager */
    ExceptionResolver exceptionResolver;

	/** configuration service */
	ConfigurationService configurationService;

    /** exclusion service factory */
    ExclusionServiceFactory exclusionServiceFactory;

    /** exclusion service */
    ExclusionService exclusionService;

    /** error reporting service factory */
    ErrorReportingServiceFactory errorReportingServiceFactory = new ErrorReportingServiceFactoryDefaultImpl();

    /** error reporting service */
    ErrorReportingService errorReportingService;

    /** flow monitor */
    Monitor monitor;

    /** flow element wiriing */
	FlowConfigurationBuilder flowConfigurationBuilder;

	/** default event factory */
	EventFactory eventFactory = new FlowEventFactory();

    /** head flow element of the exclusion flow */
    FlowElement<?> exclusionFlowHeadElement;

    /**
	 * Constructor
	 * 
	 * @param name
	 */
	public FlowBuilder(String name, String moduleName) 
	{
		this.name = name;
		if (name == null) 
		{
			throw new IllegalArgumentException("Flow name cannot be 'null'");
		}

		this.moduleName = moduleName;
		if (moduleName == null) 
		{
			throw new IllegalArgumentException(
					"Flow module name cannot be 'null'");
		}
	}

	/**
	 * Get a flow builder
	 * 
	 * @param name
	 * @return
	 */
	public static FlowBuilder newFlow(String name, String moduleName) 
	{
		return new FlowBuilder(name, moduleName);
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
	 * @param recoveryManager
	 * @return
	 */
	public FlowBuilder withRecoveryManager(RecoveryManager recoveryManager) 
	{
		this.recoveryManager = recoveryManager;
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
     * Setter for monitor
     * @param monitor
     */
    public void setMonitor(Monitor monitor)
    {
        this.monitor = monitor;
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

	/**
	 * Add a consumer
	 * 
	 * @param name
	 * @param consumer
	 * @return
	 */
	public FlowConfigurationBuilder consumer(String name, Consumer consumer) 
	{
		return new FlowConfigurationBuilder(this, name, consumer);
	}

	/**
	 * /////////////////////////////////////////////////////////////////////////
	 * ////////////////////////////////////// Class for wiring the components
	 * together
	 * 
	 * @author Ikasan Development Team
	 * 
	 */
	public class FlowConfigurationBuilder
	{
		// keep hold of the flowBuilder isntance
		FlowBuilder flowBuilder;
		
		// flow elements being configured
		List<FlowElement> flowElements = new ArrayList<FlowElement>();

		// last element specified
		FlowElement lastFlowElement;

        /** allow FE's to have their invoker behaviour configured */
        Object flowElementInvokerConfiguration;

        /**
		 * Constructor
		 * @param flowBuilder
		 * @param name
		 * @param consumer
		 */
        private FlowConfigurationBuilder(FlowBuilder flowBuilder, String name,
                Consumer consumer) 
        {
            this.flowBuilder = flowBuilder;
            if(flowBuilder == null)
            {
                throw new IllegalArgumentException("flowBuilder cannot be 'null'");
            }
            
            this.flowElements.add(new FlowElementImpl(name, consumer, new ConsumerFlowElementInvoker()));
        }

		public FlowConfigurationBuilder broker(String name, Broker broker) 
		{
			this.flowElements.add(new FlowElementImpl(name, broker, new BrokerFlowElementInvoker()));
			return this;
		}

		public FlowProducerTerminator publisher(String name, Producer producer) 
		{
			this.flowElements.add(new FlowElementImpl(name, producer, new ProducerFlowElementInvoker()));
			return new FlowProducerTerminator();
		}

		public FlowConfigurationBuilder translater(String name, Translator translator) 
		{
			this.flowElements.add(new FlowElementImpl(name, translator, new TranslatorFlowElementInvoker()));
			return this;
		}

        public FlowConfigurationBuilder converter(String name, Converter converter) 
        {
            this.flowElements.add(new FlowElementImpl(name, converter, new ConverterFlowElementInvoker()));
            return this;
        }

		public SequencerRootConfigurationBuilder sequencer(String name, Sequencer sequencer) 
		{
			this.flowElements.add(new FlowElementImpl(name, sequencer, new SequencerFlowElementInvoker()));
			return new SequencerRootConfigurationBuilder(this);
		}

		public RouterRootConfigurationBuilder router(String name, Router router) 
		{
            if(flowElementInvokerConfiguration == null)
            {
                flowElementInvokerConfiguration = new MultiRecipientRouterConfiguration();
            }
            else
            {
                if( !(flowElementInvokerConfiguration instanceof MultiRecipientRouterConfiguration) )
                {
                    throw new IllegalArgumentException("Invalid MultiRecipientRouter FlowInvoker Configuration. Requires MultiRecipientRouterConfiguration, but found " + flowElementInvokerConfiguration.getClass().getName());
                }
            }

            this.flowElements.add(new FlowElementImpl(name, router, new MultiRecipientRouterFlowElementInvoker( DefaultReplicationFactory.getInstance(), (MultiRecipientRouterConfiguration)flowElementInvokerConfiguration )));
			return new RouterRootConfigurationBuilder(this);
		}

        public RouterRootConfigurationBuilder singleRecipientRouter(String name, SingleRecipientRouter router)
        {
            this.flowElements.add(new FlowElementImpl(name, router, new SingleRecipientRouterFlowElementInvoker()));
            return new RouterRootConfigurationBuilder(this);
        }

        public RouterRootConfigurationBuilder multiRecipientRouter(String name, MultiRecipientRouter router)
        {
            if(flowElementInvokerConfiguration == null)
            {
                flowElementInvokerConfiguration = new MultiRecipientRouterConfiguration();
            }
            else
            {
                if( !(flowElementInvokerConfiguration instanceof MultiRecipientRouterConfiguration) )
                {
                    throw new IllegalArgumentException("Invalid MultiRecipientRouter FlowInvoker Configuration. Requires MultiRecipientRouterConfiguration, but found " + flowElementInvokerConfiguration.getClass().getName());
                }
            }

            this.flowElements.add(new FlowElementImpl(name, router, new MultiRecipientRouterFlowElementInvoker( DefaultReplicationFactory.getInstance(), (MultiRecipientRouterConfiguration)flowElementInvokerConfiguration )));
            return new RouterRootConfigurationBuilder(this);
        }

        /**
		 * /////////////////////////////////////////////////////////////////////
		 * /////////////////////////////////////////////////// Sequencer wiring
		 * 
		 * @author Ikasan Development Team
		 * 
		 */
		public class SequencerRootConfigurationBuilder 
		{
			// keep a handle on the flowConfigurationBuilder
			FlowConfigurationBuilder flowConfigurationBuilder;

			/**
			 * Constructor
			 * @param flowConfigurationBuilder
			 */
			public SequencerRootConfigurationBuilder(FlowConfigurationBuilder flowConfigurationBuilder) 
			{
				this.flowConfigurationBuilder = flowConfigurationBuilder;
			}

			public SequencerConfigurationBuilder sequence(String sequenceName) 
			{
				flowElements.add(new FlowElementImpl(sequenceName, new Sequence(), null));
				return new SequencerConfigurationBuilder(this, flowConfigurationBuilder);
			}

			public SequencerConfigurationBuilder sequence() 
			{
				flowElements.add(new FlowElementImpl("sequence-"  + flowElements.size(), new Sequence(), null));
                return new SequencerConfigurationBuilder(this, flowConfigurationBuilder);
			}

			public class Sequence 
			{
			}
		}

        /**
         * /////////////////////////////////////////////////////////////////////
         * /////////////////////////////////////////////////// Sequencer wiring
         * 
         * @author Ikasan Development Team
         * 
         */
        public class SequencerConfigurationBuilder
        {
            SequencerRootConfigurationBuilder sequencerRootConfigurationBuilder;
            
            // keep a handle on the flowConfigurationBuilder
            FlowConfigurationBuilder flowConfigurationBuilder;

            /**
             * Constructor
             * @param flowConfigurationBuilder
             */
            public SequencerConfigurationBuilder(SequencerRootConfigurationBuilder sequencerRootConfigurationBuilder, FlowConfigurationBuilder flowConfigurationBuilder) 
            {
                this.sequencerRootConfigurationBuilder = sequencerRootConfigurationBuilder;
                this.flowConfigurationBuilder = flowConfigurationBuilder;
            }

            public SequencerProducerTerminator publisher(String name, Producer producer) 
            {
                flowElements.add(new FlowElementImpl(name, producer, new ProducerFlowElementInvoker()));
                return new SequencerProducerTerminator(sequencerRootConfigurationBuilder);
            }

            public SequencerConfigurationBuilder broker(String name, Broker broker) 
            {
                flowElements.add(new FlowElementImpl(name, broker, new BrokerFlowElementInvoker()));
                return this;
            }

            public SequencerConfigurationBuilder translater(String name, Translator translator) 
            {
                flowElements.add(new FlowElementImpl(name, translator, new TranslatorFlowElementInvoker()));
                return this;
            }

            public SequencerConfigurationBuilder converter(String name, Converter converter) 
            {
                flowElements.add(new FlowElementImpl(name, converter, new ConverterFlowElementInvoker()));
                return this;
            }

            public SequencerConfigurationBuilder sequencer(String name, Sequencer sequencer) 
            {
                flowElements.add(new FlowElementImpl(name, sequencer, new SequencerFlowElementInvoker()));
                return this;
            }

            public RouterRootConfigurationBuilder router(String name, Router router) 
            {
                if(flowElementInvokerConfiguration == null)
                {
                    flowElementInvokerConfiguration = new MultiRecipientRouterConfiguration();
                }
                else
                {
                    if( !(flowElementInvokerConfiguration instanceof MultiRecipientRouterConfiguration) )
                    {
                        throw new IllegalArgumentException("Invalid MultiRecipientRouter FlowInvoker Configuration. Requires MultiRecipientRouterConfiguration, but found " + flowElementInvokerConfiguration.getClass().getName());
                    }
                }

                flowElements.add(new FlowElementImpl(name, router, new MultiRecipientRouterFlowElementInvoker(DefaultReplicationFactory.getInstance(), (MultiRecipientRouterConfiguration)flowElementInvokerConfiguration )));
                return new RouterRootConfigurationBuilder(flowConfigurationBuilder);
            }

            public RouterRootConfigurationBuilder multiRecipientRouter(String name, MultiRecipientRouter router)
            {
                if(flowElementInvokerConfiguration == null)
                {
                    flowElementInvokerConfiguration = new MultiRecipientRouterConfiguration();
                }
                else
                {
                    if( !(flowElementInvokerConfiguration instanceof MultiRecipientRouterConfiguration) )
                    {
                        throw new IllegalArgumentException("Invalid MultiRecipientRouter FlowInvoker Configuration. Requires MultiRecipientRouterConfiguration, but found " + flowElementInvokerConfiguration.getClass().getName());
                    }
                }

                flowElements.add(new FlowElementImpl(name, router, new MultiRecipientRouterFlowElementInvoker(DefaultReplicationFactory.getInstance(), (MultiRecipientRouterConfiguration)flowElementInvokerConfiguration )));
                return new RouterRootConfigurationBuilder(flowConfigurationBuilder);
            }

            public RouterRootConfigurationBuilder singleRecipientRouter(String name, SingleRecipientRouter router)
            {
                flowElements.add(new FlowElementImpl(name, router, new SingleRecipientRouterFlowElementInvoker()));
                return new RouterRootConfigurationBuilder(flowConfigurationBuilder);
            }
        }

		/**
		 * /////////////////////////////////////////////////////////////////////
		 * /////////////////////////////////////////////////// router wiring
		 * 
		 * @author Ikasan Development Team
		 * 
		 */
		public class RouterRootConfigurationBuilder 
		{
			// keep a handle on the flowConfigurationBuilder
			FlowConfigurationBuilder flowConfigurationBuilder;

			FlowElement routerFlowElement;

			public RouterRootConfigurationBuilder(FlowConfigurationBuilder flowConfigurationBuilder) 
			{
				this.flowConfigurationBuilder = flowConfigurationBuilder;
			}

			public RouterConfigurationBuilder when(String value) 
			{
				flowElements.add(new FlowElementImpl("when", new When(value), null));
				return new RouterConfigurationBuilder(this, flowConfigurationBuilder);
			}

			public FlowConfigurationBuilder otherise() 
			{
				flowElements.add(new FlowElementImpl("otherwise", new Otherwise(), null));
                return flowConfigurationBuilder;
			}

			public class When 
			{
				String result;

				public When(String result) 
				{
					this.result = result;
				}

				public String getResult() 
				{
					return this.result;
				}
			}

			public class Otherwise 
			{
				String result = Router.DEFAULT_RESULT;

				public String getResult() 
				{
					return this.result;
				}
			}
		}

        public class RouterConfigurationBuilder 
        {
            RouterRootConfigurationBuilder routerRootConfigurationBuilder;
            
            // keep a handle on the flowConfigurationBuilder
            FlowConfigurationBuilder flowConfigurationBuilder;

            FlowElement routerFlowElement;

            public RouterConfigurationBuilder(RouterRootConfigurationBuilder routerRootConfigurationBuilder, FlowConfigurationBuilder flowConfigurationBuilder) 
            {
                this.routerRootConfigurationBuilder = routerRootConfigurationBuilder;
                this.flowConfigurationBuilder = flowConfigurationBuilder;
            }

            public RouterRootConfigurationBuilder publisher(String name, Producer producer) 
            {
                flowElements.add(new FlowElementImpl(name, producer, new ProducerFlowElementInvoker()));
                return this.routerRootConfigurationBuilder;
            }

            public RouterConfigurationBuilder broker(String name, Broker broker) 
            {
                flowElements.add(new FlowElementImpl(name, broker, new BrokerFlowElementInvoker()));
                return this;
            }

            public RouterConfigurationBuilder translater(String name, Translator translator) 
            {
                flowElements.add(new FlowElementImpl(name, translator, new TranslatorFlowElementInvoker()));
                return this;
            }

            public RouterConfigurationBuilder converter(String name, Converter converter) 
            {
                flowElements.add(new FlowElementImpl(name, converter, new ConverterFlowElementInvoker()));
                return this;
            }

            public SequencerRootConfigurationBuilder sequencer(String name, Sequencer sequencer)
            {
                flowElements.add(new FlowElementImpl(name, sequencer, new SequencerFlowElementInvoker()));
                return new SequencerRootConfigurationBuilder(flowConfigurationBuilder);
            }

            public RouterRootConfigurationBuilder router(String name, Router router) 
            {
                if(flowElementInvokerConfiguration == null)
                {
                    flowElementInvokerConfiguration = new MultiRecipientRouterConfiguration();
                }
                else
                {
                    if( !(flowElementInvokerConfiguration instanceof MultiRecipientRouterConfiguration) )
                    {
                        throw new IllegalArgumentException("Invalid MultiRecipientRouter FlowInvoker Configuration. Requires MultiRecipientRouterConfiguration, but found " + flowElementInvokerConfiguration.getClass().getName());
                    }
                }

                flowElements.add(new FlowElementImpl(name, router, new MultiRecipientRouterFlowElementInvoker(DefaultReplicationFactory.getInstance(), (MultiRecipientRouterConfiguration)flowElementInvokerConfiguration)));
                return new RouterRootConfigurationBuilder(flowConfigurationBuilder);
            }

            public RouterRootConfigurationBuilder multiRecipientRouter(String name, MultiRecipientRouter router)
            {
                if(flowElementInvokerConfiguration == null)
                {
                    flowElementInvokerConfiguration = new MultiRecipientRouterConfiguration();
                }
                else
                {
                    if( !(flowElementInvokerConfiguration instanceof MultiRecipientRouterConfiguration) )
                    {
                        throw new IllegalArgumentException("Invalid MultiRecipientRouter FlowInvoker Configuration. Requires MultiRecipientRouterConfiguration, but found " + flowElementInvokerConfiguration.getClass().getName());
                    }
                }

                flowElements.add(new FlowElementImpl(name, router, new MultiRecipientRouterFlowElementInvoker(DefaultReplicationFactory.getInstance(), (MultiRecipientRouterConfiguration)flowElementInvokerConfiguration)));
                return new RouterRootConfigurationBuilder(flowConfigurationBuilder);
            }

            public RouterRootConfigurationBuilder singleRecipientRouter(String name, SingleRecipientRouter router)
            {
                flowElements.add(new FlowElementImpl(name, router, new SingleRecipientRouterFlowElementInvoker()));
                return new RouterRootConfigurationBuilder(flowConfigurationBuilder);
            }
        }

		public class FlowProducerTerminator 
		{

			public Flow build() 
			{
				int count = flowElements.size();
				FlowElement nextFlowElement = null;
				Map<String, FlowElement> transitions = new HashMap<String, FlowElement>();

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
					else if (flowElement.getFlowComponent() instanceof When)
					{
						transitions.put(((When) flowElement.getFlowComponent())
								.getResult(), nextFlowElement);
					} 
					else if (flowElement.getFlowComponent() instanceof Sequence) 
					{
						transitions.put(flowElement.getComponentName(), nextFlowElement);
					} 
					else if (flowElement.getFlowComponent() instanceof Otherwise) 
					{
						transitions.put(((Otherwise) flowElement
								.getFlowComponent()).getResult(),
								nextFlowElement);
					}
					else if (flowElement.getFlowComponent() instanceof Router) 
					{
						nextFlowElement = new FlowElementImpl(
								flowElement.getComponentName(),
								flowElement.getFlowComponent(),
                                flowElement.getFlowElementInvoker(), new HashMap<String,FlowElement>(transitions) );
						transitions.clear();
					}
                    else if (flowElement.getFlowComponent() instanceof MultiRecipientRouter)
                    {
                        nextFlowElement = new FlowElementImpl(
                                flowElement.getComponentName(),
                                flowElement.getFlowComponent(),
                                flowElement.getFlowElementInvoker(), new HashMap<String,FlowElement>(transitions) );
                        transitions.clear();
                    }
                    else if (flowElement.getFlowComponent() instanceof SingleRecipientRouter)
                    {
                        nextFlowElement = new FlowElementImpl(
                                flowElement.getComponentName(),
                                flowElement.getFlowComponent(),
                                flowElement.getFlowElementInvoker(), new HashMap<String,FlowElement>(transitions) );
                        transitions.clear();
                    }
                    else if (flowElement.getFlowComponent() instanceof Sequencer)
					{
						nextFlowElement = new FlowElementImpl(
								flowElement.getComponentName(),
								flowElement.getFlowComponent(),
                                flowElement.getFlowElementInvoker(), new HashMap<String,FlowElement>(transitions) );
						transitions.clear();
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

				if (configurationService == null) 
				{
					configurationService = ConfiguredResourceConfigurationService
							.getDefaultConfigurationService();
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
					recoveryManager = RecoveryManagerFactory.getInstance()
							.getRecoveryManager(
									name,
									moduleName,
									((FlowElement<Consumer>) nextFlowElement)
											.getFlowComponent(),
                                    exclusionService, errorReportingService);
				}

                if(exceptionResolver != null)
                {
                    recoveryManager.setResolver(exceptionResolver);
                }

                FlowConfiguration flowConfiguration = new DefaultFlowConfiguration(nextFlowElement, configurationService);

                ExclusionFlowConfiguration exclusionFlowConfiguration = null;
                if(exclusionFlowHeadElement != null)
                {
                    exclusionFlowConfiguration = new DefaultExclusionFlowConfiguration(exclusionFlowHeadElement, configurationService);
                }

                Flow flow = new VisitingInvokerFlow(name, moduleName, flowConfiguration, exclusionFlowConfiguration, recoveryManager, exclusionService);
                flow.setFlowListener(flowEventListener);

                if(monitor != null && flow instanceof MonitorSubject)
                {
                    if(monitor.getEnvironment() == null)
                    {
                        monitor.setEnvironment("Undefined Environment");
                    }

                    if(monitor.getName() == null)
                    {
                        monitor.setName("Module[" + moduleName + "] Flow[" + name + "]");
                    }

                    ((MonitorSubject)flow).setMonitor(monitor);
                }

                logger.info("Instantiated flow - name[" + name + "] module[" + moduleName
                        + "] with ExclusionService[" + exclusionService.getClass().getSimpleName()
                        + "] with ErrorReportingService[" + errorReportingService.getClass().getSimpleName()
                        + "] with RecoveryManager[" + recoveryManager.getClass().getSimpleName()
                        + "]; ExceptionResolver[" + ((exceptionResolver != null) ? exceptionResolver.getClass().getSimpleName() : "none")
                        + "]; Monitor[" + ((monitor != null && flow instanceof MonitorSubject) ? monitor.getClass().getSimpleName() : "none")
                        + "]");

                return flow;
			}
		}

		public class SequencerProducerTerminator extends FlowProducerTerminator
		{
		    SequencerRootConfigurationBuilder sequencerRootConfigurationBuilder;
			
			public SequencerProducerTerminator(SequencerRootConfigurationBuilder sequencerRootConfigurationBuilder)
			{
				this.sequencerRootConfigurationBuilder = sequencerRootConfigurationBuilder;
			}
			
			public Flow build()
			{
				return super.build();
			}
			
			public SequencerConfigurationBuilder sequence()
			{
				return this.sequencerRootConfigurationBuilder.sequence();
			}

			public SequencerConfigurationBuilder sequence(String sequenceName)
			{
				return this.sequencerRootConfigurationBuilder.sequence(sequenceName);
			}
		}
	}
}
