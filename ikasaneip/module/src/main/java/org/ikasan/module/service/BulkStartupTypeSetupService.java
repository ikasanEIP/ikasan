/*
 *  ====================================================================
 *  Ikasan Enterprise Integration Platform
 *
 *  Distributed under the Modified BSD License.
 *  Copyright notice: The copyright for this software and a full listing
 *  of individual contributors are as shown in the packaged copyright.txt
 *  file.
 *
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *   - Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *
 *   - Neither the name of the ORGANIZATION nor the names of its contributors may
 *     be used to endorse or promote products derived from this software without
 *     specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 *  AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 *  IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 *  FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 *  DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 *  CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 *  OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 *  USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *  ====================================================================
 *
 */

package org.ikasan.module.service;

import org.ikasan.module.startup.StartupControlImpl;
import org.ikasan.module.startup.dao.StartupControlDao;
import org.ikasan.spec.module.StartupControl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Given a BulkStartupTypeSetupServiceConfiguration will setup the flows startup type. Will be used on module launch
 * by the Module activator
 */
public class BulkStartupTypeSetupService {

    private final BulkStartupTypeSetupServiceConfiguration configuration;

    private final StartupControlDao startupControlDao;

    public BulkStartupTypeSetupService(BulkStartupTypeSetupServiceConfiguration configuration,
                                       StartupControlDao startupControlDao) {
        this.configuration = configuration;
        this.startupControlDao = startupControlDao;
    }

    private static Logger logger = LoggerFactory.getLogger(BulkStartupTypeSetupService.class);

    public void deleteAllOnlyIfConfigured(String moduleName){
        if (Boolean.TRUE.equals(configuration.getDeleteAll())){
            logger.warn("!! Deleting all flows startup type controls on module [{}] !!", moduleName);
            List<StartupControl> startupControls = startupControlDao.getStartupControls(moduleName);
            for (StartupControl startupControl : startupControls){
                startupControlDao.delete(startupControl);
            }
        }
    }

    /**
     * Will be passed by the ModuleActivator a startup control for each flow, it will then set the startup
     * type according to the configuration
     *
     * @param flowStartupControl - the control under consideration
     */
    public StartupControl setup(StartupControl flowStartupControl) {
        if (configuration != null && (configuration.getDefaultStartupType() != null ||
            configuration.getFlowStartupTypes() != null)) {
            if (flowStartupControl instanceof StartupControlImpl) {
                StartupControlImpl startupControl = (StartupControlImpl) flowStartupControl;
                if (startupControl.getId() == null) {
                    logger.debug("StartupControl [{}] has not been persisted will update",
                        flowStartupControl.getFlowName());
                    return updateStartupTypeAndSave(startupControl);
                } else if (Boolean.TRUE.equals(configuration.getAllowDbOverwrite())) {
                    logger.debug("StartupControl [{}] *has* already been persisted but configuration " +
                        "allows for Database overwrite so will update", flowStartupControl.getFlowName());
                    return updateStartupTypeAndSave(startupControl);
                } else {
                    logger.debug("StartupControl [{}] has already been persisted so not doing anything",
                        flowStartupControl.getFlowName());
                }
            }
        } else {
            logger.debug("No configuration or bulkStartupType set will leave startup controls as is");
        }
        return flowStartupControl;
    }

    private StartupControlImpl updateStartupTypeAndSave(StartupControlImpl startupControl) {
        if (checkIfShouldUpdateStartupControl(startupControl)) {
            StartupControlImpl flowSpecificStartupControl = checkForFlowSpecificStartupTypeInConfiguration
                (startupControl);
            if (flowSpecificStartupControl != null) {
                logger.debug("Will use flow specific startup type configuration for flow [{}]",
                   startupControl.getFlowName());
                startupControl.setStartupType(flowSpecificStartupControl.getStartupType());
                startupControl.setComment(flowSpecificStartupControl.getComment());
            } else if (configuration.getDefaultStartupType() != null) {
                logger.debug("Will use default bulk startup type configuration for flow [{}]",
                    startupControl.getFlowName());
                startupControl.setStartupType(configuration.getDefaultStartupType());
                startupControl.setComment("Startup Type set on Module Initialisation");
            }
            logger.info("Flow [{}] will have startup type set to [{}]",
                startupControl.getFlowName(), startupControl.getStartupType());
            startupControlDao.save(startupControl);
            return startupControl;
        }
        return startupControl;
    }

    private StartupControlImpl checkForFlowSpecificStartupTypeInConfiguration(StartupControlImpl startupControl) {
        if (configuration.getFlowStartupTypes()  != null){
            return configuration.getFlowStartupTypes().stream()
                .filter(fs -> startupControl.getFlowName().equals(fs.getFlowName())).findFirst().map(fs->{
                    StartupControlImpl flowSpecificStartupControl =
                        new StartupControlImpl(startupControl.getModuleName(),startupControl.getFlowName());
                    flowSpecificStartupControl.setStartupType(fs.getStartupType());
                    flowSpecificStartupControl.setComment(fs.getComment());
                    return flowSpecificStartupControl;
                }).orElse(null);
        }
        return null;
    }

    private boolean checkIfShouldUpdateStartupControl(StartupControl flowStartupControl) {
        return configuration.getFlowsNotToSet() == null ||
            configuration.getFlowsNotToSet() != null &&
                !configuration.getFlowsNotToSet().contains(flowStartupControl.getFlowName());
    }


}
