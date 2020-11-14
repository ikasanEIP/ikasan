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

import org.apache.solr.client.solrj.beans.Field;
import org.ikasan.spec.exclusion.ExclusionEvent;

import java.util.Arrays;

/**
 * ExclusionEvent window instance.
 *
 * @author Ikasan Development Team
 */
public class SolrExclusionEventImpl implements ExclusionEvent<String>
{
    /** surrogate id assigned from ORM */
    @Field("id")
    private String id;

    /** module name */
    @Field("moduleName")
    String moduleName;

    /** flowName */
    @Field("flowName")
    String flowName;

    /** identifier for this event */
    @Field("event")
    String identifier;

    /** original form of the event being excluded */
    @Field("payload")
    String event;

    /** timestamp indicating when this event was created */
    @Field("timestamp")
    long timestamp;

    /** error uri reported as part of this excluded event */
    @Field("id")
    String errorUri;


    /**
     * Constructor. Used by Solr.
     */
    public SolrExclusionEventImpl(){}

    /**
     * Constructor
     *
     * @param id
     * @param moduleName
     * @param flowName
     * @param identifier
     * @param event
     * @param timestamp
     * @param errorUri
     */
    public SolrExclusionEventImpl(String id, String moduleName, String flowName, String identifier, String event, long timestamp, String errorUri) {
        this.moduleName = moduleName;
        this.flowName = flowName;
        this.identifier = identifier;
        this.event = event;
        this.timestamp = timestamp;
        this.errorUri = errorUri;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public byte[] getEvent()
    {
        if(event != null)
        {
            return event.getBytes();
        }
        return "".getBytes();
    }

    public void setEvent(byte[] event) {
        this.event = new String(event);
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

    @Override
    public boolean isHarvested() {
        // Not relevant for solr implementation.
        return true;
    }

    @Override
    public void setHarvested(boolean harvested) {
        // Not relevant for solr implementation.
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SolrExclusionEventImpl that = (SolrExclusionEventImpl) o;

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
    public String toString() {
        return "ExclusionEvent{" +
                "id='" + id + '\'' +
                ", moduleName='" + moduleName + '\'' +
                ", flowName='" + flowName + '\'' +
                ", identifier='" + identifier + '\'' +
                ", event=" + event +
                ", timestamp=" + timestamp +
                ", errorUri='" + errorUri + '\'' +
                '}';
    }
}