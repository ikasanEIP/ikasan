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
package org.ikasan.dashboard.ui.mappingconfiguration.action;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ikasan.dashboard.ui.framework.action.Action;
import org.ikasan.dashboard.ui.framework.util.DashboardSessionValueConstants;
import org.ikasan.dashboard.ui.mappingconfiguration.component.MappingConfigurationConfigurationValuesTable;
import org.ikasan.dashboard.ui.mappingconfiguration.util.MappingConfigurationConstants;
import org.ikasan.mapping.model.ManyToManyTargetConfigurationValue;
import org.ikasan.mapping.model.MappingConfiguration;
import org.ikasan.mapping.model.SourceConfigurationValue;
import org.ikasan.mapping.service.MappingManagementService;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.ikasan.systemevent.service.SystemEventService;

import com.vaadin.server.VaadinService;
import com.vaadin.ui.Notification;

/**
 * @author Ikasan Development Team
 *
 */
public class DeleteRowAction implements Action
{
    /** Logger instance */
    private static Logger logger = LoggerFactory.getLogger(DeleteRowAction.class);

    private List<SourceConfigurationValue> sourceConfigurationValues;
    private MappingConfiguration mappingConfiguration;
    private MappingConfigurationConfigurationValuesTable mappingConfigurationConfigurationValuesTable;
    private MappingManagementService mappingConfigurationService;
    private SystemEventService systemEventService;

    /**
     * Constructor
     * 
     * @param sourceConfigurationValue
     * @param mappingConfirutation
     * @param mappingConfigurationConfigurationValuesTable
     */
    public DeleteRowAction(List<SourceConfigurationValue> sourceConfigurationValues,
                           MappingConfiguration mappingConfiguration,
                           MappingConfigurationConfigurationValuesTable mappingConfigurationConfigurationValuesTable,
                           MappingManagementService mappingConfigurationService, SystemEventService systemEventService)
    {
        super();
        this.sourceConfigurationValues = sourceConfigurationValues;
        this.mappingConfiguration = mappingConfiguration;
        this.mappingConfigurationConfigurationValuesTable = mappingConfigurationConfigurationValuesTable;
        this.mappingConfigurationService = mappingConfigurationService;
        this.systemEventService = systemEventService;
    }

    /* (non-Javadoc)
     * @see com.mapping.configuration.ui.action.Action#exectuteAction()
     */
    @Override
    public void exectuteAction()
    {
    	IkasanAuthentication authentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
            	.getAttribute(DashboardSessionValueConstants.USER);
        
        StringBuilder sb =  new StringBuilder("Deleted mapping configuration value(s): [Client=" + mappingConfiguration.getConfigurationServiceClient().getName()
        		+"] [Source Context=" + mappingConfiguration.getSourceContext().getName() + "] [Target Context=" 
        		+ mappingConfiguration.getTargetContext().getName() + "] [Type=" + mappingConfiguration.getConfigurationType().getName()
        		+ "]");
        
        long groupId = -1l;

        for(SourceConfigurationValue sourceConfigurationValue: this.sourceConfigurationValues)
        {
            if(mappingConfiguration.getIsManyToMany())
            {
                groupId = sourceConfigurationValue.getSourceConfigGroupId();
            }
            else
            {
                sb.append(" [Src Value=" + sourceConfigurationValue.getSourceSystemValue()
                        + "] [Tgt Value=" + sourceConfigurationValue.getTargetConfigurationValue()
                        .getTargetSystemValue() + "]");
            }
        }
        
        logger.debug("User: " + authentication.getName() 
                +" attempting to delete: " + this.sourceConfigurationValues.size() + " configuration values.");

        if(mappingConfiguration.getIsManyToMany())
        {
            for (ManyToManyTargetConfigurationValue value : mappingConfigurationConfigurationValuesTable.getManyToManyTargetConfigurationValues())
            {
                if (value.getGroupId().equals(groupId))
                {
                    this.mappingConfigurationConfigurationValuesTable.getDeletedManyToManyTargetConfigurationValues().add(value);
                }
            }
        }

        this.mappingConfiguration.getSourceConfigurationValues().removeAll(this.sourceConfigurationValues);

        try
        {
            this.mappingConfigurationConfigurationValuesTable.save();
            this.mappingConfigurationService.saveMappingConfiguration(this.mappingConfiguration);
            
            systemEventService.logSystemEvent(MappingConfigurationConstants.MAPPING_CONFIGURATION_SERVICE, 
            		sb.toString(), authentication.getName());

            logger.debug("User: " + authentication.getName() 
                + " successfully deleted the following configuration values: " 
                    + this.sourceConfigurationValues);
        }
        catch (Exception e) 
        {
        	logger.error("An error occurred trying to delete a mapping configuration value!", e); 
        	
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);

            Notification.show("An error occurred trying to delete a mapping configuration value!", sw.toString()
                , Notification.Type.ERROR_MESSAGE);
            return;
        }

        for(SourceConfigurationValue value: sourceConfigurationValues)
        {
            this.mappingConfigurationConfigurationValuesTable.removeItem(value);
        }
    }

    /* (non-Javadoc)
     * @see com.mapping.configuration.ui.action.Action#ignoreAction()
     */
    @Override
    public void ignoreAction()
    {
        // Nothing to do here
    }
}
