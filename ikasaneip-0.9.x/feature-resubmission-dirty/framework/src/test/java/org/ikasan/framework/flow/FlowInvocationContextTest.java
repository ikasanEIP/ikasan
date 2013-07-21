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

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Ikasan Development Team
 *
 */
public class FlowInvocationContextTest {

	String componentName1 = "componentName1";
	String componentName2 = "componentName2";
	/**
	 * Test method for {@link org.ikasan.framework.flow.FlowInvocationContext#getLastComponentName()}.
	 */
	@Test
	public void testGetLastComponentName_willReturnNullWhenNoComponentsAdded() {
		FlowInvocationContext flowInvocationContext = new FlowInvocationContext();
		Assert.assertNull(flowInvocationContext.getLastComponentName());
	}
	
	/**
	 * Test method for {@link org.ikasan.framework.flow.FlowInvocationContext#getLastComponentName()}.
	 */
	@Test
	public void testGetLastComponentName_willReturnMostRecentlyAddedComponentName() {

		
		FlowInvocationContext flowInvocationContext = new FlowInvocationContext();
		Assert.assertNull(flowInvocationContext.getLastComponentName());
		
		flowInvocationContext.addInvokedComponentName(componentName1);
		Assert.assertEquals(componentName1, flowInvocationContext.getLastComponentName());
		
		flowInvocationContext.addInvokedComponentName(componentName2);
		Assert.assertEquals(componentName2, flowInvocationContext.getLastComponentName());

	}


	/**
	 * Test method for {@link org.ikasan.framework.flow.FlowInvocationContext#getInvokedComponents()}.
	 */
	@Test
	public void testGetInvokedComponents() {
		FlowInvocationContext flowInvocationContext = new FlowInvocationContext();
		flowInvocationContext.addInvokedComponentName(componentName1);
		flowInvocationContext.addInvokedComponentName(componentName2);
		
		List<String> invokedComponents = flowInvocationContext.getInvokedComponents();
		Assert.assertEquals(componentName1, invokedComponents.get(0));
		Assert.assertEquals(componentName2, invokedComponents.get(1));
		
		//check the safety of the returned list - ensure it is a representation only and not the real thing
		invokedComponents.add("componentName3");
		
		Assert.assertFalse(invokedComponents.equals(flowInvocationContext.getInvokedComponents()));

	}

}
