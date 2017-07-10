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
package org.ikasan.spec.module;

import java.util.List;


/**
 * Container class for holding references to all available <code>Module</code>s
 *
 * @author Ikasan Development Team
 */
public interface ModuleContainer {

    /**
     * Returns the module named by moduleName or null if it does not exist
     *
     * @param moduleName - The name of the module to get
     * @return Module or null
     */
    Module getModule(String moduleName);

    /**
     * Exposes all the loaded <code>Module</code>s
     *
     * @return List of all loaded <code>Module</code>s
     */
    List<Module> getModules();


    /**
     * Adds a new <code>Module</code> to the container
     *
     * @param module a new module
     */
    void add(Module module);

    /**
     * Removes the given module from container.
     *
     * @param moduleName - The name of the module to remove
     */
    void remove(String moduleName);
}
