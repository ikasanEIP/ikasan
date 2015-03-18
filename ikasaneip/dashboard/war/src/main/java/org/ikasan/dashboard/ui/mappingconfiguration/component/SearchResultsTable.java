/*
 * $Id: SearchResultsTable.java 40648 2014-11-07 11:12:53Z stewmi $
 * $URL: https://svc-vcs-prd.uk.mizuho-sc.com:18080/svn/architecture/cmi2/trunk/projects/mappingConfigurationUI/war/src/main/java/org/ikasan/mapping/configuration/ui/component/SearchResultsTable.java $
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

import org.ikasan.dashboard.ui.mappingconfiguration.listener.SearchResultTableItemClickListener;

import com.vaadin.ui.Button;
import com.vaadin.ui.Table;

/**
 * @author CMI2 Development Team
 *
 */
public class SearchResultsTable extends Table
{
    private static final long serialVersionUID = -7119129093455804443L;

    /**
     * Constructor
     * 
     * @param listener
     */
    public SearchResultsTable(SearchResultTableItemClickListener listener)
    {
        init(listener);
    }

    /**
     * Helper method to initialise the component.
     * 
     * @param listener
     */
    private void init(SearchResultTableItemClickListener listener)
    {
        this.setSizeFull();
        addContainerProperty("Client", String.class,  null);
        addContainerProperty("Type", String.class,  null);
        addContainerProperty("Source Context", String.class,  null);
        addContainerProperty("Target Context", String.class,  null);
        addContainerProperty("Delete", Button.class,  null);
        
        this.addItemClickListener(listener);
        
        this.setCellStyleGenerator(new IkasanCellStyleGenerator());
    }
}
