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

import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.flow.FlowFactory;
import org.ikasan.spec.module.Module;

import java.util.List;

/**
 * A configured resource representation of a Module
 * 
 * @author Ikasan Development Team
 */
public class ConfiguredModuleImpl extends AbstractModule implements Module, FlowFactoryCapable, ConfiguredResource<ConfiguredModuleConfiguration>
{
    /** handle to the flow factory for generation of flow instances */
    FlowFactory flowFactory;

    /** configured resource identifier */
    String configuredResourceId;

    /** configuration instance */
    ConfiguredModuleConfiguration configuration = new ConfiguredModuleConfiguration();

    /**
     * Constructor
     *
     * @param name The name of the module
     * @param flowFactory factory for the generation of flow instances
     */
    public ConfiguredModuleImpl(String name, FlowFactory flowFactory)
    {
        super(name);
        this.flowFactory = flowFactory;
        if(flowFactory == null)
        {
            throw new IllegalArgumentException("flowFactory cannot be 'null'");
        }
    }

    /**
     * Constructor
     *
     * @param name The name of the module
     * @param version version of the module
     * @param flowFactory factory for the generation of flow instances
     */
    public ConfiguredModuleImpl(String name, String version, FlowFactory flowFactory)
    {
        super(name, version);
        this.flowFactory = flowFactory;
        if(flowFactory == null)
        {
            throw new IllegalArgumentException("flowFactory cannot be 'null'");
        }
    }

    /**
     * Constructor
     * @param name
     * @param version
     * @param flows
     * @param flowFactory
     * @param url
     */
    public ConfiguredModuleImpl(String name, String version, List<Flow> flows, FlowFactory flowFactory, String url)
    {
        super(name, flows, version, url);
        this.flowFactory = flowFactory;
        if(flowFactory == null)
        {
            throw new IllegalArgumentException("flowFactory cannot be 'null'");
        }
    }

    @Override
    public String getConfiguredResourceId()
    {
        return this.configuredResourceId;
    }

    @Override
    public void setConfiguredResourceId(String configuredResourceId)
    {
        this.configuredResourceId = configuredResourceId;
    }

    @Override
    public ConfiguredModuleConfiguration getConfiguration()
    {
        return configuration;
    }

    @Override
    public void setConfiguration(ConfiguredModuleConfiguration configuration)
    {
        this.configuration = configuration;
    }

    @Override
    public FlowFactory getFlowFactory()
    {
        return this.flowFactory;
    }
}
