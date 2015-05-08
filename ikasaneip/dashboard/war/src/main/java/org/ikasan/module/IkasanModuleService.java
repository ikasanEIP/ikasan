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
package org.ikasan.module;

import java.util.List;

import org.ikasan.module.model.Module;


/**
 * @author Ikasan Development Team
 *
 */
public interface IkasanModuleService
{
    /**
     * This method is responsible for returning a list of initiator names for 
     * a given URL and module name.
     * @param targetUrl
     * @param moduleName
     * @return
     */
    public List<String> getAllInitiators(String targetUrl, String moduleName);

    /**
     * This method is responsible for returning a list of flow names for 
     * a given URL and module name.
     * 
     * @param targetUrl
     * @param moduleName
     * @return
     */
    public List<String> getAllFlows(String targetUrl, String moduleName);

    /**
     * This method is responsible for returning a list of component names for 
     * a given URL, module name and flow name.
     * 
     * @param targetUrl
     * @param moduleName
     * @param flowName
     * @return
     */
    public List<String> getAllComponents(String targetUrl, String moduleName, String flowName);

    /**
     * This method is responsible for starting an initiator based on URL, module and
     * initiator name.
     * 
     * @param targetUrl
     * @param moduleName
     * @param initiatorName
     */
    public void startInitiator(String targetUrl, String moduleName, String initiatorName);

    /**
     * This method is responsible for stopping an initiator based on URL, module and
     * initiator name.
     * 
     * @param targetUrl
     * @param moduleName
     * @param initiatorName
     */
    public void stopInitiator(String targetUrl, String moduleName, String initiatorName);

    /**
     * This method is responsible for returning the statistics of an initiator based on URL, module and
     * initiator name.
     *  
     * @param targetUrl
     * @param moduleName
     * @param initiatorName
     * @return
     */
    public String getInitiatorStatus(String targetUrl, String moduleName, String initiatorName);

    /**
     * This method is responsible for resolving all Modules from around the estate.
     * 
     * @param targetUrl
     * @param moduleName
     * @return
     */
    public List<Module> getResolvedModules();
}
