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

import java.util.Date;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class ReplayAuditEvent implements Comparable<ReplayAuditEvent>
{
	private ReplayAuditEventKey id;
	private ReplayAudit replayAudit;
	private ReplayEvent replayEvent;
	private String result;
   
    
    @SuppressWarnings("unused")
	private ReplayAuditEvent()
    {
    }

    
    /**
     * Cpnstructor 
     * @param replayAudit
     * @param replayEvent
     */
	public ReplayAuditEvent(ReplayAudit replayAudit, ReplayEvent replayEvent, String result) {
		super();
		this.replayAudit = replayAudit;
		if(this.replayAudit == null)
		{
			throw new IllegalArgumentException("ReplayAudit cannot be null!!");
		}
		this.replayEvent = replayEvent;
		if(this.replayEvent == null)
		{
			throw new IllegalArgumentException("ReplayEvent cannot be null!!");
		}
		
		this.id = new ReplayAuditEventKey(replayAudit.getId(), replayEvent.getId());
		this.result = result;
	}



	/**
	 * @return the id
	 */
	public ReplayAuditEventKey getId() 
	{
		return id;
	}


	/**
	 * @param id the id to set
	 */
	public void setId(ReplayAuditEventKey id) 
	{
		this.id = id;
	}


	/**
	 * @return the replayAudit
	 */
	public ReplayAudit getReplayAudit() 
	{
		return replayAudit;
	}


	/**
	 * @param replayAudit the replayAudit to set
	 */
	public void setReplayAudit(ReplayAudit replayAudit) 
	{
		this.replayAudit = replayAudit;
	}


	/**
	 * @return the replayEvent
	 */
	public ReplayEvent getReplayEvent() 
	{
		return replayEvent;
	}


	/**
	 * @param replayEvent the replayEvent to set
	 */
	public void setReplayEvent(ReplayEvent replayEvent)
	{
		this.replayEvent = replayEvent;
	}

	/**
	 * @return the result
	 */
	public String getResult() 
	{
		return result;
	}


	/**
	 * @param result the result to set
	 */
	public void setResult(String result) 
	{
		this.result = result;
	}
	
	

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() 
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((replayAudit == null) ? 0 : replayAudit.hashCode());
		result = prime * result
				+ ((replayEvent == null) ? 0 : replayEvent.hashCode());
		result = prime * result
				+ ((this.result == null) ? 0 : this.result.hashCode());
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
		ReplayAuditEvent other = (ReplayAuditEvent) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (replayAudit == null) {
			if (other.replayAudit != null)
				return false;
		} else if (!replayAudit.equals(other.replayAudit))
			return false;
		if (replayEvent == null) {
			if (other.replayEvent != null)
				return false;
		} else if (!replayEvent.equals(other.replayEvent))
			return false;
		if (result == null) {
			if (other.result != null)
				return false;
		} else if (!result.equals(other.result))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() 
	{
		return "ReplayAuditEvent [id=" + id + ", replayAudit=" + replayAudit
				+ ", replayEvent=" + replayEvent + ", result=" + result + "]";
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(ReplayAuditEvent o) 
	{
		// TODO Auto-generated method stub
		return 0;
	}

	
	
}
