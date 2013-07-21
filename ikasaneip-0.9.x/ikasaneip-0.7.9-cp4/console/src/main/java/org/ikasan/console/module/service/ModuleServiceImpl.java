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

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.ikasan.framework.initiator.InitiatorStartupControl;
import org.ikasan.framework.initiator.InitiatorStartupControl.StartupType;
import org.ikasan.framework.module.Module;
import org.ikasan.framework.module.SimpleModule;
import org.ikasan.framework.module.service.ModuleService;

/**
 * Console implementation of <code>ModuleService</code>
 * 
 * @author Ikasan Development Team
 */
public class ModuleServiceImpl implements ModuleService
{

    /** List of modules names and their descriptions */
    Properties modulesList;
    
    /** Constructor */
    public ModuleServiceImpl()
    {
        // Do Nothing
    }

    /**
     * Get a list of modules, in this case return null
     * 
     * @see org.ikasan.framework.module.service.ModuleService#getModules()
     */
    public List<Module> getModules()
    {
        List<Module> modules = null;
        SimpleModule module = null;
        String moduleName = null; 

        if (this.modulesList != null)
        {
            modules = new ArrayList<Module>();
            for (Object key:this.modulesList.keySet())
            {
                moduleName = (String)key;
                module = new SimpleModule(moduleName);
                module.setDescription(modulesList.getProperty(moduleName));
                modules.add(module);
            }
        }
        return modules;
    }

    /**
     * Get the module given a module name, in this case return null
     * 
     * @see org.ikasan.framework.module.service.ModuleService#getModule(java.lang
     *      .String) Suppress warning because we are deliberately not using the
     *      parameter
     */
    public Module getModule(@SuppressWarnings("unused") String moduleName)
    {
        return null;
    }

    /**
     * Stop the initiator
     * 
     * @see org.ikasan.framework.module.service.ModuleService#stopInitiator(java.
     *      lang.String, java.lang.String, java.lang.String) Suppress warning
     *      because we are deliberately not using the parameters
     */
    public void stopInitiator(@SuppressWarnings("unused") String moduleName, @SuppressWarnings("unused") String initiatorName,
            @SuppressWarnings("unused") String actor)
    {
        // Do Nothing
    }

    /**
     * Start the initiator
     * 
     * @see org.ikasan.framework.module.service.ModuleService#startInitiator(java
     *      .lang.String, java.lang.String, java.lang.String) Suppress warning
     *      because we are deliberately not using the parameters
     */
    public void startInitiator(@SuppressWarnings("unused") String moduleName, @SuppressWarnings("unused") String initiatorName,
            @SuppressWarnings("unused") String actor)
    {
        // Do Nothing
    }
    
    /**
     * Set the modules list
     * 
     * @param modulesList - List of module names and their descriptions
     */
    public void setModulesList(Properties modulesList)
    {
        this.modulesList = modulesList;
    }

	public void updateInitiatorStartupType(String moduleName,
			String initiatorName, StartupType startupType, String comment,
			String actor) {
		// Do Nothing
		
	}

	public InitiatorStartupControl getInitiatorStartupControl(
			String moduleName, String initiatorName) {
		//Do Nothing
		return null;
	}
    
}
