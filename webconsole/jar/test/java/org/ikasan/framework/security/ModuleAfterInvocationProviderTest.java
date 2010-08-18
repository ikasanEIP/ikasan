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
package org.ikasan.framework.security;

import junit.framework.Assert;

import org.ikasan.framework.module.Module;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;
import org.springframework.security.AccessDeniedException;
import org.springframework.security.Authentication;
import org.springframework.security.ConfigAttributeDefinition;
import org.springframework.security.GrantedAuthority;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class ModuleAfterInvocationProviderTest
{
    
    private static final String ACCESSIBLE_MODULE_NAME = "accessibleModule";
    private static final String INACCESSIBLE_MODULE_NAME = "inaccessibleModule";

    private ModuleAfterInvocationProvider provider = new ModuleAfterInvocationProvider();
    
    private Mockery mockery = new Mockery();
    
    @Test
    public void testDecide()
    {
        ConfigAttributeDefinition config = new ConfigAttributeDefinition("AFTER_MODULE_READ");
        
        final Module accessibleModule = mockery.mock(Module.class);
        final Module inaccessibleModule = mockery.mock(Module.class);
        
        final Authentication authentication = mockery.mock(Authentication.class);
        final GrantedAuthority moduleUserAuthority = mockery.mock(GrantedAuthority.class);
        final GrantedAuthority[] grantedAuthorities = new GrantedAuthority[]{moduleUserAuthority};
        mockery.checking(new Expectations()
        {
            {
                
               allowing(accessibleModule).getName();will(returnValue(ACCESSIBLE_MODULE_NAME));
               allowing(inaccessibleModule).getName();will(returnValue(INACCESSIBLE_MODULE_NAME));
               allowing(authentication).getAuthorities();will(returnValue(grantedAuthorities));
               allowing(moduleUserAuthority).getAuthority();will(returnValue("USER_"+ACCESSIBLE_MODULE_NAME));
                                
            }
        });
        
 
        
        Assert.assertEquals("result should include the accessibleModule if we have the USER authority for it", accessibleModule, provider.decide(authentication, null, config, accessibleModule));
        
        AccessDeniedException accessDeniedException = null;
        try{
            provider.decide(authentication, null, config, inaccessibleModule);
            Assert.fail("Exception should have been thrown");
        } catch (AccessDeniedException acd){
            accessDeniedException = acd;
        }
        
        
        Assert.assertNotNull("AccessDeniedException for the inaccessibleModule if we have no USER authority for it", accessDeniedException);
        
        mockery.assertIsSatisfied();
    }
}
