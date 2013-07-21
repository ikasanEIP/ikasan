/*
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

package org.ikasan.framework.security;

import java.util.Iterator;

import org.apache.log4j.Logger;
import org.springframework.aop.framework.ReflectiveMethodInvocation;
import org.springframework.security.Authentication;
import org.springframework.security.ConfigAttribute;
import org.springframework.security.ConfigAttributeDefinition;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.vote.AccessDecisionVoter;

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
