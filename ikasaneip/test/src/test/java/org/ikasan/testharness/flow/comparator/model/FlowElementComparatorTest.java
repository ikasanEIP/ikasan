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
package org.ikasan.testharness.flow.comparator.model;

import org.ikasan.spec.component.transformation.TransformationException;
import org.ikasan.spec.component.transformation.Translator;
import org.ikasan.spec.flow.FlowElement;
import org.ikasan.testharness.flow.expectation.model.SingleRecipientRouterComponent;
import org.ikasan.testharness.flow.expectation.model.TranslatorComponent;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.concurrent.Synchroniser;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.ComparisonFailure;
import org.junit.Test;

/**
 * Tests for the <code>FlowElementComparator</code> class.
 *
 * @author Ikasan Development Team
 *
 */
public class FlowElementComparatorTest
{
    private Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
            setThreadingPolicy(new Synchroniser());
        }
    };
    
    /** mocked actual flow element */
    private final FlowElement flowElement = mockery.mock(FlowElement.class, "Mock Actual Flow Element");
    
    /**
     * Sanity test the default FlowElementComparator for an expected and actual 
     * component that are deemed equal.
     */
    @Test
    public void test_successfulFlowElementComparator() 
    {
        final TranslatorComponent translatorComponent = 
            new TranslatorComponent("name");
        
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // compare component name
                exactly(1).of(flowElement).getComponentName();
                will(returnValue("name"));

                // compare component name
                exactly(2).of(flowElement).getFlowComponent();
                will(returnValue(new TestTranslator()));
            }
        });

        FlowElementComparator flowElementComparator = new FlowElementComparator();
        flowElementComparator.compare(translatorComponent, flowElement);
        
        mockery.assertIsSatisfied();
    }
    
    /**
     * Sanity test the default FlowElementComparator for an expected and actual 
     * component that have different names.
     */
    @Test(expected = ComparisonFailure.class)
    public void test_failedFlowElementComparatorDueToDifferentNames() 
    {
        final TranslatorComponent translatorComponent = 
            new TranslatorComponent("name");
        
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // compare component name
                exactly(2).of(flowElement).getComponentName();
                will(returnValue("different name"));
            }
        });

        FlowElementComparator flowElementComparator = new FlowElementComparator();
        flowElementComparator.compare(translatorComponent, flowElement);
        
        mockery.assertIsSatisfied();
    }
    
    /**
     * Sanity test the default FlowElementComparator for an expected and actual 
     * component that have different component types.
     */
    @Test(expected = AssertionError.class)
    public void test_failedFlowElementComparatorDueToDifferentComponentTypes() 
    {
        final SingleRecipientRouterComponent routerComponent = new SingleRecipientRouterComponent("name");
        
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // compare component name
                exactly(2).of(flowElement).getComponentName();
                will(returnValue("name"));

                // compare component name
                exactly(2).of(flowElement).getFlowComponent();
                will(returnValue(new TestTranslator()));
            }
        });

        FlowElementComparator flowElementComparator = new FlowElementComparator();
        flowElementComparator.compare(routerComponent, flowElement);
        
        mockery.assertIsSatisfied();
    }

    /**
     * Simple implementation of a TestTranslator component for testing.
     * @author Ikasan Development Team
     *
     */
    private class TestTranslator implements Translator<StringBuffer>
    {

        public void translate(StringBuffer payload) throws TransformationException
        {
            // do nothing
        }
    }
}    

