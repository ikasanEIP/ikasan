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
package org.ikasan.spec.security.model;

import java.util.Date;

/**
 * Represents an authentication method configuration for the Ikasan security framework.
 *
 * <p>Defines the properties and settings for various authentication mechanisms such as LDAP,
 * local database authentication, or other custom authentication providers. Each authentication
 * method can be configured with specific parameters for connecting to authentication sources,
 * mapping user attributes, and synchronizing security groups.
 *
 * @author Ikasan Development Team
 * @since 1.0
 */
public interface AuthenticationMethod
{
    Long getId();

    void setId(Long id);

    String getMethod();

    void setMethod(String method);

    String getLdapServerUrl();

    void setLdapServerUrl(String ldapServerUrl);

    String getLdapBindUserDn();

    void setLdapBindUserDn(String ldapBindUserDn);

    String getLdapBindUserPassword();

    void setLdapBindUserPassword(String ldapBindUserPassword);

    String getLdapUserSearchBaseDn();

    void setLdapUserSearchBaseDn(String ldapUserSearchBaseDn);

    String getLdapUserSearchFilter();

    void setLdapUserSearchFilter(String ldapUserSearchFilter);

    String getApplicationSecurityBaseDn();

    void setApplicationSecurityBaseDn(String applicationSecurityBaseDn);

    String getAccountTypeAttributeName();

    void setAccountTypeAttributeName(String accountTypeAttributeName);

    String getUserAccountMappingAttributeName();

    void setUserAccountMappingAttributeName(String userAccountMappingAttributeName);

    String getUserAccountNameAttributeName();

    void setUserAccountNameAttributeName(String userAccountNameAttributeName);

    String getEmailAttributeName();

    void setEmailAttributeName(String emailAttributeName);

    String getApplicationSecurityGroupAttributeName();

    void setApplicationSecurityGroupAttributeName(String applicationSecurityGroupAttributeName);

    String getFirstNameAttributeName();

    void setFirstNameAttributeName(String firstNameAttributeName);

    String getSurnameAttributeName();

    void setSurnameAttributeName(String surnameAttributeName);

    String getDepartmentAttributeName();

    void setDepartmentAttributeName(String departmentAttributeName);

    String getLdapUserDescriptionAttributeName();

    void setLdapUserDescriptionAttributeName(String ldapUserDescriptionAttributeName);

    String getApplicationSecurityDescriptionAttributeName();

    void setApplicationSecurityDescriptionAttributeName(String applicationSecurityDescriptionAttributeName);

    String getMemberofAttributeName();

    void setMemberofAttributeName(String memberofAttributeName);

    String getName();

    void setName(String name);

    Long getOrder();

    void setOrder(Long order);

    Date getLastSynchronised();

    void setLastSynchronised(Date lastSynchronised);

    boolean isEnabled();

    void setEnabled(boolean enabled);

    String getUserSynchronisationFilter();

    void setUserSynchronisationFilter(String userFilter);

    String getGroupSynchronisationFilter();

    void setGroupSynchronisationFilter(String groupFilter);

    boolean isScheduled();

    void setScheduled(boolean scheduled);

    String getSynchronisationCronExpression();

    void setSynchronisationCronExpression(String synchronisationCronExpression);
}
