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
package org.ikasan.builder;

import java.util.Map;

public abstract class AbstractIkasanApplication
{
    /** Keep a handle on any named module builders associated with this application */
    protected Map<String, ModuleBuilder> moduleBuilders;

    public AbstractIkasanApplication(Map<String,ModuleBuilder> moduleBuilders)
    {
        this.moduleBuilders = moduleBuilders;
        if(moduleBuilders == null)
        {
            throw new IllegalArgumentException("moduleBuilders cannot be 'null'");
        }
    }

    /**
     * Get the instance of the module builder for the given module name.
     * Create one if it doesnt exist, otherwise return the exiting one.
     *
     * @param name
     * @return
     */
    public ModuleBuilder getModuleBuilder(String name)
    {
        if(this.moduleBuilders.containsKey(name))
        {
            return this.moduleBuilders.get(name);
        }

        ModuleBuilder moduleBuilder = createModuleBuilder(name);
        this.moduleBuilders.put(name, moduleBuilder);
        return moduleBuilder;
    }

    /**
     * Get an instance of the flow builder for the given module name and flow name.
     * @param moduleName
     * @param flowName
     * @return
     */
    public FlowBuilder getFlowBuilder(String moduleName, String flowName)
    {
        return getModuleBuilder(moduleName).getFlowBuilder(flowName);
    }

    protected abstract ModuleBuilder createModuleBuilder(String name);
}