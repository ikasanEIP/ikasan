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
package org.ikasan.security.service.authentication;

import org.ikasan.security.dao.constants.SecurityConstants;
import org.ikasan.security.model.AuthenticationMethod;
import org.ikasan.security.service.DashboardUserServiceImpl;
import org.ikasan.security.service.SecurityService;
import org.ikasan.security.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationProvider;

/**
 * @author Ikasan Development Team
 */
public class ModuleAuthenticationProviderFactoryImpl implements AuthenticationProviderFactory<AuthenticationMethod>
{
    /**
     * Logger instance
     */
    private static Logger logger = LoggerFactory.getLogger(ModuleAuthenticationProviderFactoryImpl.class);

    private static final String DASHBOARD_EXTRACT_ENABLED_PROPERTY = "ikasan.dashboard.extract.enabled";

    private UserService userService;

    private DashboardUserServiceImpl dashboardUserService;

    private SecurityService securityService;

    private Environment environment;

    /**
     * Constructor
     *
     * @param userService
     * @param securityService
     */
    public ModuleAuthenticationProviderFactoryImpl(UserService userService,
        DashboardUserServiceImpl dashboardUserService,
        SecurityService securityService, Environment environment)
    {
        super();
        this.userService = userService;
        if (this.userService == null)
        {
            throw new IllegalArgumentException("userService cannot be null!");
        }
        this.securityService = securityService;
        if (this.securityService == null)
        {
            throw new IllegalArgumentException("securityService cannot be null!");
        }
        this.dashboardUserService = dashboardUserService;
        if (this.dashboardUserService == null)
        {
            throw new IllegalArgumentException("dashboardUserService cannot be null!");
        }
        this.environment = environment;
    }

    /* (non-Javadoc)
     * @see org.ikasan.security.listener.authentication.AuthenticationProviderFactory#getAuthenticationProvider(java.lang.Object)
     */
    @Override
    public AuthenticationProvider getAuthenticationProvider(AuthenticationMethod authMethod)
    {
        AuthenticationProvider authProvider = null;
        if (authMethod == null || authMethod.getMethod().equals(SecurityConstants.AUTH_METHOD_LOCAL))
        {
            authProvider = createLocalAuthenticationProvider();
        }
        else if (authMethod.getMethod().equals(SecurityConstants.AUTH_METHOD_DASHBOARD))
        {
            try
            {
                authProvider = createDashboardAuthenticationProvider();
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }
        else
        {
            throw new IllegalArgumentException("authMethod not supported: " + authMethod.getMethod());
        }
        return authProvider;
    }

    /* (non-Javadoc)
     * @see org.ikasan.security.listener.authentication.AuthenticationProviderFactory#getLocalAuthenticationProvider()
     */
    @Override
    public AuthenticationProvider getLocalAuthenticationProvider()
    {
        boolean isDashboardEnabled = Boolean.valueOf(environment.getProperty(DASHBOARD_EXTRACT_ENABLED_PROPERTY, "false"));
        if(isDashboardEnabled){
            return this.createDashboardAuthenticationProvider();
        }else
        {
            return this.createLocalAuthenticationProvider();
        }
    }

    /*
     * (non-Javadoc)
     * @see org.ikasan.security.listener.authentication.AuthenticationProviderFactory#testAuthenticationConnection(org.ikasan.security.window.AuthenticationMethod)
     */
    public void testAuthenticationConnection(AuthenticationMethod authMethod) throws Exception
    {
        if (authMethod == null)
        {
            return;
        }
        else if (authMethod.getMethod().equals(SecurityConstants.AUTH_METHOD_LOCAL))
        {
            return;
        }
        else if (authMethod.getMethod().equals(SecurityConstants.AUTH_METHOD_DASHBOARD))
        {
            return;
        }
        else
        {
            throw new IllegalArgumentException("authMethod not supported: " + authMethod.getMethod());
        }
    }

    /**
     * @return
     */
    private DashboardAuthenticationProvider createDashboardAuthenticationProvider()
    {
        return new DashboardAuthenticationProvider(this.dashboardUserService);
    }

    /**
     * @return
     */
    private LocalAuthenticationProvider createLocalAuthenticationProvider()
    {
        return new LocalAuthenticationProvider(this.securityService, this.userService);
    }
}
