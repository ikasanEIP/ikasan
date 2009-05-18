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
package org.ikasan.framework.component.transformation;

import java.util.Map;

/**
 * A wrapper class that provides static access to a ThreadLocal controlled map of beans
 * 
 * Threads that call setBeans should later be able to call getBeans to retireve the same
 * map of beans, regardless of what any other thread may do in the meantime
 * 
 * @author Ikasan Development Team
 *
 */
public class ThreadLocalBeansWrapper {

    /**
     * A ThreadLocal map of beans
     */
	private static ThreadLocal<Map<String, Object>> beansWrapper = new ThreadLocal<Map<String, Object>>();
	
	/**
	 * Provides access to beans previously set by this thread
	 * 
	 * @return Map of named java beans
	 */
	public static Map<String, Object> getBeans(){
		return beansWrapper.get();
	}
	/**
	 * Allows the current thread to set its own private beans for later retrieval
	 * 
	 * @param beans Map of named java beans
	 */	
	public static void setBeans(Map<String, Object> beans){
		beansWrapper.set(beans);
	}

	/**
	 * Clears the threadlocal
	 */
	public static void remove() {
		beansWrapper.remove();
		
	}
}
