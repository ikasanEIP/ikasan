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
package org.ikasan.dashboard.notification;


/**
 * 
 * @author Ikasan Development Team
 *
 */
public class NotificationContentProducerConfiguration
{
	private String subject;
	private String body;
	private String recipients;
	private String notificationName;
	private Long lastEmailSentTimeStamp;
	
	/**
	 * @return the subject
	 */
	public String getSubject()
	{
		return subject;
	}
	
	/**
	 * @param subject the subject to set
	 */
	public void setSubject(String subject)
	{
		this.subject = subject;
	}
	
	/**
	 * @return the body
	 */
	public String getBody()
	{
		return body;
	}
	
	/**
	 * @param body the body to set
	 */
	public void setBody(String body)
	{
		this.body = body;
	}
	
	

	/**
	 * @return the notificationName
	 */
	public String getNotificationName()
	{
		return notificationName;
	}

	/**
	 * @param notificationName the notificationName to set
	 */
	public void setNotificationName(String notificationName)
	{
		this.notificationName = notificationName;
	}

	/**
	 * @return the lastEmailSentTimeStamp
	 */
	public Long getLastEmailSentTimeStamp()
	{
		return lastEmailSentTimeStamp;
	}

	/**
	 * @param lastEmailSentTimeStamp the lastEmailSentTimeStamp to set
	 */
	public void setLastEmailSentTimeStamp(Long lastEmailSentTimeStamp)
	{
		this.lastEmailSentTimeStamp = lastEmailSentTimeStamp;
	}

	/**
	 * @return the recipients
	 */
	public String getRecipients()
	{
		return recipients;
	}

	/**
	 * @param recipients the recipients to set
	 */
	public void setRecipients(String recipients)
	{
		this.recipients = recipients;
	}
}
