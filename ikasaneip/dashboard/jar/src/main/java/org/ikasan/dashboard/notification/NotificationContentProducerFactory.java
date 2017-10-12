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

import org.ikasan.dashboard.notification.contentproducer.CategorisedErrorNotificationContentProducer;
import org.ikasan.dashboard.notification.contentproducer.CategorisedErrorPeriodicNotificationContentProducer;
import org.ikasan.error.reporting.service.ErrorCategorisationService;
import org.ikasan.spec.configuration.ConfigurationService;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.configuration.PlatformConfigurationService;
import org.ikasan.topology.constants.NotificationConstants;
import org.ikasan.topology.model.Notification;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class NotificationContentProducerFactory
{
	private ErrorCategorisationService errorCategorisationService;
	private ConfigurationService<ConfiguredResource> configurationService;
	private PlatformConfigurationService platfromConfigurationService;
	
	/**
	 * Constructor
	 * 
	 * @param errorCategorisationService
	 * @param configurationService
	 */
	public NotificationContentProducerFactory(
			ErrorCategorisationService errorCategorisationService,
			ConfigurationService<ConfiguredResource> configurationService,
			PlatformConfigurationService platfromConfigurationService)
	{
		super();
		this.errorCategorisationService = errorCategorisationService;
		if(this.errorCategorisationService  == null)
		{
			throw new IllegalArgumentException("errorCategorisationService cannot be NULL!");
		}
		this.configurationService = configurationService;
		if(this.configurationService == null)
		{
			throw new IllegalArgumentException("configurationService cannot be NULL!");
		}
		this.platfromConfigurationService = platfromConfigurationService;
		if(this.platfromConfigurationService == null)
		{
			throw new IllegalArgumentException("platfromConfigurationService cannot be NULL!");
		}
	}



	public NotificationContentProducer getNotificationContentProducer(Notification notification)
	{
		NotificationContentProducer producer = null;
		
		if(notification.getContext().equals(NotificationConstants.CATEGORISED_ERROR_NOTIFICATION_CONTEXT))
		{
			CategorisedErrorNotificationContentProducer categorisedErrorNotificationContentProducer = new CategorisedErrorNotificationContentProducer(notification
					, this.errorCategorisationService, this.platfromConfigurationService);
			this.configurationService.configure(categorisedErrorNotificationContentProducer);
			
			producer = categorisedErrorNotificationContentProducer;
		}
		else if(notification.getContext().equals(NotificationConstants.CATEGORISED_ERROR_NOTIFICATION_CONTEXT_PERIODIC))
		{
			CategorisedErrorPeriodicNotificationContentProducer categorisedErrorNotificationContentProducer = new CategorisedErrorPeriodicNotificationContentProducer(notification
					, this.errorCategorisationService, this.platfromConfigurationService);
			this.configurationService.configure(categorisedErrorNotificationContentProducer);
			
			producer = categorisedErrorNotificationContentProducer;
		}
		else
		{
			throw new IllegalArgumentException(notification.getContext() + " is not a supported notification context!");
		}
		
		return producer;
	}
}
