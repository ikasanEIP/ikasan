/*
 * $Id: TargetContextComboBox.java 40648 2014-11-07 11:12:53Z stewmi $
 * $URL: https://svc-vcs-prd.uk.mizuho-sc.com:18080/svn/architecture/cmi2/trunk/projects/mappingConfigurationUI/war/src/main/java/org/ikasan/mapping/configuration/ui/component/TargetContextComboBox.java $
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

import com.mizuho.cmi2.mappingConfiguration.model.ConfigurationContext;
import com.mizuho.cmi2.mappingConfiguration.service.MappingConfigurationService;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.ui.ComboBox;

/**
 * @author CMI2 Development Team
 *
 */
public class TargetContextComboBox extends ComboBox implements Refreshable, FocusListener
{
    private static final long serialVersionUID = 6959825126925569738L;

    private MappingConfigurationService mappingConfigurationService;
    private String clientName = null;
    private String type = null;
    private String sourceContext = null;

    /**
     * Constructor
     * 
     * @param mappingConfigurationService
     */
    public TargetContextComboBox(MappingConfigurationService mappingConfigurationService)
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
        loadContextValues();
    }

    /**
     * Helper method to load context values.
     */
    private void loadContextValues()
    {
        List<ConfigurationContext> contexts = this.mappingConfigurationService.getAllConfigurationContexts();

        for(ConfigurationContext context: contexts)
        {
            this.addItem(context);
            this.setItemCaption(context, context.getName());
        }
    }

    /**
     * Helper method to load context values.
     */
    private void loadContextValues(String clientName, String type, String sourceContext)
    {
        List<ConfigurationContext> contexts = this.mappingConfigurationService
                .getTargetConfigurationContextByClientNameTypeAndSourceContext(clientName, type, sourceContext);

        for(ConfigurationContext context: contexts)
        {
            this.addItem(context);
            this.setItemCaption(context, context.getName());
        }
    }

    /**
     * Helper method to refresh the component.
     */
    public void refresh(String clientName, String type, String sourceContext)
    {
        this.clientName = clientName;
        this.type = type;
        this.sourceContext = sourceContext;

        ConfigurationContext context = (ConfigurationContext)this.getValue();
        boolean isReadOnly = this.isReadOnly();
        this.setReadOnly(false);
        this.removeAllItems();
        this.loadContextValues(clientName, type, sourceContext);
        this.setValue(context);
        this.setReadOnly(isReadOnly);
    }

    /**
     * Helper method to refresh the component.
     */
    public void refresh()
    {
        ConfigurationContext context = (ConfigurationContext)this.getValue();
        boolean isReadOnly = this.isReadOnly();
        this.setReadOnly(false);
        this.removeAllItems();
        this.loadContextValues();
        this.setValue(context);
        this.setReadOnly(isReadOnly);
    }

    /* (non-Javadoc)
     * @see com.vaadin.event.FieldEvents.FocusListener#focus(com.vaadin.event.FieldEvents.FocusEvent)
     */
    @Override
    public void focus(FocusEvent event)
    {
        this.refresh(this.clientName, this.type, this.sourceContext);
    }
}
