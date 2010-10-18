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
import java.util.List;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * <code>FactoryBean</code> for <code>FirstStrikeAttributeResolver</code>
 * 
 * Discovers and instantiates underlying <code>AttributeResolver</code>s from
 * classpath XML configuration
 * 
 * 
 * @author Ikasan Devlopment Team
 * 
 */
public class FirstStrikeAttributeResolverFactoryBean implements FactoryBean {

	/**
	 * Constructor
	 * 
	 * @param contextPath
	 *            - path for spring config file(s) that may contain one or more
	 *            <code>AttributeResolver</code> beans
	 */
	public FirstStrikeAttributeResolverFactoryBean(String contextPath) {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				contextPath);

		List<AttributeResolver> attributeResolvers = new ArrayList<AttributeResolver>();

		String[] beanNamesForType = context
				.getBeanNamesForType(AttributeResolver.class);
		for (String beanName : beanNamesForType) {
			AttributeResolver attributeResolver = (AttributeResolver) context
					.getBean(beanName);
			attributeResolvers.add(attributeResolver);
		}

		firstStrikeAttributeResolver = new FirstStrikeAttributeResolver(
				attributeResolvers);
	}

	private FirstStrikeAttributeResolver firstStrikeAttributeResolver;

	public Object getObject() throws Exception {
		return firstStrikeAttributeResolver;
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.FactoryBean#getObjectType()
	 */
	public Class getObjectType() {
		return FirstStrikeAttributeResolverFactoryBean.class;
	}

	public boolean isSingleton() {
		return true;
	}

}
