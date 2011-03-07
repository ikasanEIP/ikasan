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
package org.ikasan.framework.event.wiretap.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;

import junit.framework.JUnit4TestAdapter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * This test class supports the <code>WiretapEvent</code> class.
 * 
 * @author Ikasan Development Team
 */
public class WiretapEventTest
{
	
	// reference to the instance under test
	private WiretapEvent wiretapEvent;	
	
	// initialize the values for the required fields 
	final String moduleName = "moduleName";
    final String flowName = "flowName";
    final String componentName = "componentName";
    final String eventId = "eventId";
    final String payloadId = "payloadId";     
    final String payloadContent = "payloadContent";
    
    // variables to be initialized in the setUp() 
    long currentTimeInMillis;  
    Date expiry;  
    
    
    /**
     * setUp() runs before each test
     */
    @Before
    public void setUp()
    {
    	// set the expiry date
		currentTimeInMillis = System.currentTimeMillis();
		expiry = new Date(currentTimeInMillis + 10000);
		
	    // create an instance to be tested
	    wiretapEvent = new WiretapEvent(moduleName, flowName, componentName, eventId, payloadId,payloadContent, expiry);	    
    }
    
    /**
	 * tearDown() runs after each test
	 */
    @After
	public void tearDown()
	{
		// reset the members after each test
		currentTimeInMillis = 0;
		expiry = null;		
	}    

    /**
     * Test successful wiretap save with an event containing a single payload.
     */
    @Test
    public void test_createWiretapEvent()
    {        
        // create the instance to be tested
        WiretapEvent wiretapEvent = new WiretapEvent(moduleName, flowName, componentName, eventId, payloadId, new String(payloadContent.getBytes()), expiry);
         
        assertEquals(wiretapEvent.getModuleName(), this.moduleName);
        assertEquals(wiretapEvent.getFlowName(), this.flowName);
        assertEquals(wiretapEvent.getComponentName(), this.componentName);
        assertEquals(wiretapEvent.getEventId(), this.eventId);
        assertEquals(wiretapEvent.getPayloadId(), this.payloadId);
        assertEquals(new String(wiretapEvent.getPayloadContent()), this.payloadContent);
        assertTrue(wiretapEvent.getCreated().getTime() < wiretapEvent.getExpiry().getTime());
    }
    
    /**
	 * Tests WiretapEventHeader objects for Serializable interface implementation.
	 * This round-trip test makes sure both the original and the serialized objects are equal. 
	 */
	@Test
	public void test_serializeWiretapEvent() throws IOException, ClassNotFoundException
	{
		// serialize
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(wiretapEvent);
		oos.close();
		
		// deserialize
		byte[] inputBytes = baos.toByteArray();
		InputStream is = new ByteArrayInputStream(inputBytes);
		ObjectInputStream ois = new ObjectInputStream(is);
		Object obj = ois.readObject();
		WiretapEvent copyObj = (WiretapEvent) obj;
		
		// test the deserialized object with the original 
		assertEquals(copyObj.getModuleName(), this.moduleName);
        assertEquals(copyObj.getFlowName(), this.flowName);
        assertEquals(copyObj.getComponentName(), this.componentName);
        assertEquals(copyObj.getEventId(), this.eventId);
        assertEquals(copyObj.getPayloadId(), this.payloadId);
        assertEquals(copyObj.getPayloadContent(), this.payloadContent);
		assertEquals(copyObj.getExpiry(), new Date(this.currentTimeInMillis + 10000));		
	}	
    
    /**
     * The suite is this class
     * 
     * @return JUnit Test class
     */
    public static junit.framework.Test suite()
    {
        return new JUnit4TestAdapter(WiretapEventTest.class);
    }
}
