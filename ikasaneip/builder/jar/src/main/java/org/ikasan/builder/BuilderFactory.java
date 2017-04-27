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

import org.ikasan.exceptionResolver.ExceptionResolver;
import org.ikasan.exclusion.service.ExclusionServiceFactory;
import org.ikasan.recovery.RecoveryManagerFactory;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.configuration.ConfigurationService;
import org.ikasan.spec.error.reporting.ErrorReportingService;
import org.ikasan.spec.error.reporting.ErrorReportingServiceFactory;
import org.ikasan.spec.exclusion.ExclusionService;
import org.ikasan.spec.flow.FlowElement;
import org.ikasan.spec.flow.FlowEventListener;
import org.ikasan.spec.flow.FlowInvocationContextListener;
import org.ikasan.spec.monitor.Monitor;
import org.ikasan.spec.replay.ReplayRecordService;
import org.ikasan.spec.serialiser.SerialiserFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple Flow builder.
 * 
 * @author Ikasan Development Team
 */
public class BuilderFactory
{
    // singleton
    static BuilderFactory builderFactory = new BuilderFactory();

    static String[] flowLoaderDefaultServices = {"src/test/resources/transaction-conf.xml","platform-service-conf.xml","ikasan-transaction-conf.xml","configuration-service-conf.xml","error-reporting-service-conf.xml"};

    protected static BuilderFactory getInstance()
    {
        return builderFactory;
    }

    public static ModuleBuilder moduleBuilder(String name)
    {
        return new ModuleBuilder(name);
    }

    public static ModuleBuilder moduleBuilder(String name, String version)
    {
        return new ModuleBuilder(name, version);
    }

    public static RouteBuilder routeBuilder()
    {
        return new RouteBuilder( new RouteImpl(new ArrayList<FlowElement>()) );
    }

    public static FlowBuilder flowBuilder()
    {
        ApplicationContext builderContext = new ClassPathXmlApplicationContext(flowLoaderDefaultServices);
//        BeanDefinitionRegistry registry = ((BeanDefinitionRegistry)builderContext.getAutowireCapableBeanFactory());

        // create flowBuilder with default configuration
        FlowBuilder flowBuilder = new FlowBuilder();
        flowBuilder.withFlowListener(builderContext.getBean(FlowEventListener.class));
        flowBuilder.withRecoveryManagerFactory(builderContext.getBean(RecoveryManagerFactory.class));
        flowBuilder.setExceptionResolver(builderContext.getBean(ExceptionResolver.class));
        flowBuilder.withConfigurationService(builderContext.getBean(ConfigurationService.class));
        flowBuilder.withExclusionServiceFactory(builderContext.getBean(ExclusionServiceFactory.class));
        flowBuilder.withExclusionService(builderContext.getBean(ExclusionService.class));
        flowBuilder.withErrorReportingServiceFactory(builderContext.getBean(ErrorReportingServiceFactory.class));
        flowBuilder.withErrorReportingService(builderContext.getBean(ErrorReportingService.class));
        flowBuilder.setMonitor(builderContext.getBean(Monitor.class));
        // eventFactory ?
        flowBuilder.withSerialiserFactory(builderContext.getBean(SerialiserFactory.class));
        flowBuilder.withReplayRecordService(builderContext.getBean(ReplayRecordService.class));
        flowBuilder.withFlowInvocationContextListener(builderContext.getBean(FlowInvocationContextListener.class));

        return flowBuilder;
    }

    public static FlowBuilder flowBuilder(String name, String module)
    {
        return new FlowBuilder(name, module);
    }

    protected Route newPrimaryRoute(FlowElement<Consumer> flowElement)
    {
        List<FlowElement> flowElements = new ArrayList<FlowElement>();
        flowElements.add(flowElement);
        return new RouteImpl(flowElements);
    }
}




