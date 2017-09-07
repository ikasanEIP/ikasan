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

import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.ikasan.testharness.flow.Capture;
import org.ikasan.testharness.flow.comparator.ExpectationComparator;
import org.ikasan.testharness.flow.comparator.service.ComparatorService;
import org.junit.Assert;
import org.junit.ComparisonFailure;

import java.util.Iterator;

/**
 * Implementation of the AbstractListExpectation based on applying the expectations in
 * any order.
 *
 * @author Ikasan Development Team
 *
 */
public class UnorderedExpectation extends AbstractListExpectation
{
    private static final Logger logger = LoggerFactory.getLogger(UnorderedExpectation.class);

    /**
     * Default constructor that will initialise a default comparator
     */
    public UnorderedExpectation()
    {
        super();
    }

    /**
     * Constructor allowing an alternate comparator service.
     * @param comparatorService
     */
    public UnorderedExpectation(ComparatorService comparatorService)
    {
        super(comparatorService);
    }


    /**
     * Is this actual operation satisfied with a corresponding expectation
     * @param actual the Capture
     */
    @SuppressWarnings("unchecked")
    public void isSatisfied(Capture<?> actual)
    {
        Assert.assertFalse("FAILED - Not enough expectations specified. Actual behaviour reports next invocation of " + actual.getActual().getClass().getName(),
                expectations.isEmpty());

        // iterate through the list of expectations, trying to match one, if found remove else error
        Iterator<DefaultExpectation> iterator = expectations.iterator();
        boolean foundExpectation = false;
        while (iterator.hasNext())
        {
            DefaultExpectation expectation = iterator.next();
            ExpectationComparator expectationComparator = expectation.getExpectationComparator();
            try
            {
                expectationComparator.compare(expectation.getExpectation(), actual.getActual());
                iterator.remove();
                foundExpectation = true;
                logger.info("PASSED - " + expectation.getDescription());
                break;
            }
            catch (ComparisonFailure cfe)
            {
                // we ignore this since we are looking for a match
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

        if (!foundExpectation)
        {
            logger.info("FAILED - NOT IN EXPECTED LIST: " + actual.getActual());
            throw new ComparisonFailure("FAILED", actual.getActual().toString(), "NOT FOUND IN LIST");
        }
    }

}