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
package org.ikasan.wiretap.model;

import org.ikasan.spec.flow.FlowEvent;
import org.ikasan.spec.wiretap.WiretapEvent;
import org.ikasan.spec.wiretap.WiretapSerialiser;
import org.ikasan.wiretap.serialiser.WiretapSerialiserService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This test class supports the <code>WiretapSerialiserService</code> class.
 * 
 * @author Ikasan Development Team
 */
public class WiretapEventFactoryDefaultImplTest
{
    /** serialisers to be supported by the service */
    private Map serialisers;

    /** serialiser service instance */
    private WiretapSerialiser<Object,String> serialiser;

    @Before
    public void setup()
    {
        // create a map of required serialisers
        this.serialisers = new ConcurrentHashMap();
        serialisers.put(Integer.class, new DefaultSerialiser());

        // create a wiretap serialiser service instance passing the supported serialisers
        this.serialiser = new WiretapSerialiserService(serialisers);
    }

    /**
     * Test failed constructor.
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failed_constructor()
    {
        new WiretapEventFactoryDefaultImpl(null);
    }

    /**
     * Test new event based on default serialiser
     */
    @Test
    public void test_newEvent_with_default_serialiser()
    {
        WiretapEventFactory wiretapEventFactory = new WiretapEventFactoryDefaultImpl(this.serialiser);
        FlowEvent flowEvent = new GenericFlowEvent<String, Integer>("id", new Integer(1));

        // test the serialiser service
        WiretapEvent wiretapEvent = wiretapEventFactory.newEvent("moduleName", "flowName",
                "componentName", flowEvent, 0L);

        Assert.assertTrue(wiretapEvent.getModuleName().equals("moduleName"));
        Assert.assertTrue(wiretapEvent.getFlowName().equals("flowName"));
        Assert.assertTrue(wiretapEvent.getComponentName().equals("componentName"));
        Assert.assertTrue(wiretapEvent.getExpiry() == 0L);
        Assert.assertTrue(wiretapEvent.getEvent().equals("1"));
    }

    /**
     * Test new event based on module serialiser
     */
    @Test
    public void test_newEvent_with_overridden_default_serialiser()
    {
        WiretapEventFactory wiretapEventFactory = new WiretapEventFactoryDefaultImpl(this.serialiser);
        FlowEvent flowEvent = new GenericFlowEvent<String, Integer>("id", new Integer(1));

        WiretapSerialiser serialiser = new NewDefaultSerialiser();
        wiretapEventFactory.setSerialiser(serialiser);

        // test the serialiser service
        WiretapEvent wiretapEvent = wiretapEventFactory.newEvent("moduleName", "flowName",
                "componentName", flowEvent, 0L);

        Assert.assertTrue(wiretapEvent.getModuleName().equals("moduleName"));
        Assert.assertTrue(wiretapEvent.getFlowName().equals("flowName"));
        Assert.assertTrue(wiretapEvent.getComponentName().equals("componentName"));
        Assert.assertTrue(wiretapEvent.getExpiry() == 0L);
        Assert.assertTrue(wiretapEvent.getEvent().equals("newDefaultSerialiser1"));
    }

   /**
     * Test new event based on component serialiser
     */
    @Test
    public void test_newEvent_with_registered_module_flow_component_serialiser()
    {
        WiretapEventFactory wiretapEventFactory = new WiretapEventFactoryDefaultImpl(this.serialiser);
        FlowEvent flowEvent = new GenericFlowEvent<String, Integer>("id", new Integer(1));

        WiretapSerialiser moduleSerialiser = new NewDefaultSerialiser();
        WiretapSerialiser componentSerialiser = new ComponentSerialiser();
        wiretapEventFactory.setSerialiser(moduleSerialiser);
        wiretapEventFactory.setSerialiser("componentName", componentSerialiser);

        // test the serialiser service
        WiretapEvent wiretapEvent = wiretapEventFactory.newEvent("moduleName", "flowName",
                "componentName", flowEvent, 0L);

        Assert.assertTrue(wiretapEvent.getModuleName().equals("moduleName"));
        Assert.assertTrue(wiretapEvent.getFlowName().equals("flowName"));
        Assert.assertTrue(wiretapEvent.getComponentName().equals("componentName"));
        Assert.assertTrue(wiretapEvent.getExpiry() == 0L);
        Assert.assertTrue(wiretapEvent.getEvent().equals("component1"));
    }

    /**
     * Simple example serialiser implementation of the wiretap serialiser contract.
     * This serialises an Integer object to a byte[] for wiretapping.
     * @author Ikasan Development Team
     *
     */
    private class DefaultSerialiser implements WiretapSerialiser<Integer,String>
    {
        /**
         * Serialiser implementation for Integer wiretapping.
         * @param source
         * @return byte[] 
         */
        public String serialise(Integer source)
        {
            return source.toString();
        }
        
    }

    private class NewDefaultSerialiser implements WiretapSerialiser<Integer,String>
    {
        /**
         * Serialiser implementation for Integer wiretapping.
         * @param source
         * @return byte[]
         */
        public String serialise(Integer source)
        {
            return "newDefaultSerialiser" + source.toString();
        }

    }

    private class ComponentSerialiser implements WiretapSerialiser<Integer,String>
    {
        /**
         * Serialiser implementation for Integer wiretapping.
         * @param source
         * @return byte[]
         */
        public String serialise(Integer source)
        {
            return "component" + source.toString();
        }

    }

    /**
     * Implementation of a flowEvent based on payload being of any generic type.
     *
     * @author Ikasan Development Team
     *
     */
    private class GenericFlowEvent<ID,PAYLOAD> implements FlowEvent<ID,PAYLOAD>, Serializable
    {
        /** default serial id */
        private static final long serialVersionUID = 1L;

        /** immutable identifier */
        private ID identifier;

        /** immutable related identifier */
        private ID relatedIdentifier;

        /** immutable event creation timestamp */
        private long timestamp;

        /** payload */
        private PAYLOAD payload;

        /**
         * Constructor
         * @param identifier
         * @param payload
         */
        protected GenericFlowEvent(ID identifier, PAYLOAD payload)
        {
            this.identifier = identifier;
            if(identifier == null)
            {
                throw new IllegalArgumentException("identifier cannot be 'null'. Make sure the FlowEvent has an identifier!");
            }
            this.timestamp = System.currentTimeMillis();
            this.payload = payload;
        }

        /**
         * Constructor
         * @param identifier
         * @param relatedIdentifier
         * @param payload
         */
        protected GenericFlowEvent(ID identifier, ID relatedIdentifier, PAYLOAD payload)
        {
            this.identifier = identifier;
            if(identifier == null)
            {
                throw new IllegalArgumentException("identifier cannot be 'null'. Make sure the FlowEvent has an identifier!");
            }
            this.relatedIdentifier = relatedIdentifier;
            this.timestamp = System.currentTimeMillis();
            this.payload = payload;
        }

        /**
         * Get immutable flow event identifier.
         * @return String - event identifier
         */
        public ID getIdentifier()
        {
            return this.identifier;
        }

        /**
         * Get immutable flow event related identifier.
         * @return String - event related identifier
         */
        public ID getRelatedIdentifier()
        {
            return this.relatedIdentifier;
        }

        /**
         * Get the immutable created date/time of the flow event.
         * @return long - create date time
         */
        public long getTimestamp()
        {
            return this.timestamp;
        }

        /**
         * Get the payload of this flow event.
         * @return PAYLOAD payload
         */
        public PAYLOAD getPayload()
        {
            return this.payload;
        }

        /**
         * Set the payload of this flow event.
         * @param payload - payload
         */
        public void setPayload(PAYLOAD payload)
        {
            this.payload = payload;
        }

        @Override
        public void replace(FlowEvent<ID, PAYLOAD> flowEvent)
        {
            if (flowEvent != null)
            {
                this.setPayload(flowEvent.getPayload());

                this.identifier = flowEvent.getIdentifier();
                this.timestamp = flowEvent.getTimestamp();
                this.relatedIdentifier = flowEvent.getRelatedIdentifier();
            }
        }
    }

}
