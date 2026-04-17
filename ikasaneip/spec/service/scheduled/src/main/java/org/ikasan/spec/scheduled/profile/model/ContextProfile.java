package org.ikasan.spec.scheduled.profile.model;

import java.util.List;

public interface ContextProfile {

    /**
     * Retrieves the default context associated with this profile.
     *
     * @return the default context as a String
     */
    String getDefaultContext();

    /**
     * Sets the default context for the context profile.
     *
     * @param context the name of the default context to set.
     *                This value should represent a valid context identifier.
     */
    void setDefaultContext(String context);

    /**
     * Retrieves the list of sub-contexts associated with the context profile.
     *
     * @return a list of strings representing the sub-contexts. The list may be empty if no sub-contexts are defined.
     */
    List<String> getSubContexts();

    /**
     * Sets the list of sub-contexts associated with the context profile.
     *
     * @param subContexts a list of strings representing sub-context identifiers to be associated
     *                    with this context profile. The provided list may not be null but can
     *                    contain an empty collection to indicate no sub-contexts.
     */
    void setSubContexts(List<String> subContexts);
}
