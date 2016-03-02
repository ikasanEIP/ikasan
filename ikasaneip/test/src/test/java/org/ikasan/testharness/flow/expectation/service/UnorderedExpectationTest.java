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

import org.ikasan.spec.component.transformation.TransformationException;
import org.ikasan.spec.component.transformation.Translator;
import org.ikasan.spec.flow.FlowElement;
import org.ikasan.testharness.flow.Capture;
import org.ikasan.testharness.flow.comparator.ExpectationComparator;
import org.ikasan.testharness.flow.comparator.service.ComparatorService;
import org.ikasan.testharness.flow.expectation.model.TranslatorComponent;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for the <code>UnorderedExpectation</code> class.
 *
 * @author Ikasan Development Team
 *
 */
public class UnorderedExpectationTest
{
    Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** mocked capture */
    final Capture<?> capture = mockery.mock(Capture.class, "capture");
    final Capture<?> capture2 = mockery.mock(Capture.class, "capture2");

    /** mocked flowElement */
    final FlowElement flowElement = mockery.mock(FlowElement.class, "flowElement");
    final FlowElement flowElement2 = mockery.mock(FlowElement.class, "flowElement2");

    /** mocked comparatorService */
    @SuppressWarnings("unchecked")
    final ComparatorService comparatorService = mockery.mock(ComparatorService.class, "ComparatorService");

    /** mocked expectationComparator **/
    @SuppressWarnings("unchecked")
    final ExpectationComparator expectationComparator = mockery.mock(ExpectationComparator.class, "expectationComparator");

    /** mocked expectation */
    final Object expectation = mockery.mock(Object.class, "ExpectationObject");

    /**
     * Sanity test of a default UnorderedExpectation instance with a single
     * expectation to be matched using the default description.
     */
    @Test
    public void test_successfulDefaultUnorderedExpectationWithSingleExpectationDefaultDescription()
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // get the mocked actual flow element
                exactly(2).of(capture).getActual();
                will(returnValue(flowElement));

                // expected name
                exactly(1).of(flowElement).getComponentName();
                will(returnValue("one"));

                // expected implementation class
                exactly(2).of(flowElement).getFlowComponent();
                will(returnValue(new TestTranslator()));
            }
        });

        FlowExpectation flowExpectation = new UnorderedExpectation();
        flowExpectation.expectation(new TranslatorComponent("one"));

        // match expectation invocations to actual occurrences
        flowExpectation.isSatisfied(capture);

        // ensure no more expectations
        flowExpectation.allSatisfied();

        mockery.assertIsSatisfied();
    }

    /**
     * Sanity test of a default UnorderedExpectation instance with two
     * expectations to be matched using the default description.
     */
    @Test
    public void test_successfulDefaultUnorderedExpectationWithTwoExpectationsStandardOrdering()
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // get the mocked actual flow element
                exactly(2).of(capture).getActual();
                will(returnValue(flowElement));

                exactly(2).of(capture2).getActual();
                will(returnValue(flowElement2));

                // expected name
                exactly(1).of(flowElement2).getComponentName();
                will(returnValue("two"));
                exactly(1).of(flowElement).getComponentName();
                will(returnValue("one"));
                // expected implementation class
                exactly(2).of(flowElement).getFlowComponent();
                will(returnValue(new TestTranslator()));

                // expected implementation class
                exactly(2).of(flowElement2).getFlowComponent();
                will(returnValue(new TestTranslator()));
            }
        });

        FlowExpectation flowExpectation = new UnorderedExpectation();
        flowExpectation.expectation(new TranslatorComponent("one"), "one");
        flowExpectation.expectation(new TranslatorComponent("two"), "two");

        // match expectation invocations to actual occurrences
        flowExpectation.isSatisfied(capture);
        flowExpectation.isSatisfied(capture2);

        // ensure no more expectations
        flowExpectation.allSatisfied();

        mockery.assertIsSatisfied();
    }


    /**
     * Sanity test of a default UnorderedExpectation instance with two
     * expectations to be matched using the default description.
     */
    @Test
    public void test_successfulDefaultUnorderedExpectationWithTwoExpectationsReversedOrdering()
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // get the mocked actual flow element
                exactly(2).of(capture).getActual();
                will(returnValue(flowElement));
                exactly(3).of(capture2).getActual();
                will(returnValue(flowElement2));
                // expected name
                exactly(2).of(flowElement2).getComponentName();
                will(returnValue("two"));
                exactly(1).of(flowElement).getComponentName();
                will(returnValue("one"));
                // expected implementation class
                exactly(2).of(flowElement).getFlowComponent();
                will(returnValue(new TestTranslator()));
                // expected implementation class
                exactly(2).of(flowElement2).getFlowComponent();
                will(returnValue(new TestTranslator()));
            }
        });

        FlowExpectation flowExpectation = new UnorderedExpectation();
        flowExpectation.expectation(new TranslatorComponent("one"), "one");
        flowExpectation.expectation(new TranslatorComponent("two"), "two");

        // match expectation invocations to actual occurrences
        flowExpectation.isSatisfied(capture2);
        flowExpectation.isSatisfied(capture);

        // ensure no more expectations
        flowExpectation.allSatisfied();

        mockery.assertIsSatisfied();
    }

    /**
     * Sanity test of a default UnorderedExpectation instance with a single
     * expectation to be matched with a user defined description.
     */
    @Test
    public void test_successfulDefaultUnorderedExpectationWithSingleExpectationUserDescription()
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // get the mocked actual flow element
                exactly(2).of(capture).getActual();
                will(returnValue(flowElement));

                // expected name
                exactly(1).of(flowElement).getComponentName();
                will(returnValue("one"));

                // expected implementation class
                exactly(2).of(flowElement).getFlowComponent();
                will(returnValue(new TestTranslator()));
            }
        });

        FlowExpectation flowExpectation = new UnorderedExpectation();
        flowExpectation.expectation(new TranslatorComponent("one"), "my test expectation description");

        // match expectation invocations to actual occurrences
        flowExpectation.isSatisfied(capture);

        // ensure no more expectations
        flowExpectation.allSatisfied();

        mockery.assertIsSatisfied();
    }

    /**
     * Sanity test of a default UnorderedExpectation instance with a single
     * expectation to be ignored with default description.
     */
    @Test
    public void test_successfulDefaultUnorderedExpectationWithSingleIgnoreExpectationDefaultDescription()
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

        FlowExpectation flowExpectation = new UnorderedExpectation();
        flowExpectation.ignore(new TranslatorComponent("one"));

        // match expectation invocations to actual occurrences
        flowExpectation.isSatisfied(capture);

        // ensure no more expectations
        flowExpectation.allSatisfied();

        mockery.assertIsSatisfied();
    }

    /**
     * Sanity test of a default UnorderedExpectation instance with a single
     * expectation to be ignored with user description.
     */
    @Test
    public void test_successfulDefaultUnorderedExpectationWithSingleIgnoreExpectationUserDescription()
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

        FlowExpectation flowExpectation = new UnorderedExpectation();
        flowExpectation.ignore(new TranslatorComponent("one"), "another description");

        // match expectation invocations to actual occurrences
        flowExpectation.isSatisfied(capture);

        // ensure no more expectations
        flowExpectation.allSatisfied();

        mockery.assertIsSatisfied();
    }

    /**
     * Sanity test of a default UnorderedExpectation instance with a single
     * expectation and a user specified comparator passed explicitly for that
     * expectation. Use default expectation description.
     */
    @Test
    public void test_successfulDefaultUnorderedExpectationWithSingleExpectationAndUserComparatorDefaultDescription()
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // get the mocked actual flow element
                exactly(2).of(capture).getActual();
                will(returnValue("one"));
            }
        });

        FlowExpectation flowExpectation = new UnorderedExpectation();
        flowExpectation.expectation("one", new TestComparator());

        // match expectation invocations to actual occurrences
        flowExpectation.isSatisfied(capture);

        // ensure no more expectations
        flowExpectation.allSatisfied();

        mockery.assertIsSatisfied();
    }

    /**
     * Sanity test of a default UnorderedExpectation instance with a single
     * expectation and a user specified comparator passed explicitly for that
     * expectation. Use User description.
     */
    @Test
    public void test_successfulDefaultUnorderedExpectationWithSingleExpectationAndUserComparatorUserDescription()
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // get the mocked actual flow element
                exactly(2).of(capture).getActual();
                will(returnValue("one"));
            }
        });

        FlowExpectation flowExpectation = new UnorderedExpectation();
        flowExpectation.expectation("one", new TestComparator(), "another expectation description");

        // match expectation invocations to actual occurrences
        flowExpectation.isSatisfied(capture);

        // ensure no more expectations
        flowExpectation.allSatisfied();

        mockery.assertIsSatisfied();
    }

    /**
     * Sanity test of an UnorderedExpectation instance with an alternate ComparatorService.
     */
    @Test
    public void test_successfulUnorderedExpectationWithAlternateComparatorService()
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // get the mocked actual flow element
                exactly(2).of(capture).getActual();
                will(returnValue("one"));

                exactly(1).of(comparatorService).getComparator(with(any(Object.class)));
                will(returnValue(expectationComparator));

                exactly(1).of(expectationComparator).compare(with(any(Object.class)), with(any(Object.class)));
            }
        });

        FlowExpectation flowExpectation = new UnorderedExpectation(comparatorService);
        flowExpectation.expectation(expectation);

        // match expectation invocations to actual occurrences
        flowExpectation.isSatisfied(capture);

        // ensure no more expectations
        flowExpectation.allSatisfied();

        mockery.assertIsSatisfied();
    }

    /**
     * Sanity test of a default UnorderedExpectation instance with a single
     * expectation and a user specified comparator, but based on an incorrect
     * class comparator parameter type resulting in a ClassCastException.
     */
    @Test(expected = RuntimeException.class)
    public void test_failedDefaultUnorderedExpectationWithClassCastException()
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // get the mocked actual flow element
                exactly(3).of(capture).getActual();
                will(returnValue(flowElement));
            }
        });

        FlowExpectation flowExpectation = new UnorderedExpectation();
        flowExpectation.expectation(new TranslatorComponent("one"), new TestComparator());

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
    private class TestTranslator implements Translator<StringBuilder>
    {

        public void translate(StringBuilder payload) throws TransformationException
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
