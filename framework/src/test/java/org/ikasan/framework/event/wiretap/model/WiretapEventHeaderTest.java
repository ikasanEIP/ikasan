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
 * This test class supports the <code>WiretapEventHeader</code> class.
 * 
 * @author Ikasan Development Team
 */
public class WiretapEventHeaderTest
{
	// reference to the instance under test
	private WiretapEventHeader wiretapEventHeader;	
	
	// initialize the values for required fields 
	final String moduleName = "moduleName";
    final String flowName = "flowName";
    final String componentName = "componentName";
    final String eventId = "eventId";
    final String payloadId = "payloadId";        
    
    // variables to be initialized in the setUp() 
    long currentTimeInMillis;  
    Date expiry;  
    	
    
	/**
	 * setUp() runs before each test 	 
	 */
	@Before
	public void setup()
	{
		// set the expiry date
		currentTimeInMillis = System.currentTimeMillis();
		expiry = new Date(currentTimeInMillis + 10000);
		
	    // create an instance to be tested
	    wiretapEventHeader = new WiretapEventHeader(moduleName, flowName, componentName, eventId, payloadId, expiry);	    
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
	 * Tests the creation of a WiretapEventHeader instance 	  
	 */	
	@Test
	public void test_createWiretapEventHeader()
	{  
		// values for the optional member variables
		final Long nextPayloadId = new Long(4);
		final Long previousPayloadId = new Long(2);
		
		// set the optional member variables	    
	    wiretapEventHeader.setNextByPayload(nextPayloadId);
	    wiretapEventHeader.setPreviousByPayload(previousPayloadId);
		
        assertEquals(wiretapEventHeader.getModuleName(), this.moduleName);
        assertEquals(wiretapEventHeader.getFlowName(), this.flowName);
        assertEquals(wiretapEventHeader.getComponentName(), this.componentName);
        assertEquals(wiretapEventHeader.getEventId(), this.eventId);
        assertEquals(wiretapEventHeader.getPayloadId(), this.payloadId);        
		assertEquals(wiretapEventHeader.getExpiry(), new Date(this.currentTimeInMillis + 10000));
		assertEquals(wiretapEventHeader.getNextByPayload(), nextPayloadId);
		assertEquals(wiretapEventHeader.getPreviousByPayload(), previousPayloadId);
		assertTrue(wiretapEventHeader.getCreated().getTime() < wiretapEventHeader.getExpiry().getTime());		
	}
	
	/**
	 * Tests WiretapeventHeader objects for the Comparable interface implementation	 
	 */
	@Test
	public void test_compareWiretapEventHeader()
	{
		// create the instances to be tested 
		WiretapEventHeader wiretapEventheader1 = new WiretapEventHeader();
		WiretapEventHeader wiretapEventheader2 = new WiretapEventHeader();
		WiretapEventHeader wiretapEventheader3 = new WiretapEventHeader();
		
		// set the Id of each instances
		wiretapEventheader1.setId(new Long(1));
		wiretapEventheader2.setId(new Long(2));
		wiretapEventheader3.setId(new Long(1));
		
		// test for the comparable implementation 
		assertTrue(wiretapEventheader1.compareTo(wiretapEventheader2) < 0);
		assertTrue(wiretapEventheader2.compareTo(wiretapEventheader1) > 0);
		assertTrue(wiretapEventheader1.compareTo(wiretapEventheader3) == 0);		
	}
	
	/**
	 * Tests WiretapEventHeader objects for Serializable interface implementation.
	 * This round-trip test make sure both the original and the serialized objects are equal. 
	 */
	@Test
	public void test_serializeWiretapEventHeader() throws IOException, ClassNotFoundException
	{
		// serialize
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(wiretapEventHeader);
		oos.close();
		
		// deserialize
		byte[] inputBytes = baos.toByteArray();
		InputStream is = new ByteArrayInputStream(inputBytes);
		ObjectInputStream ois = new ObjectInputStream(is);
		Object obj = ois.readObject();
		WiretapEventHeader copyObj = (WiretapEventHeader) obj;
		
		// compare the deserialized object with the original 
		assertEquals(copyObj.getModuleName(), this.moduleName);
        assertEquals(copyObj.getFlowName(), this.flowName);
        assertEquals(copyObj.getComponentName(), this.componentName);
        assertEquals(copyObj.getEventId(), this.eventId);
        assertEquals(copyObj.getPayloadId(), this.payloadId);
		assertEquals(copyObj.getExpiry(), new Date(this.currentTimeInMillis + 10000));		
	}	
	
	/**
     * The suite is this class
     * 
     * @return JUnit Test class
     */
    public static junit.framework.Test suite()
    {
        return new JUnit4TestAdapter(WiretapEventHeaderTest.class);
    }	
}
