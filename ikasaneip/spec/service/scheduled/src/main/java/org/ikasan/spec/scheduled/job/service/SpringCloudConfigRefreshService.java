package org.ikasan.spec.scheduled.job.service;

/**
 * Scheduler can host configuration properties from multiple repositories within a single Spring Cloud Config Service. 
 * This service allows the dashboard to call the config service to refresh multiple repos that host these
 * configuration. This is based on:
 * https://docs.spring.io/spring-cloud-config/docs/current/reference/html/#_pattern_matching_and_multiple_repositories
 */
public interface SpringCloudConfigRefreshService {

    /**
     * Request config based on an applicationPattern from the available Spring Cloud Config Service.
     * This will request it on the default profile.
     * @param contextUrl url path to config service
     * @param applicationPattern application patterned required for multi repository
     */
    void refreshConfigRepo(String contextUrl, String applicationPattern);

    /**
     * Service to run the Spring Actuator Refresh
     */
    void actuatorRefresh();
}
