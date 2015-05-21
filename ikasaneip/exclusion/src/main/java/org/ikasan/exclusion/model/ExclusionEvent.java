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
package org.ikasan.exclusion.model;

import org.ikasan.spec.exclusion.ExclusionService;

import java.util.Arrays;

/**
 * Module implementation representing an ExclusionEvent instance.
 *
 * @author Ikasan Development Team
 */
public class ExclusionEvent
{
    /** module name */
    String moduleName;

    /** flowName */
    String flowName;

    /** unique identifier for this event instance */
    String identifier;

    /** event being excluded */
    byte[] event;

    /** timestamp indicating when this event was created */
    long timestamp;

    /** expiry of this event */
    long expiry;

    /** error uri reported as part of this excluded event */
    String errorUri;

    /**
     * Constructor
     * Defaults timeToLive to one day
     * @param identifier
     */
    public ExclusionEvent(String moduleName, String flowName, String identifier)
    {
        this(moduleName, flowName, identifier, ExclusionService.DEFAULT_TIME_TO_LIVE);
    }

    /**
     * Constructor
     * @param moduleName
     * @param flowName
     * @param identifier
     * @param timeToLive
     */
    public ExclusionEvent(String moduleName, String flowName, String identifier, long timeToLive)
    {
        this.moduleName = moduleName;
        this.flowName = flowName;
        this.identifier = identifier;
        long now = System.currentTimeMillis();
        this.timestamp = now;
        this.expiry = now + timeToLive;
    }

    /**
     * Constructor required by the ORM
     */
    protected ExclusionEvent(){}

    public String getIdentifier() {
        return identifier;
    }

    protected void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    protected void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Long getExpiry() {
        return expiry;
    }

    protected void setExpiry(Long expiry) {
        this.expiry = expiry;
    }

    public String getModuleName() {
        return moduleName;
    }

    protected void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getFlowName() {
        return flowName;
    }

    protected void setFlowName(String flowName) {
        this.flowName = flowName;
    }

    public String getErrorUri() {
        return errorUri;
    }

    public void setErrorUri(String errorUri) {
        this.errorUri = errorUri;
    }

    public byte[] getEvent() {
        return event;
    }

    public void setEvent(byte[] event) {
        this.event = event;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExclusionEvent that = (ExclusionEvent) o;

        if (!flowName.equals(that.flowName)) return false;
        if (!identifier.equals(that.identifier)) return false;
        if (!moduleName.equals(that.moduleName)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = moduleName.hashCode();
        result = 31 * result + flowName.hashCode();
        result = 31 * result + identifier.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ExclusionEvent{" +
                "moduleName='" + moduleName + '\'' +
                ", flowName='" + flowName + '\'' +
                ", identifier='" + identifier + '\'' +
                ", event=" + Arrays.toString(event) +
                ", timestamp=" + timestamp +
                ", expiry=" + expiry +
                ", errorUri='" + errorUri + '\'' +
                '}';
    }
}