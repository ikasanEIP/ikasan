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
package org.ikasan.framework.util.grouping;

import java.util.HashMap;
import java.util.Map;

import org.hamcrest.Matcher;

/**
 * Simple named logical group that can identify members that pertain to itself
 * 
 * @author Ikasan Devlopment Team
 *
 */
public class AttributedGroup {

	/**
	 * matcher for members
	 */
	private Matcher<?> matcher;
	
	/**
	 * Name of group
	 */
	private String groupName;
	
	/**
	 * Additional attributes that can be applied collectively to the group
	 */
	private Map<String, Object> attributes = new HashMap<String, Object>();
	
	/**
	 * @param groupName
	 * @param matcher
	 * @param attributes
	 */
	public AttributedGroup(String groupName,
			Matcher<?> matcher, Map<String, Object> attributes) {
		super();
		this.groupName = groupName;
		this.matcher = matcher;
		this.attributes = attributes;
	}


	
	/**
	 * Determines if the specfied Object is a member of this group
	 * 
	 * @param object
	 * @return true if is a memeber
	 */
	public boolean hasAsMember(Object object){
		return matcher.matches(object);
	}
	
	/**
	 * Retrieves a named attribute
	 * 
	 * @param attributeName
	 * @return attribute value or null if non-existent
	 */
	public Object getAttribute(String attributeName){
		return attributes.get(attributeName);
	}



	/**
	 * Accessor for groupName
	 * 
	 * @return groupName
	 */
	public String getGroupName() {
		return groupName;
	}
	
	
}
