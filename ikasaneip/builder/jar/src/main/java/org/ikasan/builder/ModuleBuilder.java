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

import org.ikasan.module.ConfiguredModuleConfiguration;
import org.ikasan.module.ConfiguredModuleImpl;
import org.ikasan.module.SimpleModule;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.event.EventFactory;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.flow.FlowFactory;
import org.ikasan.spec.module.Module;
import org.ikasan.spec.module.ModuleType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple Module builder.
 * 
 * @author Ikasan Development Team
 */
public class ModuleBuilder
{

    public static final String SERVER_PORT = "server.port";
    public static final String SERVER_ADDRESS = "server.address";
    public static final String SERVER_PROTOCOL = "server.protocol";
    public static final String PUBLIC_SERVICE_PORT = "public.service.port";
    public static final String PUBLIC_SERVICE_ADDRESS = "public.service.address";
    public static final String PUBLIC_SERVICE_PROTOCOL = "public.service.protocol";

    public static final String DEFAULT_PROTOCOL = "http";

    public static final String DEFAULT_HOST = "localhost";

    private static final Logger logger = LoggerFactory.getLogger(ModuleBuilder.class);

    /** The type of module **/
    ModuleType moduleType;

    /** name of the module being instantiated */
	String name;

    /** module version */
    String version;

    /** optional module description */
	String description = "Unspecified";

	/** flow builders for creating flows within this module */
	List<Flow> flows = new ArrayList<Flow>();

	/** application context */
	ApplicationContext context;

    EventFactory eventFactory;

    /** allow registration of a flow factory for dynamically instantiated flows */
    FlowFactory flowFactory;

    /** configuration */
    ConfiguredModuleConfiguration configuration;

    /**
	 * Constructor
	 * @param name
	 */
	ModuleBuilder(ApplicationContext context, String name, EventFactory eventFactory)
	{
		this.context = context;
		if(context == null)
		{
			throw new IllegalArgumentException("context cannot be 'null'");
		}

        this.name = name;
        if(name == null)
        {
            throw new IllegalArgumentException("module name cannot be 'null'");
        }

        this.eventFactory = eventFactory;
        if(eventFactory == null)
        {
            throw new IllegalArgumentException("eventFactory name cannot be 'null'");
        }
	}

	/**
	 * Constructor
	 * @param name
	 */
	ModuleBuilder(String name)
	{

		this.name = name;
		if(name == null)
		{
			throw new IllegalArgumentException("module name cannot be 'null'");
		}
	}

    /**
     * Add the module type.
     *
     * @param type
     * @return
     */
    public ModuleBuilder withType(ModuleType type)
    {
        this.moduleType = type;
        return this;
    }

    /**
	 * Add description to the module
	 * @param description
	 * @return
	 */
	public ModuleBuilder withDescription(String description)
	{
		this.description = description;
		return this;
	}

	public ModuleBuilder withFlowFactory(FlowFactory flowFactory)
    {
        this.flowFactory = flowFactory;
        return this;
    }

    public ModuleBuilder setConfiguration(ConfiguredModuleConfiguration configuration)
    {
        this.configuration = configuration;
        return this;
    }

	/**
	 * Add description to the module
	 * @param version
	 * @return
	 */
	public ModuleBuilder withVersion(String version)
	{
		this.version = version;
		return this;
	}

	/**
	 * Add a flow to the module
	 * @param flow
	 * @return
	 */
	public ModuleBuilder addFlow(Flow flow)
	{
		this.flows.add(flow);
		return this;
	}
	
	public Module build()
	{
        Module module;
	    if(flowFactory != null)
        {
            module = new ConfiguredModuleImpl(this.name, this.version, this.flows, this.flowFactory, getUrl());
            if(module instanceof ConfiguredResource resource)
            {
                resource.setConfiguredResourceId(this.name);
                if(configuration != null)
                {
                    resource.setConfiguration(configuration);
                }
                else
                {
                    resource.setConfiguration( new ConfiguredModuleConfiguration() );
                }
            }
        }
	    else
        {
            module = new SimpleModule(this.name, this.version, this.flows, getUrl());
        }

        module.setDescription(this.description);
	    module.setHost(this.getHost());
	    module.setContext(this.context.getApplicationName());
	    module.setPort(this.getPort());
	    module.setProtocol(this.getProtocol());

	    if(this.moduleType != null) {
	        module.setType(this.moduleType);
        }

        return module;
	}

	public FlowBuilder getFlowBuilder(String flowName)
	{
		AutowireCapableBeanFactory beanFactory = this.context.getAutowireCapableBeanFactory();
		FlowBuilder flowBuilder = new FlowBuilder(flowName, this.name, eventFactory);
		beanFactory.autowireBean(flowBuilder);
		flowBuilder.setApplicationContext(this.context);
		return flowBuilder;
	}


    /**
     * Gets application url
     *
     * @return application url
     */
    private String getUrl()
    {
        String host = getHost();
        Integer port = getPort();
        String pid = getPid();
        String protocol = getProtocol();
        String context = this.context.getApplicationName();
        String serverUrl = protocol + "://" + host + ":" + port + context;
        logger.info("Module url [" + serverUrl + "] running with PID [" + pid + "]");

        return serverUrl;

    }

    private Integer getPort()
    {
        try
        {
            String port = context.getEnvironment().getProperty(PUBLIC_SERVICE_PORT);
            if (port != null)
            {
                return Integer.valueOf(port);
            }
            port = context.getEnvironment().getProperty(SERVER_PORT);
            if (port != null)
            {
                return Integer.valueOf(port);
            }
            return 8080;
        }
        catch (Throwable ex)
        {
            return 8080;
        }
    }

    private String getHost()
    {
        try
        {

            String host = context.getEnvironment().getProperty(PUBLIC_SERVICE_ADDRESS);
            if (host != null)
            {
                return host;
            }
            host = context.getEnvironment().getProperty(SERVER_ADDRESS);
            if (host != null)
            {
                return host;
            }

            return DEFAULT_HOST;
        }
        catch (Throwable ex)
        {
            return DEFAULT_HOST;
        }
    }

    private String getProtocol()
    {
        try
        {

            String protocol = context.getEnvironment().getProperty(PUBLIC_SERVICE_PROTOCOL);
            if (protocol != null)
            {
                return protocol;
            }
            protocol = context.getEnvironment().getProperty(SERVER_PROTOCOL);
            if (protocol != null)
            {
                return protocol;
            }
            else
            {
                return DEFAULT_PROTOCOL;
            }
        }
        catch (Throwable ex)
        {
            return DEFAULT_PROTOCOL;
        }
    }

    private static String getPid()
    {
        try
        {
            String jvmName = ManagementFactory.getRuntimeMXBean().getName();
            return jvmName.split("@")[0];
        }
        catch (Throwable ex)
        {
            return null;
        }
    }
}

