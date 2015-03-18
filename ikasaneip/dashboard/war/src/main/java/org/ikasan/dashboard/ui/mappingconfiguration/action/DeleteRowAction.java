/*
 * $Id: DeleteRowAction.java 40677 2014-11-07 17:14:59Z stewmi $
 * $URL: https://svc-vcs-prd.uk.mizuho-sc.com:18080/svn/architecture/cmi2/trunk/projects/mappingConfigurationUI/war/src/main/java/org/ikasan/mapping/configuration/ui/action/DeleteRowAction.java $
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
import java.util.List;

import org.apache.log4j.Logger;
import org.ikasan.dashboard.ui.framework.action.Action;
import org.ikasan.dashboard.ui.framework.util.UserDetailsHelper;
import org.ikasan.dashboard.ui.mappingconfiguration.component.MappingConfigurationConfigurationValuesTable;
import org.ikasan.dashboard.ui.mappingconfiguration.util.MappingConfigurationUISessionValueConstants;

import com.mizuho.cmi2.mappingConfiguration.model.MappingConfiguration;
import com.mizuho.cmi2.mappingConfiguration.model.SourceConfigurationValue;
import com.mizuho.cmi2.mappingConfiguration.service.MappingConfigurationService;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Notification;

/**
 * @author CMI2 Development Team
 *
 */
public class DeleteRowAction implements Action
{
    /** Logger instance */
    private static Logger logger = Logger.getLogger(DeleteRowAction.class);

    private List<SourceConfigurationValue> sourceConfigurationValues;
    private MappingConfiguration mappingConfiguration;
    private MappingConfigurationConfigurationValuesTable mappingConfigurationConfigurationValuesTable;
    MappingConfigurationService mappingConfigurationService;

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
            MappingConfigurationService mappingConfigurationService)
    {
        super();
        this.sourceConfigurationValues = sourceConfigurationValues;
        this.mappingConfiguration = mappingConfiguration;
        this.mappingConfigurationConfigurationValuesTable = mappingConfigurationConfigurationValuesTable;
        this.mappingConfigurationService = mappingConfigurationService;
    }

    /* (non-Javadoc)
     * @see com.mapping.configuration.ui.action.Action#exectuteAction()
     */
    @Override
    public void exectuteAction()
    {
        UserDetailsHelper userDetailsHelper = (UserDetailsHelper)VaadinService.getCurrentRequest().getWrappedSession()
                .getAttribute(MappingConfigurationUISessionValueConstants.USER);

        logger.info("User: " + userDetailsHelper.getUserDetails().getUsername() 
            +" attempting to delete: " + this.sourceConfigurationValues.size() + " configuration values.");

        this.mappingConfiguration.getSourceConfigurationValues().removeAll(this.sourceConfigurationValues);

        try
        {
            this.mappingConfigurationConfigurationValuesTable.save();
            this.mappingConfigurationService.saveMappingConfiguration(this.mappingConfiguration);

            logger.info("User: " + userDetailsHelper.getUserDetails().getUsername() 
                + " successfully deleted the following configuration values: " 
                    + this.sourceConfigurationValues);
        }
        catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);

            Notification.show("Cauget exception trying to remove a Mapping Configuration value!", sw.toString()
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
