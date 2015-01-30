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
package org.ikasan.setup.persistence.service;

import org.apache.log4j.Logger;
import org.ikasan.setup.persistence.dao.ProviderDAO;

import java.util.List;

/**
 * Simple persistence creation utility class for the initial creation
 * of underlying persistence tables.
 *
 * Ikasan Development Team
 */
public class PersistenceServiceImpl implements PersistenceService
{
    /**
     * logger
     */
    private static Logger logger = Logger.getLogger(PersistenceServiceImpl.class);

    private static String WIRETAP = "wiretap";
    private static String FILTER = "filter";
    private static String FLOW_EVENT_TRIGGER = "flowEventTrigger";
    private static String FLOW_EVENT_TRIGGER_PARAMETERS = "flowEventTriggerParameters";
    private static String CONFIGURATION = "configuration";
    private static String CONFIGURATION_PARAMETER = "configurationParameter";
    private static String CONF_PARAM_BOOLEAN = "confParamBoolean";
    private static String CONF_PARAM_INTEGER = "confParamInteger";
    private static String CONF_PARAM_LIST = "confParamList";
    private static String CONF_PARAM_LIST_STRING = "confParamListString";
    private static String CONF_PARAM_LONG = "confParamLong";
    private static String CONF_PARAM_MAP = "confParamMap";
    private static String CONF_PARAM_MAP_STRING = "confParamMapString";
    private static String CONF_PARAM_STRING = "confParamString";

    private static String SYSTEM_EVENT = "systemEvent";
    private static String MODULE_STARTUP = "moduleStartup";
    private static String CONSOLE_MODULE = "consoleModule";
    private static String CONSOLE_POINT_TO_POINT_FLOW = "consolePointToPointFlow";
    private static String CONSOLE_POINT_TO_POINT_FLOW_PROFILE = "consolePointToPointFlowProfile";

    private static String USERS = "users";
    private static String AUTHORITIES = "authorities";
    private static String USERS_AUTHORITIES = "usersAuthorities";

    private static String DEFAULT_AUTHORITIES = "defaultAuthorities";
    private static String DEFAULT_ADMIN_USER = "defaultAdminUser";
    private static String ANY_ADMIN_USER = "anyAdminUser";

    private static String VERSION = "version";
    private static String VERSION_ENTRY = "version.entry";
    private static String EXCLUSION_EVENT = "exclusionEvent";

    /** handle to under DAO layer */
    private ProviderDAO providerDAO;

    /**
     * Constructor
     * @param providerDAO
     */
    public PersistenceServiceImpl(ProviderDAO providerDAO) {
        this.providerDAO = providerDAO;
        if (providerDAO == null) {
            throw new IllegalArgumentException("providerDAO cannot be 'null'");
        }
    }

    @Override
    public boolean adminAccountExists()
    {
        try
        {
            List<String> admins = this.providerDAO.find(ANY_ADMIN_USER);
            if(admins != null && admins.size() > 0)
            {
                return true;
            }
        }
        catch(RuntimeException e)
        {
            logger.error("providerDAO failed", e);
        }

        return false;
    }

    @Override
    public String getVersion()
    {
        return this.providerDAO.getRuntimeVersion();
    }

    @Override
    public void createPersistence() {

        this.providerDAO.create(WIRETAP);
        this.providerDAO.create(FILTER);
        this.providerDAO.create(VERSION);
        this.providerDAO.create(VERSION_ENTRY);
        this.providerDAO.create(FLOW_EVENT_TRIGGER);
        this.providerDAO.create(FLOW_EVENT_TRIGGER_PARAMETERS);
        this.providerDAO.create(CONFIGURATION);
        this.providerDAO.create(CONFIGURATION_PARAMETER);
        this.providerDAO.create(CONF_PARAM_BOOLEAN);
        this.providerDAO.create(CONF_PARAM_INTEGER);
        this.providerDAO.create(CONF_PARAM_LIST);
        this.providerDAO.create(CONF_PARAM_LIST_STRING);
        this.providerDAO.create(CONF_PARAM_LONG);
        this.providerDAO.create(CONF_PARAM_MAP);
        this.providerDAO.create(CONF_PARAM_MAP_STRING);
        this.providerDAO.create(CONF_PARAM_STRING);
        this.providerDAO.create(SYSTEM_EVENT);
        this.providerDAO.create(MODULE_STARTUP);
        this.providerDAO.create(CONSOLE_MODULE);
        this.providerDAO.create(CONSOLE_POINT_TO_POINT_FLOW_PROFILE);
        this.providerDAO.create(CONSOLE_POINT_TO_POINT_FLOW);
        this.providerDAO.create(USERS);
        this.providerDAO.create(AUTHORITIES);
        this.providerDAO.create(USERS_AUTHORITIES);
        this.providerDAO.create(EXCLUSION_EVENT);
    }

    @Override
    public void createAdminAccount()
    {
        this.providerDAO.create(DEFAULT_AUTHORITIES);
        this.providerDAO.create(DEFAULT_ADMIN_USER);
    }
}
