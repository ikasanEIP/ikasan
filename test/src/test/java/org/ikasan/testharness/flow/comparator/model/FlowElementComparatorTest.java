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

import org.ikasan.framework.component.Event;
import org.ikasan.framework.component.transformation.TransformationException;
import org.ikasan.framework.component.transformation.Transformer;
import org.ikasan.framework.flow.FlowElement;
import org.ikasan.testharness.flow.expectation.model.RouterComponent;
import org.ikasan.testharness.flow.expectation.model.TransformerComponent;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Tests for the <code>FlowElementComparator</code> class.
 *
 * @author Ikasan Development Team
 *
 */
public class FlowElementComparatorTest
{
    Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };
    
    /** mocked actual flow element */
    final FlowElement flowElement = mockery.mock(FlowElement.class, "Mock Actual Flow Element");
    
    /**
     * Sanity test the default FlowElementComparator for an expected and actual 
     * component that are deemed equal.
     */
    @Test
    public void test_successfulFlowElementComparator() 
    {
        final TransformerComponent transformerComponent = 
            new TransformerComponent("transformerName");
        
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // compare component name
                exactly(1).of(flowElement).getComponentName();
                will(returnValue("transformerName"));

                // compare component name
                exactly(1).of(flowElement).getFlowComponent();
                will(returnValue(new TestTransformer()));
            }
        });

        FlowElementComparator flowElementComparator = new FlowElementComparator();
        flowElementComparator.compare(transformerComponent, flowElement);
        
        mockery.assertIsSatisfied();
    }
    
    /**
     * Sanity test the default FlowElementComparator for an expected and actual 
     * component that have different names.
     */
    @Test(expected = junit.framework.ComparisonFailure.class)
    public void test_failedFlowElementComparatorDueToDifferentNames() 
    {
        final TransformerComponent transformerComponent = 
            new TransformerComponent("transformerName");
        
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // compare component name
                exactly(2).of(flowElement).getComponentName();
                will(returnValue("different transformerName"));
            }
        });

        FlowElementComparator flowElementComparator = new FlowElementComparator();
        flowElementComparator.compare(transformerComponent, flowElement);
        
        mockery.assertIsSatisfied();
    }
    
    /**
     * Sanity test the default FlowElementComparator for an expected and actual 
     * component that have different component types.
     */
    @Test(expected = junit.framework.AssertionFailedError.class)
    public void test_failedFlowElementComparatorDueToDifferentComponentTypes() 
    {
        final RouterComponent routerComponent = new RouterComponent("name");
        
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // compare component name
                exactly(2).of(flowElement).getComponentName();
                will(returnValue("name"));

                // compare component name
                exactly(2).of(flowElement).getFlowComponent();
                will(returnValue(new TestTransformer()));
            }
        });

        FlowElementComparator flowElementComparator = new FlowElementComparator();
        flowElementComparator.compare(routerComponent, flowElement);
        
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
}    

