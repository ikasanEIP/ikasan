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
package org.ikasan.framework.flow.event.listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.ikasan.framework.component.Event;
import org.ikasan.framework.flow.FlowElement;
import org.ikasan.framework.flow.event.dao.TriggerDao;
import org.ikasan.framework.flow.event.model.Trigger;
import org.ikasan.framework.flow.event.model.TriggerRelationship;
import org.ikasan.framework.flow.event.service.FlowEventJob;

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
public class JobAwareFlowEventListener implements FlowEventListener
{
    /** Before constant for location prefix */
    private static final String AFTER_LOCATION_PREFIX = "after";

    /** After constant for location prefix */
    private static final String BEFORE_LOCATION_PREFIX = "before";

    /** Registered jobs */
    private Map<String, FlowEventJob> flowEventJobs;

    /** Registered triggers */
    private Map<String, List<Trigger>> triggers = new HashMap<String, List<Trigger>>();

    /** Data access object for dynamic trigger persistence */
    private TriggerDao triggerDao;

    /** Logger instance */
    private static final Logger logger = Logger.getLogger(JobAwareFlowEventListener.class);

    /**
     * Constructor
     * 
     * @param flowEventJobs - The list of flow event jobs
     * @param triggerDao - The DAO for the trigger
     */
    public JobAwareFlowEventListener(Map<String, FlowEventJob> flowEventJobs, TriggerDao triggerDao)
    {
        super();
        this.flowEventJobs = flowEventJobs;
        this.triggerDao = triggerDao;
        for (Trigger dynamicTrigger : triggerDao.findAll())
        {
            mapTrigger(dynamicTrigger);
        }
    }

    /**
     * Registers a List of static triggers
     * 
     * Static Triggers are usually set through configuration, and cannot be
     * added to or deleted at runtime
     * 
     * @param staticTriggers - List of Triggers
     */
    public void addStaticTriggers(List<Trigger> staticTriggers)
    {
        for (Trigger trigger : staticTriggers)
        {
            addStaticTrigger(trigger);
        }
    }

    /**
     * Registers a static triggers
     * 
     * Static Triggers are usually set through configuration, and cannot be
     * added to or deleted at runtime
     * 
     * @param trigger - The static Trigger to add
     */
    public void addStaticTrigger(Trigger trigger)
    {
        mapTrigger(trigger);
    }

    /**
     * Registers a dynamic trigger
     * 
     * Dynamic triggers may be created and deleted at runtime. They are persised
     * using the triggerDao
     * 
     * @param trigger - The dynamic TRigger to add
     */
    public void addDynamicTrigger(Trigger trigger)
    {
        triggerDao.save(trigger);
        mapTrigger(trigger);
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
        List<Trigger> list = triggers.get(key);
        if (list == null)
        {
            list = new ArrayList<Trigger>();
            triggers.put(key, list);
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
     * org.ikasan.framework.flow.event.listener.FlowEventListener#beforeFlow
     * (java.lang.String, java.lang.String,
     * org.ikasan.framework.component.Event)
     */
    public void beforeFlow(String moduleName, String flowName, Event event)
    {
        String key = moduleName + flowName + TriggerRelationship.BEFORE.getDescription();
        List<Trigger> beforeFlowTriggers = triggers.get(key);
        fireTriggers(moduleName, flowName, event, beforeFlowTriggers, BEFORE_LOCATION_PREFIX + " " + flowName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.ikasan.framework.flow.event.listener.FlowEventListener#afterFlow(
     * java.lang.String, java.lang.String, org.ikasan.framework.component.Event)
     */
    public void afterFlow(String moduleName, String flowName, Event event)
    {
        String key = moduleName + flowName + TriggerRelationship.AFTER.getDescription();
        List<Trigger> afterFlowTriggers = triggers.get(key);
        fireTriggers(moduleName, flowName, event, afterFlowTriggers, AFTER_LOCATION_PREFIX + " " + flowName);
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
    public void beforeFlowElement(String moduleName, String flowName, FlowElement flowElement, Event event)
    {
        String flowElementName = flowElement.getComponentName();
        String key = moduleName + flowName + TriggerRelationship.BEFORE.getDescription() + flowElementName;
        List<Trigger> beforeElementTriggers = triggers.get(key);
        fireTriggers(moduleName, flowName, event, beforeElementTriggers, BEFORE_LOCATION_PREFIX + " " + flowElementName);
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
    public void afterFlowElement(String moduleName, String flowName, FlowElement flowElement, Event event)
    {
        String flowElementName = flowElement.getComponentName();
        String key = moduleName + flowName + TriggerRelationship.AFTER.getDescription() + flowElementName;
        List<Trigger> afterElementTriggers = triggers.get(key);
        fireTriggers(moduleName, flowName, event, afterElementTriggers, AFTER_LOCATION_PREFIX + " " + flowElementName);
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
    private void fireTriggers(String moduleName, String flowName, Event event, List<Trigger> associatedTriggers, String location)
    {
        if (associatedTriggers != null)
        {
            for (Trigger associatedTrigger : associatedTriggers)
            {
                String jobName = associatedTrigger.getJobName();
                FlowEventJob flowEventAgent = flowEventJobs.get(jobName);
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
    }

    /**
     * Returns a safe List of all the triggers associated with a particular point in a particular flow
     * 
     * @param moduleName - THe name of the module
     * @param flowName - The name of the flow
     * @param relationship - The Trigger relationship (before or after)
     * @param flowElementName - The flow element name
     * 
     * @return - List of triggers that apply at the point in flow specified by the parameters
     */
    public List<Trigger> getTriggers(String moduleName, String flowName, TriggerRelationship relationship, String flowElementName)
    {
        List<Trigger> result = new ArrayList<Trigger>();
        String key = moduleName + flowName + relationship.getDescription() + flowElementName;
        List<Trigger> mappedTriggers = triggers.get(key);
        if (mappedTriggers != null)
        {
            result.addAll(mappedTriggers);
        }
        return result;
    }

    /**
     * Deletes a dynamic trigger, specified by trigger id. This has the effect of:<br>
     * <br>
     *  1) de-registering the trigger from the mapped triggers, so that it no longer takes effect
     *  2) deleting the trigger so that it is not reloaded next time
     * 
     * @param triggerId - The dynamic Trigger to deregister
     */
    public void deleteDynamicTrigger(Long triggerId)
    {
        Trigger trigger = triggerDao.findById(triggerId);
        if (trigger == null)
        {
            logger.warn("could not find trigger with id [" + triggerId + "]");
        }
        else
        {
            unmapTrigger(trigger);
            triggerDao.delete(trigger);
        }
    }

    /**
     * Unmaps the specified trigger if it is mapped
     * 
     * @param trigger - The Trigger to unmap
     */
    private void unmapTrigger(Trigger trigger)
    {
        String key = generateKey(trigger);
        List<Trigger> list = triggers.get(key);
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
        return new HashMap<String, FlowEventJob>(flowEventJobs);
    }
}
