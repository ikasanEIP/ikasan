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
package org.ikasan.dashboard.ui;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.ikasan.dashboard.discovery.DiscoverySchedulerService;
import org.ikasan.dashboard.harvesting.HarvestingSchedulerService;
import org.ikasan.dashboard.housekeeping.HousekeepingSchedulerService;
import org.ikasan.dashboard.notification.NotifierServiceImpl;
import org.ikasan.dashboard.ui.framework.cache.TopologyStateCache;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * 
 * 
 * @author Ikasan Development Team
 *
 */
public class WebAppStartStopListener implements ServletContextListener
{
	private Logger logger = Logger.getLogger(WebAppStartStopListener.class);

    public WebAppStartStopListener()
    {

    }
	
    // Our web app (Vaadin app) is starting up.
    public void contextInitialized ( ServletContextEvent servletContextEvent )
    {
        ServletContext ctx = servletContextEvent.getServletContext();
        logger.info( "Web app context initialized." ); 
        logger.info( "TRACE Servlet Context Name : " + ctx.getServletContextName() );
        logger.info( "TRACE Server Info : " + ctx.getServerInfo() );
        logger.info( "TRACE getCurrentWebApplicationContext : " + ContextLoaderListener.getCurrentWebApplicationContext() );


        final WebApplicationContext springContext = WebApplicationContextUtils.getWebApplicationContext(servletContextEvent.getServletContext());
        HousekeepingSchedulerService schedulerService = (HousekeepingSchedulerService)springContext.getBean("housekeepingSchedulerService");
        HarvestingSchedulerService harvestingSchedulerService = (HarvestingSchedulerService)springContext.getBean("harvestSchedulerService");
        DiscoverySchedulerService discoverySchedulerService = (DiscoverySchedulerService)springContext.getBean("discoverySchedulerService");

        if(schedulerService != null)
        {
            try
            {
                logger.info("Starting scheduler.");
                schedulerService.registerJobs();
                schedulerService.startScheduler();
                harvestingSchedulerService.registerJobs();
                harvestingSchedulerService.startScheduler();
                discoverySchedulerService.startScheduler();
                logger.info("Successfully registered jobs and started scheduler.");
            }
            catch (Exception e)
            {
                logger.error("Could not start scheduler!", e);
            }
        }
        else
        {
            logger.warn("schedulerService was null when trying to access from web application context!");
        }

    }

    // Our web app (Vaadin app) is shutting down.
    public void contextDestroyed ( ServletContextEvent servletContextEvent )
    {
    	logger.info( "Web app context destroyed." );  // INFO logging.
        TopologyStateCache.shutdown();
        NotifierServiceImpl.shutdown();
        Broadcaster.shutdown();

        HousekeepingSchedulerService schedulerService = (HousekeepingSchedulerService)ContextLoaderListener
                .getCurrentWebApplicationContext().getBean("housekeepingSchedulerService");

        if(schedulerService != null)
        {
            try
            {
                logger.info("Shutting down scheduler.");
                schedulerService.shutdownScheduler();
                logger.info("Successfully shut down scheduler.");
            }
            catch (Exception e)
            {
                logger.error("Could not shutdown scheduler!", e);
            }
        }
        else
        {
            logger.warn("schedulerService was null when trying to access from web application context!");
        }
    }

}
