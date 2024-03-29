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
package org.ikasan.replay.model;

import jakarta.persistence.*;
import org.ikasan.harvest.HarvestEvent;
import org.ikasan.spec.replay.ReplayEvent;

import java.util.Date;

/**
 * 
 * @author Ikasan Development Team
 *
 */
@Entity
@Table(name = "ReplayEvent")
public class ReplayEventImpl implements ReplayEvent, HarvestEvent
{
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
	private Long id;

    @Column(name="ModuleName", nullable = false)
    private String moduleName;

    @Column(name="FlowName", nullable = false)
    private String flowName;

    @Column(name="Identifier", nullable = false)
    private String eventId;

    @Column(name="Event", nullable = false)
    private byte[] event;

    @Column(name="EventAsString")
    private String eventAsString;

    @Column(name="Timestamp", nullable = false)
    private long timestamp;

    @Column(name="Expiry", nullable = false)
    private long expiry;

	/** flag to indicate if the record has been harvested */
    @Column(name="Harvested", nullable = false)
    boolean harvested;

    /** the time the record was harvested */
    @Column(name="HarvestedDateTime", nullable = false)
    private long harvestedDateTime;

    /**
	 * Default constructor
	 */
	protected ReplayEventImpl()
	{
		
	}

	/**
	 * Constructor
	 *
	 * @param eventId
	 * @param event
	 * @param moduleName
	 * @param flowName
	 * @param timeToLiveDays
     */
	public ReplayEventImpl(String eventId, byte[] event, String eventAsString, String moduleName, String flowName, int timeToLiveDays)
	{
		super();

		this.eventId = eventId;
		this.event = event;
		this.eventAsString = eventAsString;
		this.moduleName = moduleName;
		this.flowName = flowName;
		this.timestamp = new Date().getTime();
		this.expiry = new Date().getTime() + Long.valueOf(timeToLiveDays * 60l * 60l * 24l * 1000l);
	}

	/**
	 * @return the id
	 */
	public Long getId()
	{
		return id;
	}

	/**
	 * @param id the id to set
	 */
	private void setId(Long id)
	{
		this.id = id;
	}


	/**
	 * @return the event
	 */
	public byte[] getEvent()
	{
		return event;
	}
	
	/**
	 * @param event the event to set
	 */
	public void setEvent(byte[] event)
	{
		this.event = event;
	}

	/**
	 * @return the moduleName
	 */
	public String getModuleName()
	{
		return moduleName;
	}

	/**
	 * @param moduleName the moduleName to set
	 */
	public void setModuleName(String moduleName)
	{
		this.moduleName = moduleName;
	}

	/**
	 * @return the flowName
	 */
	public String getFlowName()
	{
		return flowName;
	}

	/**
	 * @param flowName the flowName to set
	 */
	public void setFlowName(String flowName)
	{
		this.flowName = flowName;
	}

	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(long timestamp)
	{
		this.timestamp = timestamp;
	}
	
	/**
	 * @return the timestamp
	 */
	public long getTimestamp()
	{
		return timestamp;
	}

	/**
	 * @return the eventId
	 */
	public String getEventId() 
	{
		return eventId;
	}

	/**
	 * @param eventId the eventId to set
	 */
	public void setEventId(String eventId)
	{
		this.eventId = eventId;
	}

	/**
	 * @return the expiry
	 */
	public long getExpiry()
	{
		return expiry;
	}

	/**
	 * @param expiry the expiry to set
	 */
	public void setExpiry(long expiry) 
	{
		this.expiry = expiry;
	}

	/**
	 * Get harvested flag
	 *
	 * @return
     */
	public boolean isHarvested()
	{
		return harvested;
	}

	/**
	 * Set the harvested flag.
	 *
	 * @param harvested
     */
	public void setHarvested(boolean harvested)
	{
		this.harvested = harvested;
	}

	public String getEventAsString()
	{
		return eventAsString;
	}

	public void setEventAsString(String eventAsString)
	{
		this.eventAsString = eventAsString;
	}

    public long getHarvestedDateTime()
    {
        return harvestedDateTime;
    }

    public void setHarvestedDateTime(long harvestedDateTime)
    {
        this.harvestedDateTime = harvestedDateTime;
    }

	/* (non-Javadoc)
             * @see java.lang.Object#hashCode()
             */
	@Override
	public int hashCode() 
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((eventId == null) ? 0 : eventId.hashCode());
		result = prime * result
				+ ((flowName == null) ? 0 : flowName.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((moduleName == null) ? 0 : moduleName.hashCode());
		result = prime * result + (int) (timestamp ^ (timestamp >>> 32));
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) 
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ReplayEventImpl other = (ReplayEventImpl) obj;
		if (!id.equals(other.id))
			return false;
		
		return true;
	}

    @Override
    public String toString()
    {
        final StringBuffer sb = new StringBuffer("HibernateReplayEvent{");
        sb.append("id=").append(id);
        sb.append(", moduleName='").append(moduleName).append('\'');
        sb.append(", flowName='").append(flowName).append('\'');
        sb.append(", eventId='").append(eventId).append('\'');
        sb.append(", event=");
        if (event == null) sb.append("null");
        else
        {
            sb.append('[');
            for (int i = 0; i < event.length; ++i)
                sb.append(i == 0 ? "" : ", ").append(event[i]);
            sb.append(']');
        }
        sb.append(", eventAsString='").append(eventAsString).append('\'');
        sb.append(", timestamp=").append(timestamp);
        sb.append(", expiry=").append(expiry);
        sb.append(", harvested=").append(harvested);
        sb.append(", harvestedDateTime=").append(harvestedDateTime);
        sb.append('}');
        return sb.toString();
    }
}
