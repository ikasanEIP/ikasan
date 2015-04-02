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

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class AuthenticationMethod 
{
	private Long id;
	private String method;
	private String ldapServerUrl;
	private String ldapBindUserDn;
	private String ldapBindUserPassword;
	private String ldapUserSearchBaseDn;
	private String ldapUserSearchFilter;
	
	/**
	 * @return the id
	 */
	public Long getId()
	{
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(Long id)
	{
		this.id = id;
	}
	/**
	 * @return the method
	 */
	public String getMethod()
	{
		return method;
	}
	/**
	 * @param method the method to set
	 */
	public void setMethod(String method)
	{
		this.method = method;
	}
	/**
	 * @return the ldapServerUrl
	 */
	public String getLdapServerUrl()
	{
		return ldapServerUrl;
	}
	/**
	 * @param ldapServerUrl the ldapServerUrl to set
	 */
	public void setLdapServerUrl(String ldapServerUrl)
	{
		this.ldapServerUrl = ldapServerUrl;
	}
	/**
	 * @return the ldapBindUserDn
	 */
	public String getLdapBindUserDn()
	{
		return ldapBindUserDn;
	}
	/**
	 * @param ldapBindUserDn the ldapBindUserDn to set
	 */
	public void setLdapBindUserDn(String ldapBindUserDn)
	{
		this.ldapBindUserDn = ldapBindUserDn;
	}
	/**
	 * @return the ldapBindUserPassword
	 */
	public String getLdapBindUserPassword()
	{
		return ldapBindUserPassword;
	}
	/**
	 * @param ldapBindUserPassword the ldapBindUserPassword to set
	 */
	public void setLdapBindUserPassword(String ldapBindUserPassword)
	{
		this.ldapBindUserPassword = ldapBindUserPassword;
	}
	/**
	 * @return the ldapUserSearchBaseDn
	 */
	public String getLdapUserSearchBaseDn()
	{
		return ldapUserSearchBaseDn;
	}
	/**
	 * @param ldapUserSearchBaseDn the ldapUserSearchBaseDn to set
	 */
	public void setLdapUserSearchBaseDn(String ldapUserSearchBaseDn)
	{
		this.ldapUserSearchBaseDn = ldapUserSearchBaseDn;
	}
	/**
	 * @return the ldapUserSearchFilter
	 */
	public String getLdapUserSearchFilter()
	{
		return ldapUserSearchFilter;
	}
	/**
	 * @param ldapUserSearchFilter the ldapUserSearchFilter to set
	 */
	public void setLdapUserSearchFilter(String ldapUserSearchFilter)
	{
		this.ldapUserSearchFilter = ldapUserSearchFilter;
	}

	
}
