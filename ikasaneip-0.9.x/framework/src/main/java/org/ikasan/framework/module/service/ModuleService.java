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

import java.util.List;

import org.ikasan.spec.module.Module;

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
     * @param moduleName the module name
     * 
     * @return <code>Module</code> named by moduleName
     */
    public Module getModule(String moduleName);

    /**
     * Attempts to stop a <code>Flow</code>
     * 
     * @param moduleName 
     * @param flowName 
     * @param actor 
     * 
     */
    public void stopFlow(final String moduleName, final String flowName, final String actor);

    /**
     * Attempts to start an <code>Initiator</code>
     * 
     * @param moduleName
     * @param flowName 
     * @param actor
     */
    public void startFlow(String moduleName, String flowName, String actor);
        
//    /**
//     * Updates the startup type for the <code>Initiator</code>
//     * 
//     * @param moduleName
//     * @param initiatorName
//     * @param startupType
//     * @param comment
//     * @param actor
//     */
//    public void updateInitiatorStartupType(String moduleName, String initiatorName, StartupType startupType, String comment, String actor);

//	/**
//	 * Allows access to the <code>InitiatorStartupControl</code> object for the specified <code>Initiator</code>
//	 * 
//	 * @param moduleName
//	 * @param initiatorName
//	 * @return <code>InitiatorStartupControl</code> object for the specified <code>Initiator</code>
//	 */
//	public InitiatorStartupControl getInitiatorStartupControl(String moduleName, String initiatorName);

}
