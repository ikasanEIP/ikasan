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


import org.ikasan.dashboard.ui.framework.display.ViewComponentContainer;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.themes.BaseTheme;
import com.vaadin.ui.themes.ValoTheme;

/**
 * 
 * 
 * @author Ikasan Development Team
 *
 */
public class MappingConfigurationView extends Panel implements View
{
    private static final long serialVersionUID = -4759498822589839343L;
    private MappingConfigurationSearchPanel searchPanel;
    private MappingConfigurationSearchResultsPanel searchResultsPanel;
    private ViewComponentContainer viewComponentContainer;
    private NewActionsPanel newActionsPanel;
    private VerticalSplitPanel horizontalSplitPanel;
    private Panel leftPanelLayout;

    /**
     * Constructor
     * 
     * @param searchPanel
     * @param searchResultsPanel
     * @param viewComponentContainer
     * @param newActionsPanel
     */
    public MappingConfigurationView(MappingConfigurationSearchPanel searchPanel,
            MappingConfigurationSearchResultsPanel searchResultsPanel,
            ViewComponentContainer viewComponentContainer,
            NewActionsPanel newActionsPanel)
    {
        this.searchPanel = searchPanel;
        this.searchResultsPanel = searchResultsPanel;
        this.viewComponentContainer = viewComponentContainer;
        this.newActionsPanel = newActionsPanel;

        this.init();
    }

    /**
     * Helper method to initialise this object.
     */
    protected void init() 
    {
        this.setSizeFull();
        
        this.leftPanelLayout = getExpandedLeftSplitPanelLayout();
        
        HorizontalLayout leftContainer = new HorizontalLayout();
        leftContainer.setSizeFull();
        leftContainer.setMargin(true);
        leftContainer.addComponent(this.leftPanelLayout);
        HorizontalLayout rightContainer = new HorizontalLayout();
        rightContainer.setMargin(true);
        rightContainer.setSizeFull();
        rightContainer.addComponent(this.viewComponentContainer);
        
        this.horizontalSplitPanel 
            = new VerticalSplitPanel(leftContainer, rightContainer);
        this.horizontalSplitPanel.addStyleName(ValoTheme.SPLITPANEL_LARGE);
        this.horizontalSplitPanel.setSizeFull();
        this.horizontalSplitPanel.setSplitPosition(285, Unit.PIXELS);
        this.setContent(horizontalSplitPanel);
    }

    /**
     * Setup the layout for the left panel.
     * 
     * @return
     */
    private Panel getExpandedLeftSplitPanelLayout()
    {   
    	
        Button collapseButton = new Button("<<");
        collapseButton.setVisible(true);
        collapseButton.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                horizontalSplitPanel.setLocked(false);
                horizontalSplitPanel.setSplitPosition(70, Unit.PIXELS);
                horizontalSplitPanel.setLocked(true);
                
                HorizontalLayout leftContainer = new HorizontalLayout();
                leftContainer.setSizeFull();
                leftContainer.setMargin(true);
                leftContainer.addComponent(getCollapsedLeftSplitPanelLayout());
                horizontalSplitPanel.setFirstComponent(leftContainer);
            }
        });

        HorizontalLayout layout = new HorizontalLayout();
        layout.setMargin(true);
        layout.addComponent(collapseButton);
        collapseButton.setHeight(15, Unit.PIXELS);
        collapseButton.setWidth(15, Unit.PIXELS);
        collapseButton.setStyleName(BaseTheme.BUTTON_LINK);
        layout.setComponentAlignment(collapseButton, Alignment.TOP_RIGHT);
        layout.addComponent(searchPanel);
        layout.setComponentAlignment(searchPanel, Alignment.TOP_RIGHT);
        layout.addComponent(newActionsPanel);
        layout.setComponentAlignment(newActionsPanel, Alignment.TOP_RIGHT);

        layout.setStyleName("grey");
        
        Panel panel = new Panel();
        panel.setSizeFull();
        panel.setStyleName("dashboard-grey");
        panel.setContent(layout);
        
        return panel;
    }

    private Panel getCollapsedLeftSplitPanelLayout()
    {   
        Button expandButton = new Button(">>");
        expandButton.setVisible(true);
        expandButton.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                horizontalSplitPanel.setLocked(false);
                horizontalSplitPanel.setSplitPosition(385, Unit.PIXELS);
                horizontalSplitPanel.setLocked(true);
                
                HorizontalLayout leftContainer = new HorizontalLayout();
                leftContainer.setSizeFull();
                leftContainer.setMargin(true);
                leftContainer.addComponent(getExpandedLeftSplitPanelLayout());
                horizontalSplitPanel.setFirstComponent(leftContainer);
                
                horizontalSplitPanel.setFirstComponent(leftContainer);
            }
        });

        HorizontalLayout layout = new HorizontalLayout();
        layout.addComponent(expandButton);
        expandButton.setHeight(15, Unit.PIXELS);
        expandButton.setWidth(15, Unit.PIXELS);
        layout.setComponentAlignment(expandButton, Alignment.TOP_CENTER);
        expandButton.setStyleName(BaseTheme.BUTTON_LINK);
        layout.setStyleName("grey");
        layout.setHeight("100%");

        Panel panel = new Panel();
        panel.setSizeFull();
        panel.setStyleName("dashboard-grey");
        panel.setContent(layout);
        
        return panel;
    }

	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub
		
	}
}
