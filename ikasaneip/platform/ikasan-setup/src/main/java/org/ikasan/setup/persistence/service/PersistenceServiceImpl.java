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

    private static String SECURITY_PRINCIAL = "securityPrincipal";
    private static String SECURITY_ROLE = "securityRole";
    private static String SECUIRTY_POLICY = "securityPolicy";
    private static String PRINCIPAL_ROLE = "principalRole";
    private static String ROLE_POLICY = "rolePolicy";
    private static String USER_PRINCIPAL = "userPrincipal";
    private static String POLICY_LINK = "policyLink";
    private static String POLICY_LINK_TYPE = "policyLinkType";

    private static String MAPPING_CONFIGURATION_TYPE = "mappingConfigurationType";
    private static String MAPPING_CONFIGURATION_CONTEXT = "mappingConfigurationContext";
    private static String MAPPING_CONFIGURATION_CLIENT = "mappingConfigurationClient";
    private static String MAPPING_CONFIGURATION = "mappingConfiguration";
    private static String MAPPING_TARGET_VALUE = "mappingTargetValue";
    private static String MAPPING_SOURCE_VALUE = "mappingSourceValue";
    private static String MAPPING_KEY_LOCATION_QUERY = "mappingKeyLocationQuery";
    private static String MAPPING_SOURCE_CONFIG_GROUP_SEQ = "mappingSourceConfigGroupSeq";

    private static String FILE_TRANSFER_CHECKSUM_COMMAND = "checksumCommand";
    private static String FILE_TRANSFER_CLEANUP_CHUNKS_COMMAND = "cleanupChunksCommand";
    private static String FILE_TRANSFER_DELIVERY_BATCH_COMMAND = "deliverBatchCommand";
    private static String FILE_TRANSFER_DELIVERY_FILE_COMMAND = "deliverFileCommand";
    private static String FILE_TRANSFER_FILE_CHUNK_HEADER_COMMAND = "fileChunkHeader";
    private static String FILE_TRANSFER_FILE_CHUNK_COMMAND = "fileChunk";
    private static String FILE_TRANSFER_FILE_FILTER = "fileFilter";
    private static String FILE_TRANSFER_RETRIEVE_FILE_COMMAND = "retrieveFileCommand";
    private static String FILE_TRANSFER_TRANSACTIONAL_RESOURCE_COMMAND = "transactionalResourceCommand";
    private static String FILE_TRANSFER_XID = "xid";
    
    private static String SERVER = "server";
    private static String IKASAN_MODULE = "ikasanModule";
    private static String FLOW = "flow";
    private static String COMPONENT = "component";
    private static String BUSINESS_STREAM = "businessStream";
    private static String BUSINESS_STREAM_FLOW = "businessStreamFlow";
    private static String USER_BUSINESS_STREAM = "userBusinessStream";
    private static String EXCLUSION_EVENT_ACTION = "exclusionEventAction";


    private static String VERSION = "version";
    private static String VERSION_ENTRY = "version.entry";
    private static String EXCLUSION_EVENT = "exclusionEvent";
    private static String ERROR_OCCURRENCE = "errorOccurrence";

    private static String USERS_TABLE_EXISTS = "usersTableExists";
    private static String AUTHORITIES_TABLE_EXISTS = "authoritiesTableExists";
    private static String USERS_AUTHORITIES_TABLE_EXISTS = "usersAuthoritiesTableExists";
    
    private static String AUTHENTICATION_METHOD ="authenticationMethod";
    
    private static final String DEFAULT_PRINCIPAL = "defaultPrincipal";

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
	public boolean userTablesExist() 
    {
    	try
    	{
    		List<String> usersTableExists = this.providerDAO.find(USERS_TABLE_EXISTS);
    	}
    	catch(Exception e)
    	{
    		return false;
    	}
    	
    	return true;
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
        this.providerDAO.create(ERROR_OCCURRENCE);
        this.providerDAO.create(SECURITY_PRINCIAL);
        this.providerDAO.create(SECURITY_ROLE);
        this.providerDAO.create(POLICY_LINK_TYPE);
        this.providerDAO.create(POLICY_LINK);
        this.providerDAO.create(SECUIRTY_POLICY);
        this.providerDAO.create(PRINCIPAL_ROLE);
        this.providerDAO.create(ROLE_POLICY);
        this.providerDAO.create(USER_PRINCIPAL);
        this.providerDAO.create(MAPPING_CONFIGURATION_TYPE);
        this.providerDAO.create(MAPPING_CONFIGURATION_CONTEXT);
        this.providerDAO.create(MAPPING_CONFIGURATION_CLIENT);
        this.providerDAO.create(MAPPING_CONFIGURATION);
        this.providerDAO.create(MAPPING_TARGET_VALUE);
        this.providerDAO.create(MAPPING_SOURCE_VALUE);
        this.providerDAO.create(MAPPING_KEY_LOCATION_QUERY);
        this.providerDAO.create(MAPPING_SOURCE_CONFIG_GROUP_SEQ);
        this.providerDAO.create(SERVER);
        this.providerDAO.create(IKASAN_MODULE);
        this.providerDAO.create(FLOW);
        this.providerDAO.create(COMPONENT);
        this.providerDAO.create(BUSINESS_STREAM);
        this.providerDAO.create(BUSINESS_STREAM_FLOW);
        this.providerDAO.create(USER_BUSINESS_STREAM);
        this.providerDAO.create(EXCLUSION_EVENT_ACTION);
        this.providerDAO.create(AUTHENTICATION_METHOD);
    }

    @Override
    public void createAdminAccount()
    {
        this.providerDAO.create(DEFAULT_AUTHORITIES);
        this.providerDAO.create(DEFAULT_ADMIN_USER);
        this.providerDAO.create(DEFAULT_PRINCIPAL);
    }

    @Override
    public void createFileTransferPersistence() {

        this.providerDAO.create(FILE_TRANSFER_CHECKSUM_COMMAND);
        this.providerDAO.create(FILE_TRANSFER_CLEANUP_CHUNKS_COMMAND);
        this.providerDAO.create(FILE_TRANSFER_DELIVERY_BATCH_COMMAND);
        this.providerDAO.create(FILE_TRANSFER_DELIVERY_FILE_COMMAND);
        this.providerDAO.create(FILE_TRANSFER_FILE_CHUNK_HEADER_COMMAND);
        this.providerDAO.create(FILE_TRANSFER_FILE_CHUNK_COMMAND);
        this.providerDAO.create(FILE_TRANSFER_FILE_FILTER);
        this.providerDAO.create(FILE_TRANSFER_RETRIEVE_FILE_COMMAND);
        this.providerDAO.create(FILE_TRANSFER_XID);
        this.providerDAO.create(FILE_TRANSFER_TRANSACTIONAL_RESOURCE_COMMAND);

    }

}
