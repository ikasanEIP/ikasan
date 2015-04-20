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

import org.apache.log4j.Logger;
import org.ikasan.dashboard.ui.framework.constants.SecurityConstants;
import org.ikasan.dashboard.ui.framework.group.VisibilityGroup;
import org.ikasan.dashboard.ui.mappingconfiguration.component.MappingConfigurationConfigurationValuesTable;
import org.ikasan.dashboard.ui.mappingconfiguration.panel.MappingConfigurationPanel;
import org.ikasan.dashboard.ui.mappingconfiguration.util.MappingConfigurationUISessionValueConstants;
import org.ikasan.mapping.model.MappingConfiguration;
import org.ikasan.mapping.service.MappingConfigurationService;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.ikasan.security.service.authentication.LdapAuthenticationProvider;

import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.UI;

/**
 * @author CMI2 Development Team
 *
 */
public class MappingSearchResultTableItemClickListener implements ItemClickListener
{
    private static final long serialVersionUID = -1709533640763729567L;
    
    private static Logger logger = Logger.getLogger(MappingSearchResultTableItemClickListener.class);

    private MappingConfigurationService mappingConfigurationService;
    private MappingConfigurationConfigurationValuesTable mappingConfigurationConfigurationValuesTable;
    private MappingConfigurationPanel mappingConfigurationPanel;
    private VisibilityGroup visibilityGroup;

    /**
     * Constructor
     * 
     * @param mappingConfigurationService
     * @param mappingConfigurationConfigurationValuesTable
     * @param mappingConfigurationPanel
     */
    public MappingSearchResultTableItemClickListener(MappingConfigurationService mappingConfigurationService,
            MappingConfigurationConfigurationValuesTable mappingConfigurationConfigurationValuesTable,
            MappingConfigurationPanel mappingConfigurationPanel, VisibilityGroup visibilityGroup)
    {
        super();
        this.mappingConfigurationService = mappingConfigurationService;
        this.mappingConfigurationConfigurationValuesTable = mappingConfigurationConfigurationValuesTable;
        this.mappingConfigurationPanel = mappingConfigurationPanel;
        this.visibilityGroup = visibilityGroup;
    }

    /* (non-Javadoc)
     * @see com.vaadin.event.ItemClickEvent.ItemClickListener#itemClick(com.vaadin.event.ItemClickEvent)
     */
    @Override
    public void itemClick(ItemClickEvent event)
    {
    	IkasanAuthentication authentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
        	.getAttribute(MappingConfigurationUISessionValueConstants.USER);
    	
    	if(authentication.canAccessLinkedItem(SecurityConstants.MAPPING_CONFIGURATION_LINKED_TYPE, (Long)event.getItemId()) 
    			|| authentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
    	{
    		logger.info("User can access modify configuration: " + (Long)event.getItemId());
    		this.visibilityGroup.setVisible(true);
    	}
    	else
    	{
    		logger.info("User can NOT access modify configuration: " + (Long)event.getItemId());
    		this.visibilityGroup.setVisible(false);
    	}
    	
        UI.getCurrent().getNavigator().navigateTo("existingMappingConfigurationPanel");

        MappingConfiguration mappingConfiguration = this.mappingConfigurationService
                .getMappingConfigurationById((Long)event.getItemId());

        this.mappingConfigurationPanel.setMappingConfiguration(mappingConfiguration);
        this.mappingConfigurationPanel.populateMappingConfigurationForm();


        this.mappingConfigurationConfigurationValuesTable.populateTable(mappingConfiguration);
    }
}
