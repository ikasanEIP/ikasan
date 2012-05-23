/* 
 * $Id: InitiatorStartupControl.java 2821 2010-02-12 14:01:56Z magicduncan $
 * $URL: https://open.jira.com/svn/IKASAN/branches/ikasaneip-0.9.x/framework/src/main/java/org/ikasan/framework/initiator/InitiatorStartupControl.java $
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
package org.ikasan.flow;

import java.util.HashMap;
import java.util.Map;

import org.ikasan.flow.configuration.service.ConfiguredResourceConfigurationService;
import org.ikasan.flow.event.DefaultReplicationFactory;
import org.ikasan.flow.visitorPattern.DefaultFlowConfiguration;
import org.ikasan.flow.visitorPattern.FlowConfiguration;
import org.ikasan.flow.visitorPattern.FlowElementImpl;
import org.ikasan.flow.visitorPattern.VisitingFlowElementInvoker;
import org.ikasan.flow.visitorPattern.VisitingInvokerFlow;
import org.ikasan.recovery.RecoveryManagerFactory;
import org.ikasan.scheduler.CachingScheduledJobFactory;
import org.ikasan.scheduler.SchedulerFactory;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.component.transformation.Translator;
import org.ikasan.spec.configuration.Configuration;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.flow.FlowElement;
import org.ikasan.spec.flow.FlowElementInvoker;
import org.ikasan.spec.recovery.RecoveryManager;

/**
 * JavaBean encapsulating startup control information for Initiators
 * 
 * The following start types are defined:
 * 
 * AUTOMATIC: Initiator will be started by its container when the container is
 * initialised MANUAL: Initiator will not be started by its container when
 * container is started/initialised, but will be manually startable at a later
 * stage DISABLED: Initiator will not be started by its container when container
 * is started/initialised, and will not be manually startable at a later stage
 * 
 * @author The Ikasan Development Team
 * 
 */
public class FlowBuilder
{
    private RecoveryManagerFactory recoveryManagerFactory =
        new RecoveryManagerFactory(SchedulerFactory.getInstance().getScheduler(), CachingScheduledJobFactory.getInstance());
    
    private FlowElement<Consumer> consumerFlowElement;
    private FlowElement lastUpdatedFlowElement;
    
    public static FlowBuilder newInstance()
    {
        return new FlowBuilder();
    }
    
    private FlowBuilder()
    {
    }

    public FlowElement<Consumer> setConsumer(String flowElementName, Consumer consumer)
    {
        this.lastUpdatedFlowElement = new FlowElementImpl(flowElementName, consumer);
        return lastUpdatedFlowElement;
    }
    
    public FlowElement append(String flowElementName, Converter converter)
    {
        this.lastUpdatedFlowElement = new FlowElementImpl(flowElementName, converter, this.lastUpdatedFlowElement);
        return this.lastUpdatedFlowElement;
    }
    
    public FlowElement append(String flowElementName, Translator translator)
    {
        this.lastUpdatedFlowElement = new FlowElementImpl(flowElementName, translator, this.lastUpdatedFlowElement);
        return this.lastUpdatedFlowElement;
    }
    
    public Flow create(String flowName, String moduleName)
    {
        FlowElementInvoker flowElementInvoker = new VisitingFlowElementInvoker(DefaultReplicationFactory.getInstance());
        ConfiguredResourceConfigurationService configurationService = new ConfiguredResourceConfigurationService(new CacheConfigurationDAO(), new CacheConfigurationDAO());
        FlowConfiguration flowConfiguration = new DefaultFlowConfiguration(consumerFlowElement, configurationService);
        RecoveryManager recoveryManager = recoveryManagerFactory.getRecoveryManager(flowName, moduleName, consumerFlowElement.getFlowComponent());
        return new VisitingInvokerFlow(flowName, moduleName, flowConfiguration, flowElementInvoker, recoveryManager);
    }
    
    private class CacheConfigurationDAO implements org.ikasan.flow.configuration.dao.ConfigurationDao
    {
        private Map<String,Configuration> configurations = new HashMap<String,Configuration>();

        @Override
        public Configuration findById(String id)
        {
            return this.configurations.get(id);
        }

        @Override
        public void save(Configuration configuration)
        {
            this.configurations.put(configuration.getId(), configuration);
        }

        @Override
        public void delete(Configuration configuration)
        {
            this.configurations.remove(configuration.getId());
        }
        
    }
}
