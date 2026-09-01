package org.ikasan.spec.persistence.model;

public interface DashboardSetupItem {

    /**
     * Sets the name for the setup item.
     *
     * @param name the name to be assigned to the setup item
     */
    void setName(String name);

    /**
     * Retrieves the name associated with an object.
     *
     * @return the name as a String
     */
    String getName();

    /**
     * Sets the status of the dashboard setup item.
     *
     * @param status the status to set for the dashboard setup item
     */
    void setStatus(String status);

    /**
     * Retrieves the current status of the dashboard setup item.
     *
     * @return a String representing the status of the item
     */
    String getStatus();

    /**
     * Sets the execution timestamp for this item.
     *
     * @param timestamp the timestamp to set, represented as the number of milliseconds
     *                  since the epoch (January 1, 1970, 00:00:00 GMT)
     */
    void setExecutionTimestamp(long timestamp);

    /**
     * Retrieves the execution timestamp associated with this setup item.
     *
     * @return the execution timestamp as a long value representing the time in milliseconds
     *         since the epoch (January 1, 1970, 00:00:00 GMT).
     */
    long getExecutionTimestamp();
}
