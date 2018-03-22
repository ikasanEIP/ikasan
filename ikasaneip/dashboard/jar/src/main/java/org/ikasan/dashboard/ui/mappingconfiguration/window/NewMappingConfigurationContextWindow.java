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
package org.ikasan.dashboard.ui.mappingconfiguration.window;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ikasan.dashboard.ui.framework.group.RefreshGroup;
import org.ikasan.dashboard.ui.framework.util.SaveRequiredMonitor;
import org.ikasan.dashboard.ui.mappingconfiguration.data.NewContextFieldGroup;
import org.ikasan.mapping.service.MappingManagementService;
import org.ikasan.systemevent.service.SystemEventService;

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
import com.vaadin.ui.Alignment;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

/**
 * @author Ikasan Development Team
 *
 */
public class NewMappingConfigurationContextWindow extends Window implements View
{
	private Logger logger = LoggerFactory.getLogger(NewMappingConfigurationContextWindow.class);
	
	private static final long serialVersionUID = 715001890931201998L;

    private MappingManagementService mappingConfigurationService;
    private RefreshGroup refreshGroup;
    private SaveRequiredMonitor saveRequiredMonitor;
    private final TextField nameField = new TextField();
    private final TextArea descriptionField = new TextArea();
    private SystemEventService systemEventService;

    /**
     * Constructor
     * 
     * @param mappingConfigurationService
     * @param refreshGroup
     * @param saveRequiredMonitor
     */
    public NewMappingConfigurationContextWindow(MappingManagementService mappingConfigurationService,
            RefreshGroup refreshGroup, SaveRequiredMonitor saveRequiredMonitor, SystemEventService systemEventService)
    {
        super();
        this.refreshGroup = refreshGroup;
        this.saveRequiredMonitor = saveRequiredMonitor;
        this.systemEventService = systemEventService;
        
        this.mappingConfigurationService = mappingConfigurationService;
        init();
    }

    /**
     * Helper method to initialise this object.
     */
    protected void init()
    {
    	this.setStyleName("dashboard");
    	this.setModal(true);
    	this.setWidth(500, Unit.PIXELS);
    	this.setHeight(250, Unit.PIXELS);
    	
        PropertysetItem item = new PropertysetItem();
        item.addItemProperty(NewContextFieldGroup.NAME, new ObjectProperty<String>(""));
        item.addItemProperty(NewContextFieldGroup.DESCRIPTION,
            new ObjectProperty<String>(""));
        
        GridLayout form = new GridLayout(2, 4);
        form.setColumnExpandRatio(0, .15f);
        form.setColumnExpandRatio(1, .85f);
        form.setWidth(100, Unit.PERCENTAGE);
        form.setMargin(true);
        form.setSpacing(true);
        
        Label newTypeLabel =new Label("New Mapping Configuration Context");
        newTypeLabel.setStyleName(ValoTheme.LABEL_HUGE);
		form.addComponent(newTypeLabel, 0, 0, 1, 0);
		
		Label nameLabel = new Label("Name:");
		nameLabel.setSizeUndefined();
		form.addComponent(nameLabel, 0, 1);
		form.setComponentAlignment(nameLabel, Alignment.MIDDLE_RIGHT);

        nameField.setStyleName("ikasan");
        nameField.addValidator(new StringLengthValidator(
            "The name must not be blank!",
            1, 256, true));
        nameField.setValidationVisible(false);
        nameField.setWidth(70, Unit.PERCENTAGE);
        form.addComponent(nameField, 1, 1);

        Label descriptionLabel = new Label("Description:");
        descriptionLabel.setSizeUndefined();
		form.addComponent(descriptionLabel, 0, 2);
		form.setComponentAlignment(descriptionLabel, Alignment.TOP_RIGHT);
		
        descriptionField.setStyleName("ikasan");
        descriptionField.addValidator(new StringLengthValidator(
            "The description must not be blank!",
            1, 256, true));
        descriptionField.setValidationVisible(false);
        descriptionField.setWidth(90, Unit.PERCENTAGE);
        descriptionField.setRows(5);
        
        form.addComponent(descriptionField, 1, 2);

        final NewContextFieldGroup binder = new NewContextFieldGroup(item, this.refreshGroup
            , this.mappingConfigurationService, this.systemEventService);
        binder.bind(nameField, NewContextFieldGroup.NAME);
        binder.bind(descriptionField, NewContextFieldGroup.DESCRIPTION);

        HorizontalLayout buttons = new HorizontalLayout();
        buttons.setSpacing(true);
        
        Button saveButton = new Button("Save");
        saveButton.setStyleName(ValoTheme.BUTTON_SMALL);
        saveButton.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) 
            {
                try 
                {
                    nameField.validate();
                    descriptionField.validate();
                } catch (InvalidValueException e) {
                    nameField.setValidationVisible(true);
                    descriptionField.setValidationVisible(true);
                    return;
                }

                try 
                {
                    binder.commit();
                    UI.getCurrent().getNavigator().navigateTo("emptyPanel");
                    nameField.setValue("");
                    descriptionField.setValue("");
                    Notification.show("New Mapping Configuration Context Successfully Created!");
                    saveRequiredMonitor.setSaveRequired(false);
                    
                    close();
                } catch (CommitException e) 
                {
                	logger.error("An error occurred trying to save mapping configuration context!", e); 
                	
                    Notification.show("An error occurred saving a new context! " + e.getMessage(), Type.ERROR_MESSAGE);
                }
            }
        });
        buttons.addComponent(saveButton);

        Button cancelButton = new Button("Cancel");
        cancelButton.setStyleName(ValoTheme.BUTTON_SMALL);
        cancelButton.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                UI.getCurrent().getNavigator().navigateTo("emptyPanel");
                binder.discard();
                saveRequiredMonitor.setSaveRequired(false);
                
                close();
            }
        });
        buttons.addComponent(cancelButton);

        form.addComponent(buttons, 0, 3, 1, 3);
        form.setComponentAlignment(buttons, Alignment.MIDDLE_CENTER);
        
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
        this.descriptionField.setValidationVisible(false);
    }
}
