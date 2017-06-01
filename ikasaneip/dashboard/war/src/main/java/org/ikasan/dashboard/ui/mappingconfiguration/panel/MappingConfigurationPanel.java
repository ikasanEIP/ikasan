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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

import com.vaadin.ui.*;
import org.apache.log4j.Logger;
import org.ikasan.dashboard.ui.framework.constants.SecurityConstants;
import org.ikasan.dashboard.ui.framework.display.IkasanUIView;
import org.ikasan.dashboard.ui.framework.group.FunctionalGroup;
import org.ikasan.dashboard.ui.framework.navigation.IkasanUINavigator;
import org.ikasan.dashboard.ui.framework.navigation.MenuLayout;
import org.ikasan.dashboard.ui.framework.util.DashboardSessionValueConstants;
import org.ikasan.dashboard.ui.framework.util.PolicyLinkTypeConstants;
import org.ikasan.dashboard.ui.framework.util.SaveRequiredMonitor;
import org.ikasan.dashboard.ui.framework.validator.IntegerValidator;
import org.ikasan.dashboard.ui.framework.validator.LongValidator;
import org.ikasan.dashboard.ui.framework.window.IkasanMessageDialog;
import org.ikasan.dashboard.ui.mappingconfiguration.action.RemoveAllItemsAction;
import org.ikasan.dashboard.ui.mappingconfiguration.component.ClientComboBox;
import org.ikasan.dashboard.ui.mappingconfiguration.component.MappingConfigurationConfigurationValuesTable;
import org.ikasan.dashboard.ui.mappingconfiguration.component.SourceContextComboBox;
import org.ikasan.dashboard.ui.mappingconfiguration.component.TargetContextComboBox;
import org.ikasan.dashboard.ui.mappingconfiguration.component.TypeComboBox;
import org.ikasan.dashboard.ui.mappingconfiguration.util.MappingConfigurationConstants;
import org.ikasan.dashboard.ui.mappingconfiguration.util.MappingConfigurationExportHelper;
import org.ikasan.dashboard.ui.mappingconfiguration.util.MappingConfigurationValuesExportHelper;
import org.ikasan.dashboard.ui.mappingconfiguration.window.MappingConfigurationValuesImportWindow;
import org.ikasan.mapping.model.*;
import org.ikasan.mapping.service.MappingManagementService;
import org.ikasan.mapping.service.MappingConfigurationServiceException;
import org.ikasan.mapping.util.MappingConfigurationValidator;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.ikasan.spec.configuration.Configuration;
import org.ikasan.spec.configuration.ConfigurationManagement;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.configuration.PlatformConfigurationService;
import org.ikasan.systemevent.service.SystemEventService;
import org.vaadin.teemu.VaadinIcons;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.validator.NullValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.ValoTheme;

/**
 * @author Ikasan Development Team
 *
 */
public class MappingConfigurationPanel extends Panel implements View
{
    private static final long serialVersionUID = 5269092088876470789L;

    private Logger logger = Logger.getLogger(MappingConfigurationPanel.class);
    protected GridLayout layout;
    protected MappingConfigurationConfigurationValuesTable mappingConfigurationConfigurationValuesTable;
    protected MappingConfiguration mappingConfiguration;
    protected ClientComboBox clientComboBox;
    protected TypeComboBox typeComboBox;
    protected SourceContextComboBox sourceContextComboBox;
    protected TargetContextComboBox targetContextComboBox;
    protected TextArea descriptionTextArea;
    protected TextField numberOfSourceParametersTextField;
    protected TextField numberOfTargetParametersTextField;
    protected MappingManagementService mappingConfigurationService;
    protected SaveRequiredMonitor saveRequiredMonitor;
    protected Button editButton;
    protected Button saveButton;
    protected Button cancelButton;
    protected Button addNewRecordButton;
    protected Button deleteAllRecordsButton;
    protected Button importMappingConfigurationButton;
    protected Button exportMappingConfigurationValuesButton;
    protected Button exportMappingConfigurationButton;
    protected HorizontalLayout toolBarLayout;
    protected FunctionalGroup mappingConfigurationFunctionalGroup;
    protected MappingConfigurationExportHelper mappingConfigurationExportHelper;
    protected MappingConfigurationValuesExportHelper mappingConfigurationValuesExportHelper;
    protected SystemEventService systemEventService;
    protected String name;
    protected IkasanUINavigator mappingNavigator;
    protected IkasanUINavigator topLevelNavigator;
    protected MenuLayout menuLayout;
    protected ConfigurationManagement<ConfiguredResource, Configuration> configurationManagement;
    protected PlatformConfigurationService platformConfigurationService;
    protected CheckBox isManyToManyCheckbox;
    protected CheckBox constrainParameterListSizesCheckbox;
    protected Label numSourceParamsLabel;
    protected Label numTargetParamsLabel;
    protected List<ParameterName> sourceContextParameterNames;
    protected List<ParameterName> targetContextParameterNames;
    protected MappingConfigurationValidator mappingConfigurationValidator = new MappingConfigurationValidator();
    protected Label sourceParamNameLabel;
    protected TextArea sourceParamNameValueTextArea;
    protected Label targetParamNameLabel;
    protected TextArea targetParamNameValueTextArea;
	

    /**
     * Constructor
     * 
     * @param mappingConfigurationConfigurationValuesTable
     * @param clientComboBox
     * @param typeComboBox
     * @param sourceContextComboBox
     * @param targetContextComboBox
     * @param name
     * @param mappingConfigurationService
     * @param saveRequiredMonitor
     * @param editButton
     * @param saveButton
     * @param addNewRecordButton
     * @param deleteAllRecordsButton
     * @param importMappingConfigurationButton
     * @param exportMappingConfigurationValuesButton
     * @param cancelButton
     * @param mappingConfigurationFunctionalGroup
     */
    public MappingConfigurationPanel(MappingConfigurationConfigurationValuesTable mappingConfigurationConfigurationValuesTable
            , ClientComboBox clientComboBox, TypeComboBox typeComboBox, SourceContextComboBox sourceContextComboBox,
            TargetContextComboBox targetContextComboBox, String name, MappingManagementService mappingConfigurationService,
            SaveRequiredMonitor saveRequiredMonitor, Button editButton, Button saveButton, Button addNewRecordButton, 
            Button deleteAllRecordsButton, Button importMappingConfigurationButton, Button exportMappingConfigurationValuesButton,
            Button exportMappingConfigurationButton, Button cancelButton, FunctionalGroup mappingConfigurationFunctionalGroup,
            MappingConfigurationExportHelper mappingConfigurationExportHelper, MappingConfigurationValuesExportHelper 
            mappingConfigurationValuesExportHelper, SystemEventService systemEventService, IkasanUINavigator topLevelNavigator
            , IkasanUINavigator mappingNavigator, MenuLayout menuLayout, ConfigurationManagement<ConfiguredResource, Configuration> configurationManagement,
            PlatformConfigurationService platformConfigurationService)
    {
        super();
        this.mappingConfigurationConfigurationValuesTable = mappingConfigurationConfigurationValuesTable;
        this.clientComboBox = clientComboBox;
        this.typeComboBox = typeComboBox;
        this.sourceContextComboBox = sourceContextComboBox;
        this.targetContextComboBox = targetContextComboBox;
        this.mappingConfigurationService = mappingConfigurationService;
        this.saveRequiredMonitor = saveRequiredMonitor;
        this.editButton = editButton;
        this.saveButton = saveButton;
        this.cancelButton = cancelButton;
        this.addNewRecordButton = addNewRecordButton;
        this.deleteAllRecordsButton = deleteAllRecordsButton;
        this.importMappingConfigurationButton = importMappingConfigurationButton;
        this.exportMappingConfigurationValuesButton = exportMappingConfigurationValuesButton;
        this.exportMappingConfigurationButton = exportMappingConfigurationButton;
        this.mappingConfigurationFunctionalGroup = mappingConfigurationFunctionalGroup;
        this.mappingConfigurationExportHelper = mappingConfigurationExportHelper;
        this.mappingConfigurationValuesExportHelper = mappingConfigurationValuesExportHelper;
        this.systemEventService = systemEventService;
        this.name = name;
        this.mappingNavigator = mappingNavigator;
        this.topLevelNavigator = topLevelNavigator;
        this.menuLayout = menuLayout;
        this.configurationManagement = configurationManagement;
        this.platformConfigurationService = platformConfigurationService;
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


        toolBarLayout = new HorizontalLayout();
        toolBarLayout.setWidth("100%");

        Button linkButton = new Button();
        
        linkButton.setIcon(VaadinIcons.REPLY_ALL);
        linkButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
        linkButton.setDescription("Return to search results");
        linkButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);

        linkButton.addClickListener(new Button.ClickListener() 
        {
            public void buttonClick(ClickEvent event) 
            {
            	Navigator navigator = new Navigator(UI.getCurrent(), menuLayout.getContentContainer());

        		for (IkasanUIView view : topLevelNavigator.getIkasanViews())
        		{
        			navigator.addView(view.getPath(), view.getView());
        		}

                UI.getCurrent().getNavigator().navigateTo("mappingView");
                
                navigator = new Navigator(UI.getCurrent(), mappingNavigator.getContainer());

        		for (IkasanUIView view : mappingNavigator.getIkasanViews())
        		{
        			navigator.addView(view.getPath(), view.getView());
        		}
            }
        });

        toolBarLayout.addComponent(linkButton);
        toolBarLayout.setExpandRatio(linkButton, 0.865f);

        this.editButton.setCaption("Edit");
        this.editButton.setDescription("Edit the mapping configuration");
        this.editButton.addStyleName(ValoTheme.BUTTON_SMALL);

        this.editButton.setVisible(false);
        this.editButton.addClickListener(new Button.ClickListener() 
        {
            public void buttonClick(ClickEvent event) 
            {
                setEditable(true);
                mappingConfigurationFunctionalGroup.editButtonPressed();
            }
        });

        toolBarLayout.addComponent(this.editButton);
        toolBarLayout.setExpandRatio(this.editButton, 0.045f);


        this.saveButton.setCaption("Save");
        this.saveButton.setDescription("Save the mapping configuration");
        this.saveButton.addStyleName(ValoTheme.BUTTON_SMALL);
        this.saveButton.setVisible(false);
        this.saveButton.addClickListener(new Button.ClickListener() 
        {
            public void buttonClick(ClickEvent event) 
            {
                try
                {
                    logger.debug("Save button clicked!!");
                    save();
                    setEditable(false);
                    Notification.show("Changes Saved!",
                      "",
                      Notification.Type.HUMANIZED_MESSAGE);
                    mappingConfigurationFunctionalGroup.saveOrCancelButtonPressed();
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

                    Notification.show("Cauget exception trying to save a Mapping Configuration!", sw.toString()
                        , Notification.Type.ERROR_MESSAGE);
                }
            }
        });

        this.cancelButton.setCaption("Cancel");
        this.cancelButton.setDescription("Cancel the current edit");

        this.cancelButton.addStyleName(ValoTheme.BUTTON_SMALL);
        this.cancelButton.setVisible(false);
        this.cancelButton.addClickListener(new Button.ClickListener() 
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


        this.exportMappingConfigurationButton.setIcon(VaadinIcons.DOWNLOAD_ALT);
        this.exportMappingConfigurationButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
        this.exportMappingConfigurationButton.setDescription("Export the current mapping configuration");
        this.exportMappingConfigurationButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        toolBarLayout.addComponent(this.exportMappingConfigurationButton);
        toolBarLayout.setExpandRatio(this.exportMappingConfigurationButton, 0.045f);

        final GridLayout contentLayout = new GridLayout(1, 2);
        contentLayout.setWidth("100%");
        
        contentLayout.addComponent(toolBarLayout);
        contentLayout.addComponent(createMappingConfigurationForm());
        
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(true);
        buttonLayout.setWidth("85px");
        buttonLayout.addComponent(this.editButton);
        buttonLayout.setComponentAlignment(this.editButton, Alignment.MIDDLE_CENTER);
        buttonLayout.addComponent(this.saveButton);
        buttonLayout.setComponentAlignment(this.saveButton, Alignment.MIDDLE_CENTER);
        buttonLayout.addComponent(this.cancelButton);
        buttonLayout.setComponentAlignment(this.cancelButton, Alignment.MIDDLE_CENTER);
        
        contentLayout.addComponent(buttonLayout);
        contentLayout.setComponentAlignment(buttonLayout, Alignment.MIDDLE_CENTER);

        VerticalSplitPanel vpanel = new VerticalSplitPanel(contentLayout
            , createTableLayout(false));
        vpanel.setStyleName(ValoTheme.SPLITPANEL_LARGE);


        vpanel.setSplitPosition(400, Unit.PIXELS);
        vpanel.setMaxSplitPosition(400, Unit.PIXELS);
        this.setContent(vpanel);
        this.setSizeFull();
    }

    /**
     * Method to set whether the buttons associated with the
     * table are visible. 
     */
    protected void setTableButtonsVisible()
    {
        this.addNewRecordButton.setVisible(true);
        this.deleteAllRecordsButton.setVisible(true);
        this.importMappingConfigurationButton.setVisible(true);
        this.exportMappingConfigurationValuesButton.setVisible(true);
    }

    /**
     * Set whether this component is editable.
     * 
     * @param editable
     */
    public void setEditable(boolean editable)
    {
        this.saveRequiredMonitor.setSaveRequired(editable);
        this.mappingConfigurationConfigurationValuesTable.setEditable(editable);
        this.typeComboBox.setReadOnly(!editable);
        this.clientComboBox.setReadOnly(!editable);
        this.sourceContextComboBox.setReadOnly(!editable);
        this.targetContextComboBox.setReadOnly(!editable);
        this.descriptionTextArea.setReadOnly(!editable);
    }

    /**
     * Helper method to create the form associated with the mapping
     * configuration.
     * 
     * @return the Layout of the form
     */
    protected GridLayout createMappingConfigurationForm()
    {
        Label mappingConfigurationLabel = new Label(this.name);
 		mappingConfigurationLabel.setStyleName(ValoTheme.LABEL_HUGE);
 		layout.addComponent(mappingConfigurationLabel, 0, 0, 1, 0);
    	
    	HorizontalLayout clientLabelLayout = new HorizontalLayout();
        clientLabelLayout.setHeight(25, Unit.PIXELS);
        clientLabelLayout.setWidth(100, Unit.PIXELS);
        
        Label clientLabel = new Label("Client:");
        clientLabel.setSizeUndefined();
        clientLabelLayout.addComponent(clientLabel);
        clientLabelLayout.setComponentAlignment(clientLabel, Alignment.MIDDLE_RIGHT);
        
        layout.addComponent(clientLabelLayout, 0, 1);
        layout.setComponentAlignment(clientLabelLayout, Alignment.MIDDLE_RIGHT);
        
        HorizontalLayout clientComboBoxLayout = new HorizontalLayout();
        clientComboBoxLayout.setHeight(25, Unit.PIXELS);
        clientComboBoxLayout.setWidth(350, Unit.PIXELS);
        this.clientComboBox.setWidth(300, Unit.PIXELS);
        this.clientComboBox.removeAllValidators();
        this.clientComboBox.addValidator(new NullValidator("A client must be selected!", false));
        this.clientComboBox.setValidationVisible(false);
        clientComboBoxLayout.addComponent(this.clientComboBox);
        layout.addComponent(clientComboBoxLayout, 1, 1);

        HorizontalLayout typeLabelLayout = new HorizontalLayout();
        typeLabelLayout.setHeight(25, Unit.PIXELS);
        typeLabelLayout.setWidth(100, Unit.PIXELS);
        
        Label typeLabel = new Label("Type:");
        typeLabel.setSizeUndefined();
        typeLabelLayout.addComponent(typeLabel);
        typeLabelLayout.setComponentAlignment(typeLabel, Alignment.MIDDLE_RIGHT);
        
        layout.addComponent(typeLabelLayout, 0, 2);
        layout.setComponentAlignment(typeLabelLayout, Alignment.MIDDLE_RIGHT);
        
        HorizontalLayout typeComboBoxLayout = new HorizontalLayout();
        typeComboBoxLayout.setHeight(25, Unit.PIXELS);
        typeComboBoxLayout.setWidth(350, Unit.PIXELS);
        this.typeComboBox.setWidth(300, Unit.PIXELS);
        this.typeComboBox.removeAllValidators();
        this.typeComboBox.addValidator(new NullValidator("A type must be selected!", false));
        this.typeComboBox.setValidationVisible(false);
        typeComboBoxLayout.addComponent(this.typeComboBox);
        layout.addComponent(typeComboBoxLayout, 1, 2);

        HorizontalLayout sourceContextLabelLayout = new HorizontalLayout();
        sourceContextLabelLayout.setHeight(25, Unit.PIXELS);
        sourceContextLabelLayout.setWidth(100, Unit.PIXELS);
        
        Label sourceContextLabel = new Label("Source Context:");
        sourceContextLabel.setSizeUndefined();
        sourceContextLabelLayout.addComponent(sourceContextLabel);
        sourceContextLabelLayout.setComponentAlignment(sourceContextLabel, Alignment.MIDDLE_RIGHT);
        
        layout.addComponent(sourceContextLabelLayout, 0, 3);
        layout.setComponentAlignment(sourceContextLabelLayout, Alignment.MIDDLE_RIGHT);
        
        HorizontalLayout sourceContextComboBoxLayout = new HorizontalLayout();
        sourceContextComboBoxLayout.setHeight(25, Unit.PIXELS);
        sourceContextComboBoxLayout.setWidth(350, Unit.PIXELS);
        this.sourceContextComboBox.setWidth(300, Unit.PIXELS);
        this.sourceContextComboBox.removeAllValidators();
        this.sourceContextComboBox.addValidator(new NullValidator("A source context must be selected", false));
        this.sourceContextComboBox.setValidationVisible(false);
        sourceContextComboBoxLayout.addComponent(this.sourceContextComboBox);
        layout.addComponent(sourceContextComboBoxLayout, 1, 3);

        HorizontalLayout targetContextLabelLayout = new HorizontalLayout();
        targetContextLabelLayout.setHeight(25, Unit.PIXELS);
        targetContextLabelLayout.setWidth(100, Unit.PIXELS);
        
        Label targetContextLabel = new Label("Target Context:");
        targetContextLabel.setSizeUndefined();
        targetContextLabelLayout.addComponent(targetContextLabel);
        targetContextLabelLayout.setComponentAlignment(targetContextLabel, Alignment.MIDDLE_RIGHT);
        
        layout.addComponent(targetContextLabelLayout, 0, 4);
        layout.setComponentAlignment(targetContextLabelLayout, Alignment.MIDDLE_RIGHT);
        
        HorizontalLayout targetContextComboBoxLayout = new HorizontalLayout();
        targetContextComboBoxLayout.setHeight(25, Unit.PIXELS);
        targetContextComboBoxLayout.setWidth(350, Unit.PIXELS);
        this.targetContextComboBox.setWidth(300, Unit.PIXELS);
        this.targetContextComboBox.removeAllValidators();
        this.targetContextComboBox.addValidator(new NullValidator("A target context must be selected",false));
        this.targetContextComboBox.setValidationVisible(false);
        targetContextComboBoxLayout.addComponent(this.targetContextComboBox);
        layout.addComponent(this.targetContextComboBox, 1, 4);
        layout.setComponentAlignment(this.targetContextComboBox, Alignment.MIDDLE_LEFT);

        HorizontalLayout descriptionLabelLayout = new HorizontalLayout();
        descriptionLabelLayout.setHeight(25, Unit.PIXELS);
        descriptionLabelLayout.setWidth(100, Unit.PIXELS);
        
        Label descriptionLabel = new Label("Description:");
        descriptionLabel.setSizeUndefined();
        descriptionLabelLayout.addComponent(descriptionLabel);
        descriptionLabelLayout.setComponentAlignment(descriptionLabel, Alignment.TOP_RIGHT);
        
        layout.addComponent(descriptionLabelLayout, 0, 5);
        layout.setComponentAlignment(descriptionLabelLayout, Alignment.TOP_RIGHT);
        
        HorizontalLayout descriptionTextAreaLayout = new HorizontalLayout();
        descriptionTextAreaLayout.setHeight(75, Unit.PIXELS);
        descriptionTextAreaLayout.setWidth(350, Unit.PIXELS);
        this.descriptionTextArea = new TextArea();
        this.descriptionTextArea.setWidth(300, Unit.PIXELS);
        this.descriptionTextArea.setRows(4);
        this.descriptionTextArea.addValidator(new StringLengthValidator(
            "A description must be entered.",
            1, null, true));
        this.descriptionTextArea.setValidationVisible(false);
        descriptionTextAreaLayout.addComponent(this.descriptionTextArea);
        layout.addComponent(descriptionTextAreaLayout, 1, 5);

        numSourceParamsLabel = new Label("Number of source parameters:");
        numSourceParamsLabel.setWidth(175, Unit.PIXELS);
        numTargetParamsLabel = new Label("Number of target parameters:");
        numTargetParamsLabel.setWidth(175, Unit.PIXELS);
        numTargetParamsLabel.setVisible(false);

        this.isManyToManyCheckbox = new CheckBox("Many to Many");
        this.isManyToManyCheckbox.setWidth(175, Unit.PIXELS);

        layout.addComponent(this.isManyToManyCheckbox, 2, 1);

        this.constrainParameterListSizesCheckbox = new CheckBox("Fixed parameter list sizes");
        this.constrainParameterListSizesCheckbox.setVisible(false);

        layout.addComponent(this.constrainParameterListSizesCheckbox, 3, 1);
        


        layout.addComponent(numSourceParamsLabel, 2, 2);
        this.numberOfSourceParametersTextField = new TextField();
        this.numberOfSourceParametersTextField.setWidth(75, Unit.PIXELS);
        this.numberOfSourceParametersTextField.removeAllValidators();
        this.numberOfSourceParametersTextField.addValidator(new LongValidator("Number of source parameters " +
        		"must be defined."));
        this.numberOfSourceParametersTextField.setValidationVisible(false);
        layout.addComponent(this.numberOfSourceParametersTextField, 3, 2);

        layout.addComponent(numTargetParamsLabel, 2, 3);
        this.numberOfTargetParametersTextField = new TextField();
        this.numberOfTargetParametersTextField.setWidth(75, Unit.PIXELS);
        this.numberOfTargetParametersTextField.removeAllValidators();
        this.numberOfTargetParametersTextField.addValidator(new IntegerValidator("Number of target parameters " +
                "must be defined."));
        this.numberOfTargetParametersTextField.setValidationVisible(false);
        this.numberOfTargetParametersTextField.setVisible(false);
        layout.addComponent(this.numberOfTargetParametersTextField, 3, 3);


        sourceParamNameLabel = new Label("Source Parameter Names");
        sourceParamNameLabel.setWidth(175, Unit.PIXELS);

        sourceParamNameValueTextArea = new TextArea();
        sourceParamNameValueTextArea.setWidth("80%");
        sourceParamNameValueTextArea.setRows(4);
        sourceParamNameValueTextArea.setReadOnly(true);

        layout.addComponent(sourceParamNameLabel, 2, 4);
        layout.addComponent(sourceParamNameValueTextArea, 3, 4);


        targetParamNameLabel = new Label("Target Parameter Names");
        targetParamNameLabel.setWidth(175, Unit.PIXELS);

        targetParamNameValueTextArea = new TextArea();
        targetParamNameValueTextArea.setWidth("80%");
        targetParamNameValueTextArea.setRows(4);
        targetParamNameValueTextArea.setReadOnly(true);

        layout.addComponent(targetParamNameLabel, 2, 5);
        layout.addComponent(targetParamNameValueTextArea, 3, 5);

        return layout;
    }

    /**
     * Helper method to create the layout of the table.
     * 
     * @param buttonsVisible are the buttons visible?
     * 
     * @return the Layout associated with the table.
     */
    protected Layout createTableLayout(boolean buttonsVisible)
    {


        HorizontalLayout controlsLayout = new HorizontalLayout();
        controlsLayout.setWidth("100%");
        controlsLayout.setHeight("30px");

        this.addNewRecordButton.setIcon(VaadinIcons.PLUS);
        this.addNewRecordButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
        this.addNewRecordButton.setDescription("Add a mapping configuration value to the table below");
        this.addNewRecordButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        this.addNewRecordButton.setVisible(buttonsVisible);

        this.addNewRecordButton.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                saveRequiredMonitor.setSaveRequired(true);
                try
                {
                    mappingConfigurationFunctionalGroup.editButtonPressed();
                    mappingConfigurationConfigurationValuesTable.addNewRecord();
                }
                catch (MappingConfigurationServiceException e)
                {
                    logger.error(e.getMessage(), e);
                }
            }
        });

        final RemoveAllItemsAction removeAllItemsAction = new RemoveAllItemsAction(this.mappingConfigurationConfigurationValuesTable);

        this.deleteAllRecordsButton.setIcon(VaadinIcons.TRASH);
        this.deleteAllRecordsButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
        this.deleteAllRecordsButton.setDescription("Delete all values from the table below");
        this.deleteAllRecordsButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        this.deleteAllRecordsButton.setVisible(buttonsVisible);
        this.deleteAllRecordsButton.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                IkasanMessageDialog dialog = new IkasanMessageDialog("Delete all records!", 
                    "This action will delete all source and target mapping configurations " +
                    "from the database. Are you sure you would like to proceed?", removeAllItemsAction);

                UI.getCurrent().addWindow(dialog);
            }
        });

        this.importMappingConfigurationButton.setIcon(VaadinIcons.UPLOAD_ALT);
        this.importMappingConfigurationButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
        this.importMappingConfigurationButton.setDescription("Import mapping configuration values");
        this.importMappingConfigurationButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        this.importMappingConfigurationButton.setVisible(buttonsVisible);
        this.importMappingConfigurationButton.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                MappingConfigurationValuesImportWindow dialog = new MappingConfigurationValuesImportWindow(mappingConfigurationService
                    , mappingConfiguration, mappingConfigurationConfigurationValuesTable, systemEventService);

                UI.getCurrent().addWindow(dialog);
            }
        });

        this.exportMappingConfigurationValuesButton.setIcon(VaadinIcons.DOWNLOAD_ALT);
        this.exportMappingConfigurationValuesButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
        this.exportMappingConfigurationValuesButton.setDescription("Export mapping configuration values");
        this.exportMappingConfigurationValuesButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        this.exportMappingConfigurationValuesButton.setVisible(buttonsVisible);

        Label spacer = new Label("&nbsp;",  ContentMode.HTML);
        controlsLayout.addComponent(spacer);
        controlsLayout.setExpandRatio(spacer, 0.84f);
        controlsLayout.addComponent(this.addNewRecordButton);
        controlsLayout.setExpandRatio(this.addNewRecordButton, 0.04f);
        controlsLayout.addComponent(this.deleteAllRecordsButton);
        controlsLayout.setExpandRatio(this.deleteAllRecordsButton, 0.04f);
        controlsLayout.addComponent(this.importMappingConfigurationButton);
        controlsLayout.setExpandRatio(this.importMappingConfigurationButton, 0.04f);
        controlsLayout.addComponent(this.exportMappingConfigurationValuesButton);
        controlsLayout.setExpandRatio(this.exportMappingConfigurationValuesButton, 0.04f);

        VerticalLayout tableLayout = new VerticalLayout();
        tableLayout.setSpacing(false);
        tableLayout.setSizeFull();


        VerticalSplitPanel vpanel = new VerticalSplitPanel(controlsLayout
                , this.mappingConfigurationConfigurationValuesTable);
        vpanel.setSplitPosition(30, Unit.PIXELS);
        vpanel.setLocked(true);

        tableLayout.addComponent(vpanel);

        return tableLayout;
    }

    /**
     * Helper method to get the stream associated with the export of the file.
     * 
     * @return the StreamResource associated with the export.
     */
    private StreamResource getMappingConfigurationValuesExportStream() 
    {
        StreamResource.StreamSource source = new StreamResource.StreamSource()
        {

                public InputStream getStream() {
                    ByteArrayOutputStream stream = null;
                    try
                    {
                        stream = getMappingConfigurationValuesExport();
                    }
                    catch (IOException e)
                    {
                    	logger.error(e.getMessage(), e);
                    }
                    InputStream input = new ByteArrayInputStream(stream.toByteArray());
                      return input;
    
                }
        };

        StringBuffer fileName = new StringBuffer();
        fileName.append(this.mappingConfiguration.getConfigurationServiceClient().getName()).append("_");
        fileName.append(this.mappingConfiguration.getConfigurationType().getName()).append("_");
        fileName.append(this.mappingConfiguration.getSourceContext().getName()).append("_");
        fileName.append(this.mappingConfiguration.getTargetContext().getName()).append("_mappingValuesExport.xml");

        StreamResource resource = new StreamResource ( source, fileName.toString());
        return resource;
    }

    /**
     * Helper method to get the stream associated with the export of the file.
     * 
     * @return the StreamResource associated with the export.
     */
    private StreamResource getMappingConfigurationExportStream() 
    {
        StreamResource.StreamSource source = new StreamResource.StreamSource() 
        {

                public InputStream getStream() {
                    ByteArrayOutputStream stream = null;
                    try
                    {
                        stream = getMappingConfigurationExport();
                    }
                    catch (IOException e)
                    {
                    	logger.error(e.getMessage(), e);
                    }
                    InputStream input = new ByteArrayInputStream(stream.toByteArray());
                      return input;
    
                }
        };

        StringBuffer fileName = new StringBuffer();
        fileName.append(this.mappingConfiguration.getConfigurationServiceClient().getName()).append("_");
        fileName.append(this.mappingConfiguration.getConfigurationType().getName()).append("_");
        fileName.append(this.mappingConfiguration.getSourceContext().getName()).append("_");
        fileName.append(this.mappingConfiguration.getTargetContext().getName()).append("_mappingExport.xml");

        StreamResource resource = new StreamResource ( source, fileName.toString());
          return resource;
    }

    /**
     * Helper method to save values associated with this panel.
     * 
     * @throws InvalidValueException
     * @throws Exception
     */
    public void save() throws InvalidValueException, Exception
    {
    	IkasanAuthentication principal = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
                .getAttribute(DashboardSessionValueConstants.USER);

        try 
        {
            try 
            {
                this.clientComboBox.validate();
                this.typeComboBox.validate();
                this.sourceContextComboBox.validate();
                this.targetContextComboBox.validate();
                this.descriptionTextArea.validate();
            } 
            catch (InvalidValueException e) 
            {
                this.clientComboBox.setValidationVisible(true);
                this.typeComboBox.setValidationVisible(true);
                this.sourceContextComboBox.setValidationVisible(true);
                this.targetContextComboBox.setValidationVisible(true);
                this.descriptionTextArea.setValidationVisible(true);

                throw e;
            }


            logger.debug("Attempting to save mapping configuration.");
            this.mappingConfiguration.setConfigurationServiceClient
                ((ConfigurationServiceClient)this.clientComboBox.getValue());
    
            this.mappingConfiguration.setConfigurationType
                ((ConfigurationType)this.typeComboBox.getValue());
    
            logger.debug("Source context = ." + ((ConfigurationContext)
                    this.sourceContextComboBox.getValue()).getName());
    
            this.mappingConfiguration.setSourceContext
                ((ConfigurationContext)this.sourceContextComboBox.getValue());
    
            this.mappingConfiguration.setTargetContext
                ((ConfigurationContext)this.targetContextComboBox.getValue());

            this.mappingConfiguration.setLastUpdatedBy(principal.getName());

            this.mappingConfiguration.setNumberOfMappings
                    (this.getNumberOfMappings(mappingConfiguration.getSourceConfigurationValues()));

            logger.info("Number of target values = " + this.mappingConfiguration.getNumTargetValues());

            try
            {
               
                logger.debug("User: " + principal.getName() + " saving Mapping Configuration: " +
                		this.mappingConfiguration);

                if(this.mappingConfigurationValidator.validate(mappingConfiguration) == false)
                {
                    Notification.show("This mapping has been saved with the following warnings!\r\n\r\n" +
                            "The following source system values are duplicated. This has the effect of calls to the mapping " +
                            "service resolving multiple results. \r\n\r\n"
                            + this.mappingConfigurationValidator.getErrorMessage(), Notification.Type.ERROR_MESSAGE);
                }

                this.mappingConfigurationService.saveMappingConfiguration(this.mappingConfiguration);
                
                String message = "[Client=" + mappingConfiguration.getConfigurationServiceClient().getName()
                		+"] [Source Context=" + mappingConfiguration.getSourceContext().getName() + "] [Target Context=" 
                		+ mappingConfiguration.getTargetContext().getName() + "] [Type=" + mappingConfiguration.getConfigurationType().getName()
                		+ "] [Description=" + mappingConfiguration.getDescription() +"] [Number of source params=" 
                		+ mappingConfiguration.getNumberOfParams() + "]";
                
                systemEventService.logSystemEvent(MappingConfigurationConstants.MAPPING_CONFIGURATION_SERVICE, 
                		"Saving Mapping Configuration: " + message, principal.getName());
            }
            catch(MappingConfigurationServiceException e)
            {
                throw new Exception("Unable to save Mapping Configuration. Client, " +
                        "Type, Source and Target Context must be unique!");
            }

            this.mappingConfigurationConfigurationValuesTable.save();

            this.clientComboBox.setValidationVisible(false);
            this.typeComboBox.setValidationVisible(false);
            this.sourceContextComboBox.setValidationVisible(false);
            this.targetContextComboBox.setValidationVisible(false);
            this.descriptionTextArea.setValidationVisible(false);
            this.numberOfSourceParametersTextField.setValidationVisible(false);

            this.isManyToManyCheckbox.setReadOnly(true);
        }
        catch(InvalidValueException e)
        {
            throw e;
        }
        catch(Exception e)
        {
            throw e;
        }
    }

    private int getNumberOfMappings(Set<SourceConfigurationValue> values)
    {
        int num = 0;

        Set<Long> groupKeys = new HashSet<Long>();

        for(SourceConfigurationValue value: values)
        {
           if(value.getSourceConfigGroupId() == null)
           {
               num++;
           }
           else
           {
               groupKeys.add(value.getSourceConfigGroupId());
           }
        }

        if(num > 0)
        {
            return num;
        }
        else
        {
            return groupKeys.size();
        }
    }

    /**
     * Helper method to get the ByteArrayOutputStream associated with the export.
     * 
     * @return
     * @throws IOException
     */
    private ByteArrayOutputStream getMappingConfigurationValuesExport() throws IOException
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        String schemaLocation = (String)this.platformConfigurationService.getConfigurationValue("mappingValuesExportSchemaLocation");        
        
        if(schemaLocation == null || schemaLocation.length() == 0)
        {
        	throw new RuntimeException("Cannot resolve the platform configuration mappingValuesExportSchemaLocation!");
        }
        
        logger.debug("Resolved schemaLocation " + schemaLocation);

        String exportXml = this.mappingConfigurationValuesExportHelper.getMappingConfigurationExportXml(this.mappingConfiguration, true,
        		schemaLocation);

        out.write(exportXml.getBytes());

        return out;
    }

    /**
     * Helper method to get the ByteArrayOutputStream associated with the export.
     * 
     * @return
     * @throws IOException
     */
    private ByteArrayOutputStream getMappingConfigurationExport() throws IOException
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        String schemaLocation = (String)this.platformConfigurationService.getConfigurationValue("mappingExportSchemaLocation");        
        
        if(schemaLocation == null || schemaLocation.length() == 0)
        {
        	throw new RuntimeException("Cannot resolve the platform configuration mappingExportSchemaLocation!");
        }
        
        logger.debug("Resolved schemaLocation " + schemaLocation);


        String exportXml = this.mappingConfigurationExportHelper.getMappingConfigurationExportXml(this.mappingConfiguration,
            this.sourceContextParameterNames, this.targetContextParameterNames, schemaLocation);

        out.write(exportXml.getBytes());

        return out;
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
        this.isManyToManyCheckbox.setReadOnly(false);
        this.constrainParameterListSizesCheckbox.setReadOnly(false);

        BeanItem<MappingConfiguration> mappingConfigurationItem = new BeanItem<MappingConfiguration>(this.mappingConfiguration);
        
        logger.debug("Attempting to populate form with mapping configuration: " + this.mappingConfiguration);
        
        this.clientComboBox.loadClientSelectValues();
        this.typeComboBox.loadClientTypeValues();
        this.sourceContextComboBox.loadContextValues();
        this.targetContextComboBox.loadContextValues();

        this.clientComboBox.setValue(this.mappingConfiguration.getConfigurationServiceClient());
        this.typeComboBox.setValue(mappingConfiguration.getConfigurationType());
        this.sourceContextComboBox.setValue(mappingConfiguration.getSourceContext());
        this.targetContextComboBox.setValue(mappingConfiguration.getTargetContext());
        this.isManyToManyCheckbox.setValue(mappingConfiguration.getIsManyToMany());
        this.constrainParameterListSizesCheckbox.setValue(mappingConfiguration.getConstrainParameterListSizes());
        this.descriptionTextArea.setPropertyDataSource(mappingConfigurationItem.getItemProperty("description"));
        this.numberOfSourceParametersTextField.setPropertyDataSource(mappingConfigurationItem.getItemProperty("numberOfParams"));
        this.numberOfTargetParametersTextField.setPropertyDataSource(mappingConfigurationItem.getItemProperty("numTargetValues"));

        if(isManyToManyCheckbox.getValue() == true)
        {
            constrainParameterListSizesCheckbox.setVisible(true);

            if(constrainParameterListSizesCheckbox.getValue() == true)
            {
                numSourceParamsLabel.setVisible(true);
                numTargetParamsLabel.setVisible(true);
                numberOfSourceParametersTextField.setVisible(true);
                numberOfTargetParametersTextField.setVisible(true);
            }
            else
            {
                numTargetParamsLabel.setVisible(false);
                numberOfTargetParametersTextField.setVisible(false);
                numSourceParamsLabel.setVisible(false);
                numberOfSourceParametersTextField.setVisible(false);
            }
        }
        else
        {
            numSourceParamsLabel.setVisible(true);
            numberOfSourceParametersTextField.setVisible(true);
        }

        if(sourceContextParameterNames != null && sourceContextParameterNames.size() > 0)
        {
            StringBuffer sb = new StringBuffer();

            for(ParameterName name: sourceContextParameterNames)
            {
                sb.append(name.getName()).append("\n");
            }

            sourceParamNameValueTextArea.setReadOnly(false);
            sourceParamNameValueTextArea.setValue(sb.toString());
            sourceParamNameValueTextArea.setReadOnly(true);
            sourceParamNameValueTextArea.setVisible(true);
            sourceParamNameLabel.setVisible(true);
        }
        else
        {
            sourceParamNameValueTextArea.setVisible(false);
            sourceParamNameLabel.setVisible(false);
        }

        if(targetContextParameterNames != null && targetContextParameterNames.size() > 0)
        {
            StringBuffer sb = new StringBuffer();

            for(ParameterName name: targetContextParameterNames)
            {
                sb.append(name.getName()).append("\n");
            }

            targetParamNameValueTextArea.setReadOnly(false);
            targetParamNameValueTextArea.setValue(sb.toString());
            targetParamNameValueTextArea.setReadOnly(true);
            targetParamNameValueTextArea.setVisible(true);
            targetParamNameLabel.setVisible(true);
        }
        else
        {
            targetParamNameValueTextArea.setVisible(false);
            targetParamNameLabel.setVisible(false);
        }



        this.isManyToManyCheckbox.setReadOnly(true);
        typeComboBox.setReadOnly(true);
        clientComboBox.setReadOnly(true);
        sourceContextComboBox.setReadOnly(true);
        targetContextComboBox.setReadOnly(true);
        this.descriptionTextArea.setReadOnly(true);
        this.numberOfSourceParametersTextField.setReadOnly(true);
        this.numberOfTargetParametersTextField.setReadOnly(true);
        this.constrainParameterListSizesCheckbox.setReadOnly(true);
    }

    /* (non-Javadoc)
     * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
     */
    @Override
    public void enter(ViewChangeEvent event)
    {
    	this.clientComboBox.loadClientSelectValues();
    	this.sourceContextComboBox.loadContextValues();
    	this.targetContextComboBox.loadContextValues();
    	this.typeComboBox.loadClientTypeValues();
    	
    	final IkasanAuthentication authentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
 	        	.getAttribute(DashboardSessionValueConstants.USER);
        	
    	if(authentication != null 
    			&& (authentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY)
    					|| authentication.hasGrantedAuthority(SecurityConstants.EDIT_MAPPING_AUTHORITY)
    					|| (this.mappingConfiguration != null && authentication.canAccessLinkedItem
    						(PolicyLinkTypeConstants.MAPPING_CONFIGURATION_LINK_TYPE, this.mappingConfiguration.getId()))))
    	{
    		this.mappingConfigurationFunctionalGroup.initialiseButtonState();
    	}
    }

    private void loadParameterNames()
    {
        List<ParameterName> parameterNames = this.mappingConfigurationService.getParameterNamesByMappingConfigurationId
                (this.mappingConfiguration.getId());

        this.sourceContextParameterNames = new ArrayList<ParameterName>();
        this.targetContextParameterNames = new ArrayList<ParameterName>();

        for(ParameterName parameterName: parameterNames)
        {
            if(parameterName.getContext().equals(ParameterName.SOURCE_CONTEXT))
            {
                this.sourceContextParameterNames.add(parameterName);
            }
            else if(parameterName.getContext().equals(ParameterName.TARGET_CONTEXT))
            {
                this.targetContextParameterNames.add(parameterName);
            }
        }
    }

    /**
     * @param mappingConfiguration the mappingConfiguration to set
     */
    public void setMappingConfiguration(MappingConfiguration mappingConfiguration)
    {
        this.mappingConfiguration = mappingConfiguration;
        this.loadParameterNames();

        FileDownloader fd = new FileDownloader(this.getMappingConfigurationExportStream());
        fd.extend(exportMappingConfigurationButton);

        FileDownloader fdValues = new FileDownloader(this.getMappingConfigurationValuesExportStream());
        fdValues.extend(exportMappingConfigurationValuesButton);
        
        this.mappingConfigurationConfigurationValuesTable.populateTable(mappingConfiguration);
    }
}
