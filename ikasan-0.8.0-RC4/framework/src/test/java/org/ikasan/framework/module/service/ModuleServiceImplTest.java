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
