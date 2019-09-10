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

import org.springframework.security.core.userdetails.UserDetails;

import java.security.Principal;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Implementation of <code>UserDetails</code> suitable for ORM
 * 
 * @author Ikasan Development Team
 * 
 */
public class UserLite
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

    /**
	 * Last time the user accessed the system
	 */
	private long previousAccessTimestamp;

    /**
     * Default constructor required by ORM
     */
    @SuppressWarnings("unused")
    public UserLite()
    {
        // Do Nothing - Default constructor required by ORM
    }

	/**
	 *
	 * @return
     */
    public boolean isAccountNonExpired()
    {
        return true;
    }

	/**
	 *
	 * @return
     */
    public boolean isAccountNonLocked()
    {
        return true;
    }

	/**
	 *
	 * @return
     */
    public boolean isCredentialsNonExpired()
    {
        return true;
    }

	/**
	 *
	 * @return
     */
    public boolean isEnabled()
    {
        return enabled;
    }


    /**
	 *
	 * @return
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
	 * @return the previousAccessTimestamp
	 */
	public long getPreviousAccessTimestamp()
	{
		return previousAccessTimestamp;
	}

	/**
	 * @param previousAccessTimestamp the previousAccessTimestamp to set
	 */
	public void setPreviousAccessTimestamp(long previousAccessTimestamp)
	{
		this.previousAccessTimestamp = previousAccessTimestamp;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		UserLite userLite = (UserLite) o;

		if (enabled != userLite.enabled) return false;
		if (previousAccessTimestamp != userLite.previousAccessTimestamp) return false;
		if (id != null ? !id.equals(userLite.id) : userLite.id != null) return false;
		if (username != null ? !username.equals(userLite.username) : userLite.username != null) return false;
		if (password != null ? !password.equals(userLite.password) : userLite.password != null) return false;
		if (email != null ? !email.equals(userLite.email) : userLite.email != null) return false;
		if (firstName != null ? !firstName.equals(userLite.firstName) : userLite.firstName != null) return false;
		if (surname != null ? !surname.equals(userLite.surname) : userLite.surname != null) return false;
		return department != null ? department.equals(userLite.department) : userLite.department == null;

	}

	@Override
	public int hashCode() {
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + (username != null ? username.hashCode() : 0);
		result = 31 * result + (password != null ? password.hashCode() : 0);
		result = 31 * result + (email != null ? email.hashCode() : 0);
		result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
		result = 31 * result + (surname != null ? surname.hashCode() : 0);
		result = 31 * result + (department != null ? department.hashCode() : 0);
		result = 31 * result + (enabled ? 1 : 0);
		result = 31 * result + (int) (previousAccessTimestamp ^ (previousAccessTimestamp >>> 32));
		return result;
	}

	/* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
	@Override
	public String toString()
	{
		return username;
	}	   
}
