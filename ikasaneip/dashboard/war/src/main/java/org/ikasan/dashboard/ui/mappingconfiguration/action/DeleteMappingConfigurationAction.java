/*
 * $Id: DeleteMappingConfigurationAction.java 40677 2014-11-07 17:14:59Z stewmi $
 * $URL: https://svc-vcs-prd.uk.mizuho-sc.com:18080/svn/architecture/cmi2/trunk/projects/mappingConfigurationUI/war/src/main/java/org/ikasan/mapping/configuration/ui/action/DeleteMappingConfigurationAction.java $
 *
 * ====================================================================
 *
 * Copyright (c) 2000-2011 by Mizuho International plc.
 * All Rights Reserved.
 *
 * ====================================================================
 *
 */
package org.ikasan.dashboard.ui.mappingconfiguration.action;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.log4j.Logger;
import org.ikasan.dashboard.ui.framework.action.Action;
import org.ikasan.dashboard.ui.framework.util.UserDetailsHelper;
import org.ikasan.dashboard.ui.mappingconfiguration.component.SearchResultsTable;
import org.ikasan.dashboard.ui.mappingconfiguration.util.MappingConfigurationUISessionValueConstants;
import org.ikasan.mapping.model.MappingConfiguration;
import org.ikasan.mapping.service.MappingConfigurationService;

import com.vaadin.server.VaadinService;
import com.vaadin.ui.Notification;

/**
 * @author CMI2 Development Team
 *
 */
public class DeleteMappingConfigurationAction implements Action
{
    /** Logger instance */
    private static Logger logger = Logger.getLogger(DeleteRowAction.class);
    
    private Long mappingConfigurationId;
    private SearchResultsTable searchResultsTable;
    private MappingConfigurationService mappingConfigurationService;

    /**
     * Constructor
     * 
     * @param sourceConfigurationValue
     * @param mappingConfirutation
     * @param mappingConfigurationConfigurationValuesTable
     */
    public DeleteMappingConfigurationAction(Long mappingConfigurationId, SearchResultsTable searchResultsTable,
            MappingConfigurationService mappingConfigurationService)
    {
        super();

        this.mappingConfigurationId = mappingConfigurationId;
        this.searchResultsTable = searchResultsTable;
        this.mappingConfigurationService = mappingConfigurationService;
    }

    /* (non-Javadoc)
     * @see com.mapping.configuration.ui.action.Action#exectuteAction()
     */
    @Override
    public void exectuteAction()
    {
        try
        {
            MappingConfiguration mappingConfiguration = this.mappingConfigurationService
                    .getMappingConfigurationById(this.mappingConfigurationId);
            
            this.mappingConfigurationService.deleteMappingConfiguration(mappingConfiguration);
            this.searchResultsTable.removeItem(this.mappingConfigurationId);

            UserDetailsHelper userDetailsHelper = (UserDetailsHelper)VaadinService.getCurrentRequest().getWrappedSession()
                    .getAttribute(MappingConfigurationUISessionValueConstants.USER);

            logger.info("User: " + userDetailsHelper.getUserDetails().getUsername() 
                + " successfully deleted the following Mapping Configuration: " 
                    + mappingConfiguration);
        }
        catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);

            Notification.show("Cauget exception trying to remove a Mapping Configuration!", sw.toString()
                , Notification.Type.ERROR_MESSAGE);
            return;
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
