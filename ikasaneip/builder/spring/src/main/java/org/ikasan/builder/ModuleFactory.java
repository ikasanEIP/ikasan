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

import java.util.List;

import org.ikasan.flow.configuration.FlowComponentInvokerConfiguration;
import org.ikasan.flow.configuration.FlowComponentInvokerSetupServiceConfiguration;
import org.ikasan.flow.visitorPattern.invoker.InvokerConfiguration;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.annotation.PostConstruct;

/**
 * Spring based FactoryBean for the creation of Ikasan Modules.
 * @author Ikasan Development Team
 * 
 */
public class ModuleFactory implements FactoryBean<Module>, ApplicationContextAware
{
    /** module name */
    String name;

    /** module version */
    String version;

    /** module descriptive purpose */
    String description;

    /** module's flows */
    List<Flow> flows;

    /** the flow configuration map to allow for externalised configurations **/
    FlowComponentInvokerSetupServiceConfiguration flowComponentInvokerSetupServiceConfiguration;

    /**
     * Setter for name
     * @param name
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Setter for version
     * @param version
     */
    public void setVersion(String version)
    {
        this.version = version;
    }

    /**
     * Setter for description
     * @param description
     */
    public void setDescription(String description)
    {
        this.description = description;
    }

    /**
     * Setter for flows.
     * @param flows
     */
    public void setFlows(List<Flow> flows)
    {
        this.flows = flows;
    }



    /*
     * (non-Javadoc)
     * @see org.springframework.beans.factory.FactoryBean#getObject()
     */
    public Module getObject()
    {
        Module module = new org.ikasan.module.SimpleModule(name, version, flows);
        module.setDescription(description);
        return module;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.beans.factory.FactoryBean#getObjectType()
     */
    public Class<Module> getObjectType()
    {
        return Module.class;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.beans.factory.FactoryBean#isSingleton()
     */
    public boolean isSingleton()
    {
        return false;
    }

    @PostConstruct
    private void initialFlowComponentInvokerConfigurations() {
        if(this.flows != null) {
            flows.forEach(flow -> {
                flow.getFlowElements().forEach(flowElement -> {
                    this.applyFlowElementInvokerConfiguration(flow.getName()
                        , flowElement.getComponentName(), (InvokerConfiguration) flowElement.getFlowElementInvoker().getConfiguration());
                });
            });
        }
    }

    /**
     * Apply the configuration from a FlowComponentInvokerConfiguration to an InvokerConfiguration.
     *
     * @param flowName the name of the flow
     * @param flowElementName the name of the flow element
     * @param invokerConfiguration the InvokerConfiguration to be configured
     */
    private void applyFlowElementInvokerConfiguration(String flowName, String flowElementName, InvokerConfiguration invokerConfiguration) {
        FlowComponentInvokerConfiguration flowComponentInvokerConfiguration
            = this.flowComponentInvokerSetupServiceConfiguration.getConfiguration(flowName, flowElementName);

        if(flowComponentInvokerConfiguration != null && invokerConfiguration != null) {
            invokerConfiguration.setSnapEvent(flowComponentInvokerConfiguration.isSnapEvent());
            invokerConfiguration.setCaptureMetrics(flowComponentInvokerConfiguration.isCaptureMetrics());
            invokerConfiguration.setDynamicConfiguration(flowComponentInvokerConfiguration.isDynamicConfiguration());
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.flowComponentInvokerSetupServiceConfiguration = applicationContext.getBean("flowComponentInvokerConfigurations"
            , FlowComponentInvokerSetupServiceConfiguration.class);
    }
}