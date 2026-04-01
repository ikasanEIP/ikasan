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

import org.ikasan.spec.security.model.AuthenticationMethod;

import java.util.Date;

/**
 * Test implementation of AuthenticationMethod for use in test scenarios.
 *
 * @author Ikasan Development Team
 */
public class AuthenticationMethodImpl implements AuthenticationMethod
{
    private Object id;
    private String method;
    private String ldapServerUrl;
    private String ldapBindUserDn;
    private String ldapBindUserPassword;
    private String ldapUserSearchBaseDn;
    private String ldapUserSearchFilter;
    private String applicationSecurityBaseDn;
    private String accountTypeAttributeName;
    private String userAccountMappingAttributeName;
    private String userAccountNameAttributeName;
    private String emailAttributeName;
    private String applicationSecurityGroupAttributeName;
    private String firstNameAttributeName;
    private String surnameAttributeName;
    private String departmentAttributeName;
    private String ldapUserDescriptionAttributeName;
    private String applicationSecurityDescriptionAttributeName;
    private String memberofAttributeName;
    private String name;
    private Long order;
    private Date lastSynchronised;
    private boolean enabled = true;
    private String userSynchronisationFilter;
    private String groupSynchronisationFilter;
    private boolean scheduled = false;
    private String synchronisationCronExpression;

    @Override
    public Object getId()
    {
        return id;
    }

    @Override
    public void setId(Object id)
    {
        this.id = id;
    }

    @Override
    public String getMethod()
    {
        return method;
    }

    @Override
    public void setMethod(String method)
    {
        this.method = method;
    }

    @Override
    public String getLdapServerUrl()
    {
        return ldapServerUrl;
    }

    @Override
    public void setLdapServerUrl(String ldapServerUrl)
    {
        this.ldapServerUrl = ldapServerUrl;
    }

    @Override
    public String getLdapBindUserDn()
    {
        return ldapBindUserDn;
    }

    @Override
    public void setLdapBindUserDn(String ldapBindUserDn)
    {
        this.ldapBindUserDn = ldapBindUserDn;
    }

    @Override
    public String getLdapBindUserPassword()
    {
        return ldapBindUserPassword;
    }

    @Override
    public void setLdapBindUserPassword(String ldapBindUserPassword)
    {
        this.ldapBindUserPassword = ldapBindUserPassword;
    }

    @Override
    public String getLdapUserSearchBaseDn()
    {
        return ldapUserSearchBaseDn;
    }

    @Override
    public void setLdapUserSearchBaseDn(String ldapUserSearchBaseDn)
    {
        this.ldapUserSearchBaseDn = ldapUserSearchBaseDn;
    }

    @Override
    public String getLdapUserSearchFilter()
    {
        return ldapUserSearchFilter;
    }

    @Override
    public void setLdapUserSearchFilter(String ldapUserSearchFilter)
    {
        this.ldapUserSearchFilter = ldapUserSearchFilter;
    }

    @Override
    public String getApplicationSecurityBaseDn()
    {
        return applicationSecurityBaseDn;
    }

    @Override
    public void setApplicationSecurityBaseDn(String applicationSecurityBaseDn)
    {
        this.applicationSecurityBaseDn = applicationSecurityBaseDn;
    }

    @Override
    public String getAccountTypeAttributeName()
    {
        return accountTypeAttributeName;
    }

    @Override
    public void setAccountTypeAttributeName(String accountTypeAttributeName)
    {
        this.accountTypeAttributeName = accountTypeAttributeName;
    }

    @Override
    public String getUserAccountMappingAttributeName()
    {
        return userAccountMappingAttributeName;
    }

    @Override
    public void setUserAccountMappingAttributeName(String userAccountMappingAttributeName)
    {
        this.userAccountMappingAttributeName = userAccountMappingAttributeName;
    }

    @Override
    public String getUserAccountNameAttributeName()
    {
        return userAccountNameAttributeName;
    }

    @Override
    public void setUserAccountNameAttributeName(String userAccountNameAttributeName)
    {
        this.userAccountNameAttributeName = userAccountNameAttributeName;
    }

    @Override
    public String getEmailAttributeName()
    {
        return emailAttributeName;
    }

    @Override
    public void setEmailAttributeName(String emailAttributeName)
    {
        this.emailAttributeName = emailAttributeName;
    }

    @Override
    public String getApplicationSecurityGroupAttributeName()
    {
        return applicationSecurityGroupAttributeName;
    }

    @Override
    public void setApplicationSecurityGroupAttributeName(String applicationSecurityGroupAttributeName)
    {
        this.applicationSecurityGroupAttributeName = applicationSecurityGroupAttributeName;
    }

    @Override
    public String getFirstNameAttributeName()
    {
        return firstNameAttributeName;
    }

    @Override
    public void setFirstNameAttributeName(String firstNameAttributeName)
    {
        this.firstNameAttributeName = firstNameAttributeName;
    }

    @Override
    public String getSurnameAttributeName()
    {
        return surnameAttributeName;
    }

    @Override
    public void setSurnameAttributeName(String surnameAttributeName)
    {
        this.surnameAttributeName = surnameAttributeName;
    }

    @Override
    public String getDepartmentAttributeName()
    {
        return departmentAttributeName;
    }

    @Override
    public void setDepartmentAttributeName(String departmentAttributeName)
    {
        this.departmentAttributeName = departmentAttributeName;
    }

    @Override
    public String getLdapUserDescriptionAttributeName()
    {
        return ldapUserDescriptionAttributeName;
    }

    @Override
    public void setLdapUserDescriptionAttributeName(String ldapUserDescriptionAttributeName)
    {
        this.ldapUserDescriptionAttributeName = ldapUserDescriptionAttributeName;
    }

    @Override
    public String getApplicationSecurityDescriptionAttributeName()
    {
        return applicationSecurityDescriptionAttributeName;
    }

    @Override
    public void setApplicationSecurityDescriptionAttributeName(String applicationSecurityDescriptionAttributeName)
    {
        this.applicationSecurityDescriptionAttributeName = applicationSecurityDescriptionAttributeName;
    }

    @Override
    public String getMemberofAttributeName()
    {
        return memberofAttributeName;
    }

    @Override
    public void setMemberofAttributeName(String memberofAttributeName)
    {
        this.memberofAttributeName = memberofAttributeName;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public void setName(String name)
    {
        this.name = name;
    }

    @Override
    public Long getOrder()
    {
        return order;
    }

    @Override
    public void setOrder(Long order)
    {
        this.order = order;
    }

    @Override
    public Date getLastSynchronised()
    {
        return lastSynchronised;
    }

    @Override
    public void setLastSynchronised(Date lastSynchronised)
    {
        this.lastSynchronised = lastSynchronised;
    }

    @Override
    public boolean isEnabled()
    {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    @Override
    public String getUserSynchronisationFilter()
    {
        return userSynchronisationFilter;
    }

    @Override
    public void setUserSynchronisationFilter(String userSynchronisationFilter)
    {
        this.userSynchronisationFilter = userSynchronisationFilter;
    }

    @Override
    public String getGroupSynchronisationFilter()
    {
        return groupSynchronisationFilter;
    }

    @Override
    public void setGroupSynchronisationFilter(String groupSynchronisationFilter)
    {
        this.groupSynchronisationFilter = groupSynchronisationFilter;
    }

    @Override
    public boolean isScheduled()
    {
        return scheduled;
    }

    @Override
    public void setScheduled(boolean scheduled)
    {
        this.scheduled = scheduled;
    }

    @Override
    public String getSynchronisationCronExpression()
    {
        return synchronisationCronExpression;
    }

    @Override
    public void setSynchronisationCronExpression(String synchronisationCronExpression)
    {
        this.synchronisationCronExpression = synchronisationCronExpression;
    }
}
