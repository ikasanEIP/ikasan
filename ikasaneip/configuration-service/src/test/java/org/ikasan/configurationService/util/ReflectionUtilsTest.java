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
package org.ikasan.configurationService.util;

import org.junit.Test;
import org.springframework.util.Assert;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * Sample configuration class for testing
 * Ikasan Development Team.
 */
public class ReflectionUtilsTest
{
	@Test
	public void test_reflection_on_method() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
		ReflectionUtilsExtendedExampleConfiguration persistedConfiguration = new ReflectionUtilsExtendedExampleConfiguration();
		persistedConfiguration.setInty(Integer.valueOf(10));
		persistedConfiguration.setLongy(Long.valueOf(100));
		persistedConfiguration.setStry("My String");

		ReflectionUtilsExtendedExampleConfiguration runtimeConfiguration = new ReflectionUtilsExtendedExampleConfiguration();
		Assert.isNull(runtimeConfiguration.getInty(), "Integer should be null");
		Assert.isNull(runtimeConfiguration.getLongy(), "Long should be null");
		Assert.isNull(runtimeConfiguration.getStry(), "String should be null");

		Map<String,Object> properties = ReflectionUtils.getPropertiesIgnoringExceptions(persistedConfiguration);
		for(Map.Entry<String,Object> entry:properties.entrySet())
		{
			ReflectionUtils.setProperty(runtimeConfiguration, entry.getKey(), entry.getValue());
		}

		Assert.isTrue(runtimeConfiguration.getInty().equals( persistedConfiguration.getInty() ), "persisted and runtime should have the same Integer instance");
		Assert.isTrue(runtimeConfiguration.getLongy().equals( persistedConfiguration.getLongy() ), "persisted and runtime should have the same Long instance");
		Assert.isTrue(runtimeConfiguration.getStry().equals( persistedConfiguration.getStry() ), "persisted and runtime should have the same String instance");
		Assert.isTrue(runtimeConfiguration.getShorty() == persistedConfiguration.getShorty(), "persisted and runtime should have the same Short instance");
		Assert.isTrue(runtimeConfiguration.getBool().equals( persistedConfiguration.getBool() ), "persisted and runtime should have the same Boolean instance");
		Assert.isTrue(runtimeConfiguration.isPrimBool() == persistedConfiguration.isPrimBool(), "persisted and runtime should have the same primBool instance");
	}
}
