/*
 * $Id: NewClientPanel.java 40648 2014-11-07 11:12:53Z stewmi $
 * $URL: https://svc-vcs-prd.uk.mizuho-sc.com:18080/svn/architecture/cmi2/trunk/projects/mappingConfigurationUI/war/src/main/java/org/ikasan/mapping/configuration/ui/panel/NewClientPanel.java $
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

import org.ikasan.dashboard.ui.framework.group.RefreshGroup;
import org.ikasan.dashboard.ui.framework.util.SaveRequiredMonitor;
import org.ikasan.dashboard.ui.mappingconfiguration.data.NewClientFieldGroup;
import org.ikasan.mapping.service.MappingConfigurationService;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.Reindeer;

/**
 * @author CMI2 Development Team
 *
 */
public class NewClientPanel extends Panel implements View
{
    private static final long serialVersionUID = -5772122320534411604L;

    private RefreshGroup refreshGroup;
    private MappingConfigurationService mappingConfigurationService;
    private SaveRequiredMonitor saveRequiredMonitor;
    final TextField nameField = new TextField("Name");

    /**
     * Constructor
     * 
     * @param mappingConfigurationService
     * @param refreshGroup
     * @param saveRequiredMonitor
     */
    public NewClientPanel(MappingConfigurationService mappingConfigurationService, RefreshGroup refreshGroup,
            SaveRequiredMonitor saveRequiredMonitor)
    {
        super("Create new client");
        this.refreshGroup = refreshGroup;
        this.mappingConfigurationService = mappingConfigurationService;
        this.saveRequiredMonitor = saveRequiredMonitor;
        init();
    }

    /**
     * Helper method to initialise this object.
     */
    protected void init()
    {
    	this.setStyleName("dashboard");
    	
        PropertysetItem item = new PropertysetItem();
        item.addItemProperty(NewClientFieldGroup.NAME, new ObjectProperty<String>(""));
        item.addItemProperty(NewClientFieldGroup.KEY_LOCATION_QUERY_PROCESSOR_TYPE,
            new ObjectProperty<String>("com.mizuho.cmi2.mappingConfiguration.keyQueryProcessor.impl.XPathKeyLocationQueryProcessor"));
        
        FormLayout form = new FormLayout();

        nameField.addValidator(new StringLengthValidator(
            "The name must not be blank!",
            1, 256, true));
        nameField.setValidationVisible(false);
        nameField.setStyleName("ikasan");
        form.addComponent(nameField);


        TextField keyLocationQueryProcessorTypeField = new TextField("Key Location Query Processor Type");
        keyLocationQueryProcessorTypeField.setStyleName("ikasan");
        keyLocationQueryProcessorTypeField.setWidth(500, Unit.PIXELS);
        form.addComponent(keyLocationQueryProcessorTypeField);

        final NewClientFieldGroup binder = new NewClientFieldGroup(item, this.refreshGroup, this.mappingConfigurationService);
        binder.bind(nameField, "name");
        binder.bind(keyLocationQueryProcessorTypeField, "keyLocationQueryProcessorType");

        keyLocationQueryProcessorTypeField.setReadOnly(true);

        HorizontalLayout buttons = new HorizontalLayout();
        
        Button saveButton = new Button("Save");
        saveButton.setStyleName(Reindeer.BUTTON_SMALL);
        saveButton.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                try {
                    nameField.validate();
                } catch (InvalidValueException e) {
                    nameField.setValidationVisible(true);
                    return;
                }

                try {
                    binder.commit();
                    UI.getCurrent().getNavigator().navigateTo("emptyPanel");
                    nameField.setValue("");
                    Notification.show("New Mapping Configuration Client Successfully Created!");
                    saveRequiredMonitor.setSaveRequired(false);
                } catch (CommitException e) {
                    Notification.show("You fail!");
                }
            }
        });
        buttons.addComponent(saveButton);

        Button cancelButton = new Button("Cancel");
        cancelButton.setStyleName(Reindeer.BUTTON_SMALL);
        cancelButton.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                UI.getCurrent().getNavigator().navigateTo("emptyPanel");
                binder.discard();
                saveRequiredMonitor.setSaveRequired(false);
            }
        });
        buttons.addComponent(cancelButton);

        form.addComponent(buttons);
        this.setContent(form);
    }

    /* (non-Javadoc)
     * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
     */
    @Override
    public void enter(ViewChangeEvent event)
    {
        this.saveRequiredMonitor.setSaveRequired(true);
        this.nameField.setValidationVisible(false);
    }
}
