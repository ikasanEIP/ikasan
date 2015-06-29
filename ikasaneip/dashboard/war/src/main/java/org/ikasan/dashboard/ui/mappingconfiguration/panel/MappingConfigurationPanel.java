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
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.ikasan.dashboard.ui.framework.group.FunctionalGroup;
import org.ikasan.dashboard.ui.framework.util.DashboardSessionValueConstants;
import org.ikasan.dashboard.ui.framework.util.SaveRequiredMonitor;
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
import org.ikasan.mapping.model.ConfigurationContext;
import org.ikasan.mapping.model.ConfigurationServiceClient;
import org.ikasan.mapping.model.ConfigurationType;
import org.ikasan.mapping.model.KeyLocationQuery;
import org.ikasan.mapping.model.MappingConfiguration;
import org.ikasan.mapping.model.PlatformConfiguration;
import org.ikasan.mapping.service.MappingConfigurationService;
import org.ikasan.mapping.service.MappingConfigurationServiceException;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.ikasan.systemevent.service.SystemEventService;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.validator.NullValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.themes.BaseTheme;
import com.vaadin.ui.themes.Reindeer;

/**
 * @author Ikasan Development Team
 *
 */
public class MappingConfigurationPanel extends Panel implements View
{
    private static final long serialVersionUID = 5269092088876470789L;

    private Logger logger = Logger.getLogger(MappingConfigurationPanel.class);
    protected GridLayout layout;
    protected VerticalLayout paramQueriesLayout = new VerticalLayout();
    protected MappingConfigurationConfigurationValuesTable mappingConfigurationConfigurationValuesTable;
    protected MappingConfiguration mappingConfiguration;
    protected ClientComboBox clientComboBox;
    protected TypeComboBox typeComboBox;
    protected SourceContextComboBox sourceContextComboBox;
    protected TargetContextComboBox targetContextComboBox;
    protected TextArea descriptionTextArea;
    protected TextField numberOfParametersTextField;
    protected List<TextField> parameterQueryTextFields = new ArrayList<TextField>();
    protected MappingConfigurationService mappingConfigurationService;
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
    protected List<KeyLocationQuery> keyLocationQueries;
    protected FunctionalGroup mappingConfigurationFunctionalGroup;
    protected MappingConfigurationExportHelper mappingConfigurationExportHelper;
    protected MappingConfigurationValuesExportHelper mappingConfigurationValuesExportHelper;
    protected SystemEventService systemEventService;
    

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
            TargetContextComboBox targetContextComboBox, String name, MappingConfigurationService mappingConfigurationService,
            SaveRequiredMonitor saveRequiredMonitor, Button editButton, Button saveButton, Button addNewRecordButton, 
            Button deleteAllRecordsButton, Button importMappingConfigurationButton, Button exportMappingConfigurationValuesButton,
            Button exportMappingConfigurationButton, Button cancelButton, FunctionalGroup mappingConfigurationFunctionalGroup,
            MappingConfigurationExportHelper mappingConfigurationExportHelper, MappingConfigurationValuesExportHelper 
            mappingConfigurationValuesExportHelper, SystemEventService systemEventService)
    {
        super(name);
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
    }

    /**
     * Helper method to initialise this object.
     */
    @SuppressWarnings("serial")
    protected void init()
    {
    	this.setStyleName("dashboard");
        layout = new GridLayout(4, 5);
        paramQueriesLayout = new VerticalLayout();

        toolBarLayout = new HorizontalLayout();
        toolBarLayout.setWidth("100%");

        Button linkButton = new Button("Return to search results");
        linkButton.setStyleName(BaseTheme.BUTTON_LINK);

        linkButton.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                saveRequiredMonitor.manageSaveRequired("searchResultsPanel");
            }
        });

        toolBarLayout.addComponent(linkButton);
        toolBarLayout.setExpandRatio(linkButton, 0.865f);

        this.editButton.setStyleName(BaseTheme.BUTTON_LINK);
        this.editButton.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                setEditable(true);
                mappingConfigurationFunctionalGroup.editButtonPressed();
            }
        });

        toolBarLayout.addComponent(this.editButton);
        toolBarLayout.setExpandRatio(this.editButton, 0.045f);

        this.saveButton.setStyleName(BaseTheme.BUTTON_LINK);
        this.saveButton.setVisible(false);
        this.saveButton.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                try
                {
                    logger.info("Save button clicked!!");
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
                catch (Exception e) {
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    e.printStackTrace(pw);

                    Notification.show("Cauget exception trying to save a Mapping Configuration!", sw.toString()
                        , Notification.Type.ERROR_MESSAGE);
                }
            }
        });

        toolBarLayout.addComponent(this.saveButton);
        toolBarLayout.setExpandRatio(this.saveButton, 0.045f);

        this.cancelButton.setStyleName(BaseTheme.BUTTON_LINK);
        this.cancelButton.setVisible(false);
        this.cancelButton.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                setEditable(false);
                mappingConfigurationFunctionalGroup.saveOrCancelButtonPressed();
                UI.getCurrent().getNavigator().navigateTo("searchResultsPanel");
            }
        });

        toolBarLayout.addComponent(this.cancelButton);
        toolBarLayout.setExpandRatio(this.cancelButton, 0.045f);

        FileDownloader fd = new FileDownloader(this.getMappingConfigurationExportStream());
        fd.extend(exportMappingConfigurationButton);

        this.exportMappingConfigurationButton.setStyleName(BaseTheme.BUTTON_LINK);
        toolBarLayout.addComponent(this.exportMappingConfigurationButton);
        toolBarLayout.setExpandRatio(this.exportMappingConfigurationButton, 0.045f);

        final VerticalLayout contentLayout = new VerticalLayout();
        
        contentLayout.addComponent(toolBarLayout);
        contentLayout.addComponent(createMappingConfigurationForm());

        VerticalSplitPanel vpanel = new VerticalSplitPanel(contentLayout
            , createTableLayout(false));

        Panel queryParamsPanel = new Panel("Source Configuration Value Queries");
        queryParamsPanel.setHeight(200, Unit.PIXELS);
        queryParamsPanel.setWidth(100, Unit.PERCENTAGE);
        queryParamsPanel.setContent(paramQueriesLayout);
        this.layout.addComponent(queryParamsPanel, 2, 4, 3, 4);

        vpanel.setSplitPosition(290, Unit.PIXELS);
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

        for(TextField textField: this.parameterQueryTextFields)
        {
            textField.setReadOnly(!editable);
        }
    }

    /**
     * Helper method to create the form associated with the mapping
     * configuration.
     * 
     * @return the Layout of the form
     */
    protected GridLayout createMappingConfigurationForm()
    {
        layout.setMargin(true);

        HorizontalLayout clientLabelLayout = new HorizontalLayout();
        clientLabelLayout.setHeight(25, Unit.PIXELS);
        clientLabelLayout.setWidth(100, Unit.PIXELS);
        clientLabelLayout.addComponent(new Label("Client"));
        layout.addComponent(clientLabelLayout, 0, 0);
        HorizontalLayout clientComboBoxLayout = new HorizontalLayout();
        clientComboBoxLayout.setHeight(25, Unit.PIXELS);
        clientComboBoxLayout.setWidth(350, Unit.PIXELS);
        this.clientComboBox.setWidth(300, Unit.PIXELS);
        this.clientComboBox.removeAllValidators();
        this.clientComboBox.addValidator(new NullValidator("A client must be selected!", false));
        this.clientComboBox.setValidationVisible(false);
        clientComboBoxLayout.addComponent(this.clientComboBox);
        layout.addComponent(clientComboBoxLayout, 1, 0);

        HorizontalLayout typeLabelLayout = new HorizontalLayout();
        typeLabelLayout.setHeight(25, Unit.PIXELS);
        typeLabelLayout.setWidth(100, Unit.PIXELS);
        typeLabelLayout.addComponent(new Label("Type"));
        layout.addComponent(typeLabelLayout, 0, 1);
        HorizontalLayout typeComboBoxLayout = new HorizontalLayout();
        typeComboBoxLayout.setHeight(25, Unit.PIXELS);
        typeComboBoxLayout.setWidth(350, Unit.PIXELS);
        this.typeComboBox.setWidth(300, Unit.PIXELS);
        this.typeComboBox.removeAllValidators();
        this.typeComboBox.addValidator(new NullValidator("A type must be selected!", false));
        this.typeComboBox.setValidationVisible(false);
        typeComboBoxLayout.addComponent(this.typeComboBox);
        layout.addComponent(typeComboBoxLayout, 1, 1);

        HorizontalLayout sourceContextLabelLayout = new HorizontalLayout();
        sourceContextLabelLayout.setHeight(25, Unit.PIXELS);
        sourceContextLabelLayout.setWidth(100, Unit.PIXELS);
        sourceContextLabelLayout.addComponent(new Label("Source Context"));
        layout.addComponent(sourceContextLabelLayout, 0, 2);
        HorizontalLayout sourceContextComboBoxLayout = new HorizontalLayout();
        sourceContextComboBoxLayout.setHeight(25, Unit.PIXELS);
        sourceContextComboBoxLayout.setWidth(350, Unit.PIXELS);
        this.sourceContextComboBox.setWidth(300, Unit.PIXELS);
        this.sourceContextComboBox.removeAllValidators();
        this.sourceContextComboBox.addValidator(new NullValidator("A source context must be selected", false));
        this.sourceContextComboBox.setValidationVisible(false);
        sourceContextComboBoxLayout.addComponent(this.sourceContextComboBox);
        layout.addComponent(sourceContextComboBoxLayout, 1, 2);

        HorizontalLayout targetContextLabelLayout = new HorizontalLayout();
        targetContextLabelLayout.setHeight(25, Unit.PIXELS);
        targetContextLabelLayout.setWidth(100, Unit.PIXELS);
        targetContextLabelLayout.addComponent(new Label("Target Context"));
        layout.addComponent(targetContextLabelLayout, 0, 3);
        HorizontalLayout targetContextComboBoxLayout = new HorizontalLayout();
        targetContextComboBoxLayout.setHeight(25, Unit.PIXELS);
        targetContextComboBoxLayout.setWidth(350, Unit.PIXELS);
        this.targetContextComboBox.setWidth(300, Unit.PIXELS);
        this.targetContextComboBox.removeAllValidators();
        this.targetContextComboBox.addValidator(new NullValidator("A target context must be selected",false));
        this.targetContextComboBox.setValidationVisible(false);
        targetContextComboBoxLayout.addComponent(this.targetContextComboBox);
        layout.addComponent(this.targetContextComboBox, 1, 3);

        HorizontalLayout descriptionLabelLayout = new HorizontalLayout();
        descriptionLabelLayout.setHeight(25, Unit.PIXELS);
        descriptionLabelLayout.setWidth(100, Unit.PIXELS);
        descriptionLabelLayout.addComponent(new Label("Description"));
        layout.addComponent(descriptionLabelLayout, 0, 4);
        HorizontalLayout descriptionTextAreaLayout = new HorizontalLayout();
        descriptionTextAreaLayout.setHeight(25, Unit.PIXELS);
        descriptionTextAreaLayout.setWidth(350, Unit.PIXELS);
        this.descriptionTextArea = new TextArea();
        this.descriptionTextArea.setWidth(300, Unit.PIXELS);
        this.descriptionTextArea.setRows(6);
        this.descriptionTextArea.addValidator(new StringLengthValidator(
            "A description must be entered.",
            1, null, true));
        this.descriptionTextArea.setValidationVisible(false);
        descriptionTextAreaLayout.addComponent(this.descriptionTextArea);
        layout.addComponent(descriptionTextAreaLayout, 1, 4);

        HorizontalLayout paramsLabelLayout = new HorizontalLayout();
        paramsLabelLayout.setHeight(25, Unit.PIXELS);
        paramsLabelLayout.setWidth(260, Unit.PIXELS);
        paramsLabelLayout.addComponent(new Label("Number source of parameters"));
        layout.addComponent(paramsLabelLayout, 2, 1);
        this.numberOfParametersTextField = new TextField();
        this.numberOfParametersTextField.setWidth(100, Unit.PIXELS);
        this.numberOfParametersTextField.removeAllValidators();
        this.numberOfParametersTextField.addValidator(new LongValidator("Number of source parameters " +
        		"and key location queries must be defined."));
        this.numberOfParametersTextField.setValidationVisible(false);
        layout.addComponent(this.numberOfParametersTextField, 3, 1);

        HorizontalLayout queriesLabelLayout = new HorizontalLayout();
        queriesLabelLayout.setHeight(25, Unit.PIXELS);
        queriesLabelLayout.setWidth(250, Unit.PIXELS);


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
        VerticalLayout tableLayout = new VerticalLayout();

        HorizontalLayout controlsLayout = new HorizontalLayout();
        controlsLayout.setWidth("100%");
        
        this.addNewRecordButton.setStyleName(Reindeer.BUTTON_LINK);
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
                    e.printStackTrace();
                }
            }
        });

        final RemoveAllItemsAction removeAllItemsAction = new RemoveAllItemsAction(this.mappingConfigurationConfigurationValuesTable);
        this.deleteAllRecordsButton.setStyleName(Reindeer.BUTTON_LINK);
        this.deleteAllRecordsButton.setVisible(buttonsVisible);
        this.deleteAllRecordsButton.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                IkasanMessageDialog dialog = new IkasanMessageDialog("Delete all records!", 
                    "This action will delete all source and target mapping configurations " +
                    "from the database. Are you sure you would like to proceed?", removeAllItemsAction);

                UI.getCurrent().addWindow(dialog);
            }
        });

        this.importMappingConfigurationButton.setStyleName(Reindeer.BUTTON_LINK);
        this.importMappingConfigurationButton.setVisible(buttonsVisible);
        this.importMappingConfigurationButton.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                MappingConfigurationValuesImportWindow dialog = new MappingConfigurationValuesImportWindow(mappingConfigurationService
                    , mappingConfiguration, mappingConfigurationConfigurationValuesTable, systemEventService);

                UI.getCurrent().addWindow(dialog);
            }
        });

        this.exportMappingConfigurationValuesButton.setStyleName(Reindeer.BUTTON_LINK);
        this.exportMappingConfigurationValuesButton.setVisible(buttonsVisible);

        FileDownloader fd = new FileDownloader(this.getMappingConfigurationValuesExportStream());
        fd.extend(exportMappingConfigurationValuesButton);

        Label spacer = new Label("&nbsp;",  ContentMode.HTML);
        controlsLayout.addComponent(spacer);
        controlsLayout.setExpandRatio(spacer, 0.8f);
        controlsLayout.addComponent(this.addNewRecordButton);
        controlsLayout.setExpandRatio(this.addNewRecordButton, 0.04f);
        controlsLayout.addComponent(this.deleteAllRecordsButton);
        controlsLayout.setExpandRatio(this.deleteAllRecordsButton, 0.06f);
        controlsLayout.addComponent(this.importMappingConfigurationButton);
        controlsLayout.setExpandRatio(this.importMappingConfigurationButton, 0.05f);
        controlsLayout.addComponent(this.exportMappingConfigurationValuesButton);
        controlsLayout.setExpandRatio(this.exportMappingConfigurationValuesButton, 0.05f);

        tableLayout.addComponent(controlsLayout);
        tableLayout.addComponent(this.mappingConfigurationConfigurationValuesTable);

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
                        e.printStackTrace();
                    }
                    InputStream input = new ByteArrayInputStream(stream.toByteArray());
                      return input;
    
                }
            };
          StreamResource resource = new StreamResource ( source,"mappingConfigurationExport.xml");
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
                        e.printStackTrace();
                    }
                    InputStream input = new ByteArrayInputStream(stream.toByteArray());
                      return input;
    
                }
            };
          StreamResource resource = new StreamResource ( source,"mappingConfigurationExport.xml");
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
                this.numberOfParametersTextField.validate();

                for(TextField tf: this.parameterQueryTextFields)
                {
                    tf.validate();
                }
            } 
            catch (InvalidValueException e) 
            {
                this.clientComboBox.setValidationVisible(true);
                this.typeComboBox.setValidationVisible(true);
                this.sourceContextComboBox.setValidationVisible(true);
                this.targetContextComboBox.setValidationVisible(true);
                this.descriptionTextArea.setValidationVisible(true);
                this.numberOfParametersTextField.setValidationVisible(true);

                for(TextField tf: this.parameterQueryTextFields)
                {
                    tf.setValidationVisible(true);
                }
                throw e;
            }

            logger.info("this.parameterQueryTextFields.size() = " + this.parameterQueryTextFields.size());
            logger.info("this.mappingConfiguration.getNumberOfParams() = " + this.mappingConfiguration.getNumberOfParams());
            
            if(this.parameterQueryTextFields.size() != this.mappingConfiguration.getNumberOfParams())
            {
                throw new Exception("You must define the key location queries!");
            }

            logger.info("Attempting to save mapping configuration.");
            this.mappingConfiguration.setConfigurationServiceClient
                ((ConfigurationServiceClient)this.clientComboBox.getValue());
    
            this.mappingConfiguration.setConfigurationType
                ((ConfigurationType)this.typeComboBox.getValue());
    
            logger.info("Source context = ." + ((ConfigurationContext)
                    this.sourceContextComboBox.getValue()).getName());
    
            this.mappingConfiguration.setSourceContext
                ((ConfigurationContext)this.sourceContextComboBox.getValue());
    
            this.mappingConfiguration.setTargetContext
                ((ConfigurationContext)this.targetContextComboBox.getValue());

            try
            {
               
                logger.info("User: " + principal.getName() + " saving Mapping Configuration: " +
                		this.mappingConfiguration);
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
                e.printStackTrace();
                throw new Exception("Unable to save Mapping Configuration. Client, " +
                        "Type, Source and Target Context must be unique!");
            }

            for(KeyLocationQuery query: this.keyLocationQueries)
            {
                query.setMappingConfigurationId(this.mappingConfiguration.getId());

                logger.info("User: " + principal.getName() + " saving Key Location Query: " +
                        query);
                this.mappingConfigurationService.saveKeyLocationQuery(query);
                
                systemEventService.logSystemEvent(MappingConfigurationConstants.MAPPING_CONFIGURATION_SERVICE, 
                		"Saving Key Location Query: " + query, principal.getName());
            }

            this.mappingConfigurationConfigurationValuesTable.save();

            this.clientComboBox.setValidationVisible(false);
            this.typeComboBox.setValidationVisible(false);
            this.sourceContextComboBox.setValidationVisible(false);
            this.targetContextComboBox.setValidationVisible(false);
            this.descriptionTextArea.setValidationVisible(false);
            this.numberOfParametersTextField.setValidationVisible(false);
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

    /**
     * Helper method to get the ByteArrayOutputStream associated with the export.
     * 
     * @return
     * @throws IOException
     */
    private ByteArrayOutputStream getMappingConfigurationValuesExport() throws IOException
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        PlatformConfiguration platformConfiguration 
    		= this.mappingConfigurationService.getPlatformConfigurationByName("mappingValuesExportSchemaLocation");
        
        logger.info("Resolved PlatformConfiguration " + platformConfiguration);

        String exportXml = this.mappingConfigurationValuesExportHelper.getMappingConfigurationExportXml(this.mappingConfiguration, true,
        		platformConfiguration.getValue());

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

        PlatformConfiguration platformConfiguration 
        	= this.mappingConfigurationService.getPlatformConfigurationByName("mappingExportSchemaLocation");

        logger.info("Resolved PlatformConfiguration " + platformConfiguration);
        
        String exportXml = this.mappingConfigurationExportHelper.getMappingConfigurationExportXml(this.mappingConfiguration
            , this.keyLocationQueries, platformConfiguration.getValue());

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
        this.numberOfParametersTextField.setReadOnly(false);

        BeanItem<MappingConfiguration> mappingConfigurationItem = new BeanItem<MappingConfiguration>(this.mappingConfiguration);
        
        logger.info("Attempting to populate form with mapping configuration: " + this.mappingConfiguration);

        this.clientComboBox.setValue(this.mappingConfiguration.getConfigurationServiceClient());
        this.typeComboBox.setValue(mappingConfiguration.getConfigurationType());
        this.sourceContextComboBox.setValue(mappingConfiguration.getSourceContext());
        this.targetContextComboBox.setValue(mappingConfiguration.getTargetContext());
        this.descriptionTextArea.setPropertyDataSource(mappingConfigurationItem.getItemProperty("description"));
        this.numberOfParametersTextField.setPropertyDataSource(mappingConfigurationItem.getItemProperty("numberOfParams"));

        this.keyLocationQueries = this.mappingConfigurationService
                .getKeyLocationQueriesByMappingConfigurationId(this.mappingConfiguration.getId());

        this.parameterQueryTextFields = new ArrayList<TextField>();

        paramQueriesLayout.removeAllComponents();

        for(KeyLocationQuery query: this.keyLocationQueries)
        {
            BeanItem<KeyLocationQuery> keyLocationQueryItem 
                = new BeanItem<KeyLocationQuery>(query);
            TextField tf = new TextField();
            tf.addValidator(new StringLengthValidator(
                "The key location query cannot be blank!",
                1, 256, true));
            tf.setValidationVisible(false);
            
            tf.setWidth(350, Unit.PIXELS);
         
            tf.setPropertyDataSource(keyLocationQueryItem.getItemProperty("value"));
            this.parameterQueryTextFields.add(tf);
            tf.setReadOnly(true);
            paramQueriesLayout.addComponent(tf);
        }

        typeComboBox.setReadOnly(true);
        clientComboBox.setReadOnly(true);
        sourceContextComboBox.setReadOnly(true);
        targetContextComboBox.setReadOnly(true);
        this.descriptionTextArea.setReadOnly(true);
        this.numberOfParametersTextField.setReadOnly(true);
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
    }

    /**
     * @param mappingConfiguration the mappingConfiguration to set
     */
    public void setMappingConfiguration(MappingConfiguration mappingConfiguration)
    {
        this.mappingConfiguration = mappingConfiguration;
    }
}
