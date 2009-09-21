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

import org.hamcrest.Description;
import org.ikasan.framework.flow.initiator.dao.InitiatorCommandDao;
import org.ikasan.framework.initiator.Initiator;
import org.ikasan.framework.initiator.InitiatorCommand;
import org.ikasan.framework.module.Module;
import org.ikasan.framework.module.container.ModuleContainer;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Assert;
import org.junit.Test;
import org.junit.matchers.TypeSafeMatcher;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class ModuleServiceImplTest
{
    private Mockery mockery = new Mockery();
    
    ModuleContainer moduleContainer = mockery.mock(ModuleContainer.class);
    
    InitiatorCommandDao initiatorCommandDao = mockery.mock(InitiatorCommandDao.class);

    
    /**
     * Class under test
     */
    private ModuleServiceImpl moduleServiceImpl = new ModuleServiceImpl(moduleContainer,initiatorCommandDao);
    
    
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
    public void testStopInitiator_willPersistAppropriateInitiatorCommandAndCallStopOnInitiator(){
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
                one(initiatorCommandDao).save(with(new InitiatorCommandMatcher(moduleName, initiatorName, "stop", actor)),with(equal(false)));
                one(initiator).stop();
            }
        });
        
        moduleServiceImpl.stopInitiator(moduleName, initiatorName, actor);

        
        mockery.assertIsSatisfied(); 
    }
    
    @Test
    public void testStartInitiator_willPersistAppropriateInitiatorCommandAndCallStartOnInitiator(){
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
                one(initiatorCommandDao).save(with(new InitiatorCommandMatcher(moduleName, initiatorName, "start", actor)),with(equal(false)));
                one(initiator).start();
            }
        });
        
        moduleServiceImpl.startInitiator(moduleName, initiatorName, actor);

        
        mockery.assertIsSatisfied(); 
    }
    
    public class InitiatorCommandMatcher extends TypeSafeMatcher<InitiatorCommand> {
        
        private String moduleName;
        
        public InitiatorCommandMatcher(String moduleName, String initiatorName, String action, String actor)
        {
            super();
            this.moduleName = moduleName;
            this.initiatorName = initiatorName;
            this.action = action;
            this.actor = actor;
        }

        private String initiatorName;
        
        private String action;

        private String actor;
        @Override
        public boolean matchesSafely(InitiatorCommand initiatorCommand)
        {
            if (!this.moduleName.equals(initiatorCommand.getModuleName())){
                return false;
            }
            if (!this.initiatorName.equals(initiatorCommand.getInitiatorName())){
                return false;
            }            
            if (!this.action.equals(initiatorCommand.getAction())){
                return false;
            }   
            if (!this.actor.equals(initiatorCommand.getActor())){
                return false;
            }   
            return true;
        }

        public void describeTo(Description arg0)
        {
            
        }
        
    }
    
    
}
