package org.ikasan.web;

import org.ikasan.security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebSecurity
@EnableWebMvc
@EnableGlobalMethodSecurity(prePostEnabled = true)
//    @DependsOn({"passwordEncoder", "userService", "ikasanAuthenticationProvider"})
public class WebSecurityConfig //extends WebSecurityConfigurerAdapter
{
//    @Autowired
//    PasswordEncoder passwordEncoder;
//
//    @Autowired
//    UserService userService;
//
//    @Autowired
//    AuthenticationProvider ikasanAuthenticationProvider;
//
//    @Value("${ikasan.additional.unsecured.endpoint:/actuator/**}")
//    private String additionalUnsecuredEndpoints;


//    @Override protected void configure(AuthenticationManagerBuilder auth) throws Exception
//    {
//        auth.authenticationProvider(ikasanAuthenticationProvider)
//            .userDetailsService(userService).passwordEncoder(passwordEncoder);
//    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorizationManagerRequestMatcherRegistry
            -> authorizationManagerRequestMatcherRegistry.requestMatchers("**").hasRole("USER"));
        return http.build();
    }

//    @Bean
//    public UserDetailsService userDetailsService() {
//        UserDetails user = User.withDefaultPasswordEncoder()
//            .username("user")
//            .password("password")
//            .roles("USER")
//            .build();
//        return new InMemoryUserDetailsManager(user);
//    }

//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http.authorizeHttpRequests(authorizationManagerRequestMatcherRegistry
//            -> authorizationManagerRequestMatcherRegistry.requestMatchers("**"));
//        return http.build();
//    }

//    @Bean
//    public WebSecurityCustomizer webSecurityCustomizer() {
//        return (web) -> web.antMatchers("/login.jsp").permitAll()
//            .antMatchers("/css/**","/images/**","/js/**").permitAll()
//            .antMatchers("/rest/**").permitAll()
//            .antMatchers(additionalUnsecuredEndpoints).permitAll()
//            .antMatchers("/admin/**").hasAnyAuthority("ALL", "WebServiceAdmin", "WriteBlueConsole")
//            .antMatchers("/**").hasAnyAuthority("ALL", "ReadBlueConsole")
//            .anyRequest().authenticated()
//            .and()
//            .formLogin()
//            .loginPage("/login.jsp").loginProcessingUrl("/j_spring_security_check")
//            .usernameParameter("j_username").passwordParameter("j_password").defaultSuccessUrl("/home.htm",true).permitAll()
//            .and()
//            .logout().logoutSuccessUrl("/").logoutUrl("/j_spring_security_logout").deleteCookies("JSESSIONID")
//            .and()
//            .csrf().disable()
//            .headers().frameOptions().sameOrigin()
//    }

//    /**
//     * <http auto-config="false" use-expressions="true"
//     * <p>
//     * authentication-manager-ref="auth-manager" access-decision-manager-ref="accessDecisionManager">
//     * <p>
//     * <access-denied-handler error-page="/accessDenied.jsp" />
//     * <intercept-url pattern="/login.jsp" access="permitAll"/>
//     * <intercept-url pattern="/css/style.css" access="permitAll"/>
//     * <intercept-url pattern="/images/**" access="permitAll"/>
//     * <!-- Keep Authorization on rest on the code level -->
//     * <!--<intercept-url pattern="/rest/**" access="hasAnyAuthority('ALL','WebServiceAdmin')"/>-->
//     * <intercept-url pattern="/rest/**" access="permitAll"/>
//     * <intercept-url pattern="/admin/**" access="hasAnyAuthority('ALL','WebServiceAdmin','WriteBlueConsole')" />
//     * <!--<intercept-url pattern="/org/ikasan/trigger/service" access="hasAnyAuthority('ALL','ReadBlueConsole','WriteBlueConsole')"/>-->
//     * <intercept-url pattern="/**" access="hasAnyAuthority('ALL','ReadBlueConsole')"/>
//     * <p>
//     * <form-login login-page="/login.jsp"
//     * always-use-default-target="true"
//     * default-target-url="/home.htm"
//     * username-parameter="j_username"
//     * password-parameter="j_password"
//     * login-processing-url="/j_spring_security_check"
//     * authentication-failure-url="/login.jsp?login_error=1"
//     * />
//     *
//     * <csrf disabled="true"/>
//     * <logout logout-success-url="/" logout-url="/j_spring_security_logout" />
//     * <remember-me
//     * <p>
//     * remember-me-parameter="_spring_security_remember_me"
//     * remember-me-cookie="SPRING_SECURITY_REMEMBER_ME_COOKIE"
//     * />
//     * <http-basic />
//     *
//     * </http>
//     *
//     * @param http
//     * @throws Exception
//     */
//    @Override protected void configure(HttpSecurity http) throws Exception
//    {
//        http
//            .httpBasic()
//            .and()
//            .authorizeRequests()
//            .antMatchers("/login.jsp").permitAll()
//            .antMatchers("/css/**","/images/**","/js/**").permitAll()
//            .antMatchers("/rest/**").permitAll()
//            .antMatchers(additionalUnsecuredEndpoints).permitAll()
//            .antMatchers("/admin/**").hasAnyAuthority("ALL", "WebServiceAdmin", "WriteBlueConsole")
//            .antMatchers("/**").hasAnyAuthority("ALL", "ReadBlueConsole")
//            .anyRequest().authenticated()
//            .and()
//            .formLogin()
//            .loginPage("/login.jsp").loginProcessingUrl("/j_spring_security_check")
//            .usernameParameter("j_username").passwordParameter("j_password").defaultSuccessUrl("/home.htm",true).permitAll()
//            .and()
//            .logout().logoutSuccessUrl("/").logoutUrl("/j_spring_security_logout").deleteCookies("JSESSIONID")
//            .and()
//            .csrf().disable()
//            .headers().frameOptions().sameOrigin();
//    }
}
