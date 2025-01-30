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
package org.ikasan.spec.history;

import java.util.Set;

/**
 * Ikasan MessageHistoryEvent Value Object.
 *
 * @author Ikasan Development Team
 */
public interface ComponentInvocationMetric<ID, METRIC, EVENT> {

    /**
     * Retrieves the name of the component.
     *
     * @return the name of the component as a String
     */
    String getComponentName();

    /**
     * Returns the identifier of the event that occurred before the current event.
     *
     * @return the identifier of the event that occurred before the current event
     */
    ID getBeforeEventIdentifier();

    /**
     * Returns the identifier of the related event that occurred just before the current event.
     *
     * @return the identifier of the related event before the current event
     */
    ID getBeforeRelatedEventIdentifier();

    /**
     * Get the identifier of the event that comes after the current event in the component invocation.
     *
     * @return the identifier of the event that comes after the current event
     */
    ID getAfterEventIdentifier();

    /**
     * Get the identifier of the event that occurred after a related event.
     *
     * @return Identifier of the event that occurred after a related event
     */
    ID getAfterRelatedEventIdentifier();

    /**
     * Retrieve the start time of the component invocation in milliseconds.
     *
     * @return the start time of the component invocation in milliseconds
     */
    long getStartTimeMillis();

    /**
     * Get the end time in milliseconds of the ComponentInvocationMetric event.
     *
     * @return the end time in milliseconds
     */
    long getEndTimeMillis();

    /**
     * Returns a set of metrics associated with the ComponentInvocationMetric.
     *
     * @return Set of metrics associated with the ComponentInvocationMetric
     */
    Set<METRIC> getMetrics();

    /**
     * Set the metrics for the ComponentInvocationMetric.
     *
     * @param metrics a Set of metrics to be set for the ComponentInvocationMetric
     */
    void setMetrics(Set<METRIC> metrics);

    /**
     * Retrieves the wiretap flow event associated with this component invocation metric.
     *
     * @return The wiretap flow event, or null if not available.
     */
    public EVENT getWiretapFlowEvent();

    /**
     * Set the wiretap flow event for this ComponentInvocationMetric.
     * This event represents a point in the flow where monitoring or data interception occurs.
     *
     * @param wiretapFlowEvent the wiretap flow event to set
     */
    public void setWiretapFlowEvent(EVENT wiretapFlowEvent);

    /**
     * Get the Flow Invocation details.
     *
     * @return FlowInvocationMetric instance containing information about module name, flow name, invocation start time,
     *         invocation end time, final action, invocation events, harvested flag, harvested date time, metric expiry,
     *         and error URI.
     */
    public FlowInvocationMetric getFlowInvocation();

    /**
     * Set the FlowInvocationMetric for this ComponentInvocationMetric.
     *
     * @param flowInvocation the FlowInvocationMetric to set
     */
    public void setFlowInvocation(FlowInvocationMetric flowInvocation);
}
