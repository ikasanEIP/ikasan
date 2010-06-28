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

import java.util.Arrays;

import junit.framework.Assert;

import org.ikasan.common.Payload;
import org.ikasan.framework.component.Event;
import org.ikasan.testharness.flow.comparator.ExpectationComparator;


/**
 * Compares expected flow event against captured flow event, including
 * 
 * event name is equal
 * event srcSystem is equal
 * payloads size is equal
 * payload content is equal
 * 
 * @author Ikasan Development Team
 * 
 */
public class EventComparator
    implements ExpectationComparator<Event, Event>
{
    /**
     * Compare the two incoming expected and actual events.
     */
    public void compare(Event expected, Event actualEvent)
    {
        Assert.assertEquals("Event name differs. Expected["
                + expected.getName() + "] actual["
                + actualEvent.getName() + "]", expected.getName(), actualEvent.getName());

        Assert.assertEquals("Event srcSystem differs. Expected["
                + expected.getSrcSystem() + "] actual["
                + actualEvent.getSrcSystem() + "]", expected.getSrcSystem(), actualEvent.getSrcSystem());


        int expectedTotalPayloads = expected.getPayloads().size();
        int actualTotalPayloads = actualEvent.getPayloads().size();

        Assert.assertEquals("Payload totals differ. " +
                "Expected[" + expectedTotalPayloads 
                + "] actual[" + actualTotalPayloads + "]", 
                expectedTotalPayloads, actualTotalPayloads);

        for(int plCount = 0; plCount < actualTotalPayloads; plCount++)
        {
            this.compare(expected.getPayloads().get(plCount), actualEvent.getPayloads().get(plCount));
        }
    }

    /**
     * Payload content comparator
     * @param expected
     * @param actual
     */
    protected void compare(Payload expected, Payload actual)
    {
        Assert.assertTrue("Payload content differs. Expected[" 
                + new String(expected.getContent()) 
                + " actual[" + actual.getContent() + "]", 
                Arrays.equals(actual.getContent(), expected.getContent()));
        
    }
}
