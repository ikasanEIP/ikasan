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
package org.ikasan.attributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hamcrest.Matcher;

/**
 * Default implementation of <code>AttributeResolver</code>
 * 
 * Utilises an internal <code>org.hamcrest.Matcher</code> to determine
 * applicability to candidate object Also supports subresolvers (additional
 * <code>AttributeResolver</code>s) that will only be consulted if the internal
 * matcher matches.
 * 
 * @author Ikasan Development Team
 * 
 */
public class DefaultAttributeResolver implements AttributeResolver{
	/**
	 * matcher for members
	 */
	private Matcher<?> matcher;

	/**
	 * any additional resolvers that should be consulted of the matcher matches
	 */
	private List<AttributeResolver> subresolvers = new ArrayList<AttributeResolver>();

	/**
	 * Additional attributes that can be applied collectively to the group
	 */
	private Map<String, Object> attributes = new HashMap<String, Object>();

	/**
	 * @param matcher
	 *            - for determining candidate object applicability. If the
	 *            matcher successfully matches the candidate object passed to
	 *            resolveAttributes, then the configured attributes will be
	 *            deemed to apply to that object. Any subresolvers present will
	 *            also then be consulted
	 * @param attributes
	 *            - an arbitrary map of named objects that will be returned
	 *            (subject to overriding) by resolveAttribute if the internal
	 *            matcher matches the candidate object passed
	 */
	public DefaultAttributeResolver(Matcher<?> matcher,
			Map<String, Object> attributes) {
		this.matcher = matcher;
		this.attributes = attributes;
	}

	/**
	 * Setter for subresolvers
	 * 
	 * @param subresolvers
	 *            - an ordered list of additional <code>AttributeResolver</code>
	 *            s that will be consulted by resolveAttribute only if the
	 *            internal matcher matches the candidate object passed. Any
	 *            attributes returned by a later subresolver will be used to
	 *            override those from an earlier one if/when the same attribute
	 *            name is encountered. Likewise, any attribute returned from a
	 *            subresolver that has the same name as an attribute configured
	 *            locally will override that local attribute in the final result
	 *            of resolveAttributes
	 */
	public void setSubresolvers(List<AttributeResolver> subresolvers) {
		this.subresolvers.addAll(subresolvers);
	}

	/**
	 * Resolves any attributes appropriate for specified object
	 * 
	 * @param object
	 * @return any applicable attributes
	 */
	public Map<String, Object> resolveAttributes(Object object) {
		Map<String, Object> result = new HashMap<String, Object>();
		if (matcher.matches(object)) {
			result.putAll(attributes);
		}

		for (AttributeResolver subresolver : subresolvers) {
			result.putAll(subresolver.resolveAttributes(object));
		}

		return result;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer("AttributedGroup [");
		sb.append("matcher=[" + matcher + "]");
		sb.append(", ");
		sb.append("attributes=[" + attributes + "]");
		sb.append("]");
		return sb.toString();
	}

}
