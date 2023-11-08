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
package org.ikasan.security.service;

import org.apache.commons.lang.CharEncoding;
import org.ikasan.security.dao.SecurityDao;
import org.ikasan.security.dao.UserDao;
import org.ikasan.security.model.AuthenticationMethod;
import org.ikasan.security.model.IkasanPrincipal;
import org.ikasan.security.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.control.PagedResult;
import org.springframework.ldap.control.PagedResultsCookie;
import org.springframework.ldap.control.PagedResultsDirContextProcessor;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 
 * @author Ikasan Development Team
 * 
 */
public class LdapServiceImpl implements LdapService
{
	private static Logger logger = LoggerFactory.getLogger(LdapServiceImpl.class);
    private static final CharsetEncoder VALID_CHARSET_ENCODER = Charset.forName(CharEncoding.UTF_8).newEncoder();

	private SecurityDao securityDao;
	private UserDao userDao;
	private AuthenticationMethod authenticationMethod;
	
	/*
     * <code>PasswordEncoder</code> for encoding user passwords
     */
    private PasswordEncoder passwordEncoder;

	/**
	 * Constructor
	 *
	 * @param securityDao
	 * @param userDao
	 * @param passwordEncoder
     */
	public LdapServiceImpl(SecurityDao securityDao,
			UserDao userDao, PasswordEncoder passwordEncoder)
	{
		super();
		this.securityDao = securityDao;
		if (this.securityDao == null)
		{
			throw new IllegalArgumentException(
					"securityDao cannot be null!");
		}
		this.userDao = userDao;
		if (this.userDao == null)
		{
			throw new IllegalArgumentException("userDao cannot be null!");
		}
		this.passwordEncoder = passwordEncoder;
		if (this.userDao == null)
		{
			throw new IllegalArgumentException("passwordEncoder cannot be null!");
		}
	}

	
	protected LdapUser getLdapUser(String userName) throws LdapServiceException
	{		
		AuthenticationMethod authenticationMethod = this
				.getAuthenticationMethod();


		DefaultSpringSecurityContextSource contextSource = this.getContextSource();

		FilterBasedLdapUserSearch userSearch = new FilterBasedLdapUserSearch(
				authenticationMethod.getLdapUserSearchBaseDn(), "CN={0}",
				contextSource);
		
                DirContextOperations dir = null;
		try
		{
			dir = userSearch.searchForUser(userName);
		} 
		catch (UsernameNotFoundException e)
		{
			logger.warn("An exception occurred trying to search for LDAP user: " + e.getMessage());
			e.printStackTrace();
			return null;
		} 
		catch (RuntimeException e)
		{
			logger.warn("An exception occurred trying to search for LDAP user: " + e.getMessage());
			e.printStackTrace();
			return null;
		}

		String accountType = dir.getStringAttribute(authenticationMethod.getAccountTypeAttributeName());
		LdapUser user = null;
			
		String email = dir.getStringAttribute(authenticationMethod.getEmailAttributeName());
		String surname = dir.getStringAttribute(authenticationMethod.getSurnameAttributeName());
		String firstName = dir.getStringAttribute(authenticationMethod.getFirstNameAttributeName());
		
		String accountName = dir.getStringAttribute(authenticationMethod.getUserAccountNameAttributeName());
		
		if(accountName == null)
		{
			return null;
		}
		
		if (email == null || email.length() == 0)
		{
			email = "no email";
		}
		
		if (surname == null || surname.length() == 0)
		{
			surname = "no surname";
		}
		
		if (firstName == null || firstName.length() == 0)
		{
			firstName = "no firstname";
		}
		
		user = new LdapUser();
		user.accountName = accountName.toLowerCase();
		user.email = email;
		user.surname = surname;
		user.accountType = accountType;
		user.firstName = firstName;
		user.department = dir.getStringAttribute(authenticationMethod.getDepartmentAttributeName());
		user.description = dir.getStringAttribute(authenticationMethod.getLdapUserDescriptionAttributeName());
		user.memberOf = dir.getStringAttributes(authenticationMethod.getMemberofAttributeName());

		return user;
	}
	
	public List<String> getAllLdapUsers() throws LdapServiceException
	{
		 AuthenticationMethod authenticationMethod = this.getAuthenticationMethod();

		 DefaultSpringSecurityContextSource contextSource = this.getContextSource();
		 contextSource.setBase(authenticationMethod.getLdapUserSearchBaseDn());

		try
		{
			contextSource.afterPropertiesSet();
		} 
		catch (Exception e)
		{
			throw new LdapServiceException();
		}

		LdapTemplate ldapTemplate = new LdapTemplate(contextSource);

		// Get all groups in many paged results (needed for large numbers of
		// groups)
		PagedResultsCookie cookie = null;
		PagedResult result;

		List<String> results = new ArrayList<String>();

		do
		{
			result = getAllUsers(cookie, ldapTemplate);
			results.addAll(new ArrayList(result.getResultList()));
			cookie = result.getCookie();
		} 
		while (cookie.getCookie() != null);

		logger.debug("Returning users: " + results.size());
		return results;
	}

	protected PagedResult getAllUsers(PagedResultsCookie cookie,
			LdapTemplate ldapTemplate)
	{
		PagedResultsDirContextProcessor contextProcessor = new PagedResultsDirContextProcessor(
				200, cookie);
		SearchControls searchControls = new SearchControls();
		searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

		List<?> groups = ldapTemplate.search("",
				this.authenticationMethod.getUserSynchronisationFilter(), searchControls, new ApplicationUserAttributeMapper(),
				contextProcessor);

		return new PagedResult(groups, contextProcessor.getCookie());
	}

	public List<String> getAllApplicationSecurity() throws LdapServiceException
	{
		 AuthenticationMethod authenticationMethod = this.getAuthenticationMethod();

		 DefaultSpringSecurityContextSource contextSource = this.getContextSource();
		 contextSource.setBase(authenticationMethod.getApplicationSecurityBaseDn());

		try
		{
			contextSource.afterPropertiesSet();
		} 
		catch (Exception e)
		{
			throw new LdapServiceException();
		}

		LdapTemplate ldapTemplate = new LdapTemplate(contextSource);

		// Get all groups in many paged results (needed for large numbers of
		// groups)
		PagedResultsCookie cookie = null;
		PagedResult result;

		List<String> results = new ArrayList<String>();

		do
		{
			result = getAllGroups(cookie, ldapTemplate);
			results.addAll(new ArrayList(result.getResultList()));
			cookie = result.getCookie();
		} 
		while (cookie.getCookie() != null);

		return results;
	}

	protected PagedResult getAllGroups(PagedResultsCookie cookie,
			LdapTemplate ldapTemplate)
	{
		PagedResultsDirContextProcessor contextProcessor = new PagedResultsDirContextProcessor(
				200, cookie);
		SearchControls searchControls = new SearchControls();
		searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

		List<?> groups = ldapTemplate.search("",
				this.authenticationMethod.getGroupSynchronisationFilter(), 
				searchControls, new ApplicationSecurityGroupAttributeMapper(),
				contextProcessor);

		return new PagedResult(groups, contextProcessor.getCookie());
	}

	public IkasanPrincipal getApplicationSecurity(String userName)
			throws LdapServiceException
	{		
		AuthenticationMethod authenticationMethod = this.getAuthenticationMethod();

		DefaultSpringSecurityContextSource contextSource = this.getContextSource();

		FilterBasedLdapUserSearch userSearch = new FilterBasedLdapUserSearch(
				authenticationMethod.getApplicationSecurityBaseDn(), "CN={0}", contextSource);

		DirContextOperations dir = null;
		try
		{
			dir = userSearch.searchForUser(userName);
		} 
		catch (UsernameNotFoundException e)
		{
			return null;
		} 
		catch (RuntimeException e)
		{
			throw new LdapServiceException(e);
		}

		String accountName = dir.getStringAttribute(authenticationMethod.getApplicationSecurityGroupAttributeName());
		String description = dir.getStringAttribute(authenticationMethod.getApplicationSecurityDescriptionAttributeName());

		IkasanPrincipal principal = null;

		if (accountName != null && accountName.length() > 0)
		{
			principal = new IkasanPrincipal();
			principal.setName(accountName);
			principal.setType("application");
			
			if(description != null && description.length() > 0)
			{
				principal.setDescription(description);
			}
			else
			{
				principal.setDescription("No description");
			}
		}

		return principal;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ikasan.security.service.LdapService#synchronize()
	 */
	@Override
	public void synchronize(AuthenticationMethod authenticationMethod) throws LdapServiceException
	{
		this.authenticationMethod = authenticationMethod;
		
		List<String> applicationSecurities = getAllApplicationSecurity();
		
		for (String applicationSecurity : applicationSecurities)
		{
			IkasanPrincipal principal = securityDao
					.getPrincipalByName(applicationSecurity);

			if (principal == null)
			{
				principal = getApplicationSecurity(applicationSecurity);
			}

			if(principal != null)
			{
                principal.setApplicationSecurityBaseDn(this.authenticationMethod.getApplicationSecurityBaseDn());
				this.securityDao.saveOrUpdatePrincipal(principal);
			}
		}

		List<String> users = getAllLdapUsers();


		for (String username : users)
		{
            LdapUser ldapUser = null;
		    try
            {
                ldapUser = getLdapUser(username);

                if (ldapUser == null)
                {
                    continue;
                }

                if(!isValidEncoding(ldapUser))
                {
                    logger.warn("User[%s] contains an unsupported character encoding, skipping.".formatted(ldapUser));
                    continue;
                }

                List<IkasanPrincipal> ikasanPrincipals = new ArrayList<IkasanPrincipal>();
                User user = userDao.getUser(ldapUser.accountName);

                if (user == null)
                {
                    // Setting a default password. Need to think about forcing the user to change it,
                    String encodedPassword = passwordEncoder.encode("pa55word");

                    user = new User(ldapUser.accountName, encodedPassword, ldapUser.email, true);
                    user.setDepartment(ldapUser.department);
                    user.setFirstName(ldapUser.firstName);
                    user.setSurname(ldapUser.surname);
                    user.setPrincipals(new HashSet<>(ikasanPrincipals));

                    this.userDao.save(user);

                    user = userDao.getUser(ldapUser.accountName);
                }

                IkasanPrincipal principal = securityDao
                    .getPrincipalByName(ldapUser.accountName);
                if (principal == null)
                {
                    principal = new IkasanPrincipal();
                    principal.setName(ldapUser.accountName);
                    principal.setType("user");
                    if (ldapUser.description == null)
                    {
                        principal.setDescription("No description");
                    } else
                    {
                        principal.setDescription(ldapUser.description);
                    }

                    securityDao.saveOrUpdatePrincipal(principal);
                }

                ikasanPrincipals.add(principal);

                if (ldapUser.memberOf != null)
                {
                    for (String name : ldapUser.memberOf)
                    {
                        if (name.contains(this.getAuthenticationMethod().getApplicationSecurityBaseDn()))
                        {
                            DistinguishedName dn = new DistinguishedName(name);
                            String cn = dn.getValue("cn");

                            principal = this.securityDao.getPrincipalByName(cn);

                            if (principal != null)
                            {
                                ikasanPrincipals.add(principal);
                            }
                        }
                    }
                }

                user.setEmail(ldapUser.email);
                user.setFirstName(ldapUser.firstName);
                user.setSurname(ldapUser.surname);
                user.setDepartment(ldapUser.department);

                Set<IkasanPrincipal> userPrincipals = user.getPrincipals();

                if(userPrincipals == null) {
                    userPrincipals = new HashSet<>();
                }

                userPrincipals = userPrincipals.stream()
                    .filter(up -> up.getApplicationSecurityBaseDn() != null && !up.getApplicationSecurityBaseDn().equals(this.authenticationMethod.getApplicationSecurityBaseDn()))
                    .collect(Collectors.toSet());

                userPrincipals.addAll(ikasanPrincipals);

                user.setPrincipals(userPrincipals);

                this.userDao.save(user);
            }
            catch (Exception e)
            {
                logger.warn("An error has occurred attempting to synchronise user[%s] , with error message[%s]".formatted(ldapUser, e.getMessage()), e);
            }
		}
	}

    protected boolean isValidEncoding(LdapUser ldapUser){

        if(ldapUser.accountName != null && !VALID_CHARSET_ENCODER.canEncode(ldapUser.accountName)){
            logger.warn("User[%s] has character encoding issue for accountName='%s'".formatted(ldapUser.accountName, ldapUser.accountName));
            return false;
        }

        if(ldapUser.firstName != null && !VALID_CHARSET_ENCODER.canEncode(ldapUser.firstName)){
            logger.warn("User[%s] has character encoding issue for firstName='%s'".formatted(ldapUser.accountName, ldapUser.firstName));
            return false;
        }

        if(ldapUser.surname != null && !VALID_CHARSET_ENCODER.canEncode(ldapUser.surname)){
            logger.warn("User[%s] has character encoding issue for surname='%s'".formatted(ldapUser.accountName, ldapUser.surname));
            return false;
        }

        if(ldapUser.email != null && !VALID_CHARSET_ENCODER.canEncode(ldapUser.email)){
            logger.warn("User[%s] has character encoding issue for email='%s'".formatted(ldapUser.accountName, ldapUser.email));
            return false;
        }

        if(ldapUser.description != null && !VALID_CHARSET_ENCODER.canEncode(ldapUser.description)){
            logger.warn("User[%s] has character encoding issue for description='%s'".formatted(ldapUser.accountName, ldapUser.description));
            return false;
        }

        if(ldapUser.department != null && !VALID_CHARSET_ENCODER.canEncode(ldapUser.department)){
            logger.warn("User[%s] has character encoding issue for department='%s'".formatted(ldapUser.accountName, ldapUser.department));
            return false;
        }

        if (ldapUser.memberOf != null) {
            for (String group : ldapUser.memberOf) {
                if (!VALID_CHARSET_ENCODER.canEncode(group)) {
                    logger.warn("User[%s] has character encoding issue for memberOf.group='%s'".formatted(ldapUser.accountName, group));
                    return false;
                }
            }
        }

        return true;
    }

	protected AuthenticationMethod getAuthenticationMethod()
			throws LdapServiceException
	{		
		if(this.authenticationMethod == null)
		 {
			 throw new	LdapServiceException("Null AuthenticationMethod!");
		 }
		
		return this.authenticationMethod;
	}
	
	protected DefaultSpringSecurityContextSource getContextSource() throws LdapServiceException
	{
		DefaultSpringSecurityContextSource contextSource = new DefaultSpringSecurityContextSource(
					authenticationMethod.getLdapServerUrl());
		contextSource.setUserDn(authenticationMethod.getLdapBindUserDn());
		contextSource.setPassword(authenticationMethod.getLdapBindUserPassword());
		
		try
		{
			contextSource.afterPropertiesSet();
		} 
		catch (Exception e)
		{
			throw new LdapServiceException();
		}

		return contextSource;
	}
	
	protected class ApplicationSecurityGroupAttributeMapper implements AttributesMapper
	{
		@Override
		public Object mapFromAttributes(Attributes attributes)
				throws NamingException
		{
			return attributes.get(authenticationMethod.getApplicationSecurityGroupAttributeName()).get();
		}
	}
	
	protected class ApplicationUserAttributeMapper implements AttributesMapper
	{
		@Override
		public Object mapFromAttributes(Attributes attributes)
				throws NamingException
		{			
			return attributes.get("name").get();
		}
	}

	protected class LdapUser
	{
		String accountType;
		String accountName;
		String email;
		String firstName;
		String surname;
		String department;
		String description;
		String[] memberOf;
		
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString()
		{
			return "LdapUser [accountType=" + accountType + ", accountName="
					+ accountName + ", email=" + email + ", firstName="
					+ firstName + ", surname=" + surname + ", department="
					+ department + ", description=" + description
					+ ", memberOf=" + Arrays.toString(memberOf) + "]";
		}
	}
}
