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
package org.ikasan.flow.event;

import org.ikasan.spec.flow.FlowElement;
import org.ikasan.spec.flow.FlowElementInvocation;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Simple factory for creating FlowElementInvocation objects
 *
 * @author Ikasan Development Team
 */
public class FlowElementInvocationFactory
{

    private FlowElementInvocationFactory(){}

    /**
     * Returns a new FlowElementInvocation object
     * @return a new FlowElementInvocation object
     */
    public static FlowElementInvocation newInvocation()
    {
        return new DefaultFlowElementInvocation();
    }

    /**
     * Default implementation of the FlowElementInvocation
     */
    public static class DefaultFlowElementInvocation implements FlowElementInvocation<Object, List<AbstractMap.SimpleImmutableEntry<String, String>>>
    {
        /** the start and end times (epoch) of the FlowElement invocation */
        private volatile long startTime, endTime;

        /** handle to the FlowElement that is invoked */
        private FlowElement flowElement;

        /** the FlowEvent IDENTIFIER */
        private Object beforeIdentifier;

        /** the FlowEvent IDENTIFIER */
        private Object beforeRelatedIdentifier;

        /** the FlowEvent IDENTIFIER */
        private Object afterIdentifier;

        /** the FlowEvent IDENTIFIER */
        private Object afterRelatedIdentifier;

        /** custom metrics of any name/value */
        private List<AbstractMap.SimpleImmutableEntry<String, String>> customMetrics
                = Collections.synchronizedList(new ArrayList<AbstractMap.SimpleImmutableEntry<String, String>>());


        @Override
        public void beforeInvocation(FlowElement flowElement) {
            startTime = System.currentTimeMillis();
            this.flowElement = flowElement;
        }

        @Override
        public void afterInvocation(FlowElement flowElement) {
            endTime = System.currentTimeMillis();
        }

        @Override
        public FlowElement getFlowElement() {
            return flowElement;
        }

        @Override
        public long getStartTimeMillis() {
            return startTime;
        }

        @Override
        public long getEndTimeMillis() {
            return endTime;
        }

        @Override
        public Object getBeforeIdentifier()
        {
            return beforeIdentifier;
        }

        @Override
        public void setBeforeIdentifier(Object identifier)
        {
            beforeIdentifier = identifier;
        }

        @Override
        public Object getBeforeRelatedIdentifier()
        {
            return beforeRelatedIdentifier;
        }

        @Override
        public void setBeforeRelatedIdentifier(Object relatedIdentifier)
        {
            beforeRelatedIdentifier = relatedIdentifier;
        }

        @Override
        public Object getAfterIdentifier()
        {
            return afterIdentifier;
        }

        @Override
        public void setAfterIdentifier(Object identifier)
        {
            afterIdentifier = identifier;
        }

        @Override
        public Object getAfterRelatedIdentifier()
        {
            return afterRelatedIdentifier;
        }

        @Override
        public void setAfterRelatedIdentifier(Object relatedIdentifier)
        {
            afterRelatedIdentifier = relatedIdentifier;
        }

        @Override
        public void addCustomMetric(String name, String value)
        {
            customMetrics.add(new AbstractMap.SimpleImmutableEntry<>(name, value));
        }

        @Override
        public List<AbstractMap.SimpleImmutableEntry<String, String>> getCustomMetrics()
        {
            return customMetrics;
        }
    }
}
