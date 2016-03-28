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

import java.util.Arrays;
import java.util.Date;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class ReplayEvent
{
	private Long id;
    private String moduleName;
    private String flowName;
	private String eventId;
    private byte[] event;
    private long timestamp;
    private long expiry;
	
	/**
	 * Default constructor for Hibernate
	 */
	@SuppressWarnings("unused")
	private ReplayEvent()
	{
		
	}

	/**
	 * Constructor 
	 * 
	 * @param id
	 * @param errorUri
	 * @param actionedBy
	 * @param state
	 * @param timestamp
	 */
	public ReplayEvent(String eventId, byte[] event, String moduleName, String flowName, int timeToLiveDays)
	{
		super();

		this.eventId = eventId;
		this.event = event;
		this.moduleName = moduleName;
		this.flowName = flowName;
		this.timestamp = new Date().getTime();
		this.expiry = new Date().getTime() + (timeToLiveDays * 60 * 60 * 24 * 1000);
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
		ReplayEvent other = (ReplayEvent) obj;
		if (!id.equals(other.id))
			return false;
		
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() 
	{
		return "ReplayEvent [id=" + id + ", moduleName=" + moduleName
				+ ", flowName=" + flowName + ", eventId=" + eventId
				+ ", event=" + Arrays.toString(event) + ", timestamp="
				+ timestamp + "]";
	}
	
}
