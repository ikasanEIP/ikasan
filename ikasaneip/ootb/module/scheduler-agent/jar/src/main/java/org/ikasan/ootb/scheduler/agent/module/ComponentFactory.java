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
package org.ikasan.ootb.scheduler.agent.module;

import com.leansoft.bigqueue.BigQueueImpl;
import com.leansoft.bigqueue.IBigQueue;
import org.ikasan.builder.BuilderFactory;
import org.ikasan.ootb.scheduler.agent.module.component.*;
import org.ikasan.ootb.scheduler.agent.module.component.endpoint.consumer.BigQueueConsumer;
import org.ikasan.spec.component.endpoint.Broker;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.component.filter.Filter;
import org.ikasan.spec.component.routing.SingleRecipientRouter;
import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.scheduled.ScheduledProcessService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * Scheduler Agent component factory.
 *
 * @author Ikasan Development Team
 */
@Configuration
@ImportResource( {
    "classpath:h2-datasource-conf.xml"
} )
@EnableSwagger2
public class ComponentFactory
{
    @Value( "${module.name}" )
    String moduleName;

    @Resource
    BuilderFactory builderFactory;

    @Resource
    ScheduledProcessService scheduledProcessService;

    @Value( "${big.queue.consumer.configuration.queueDir}" )
    private String queueDir = "/sandbox/mick/bigquque";

    @Value( "${big.queue.consumer.configuration.inboundQueueName}" )
    private String inboundQueueName = "module-inbound-context-queue";

    private String outboundQueueName = "module-outbound-context-queue";

    SingleRecipientRouter getBlackoutRouter()
    {
        return new BlackoutRouter();
    }

    Broker getProcessExecutionBroker()
    {
        ProcessExecutionBrokerConfiguration configuration = new ProcessExecutionBrokerConfiguration();
        configuration.setCommandLine("pwd");    // default safe command across all platforms

        ProcessExecutionBroker processExecutionBroker = new ProcessExecutionBroker();
        processExecutionBroker.setConfiguration(configuration);
        return processExecutionBroker;
    }

    Producer getScheduledStatusProducer()
    {
        return new ScheduledProcessEventProducer(scheduledProcessService);
    }

    Filter getScheduledStatusFilter()
    {
        return new ScheduledProcessEventFilter();
    }

    Converter getJobExecutionConverter() { return new JobExecutionConverter(moduleName); }

    @Bean
    public Consumer bigQueueConsumer(IBigQueue inboundQueue) {
        BigQueueConsumer consumer = new BigQueueConsumer(inboundQueue);
        return consumer;
    }


    @Bean
    public IBigQueue inboundQueue() throws IOException {
        return new BigQueueImpl(this.queueDir, this.inboundQueueName);
    }

    @Bean
    public IBigQueue outboundQueue() throws IOException {
        return new BigQueueImpl(this.queueDir, this.outboundQueueName);
    }

    @Bean
    public Docket scheeduleaApi() {
        return new Docket(DocumentationType.SWAGGER_2)
            .select()
            .apis(RequestHandlerSelectors.basePackage("org.ikasan.ootb.scheduler.agent.module.rest"))
            .paths(PathSelectors.any())
            .build()
            .groupName("scheduler-agent");
    }
}

