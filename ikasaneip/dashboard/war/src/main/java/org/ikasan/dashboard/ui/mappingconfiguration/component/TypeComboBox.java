/*
 * $Id: TypeComboBox.java 40648 2014-11-07 11:12:53Z stewmi $
 * $URL: https://svc-vcs-prd.uk.mizuho-sc.com:18080/svn/architecture/cmi2/trunk/projects/mappingConfigurationUI/war/src/main/java/org/ikasan/mapping/configuration/ui/component/TypeComboBox.java $
 *
 * ====================================================================
 *
 * Copyright (c) 2000-2011 by Mizuho International plc.
 * All Rights Reserved.
 *
 * ====================================================================
 *
 */
package org.ikasan.dashboard.ui.mappingconfiguration.component;

import java.util.List;

import org.ikasan.dashboard.ui.framework.group.Refreshable;
import org.ikasan.mapping.model.ConfigurationType;
import org.ikasan.mapping.service.MappingConfigurationService;

import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.ui.ComboBox;

/**
 * @author CMI2 Development Team
 *
 */
public class TypeComboBox extends ComboBox implements Refreshable, FocusListener
{
    private static final long serialVersionUID = -2305511116954830348L;

    private MappingConfigurationService mappingConfigurationService;
    private String clientName = null;

    /**
     * Constructor
     * 
     * @param mappingConfigurationService
     */
    public TypeComboBox(MappingConfigurationService mappingConfigurationService)
    {
        this.mappingConfigurationService = mappingConfigurationService;
        this.init();
    }

    /**
     * Helper method to initialise the component.
     */
    @SuppressWarnings("serial")
    protected void init()
    {
        this.setWidth(140, Unit.PIXELS);
        loadClientTypeValues();
    }

    /**
     * Helper method to load client values.
     */
    public void loadClientTypeValues()
    {
        List<ConfigurationType> types = this.mappingConfigurationService.getAllConfigurationTypes();

        for(ConfigurationType type: types)
        {
            this.addItem(type);
            this.setItemCaption(type, type.getName());
        }
    }

    /**
     * Helper method to load client values.
     */
    public void loadClientTypeValues(String clientName)
    {
        List<ConfigurationType> types = this.mappingConfigurationService
                .getConfigurationTypesByClientName(clientName);

        for(ConfigurationType type: types)
        {
            this.addItem(type);
            this.setItemCaption(type, type.getName());
        }
    }

    /**
     * Helper method to refresh the component.
     */
    public void refresh()
    {
        ConfigurationType type = (ConfigurationType)this.getValue();
        boolean isReadOnly = this.isReadOnly();
        this.setReadOnly(false);
        this.removeAllItems();
        this.loadClientTypeValues();
        this.setValue(type);
        this.setReadOnly(isReadOnly);
    }

    /**
     * Helper method to refresh the component.
     */
    public void refresh(String clientName)
    {
        this.clientName = clientName;

        ConfigurationType type = (ConfigurationType)this.getValue();
        boolean isReadOnly = this.isReadOnly();
        this.setReadOnly(false);
        this.removeAllItems();
        this.loadClientTypeValues(clientName);
        this.setValue(type);
        this.setReadOnly(isReadOnly);
    }

    /* (non-Javadoc)
     * @see com.vaadin.event.FieldEvents.FocusListener#focus(com.vaadin.event.FieldEvents.FocusEvent)
     */
    @Override
    public void focus(FocusEvent event)
    {
        this.refresh(this.clientName);
    }
}
