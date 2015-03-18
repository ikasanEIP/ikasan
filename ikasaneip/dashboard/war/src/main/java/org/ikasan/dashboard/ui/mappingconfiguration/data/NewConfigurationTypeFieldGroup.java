/*
 * $Id: NewConfigurationTypeFieldGroup.java 40677 2014-11-07 17:14:59Z stewmi $
 * $URL: https://svc-vcs-prd.uk.mizuho-sc.com:18080/svn/architecture/cmi2/trunk/projects/mappingConfigurationUI/war/src/main/java/org/ikasan/mapping/configuration/ui/data/NewConfigurationTypeFieldGroup.java $
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

import org.apache.log4j.Logger;
import org.ikasan.dashboard.ui.framework.group.RefreshGroup;
import org.ikasan.dashboard.ui.framework.util.UserDetailsHelper;
import org.ikasan.dashboard.ui.mappingconfiguration.util.MappingConfigurationUISessionValueConstants;

import com.mizuho.cmi2.mappingConfiguration.model.ConfigurationType;
import com.mizuho.cmi2.mappingConfiguration.service.MappingConfigurationService;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Field;

/**
 * @author CMI2 Development Team
 *
 */
public class NewConfigurationTypeFieldGroup extends FieldGroup
{
    /** Logger instance */
    private static Logger logger = Logger.getLogger(NewConfigurationTypeFieldGroup.class);

    private static final long serialVersionUID = -6584144145939855353L;

    public static final String NAME = "name";
    public static final String DESCRIPTION = "description";

    private RefreshGroup refreshGroup;
    private MappingConfigurationService mappingConfigurationService;

    /**
     * Constructor
     * 
     * @param refreshGroup
     * @param mappingConfigurationService
     */
    public NewConfigurationTypeFieldGroup(RefreshGroup refreshGroup
            , MappingConfigurationService mappingConfigurationService)
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
    public NewConfigurationTypeFieldGroup(Item itemDataSource, RefreshGroup refreshGroup
            , MappingConfigurationService mappingConfigurationService)
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

        ConfigurationType type = new ConfigurationType();
        type.setName(name.getValue());

        try
        {
            this.mappingConfigurationService.saveConfigurationType(type);

            UserDetailsHelper userDetailsHelper = (UserDetailsHelper)VaadinService.getCurrentRequest().getWrappedSession()
                    .getAttribute(MappingConfigurationUISessionValueConstants.USER);

            logger.info("User: " + userDetailsHelper.getUserDetails().getUsername() 
                + " added a new Mapping Configuration Type:  " 
                    + type);
        }
        catch (Exception e)
        {
            throw new CommitException(e);
        }

        this.refreshGroup.refresh();
    }
}
