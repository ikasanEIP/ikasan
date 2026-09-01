package org.ikasan.spec.persistence.model;

import java.util.List;

public interface DashboardPlatformSetup {

    /**
     * Retrieves the unique identifier associated with this object.
     *
     * @return the unique identifier as a String
     */
    String getId();

    /**
     * Sets the identifier for the DashboardPlatformSetup.
     *
     * @param var1 the unique identifier to set
     */
    void setId(String var1);

    /**
     * Retrieves the type associated with the platform setup.
     *
     * @return a String representing the type of the platform setup.
     */
    String getType();

    /**
     * Sets the type of the dashboard platform.
     *
     * @param type the type to be set for the dashboard platform
     */
    void setType(String type);

    /**
     * Retrieves a list of platform setup items associated with the dashboard.
     *
     * @return a list of DashboardSetupItem representing the configuration items for the platform setup
     */
    List<DashboardSetupItem> getPlatformSetupItems();

    /**
     * Updates the list of platform setup items for the dashboard.
     *
     * @param dashboardSetupItems the list of {@code DashboardSetupItem} objects representing
     *                            the setup items for the platform.
     */
    void setPlatformSetupItems(List<DashboardSetupItem> dashboardSetupItems);

    /**
     * Retrieves the timestamp associated with the platform setup.
     *
     * @return the timestamp as a long value representing the time in milliseconds since the epoch.
     */
    long getTimestamp();

    /**
     * Sets the timestamp value representing a specific point in time.
     *
     * @param timestamp the timestamp to set, expressed as the number of milliseconds
     *                  since the epoch (January 1, 1970, 00:00:00 GMT).
     */
    void setTimestamp(long timestamp);

    /**
     * Retrieves the last modification timestamp of the platform setup.
     *
     * @return the timestamp representing the last modification time in milliseconds.
     */
    long getModifiedTimestamp();

    void setModifiedTimestamp(long modifiedTimestamp);
}
