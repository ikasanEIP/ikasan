 /* 
 * $Id: ModuleContainer.java 16808 2009-04-27 07:28:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/framework/src/main/java/org/ikasan/framework/module/container/ModuleContainer.java $
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

import java.util.List;

import org.ikasan.framework.module.Module;


/**
 * Container class for holding references to all available <code>Module</code>s
 * 
 * @author Ikasan Development Team
 */
public interface ModuleContainer
{
    
    /**
     * Returns the module named by moduleName or null if it does not exist
     * 
     * @param moduleName - The name of the module to get
     * @return Module or null
     */
    public Module getModule(String moduleName);
    
    /**
     * Exposes all the loaded <code>Module</code>s
     * 
     * @return List of all loaded <code>Module</code>s 
     */
    public List<Module> getModules();

    
    /**
     * Adds a new <code>Module</code> to the container
     * 
     * @param module
     */
    public void add(Module module);
}
