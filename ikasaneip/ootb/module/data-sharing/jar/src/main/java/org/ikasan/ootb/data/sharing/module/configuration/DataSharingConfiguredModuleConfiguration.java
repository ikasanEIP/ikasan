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
package org.ikasan.ootb.data.sharing.module.configuration;

import org.ikasan.module.ConfiguredModuleConfiguration;
import org.ikasan.spec.configuration.Masked;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class DataSharingConfiguredModuleConfiguration extends ConfiguredModuleConfiguration implements Serializable {
    private String upstreamDashboard;
    private String upstreamDashboardUsername;
    @Masked
    private String upstreamDashboardPassword;
    private Map<String, String> downStreamDashboards = new HashMap<>();
    private Map<String, String> downStreamDashboardUsernames = new HashMap<>();
    @Masked
    private Map<String, String> downStreamDashboardPasswords = new HashMap<>();
    private Map<String, String> downStreamDashboardEntities = new HashMap<>();
    private Map<String, String> downStreamDashboardModules = new HashMap<>();
    private Map<String, String> downStreamDashboardEntityCronExpressions = new HashMap<>();

    public String getUpstreamDashboard() {
        return upstreamDashboard;
    }

    public void setUpstreamDashboard(String upstreamDashboard) {
        this.upstreamDashboard = upstreamDashboard;
    }

    public String getUpstreamDashboardUsername() {
        return upstreamDashboardUsername;
    }

    public void setUpstreamDashboardUsername(String upstreamDashboardUsername) {
        this.upstreamDashboardUsername = upstreamDashboardUsername;
    }

    public String getUpstreamDashboardPassword() {
        return upstreamDashboardPassword;
    }

    public void setUpstreamDashboardPassword(String upstreamDashboardPassword) {
        this.upstreamDashboardPassword = upstreamDashboardPassword;
    }

    public Map<String, String> getDownStreamDashboards() {
        return downStreamDashboards;
    }

    public void setDownStreamDashboards(Map<String, String> downStreamDashboards) {
        this.downStreamDashboards = downStreamDashboards;
    }

    public Map<String, String> getDownStreamDashboardUsernames() {
        return downStreamDashboardUsernames;
    }

    public void setDownStreamDashboardUsernames(Map<String, String> downStreamDashboardUsernames) {
        this.downStreamDashboardUsernames = downStreamDashboardUsernames;
    }

    public Map<String, String> getDownStreamDashboardPasswords() {
        return downStreamDashboardPasswords;
    }

    public void setDownStreamDashboardPasswords(Map<String, String> downStreamDashboardPasswords) {
        this.downStreamDashboardPasswords = downStreamDashboardPasswords;
    }

    public Map<String, String> getDownStreamDashboardEntities() {
        return downStreamDashboardEntities;
    }

    public void setDownStreamDashboardEntities(Map<String, String> downStreamDashboardEntities) {
        this.downStreamDashboardEntities = downStreamDashboardEntities;
    }

    public Map<String, String> getDownStreamDashboardModules() {
        return downStreamDashboardModules;
    }

    public void setDownStreamDashboardModules(Map<String, String> downStreamDashboardModules) {
        this.downStreamDashboardModules = downStreamDashboardModules;
    }

    public Map<String, String> getDownStreamDashboardEntityCronExpressions() {
        return downStreamDashboardEntityCronExpressions;
    }

    public void setDownStreamDashboardEntityCronExpressions(Map<String, String> downStreamDashboardEntityCronExpressions) {
        this.downStreamDashboardEntityCronExpressions = downStreamDashboardEntityCronExpressions;
    }
}
