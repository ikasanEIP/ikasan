 /* 
 * $Id: ModuleContainerImpl.java 16808 2009-04-27 07:28:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/framework/src/main/java/org/ikasan/framework/module/container/ModuleContainerImpl.java $
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
package org.ikasan.framework.module.container;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.ikasan.framework.module.Module;

/**
 * Default implementation for Module Container 
 * 
 * @author Ikasan Development Team
 */
public class ModuleContainerImpl implements   ModuleContainer
{

    /** Map of all loaded modules */
    protected Map<String, Module> modules = new LinkedHashMap<String, Module>();



    /**
     * Exposes all the loaded <code>Module</code>s
     * 
     * @return List of all loaded <code>Module</code>s
     */
    public List<Module> getModules()
    {
        return new ArrayList<Module>(modules.values());
    }

    /**
     * Returns a module by name
     * 
     * @param moduleName - The name of the module to get
     * @return Module
     */
    public Module getModule(String moduleName)
    {
        return modules.get(moduleName);
    }

    public void add(Module module)
    {
        modules.put(module.getName(), module);
        
    }
}
