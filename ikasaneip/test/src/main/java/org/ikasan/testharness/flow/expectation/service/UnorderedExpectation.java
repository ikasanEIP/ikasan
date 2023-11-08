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
import org.ikasan.testharness.flow.comparator.service.ComparatorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.Assert.fail;

/**
 * Implementation of the AbstractListExpectation based on applying the expectations in
 * any order.
 *
 * @author Ikasan Development Team
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
     *
     * @param comparatorService
     */
    public UnorderedExpectation(ComparatorService comparatorService)
    {
        super(comparatorService);
    }

    /**
     * Have all expectations been satisfied
     *
     * @param captures Ordered list of captured FlowElement invocations
     * @throws AssertionError if all expected invocations not satisfied
     */
    @Override
    public void allSatisfied(List<Capture<?>> captures)
    {
        ExpectationDifference diff = getExpectationDifference(expectations, captures);
        if (!diff.differencesFound())
        {
            return;
        }
        // @formatter:off
        String format = """
                %n\
                Expected FlowElement invocations in any order:%n\
                  <%s>%n\
                Actual FlowElement invocations:%n\
                  <%s>%n\
                FlowElements not invoked:%n\
                  <%s>%n\
                FlowElements invoked but not expected:%n\
                  <%s>%n\
                """;
        // @formatter:on
        String message = format
            .formatted(formatList(expectations), formatList(captures),
                formatList(diff.getUnsatisfiedExpectations()),
                formatList(diff.getUnexpectedCaptures()));
        fail(message);
    }
}