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
package org.ikasan.console.module.service;

import java.util.List;
import java.util.Properties;

import org.ikasan.framework.module.Module;
import org.junit.Assert;
import org.junit.Test;

/**
 * JUnit based test class for testing WiretapSearchCriteria
 * 
 * @author Ikasan Development Team
 */
public class ModuleServiceImplTest
{
    /** List of module names and descriptions */ 
    private Properties modulesList = new Properties();
    
    /** The ModuleImplService we want to test */
    private ModuleServiceImpl moduleImplService = new ModuleServiceImpl();
    
    /** Test that a 'successful' call of getModules() returns a not null list of 1 */
    @Test
    public void testGetModules()
    {
        // Seed the Module Service
        modulesList.put("moduleName", "moduleValue");
        this.moduleImplService.setModulesList(modulesList);
        // Get the modules
        List<Module> modules = this.moduleImplService.getModules();
        // Make sure there is one valid Module in the list
        Assert.assertNotNull(modules);
        Assert.assertEquals(1, modules.size());
        // Check the key and value of that single module
        Module module = modules.get(0);
        Assert.assertEquals("moduleName", module.getName());
        Assert.assertEquals("moduleValue", module.getDescription());
    }

    /** Test that the getModules() call returns null if there are no modules */
    @Test
    public void testGetNullModules()
    {
        List<Module> modules = this.moduleImplService.getModules();
        Assert.assertNull(modules);
    }
    
    /** 
     * Test that the getModule(String moduleName) call always returns null
     */
    @Test
    public void testGetModule()
    {
        Module module = this.moduleImplService.getModule("moduleName");
        Assert.assertNull(module);
        module = this.moduleImplService.getModule("");
        Assert.assertNull(module);
        module = this.moduleImplService.getModule(null);
        Assert.assertNull(module);
    }

    /** 
     * Test that the stopInitiator(String moduleName, String initiatorName, String actor) 
     * call does nothing.
     */
    @Test
    public void testStopInitiator()
    {
        this.moduleImplService.stopInitiator("moduleName", "initiatorName", "actor");
    }
    
    /** 
     * Test that the startInitiator(String moduleName, String initiatorName, String actor) 
     * call does nothing.
     */
    @Test
    public void testStartInitiator()
    {
        this.moduleImplService.startInitiator("moduleName", "initiatorName", "actor");
    }

}
