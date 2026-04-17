package org.ikasan.spec.scheduled.profile.model;

import java.util.List;

public interface ContextProfileRecord {
    String SYSTEM_OWNER = "SYSTEM_OWNER";

    /**
     * Retrieves the profile name associated with the current context.
     *
     * @return the profile name as a String
     */
    String getProfileName();

    /**
     * Sets the profile name for this record.
     *
     * @param profileName the name of the profile to set
     */
    void setProfileName(String profileName);

    /**
     * Retrieves the name of the context associated with this record.
     *
     * @return the context name as a String
     */
    String getContextName();

    /**
     * Sets the context name associated with the profile record.
     *
     * @param contextName the name of the context to be set
     */
    void setContextName(String contextName);

    /**
     * Retrieves the owner associated with the context profile record.
     *
     * @return the owner of the context profile record as a String
     */
    String getOwner();

    /**
     * Sets the owner of the context profile record.
     *
     * @param owner the owner to set for this context profile record
     */
    void setOwner(String owner);

    /**
     * Retrieves the context profile instance associated with this record.
     *
     * @return the {@code ContextProfile} object representing the context profile,
     *         or null if no context profile is associated.
     */
    ContextProfile getContextProfile();

    /**
     * Sets the context profile associated with this record.
     *
     * @param contextProfile the {@link ContextProfile} to be assigned to the record
     */
    void setContextProfile(ContextProfile contextProfile);

    /**
     * Retrieves the list of access groups associated with the context profile record.
     *
     * @return a list of strings representing the access groups.
     */
    List<String> getAccessGroups();

    /**
     * Sets the access groups associated with this context profile record.
     *
     * @param accessRoles a list of group identifiers representing the access permissions
     *                    for this context profile record.
     */
    void setAccessGroups(List<String> accessRoles);

    /**
     * Retrieves a list of users who have access to the associated context profile.
     *
     * @return a list of user identifiers as strings.
     */
    List<String> getAccessUsers();

    /**
     * Sets the list of users that have access.
     *
     * @param accessUsers the list of user identifiers to be set as access users
     */
    void setAccessUsers(List<String> accessUsers);

    /**
     * Retrieves the timestamp representing when the record was created.
     *
     * @return the creation date and time of the record as a long value, typically in milliseconds since the epoch.
     */
    long getCreatedDateTime();

    /**
     * Sets the created date and time of the context profile record.
     *
     * @param createdDateTime the timestamp representing the creation date and time in milliseconds since the epoch
     */
    void setCreatedDateTime(long createdDateTime);

    /**
     * Retrieves the last modification timestamp of the context profile record.
     *
     * @return the timestamp of the last modification as a long value, representing milliseconds since epoch.
     */
    long getModifiedDateTime();

    /**
     * Sets the modified date and time for the context profile record.
     *
     * @param createdDateTime the timestamp representing the modified date and time in milliseconds since epoch
     */
    void setModifiedDateTime(long createdDateTime);

    /**
     * Retrieves the identifier of the user who last modified the record.
     *
     * @return A string representing the user or system that performed the most recent modification.
     */
    String getModifiedBy();

    /**
     * Sets the identifier of the user or system responsible for the most recent modification.
     *
     * @param modifiedBy the identifier of the user or system that modified the record
     */
    void setModifiedBy(String modifiedBy);
}
