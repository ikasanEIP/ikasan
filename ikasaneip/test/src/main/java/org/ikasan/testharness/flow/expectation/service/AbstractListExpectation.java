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

import org.ikasan.testharness.flow.Capture;
import org.ikasan.testharness.flow.comparator.ExpectationComparator;
import org.ikasan.testharness.flow.comparator.model.IgnoreComparator;
import org.ikasan.testharness.flow.comparator.service.ComparatorService;
import org.ikasan.testharness.flow.comparator.service.ComparatorServiceImpl;
import org.ikasan.testharness.flow.expectation.model.AbstractComponent;
import org.ikasan.testharness.flow.expectation.model.IgnoreExpectation;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Abstract expectation based on a List of DefaultExpectations
 *
 * @author Ikasan Development Team
 */
public abstract class AbstractListExpectation implements FlowExpectation
{
    protected List<DefaultExpectation> expectations = new ArrayList<>();

    private AtomicInteger expectionIndexCounter = new AtomicInteger(0);

    /**
     * comparator service for expectations
     */
    protected ComparatorService comparatorService;

    public AbstractListExpectation()
    {
        this.comparatorService = getDefaultComparatorService();
    }

    /**
     * Constructor allowing an alternate comparator service.
     *
     * @param comparatorService
     */
    public AbstractListExpectation(ComparatorService comparatorService)
    {
        this.comparatorService = comparatorService;
    }

    /**
     * Default comparator service
     *
     * @return
     */
    protected static ComparatorService getDefaultComparatorService()
    {
        return new ComparatorServiceImpl();
    }

    /**
     * Add an Expectation and specific user defined comparator to the ordered expectations
     *
     * @param expectation
     * @param expectationComparator
     */
    @SuppressWarnings("unchecked")
    public <T> void expectation(T expectation, ExpectationComparator<?, ?> expectationComparator)
    {
        addExpectation(new DefaultExpectation(expectation, expectationComparator), null);
    }

    /**
     * Add an Expectation and specific user defined comparator to the ordered expectations
     * and provide a specific description to help identity this expectation at runtime.
     *
     * @param expectation
     * @param expectationComparator
     * @param description
     */
    @SuppressWarnings("unchecked")
    public <T> void expectation(T expectation, ExpectationComparator<?, ?> expectationComparator, String description)
    {
        addExpectation(new DefaultExpectation(expectation, expectationComparator), description);
    }

    /**
     * Add an Expectation to the ordered expectations
     *
     * @param expectation
     */
    @SuppressWarnings("unchecked")
    public <T> void expectation(T expectation)
    {
        ExpectationComparator expectationComparator = comparatorService.getComparator(expectation.getClass());
        addExpectation(new DefaultExpectation(expectation, expectationComparator), null);
    }

    /**
     * Add an Expectation to the ordered expectations and provide a description
     * to help identify this expectation at runtime.
     *
     * @param expectation
     * @param description
     */
    @SuppressWarnings("unchecked")
    public <T> void expectation(T expectation, String description)
    {
        ExpectationComparator expectationComparator = comparatorService.getComparator(expectation.getClass());
        addExpectation(new DefaultExpectation(expectation, expectationComparator), description);
    }

    /**
     * Add an ignore operation to the expectations order
     *
     * @param expectation
     */
    @SuppressWarnings("unchecked")
    public <T> void ignore(T expectation)
    {
        addExpectation(new DefaultExpectation(new IgnoreExpectation(expectation), new IgnoreComparator()), null);
    }

    /**
     * Add an ignore operation to the expectations order and provide
     * a description to help identify this expectation at runtime.
     *
     * @param expectation
     * @param description
     */
    @SuppressWarnings("unchecked")
    public <T> void ignore(T expectation, String description)
    {
        addExpectation(new DefaultExpectation(new IgnoreExpectation(expectation), new IgnoreComparator()), description);
    }

    /**
     * Append the expectation to the already defined expectations
     *
     * @param defaultExpectation
     * @param description
     */
    protected void addExpectation(DefaultExpectation<?> defaultExpectation, String description)
    {
        if (isNotBlank(description))
        {
            defaultExpectation.setDescription(description);
        }
        int index = expectionIndexCounter.getAndIncrement();
        defaultExpectation.setOrder(index + 1);
        this.expectations.add(index, defaultExpectation);
    }

    /**
     * Have all expectations been satisfied
     *
     * @param captures Ordered list of captured FlowElement invocations
     * @throws AssertionError if all expected invocations not satisfied
     */
    public abstract void allSatisfied(List<Capture<?>> captures);

    protected ExpectationDifference getExpectationDifference(List<DefaultExpectation> expectations,
            List<Capture<?>> captures)
    {
        List<DefaultExpectation> unsatisfiedExpectations = new ArrayList<>();
        List<Capture<?>> copyOfCaptures = new ArrayList<>(captures);
        for (DefaultExpectation<?> expectation : expectations)
        {
            boolean expectationSatisfied = false;
            Iterator<Capture<?>> captureIterator = copyOfCaptures.iterator();
            while (captureIterator.hasNext())
            {
                Capture<?> capture = captureIterator.next();
                try
                {
                    ExpectationComparator expectationComparator = expectation.getExpectationComparator();
                    expectationComparator.compare(expectation.getExpectation(), capture.getActual());
                    captureIterator.remove();
                    expectationSatisfied = true;
                    break;
                }
                catch (AssertionError e)
                {
                    // carry on
                }
                catch (ClassCastException e)
                {
                    String comparatorClassName = expectation.getExpectationComparator().getClass().getName();
                    String expectationClassName = expectation.getExpectation().getClass().getName();
                    String actualClassName = capture.getActual().getClass().getName();
                    throw new RuntimeException(
                            "FAILED - " + expectation.getDescription() + " when invoking Comparator.compare method["
                                    + comparatorClassName
                                    + "]. Could be comparator method parameters are of the wrong type for this expectation class["
                                    + expectationClassName + "] or actual class[" + actualClassName + "].", e);
                }
            }
            if (!expectationSatisfied)
            {
                unsatisfiedExpectations.add(expectation);
            }
        }
        return new ExpectationDifference(unsatisfiedExpectations, new ArrayList<>(copyOfCaptures));
    }

    protected String formatList(List<?> list)
    {
        StringJoiner sj = new StringJoiner("," + System.lineSeparator() + "    ", "[", "]");
        list.stream().map(Object::toString).forEach(sj::add);
        return sj.toString();
    }

    /**
     * Default expectation allows for anything to be provided as an expectation.
     *
     * @author Ikasan Development Team
     */
    protected class DefaultExpectation<T>
    {
        /**
         * Order the DefaultExpectation is declared
         */
        private Integer order;

        /**
         * generic type of expectation
         */
        private T expectation;

        /**
         * expectation description
         */
        private String description;

        /**
         * expectation comparator
         */
        private ExpectationComparator<?, ?> expectationComparator;

        /**
         * Constructor
         *
         * @param expectation
         */
        protected DefaultExpectation(T expectation, ExpectationComparator<?, ?> expectationComparator)
        {
            this.expectation = expectation;
            this.expectationComparator = expectationComparator;
        }

        /**
         * Order the DefaultExpectation is declared
         *
         * @return order
         */
        public Integer getOrder()
        {
            return order;
        }

        /**
         * Order the DefaultExpectation is declared
         *
         * @param order
         */
        public void setOrder(int order)
        {
            this.order = order;
        }

        /**
         * Setter for exception description
         *
         * @param description
         */
        protected void setDescription(String description)
        {
            this.description = description;
        }

        /**
         * Getter for exception description
         */
        protected String getDescription()
        {
            return this.description;
        }

        /**
         * Getter for generic expectation type
         *
         * @return
         */
        protected T getExpectation()
        {
            return expectation;
        }

        /**
         * Getter for expectation comparator
         *
         * @return
         */
        protected ExpectationComparator<?, ?> getExpectationComparator()
        {
            return this.expectationComparator;
        }

        @Override
        public String toString()
        {
            StringBuilder stringBuilder = new StringBuilder("[Expectation");
            if (order != null)
            {
                stringBuilder.append("[")
                        .append(order)
                        .append("]");
            }
            stringBuilder.append(" ");
            if (expectation instanceof AbstractComponent)
            {
                stringBuilder.append("FlowComponent[")
                        .append(expectation.toString())
                        .append("]");
            }
            else
            {
                stringBuilder.append(expectation.toString());
            }
            if (description != null)
            {
                stringBuilder.append(" description[")
                        .append(description)
                        .append("]");
            }
            stringBuilder.append("]");
            return stringBuilder.toString();
        }
    }

    static class ExpectationDifference
    {
        private final List<DefaultExpectation> unsatisfiedExpectations;

        private final List<Capture<?>> unexpectedCaptures;

        ExpectationDifference(List<DefaultExpectation> unsatisfiedExpectations, List<Capture<?>> unexpectedCaptures)
        {
            this.unsatisfiedExpectations = Collections.unmodifiableList(unsatisfiedExpectations);
            this.unexpectedCaptures = Collections.unmodifiableList(unexpectedCaptures);
        }

        public boolean differencesFound()
        {
            return !unsatisfiedExpectations.isEmpty() || !unexpectedCaptures.isEmpty();
        }

        public List<DefaultExpectation> getUnsatisfiedExpectations()
        {
            return unsatisfiedExpectations;
        }

        public List<Capture<?>> getUnexpectedCaptures()
        {
            return unexpectedCaptures;
        }
    }
}