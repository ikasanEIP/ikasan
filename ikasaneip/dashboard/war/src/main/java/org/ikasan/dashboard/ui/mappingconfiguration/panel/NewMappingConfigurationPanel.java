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

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.log4j.Logger;
import org.ikasan.dashboard.ui.framework.display.IkasanUIView;
import org.ikasan.dashboard.ui.framework.group.Editable;
import org.ikasan.dashboard.ui.framework.group.FunctionalGroup;
import org.ikasan.dashboard.ui.framework.navigation.IkasanUINavigator;
import org.ikasan.dashboard.ui.framework.navigation.MenuLayout;
import org.ikasan.dashboard.ui.framework.util.SaveRequiredMonitor;
import org.ikasan.dashboard.ui.mappingconfiguration.component.ClientComboBox;
import org.ikasan.dashboard.ui.mappingconfiguration.component.MappingConfigurationConfigurationValuesTable;
import org.ikasan.dashboard.ui.mappingconfiguration.component.SourceContextComboBox;
import org.ikasan.dashboard.ui.mappingconfiguration.component.TargetContextComboBox;
import org.ikasan.dashboard.ui.mappingconfiguration.component.TypeComboBox;
import org.ikasan.dashboard.ui.mappingconfiguration.util.MappingConfigurationExportHelper;
import org.ikasan.dashboard.ui.mappingconfiguration.util.MappingConfigurationValuesExportHelper;
import org.ikasan.mapping.model.MappingConfiguration;
import org.ikasan.mapping.service.MappingManagementService;
import org.ikasan.spec.configuration.Configuration;
import org.ikasan.spec.configuration.ConfigurationManagement;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.configuration.PlatformConfigurationService;
import org.ikasan.systemevent.service.SystemEventService;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.themes.ValoTheme;

/**
 * @author Ikasan Development Team
 *
 */
public class NewMappingConfigurationPanel extends MappingConfigurationPanel implements View, Editable
{
    private static final long serialVersionUID = -9199358319962572807L;

    private Logger logger = Logger.getLogger(NewMappingConfigurationPanel.class);


    /**
     * Constructor
     * 
     * @param mappingConfigurationConfigurationValuesTable
     * @param clientComboBox
     * @param typeComboBox
     * @param sourceContextComboBox
     * @param targetContextComboBox
     * @param mappingConfigurationService
     * @param saveRequiredMonitor
     * @param editButton
     * @param saveButton
     * @param addNewRecordButton
     * @param deleteAllRecordsButton
     * @param importMappingConfigurationButton
     * @param exportMappingConfigurationValuesButton
     * @param cancelButton
     * @param newMappingConfigurationFunctionalGroup
     */
    public NewMappingConfigurationPanel(
            MappingConfigurationConfigurationValuesTable mappingConfigurationConfigurationValuesTable,
            ClientComboBox clientComboBox, TypeComboBox typeComboBox, SourceContextComboBox sourceContextComboBox,
            TargetContextComboBox targetContextComboBox, MappingManagementService mappingConfigurationService,
            SaveRequiredMonitor saveRequiredMonitor, Button editButton, Button saveButton, Button addNewRecordButton, 
            Button deleteAllRecordsButton, Button importMappingConfigurationButton, Button exportMappingConfigurationValuesButton,
            Button exportMappingConfigurationButton, Button cancelButton, FunctionalGroup newMappingConfigurationFunctionalGroup,
            MappingConfigurationExportHelper mappingConfigurationExportHelper, MappingConfigurationValuesExportHelper 
            mappingConfigurationValuesExportHelper, SystemEventService systemEventService, IkasanUINavigator topLevelNavigator,
            IkasanUINavigator uiNavigator, MenuLayout menuLayout, ConfigurationManagement<ConfiguredResource, Configuration> configurationManagement,
            PlatformConfigurationService platformConfigurationService)
    {
        super(mappingConfigurationConfigurationValuesTable, clientComboBox, typeComboBox, sourceContextComboBox,
            targetContextComboBox, "New Mapping Configuration", mappingConfigurationService, saveRequiredMonitor, editButton,
            saveButton, addNewRecordButton, deleteAllRecordsButton, importMappingConfigurationButton, exportMappingConfigurationValuesButton,
            exportMappingConfigurationButton, cancelButton, newMappingConfigurationFunctionalGroup, mappingConfigurationExportHelper,
            mappingConfigurationValuesExportHelper, systemEventService, topLevelNavigator, uiNavigator, menuLayout, configurationManagement,
            platformConfigurationService);

        this.registerListeners();
    }

    /**
     * Helper method to initialise this object.
     */
    @SuppressWarnings("serial")
    protected void init()
    {
    	layout = new GridLayout(5, 6);
    	layout.setSpacing(true);
        layout.setMargin(true);
        layout.setWidth("100%");
        
        this.addStyleName(ValoTheme.PANEL_BORDERLESS);
    	
    	this.typeComboBox.setReadOnly(false);
        this.clientComboBox.setReadOnly(false);
        this.sourceContextComboBox.setReadOnly(false);
        this.targetContextComboBox.setReadOnly(false);
    	super.clientComboBox.unselect(super.clientComboBox.getValue());
    	super.sourceContextComboBox.unselect(super.sourceContextComboBox.getValue());
    	super.targetContextComboBox.unselect(super.targetContextComboBox.getValue());
    	super.typeComboBox.unselect(super.typeComboBox.getValue());

    	
        super.mappingConfigurationFunctionalGroup.editButtonPressed();

        super.mappingConfiguration = new MappingConfiguration();
        this.mappingConfigurationConfigurationValuesTable.populateTable(mappingConfiguration);

        HorizontalLayout toolBarLayout = new HorizontalLayout();
        toolBarLayout.setWidth("100%");

        Label spacerLabel = new Label("");
        toolBarLayout.addComponent(spacerLabel);
        toolBarLayout.setExpandRatio(spacerLabel, 0.865f);

        this.editButton.setDescription("Edit the mapping configuration");
        this.editButton.addStyleName(ValoTheme.BUTTON_SMALL);

        toolBarLayout.addComponent(editButton);
        toolBarLayout.setExpandRatio(editButton, 0.045f);

        this.saveButton.addStyleName(ValoTheme.BUTTON_SMALL);
        this.saveButton.setDescription("Save the mapping configuration");


        toolBarLayout.addComponent(saveButton);
        toolBarLayout.setExpandRatio(saveButton, 0.045f);

        this.cancelButton.addStyleName(ValoTheme.BUTTON_SMALL);
        this.cancelButton.setDescription("Cancel the current edit");

        toolBarLayout.addComponent(this.cancelButton);
        toolBarLayout.setExpandRatio(this.cancelButton, 0.045f);

        final VerticalLayout contentLayout = new VerticalLayout();
        
        contentLayout.addComponent(toolBarLayout);
        contentLayout.addComponent(createMappingConfigurationForm());
        

        VerticalSplitPanel vpanel = new VerticalSplitPanel(contentLayout
            , createTableLayout(false));
        vpanel.setStyleName(ValoTheme.SPLITPANEL_LARGE);


        vpanel.setSplitPosition(325, Unit.PIXELS);
        this.setContent(vpanel);
        this.setSizeFull();

    }

    /**
     * Register listeners with components associated with this panel.
     */
    protected void registerListeners()
    {
        super.editButton.addClickListener(new Button.ClickListener() 
        {
            public void buttonClick(ClickEvent event) 
            {
                setEditable(true);
                mappingConfigurationFunctionalGroup.editButtonPressed();
            }
        });

        super.saveButton.addClickListener(new Button.ClickListener() 
        {
            public void buttonClick(ClickEvent event) 
            {
                try
                {
                    save();
                    setEditable(false);
                    Notification.show("Changes Saved!",
                      "",
                      Notification.Type.HUMANIZED_MESSAGE);
                    mappingConfigurationFunctionalGroup.saveOrCancelButtonPressed();
                    setTableButtonsVisible();
                }
                catch(InvalidValueException e)
                {
                    // We can ignore this one as we have already dealt with the
                    // validation messages using the validation framework.
                }
                catch (Exception e) 
                {
                	logger.error("An error occurred trying to save a mapping configuration!", e); 
                	
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    e.printStackTrace(pw);

                    Notification.show("Caught exception trying to save a Mapping Configuration!", sw.toString()
                        , Notification.Type.ERROR_MESSAGE);
                }
            }
        });

        super.cancelButton.addClickListener(new Button.ClickListener() 
        {
            public void buttonClick(ClickEvent event) 
            {
                setEditable(false);
                mappingConfigurationFunctionalGroup.saveOrCancelButtonPressed();
                
                Navigator navigator = new Navigator(UI.getCurrent(), menuLayout.getContentContainer());

        		for (IkasanUIView view : topLevelNavigator.getIkasanViews())
        		{
        			navigator.addView(view.getPath(), view.getView());
        		}
            	
                saveRequiredMonitor.manageSaveRequired("mappingView");
                
                navigator = new Navigator(UI.getCurrent(), mappingNavigator.getContainer());

        		for (IkasanUIView view : mappingNavigator.getIkasanViews())
        		{
        			navigator.addView(view.getPath(), view.getView());
        		}
            }
        });
    }

    /**
     * Set if this panel is editable.
     */
    public void setEditable(boolean editable)
    {
    	if(this.saveRequiredMonitor!= null)
    		this.saveRequiredMonitor.setSaveRequired(editable);
        if(this.mappingConfigurationConfigurationValuesTable != null)
        	this.mappingConfigurationConfigurationValuesTable.setEditable(editable);
        if(this.typeComboBox != null)
        	this.typeComboBox.setReadOnly(!editable);
        if(this.clientComboBox != null)
        	this.clientComboBox.setReadOnly(!editable);
        if(this.sourceContextComboBox != null)
        	this.sourceContextComboBox.setReadOnly(!editable);
        if(this.targetContextComboBox != null)
        	this.targetContextComboBox.setReadOnly(!editable);
        if(this.descriptionTextArea != null)
        	this.descriptionTextArea.setReadOnly(!editable);
        if(this.numberOfSourceParametersTextField != null)
        	this.numberOfSourceParametersTextField.setReadOnly(!editable);

    }

    
    /**
     * Method to populate the mapping configuration form.
     */
    public void populateMappingConfigurationForm()
    {
        
        typeComboBox.setReadOnly(false);
        clientComboBox.setReadOnly(false);
        sourceContextComboBox.setReadOnly(false);
        targetContextComboBox.setReadOnly(false);
        this.descriptionTextArea.setReadOnly(false);
        this.numberOfSourceParametersTextField.setReadOnly(false);

        BeanItem<MappingConfiguration> mappingConfigurationItem = new BeanItem<MappingConfiguration>(this.mappingConfiguration);

        this.clientComboBox.setValue(this.mappingConfiguration.getConfigurationServiceClient());
        this.typeComboBox.setValue(mappingConfiguration.getConfigurationType());
        this.sourceContextComboBox.setValue(mappingConfiguration.getSourceContext());
        this.targetContextComboBox.setValue(mappingConfiguration.getTargetContext());
        this.descriptionTextArea.setPropertyDataSource(mappingConfigurationItem.getItemProperty("description"));
        this.numberOfSourceParametersTextField.setPropertyDataSource(mappingConfigurationItem.getItemProperty("numberOfParams"));
        this.numberOfTargetParametersTextField.setPropertyDataSource(mappingConfigurationItem.getItemProperty("numTargetValues"));


        typeComboBox.setReadOnly(true);
        clientComboBox.setReadOnly(true);
        sourceContextComboBox.setReadOnly(true);
        targetContextComboBox.setReadOnly(true);
        this.descriptionTextArea.setReadOnly(true);
        this.numberOfSourceParametersTextField.setReadOnly(true);
    }

    /* (non-Javadoc)
     * @see com.mapping.configuration.ui.panel.MappingConfigurationPanel#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
     */
    @Override
    public void enter(ViewChangeEvent event)
    {
        this.saveRequiredMonitor.setSaveRequired(true);
        populateMappingConfigurationForm();
    }


}
