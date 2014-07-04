/* 
 * $Id: ModuleService.java 3676 2011-04-28 12:27:38Z mitcje $
 * $URL: https://open.jira.com/svn/IKASAN/branches/ikasaneip-0.9.x/framework/src/main/java/org/ikasan/framework/module/service/ModuleService.java $
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
package org.ikasan.spec.module;

import java.util.List;

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
     * Attempts to start a <code>Flow</code>
     * 
     * @param moduleName
     * @param flowName 
     * @param actor
     */
    public void startFlow(String moduleName, String flowName, String actor);

    /**
     * Attempts to pause a <code>Flow</code>
     *
     * @param moduleName
     * @param flowName
     * @param actor
     */
    public void pauseFlow(String moduleName, String flowName, String actor);

    /**
     * Attempts to resume a <code>Flow</code>
     *
     * @param moduleName
     * @param flowName
     * @param actor
     */
    public void resumeFlow(String moduleName, String flowName, String actor);

    /**
     * Set the startup type for the given module and flow
     * 
     * @param moduleName
     * @param flowName
     * @param startupType 
     * @param comment
     * @param actor
     */
    public void setStartupType(String moduleName, String flowName, StartupType startupType, String comment, String actor);

    /**
     * Get the startup control for the given module and flow
     * 
     * @param moduleName
     * @param flowName 
     */
    public StartupControl getStartupControl(String moduleName, String flowName);
}
