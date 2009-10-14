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
package org.ikasan.console.web.command;

import static org.junit.Assert.*;

import java.util.LinkedHashSet;

import org.ikasan.console.web.command.WiretapSearchCriteria;
import org.junit.Test;

/**
 * JUnit based test class for testing WiretapSearchCriteria
 * 
 * @author Ikasan Development Team
 */
public class WiretapSearchCriteriaTest
{
    
    /** Test the constructor and various aspects of its default behaviour */
    @Test
    public void testConstructorWithNullModule()
    {
        WiretapSearchCriteria wiretapSearchCriteria = new WiretapSearchCriteria(null);
        assertNull(wiretapSearchCriteria.getModules());

        wiretapSearchCriteria = new WiretapSearchCriteria(new LinkedHashSet<Long>());
        assertTrue(wiretapSearchCriteria.getModules().isEmpty());
    }

    /** Test the setters and getters */
    @Test
    public void testGettersAndSetters()
    {
        WiretapSearchCriteria wiretapSearchCriteria = new WiretapSearchCriteria(null);

        wiretapSearchCriteria.setModules(new LinkedHashSet<Long>());
        assertTrue(wiretapSearchCriteria.getModules().isEmpty());
        
        // Component Name
        wiretapSearchCriteria.setComponentName("componentName");
        assertEquals("componentName", wiretapSearchCriteria.getComponentName());
        wiretapSearchCriteria.setComponentName("");
        assertNull(wiretapSearchCriteria.getComponentName());

        // Event Id
        wiretapSearchCriteria.setEventId("1");
        assertEquals("1", wiretapSearchCriteria.getEventId());
        wiretapSearchCriteria.setEventId("");
        assertNull(wiretapSearchCriteria.getEventId());
        
        wiretapSearchCriteria.setFromDate("11/10/2009");
        assertEquals("11/10/2009", wiretapSearchCriteria.getFromDate());
        wiretapSearchCriteria.setFromTime("12:00:00");
        assertEquals("12:00:00", wiretapSearchCriteria.getFromTime());

        // Payload Content
        wiretapSearchCriteria.setPayloadContent("payloadContent");
        assertEquals("payloadContent", wiretapSearchCriteria.getPayloadContent());
        wiretapSearchCriteria.setPayloadContent("");
        assertNull(wiretapSearchCriteria.getPayloadContent());
        
        // Payload Id
        wiretapSearchCriteria.setPayloadId("2");
        assertEquals("2", wiretapSearchCriteria.getPayloadId());
        wiretapSearchCriteria.setPayloadId("");
        assertNull(wiretapSearchCriteria.getPayloadId());

        wiretapSearchCriteria.setUntilDate("12/10/2009");
        assertEquals("12/10/2009", wiretapSearchCriteria.getUntilDate());
        wiretapSearchCriteria.setUntilTime("13:00:00");
        assertEquals("13:00:00", wiretapSearchCriteria.getUntilTime());
        
        assertEquals(3595514737829632181L, WiretapSearchCriteria.getSerialVersionUID());
    }
    
    /** Test the getFromDateTime and getUntilDateTime with a NULL date */
    @Test
    public void testDateTimeWithNullDate()
    {
        WiretapSearchCriteria wiretapSearchCriteria = new WiretapSearchCriteria(null);
        wiretapSearchCriteria.setFromDate(null);
        assertNull(wiretapSearchCriteria.getFromDateTime());
        wiretapSearchCriteria.setUntilDate(null);
        assertNull(wiretapSearchCriteria.getUntilDateTime());
    }

    /** Test the getFromDateTime and getUntilDateTime with an empty date */
    @Test
    public void testDateTimeWithAnEmptyDate()
    {
        WiretapSearchCriteria wiretapSearchCriteria = new WiretapSearchCriteria(null);
        wiretapSearchCriteria.setFromDate("");
        assertNull(wiretapSearchCriteria.getFromDateTime());
        wiretapSearchCriteria.setUntilDate("");
        assertNull(wiretapSearchCriteria.getUntilDateTime());
    }

    /** Test valid getFromDateTime and getUntilDateTime with a NULL time */
    @Test
    public void testDateTimeWithNullTime()
    {
        WiretapSearchCriteria wiretapSearchCriteria = new WiretapSearchCriteria(null);
        wiretapSearchCriteria.setFromDate("11/10/2009");
        wiretapSearchCriteria.setFromTime(null);
        assertEquals("Sun Oct 11 00:00:00 BST 2009", wiretapSearchCriteria.getFromDateTime().toString());
        wiretapSearchCriteria.setUntilDate("11/10/2009");
        wiretapSearchCriteria.setUntilTime(null);
        assertEquals("Sun Oct 11 00:00:00 BST 2009", wiretapSearchCriteria.getUntilDateTime().toString());
    }

    /** Test valid getFromDateTime and getUntilDateTime with a empty time */
    @Test
    public void testDateTimeWithEmptyTime()
    {
        WiretapSearchCriteria wiretapSearchCriteria = new WiretapSearchCriteria(null);
        wiretapSearchCriteria.setFromDate("11/10/2009");
        wiretapSearchCriteria.setFromTime("");
        assertEquals("Sun Oct 11 00:00:00 BST 2009", wiretapSearchCriteria.getFromDateTime().toString());
        wiretapSearchCriteria.setUntilDate("11/10/2009");
        wiretapSearchCriteria.setUntilTime("");
        assertEquals("Sun Oct 11 00:00:00 BST 2009", wiretapSearchCriteria.getUntilDateTime().toString());
    }
    
    /** Test valid getFromDateTime and getUntilDateTime with an unparsable dates and times */
    @Test
    public void testCreateDateTimeWithUnParsableDateTime()
    {
        WiretapSearchCriteria wiretapSearchCriteria = new WiretapSearchCriteria(null);
        wiretapSearchCriteria.setFromDate("11102009");
        wiretapSearchCriteria.setFromTime("00:00:00");
        assertNull(wiretapSearchCriteria.getFromDateTime());
        wiretapSearchCriteria.setFromDate("11/10/2009");
        wiretapSearchCriteria.setFromTime("000000");
        assertNull(wiretapSearchCriteria.getFromDateTime());
        wiretapSearchCriteria.setUntilDate("11102009");
        wiretapSearchCriteria.setUntilTime("00:00:00");
        assertNull(wiretapSearchCriteria.getUntilDateTime());
        wiretapSearchCriteria.setUntilDate("11/10/2009");
        wiretapSearchCriteria.setUntilTime("000000");
        assertNull(wiretapSearchCriteria.getUntilDateTime());
    }

}
