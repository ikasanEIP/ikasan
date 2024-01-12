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
package org.ikasan.spec.hospital.model;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public interface ExclusionEventAction<EVENT>
{
    String RESUBMIT = "re-submitted";
    String IGNORED = "ignored";

	/**
	 * @return the errorUri
	 */
	public String getErrorUri();

	/**
	 * @param errorUri the errorUri to set
	 */
	public void setErrorUri(String errorUri);

	/**
	 * @return the actionedBy
	 */
	public String getActionedBy();

	/**
	 * @param actionedBy the actionedBy to set
	 */
	public void setActionedBy(String actionedBy);

	/**
	 * @return the action
	 */
	public String getAction();

	/**
	 * @param action the action to set
	 */
	public void setAction(String action);

	/**
	 * @return the event
	 */
	public EVENT getEvent();
	
	/**
	 * @param event the event to set
	 */
	public void setEvent(EVENT event);

	/**
	 * @return the moduleName
	 */
	public String getModuleName();

	/**
	 * @param moduleName the moduleName to set
	 */
	public void setModuleName(String moduleName);

	/**
	 * @return the flowName
	 */
	public String getFlowName();

	/**
	 * @param flowName the flowName to set
	 */
	public void setFlowName(String flowName);

	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(long timestamp);
	
	/**
	 * @return the timestamp
	 */
	public long getTimestamp();

    /**
     * Set the comment.
     *
     * @param comment
     */
    public void setComment(String comment);

    /**
     * Get the comment.
     *
     * @return
     */
	public String getComment();

}
