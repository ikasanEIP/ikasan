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

import junit.framework.ComparisonFailure;
import org.apache.log4j.Logger;
import org.ikasan.testharness.flow.Capture;
import org.ikasan.testharness.flow.comparator.ExpectationComparator;
import org.ikasan.testharness.flow.comparator.service.ComparatorService;
import org.junit.Assert;

/**
 * Implementation of a FlowExpectation based on applying the expectations in
 * a strict given order.
 * 
 * @author Ikasan Development Team 
 * 
 */
public class OrderedExpectation extends AbstractListExpectation
{
    /** Logger instance */
    private static Logger logger = Logger.getLogger(OrderedExpectation.class);

    /**
     * Constructor
     */
    public OrderedExpectation()
    {
        super();
    }
    
    /**
     * Contructor allowing an alternate comparator service.
     * @param comparatorService
     */
    public OrderedExpectation(ComparatorService comparatorService)
    {
        super(comparatorService);
    }
    

    /**
     * Is this actual operation satisfied with a corresponding expectation
     * @param actual
     */
    @SuppressWarnings("unchecked")
    public void isSatisfied(Capture<?> actual)
    {
        Assert.assertFalse("FAILED - Not enough expectations specified. Actual behaviour reports next invocation of " +  actual.getActual().getClass().getName(),
                expectations.isEmpty());

        DefaultExpectation expectation = expectations.remove(0);
        ExpectationComparator expectationComparator = expectation.getExpectationComparator();

        try
        {
            expectationComparator.compare(expectation.getExpectation(), actual.getActual());
            logger.info("PASSED - " + expectation.getDescription());
        }
        catch(ComparisonFailure e)
        {
            logger.info("FAILED - " + expectation.getDescription());
            throw e;
        }
        catch(ClassCastException e)
        {
            logger.info("FAILED - " + expectation.getDescription());
            String comparatorClassName = expectationComparator.getClass().getName();
            String expectationClassName = expectation.getExpectation().getClass().getName();
            String actualClassName = actual.getActual().getClass().getName();
            throw new RuntimeException("FAILED - " + expectation.getDescription()
                    + " when invoking Comparator.compare method[" 
                    + comparatorClassName 
                    + "]. Could be comparator method parameters are of the wrong type for this expectation class[" +
                    expectationClassName + "] or actual class[" + actualClassName + "].",e);
        }
    }
}
