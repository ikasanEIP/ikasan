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
package org.ikasan.spec.error.reporting;


/**
 * This interface represents an occurrence of an error in the system encapsulating as much as
 * possible about the occurrence of that error
 * 
 * @author Ikasan Development Team
 *
 */
public interface ErrorOccurrence<EVENT>
{
	/**
	 * Get the module name associated with the error occurrence
	 * @return
     */
    public String getModuleName();

	/**
	 *
	 * @param moduleName
     */
	public void setModuleName(String moduleName);

	/**
	 *
	 * @return
     */
    public String getFlowName();

	/**
	 *
	 * @param flowName
     */
	public void setFlowName(String flowName);

	/**
	 *
	 * @return
     */
    public String getFlowElementName();

	/**
	 *
	 * @param flowElementName
     */
	public void setFlowElementName(String flowElementName);

	/**
	 *
	 * @return
     */
    public String getErrorDetail();

	/**
	 *
	 * @param errorDetail
     */
	public void setErrorDetail(String errorDetail);

	/**
	 *
	 * @return
     */
    public String getEventLifeIdentifier();

	/**
	 *
	 * @param eventLifeIdentifier
     */
    public void setEventLifeIdentifier(String eventLifeIdentifier);

	/**
	 *
	 * @return
     */
    public EVENT getEvent();

	/**
	 *
	 * @param event
     */
	public void setEvent(EVENT event);

	/**
	 *
	 * @return
     */
    public long getTimestamp();

	/**
	 *
	 * @param timestamp
     */
	public void setTimestamp(long timestamp);

	/**
	 *
	 * @return
     */
    public long getExpiry();

	/**
	 *
	 * @param expiry
     */
	public void setExpiry(long expiry);

	/**
	 *
	 * @return
     */
    public String getEventRelatedIdentifier();

	/**
	 *
	 * @param eventRelatedIdentifier
     */
    public void setEventRelatedIdentifier(String eventRelatedIdentifier);

	/**
	 *
	 * @return
     */
    public String getUri();

	/**
	 *
	 * @param uri
     */
	public void setUri(String uri);

	/**
	 *
	 * @return
     */
    public String getAction();

	/**
	 *
	 * @param action
     */
    public void setAction(String action);

	/**
	 *
	 * @return
     */
    public String getErrorMessage();

	/**
	 *
	 * @param errorMessage
     */
	public void setErrorMessage(String errorMessage);
    
    /**
	 * @return the exceptionClass
	 */
	public String getExceptionClass();

	/**
	 * @param exceptionClass the exceptionClass to set
	 */
	public void setExceptionClass(String exceptionClass);
	
	/**
	 * @return the userAction
	 */
	public String getUserAction();

	/**
	 * @param userAction the userAction to set
	 */
	public void setUserAction(String userAction);

	/**
	 * @return the actionedBy
	 */
	public String getActionedBy();

	/**
	 * @param actionedBy the actionedBy to set
	 */
	public void setActionedBy(String actionedBy);

	/**
	 * @return the userActionTimestamp
	 */
	public long getUserActionTimestamp();

	/**
	 * @param userActionTimestamp the userActionTimestamp to set
	 */
	public void setUserActionTimestamp(long userActionTimestamp);

	/**
	 * @return the eventAsString
	 */
	public String getEventAsString();

	/**
	 * @param eventAsString the eventAsString to set
	 */
	public void setEventAsString(String eventAsString);


}
