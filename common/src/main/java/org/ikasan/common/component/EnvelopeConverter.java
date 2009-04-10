/*
 * $Id: EnvelopeConverter.java 16606 2009-04-09 08:07:33Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/common/src/main/java/org/ikasan/common/component/EnvelopeConverter.java $
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

// Imported xstream classes
import java.util.List;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;

import org.ikasan.common.CommonRuntimeException;
import org.ikasan.common.Envelope;
import org.ikasan.common.MetaDataInterface;
import org.ikasan.common.Payload;
import org.ikasan.common.ResourceLoader;
import org.ikasan.common.ServiceLocator;

// Imported log4j classes
import org.apache.log4j.Logger;

/**
 * This class provides a default exception message structure. <p/>
 * 
 * @author Jun Suetake
 */
public class EnvelopeConverter extends PayloadConverter
{
    /**
     * The logger instance.
     */
    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(EnvelopeConverter.class);

    /**
     * Creates a new <code>PayloadConverter</code> instance.
     * 
     * @param implementationClass
     */
    public EnvelopeConverter(Class<? extends Envelope> implementationClass)
    {
        super(implementationClass);
    }

    /**
     * Converts an object to textual data.
     */
    @Override
    public void marshal(Object object, HierarchicalStreamWriter writer, MarshallingContext context)
    {
        Envelope envelope = (Envelope) object;
        // XMLSchema Instance NS URI
        if (envelope.getSchemaInstanceNSURI() != null)
        {
            writer.addAttribute("xmlns:xsi", //$NON-NLS-1$
                envelope.getSchemaInstanceNSURI());
        }
        // No Namespace Schema Location
        if (envelope.getNoNamespaceSchemaLocation() != null)
        {
            writer.addAttribute("xsi:noNamespaceSchemaLocation", //$NON-NLS-1$
                envelope.getNoNamespaceSchemaLocation());
        }
        // encapsulate meta-data inside the header
        writer.startNode("header"); //$NON-NLS-1$
        // lifetime id
        if (envelope.getId() != null)
        {
            writer.startNode("id"); //$NON-NLS-1$
            writer.setValue(envelope.getId());
            writer.endNode();
        }
        // timestamp in millis since Java base date
        if (envelope.getTimestamp().longValue() > 0L)
        {
            writer.startNode("timestamp"); //$NON-NLS-1$
            writer.setValue(String.valueOf(envelope.getTimestamp()));
            writer.endNode();
            writer.startNode("timestampFormat"); //$NON-NLS-1$
            writer.setValue(envelope.getTimestampFormat());
            writer.endNode();
        }
        if (envelope.getTimezone() != null)
        {
            writer.startNode("timezone"); //$NON-NLS-1$
            writer.setValue(envelope.getTimezone());
            writer.endNode();
        }
        if (envelope.getPriority().intValue() > -1)
        {
            writer.startNode("priority"); //$NON-NLS-1$
            writer.setValue(String.valueOf(envelope.getPriority()));
            writer.endNode();
        }
        if (envelope.getName() != null)
        {
            writer.startNode("name"); //$NON-NLS-1$
            writer.setValue(envelope.getName());
            writer.endNode();
        }
        if (envelope.getSpec() != null)
        {
            writer.startNode("spec"); //$NON-NLS-1$
            writer.setValue(envelope.getSpec());
            writer.endNode();
        }
        if (envelope.getFormat() != null)
        {
            writer.startNode("format"); //$NON-NLS-1$
            writer.setValue(envelope.getFormat());
            writer.endNode();
        }
        if (envelope.getEncoding() != null)
        {
            writer.startNode("encoding"); //$NON-NLS-1$
            writer.setValue(envelope.getEncoding());
            writer.endNode();
        }
        if (envelope.getCharset() != null)
        {
            writer.startNode("charset"); //$NON-NLS-1$
            writer.setValue(envelope.getCharset());
            writer.endNode();
        }
        if (envelope.getSize().longValue() > 0L)
        {
            writer.startNode("size"); //$NON-NLS-1$
            writer.setValue(String.valueOf(envelope.getSize()));
            writer.endNode();
        }
        if (envelope.getChecksum() != null)
        {
            writer.startNode("checksum"); //$NON-NLS-1$
            writer.setValue(envelope.getChecksum());
            writer.endNode();
        }
        if (envelope.getChecksumAlg() != null)
        {
            writer.startNode("checksumAlg"); //$NON-NLS-1$
            writer.setValue(envelope.getChecksumAlg());
            writer.endNode();
        }
        if (envelope.getProcessIds() != null)
        {
            writer.startNode("processIds"); //$NON-NLS-1$
            writer.setValue(envelope.getProcessIds());
            writer.endNode();
        }
        if (envelope.getSrcSystem() != null)
        {
            writer.startNode("srcSystem"); //$NON-NLS-1$
            writer.setValue(envelope.getSrcSystem());
            writer.endNode();
        }
        if (envelope.getTargetSystems() != null)
        {
            writer.startNode("targetSystems"); //$NON-NLS-1$
            writer.setValue(envelope.getTargetSystems());
            writer.endNode();
        }
        if (envelope.getResubmissionInfo() != null)
        {
            writer.startNode("resubmissionInfo"); //$NON-NLS-1$
            writer.setValue(envelope.getResubmissionInfo());
            writer.endNode();
        }
        // end of header meta-data
        writer.endNode();
        if (envelope.getPayloads() != null)
        {
            writer.startNode("payloads"); //$NON-NLS-1$
            context.convertAnother(envelope.getPayloads());
            writer.endNode();
        }
    }

    /**
     * Converts textual data back into an object.
     * 
     * @param reader
     * @param context
     * @return Object
     */
    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context)
    {
        try
        {
            // TODO - find an easier way to do this...
            //
            // We need to create an initial Envelope to work with, but envelope
            // creation is strict to ensure only valid envelopes are created.
            // Problem is we don't have any valid envelope criteria to start
            // with
            // as its all in the incoming object being unmarshalled.
            //
            // To get round this we create an envelope based on a dummy payload
            // and then clear that dummy payload.
            /*
             * TODO (RD) As above, this implementation is defeating the purpose
             * of the Envelope's constructor. Rather than creating dummy
             * Envelopes and Payloads, get all the header information out into
             * another data sructure such as a map, and the construct the
             * Envelope properly. Create a unit test to test this before this
             * change is made.
             */
            // create dummy payload
            // TODO Global service locator
            ServiceLocator serviceLocator = ResourceLoader.getInstance();
            Payload payload = serviceLocator.getPayloadFactory().newPayload(MetaDataInterface.UNDEFINED, MetaDataInterface.UNDEFINED,
                MetaDataInterface.UNDEFINED);
            // create envelope based on dummy payload
            Envelope envelope = serviceLocator.getEnvelopeFactory().newEnvelope(payload);
            // clear out the dummy payload from the created envelope
            envelope.setPayloads(null);
            // now we're good to go...
            String nodeName = null;
            while (reader.hasMoreChildren())
            {
                reader.moveDown();
                nodeName = reader.getNodeName();
                // if header then go inside
                if (nodeName.equals("header")) //$NON-NLS-1$
                {
                    envelope = unmarshalHeader(envelope, reader);
                }
                // payloads
                else if (nodeName.equals("payloads")) //$NON-NLS-1$
                {
                    List<Payload> payloads = (List<Payload>) context.convertAnother(envelope, List.class);
                    envelope.setPayloads(payloads);
                }
                reader.moveUp();
            }
            return envelope;
        }
        catch (CommonRuntimeException e)
        {
            throw new ConversionException(e);
        }
    }

    /**
     * Converts textual header data back into an object.
     * 
     * @param envelope
     * @param reader
     * @return Envelope
     */
    private Envelope unmarshalHeader(Envelope envelope, HierarchicalStreamReader reader)
    {
        String nodeName = null;
        while (reader.hasMoreChildren())
        {
            reader.moveDown();
            nodeName = reader.getNodeName();
            // schemaInstanceNSURI
            if (nodeName.equals("schemaInstanceNSURI")) //$NON-NLS-1$
            {
                String value = reader.getValue();
                envelope.setId(value);
            }
            // noNamespaceSchemaLocation
            if (nodeName.equals("noNamespaceSchemaLocation")) //$NON-NLS-1$
            {
                String value = reader.getValue();
                envelope.setId(value);
            }
            // lifetime id
            if (nodeName.equals("id")) //$NON-NLS-1$
            {
                String value = reader.getValue();
                envelope.setId(value);
            }
            // timestamp
            else if (nodeName.equals("timestamp")) //$NON-NLS-1$
            {
                String value = reader.getValue();
                try
                {
                    Long longVal = new Long(value);
                    envelope.setTimestamp(longVal);
                }
                catch (NumberFormatException e)
                {
                    throw new ConversionException(e);
                }
            }
            // timestamp format
            else if (nodeName.equals("timestampFormat")) //$NON-NLS-1$
            {
                String value = reader.getValue();
                envelope.setTimestampFormat(value);
            }
            // timezone
            else if (nodeName.equals("timezone")) //$NON-NLS-1$
            {
                String value = reader.getValue();
                envelope.setTimezone(value);
            }
            // Priority
            else if (nodeName.equals("priority")) //$NON-NLS-1$
            {
                String value = reader.getValue();
                try
                {
                    int priority = Integer.parseInt(value);
                    envelope.setPriority(priority);
                }
                catch (NumberFormatException e)
                {
                    throw new ConversionException(e);
                }
            }
            // Name
            else if (nodeName.equals("name")) //$NON-NLS-1$
            {
                String value = reader.getValue();
                envelope.setName(value);
            }
            // Spec
            else if (nodeName.equals("spec")) //$NON-NLS-1$
            {
                String value = reader.getValue();
                envelope.setSpec(value);
            }
            // Format
            else if (nodeName.equals("format")) //$NON-NLS-1$
            {
                String value = reader.getValue();
                envelope.setFormat(value);
            }
            // Encoding
            else if (nodeName.equals("encoding")) //$NON-NLS-1$
            {
                String value = reader.getValue();
                envelope.setEncoding(value);
            }
            // Charset
            else if (nodeName.equals("charset")) //$NON-NLS-1$
            {
                String value = reader.getValue();
                envelope.setCharset(value);
            }
            // Size
            else if (nodeName.equals("size")) //$NON-NLS-1$
            {
                String value = reader.getValue();
                try
                {
                    Long longVal = new Long(value);
                    envelope.setSize(longVal);
                }
                catch (NumberFormatException e)
                {
                    throw new ConversionException(e);
                }
            }
            // checksum
            else if (nodeName.equals("checksum")) //$NON-NLS-1$
            {
                String value = reader.getValue();
                envelope.setChecksum(value);
            }
            // checksumAlg
            else if (nodeName.equals("checksumAlg")) //$NON-NLS-1$
            {
                String value = reader.getValue();
                envelope.setChecksumAlg(value);
            }
            // processIds
            else if (nodeName.equals("processIds")) //$NON-NLS-1$
            {
                String value = reader.getValue();
                envelope.setProcessIds(value);
            }
            // srcSystem
            else if (nodeName.equals("srcSystem")) //$NON-NLS-1$
            {
                String value = reader.getValue();
                envelope.setSrcSystem(value);
            }
            // TargetSystems
            else if (nodeName.equals("targetSystems")) //$NON-NLS-1$
            {
                String value = reader.getValue();
                envelope.setTargetSystems(value);
            }
            // ResubmissionInfo
            else if (nodeName.equals("resubmissionInfo")) //$NON-NLS-1$
            {
                String value = reader.getValue();
                envelope.setResubmissionInfo(value);
            }
            reader.moveUp();
        }
        return envelope;
    }
}
