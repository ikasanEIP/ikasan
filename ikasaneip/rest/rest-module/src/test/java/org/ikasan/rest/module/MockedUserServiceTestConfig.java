package org.ikasan.rest.module;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.ikasan.configurationService.metadata.ConfigurationMetaDataImpl;
import org.ikasan.configurationService.metadata.ConfigurationParameterMetaDataImpl;
import org.ikasan.spec.metadata.model.ConfigurationMetaData;
import org.ikasan.spec.metadata.model.ConfigurationParameterMetaData;
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
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;

import java.util.Arrays;

@TestConfiguration
@EnableWebSecurity
@EnableWebMvc
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class MockedUserServiceTestConfig implements WebMvcConfigurer
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
        SimpleModule m = new SimpleModule();
        m.addAbstractTypeMapping(
            ConfigurationParameterMetaData.class, ConfigurationParameterMetaDataImpl.class);
        m.addAbstractTypeMapping(ConfigurationMetaData.class, ConfigurationMetaDataImpl.class);

        JsonMapper jsonMapper = JsonMapper.builder()
            .addModule(m)
            .changeDefaultPropertyInclusion(incl -> incl.withContentInclusion(JsonInclude.Include.NON_NULL)
                .withValueInclusion(JsonInclude.Include.NON_NULL))
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
            .build();

        builder.disableDefaults();
        builder.withJsonConverter(new JacksonJsonHttpMessageConverter(jsonMapper));
    }
}
