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

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.ikasan.spec.configuration.ConfigurationService;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.configuration.PlatformConfigurationService;
import org.ikasan.topology.model.Notification;
import org.ikasan.topology.service.TopologyService;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class NotifierServiceImpl implements NotifierService
{
	private static Logger logger = Logger.getLogger(NotifierServiceImpl.class);
	
	private static ScheduledExecutorService executor;
	private int notificationIntervalMinutes = 5;
	
	private TopologyService topologyService;	
	private NotificationContentProducerFactory notificationContentProducerFactory;
	private List<Notifier> notifiers;
	private PlatformConfigurationService platformConfigurationService;
	private String state;
	private ConfigurationService<ConfiguredResource> configurationService;
	
	/**
	 * Constructor
	 * 
	 * @param topologyService
	 * @param notificationContentProducerFactory
	 * @param notifiers
	 * @param platformConfigurationService
	 */
	public NotifierServiceImpl(
			TopologyService topologyService,
			NotificationContentProducerFactory notificationContentProducerFactory,
			List<Notifier> notifiers,
			PlatformConfigurationService platformConfigurationService,
			ConfigurationService<ConfiguredResource> configurationService)
	{
		super();
		this.topologyService = topologyService;
		this.notificationContentProducerFactory = notificationContentProducerFactory;
		this.notifiers = notifiers;
		this.platformConfigurationService = platformConfigurationService;
		this.configurationService = configurationService;
		
		this.start();
	}

	/* (non-Javadoc)
	 * @see org.ikasan.dashboard.notification.NotifierService#performNotifications()
	 */
	@Override
	public void performNotifications()
	{
		List<Notification> notifications = this.topologyService.getAllNotifications();
		
		if(notifications == null || notifications.size() == 0)
		{
			logger.debug("No notifications to perform!");
		}
		else
		{
			
			for(Notification notification: notifications)
			{
				try
				{
					NotificationContentProducer notificationContentProducer = this.notificationContentProducerFactory.getNotificationContentProducer(notification);
					
					if(notificationContentProducer.isNotificationRequired())
					{
						for(Notifier notifier: notifiers)
						{
							notifier.sendNotification(notificationContentProducer);
						}
						
						notificationContentProducer.getConfiguration().setLastEmailSentTimeStamp(System.currentTimeMillis() - 2000);
						
						this.configurationService.update(notificationContentProducer);
					}
					else
					{
						logger.debug("No need to notify for notification: " + notification);
					}
				}
				catch(Exception e)
				{
					// log error and then move onto next notification.
					logger.error("An error has occurred trying to send dashboard notifications. This is most likely due to " +
							"a badly configured notification " + notification, e);
				}
			}			
		}
	}

	/* (non-Javadoc)
	 * @see org.ikasan.dashboard.notification.NotifierService#start()
	 */
	@Override
	public void start()
	{
		String notificationInterval = this.platformConfigurationService.getConfigurationValue("notificationIntervalMinutes");
		
		try
		{
			notificationIntervalMinutes = Integer.parseInt(notificationInterval);
		}
		catch (Exception e) 
		{
			logger.warn("Unable to set notification interval from platform configuration using configutation name: notificationIntervalMinutes." +
					" Using default value of " + this.notificationIntervalMinutes + " minutes.");
		}
		
		logger.info("Starting dashboard notification service with a notificaiton interval of " 
				+ this.notificationIntervalMinutes + " minutes.");
		
		executor = Executors.newScheduledThreadPool(1);
		executor.scheduleAtFixedRate(new NotificationTask(), 1, notificationIntervalMinutes, TimeUnit.MINUTES);
		
		this.state = STATE_RUNNING;
	}

	/* (non-Javadoc)
	 * @see org.ikasan.dashboard.notification.NotifierService#stop()
	 */
	@Override
	public void stop()
	{
		logger.info("Stopping dashboard notification service");
		executor.shutdown();
		this.state = STATE_STOPPED;
		
		
	}
	
	private class NotificationTask implements Runnable
	{

		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run()
		{
			performNotifications();	
		}
		
	}

	public static void shutdown()
	{
		logger.info("Stopping dashboard notification service");
		executor.shutdown();
	}

	/* (non-Javadoc)
	 * @see org.ikasan.dashboard.notification.NotifierService#getState()
	 */
	@Override
	public String getState()
	{
		return this.state;
	}

	/* (non-Javadoc)
	 * @see org.ikasan.dashboard.notification.NotifierService#getNotificationInterval()
	 */
	@Override
	public int getNotificationInterval()
	{
		return this.notificationIntervalMinutes;
	}
}
