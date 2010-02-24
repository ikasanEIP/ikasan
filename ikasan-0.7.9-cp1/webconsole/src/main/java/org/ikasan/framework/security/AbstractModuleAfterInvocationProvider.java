/*
 * $Id$
 * $URL$
 * 
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * Copyright (c) 2003-2008 Mizuho International plc. and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the 
 * Free Software Foundation Europe e.V. Talstrasse 110, 40217 Dusseldorf, Germany 
 * or see the FSF site: http://www.fsfeurope.org/.
 * ====================================================================
 */
package org.ikasan.framework.security;

import org.apache.log4j.Logger;
import org.ikasan.framework.module.Module;
import org.springframework.security.Authentication;
import org.springframework.security.ConfigAttribute;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.afterinvocation.AfterInvocationProvider;

/**
 * Abstract class that provides functionality for determining access/configuration rights
 * 
 * @author Ikasan Development Team
 */
public abstract class AbstractModuleAfterInvocationProvider implements AfterInvocationProvider
{
    /** Configuration attribute to check */
    private String responsiveConfigAttribute;

    /** Logger for this class */
    private Logger logger = Logger.getLogger(AbstractModuleAfterInvocationProvider.class);

    /**
     * Constructor
     * 
     * @param responsiveConfigAttribute - The configuration to check 
     */
    public AbstractModuleAfterInvocationProvider(String responsiveConfigAttribute)
    {
        super();
        this.responsiveConfigAttribute = responsiveConfigAttribute;
    }

    /**
     * Determines if the specified module should be accessible to the currently logged in user
     * 
     * @param authentication - The authentication to use
     * @param module - The module to check against
     * @return true if user should be allowed to read module
     */
    protected boolean mayReadModule(Authentication authentication, Module module)
    {
        GrantedAuthority[] authorities = authentication.getAuthorities();
        for (GrantedAuthority grantedAuthority : authorities)
        {
            if (grantedAuthority.getAuthority().equals("USER_" + module.getName()))
            {
                return true;
            }
        }
        return false;
    }

    public boolean supports(ConfigAttribute configAttribute)
    {
        return configAttribute.getAttribute().equalsIgnoreCase(responsiveConfigAttribute);
    }

    /*
     * Warning is suppressed as this method implements an interface that does not 
     * support generics
     */
    @SuppressWarnings("unchecked")
    public boolean supports(Class clazz)
    {
        logger.info("called with  clazz [" + clazz + "]");
        return true;
    }
}
