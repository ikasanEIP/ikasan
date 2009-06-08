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
package org.ikasan.framework.component;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.ikasan.common.Envelope;
import org.ikasan.common.MetaDataInterface;
import org.ikasan.common.Payload;
import org.ikasan.common.component.MetaData;
import org.ikasan.common.component.PayloadHelper;
import org.ikasan.common.component.Priority;
import org.ikasan.common.factory.EnvelopeFactory;

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
    /** Component Group Name currently handling the Event */
    private String componentGroupName = null;
    /** Component in the Component Group Name handling the Event */
    private String componentName = null;


    /** Event contained payloads */
    private List<Payload> payloads = null;



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
        this(envelope.getPayloads(), componentGroupName, componentName);
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
        if (this.getTimestamp() != null){ 
        	clone.setTimestamp(new Long(this.getTimestamp()));
        }
        if (this.getPriority() != null){
        	clone.setPriority(new Integer(this.getPriority()));
        }
        if (this.getSize() != null){
        	clone.setSize(new Long(this.getSize()));
        }

        // populate actual payload(s)
        List<Payload> clonedPayloads = new ArrayList<Payload>();
        for (Payload payload : this.getPayloads())
        {
            clonedPayloads.add(payload.clone());
        }
        clone.payloads = clonedPayloads;
        
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
    private Event(final Payload payload, final boolean keepOriginal, String componentGroupName, String componentName)
    {
        this(componentGroupName, componentName);
        this.setPayload(payload);

    }



    /**
     * Event constructor based on incoming payload list and specified option of
     * keeping an original copy of the incoming payloads within the event.
     * 
     * @param payloads
     * @param componentGroupName
     * @param componentName
     */
    public Event(final List<Payload> payloads, String componentGroupName, String componentName)
    {
        //
        // timestamp it immediately
        this.timestamp = generateTimestamp();
        this.timestampFormat = MetaDataInterface.DEFAULT_TIMESTAMP_FORMAT;
        this.timezone = MetaDataInterface.DEFAULT_TIMEZONE;
        this.name = Event.UNDEFINED;
        this.id = generateId();

        // populate actual payload(s)
        this.payloads = new ArrayList<Payload>();
        this.payloads.addAll(payloads);

        // initialise the events current component details
        this.setComponentGroupName(componentGroupName);
        this.setComponentName(componentName);

        // set priority to default if no payload defined; otherwise
        // set the priority to the priority of the highest incoming payload
        Payload pl = getPayloadHighestPriority();
        if (pl == null){
            this.priority = new Integer(Priority.NORMAL.getLevel());
        }
        else{
            this.priority = pl.getPriority();
        }
       
        
        
        logger.info("Created " + this.idToString() //$NON-NLS-1$
                + "within [" + this.getComponentName() + "] [" //$NON-NLS-1$//$NON-NLS-2$
                + this.getComponentGroupName() + "]."); //$NON-NLS-1$
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
        for (Payload payload : payloadList){
            setPayload(payload);
        }
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
     * Return the highest priority Payload from the list passed in
     *
     * @param payloadList
     * @return the highest priority Payload from the list passed in
     */
    private  Payload getPayloadHighestPriority()
    {
        Payload priorityPayload = null;

        // Iterate over payloads
        for (Payload payload : payloads)
        {
            // Populate first time through
            if (priorityPayload == null)
            {
                priorityPayload = payload;
            }

            // Update only if we find a higher priority
            if (payload.getPriority().compareTo(priorityPayload.getPriority()) > 0)
            {
                priorityPayload = payload;
            }
        }

        return priorityPayload;
    }


}
