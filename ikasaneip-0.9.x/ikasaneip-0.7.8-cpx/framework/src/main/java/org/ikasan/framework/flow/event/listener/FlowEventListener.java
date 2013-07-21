/*
 * $Id$
 * $URL$
 * 
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
package org.ikasan.framework.flow.event.listener;

import org.ikasan.framework.component.Event;
import org.ikasan.framework.flow.FlowElement;

/**
 * Interface for objects which respond to <code>Flow</code> runtime lifecycle events
 * 
 * @author Ikasan Development Team
 *
 */
public interface FlowEventListener {

	/**
	 * Callback method to be invoked prior to <code>Flow</code> execution
	 * 
	 * @param moduleName - name of the module
	 * @param flowName - name of the flow
	 * @param event - event with which flow is to be invoked
	 */
	public void beforeFlow(String moduleName, String flowName, Event event);

	/**
	 * Callback method to be invoked subsequent to <code>Flow</code> execution
	 * 
	 * @param moduleName - name of the module
	 * @param flowName - name of the flow
	 * @param event - event with which flow was invoked
	 */
	public void afterFlow(String moduleName, String flowName, Event event);

	/**
	 * Callback method to be invoked prior to <code>FlowElement</code> execution
	 * 
	 * @param moduleName - name of the module
	 * @param flowName - name of the flow
	 * @param flowElement - FlowElement about to be invoked
	 * @param event - event with which flow element is to be invoked
	 */
	public void beforeFlowElement(String moduleName, String flowName,
			FlowElement flowElement, Event event);
	
	/**
	 * Callback method to be called subsequent to <code>FlowElement</code> execution
	 * 
	 * @param moduleName - name of the module
	 * @param flowName - name of the flow
	 * @param flowElement - FlowElement which was invoked
	 * @param event - event with which flow element was invoked
	 */
	public void afterFlowElement(String moduleName, String flowName,
			FlowElement flowElement, Event event);

}
