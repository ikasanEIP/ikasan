package org.ikasan.ootb.scheduler.agent.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.ikasan.job.orchestration.model.context.*;
import org.ikasan.job.orchestration.model.job.*;
import org.ikasan.ootb.scheduler.agent.rest.dto.InternalEventDrivenJobInstanceDto;
import org.ikasan.spec.scheduled.context.model.*;
import org.ikasan.spec.scheduled.event.model.ContextualisedScheduledProcessEvent;
import org.ikasan.spec.scheduled.event.model.ScheduledProcessEvent;
import org.ikasan.spec.scheduled.event.model.SchedulerJobInitiationEvent;
import org.ikasan.spec.scheduled.instance.model.*;
import org.ikasan.spec.scheduled.job.model.*;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.HttpMessageConverters;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;

import java.util.*;

@TestConfiguration
@EnableWebSecurity
@EnableWebMvc
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class MockedUserServiceTestConfigWithConverter implements WebMvcConfigurer
{
    @Bean
    @Primary
    public UserDetailsService userDetailsService()
    {
        return new InMemoryUserDetailsManager(Arrays.asList(
            User.withUsername("webServiceAdmin")
                .password("password")
                .authorities("WebServiceAdmin")
                .build(),
            User.withUsername("readonly")
                .password("readonly")
                .authorities("readonly")
                .build()
        ));
    }


    @Override
    public void configureMessageConverters(HttpMessageConverters.ServerBuilder builder) {
        JsonMapper jsonMapper = JsonMapper.builder()
            .addModule(newSimpleModule())
            .changeDefaultPropertyInclusion(incl -> incl.withContentInclusion(JsonInclude.Include.NON_NULL)
                .withValueInclusion(JsonInclude.Include.NON_NULL))
            .configure(tools.jackson.databind.SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
            .build();

        builder.disableDefaults();
        builder.withJsonConverter(new JacksonJsonHttpMessageConverter(jsonMapper));
    }

    public static SimpleModule newSimpleModule() {
        return new SimpleModule()
            .addAbstractTypeMapping(And.class, AndImpl.class)
            .addAbstractTypeMapping(Or.class, OrImpl.class)
            .addAbstractTypeMapping(Not.class, NotImpl.class)
            .addAbstractTypeMapping(ContextTemplate.class, ContextTemplateImpl.class)
            .addAbstractTypeMapping(Context.class, ContextImpl.class)
            .addAbstractTypeMapping(ContextParameter.class, ContextParameterImpl.class)
            .addAbstractTypeMapping(SchedulerJob.class, SchedulerJobImpl.class)
            .addAbstractTypeMapping(QuartzScheduleDrivenJob.class, QuartzScheduleDrivenJobImpl.class)
            .addAbstractTypeMapping(FileEventDrivenJob.class, FileEventDrivenJobImpl.class)
            .addAbstractTypeMapping(InternalEventDrivenJob.class, InternalEventDrivenJobImpl.class)
            .addAbstractTypeMapping(SchedulerJobWrapper.class, SchedulerJobWrapperImpl.class)
            .addAbstractTypeMapping(JobDependency.class, JobDependencyImpl.class)
            .addAbstractTypeMapping(ContextDependency.class, ContextDependencyImpl.class)
            .addAbstractTypeMapping(LogicalGrouping.class, LogicalGroupingImpl.class)
            .addAbstractTypeMapping(LogicalOperator.class, LogicalOperatorImpl.class)
            .addAbstractTypeMapping(ContextInstance.class, ContextInstanceImpl.class)
            .addAbstractTypeMapping(SchedulerJobInstance.class, SchedulerJobInstanceImpl.class)
            .addAbstractTypeMapping(SchedulerJobLockParticipant.class, SchedulerJobLockParticipantImpl.class)
            .addAbstractTypeMapping(ContextParameterInstance.class, ContextParameterInstanceImpl.class)
            .addAbstractTypeMapping(JobLock.class, JobLockImpl.class)
            .addAbstractTypeMapping(JobLockInstance.class, JobLockInstanceImpl.class)
            .addAbstractTypeMapping(ScheduledProcessEvent.class, ContextualisedScheduledProcessEventImpl.class)
            .addAbstractTypeMapping(ContextualisedScheduledProcessEvent.class, ContextualisedScheduledProcessEventImpl.class)
            .addAbstractTypeMapping(SchedulerJobInitiationEvent.class, SchedulerJobInitiationEventImpl.class)
            .addAbstractTypeMapping(InternalEventDrivenJobInstance.class, InternalEventDrivenJobInstanceDto.class)
            .addAbstractTypeMapping(ReplacementPair.class, ReplacementPairImpl.class)
            .addAbstractTypeMapping(List.class, ArrayList.class)
            .addAbstractTypeMapping(Map.class, HashMap.class);
    }
}
