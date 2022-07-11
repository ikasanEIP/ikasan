package org.ikasan.spec.scheduled.profile.model;

import java.util.List;

public interface ContextProfileRecord {
    String SYSTEM_OWNER = "SYSTEM_OWNER";

    String getProfileName();

    void setProfileName(String profileName);

    String getContextName();

    void setContextName(String contextName);

    String getOwner();

    void setOwner(String owner);

    ContextProfile getContextProfile();

    void setContextProfile(ContextProfile contextProfile);

    List<String> getAccessGroups();

    void setAccessGroups(List<String> accessRoles);

    List<String> getAccessUsers();

    void setAccessUsers(List<String> accessUsers);

    long getCreatedDateTime();

    void setCreatedDateTime(long createdDateTime);

    long getModifiedDateTime();

    void setModifiedDateTime(long createdDateTime);

    String getModifiedBy();

    void setModifiedBy(String modifiedBy);
}
