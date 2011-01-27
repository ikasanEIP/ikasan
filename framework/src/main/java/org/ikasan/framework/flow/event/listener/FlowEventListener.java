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
package org.ikasan.framework.flow.event.listener;

import org.ikasan.spec.flow.FlowElement;
import org.ikasan.spec.flow.event.FlowEvent;

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
	public void beforeFlow(String moduleName, String flowName, FlowEvent event);

	/**
	 * Callback method to be invoked subsequent to <code>Flow</code> execution
	 * 
	 * @param moduleName - name of the module
	 * @param flowName - name of the flow
	 * @param event - event with which flow was invoked
	 */
	public void afterFlow(String moduleName, String flowName, FlowEvent event);

	/**
	 * Callback method to be invoked prior to <code>FlowElement</code> execution
	 * 
	 * @param moduleName - name of the module
	 * @param flowName - name of the flow
	 * @param flowElement - FlowElement about to be invoked
	 * @param event - event with which flow element is to be invoked
	 */
	public void beforeFlowElement(String moduleName, String flowName,
			FlowElement flowElement, FlowEvent event);
	
	/**
	 * Callback method to be called subsequent to <code>FlowElement</code> execution
	 * 
	 * @param moduleName - name of the module
	 * @param flowName - name of the flow
	 * @param flowElement - FlowElement which was invoked
	 * @param event - event with which flow element was invoked
	 */
	public void afterFlowElement(String moduleName, String flowName,
			FlowElement flowElement, FlowEvent event);

}
