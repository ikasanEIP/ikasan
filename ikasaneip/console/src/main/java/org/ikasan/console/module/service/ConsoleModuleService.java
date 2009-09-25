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

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.ikasan.framework.module.Module;
import org.ikasan.framework.module.SimpleModule;
import org.ikasan.framework.module.service.ModuleService;

/**
 * Console implementation of <code>ModuleService</code>
 * 
 * @author Ikasan Development Team
 */
public class ConsoleModuleService implements ModuleService
{

    /** Constructor */
    public ConsoleModuleService()
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
    
}
