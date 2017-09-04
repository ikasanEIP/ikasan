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

import org.ikasan.module.SimpleModule;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple Module builder.
 * 
 * @author Ikasan Development Team
 */
public class ModuleBuilder
{
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

	/**
	 * Constructor
	 * @param name
	 */
	ModuleBuilder(ApplicationContext context, String name)
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
	 * Add description to the module
	 * @param description
	 * @return
	 */
	public ModuleBuilder withDescription(String description)
	{
		this.description = description;
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
		Module module = new SimpleModule(this.name, this.version, this.flows);
		module.setDescription(this.description);
		return module;
	}

	public FlowBuilder getFlowBuilder(String flowName)
	{
		AutowireCapableBeanFactory beanFactory = this.context.getAutowireCapableBeanFactory();
		FlowBuilder flowBuilder = new FlowBuilder(flowName, this.name);
		beanFactory.autowireBean(flowBuilder);
		flowBuilder.setApplicationContext(this.context);
		return flowBuilder;
	}
}

