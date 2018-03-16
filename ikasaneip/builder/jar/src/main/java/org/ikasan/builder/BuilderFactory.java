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

import org.ikasan.builder.component.ComponentBuilder;
import org.ikasan.configurationService.model.PlatformConfiguration;
import org.ikasan.monitor.MonitorFactory;
import org.ikasan.monitor.notifier.NotifierFactory;
import org.ikasan.spec.configuration.PlatformConfigurationService;
import org.ikasan.spec.flow.FlowElement;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Builder Factory implementation.
 *
 * @author Ikasan Development Team
 */
public class BuilderFactory implements ApplicationContextAware
{
    /** handle to the spring application context */
    ApplicationContext applicationContext;

    Map<String, ModuleBuilder> moduleBuilders = new HashMap<String, ModuleBuilder>();

    /**
     * Get the instance of the module builder for the given module name.
     * Create one if it doesnt exist, otherwise return the exiting one.
     *
     * @return
     */
    public ModuleBuilder getModuleBuilder(String moduleName)
    {
        if(this.moduleBuilders.containsKey(moduleName))
        {
            return this.moduleBuilders.get(moduleName);
        }

        ModuleBuilder moduleBuilder = new ModuleBuilder(this.applicationContext, moduleName);
        this.moduleBuilders.put(moduleName, moduleBuilder);
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

    /**
     * Get an instance of a component builder for the creation of Ikasan components.
     * @return
     */
    public ComponentBuilder getComponentBuilder()
    {
        return new ComponentBuilder(this.applicationContext);
    }

    /**
     * Get an instance of a monitor builder.
     * @return
     */
    public MonitorBuilder getMonitorBuilder()
    {
        return new MonitorBuilder(this.applicationContext.getBean(MonitorFactory.class), this.applicationContext.getBean(NotifierFactory.class),
            this.applicationContext.getBean(PlatformConfigurationService.class));
    }

    /**
     * Get an instance of a route builder for creating nested routes within a flow.
     * @return
     */
    public RouteBuilder getRouteBuilder()
    {
        return new RouteBuilder( new RouteImpl(new ArrayList<FlowElement>()) );
    }

    /**
     * Get an instance of an exception resolver builder.
     * @return
     */
    public ExceptionResolverBuilderImpl getExceptionResolverBuilder()
    {
        return new ExceptionResolverBuilderImpl();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
