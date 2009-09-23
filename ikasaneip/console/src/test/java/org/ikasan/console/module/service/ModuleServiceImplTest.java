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

import java.util.List;
import java.util.Properties;

import org.ikasan.console.module.service.ModuleServiceImpl;
import org.ikasan.framework.module.Module;
import org.junit.Test;
import org.junit.Assert;

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
