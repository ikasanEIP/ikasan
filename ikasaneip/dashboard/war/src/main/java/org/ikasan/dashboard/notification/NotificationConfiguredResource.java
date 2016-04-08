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

import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.topology.model.Notification;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public abstract class NotificationConfiguredResource implements ConfiguredResource<NotificationContentProducerConfiguration>
{
	public static final String CONFIGURED_RESOURCE_ID_PREFIX = "dashboardNotificationConfiguredResourceId-";
	
	protected Notification notification;
	protected NotificationContentProducerConfiguration configuration =
			new NotificationContentProducerConfiguration();
	
	/* (non-Javadoc)
	 * @see org.ikasan.spec.configuration.Configured#getConfiguration()
	 */
	@Override
	public NotificationContentProducerConfiguration getConfiguration()
	{
		return this.configuration;
	}

	/* (non-Javadoc)
	 * @see org.ikasan.spec.configuration.Configured#setConfiguration(java.lang.Object)
	 */
	@Override
	public void setConfiguration(NotificationContentProducerConfiguration configuration)
	{
		this.configuration = configuration;
	}

	/* (non-Javadoc)
	 * @see org.ikasan.spec.configuration.ConfiguredResource#getConfiguredResourceId()
	 */
	@Override
	public String getConfiguredResourceId()
	{
		return this.CONFIGURED_RESOURCE_ID_PREFIX + notification.getName();
	}

	/* (non-Javadoc)
	 * @see org.ikasan.spec.configuration.ConfiguredResource#setConfiguredResourceId(java.lang.String)
	 */
	@Override
	public void setConfiguredResourceId(String id)
	{
		// nothing to do here as we have a fixed name based upon the name passed in on construction.
	}

}
