package org.ikasan.spec.module.client;

public interface ReplayService
{
    /**
     * Replay an event to a specific flow in a module.
     *
     * @param contextUrl
     * @param username
     * @param password
     * @param moduleName
     * @param flowName
     * @param event
     * @return
     */
    public boolean replay(String contextUrl, String username, String password, String moduleName, String flowName,
                          byte[] event);
}
