package org.ikasan.spec.scheduled.profile.model;

import java.util.List;

public interface ContextProfileSearchFilter {
    String getProfileName();

    void setProfileName(String profileName);

    String getContextName();

    void setContextName(String contextName);

    String getOwner();

    void setOwner(String owner);

    List<String> getAccessRoles();

    void setAccessRoles(List<String> accessRoles);

    String getUser();

    void setUser(String user);
}
