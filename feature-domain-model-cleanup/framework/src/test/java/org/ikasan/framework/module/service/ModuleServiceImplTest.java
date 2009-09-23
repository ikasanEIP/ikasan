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
