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
package org.ikasan.framework.flow;

import java.util.ArrayList;
import java.util.List;

/**
 * This class acts as a transfer object holding flow invocation time data relevent only
 * to a single invocation of an Event down a Flow. 
 * 
 * At time of writing, the only data item that we are interested in is the name of the last
 * component invoked, and only then when dealing with an error scenario.
 * 
 * Unlike the Event object, the same FlowInvocation object will be present immediately prior
 * to the invocation of any component in a flow. The Events of course may be split, aggregated, etc.
 * 
 * It remains to be seen if we will need to transport any other data in this object, of if at some
 * later stage, the FlowComponents themselves will need access to this information
 * 
 * @author Ikasan Development Team
 *
 */
public class FlowInvocationContext {
	
	/**
	 * a stack of the names of all components invoked so far
	 */
	private List<String> invokedComponents = new ArrayList<String>();
	
	
	/**
	 * Accessor for the name of the last component invoked
	 * 
	 * @return name of the last component invoked, or null if none exists yet
	 */
	public String getLastComponentName(){
		String lastComponentName = null;
		if (!invokedComponents.isEmpty()){
			lastComponentName = invokedComponents.get(invokedComponents.size()-1);
		}
		return lastComponentName;
	}
	
	/**
	 * Allows a new componentName to be added to the stack of invoked components
	 * 
	 * @param componentName
	 */
	public void addInvokedComponentName(String componentName){
		invokedComponents.add(componentName);
	}

	/**
	 * Safe accessor for the entire stack of invoked components
	 * 
	 * @return List of componentNames of all invoked components
	 */
	public List<String> getInvokedComponents(){
		return new ArrayList<String>(invokedComponents);
	}

}
