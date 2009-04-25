/*
 * $Id: ModuleAfterInvocationProviderTest.java 16798 2009-04-24 14:12:09Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/webconsole/war/src/test/java/org/ikasan/framework/security/ModuleAfterInvocationProviderTest.java $
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
