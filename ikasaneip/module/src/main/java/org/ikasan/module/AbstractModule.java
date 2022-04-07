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

import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.ikasan.spec.module.ModuleType;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple representation of a Module
 * 
 * @author Ikasan Development Team
 */
public abstract class AbstractModule implements Module
{
    // default to integration module
    private ModuleType moduleType = ModuleType.INTEGRATION_MODULE;

    /** The url of the module */
    private String url;

    /** The host of the module */
    private String host;

    /** The port number of the module */
    private Integer port;

    /** The root context of the module */
    private String context;

    /** The protocol context of the module */
    private String protocol;

    /** Module name */
    protected String name;

    /** Module version */
    protected String version;

    /** Human readable description of this module */
    private String description;

    /** Flows within this module */
    private List<Flow> flows = new ArrayList();

    /**
     * Constructor
     *
     * @param name The name of the module
     * @param version version of the module
     */
    public AbstractModule(String name, String version, String url)
    {
        this(name, version);
        this.url = url;
    }

    /**
     * Constructor
     *
     * @param name Name of the module
     */
    public AbstractModule(String name)
    {
        this.name = name;
        if(name == null)
        {
            throw new IllegalArgumentException("name cannot be 'null'");
        }
    }

    /**
     * Constructor
     *
     * @param name Name of the module
     * @param version version of the module
     */
    public AbstractModule(String name, String version)
    {
        this.name = name;
        if(name == null)
        {
            throw new IllegalArgumentException("name cannot be 'null'");
        }

        this.version = version;
    }

    /**
     * Constructor
     *
     * @param name The name of the module
     * @param version version of the module
     */
    public AbstractModule(String name, List<Flow> flows, String version, String url)
    {
        this(name, flows, version);
        this.url = url;
    }

    /**
     * Constructor
     *
     * @param name Name of the module
     */
    public AbstractModule(String name, List<Flow> flows)
    {
        this.name = name;
        if(name == null)
        {
            throw new IllegalArgumentException("name cannot be 'null'");
        }

        this.flows = flows;
        if(flows == null)
        {
            throw new IllegalArgumentException("flows cannot be 'null'");
        }
    }

    /**
     * Constructor
     *
     * @param name Name of the module
     * @param version version of the module
     */
    public AbstractModule(String name, List<Flow> flows, String version)
    {
        this.name = name;
        if(name == null)
        {
            throw new IllegalArgumentException("name cannot be 'null'");
        }

        this.flows = flows;
        if(flows == null)
        {
            throw new IllegalArgumentException("flows cannot be 'null'");
        }

        this.version = version;
    }

    @Override
    public void setType(ModuleType moduleType) {
        this.moduleType = moduleType;
    }

    @Override
    public ModuleType getType() {
        return this.moduleType;
    }

    /**
     * Accessor for name
     * 
     * @return module name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @return the flows
     */
    public  List<Flow> getFlows()
    {
        return this.flows;
    }

    /**
     * @return the flow matching the given name
     */
    public Flow getFlow(String name)
    {
        for(Flow flow:this.flows)
        {
            if(flow.getName().equals(name))
            {
                return flow;
            }
        }

        return null;
    }

    /**
     * @see Module#getDescription()
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Set the description. 
     * 
     * @param description - description to set
     */
    public void setDescription(String description)
    {
        this.description = description;
    }

    /**
     * Get the runtime version of this module
     * @return version
     */
    public String getVersion()
    {
        return this.version;
    }

    @Override
    public String getUrl()
    {
        return url;
    }

    @Override
    public void setUrl(String url)
    {
        this.url = url;
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public void setHost(String host) {
        this.host = host;
    }

    @Override
    public Integer getPort() {
        return port;
    }

    @Override
    public void setPort(Integer port) {
        this.port = port;
    }

    @Override
    public String getContext() {
        return context;
    }

    @Override
    public void setContext(String context) {
        this.context = context;
    }

    @Override
    public String getProtocol() {
        return protocol;
    }

    @Override
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }
}
