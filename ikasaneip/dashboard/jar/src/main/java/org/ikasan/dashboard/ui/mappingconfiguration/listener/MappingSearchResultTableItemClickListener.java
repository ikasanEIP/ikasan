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
package org.ikasan.dashboard.ui.mappingconfiguration.listener;

import org.apache.log4j.Logger;
import org.ikasan.dashboard.ui.framework.display.IkasanUIView;
import org.ikasan.dashboard.ui.framework.group.VisibilityGroup;
import org.ikasan.dashboard.ui.framework.navigation.IkasanUINavigator;
import org.ikasan.dashboard.ui.framework.util.PolicyLinkTypeConstants;
import org.ikasan.dashboard.ui.mappingconfiguration.component.MappingConfigurationConfigurationValuesTable;
import org.ikasan.dashboard.ui.mappingconfiguration.panel.MappingConfigurationPanel;
import org.ikasan.mapping.model.MappingConfiguration;
import org.ikasan.mapping.service.MappingManagementService;

import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.navigator.Navigator;
import com.vaadin.ui.UI;

/**
 * @author Ikasan Development Team
 *
 */
public class MappingSearchResultTableItemClickListener implements ItemClickListener
{
    private static final long serialVersionUID = -1709533640763729567L;
    
    private static Logger logger = Logger.getLogger(MappingSearchResultTableItemClickListener.class);

    private MappingManagementService mappingConfigurationService;
    private MappingConfigurationConfigurationValuesTable mappingConfigurationConfigurationValuesTable;
    private MappingConfigurationPanel mappingConfigurationPanel;
    private VisibilityGroup visibilityGroup;
    private IkasanUINavigator mapingNavigator;

    /**
     * Constructor
     * 
     * @param mappingConfigurationService
     * @param mappingConfigurationConfigurationValuesTable
     * @param mappingConfigurationPanel
     */
    public MappingSearchResultTableItemClickListener(MappingManagementService mappingConfigurationService,
            MappingConfigurationConfigurationValuesTable mappingConfigurationConfigurationValuesTable,
            MappingConfigurationPanel mappingConfigurationPanel, VisibilityGroup visibilityGroup,
            IkasanUINavigator mappingNavigator)
    {
        super();
        this.mappingConfigurationService = mappingConfigurationService;
        this.mappingConfigurationConfigurationValuesTable = mappingConfigurationConfigurationValuesTable;
        this.mappingConfigurationPanel = mappingConfigurationPanel;
        this.visibilityGroup = visibilityGroup;
        this.mapingNavigator = mappingNavigator;
    }

    /* (non-Javadoc)
     * @see com.vaadin.event.ItemClickEvent.ItemClickListener#itemClick(com.vaadin.event.ItemClickEvent)
     */
    @Override
    public void itemClick(ItemClickEvent event)
    {
    	MappingConfiguration mappingConfiguration = this.mappingConfigurationService
                .getMappingConfigurationById((Long)event.getItemId());
    	
    	this.visibilityGroup.setVisible(PolicyLinkTypeConstants.MAPPING_CONFIGURATION_LINK_TYPE, (Long)event.getItemId());
    	
    	Navigator navigator = new Navigator(UI.getCurrent(), mapingNavigator.getParentContainer());

		for (IkasanUIView view : mapingNavigator.getIkasanViews())
		{
			logger.debug("Adding view:" + view.getPath());
			navigator.addView(view.getPath(), view.getView());
		}


        this.mappingConfigurationPanel.setMappingConfiguration(mappingConfiguration);
        this.mappingConfigurationPanel.populateMappingConfigurationForm();


        this.mappingConfigurationConfigurationValuesTable.populateTable(mappingConfiguration);
        
        UI.getCurrent().getNavigator().navigateTo("existingMappingConfigurationPanel");
    }
}
