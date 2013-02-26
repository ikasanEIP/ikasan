/* 
 * $Id: SimpleModule.java 3875 2012-05-31 14:42:20Z mitcje $
 * $URL: https://open.jira.com/svn/IKASAN/branches/ikasaneip-0.9.x/module/src/main/java/org/ikasan/module/SimpleModule.java $
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

import org.ikasan.builder.FlowBuilder.FlowConfigurationBuilder.RouterConfigurationBuilder.Otherwise;
import org.ikasan.builder.FlowBuilder.FlowConfigurationBuilder.RouterConfigurationBuilder.When;
import org.ikasan.builder.FlowBuilder.FlowConfigurationBuilder.SequencerConfigurationBuilder.Sequence;
import org.ikasan.configurationService.service.ConfiguredResourceConfigurationService;
import org.ikasan.flow.event.DefaultReplicationFactory;
import org.ikasan.flow.visitorPattern.DefaultFlowConfiguration;
import org.ikasan.flow.visitorPattern.FlowConfiguration;
import org.ikasan.flow.visitorPattern.FlowElementImpl;
import org.ikasan.flow.visitorPattern.VisitingFlowElementInvoker;
import org.ikasan.flow.visitorPattern.VisitingInvokerFlow;
import org.ikasan.recovery.RecoveryManagerFactory;
import org.ikasan.spec.component.endpoint.Broker;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.component.routing.Router;
import org.ikasan.spec.component.sequencing.Sequencer;
import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.component.transformation.Translator;
import org.ikasan.spec.configuration.ConfigurationService;
import org.ikasan.spec.event.EventFactory;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.flow.FlowElement;
import org.ikasan.spec.flow.FlowElementInvoker;
import org.ikasan.spec.flow.FlowEventListener;
import org.ikasan.spec.recovery.RecoveryManager;

/**
 * A simple Flow builder.
 * 
 * @author Ikasan Development Team
 */
public class FlowBuilder 
{
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

	/** configuration service */
	ConfigurationService configurationService;

	/** flow element wiriing */
	FlowConfigurationBuilder flowConfigurationBuilder;

	/** default event factory */
	EventFactory eventFactory;

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
	 * @param description
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
	 * @param description
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
			
			this.flowElements.add(new FlowElementImpl(name, consumer));
		}

		public FlowConfigurationBuilder broker(String name, Broker broker) 
		{
			this.flowElements.add(new FlowElementImpl(name, broker));
			return this;
		}

		public FlowProducerTerminator publisher(String name, Producer producer) 
		{
			this.flowElements.add(new FlowElementImpl(name, producer));
			return new FlowProducerTerminator();
		}

		public FlowConfigurationBuilder translater(String name, Translator translator) 
		{
			this.flowElements.add(new FlowElementImpl(name, translator));
			return this;
		}

		public FlowConfigurationBuilder converter(String name, Converter converter) 
		{
			this.flowElements.add(new FlowElementImpl(name, converter));
			return this;
		}

		public SequencerConfigurationBuilder sequencer(String name, Sequencer sequencer) 
		{
			this.flowElements.add(new FlowElementImpl(name, sequencer));
			return new SequencerConfigurationBuilder(this);
		}

		public RouterConfigurationBuilder router(String name, Router router) 
		{
			this.flowElements.add(new FlowElementImpl(name, router));
			return new RouterConfigurationBuilder(this);
		}

		/**
		 * /////////////////////////////////////////////////////////////////////
		 * /////////////////////////////////////////////////// Sequencer wiring
		 * 
		 * @author Ikasan Developmnet Team
		 * 
		 */
		public class SequencerConfigurationBuilder 
		{
			// keep a handle on the flowConfigurationBuilder
			FlowConfigurationBuilder flowConfigurationBuilder;

			/**
			 * Constructor
			 * @param flowConfigurationBuilder
			 */
			public SequencerConfigurationBuilder(FlowConfigurationBuilder flowConfigurationBuilder) 
			{
				this.flowConfigurationBuilder = flowConfigurationBuilder;
			}

			public SequencerConfigurationBuilder sequence(String sequenceName) 
			{
				flowElements.add(new FlowElementImpl(sequenceName, new Sequence()));
				return this;
			}

			public SequencerConfigurationBuilder sequence() 
			{
				flowElements.add(new FlowElementImpl("sequence-"  + flowElements.size(), new Sequence()));
				return this;
			}

			public SequencerProducerTerminator publisher(String name, Producer producer) 
			{
				flowElements.add(new FlowElementImpl(name, producer));
				return new SequencerProducerTerminator(this);
			}

			public SequencerConfigurationBuilder broker(String name, Broker broker) 
			{
				flowElements.add(new FlowElementImpl(name, broker));
				return this;
			}

			public SequencerConfigurationBuilder translater(String name, Translator translator) 
			{
				flowElements.add(new FlowElementImpl(name, translator));
				return this;
			}

			public SequencerConfigurationBuilder converter(String name, Converter converter) 
			{
				flowElements.add(new FlowElementImpl(name, converter));
				return this;
			}

			public SequencerConfigurationBuilder sequencer(String name, Sequencer sequencer) 
			{
				flowElements.add(new FlowElementImpl(name, sequencer));
				return this;
			}

			public RouterConfigurationBuilder router(String name, Router router) 
			{
				flowElements.add(new FlowElementImpl(name, router));
				return new RouterConfigurationBuilder(flowConfigurationBuilder);
			}

			public class Sequence 
			{
			}

		}

		/**
		 * /////////////////////////////////////////////////////////////////////
		 * /////////////////////////////////////////////////// Sequencer wiring
		 * 
		 * @author Ikasan Developmnet Team
		 * 
		 */
		public class RouterConfigurationBuilder 
		{
			// keep a handle on the flowConfigurationBuilder
			FlowConfigurationBuilder flowConfigurationBuilder;

			FlowElement routerFlowElement;

			public RouterConfigurationBuilder(FlowConfigurationBuilder flowConfigurationBuilder) 
			{
				this.flowConfigurationBuilder = flowConfigurationBuilder;
			}

			public RouterConfigurationBuilder when(String value) 
			{
				flowElements.add(new FlowElementImpl("when", new When(value)));
				return this;
			}

			public FlowConfigurationBuilder otherise() 
			{
				flowElements.add(new FlowElementImpl("otherwise", new Otherwise()));
				return this.flowConfigurationBuilder;
			}

			public RouterConfigurationBuilder publisher(String name, Producer producer) 
			{
				flowElements.add(new FlowElementImpl(name, producer));
				return this;
			}

			public RouterConfigurationBuilder broker(String name, Broker broker) 
			{
				flowElements.add(new FlowElementImpl(name, broker));
				return this;
			}

			public RouterConfigurationBuilder translater(String name, Translator translator) 
			{
				flowElements.add(new FlowElementImpl(name, translator));
				return this;
			}

			public RouterConfigurationBuilder converter(String name, Converter converter) 
			{
				flowElements.add(new FlowElementImpl(name, converter));
				return this;
			}

			public SequencerConfigurationBuilder sequencer(String name, Sequencer sequencer) 
			{
				flowElements.add(new FlowElementImpl(name, sequencer));
				return new SequencerConfigurationBuilder(flowConfigurationBuilder);
			}

			public RouterConfigurationBuilder router(String name, Router router) 
			{
				flowElements.add(new FlowElementImpl(name, router));
				return new RouterConfigurationBuilder(flowConfigurationBuilder);
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
					if (flowElement.getFlowComponent() instanceof Producer) 
					{
						((Consumer)flowElement).setEventFactory(eventFactory);
						nextFlowElement = flowElement;
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
								flowElement.getFlowComponent(), new HashMap<String,FlowElement>(transitions) );
						transitions.clear();
					}
					else if (flowElement.getFlowComponent() instanceof Sequencer) 
					{
						nextFlowElement = new FlowElementImpl(
								flowElement.getComponentName(),
								flowElement.getFlowComponent(), new HashMap<String,FlowElement>(transitions) );
						transitions.clear();
					}
					else if (!(flowElement.getFlowComponent() instanceof Producer)) 
					{
						nextFlowElement = new FlowElementImpl(
								flowElement.getComponentName(),
								flowElement.getFlowComponent(), nextFlowElement);
					}
				}

				if (configurationService == null) 
				{
					configurationService = ConfiguredResourceConfigurationService
							.getDefaultConfigurationService();
				}

				if (recoveryManager == null) 
				{
					recoveryManager = RecoveryManagerFactory.getInstance()
							.getRecoveryManager(
									name,
									moduleName,
									((FlowElement<Consumer>) nextFlowElement)
											.getFlowComponent());
				}

				FlowElementInvoker flowElementInvoker = new VisitingFlowElementInvoker(DefaultReplicationFactory.getInstance());
				flowElementInvoker.setFlowEventListener(flowEventListener);
				FlowConfiguration flowConfiguration = new DefaultFlowConfiguration(nextFlowElement, configurationService);
				return new VisitingInvokerFlow(name, moduleName,flowConfiguration, flowElementInvoker, recoveryManager);
			}
		}

		public class SequencerProducerTerminator extends FlowProducerTerminator
		{
			SequencerConfigurationBuilder sequencerConfigurationBuilder;
			
			public SequencerProducerTerminator(SequencerConfigurationBuilder sequencerConfigurationBuilder)
			{
				this.sequencerConfigurationBuilder = sequencerConfigurationBuilder;
			}
			
			public Flow build()
			{
				return super.build();
			}
			
			public SequencerConfigurationBuilder sequence()
			{
				return this.sequencerConfigurationBuilder.sequence();
			}

			public SequencerConfigurationBuilder sequence(String sequenceName)
			{
				return this.sequencerConfigurationBuilder.sequence(sequenceName);
			}
		}
	}
}
