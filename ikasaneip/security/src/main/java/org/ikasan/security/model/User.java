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
package org.ikasan.security.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.userdetails.UserDetails;

/**
 * Implementation of <code>UserDetails</code> suitable for ORM
 * 
 * @author Ikasan Development Team
 * 
 */
public class User implements UserDetails
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

    /** The users firstname */
    private String firstName;
    
    /** The users surname */
    private String surname;
    
    /** The users department */
    private String department;

    /** Activation status for the user in the system */
    private boolean enabled;

    /** All <code>GrantedAuthrities</code> held by the user for the system */
    private Set<Authority> grantedAuthorities = new HashSet<Authority>();
    
    /** All <code>IkasanPrincipals</code> held by the owner for the system */
    private Set<IkasanPrincipal> principals;

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
    public Collection<? extends org.springframework.security.core.GrantedAuthority> getAuthorities()
    {
        return grantedAuthorities;
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

	/**
	 * @return the firstName
	 */
	public String getFirstName()
	{
		return firstName;
	}

	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	/**
	 * @return the surname
	 */
	public String getSurname()
	{
		return surname;
	}

	/**
	 * @param surname the surname to set
	 */
	public void setSurname(String surname)
	{
		this.surname = surname;
	}

	/**
	 * @return the department
	 */
	public String getDepartment()
	{
		return department;
	}

	/**
	 * @param department the department to set
	 */
	public void setDepartment(String department)
	{
		this.department = department;
	}

	/**
	 * @return the principals
	 */
	public Set<IkasanPrincipal> getPrincipals()
	{
		return principals;
	}

	/**
	 * @param principals the principals to set
	 */
	public void setPrincipals(Set<IkasanPrincipal> principals)
	{
		this.principals = principals;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((department == null) ? 0 : department.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + (enabled ? 1231 : 1237);
		result = prime * result
				+ ((firstName == null) ? 0 : firstName.hashCode());
		result = prime
				* result
				+ ((grantedAuthorities == null) ? 0 : grantedAuthorities
						.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((password == null) ? 0 : password.hashCode());
		result = prime * result
				+ ((principals == null) ? 0 : principals.hashCode());
		result = prime * result + ((surname == null) ? 0 : surname.hashCode());
		result = prime * result
				+ ((username == null) ? 0 : username.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (department == null)
		{
			if (other.department != null)
				return false;
		} else if (!department.equals(other.department))
			return false;
		if (email == null)
		{
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (enabled != other.enabled)
			return false;
		if (firstName == null)
		{
			if (other.firstName != null)
				return false;
		} else if (!firstName.equals(other.firstName))
			return false;
		if (grantedAuthorities == null)
		{
			if (other.grantedAuthorities != null)
				return false;
		} else if (!grantedAuthorities.equals(other.grantedAuthorities))
			return false;
		if (id == null)
		{
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (password == null)
		{
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (principals == null)
		{
			if (other.principals != null)
				return false;
		} else if (!principals.equals(other.principals))
			return false;
		if (surname == null)
		{
			if (other.surname != null)
				return false;
		} else if (!surname.equals(other.surname))
			return false;
		if (username == null)
		{
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "User [id=" + id + ", username=" + username + ", password="
				+ password + ", email=" + email + ", firstName=" + firstName
				+ ", surname=" + surname + ", department=" + department
				+ ", enabled=" + enabled + ", grantedAuthorities="
				+ grantedAuthorities + ", principals=" + principals + "]";
	}

	   
}
