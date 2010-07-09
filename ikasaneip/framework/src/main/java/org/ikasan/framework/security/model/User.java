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
public class User implements UserDetails, Comparable<User>
{
    /** serialVersionUID */
    private static final long serialVersionUID = 8975017088981341914L;

    /** Id field utilised by ORM */
    private Long id;

    /** Users username for the system */
    private String username;

    /** Users password for the system */
    private String password;

    /** Users email address for the system */
    private String email;

    /** Activation status for the user in the system */
    private boolean enabled;

    /** All <code>GrantedAuthrities</code> held by the user for the system */
    private Set<Authority> grantedAuthorities = new HashSet<Authority>();

    /**
     * Constructor
     * 
     * @param username
     * @param password
     * @param email
     * @param enabled
     */
    public User(String username, String password, String email, boolean enabled)
    {
        this.username = username;
        this.password = password;
        this.email = email;
        this.enabled = enabled;
    }

    /**
     * Default constructor required by ORM
     */
    @SuppressWarnings("unused")
    private User()
    {
        // Do Nothing - Default constructor required by ORM
    }

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

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.security.userdetails.UserDetails#getAuthorities()
     */
    public GrantedAuthority[] getAuthorities()
    {
        return grantedAuthorities.toArray(new GrantedAuthority[0]);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.security.userdetails.UserDetails#isAccountNonExpired ()
     */
    public boolean isAccountNonExpired()
    {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.security.userdetails.UserDetails#isAccountNonLocked()
     */
    public boolean isAccountNonLocked()
    {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.security.userdetails.UserDetails#isCredentialsNonExpired ()
     */
    public boolean isCredentialsNonExpired()
    {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.security.userdetails.UserDetails#isEnabled()
     */
    public boolean isEnabled()
    {
        return enabled;
    }

    /*
     * (non-Javadoc)
     * 
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
    public void setUsername(String username)
    {
        this.username = username;
    }

    /*
     * (non-Javadoc)
     * 
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
     * Accessor method for email
     * 
     * @return email
     */
    public String getEmail()
    {
        return email;
    }

    /**
     * Setter method for email address
     * 
     * @param email
     */
    public void setEmail(String email)
    {
        this.email = email;
    }
    
    /**
     * Setter method for enabled
     * 
     * @param enabled
     */
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    /**
     * Allows an Authority to be granted to a User
     * 
     * @param authority
     */
    public void grantAuthority(Authority authority)
    {
        if (grantedAuthorities.contains(authority))
        {
            throw new IllegalArgumentException("Authority [" + authority + "] is already granted to user [" + this
                    + "]");
        }
        grantedAuthorities.add(authority);
    }

    /**
     * Removes an Authority from a user's granted authorities
     * 
     * @param authority
     */
    public void revokeAuthority(Authority authority)
    {
        if (!grantedAuthorities.contains(authority))
        {
            throw new IllegalArgumentException("Authority [" + authority + "] has not been granted to user [" + this
                    + "]");
        }
        grantedAuthorities.remove(authority);
    }

    /**
     * Accessor for id
     * 
     * @return id or null if non persisted
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
    public String toString()
    {
        StringBuffer sb = new StringBuffer("User [");
        sb.append("username=");
        sb.append(username);
        sb.append(", ");
        sb.append("email=");
        sb.append(email);
        sb.append(", ");
        sb.append("enabled=");
        sb.append(enabled);
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
        result = prime * result + ((email == null) ? 0 : email.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        User other = (User) obj;
        if (enabled != other.enabled)
        {
            return false;
        }
        if (id == null)
        {
            if (other.id != null)
            {
                return false;
            }
        }
        else if (!id.equals(other.id))
        {
            return false;
        }
        if (password == null)
        {
            if (other.password != null)
            {
                return false;
            }
        }
        else if (!password.equals(other.password))
        {
            return false;
        }
        if (username == null)
        {
            if (other.username != null)
            {
                return false;
            }
        }
        else if (!username.equals(other.username))
        {
            return false;
        }
        if (email == null)
        {
            if (other.email != null)
            {
                return false;
            }
        }
        else if (!email.equals(other.email))
        {
            return false;
        }

        return true;
    }
    
    /**
     * Comparisons between user objects is based on alphabetical ordering
     * of usernames
     * 
     * Note: Maybe extend the possible comparisons/
     * refactor to a separate comparator
     */
    public int compareTo(User user)
    {
        return this.getUsername().compareTo(user.getUsername());
    }
}
