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
package com.ikasan.sample.spring.boot.builderpattern;

import com.ikasan.component.factory.IkasanComponent;
import com.ikasan.component.factory.IkasanComponentFactory;
import com.sample.spring.component.custom.CustomConverter;
import liquibase.pro.packaged.T;
import org.apache.activemq.ActiveMQXAConnectionFactory;
import org.ikasan.builder.BuilderFactory;
import org.ikasan.builder.FlowBuilder;
import org.ikasan.builder.ModuleBuilder;

import org.ikasan.builder.component.endpoint.FtpConsumerBuilderImpl;
import org.ikasan.component.endpoint.jms.spring.consumer.JmsContainerConsumer;
import org.ikasan.component.endpoint.jms.spring.producer.JmsTemplateProducer;
import org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumer;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.env.ConfigurableEnvironment;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.jms.ConnectionFactory;

@Configuration
@ImportResource( {
        "classpath:ikasan-transaction-pointcut-jms.xml",
        "classpath:h2-datasource-conf.xml"
} )
public class ModuleConfig
{
    @Resource
    private BuilderFactory builderFactory;

    @Resource
    private IkasanComponentFactory ikasanComponentFactory;

    /**
     * NOTE 1 - Full Type is used by component following practice of using Full Type at creation (so the user
     * knows what they are actually using - rather than having to search builder code buried deep in Ikasan).
     *
     * NOTE 2 - The Module and Flow and ComponentFactories in *all* are modules are spring annotated / dependent on spring.
     * The IkasanComponentFactory interface and the @IkasanComponent *ARE NOT* dependent on spring.
     *
     * NOTE 3 - Use of the annotation is equivalent to :-
     *
     *
     *
     */
    @IkasanComponent(prefix="sample.jms", factoryPrefix = "sample.jms.consumer")
    private JmsContainerConsumer sampleJmsConsumer;

    @IkasanComponent(prefix="sample.jms", factoryPrefix = "sample.jms.producer")
    private JmsTemplateProducer sampleJmsProducer;

//    @IkasanComponent(prefix="sample.ftp.consumer")
//    private ScheduledConsumer ftpConsumer;
//
//    @IkasanComponent()
//    private SimpleCustomComponent simpleCustomComponent;
//
    @IkasanComponent(prefix="custom.converter")
    private CustomConverter customConverter;


    @Bean
    public Module getModule(){

        ModuleBuilder mb = builderFactory.getModuleBuilder("sample-boot-jms");
        FlowBuilder fb = mb.getFlowBuilder("Jms Sample Flow");

        Flow flow = fb
                .withDescription("Flow demonstrates usage of JMS Consumer and JMS Producer")
                .consumer("JMS Consumer", sampleJmsConsumer)
                .converter("Custom Converter", customConverter)
                .broker( "Exception Generating Broker", new ExceptionGenerationgBroker())
                .broker( "Delay Generating Broker", new DelayGenerationBroker())
                .producer("JMS Producer", sampleJmsProducer)
                .build();

        Module module = mb.withDescription("Sample Module")
            .addFlow(flow)
            .build();
        return module;
    }


    /**
     * TODO -  hide this from view by moving into Ikasan (need to hook into bean creation lifecyle)
     * TODO -  the user will not have to *worry* about this call;
     */
    @PostConstruct
    public void processAnnotation(){
        ikasanComponentFactory.populateAnnotations(this);
    }

}
