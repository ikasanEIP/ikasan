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
package org.ikasan.testharness.flow.expectation.service;

import junit.framework.Assert;

import org.ikasan.framework.component.Event;
import org.ikasan.framework.component.transformation.TransformationException;
import org.ikasan.framework.component.transformation.Transformer;
import org.ikasan.framework.flow.FlowElement;
import org.ikasan.testharness.flow.Capture;
import org.ikasan.testharness.flow.comparator.ExpectationComparator;
import org.ikasan.testharness.flow.comparator.service.ComparatorService;
import org.ikasan.testharness.flow.expectation.model.TransformerComponent;
import org.ikasan.testharness.flow.expectation.service.FlowExpectation;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Tests for the <code>OrderedExpectation</code> class.
 *
 * @author Ikasan Development Team
 *
 */
public class OrderedExpectationTest
{
    Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };
    
    /** mocked capture */
    final Capture<?> capture = mockery.mock(Capture.class, "capture");
    
    /** mocked flowElement */
    final FlowElement flowElement = mockery.mock(FlowElement.class, "flowElement");
    
    /** mocked comparatorService */
    @SuppressWarnings("unchecked")
    final ComparatorService comparatorService = mockery.mock(ComparatorService.class, "ComparatorService");
    
    /** mocked expectationComparator **/
    @SuppressWarnings("unchecked")
    final ExpectationComparator expectationComparator = mockery.mock(ExpectationComparator.class, "expectationComparator");
    
    /** mocked expectation */
    final Object expectation = mockery.mock(Object.class, "ExpectationObject");
    
    /**
     * Sanity test of a default OrderedExpectation instance with a single 
     * expectation to be matched using the default description.
     */
    @Test
    public void test_successfulDefaultOrderedExpectationWithSingleExpectationDefaultDescription() 
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // get the mocked actual flow element
                exactly(1).of(capture).getActual();
                will(returnValue(flowElement));
                
                // expected name
                exactly(1).of(flowElement).getComponentName();
                will(returnValue("one"));

                // expected implementation class
                exactly(1).of(flowElement).getFlowComponent();
                will(returnValue(new TestTransformer()));
            }
        });
        
        FlowExpectation flowExpectation = new OrderedExpectation();
        flowExpectation.expectation(new TransformerComponent("one"));
        
        // match expectation invocations to actual occurrences
        flowExpectation.isSatisfied(capture);
        
        // ensure no more expectations
        flowExpectation.allSatisfied();
        
        mockery.assertIsSatisfied();
    }

    /**
     * Sanity test of a default OrderedExpectation instance with a single 
     * expectation to be matched with a user defined description.
     */
    @Test
    public void test_successfulDefaultOrderedExpectationWithSingleExpectationUserDescription() 
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // get the mocked actual flow element
                exactly(1).of(capture).getActual();
                will(returnValue(flowElement));
                
                // expected name
                exactly(1).of(flowElement).getComponentName();
                will(returnValue("one"));

                // expected implementation class
                exactly(1).of(flowElement).getFlowComponent();
                will(returnValue(new TestTransformer()));
            }
        });
        
        FlowExpectation flowExpectation = new OrderedExpectation();
        flowExpectation.expectation(new TransformerComponent("one"), "my test expectation description");
        
        // match expectation invocations to actual occurrences
        flowExpectation.isSatisfied(capture);
        
        // ensure no more expectations
        flowExpectation.allSatisfied();
        
        mockery.assertIsSatisfied();
    }

    /**
     * Sanity test of a default OrderedExpectation instance with a single 
     * expectation to be ignored with default description.
     */
    @Test
    public void test_successfulDefaultOrderedExpectationWithSingleIgnoreExpectationDefaultDescription() 
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // get the mocked actual flow element
                exactly(1).of(capture).getActual();
                will(returnValue(flowElement));
            }
        });
        
        FlowExpectation flowExpectation = new OrderedExpectation();
        flowExpectation.ignore(new TransformerComponent("one"));
        
        // match expectation invocations to actual occurrences
        flowExpectation.isSatisfied(capture);
        
        // ensure no more expectations
        flowExpectation.allSatisfied();
        
        mockery.assertIsSatisfied();
    }

    /**
     * Sanity test of a default OrderedExpectation instance with a single 
     * expectation to be ignored with user description.
     */
    @Test
    public void test_successfulDefaultOrderedExpectationWithSingleIgnoreExpectationUserDescription() 
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // get the mocked actual flow element
                exactly(1).of(capture).getActual();
                will(returnValue(flowElement));
            }
        });
        
        FlowExpectation flowExpectation = new OrderedExpectation();
        flowExpectation.ignore(new TransformerComponent("one"), "another description");
        
        // match expectation invocations to actual occurrences
        flowExpectation.isSatisfied(capture);
        
        // ensure no more expectations
        flowExpectation.allSatisfied();
        
        mockery.assertIsSatisfied();
    }

    /**
     * Sanity test of a default OrderedExpectation instance with a single 
     * expectation and a user specified comparator passed explicitly for that
     * expectation. Use default expectation description.
     */
    @Test
    public void test_successfulDefaultOrderedExpectationWithSingleExpectationAndUserComparatorDefaultDescription() 
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // get the mocked actual flow element
                exactly(1).of(capture).getActual();
                will(returnValue("one"));
            }
        });
        
        FlowExpectation flowExpectation = new OrderedExpectation();
        flowExpectation.expectation(new String("one"), new TestComparator());
        
        // match expectation invocations to actual occurrences
        flowExpectation.isSatisfied(capture);
        
        // ensure no more expectations
        flowExpectation.allSatisfied();
        
        mockery.assertIsSatisfied();
    }

    /**
     * Sanity test of a default OrderedExpectation instance with a single 
     * expectation and a user specified comparator passed explicitly for that
     * expectation. Use User description.
     */
    @Test
    public void test_successfulDefaultOrderedExpectationWithSingleExpectationAndUserComparatorUserDescription() 
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // get the mocked actual flow element
                exactly(1).of(capture).getActual();
                will(returnValue("one"));
            }
        });
        
        FlowExpectation flowExpectation = new OrderedExpectation();
        flowExpectation.expectation(new String("one"), new TestComparator(), "another expectation description");
        
        // match expectation invocations to actual occurrences
        flowExpectation.isSatisfied(capture);
        
        // ensure no more expectations
        flowExpectation.allSatisfied();
        
        mockery.assertIsSatisfied();
    }

    /**
     * Sanity test of an OrderedExpectation instance with an alternate ComparatorService.
     */
    @Test
    public void test_successfulOrderedExpectationWithAlternateComparatorService()
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // get the mocked actual flow element
                exactly(1).of(capture).getActual();
                will(returnValue("one"));
                
                exactly(1).of(comparatorService).getComparator(with(any(Object.class)));
                will(returnValue(expectationComparator));
                
                exactly(1).of(expectationComparator).compare(with(any(Object.class)), with(any(Object.class)));
            }
        });
        
        FlowExpectation flowExpectation = new OrderedExpectation(comparatorService);
        flowExpectation.expectation(expectation);
        
        // match expectation invocations to actual occurrences
        flowExpectation.isSatisfied(capture);
        
        // ensure no more expectations
        flowExpectation.allSatisfied();
        
        mockery.assertIsSatisfied();
    }

    /**
     * Sanity test of a default OrderedExpectation instance with a single 
     * expectation and a user specified comparator, but based on an incorrect 
     * class comparator parameter type resulting in a ClassCastException.
     */
    @Test(expected = RuntimeException.class)
    public void test_failedDefaultOrderedExpectationWithClassCastException() 
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // get the mocked actual flow element
                exactly(2).of(capture).getActual();
                will(returnValue(flowElement));
            }
        });
        
        FlowExpectation flowExpectation = new OrderedExpectation();
        flowExpectation.expectation(new TransformerComponent("one"), new TestComparator());
        
        // match expectation invocations to actual occurrences
        flowExpectation.isSatisfied(capture);
        
        // ensure no more expectations
        flowExpectation.allSatisfied();
        
        mockery.assertIsSatisfied();
    }

    /**
     * Simple implementation of a Transformer component for testing.
     * @author Ikasan Development Team
     *
     */
    private class TestTransformer implements Transformer
    {

        public void onEvent(Event event) throws TransformationException
        {
            // do nothing
        }
        
    }

    /**
     * Simple implementation of a TestComparator for testing.
     * @author Ikasan Development Team
     *
     */
    private class TestComparator implements ExpectationComparator<String,String>
    {

        public void compare(String expected, String actual)
        {
            Assert.assertEquals(expected, actual);
        }

    }
}    

