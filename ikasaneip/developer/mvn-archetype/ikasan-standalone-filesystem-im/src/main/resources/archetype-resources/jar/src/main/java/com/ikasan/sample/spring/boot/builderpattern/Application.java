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

import org.ikasan.builder.*;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Sample local file consumer and local file producer Integration Module
 * @author Ikasan Development Team
 */
@SpringBootApplication
@ComponentScan({"org.ikasan.*", "com.ikasan.*"})
public class Application
{
    public static void main(String[] args) throws Exception
    {
        new Application().boot(args);
    }

    /**
     * Create the integration module and boot it.
     * @param args
     * @return IkasanApplication
     */
    public IkasanApplication boot(String[] args)
    {
        // get an ikasanApplication instance
        IkasanApplication ikasanApplication = IkasanApplicationFactory.getIkasanApplication(Application.class, args);

        // get local integration module componentFactory instance
        ComponentFactory componentFactory = ikasanApplication.getBean(ComponentFactory.class);

        // get the builders
        BuilderFactory builderFactory = ikasanApplication.getBuilderFactory();
        ModuleBuilder moduleBuilder = builderFactory.getModuleBuilder("sampleFileIntegrationModule").withDescription("Sample File reader/writer module.");

        Flow sourceFlow = moduleBuilder.getFlowBuilder("sourceFileFlow")
                .withDescription("Sample file to JMS flow")
                .withExceptionResolver( componentFactory.getSourceFlowExceptionResolver() )
                .consumer("File Consumer", componentFactory.getFileConsumer())
                .converter("File Converter", componentFactory.getSourceFileConverter())
                .producer("JMS Producer", componentFactory.getJmsProducer()).build();

        Flow targetFlow = moduleBuilder.getFlowBuilder("targetFileFlow")
                .withDescription("Sample JMS to file flow")
                .consumer("JMS Consumer", componentFactory.getJmsConsumer())
                .producer("File Producer", componentFactory.getFileProducer()).build();

        Module module = moduleBuilder.withDescription("Sample file consumer / producer module.")
                .addFlow(sourceFlow)
                .addFlow(targetFlow)
                .build();

        ikasanApplication.run(module);
        return ikasanApplication;
    }
}