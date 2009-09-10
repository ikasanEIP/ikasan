/*
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

import java.util.Iterator;

import org.apache.log4j.Logger;

import org.springframework.security.Authentication;
import org.springframework.security.ConfigAttribute;
import org.springframework.security.ConfigAttributeDefinition;
import org.springframework.security.GrantedAuthority;

import org.springframework.security.vote.AccessDecisionVoter;

import org.springframework.aop.framework.ReflectiveMethodInvocation;

/**
 * Votes if any {@link ConfigAttribute#getAttribute()} matches MODULE_ADMIN
 * 
 * If it is called to vote, this voter will only vote yes, if the user has
 * the admin role for the specified module.
 * 
 * Note that the moduleName must be the first argument to the called method
 * 

 */
public class ModuleAdminVoter implements AccessDecisionVoter {
 
    public static final String MODULE_ADMIN_ATTRIBUTE = "MODULE_ADMIN";

    /** Logger for this class */
    private Logger logger = Logger.getLogger(ModuleAdminVoter.class);


    public boolean supports(ConfigAttribute attribute) {
    	boolean result = attribute.getAttribute().equals(MODULE_ADMIN_ATTRIBUTE);

        return result;
    }


    public boolean supports(Class clazz) {
    	//TODO this should only return true if the clazz is type castable to ReflectiveMethodInvocation
        return true;
    }

    public int vote(Authentication authentication, Object object, ConfigAttributeDefinition config) {
        int result = ACCESS_ABSTAIN;
        Iterator iter = config.getConfigAttributes().iterator();
        GrantedAuthority[] authorities = authentication.getAuthorities();       

        while (iter.hasNext()) {
            ConfigAttribute attribute = (ConfigAttribute) iter.next();

            if (this.supports(attribute)) {
                result = ACCESS_DENIED;

                // Attempt to find a granted authority matching the admin role for this module
                ReflectiveMethodInvocation methodInvocation = (ReflectiveMethodInvocation)object;
            	
                //we assume that the moduleName is the first argument.
                String moduleName = (String)methodInvocation.getArguments()[0];
                
                String moduleAdminRole = "ADMIN_"+moduleName;
                for (int i = 0; i < authorities.length; i++) {
                    if (moduleAdminRole.equals(authorities[i].getAuthority())) {
                        
                    	return ACCESS_GRANTED;
                    }
                }
            }
        }
        return result;
    }
    
}
