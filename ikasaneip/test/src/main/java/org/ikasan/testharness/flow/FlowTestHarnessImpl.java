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
package org.ikasan.testharness.flow;

import org.ikasan.testharness.flow.expectation.service.FlowExpectation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Collections.unmodifiableList;

/**
 * Implementation of the FlowTestHarness as a FlowObserver.
 *
 * @author Ikasan Development Team
 */
public class FlowTestHarnessImpl implements FlowObserver, FlowTestHarness
{
    /**
     * actual captured flow behaviour, synchronized to ensure state is published when read
     */
    private List<Capture<?>> captures = Collections.synchronizedList(new ArrayList<>());

    /**
     * Index for modifying the captures Collection
     */
    private final AtomicInteger capturesIndex = new AtomicInteger(0);

    /**
     * expectations of the flow behaviour
     */
    private FlowExpectation flowExpectation;

    /**
     * Constructor
     *
     * @param flowExpectation
     */
    public FlowTestHarnessImpl(FlowExpectation flowExpectation)
    {
        this.flowExpectation = flowExpectation;
    }

    /**
     * Notification of a behavior in the flow
     *
     * @param actual
     */
    @SuppressWarnings("unchecked")
    public <T> void notify(T actual)
    {
        int index = capturesIndex.getAndIncrement();
        this.captures.add(new Capture(index + 1, actual));
    }

    public void assertIsSatisfied()
    {
        flowExpectation.allSatisfied(unmodifiableList(captures));
    }
}
