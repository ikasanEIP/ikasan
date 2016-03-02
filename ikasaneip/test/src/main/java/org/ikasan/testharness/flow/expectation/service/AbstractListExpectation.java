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
import org.ikasan.testharness.flow.expectation.model.IgnoreExpectation;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract expectation based on a List of DefaultExpectations
 *
 * @author Ikasan Development Team
 */
public abstract class AbstractListExpectation implements FlowExpectation
{
    protected List<DefaultExpectation> expectations = new ArrayList<>();

    /** comparator service for expectations */
    protected ComparatorService comparatorService;

    public AbstractListExpectation()
    {
        this.comparatorService = getDefaultComparatorService();
    }

    /**
     * Constructor allowing an alternate comparator service.
     * @param comparatorService
     */
    public AbstractListExpectation(ComparatorService comparatorService)
    {
        this.comparatorService = comparatorService;
    }


    /**
     * Default comparator service
     * @return
     */
    protected static ComparatorService getDefaultComparatorService()
    {
        return new ComparatorServiceImpl();
    }



    /**
     * Add an Expectation and specific user defined comparator to the ordered expectations
     * @param expectation
     * @param expectationComparator
     */
    @SuppressWarnings("unchecked")
    public <T> void expectation(T expectation, ExpectationComparator<?,?> expectationComparator)
    {
        addExpectation( new DefaultExpectation(expectation, expectationComparator), null );
    }

    /**
     * Add an Expectation and specific user defined comparator to the ordered expectations
     * and provide a specific description to help identity this expectation at runtime.
     * @param expectation
     * @param expectationComparator
     * @param description
     */
    @SuppressWarnings("unchecked")
    public <T> void expectation(T expectation, ExpectationComparator<?,?> expectationComparator, String description)
    {
        addExpectation( new DefaultExpectation(expectation, expectationComparator), description );
    }

    /**
     * Add an Expectation to the ordered expectations
     * @param expectation
     */
    @SuppressWarnings("unchecked")
    public <T> void expectation(T expectation)
    {
        ExpectationComparator expectationComparator = comparatorService.getComparator(expectation.getClass());
        addExpectation( new DefaultExpectation(expectation, expectationComparator), null );
    }

    /**
     * Add an Expectation to the ordered expectations and provide a description
     * to help identify this expectation at runtime.
     * @param expectation
     * @param description
     */
    @SuppressWarnings("unchecked")
    public <T> void expectation(T expectation, String description)
    {
        ExpectationComparator expectationComparator = comparatorService.getComparator(expectation.getClass());
        addExpectation( new DefaultExpectation(expectation, expectationComparator), description );
    }

    /**
     * Add an ignore operation to the expectations order
     * @param expectation
     */
    @SuppressWarnings("unchecked")
    public <T> void ignore(T expectation)
    {
        addExpectation( new DefaultExpectation(new IgnoreExpectation(expectation), new IgnoreComparator()), null );
    }

    /**
     * Add an ignore operation to the expectations order and provide
     * a description to help identify this expectation at runtime.
     * @param expectation
     * @param description
     */
    @SuppressWarnings("unchecked")
    public <T> void ignore(T expectation, String description)
    {
        addExpectation( new DefaultExpectation(new IgnoreExpectation(expectation), new IgnoreComparator()), description );
    }

    /**
     * Append the expectation to the already defined expectations
     * @param defaultExpectation
     * @param description
     */
    protected void addExpectation(DefaultExpectation<?> defaultExpectation, String description)
    {
        defaultExpectation.setDescription("Expectation[" + (this.expectations.size() + 1)
                + "] " + ((description == null) ? "":description) );
        this.expectations.add(defaultExpectation);
    }

    /**
     * Is this actual operation satisfied with a corresponding expectation
     * @param actual
     */
    @SuppressWarnings("unchecked")
    public abstract void isSatisfied(Capture<?> actual);


    /**
     * Have all expectations been satisfied
     */
    public void allSatisfied()
    {
        Assert.assertTrue("[" + expectations.size() + "] expectations not satisfied. Outstanding expectations ["
                + expectations.toString() + "]", expectations.isEmpty());
    }


    /**
     * Default expectation allows for anything to be provided as an expectation.
     *
     * @author Ikasan Development Team
     *
     */
    protected class DefaultExpectation<T>
    {
        /** expectation description */
        private String description;

        /** generic type of expectation */
        private T expectation;

        /** expectation comparator */
        private ExpectationComparator<?,?> expectationComparator;

        /**
         * Constructor
         * @param expectation
         */
        protected DefaultExpectation(T expectation, ExpectationComparator<?,?> expectationComparator)
        {
            this.expectation = expectation;
            this.expectationComparator = expectationComparator;
        }

        /**
         * Setter for exception description
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
         * @return
         */
        protected T getExpectation()
        {
            return expectation;
        }

        /**
         * Getter for expectation comparator
         * @return
         */
        protected ExpectationComparator<?,?> getExpectationComparator()
        {
            return this.expectationComparator;
        }

        @Override
        public String toString()
        {
            return "Expectation description[" + description + "] detail[" + expectation.toString() + "]";
        }
    }
}