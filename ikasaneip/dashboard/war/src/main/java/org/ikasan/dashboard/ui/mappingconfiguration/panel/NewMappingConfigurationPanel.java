/*
 * $Id: NewMappingConfigurationPanel.java 40648 2014-11-07 11:12:53Z stewmi $
 * $URL: https://svc-vcs-prd.uk.mizuho-sc.com:18080/svn/architecture/cmi2/trunk/projects/mappingConfigurationUI/war/src/main/java/org/ikasan/mapping/configuration/ui/panel/NewMappingConfigurationPanel.java $
 *
 * ====================================================================
 *
 * Copyright (c) 2000-2011 by Mizuho International plc.
 * All Rights Reserved.
 *
 * ====================================================================
 *
 */
package org.ikasan.dashboard.ui.mappingconfiguration.panel;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.ikasan.dashboard.ui.framework.group.Editable;
import org.ikasan.dashboard.ui.framework.group.FunctionalGroup;
import org.ikasan.dashboard.ui.framework.util.SaveRequiredMonitor;
import org.ikasan.dashboard.ui.mappingconfiguration.component.ClientComboBox;
import org.ikasan.dashboard.ui.mappingconfiguration.component.MappingConfigurationConfigurationValuesTable;
import org.ikasan.dashboard.ui.mappingconfiguration.component.SourceContextComboBox;
import org.ikasan.dashboard.ui.mappingconfiguration.component.TargetContextComboBox;
import org.ikasan.dashboard.ui.mappingconfiguration.component.TypeComboBox;
import org.ikasan.dashboard.ui.mappingconfiguration.util.MappingConfigurationExportHelper;
import org.ikasan.dashboard.ui.mappingconfiguration.util.MappingConfigurationValuesExportHelper;
import org.ikasan.mapping.model.KeyLocationQuery;
import org.ikasan.mapping.model.MappingConfiguration;
import org.ikasan.mapping.service.MappingConfigurationService;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.themes.BaseTheme;

/**
 * @author CMI2 Development Team
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
            TargetContextComboBox targetContextComboBox, MappingConfigurationService mappingConfigurationService,
            SaveRequiredMonitor saveRequiredMonitor, Button editButton, Button saveButton, Button addNewRecordButton, 
            Button deleteAllRecordsButton, Button importMappingConfigurationButton, Button exportMappingConfigurationValuesButton,
            Button exportMappingConfigurationButton, Button cancelButton, FunctionalGroup newMappingConfigurationFunctionalGroup,
            MappingConfigurationExportHelper mappingConfigurationExportHelper, MappingConfigurationValuesExportHelper 
            mappingConfigurationValuesExportHelper)
    {
        super(mappingConfigurationConfigurationValuesTable, clientComboBox, typeComboBox, sourceContextComboBox,
            targetContextComboBox, "New Mapping Configuration", mappingConfigurationService, saveRequiredMonitor, editButton,
            saveButton, addNewRecordButton, deleteAllRecordsButton, importMappingConfigurationButton, exportMappingConfigurationValuesButton,
            exportMappingConfigurationButton, cancelButton, newMappingConfigurationFunctionalGroup, mappingConfigurationExportHelper,
            mappingConfigurationValuesExportHelper);

//        this.init();
        this.registerListeners();
    }

    /**
     * Helper method to initialise this object.
     */
    @SuppressWarnings("serial")
    protected void init()
    {
    	this.setStyleName("dashboard");
    	
    	this.parameterQueryTextFields = new ArrayList<TextField>();
    	
    	this.typeComboBox.setReadOnly(false);
        this.clientComboBox.setReadOnly(false);
        this.sourceContextComboBox.setReadOnly(false);
        this.targetContextComboBox.setReadOnly(false);
    	super.clientComboBox.unselect(super.clientComboBox.getValue());
    	super.sourceContextComboBox.unselect(super.sourceContextComboBox.getValue());
    	super.targetContextComboBox.unselect(super.targetContextComboBox.getValue());
    	super.typeComboBox.unselect(super.typeComboBox.getValue());

    	
        super.layout = new GridLayout(4, 5);
        logger.info("Setting editButtonPressed!");
        super.mappingConfigurationFunctionalGroup.editButtonPressed();

        super.mappingConfiguration = new MappingConfiguration();
        this.mappingConfigurationConfigurationValuesTable.populateTable(mappingConfiguration);

        HorizontalLayout toolBarLayout = new HorizontalLayout();
        toolBarLayout.setWidth("100%");

        Label spacerLabel = new Label("");
        toolBarLayout.addComponent(spacerLabel);
        toolBarLayout.setExpandRatio(spacerLabel, 0.865f);

        editButton.setStyleName(BaseTheme.BUTTON_LINK);

        toolBarLayout.addComponent(editButton);
        toolBarLayout.setExpandRatio(editButton, 0.045f);

        saveButton.setStyleName(BaseTheme.BUTTON_LINK);

        toolBarLayout.addComponent(saveButton);
        toolBarLayout.setExpandRatio(saveButton, 0.045f);

        this.cancelButton.setStyleName(BaseTheme.BUTTON_LINK);

        toolBarLayout.addComponent(this.cancelButton);
        toolBarLayout.setExpandRatio(this.cancelButton, 0.045f);

        final VerticalLayout contentLayout = new VerticalLayout();
        
        contentLayout.addComponent(toolBarLayout);
        contentLayout.addComponent(createMappingConfigurationForm());
        

        VerticalSplitPanel vpanel = new VerticalSplitPanel(contentLayout
            , createTableLayout(false));

        Button addParametersButton = new Button("Create");
        addParametersButton.setStyleName(BaseTheme.BUTTON_LINK);

        addParametersButton.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                addParamQueryFields();
            }
        });

        logger.info("Trying to add create button! " + addParametersButton);
        paramQueriesLayout.removeAllComponents();
        paramQueriesLayout.addComponent(addParametersButton);

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
     * Register listeners with components associated with this panel.
     */
    protected void registerListeners()
    {
        super.editButton.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                setEditable(true);
                mappingConfigurationFunctionalGroup.editButtonPressed();
            }
        });

        super.saveButton.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
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
                catch (Exception e) {
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    e.printStackTrace(pw);

                    Notification.show("Caught exception trying to save a Mapping Configuration!", sw.toString()
                        , Notification.Type.ERROR_MESSAGE);
                }
            }
        });

        super.cancelButton.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                setEditable(false);
                mappingConfigurationFunctionalGroup.saveOrCancelButtonPressed();
                UI.getCurrent().getNavigator().navigateTo("emptyPanel");
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
        if(this.numberOfParametersTextField != null)
        	this.numberOfParametersTextField.setReadOnly(!editable);

        if(this.parameterQueryTextFields != null)
        {
	        for(TextField textField: this.parameterQueryTextFields)
	        {
	            textField.setReadOnly(!editable);
	        }
        }
    }

    /**
     * Adds key location query fileds to the UI. 
     */
    public void addParamQueryFields()
    {
        paramQueriesLayout.removeAllComponents();

        Button addParametersButton = new Button("Create");
        addParametersButton.setStyleName(BaseTheme.BUTTON_LINK);

        addParametersButton.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                addParamQueryFields();
            }
        });

        paramQueriesLayout.addComponent(addParametersButton);

        Long numberOfParams = (Long)super.numberOfParametersTextField.getPropertyDataSource().getValue();
        for(int i=0; i<numberOfParams; i++)
        {
            KeyLocationQuery query = new KeyLocationQuery();
            query.setMappingConfigurationId(super.mappingConfiguration.getId());
            query.setValue("");
            super.keyLocationQueries.add(query);
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
            paramQueriesLayout.addComponent(tf);
        }
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

        this.clientComboBox.setValue(this.mappingConfiguration.getConfigurationServiceClient());
        this.typeComboBox.setValue(mappingConfiguration.getConfigurationType());
        this.sourceContextComboBox.setValue(mappingConfiguration.getSourceContext());
        this.targetContextComboBox.setValue(mappingConfiguration.getTargetContext());
        this.descriptionTextArea.setPropertyDataSource(mappingConfigurationItem.getItemProperty("description"));
        this.numberOfParametersTextField.setPropertyDataSource(mappingConfigurationItem.getItemProperty("numberOfParams"));

        this.keyLocationQueries = this.mappingConfigurationService
                .getKeyLocationQueriesByMappingConfigurationId(this.mappingConfiguration.getId());

        this.parameterQueryTextFields = new ArrayList<TextField>();

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
     * @see com.mapping.configuration.ui.panel.MappingConfigurationPanel#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
     */
    @Override
    public void enter(ViewChangeEvent event)
    {
        this.saveRequiredMonitor.setSaveRequired(true);
        populateMappingConfigurationForm();
    }

}
