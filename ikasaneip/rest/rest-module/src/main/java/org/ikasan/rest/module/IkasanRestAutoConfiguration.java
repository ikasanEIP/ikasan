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
package org.ikasan.rest.module;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.ikasan.configurationService.metadata.ConfigurationMetaDataImpl;
import org.ikasan.configurationService.metadata.ConfigurationParameterMetaDataImpl;
import org.ikasan.rest.module.sse.MonitoringFileService;
import org.ikasan.spec.metadata.ConfigurationMetaData;
import org.ikasan.spec.metadata.ConfigurationParameterMetaData;
import org.ikasan.spec.persistence.service.GeneralDatabaseService;
import org.ikasan.spec.persistence.service.InDoubtTransactionService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
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
    public ConfigurationApplication configurationApplication(){
        return new ConfigurationApplication();
    }

    @Bean
    public ModuleControlApplication moduleControlApplication(){
        return new ModuleControlApplication();
    }

    @Bean
    public ReplayApplication replayApplication()
    {
        return new ReplayApplication();
    }

    @Bean
    public ResubmissionApplication resubmissionApplication(){
        return new ResubmissionApplication();
    }

    @Bean
    public WiretapApplication wiretapApplication(){
        return new WiretapApplication();
    }

    @Bean
    public ErrorApplication errorApplication(){
        return new ErrorApplication();
    }

    @Bean
    public ExclusionApplication exclusionApplication(){
        return new ExclusionApplication();
    }

    @Bean
    public MetaDataApplication metaDataApplication(){
        return new MetaDataApplication();
    }

    @Bean
    public FilterApplication filterApplication(){
        return new FilterApplication();
    }

    @Bean
    public MonitoringFileService monitoringFileService() {
        return new MonitoringFileService();
    }

    @Bean
    public LogFileStreamApplication logFileStreamApplication() {
        return new LogFileStreamApplication();
    }

    @Bean
    public DownloadLogFileApplication downloadLogFileApplication() {
        return new DownloadLogFileApplication();
    }

    @Bean
    public SchedulerApplication schedulerApplication() {
        return new SchedulerApplication();
    }

    @ConditionalOnBean(GeneralDatabaseService.class)
    @Bean PersistenceApplication persistenceApplication() {
        return new PersistenceApplication();
    }

    @ConditionalOnBean(InDoubtTransactionService.class)
    @Bean InDoubtTransactionsApplication inDoubtTransactionsApplication() {
        return new InDoubtTransactionsApplication();
    }

    @Bean ModuleVersionApplication moduleVersionApplication() {
        return new ModuleVersionApplication();
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters){

        converters.stream()
                  .filter(c -> c instanceof MappingJackson2HttpMessageConverter)
                  .forEach(converter-> {
                      SimpleModule m = new SimpleModule();
                      m.addAbstractTypeMapping(
                          ConfigurationParameterMetaData.class, ConfigurationParameterMetaDataImpl.class);
                      m.addAbstractTypeMapping(ConfigurationMetaData.class, ConfigurationMetaDataImpl.class);
                      ((MappingJackson2HttpMessageConverter)converter).getObjectMapper().registerModule(m);

                      ((MappingJackson2HttpMessageConverter)converter).getObjectMapper().configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
                      ((MappingJackson2HttpMessageConverter)converter).getObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);

                  } );
    }

}
