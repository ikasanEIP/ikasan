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
    /**
     * Gets the unique identifier for this authentication method.
     *
     * @return the authentication method ID
     */
    Object getId();

    /**
     * Sets the unique identifier for this authentication method.
     *
     * @param id the authentication method ID
     */
    void setId(Object id);

    /**
     * Gets the authentication method type (e.g., "ldap", "local", "oauth").
     *
     * @return the authentication method type
     */
    String getMethod();

    /**
     * Sets the authentication method type (e.g., "ldap", "local", "oauth").
     *
     * @param method the authentication method type
     */
    void setMethod(String method);

    /**
     * Gets the LDAP server URL for directory service connections.
     * Typically, in the format {@code ldap://host:port} or {@code ldaps://host:port}.
     *
     * @return the LDAP server URL, or {@code null} if not configured
     */
    String getLdapServerUrl();

    /**
     * Sets the LDAP server URL for directory service connections.
     * Typically, in the format {@code ldap://host:port} or {@code ldaps://host:port}.
     *
     * @param ldapServerUrl the LDAP server URL
     */
    void setLdapServerUrl(String ldapServerUrl);

    /**
     * Gets the distinguished name (DN) of the user account used to bind to the LDAP server.
     * This account is used for authenticating the connection to perform user lookups.
     *
     * @return the LDAP bind user DN, or {@code null} if anonymous binding is used
     */
    String getLdapBindUserDn();

    /**
     * Sets the distinguished name (DN) of the user account used to bind to the LDAP server.
     * This account is used for authenticating the connection to perform user lookups.
     *
     * @param ldapBindUserDn the LDAP bind user DN
     */
    void setLdapBindUserDn(String ldapBindUserDn);

    /**
     * Gets the password for the LDAP bind user account.
     *
     * @return the LDAP bind user password, or {@code null} if not configured
     */
    String getLdapBindUserPassword();

    /**
     * Sets the password for the LDAP bind user account.
     *
     * @param ldapBindUserPassword the LDAP bind user password
     */
    void setLdapBindUserPassword(String ldapBindUserPassword);

    /**
     * Gets the base distinguished name (DN) from which to begin searching for users in the LDAP directory.
     * For example, {@code ou=users,dc=example,dc=com}.
     *
     * @return the LDAP user search base DN, or {@code null} if not configured
     */
    String getLdapUserSearchBaseDn();

    /**
     * Sets the base distinguished name (DN) from which to begin searching for users in the LDAP directory.
     * For example, {@code ou=users,dc=example,dc=com}.
     *
     * @param ldapUserSearchBaseDn the LDAP user search base DN
     */
    void setLdapUserSearchBaseDn(String ldapUserSearchBaseDn);

    /**
     * Gets the LDAP filter expression used to locate user entries.
     * For example, {@code (uid={0})} where {0} is replaced with the username.
     *
     * @return the LDAP user search filter, or {@code null} if not configured
     */
    String getLdapUserSearchFilter();

    /**
     * Sets the LDAP filter expression used to locate user entries.
     * For example, {@code (uid={0})} where {0} is replaced with the username.
     *
     * @param ldapUserSearchFilter the LDAP user search filter
     */
    void setLdapUserSearchFilter(String ldapUserSearchFilter);

    /**
     * Gets the base distinguished name (DN) for application security groups in the LDAP directory.
     * This is used when synchronizing security roles and permissions.
     *
     * @return the application security base DN, or {@code null} if not configured
     */
    String getApplicationSecurityBaseDn();

    /**
     * Sets the base distinguished name (DN) for application security groups in the LDAP directory.
     * This is used when synchronizing security roles and permissions.
     *
     * @param applicationSecurityBaseDn the application security base DN
     */
    void setApplicationSecurityBaseDn(String applicationSecurityBaseDn);

    /**
     * Gets the name of the LDAP attribute that specifies the account type.
     * This attribute helps distinguish between different types of accounts (e.g., user, service account).
     *
     * @return the account type attribute name, or {@code null} if not configured
     */
    String getAccountTypeAttributeName();

    /**
     * Sets the name of the LDAP attribute that specifies the account type.
     * This attribute helps distinguish between different types of accounts (e.g., user, service account).
     *
     * @param accountTypeAttributeName the account type attribute name
     */
    void setAccountTypeAttributeName(String accountTypeAttributeName);

    /**
     * Gets the name of the LDAP attribute used to map user accounts to application principals.
     * This attribute is used to correlate LDAP users with local application user accounts.
     *
     * @return the user account mapping attribute name, or {@code null} if not configured
     */
    String getUserAccountMappingAttributeName();

    /**
     * Sets the name of the LDAP attribute used to map user accounts to application principals.
     * This attribute is used to correlate LDAP users with local application user accounts.
     *
     * @param userAccountMappingAttributeName the user account mapping attribute name
     */
    void setUserAccountMappingAttributeName(String userAccountMappingAttributeName);

    /**
     * Gets the name of the LDAP attribute that contains the user's account name or login identifier.
     * Common values include {@code uid}, {@code sAMAccountName}, or {@code cn}.
     *
     * @return the user account name attribute name, or {@code null} if not configured
     */
    String getUserAccountNameAttributeName();

    /**
     * Sets the name of the LDAP attribute that contains the user's account name or login identifier.
     * Common values include {@code uid}, {@code sAMAccountName}, or {@code cn}.
     *
     * @param userAccountNameAttributeName the user account name attribute name
     */
    void setUserAccountNameAttributeName(String userAccountNameAttributeName);

    /**
     * Gets the name of the LDAP attribute that contains the user's email address.
     * Common values include {@code mail} or {@code email}.
     *
     * @return the email attribute name, or {@code null} if not configured
     */
    String getEmailAttributeName();

    /**
     * Sets the name of the LDAP attribute that contains the user's email address.
     * Common values include {@code mail} or {@code email}.
     *
     * @param emailAttributeName the email attribute name
     */
    void setEmailAttributeName(String emailAttributeName);

    /**
     * Gets the name of the LDAP attribute that identifies application security group membership.
     * This is used to determine which security roles a user belongs to.
     *
     * @return the application security group attribute name, or {@code null} if not configured
     */
    String getApplicationSecurityGroupAttributeName();

    /**
     * Sets the name of the LDAP attribute that identifies application security group membership.
     * This is used to determine which security roles a user belongs to.
     *
     * @param applicationSecurityGroupAttributeName the application security group attribute name
     */
    void setApplicationSecurityGroupAttributeName(String applicationSecurityGroupAttributeName);

    /**
     * Gets the name of the LDAP attribute that contains the user's first name or given name.
     * Common values include {@code givenName} or {@code firstName}.
     *
     * @return the first name attribute name, or {@code null} if not configured
     */
    String getFirstNameAttributeName();

    /**
     * Sets the name of the LDAP attribute that contains the user's first name or given name.
     * Common values include {@code givenName} or {@code firstName}.
     *
     * @param firstNameAttributeName the first name attribute name
     */
    void setFirstNameAttributeName(String firstNameAttributeName);

    /**
     * Gets the name of the LDAP attribute that contains the user's surname or last name.
     * Common values include {@code sn} or {@code lastName}.
     *
     * @return the surname attribute name, or {@code null} if not configured
     */
    String getSurnameAttributeName();

    /**
     * Sets the name of the LDAP attribute that contains the user's surname or last name.
     * Common values include {@code sn} or {@code lastName}.
     *
     * @param surnameAttributeName the surname attribute name
     */
    void setSurnameAttributeName(String surnameAttributeName);

    /**
     * Gets the name of the LDAP attribute that contains the user's department or organizational unit.
     * Common values include {@code department} or {@code ou}.
     *
     * @return the department attribute name, or {@code null} if not configured
     */
    String getDepartmentAttributeName();

    /**
     * Sets the name of the LDAP attribute that contains the user's department or organizational unit.
     * Common values include {@code department} or {@code ou}.
     *
     * @param departmentAttributeName the department attribute name
     */
    void setDepartmentAttributeName(String departmentAttributeName);

    /**
     * Gets the name of the LDAP attribute that contains descriptive information about a user account.
     * This is used to retrieve additional context about the user from the directory.
     *
     * @return the LDAP user description attribute name, or {@code null} if not configured
     */
    String getLdapUserDescriptionAttributeName();

    /**
     * Sets the name of the LDAP attribute that contains descriptive information about a user account.
     * This is used to retrieve additional context about the user from the directory.
     *
     * @param ldapUserDescriptionAttributeName the LDAP user description attribute name
     */
    void setLdapUserDescriptionAttributeName(String ldapUserDescriptionAttributeName);

    /**
     * Gets the name of the LDAP attribute that contains descriptive information about application security groups.
     * This is used to synchronize group descriptions from the directory.
     *
     * @return the application security description attribute name, or {@code null} if not configured
     */
    String getApplicationSecurityDescriptionAttributeName();

    /**
     * Sets the name of the LDAP attribute that contains descriptive information about application security groups.
     * This is used to synchronize group descriptions from the directory.
     *
     * @param applicationSecurityDescriptionAttributeName the application security description attribute name
     */
    void setApplicationSecurityDescriptionAttributeName(String applicationSecurityDescriptionAttributeName);

    /**
     * Gets the name of the LDAP attribute that lists the groups a user is a member of.
     * Common values include {@code memberOf} in Active Directory.
     *
     * @return the member-of attribute name, or {@code null} if not configured
     */
    String getMemberofAttributeName();

    /**
     * Sets the name of the LDAP attribute that lists the groups a user is a member of.
     * Common values include {@code memberOf} in Active Directory.
     *
     * @param memberofAttributeName the member-of attribute name
     */
    void setMemberofAttributeName(String memberofAttributeName);

    /**
     * Gets the display name for this authentication method.
     * This is used for identification in the user interface.
     *
     * @return the authentication method name
     */
    String getName();

    /**
     * Sets the display name for this authentication method.
     * This is used for identification in the user interface.
     *
     * @param name the authentication method name
     */
    void setName(String name);

    /**
     * Gets the order in which this authentication method should be evaluated.
     * Lower values indicate higher priority in the authentication chain.
     *
     * @return the authentication method order, or {@code null} if not specified
     */
    Long getOrder();

    /**
     * Sets the order in which this authentication method should be evaluated.
     * Lower values indicate higher priority in the authentication chain.
     *
     * @param order the authentication method order
     */
    void setOrder(Long order);

    /**
     * Gets the timestamp of the last successful synchronization with the authentication source.
     * This is used to track when user and group data was last updated from the directory.
     *
     * @return the last synchronization date, or {@code null} if never synchronized
     */
    Date getLastSynchronised();

    /**
     * Sets the timestamp of the last successful synchronization with the authentication source.
     * This is used to track when user and group data was last updated from the directory.
     *
     * @param lastSynchronised the last synchronization date
     */
    void setLastSynchronised(Date lastSynchronised);

    /**
     * Checks whether this authentication method is currently enabled and active.
     * Disabled authentication methods are skipped during authentication attempts.
     *
     * @return {@code true} if enabled, {@code false} otherwise
     */
    boolean isEnabled();

    /**
     * Sets whether this authentication method is currently enabled and active.
     * Disabled authentication methods are skipped during authentication attempts.
     *
     * @param enabled {@code true} to enable, {@code false} to disable
     */
    void setEnabled(boolean enabled);

    /**
     * Gets the LDAP filter expression used to select which users should be synchronized.
     * This allows filtering which user accounts are imported from the directory.
     * For example, {@code (objectClass=person)} or {@code (&(objectClass=user)(!(userAccountControl:1.2.840.113556.1.4.803:=2)))}.
     *
     * @return the user synchronization filter, or {@code null} if all users are synchronized
     */
    String getUserSynchronisationFilter();

    /**
     * Sets the LDAP filter expression used to select which users should be synchronized.
     * This allows filtering which user accounts are imported from the directory.
     * For example, {@code (objectClass=person)} or {@code (&(objectClass=user)(!(userAccountControl:1.2.840.113556.1.4.803:=2)))}.
     *
     * @param userFilter the user synchronization filter
     */
    void setUserSynchronisationFilter(String userFilter);

    /**
     * Gets the LDAP filter expression used to select which groups should be synchronized.
     * This allows filtering which security groups are imported from the directory.
     * For example, {@code (objectClass=group)} or {@code (cn=APP_*)}.
     *
     * @return the group synchronization filter, or {@code null} if all groups are synchronized
     */
    String getGroupSynchronisationFilter();

    /**
     * Sets the LDAP filter expression used to select which groups should be synchronized.
     * This allows filtering which security groups are imported from the directory.
     * For example, {@code (objectClass=group)} or {@code (cn=APP_*)}.
     *
     * @param groupFilter the group synchronization filter
     */
    void setGroupSynchronisationFilter(String groupFilter);

    /**
     * Checks whether automated synchronization is scheduled for this authentication method.
     * When enabled, user and group data is automatically synchronized according to the cron expression.
     *
     * @return {@code true} if scheduled synchronization is enabled, {@code false} otherwise
     */
    boolean isScheduled();

    /**
     * Sets whether automated synchronization is scheduled for this authentication method.
     * When enabled, user and group data is automatically synchronized according to the cron expression.
     *
     * @param scheduled {@code true} to enable scheduled synchronization, {@code false} to disable
     */
    void setScheduled(boolean scheduled);

    /**
     * Gets the cron expression that defines when automated synchronization should occur.
     * The expression follows standard cron format (e.g., {@code 0 0 2 * * ?} for 2 AM daily).
     *
     * @return the synchronization cron expression, or {@code null} if not configured
     */
    String getSynchronisationCronExpression();

    /**
     * Sets the cron expression that defines when automated synchronization should occur.
     * The expression follows standard cron format (e.g., {@code 0 0 2 * * ?} for 2 AM daily).
     *
     * @param synchronisationCronExpression the synchronization cron expression
     */
    void setSynchronisationCronExpression(String synchronisationCronExpression);
}
