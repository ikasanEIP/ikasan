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
package org.ikasan.console.security;

import org.apache.log4j.Logger;
import org.ikasan.console.module.Module;
import org.ikasan.console.pointtopointflow.PointToPointFlow;
import org.ikasan.console.pointtopointflow.PointToPointFlowProfile;
import org.springframework.security.Authentication;
import org.springframework.security.ConfigAttribute;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.afterinvocation.AfterInvocationProvider;

/**
 * Abstract class that provides functionality for determining access/configuration rights
 * 
 * @author Ikasan Development Team
 */
public abstract class AbstractPointToPointFlowProfileAfterInvocationProvider implements AfterInvocationProvider
{
    /** Configuration attribute to check */
    private String responsiveConfigAttribute;

    /** Logger for this class */
    private Logger logger = Logger.getLogger(AbstractPointToPointFlowProfileAfterInvocationProvider.class);

    /**
     * Constructor
     * 
     * @param responsiveConfigAttribute - The configuration to check 
     */
    public AbstractPointToPointFlowProfileAfterInvocationProvider(String responsiveConfigAttribute)
    {
        super();
        this.responsiveConfigAttribute = responsiveConfigAttribute;
    }

    /**
     * Determines if the specified PointToPointFlowProfile should be accessible to the currently logged in user
     * 
     * @param authentication - The authentication to use
     * @param pointToPointFlowProfile - The PointToPointFlowProfile to check against
     * @return true if user should be allowed to read PointToPointFlowProfile
     */
    protected boolean mayReadPointToPointFlowProfile(Authentication authentication, PointToPointFlowProfile pointToPointFlowProfile)
    {
        GrantedAuthority[] authorities = authentication.getAuthorities();
        Module fromModule;
        Module toModule;
        boolean fromModuleAuthorised;
        boolean toModuleAuthorised;

        // TODO Slightly inefficient algorithm?  Perhaps use less code
        for (PointToPointFlow pointToPointFlow : pointToPointFlowProfile.getPointToPointFlows())
        {
            fromModuleAuthorised = false;
            toModuleAuthorised = false;
            fromModule = pointToPointFlow.getFromModule();
            toModule = pointToPointFlow.getToModule();
            for (GrantedAuthority grantedAuthority : authorities)
            {
                if (fromModule == null)
                {
                    fromModuleAuthorised = true;
                }
                else if (grantedAuthority.getAuthority().equals("USER_" + fromModule.getName()))
                {
                    fromModuleAuthorised = true;
                }

                if (toModule == null)
                {
                    toModuleAuthorised = true;
                }
                else if (grantedAuthority.getAuthority().equals("USER_" + toModule.getName()))
                {
                    toModuleAuthorised = true;
                }
            }
            // If both modules aren't authorised, then this pointToPointFlow 
            // is not authorised and therefore the pointToPointFlowProfile is 
            // not authorised.
            if (!fromModuleAuthorised || !toModuleAuthorised)
            {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns true if the configuration attribute is supported by this provider
     *  
     * @param configAttribute configuration attribute to test 
     * @return true if the configuration attribute is supported by this provider
     */
    public boolean supports(ConfigAttribute configAttribute)
    {
        return configAttribute.getAttribute().equalsIgnoreCase(this.responsiveConfigAttribute);
    }

    /**
     * Returns true if the class is supported by this provider
     * 
     * @param clazz - class to check support for
     * @return true if the class is supported by this provider
     * 
     * Warning is suppressed as this method implements an interface that does not 
     * support generics
     */
    @SuppressWarnings("unchecked")
    public boolean supports(Class clazz)
    {
        this.logger.info("called with clazz [" + clazz + "]");
        return true;
    }
}
