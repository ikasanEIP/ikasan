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

import org.ikasan.dashboard.ui.administration.window.PolicyAssociationMappingSearchWindow;
import org.ikasan.mapping.model.MappingConfigurationLite;

import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.UI;

/**
 * @author CMI2 Development Team
 *
 */
public class PolicyLinkMappingSearchResultTableItemClickListener implements ItemClickListener
{
    private static final long serialVersionUID = -1709533640763729567L;

    private PolicyAssociationMappingSearchWindow policyAssociationMappingSearchWindow;

    /**
     * Constructor
     * 
     * @param mappingConfigurationService
     * @param mappingConfigurationConfigurationValuesTable
     * @param mappingConfigurationPanel
     */
    public PolicyLinkMappingSearchResultTableItemClickListener(PolicyAssociationMappingSearchWindow policyAssociationMappingSearchWindow)
    {
        super();
        this.policyAssociationMappingSearchWindow = policyAssociationMappingSearchWindow;
    }

    /* (non-Javadoc)
     * @see com.vaadin.event.ItemClickEvent.ItemClickListener#itemClick(com.vaadin.event.ItemClickEvent)
     */
    @Override
    public void itemClick(ItemClickEvent event)
    {
    	this.policyAssociationMappingSearchWindow.setMappingConfiguration((MappingConfigurationLite)event.getItemId());
    	UI.getCurrent().removeWindow(policyAssociationMappingSearchWindow);
    }
}
