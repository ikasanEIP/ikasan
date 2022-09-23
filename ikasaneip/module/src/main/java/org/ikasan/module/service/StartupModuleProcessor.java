package org.ikasan.module.service;

import org.ikasan.module.StartupModuleConfiguration;
import org.ikasan.module.WiretapTriggerConfiguration;
import org.ikasan.module.startup.StartupControlImpl;
import org.ikasan.module.startup.dao.StartupControlDao;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.ikasan.spec.module.StartupControl;
import org.ikasan.spec.module.StartupType;
import org.ikasan.spec.trigger.Trigger;
import org.ikasan.trigger.model.TriggerImpl;
import org.ikasan.wiretap.listener.JobAwareFlowEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Uses StartupModuleConfiguration to perform module configuration actions at module startup. This does the following
 *
 * Sets and persists the startupType on flows that have not had this saved previously
 * Sets up wiretap triggers that have not been saved previously
 */
public class StartupModuleProcessor {

    private final StartupModuleConfiguration configuration;

    private final StartupControlDao startupControlDao;

    private final JobAwareFlowEventListener jobAwareFlowEventListener;

    public final static String WIRETAP_JOB = "wiretapJob";


    private static final Logger logger = LoggerFactory.getLogger(StartupModuleProcessor.class);

    public StartupModuleProcessor(StartupModuleConfiguration configuration, StartupControlDao startupControlDao,
    JobAwareFlowEventListener jobAwareFlowEventListener){
        this.configuration = configuration;
        this.startupControlDao = startupControlDao;
        this.jobAwareFlowEventListener = jobAwareFlowEventListener;
    }

    public void run(Module<Flow> module){
        if (configuration.areStartupTypePropertiesPopulated()){
            saveFlowsStartupTypeValueIfNotPreviouslySaved(module, configuration);
        }
        if (Boolean.TRUE.equals(configuration.getDeleteAllPreviouslySavedWiretaps())){
            deleteAllPreviouslySavedWiretaps(module);
        }
        if (configuration.getWiretaps() != null){
            savePreviouslyUnsavedWiretaps(module, configuration);
        }
    }

    private void deleteAllPreviouslySavedWiretaps(Module<Flow> module) {
        logger.info("Deleting all previously saved wiretaps");
        List<Trigger> existingTriggers = jobAwareFlowEventListener.getTriggers();
        if (!CollectionUtils.isEmpty(existingTriggers)) {
            List<Trigger> wiretapTriggers = existingTriggers.stream().filter(t -> t.getModuleName().equals(module.getName())
                && t.getJobName().equals(WIRETAP_JOB)).collect(Collectors.toList());
            for (Trigger trigger : wiretapTriggers) {
                logger.info("About to delete wiretap trigger [{}]", trigger);
                jobAwareFlowEventListener.deleteDynamicTrigger(trigger.getId());
            }
        }
    }

    private void savePreviouslyUnsavedWiretaps(Module<Flow> module, StartupModuleConfiguration configuration) {
        logger.info("Saving these wiretap triggers if havent been saved already [{}]", configuration.getWiretaps());
        List<TriggerImpl> triggerList = configuration.getWiretaps().entrySet().stream().map(entry -> {
            WiretapTriggerConfiguration wc = entry.getValue();
            Map<String, String> params = new HashMap();
            Integer timeToLive = Integer.parseInt(wc.getTimeToLiveInSeconds()) * 1000;
            params.put("timeToLive", timeToLive.toString());
            TriggerImpl triggerImpl = new TriggerImpl(module.getName(), wc.getFlowName(),
                wc.getBeforeOrAfter(), WIRETAP_JOB,
                wc.getComponentName(), params);
            return triggerImpl;
        }).collect(Collectors.toList());
        List<Trigger> existingTriggers = jobAwareFlowEventListener.getTriggers();
        List<TriggerImpl> notAlreadyExistingTriggerList = triggerList;
            if (!CollectionUtils.isEmpty(existingTriggers)) {
               notAlreadyExistingTriggerList = triggerList.stream().filter(t ->
                    existingTriggers.stream().noneMatch(et -> t.getModuleName().equals(et.getModuleName())
                        && t.getFlowName().equals(et.getFlowName())
                        && t.getFlowElementName().equals(et.getFlowElementName())
                        && t.getRelationship().equals(et.getRelationship()))).collect(Collectors.toList());
            }
        for (Trigger triggerToAdd : notAlreadyExistingTriggerList){
            logger.info("Saving new trigger to database [{}]", triggerToAdd);
            jobAwareFlowEventListener.addDynamicTrigger(triggerToAdd);
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
