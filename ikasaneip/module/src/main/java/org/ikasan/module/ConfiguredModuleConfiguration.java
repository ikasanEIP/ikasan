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

import java.awt.*;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuration for a module where flows are configured at runtime.
 *
 * @author Ikasan Development Team
 */
public class ConfiguredModuleConfiguration implements Serializable
{
    protected Map<String,String> flowDefinitions = new HashMap();
    protected Map<String,String> flowDefinitionProfiles = new HashMap();

    /**
     * Retrieves the flow definitions associated with this module configuration.
     *
     * @return a map containing the flow definitions where the key is the flow name
     *         and the value is the startup type of that flow
     */
    public Map<String, String> getFlowDefinitions()
    {
        return flowDefinitions;
    }

    /**
     * Sets the flow definitions for the module configuration.
     *
     * @param flowDefinitions a map containing the flow definitions where the key is the flow name
     *                        and the value is the startup type of that flow
     */
    public void setFlowDefinitions(Map<String,String> flowDefinitions)
    {
        this.flowDefinitions = flowDefinitions;
    }

    /**
     * Returns the map containing the flow definition profiles for this module configuration.
     *
     * @return a map where the key is the flow name and the value is the profile for that particular flow
     */
    public Map<String, String> getFlowDefinitionProfiles() {
        return flowDefinitionProfiles;
    }

    /**
     * Set the flow definition profiles for the module configuration.
     *
     * @param flowDefinitionProfiles a map containing the flow definition profiles where the key is the flow name
     *                               and the value is the profile for that particular flow
     */
    public void setFlowDefinitionProfiles(Map<String, String> flowDefinitionProfiles) {
        this.flowDefinitionProfiles = flowDefinitionProfiles;
    }
}
