package org.ikasan.spec.module.client;

public interface ResubmissionService
{


    /**
     * Resubmits a failed submission to a specified context URL, module name, flow name,
     * with a specific action and error URI, using the provided username for authentication.
     *
     * @param contextUrl The URL of the context where the resubmission will take place.
     * @param moduleName The name of the module related to the resubmission.
     * @param flowName The name of the flow within the module for resubmission.
     * @param action The action to be performed during the resubmission process.
     * @param errorUri The URI identifying the error that caused the initial submission to fail.
     * @param username The username to be used for authentication during resubmission.
     * @return True if the resubmission is successful, false otherwise.
     */
    public boolean resubmit(String contextUrl, String moduleName, String flowName, String action, String errorUri, String username);
}
