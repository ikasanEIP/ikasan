package org.ikasan.spec.module.client;

import java.util.Optional;

public interface ModuleControlService<M, F>
{
    /**
     * Get a metadata object containing the states for all flows in a module.
     *
     * @param contextUrl
     * @param moduleName
     * @return
     */
    public Optional<M> getFlowStates(String contextUrl, String moduleName);


    /**
     * Get a metadata object for an individual flow.
     *
     * @param contextUrl
     * @param moduleName
     * @param flowName
     * @return
     */
    public Optional<F> getFlowState(String contextUrl, String moduleName, String flowName);

    /**
     * Change the flow state. Supports actions 'start', 'startPause', 'pause', 'resume', 'stop'.
     *
     * @param contextUrl
     * @param moduleName
     * @param flowName
     * @param action
     * @return
     */
    public boolean changeFlowState(String contextUrl, String moduleName, String flowName, String action);

    /**
     * Set the flow startup type. Supports types 'manual', 'automatic', 'disabled'.
     *
     * 'manual' requires a flow to be manually started.
     * 'automatic' will automatically restart a flow when a module is restarted.
     * 'disabled' a flow cannot be started.
     *
     * @param contextUrl
     * @param moduleName
     * @param flowName
     * @param startupType
     * @param comment
     * @return
     */
    public boolean changeFlowStartupType(String contextUrl, String moduleName, String flowName, String startupType,
                                         String comment);

}
