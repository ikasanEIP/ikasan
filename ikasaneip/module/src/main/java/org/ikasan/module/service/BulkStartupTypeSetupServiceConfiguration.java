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

import org.ikasan.spec.module.StartupType;

import java.util.List;

/**
 * This configuration makes it easier to setup a new module, as means the user doesnt have to manually set up the
 * startup type for each flow in  environment. In the most common case only "defaultStatupType=AUTOMATIC" should be set
 * this will change the startup type to AUTOMATIC for every flow and persist to the database for every flow
 * that does not already have a persistent startupType
 */
public class BulkStartupTypeSetupServiceConfiguration {

    /**
     * If set to true this will overwrite what ever has been persisted in the startup type table previously
     * for a given flow. By default db overwrite wont be allowed so startup types set by users cant be
     * overwritten
     */
    private Boolean allowDbOverwrite;

    /**
     * All flows will be set to this startup type by default if they have not been set already or allowDbOverwrite is
     * true, they are not in the flowsNotToSet collecttion or the flowStartupTypes list
     */
    private StartupType defaultStartupType;

    /**
     * Anything in this list wont be set
     */
    private List<String> flowsNotToSet;

    /**
     * Allows startup types to be set on specific flows
     */
    private List<FlowStartupTypeConfiguration>flowStartupTypes;


    /**
     * Will delete all persisted startup types on the database. USE WITH CAUTION !
     */
    private Boolean deleteAll;


    public Boolean getAllowDbOverwrite() {
        return allowDbOverwrite;
    }

    public void setAllowDbOverwrite(Boolean allowDbOverwrite) {
        this.allowDbOverwrite = allowDbOverwrite;
    }

    public StartupType getDefaultStartupType() {
        return defaultStartupType;
    }

    public void setDefaultStartupType(StartupType defaultStartupType) {
        this.defaultStartupType = defaultStartupType;
    }

    public List<String> getFlowsNotToSet() {
        return flowsNotToSet;
    }

    public void setFlowsNotToSet(List<String> flowsNotToSet) {
        this.flowsNotToSet = flowsNotToSet;
    }

    public List<FlowStartupTypeConfiguration> getFlowStartupTypes() {
        return flowStartupTypes;
    }

    public void setFlowStartupTypes(List<FlowStartupTypeConfiguration> flowStartupTypes) {
        this.flowStartupTypes = flowStartupTypes;
    }

    public Boolean getDeleteAll() {
        return deleteAll;
    }

    public void setDeleteAll(Boolean deleteAll) {
        this.deleteAll = deleteAll;
    }
}
