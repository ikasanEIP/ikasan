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
package org.ikasan.dashboard.ui.mappingconfiguration.data;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.log4j.Logger;
import org.ikasan.dashboard.ui.framework.group.RefreshGroup;
import org.ikasan.dashboard.ui.framework.util.DashboardSessionValueConstants;
import org.ikasan.dashboard.ui.mappingconfiguration.util.MappingConfigurationConstants;
import org.ikasan.mapping.model.ConfigurationServiceClient;
import org.ikasan.mapping.service.MappingManagementService;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.ikasan.systemevent.service.SystemEventService;

import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Field;
import com.vaadin.ui.Notification;

/**
 * @author Ikasan Development Team
 *
 */
public class NewClientFieldGroup extends FieldGroup
{
    /** Logger instance */
    private static Logger logger = Logger.getLogger(NewClientFieldGroup.class);

    private static final long serialVersionUID = -4171297865032531886L;

    public static final String NAME = "name";

    private RefreshGroup refreshGroup;
    private MappingManagementService mappingConfigurationService;
    private SystemEventService systemEventService;

    /**
     * Constructor
     * 
     * @param refreshGroup
     * @param mappingConfigurationService
     */
    public NewClientFieldGroup(RefreshGroup refreshGroup, MappingManagementService mappingConfigurationService,
    		SystemEventService systemEventService)
    {
        super();
        this.refreshGroup = refreshGroup;
        this.mappingConfigurationService = mappingConfigurationService;
        this.systemEventService = systemEventService;
    }

    /**
     * Constructor
     * 
     * @param itemDataSource
     * @param refreshGroup
     * @param mappingConfigurationService
     */
    public NewClientFieldGroup(Item itemDataSource, RefreshGroup refreshGroup, MappingManagementService mappingConfigurationService,
    		SystemEventService systemEventService)
    {
        super(itemDataSource);
        this.refreshGroup = refreshGroup;
        this.mappingConfigurationService = mappingConfigurationService;
        this.systemEventService = systemEventService;
    }

    /* (non-Javadoc)
     * @see com.vaadin.data.fieldgroup.FieldGroup#commit()
     */
    @Override
    public void commit() throws CommitException
    {
        Field<String> name = (Field<String>) this.getField(NAME);


        ConfigurationServiceClient client = new ConfigurationServiceClient();
        client.setName(name.getValue());

        try
        {
            this.mappingConfigurationService.saveConfigurationServiceClient(client);

            IkasanAuthentication authentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
                	.getAttribute(DashboardSessionValueConstants.USER);

            systemEventService.logSystemEvent(MappingConfigurationConstants.MAPPING_CONFIGURATION_SERVICE, 
            		"Created new mapping configuration client: " + client.getName(), authentication.getName());

            logger.debug("User: " + authentication.getName() 
                + " added a new Mapping Configuration Client:  " 
                    + client);
        }
        catch (Exception e)
        {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);

            Notification.show("Cauget exception trying to save a new Client!", sw.toString()
                , Notification.Type.ERROR_MESSAGE);
        }

        this.refreshGroup.refresh();
    }
}
