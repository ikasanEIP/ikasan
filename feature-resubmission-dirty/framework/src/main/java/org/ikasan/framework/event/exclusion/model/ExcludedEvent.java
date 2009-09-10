/*
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * Copyright (c) 2003-2008 Mizuho International plc. and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the 
 * Free Software Foundation Europe e.V. Talstrasse 110, 40217 Dusseldorf, Germany 
 * or see the FSF site: http://www.fsfeurope.org/.
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
