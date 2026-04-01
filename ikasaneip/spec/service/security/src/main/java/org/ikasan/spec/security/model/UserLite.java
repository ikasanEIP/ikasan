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
}
