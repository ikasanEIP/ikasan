package org.ikasan.spec.security.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.security.Principal;
import java.util.Collection;
import java.util.Set;

/**
 * Represents a user account in the Ikasan security framework.
 *
 * <p>Extends Spring Security's {@link UserDetails} and Java's {@link Principal} interfaces to provide
 * comprehensive user account information including credentials, account status, contact details, and
 * associated security principals. Users are authenticated entities that can be granted permissions
 * through their association with {@link IkasanPrincipal}s and their assigned {@link Role}s.
 *
 * @author Ikasan Development Team
 * @since 1.0
 */
public interface User extends UserDetails, Principal
{
    Collection<? extends GrantedAuthority> getAuthorities();

    boolean isAccountNonExpired();

    boolean isAccountNonLocked();

    boolean isCredentialsNonExpired();

    boolean isEnabled();

    String getName();

    String getUsername();

    void setUsername(String username);

    String getPassword();

    void setPassword(String password);

    String getEmail();

    void setEmail(String email);

    void setEnabled(boolean enabled);

    void revokePolicy(Policy policy);

    void addPrincipal(IkasanPrincipal principal);

    Long getId();

    String getFirstName();

    void setFirstName(String firstName);

    String getSurname();

    void setSurname(String surname);

    String getDepartment();

    void setDepartment(String department);

    Set<IkasanPrincipal> getPrincipals();

    void setPrincipals(Set<IkasanPrincipal> principals);

    long getPreviousAccessTimestamp();

    void setPreviousAccessTimestamp(long previousAccessTimestamp);

    boolean isRequiresPasswordChange();

    void setRequiresPasswordChange(boolean requiresPasswordChange);
}
