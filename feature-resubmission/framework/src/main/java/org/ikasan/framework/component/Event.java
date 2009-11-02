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

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    /**
     * Identifier for the Event
     */
    private String id;
    
    /**
     * Origination time for the Event
     */
    private Date timestamp;
    
    /**
     * Relative priority - see JMS Message Priority
     * 
     * TODO, if this really is just a number from 0-9, then this constraint should be modelled somehow
     */
    private int priority =4;    
    
    /**
     * Unique identifier required when persisting using ORM
     */
    private Long persistenceId;
    

	/** Event contained payloads */
    private List<Payload> payloads = null;
    
    
  
    
    /**
     * Constructor
     * 
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
    	this.timestamp=new Date();
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
     * @param paylaods
     */
    public Event(String id, int priority, Date timestamp,  List<Payload> payloads) {
    	this.id=id;
		this.priority = priority;
		this.timestamp = timestamp;
		this.payloads = new ArrayList<Payload>(payloads);
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

        clone.setTimestamp(getTimestamp());
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
        	payloadStringBuffer.append("Payload ["+payload.getId()+"]");
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
        return "id=[" + id + "] "
        		+ "persistenceId=[" + persistenceId + "] "
                + "priority=[" + priority + "] "
                + "timestamp=[" + timestamp + "] "
                + "payloads=[" + payloads + "] "
        		+ "class=[" + getClass() + "] ";
    }


  
    

	/**
	 * Spawns a new child Event from this Event
	 * 
	 * @param moduleName
	 * @param componentName
	 * @param siblingNo
	 * @param payloads
	 * @return
	 */
	public Event spawnChild(String moduleName, String componentName, int siblingNo,
			List<Payload> payloads) {
		Event event = new Event(moduleName, componentName, "#"+getId()+"."+siblingNo,payloads);
		event.setPriority(getPriority());
		return event;
	}

	/**
	 * Spawns a new child Event from this Event
	 * 
	 * @param moduleName
	 * @param componentName
	 * @param siblingNo
	 * @param payload
	 * @return
	 */
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
    public Date getTimestamp(){
    	return timestamp;
    }
    
    /**
     * Mutator for timestamp, required by ORM
     * 
     * @param timestamp
     */
    private void setTimestamp(Date timestamp){
    	this.timestamp = timestamp;
    }
    
    /**
     * Retrieves a String representation of the timestamp using the <code>DateFormat</code> provided
     * 
     * @param dateFormat
     * @return
     */
    public String getFormattedTimestamp(DateFormat dateFormat){
		return dateFormat.format(timestamp);
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
    
    /**
     * Accessor for persistenceId
     * 
     * @return persistenceId if set
     */
    public Long getPersistenceId() {
		return persistenceId;
	}

	/**
	 * Mutator for persistenceId
	 * 
	 * @param persistenceId
	 */
	private void setPersistenceId(Long persistenceId) {
		this.persistenceId = persistenceId;
	}
}
