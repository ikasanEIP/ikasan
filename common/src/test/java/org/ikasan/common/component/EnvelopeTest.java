/*
 * $Id: EnvelopeTest.java 16606 2009-04-09 08:07:33Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/common/src/test/java/org/ikasan/common/component/EnvelopeTest.java $
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
 * @author Ikasan Team
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
