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
package org.ikasan.framework.flow.event.listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ikasan.core.flow.FlowElement;
import org.ikasan.framework.component.Event;
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
