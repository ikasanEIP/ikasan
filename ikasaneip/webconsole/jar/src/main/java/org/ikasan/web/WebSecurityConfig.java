package org.ikasan.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebSecurity
@EnableWebMvc
@EnableMethodSecurity
public class WebSecurityConfig {

    @Value("${ikasan.additional.unsecured.endpoint:/actuator/**}")
    private String additionalUnsecuredEndpoints;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .httpBasic(httpSecurityHttpBasicConfigurer -> {})
            .authorizeHttpRequests(authorizationManagerRequestMatcherRegistry
                -> authorizationManagerRequestMatcherRegistry
                    .requestMatchers("/login.jsp", "/css/**", "/images/**", "/js/**", "/rest/**", additionalUnsecuredEndpoints)
                    .permitAll()
                    .requestMatchers("/admin/**")
                    .hasAnyAuthority("ALL", "WebServiceAdmin", "WriteBlueConsole")
                    .requestMatchers("/**")
                    .hasAnyAuthority("ALL", "ReadBlueConsole")
                    .anyRequest().authenticated())
            .formLogin(httpSecurityFormLoginConfigurer
                -> httpSecurityFormLoginConfigurer.loginPage("/login.jsp")
                .loginProcessingUrl("/j_spring_security_check")
                .usernameParameter("j_username")
                .passwordParameter("j_password")
                .defaultSuccessUrl("/home.htm",true)
                .permitAll())
            .logout(httpSecurityLogoutConfigurer -> httpSecurityLogoutConfigurer
                .logoutSuccessUrl("/")
                .logoutUrl("/j_spring_security_logout")
                .deleteCookies("JSESSIONID"))
            .csrf(httpSecurityCsrfConfigurer
                -> httpSecurityCsrfConfigurer.disable())
            .headers(httpSecurityHeadersConfigurer
                -> httpSecurityHeadersConfigurer.frameOptions(frameOptionsConfig
                    -> frameOptionsConfig.sameOrigin()));

        return http.build();
    }
}
