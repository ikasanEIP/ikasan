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
package org.ikasan.framework.security.model;

import junit.framework.Assert;

import org.junit.Test;


/**
 * 
 * @author Ikasan Development Team
 *
 */
public class UserTest
{
    private Authority anAuthority = new Authority("anAuthority");
    private Authority anotherAuthority = new Authority("anotherAuthority");
    
    
    @Test
    public void testGrantAuthority()
    {
        //create a new user without any granted authorities
        User user = new User("username", "password", true);
        Assert.assertEquals("User should have no grantedAuthorities when created",0,user.getAuthorities().length);
        
        //grant an authority that the user does not already have
        user.grantAuthority(anAuthority);
        Assert.assertEquals("User should have exactly 1 grantedAuthorities",1,user.getAuthorities().length);        
        Assert.assertEquals("granted authority should be that set", anAuthority, user.getAuthorities()[0]);
        
        //test that granting the same authority again throws an exception
        IllegalArgumentException illegalArgumentException = null;
        try{
            user.grantAuthority(anAuthority);
            Assert.fail("Exception should have been thrown for granting an authority to a user that they already hold");
        } catch(IllegalArgumentException illegalArgumentException2){
            illegalArgumentException = illegalArgumentException2;
        }
        Assert.assertNotNull("Thrown exception should have been IllegalArgumentException", illegalArgumentException);
 
        //grant another authority that the user does not already have
        user.grantAuthority(anotherAuthority);
        Assert.assertEquals("User should have exactly 2 grantedAuthorities",2,user.getAuthorities().length);        
        Assert.assertEquals("granted authority should be that set", anotherAuthority, user.getAuthorities()[1]);

    }

    public void testRevokeAuthority()
    {
        //create a new user without any granted authorities
        User user = new User("username", "password", true);
        Assert.assertEquals("User should have no grantedAuthorities when created",0,user.getAuthorities().length);
        
        //test that revoking an authority which the user doesnt hold throws an exception
        IllegalArgumentException illegalArgumentException = null;
        try{
            user.revokeAuthority(anAuthority);
            Assert.fail("Exception should have been thrown for revoking an authority a user doesnt hold");
        } catch(IllegalArgumentException illegalArgumentException2){
            illegalArgumentException = illegalArgumentException2;
        }
        Assert.assertNotNull("Thrown exception should have been IllegalArgumentException", illegalArgumentException);

        //grant two different authorities to the user
        user.grantAuthority(anAuthority);
        user.grantAuthority(anotherAuthority);
        
        //test that revoking an authority removes it from the users grantedAuthorities
        user.revokeAuthority(anAuthority);
        Assert.assertEquals("User should have exactly 1 grantedAuthorities",1,user.getAuthorities().length);        
        Assert.assertEquals("only remaining granted authority should be the other one", anotherAuthority, user.getAuthorities()[0]);
       

        
    }
}
