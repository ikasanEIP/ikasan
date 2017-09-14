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
package org.ikasan.sample.spring.boot.builderpattern;


import org.ikasan.builder.BuilderFactory;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.component.endpoint.Producer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.List;

/**
 * Sample component factory.
 *
 * @author Ikasan Development Team
 */
@Configuration
public class ComponentFactory
{
    @Resource
    private BuilderFactory builderFactory;

    @Value("#{'${file.consumer.filenames}'.split(',')}")
    List<String> sourceFilenames;

    @Value("${file.consumer.cronExpression}")
    String cronExpression;

    @Value("${file.consumer.scheduledGroupName}")
    String scheduledGroupName;

    @Value("${file.consumer.scheduledName}")
    String scheduledName;

    @Value("${file.consumer.configuredResourceId}")
    String fileConsumerConfiguredResourceId;

    @Value("${file.producer.configuredResourceId}")
    String fileProducerConfiguredResourceId;

    @Value("${file.producer.filename}")
    String targetFilename;

    /**
     * Return an instance of a configured file consumer
     * @return
     */
    Consumer getFileConsumer()
    {
        return builderFactory.getComponentBuilder().fileConsumer()
                .setCronExpression(cronExpression)
                .setScheduledJobGroupName(scheduledGroupName)
                .setScheduledJobName(scheduledName)
                .setFilenames(sourceFilenames)
                .setConfiguredResourceId(fileConsumerConfiguredResourceId)
                .build();
    }

    Producer getFileProducer()
    {
         return builderFactory.getComponentBuilder().fileProducer()
                .setConfiguredResourceId(fileProducerConfiguredResourceId)
                .setFilename(targetFilename)
                .build();
    }

}
