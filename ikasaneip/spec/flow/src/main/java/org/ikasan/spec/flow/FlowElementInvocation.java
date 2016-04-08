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
package org.ikasan.spec.flow;

/**
 * Contract for a model of an invocation of a FlowElement. This effectively holds meta-data regarding
 * the invocations of FlowElements for a specific event
 * @author Ikasan Development Team
 *
 */
public interface FlowElementInvocation<IDENTIFIER, METRIC> {

    /**
     * Called before the FlowElement is invoked by the FlowElementInvoker
     * @param flowElement the FlowElement
     */
    void beforeInvocation(FlowElement flowElement);

    /**
     * Called after the FlowElement is invoked by the FlowElementInvoker
     * @param flowElement
     */
    void afterInvocation(FlowElement flowElement);

    /**
     * Get the underlying FlowElement that was invoked
     * @return the FlowElement
     */
    FlowElement getFlowElement();

    /**
     * Get the start time of the invocation in milliseconds
     * @return the epoch time in milliseconds when this FlowElement was invoked
     */
    long getStartTimeMillis();

    /**
     * Get the end time of the invocation in milliseconds
     * @return the epoch time in milliseconds after the FlowElement was invoked
     */
    long getEndTimeMillis();

    /**
     * Returns the identifier for before this invocation
     * @return the IDENTIFIER
     */
    IDENTIFIER getBeforeIdentifier();

    /**
     * Sets the identifier before invocation
     * @param identifier
     */
    void setBeforeIdentifier(IDENTIFIER identifier);

    /**
     * Returns the related identifier for before this invocation
     * @return the IDENTIFIER
     */
    IDENTIFIER getBeforeRelatedIdentifier();

    /**
     * Sets the related identifier after invocation
     * @param relatedIdentifier
     */
    void setBeforeRelatedIdentifier(IDENTIFIER relatedIdentifier);

    /**
     * Returns the identifier for after this invocation
     * @return the IDENTIFIER
     */
    IDENTIFIER getAfterIdentifier();

    /**
     * Sets the identifier before invocation
     * @param identifier
     */
    void setAfterIdentifier(IDENTIFIER identifier);

    /**
     * Returns the related identifier for after this invocation
     * @return the IDENTIFIER
     */
    IDENTIFIER getAfterRelatedIdentifier();

    /**
     * Sets the related identifier after invocation
     * @param relatedIdentifier
     */
    void setAfterRelatedIdentifier(IDENTIFIER relatedIdentifier);


    void addCustomMetric(String name, String value);

    METRIC getCustomMetrics();
}
