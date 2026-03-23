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
package org.ikasan.security.service.model;
import org.ikasan.spec.security.model.IkasanPrincipal;
import org.ikasan.spec.security.model.Policy;
import org.ikasan.spec.security.model.Role;
import org.ikasan.spec.security.model.User;
import org.springframework.security.core.GrantedAuthority;

import java.util.*;

/**
 * Test implementation of User for use in test scenarios.
 *
 * @author Ikasan Development Team
 */
public class UserImpl implements User
{
    private Long id;
    private String username;
    private String password;
    private String email;
    private String firstName;
    private String surname;
    private String department;
    private boolean enabled = true;
    private boolean accountNonExpired = true;
    private boolean accountNonLocked = true;
    private boolean credentialsNonExpired = true;
    private boolean requiresPasswordChange = false;
    private long previousAccessTimestamp;
    private Set<IkasanPrincipal> principals = new HashSet<>();

    /**
     * Default no-argument constructor for the UserImpl class.
     * This constructor initializes an instance of UserImpl with default values.
     */
    public UserImpl() {}

    /**
     * Constructs a new instance of {@code UserImpl}.
     *
     * @param username the username of the user; cannot be null or empty.
     * @param password the password of the user; cannot be null or empty.
     * @param email the email address of the user; cannot be null or empty.
     * @param enabled a boolean indicating whether the user is enabled or not.
     */
    public UserImpl(String username, String password, String email, boolean enabled)
    {
        this.username = username;
        this.password = password;
        this.email = email;
        this.enabled = enabled;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities()
    {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        if (principals != null)
        {
            for (IkasanPrincipal principal : principals)
            {
                if (principal.getRoles() != null)
                {
                    for (Role role : principal.getRoles())
                    {
                        if (role.getPolicies() != null)
                        {
                            authorities.addAll(role.getPolicies());
                        }
                    }
                }
            }
        }
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired()
    {
        return accountNonExpired;
    }

    public void setAccountNonExpired(boolean accountNonExpired)
    {
        this.accountNonExpired = accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked()
    {
        return accountNonLocked;
    }

    public void setAccountNonLocked(boolean accountNonLocked)
    {
        this.accountNonLocked = accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired()
    {
        return credentialsNonExpired;
    }

    public void setCredentialsNonExpired(boolean credentialsNonExpired)
    {
        this.credentialsNonExpired = credentialsNonExpired;
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
    public String getName()
    {
        return username;
    }

    @Override
    public String getUsername()
    {
        return username;
    }

    @Override
    public void setUsername(String username)
    {
        this.username = username;
    }

    @Override
    public String getPassword()
    {
        return password;
    }

    @Override
    public void setPassword(String password)
    {
        this.password = password;
    }

    @Override
    public String getEmail()
    {
        return email;
    }

    @Override
    public void setEmail(String email)
    {
        this.email = email;
    }

    @Override
    public void revokePolicy(Policy policy)
    {
        if (principals != null)
        {
            for (IkasanPrincipal principal : principals)
            {
                if (principal.getRoles() != null)
                {
                    for (Role role : principal.getRoles())
                    {
                        if (role.getPolicies() != null)
                        {
                            role.getPolicies().remove(policy);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void addPrincipal(IkasanPrincipal principal)
    {
        if (this.principals == null)
        {
            this.principals = new HashSet<>();
        }
        this.principals.add(principal);
    }

    @Override
    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    @Override
    public String getFirstName()
    {
        return firstName;
    }

    @Override
    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    @Override
    public String getSurname()
    {
        return surname;
    }

    @Override
    public void setSurname(String surname)
    {
        this.surname = surname;
    }

    @Override
    public String getDepartment()
    {
        return department;
    }

    @Override
    public void setDepartment(String department)
    {
        this.department = department;
    }

    @Override
    public Set<IkasanPrincipal> getPrincipals()
    {
        return principals;
    }

    @Override
    public void setPrincipals(Set<IkasanPrincipal> principals)
    {
        this.principals = principals;
    }

    @Override
    public long getPreviousAccessTimestamp()
    {
        return previousAccessTimestamp;
    }

    @Override
    public void setPreviousAccessTimestamp(long previousAccessTimestamp)
    {
        this.previousAccessTimestamp = previousAccessTimestamp;
    }

    @Override
    public boolean isRequiresPasswordChange()
    {
        return requiresPasswordChange;
    }

    @Override
    public void setRequiresPasswordChange(boolean requiresPasswordChange)
    {
        this.requiresPasswordChange = requiresPasswordChange;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserImpl user = (UserImpl) o;
        return enabled == user.enabled
            && accountNonExpired == user.accountNonExpired
            && accountNonLocked == user.accountNonLocked
            && credentialsNonExpired == user.credentialsNonExpired
            && requiresPasswordChange == user.requiresPasswordChange
            && previousAccessTimestamp == user.previousAccessTimestamp
            && Objects.equals(id, user.id)
            && Objects.equals(username, user.username)
            && Objects.equals(password, user.password)
            && Objects.equals(email, user.email)
            && Objects.equals(firstName, user.firstName)
            && Objects.equals(surname, user.surname)
            && Objects.equals(department, user.department)
            && Objects.equals(principals, user.principals);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, password, email, firstName, surname
            , department, enabled, accountNonExpired, accountNonLocked, credentialsNonExpired
            , requiresPasswordChange, previousAccessTimestamp, principals);
    }
}
