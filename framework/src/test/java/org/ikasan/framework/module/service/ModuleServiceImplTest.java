/*
 * $Id: ModuleServiceImplTest.java 16808 2009-04-27 07:28:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/framework/src/test/java/org/ikasan/framework/module/service/ModuleServiceImplTest.java $
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

import org.ikasan.framework.module.Module;
import org.ikasan.framework.module.container.ModuleContainer;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Assert;
import org.junit.Test;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class ModuleServiceImplTest
{
    private Mockery mockery = new Mockery();
    
    ModuleContainer moduleContainer = mockery.mock(ModuleContainer.class);

    
    /**
     * Class under test
     */
    private ModuleServiceImpl moduleServiceImpl = new ModuleServiceImpl(moduleContainer);
    
    
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
}
