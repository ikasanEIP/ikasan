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
package org.ikasan.framework.security.model;

import junit.framework.Assert;

import org.junit.Test;

/**
 * This is a class for Unit testing the org.ikasan.framework.security.model.User object
 * 
 * @author Ikasan Development Team
 */
public class UserTest
{
    /** An Authority object to test with */
    private Authority anAuthority = new Authority("anAuthority");

    /** Another Authority object to test with */
    private Authority anotherAuthority = new Authority("anotherAuthority");

    /**
     * Test that the granting an authority to a user works
     */
    @Test
    public void testGrantAuthority()
    {
        // Create a new user without any granted authorities
        User user = new User("username", "password", "email", true);
        Assert.assertEquals("User should have no grantedAuthorities when created", 0, user.getAuthorities().length);

        // Grant an authority that the user does not already have
        user.grantAuthority(anAuthority);
        Assert.assertEquals("User should have exactly 1 grantedAuthorities", 1, user.getAuthorities().length);
        Assert.assertEquals("granted authority should be that set", anAuthority, user.getAuthorities()[0]);
    }

    /**
     * Test that the granting more than one authority to a user works
     */
    @Test
    public void testGrantAuthorities()
    {
        // Create a new user without any granted authorities
        User user = new User("username", "password", "email", true);
        Assert.assertEquals("User should have no grantedAuthorities when created", 0, user.getAuthorities().length);

        // Grant an authority that the user does not already have
        user.grantAuthority(anAuthority);
        Assert.assertEquals("User should have exactly 1 grantedAuthorities", 1, user.getAuthorities().length);
        Assert.assertEquals("granted authority should be that set", anAuthority, user.getAuthorities()[0]);

        // Grant another authority that the user does not already have
        user.grantAuthority(anotherAuthority);
        Assert.assertEquals("User should have exactly 2 grantedAuthorities", 2, user.getAuthorities().length);
        Assert.assertEquals("granted authority should be that set", anotherAuthority, user.getAuthorities()[1]);
    }
    
    /**
     * Test that the granting of the same authority to a user more than once 
     * throws an IllegalArgumentException
     */
    @Test(expected=IllegalArgumentException.class) 
    public void testGrantingSameAuthorityMoreThanOnce()
    {
        User user = new User("username", "password", "email", true);
        user.grantAuthority(anAuthority);
        user.grantAuthority(anAuthority);
    }
    
    /**
     * Test that revoking an authority from a user works
     */
    @Test
    public void testRevokeAuthority()
    {
        // Create a new user without any granted authorities
        User user = new User("username", "password", "email", true);
        Assert.assertEquals("User should have no grantedAuthorities when created", 0, user.getAuthorities().length);

        // Grant two different authorities to the user
        user.grantAuthority(anAuthority);
        user.grantAuthority(anotherAuthority);
        
        // Test that revoking an authority removes it from the users
        // grantedAuthorities
        user.revokeAuthority(anAuthority);
        Assert.assertEquals("User should have exactly 1 grantedAuthorities", 1, user.getAuthorities().length);
        Assert.assertEquals("only remaining granted authority should be the other one", anotherAuthority, user
            .getAuthorities()[0]);
    }
    
    /**
     * Test that revoking an authority from a user that the user doesn't have throws an 
     * IllegalArgumentException
     */
    @Test(expected=IllegalArgumentException.class)
    public void testRevokeUnownedAuthority()
    {
        // Create a new user without any granted authorities
        User user = new User("username", "password", "email", true);
        Assert.assertEquals("User should have no grantedAuthorities when created", 0, user.getAuthorities().length);
        user.revokeAuthority(anAuthority);
    }
    
    /**
     * Test to ensure that User comparisons are being performed correctly.
     */
    @Test
    public void testCompare()
    {
        User alice = new User("Alice", "password", "email", true);
        User bob = new User("Bob", "password", "email", true);
        
        Assert.assertTrue(bob.compareTo(alice) > 0);
        Assert.assertTrue(bob.compareTo(bob) == 0);
        Assert.assertTrue(alice.compareTo(bob) < 0);
    }
}
