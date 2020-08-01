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
package org.ikasan.wiretap.listener;

import org.ikasan.spec.dashboard.DashboardRestService;
import org.ikasan.spec.flow.FlowElement;
import org.ikasan.spec.flow.FlowEvent;
import org.ikasan.spec.flow.FlowEventListener;
import org.ikasan.spec.management.FlowEventListenerMaintenanceService;
import org.ikasan.spec.module.ModuleService;
import org.ikasan.spec.trigger.Trigger;
import org.ikasan.spec.trigger.TriggerRelationship;
import org.ikasan.spec.trigger.TriggerService;
import org.ikasan.trigger.dao.TriggerDao;
import org.ikasan.trigger.service.FlowEventJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The <code>JobAwareFlowEventListener</code> provides a
 * <code>FlowEventListener</code> implementation that brokers the life-cycle
 * callbacks that it receives through to locally registered
 * <code>FlowEventJob</code> instances.
 * 
 * Each <code>FlowEventJob</code> represents some sort of executable platform
 * service
 * 
 * Whether not a particular <code>FlowEventJob</code> need be called at a given
 * life-cycle point will depend on the existence of <code>Trigger</code> bound to
 * either before or after the flow or flow element
 * 
 * @author Ikasan Development Team
 */
public class JobAwareFlowEventListener implements FlowEventListener, FlowEventListenerMaintenanceService<FlowEventJob>,
                                                  TriggerService
{

    /** Registered jobs */
    private Map<String, FlowEventJob> flowEventJobs;

    /** Registered triggers */
    private Map<String, List<Trigger>> triggers = new HashMap<>();

    /** Data access object for dynamic trigger persistence */
    private TriggerDao triggerDao;

    /** Module service is used to get information about module
     *  Required for publishing metadata
     **/
    private ModuleService moduleService;

    /**
     * Dashboard client used for publishing module metadata to dashboard
     */
    private DashboardRestService moduleMetadataDashboardRestService;

    /** Logger instance */
    private static final Logger logger = LoggerFactory.getLogger(JobAwareFlowEventListener.class);

    // TODO - find a better way of identifying failure and reloading triggers
    /** flag to identify initial trigger load failures */
    private boolean triggersLoaded = false;

    /**
     * Constructor
     * 
     * @param flowEventJobs - The list of flow event jobs
     * @param triggerDao - The DAO for the trigger
     */
    public JobAwareFlowEventListener(Map<String, FlowEventJob> flowEventJobs,
                                     TriggerDao triggerDao,
                                     ModuleService moduleService,
                                     DashboardRestService moduleMetadataDashboardRestService
                                     )
    {
        super();
        this.flowEventJobs = flowEventJobs;
        this.triggerDao = triggerDao;
        this.moduleMetadataDashboardRestService = moduleMetadataDashboardRestService;
        this.moduleService = moduleService;
        loadTriggers();
    }

    /**
     * Load all triggers available from the DAO
     */
    private void loadTriggers()
    {
        try
        {
            for (Trigger dynamicTrigger : triggerDao.findAll())
            {
                mapTrigger(dynamicTrigger);
            }

            this.triggersLoaded = true;
        }
        catch(RuntimeException e)
        {
            // just log failure as the application should still deploy
            logger.debug("Failed to load DAO triggers", e);
        }
    }

    /**
     * Registers a dynamic trigger
     * 
     * Dynamic triggers may be created and deleted at runtime. They are persisted
     * using the triggerDao
     * 
     * @param trigger - The dynamic TRigger to add
     */
    public void addDynamicTrigger(Trigger trigger)
    {
        this.triggerDao.save(trigger);
        mapTrigger(trigger);
        moduleMetadataDashboardRestService.publish(moduleService.getModule(trigger.getModuleName()));
    }

    /**
     * Registers a trigger locally, mapping it by a key comprised of the
     * credentials by which it may later be retrieved
     * 
     * @param trigger - The trigger to map
     */
    private void mapTrigger(Trigger trigger)
    {
        String key = generateKey(trigger);
        List<Trigger> list = this.triggers.get(key);
        if (list == null)
        {
            list = new ArrayList<>();
            this.triggers.put(key, list);
        }
        list.add(trigger);
    }

    /**
     * Generates a mapping key using the credentials by which it will be required at flow time
     * 
     * @param trigger - The Trigger to generate a key from
     * @return The key
     */
    private String generateKey(Trigger trigger)
    {
        String key = trigger.getModuleName() + trigger.getFlowName() + trigger.getRelationship().getDescription();
        if (trigger.appliesToFlowElement())
        {
            key = key + trigger.getFlowElementName();
        }
        return key;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.ikasan.framework.flow.event.listener.FlowEventListener#beforeFlowElement
     * (java.lang.String, java.lang.String,
     * org.ikasan.framework.flow.FlowElement,
     * org.ikasan.framework.component.Event)
     */
    @Override
    public void beforeFlowElement(String moduleName, String flowName, FlowElement flowElement, FlowEvent event)
    {
        flowElement(moduleName,flowName,flowElement,event,TriggerRelationship.BEFORE.getDescription());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.ikasan.framework.flow.event.listener.FlowEventListener#afterFlowElement
     * (java.lang.String, java.lang.String,
     * org.ikasan.framework.flow.FlowElement,
     * org.ikasan.framework.component.Event)
     */
    @Override
    public void afterFlowElement(String moduleName, String flowName, FlowElement flowElement, FlowEvent event)
    {
        flowElement(moduleName,flowName,flowElement,event,TriggerRelationship.AFTER.getDescription());
    }

    private void flowElement(String moduleName, String flowName, FlowElement flowElement, FlowEvent event, String location)
    {
        if(!triggersLoaded)
        {
            loadTriggers();
        }

        String flowElementName = flowElement.getComponentName();
        String key = moduleName + flowName + location + flowElementName;
        List<Trigger> afterElementTriggers = this.triggers.get(key);
        if(afterElementTriggers != null && afterElementTriggers.size() > 0)
        {
            fireTriggers(moduleName, flowName, event, afterElementTriggers, location + " " + flowElementName);
        }
    }


    /**
     * location aware implementation method, calls the agent associated with
     * each relevant advice
     * 
     * @param moduleName - THe name of the module
     * @param flowName - The name of the flow
     * @param event - The event
     * @param associatedTriggers - The associated triggers to fire
     * @param location - The location of the listener
     */
    private void fireTriggers(String moduleName, String flowName, FlowEvent event, List<Trigger> associatedTriggers, String location)
    {
        for (Trigger associatedTrigger : associatedTriggers)
        {
            String jobName = associatedTrigger.getJobName();
            FlowEventJob flowEventAgent = this.flowEventJobs.get(jobName);
            if (flowEventAgent == null)
            {
                logger.warn("unknown job [" + jobName + "]");
            }
            else
            {
                flowEventAgent.execute(location, moduleName, flowName, event, new HashMap<String, String>(associatedTrigger.getParams()));
            }
        }
    }

    @Override
    public List<Trigger> getTriggers()
    {
         return triggerDao.findAll();
    }

    @Override
    public Map<String,List<Trigger>> getTriggers(String moduleName, String flowName)
    {
        if ( !triggersLoaded )
        {
            loadTriggers();
        }
        Map<String,List<Trigger>> r = triggers.entrySet().stream()
                                              .filter(e -> isKeyPartOfFlow(e.getKey(),moduleName,flowName))
                                              .collect(Collectors.toMap(e->getShortKey(e.getKey(),moduleName,flowName),Map.Entry::getValue));
        return r;

    }

    private String getShortKey(String key, String moduleName, String flowName)
    {
        return key.replace(moduleName+flowName,"");
    }

    private boolean isKeyPartOfFlow(String key, String moduleName, String flowName)
    {
        return key.startsWith(moduleName+flowName);
    }



    @Override
    public List<Trigger> getTriggers(String moduleName, String flowName, TriggerRelationship relationship, String flowElementName)
    {
        if(!triggersLoaded)
        {
            loadTriggers();
        }

        List<Trigger> result = new ArrayList<Trigger>();
        String key = moduleName + flowName + relationship.getDescription() + flowElementName;
        List<Trigger> mappedTriggers = this.triggers.get(key);
        if (mappedTriggers != null)
        {
            result.addAll(mappedTriggers);
        }
        return result;
    }

    @Override
    public void deleteDynamicTrigger(Long triggerId)
    {
        Trigger trigger = this.triggerDao.findById(triggerId);
        if (trigger == null)
        {
            logger.warn("could not find trigger with id [" + triggerId + "]");
        }
        else
        {
            unmapTrigger(trigger);
            this.triggerDao.delete(trigger);
            moduleMetadataDashboardRestService.publish(moduleService.getModule(trigger.getModuleName()));

        }
    }

    /**
     * Unmaps the specified trigger if it is mapped
     * 
     * @param trigger - The Trigger to unmap
     */
    private void unmapTrigger(Trigger trigger)
    {
        if(!triggersLoaded)
        {
            loadTriggers();
        }

        String key = generateKey(trigger);
        List<Trigger> list = this.triggers.get(key);
        if (list != null)
        {
            Trigger mappedTriggerToDelete = null;
            for (Trigger mappedTrigger : list)
            {
                if (mappedTrigger.getId().equals(trigger.getId()))
                {
                    mappedTriggerToDelete = mappedTrigger;
                }
            }
            if (mappedTriggerToDelete != null)
            {
                list.remove(mappedTriggerToDelete);
            }
        }
    }

    /**
     * Returns a safe Map of all registered FlowEventJobs, keyed by jobName
     * 
     * @return - Map of FlowEventJob keyed by jobName
     */
    public Map<String, FlowEventJob> getRegisteredJobs()
    {
        return new HashMap<String, FlowEventJob>(this.flowEventJobs);
    }

    /**
     * Management for the addition of a new flow event job
     * @param name
     * @param flowEventJob
     */
    public void addJob(String name, FlowEventJob flowEventJob)
    {
        this.flowEventJobs.put(name, flowEventJob);
    }

    /**
     * Management for the removal of an existing flow event job
     * @param name
     * @return true is job removed, false if not
     */
    public FlowEventJob removeJob(String name)
    {
        return this.flowEventJobs.remove(name);
    }
}
