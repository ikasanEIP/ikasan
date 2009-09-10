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
package org.ikasan.common.component;

import java.nio.charset.Charset;
import javax.xml.XMLConstants;

import org.apache.log4j.Logger;
import org.junit.*;

import org.ikasan.common.MetaDataInterface;
import org.ikasan.common.Payload;
import org.ikasan.common.ResourceLoader;
import org.ikasan.common.ServiceLocator;
import org.ikasan.common.factory.PayloadFactory;

import junit.framework.JUnit4TestAdapter;

/**
 * PayloadHelper JUnit test class
 * 
 * @author Ikasan Development Team
 *
 */
public class PayloadTest
{
    /** The logger */
    private static Logger logger = Logger.getLogger(PayloadTest.class);
    
    /** Payload instance being tested */
    Payload payload;
    /** Payload is required as a minimum to create an envelope - payload name */
    String payloadName = "testPayload"; //$NON-NLS-1$
    /** Payload is required as a minimum to create an envelope - payload source system */
    String srcSystem = "JUnit"; //$NON-NLS-1$
    
    /**
     * Setup
     */
    @Before public void setUp()
    {
        logger.info("setUp"); //$NON-NLS-1$
    }
    
    /**
     * Creation of a payload.
     */
    @Test 
    public void testNewPayload()
    {
        String expectedNoNamespaceSchemaLocation = null;
        String expectedSchemaInstanceNSURI = 
            XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI;
        String expectedTimezone = "UTC";
        Integer expectedPriority = new Integer(Priority.NORMAL.getLevel());
        String expectedContent = "This is a test";
        String expectedTimestampFormat = MetaDataInterface.DEFAULT_TIMESTAMP_FORMAT;
        String expectedName = this.payloadName;
        String expectedEncoding = Encoding.NOENC.toString();
        String expectedFormat = null;
        String expectedCharSet = Charset.defaultCharset().toString();
        Long expectedSize = new Long(14);
        String expectedCheckSumAlg = MetaDataInterface.DEFAULT_CHECKSUM_ALG;
        String expectedSrcSystem = this.srcSystem;
        

        this.payload = new DefaultPayload("id", this.payloadName, 
                    Spec.TEXT_PLAIN, this.srcSystem, "This is a test".getBytes());
        
        String noNamespaceSchemaLocation = this.payload.getNoNamespaceSchemaLocation();
        Assert.assertEquals(expectedNoNamespaceSchemaLocation, 
                noNamespaceSchemaLocation);
        
        String schemaInstanceNSURI = this.payload.getSchemaInstanceNSURI();
        Assert.assertEquals(expectedSchemaInstanceNSURI, 
                schemaInstanceNSURI);
        
        String timezone = this.payload.getTimezone();
        Assert.assertEquals(expectedTimezone, timezone);
        
        String timestampFormat = this.payload.getTimestampFormat();
        Assert.assertEquals(expectedTimestampFormat, timestampFormat);
        
        Integer priority = this.payload.getPriority();
        Assert.assertEquals(expectedPriority, priority);
        
        String content = new String(this.payload.getContent());
        Assert.assertEquals(expectedContent, content);
        
        String name = this.payload.getName();
        Assert.assertEquals(expectedName, name);
        
        String encoding = this.payload.getEncoding();
        Assert.assertEquals(expectedEncoding, encoding);

        String format = this.payload.getFormat();
        Assert.assertEquals(expectedFormat, format);

        String charSet = this.payload.getCharset();
        Assert.assertEquals(expectedCharSet, charSet);

        Long size = this.payload.size();
        Assert.assertEquals(expectedSize, size);

        String checkSumAlg = this.payload.getChecksumAlg();
        Assert.assertEquals(expectedCheckSumAlg, checkSumAlg);

        String srcSys = this.payload.getSrcSystem();
        Assert.assertEquals(expectedSrcSystem, srcSys);
    }



    /**
     * Test Payload Cloning  
     * @throws CloneNotSupportedException 
     */
    @Test 
    public void testPayloadClone()
        throws CloneNotSupportedException
    {
//        String expectedNoNamespaceSchemaLocation = null;
//        String expectedSchemaInstanceNSURI = 
//            XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI;
//        String expectedTimezone = "UTC";
//        Integer expectedPriority = new Integer(Priority.NORMAL.getLevel());
//        String expectedContent = "This is a test";
//        String expectedTimestampFormat = MetaDataInterface.DEFAULT_TIMESTAMP_FORMAT;
//        String expectedName = this.payloadName;
//        String expectedEncoding = Encoding.NOENC.toString();
//        String expectedFormat = null;
//        String expectedCharSet = Charset.defaultCharset().toString();
//        Long expectedSize = 14L;
//        String expectedCheckSumAlg = MetaDataInterface.DEFAULT_CHECKSUM_ALG;
//        String expectedSrcSystem = this.srcSystem;
        

        this.payload = new DefaultPayload("id", this.payloadName, 
                    Spec.TEXT_PLAIN, this.srcSystem, "This is a test".getBytes());
        
        Payload clonePayload = this.payload.clone();

        // check we have a different object
        Assert.assertFalse(this.payload == clonePayload);

        // check object values are the same
        Assert.assertEquals(this.payload.getNoNamespaceSchemaLocation(), 
                clonePayload.getNoNamespaceSchemaLocation());
       
        Assert.assertEquals(this.payload.getSchemaInstanceNSURI(), 
                clonePayload.getSchemaInstanceNSURI());
        
        Assert.assertEquals(this.payload.getId(), 
                clonePayload.getId());

        Assert.assertEquals(this.payload.getTimestamp(), clonePayload.getTimestamp());
        Assert.assertEquals(this.payload.getFormattedTimestamp(), clonePayload.getFormattedTimestamp());
        Assert.assertEquals(this.payload.getTimezone(), clonePayload.getTimezone());
        
        Assert.assertEquals(this.payload.getTimestampFormat(), clonePayload.getTimestampFormat());
        
        Assert.assertFalse(this.payload.getPriority() == clonePayload.getPriority());
        Assert.assertEquals(this.payload.getPriority(), clonePayload.getPriority());
        
        String originalContent = new String(this.payload.getContent());
        String cloneContent = new String(clonePayload.getContent());
        Assert.assertEquals(originalContent, cloneContent);
        
        Assert.assertEquals(this.payload.getName(), clonePayload.getName());
        
        Assert.assertEquals(this.payload.getEncoding(), clonePayload.getEncoding());

        Assert.assertEquals(this.payload.getFormat(), clonePayload.getFormat());

        Assert.assertEquals(this.payload.getCharset(), clonePayload.getCharset());

        Assert.assertTrue(this.payload.size() == clonePayload.size());

        Assert.assertEquals(this.payload.getChecksum(), clonePayload.getChecksum());
        Assert.assertEquals(this.payload.getChecksumAlg(), clonePayload.getChecksumAlg());

        Assert.assertEquals(this.payload.getSpec(), clonePayload.getSpec());
        Assert.assertEquals(this.payload.getSrcSystem(), clonePayload.getSrcSystem());
        Assert.assertEquals(this.payload.getProcessIds(), clonePayload.getProcessIds());
        Assert.assertEquals(this.payload.getResubmissionInfo(), clonePayload.getResubmissionInfo());
        Assert.assertEquals(this.payload.getTargetSystems(), clonePayload.getTargetSystems());
    }

    /**
     * Test Payload spawning
     * @throws CloneNotSupportedException 
     * @throws InterruptedException 
     */
    @Test 
    public void testPayloadSpawn()
        throws CloneNotSupportedException, InterruptedException
    {
//        String expectedNoNamespaceSchemaLocation = null;
//        String expectedSchemaInstanceNSURI = 
//            XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI;
//        String expectedTimezone = "UTC";
//        Integer expectedPriority = new Integer(Priority.NORMAL.getLevel());
//        String expectedContent = "This is a test";
//        String expectedTimestampFormat = MetaDataInterface.DEFAULT_TIMESTAMP_FORMAT;
//        String expectedName = this.payloadName;
//        String expectedEncoding = Encoding.NOENC.toString();
//        String expectedFormat = null;
//        String expectedCharSet = Charset.defaultCharset().toString();
//        Long expectedSize = 14L;
//        String expectedCheckSumAlg = MetaDataInterface.DEFAULT_CHECKSUM_ALG;
//        String expectedSrcSystem = this.srcSystem;
//        

        this.payload = new DefaultPayload("id", this.payloadName, 
                    Spec.TEXT_PLAIN, this.srcSystem, "This is a test".getBytes());

        // put a sleep in here to allow us to ensure a clean comparison 
        // of timestamps between the original payload and the spawned payload
        Thread.sleep(100);
        
        Payload spawnPayload = this.payload.spawn();

        // check we have a different object
        Assert.assertFalse(this.payload == spawnPayload);

        // check object values are the same
        Assert.assertEquals(this.payload.getNoNamespaceSchemaLocation(), 
                spawnPayload.getNoNamespaceSchemaLocation());
       
        Assert.assertEquals(this.payload.getSchemaInstanceNSURI(), 
                spawnPayload.getSchemaInstanceNSURI());
        
        // these fields must be different
        Assert.assertFalse(this.payload.getId().equals(spawnPayload.getId()));
        Assert.assertFalse(this.payload.getTimestamp().equals(spawnPayload.getTimestamp()));
        Assert.assertFalse(this.payload.getFormattedTimestamp().equals(spawnPayload.getFormattedTimestamp()));

        // this is potentially different, but not for the current implementation
        Assert.assertEquals(this.payload.getTimezone(), spawnPayload.getTimezone());
        
        Assert.assertEquals(this.payload.getTimestampFormat(), spawnPayload.getTimestampFormat());
        
        Assert.assertFalse(this.payload.getPriority() == spawnPayload.getPriority());
        Assert.assertEquals(this.payload.getPriority(), spawnPayload.getPriority());
        
        String originalContent = new String(this.payload.getContent());
        String cloneContent = new String(spawnPayload.getContent());
        Assert.assertEquals(originalContent, cloneContent);
        
        Assert.assertEquals(this.payload.getName(), spawnPayload.getName());
        
        Assert.assertEquals(this.payload.getEncoding(), spawnPayload.getEncoding());

        Assert.assertEquals(this.payload.getFormat(), spawnPayload.getFormat());

        Assert.assertEquals(this.payload.getCharset(), spawnPayload.getCharset());

        Assert.assertTrue(this.payload.size() == spawnPayload.size());

        Assert.assertEquals(this.payload.getChecksum(), spawnPayload.getChecksum());
        Assert.assertEquals(this.payload.getChecksumAlg(), spawnPayload.getChecksumAlg());

        Assert.assertEquals(this.payload.getSpec(), spawnPayload.getSpec());
        Assert.assertEquals(this.payload.getSrcSystem(), spawnPayload.getSrcSystem());
        Assert.assertEquals(this.payload.getProcessIds(), spawnPayload.getProcessIds());
        Assert.assertEquals(this.payload.getResubmissionInfo(), spawnPayload.getResubmissionInfo());
        Assert.assertEquals(this.payload.getTargetSystems(), spawnPayload.getTargetSystems());
    }

    /**
     * Teardown
     */
    @After public void tearDown()
    {
        logger.info("tearDown");
    }

    /**
     * Test suite
     * @return Test
     */
    public static junit.framework.Test suite() 
    {
        return new JUnit4TestAdapter(PayloadTest.class);
    }    
}
