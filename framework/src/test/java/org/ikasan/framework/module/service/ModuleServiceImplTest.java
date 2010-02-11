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
package org.ikasan.framework.module.service;

import java.util.ArrayList;
import java.util.List;

import org.ikasan.framework.flow.initiator.dao.InitiatorStartupControlDao;
import org.ikasan.framework.initiator.Initiator;
import org.ikasan.framework.initiator.InitiatorStartupControl;
import org.ikasan.framework.initiator.InitiatorStartupControl.StartupType;
import org.ikasan.framework.module.Module;
import org.ikasan.framework.module.container.ModuleContainer;
import org.ikasan.framework.systemevent.service.SystemEventService;
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
public class ModuleServiceImplTest
{
    private Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };
    
    ModuleContainer moduleContainer = mockery.mock(ModuleContainer.class);
    
    InitiatorStartupControlDao initiatorStartupControlDao = mockery.mock(InitiatorStartupControlDao.class);
    
    SystemEventService systemEventService = mockery.mock(SystemEventService.class);

    
    /**
     * Class under test
     */
    private ModuleServiceImpl moduleServiceImpl = new ModuleServiceImpl(moduleContainer,initiatorStartupControlDao,systemEventService);
    
    
    /**
     * Test that getModules simply delgates to the moduleContainer
     */
    @Test
    public void testGetModules()
    {
        final List<Module> modules = new ArrayList<Module>();
        
        mockery.checking(new Expectations()
        {
            {
                one(moduleContainer).getModules();
                will(returnValue(modules));
            }
        });
        Assert.assertEquals("getModules should simply delegate to the container", modules, moduleServiceImpl.getModules());
        
        mockery.assertIsSatisfied();
    }

    @Test
    public void testGetModule()
    {
        final Module module = mockery.mock(Module.class);
        
        final String moduleName = "moduleName";
        
        mockery.checking(new Expectations()
        {
            {
                one(moduleContainer).getModule(moduleName);
                will(returnValue(module));
            }
        });
        Assert.assertEquals("getModule should simply delegate to the container", module, moduleServiceImpl.getModule(moduleName));
        
        mockery.assertIsSatisfied();
    }
    
    @Test
    public void testStopInitiator_willThrowIllegalArgumentExceptionForUnknownModuleName(){
        final String moduleName = "unknownModule";
        final String initiatorName = "initiatorName";
        final String actor = "actor";
        
        mockery.checking(new Expectations()
        {
            {
                one(moduleContainer).getModule(moduleName);
                will(returnValue(null));
            }
        });
        
        IllegalArgumentException illegalArgumentException = null;
        try{
            moduleServiceImpl.stopInitiator(moduleName, initiatorName, actor);
            Assert.fail("exception should have been thrown for unknown moduleName");
        } catch(IllegalArgumentException e){
            illegalArgumentException = e;
        }
        Assert.assertNotNull("exception should have been thrown for unknown moduleName",illegalArgumentException);
        
        mockery.assertIsSatisfied();
        
        
    }
    
    @Test
    public void testStopInitiator_willThrowIllegalArgumentExceptionForUnknownInitiatorName(){
        final String moduleName = "moduleName";
        final String initiatorName = "unknownInitiator";
        final String actor = "actor";
        
        final Module module = mockery.mock(Module.class);
        
        mockery.checking(new Expectations()
        {
            {
                one(moduleContainer).getModule(moduleName);
                will(returnValue(module));
                one(module).getInitiator(initiatorName);
                will(returnValue(null));
            }
        });
        
        IllegalArgumentException illegalArgumentException = null;
        try{
            moduleServiceImpl.stopInitiator(moduleName, initiatorName, actor);
            Assert.fail("exception should have been thrown for unknown initiatorName");
        } catch(IllegalArgumentException e){
            illegalArgumentException = e;
        }
        Assert.assertNotNull("exception should have been thrown for unknown initiatorName",illegalArgumentException);
        
        mockery.assertIsSatisfied(); 
    }
    
    @Test
    public void testStopInitiator_willLogSystemEventAndStopOnInitiator(){
        final String moduleName = "moduleName";
        final String initiatorName = "initiatorName";
        final String actor = "actor";
        
        final Module module = mockery.mock(Module.class);
        final Initiator initiator = mockery.mock(Initiator.class);
        
        mockery.checking(new Expectations()
        {
            {
                one(moduleContainer).getModule(moduleName);
                will(returnValue(module));
                one(module).getInitiator(initiatorName);
                will(returnValue(initiator));
                
                one(systemEventService).logSystemEvent(moduleName+"."+initiatorName, ModuleServiceImpl.INITIATOR_STOP_REQUEST_SYSTEM_EVENT_ACTION, actor);
                one(initiator).stop();
            }
        });
        
        moduleServiceImpl.stopInitiator(moduleName, initiatorName, actor);

        
        mockery.assertIsSatisfied(); 
    }
    
    @Test
    public void testStartInitiator_willLogSystemEventAndStartInitiator(){
        final String moduleName = "moduleName";
        final String initiatorName = "initiatorName";
        final String actor = "actor";
        
        final Module module = mockery.mock(Module.class);
        final Initiator initiator = mockery.mock(Initiator.class);
        
        final InitiatorStartupControl initiatorStartupControl = mockery.mock(InitiatorStartupControl.class);
        
        mockery.checking(new Expectations()
        {
            {
                one(moduleContainer).getModule(moduleName);
                will(returnValue(module));
                one(module).getInitiator(initiatorName);
                will(returnValue(initiator));
                one(initiatorStartupControlDao).getInitiatorStartupControl(moduleName, initiatorName);will(returnValue(initiatorStartupControl));
                one(initiatorStartupControl).isDisabled();will(returnValue(false));
                one(systemEventService).logSystemEvent(moduleName+"."+initiatorName, ModuleServiceImpl.INITIATOR_START_REQUEST_SYSTEM_EVENT_ACTION, actor);
                one(initiator).start();
            }
        });
        
        moduleServiceImpl.startInitiator(moduleName, initiatorName, actor);

        
        mockery.assertIsSatisfied(); 
    }
    
    
    @Test(expected=IllegalStateException.class)
    public void testStartInitiator_willThrowIllegalStateException(){
        final String moduleName = "moduleName";
        final String initiatorName = "initiatorName";
        final String actor = "actor";
        
        final Module module = mockery.mock(Module.class);
        final Initiator initiator = mockery.mock(Initiator.class);
        
        final InitiatorStartupControl initiatorStartupControl = mockery.mock(InitiatorStartupControl.class);
        
        mockery.checking(new Expectations()
        {
            {
                one(moduleContainer).getModule(moduleName);
                will(returnValue(module));
                one(module).getInitiator(initiatorName);
                will(returnValue(initiator));
                one(initiatorStartupControlDao).getInitiatorStartupControl(moduleName, initiatorName);will(returnValue(initiatorStartupControl));
                one(initiatorStartupControl).isDisabled();will(returnValue(true));

            }
        });
        
        moduleServiceImpl.startInitiator(moduleName, initiatorName, actor);

        
        mockery.assertIsSatisfied(); 
    }
    
    
    
    @Test 
    public void testUpdateInitiatorStartupType(){
        final String moduleName = "moduleName";
        final String initiatorName = "initiatorName";
        final String actor = "actor";

        
        final StartupType startupType = StartupType.DISABLED;
        final String comment = "comment";
        final InitiatorStartupControl initiatorStartupControl = mockery.mock(InitiatorStartupControl.class);
        
        mockery.checking(new Expectations()
        {
            {
                one(systemEventService).logSystemEvent(moduleName+"."+initiatorName, ModuleServiceImpl.INITIATOR_SET_STARTUP_TYPE_EVENT_ACTION+startupType.toString(), actor);
                one(initiatorStartupControlDao).getInitiatorStartupControl(moduleName, initiatorName);will(returnValue(initiatorStartupControl));
                
                one(initiatorStartupControl).setStartupType(startupType);
                one(initiatorStartupControl).setComment(comment);
                one(initiatorStartupControlDao).save(initiatorStartupControl);
            }
        });
        
        moduleServiceImpl.updateInitiatorStartupType(moduleName, initiatorName, startupType, comment, actor);
        mockery.assertIsSatisfied();
    }
    
    
    
    
    
    
}
