package org.ikasan.spec.module.client;

public interface ResubmissionService
{
    /**
     * Resubmit an to a specific flow in a module.
     *
     * @param contextUrl
     * @param moduleName
     * @param flowName
     * @param action
     * @param errorUri
     * @return
     */
    public boolean resubmit(String contextUrl, String moduleName, String flowName, String action, String errorUri);
}
