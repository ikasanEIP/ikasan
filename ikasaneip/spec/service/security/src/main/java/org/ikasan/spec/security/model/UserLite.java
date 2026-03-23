package org.ikasan.spec.security.model;

/**
 * Lightweight representation of a user in the Ikasan security framework.
 *
 * <p>Provides a subset of {@link User} properties optimized for listing and filtering operations
 * where full user details (such as authorities and principals) are not required. This interface
 * improves performance when retrieving large numbers of users for display in user interfaces.
 *
 * @author Ikasan Development Team
 * @since 1.0
 */
public interface UserLite
{
    boolean isAccountNonExpired();

    boolean isAccountNonLocked();

    boolean isCredentialsNonExpired();

    boolean isEnabled();

    String getUsername();

    void setUsername(String username);

    String getPassword();

    void setPassword(String password);

    String getEmail();

    void setEmail(String email);

    void setEnabled(boolean enabled);

    Long getId();

    String getFirstName();

    void setFirstName(String firstName);

    String getSurname();

    void setSurname(String surname);

    String getDepartment();

    void setDepartment(String department);

    long getPreviousAccessTimestamp();

    void setPreviousAccessTimestamp(long previousAccessTimestamp);
}
