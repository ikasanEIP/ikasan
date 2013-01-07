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
package org.ikasan.framework.web.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

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
