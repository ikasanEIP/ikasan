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
package org.ikasan.dashboard.ui.administration.listener;

import org.apache.log4j.Logger;
import org.ikasan.dashboard.ui.administration.panel.PrincipalManagementPanel;
import org.ikasan.dashboard.ui.framework.panel.ViewContext;
import org.ikasan.security.model.IkasanPrincipal;

import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.UI;

/**
 * @author CMI2 Development Team
 *
 */
public class AssociatedPrincipalItemClickListener implements ItemClickListener
{
    private static final long serialVersionUID = -1709533640763729567L;
    
    private static Logger logger = Logger.getLogger(AssociatedPrincipalItemClickListener.class);

    private PrincipalManagementPanel principalManagementPanel;
    private ViewContext viewContext;

    /**
     * Constructor
     * 
	 * @param principalManagementPanel
	 */
	public AssociatedPrincipalItemClickListener(PrincipalManagementPanel principalManagementPanel,
			ViewContext viewContext)
	{
		super();
		this.principalManagementPanel = principalManagementPanel;
		this.viewContext = viewContext;
	}



	/* (non-Javadoc)
     * @see com.vaadin.event.ItemClickEvent.ItemClickListener#itemClick(com.vaadin.event.ItemClickEvent)
     */
    @Override
    public void itemClick(ItemClickEvent event)
    {
    	this.principalManagementPanel.setPrincipal((IkasanPrincipal)event.getItemId());
    	this.viewContext.setCurrentView("principalManagementPanel");
    	
        UI.getCurrent().getNavigator().navigateTo("principalManagementPanel");
    }
}
