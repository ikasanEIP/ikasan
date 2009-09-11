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
import org.ikasan.common.Payload;

/**
 * Event provides the transport framing object for all payloads and associated
 * data across synchronous hops ie JMS -> JMS.
 * 
 * @author Ikasan Development Team
 */
public class Event implements Cloneable
{
    /** Serialize ID */
    private static final long serialVersionUID = 1L;
    /** Logger instance */
    private static Logger logger = Logger.getLogger(Event.class);


    /**
     * Identifier for the Event
     */
    private String id;
    
    /**
     * Origination time for the Event
     */
    private long timestamp;
    
    /**
     * Relative priority - see JMS Message Priority
     * 
     * TODO, if this really is just a number from 0-9, then this constraint should be modelled somehow
     */
    private int priority =4;    

    /**
     * Name of the Event
     * 
     * TODO - what does this mean? Does an Event need a name? Why? Do all? How/When should this change?
     */
    private String name;
    
    /**
     * Description of the external system this event is originated from
     */
    private String srcSystem;   
    
    /** Event contained payloads */
    private List<Payload> payloads = null;
    
    
    


    
    /**
     * private Default constructor requried by ORM
     */
    @SuppressWarnings("unused")
	private Event(){}
    
    /**
     * Constructor for new Events
     * 
     * To be used only for origination of new Events.
     * Not to used for deserialising serialised Events
     * 
     * 
     * @param originatingModuleName
     * @param originatingComponentName
     * @param initiatorGeneratedId
     */
    private Event(String originatingModuleName, String originatingComponentName, String originatorGeneratedId){
    	if (originatorGeneratedId==null){
    		throw new IllegalArgumentException("Event originator did not provide a generated id!");
    	}
    	this.id=originatingModuleName+"_"+originatingComponentName+"_"+originatorGeneratedId;
    	this.timestamp=System.currentTimeMillis();
    } 
    
    /**
     * Constructor for new Events
     * 
     * To be used only for origination of new Events.
     * Not to used for deserialising serialised Events
     * 
     * 
     * @param originatingModuleName
     * @param originatingComponentName
     * @param originatorGeneratedId
     * @param payloads
     */
    public Event(String originatingModuleName, String originatingComponentName, String originatorGeneratedId, List<Payload> payloads){
    	this(originatingModuleName,originatingComponentName,originatorGeneratedId);
    	this.payloads=new ArrayList<Payload>(payloads);
    }
    
    /**
     * Constructor for new Events
     * 
     * To be used only for origination of new Events.
     * Not to used for deserialising serialised Events
     * 
     * 
     * @param originatingModuleName
     * @param originatingInitiatorName
     * @param initiatorGeneratedId
     * @param payload
     */
    public Event(String originatingModuleName, String originatingComponentName, String originatorGeneratedId, Payload payload){
    	this(originatingModuleName,originatingComponentName,originatorGeneratedId);
    	List<Payload> payloads = new ArrayList<Payload>();
    	payloads.add(payload);
    	this.payloads=new ArrayList<Payload>(payloads);
    } 

    /**
     * Constructor for reconstituting Events
     * 
     * To be used only for reconstitution of previously serialised Events.
     * Not to used for originating new Events
     *      
     * @param id
     * @param priority
     * @param timestamp
     * @param srcSystem
     * @param paylaods
     */
    public Event(String id, int priority, long timestamp, String srcSystem, List<Payload> payloads) {
		//System.out.println("new Event called with id["+id+"], priority ["+priority+"] timestamp ["+timestamp+"] srcSystem ["+srcSystem+"] payloads ["+payloads+"]");
    	this.id=id;
		this.priority = priority;
		this.timestamp = timestamp;
		this.srcSystem = srcSystem;
		this.payloads = new ArrayList<Payload>(payloads);
	}


//	/**
//     * Sets the event from an envelope
//     * 
//     * @param envelope
//     */
//    public void setEventAttribsFromEnvelope(final Envelope envelope)
//    {
//        this.timestamp = envelope.getTimestamp();
//        this.timestampFormat = envelope.getTimestampFormat();
//        this.timezone = envelope.getTimezone();
//        this.priority = envelope.getPriority();
//        this.name = envelope.getName();
//        this.spec = envelope.getSpec();
//        this.encoding = envelope.getEncoding();
//        this.format = envelope.getFormat();
//        this.charset = envelope.getCharset();
//        this.checksum = envelope.getChecksum();
//        this.checksumAlg = envelope.getChecksumAlg();
//        this.srcSystem = envelope.getSrcSystem();
//        this.targetSystems = envelope.getTargetSystems();
//        this.processIds = envelope.getProcessIds();
//    }

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

        clone.setTimestamp(timestamp);
        clone.setPriority(priority);
        

        // populate actual payload(s)
        List<Payload> clonedPayloads = new ArrayList<Payload>();
        for (Payload payload : this.getPayloads())
        {
            clonedPayloads.add(payload.clone());
        }
        clone.payloads = clonedPayloads;
        
        return clone;
    }




//    /**
//     * Adds a payload or replaces if exists.
//     * 
//     * If the payload already exists, it is overwritten. If it is a new payload
//     * it is simply added to the end of the payload array list.
//     * 
//     * @param payload
//     */
//    public void setPayload(final Payload payload)
//    {
//        int index = this.payloads.indexOf(payload);
//        if (index < 0)
//        {
//            this.payloads.add(payload);
//            logger.debug("First payload added at index [" //$NON-NLS-1$
//                    + this.payloads.indexOf(payload) + "]."); //$NON-NLS-1$
//            // This is the primary payload, so set the name, spec and srcSystem
//            logger.debug("Setting name, spec and srcSystem on event from primary payload."); //$NON-NLS-1$
//            this.name = payload.getName();
//            this.spec = payload.getSpec();
//            this.srcSystem = payload.getSrcSystem();
//        }
//        else
//        {
//            this.payloads.set(index, payload);
//            logger.debug("Payload set at index [" + index + "]."); //$NON-NLS-1$//$NON-NLS-2$
//        }
//    }







    /**
     * Create a formatted string detailing the event id and associated payload
     * id(s).
     * 
     * @return String - formatted messsage
     */
    public String idToString()
    {
        StringBuffer payloadStringBuffer = new StringBuffer();
        for(Payload payload:payloads){
        	payloadStringBuffer.append(payload.idToString());
        }
    	
    	
    	
        StringBuffer sb = new StringBuffer();
        sb.append("Event Id [" + this.getId() + "] "); //$NON-NLS-1$ //$NON-NLS-2$
        sb.append(payloadStringBuffer.toString());
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
                + "payload=[" + this.payloads + "] ";
    }





    /**
     * If this event has payloads, return the primary payload's name.
     * Otherwise, return event name. 
     * The primary payload being the first in this event's payload list.
     * @return String
     */
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
    
    

	public Event spawnChild(String moduleName, String componentName, int siblingNo,
			List<Payload> payloads) {
		Event event = new Event(moduleName, componentName, "#"+getId()+"."+siblingNo,payloads);
		event.setPriority(getPriority());
		return event;
	}

	public Event spawnChild(String moduleName, String componentName, int siblingNo,
			Payload payload) {
		List<Payload>payloads = new ArrayList<Payload>();
		payloads.add(payload);
		return spawnChild(moduleName, componentName, siblingNo, payloads);
	}
	
	
	
	
	
	
	
	
	
	/**
     * Accessor for id
     * 
     * @return
     */
    public String getId(){
    	return id;
    }

    /**
     * Mutator for id, required for ORM
     * 
     * @param id
     */
    @SuppressWarnings("unused")
	private void setId(String id){
    	this.id = id;
    }
    

    
    /**
     * Accessor for timestamp
     * 
     * @return timestamp
     */
    public long getTimestamp(){
    	return timestamp;
    }
    
    /**
     * Mutator for timestamp, required by ORM
     * 
     * @param timestamp
     */
    private void setTimestamp(long timestamp){
    	this.timestamp = timestamp;
    }
    
    
    /**
     * Accessor for priority
     * 
     * @return priority
     */
    public int getPriority(){
    	return priority;
    }
    
    /**
     * Mutator for priority
     * 
     * @param priority
     */
    public void setPriority(int priority){
    	this.priority = priority;
    }
    

    
    /**
     * Mutator for name
     * 
     * @param name
     */
    public void setName(String name){
    	this.name = name;
    }
    

    
    /**
     * Accessor for srcSystem
     * 
     * @return srcSysterm
     */
    public String getSrcSystem(){
    	return srcSystem;
    }
    
    /**
     * Mutator for srcSystem, needed by ORM
     * @param srcSystem
     */
    @SuppressWarnings("unused")
	private void setSrcSystem(String srcSystem){
    	this.srcSystem = srcSystem;
    }
    
    /**
     * Sets payload list.
     * 
     * @param payloadList
     */
    public void setPayloads(List<Payload> payloadList)
    {
    	this.payloads=payloadList;
    }
    
    /**
     * Returns a list of payloads.
     * 
     * @return payload as an ArrayList
     */
    public List<Payload> getPayloads()
    {
        return this.payloads;
    }
}
