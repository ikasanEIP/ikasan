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
package org.ikasan.tools.messaging.serialisation;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.ikasan.tools.messaging.model.MapMessageWrapper;
import org.ikasan.tools.messaging.model.MessageWrapper;
import org.ikasan.tools.messaging.model.TextMessageWrapper;
import org.junit.Test;

public class DefaultMessageXmlSerialiserTest {

	private static final String MESSAGE_ID = "messageId";
	private static final String STRING_PROPERTY_NAME = "stringPropertyName";
	private static final String STRING_PROPERTY_VALUE = "stringPropertyValue";
	private static final String INTEGER_PROPERTY_NAME = "integerPropertyName";
	private static final Integer INTEGER_PROPERTY_VALUE = 5;
	private static final String LONG_PROPERTY_NAME = "longPropertyName";
	private static final Long LONG_PROPERTY_VALUE = 7l;
	private static final String BOOLEAN_PROPERTY_NAME = "booleanPropertyName";
	private static final Boolean BOOLEAN_PROPERTY_VALUE = new Boolean(false);
	private static final String DOUBLE_PROPERTY_NAME = "doublePropertyName";
	private static final Double DOUBLE_PROPERTY_VALUE = 9d;	
	private static final String BYTE_PROPERTY_NAME = "bytePropertyName";
	private static final Byte BYTE_PROPERTY_VALUE = 1;	
	private static final String SHORT_PROPERTY_NAME = "shortPropertyName";
	private static final Short SHORT_PROPERTY_VALUE = 2;	
	private static final String BYTE_ARRAY_PROPERTY_NAME = "byteArrayPropertyName";

	
	private static final String MESSAGE_TEXT = "messageText";
	
	private Map<String, Object> properties = new HashMap<String, Object>();
	
	
	
	private DefaultMessageXmlSerialiser serialiser = new DefaultMessageXmlSerialiser();
	
	public DefaultMessageXmlSerialiserTest(){
		properties.put(STRING_PROPERTY_NAME, STRING_PROPERTY_VALUE);
		properties.put(INTEGER_PROPERTY_NAME, INTEGER_PROPERTY_VALUE);
		properties.put(LONG_PROPERTY_NAME, LONG_PROPERTY_VALUE);
		properties.put(BOOLEAN_PROPERTY_NAME, BOOLEAN_PROPERTY_VALUE);
		properties.put(DOUBLE_PROPERTY_NAME, DOUBLE_PROPERTY_VALUE);
		properties.put(BYTE_PROPERTY_NAME, BYTE_PROPERTY_VALUE);
		properties.put(SHORT_PROPERTY_NAME, SHORT_PROPERTY_VALUE);
	}

	@Test
	public void testToFromXml_withTextMessage() {
		TextMessageWrapper textMessageWrapper = new TextMessageWrapper(MESSAGE_TEXT, properties);
		textMessageWrapper.setMessageId(MESSAGE_ID);
		
		String xml = serialiser.toXml(textMessageWrapper);
		
		MessageWrapper reconstituted = serialiser.getMessageObject(xml);
		Assert.assertEquals(textMessageWrapper, reconstituted);
	}
	
	@Test
	public void testToFromXml_withMapMessage() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(STRING_PROPERTY_NAME, STRING_PROPERTY_VALUE);
		map.put(INTEGER_PROPERTY_NAME, INTEGER_PROPERTY_VALUE);
		map.put(LONG_PROPERTY_NAME, LONG_PROPERTY_VALUE);
		map.put(BOOLEAN_PROPERTY_NAME, BOOLEAN_PROPERTY_VALUE);
		map.put(DOUBLE_PROPERTY_NAME, DOUBLE_PROPERTY_VALUE);
		map.put(BYTE_PROPERTY_NAME, BYTE_PROPERTY_VALUE);
		map.put(SHORT_PROPERTY_NAME, SHORT_PROPERTY_VALUE);

		
		MapMessageWrapper mapMessageWrapper = new MapMessageWrapper(map, properties);
		mapMessageWrapper.setMessageId(MESSAGE_ID);
		
		String xml = serialiser.toXml(mapMessageWrapper);
		
		MessageWrapper reconstituted = serialiser.getMessageObject(xml);
		Assert.assertEquals(mapMessageWrapper, reconstituted);
	}



}
