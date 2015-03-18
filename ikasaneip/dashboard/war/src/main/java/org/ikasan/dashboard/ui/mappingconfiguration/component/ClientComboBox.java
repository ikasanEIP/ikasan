/*
 * $Id: ClientComboBox.java 40648 2014-11-07 11:12:53Z stewmi $
 * $URL: https://svc-vcs-prd.uk.mizuho-sc.com:18080/svn/architecture/cmi2/trunk/projects/mappingConfigurationUI/war/src/main/java/org/ikasan/mapping/configuration/ui/component/ClientComboBox.java $
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

import com.mizuho.cmi2.mappingConfiguration.model.ConfigurationServiceClient;
import com.mizuho.cmi2.mappingConfiguration.service.MappingConfigurationService;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.ui.ComboBox;

/**
 * @author CMI2 Development Team
 *
 */
public class ClientComboBox extends ComboBox implements Refreshable, FocusListener
{
    private static final long serialVersionUID = -2820064207169688211L;

    private MappingConfigurationService mappingConfigurationService;

    /**
     * Constructor
     * 
     * @param mappingConfigurationService
     */
    public ClientComboBox(MappingConfigurationService mappingConfigurationService)
    {
        this.mappingConfigurationService = mappingConfigurationService;
        this.init();
    }

    @SuppressWarnings("serial")
    protected void init()
    {
        this.setWidth(140, Unit.PIXELS);
        loadClientSelectValues();
    }

    /**
     * Method to load the values to populate the combo box.
     */
    public void loadClientSelectValues()
    {
        List<ConfigurationServiceClient> clients = this.mappingConfigurationService.getAllConfigurationServiceClients();

        for(ConfigurationServiceClient client: clients)
        {
            this.addItem(client);
            this.setItemCaption(client, client.getName());
        }
    }

    /**
     * Method to refresh the combo box.
     */
    public void refresh()
    {
        ConfigurationServiceClient client = (ConfigurationServiceClient)this.getValue();
        boolean isReadOnly = this.isReadOnly();
        this.setReadOnly(false);
        this.removeAllItems();
        this.loadClientSelectValues();
        this.setValue(client);
        this.setReadOnly(isReadOnly);
    }

    /* (non-Javadoc)
     * @see com.vaadin.event.FieldEvents.FocusListener#focus(com.vaadin.event.FieldEvents.FocusEvent)
     */
    @Override
    public void focus(FocusEvent event)
    {
        this.refresh();
    }
}
