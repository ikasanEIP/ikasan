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
package org.ikasan.security.service;

import java.util.List;

/**
 * Service interface for provisioning Ikasan entity security.
 *
 * <p>Defines the contract for managing role assignments to modules and job plans,
 * enabling fine-grained access control over integration modules and scheduled job plans.
 *
 * @author Ikasan Development Team
 */
public interface EntitySecurityProvisionService {

    /**
     * Associates the specified module with a list of roles to enable or restrict
     * access control for the module based on the provided roles. If any of the roles
     * in the list do not exist or cannot be assigned, an appropriate exception is thrown.
     *
     * @param moduleName the name of the module to associate with the roles
     * @param roleNames the list of role names to assign to the module
     * @throws EntitySecurityProvisionServiceException if an error occurs during the
     *         provisioning process or if the roles could not be assigned to the module
     */
    void assignModuleToRoles(String moduleName, List<String> roleNames)
        throws EntitySecurityProvisionServiceException;

    /**
     * Assigns a job plan to multiple roles.
     *
     * Associates the specified job plan with the provided list of roles, enabling access control
     * for the job plan based on the assigned roles. If any role in the list does not exist, it is
     * skipped and a warning is logged.
     *
     * @param jobPlanName the name of the job plan to assign
     * @param roleNames the list of role names to assign the job plan to
     * @throws EntitySecurityProvisionServiceException if an error occurs during the provisioning process
     */
    void assignJobPlanToRoles(String jobPlanName, List<String> roleNames)
        throws EntitySecurityProvisionServiceException;
}
