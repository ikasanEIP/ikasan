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
package org.ikasan.component.endpoint.consumer.api;

import org.ikasan.component.endpoint.consumer.api.TechEndpointEventProvider;
import org.ikasan.component.endpoint.consumer.api.TechEndpointMultipleEventProvidersImpl;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Test class for DefaultTechEndpointEventProviderImpl.
 * 
 * @author Ikasan Development Team
 */
public class TechEndpointMultipleEventProvidersImplTest
{
    /**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery() {{
        setImposteriser(ClassImposteriser.INSTANCE);
    }};

    private TechEndpointEventProvider techEndpointEventProvider = mockery.mock(TechEndpointEventProvider.class);

    /**
     * Test
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failed_constructor_null_techEndpointEventProviders()
    {
        new TechEndpointMultipleEventProvidersImpl(null);
    }

    /**
     * Test
     */
    @Test
    public void test_send_with_consumeEvent()
    {
        List<TechEndpointEventProvider> techEndpointEventProviders = new ArrayList<TechEndpointEventProvider>();
        techEndpointEventProviders.add( new StringTechEventProviderImpl() );
        techEndpointEventProviders.add( new IntegerTechEventProviderImpl() );

        TechEndpointEventProvider techEndpointEventProviderOnTest =
                new TechEndpointMultipleEventProvidersImpl(techEndpointEventProviders);

        Assert.assertTrue(techEndpointEventProviderOnTest.consumeEvent().equals("event1"));
        Assert.assertTrue(techEndpointEventProviderOnTest.consumeEvent().equals("event2"));
        Assert.assertTrue(techEndpointEventProviderOnTest.consumeEvent().equals("event3"));
        Assert.assertTrue(techEndpointEventProviderOnTest.consumeEvent().equals(Integer.valueOf(1)));
        Assert.assertTrue(techEndpointEventProviderOnTest.consumeEvent().equals(Integer.valueOf(2)));
        Assert.assertTrue(techEndpointEventProviderOnTest.consumeEvent().equals(Integer.valueOf(3)));
        Assert.assertNull(techEndpointEventProviderOnTest.consumeEvent());
    }

    /**
     * Test
     */
    @Test
    public void test_send_with_consumeEvent_with_rollback()
    {
        List<TechEndpointEventProvider> techEndpointEventProviders = new ArrayList<TechEndpointEventProvider>();
        techEndpointEventProviders.add( new StringTechEventProviderImpl() );
        techEndpointEventProviders.add( new IntegerTechEventProviderImpl() );

        TechEndpointEventProvider techEndpointEventProviderOnTest =
                new TechEndpointMultipleEventProvidersImpl(techEndpointEventProviders);

        Assert.assertTrue(techEndpointEventProviderOnTest.consumeEvent().equals("event1"));
        Assert.assertTrue(techEndpointEventProviderOnTest.consumeEvent().equals("event2"));

        techEndpointEventProviderOnTest.rollback();
        Assert.assertTrue(techEndpointEventProviderOnTest.consumeEvent().equals("event2"));

        Assert.assertTrue(techEndpointEventProviderOnTest.consumeEvent().equals("event3"));
        Assert.assertTrue(techEndpointEventProviderOnTest.consumeEvent().equals(Integer.valueOf(1)));
        Assert.assertTrue(techEndpointEventProviderOnTest.consumeEvent().equals(Integer.valueOf(2)));

        techEndpointEventProviderOnTest.rollback();
        Assert.assertTrue(techEndpointEventProviderOnTest.consumeEvent().equals(Integer.valueOf(2)));
        Assert.assertTrue(techEndpointEventProviderOnTest.consumeEvent().equals(Integer.valueOf(3)));
        Assert.assertNull(techEndpointEventProviderOnTest.consumeEvent());
    }

    class StringTechEventProviderImpl implements TechEndpointEventProvider<String>
    {
        List<String> events = new ArrayList<String>();
        int count=0;

        public StringTechEventProviderImpl()
        {
            events.add("event1");
            events.add("event2");
            events.add("event3");
        }

        @Override
        public String consumeEvent()
        {
            try
            {
                return events.get(count++);
            }
            catch(IndexOutOfBoundsException ex)
            {
                return null;
            }
        }

        @Override
        public List getEvents() {
            return events;
        }

        @Override
        public void rollback()
        {
            // simple rollback
            count--;
        }

        @Override
        public TechEndpointEventProvider clone()
        {
            return new StringTechEventProviderImpl();
        }

        @Override
        public boolean isRepeatEventCycle()
        {
            return false;
        }
    }

    class IntegerTechEventProviderImpl implements TechEndpointEventProvider<Integer>
    {
        List<Integer> events = new ArrayList<Integer>();
        int count=0;

        public IntegerTechEventProviderImpl()
        {
            events.add(Integer.valueOf(1));
            events.add(Integer.valueOf(2));
            events.add(Integer.valueOf(3));
        }

        @Override
        public Integer consumeEvent()
        {
            try
            {
                return events.get(count++);
            }
            catch(IndexOutOfBoundsException ex)
            {
                return null;
            }
        }

        @Override
        public List getEvents() {
            return events;
        }

        @Override
        public void rollback()
        {
            // simple rollback
            count--;
        }

        @Override
        public TechEndpointEventProvider clone()
        {
            return new IntegerTechEventProviderImpl();
        }

        @Override
        public boolean isRepeatEventCycle()
        {
            return false;
        }
    }
}
