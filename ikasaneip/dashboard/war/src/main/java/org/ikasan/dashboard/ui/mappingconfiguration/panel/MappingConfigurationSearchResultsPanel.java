/*
 * $Id: MappingConfigurationSearchResultsPanel.java 40648 2014-11-07 11:12:53Z stewmi $
 * $URL: https://svc-vcs-prd.uk.mizuho-sc.com:18080/svn/architecture/cmi2/trunk/projects/mappingConfigurationUI/war/src/main/java/org/ikasan/mapping/configuration/ui/panel/MappingConfigurationSearchResultsPanel.java $
 *
 * ====================================================================
 *
 * Copyright (c) 2000-2011 by Mizuho International plc.
 * All Rights Reserved.
 *
 * ====================================================================
 *
 */
package org.ikasan.dashboard.ui.mappingconfiguration.panel;

import org.ikasan.dashboard.ui.mappingconfiguration.component.SearchResultsTable;

import com.mizuho.cmi2.mappingConfiguration.service.MappingConfigurationService;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;

/**
 * @author CMI2 Development Team
 *
 */
public class MappingConfigurationSearchResultsPanel extends Panel implements View
{
    private static final long serialVersionUID = 5863997626883201511L;

    private MappingConfigurationService mappingConfigurationService;
    private SearchResultsTable searchResultsTable;

    /**
     * Constructor
     * 
     * @param mappingConfigurationService
     * @param searchResultsTable
     */
    public MappingConfigurationSearchResultsPanel(MappingConfigurationService mappingConfigurationService,
            SearchResultsTable searchResultsTable)
    {
        super("Search Results");
        this.mappingConfigurationService = mappingConfigurationService;
        this.searchResultsTable = searchResultsTable;
        this.init();
    }

    /**
     * Helper method to initialise this object.
     */
    @SuppressWarnings("serial")
    protected void init()
    {
        final HorizontalLayout contentLayout = new HorizontalLayout();

        contentLayout.setSizeFull();
        contentLayout.setMargin(true);
        contentLayout.addComponent(this.searchResultsTable);
        this.setContent(contentLayout);
        this.setSizeFull();
    }

    /* (non-Javadoc)
     * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
     */
    @Override
    public void enter(ViewChangeEvent event)
    {
        // TODO Auto-generated method stub
        
    }
}
