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

import org.ikasan.spec.trigger.Trigger;
import org.ikasan.spec.trigger.TriggerRelationship;
import org.ikasan.trigger.model.TriggerImpl;
import org.ikasan.wiretap.listener.JobAwareFlowEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class WiretapTriggerSetupService {

    private final WiretapTriggerSetupServiceConfiguration configuration;

    private final JobAwareFlowEventListener wiretapTriggerService;

    private final String WIRETAP_JOB_NAME = "wiretapJob";

    private final String TIME_TO_LIVE_PARAMETER_NAME = "timeToLive";

    public WiretapTriggerSetupService(WiretapTriggerSetupServiceConfiguration configuration,
                                      JobAwareFlowEventListener wiretapTriggerService) {
        this.configuration = configuration;
        this.wiretapTriggerService = wiretapTriggerService;
    }

    public void setup(String moduleName) {
        if (configuration != null) {
            if (Boolean.TRUE.equals(configuration.getDeleteAllTriggers())) {
                logger.warn("!! Deleting all wiretap triggers on module [{}] !!", moduleName);
                List<Trigger> triggers = wiretapTriggerService.getTriggers().stream().filter(wt->
                    WIRETAP_JOB_NAME.equals(wt.getJobName())).collect(Collectors.toList());
                for (Trigger trigger : triggers) {
                    wiretapTriggerService.deleteDynamicTrigger(trigger.getId());
                }
            }
            if (configuration.getTriggers() != null) {
                for (WiretapTriggerConfiguration trigger : configuration.getTriggers()) {
                    switch (trigger.getAction()) {
                        case INSERT:
                            insertTrigger(trigger, moduleName);
                            break;
                        case UPDATE:
                            updateTrigger(trigger, moduleName);
                            break;
                        case DELETE:
                            deleteTrigger(trigger, moduleName);
                            break;
                    }
                }
            }
        }

    }

    private static Logger logger = LoggerFactory.getLogger(WiretapTriggerSetupService.class);

    private void deleteTrigger(WiretapTriggerConfiguration triggerConfig, String moduleName) {
        Optional<Trigger> trigger = lookupTrigger(triggerConfig, moduleName);
        if (trigger.isPresent()) {
            logger.warn("DELETING Wiretap trigger [{}]" +
                " for module [{}]", triggerConfig, moduleName);
            wiretapTriggerService.deleteDynamicTrigger(trigger.get().getId());
        } else {
            logger.warn("NOT DELETING Wiretap Trigger config [{}] as doesnt exist in db for module [{}]",
                triggerConfig, moduleName);
        }
    }

    private void updateTrigger(WiretapTriggerConfiguration triggerConfig, String moduleName) {
        Optional<Trigger> trigger = lookupTrigger(triggerConfig, moduleName);
        if (trigger.isPresent()) {
            logger.warn("UPDATING Wiretap trigger [{}]" +
                " for module [{}]", triggerConfig, moduleName);
            TriggerImpl triggerImpl = (TriggerImpl) (trigger.get());
            Map<String, String> params = triggerImpl.getParams();
            params.put(TIME_TO_LIVE_PARAMETER_NAME, triggerConfig.getTimeToLive());
            saveAndNotifyDashboard(triggerImpl);
        } else {
            logger.warn("NOT UPDATING Wiretap Trigger [{}] as doesnt exist in db for module [{}]",
                triggerConfig, moduleName);
        }
    }

    private void saveAndNotifyDashboard(TriggerImpl triggerImpl) {
        try {
            wiretapTriggerService.addDynamicTrigger(triggerImpl);
        } catch (org.springframework.web.client.RestClientException rce){
            logger.error("There was an exception publishing the module meta data to the dashboard on " +
                "the wiretap trigger save. PLEASE INVESTIGATE !!", rce);
        }
    }

    private void insertTrigger(WiretapTriggerConfiguration triggerConfig, String moduleName) {
        Optional<Trigger> trigger = lookupTrigger(triggerConfig, moduleName);
        if (trigger.isPresent()) {
            logger.warn("NOT INSERTING Wiretap Trigger [{}] as alreay exists in db" +
                " for module [{}]", triggerConfig, moduleName);
        } else {
            logger.info("INSERTING Wiretap Trigger [{}] for module [{}]", triggerConfig,
                moduleName);
            saveAndNotifyDashboard(new TriggerImpl(moduleName,
                triggerConfig.getFlowName(), triggerConfig.getRelationship().toUpperCase(),
                WIRETAP_JOB_NAME, triggerConfig.getComponentName(),
                new HashMap<>() {{
                    put(TIME_TO_LIVE_PARAMETER_NAME, triggerConfig.getTimeToLive());
                }}
            ));
        }

    }

    private Optional<Trigger> lookupTrigger(WiretapTriggerConfiguration triggerConfig, String moduleName) {
        List<Trigger> triggers = wiretapTriggerService.getTriggers(moduleName,
            triggerConfig.getFlowName(), TriggerRelationship.valueOf(triggerConfig.getRelationship().toUpperCase()),
            triggerConfig.getComponentName());
        return triggers.stream().filter(t -> WIRETAP_JOB_NAME.equals(t.getJobName())).findFirst();
    }
}
