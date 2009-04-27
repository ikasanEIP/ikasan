/*
 * $Id: User.java 16808 2009-04-27 07:28:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/framework/src/main/java/org/ikasan/framework/security/model/User.java $
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

import java.util.HashSet;
import java.util.Set;

import org.springframework.security.GrantedAuthority;
import org.springframework.security.userdetails.UserDetails;

/**
 * Implementation of <code>UserDetails</code> suitable for ORM
 * 
 * @author Ikasan Development Team
 *
 */
public class User implements UserDetails
{

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 8975017088981341914L;

    /**
     * Id field utilised by ORM
     */
    private Long id;

    /**
     * Users username for the system
     */
    private String username;
    
    
    /**
     * Users password for the system
     */
    private String password;
    
    
    /**
     * Activation status for the user in the system
     */
    private boolean enabled;
    
    /**
     * All <code>GrantedAuthrities</code> held by the user for the system
     */
    private Set<Authority> grantedAuthorities = new HashSet<Authority>();
    
    
    /**
     * Constructor
     * 
     * @param username
     * @param password
     * @param enabled
     */
    public User(String username, String password, boolean enabled)
    {
        this.username = username;
        this.password = password;
        this.enabled = enabled;
    }
    
    /**
     * Default constructor required by ORM
     */
    @SuppressWarnings("unused")
    private User(){}



    /**
     * Accessor method for grantedAuthrities, used by ORM
     * 
     * @return Set of granted authorities
     */
    @SuppressWarnings("unused")
    private Set<Authority> getGrantedAuthorities()
    {
        return grantedAuthorities;
    }



    /**
     * Setter method for GrantedAuthorities
     * 
     * @param grantedAuthorities
     */
    public void setGrantedAuthorities(Set<Authority> grantedAuthorities)
    {
        this.grantedAuthorities = grantedAuthorities;
    }



    /* (non-Javadoc)
     * @see org.springframework.security.userdetails.UserDetails#getAuthorities()
     */
    public GrantedAuthority[] getAuthorities()
    {
        return grantedAuthorities.toArray(new GrantedAuthority[0]);
    }



    /* (non-Javadoc)
     * @see org.springframework.security.userdetails.UserDetails#isAccountNonExpired()
     */
    public boolean isAccountNonExpired()
    {
        return true;
    }

    /* (non-Javadoc)
     * @see org.springframework.security.userdetails.UserDetails#isAccountNonLocked()
     */
    public boolean isAccountNonLocked()
    {
        return true;
    }

    /* (non-Javadoc)
     * @see org.springframework.security.userdetails.UserDetails#isCredentialsNonExpired()
     */
    public boolean isCredentialsNonExpired()
    {
        return true;
    }

    /* (non-Javadoc)
     * @see org.springframework.security.userdetails.UserDetails#isEnabled()
     */
    public boolean isEnabled()
    {
        return enabled;
    }
    /* (non-Javadoc)
     * @see org.springframework.security.userdetails.UserDetails#getUsername()
     */
    public String getUsername()
    {
        return username;
    }



    /**
     * Setter method for username
     * 
     * @param username
     */
    @SuppressWarnings("unused")
    private void setUsername(String username)
    {
        this.username = username;
    }



    /* (non-Javadoc)
     * @see org.springframework.security.userdetails.UserDetails#getPassword()
     */
    public String getPassword()
    {
        return password;
    }



    /**
     * Setter method for password
     * 
     * @param password
     */
    public void setPassword(String password)
    {
        this.password = password;
    }
    
    /**
     * Setter method for enabled
     * 
     * @param enabled
     */
    public void setEnabled(boolean enabled){
        this.enabled = enabled;
    }
    
    /**
     * Allows an Authority to be granted to a User
     * 
     * @param authority
     */
    public void grantAuthority(Authority authority){
        if (grantedAuthorities.contains(authority)){
            throw new IllegalArgumentException("Authority ["+authority+"] is already granted to user ["+this+"]");
        }
        grantedAuthorities.add(authority);
    }
    
    /**
     * Removes an Authority from a user's granted authorities
     * 
     * @param authority
     */
    public void revokeAuthority(Authority authority){
        if (!grantedAuthorities.contains(authority)){
            throw new IllegalArgumentException("Authority ["+authority+"] has not been granted to user ["+this+"]");
        }
        grantedAuthorities.remove(authority);
    }
 
    /**
     * Accessor for id
     * 
     * @return id or null if non persisted
     * 
     */
    public Long getId()
    {
        return id;
    }

    /**
     * Setter for id, used by ORM
     * 
     * @param id
     */
    @SuppressWarnings("unused")
    private void setId(Long id)
    {
        this.id = id;
    }
    @Override
    public String toString(){
        StringBuffer sb = new StringBuffer("User [");
        sb.append("username=");sb.append(username);sb.append(", ");
        sb.append("enabled=");sb.append(enabled);
        sb.append("]");
        return sb.toString();
    }
    
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + (enabled ? 1231 : 1237);
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((password == null) ? 0 : password.hashCode());
        result = prime * result + ((username == null) ? 0 : username.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        User other = (User) obj;
        if (enabled != other.enabled) return false;
        if (id == null)
        {
            if (other.id != null) return false;
        }
        else if (!id.equals(other.id)) return false;
        if (password == null)
        {
            if (other.password != null) return false;
        }
        else if (!password.equals(other.password)) return false;
        if (username == null)
        {
            if (other.username != null) return false;
        }
        else if (!username.equals(other.username)) return false;
        return true;
    }


    
}
