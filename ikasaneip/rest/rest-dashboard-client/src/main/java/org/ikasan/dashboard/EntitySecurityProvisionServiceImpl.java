package org.ikasan.dashboard;

import org.ikasan.security.service.EntitySecurityProvisionService;
import org.ikasan.security.service.EntitySecurityProvisionServiceException;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * REST client implementation for provisioning Ikasan entity security.
 *
 * <p>Provides REST-based access to security provisioning operations,
 * enabling remote assignment of modules and job plans to roles.
 *
 * @author Ikasan Development Team
 */
public class EntitySecurityProvisionServiceImpl extends AbstractRestServiceImpl implements EntitySecurityProvisionService {

    public static final String DASHBOARD_BASE_URL_PROPERTY = "ikasan.dashboard.base.url";
    public static final String DASHBOARD_USERNAME_PROPERTY = "ikasan.dashboard.rest.username";
    public static final String DASHBOARD_PASSWORD_PROPERTY = "ikasan.dashboard.rest.password";
    public static final String DASHBOARD_REST_USERAGENT = "ikasan.dashboard.rest.useragent";
    public static final String MODULE_ROLES_PATH = "/rest/security/module/roles";
    public static final String JOB_PLAN_ROLES_PATH = "/rest/security/jobplan/roles";

    private final String userAgent;

    /**
     * Constructor for EntitySecurityProvisionServiceImpl.
     *
     * @param environment the Spring environment for property resolution
     * @param httpComponentsClientHttpRequestFactory the HTTP client factory
     */
    public EntitySecurityProvisionServiceImpl(Environment environment,
                                             HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory) {
        this.restTemplate = new RestTemplate(httpComponentsClientHttpRequestFactory);

        super.url = environment.getProperty(DASHBOARD_BASE_URL_PROPERTY);
        super.authenticateUrl = environment.getProperty(DASHBOARD_BASE_URL_PROPERTY) + "/authenticate";
        super.username = environment.getProperty(DASHBOARD_USERNAME_PROPERTY);
        super.password = environment.getProperty(DASHBOARD_PASSWORD_PROPERTY);
        this.userAgent = environment.getProperty(DASHBOARD_REST_USERAGENT);
    }

    @Override
    public void assignModuleToRoles(String moduleName, List<String> roleNames)
            throws EntitySecurityProvisionServiceException {
        assignToRoles(moduleName, roleNames, MODULE_ROLES_PATH, "module", true);
    }

    @Override
    public void assignJobPlanToRoles(String jobPlanName, List<String> roleNames)
            throws EntitySecurityProvisionServiceException {
        assignToRoles(jobPlanName, roleNames, JOB_PLAN_ROLES_PATH, "job plan", true);
    }

    /**
     * Generic method to assign an entity (module or job plan) to multiple roles.
     *
     * @param entityName the name of the entity to assign
     * @param roleNames the list of role names to assign
     * @param path the REST endpoint path
     * @param entityType the type of entity (for error messages)
     * @param isFirst flag indicating if this is the first attempt (for re-authentication)
     * @throws EntitySecurityProvisionServiceException if the operation fails
     */
    private void assignToRoles(String entityName, List<String> roleNames, String path,
                               String entityType, boolean isFirst)
            throws EntitySecurityProvisionServiceException {

        if (entityName == null || entityName.trim().isEmpty()) {
            throw new EntitySecurityProvisionServiceException(
                String.format("%s name cannot be null or empty", entityType));
        }

        if (roleNames == null || roleNames.isEmpty()) {
            throw new EntitySecurityProvisionServiceException("Role names list cannot be null or empty");
        }

        HttpHeaders headers = super.createHttpHeaders(userAgent);
        HttpEntity<List<String>> entity = new HttpEntity<>(roleNames, headers);

        try {
            String urlWithParams = url + path + "?moduleName=" + entityName;
            if (path.contains("jobplan")) {
                urlWithParams = url + path + "?jobPlanName=" + entityName;
            }

            restTemplate.exchange(
                urlWithParams,
                HttpMethod.POST,
                entity,
                Void.class
            );

            logger.info("Successfully assigned {} [{}] to {} role(s)",
                entityType, entityName, roleNames.size());

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().equals(HttpStatusCode.valueOf(401)) && isFirst) {
                this.token = null;
                if (authenticate(this.userAgent)) {
                    assignToRoles(entityName, roleNames, path, entityType, false);
                    return;
                }
            }

            String errorMsg = String.format(
                "Error assigning %s [%s] to roles %s: %s",
                entityType, entityName, roleNames, e.getMessage()
            );
            logger.error(errorMsg, e);
            throw new EntitySecurityProvisionServiceException(errorMsg, e);

        } catch (RestClientException e) {
            String errorMsg = String.format(
                "Error assigning %s [%s] to roles %s: %s",
                entityType, entityName, roleNames, e.getMessage()
            );
            logger.error(errorMsg, e);
            throw new EntitySecurityProvisionServiceException(errorMsg, e);
        }
    }
}
