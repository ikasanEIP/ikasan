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

import org.ikasan.framework.component.Event;

/**
 * @author The Ikasan Development Team
 *
 */
public class ExcludedEvent {
	
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

}
