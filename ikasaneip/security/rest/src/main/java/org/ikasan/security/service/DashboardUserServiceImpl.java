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

import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.ikasan.security.model.Policy;
import org.ikasan.security.model.User;
import org.ikasan.security.model.UserFilter;
import org.ikasan.security.model.UserLite;
import org.ikasan.security.service.dto.JwtRequest;
import org.ikasan.security.service.dto.JwtResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.http.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of the <code>UserService</code> utilising Dashboard
 *
 * @author Ikasan Development Team
 */
public class DashboardUserServiceImpl implements UserService
{
    protected static final String SERVICE_USER_PATH = "/rest/user?username={username}";

    protected static final String SERVICE_USERS_PATH = "/rest/users";

    protected static final String MODULE_NAME_PROPERTY = "module.name";

    protected static final String DASHBOARD_BASE_URL_PROPERTY = "ikasan.dashboard.extract.base.url";

    protected static final String DASHBOARD_EXTRACT_ENABLED_PROPERTY = "ikasan.dashboard.extract.enabled";

    protected static final String DASHBOARD_AUTHENTICATION_USER_CREDENTIAL_CACHE_TIMEOUT_SECONDS
        = "ikasan.dashboard.authentication.user.credential.cache.timeout.seconds";

    Logger logger = LoggerFactory.getLogger(DashboardUserServiceImpl.class);

    private RestTemplate restTemplate;

    private String baseUrl;

    private String authenticateUrl;

    private String moduleName;

    private String token;

    private boolean isEnabled;

    private int userCredentialCacheTimeoutSeconds = 300;

    private Cache<String, JwtRequest> userCredentialCache;

    /**
     * Constructor
     *
     * @param environment
     */
    public DashboardUserServiceImpl(Environment environment)
    {
        super();
        this.restTemplate = new RestTemplate();
        MappingJackson2HttpMessageConverter jsonHttpMessageConverter = new MappingJackson2HttpMessageConverter();
        jsonHttpMessageConverter.getObjectMapper().configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        restTemplate.getMessageConverters().add(jsonHttpMessageConverter);
        isEnabled = Boolean.valueOf(environment.getProperty(DASHBOARD_EXTRACT_ENABLED_PROPERTY, "false"));
        if (isEnabled)
        {
            this.baseUrl = environment.getProperty(DASHBOARD_BASE_URL_PROPERTY);
            this.authenticateUrl = environment.getProperty(DASHBOARD_BASE_URL_PROPERTY) + "/authenticate";
            this.moduleName = environment.getProperty(MODULE_NAME_PROPERTY);

            if(environment.containsProperty(DASHBOARD_AUTHENTICATION_USER_CREDENTIAL_CACHE_TIMEOUT_SECONDS)) {
                this.userCredentialCacheTimeoutSeconds = environment.getProperty(DASHBOARD_AUTHENTICATION_USER_CREDENTIAL_CACHE_TIMEOUT_SECONDS, Integer.class);
            }

            this.userCredentialCache = CacheBuilder.newBuilder()
                .expireAfterAccess(userCredentialCacheTimeoutSeconds,TimeUnit.SECONDS)
                .build();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.ikasan.framework.security.service.UserService#getUsers()
     */
    public List<User> getUsers()
    {
        HttpHeaders headers = createHttpHeaders(true);
        HttpEntity entity = new HttpEntity(headers);
        try
        {
            ResponseEntity<List<User>> users = restTemplate
                .exchange(baseUrl + SERVICE_USERS_PATH, HttpMethod.GET, entity,
                    new ParameterizedTypeReference<List<User>>(){});
            return users.getBody();
        }
        catch (RestClientException e)
        {
            throw new UsernameNotFoundException("Unknown username : ");
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.ikasan.framework.security.service.UserService#getUserLites()
     */
    public List<UserLite> getUserLites()
    {
        throw new UnsupportedOperationException("Not Supported operation.");
    }

    /*
     * (non-Javadoc)
     *
     * @see org.springframework.security.userdetails.UserDetailsManager#changePassword(java.lang.String,
     * java.lang.String)
     */
    public void changePassword(String oldPassword, String newPassword)
    {
        throw new UnsupportedOperationException(
            "As administrators can change passwords for other users we have our own userChangePasssword method.");
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.springframework.security.userdetails.UserDetailsManager#createUser(org.springframework.security.userdetails
     * .UserDetails)
     */
    public void createUser(UserDetails userDetails)
    {
        throw new UnsupportedOperationException("Not Supported operation.");
    }

    /*
     * (non-Javadoc)
     *
     * @see org.springframework.security.userdetails.UserDetailsManager#deleteUser(java.lang.String)
     */
    public void deleteUser(String username)
    {
        throw new UnsupportedOperationException("Not Supported operation.");
    }

    /*
     * (non-Javadoc)
     *
     * @see org.ikasan.framework.security.service.UserService#disableUser(java.lang.String)
     */
    public void disableUser(String username)
    {
        throw new UnsupportedOperationException("Not Supported operation.");
    }

    /*
     * (non-Javadoc)
     *
     * @see org.ikasan.framework.security.service.UserService#enableUser(java.lang.String)
     */
    public void enableUser(String username)
    {
        throw new UnsupportedOperationException("Not Supported operation.");
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.springframework.security.userdetails.UserDetailsManager#updateUser(org.springframework.security.userdetails
     * .UserDetails)
     */
    public void updateUser(UserDetails userDetails)
    {
        throw new UnsupportedOperationException("Not Supported operation.");
    }

    /*
     * (non-Javadoc)
     *
     * @see org.springframework.security.userdetails.UserDetailsManager#userExists(java.lang.String)
     */
    public boolean userExists(String username)
    {
        throw new UnsupportedOperationException("Not Supported operation.");
    }

    @Override
    public List<UserLite> getUsersWithRole(String roleName, UserFilter userFilter, int limit, int offset) {
        throw new UnsupportedOperationException("Not Supported operation.");
    }

    @Override
    public int getUsersWithRoleCount(String roleName, UserFilter userFilter) {
        throw new UnsupportedOperationException("Not Supported operation.");
    }

    @Override
    public List<UserLite> getUsersWithoutRole(String roleName, UserFilter userFilter, int limit, int offset) {
        throw new UnsupportedOperationException("Not Supported operation.");
    }

    @Override
    public int getUsersWithoutRoleCount(String roleName, UserFilter userFilter) {
        throw new UnsupportedOperationException("Not Supported operation.");
    }

    @Override
    public int getUserCount(UserFilter userFilter) {
        throw new UnsupportedOperationException("Not Supported operation.");
    }

    @Override
    public List<User> getUsers(UserFilter userFilter, int limit, int offset) {
        throw new UnsupportedOperationException("Not Supported operation.");
    }

    @Override
    public List<UserLite> getUserLites(int limit, int offset) {
        throw new UnsupportedOperationException("Not Supported operation.");
    }

    /*
     * (non-Javadoc)
     *
     * @see org.ikasan.framework.security.service.UserService#loadUserByUsername(java.lang.String)
     */
    public User loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException
    {
        HttpHeaders headers = createHttpHeaders(true);
        HttpEntity entity = new HttpEntity(headers);
        Map<String, String> params = new HashMap<String, String>();
        params.put("username", username);
        try
        {
            ResponseEntity<User> user = restTemplate
                .exchange(baseUrl + SERVICE_USER_PATH, HttpMethod.GET, entity, User.class, params);
            if (user.getBody() == null)
            {
                throw new UsernameNotFoundException("Unknown username : " + username);
            }
            if (!user.getBody().isEnabled())
            {
                throw new UsernameNotFoundException("Given user: " + username + " is disabled. Contact administrator.");
            }

            return user.getBody();
        }
        catch (HttpClientErrorException e)
        {
            throw new UsernameNotFoundException("Unknown username : " + username);
        }
        catch (RestClientException e)
        {
            throw new UsernameNotFoundException("Unknown username : " + username);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.ikasan.framework.security.service.UserService#getAuthorities()
     */
    public List<Policy> getAuthorities()
    {
        //        throw new UnsupportedOperationException("Not Supported operation.");
        return new ArrayList<>();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.ikasan.framework.security.service.UserService#grantAuthority(java.lang.String, java.lang.String)
     */
    public void grantAuthority(String username, String authority)
    {
        throw new UnsupportedOperationException("Not Supported operation.");
    }

    /*
     * (non-Javadoc)
     *
     * @see org.ikasan.framework.security.service.UserService#revokeAuthority(java.lang.String, java.lang.String)
     */
    public void revokeAuthority(String username, String authority)
    {
        throw new UnsupportedOperationException("Not Supported operation.");
    }

    /*
     * (non-Javadoc)
     *
     * @see org.ikasan.framework.security.service.UserService#changeUsersPassword(java.lang.String, java.lang.String)
     */
    public void changeUsersPassword(String username, String newPassword, String confirmNewPassword)
        throws IllegalArgumentException
    {
        throw new UnsupportedOperationException("Not Supported operation.");
    }

    /*
     * (non-Javadoc)
     *
     * @see org.ikasan.framework.security.service.UserService#changeUsersEmail(java.lang.String, java.lang.String)
     */
    public void changeUsersEmail(String username, String newEmail) throws IllegalArgumentException
    {
        throw new UnsupportedOperationException("Not Supported operation.");
    }

    /* (non-Javadoc)
     * @see org.ikasan.security.service.UserService#getUserByUsernameLike(java.lang.String)
     */
    @Override
    public List<User> getUserByUsernameLike(String username)
    {
        throw new UnsupportedOperationException("Not Supported operation.");
    }

    /* (non-Javadoc)
     * @see org.ikasan.security.service.UserService#getUserByFirstnameLike(java.lang.String)
     */
    @Override
    public List<User> getUserByFirstnameLike(String firstname)
    {
        throw new UnsupportedOperationException("Not Supported operation.");
    }

    /* (non-Javadoc)
     * @see org.ikasan.security.service.UserService#getUserBySurnameLike(java.lang.String)
     */
    @Override
    public List<User> getUserBySurnameLike(String surname)
    {
        throw new UnsupportedOperationException("Not Supported operation.");
    }

    public boolean authenticate(String username, String password)
    {
        JwtRequest jwtRequest = new JwtRequest(username, password);

        if(this.token != null) {
            JwtRequest cachedJwtRequest = this.userCredentialCache.getIfPresent(this.token);
            if (cachedJwtRequest != null && cachedJwtRequest.equals(jwtRequest)) {
                return true;
            }
        }

        HttpEntity<JwtRequest> entity = new HttpEntity(new JwtRequest(username, password), createHttpHeaders(false));
        try
        {
            if (username != null && password != null)
            {
                ResponseEntity<JwtResponse> response = restTemplate
                    .exchange(authenticateUrl, HttpMethod.POST, entity, JwtResponse.class);

                if(response.getStatusCode().is2xxSuccessful()) {
                    this.token = response.getBody().getToken();
                    this.userCredentialCache.put(this.token, jwtRequest);
                    return true;
                }
                else {
                    return false;
                }
            }
            return false;
        }
        catch (HttpClientErrorException e)
        {
            logger.warn("Issue while authenticating to dashboard [" + authenticateUrl
                + "] with response [{" + e.getResponseBodyAsString() + "}]");
            return false;
        }
    }

    private HttpHeaders createHttpHeaders(boolean includeToken)
    {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.add(HttpHeaders.USER_AGENT, moduleName);
        if (token != null && includeToken)
        {
            headers.add("Authorization", "Bearer " + token);
        }
        return headers;
    }
}
