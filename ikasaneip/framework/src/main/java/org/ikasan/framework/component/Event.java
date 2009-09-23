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
package org.ikasan.framework.component;

import java.util.ArrayList;
import java.util.List;

import org.ikasan.framework.FrameworkException;
import org.ikasan.framework.ResourceLoader;
import org.ikasan.common.component.MetaData;
import org.ikasan.common.component.PayloadHelper;
import org.ikasan.common.component.Priority;
import org.ikasan.common.component.Spec;
import org.ikasan.common.factory.EnvelopeFactory;
import org.ikasan.common.Administrator;
import org.ikasan.common.CommonException;
import org.ikasan.common.Envelope;
import org.ikasan.common.MetaDataInterface;
import org.ikasan.common.Payload;
import org.ikasan.common.ServiceLocator;

// Imported log4j classes
import org.apache.log4j.Logger;

/**
 * Event provides the transport framing object for all payloads and associated
 * data across synchronous hops ie JMS -> JMS.
 * 
 * @author Ikasan Development Team
 */
public class Event extends MetaData implements Cloneable
{
    /** Serialize ID */
    private static final long serialVersionUID = 1L;
    /** Logger instance */
    private static Logger logger = Logger.getLogger(Event.class);
    /** Event constants */
    public static String UNDEFINED = "UNDEFINED"; //$NON-NLS-1$
    /** No event ids message */
    public static String NO_EVENT_IDS_AVAILABLE = "No event ids available!"; //$NON-NLS-1$
    /** thread id that created the Event */
    private Long threadId = new Long(0L);
    /** Component Group Name currently handling the Event */
    private String componentGroupName = null;
    /** Component in the Component Group Name handling the Event */
    private String componentName = null;
    /** method name handling the Event */
    private String methodName = null;
    /** Event contained payloads */
    private List<Payload> payloads = null;
    /** scratchpad objects which may be created and destroyed as needed */
    private List<Object> scratchPad = null;
    /** Payloads in their original form */
    private List<Payload> originalPayloads = null;
    /** Any encountered exception being passed to the exception handler */
    private Throwable exception = null;
    /**
     * administrator instance of the caller - useful for caller details from the
     * invoked bean
     */
    private Administrator caller = null;

    /**
     * New Event based on no incoming args. This will create an Event with an
     * empty payload list.
     * 
     * @param componentGroupName
     * @param componentName
     */
    public Event(String componentGroupName, String componentName)
    {
        //
        // pass an empty payload arraylist
        this(new ArrayList<Payload>(), componentGroupName, componentName);
        this.name = Event.UNDEFINED;
    }

    /**
     * New event createed from the incoming Envelope.
     * 
     * @param envelope
     * @param componentGroupName
     * @param componentName
     */
    public Event(final Envelope envelope, String componentGroupName, String componentName)
    {
        this(envelope.getPayloads(), true, componentGroupName, componentName);
        setEventAttribsFromEnvelope(envelope);
    }

    /**
     * Sets the event from an envelope
     * 
     * @param envelope
     */
    public void setEventAttribsFromEnvelope(final Envelope envelope)
    {
        this.id = envelope.getId();
        this.timestamp = envelope.getTimestamp();
        this.timestampFormat = envelope.getTimestampFormat();
        this.timezone = envelope.getTimezone();
        this.priority = envelope.getPriority();
        this.name = envelope.getName();
        this.spec = envelope.getSpec();
        this.encoding = envelope.getEncoding();
        this.format = envelope.getFormat();
        this.charset = envelope.getCharset();
        this.size = envelope.getSize();
        this.checksum = envelope.getChecksum();
        this.checksumAlg = envelope.getChecksumAlg();
        this.srcSystem = envelope.getSrcSystem();
        this.targetSystems = envelope.getTargetSystems();
        this.processIds = envelope.getProcessIds();
    }

    /**
     * Returns a completely new instance of the payload with a deep copy of all
     * fields. Note the subtle difference in comparison with spawn() which
     * changes some field values to reflect a newly created instance.
     * 
     * @return cloned Event
     * @throws CloneNotSupportedException
     * 
     */
    @Override
    public Event clone() throws CloneNotSupportedException
    {
        Event clone = (Event) super.clone();
        if (this.getTimestamp() != null) clone.setTimestamp(new Long(this.getTimestamp()));
        if (this.getThreadId() != null) clone.setThreadId(new Long(this.getThreadId()));
        if (this.getPriority() != null) clone.setPriority(new Integer(this.getPriority()));
        if (this.getSize() != null) clone.setSize(new Long(this.getSize()));
        //
        // populate actual payload(s)
        List<Payload> clonedPayloads = new ArrayList<Payload>();
        for (Payload payload : this.getPayloads())
        {
            clonedPayloads.add(payload.clone());
        }
        clone.payloads = clonedPayloads;
        //
        // populate original copies for later use, if requested
        List<Payload> clonedOriginalPayloads = new ArrayList<Payload>();
        for (Payload originalPayload : this.getOriginalPayloads())
        {
            clonedOriginalPayloads.add(originalPayload.clone());
        }
        clone.originalPayloads = clonedOriginalPayloads;
        //
        // TODO - scratchpad is tricky as they are undefined objects
        // leave this empty for now
        clone.scratchPad = new ArrayList<Object>();
        //
        // TODO - revisit the exception setting as this will wrap
        if (this.getException() != null) clone.setException(new CommonException(this.getException()));
        //
        // TODO - just copy the reference for the caller (admin object)
        // as this will be deprecated in the short term
        if (this.getCaller() != null) clone.setCaller(this.caller);
        logger.info("Cloned " + this.idToString() //$NON-NLS-1$
                + "within [" + this.getComponentName() + "] [" //$NON-NLS-1$//$NON-NLS-2$
                + this.getComponentGroupName() + "]."); //$NON-NLS-1$
        return clone;
    }

    /**
     * Returns a completely new instance of the payload with a deep copy of all
     * fields with the exception of id and timestamp which are populated with
     * new values to reflect that this is a distinctly new instance from the
     * original. Note the subtle difference in comparison with clone() which
     * does not change any fields from their original values.
     * 
     * @return spawned Event
     * @throws CloneNotSupportedException
     * 
     */
    public Event spawn() throws CloneNotSupportedException
    {
        Event spawned = this.clone();
        spawned.setId(generateId());
        spawned.setTimestamp(generateTimestamp());
        return spawned;
    }

    /**
     * Event Constructor
     * 
     * @param payload
     * @param componentGroupName
     * @param componentName
     */
    public Event(final Payload payload, String componentGroupName, String componentName)
    {
        this(payload, true, componentGroupName, componentName);
    }

    /**
     * Event Constructor
     * 
     * @param payload
     * @param keepOriginal
     * @param componentGroupName
     * @param componentName
     */
    public Event(final Payload payload, final boolean keepOriginal, String componentGroupName, String componentName)
    {
        this(componentGroupName, componentName);
        this.setPayload(payload);
        if (keepOriginal)
        {
            this.setOriginalPayload(payload);
        }
    }

    /**
     * Event constructor based on incoming payload list.
     * 
     * @param payloads
     * @param componentGroupName
     * @param componentName
     */
    public Event(final List<Payload> payloads, String componentGroupName, String componentName)
    {
        //
        // create the event based on the incoming payloads
        // and default to keeping a copy of the original payload in the event
        this(payloads, true, componentGroupName, componentName);
    }

    /**
     * Event constructor based on incoming payload list and specified option of
     * keeping an original copy of the incoming payloads within the event.
     * 
     * @param payloads
     * @param keepOriginal a copy of the incoming payloads in their original
     *            form
     * @param componentGroupName
     * @param componentName
     */
    public Event(final List<Payload> payloads, final boolean keepOriginal, String componentGroupName, String componentName)
    {
        //
        // timestamp it immediately
        this.timestamp = generateTimestamp();
        this.timestampFormat = MetaDataInterface.DEFAULT_TIMESTAMP_FORMAT;
        this.timezone = MetaDataInterface.DEFAULT_TIMEZONE;
        this.name = Event.UNDEFINED;
        this.id = generateId();
        this.threadId = new Long(Thread.currentThread().getId());
        //
        // populate actual payload(s)
        this.payloads = new ArrayList<Payload>();
        this.payloads.addAll(payloads);
        //
        // populate original copies for later use, if requested
        this.originalPayloads = new ArrayList<Payload>();
        if (keepOriginal) this.originalPayloads.addAll(payloads);
        //
        // allow for general scratch area for any other object transports
        this.scratchPad = new ArrayList<Object>();
        //
        // initialise the exception
        this.exception = null;
        //
        // initialise the events current component details
        this.setComponentGroupName(componentGroupName);
        this.setComponentName(componentName);
        this.methodName = ""; //$NON-NLS-1$
        //
        // set priority to default if no payload defined; otherwise
        // set the priority to the priority of the highest incoming payload
        Payload pl = EventHelper.getPayloadHighestPriority(payloads);
        if (pl == null)
            this.priority = new Integer(Priority.NORMAL.getLevel());
        else
            this.priority = pl.getPriority();
        logger.info("Created " + this.idToString() //$NON-NLS-1$
                + "within [" + this.getComponentName() + "] [" //$NON-NLS-1$//$NON-NLS-2$
                + this.getComponentGroupName() + "]."); //$NON-NLS-1$
    }

    /**
     * Sets thread ID.
     * 
     * @param threadId
     */
    protected final void setThreadId(final Long threadId)
    {
        this.threadId = threadId;
        logger.debug("Thread ID set to [" + this.threadId + "]."); //$NON-NLS-1$//$NON-NLS-2$
    }

    /**
     * Returns thread ID.
     * 
     * @return Long
     */
    public final Long getThreadId()
    {
        logger.debug("Returning thread ID [" + this.threadId + "]..."); //$NON-NLS-1$ //$NON-NLS-2$
        return this.threadId;
    }

    /**
     * Sets component group name.
     * 
     * @param componentGroupName
     */
    public final void setComponentGroupName(final String componentGroupName)
    {
        this.componentGroupName = componentGroupName;
        logger.debug("Component group name set to [" //$NON-NLS-1$
                + this.componentGroupName + "]."); //$NON-NLS-1$
    }

    /**
     * Returns component group name.
     * 
     * @return String
     * 
     */
    public final String getComponentGroupName()
    {
        logger.debug("Returning component group name [" //$NON-NLS-1$
                + this.componentGroupName + "]..."); //$NON-NLS-1$
        return this.componentGroupName;
    }

    /**
     * Sets component name.
     * 
     * @param componentName
     */
    public final void setComponentName(final String componentName)
    {
        this.componentName = componentName;
        logger.debug("Component name set to [" + this.componentName + "]."); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Returns component name.
     * 
     * @return componentName
     */
    public final String getComponentName()
    {
        logger.debug("Returning component name [" + this.componentName + "]..."); //$NON-NLS-1$ //$NON-NLS-2$
        return this.componentName;
    }

    /**
     * Adds a payload or replaces if exists.
     * 
     * If the payload already exists, it is overwritten. If it is a new payload
     * it is simply added to the end of the payload array list.
     * 
     * @param payload
     */
    public void setPayload(final Payload payload)
    {
        int index = this.payloads.indexOf(payload);
        if (index < 0)
        {
            this.payloads.add(payload);
            logger.debug("First payload added at index [" //$NON-NLS-1$
                    + this.payloads.indexOf(payload) + "]."); //$NON-NLS-1$
            // This is the primary payload, so set the name, spec and srcSystem
            logger.debug("Setting name, spec and srcSystem on event from primary payload."); //$NON-NLS-1$
            this.name = payload.getName();
            this.spec = payload.getSpec();
            this.srcSystem = payload.getSrcSystem();
        }
        else
        {
            this.payloads.set(index, payload);
            logger.debug("Payload set at index [" + index + "]."); //$NON-NLS-1$//$NON-NLS-2$
        }
    }

    /**
     * Returns a list of payloads.
     * 
     * @return payload as an ArrayList
     */
    public List<Payload> getPayloads()
    {
        logger.debug("Returning payload list..."); //$NON-NLS-1$
        return this.payloads;
    }

    /**
     * Sets payload list.
     * 
     * @param payloadList
     */
    public void setPayloads(List<Payload> payloadList)
    {
        logger.debug("Setting payload list..."); //$NON-NLS-1$
        //
        // iterate over existing entries
        for (Payload payload : payloadList)
            setPayload(payload);
    }

    /**
     * Adds payload to the list.
     * 
     * @param originalPayloadList
     */
    public void setOriginalPayload(final List<Payload> originalPayloadList)
    {
        logger.debug("Setting original payload list..."); //$NON-NLS-1$
        //
        // iterate over existing entries
        for (Payload originalPayload : originalPayloadList)
            setOriginalPayload(originalPayload);
    }

    /**
     * Sets original payload.
     * 
     * @param originalPayload
     */
    protected void setOriginalPayload(final Payload originalPayload)
    {
        int index = this.originalPayloads.indexOf(originalPayload);
        if (index < 0)
        {
            this.originalPayloads.add(originalPayload);
            logger.debug("First original payload added at index [" //$NON-NLS-1$
                    + this.originalPayloads.indexOf(originalPayload) + "]."); //$NON-NLS-1$
        }
        else
        {
            this.originalPayloads.set(index, originalPayload);
            logger.debug("Original payload set at index [" + index + "]."); //$NON-NLS-1$//$NON-NLS-2$
        }
    }

    /**
     * Returns original payloads.
     * 
     * @return payloads
     */
    public List<Payload> getOriginalPayloads()
    {
        logger.debug("Returning original payload list..."); //$NON-NLS-1$
        return this.originalPayloads;
    }

    /**
     * Sets scratch-pad.
     * 
     * @param scratchPad
     */
    public void setScratchPad(final Object scratchPad)
    {
        int index = this.scratchPad.indexOf(scratchPad);
        if (index < 0)
        {
            this.scratchPad.add(scratchPad);
            logger.debug("First scratch-pad added at index [" //$NON-NLS-1$
                    + this.scratchPad.indexOf(scratchPad) + "]."); //$NON-NLS-1$
        }
        else
        {
            this.scratchPad.set(index, scratchPad);
            logger.debug("Scratch-pad set at index [" + index + "]."); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    /**
     * Sets scratch-pad.
     * 
     * @param scratchPadList
     */
    public void setScratchPad(final List<Object> scratchPadList)
    {
        logger.debug("Setting scratchPad list..."); //$NON-NLS-1$
        //
        // iterate over existing entries
        for (Object pad : scratchPadList)
            setScratchPad(pad);
    }

    /**
     * Returns scratch-pad.
     * 
     * @return scratch pad
     */
    public List<Object> getScratchPad()
    {
        logger.debug("Returning scratch-pad..."); //$NON-NLS-1$
        return this.scratchPad;
    }

    /**
     * Sets exception.
     * 
     * @param exception
     */
    public final void setException(final Throwable exception)
    {
        this.exception = exception;
        logger.debug("Exception set to [" + this.exception + "]."); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Returns the current exception.
     * 
     * @return FrameworkException
     */
    public final Throwable getException()
    {
        logger.debug("Returning exception [" + this.exception + "]..."); //$NON-NLS-1$ //$NON-NLS-2$
        return this.exception;
    }

    /**
     * @return the methodName
     */
    public String getMethodName()
    {
        logger.debug("Getting this.methodName [" + this.methodName + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        return this.methodName;
    }

    /**
     * @param methodName the methodName to set
     */
    public void setMethodName(String methodName)
    {
        this.methodName = methodName;
        logger.debug("Setting this.methodName [" + this.methodName + "]"); //$NON-NLS-1$//$NON-NLS-2$
    }

    /**
     * Create a formatted string detailing the event id and associated payload
     * id(s).
     * 
     * @return String - formatted messsage
     */
    @Override
    public String idToString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("Event Id [" + this.getId() + "] "); //$NON-NLS-1$ //$NON-NLS-2$
        sb.append(PayloadHelper.idToString(this.getPayloads()));
        return sb.toString();
    }

    /**
     * Returns a string representation of this object.
     * 
     * @return String representation of Event
     * @deprecated - use toString()
     */
    @Deprecated
    public String toLogString()
    {
        return "Event \n" //$NON-NLS-1$
                + "id                 [" + this.id + "]\n" //$NON-NLS-1$ //$NON-NLS-2$
                + "priority           [" + this.priority + "]\n" //$NON-NLS-1$ //$NON-NLS-2$
                + "timestamp          [" + this.timestamp + "]\n" //$NON-NLS-1$ //$NON-NLS-2$
                + "componentGroupName [" + this.componentGroupName + "]\n" //$NON-NLS-1$ //$NON-NLS-2$
                + "componentName      [" + this.componentName + "]\n" //$NON-NLS-1$ //$NON-NLS-2$
                + "payload            [" + this.payloads + "]\n" //$NON-NLS-1$ //$NON-NLS-2$
                + "originalPayload    [" + this.originalPayloads + "]\n" //$NON-NLS-1$ //$NON-NLS-2$
                + "scratchPad         [" + this.scratchPad + "]\n" //$NON-NLS-1$ //$NON-NLS-2$
                + "exception          [" + this.exception.getMessage() + "]\n"; //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Returns a string representation of this object.
     * @return String representation of Event
     */
    @Override
    public String toString()
    {
        return "id=[" + this.id + "] "
                + "priority=[" + this.priority + "] "
                + "timestamp=[" + this.timestamp + "] "
                + "componentGroupName=[" + this.componentGroupName + "] "
                + "componentName=[" + this.componentName + "] "
                + "payload=[" + this.payloads + "] ";
    }

    /**
     * Runs this class for test.
     * 
     * TODO replace with unit tests
     * 
     * @param args
     */
    public static void main(String args[])
    {
        System.out.println("main test method starting");
        Event event = new Event("componentGroupName", "componentName");
        event.setPriority(new Integer(Priority.NORMAL.getLevel()));
        event.setComponentGroupName("testComponentGroupName");
        event.setComponentName("testComponentName");
        // TODO global service locator
        ServiceLocator serviceLocator = ResourceLoader.getInstance();
        Payload payload = serviceLocator.getPayloadFactory().newPayload("test", Spec.TEXT_XML, "testSrcSystem");
        payload.setContent("hello".getBytes());
        payload = serviceLocator.getPayloadFactory().newPayload("test", Spec.TEXT_XML, "testSrcSystem");
        payload.setContent("hello".getBytes());
        event.setPayload(payload);
        event.setPayload(event.getPayloads().get(0));
        payload = serviceLocator.getPayloadFactory().newPayload("test", Spec.TEXT_XML, "testSrcSystem");
        payload.setContent("lo".getBytes());
        event.setPayload(payload);
        event.setScratchPad(new ArrayList<Payload>());
        event.setException(new FrameworkException("test Exception"));
        System.out.println(event.toLogString());
        System.out.println("main test method finished");
    }

    @Override
    // TODO - check this implementation
    protected String calculateChecksum()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    // TODO - check this implementation
    public void setSize()
    {
        // TODO Auto-generated method stub
    }

    /**
     * @return the caller
     */
    public Administrator getCaller()
    {
        logger.debug("Getting caller [" //$NON-NLS-1$
                + this.caller + "]"); //$NON-NLS-1$
        return this.caller;
    }

    /**
     * @param caller the caller to set
     */
    public void setCaller(Administrator caller)
    {
        this.caller = caller;
        logger.debug("Setting caller [" //$NON-NLS-1$
                + this.caller + "]"); //$NON-NLS-1$
    }

    /**
     * If this event has payloads, return the primary payload's name.
     * Otherwise, return event name. 
     * The primary payload being the first in this event's payload list.
     * @return String
     */
    @Override
    public String getName()
    {
        String eventName = this.name;
        if (this.payloads != null && this.payloads.size() > 0)
        {
            eventName = this.payloads.get(0).getName();
        }
        logger.debug("Returning name [" + eventName + "].");
        return eventName;
    }

    /**
     * Create an Envelope from and Event
     * 
     * TODO move this into the Envelope Factory once Event is moved to the
     * Common project
     * 
     * @param envelopeFactory
     * 
     * @return Envelope
     */
    public Envelope getEnvelope(EnvelopeFactory envelopeFactory)
    {
        Envelope envelope = envelopeFactory.newEnvelope(getPayloads());
        envelope.setId(getId());
        envelope.setTimestamp(getTimestamp());
        envelope.setTimestampFormat(getTimestampFormat());
        envelope.setTimezone(getTimezone());
        envelope.setPriority(getPriority());
        envelope.setName(getName());
        envelope.setSpec(getSpec());
        envelope.setEncoding(getEncoding());
        envelope.setFormat(getFormat());
        envelope.setCharset(getCharset());
        envelope.setSize(getSize());
        envelope.setChecksum(getChecksum());
        envelope.setChecksumAlg(getChecksumAlg());
        envelope.setSrcSystem(getSrcSystem());
        envelope.setTargetSystems(getTargetSystems());
        envelope.setProcessIds(getProcessIds());
        return envelope;
    }

    /**
     * Calculate the total size of the payloads in the event
     * 
     * @return total size of the payloads
     */
    public long calculateTotalPayloadSize()
    {
        long totalSize = 0;
        for (Payload payload : getPayloads())
        {
            totalSize = totalSize + payload.getSize();
        }
        return totalSize;
    }

    /**
     * Truncate the size of the payloads in the event
     * 
     * @param maxIndividualPayloadSize The max individual payload size
     */
    public void truncatePayloads(int maxIndividualPayloadSize)
    {
        byte[] trimmedPayload = new byte[maxIndividualPayloadSize];
        String tempPayloadContent = null;
        for (Payload payload : getPayloads())
        {
            if (payload.getContent().length > maxIndividualPayloadSize)
            {
                // Force the truncation
                logger.debug("payload length = [" + payload.getContent().length + "]"); //$NON-NLS-1$ //$NON-NLS-2$
                tempPayloadContent = new String(payload.getContent());
                tempPayloadContent = tempPayloadContent.substring(0, maxIndividualPayloadSize);
                trimmedPayload = tempPayloadContent.getBytes();
                logger.debug("trimmedPayload length = [" + trimmedPayload.length + "]"); //$NON-NLS-1$ //$NON-NLS-2$
                payload.setContent(trimmedPayload);
                logger.info("The content of Payload: [" + payload.getId() //$NON-NLS-1$ 
                        + "] was trimmed to [" + maxIndividualPayloadSize //$NON-NLS-1$
                        + "] bytes, as it was too large to handle."); //$NON-NLS-1$
            }
        }
    }

    /**
     * Base64 encodes binary type payloads
     */
    public void base64EncodeBinaryPayloads()
    {
        List<Payload> allPayloads = new ArrayList<Payload>();
        allPayloads.addAll(getPayloads());
        allPayloads.addAll(getOriginalPayloads());
        for (Payload payload : allPayloads)
        {
            if (payload.getSpec().equals(Spec.BYTE_JAR.toString()) || payload.getSpec().equals(Spec.BYTE_ZIP.toString())
                    || payload.getSpec().equals(Spec.BYTE_PLAIN.toString()))
            {
                payload.base64EncodePayload();
            }
            else
            {
                logger.info("Text payload returned without encoding."); //$NON-NLS-1$
            }
        }
    }
}
