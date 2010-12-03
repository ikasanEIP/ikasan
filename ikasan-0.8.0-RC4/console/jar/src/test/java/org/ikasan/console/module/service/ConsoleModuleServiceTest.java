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
package org.ikasan.console.module.service;

import java.util.LinkedHashSet;
import java.util.Set;

import org.ikasan.console.module.Module;
import org.ikasan.console.module.dao.HibernateModuleDao;
import org.ikasan.console.module.dao.ModuleDao;
import org.ikasan.console.module.service.ConsoleModuleService;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.junit.Assert;

/**
 * JUnit based test class for testing ConsoleModuleService
 * 
 * @author Ikasan Development Team
 */
public class ConsoleModuleServiceTest
{

    /**
     * The context that the tests run in, allows for mocking actual concrete classes
     */
    private Mockery context = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** The mocked DAO used in several tests */
    final ModuleDao moduleDao = context.mock(HibernateModuleDao.class);
    
    /** The Console Service we are using in several tests */
    private final ModuleService consoleModuleService = new ConsoleModuleService(moduleDao);
    
    /** Test that a constructor throws an IllegalArgumentException if we pass it a null DAO */
    @Test(expected=IllegalArgumentException.class)
    public void testConstructorWithNullDao()
    {
        new ConsoleModuleService(null);
    }

    /**
     * Test that calling getAllModules returns an Empty Set if none were found.
     */
    @Test
    public void testGetAllModulesReturnsEmpty()
    {
        // Setup
        final Set<Module> returnedModules = new LinkedHashSet<Module>();
        
        // Expectations
        context.checking(new Expectations()
        {
            {
                one(moduleDao).findAllModules();
                will(returnValue(returnedModules));
            }
        });
        // Test
        Set<Module> modules = consoleModuleService.getAllModules();
        // Verify
        Assert.assertTrue(modules.isEmpty());
        context.assertIsSatisfied();
    }
    
    /**
     * Test that calling getAllModules returns a Set of Modules.
     */
    @Test
    public void testGetAllModules()
    {
        // Setup
        final Set<Module> returnedModules = new LinkedHashSet<Module>();
        final Module module1 = context.mock(Module.class);
        returnedModules.add(module1);
        final Module module2 = context.mock(Module.class);
        returnedModules.add(module2);
        
        // Expectations
        context.checking(new Expectations()
        {
            {
                one(moduleDao).findAllModules();
                will(returnValue(returnedModules));
            }
        });
        // Test
        Set<Module> modules = consoleModuleService.getAllModules();
        // Verify
        Assert.assertEquals(2, modules.size());
        context.assertIsSatisfied();
    }

    /**
     * Test that calling getModule with null Id returns a null Module
     */
    @Test
    public void testGetModuleWithNullId()
    {
        // Setup
        final Long moduleId = null;
        
        // Expectations
        context.checking(new Expectations()
        {
            {
                one(moduleDao).getModule(moduleId);
                will(returnValue(null));
            }
        });
        // Test
        Module module = consoleModuleService.getModule(moduleId);
        // Verify
        Assert.assertNull(module);
        context.assertIsSatisfied();
    }

    /**
     * Test that calling getModule with non existent Id returns a null Module
     */
    @Test
    public void testGetModuleWithIncorrectId()
    {
        // Setup
        final Long moduleId = new Long(-1);
        
        // Expectations
        context.checking(new Expectations()
        {
            {
                one(moduleDao).getModule(moduleId);
                will(returnValue(null));
            }
        });
        // Test
        Module module = consoleModuleService.getModule(moduleId);
        // Verify
        Assert.assertNull(module);
        context.assertIsSatisfied();
    }

    /**
     * Test that calling getModule with non existent Id returns a null Module
     */
    @Test
    public void testGetModule()
    {
        // Setup
        final Long moduleId = new Long(-1);
        final Module module = context.mock(Module.class);
        
        // Expectations
        context.checking(new Expectations()
        {
            {
                one(moduleDao).getModule(moduleId);
                will(returnValue(module));
            }
        });
        // Test & Verify
        Assert.assertNotNull(consoleModuleService.getModule(moduleId));
        context.assertIsSatisfied();
    }
    
    /**
     * Test that calling findModules with null Ids returns an Empty Set of Module names
     */
    @Test
    public void testfindModulesWithNullIds()
    {
        // Setup
        final Set<Long> moduleIds = null;
        
        // Expectations
        context.checking(new Expectations()
        {
            {
                one(moduleDao).findModules(moduleIds);
                will(returnValue(new LinkedHashSet<String>()));
            }
        });
        // Test
        Set<String> moduleNames = consoleModuleService.getModuleNames(moduleIds);
        // Verify
        Assert.assertTrue(moduleNames.isEmpty());
        context.assertIsSatisfied();
    }

    /**
     * Test that calling findModules with an empty Set of Ids returns an Empty Set of Module names
     */
    @Test
    public void testfindModulesWithNoIds()
    {
        // Setup
        final Set<Long> moduleIds = new LinkedHashSet<Long>();
        
        // Expectations
        context.checking(new Expectations()
        {
            {
                one(moduleDao).findModules(moduleIds);
                will(returnValue(new LinkedHashSet<String>()));
            }
        });
        // Test
        Set<String> moduleNames = consoleModuleService.getModuleNames(moduleIds);
        // Verify
        Assert.assertTrue(moduleNames.isEmpty());
        context.assertIsSatisfied();
    }
    
    /**
     * Test that calling findModules with an Id that doesn't exist returns an Empty Set of Module names
     */
    @Test
    public void testfindModulesWithNonExistentId()
    {
        // Setup
        final Set<Long> moduleIds = new LinkedHashSet<Long>();
        moduleIds.add(new Long(-1));
        
        // Expectations
        context.checking(new Expectations()
        {
            {
                one(moduleDao).findModules(moduleIds);
                will(returnValue(new LinkedHashSet<String>()));
            }
        });
        // Test
        Set<String> moduleNames = consoleModuleService.getModuleNames(moduleIds);
        // Verify
        Assert.assertTrue(moduleNames.isEmpty());
        context.assertIsSatisfied();
    }

    /**
     * Test that calling findModules with valid Ids returns a Set of Module names.
     * We prefer mocking the domain objects so that the test is tightly focussed on 
     * the service.
     */
    @Test
    public void testfindModulesWithIds()
    {
        // Setup
        final Set<Long> moduleIds = new LinkedHashSet<Long>();
        moduleIds.add(new Long(1));
        moduleIds.add(new Long(2));
        
        final Set<Module> modules = new LinkedHashSet<Module>();
        final Module module1 = context.mock(Module.class); 
        final Module module2 = context.mock(Module.class);
        modules.add(module1);
        modules.add(module2);
        
        // Expectations
        context.checking(new Expectations()
        {
            {
                one(moduleDao).findModules(moduleIds);
                will(returnValue(modules));
                one(module1).getName();
                will(returnValue("module1"));
                one(module2).getName();
                will(returnValue("module2"));
            }
        });
        // Test
        Set<String> moduleNames = consoleModuleService.getModuleNames(moduleIds);
        // Verify
        Assert.assertEquals(2, moduleNames.size());
        context.assertIsSatisfied();
    }

    /**
     * Test that calling findModules with valid Ids but the same name returns a 
     * reduced Set of Module names
     */
    @Test
    public void testfindModulesWithSameNames()
    {
        // Setup
        final Set<Long> moduleIds = new LinkedHashSet<Long>();
        moduleIds.add(new Long(1));
        moduleIds.add(new Long(1));
        
        final Set<Module> modules = new LinkedHashSet<Module>();
        final Module module1 = context.mock(Module.class); 
        final Module module2 = context.mock(Module.class);
        modules.add(module1);
        modules.add(module2);
        
        // Expectations
        context.checking(new Expectations()
        {
            {
                one(moduleDao).findModules(moduleIds);
                will(returnValue(modules));
                one(module1).getName();
                will(returnValue("module1"));
                one(module2).getName();
                will(returnValue("module1"));
            }
        });
        // Test
        Set<String> moduleNames = consoleModuleService.getModuleNames(moduleIds);
        // Verify
        Assert.assertEquals(1, moduleNames.size());
        context.assertIsSatisfied();
    }
    
}
