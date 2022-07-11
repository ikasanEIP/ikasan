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
package org.ikasan.ootb.scheduler.agent.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.ikasan.component.endpoint.bigqueue.service.BigQueueManagementServiceImpl;
import org.ikasan.ootb.scheduler.agent.rest.converters.ObjectMapperFactory;
import org.ikasan.spec.bigqueue.service.BigQueueManagementService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class IkasanRestAutoConfiguration implements WebMvcConfigurer
{

    @Bean
    public SchedulerJobInitiationEventApplication schedulerJobInitiationEventApplication(){
        return new SchedulerJobInitiationEventApplication();
    }

    @Bean
    public SchedulerApplication schedulerApplication(){
        return new SchedulerApplication();
    }

    @Bean
    public JobProvisionApplication jobProvisionApplication() {
        return new JobProvisionApplication();
    }

    @Bean
    public DryRunSchedulerApplication dryRunSchedulerApplication() {
        return new DryRunSchedulerApplication();
    }

    @Bean
    public JobUtilsApplication jobUtilsApplication() {
        return new JobUtilsApplication();
    }

    @Bean
    public ContextInstanceApplication contextParametersApplication() {
        return new ContextInstanceApplication();
    }

    @Bean
    public BigQueueManagementApplication bigQueueManagementApplication() {
        return new BigQueueManagementApplication();
    }

    @Bean
    public BigQueueManagementService bigQueueManagementService() {
        return new BigQueueManagementServiceImpl();
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.forEach(converter -> {
            if(converter instanceof MappingJackson2HttpMessageConverter) {
                ((MappingJackson2HttpMessageConverter)converter).getObjectMapper().registerModule(ObjectMapperFactory.newSimpleModule());
                ((MappingJackson2HttpMessageConverter)converter).getObjectMapper().configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
                ((MappingJackson2HttpMessageConverter)converter).getObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);
            }
        });
    }

}
