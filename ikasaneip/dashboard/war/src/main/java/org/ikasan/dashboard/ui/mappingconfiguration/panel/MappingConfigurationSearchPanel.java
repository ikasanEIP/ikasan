/*
 * $Id: MappingConfigurationSearchPanel.java 40648 2014-11-07 11:12:53Z stewmi $
 * $URL: https://svc-vcs-prd.uk.mizuho-sc.com:18080/svn/architecture/cmi2/trunk/projects/mappingConfigurationUI/war/src/main/java/org/ikasan/mapping/configuration/ui/panel/MappingConfigurationSearchPanel.java $
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

import org.ikasan.dashboard.ui.mappingconfiguration.component.ClientComboBox;
import org.ikasan.dashboard.ui.mappingconfiguration.component.SourceContextComboBox;
import org.ikasan.dashboard.ui.mappingconfiguration.component.TargetContextComboBox;
import org.ikasan.dashboard.ui.mappingconfiguration.component.TypeComboBox;
import org.ikasan.dashboard.ui.mappingconfiguration.listener.SearchButtonClickListener;
import org.ikasan.mapping.model.ConfigurationContext;
import org.ikasan.mapping.model.ConfigurationServiceClient;
import org.ikasan.mapping.model.ConfigurationType;
import org.ikasan.mapping.service.MappingConfigurationService;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.themes.Reindeer;

/**
 * @author CMI2 Development Team
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
    private SearchButtonClickListener searchButtonClickListener;

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
            TargetContextComboBox targetContextComboBox, SearchButtonClickListener searchButtonClickListener)
    {
        super("Mapping Configuration Search");
        this.mappingConfigurationService = mappingConfigurationService;
        this.clientComboBox = clientComboBox;
        this.typeComboBox = typeComboBox;
        this.sourceContextComboBox = sourceContextComboBox;
        this.targetContextComboBox = targetContextComboBox;
        this.searchButtonClickListener = searchButtonClickListener;

        this.init();
    }

    /**
     * Helper method to initialise this object.
     */
    @SuppressWarnings("serial")
    protected void init()
    {
        this.setStyleName("grey");

        final GridLayout contentLayout = new GridLayout(2, 5);
        contentLayout.setColumnExpandRatio(0, 1);
        contentLayout.setColumnExpandRatio(1, 1);
        contentLayout.setRowExpandRatio(0, 1);
        contentLayout.setRowExpandRatio(1, 1);
        contentLayout.setRowExpandRatio(2, 1);
        contentLayout.setRowExpandRatio(3, 1);
        contentLayout.setRowExpandRatio(4, 1);
        contentLayout.setRowExpandRatio(5, 1);
        
        contentLayout.setHeight("100%");
        contentLayout.setMargin(true);

        HorizontalLayout clientLabelLayout = new HorizontalLayout();
        clientLabelLayout.setHeight(25, Unit.PIXELS);
        clientLabelLayout.setWidth(100, Unit.PIXELS);
        clientLabelLayout.addComponent(new Label("Client"));
        contentLayout.addComponent(clientLabelLayout, 0, 0);
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
                }
            }
        });
        contentLayout.addComponent(clientComboBoxLayout, 1, 0);

        HorizontalLayout typeLabelLayout = new HorizontalLayout();
        typeLabelLayout.setHeight(25, Unit.PIXELS);
        typeLabelLayout.setWidth(100, Unit.PIXELS);
        typeLabelLayout.addComponent(new Label("Type"));
        contentLayout.addComponent(typeLabelLayout, 0, 1);
        HorizontalLayout typeComboBoxLayout = new HorizontalLayout();
        typeComboBoxLayout.setHeight(25, Unit.PIXELS);
        typeComboBoxLayout.setWidth(180, Unit.PIXELS);
        this.typeComboBox.setWidth(180, Unit.PIXELS);
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
                }
            }
        });
        typeComboBoxLayout.addComponent(this.typeComboBox);
        contentLayout.addComponent(typeComboBoxLayout, 1, 1);

        HorizontalLayout sourceContextLabelLayout = new HorizontalLayout();
        sourceContextLabelLayout.setHeight(25, Unit.PIXELS);
        sourceContextLabelLayout.setWidth(100, Unit.PIXELS);
        sourceContextLabelLayout.addComponent(new Label("Source Context"));
        contentLayout.addComponent(sourceContextLabelLayout, 0, 2);
        HorizontalLayout sourceContextComboBoxLayout = new HorizontalLayout();
        sourceContextComboBoxLayout.setHeight(25, Unit.PIXELS);
        sourceContextComboBoxLayout.setWidth(180, Unit.PIXELS);
        this.sourceContextComboBox.setWidth(180, Unit.PIXELS);
        this.sourceContextComboBox.addValueChangeListener(new ValueChangeListener() {
            public void valueChange(ValueChangeEvent event) {
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
                }
            }
        });
        sourceContextComboBoxLayout.addComponent(this.sourceContextComboBox);
        contentLayout.addComponent(sourceContextComboBoxLayout, 1, 2);

        HorizontalLayout targetContextLabelLayout = new HorizontalLayout();
        targetContextLabelLayout.setHeight(25, Unit.PIXELS);
        targetContextLabelLayout.setWidth(100, Unit.PIXELS);
        targetContextLabelLayout.addComponent(new Label("Target Context"));
        contentLayout.addComponent(targetContextLabelLayout, 0, 3);
        HorizontalLayout targetContextComboBoxLayout = new HorizontalLayout();
        targetContextComboBoxLayout.setHeight(25, Unit.PIXELS);
        targetContextComboBoxLayout.setWidth(180, Unit.PIXELS);
        this.targetContextComboBox.setWidth(180, Unit.PIXELS);
        targetContextComboBoxLayout.addComponent(this.targetContextComboBox);
        contentLayout.addComponent(this.targetContextComboBox, 1, 3);

        Button button = new Button("Search");
        button.setStyleName(Reindeer.BUTTON_SMALL);
        button.addClickListener(searchButtonClickListener);

        contentLayout.addComponent(button, 1, 4);
        
        this.setContent(contentLayout);
    }

    /* (non-Javadoc)
     * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
     */
    @Override
    public void enter(ViewChangeEvent event)
    {
        // TODO Auto-generated method stub
        
    }
}
