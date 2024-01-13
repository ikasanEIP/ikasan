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

import org.ikasan.WiretapAutoConfiguration;
import org.ikasan.configurationService.ConfigurationServiceAutoConfiguration;
import org.ikasan.dashboard.DashboardClientAutoConfiguration;
import org.ikasan.error.reporting.ErrorReportingAutoConfiguration;
import org.ikasan.exceptionResolver.ExceptionConfig;
import org.ikasan.exceptionResolver.ExceptionResolver;
import org.ikasan.exceptionResolver.action.ExcludeEventAction;
import org.ikasan.exceptionResolver.action.IgnoreAction;
import org.ikasan.exceptionResolver.action.RetryAction;
import org.ikasan.exceptionResolver.action.ScheduledRetryAction;
import org.ikasan.exclusion.ExclusionAutoConfiguration;
import org.ikasan.filter.FilterAutoConfiguration;
import org.ikasan.harvesting.HarvestingAutoConfiguration;
import org.ikasan.hospital.HospitalAutoConfiguration;
import org.ikasan.housekeeping.ModuleHousekeepingAutoConfiguration;
import org.ikasan.module.IkasanModuleAutoConfiguration;
import org.ikasan.module.service.FlowStartupTypeConfigurationConverter;
import org.ikasan.module.service.WiretapTriggerConfigurationConverter;
import org.ikasan.monitor.IkasanMonitorAutoConfiguration;
import org.ikasan.replay.ReplayAutoConfiguration;
import org.ikasan.rest.module.IkasanRestAutoConfiguration;
import org.ikasan.systemevent.SystemEventAutoConfiguration;
import org.ikasan.transaction.IkasanTransactionConfiguration;
import org.ikasan.web.IkasanWebAutoConfiguration;
import org.ikasan.web.WebSecurityConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource( {
        "classpath:ikasan-transaction-conf.xml",
        "classpath:ikasan-transaction-pointcut-resubmission.xml",
        "classpath:ikasan-transaction-pointcut-quartz.xml",
        "classpath:serialiser-service-conf.xml",
        "classpath:scheduler-service-conf.xml",
        "classpath:recoveryManager-service-conf.xml",
        "classpath:topology-conf.xml",
        "classpath:datasource-conf.xml",
        "classpath:security-service-boot-conf.xml",
        "classpath:springapp-servlet-boot.xml",
} )
@Import({ ExceptionConfig.class, IkasanTransactionConfiguration.class, IkasanWebAutoConfiguration.class, IkasanModuleAutoConfiguration.class,
            WebSecurityConfig.class, IkasanRestAutoConfiguration.class, IkasanMonitorAutoConfiguration.class, ErrorReportingAutoConfiguration.class,
            FilterAutoConfiguration.class, ConfigurationServiceAutoConfiguration.class,  SystemEventAutoConfiguration.class, ReplayAutoConfiguration.class,
            WiretapAutoConfiguration.class, HospitalAutoConfiguration.class, ExclusionAutoConfiguration.class, DashboardClientAutoConfiguration.class,
            HarvestingAutoConfiguration.class, ModuleHousekeepingAutoConfiguration.class})
public class IkasanBaseAutoConfiguration
{

    @Bean
    public BuilderFactory builderFactory(){
         return new BuilderFactory();
    }
    @Bean
    public AopProxyProvider aopProxyProvider()
    {
        return new AopProxyProviderSpringImpl();
    }

    @Bean(name = "exceptionConfig")
    @ConfigurationProperties(prefix = "ikasan.exceptions")
    public ExceptionConfig exceptionConfig(){
        return new ExceptionConfig();
    }
    @Bean
    public ExceptionResolver exceptionResolver(BuilderFactory builderFactory, @Qualifier("exceptionConfig") ExceptionConfig exceptionConfig)
    {
        ExceptionResolverBuilder builder = builderFactory.getExceptionResolverBuilder();

        if( exceptionConfig.getExcludedClasses() !=null)
        {
            exceptionConfig.getExcludedClasses().stream().forEach(
                exclusion -> builder.addExceptionToAction(exclusion, ExcludeEventAction.instance()));
        }

        if( exceptionConfig.getIgnoredClasses() !=null)
        {
            exceptionConfig.getIgnoredClasses().stream().forEach(ignore -> builder.addExceptionToAction(ignore, IgnoreAction.instance()));
        }

        if( exceptionConfig.getRetryConfigs() !=null)
        {
            exceptionConfig.getRetryConfigs().stream().forEach(r -> builder
                .addExceptionToAction(r.getClassName(), new RetryAction(r.getDelayInMillis(), r.getMaxRetries())));
        }

        if( exceptionConfig.getScheduledRetryConfigs() !=null)
        {
            exceptionConfig.getScheduledRetryConfigs().stream().forEach(r -> builder.addExceptionToAction(r.getClassName(),
                new ScheduledRetryAction(r.getCronExpression(), r.getMaxRetries())));
        }
        return builder.build();
    }

    /** Put Spring Type Converters here or will get problems with circular dependencies **/
    @Bean
    @ConfigurationPropertiesBinding
    public FlowStartupTypeConfigurationConverter flowStartupTypeConfigurationConverter(){
        return new FlowStartupTypeConfigurationConverter();
    }

    @Bean
    @ConfigurationPropertiesBinding
    public WiretapTriggerConfigurationConverter wiretapTriggerConfigurationConverter(){
        return new WiretapTriggerConfigurationConverter();
    }
}
