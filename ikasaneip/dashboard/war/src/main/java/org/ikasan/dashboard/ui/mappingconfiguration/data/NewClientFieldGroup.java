/*
 * $Id: NewClientFieldGroup.java 40677 2014-11-07 17:14:59Z stewmi $
 * $URL: https://svc-vcs-prd.uk.mizuho-sc.com:18080/svn/architecture/cmi2/trunk/projects/mappingConfigurationUI/war/src/main/java/org/ikasan/mapping/configuration/ui/data/NewClientFieldGroup.java $
 *
 * ====================================================================
 *
 * Copyright (c) 2000-2011 by Mizuho International plc.
 * All Rights Reserved.
 *
 * ====================================================================
 *
 */
package org.ikasan.dashboard.ui.mappingconfiguration.data;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.log4j.Logger;
import org.ikasan.dashboard.ui.framework.group.RefreshGroup;
import org.ikasan.dashboard.ui.framework.util.UserDetailsHelper;
import org.ikasan.dashboard.ui.mappingconfiguration.util.MappingConfigurationUISessionValueConstants;

import com.mizuho.cmi2.mappingConfiguration.model.ConfigurationServiceClient;
import com.mizuho.cmi2.mappingConfiguration.service.MappingConfigurationService;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Field;
import com.vaadin.ui.Notification;

/**
 * @author CMI2 Development Team
 *
 */
public class NewClientFieldGroup extends FieldGroup
{
    /** Logger instance */
    private static Logger logger = Logger.getLogger(NewClientFieldGroup.class);

    private static final long serialVersionUID = -4171297865032531886L;

    public static final String NAME = "name";
    public static final String KEY_LOCATION_QUERY_PROCESSOR_TYPE = "keyLocationQueryProcessorType";

    private RefreshGroup refreshGroup;
    private MappingConfigurationService mappingConfigurationService;

    /**
     * Constructor
     * 
     * @param refreshGroup
     * @param mappingConfigurationService
     */
    public NewClientFieldGroup(RefreshGroup refreshGroup, MappingConfigurationService mappingConfigurationService)
    {
        super();
        this.refreshGroup = refreshGroup;
        this.mappingConfigurationService = mappingConfigurationService;
    }

    /**
     * Constructor
     * 
     * @param itemDataSource
     * @param refreshGroup
     * @param mappingConfigurationService
     */
    public NewClientFieldGroup(Item itemDataSource, RefreshGroup refreshGroup, MappingConfigurationService mappingConfigurationService)
    {
        super(itemDataSource);
        this.refreshGroup = refreshGroup;
        this.mappingConfigurationService = mappingConfigurationService;
    }

    /* (non-Javadoc)
     * @see com.vaadin.data.fieldgroup.FieldGroup#commit()
     */
    @Override
    public void commit() throws CommitException
    {
        Field<String> name = (Field<String>) this.getField(NAME);
        Field<String> keyLocationQueryProcessorType = (Field<String>) this.getField(KEY_LOCATION_QUERY_PROCESSOR_TYPE);

        ConfigurationServiceClient client = new ConfigurationServiceClient();
        client.setKeyLocationQueryProcessorType(keyLocationQueryProcessorType.getValue());
        client.setName(name.getValue());

        try
        {
            this.mappingConfigurationService.saveConfigurationServiceClient(client);

            UserDetailsHelper userDetailsHelper = (UserDetailsHelper)VaadinService.getCurrentRequest().getWrappedSession()
                    .getAttribute(MappingConfigurationUISessionValueConstants.USER);

            logger.info("User: " + userDetailsHelper.getUserDetails().getUsername() 
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
