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

import org.ikasan.dashboard.ui.mappingconfiguration.component.ClientComboBox;
import org.ikasan.dashboard.ui.mappingconfiguration.component.SourceContextComboBox;
import org.ikasan.dashboard.ui.mappingconfiguration.component.TargetContextComboBox;
import org.ikasan.dashboard.ui.mappingconfiguration.component.TypeComboBox;
import org.ikasan.mapping.model.ConfigurationContext;
import org.ikasan.mapping.model.ConfigurationServiceClient;
import org.ikasan.mapping.model.ConfigurationType;
import org.ikasan.mapping.service.MappingConfigurationService;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.themes.ValoTheme;

/**
 * @author Ikasan Development Team
 *
 */
@SuppressWarnings("serial")
public class MappingConfigurationSearchPanel extends Panel implements View
{
    private ClientComboBox clientComboBox;
    private TypeComboBox typeComboBox;
    private SourceContextComboBox sourceContextComboBox;
    private TargetContextComboBox targetContextComboBox;
    private MappingConfigurationService mappingConfigurationService;
    private ClickListener searchButtonClickListener;
    private NewActions newActions;

    /**
     * Constructor
     * 
     * @param mappingConfigurationService
     * @param clientComboBox
     * @param typeComboBox
     * @param sourceContextComboBox
     * @param targetContextComboBox
     * @param searchButtonClickListener
     */
    public MappingConfigurationSearchPanel(MappingConfigurationService mappingConfigurationService,
            ClientComboBox clientComboBox, TypeComboBox typeComboBox, SourceContextComboBox sourceContextComboBox,
            TargetContextComboBox targetContextComboBox, ClickListener searchButtonClickListener, NewActions newActions)
    {
        this.mappingConfigurationService = mappingConfigurationService;
        this.clientComboBox = clientComboBox;
        this.typeComboBox = typeComboBox;
        this.sourceContextComboBox = sourceContextComboBox;
        this.targetContextComboBox = targetContextComboBox;
        this.searchButtonClickListener = searchButtonClickListener;
        this.newActions = newActions;

        this.init();
    }

    /**
     * Helper method to initialise this object.
     */
    @SuppressWarnings("serial")
    protected void init()
    {
    	this.addStyleName(ValoTheme.PANEL_BORDERLESS);
    	
    	final Label typeLabel = new Label("Type:");
    	final Label sourceContextLabel = new Label("Source Context:");
    	final Label targetContextLabel = new Label("Target Context:");    	

        final GridLayout contentLayout = new GridLayout(4, 6);   
        contentLayout.setColumnExpandRatio(0, .25f);
        contentLayout.setColumnExpandRatio(1, .15f);
        contentLayout.setColumnExpandRatio(2, .25f);
        contentLayout.setColumnExpandRatio(3, .35f);
        contentLayout.setWidth("100%");
        contentLayout.setSpacing(true);

        
        Label errorOccurrenceDetailsLabel = new Label("Mapping Configuration Search");
		errorOccurrenceDetailsLabel.setStyleName(ValoTheme.LABEL_HUGE);
		contentLayout.addComponent(errorOccurrenceDetailsLabel, 0, 0, 1, 0);
        
        Label clientLabel = new Label("Client:");
        clientLabel.setSizeUndefined();        
        contentLayout.addComponent(clientLabel, 0, 1);
        contentLayout.setComponentAlignment(clientLabel, Alignment.MIDDLE_RIGHT);
        
        HorizontalLayout clientComboBoxLayout = new HorizontalLayout();
        clientComboBoxLayout.setHeight(25, Unit.PIXELS);
        clientComboBoxLayout.setWidth(180, Unit.PIXELS);
        this.clientComboBox.setWidth(180, Unit.PIXELS);
        clientComboBoxLayout.addComponent(this.clientComboBox);
        this.clientComboBox.addValueChangeListener(new ValueChangeListener() {
            public void valueChange(ValueChangeEvent event) {
                if(event.getProperty() != null && event.getProperty().getValue() != null)
                {
                    typeComboBox.refresh(((ConfigurationServiceClient)event.getProperty().getValue()).getName());
                    sourceContextComboBox.refresh(((ConfigurationServiceClient)event.getProperty().getValue()).getName(), null);
                    targetContextComboBox.refresh(((ConfigurationServiceClient)event.getProperty().getValue()).getName(), null, null);
                    
                    typeLabel.setVisible(true);
                    typeComboBox.setVisible(true);
                }
            }
        });
        contentLayout.addComponent(clientComboBoxLayout, 1, 1);
        
        typeLabel.setSizeUndefined();        
        contentLayout.addComponent(typeLabel, 0, 2);
        contentLayout.setComponentAlignment(typeLabel, Alignment.MIDDLE_RIGHT);
        typeLabel.setVisible(false);
        
        HorizontalLayout typeComboBoxLayout = new HorizontalLayout();
        typeComboBoxLayout.setHeight(25, Unit.PIXELS);
        typeComboBoxLayout.setWidth(180, Unit.PIXELS);
        this.typeComboBox.setWidth(180, Unit.PIXELS);
        this.typeComboBox.setVisible(false);
        this.typeComboBox.addValueChangeListener(new ValueChangeListener() {
            public void valueChange(ValueChangeEvent event) {
                String client = null;

                if(clientComboBox.getValue() != null)
                {
                    client = ((ConfigurationServiceClient)clientComboBox.getValue()).getName();
                }

                if(event.getProperty() != null && event.getProperty().getValue() != null)
                {
                    sourceContextComboBox.refresh(client , ((ConfigurationType)event.getProperty().getValue()).getName());
                    targetContextComboBox.refresh(client , ((ConfigurationType)event.getProperty().getValue()).getName(), null);
                    
                    sourceContextLabel.setVisible(true);
                    sourceContextComboBox.setVisible(true);
                }
            }
        });
        typeComboBoxLayout.addComponent(this.typeComboBox);
        contentLayout.addComponent(typeComboBoxLayout, 1, 2);
        
        sourceContextLabel.setSizeUndefined();        
        contentLayout.addComponent(sourceContextLabel, 0, 3);
        contentLayout.setComponentAlignment(sourceContextLabel, Alignment.MIDDLE_RIGHT);
        sourceContextLabel.setVisible(false);
        
        HorizontalLayout sourceContextComboBoxLayout = new HorizontalLayout();
        sourceContextComboBoxLayout.setHeight(25, Unit.PIXELS);
        sourceContextComboBoxLayout.setWidth(180, Unit.PIXELS);
        this.sourceContextComboBox.setWidth(180, Unit.PIXELS);
        this.sourceContextComboBox.setVisible(false);
        this.sourceContextComboBox.addValueChangeListener(new ValueChangeListener() 
        {
            public void valueChange(ValueChangeEvent event) 
            {
                String type = null;
                String client = null;

                if(typeComboBox.getValue() != null)
                {
                    type = ((ConfigurationType)typeComboBox.getValue()).getName();
                }

                if(clientComboBox.getValue() != null)
                {
                    client = ((ConfigurationServiceClient)clientComboBox.getValue()).getName();
                }

                if(event.getProperty() != null && event.getProperty().getValue() != null)
                {
                    targetContextComboBox.refresh(client, type, ((ConfigurationContext)event.getProperty().getValue()).getName());
                    
                    targetContextLabel.setVisible(true);
                    targetContextComboBox.setVisible(true);
                }
            }
        });
        sourceContextComboBoxLayout.addComponent(this.sourceContextComboBox);
        contentLayout.addComponent(sourceContextComboBoxLayout, 1, 3);
        
        targetContextLabel.setSizeUndefined();        
        contentLayout.addComponent(targetContextLabel, 0, 4);
        contentLayout.setComponentAlignment(targetContextLabel, Alignment.MIDDLE_RIGHT);
        targetContextLabel.setVisible(false);
        
        HorizontalLayout targetContextComboBoxLayout = new HorizontalLayout();
        targetContextComboBoxLayout.setHeight(25, Unit.PIXELS);
        targetContextComboBoxLayout.setWidth(180, Unit.PIXELS);
        this.targetContextComboBox.setWidth(180, Unit.PIXELS);
        this.targetContextComboBox.setVisible(false);
        targetContextComboBoxLayout.addComponent(this.targetContextComboBox);
        contentLayout.addComponent(this.targetContextComboBox, 1, 4);
        
        Label actionsLabel = new Label("Actions");
        actionsLabel.setStyleName(ValoTheme.LABEL_HUGE);
		contentLayout.addComponent(actionsLabel, 2, 0, 3, 0);
        
        Label createNewClientLabel = new Label("New Client:");
        createNewClientLabel.setSizeUndefined();
        contentLayout.addComponent(createNewClientLabel, 2, 1);
        contentLayout.setComponentAlignment(createNewClientLabel, Alignment.MIDDLE_RIGHT);
        contentLayout.addComponent(newActions.getNewClientButton(), 3, 1);
        contentLayout.setComponentAlignment(newActions.getNewClientButton(), Alignment.MIDDLE_LEFT);
        
        Label createNewTypeLabel = new Label("New Type:");
        createNewTypeLabel.setSizeUndefined();
        contentLayout.addComponent(createNewTypeLabel, 2, 2);
        contentLayout.setComponentAlignment(createNewTypeLabel, Alignment.MIDDLE_RIGHT);
        contentLayout.addComponent(newActions.getNewTypeButton(), 3, 2);
        contentLayout.setComponentAlignment(newActions.getNewTypeButton(), Alignment.MIDDLE_LEFT);
        
        Label createContextTypeLabel = new Label("New Context:");
        createContextTypeLabel.setSizeUndefined();
        contentLayout.addComponent(createContextTypeLabel, 2, 3);
        contentLayout.setComponentAlignment(createContextTypeLabel, Alignment.MIDDLE_RIGHT);
        contentLayout.addComponent(newActions.getNewContextButton(), 3, 3);
        contentLayout.setComponentAlignment(newActions.getNewContextButton(), Alignment.MIDDLE_LEFT);

        Button button = new Button("Search");
        button.setStyleName(ValoTheme.BUTTON_SMALL);
        button.addClickListener(searchButtonClickListener);

        contentLayout.addComponent(button, 1, 5);
        
        this.clientComboBox.loadClientSelectValues();
//        this.sourceContextComboBox.loadContextValues();
//        this.targetContextComboBox.loadContextValues();
//        this.typeComboBox.loadClientTypeValues();
        
        this.setContent(contentLayout);
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
    
    public void clear()
    {
    	this.clientComboBox.setValue(null);
    	this.sourceContextComboBox.setValue(null);
    	this.targetContextComboBox.setValue(null);
    	this.typeComboBox.setValue(null);
        this.clientComboBox.loadClientSelectValues();
        this.sourceContextComboBox.loadContextValues();
        this.targetContextComboBox.loadContextValues();
        this.typeComboBox.loadClientTypeValues();
    }
}
