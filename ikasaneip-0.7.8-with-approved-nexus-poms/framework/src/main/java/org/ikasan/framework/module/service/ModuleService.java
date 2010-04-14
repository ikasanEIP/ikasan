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

import java.util.List;

import org.ikasan.framework.initiator.InitiatorStartupControl;
import org.ikasan.framework.initiator.InitiatorStartupControl.StartupType;
import org.ikasan.framework.module.Module;

/**
 * Service Tier interface for providing user access to modules 
 * 
 * @author Ikasan Development Team
 *
 */
public interface ModuleService
{
	
    /**
     * Returns all available <code>Module</code>s
     * 
     * @return List of all accessible <code>Module</code>s
     */
    public List<Module> getModules();

    /**
     * Resolves a specified <code>Module</code> by name
     * 
     * @param moduleName
     * 
     * @return <code>Module</code> named by moduleName
     */
    public Module getModule(String moduleName);   
    
    /**
     * Attempts to stop an <code>Initiator</code>
     * 
     * @param moduleName
     * @param initiatorName
     * @param actor
     */
    public void stopInitiator(String moduleName, String initiatorName, String actor);
    
    /**
     * Attempts to start an <code>Initiator</code>
     * 
     * @param moduleName
     * @param initiatorName
     * @param actor
     */
    public void startInitiator(String moduleName, String initiatorName, String actor);
        
    /**
     * Updates the startup type for the <code>Initiator</code>
     * 
     * @param moduleName
     * @param initiatorName
     * @param startupType
     * @param comment
     * @param actor
     */
    public void updateInitiatorStartupType(String moduleName, String initiatorName, StartupType startupType, String comment, String actor);

	/**
	 * Allows access to the <code>InitiatorStartupControl</code> object for the specified <code>Initiator</code>
	 * 
	 * @param moduleName
	 * @param initiatorName
	 * @return <code>InitiatorStartupControl</code> object for the specified <code>Initiator</code>
	 */
	public InitiatorStartupControl getInitiatorStartupControl(String moduleName, String initiatorName);

}
