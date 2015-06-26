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

import java.util.Date;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class AuthenticationMethod 
{
	private Long id;
	private String name = "";
	private String method = "";
	private Integer order;
	private Date lastSynchronised;
	private String ldapServerUrl = "";
	private String ldapBindUserDn = "";
	private String ldapBindUserPassword = "";
	private String ldapUserSearchBaseDn = "";
	private String applicationSecurityBaseDn = "";
	private String applicationSecurityGroupAttributeName = "sAMAccountName";
	private String ldapUserSearchFilter = "(sAMAccountName={0})";
	private String accountTypeAttributeName = "accountType";
	private String userAccountNameAttributeName = "sAMAccountName";
	private String emailAttributeName = "mail";
	private String firstNameAttributeName = "givenName";
	private String surnameAttributeName = "sn";
	private String departmentAttributeName = "department";
	private String ldapUserDescriptionAttributeName = "description";
	private String applicationSecurityDescriptionAttributeName = "description";
	private String memberofAttributeName = "memberOf";
	
	public AuthenticationMethod()
	{
		
	}
	
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
	
	/**
	 * @return the applicationSecurityBaseDn
	 */
	public String getApplicationSecurityBaseDn()
	{
		return applicationSecurityBaseDn;
	}
	
	/**
	 * @param applicationSecurityBaseDn the applicationSecurityBaseDn to set
	 */
	public void setApplicationSecurityBaseDn(String applicationSecurityBaseDn)
	{
		this.applicationSecurityBaseDn = applicationSecurityBaseDn;
	}
	
	/**
	 * @return the accountTypeAttributeName
	 */
	public String getAccountTypeAttributeName()
	{
		return accountTypeAttributeName;
	}
	
	/**
	 * @param accountTypeAttributeName the accountTypeAttributeName to set
	 */
	public void setAccountTypeAttributeName(String accountTypeAttributeName)
	{
		this.accountTypeAttributeName = accountTypeAttributeName;
	}
	
	/**
	 * @return the userAccountNameAttributeName
	 */
	public String getUserAccountNameAttributeName()
	{
		return userAccountNameAttributeName;
	}
	
	/**
	 * @param userAccountNameAttributeName the userAccountNameAttributeName to set
	 */
	public void setUserAccountNameAttributeName(String userAccountNameAttributeName)
	{
		this.userAccountNameAttributeName = userAccountNameAttributeName;
	}
	
	/**
	 * @return the emailAttributeName
	 */
	public String getEmailAttributeName()
	{
		return emailAttributeName;
	}
	
	/**
	 * @param emailAttributeName the emailAttributeName to set
	 */
	public void setEmailAttributeName(String emailAttributeName)
	{
		this.emailAttributeName = emailAttributeName;
	}
	
	/**
	 * @return the applicationSecurityGroupAttributeName
	 */
	public String getApplicationSecurityGroupAttributeName()
	{
		return applicationSecurityGroupAttributeName;
	}
	
	/**
	 * @param applicationSecurityGroupAttributeName the applicationSecurityGroupAttributeName to set
	 */
	public void setApplicationSecurityGroupAttributeName(
			String applicationSecurityGroupAttributeName)
	{
		this.applicationSecurityGroupAttributeName = applicationSecurityGroupAttributeName;
	}

	/**
	 * @return the firstNameAttributeName
	 */
	public String getFirstNameAttributeName()
	{
		return firstNameAttributeName;
	}

	/**
	 * @param firstNameAttributeName the firstNameAttributeName to set
	 */
	public void setFirstNameAttributeName(String firstNameAttributeName)
	{
		this.firstNameAttributeName = firstNameAttributeName;
	}

	/**
	 * @return the surnameAttributeName
	 */
	public String getSurnameAttributeName()
	{
		return surnameAttributeName;
	}

	/**
	 * @param surnameAttributeName the surnameAttributeName to set
	 */
	public void setSurnameAttributeName(String surnameAttributeName)
	{
		this.surnameAttributeName = surnameAttributeName;
	}

	/**
	 * @return the departmentAttributeName
	 */
	public String getDepartmentAttributeName()
	{
		return departmentAttributeName;
	}

	/**
	 * @param departmentAttributeName the departmentAttributeName to set
	 */
	public void setDepartmentAttributeName(String departmentAttributeName)
	{
		this.departmentAttributeName = departmentAttributeName;
	}

	/**
	 * @return the ldapUserDescriptionAttributeName
	 */
	public String getLdapUserDescriptionAttributeName()
	{
		return ldapUserDescriptionAttributeName;
	}

	/**
	 * @param ldapUserDescriptionAttributeName the ldapUserDescriptionAttributeName to set
	 */
	public void setLdapUserDescriptionAttributeName(
			String ldapUserDescriptionAttributeName)
	{
		this.ldapUserDescriptionAttributeName = ldapUserDescriptionAttributeName;
	}

	/**
	 * @return the applicationSecurityDescriptionAttributeName
	 */
	public String getApplicationSecurityDescriptionAttributeName()
	{
		return applicationSecurityDescriptionAttributeName;
	}

	/**
	 * @param applicationSecurityDescriptionAttributeName the applicationSecurityDescriptionAttributeName to set
	 */
	public void setApplicationSecurityDescriptionAttributeName(
			String applicationSecurityDescriptionAttributeName)
	{
		this.applicationSecurityDescriptionAttributeName = applicationSecurityDescriptionAttributeName;
	}

	/**
	 * @return the memberofAttributeName
	 */
	public String getMemberofAttributeName()
	{
		return memberofAttributeName;
	}

	/**
	 * @param memberofAttributeName the memberofAttributeName to set
	 */
	public void setMemberofAttributeName(String memberofAttributeName)
	{
		this.memberofAttributeName = memberofAttributeName;
	}

	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * @return the order
	 */
	public Integer getOrder()
	{
		return order;
	}

	/**
	 * @param order the order to set
	 */
	public void setOrder(Integer order)
	{
		this.order = order;
	}

	/**
	 * @return the lastSynchronised
	 */
	public Date getLastSynchronised()
	{
		return lastSynchronised;
	}

	/**
	 * @param lastSynchronised the lastSynchronised to set
	 */
	public void setLastSynchronised(Date lastSynchronised)
	{
		this.lastSynchronised = lastSynchronised;
	}

//	/* (non-Javadoc)
//	 * @see java.lang.Object#toString()
//	 */
//	@Override
//	public String toString()
//	{
//		return "AuthenticationMethod [id=" + id + ", name=" + name
//				+ ", method=" + method + ", order=" + order
//				+ ", lastSynchronised=" + lastSynchronised + ", ldapServerUrl="
//				+ ldapServerUrl + ", ldapBindUserDn=" + ldapBindUserDn
//				+ ", ldapBindUserPassword=" + ldapBindUserPassword
//				+ ", ldapUserSearchBaseDn=" + ldapUserSearchBaseDn
//				+ ", applicationSecurityBaseDn=" + applicationSecurityBaseDn
//				+ ", applicationSecurityGroupAttributeName="
//				+ applicationSecurityGroupAttributeName
//				+ ", ldapUserSearchFilter=" + ldapUserSearchFilter
//				+ ", accountTypeAttributeName=" + accountTypeAttributeName
//				+ ", userAccountNameAttributeName="
//				+ userAccountNameAttributeName + ", emailAttributeName="
//				+ emailAttributeName + ", firstNameAttributeName="
//				+ firstNameAttributeName + ", surnameAttributeName="
//				+ surnameAttributeName + ", departmentAttributeName="
//				+ departmentAttributeName
//				+ ", ldapUserDescriptionAttributeName="
//				+ ldapUserDescriptionAttributeName
//				+ ", applicationSecurityDescriptionAttributeName="
//				+ applicationSecurityDescriptionAttributeName
//				+ ", memberofAttributeName=" + memberofAttributeName + "]";
//	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((accountTypeAttributeName == null) ? 0
						: accountTypeAttributeName.hashCode());
		result = prime
				* result
				+ ((applicationSecurityBaseDn == null) ? 0
						: applicationSecurityBaseDn.hashCode());
		result = prime
				* result
				+ ((applicationSecurityDescriptionAttributeName == null) ? 0
						: applicationSecurityDescriptionAttributeName
								.hashCode());
		result = prime
				* result
				+ ((applicationSecurityGroupAttributeName == null) ? 0
						: applicationSecurityGroupAttributeName.hashCode());
		result = prime
				* result
				+ ((departmentAttributeName == null) ? 0
						: departmentAttributeName.hashCode());
		result = prime
				* result
				+ ((emailAttributeName == null) ? 0 : emailAttributeName
						.hashCode());
		result = prime
				* result
				+ ((firstNameAttributeName == null) ? 0
						: firstNameAttributeName.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime
				* result
				+ ((lastSynchronised == null) ? 0 : lastSynchronised.hashCode());
		result = prime * result
				+ ((ldapBindUserDn == null) ? 0 : ldapBindUserDn.hashCode());
		result = prime
				* result
				+ ((ldapBindUserPassword == null) ? 0 : ldapBindUserPassword
						.hashCode());
		result = prime * result
				+ ((ldapServerUrl == null) ? 0 : ldapServerUrl.hashCode());
		result = prime
				* result
				+ ((ldapUserDescriptionAttributeName == null) ? 0
						: ldapUserDescriptionAttributeName.hashCode());
		result = prime
				* result
				+ ((ldapUserSearchBaseDn == null) ? 0 : ldapUserSearchBaseDn
						.hashCode());
		result = prime
				* result
				+ ((ldapUserSearchFilter == null) ? 0 : ldapUserSearchFilter
						.hashCode());
		result = prime
				* result
				+ ((memberofAttributeName == null) ? 0 : memberofAttributeName
						.hashCode());
		result = prime * result + ((method == null) ? 0 : method.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((order == null) ? 0 : order.hashCode());
		result = prime
				* result
				+ ((surnameAttributeName == null) ? 0 : surnameAttributeName
						.hashCode());
		result = prime
				* result
				+ ((userAccountNameAttributeName == null) ? 0
						: userAccountNameAttributeName.hashCode());
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
		AuthenticationMethod other = (AuthenticationMethod) obj;
		if (accountTypeAttributeName == null)
		{
			if (other.accountTypeAttributeName != null)
				return false;
		} else if (!accountTypeAttributeName
				.equals(other.accountTypeAttributeName))
			return false;
		if (applicationSecurityBaseDn == null)
		{
			if (other.applicationSecurityBaseDn != null)
				return false;
		} else if (!applicationSecurityBaseDn
				.equals(other.applicationSecurityBaseDn))
			return false;
		if (applicationSecurityDescriptionAttributeName == null)
		{
			if (other.applicationSecurityDescriptionAttributeName != null)
				return false;
		} else if (!applicationSecurityDescriptionAttributeName
				.equals(other.applicationSecurityDescriptionAttributeName))
			return false;
		if (applicationSecurityGroupAttributeName == null)
		{
			if (other.applicationSecurityGroupAttributeName != null)
				return false;
		} else if (!applicationSecurityGroupAttributeName
				.equals(other.applicationSecurityGroupAttributeName))
			return false;
		if (departmentAttributeName == null)
		{
			if (other.departmentAttributeName != null)
				return false;
		} else if (!departmentAttributeName
				.equals(other.departmentAttributeName))
			return false;
		if (emailAttributeName == null)
		{
			if (other.emailAttributeName != null)
				return false;
		} else if (!emailAttributeName.equals(other.emailAttributeName))
			return false;
		if (firstNameAttributeName == null)
		{
			if (other.firstNameAttributeName != null)
				return false;
		} else if (!firstNameAttributeName.equals(other.firstNameAttributeName))
			return false;
		if (id == null)
		{
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (lastSynchronised == null)
		{
			if (other.lastSynchronised != null)
				return false;
		} else if (!lastSynchronised.equals(other.lastSynchronised))
			return false;
		if (ldapBindUserDn == null)
		{
			if (other.ldapBindUserDn != null)
				return false;
		} else if (!ldapBindUserDn.equals(other.ldapBindUserDn))
			return false;
		if (ldapBindUserPassword == null)
		{
			if (other.ldapBindUserPassword != null)
				return false;
		} else if (!ldapBindUserPassword.equals(other.ldapBindUserPassword))
			return false;
		if (ldapServerUrl == null)
		{
			if (other.ldapServerUrl != null)
				return false;
		} else if (!ldapServerUrl.equals(other.ldapServerUrl))
			return false;
		if (ldapUserDescriptionAttributeName == null)
		{
			if (other.ldapUserDescriptionAttributeName != null)
				return false;
		} else if (!ldapUserDescriptionAttributeName
				.equals(other.ldapUserDescriptionAttributeName))
			return false;
		if (ldapUserSearchBaseDn == null)
		{
			if (other.ldapUserSearchBaseDn != null)
				return false;
		} else if (!ldapUserSearchBaseDn.equals(other.ldapUserSearchBaseDn))
			return false;
		if (ldapUserSearchFilter == null)
		{
			if (other.ldapUserSearchFilter != null)
				return false;
		} else if (!ldapUserSearchFilter.equals(other.ldapUserSearchFilter))
			return false;
		if (memberofAttributeName == null)
		{
			if (other.memberofAttributeName != null)
				return false;
		} else if (!memberofAttributeName.equals(other.memberofAttributeName))
			return false;
		if (method == null)
		{
			if (other.method != null)
				return false;
		} else if (!method.equals(other.method))
			return false;
		if (name == null)
		{
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (order == null)
		{
			if (other.order != null)
				return false;
		} else if (!order.equals(other.order))
			return false;
		if (surnameAttributeName == null)
		{
			if (other.surnameAttributeName != null)
				return false;
		} else if (!surnameAttributeName.equals(other.surnameAttributeName))
			return false;
		if (userAccountNameAttributeName == null)
		{
			if (other.userAccountNameAttributeName != null)
				return false;
		} else if (!userAccountNameAttributeName
				.equals(other.userAccountNameAttributeName))
			return false;
		return true;
	}


}
