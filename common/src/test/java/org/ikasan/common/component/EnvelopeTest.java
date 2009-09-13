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
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;

import org.apache.log4j.Logger;
import org.junit.*;

import org.ikasan.common.Envelope;
import org.ikasan.common.MetaDataInterface;
import org.ikasan.common.Payload;
import org.ikasan.common.ResourceLoader;
import org.ikasan.common.ServiceLocator;
import org.ikasan.common.factory.EnvelopeFactory;
import org.ikasan.common.factory.PayloadFactory;
import org.ikasan.common.xml.serializer.EnvelopeXmlSerializer;

import junit.framework.JUnit4TestAdapter;

/**
 * EnvelopeHelper JUnit test class
 *
 * @author Ikasan Development Team
 */
public class EnvelopeTest
{
    /** The logger */
    private static Logger logger = Logger.getLogger(EnvelopeTest.class);

    /** envelope instance being tested */
    Envelope envelope;

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
     * Creation of an envelope from an incoming primary payload.
     */
    @Test
    public void testNewEnvelopeFromPayload()
    {
        ServiceLocator serviceLocator = ResourceLoader.getInstance();
        String expectedNoNamespaceSchemaLocation = null;
        String expectedSchemaInstanceNSURI =
            XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI;
        String expectedTimezone = "UTC";
        Integer expectedPriority = new Integer(Priority.NORMAL.getLevel());
        Payload payload = serviceLocator.getPayloadFactory().newPayload(this.payloadName, Spec.TEXT_PLAIN, this.srcSystem, "This is a test".getBytes());
        String expectedTimestampFormat = MetaDataInterface.DEFAULT_TIMESTAMP_FORMAT;
        String expectedName = this.payloadName;
        String expectedEncoding = Encoding.NOENC.toString();
        String expectedFormat = null;
        String expectedCharSet = Charset.defaultCharset().toString();
        String expectedCheckSumAlg = MetaDataInterface.DEFAULT_CHECKSUM_ALG;
        String expectedSrcSystem = this.srcSystem;

        this.envelope = serviceLocator.getEnvelopeFactory().newEnvelope(payload);

        String noNamespaceSchemaLocation = this.envelope.getNoNamespaceSchemaLocation();
        Assert.assertEquals(expectedNoNamespaceSchemaLocation,
                noNamespaceSchemaLocation);

        String schemaInstanceNSURI = this.envelope.getSchemaInstanceNSURI();
        Assert.assertEquals(expectedSchemaInstanceNSURI,
                schemaInstanceNSURI);

        String timezone = this.envelope.getTimezone();
        Assert.assertEquals(expectedTimezone, timezone);

        String timestampFormat = this.envelope.getTimestampFormat();
        Assert.assertEquals(expectedTimestampFormat, timestampFormat);

        Integer priority = this.envelope.getPriority();
        Assert.assertEquals(expectedPriority, priority);

        List<Payload> payloads = this.envelope.getPayloads();
        Assert.assertEquals(payload, payloads.get(0));

        String name = this.envelope.getName();
        Assert.assertEquals(expectedName, name);

        String encoding = this.envelope.getEncoding();
        Assert.assertEquals(expectedEncoding, encoding);

        String format = this.envelope.getFormat();
        Assert.assertEquals(expectedFormat, format);

        String charSet = this.envelope.getCharset();
        Assert.assertEquals(expectedCharSet, charSet);

        String checkSumAlg = this.envelope.getChecksumAlg();
        Assert.assertEquals(expectedCheckSumAlg, checkSumAlg);

        String srcSys = this.envelope.getSrcSystem();
        Assert.assertEquals(expectedSrcSystem, srcSys);
    }

    /**
     * Creation of an envelope based on the incoming payload list.
     */
    @Test
    public void testNewEnvelopeFromPayloadList()
    {
        String expectedNoNamespaceSchemaLocation = null;
        String expectedSchemaInstanceNSURI =
            XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI;
        String expectedTimezone = "UTC";
        Integer expectedPriority = new Integer(Priority.NORMAL.getLevel());
        ServiceLocator serviceLocator = ResourceLoader.getInstance();
        Payload payload = serviceLocator.getPayloadFactory().newPayload("testPayload", Spec.TEXT_PLAIN, "testSrcSystem", "This is a test".getBytes());
        Payload supplementaryPayload1 = serviceLocator.getPayloadFactory().newPayload(this.payloadName, Spec.TEXT_PLAIN, this.srcSystem, "This is a test".getBytes());
        Payload supplementaryPayload2 = serviceLocator.getPayloadFactory().newPayload(this.payloadName, Spec.TEXT_PLAIN, this.srcSystem, "This is a test".getBytes());
        List<Payload> expectedPayloads = new ArrayList<Payload>();
        expectedPayloads.add(payload);
        expectedPayloads.add(supplementaryPayload1);
        expectedPayloads.add(supplementaryPayload2);
        String expectedTimestampFormat = MetaDataInterface.DEFAULT_TIMESTAMP_FORMAT;
        String expectedName = this.payloadName;
        String expectedEncoding = Encoding.NOENC.toString();
        String expectedFormat = null;
        String expectedCharSet = Charset.defaultCharset().toString();
        String expectedCheckSumAlg = MetaDataInterface.DEFAULT_CHECKSUM_ALG;
        String expectedSrcSystem = "testSrcSystem";

        this.envelope = serviceLocator.getEnvelopeFactory().newEnvelope(expectedPayloads);

        String noNamespaceSchemaLocation = this.envelope.getNoNamespaceSchemaLocation();
        Assert.assertEquals(expectedNoNamespaceSchemaLocation,
                noNamespaceSchemaLocation);

        String schemaInstanceNSURI = this.envelope.getSchemaInstanceNSURI();
        Assert.assertEquals(expectedSchemaInstanceNSURI,
                schemaInstanceNSURI);

        String timezone = this.envelope.getTimezone();
        Assert.assertEquals(expectedTimezone, timezone);

        String timestampFormat = this.envelope.getTimestampFormat();
        Assert.assertEquals(expectedTimestampFormat, timestampFormat);

        Integer priority = this.envelope.getPriority();
        Assert.assertEquals(expectedPriority, priority);

        List<Payload> payloads = this.envelope.getPayloads();
        Assert.assertEquals(expectedPayloads, payloads);

        String name = this.envelope.getName();
        Assert.assertEquals(expectedName, name);

        String encoding = this.envelope.getEncoding();
        Assert.assertEquals(expectedEncoding, encoding);

        String format = this.envelope.getFormat();
        Assert.assertEquals(expectedFormat, format);

        String charSet = this.envelope.getCharset();
        Assert.assertEquals(expectedCharSet, charSet);

        String checkSumAlg = this.envelope.getChecksumAlg();
        Assert.assertEquals(expectedCheckSumAlg, checkSumAlg);

        String srcSys = this.envelope.getSrcSystem();
        Assert.assertEquals(expectedSrcSystem, srcSys);
    }

    /**
     * Creation of an envelope based on the incoming payload list.  
     */
    @Test
    public void testXMLToEnvelopeToXML() 
    {
        ServiceLocator serviceLocator = ResourceLoader.getInstance();
        Payload payload = serviceLocator.getPayloadFactory().newPayload("ImPrimary",
                Spec.TEXT_PLAIN, this.srcSystem, "This is a test".getBytes());
        Payload supplementaryPayload1 = serviceLocator.getPayloadFactory().newPayload(this.payloadName,
                Spec.TEXT_PLAIN, this.srcSystem, "This is a test".getBytes());
        Payload supplementaryPayload2 = serviceLocator.getPayloadFactory().newPayload(this.payloadName,
                Spec.TEXT_PLAIN, this.srcSystem, "This is a test".getBytes());
        List<Payload> expectedPayloads = new ArrayList<Payload>();
        expectedPayloads.add(payload);
        expectedPayloads.add(supplementaryPayload1);
        expectedPayloads.add(supplementaryPayload2);

        EnvelopeFactory envelopeFactory = serviceLocator.getEnvelopeFactory();
        PayloadFactory payloadFactory = serviceLocator.getPayloadFactory();
        Class<? extends Payload> payloadClass = payloadFactory.getPayloadImplClass();
        Class<? extends Envelope> envelopeClass = envelopeFactory.getEnvelopeImplClass();
        
        this.envelope = envelopeFactory.newEnvelope(expectedPayloads);

        EnvelopeXmlSerializer envelopeXmlSerializer = new EnvelopeXmlSerializer(payloadClass, envelopeClass);
        String xml = envelopeXmlSerializer.toXml(this.envelope);

        Envelope reconstitutedEnvelope = envelopeXmlSerializer.toObject(xml);
        Assert.assertTrue(reconstitutedEnvelope.equals(this.envelope));
    }

    /**
     * Tear down
     */
    @After public void tearDown()
    {
        logger.info("tearDown"); //$NON-NLS-1$
    }

    /**
     * JUnit suite
     * @return Test
     */
    public static junit.framework.Test suite()
    {
        return new JUnit4TestAdapter(EnvelopeTest.class);
    }
}
