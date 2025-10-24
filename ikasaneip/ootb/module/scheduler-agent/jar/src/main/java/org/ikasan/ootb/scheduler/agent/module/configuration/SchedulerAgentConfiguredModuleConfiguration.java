/*
 * $Id$
 * $URL$
 *
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 *
 * Distributed under the Modified BSD License.
 * Copyright notice: The copyright for this software and a full listing
 * of individual contributors are as shown in the packaged copyright.txt
 * file.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  - Neither the name of the ORGANIZATION nor the names of its contributors may
 *    be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 */
package org.ikasan.ootb.scheduler.agent.module.configuration;

import org.ikasan.ootb.scheduled.dryrun.configuration.DryRunConfiguredModuleConfiguration;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class SchedulerAgentConfiguredModuleConfiguration extends DryRunConfiguredModuleConfiguration implements Serializable {

    private Map<String, String> flowContextMap = new HashMap<>();
    private Map<String, String> fileWatcherJobMap = new  HashMap();
    private Map<String, String> scheduledJobMap = new  HashMap();


    /**
     * Retrieves the flow context map stored in the configuration.
     *
     * @return A map containing flow context information where the keys represent job names
     * and values represent the corresponding context names.
     */
    public Map<String, String> getFlowContextMap() {
        return flowContextMap;
    }

    /**
     * Sets the flow context map in the configuration. The flow context map stores
     * flow context information where the keys represent job names and values represent
     * the corresponding context names.
     *
     * @param flowContextMap A map containing flow context information with job names as keys
     *                       and context names as values.
     */
    public void setFlowContextMap(Map<String, String> flowContextMap) {
        this.flowContextMap = flowContextMap;
    }

    /**
     * Retrieves the map containing file watcher job information.
     *
     * @return A map where the keys represent file watcher job names and values represent the corresponding job definitions.
     */
    public Map<String, String> getFileWatcherJobMap() {
        return fileWatcherJobMap;
    }

    /**
     * Sets the file watcher job map in the configuration.
     *
     * @param fileWatcherJobMap A map containing file watcher job information where the keys represent
     *                         job names and values represent the corresponding job definitions.
     */
    public void setFileWatcherJobMap(Map<String, String> fileWatcherJobMap) {
        this.fileWatcherJobMap = fileWatcherJobMap;
    }

    /**
     * Retrieves the scheduled job map stored in the configuration.
     *
     * @return A map containing scheduled job information where the keys represent job names
     *         and values represent the corresponding job definitions.
     */
    public Map<String, String> getScheduledJobMap() {
        return scheduledJobMap;
    }

    /**
     * Sets the scheduled job map in the configuration. The scheduled job map stores
     * scheduled job information where the keys represent job names and values represent
     * the corresponding job definitions.
     *
     * @param scheduledJobMap A map containing scheduled job information where the keys represent
     *                        job names and values represent the corresponding job definitions.
     */
    public void setScheduledJobMap(Map<String, String> scheduledJobMap) {
        this.scheduledJobMap = scheduledJobMap;
    }
}
