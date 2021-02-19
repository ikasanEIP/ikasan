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

import org.ikasan.exceptionResolver.ExceptionConfig;
import org.ikasan.exceptionResolver.ExceptionResolver;
import org.ikasan.exceptionResolver.action.ExcludeEventAction;
import org.ikasan.exceptionResolver.action.IgnoreAction;
import org.ikasan.exceptionResolver.action.RetryAction;
import org.ikasan.exceptionResolver.action.ScheduledRetryAction;
import org.ikasan.module.IkasanModuleAutoConfiguration;
import org.ikasan.monitor.IkasanMonitorAutoConfiguration;
import org.ikasan.rest.module.IkasanRestAutoConfiguration;
import org.ikasan.transaction.IkasanTransactionConfiguration;
import org.ikasan.web.IkasanWebAutoConfiguration;
import org.ikasan.web.WebSecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.Map;
import java.util.Properties;

@Configuration
@ImportResource( {
        "classpath:ikasan-transaction-conf.xml",
        "classpath:ikasan-transaction-pointcut-resubmission.xml",
        "classpath:ikasan-transaction-pointcut-quartz.xml",
        "classpath:serialiser-service-conf.xml",
        "classpath:scheduler-service-conf.xml",
        "classpath:error-reporting-service-conf.xml",
        "classpath:recoveryManager-service-conf.xml",
        "classpath:filter-service-conf.xml",
        "classpath:configuration-service-conf.xml",
        "classpath:systemevent-service-conf.xml",
        "classpath:replay-service-conf.xml",
        "classpath:wiretap-service-conf.xml",
        "classpath:hospital-conf.xml",
        "classpath:exclusion-service-conf.xml",
        "classpath:topology-conf.xml",
        "classpath:datasource-conf.xml",
        "classpath:security-service-boot-conf.xml",
        "classpath:springapp-servlet-boot.xml",

} )
@Import({ ExceptionConfig.class, IkasanTransactionConfiguration.class, IkasanWebAutoConfiguration.class, IkasanModuleAutoConfiguration.class,
            WebSecurityConfig.class, IkasanRestAutoConfiguration.class, IkasanMonitorAutoConfiguration.class})
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

    @Bean
    @ConfigurationProperties(prefix = "ikasan.exceptions")
    public ExceptionConfig exceptionConfig(){
        return new ExceptionConfig();
    }
    @Bean
    public ExceptionResolver exceptionResolver(BuilderFactory builderFactory,ExceptionConfig exceptionConfig)
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
                new ScheduledRetryAction(r.getCronExpression(), r.getMaxRetries())
                                                                                    ));
        }
        return builder.build();
    }



    @Autowired
    @Qualifier("ikasan.ds")
    DataSource ikasands;

    @Autowired
    @Qualifier("ikasan.xads")
    DataSource ikasanxads;

    @Resource
    Map platformHibernateProperties;

    @Bean
    public LocalSessionFactoryBean ikasanNonXAAllSessionFactory(
                                                         )
    {
        LocalSessionFactoryBean sessionFactoryBean = new LocalSessionFactoryBean();
        sessionFactoryBean.setDataSource(ikasands);
        sessionFactoryBean.setMappingResources(
            "/org/ikasan/security/model/Principal.hbm.xml",
            "/org/ikasan/security/model/Role.hbm.xml",
            "/org/ikasan/security/model/Policy.hbm.xml",
            "/org/ikasan/security/model/User.hbm.xml",
            "/org/ikasan/security/model/Authority.hbm.xml",
            "/org/ikasan/security/model/AuthenticationMethod.hbm.xml",
            "/org/ikasan/security/model/PolicyLink.hbm.xml",
            "/org/ikasan/security/model/PolicyLinkType.hbm.xml",
            "/org/ikasan/security/model/RoleModule.hbm.xml",


            "/org/ikasan/configurationService/model/Configuration.hbm.xml",


             "/org/ikasan/error/reporting/model/ErrorOccurrence.hbm.xml",
             "/org/ikasan/error/reporting/model/ErrorCategorisation.hbm.xml",
             "/org/ikasan/error/reporting/model/ErrorCategorisationLink.hbm.xml",
             "/org/ikasan/error/reporting/model/ErrorOccurrenceAction.hbm.xml",
             "/org/ikasan/error/reporting/model/Link.hbm.xml",
             "/org/ikasan/error/reporting/model/Note.hbm.xml",
             "/org/ikasan/error/reporting/model/ErrorOccurrenceLink.hbm.xml",
             "/org/ikasan/error/reporting/model/ErrorOccurrenceNote.hbm.xml"

            );
        Properties properties = new Properties();
        properties.putAll(platformHibernateProperties);
        sessionFactoryBean.setHibernateProperties(properties);

        return sessionFactoryBean;
    }

    @Bean
    public LocalSessionFactoryBean ikasanXAAllSessionFactory(
                                                               )
    {
        LocalSessionFactoryBean sessionFactoryBean = new LocalSessionFactoryBean();
        sessionFactoryBean.setDataSource(ikasanxads);
        sessionFactoryBean.setMappingResources(
            "/org/ikasan/security/model/Principal.hbm.xml",
            "/org/ikasan/security/model/Role.hbm.xml",
            "/org/ikasan/security/model/Policy.hbm.xml",
            "/org/ikasan/security/model/User.hbm.xml",
            "/org/ikasan/security/model/Authority.hbm.xml",
            "/org/ikasan/security/model/AuthenticationMethod.hbm.xml",
            "/org/ikasan/security/model/PolicyLink.hbm.xml",
            "/org/ikasan/security/model/PolicyLinkType.hbm.xml",
            "/org/ikasan/security/model/RoleModule.hbm.xml",

            "/org/ikasan/configurationService/model/Configuration.hbm.xml",

            "/org/ikasan/exclusion/model/ExclusionEvent.hbm.xml",

            "/org/ikasan/hospital/model/ExclusionEventAction.hbm.xml",

            "/org/ikasan/replay/model/ReplayEvent.hbm.xml",
			"/org/ikasan/replay/model/ReplayAudit.hbm.xml",
			"/org/ikasan/replay/model/ReplayAuditEvent.hbm.xml",

            "/org/ikasan/systemevent/model/SystemEvent.hbm.xml",



            "/org/ikasan/wiretap/model/WiretapEventImpl.hbm.xml",
            "/org/ikasan/trigger/model/Trigger.hbm.xml",
            "/org/ikasan/history/model/ComponentInvocationMetric.hbm.xml",
            "/org/ikasan/history/model/CustomMetric.hbm.xml",
            "/org/ikasan/history/model/MetricEvent.hbm.xml",
            "/org/ikasan/history/model/FlowInvocationMetric.hbm.xml",

            "/org/ikasan/filter/duplicate/model/DefaultFilterEntry.hbm.xml"

            );
        Properties properties = new Properties();
        properties.putAll(platformHibernateProperties);
        sessionFactoryBean.setHibernateProperties(properties);

        return sessionFactoryBean;
    }

}
