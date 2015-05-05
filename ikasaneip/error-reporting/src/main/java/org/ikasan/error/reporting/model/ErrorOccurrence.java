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
package org.ikasan.error.reporting.model;

/**
 * This class represents an occurrence of an error in the system encapsulating as much as 
 * possible about the occurrence of that error
 * 
 * @author Ikasan Development Team
 *
 */
public class ErrorOccurrence<EVENT>
{
    /** unique identifier for this instance */
    private String uri;

    /**
     * name of the module where this error occurred
     */
    private String moduleName;

    /**
     * name of the flow where this error occurred, if it was event/flow related
     */
    private String flowName;

    /**
     * name of the flow element where this error occurred, if it was event/flow related
     */
    private String flowElementName;

    /**
	 * raw dump of the error as it occurred
	 */
	private String errorDetail;

	/**
	 * Id of the event associated with this error, if it was event/flow related
	 */
	private String eventLifeIdentifier;

    /**
     * Related identifier
     */
    private String eventRelatedIdentifier;

    /**
     * Representation of the Event at the time that the error took place
     */
    private EVENT event;

    /**
	 * Time that this error was logged
	 */
	private long timestamp;

    /**
     * useby date for the errorOccurrence, after which the system may delete it
     */
    private long expiry;

    /**
     * Constructor
     */
    private ErrorOccurrence()
    {
        // required by the ORM
    }

    /**
     * Constructor
     * @param moduleName
     * @param flowName
     * @param flowElementName
     * @param errorDetail
     * @param event
     */
    public ErrorOccurrence(String moduleName, String flowName, String flowElementName, String errorDetail, EVENT event)
    {
        this.moduleName = moduleName;
        if(moduleName == null)
        {
            throw new IllegalArgumentException("moduleName cannot be 'null");
        }

        this.flowName = flowName;
        if(flowName == null)
        {
            throw new IllegalArgumentException("flowName cannot be 'null");
        }

        this.flowElementName = flowElementName;
        if(flowElementName == null)
        {
            throw new IllegalArgumentException("flowElementName cannot be 'null");
        }

        this.errorDetail = errorDetail;
        if(errorDetail == null)
        {
            throw new IllegalArgumentException("errorDetail cannot be 'null");
        }

        this.event = event;
        this.timestamp = System.currentTimeMillis();

        this.uri = String.valueOf(this.hashCode());
    }

    /**
     * Constructor
     * @param moduleName
     * @param flowName
     * @param flowElementName
     * @param errorDetail
     */
    public ErrorOccurrence(String moduleName, String flowName, String flowElementName, String errorDetail)
    {
        this(moduleName, flowName, flowElementName, errorDetail, null);
    }

    public String getModuleName() {
        return moduleName;
    }

    private void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getFlowName() {
        return flowName;
    }

    private void setFlowName(String flowName) {
        this.flowName = flowName;
    }

    public String getFlowElementName() {
        return flowElementName;
    }

    private void setFlowElementName(String flowElementName) {
        this.flowElementName = flowElementName;
    }

    public String getErrorDetail() {
        return errorDetail;
    }

    private void setErrorDetail(String errorDetail) {
        this.errorDetail = errorDetail;
    }

    public String getEventLifeIdentifier() {
        return eventLifeIdentifier;
    }

    public void setEventLifeIdentifier(String eventLifeIdentifier) {
        this.eventLifeIdentifier = eventLifeIdentifier;
    }

    public EVENT getEvent() {
        return event;
    }

    private void setEvent(EVENT event) {
        this.event = event;
    }

    public long getTimestamp() {
        return timestamp;
    }

    private void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getExpiry() {
        return expiry;
    }

    public void setExpiry(long expiry) {
        this.expiry = expiry;
    }

    public String getEventRelatedIdentifier() {
        return eventRelatedIdentifier;
    }

    public void setEventRelatedIdentifier(String eventRelatedIdentifier) {
        this.eventRelatedIdentifier = eventRelatedIdentifier;
    }

    public String getUri()
    {
        return this.uri;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ErrorOccurrence that = (ErrorOccurrence) o;

        if (timestamp != that.timestamp) return false;
        if (!errorDetail.equals(that.errorDetail)) return false;
        if (event != null ? !event.equals(that.event) : that.event != null) return false;
        if (!flowElementName.equals(that.flowElementName)) return false;
        if (!flowName.equals(that.flowName)) return false;
        if (!moduleName.equals(that.moduleName)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = moduleName.hashCode();
        result = 31 * result + flowName.hashCode();
        result = 31 * result + flowElementName.hashCode();
        result = 31 * result + errorDetail.hashCode();
        result = 31 * result + (event != null ? event.hashCode() : 0);
        result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
        return result;
    }
}
