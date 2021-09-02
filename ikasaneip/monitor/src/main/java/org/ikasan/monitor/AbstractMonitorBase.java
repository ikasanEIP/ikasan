package org.ikasan.monitor;

import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.monitor.Monitor;
import org.ikasan.spec.monitor.Notifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

public abstract class AbstractMonitorBase<T> implements Monitor<T>,  ConfiguredResource<MonitorConfiguration> {

    /** logger instance */
    private static Logger logger = LoggerFactory.getLogger(AbstractMonitorBase.class);

    // executor service for thread dispatching
    protected final ExecutorService executorService;

    /** configuration instance for this monitor */
    protected MonitorConfiguration configuration;

    /** configured resource identifier */
    protected String configuredResourceId;

    /** environment label */
    protected String environment;

    /** module name */
    protected String moduleName;

    /** has the state changed */
    protected Map<String,T> states = new ConcurrentHashMap<>();

    /** list of notifiers to be informed */
    protected List<Notifier> notifiers = new ArrayList<>();

    /**
     * Constructor
     * @param executorService
     */
    public AbstractMonitorBase(ExecutorService executorService)
    {
        this.executorService = executorService;
        if(executorService == null)
        {
            throw new IllegalArgumentException("executorService cannot be 'null'");
        }
    }

    @Override
    public String getEnvironment() {
        return environment;
    }

    @Override
    public void setEnvironment(String environment)
    {
        this.environment = environment;
    }

    public void invoke(final T status, String context) {
        if(configuration.isActive())
        {
            String monitorName = (configuration.getMonitorName() != null ? configuration.getMonitorName()
                : "Module[" + moduleName + "] Context[" + context + "]");

            boolean stateChanged = hasStateChanged(environment + monitorName, status);

            if((this.notifiers == null || this.notifiers.size() == 0) && stateChanged)
            {
                logger.info("Monitor [" + monitorName + "] has no registered notifiers");
                return;
            }

            if (executorService == null || executorService.isShutdown())
            {
                logger.warn("Cannot invoke Monitor after destroy has been called - executorService is null or shutdown");
                return;
            }

            for(final Notifier notifier: notifiers)
            {
                if( !notifier.isNotifyStateChangesOnly() || (notifier.isNotifyStateChangesOnly() && stateChanged) ) {
                    executorService.execute(() -> {
                        try
                        {
                            notifier.invoke(environment, moduleName, context, status);
                        }
                        catch(RuntimeException e)
                        {
                            logger.warn("Failed to invoke notifier[" + notifier.getClass().getName() + "]", e);
                        }
                    });
                }
            }
        }

    }

    /**
     * Has the reported state for this key changed since the last reported state
     * @param stateKey
     * @param status
     * @return
     */
    protected boolean hasStateChanged(String stateKey, T status)
    {
        T lastState = states.get(stateKey);
        if(lastState == null || !(lastState.equals(status)) )
        {
            states.put(stateKey, status);
            return true;
        }

        return false;
    }

    @Override
    public void destroy()
    {
        if (executorService != null)
        {
            logger.info("Monitor shutting down executorService");
            executorService.shutdown();
        }
    }

    @Override
    public MonitorConfiguration getConfiguration()
    {
        return configuration;
    }

    @Override
    public void setConfiguration(MonitorConfiguration configuration)
    {
        this.configuration = configuration;
    }

    @Override
    public String getConfiguredResourceId()
    {
        return this.configuredResourceId;
    }

    @Override
    public void setConfiguredResourceId(String configuredResourceId)
    {
        this.configuredResourceId = configuredResourceId;
    }

    @Override
    public void setNotifiers(List<Notifier> flowNotifiers) {
        this.notifiers = flowNotifiers;
    }

    @Override
    public List<Notifier> getNotifiers()
    {
        return this.notifiers;
    }

    /**
     * @return the moduleName
     */
    public String getModuleName()
    {
        return moduleName;
    }

    /**
     * @param moduleName the moduleName to set
     */
    public void setModuleName(String moduleName)
    {
        this.moduleName = moduleName;
    }
}
