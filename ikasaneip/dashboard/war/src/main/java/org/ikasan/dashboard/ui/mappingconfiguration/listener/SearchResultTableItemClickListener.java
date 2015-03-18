/*
 * $Id: SearchResultTableItemClickListener.java 40648 2014-11-07 11:12:53Z stewmi $
 * $URL: https://svc-vcs-prd.uk.mizuho-sc.com:18080/svn/architecture/cmi2/trunk/projects/mappingConfigurationUI/war/src/main/java/org/ikasan/mapping/configuration/ui/listener/SearchResultTableItemClickListener.java $
 *
 * ====================================================================
 *
 * Copyright (c) 2000-2011 by Mizuho International plc.
 * All Rights Reserved.
 *
 * ====================================================================
 *
 */
package org.ikasan.dashboard.ui.mappingconfiguration.listener;

import org.ikasan.dashboard.ui.mappingconfiguration.component.MappingConfigurationConfigurationValuesTable;
import org.ikasan.dashboard.ui.mappingconfiguration.panel.MappingConfigurationPanel;

import com.mizuho.cmi2.mappingConfiguration.model.MappingConfiguration;
import com.mizuho.cmi2.mappingConfiguration.service.MappingConfigurationService;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.UI;

/**
 * @author CMI2 Development Team
 *
 */
public class SearchResultTableItemClickListener implements ItemClickListener
{
    private static final long serialVersionUID = -1709533640763729567L;

    private MappingConfigurationService mappingConfigurationService;
    private MappingConfigurationConfigurationValuesTable mappingConfigurationConfigurationValuesTable;
    private MappingConfigurationPanel mappingConfigurationPanel;

    /**
     * Constructor
     * 
     * @param mappingConfigurationService
     * @param mappingConfigurationConfigurationValuesTable
     * @param mappingConfigurationPanel
     */
    public SearchResultTableItemClickListener(MappingConfigurationService mappingConfigurationService,
            MappingConfigurationConfigurationValuesTable mappingConfigurationConfigurationValuesTable,
            MappingConfigurationPanel mappingConfigurationPanel)
    {
        super();
        this.mappingConfigurationService = mappingConfigurationService;
        this.mappingConfigurationConfigurationValuesTable = mappingConfigurationConfigurationValuesTable;
        this.mappingConfigurationPanel = mappingConfigurationPanel;
    }

    /* (non-Javadoc)
     * @see com.vaadin.event.ItemClickEvent.ItemClickListener#itemClick(com.vaadin.event.ItemClickEvent)
     */
    @Override
    public void itemClick(ItemClickEvent event)
    {
        UI.getCurrent().getNavigator().navigateTo("existingMappingConfigurationPanel");

        MappingConfiguration mappingConfiguration = this.mappingConfigurationService
                .getMappingConfigurationById((Long)event.getItemId());

        this.mappingConfigurationPanel.setMappingConfiguration(mappingConfiguration);
        this.mappingConfigurationPanel.populateMappingConfigurationForm();


        this.mappingConfigurationConfigurationValuesTable.populateTable(mappingConfiguration);
    }
}
