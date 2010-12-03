/*
 * $Id: 
 * $URL:
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
package org.ikasan.common.component;

import org.ikasan.common.Payload;
import org.junit.Assert;
import org.junit.Test;

/**
 * PayloadHelper JUnit test class
 * 
 * @author Ikasan Development Team
 * 
 */
public class DefaultPayloadTest {




	String payloadId = "payloadId";

	/**
	 * Test Payload Cloning
	 * 
	 * @throws CloneNotSupportedException
	 */
	@Test
	public void testPayloadClone() throws CloneNotSupportedException {

		DefaultPayload payload = new DefaultPayload(payloadId, 
				  "This is a test".getBytes());
		
		String attributeName = "someAttributeName";
		String attributeValue = "someAttributeValue";
		payload.setAttribute(attributeName, attributeValue);

		Payload clonePayload = payload.clone();

		// check we have a different object
		Assert.assertFalse(payload == clonePayload);

		Assert.assertEquals(payload.getId(), clonePayload.getId());

		String originalContent = new String(payload.getContent());
		String cloneContent = new String(clonePayload.getContent());
		Assert.assertEquals(originalContent, cloneContent);

		
		Assert.assertEquals(attributeValue, clonePayload.getAttribute(attributeName));

	}
	
	@Test
	public void testSetGetAttribute(){
		DefaultPayload defaultPayload = new DefaultPayload( null, new byte[]{});
		String someAttributeName = "someAttribute";
		String someAttributeValue = "someAttributeValue";
		
		Assert.assertNull("someAttribute should be null before it has been set", defaultPayload.getAttribute(someAttributeName));
		Assert.assertTrue("attributeMap should be empty for starters", defaultPayload.getAttributeMap().isEmpty());
		
		defaultPayload.setAttribute(someAttributeName, someAttributeValue);
		Assert.assertEquals("someAttribute should be available after it has been set", someAttributeValue, defaultPayload.getAttribute(someAttributeName));
		Assert.assertEquals("someAttribute should be availalbe in attributeMap after it has been set", someAttributeValue, defaultPayload.getAttributeMap().get(someAttributeName));
	}
	
	

}
