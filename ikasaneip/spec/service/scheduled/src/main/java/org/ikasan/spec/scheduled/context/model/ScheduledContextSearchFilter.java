package org.ikasan.spec.scheduled.context.model;

import java.io.Serializable;
import java.util.List;

public interface ScheduledContextSearchFilter extends Serializable {

    /**
     * Get the context name from the filter.
     *
     * @return
     */
    public String getContextName();

    /**
     * Set the context name filter. The search will perform a wildcard search
     * with partial match pf the context name.
     *
     * @param contextName
     */
    public void setContextName(String contextName);

    /**
     * Get the list of context names that the search will be
     * constrained to.
     *
     * @return
     */
    public List<String> getContextNames();

    /**
     * Set the list of context names that the search will be
     * constrained to.
     *
     * @param contextNames
     */
    public void setContextNames(List<String> contextNames);
}