package org.ikasan.spec.persistence.service;

import org.ikasan.spec.persistence.model.DashboardPlatformSetup;

public interface SetupService {

    /**
     * Retrieves the current dashboard platform setup configuration.
     *
     * @return an instance of {@code DashboardPlatformSetup} representing the configuration
     *         of the dashboard platform.
     */
    DashboardPlatformSetup getDashboardPlatformSetup();

    /**
     * Saves the given dashboard platform setup configuration.
     *
     * @param dashboardPlatformSetup the {@code DashboardPlatformSetup} instance to be saved,
     *                                containing the configuration details of the dashboard platform.
     */
    void save(DashboardPlatformSetup dashboardPlatformSetup);
}
