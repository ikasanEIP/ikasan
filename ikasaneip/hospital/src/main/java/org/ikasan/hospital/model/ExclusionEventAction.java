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
package org.ikasan.hospital.model;

import java.util.Date;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class ExclusionEventAction
{
	public static final String RESUBMIT = "re-submit";
	public static final String IGNORED = "ignored";

	private Long id;
	private String errorUri;
	private String actionedBy;
	private String action;
	private Date timestamp;	
	
	/**
	 * Default constructor for Hibernate
	 */
	@SuppressWarnings("unused")
	private ExclusionEventAction()
	{
		
	}

	/**
	 * @param id
	 * @param errorUri
	 * @param actionedBy
	 * @param state
	 * @param timestamp
	 */
	public ExclusionEventAction(String errorUri, String actionedBy,
			String action)
	{
		super();
		this.errorUri = errorUri;
		this.actionedBy = actionedBy;
		this.action = action;
		this.timestamp = new Date();
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
	public void setId(Long id)
	{
		this.id = id;
	}

	/**
	 * @return the errorUri
	 */
	public String getErrorUri()
	{
		return errorUri;
	}

	/**
	 * @param errorUri the errorUri to set
	 */
	public void setErrorUri(String errorUri)
	{
		this.errorUri = errorUri;
	}

	/**
	 * @return the actionedBy
	 */
	public String getActionedBy()
	{
		return actionedBy;
	}

	/**
	 * @param actionedBy the actionedBy to set
	 */
	public void setActionedBy(String actionedBy)
	{
		this.actionedBy = actionedBy;
	}

	/**
	 * @return the timestamp
	 */
	public Date getTimestamp()
	{
		return timestamp;
	}

	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(Date timestamp)
	{
		this.timestamp = timestamp;
	}

	/**
	 * @return the action
	 */
	public String getAction()
	{
		return action;
	}

	/**
	 * @param action the action to set
	 */
	public void setAction(String action)
	{
		this.action = action;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((action == null) ? 0 : action.hashCode());
		result = prime * result
				+ ((actionedBy == null) ? 0 : actionedBy.hashCode());
		result = prime * result
				+ ((errorUri == null) ? 0 : errorUri.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((timestamp == null) ? 0 : timestamp.hashCode());
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
		ExclusionEventAction other = (ExclusionEventAction) obj;
		if (action == null)
		{
			if (other.action != null)
				return false;
		} else if (!action.equals(other.action))
			return false;
		if (actionedBy == null)
		{
			if (other.actionedBy != null)
				return false;
		} else if (!actionedBy.equals(other.actionedBy))
			return false;
		if (errorUri == null)
		{
			if (other.errorUri != null)
				return false;
		} else if (!errorUri.equals(other.errorUri))
			return false;
		if (id == null)
		{
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (timestamp == null)
		{
			if (other.timestamp != null)
				return false;
		} else if (!timestamp.equals(other.timestamp))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "ExclusionEventAction [id=" + id + ", errorUri=" + errorUri
				+ ", actionedBy=" + actionedBy + ", action=" + action
				+ ", timestamp=" + timestamp + "]";
	}

}
