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

import java.util.ArrayList;
import java.util.List;

import org.ikasan.testharness.flow.Capture;
import org.ikasan.testharness.flow.comparator.ExpectationComparator;
import org.ikasan.testharness.flow.comparator.service.ComparatorService;
import org.ikasan.testharness.flow.comparator.service.ComparatorServiceImpl;
import org.ikasan.testharness.flow.expectation.model.DefaultExpectation;
import org.ikasan.testharness.flow.expectation.model.IgnoreExpectation;
import org.junit.Assert;


/**
 * Implementation of a FlowExpecation based on applying the expectations in 
 * a strict given order.
 * 
 * @author Ikasan Development Team 
 * 
 */
public class OrderedExpectation 
    implements FlowExpectation
{
    // list provides ordered expectations
    @SuppressWarnings("unchecked")
    private List<DefaultExpectation> expectations = new ArrayList<DefaultExpectation>();

    /** comparator service for expectations */
    @SuppressWarnings("unchecked")
    private ComparatorService comparatorService;
    
    /**
     * Constructor
     */
    public OrderedExpectation()
    {
        this.comparatorService = getDefaultComparatorService();
    }
    
    /**
     * Default comparator service
     * @return
     */
    @SuppressWarnings("unchecked")
    protected static ComparatorService getDefaultComparatorService()
    {
        return new ComparatorServiceImpl();
    }
    
    /**
     * Contructor allowing an alternate comparator service.
     * @param comparatorService
     */
    @SuppressWarnings({ "unchecked" })
    public OrderedExpectation(ComparatorService comparatorService)
    {
        this.comparatorService = comparatorService;
    }
    
    /**
     * Add an Expectation and specific user defined comparator to the ordered expectations
     * @param expectation
     * @param comparator
     */
    @SuppressWarnings("unchecked")
    public <T> void expectation(T expectation, ExpectationComparator<?,?> expectationComparator)
    {
        comparatorService.addUserComparator(expectation.getClass(), expectationComparator);
        this.expectation(expectation);
    }

    /**
     * Add an Expectation to the ordered expectations
     * @param expectation
     */
    @SuppressWarnings("unchecked")
    public <T> void expectation(T expectation)
    {
        this.expectations.add(new DefaultExpectation(expectation));
    }

    /**
     * Add an ignore operation to the expectations order
     * @param expectation
     */
    @SuppressWarnings("unchecked")
    public <T> void ignore(T expectation)
    {
        this.expectations.add(new DefaultExpectation(new IgnoreExpectation(expectation)));
    }

    /**
     * Is this actual operation satisfied with a corresponding expectation
     * @param capture
     */
    @SuppressWarnings("unchecked")
    public void isSatisfied(Capture<?> actual)
    {
        Assert.assertFalse("Not enough expectations specified for the actual behaviour occurrences", 
                expectations.isEmpty());

        DefaultExpectation expectation = expectations.remove(0);
        ExpectationComparator expectationComparator = comparatorService.getComparator(expectation.getExpectation().getClass());

        try
        {
            expectationComparator.compare(expectation.getExpectation(), actual.getActual());
        }
        catch(ClassCastException e)
        {
            String comparatorClassName = expectationComparator.getClass().getName();
            String expectationClassName = expectation.getExpectation().getClass().getName();
            String actualClassName = actual.getActual().getClass().getName();
            throw new RuntimeException("Failed to invoke Comparator.compare method[" 
                    + comparatorClassName 
                    + "]. Could be comparator method parameters are of the wrong type for this expectation class[" +
                    expectationClassName + "] or actual class[" + actualClassName + "].",e);
        }
    }

    /**
     * Have all expectations been satisfied
     */
    public void allSatisfied()
    {
        Assert.assertTrue("Not all expectations were completed", expectations.isEmpty());
    }
    
}
