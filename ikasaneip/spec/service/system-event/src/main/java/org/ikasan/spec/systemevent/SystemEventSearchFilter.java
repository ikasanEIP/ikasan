package org.ikasan.spec.systemevent;

public interface SystemEventSearchFilter {

    /**
     * Set the actor filter;
     *
     * @param actor
     */
    void setActor(String actor);

    /**
     * Get the actor filter.
     *
     * @return
     */
    String getActor();

    /**
     * Set the subject filter.
     *
     * @param subject
     */
    void setSubject(String subject);

    /**
     * Get the subject search filter.
     *
     * @return
     */
    String getSubject();

    /**
     * Set the action filter.
     *
     * @param action
     */
    void setAction(String action);

    /**
     * Set the search term filter.
     *
     * @return
     */
    String getSearchTerm();

    /**
     * Set the search term filter.
     *
     * @param searchTerm
     */
    void setSearchTerm(String searchTerm);

    /**
     * Set the action filter.
     *
     * @return
     */
    String getAction();

    /**
     * Set the filter start time
     *
     * @param startTime
     */
    void setStartTime(long startTime);

    /**
     * Get the filter start time
     */
    long getStartTime();

    /**
     * Set the filter end time
     *
     * @param endTime
     */
    void setEndTime(long endTime);

    /**
     * Get the filter end time
     */
    long getEndTime();
}
