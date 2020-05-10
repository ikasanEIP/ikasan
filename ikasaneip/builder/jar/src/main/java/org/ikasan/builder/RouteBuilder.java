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
import org.ikasan.builder.invoker.*;
import org.ikasan.flow.event.DefaultReplicationFactory;
import org.ikasan.flow.visitorPattern.FlowElementImpl;
import org.ikasan.flow.visitorPattern.invoker.*;
import org.ikasan.spec.component.endpoint.Broker;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.component.filter.Filter;
import org.ikasan.spec.component.routing.MultiRecipientRouter;
import org.ikasan.spec.component.routing.SingleRecipientRouter;
import org.ikasan.spec.component.sequencing.Sequencer;
import org.ikasan.spec.component.splitting.Splitter;
import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.component.transformation.Translator;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Default route builder.
 *
 * @author Ikasan Development Team
 */
public class RouteBuilder
{
	Route route;

	public RouteBuilder(Route route)
	{
		this.route = route;
		if(route == null)
		{
			throw new IllegalArgumentException("route cannot be 'null'");
		}
	}

	public RouteBuilder converter(String name, Converter converter)
	{
		this.route.addFlowElement(new FlowElementImpl(name, converter, new ConverterFlowElementInvoker()));
		return this;
	}

	public RouteBuilder converter(String name, Builder<Converter> converterBuilder, InvokerConfiguration converterInvokerConfiguration)
	{
		return this.converter(name, converterBuilder.build(), converterInvokerConfiguration);
	}

	public RouteBuilder converter(String name, Converter converter, InvokerConfiguration converterInvokerConfiguration)
	{
		ConverterFlowElementInvoker converterFlowElementInvoker = new ConverterFlowElementInvoker();
		converterFlowElementInvoker.setConfiguration(converterInvokerConfiguration);
		this.route.addFlowElement(new FlowElementImpl(name, converter, converterFlowElementInvoker));
		return this;
	}

	public RouteBuilder converter(String name, Builder<Converter> converterBuilder, VanillaInvokerConfigurationBuilder converterInvokerConfigurationBuilder)
	{
		return this.converter(name, converterBuilder.build(), converterInvokerConfigurationBuilder.build());
	}

	public RouteBuilder converter(String name, Converter converter, VanillaInvokerConfigurationBuilder converterInvokerConfigurationBuilder)
	{
		return this.converter(name, converter, converterInvokerConfigurationBuilder.build());
	}

	public RouteBuilder converter(String name, Builder<Converter> converterBuilder)
	{
		return this.converter(name, converterBuilder.build());
	}

	public RouteBuilder translator(String name, Translator translator)
	{
		this.route.addFlowElement(new FlowElementImpl(name, translator, new TranslatorFlowElementInvoker()));
		return this;
	}

	public RouteBuilder translator(String name, Builder<Translator> translatorBuilder)
	{
		return this.translator(name, translatorBuilder.build());
	}

	public RouteBuilder translator(String name, Translator translator, TranslatorInvokerConfiguration translatorInvokerConfiguration)
	{
		TranslatorFlowElementInvoker translatorFlowElementInvoker = new TranslatorFlowElementInvoker();
		translatorFlowElementInvoker.setConfiguration(translatorInvokerConfiguration);
		this.route.addFlowElement(new FlowElementImpl(name, translator, translatorFlowElementInvoker));
		return this;
	}

	public RouteBuilder translator(String name, Builder<Translator> translatorBuilder, TranslatorInvokerConfiguration translatorInvokerConfiguration)
	{
		return this.translator(name, translatorBuilder.build(), translatorInvokerConfiguration);
	}

	public RouteBuilder translator(String name, Builder<Translator> translatorBuilder, TranslatorInvokerConfigurationBuilder translatorInvokerConfigurationBuilder)
	{
		return this.translator(name, translatorBuilder.build(), translatorInvokerConfigurationBuilder.build());
	}

	public RouteBuilder translator(String name, Translator translator, TranslatorInvokerConfigurationBuilder translatorInvokerConfigurationBuilder)
	{
		return this.translator(name, translator, translatorInvokerConfigurationBuilder.build());
	}

	public RouteBuilder splitter(String name, Splitter splitter)
	{
		this.route.addFlowElement(new FlowElementImpl(name, splitter, new SplitterFlowElementInvoker()));
		return this;
	}

	public RouteBuilder splitter(String name, Builder<Splitter> splitterBuilder)
	{
		return this.splitter(name, splitterBuilder.build());
	}

	public RouteBuilder splitter(String name, Splitter splitter, SplitterInvokerConfiguration splitterInvokerConfiguration)
	{
		SplitterFlowElementInvoker splitterFlowElementInvoker = new SplitterFlowElementInvoker();
		splitterFlowElementInvoker.setConfiguration(splitterInvokerConfiguration);
		this.route.addFlowElement(new FlowElementImpl(name, splitter, splitterFlowElementInvoker));
		return this;
	}

	public RouteBuilder splitter(String name, Builder<Splitter> splitterBuilder, SplitterInvokerConfiguration splitterInvokerConfiguration)
	{
		return this.splitter(name, splitterBuilder.build(), splitterInvokerConfiguration);
	}

	public RouteBuilder splitter(String name, Builder<Splitter> splitterBuilder, SplitterInvokerConfigurationBuilder splitterInvokerConfigurationBuilder)
	{
		return this.splitter(name, splitterBuilder.build(), splitterInvokerConfigurationBuilder.build());
	}

	public RouteBuilder splitter(String name, Splitter splitter, SplitterInvokerConfigurationBuilder splitterInvokerConfigurationBuilder)
	{
		return this.splitter(name, splitter, splitterInvokerConfigurationBuilder.build());
	}

    public RouteBuilder concurrentSplitter(String name, Splitter splitter)
    {
        // TODO - how to override for nu ber of threads
        ConcurrentSplitterInvokerConfiguration concurrentSplitterInvokerConfiguration = new ConcurrentSplitterInvokerConfiguration();
        ExecutorService executorService = Executors.newFixedThreadPool(concurrentSplitterInvokerConfiguration.getConcurrentThreads());
        this.route.addFlowElement(new FlowElementImpl(name, splitter, new ConcurrentSplitterFlowElementInvoker(executorService)));
        return this;
    }

    public RouteBuilder concurrentSplitter(String name, Builder<Splitter> concurrentSplitterBuilder)
    {
        return this.concurrentSplitter(name, concurrentSplitterBuilder.build());
    }

    public RouteBuilder concurrentSplitter(String name, Splitter splitter, ConcurrentSplitterInvokerConfiguration concurrentSplitterInvokerConfiguration)
    {
        ExecutorService executorService = Executors.newFixedThreadPool(concurrentSplitterInvokerConfiguration.getConcurrentThreads());
        ConcurrentSplitterFlowElementInvoker concurrentSplitterFlowElementInvoker = new ConcurrentSplitterFlowElementInvoker(executorService);
        concurrentSplitterFlowElementInvoker.setConfiguration(concurrentSplitterInvokerConfiguration);
        this.route.addFlowElement(new FlowElementImpl(name, splitter, concurrentSplitterFlowElementInvoker));
        return this;
    }

    public RouteBuilder concurrentSplitter(String name, Builder<Splitter> concurrentSplitterBuilder, ConcurrentSplitterInvokerConfiguration concurrentSplitterInvokerConfiguration)
    {
        return this.concurrentSplitter(name, concurrentSplitterBuilder.build(), concurrentSplitterInvokerConfiguration);
    }

    public RouteBuilder concurrentSplitter(String name, Builder<Splitter> splitterBuilder, ConcurrentSplitterInvokerConfigurationBuilder concurrentSplitterInvokerConfigurationBuilder)
    {
        return this.concurrentSplitter(name, splitterBuilder.build(), concurrentSplitterInvokerConfigurationBuilder.build());
    }

    public RouteBuilder concurrentSplitter(String name, Splitter splitter, ConcurrentSplitterInvokerConfigurationBuilder concurrentSplitterInvokerConfigurationBuilder)
    {
        return this.concurrentSplitter(name, splitter, concurrentSplitterInvokerConfigurationBuilder.build());
    }

    public RouteBuilder filter(String name, Filter filter)
	{
		this.route.addFlowElement(new FlowElementImpl(name, filter, new FilterFlowElementInvoker()));
		return this;
	}

	public RouteBuilder filter(String name, Builder<Filter> filterBuilder)
	{
		return this.filter(name, filterBuilder.build());
	}

	public RouteBuilder filter(String name, Filter filter, FilterInvokerConfiguration filterInvokerConfiguration)
	{
		FilterFlowElementInvoker filterFlowElementInvoker = new  FilterFlowElementInvoker();
		filterFlowElementInvoker.setConfiguration(filterInvokerConfiguration);
		this.route.addFlowElement(new FlowElementImpl(name, filter, filterFlowElementInvoker));
		return this;
	}

	public RouteBuilder filter(String name, Builder<Filter> filterBuilder, FilterInvokerConfiguration filterInvokerConfiguration)
	{
		return this.filter(name, filterBuilder.build(), filterInvokerConfiguration);
	}

	public RouteBuilder filter(String name, Builder<Filter> filterBuilder, FilterInvokerConfigurationBuilder filterInvokerConfigurationBuilder)
	{
		return this.filter(name, filterBuilder.build(), filterInvokerConfigurationBuilder.build());
	}

	public RouteBuilder filter(String name, Filter filter, FilterInvokerConfigurationBuilder filterInvokerConfigurationBuilder)
	{
		return this.filter(name, filter, filterInvokerConfigurationBuilder.build());
	}

	public Sequence<Route> sequencer(String name, Sequencer sequencer)
	{
		this.route.addFlowElement(new FlowElementImpl(name, sequencer, new SequencerFlowElementInvoker()));
		return new SequenceImpl(route);
	}

	public Sequence<Route> sequencer(String name, Builder<Sequencer> sequencerBuilder)
	{
		return this.sequencer(name, sequencerBuilder.build());
	}

	public Sequence<Route> sequencer(String name, Sequencer sequencer, InvokerConfiguration sequencerInvokerConfiguration)
	{
		SequencerFlowElementInvoker sequencerFlowElementInvoker = new SequencerFlowElementInvoker();
		sequencerFlowElementInvoker.setConfiguration(sequencerInvokerConfiguration);
		this.route.addFlowElement(new FlowElementImpl(name, sequencer, sequencerFlowElementInvoker));
		return new SequenceImpl(route);
	}

	public Sequence<Route> sequencer(String name, Builder<Sequencer> sequencerBuilder, InvokerConfiguration sequencerInvokerConfiguration)
	{
		return this.sequencer(name, sequencerBuilder.build(), sequencerInvokerConfiguration);
	}

	public Sequence<Route> sequencer(String name, Builder<Sequencer> sequencerBuilder, VanillaInvokerConfigurationBuilder sequencerInvokerConfigurationBuilder)
	{
		return this.sequencer(name, sequencerBuilder.build(), sequencerInvokerConfigurationBuilder.build());
	}

	public Sequence<Route> sequencer(String name, Sequencer sequencer, VanillaInvokerConfigurationBuilder sequencerInvokerConfigurationBuilder)
	{
		return this.sequencer(name, sequencer, sequencerInvokerConfigurationBuilder.build());
	}

	public RouteBuilder broker(String name, Broker broker)
	{
		this.route.addFlowElement(new FlowElementImpl(name, broker, new BrokerFlowElementInvoker()));
		return this;
	}

	public RouteBuilder broker(String name, Builder<Broker> brokerBuilder)
	{
		return this.broker(name, brokerBuilder.build());
	}

	public RouteBuilder broker(String name, Broker broker, InvokerConfiguration brokerInvokerConfiguration)
	{
		BrokerFlowElementInvoker brokerFlowElementInvoker = new BrokerFlowElementInvoker();
		brokerFlowElementInvoker.setConfiguration(brokerInvokerConfiguration);
		this.route.addFlowElement(new FlowElementImpl(name, broker, brokerFlowElementInvoker));
		return this;
	}

	public RouteBuilder broker(String name, Builder<Broker> brokerBuilder, InvokerConfiguration brokerInvokerConfiguration)
	{
		return this.broker(name, brokerBuilder.build(), brokerInvokerConfiguration);
	}

	public RouteBuilder broker(String name, Builder<Broker> brokerBuilder, VanillaInvokerConfigurationBuilder brokerInvokerConfigurationBuilder)
	{
		return this.broker(name, brokerBuilder.build(), brokerInvokerConfigurationBuilder.build());
	}

	public RouteBuilder broker(String name, Broker broker, VanillaInvokerConfigurationBuilder brokerInvokerConfigurationBuilder)
	{
		return this.broker(name, broker, brokerInvokerConfigurationBuilder.build());
	}

	public EvaluationOtherwise<Route> singleRecipientRouter(String name, SingleRecipientRouter singleRecipientRouter)
	{
		this.route.addFlowElement(new FlowElementImpl(name, singleRecipientRouter, new SingleRecipientRouterFlowElementInvoker()));
		return new EvaluationOtherwiseImpl(route);
	}

	public EvaluationOtherwise<Route> singleRecipientRouter(String name, SingleRecipientRouter singleRecipientRouter, InvokerConfiguration singleRecipientRouterInvokerConfiguration)
	{
		SingleRecipientRouterFlowElementInvoker singleRecipientRouterFlowElementInvoker = new SingleRecipientRouterFlowElementInvoker();
		singleRecipientRouterFlowElementInvoker.setConfiguration(singleRecipientRouterInvokerConfiguration);
		this.route.addFlowElement(new FlowElementImpl(name, singleRecipientRouter, singleRecipientRouterFlowElementInvoker));
		return new EvaluationOtherwiseImpl(route);
	}

	public EvaluationOtherwise<Route> singleRecipientRouter(String name, Builder<SingleRecipientRouter> singleRecipientRouterBuilder)
	{
		return this.singleRecipientRouter(name, singleRecipientRouterBuilder.build());
	}

	public EvaluationOtherwise<Route> singleRecipientRouter(String name, Builder<SingleRecipientRouter> singleRecipientRouterBuilder, InvokerConfiguration singleRecipientRouterInvokerConfiguration)
	{
		return this.singleRecipientRouter(name, singleRecipientRouterBuilder.build(), singleRecipientRouterInvokerConfiguration);
	}

	public EvaluationOtherwise<Route> singleRecipientRouter(String name, SingleRecipientRouter singleRecipientRouter, VanillaInvokerConfigurationBuilder singleRecipientRouterInvokerConfigurationBuilder)
	{
		return this.singleRecipientRouter(name, singleRecipientRouter, singleRecipientRouterInvokerConfigurationBuilder.build());
	}

	public EvaluationOtherwise<Route> singleRecipientRouter(String name, Builder<SingleRecipientRouter> singleRecipientRouterBuilder, VanillaInvokerConfigurationBuilder singleRecipientRouterInvokerConfigurationBuilder)
	{
		return this.singleRecipientRouter(name, singleRecipientRouterBuilder.build(), singleRecipientRouterInvokerConfigurationBuilder.build());
	}

	public EvaluationWhen<Route> multiRecipientRouter(String name, MultiRecipientRouter multiRecipientRouter)
	{
		this.route.addFlowElement(new FlowElementImpl(name, multiRecipientRouter, new MultiRecipientRouterFlowElementInvoker(DefaultReplicationFactory.getInstance(), new MultiRecipientRouterInvokerConfiguration())));
		return new EvaluationWhenImpl(route);
	}

	public EvaluationWhen<Route> multiRecipientRouter(String name, Builder<MultiRecipientRouter> multiRecipientRouterBuilder)
	{
		return this.multiRecipientRouter(name, multiRecipientRouterBuilder.build());
	}

	public EvaluationWhen<Route> multiRecipientRouter(String name, MultiRecipientRouter multiRecipientRouter, MultiRecipientRouterInvokerConfiguration invokerConfiguration)
	{
		MultiRecipientRouterFlowElementInvoker multiRecipientRouterFlowElementInvoker = new MultiRecipientRouterFlowElementInvoker(DefaultReplicationFactory.getInstance(), invokerConfiguration);
		this.route.addFlowElement(new FlowElementImpl(name, multiRecipientRouter, multiRecipientRouterFlowElementInvoker));
		return new EvaluationWhenImpl(route);
	}

	public EvaluationWhen<Route> multiRecipientRouter(String name, Builder<MultiRecipientRouter> multiRecipientRouterBuilder, MultiRecipientRouterInvokerConfiguration invokerConfiguration)
	{
		return this.multiRecipientRouter(name, multiRecipientRouterBuilder.build(), invokerConfiguration);
	}

	public EvaluationWhen<Route> multiRecipientRouter(String name, Builder<MultiRecipientRouter> multiRecipientRouterBuilder, MultiRecipientRouterInvokerConfigurationBuilder invokerConfigurationBuilder)
	{
		return this.multiRecipientRouter(name, multiRecipientRouterBuilder.build(), invokerConfigurationBuilder.build());
	}

	public EvaluationWhen<Route> multiRecipientRouter(String name, MultiRecipientRouter multiRecipientRouter, MultiRecipientRouterInvokerConfigurationBuilder invokerConfigurationBuilder)
	{
		return this.multiRecipientRouter(name, multiRecipientRouter, invokerConfigurationBuilder.build());
	}

	public Route producer(String name, Producer producer)
	{
		this.route.addFlowElement(new FlowElementImpl(name, producer, new ProducerFlowElementInvoker()));
		return this.route;
	}

	public Route producer(String name, Builder<Producer> producerBuilder)
	{
		return this.producer(name, producerBuilder.build());
	}

	public Route producer(String name, Producer producer, InvokerConfiguration producerInvokerConfiguration)
	{
		ProducerFlowElementInvoker producerFlowElementInvoker = new ProducerFlowElementInvoker();
		producerFlowElementInvoker.setConfiguration(producerInvokerConfiguration);
		this.route.addFlowElement( new FlowElementImpl(name, producer, producerFlowElementInvoker) );
		return this.route;
	}

	public Route producer(String name, Builder<Producer> producerBuilder, InvokerConfiguration producerInvokerConfiguration)
	{
		return this.producer(name, producerBuilder.build(), producerInvokerConfiguration);
	}

	public Route producer(String name, Builder<Producer> producerBuilder, VanillaInvokerConfigurationBuilder producerInvokerConfigurationBuilder)
	{
		return this.producer(name, producerBuilder.build(), producerInvokerConfigurationBuilder.build());
	}

	public Route producer(String name, Producer producer, VanillaInvokerConfigurationBuilder producerInvokerConfigurationBuilder)
	{
		return this.producer(name, producer, producerInvokerConfigurationBuilder.build());
	}
}
