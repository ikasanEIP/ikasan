package org.ikasan.module.service;

import org.ikasan.module.StartupModuleConfiguration;
import org.ikasan.module.startup.StartupControlImpl;
import org.ikasan.module.startup.dao.StartupControlDao;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.ikasan.spec.module.StartupControl;
import org.ikasan.spec.module.StartupType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Uses StartupModuleConfiguration to perform module configuration actions at module startup. This does the following
 *
 * Sets and persists the startupType on flows that have not had this saved previously
 * Sets up wiretap triggers that have not been saved previously
 */
public class StartupModuleProcessor {

    private final StartupModuleConfiguration configuration;

    private final StartupControlDao startupControlDao;

    private static Logger logger = LoggerFactory.getLogger(StartupModuleProcessor.class);

    public StartupModuleProcessor(StartupModuleConfiguration configuration, StartupControlDao startupControlDao){
        this.configuration = configuration;
        this.startupControlDao = startupControlDao;
    }

    public void run(Module<Flow> module){
        if (configuration.areStartupTypePropertiesPopulated()){
            saveFlowsStartupTypeValueIfNotPreviouslySaved(module, configuration);
        }
    }

    /**
     * This saves the flows startup type if there was no startup type pre-existing in the database. This is an
     * easy way to using the configuration to set flows to a specific startup type or to a default such as Automatic
     * at module startup
     *
     * @param module
     * @param startupModuleConfiguration
     */
    private void saveFlowsStartupTypeValueIfNotPreviouslySaved(Module<Flow> module, StartupModuleConfiguration
        startupModuleConfiguration) {
        logger.info("Save previously unsaved flows startup types using the startupModuleConfiguration [{}]",
            startupModuleConfiguration);
        Map<String, StartupControl> savedStartupControls = new HashMap<String, StartupControl>();
        String defaultFlowStartupType = startupModuleConfiguration.getDefaultFlowStartupType();
        Map<String,String>flowNameToStartupTypeMap = startupModuleConfiguration.getFlowNameToStartupTypeMap();
        for (StartupControl startupControl : this.startupControlDao.getStartupControls(module.getName())) {
            if (Boolean.TRUE.equals(startupModuleConfiguration.getDeleteAllPreviouslySavedStartupTypes())){
                logger.info("Deleting previously saved startup type on flow [{}] with value [{}]",
                    startupControl.getFlowName(), startupControl.getStartupType());
                startupControlDao.delete(startupControl);
            } else {
                savedStartupControls.put(startupControl.getFlowName(), startupControl);
            }
        }
        for (Flow flow : module.getFlows()) {
            if (savedStartupControls.get(flow.getName()) == null){
                StartupControl unsavedStartupControl =
                    new StartupControlImpl(module.getName(), flow.getName());
                String startupTypeString = flowNameToStartupTypeMap != null &&
                    flowNameToStartupTypeMap.get(flow.getName()) != null ? flowNameToStartupTypeMap.get(flow.getName())
                    : defaultFlowStartupType;
                if (startupTypeString != null) {
                    logger.info("Saving previously unsaved startup type on flow [{}] to [{}]", flow.getName(),
                        startupTypeString);
                    unsavedStartupControl.setStartupType(StartupType.valueOf(startupTypeString));
                    this.startupControlDao.save(unsavedStartupControl);
                }
            } else {
                StartupControl previouslySavedStartupControl = savedStartupControls.get(flow.getName());
                logger.info("Will not change startup type [{}] on flow [{}] as has previously been saved",
                    previouslySavedStartupControl.getStartupType(), previouslySavedStartupControl.getFlowName());
            }
        }
    }
}
