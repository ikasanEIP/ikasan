/*
 * $Id: JobAwareFlowEventListenerTest.java 16808 2009-04-27 07:28:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/framework/src/test/java/org/ikasan/framework/flow/event/listener/JobAwareFlowEventListenerTest.java $
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

import org.ikasan.framework.component.Event;
import org.ikasan.framework.flow.FlowElement;
import org.ikasan.framework.flow.event.dao.TriggerDao;
import org.ikasan.framework.flow.event.model.Trigger;
import org.ikasan.framework.flow.event.model.TriggerRelationship;
import org.ikasan.framework.flow.event.service.FlowEventJob;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Assert;
import org.junit.Test;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class JobAwareFlowEventListenerTest {
	
    /**
     * Mockery for classes
     */
    private Mockery classMockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };
    
    
    String moduleName = "moduleName";
    
    String flowName = "flowName";
    
    Event event = classMockery.mock(Event.class);
    
	private String jobName = "jobName";
	
	FlowEventJob flowEventJob = classMockery.mock(FlowEventJob.class);
    
	private Map<String, FlowEventJob> flowEventJobs;
	
	TriggerDao triggerDao = classMockery.mock(TriggerDao.class);
    
    /**
     * Constructor
     */
    public JobAwareFlowEventListenerTest(){
    	flowEventJobs = new HashMap<String, FlowEventJob>();
    	flowEventJobs.put(jobName, flowEventJob);
    }
	
	@Test
	public void testBeforeFlowElement(){
		
		final FlowElement flowElement = classMockery.mock(FlowElement.class);
		final String componentName = "componentName";
		final List<Trigger> triggers = new ArrayList<Trigger>();
		
		final Map<String, String> triggerParameters = new HashMap<String, String>();
		Trigger trigger = new Trigger( moduleName, flowName, TriggerRelationship.BEFORE.getDescription(), jobName, componentName, triggerParameters );
		triggers.add(trigger);
		
		classMockery.checking(new Expectations()
	    {
	        {
	        	one(triggerDao).findAll();will(returnValue(new ArrayList<Trigger>()));
				one(flowElement).getComponentName();
				will(returnValue(componentName));
				
				one(flowEventJob).execute("before "+componentName, moduleName, flowName, event, triggerParameters);

	        }
	    });
		
		JobAwareFlowEventListener jobAwareFlowEventListener = new JobAwareFlowEventListener(flowEventJobs, triggerDao) ;
		jobAwareFlowEventListener.addStaticTriggers(triggers);

		jobAwareFlowEventListener.beforeFlowElement(moduleName, flowName, flowElement, event);
		
		
	}
	
	@Test
	public void testAfterFlowElement(){
		
		final FlowElement flowElement = classMockery.mock(FlowElement.class);
		final String componentName = "componentName";
		final List<Trigger> triggers = new ArrayList<Trigger>();
		
		final Map<String, String> triggerParameters= new HashMap<String, String>();
		triggers.add(new Trigger(moduleName, flowName, TriggerRelationship.AFTER.getDescription(), jobName, componentName, triggerParameters));
		
		classMockery.checking(new Expectations()
	    {
	        {
	        	one(triggerDao).findAll();will(returnValue(new ArrayList<Trigger>()));
	        	
	        	
				one(flowElement).getComponentName();
				will(returnValue(componentName));
				
				one(flowEventJob).execute("after "+componentName, moduleName, flowName, event, triggerParameters);

	        }
	    });
		
		JobAwareFlowEventListener jobAwareFlowEventListener = new JobAwareFlowEventListener(flowEventJobs, triggerDao);
		jobAwareFlowEventListener.addStaticTriggers(triggers);
		jobAwareFlowEventListener.afterFlowElement(moduleName, flowName, flowElement, event);
	}
	
	@Test
	public void testAddStaticTriggers(){
		
		classMockery.checking(new Expectations()
	    {
	        {
	        	one(triggerDao).findAll();will(returnValue(new ArrayList<Trigger>()));
	        }
	    });
		JobAwareFlowEventListener jobAwareFlowEventListener = new JobAwareFlowEventListener(flowEventJobs, triggerDao);
		String flowElementName = "thisFlowElement";
		Trigger flowElementTrigger1 = new Trigger(moduleName, flowName,TriggerRelationship.BEFORE.getDescription(), jobName, flowElementName );
		Trigger flowElementTrigger2 = new Trigger(moduleName, flowName,TriggerRelationship.BEFORE.getDescription(), jobName, flowElementName );
		Trigger flowElementTrigger3 = new Trigger(moduleName, flowName,TriggerRelationship.AFTER.getDescription(), jobName, flowElementName );

		List<Trigger> staticTriggers = new ArrayList<Trigger>();
		staticTriggers.add(flowElementTrigger1);
		staticTriggers.add(flowElementTrigger2);
		staticTriggers.add(flowElementTrigger3);
		
		jobAwareFlowEventListener.addStaticTriggers(staticTriggers);
		
		List<Trigger> beforeTriggers = jobAwareFlowEventListener.getTriggers(moduleName, flowName, TriggerRelationship.BEFORE, flowElementName);
		Assert.assertTrue("flowElementTrigger1 should now be mapped in the elements list of before triggers",beforeTriggers.contains(flowElementTrigger1));
		Assert.assertTrue("flowElementTrigger2 should now be mapped in the elements list of before triggers",beforeTriggers.contains(flowElementTrigger2));
		Assert.assertTrue("there should be exactly 2 beforeTriggers",beforeTriggers.size()==2);
		
		List<Trigger> afterTriggers = jobAwareFlowEventListener.getTriggers(moduleName, flowName, TriggerRelationship.AFTER, flowElementName);
		Assert.assertTrue("flowElementTrigger3 should now be mapped in the elements list of after triggers",afterTriggers.contains(flowElementTrigger3));
		Assert.assertTrue("there should be exactly 1 afterTriggers",afterTriggers.size()==1);
		classMockery.assertIsSatisfied();
	}
	
	@Test
	public void testAddAndDeleteDynamicTrigger(){
		final String flowElementName = "thisFlowElement";
		
		classMockery.checking(new Expectations()
	    {
	        {
	        	one(triggerDao).findAll();will(returnValue(new ArrayList<Trigger>()));
	        }
	    });
		JobAwareFlowEventListener jobAwareFlowEventListener = new JobAwareFlowEventListener(flowEventJobs, triggerDao);
		classMockery.assertIsSatisfied();
		
		

		//lets create a pretend Trigger
		final Trigger trigger = classMockery.mock(Trigger.class);
		//new Trigger(moduleName, flowName,TriggerRelationship.BEFORE.getDescription(), jobName, flowElementName,new HashMap<String,String>() );
		classMockery.checking(new Expectations()
	    {
	        {
	        	allowing(trigger).getModuleName();will(returnValue(moduleName));
	        	allowing(trigger).getFlowName();will(returnValue(flowName));
	        	allowing(trigger).getFlowElementName();will(returnValue(flowElementName));
	        	allowing(trigger).getRelationship();will(returnValue(TriggerRelationship.get("before")));
	        	allowing(trigger).appliesToFlowElement();will(returnValue(true));
	        }
	    });
		
		
		
		
		//now add
		classMockery.checking(new Expectations()
	    {
	        {
	        	one(triggerDao).save(trigger);
	        }
	    });

		jobAwareFlowEventListener.addDynamicTrigger(trigger);
		List<Trigger> beforeTriggers = jobAwareFlowEventListener.getTriggers(moduleName, flowName, TriggerRelationship.BEFORE, flowElementName);
		Assert.assertTrue("trigger should be mapped in this element's before triggers", beforeTriggers.contains(trigger));
		Assert.assertTrue("this element should have exactly 1 before triggers",beforeTriggers.size()==1);
		classMockery.assertIsSatisfied();
		
		
		//now delete
		final Long triggerId = 1l;
		classMockery.checking(new Expectations()
	    {
	        {
	        	one(triggerDao).findById(triggerId);will(returnValue(trigger));
	        	one(triggerDao).delete(trigger);
	        }
	    });
		classMockery.checking(new Expectations()
	    {
	        {
	        	allowing(trigger).getId();will(returnValue(triggerId));
	        	
	        }
	    });
		jobAwareFlowEventListener.deleteDynamicTrigger(triggerId);
		beforeTriggers = jobAwareFlowEventListener.getTriggers(moduleName, flowName, TriggerRelationship.BEFORE, flowElementName);
		Assert.assertTrue("trigger should no longer exist in this element's before triggers",beforeTriggers.isEmpty());
		classMockery.assertIsSatisfied();
	}
	


}
