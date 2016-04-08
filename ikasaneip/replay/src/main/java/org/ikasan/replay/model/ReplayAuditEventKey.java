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

import java.io.Serializable;

/**
 * 
 * @author Ikasan Development Team
 *
 */
@SuppressWarnings("serial")
public class ReplayAuditEventKey implements Serializable
{
	private static final long serialVersionUID = 4202004053354602230L;

	private Long replayAuditId;
	private Long replayEventId;
	
	@SuppressWarnings("unused")
	private ReplayAuditEventKey(){}
	
	public ReplayAuditEventKey(Long replayAuditId, Long replayEventId) 
	{
		super();
		this.replayAuditId = replayAuditId;
		this.replayEventId = replayEventId;
	}

	/**
	 * @return the replayAuditId
	 */
	public Long getReplayAuditId() 
	{
		return replayAuditId;
	}

	/**
	 * @param replayAuditId the replayAuditId to set
	 */
	public void setReplayAuditId(Long replayAuditId) 
	{
		this.replayAuditId = replayAuditId;
	}

	/**
	 * @return the replayEventId
	 */
	public Long getReplayEventId() 
	{
		return replayEventId;
	}

	/**
	 * @param replayEventId the replayEventId to set
	 */
	public void setReplayEventId(Long replayEventId) 
	{
		this.replayEventId = replayEventId;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() 
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((replayAuditId == null) ? 0 : replayAuditId.hashCode());
		result = prime * result
				+ ((replayEventId == null) ? 0 : replayEventId.hashCode());
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
		ReplayAuditEventKey other = (ReplayAuditEventKey) obj;
		if (replayAuditId == null) {
			if (other.replayAuditId != null)
				return false;
		} else if (!replayAuditId.equals(other.replayAuditId))
			return false;
		if (replayEventId == null) {
			if (other.replayEventId != null)
				return false;
		} else if (!replayEventId.equals(other.replayEventId))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "ReplayAuditEventKey [replayAuditId=" + replayAuditId
				+ ", replayEventId=" + replayEventId + "]";
	}
}
