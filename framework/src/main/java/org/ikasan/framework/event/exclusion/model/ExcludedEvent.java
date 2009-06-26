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
	 * default constructor required by ORM
	 */
	@SuppressWarnings("unused")
	private ExcludedEvent(){}

	/**
	 * @param event
	 */
	public ExcludedEvent(Event event) {
		this.event=event;
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
	public void setEvent(Event event) {
		this.event = event;
	}

}
