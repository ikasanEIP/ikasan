package org.ikasan.rest.module;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.ikasan.configurationService.metadata.ConfigurationMetaDataImpl;
import org.ikasan.configurationService.metadata.ConfigurationParameterMetaDataImpl;
import org.ikasan.spec.metadata.ConfigurationMetaData;
import org.ikasan.spec.metadata.ConfigurationParameterMetaData;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

@Configuration
@TestConfiguration
@EnableWebSecurity
@EnableWebMvc
@EnableMethodSecurity
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
    public void configureMessageConverters(
        List<HttpMessageConverter<?>> converters) {

        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();

        SimpleModule m = new SimpleModule();
        m.addAbstractTypeMapping(
            ConfigurationParameterMetaData.class, ConfigurationParameterMetaDataImpl.class);
        m.addAbstractTypeMapping(ConfigurationMetaData.class, ConfigurationMetaDataImpl.class);
        converter.getObjectMapper().registerModule(m);
        converter.getObjectMapper().configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        converter.getObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);
        converters.add(converter);
    }

}
