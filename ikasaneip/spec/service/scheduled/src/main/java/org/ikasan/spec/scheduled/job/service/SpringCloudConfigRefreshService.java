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
     * Request config service to decrypt the value so we can read the values
     * @param contextUrl url path to config service
     * @param encryptedValue value you want to decrypt using Spring Cloud Config Service decryption
     * @return decrypted value
     */
    String decrypt(String contextUrl, String encryptedValue);

    /**
     * Request config service to encrypt a value
     * @param valueToEncrypt value you want to encrypt using Spring Cloud Config Service
     * @return encrypt value
     */
    String encrypt(String valueToEncrypt);

    /**
     * Service to run the Spring Actuator Refresh
     */
    void actuatorRefresh();
}
