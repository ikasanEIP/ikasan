 /*
 * $Id$
 * $URL$
 *
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 *
 * Distributed under the Modified BSD License.
 * Copyright notice: The copyright for this software and a full listing
 * of individual contributors are as shown in the packaged copyright.txt
 * file.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  - Neither the name of the ORGANIZATION nor the names of its contributors may
 *    be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 */
package org.ikasan.dashboard.ui.mappingconfiguration.panel;

import org.ikasan.mapping.service.MappingManagementService;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.themes.ValoTheme;

/**
 * @author Ikasan Development Team
 *
 */
public class MappingConfigurationSearchResultsPanel extends Panel implements View
{
    private static final long serialVersionUID = 5863997626883201511L;

    private MappingManagementService mappingConfigurationService;
    private Table searchResultsTable;

    /**
     * Constructor
     * 
     * @param mappingConfigurationService
     * @param searchResultsTable
     */
    public MappingConfigurationSearchResultsPanel(MappingManagementService mappingConfigurationService,
            Table searchResultsTable)
    {
        super();
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
    	this.addStyleName(ValoTheme.PANEL_BORDERLESS);
    	
        final HorizontalLayout contentLayout = new HorizontalLayout();

        contentLayout.setSizeFull();
        contentLayout.setMargin(true);
        contentLayout.setSpacing(true);
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
    }
    
    /* (non-Javadoc)
     * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
     */
    public void clear()
    {
    	this.searchResultsTable.removeAllItems();
    }
}
