/*
 * $Id: SchedulerController.java 16798 2009-04-24 14:12:09Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/webconsole/war/src/main/java/org/ikasan/framework/web/controller/SchedulerController.java $
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
@RequestMapping("/admin/scheduler.htm")
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
    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView handleRequest() throws Exception
    {
        List<Trigger> triggers = new ArrayList<Trigger>();
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
        ModelMap myModel = new ModelMap();
        myModel.put("triggers", triggers);
        return new ModelAndView("admin/viewScheduler", myModel);
    }
}
