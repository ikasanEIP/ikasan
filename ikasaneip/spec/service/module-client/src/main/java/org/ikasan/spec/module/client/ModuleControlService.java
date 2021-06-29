package org.ikasan.spec.module.client;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public interface ModuleControlService<M, F, C>
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

    /**
     * Get the startup type of the flow.
     *
     * @param contextUrl
     * @param moduleName
     * @param flowName
     * @return
     */
    public Optional<C> getFlowStartupType(String contextUrl, String moduleName, String flowName);


    /**
     * Change the module activation state of a module.
     *
     * @param contextUrl - url of service to call
     * @param moduleName - the name of the module
     * @param action - 'activate' or 'deactivate'
     * @return true if successful otherwise false
     */
    public boolean changeModuleActivationState(String contextUrl, String moduleName, String action);


    /**
     * Get the module activation state.
     *
     * @param contextUrl - url of service to call
     * @param moduleName - the name of the module
     * @return 'activated' or 'deactivated'
     */
    public Optional<String> getModuleActivationState(String contextUrl, String moduleName);

}
