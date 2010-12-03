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
package org.ikasan.framework.event.exclusion.model;

import java.util.Date;
import java.util.List;

import java.lang.IllegalStateException;

import org.ikasan.framework.component.Event;
import org.ikasan.framework.error.model.ErrorOccurrence;

/**
 * @author The Ikasan Development Team
 *
 */
public class ExcludedEvent {
	
	/**
	 * Resolution indicator for a Resubmitted ExcludedEvent
	 */
	public static final String RESOLUTION_RESUBMITTED = "Resubmitted";
	
	/**
	 * Resolution indicator for a Cancelled ExcludedEvent
	 */
	public static final String RESOLUTION_CANCELLED = "Cancelled";
	
	
	/**
	 * unique identifier set by ORM
	 */
	private Long id;
	
	/**
	 * Event being excluded
	 */
	private Event event;
	
	/**
	 * Name of the module from which the event is being excluded
	 */
	private String moduleName;

	/**
	 * Name of the flow from which the event is being excluded
	 */
	private String flowName;
	
	/**
	 * Date/time of event exclusion
	 */
	private Date exclusionTime;

	/**
	 * Date/time of event resubmission
	 */
	private Date lastUpdatedTime;
	
	/**
	 * Indication of how this ExcludedEvent was resolved (ie cancelled, resubmitted)
	 */
	private String resolution = null;
	
	

	/**
	 * Name of user who updated (resubmitted, canceled) 
	 */
	private String lastUpdatedBy;
	
	/** Error Occurrences related to this ExcludedEvent */
	private List<ErrorOccurrence> errorOccurrences;
	


	/**
	 * default constructor required by ORM
	 */
	@SuppressWarnings("unused")
	private ExcludedEvent(){}

	/**
	 * Constructor
	 * 
	 * @param event - the event being excluded
	 * @param moduleName - module from which event is excluded
	 * @param flowName - flow from which event is excluded
	 * @param exclusionTime - date/time of event exclusion
	 */
	public ExcludedEvent(Event event, String moduleName, String flowName, Date exclusionTime) {
		this.event=event;
		this.moduleName=moduleName;
		this.flowName=flowName;
		this.exclusionTime = exclusionTime;
	}
	
	
	/**
	 * Marks the ExcludedEvent as Resubmitted, setting the lastUpdatedBy and lastUpdatedTime fields
	 * 
	 * @param resolver to be used for lastUpdatedBy
	 */
	public void resolveAsResubmitted(String resolver){
	    resolve(resolver, RESOLUTION_RESUBMITTED);
	}
	
	/**
	 * Marks the ExcludedEvent as Cancelled, setting the lastUpdatedBy and lastUpdatedTime fields
	 * 
	 * @param resolver to be used for lastUpdatedBy
	 */	
	public void resolveAsCancelled(String resolver){
	    resolve(resolver, RESOLUTION_CANCELLED);
	}
	
	private void resolve(String resolver, String resolution) throws IllegalStateException {
		if (isResolved()){
			throw new IllegalStateException("Cannot resolve excludedEvent ["+id+"], as it was previously resolved as ["+resolution+"]");
		}
		setLastUpdatedTime(new Date());
	    setLastUpdatedBy(resolver);
	    setResolution(resolution);
	}

	/**
	 * Accessor for id
	 * 
	 * @return id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Mutator for id
	 * 
	 * @param id
	 */
	@SuppressWarnings("unused")
	private void setId(Long id) {
		this.id = id;
	}

	/**
	 * Accessor for event
	 * 
	 * @return event
	 */
	public Event getEvent() {
		return event;
	}

	/**
	 * Mutator for event
	 * 
	 * @param event
	 */
	@SuppressWarnings("unused")
	private void setEvent(Event event) {
		this.event = event;
	}
	
	/**
	 * Accessor for moduleName
	 * 
	 * @return moduleName
	 */
	public String getModuleName() {
		return moduleName;
	}

	/**
	 * Mutator for moduleName
	 * 
	 * @param moduleName
	 */
	@SuppressWarnings("unused")
	private void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	/**
	 * Accessor for flowName
	 * 
	 * @return flowName
	 */
	public String getFlowName() {
		return flowName;
	}

	/**
	 * Mutator for flowName
	 * 
	 * @param flowName
	 */
	@SuppressWarnings("unused")
	private void setFlowName(String flowName) {
		this.flowName = flowName;
	}

	/**
	 * Accessor for exclusionTime
	 * 
	 * @return exclusionTime
	 */
	public Date getExclusionTime() {
		return exclusionTime;
	}

	/**
	 * Mutator for exclusionTime
	 * 
	 * @param exclusionTime
	 */
	@SuppressWarnings("unused")
	private void setExclusionTime(Date exclusionTime) {
		this.exclusionTime = exclusionTime;
	}

	/**
	 * Accessor for resubmissionTime
	 * 
	 * @return resubmissionTime
	 */
	public Date getLastUpdatedTime() {
		return lastUpdatedTime;
	}

	/**
	 * Mutator for lastUpdatedTime
	 * 
	 * @param lastUpdatedTime
	 */
	public void setLastUpdatedTime(Date lastUpdatedTime) {
		this.lastUpdatedTime = lastUpdatedTime;
	}

	/**
	 * Accessor for lastUpdatedBy
	 * @return lastUpdatedBy
	 */ 
	public String getLastUpdatedBy() {
		return lastUpdatedBy;
	}

	/**
	 * Mutator for lastUpdatedBy
	 * 
	 * @param lastUpdatedBy
	 */
	public void setLastUpdatedBy(String lastUpdatedBy) {
		this.lastUpdatedBy = lastUpdatedBy;
	}
	
	/**
	 * Accessor for errorOccurrences
	 * 
	 * @return listing of occurrences of errors processing the Event
	 */
	public List<ErrorOccurrence> getErrorOccurrences() {
		return errorOccurrences;
	}

	/**
	 * Mutator for errorOccurrences
	 * TODO would be great if this didnt need to be public
	 * 
	 * @param errorOccurrences
	 */
	public void setErrorOccurrences(List<ErrorOccurrence> errorOccurrences) {
		this.errorOccurrences = errorOccurrences;
	}

	/**
	 * Shortcut for determining if this has been resolved
	 * 
	 * @return true if resolution exists
	 */
	public boolean isResolved() {
		return resolution!=null;
	}
	
	/**
	 * Accessor for resolution 
	 * 
	 * @return resolution
	 */
	public String getResolution(){
		return resolution;
	}
	
	/**
	 * Mutator for resolution
	 * 
	 * @param resolution
	 */
	@SuppressWarnings("unused")
	private void setResolution(String resolution){
		this.resolution = resolution;
	}

}
