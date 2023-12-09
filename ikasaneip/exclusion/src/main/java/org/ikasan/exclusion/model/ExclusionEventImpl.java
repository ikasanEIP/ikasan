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

import jakarta.persistence.*;
import org.ikasan.harvest.HarvestEvent;
import org.ikasan.spec.exclusion.ExclusionEvent;

import java.util.Arrays;

/**
 * ExclusionEvent window instance.
 *
 * @author Ikasan Development Team
 */
@Entity
@Table(name = "ExclusionEvent")
public class ExclusionEventImpl implements ExclusionEvent<Long>, HarvestEvent
{
    /** surrogate id assigned from ORM */
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    /**
     * Name of the module with which the target Flow is associated
     */
    @Column(name = "ModuleName", nullable = false)
    private String moduleName;

    /**
     * Name of the target Flow
     */
    @Column(name = "FlowName", nullable = false)
    private String flowName;

    /** identifier for this event */
    @Column(name = "Identifier", nullable = false)
    String identifier;

    /** original form of the event being excluded */
    @Column(name = "Event", nullable = false)
    byte[] event;

    /** timestamp indicating when this event was created */
    @Column(name = "Timestamp", nullable = false)
    long timestamp;

    /** error uri reported as part of this excluded event */
    @Column(name = "ErrorUri", nullable = false)
    String errorUri;

    /** flag to indicate if the record has been harvested */
    @Column(name = "Harvested")
    boolean harvested;

    /** the time the record was harvested */
    @Column(name = "HarvestedDateTime")
    private long harvestedDateTime;

    /**
     * Constructor
     * @param moduleName
     * @param flowName
     * @param identifier
     * @param event
     * @param errorUri
     */
    public ExclusionEventImpl(String moduleName, String flowName, String identifier, byte[] event, String errorUri)
    {
        this.moduleName = moduleName;
        if(moduleName == null)
        {
            throw new IllegalArgumentException("moduleName cannot be 'null'");
        }

        this.flowName = flowName;
        if(flowName == null)
        {
            throw new IllegalArgumentException("flowName cannot be 'null'");
        }
        this.identifier = identifier;
        if(identifier == null)
        {
            throw new IllegalArgumentException("identifier cannot be 'null'");
        }
        this.event = event;
        if(event == null)
        {
            throw new IllegalArgumentException("event cannot be 'null'");
        }
        this.errorUri = errorUri;
        long now = System.currentTimeMillis();
        this.timestamp = now;
    }

    /**
     * Constructor required by the ORM
     */
    protected ExclusionEventImpl(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getFlowName() {
        return flowName;
    }

    public void setFlowName(String flowName) {
        this.flowName = flowName;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public byte[] getEvent() {
        return event;
    }

    public void setEvent(byte[] event) {
        this.event = event;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getErrorUri() {
        return errorUri;
    }

    public void setErrorUri(String errorUri) {
        this.errorUri = errorUri;
    }

    public boolean isHarvested()
    {
        return harvested;
    }

    public void setHarvested(boolean harvested)
    {
        this.harvested = harvested;
    }

    public long getHarvestedDateTime()
    {
        return harvestedDateTime;
    }

    public void setHarvestedDateTime(long harvestedDateTime)
    {
        this.harvestedDateTime = harvestedDateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExclusionEventImpl that = (ExclusionEventImpl) o;

        if (id != that.id) return false;
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
    public String toString()
    {
        final StringBuffer sb = new StringBuffer("ExclusionEventImpl{");
        sb.append("id=").append(id);
        sb.append(", moduleName='").append(moduleName).append('\'');
        sb.append(", flowName='").append(flowName).append('\'');
        sb.append(", identifier='").append(identifier).append('\'');
        sb.append(", event=");
        if (event == null) sb.append("null");
        else
        {
            sb.append('[');
            for (int i = 0; i < event.length; ++i)
                sb.append(i == 0 ? "" : ", ").append(event[i]);
            sb.append(']');
        }
        sb.append(", timestamp=").append(timestamp);
        sb.append(", errorUri='").append(errorUri).append('\'');
        sb.append(", harvested=").append(harvested);
        sb.append(", harvestedDateTime=").append(harvestedDateTime);
        sb.append('}');
        return sb.toString();
    }
}