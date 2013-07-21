/*
 * $Id$
 * $URL$
 * 
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * Copyright (c) 2003-2008 Mizuho International plc. and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the 
 * Free Software Foundation Europe e.V. Talstrasse 110, 40217 Dusseldorf, Germany 
 * or see the FSF site: http://www.fsfeurope.org/.
 * ====================================================================
 */
package org.ikasan.framework.web.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.quartz.Scheduler;
import org.quartz.Trigger;

/**
 * Controller class for the scheduler view
 * 
 * @author Ikasan Development Team
 */
@Controller
public class SchedulerController
{
    /** Logger for this class */
    private static Logger logger = Logger.getLogger(SchedulerController.class);

    /** The scheduler to use */
    private Scheduler platformScheduler;

    /**
     * Constructor
     * 
     * @param platformScheduler - THe platform scheduler
     */
    @Autowired
    public SchedulerController(Scheduler platformScheduler)
    {
        super();
        this.platformScheduler = platformScheduler;
        logger.info("platformScheduler:" + platformScheduler);
    }

    /**
     * Get the platform scheduler
     * 
     * @return platform scheduler
     */
    @ModelAttribute("platformScheduler")
    public Scheduler getPlatformScheduler()
    {
        return platformScheduler;
    }

    /**
     * Handle the request
     * 
     * Warning is suppressed because ModelMap does not support Generics
     * 
     * @return model and view to go to next ("admin/viewScheduler")
     * @throws Exception - Catch all
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/admin/viewScheduler.htm", method = RequestMethod.GET)
    public ModelAndView viewScheduler() throws Exception
    {
        List<Trigger> triggers = getTriggers();
        ModelMap myModel = new ModelMap();
        myModel.put("triggers", triggers);
        return new ModelAndView("admin/viewScheduler", myModel);
    }
    
    /**
     * Handle the request to put the scheduler on standby
     * 
     * @return a redirect to viewScheduler
     * @throws Exception - Catch all
     */
    @RequestMapping(value = "/admin/schedulerStandby.htm", method = RequestMethod.POST)
    public String stopScheduler() throws Exception
    {
        if (!platformScheduler.isInStandbyMode())
        {
            platformScheduler.standby();
        }
        return "redirect:viewScheduler.htm";
    }

    /**
     * Handle the request to resume the scheduler
     * 
     * @return a redirect to viewScheduler
     * @throws Exception - Catch all
     */
    @RequestMapping(value = "/admin/schedulerResume.htm", method = RequestMethod.POST)
    public String resumeScheduler() throws Exception
    {
        if (platformScheduler.isInStandbyMode())
        {
            platformScheduler.start();
        }
        return "redirect:viewScheduler.htm";
    }

    /**
     * Helper wmethod to return a list of triggers fro a started scheduler
     * 
     * @return List of Triggers
     */
    private List<Trigger> getTriggers() throws Exception
    {
        List<Trigger> triggers = new ArrayList<Trigger>();
        if (!platformScheduler.isShutdown())
        {
            String[] triggerGroupNames = platformScheduler.getTriggerGroupNames();
            logger.info("found triggerGroupNames:" + triggerGroupNames.length);
            for (String triggerGroupName : triggerGroupNames)
            {
                String[] triggerNames = platformScheduler.getTriggerNames(triggerGroupName);
                logger.info("found triggerNames:" + triggerNames.length + ", for triggerGroupName:" + triggerGroupName);
                for (String triggerName : triggerNames)
                {
                    triggers.add(platformScheduler.getTrigger(triggerName, triggerGroupName));
                }
            }
        }
        return triggers;
    }
    
}
