/*
 * $Id$
 * $URL$
 * 
 * =============================================================================
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
 * =============================================================================
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
import org.ikasan.common.xml.serializer.PayloadXmlSerializer;

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
        
        ServiceLocator serviceLocator = ResourceLoader.getInstance();
        this.payload = 
            serviceLocator.getPayloadFactory().newPayload(this.payloadName, 
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

        Long size = this.payload.getSize();
        Assert.assertEquals(expectedSize, size);

        String checkSumAlg = this.payload.getChecksumAlg();
        Assert.assertEquals(expectedCheckSumAlg, checkSumAlg);

        String srcSys = this.payload.getSrcSystem();
        Assert.assertEquals(expectedSrcSystem, srcSys);
    }

    /**
     * Test Payload to XML string  
     */
    @Test 
    public void testPayloadToXml() 
    {
        String expectedPayloadXML = 
            "<payload xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" 
            + " ID=\"testID\"" 
            + " TIMESTAMP_FORMATTED=\"20070711132549580\""
            + " TIMESTAMP_FORMAT=\"yyyyMMddHHmmssSSS\""
            + " TIMESTAMP=\"1184160349580\""
            + " TIMEZONE=\"UTC\""
            + " PRIORITY=\"4\""
            + " NAME=\"testPayload\""
            + " SPEC=\"text/xml\""
            + " ENCODING=\"noenc\""
            + " CHARSET=\"windows-1252\"" 
            + " SIZE=\"100\""
            + " CHECKSUM=\"999\""
            + " CHECKSUM_ALG=\"MD5\""
            + " SRC_SYSTEM=\"JUnit\""
            + " TARGET_SYSTEMS=\"testTargetSystems\">&lt;![CDATA[This is a test]]&gt;</payload>";
        
        ServiceLocator serviceLocator = ResourceLoader.getInstance();
        PayloadFactory payloadFactory = serviceLocator.getPayloadFactory();
        this.payload = 
            payloadFactory.newPayload(this.payloadName,
                    Spec.TEXT_XML, this.srcSystem, "This is a test".getBytes());
        this.payload.setCharset("windows-1252");
        this.payload.setChecksum("999");
        this.payload.setChecksumAlg("MD5");
        this.payload.setEncoding("noenc");
        this.payload.setId("testID");
        this.payload.setPriority(new Integer(4));
        this.payload.setSchemaInstanceNSURI("http://www.w3.org/2001/XMLSchema-instance");
        this.payload.setSize(new Long(100));
        this.payload.setTargetSystems("testTargetSystems");
        this.payload.setTimestamp(new Long(1184160349580L));
        this.payload.setTimestampFormat(MetaDataInterface.DEFAULT_TIMESTAMP_FORMAT);
        this.payload.setTimezone("UTC");
 
        
        PayloadXmlSerializer payloadXmlSerializer = new PayloadXmlSerializer(payloadFactory.getPayloadImplClass());

        String xml = payloadXmlSerializer.toXml(this.payload);
        Assert.assertEquals(expectedPayloadXML, xml);
        
        
        
        Payload reconstitutedPayload = payloadXmlSerializer.toObject(expectedPayloadXML);
        Assert.assertTrue(reconstitutedPayload.equals(this.payload));
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
        
        ServiceLocator serviceLocator = ResourceLoader.getInstance();
        this.payload = 
            serviceLocator.getPayloadFactory().newPayload(this.payloadName, 
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

        Assert.assertFalse(this.payload.getSize() == clonePayload.getSize());
        Assert.assertEquals(this.payload.getSize(), clonePayload.getSize());

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
        ServiceLocator serviceLocator = ResourceLoader.getInstance();
        this.payload = 
            serviceLocator.getPayloadFactory().newPayload(this.payloadName, 
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

        Assert.assertFalse(this.payload.getSize() == spawnPayload.getSize());
        Assert.assertEquals(this.payload.getSize(), spawnPayload.getSize());

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
