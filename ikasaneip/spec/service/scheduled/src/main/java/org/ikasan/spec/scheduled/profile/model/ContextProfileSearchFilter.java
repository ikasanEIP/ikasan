package org.ikasan.spec.scheduled.profile.model;

import java.util.List;

public interface ContextProfileSearchFilter {
    /**
     * Retrieves the profile name associated with the context profile.
     *
     * @return the profile name as a String
     */
    String getProfileName();

    /**
     * Sets the profile name associated with the context.
     *
     * @param profileName the name of the profile to set
     */
    void setProfileName(String profileName);

    /**
     * Retrieves the name of the context associated with this filter.
     *
     * @return the context name as a String
     */
    String getContextName();

    /**
     * Sets the name of the context associated with the filter.
     *
     * @param contextName the name of the context to set
     */
    void setContextName(String contextName);

    /**
     * Retrieves the owner associated with the context profile search filter.
     *
     * @return the owner as a String
     */
    String getOwner();

    /**
     * Sets the owner associated with the context profile or filter.
     *
     * @param owner the identifier of the owner to set, typically a user or system name
     */
    void setOwner(String owner);

    /**
     * Retrieves a list of access roles associated with the current context profile.
     *
     * @return a list of strings representing the access roles.
     */
    List<String> getAccessRoles();

    /**
     * Assigns a list of access roles to this context profile search filter.
     * The specified roles determine the access permissions for the related context profile.
     *
     * @param accessRoles a list of strings representing the access roles to be associated
     *                    with this filter
     */
    void setAccessRoles(List<String> accessRoles);

    /**
     * Retrieves the username or user information as a string.
     *
     * @return a String representing the user. The returned value may vary based on the implementation, such as a username, user ID, or other user-related information.
     */
    String getUser();

    /**
     * Sets the user with the specified username.
     *
     * @param user the username to be set, must not be null or empty
     */
    void setUser(String user);
}
