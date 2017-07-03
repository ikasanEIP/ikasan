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
package org.ikasan.sample.standalone;

import org.ikasan.builder.BuilderFactory;
import org.ikasan.sample.component.consumer.SimpleConsumer;
import org.ikasan.sample.component.converter.SimpleConverter;
import org.ikasan.sample.component.producer.SimpleProducer;
import org.ikasan.sample.component.router.SimpleRouter;
import org.ikasan.spec.exclusion.ExclusionService;
import org.ikasan.spec.flow.FlowInvocationContext;
import org.ikasan.spec.module.Module;
import org.ikasan.spec.serialiser.Serialiser;
import org.ikasan.spec.serialiser.SerialiserFactory;

/**
 * Simple Standalone JVM Example of Ikasan i9.
 * @author Ikasan Development Team.
 */
public class SimpleExample
{
    /**
     * Create a module with a single flow which
     *  - consumes Integers from a techEndpoint
     *  - converts Integer to String
     *  - routes even numbered strings to one producer and odd numbered strings to another producer
     *  - one of two produces get invoked based on odd or even payload content
     *
     * @param moduleName
     * @return
     */
    public Module createModule(String moduleName)
    {
        return BuilderFactory.moduleBuilder(moduleName)
                .addFlow(BuilderFactory.flowBuilder("flowName", moduleName).withDescription("Simple Module Example").withExclusionService(new StubbedExclusionService()).withSerialiserFactory(new StubbedSerialiserFactory())
                        .consumer("consumerName", new SimpleConsumer())     // of Integer
                        .converter("converterName", new SimpleConverter()) // to String
                        .singleRecipientRouter("routerName", new SimpleRouter())
                        .when("odd", BuilderFactory.routeBuilder().producer("oddValuePublisher", new SimpleProducer()))
                        .otherwise(BuilderFactory.routeBuilder().producer("evenValuePublisher", new SimpleProducer()))
                        .build())
            .build();
    }
    
    class StubbedSerialiserFactory implements SerialiserFactory
    {

		/* (non-Javadoc)
		 * @see org.ikasan.spec.serialiser.SerialiserFactory#getDefaultSerialiser()
		 */
		@Override
		public Serialiser getDefaultSerialiser()
		{
			// TODO Auto-generated method stub
			return null;
		}

		/* (non-Javadoc)
		 * @see org.ikasan.spec.serialiser.SerialiserFactory#getSerialiser(java.lang.Class)
		 */
		@Override
		public Serialiser getSerialiser(Class cls)
		{
			// TODO Auto-generated method stub
			return null;
		}
    	
    }

    /**
     * Stub out a dummy exclusion service to use for this module sample.
     */
    class StubbedExclusionService implements ExclusionService
    {

        @Override
        public boolean isBlackListed(Object o) {
            return false;
        }

        @Override
        public void addBlacklisted(Object o, String uri, FlowInvocationContext flowInvocationContext)
        {

        }

        @Override
        public String getErrorUri(Object o) {
            return null;
        }

        @Override
        public FlowInvocationContext getFlowInvocationContext(Object o)
        {
            return null;
        }

        @Override
        public void park(Object event, Object id) {

        }

        @Override
        public void removeBlacklisted(Object o) {

        }

        @Override
        public void setTimeToLive(Long timeToLive) {

        }

        @Override
        public void housekeep() {

        }

        @Override
        public boolean isBlackListEmpty() {
            return false;
        }
    }
}
