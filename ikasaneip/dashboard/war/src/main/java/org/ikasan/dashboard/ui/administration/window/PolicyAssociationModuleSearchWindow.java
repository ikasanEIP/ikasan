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
package org.ikasan.dashboard.ui.administration.window;

import org.ikasan.dashboard.ui.mappingconfiguration.panel.MappingConfigurationSearchPanel;
import org.ikasan.dashboard.ui.mappingconfiguration.panel.MappingConfigurationSearchResultsPanel;
import org.ikasan.mapping.model.MappingConfigurationLite;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class PolicyAssociationModuleSearchWindow extends Window
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7298145261413392839L;

	private MappingConfigurationSearchPanel mappingConfigurationSearchPanel;
	private MappingConfigurationSearchResultsPanel mappingConfigurationSearchResultsPanel;
	private HorizontalSplitPanel horizontalSplitPanel;
	private MappingConfigurationLite mappingConfiguration;

	/**
	 * @param mappingConfigurationSearchPanel
	 * @param mappingConfigurationSearchResultsPanel
	 */
	public PolicyAssociationModuleSearchWindow(
			MappingConfigurationSearchPanel mappingConfigurationSearchPanel,
			MappingConfigurationSearchResultsPanel mappingConfigurationSearchResultsPanel)
	{
		super();
		this.mappingConfigurationSearchPanel = mappingConfigurationSearchPanel;
		this.mappingConfigurationSearchResultsPanel = mappingConfigurationSearchResultsPanel;
		
		init();
	}
	
	/**
     * Helper method to initialise this object.
     * 
     * @param message
     */
    protected void init()
    {
    	this.setSizeFull();
    	this.setModal(true);
    	
    	this.mappingConfigurationSearchPanel.setWidth("100%");
    	VerticalLayout leftPanelLayout = new VerticalLayout();
    	leftPanelLayout.setWidth(320, Unit.PIXELS);
    	leftPanelLayout.setHeight("100%");
    	leftPanelLayout.addComponent(this.mappingConfigurationSearchPanel);
    	
    	HorizontalLayout rightPanelLayout = new HorizontalLayout();
    	rightPanelLayout.setSizeFull();
    	rightPanelLayout.addComponent(this.mappingConfigurationSearchResultsPanel);
    	
    	this.horizontalSplitPanel 
        	= new HorizontalSplitPanel(leftPanelLayout, rightPanelLayout);
	    this.horizontalSplitPanel.setSizeFull();
	    this.horizontalSplitPanel.setSplitPosition(320, Unit.PIXELS);
	    this.horizontalSplitPanel.setLocked(true);
	    this.horizontalSplitPanel.addStyleName("ikasansplitpanel");
	    this.setContent(horizontalSplitPanel);
    }
    
    public void clear()
    {
    	this.mappingConfigurationSearchPanel.clear();
    	this.mappingConfigurationSearchResultsPanel.clear();
    }

	/**
	 * @return the mappingConfiguration
	 */
	public MappingConfigurationLite getMappingConfiguration()
	{
		return mappingConfiguration;
	}

	/**
	 * @param mappingConfiguration the mappingConfiguration to set
	 */
	public void setMappingConfiguration(MappingConfigurationLite mappingConfiguration)
	{
		this.mappingConfiguration = mappingConfiguration;
	}
}
