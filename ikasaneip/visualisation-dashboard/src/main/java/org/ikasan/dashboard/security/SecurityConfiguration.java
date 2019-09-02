package org.ikasan.dashboard.security;

import org.ikasan.rest.dashboard.JwtAuthenticationEntryPoint;
import org.ikasan.rest.dashboard.JwtRequestFilter;
import org.ikasan.security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configures spring security, doing the following:
 * <li>Bypass security checks for static resources,</li>
 * <li>Restrict access to the application, allowing only logged in users,</li>
 * <li>Set up the login form</li>
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity (prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter
{
    private static final String LOGIN_PROCESSING_URL = "/login";

    private static final String LOGIN_FAILURE_URL = "/login";

    private static final String LOGIN_URL = "/login";

    private static final String LOGOUT_SUCCESS_URL = "/login";

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationProvider ikasanAuthenticationProvider;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception
    {
        auth.authenticationProvider(ikasanAuthenticationProvider).userDetailsService(userService)
            .passwordEncoder(passwordEncoder);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception
    {
        return super.authenticationManagerBean();
    }

    /**
     * Require login to access internal pages and configure login form.
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception
    {
        //formatter:off
        // Not using Spring CSRF here to be able to use plain HTML for the login page
        http.csrf().disable()
            // Register our CustomRequestCache, that saves unauthorized access attempts, so
            // the user is redirected after login.
            .requestCache().requestCache(new CustomRequestCache())
            // Restrict access to our application.
            .and().authorizeRequests()
            // Allow all flow internal requests.
            .requestMatchers(SecurityUtils::isFrameworkInternalRequest).permitAll().antMatchers("/", "/VAADIN/**",
                                                                                                // the standard favicon URI
                                                                                                "/favicon.ico",
                                                                                                // the robots exclusion standard
                                                                                                "/robots.txt",
                                                                                                // web application manifest
                                                                                                "/manifest.webmanifest",
                                                                                                "/sw.js",
                                                                                                "/offline-page.html",
                                                                                                // icons and images
                                                                                                "/icons/**",
                                                                                                "/images/**",
                                                                                                // (development mode) static resources
                                                                                                "/frontend/**",
                                                                                                // (development mode) webjars
                                                                                                "/webjars/**",
                                                                                                // (development mode) H2 debugging console
                                                                                                "/h2-console/**",
                                                                                                // (production mode) static resources
                                                                                                "/frontend-es5/**",
                                                                                                "/frontend-es6/**")
            .permitAll().antMatchers("/persistenceSetup").permitAll().antMatchers("/authenticate").permitAll()
            .antMatchers("/swagger-ui.html").permitAll()
            // Allow all requests by logged in users.
            .anyRequest().authenticated()
            // Configure the login page.
            .and().formLogin().loginPage(LOGIN_URL).permitAll().loginProcessingUrl(LOGIN_PROCESSING_URL)
            .failureUrl(LOGIN_FAILURE_URL)
            // Configure logout
            .and().logout().logoutSuccessUrl(LOGOUT_SUCCESS_URL).and().exceptionHandling()
            .authenticationEntryPoint(jwtAuthenticationEntryPoint);
        /**
         * Session Management should be set to stateless for JWT token, but due to VAADIN utilising
         * cookies we cannot do that
         */
        //            .and()
        //            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        // Add a filter to validate the tokens with every request
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        //formatter:on
    }
}
