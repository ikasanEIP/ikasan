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
package org.ikasan.dashboard.notification.contentproducer;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ikasan.dashboard.notification.NotificationConfiguredResource;
import org.ikasan.dashboard.notification.NotificationContentProducer;
import org.ikasan.error.reporting.service.ErrorCategorisationService;
import org.ikasan.spec.configuration.PlatformConfigurationService;
import org.ikasan.topology.model.Component;
import org.ikasan.topology.model.Filter;
import org.ikasan.topology.model.FilterComponent;
import org.ikasan.topology.model.Notification;


/**
 * 
 * @author Ikasan Development Team
 *
 */
public class CategorisedErrorNotificationContentProducer extends NotificationConfiguredResource implements NotificationContentProducer
{	
	private static Logger logger = LoggerFactory.getLogger(CategorisedErrorNotificationContentProducer.class);
	
	private ErrorCategorisationService errorCategorisationService;
	private PlatformConfigurationService platfromConfigurationService;
	
	/**
	 * 
	 * @param notification
	 */
	public CategorisedErrorNotificationContentProducer(Notification notification)
	{
		super();
		super.notification = notification;
		if(super.notification == null)
		{
			throw new IllegalArgumentException("Notification cannot be NULL!");
		}
	}

	/**
	 * 
	 * @param notification
	 * @param errorCategorisationService
	 */
	public CategorisedErrorNotificationContentProducer(Notification notification,
			ErrorCategorisationService errorCategorisationService,
			PlatformConfigurationService platfromConfigurationService)
	{
		super();
		super.notification = notification;
		if(super.notification == null)
		{
			throw new IllegalArgumentException("Notification cannot be NULL!");
		}
		this.errorCategorisationService = errorCategorisationService;
		if(this.errorCategorisationService == null)
		{
			throw new IllegalArgumentException("errorCategorisationService cannot be NULL!");
		}
		this.platfromConfigurationService = platfromConfigurationService;
		if(this.platfromConfigurationService == null)
		{
			throw new IllegalArgumentException("platfromConfigurationService cannot be NULL!");
		}
	}
	
	protected String createDeepLinkUrl() throws UnsupportedEncodingException
	{
		StringBuilder deeplinkUrl = new StringBuilder(this.platfromConfigurationService.getConfigurationValue("dashboardBaseUrl"));
		
		deeplinkUrl.append("/?filter=").append(URLEncoder.encode(this.notification.getFilter().getName(), "UTF-8")).append("&ui=categorisedErrorByFilter");
		
		return deeplinkUrl.toString();
	}

	
	/* (non-Javadoc)
	 * @see org.ikasan.dashboard.notification.NotificationContentProducer#getNoitificationReceivers()
	 */
	@Override
	public String getNoitificationReceivers()
	{
		return this.configuration.getRecipients();
	}

	/* (non-Javadoc)
	 * @see org.ikasan.dashboard.notification.NotificationContentProducer#getNotificationSubject()
	 */
	@Override
	public String getNotificationSubject()
	{
		return this.configuration.getSubject();
	}

	/* (non-Javadoc)
	 * @see org.ikasan.dashboard.notification.NotificationContentProducer#getNotificationContent()
	 */
	@Override
	public String getNotificationContent() throws UnsupportedEncodingException
	{
		StringBuilder body = new StringBuilder(this.configuration.getBody()).append("<br>");
		
		body.append("Please click <a href=\"").append(this.createDeepLinkUrl())
			.append("\">here</a>").append(" to access the errors.");
		
		return body.toString();
	}


	/* (non-Javadoc)
	 * @see org.ikasan.dashboard.notification.NotificationContentProducer#isNotificationRequired()
	 */
	@Override
	public boolean isNotificationRequired()
	{
		Filter filter = this.notification.getFilter();
		
		ArrayList<String> moduleNames = new ArrayList<String>();
		ArrayList<String> flowNames = new ArrayList<String>();
		ArrayList<String> componentNames = new ArrayList<String>();
		
		for(FilterComponent filterComponent: filter.getComponents())
		{
			Component component = filterComponent.getComponent();
			
			if(!componentNames.contains(component.getName()))
			{
				logger.debug("adding component = " + component.getName());
				componentNames.add(component.getName());
			}
			
			if(!flowNames.contains(component.getFlow().getName()))
			{
				logger.debug("adding flow = " + component.getFlow().getName());
				flowNames.add(component.getFlow().getName());
			}
			
			if(!moduleNames.contains(component.getFlow().getModule().getName()))
			{
				logger.debug("adding module = " + component.getFlow().getModule().getName());
				moduleNames.add(component.getFlow().getModule().getName());
			}
		}

		
		long lastSent = 0;
		
		if(this.configuration.getLastEmailSentTimeStamp() != null)
		{
			lastSent = this.configuration.getLastEmailSentTimeStamp();
		}
	
		return (this.errorCategorisationService.findCategorisedErrorOccurences(moduleNames, flowNames, componentNames,
				null, null, null, new Date(lastSent), new Date(), 1000).size() > 0);
	}

}
