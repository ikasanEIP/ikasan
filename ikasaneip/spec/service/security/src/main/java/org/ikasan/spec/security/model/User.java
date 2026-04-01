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
    /**
     * Retrieves the authorities granted to this user.
     *
     * <p>This method is required by {@link UserDetails} and returns all {@link GrantedAuthority}
     * instances (policies and roles) assigned to this user through their principals.
     *
     * @return a collection of granted authorities, never {@code null}
     */
    Collection<? extends GrantedAuthority> getAuthorities();

    /**
     * Indicates whether the user's account has expired.
     *
     * @return {@code true} if the account is valid (non-expired), {@code false} if expired
     */
    boolean isAccountNonExpired();

    /**
     * Indicates whether the user's account is locked.
     *
     * @return {@code true} if the account is not locked, {@code false} if locked
     */
    boolean isAccountNonLocked();

    /**
     * Indicates whether the user's credentials (password) have expired.
     *
     * @return {@code true} if the credentials are valid (non-expired), {@code false} if expired
     */
    boolean isCredentialsNonExpired();

    /**
     * Indicates whether the user account is enabled.
     *
     * @return {@code true} if the account is enabled, {@code false} if disabled
     */
    boolean isEnabled();

    /**
     * Retrieves the name of this user principal.
     *
     * <p>This method is required by {@link Principal} and typically returns the username.
     *
     * @return the user's name, never {@code null}
     */
    String getName();

    /**
     * Retrieves the username used for authentication.
     *
     * @return the username, or {@code null} if not set
     */
    String getUsername();

    /**
     * Sets the username used for authentication.
     *
     * @param username the username to set
     */
    void setUsername(String username);

    /**
     * Retrieves the user's password.
     *
     * @return the password (typically hashed), or {@code null} if not set
     */
    String getPassword();

    /**
     * Sets the user's password.
     *
     * @param password the password to set (should be hashed before storage)
     */
    void setPassword(String password);

    /**
     * Retrieves the user's email address.
     *
     * @return the email address, or {@code null} if not set
     */
    String getEmail();

    /**
     * Sets the user's email address.
     *
     * @param email the email address to set
     */
    void setEmail(String email);

    /**
     * Sets whether the user account is enabled.
     *
     * @param enabled {@code true} to enable the account, {@code false} to disable it
     */
    void setEnabled(boolean enabled);

    /**
     * Revokes a policy from this user by removing it from all associated principals.
     *
     * @param policy the {@link Policy} to revoke, must not be {@code null}
     */
    void revokePolicy(Policy policy);

    /**
     * Adds a principal association to this user.
     *
     * <p>Associating a principal with a user grants the user all roles and policies
     * assigned to that principal.
     *
     * @param principal the {@link IkasanPrincipal} to associate, must not be {@code null}
     */
    void addPrincipal(IkasanPrincipal principal);

    /**
     * Retrieves the unique identifier of this user.
     *
     * @return the unique identifier, or {@code null} if not yet persisted
     */
    Object getId();

    /**
     * Retrieves the user's first name.
     *
     * @return the first name, or {@code null} if not set
     */
    String getFirstName();

    /**
     * Sets the user's first name.
     *
     * @param firstName the first name to set
     */
    void setFirstName(String firstName);

    /**
     * Retrieves the user's surname (last name).
     *
     * @return the surname, or {@code null} if not set
     */
    String getSurname();

    /**
     * Sets the user's surname (last name).
     *
     * @param surname the surname to set
     */
    void setSurname(String surname);

    /**
     * Retrieves the user's department.
     *
     * @return the department name, or {@code null} if not set
     */
    String getDepartment();

    /**
     * Sets the user's department.
     *
     * @param department the department name to set
     */
    void setDepartment(String department);

    /**
     * Retrieves the set of principals associated with this user.
     *
     * @return a set of {@link IkasanPrincipal} instances, never {@code null}
     */
    Set<IkasanPrincipal> getPrincipals();

    /**
     * Sets the principals associated with this user.
     *
     * @param principals the set of {@link IkasanPrincipal} instances to associate, must not be {@code null}
     */
    void setPrincipals(Set<IkasanPrincipal> principals);

    /**
     * Retrieves the timestamp of the user's previous access to the system.
     *
     * @return the previous access timestamp in milliseconds since epoch
     */
    long getPreviousAccessTimestamp();

    /**
     * Sets the timestamp of the user's previous access to the system.
     *
     * @param previousAccessTimestamp the previous access timestamp in milliseconds since epoch
     */
    void setPreviousAccessTimestamp(long previousAccessTimestamp);

    /**
     * Indicates whether the user is required to change their password on next login.
     *
     * @return {@code true} if a password change is required, {@code false} otherwise
     */
    boolean isRequiresPasswordChange();

    /**
     * Sets whether the user is required to change their password on next login.
     *
     * @param requiresPasswordChange {@code true} to require a password change, {@code false} otherwise
     */
    void setRequiresPasswordChange(boolean requiresPasswordChange);
}
