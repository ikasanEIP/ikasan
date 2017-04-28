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

import org.ikasan.flow.visitorPattern.FlowElementImpl;
import org.ikasan.flow.visitorPattern.invoker.*;
import org.ikasan.spec.component.endpoint.Broker;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.component.filter.Filter;
import org.ikasan.spec.component.routing.MultiRecipientRouter;
import org.ikasan.spec.component.routing.SingleRecipientRouter;
import org.ikasan.spec.component.sequencing.Sequencer;
import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.component.transformation.Translator;

/**
 * Default route builder.
 *
 * @author Ikasan Development Team
 */
public class RouteBuilder
{
	Route<Route> route;

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

	public RouteBuilder translator(String name, Translator translator)
	{
		this.route.addFlowElement(new FlowElementImpl(name, translator, new TranslatorFlowElementInvoker()));
		return this;
	}

	public RouteBuilder filter(String name, Filter filter)
	{
		this.route.addFlowElement(new FlowElementImpl(name, filter, new FilterFlowElementInvoker()));
		return this;
	}

	public Sequence<Route> sequencer(String name, Sequencer sequencer)
	{
		this.route.addFlowElement(new FlowElementImpl(name, sequencer, new SequencerFlowElementInvoker()));
		return new SequenceImpl(route);
	}

	public RouteBuilder broker(String name, Broker broker)
	{
		this.route.addFlowElement(new FlowElementImpl(name, broker, new BrokerFlowElementInvoker()));
		return this;
	}

	public Evaluation<Route> singleRecipientRouter(String name, SingleRecipientRouter singleRecipientRouter)
	{
		this.route.addFlowElement(new FlowElementImpl(name, singleRecipientRouter, new SingleRecipientRouterFlowElementInvoker()));
		return new EvaluationImpl(route);
	}

	public Evaluation<Route> multiRecipientRouter(String name, MultiRecipientRouter multiRecipientRouter)
	{
		this.route.addFlowElement(new FlowElementImpl(name, multiRecipientRouter, new MultiRecipientRouterFlowElementInvoker()));
		return new EvaluationImpl(route);
	}

	public Route producer(String name, Producer producer)
	{
		this.route.addFlowElement(new FlowElementImpl(name, producer, new ProducerFlowElementInvoker()));
		return this.route;
	}

}
