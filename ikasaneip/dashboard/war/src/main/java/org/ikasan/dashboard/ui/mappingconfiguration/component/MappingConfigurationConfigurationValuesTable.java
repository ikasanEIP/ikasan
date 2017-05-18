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
package org.ikasan.dashboard.ui.mappingconfiguration.component;

import java.util.*;

import com.vaadin.ui.*;
import org.apache.log4j.Logger;
import org.ikasan.dashboard.ui.framework.constants.SecurityConstants;
import org.ikasan.dashboard.ui.framework.group.VisibilityGroup;
import org.ikasan.dashboard.ui.framework.util.DashboardSessionValueConstants;
import org.ikasan.dashboard.ui.framework.util.PolicyLinkTypeConstants;
import org.ikasan.dashboard.ui.framework.window.IkasanMessageDialog;
import org.ikasan.dashboard.ui.mappingconfiguration.action.DeleteRowAction;
import org.ikasan.dashboard.ui.mappingconfiguration.util.MappingConfigurationConstants;
import org.ikasan.mapping.model.*;
import org.ikasan.mapping.service.MappingManagementService;
import org.ikasan.mapping.service.MappingConfigurationServiceException;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.ikasan.systemevent.service.SystemEventService;
import org.vaadin.teemu.VaadinIcons;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.DefaultItemSorter;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.ValoTheme;

/**
 * @author Ikasan Development Team
 *
 */
public class MappingConfigurationConfigurationValuesTable extends Table
{
    private static final long serialVersionUID = -3565819620819253906L;

    /** Logger instance */
    private static Logger logger = Logger.getLogger(MappingConfigurationConfigurationValuesTable.class);
    
    private MappingManagementService mappingConfigurationService;
    private MappingConfiguration mappingConfiguration;
    private IndexedContainer container;
    private VisibilityGroup visibilityGroup;
    private SystemEventService systemEventService;
    private ArrayList<ManyToManyTargetConfigurationValue> manyToManyTargetConfigurationValues;
    private ArrayList<ManyToManyTargetConfigurationValue> deletedManyToManyTargetConfigurationValues;
    protected List<ParameterName> sourceContextParameterNames;
    protected List<ParameterName> targetContextParameterNames;

    private ArrayList<Component> neverToBeEdited = null;

    /**
     * Constructor
     * 
     * @param mappingConfigurationService
     * @param visibilityGroup
     */
    public MappingConfigurationConfigurationValuesTable(MappingManagementService mappingConfigurationService,
            VisibilityGroup visibilityGroup, SystemEventService systemEventService)
    {
        this.mappingConfigurationService = mappingConfigurationService;
        this.visibilityGroup = visibilityGroup;
        this.systemEventService = systemEventService;
        init();
    }

    /**
     * Helper method to initialise the component.
     */
    private void init()
    {
        this.visibilityGroup.registerRefreshableTable(this);

        this.setSizeFull();

        container = new IndexedContainer() {
            @Override
            public Collection<?> getSortableContainerPropertyIds() {
                // Default implementation allows sorting only if the property
                // type can be cast to Comparable
                return getContainerPropertyIds();
            }
        };

        container.setItemSorter(new DefaultItemSorter(new Comparator<Object>() {

            public int compare(Object o1, Object o2) {

                return manageTableSorting(o1, o2);

            }
        }));

        container.addContainerProperty("Source Configuration Value", VerticalLayout.class,  null);
        container.addContainerProperty("Target Configuration Value", VerticalLayout.class,  null);
        container.addContainerProperty("Delete", Button.class,  null);

        this.setColumnExpandRatio("Source Configuration Value", 47.5f);
        this.setColumnExpandRatio("Target Configuration Value", 47.5f);
        this.setColumnExpandRatio("Delete", 5f);

        this.setCellStyleGenerator(new IkasanSmallCellStyleGenerator());
        this.setContainerDataSource(container);
    }

    /* (non-Javadoc)
     * @see com.vaadin.ui.Table#setEditable(boolean)
     */
    @SuppressWarnings("unchecked")
    @Override
    public void setEditable(boolean editable)
    {
        Collection<Long> itemIds = (Collection<Long>)this.getItemIds();

        Iterator<Long> itemIdsIter = itemIds.iterator();

        while(itemIdsIter.hasNext())
        {
            Item item = this.getItem(itemIdsIter.next());
            Property<VerticalLayout> property = item.getItemProperty("Source Configuration Value");
            VerticalLayout layout = property.getValue();

            for(int i=0; i<layout.getComponentCount(); i++)
            {
                if(layout.getComponent(i) instanceof HorizontalLayout)
                {
                    for(int j=0; j<((HorizontalLayout) layout.getComponent(i)).getComponentCount(); j++)
                    {
                        Component component = ((HorizontalLayout) layout.getComponent(i)).getComponent(j);

                        if (this.neverToBeEdited.contains(component))
                        {
                            continue;
                        }
                        else if(component instanceof TextField)
                        {
                            component.setReadOnly(!editable);
                        }
                        else if(component instanceof Button)
                        {
                            component.setVisible(editable);
                        }
                    }
                }
                else
                {
                    ((TextField) layout.getComponent(i)).setReadOnly(!editable);
                }
            }

            Property<VerticalLayout> targetProperty = item.getItemProperty("Target Configuration Value");
            layout = targetProperty.getValue();

            for(int i=0; i<layout.getComponentCount(); i++)
            {
                if(layout.getComponent(i) instanceof HorizontalLayout)
                {
                    for(int j=0; j<((HorizontalLayout) layout.getComponent(i)).getComponentCount(); j++)
                    {
                        Component component = ((HorizontalLayout) layout.getComponent(i)).getComponent(j);

                        if (this.neverToBeEdited.contains(component))
                        {
                            continue;
                        }
                        else if(component instanceof TextField)
                        {
                            component.setReadOnly(!editable);
                        }
                        else if(component instanceof Button)
                        {
                            component.setVisible(editable);
                        }
                    }
                }
                else
                {
                    ((TextField) layout.getComponent(i)).setReadOnly(!editable);
                }
            }
        }

        super.setEditable(editable);
    }

    /**
     * Method to save values associated with the component.
     * 
     * @throws Exception
     */
    public void save() throws Exception
    {
        if(!mappingConfiguration.getIsManyToMany())
        {
            for (SourceConfigurationValue value : this.mappingConfiguration.getSourceConfigurationValues())
            {
                Long numberOfSourceConfigurationValues = this.mappingConfigurationService
                        .getNumberOfSourceConfigurationValuesReferencingTargetConfigurationValue(value.getTargetConfigurationValue());

                if (numberOfSourceConfigurationValues == 0)
                {
                    this.mappingConfigurationService.deleteTargetConfigurationValue(value.getTargetConfigurationValue());
                } else
                {
                    IkasanAuthentication authentication = (IkasanAuthentication) VaadinService.getCurrentRequest().getWrappedSession()
                            .getAttribute(DashboardSessionValueConstants.USER);

                    logger.debug("User: " + authentication.getName() + " saving Target Configuration Value: " +
                            value);
                    this.mappingConfigurationService.saveTargetConfigurationValue(value.getTargetConfigurationValue());

                    systemEventService.logSystemEvent(MappingConfigurationConstants.MAPPING_CONFIGURATION_SERVICE,
                            "Saving Target Configuration Value: " + value, authentication.getName());
                }
            }
        }
        else
        {
            for(ManyToManyTargetConfigurationValue value: this.deletedManyToManyTargetConfigurationValues)
            {
                this.manyToManyTargetConfigurationValues.remove(value);

                logger.info("Trying to delete: " + value);

                this.mappingConfigurationService.deleteManyToManyTargetConfigurationValue(value);
            }

            for(ManyToManyTargetConfigurationValue value: this.manyToManyTargetConfigurationValues)
            {
                this.mappingConfigurationService.storeManyToManyTargetConfigurationValue(value);
            }

            deletedManyToManyTargetConfigurationValues = new ArrayList<ManyToManyTargetConfigurationValue>();
        }
    }

    /**
     * Method to add a record to the component.
     * 
     * @throws MappingConfigurationServiceException
     */
    public void addNewRecord() throws MappingConfigurationServiceException
    {
        Long sourceSystemGroupId = null;
        
        if(this.mappingConfiguration.getNumberOfParams() > 1 || mappingConfiguration.getIsManyToMany())
        {
            sourceSystemGroupId = this.mappingConfigurationService.getNextSequenceNumber();
        }
        TargetConfigurationValue targetConfigurationValue = new TargetConfigurationValue();
        targetConfigurationValue.setTargetSystemValue("Add targetSystemValue");

        this.mappingConfigurationService.saveTargetConfigurationValue(targetConfigurationValue);

        final VerticalLayout sourceValueTableCellLayout = new VerticalLayout();
        sourceValueTableCellLayout.setSpacing(true);
        sourceValueTableCellLayout.setMargin(true);
        sourceValueTableCellLayout.setWidth("100%");

        if(this.showSourceParamNames())
        {
            HorizontalLayout hl = new HorizontalLayout();
            Label nameLabel = new Label("Name");
            nameLabel.setWidth(300, Unit.PIXELS);
            hl.addComponent(nameLabel);

            Label valueLabel = new Label("Value");
            valueLabel.setWidth(300, Unit.PIXELS);
            hl.addComponent(valueLabel);

            sourceValueTableCellLayout.addComponent(hl);
        }

        SourceConfigurationValue sourceConfigurationValue = null;
        final Button deleteButton = new Button("Delete");

        ArrayList<SourceConfigurationValue> sourceConfigurationValues = new ArrayList<SourceConfigurationValue>();

        final Long fsourceSystemGroupId = sourceSystemGroupId;

        for(int i=0; i<this.mappingConfiguration.getNumberOfParams(); i++)
        {
            sourceConfigurationValue = new SourceConfigurationValue();
            sourceConfigurationValue.setSourceSystemValue("Add source system value");
            sourceConfigurationValue.setSourceConfigGroupId(sourceSystemGroupId);
            sourceConfigurationValue.setTargetConfigurationValue(targetConfigurationValue);
            sourceConfigurationValue.setMappingConfigurationId(this.mappingConfiguration.getId());

            sourceConfigurationValues.add(sourceConfigurationValue);

            this.mappingConfiguration.getSourceConfigurationValues().add(sourceConfigurationValue);
            
            final BeanItem<SourceConfigurationValue> item = new BeanItem<SourceConfigurationValue>(sourceConfigurationValue);
            final TextField nameTextField = new TextField(item.getItemProperty("name"));
            nameTextField.setWidth(300, Unit.PIXELS);
            final TextField tf = new TextField(item.getItemProperty("sourceSystemValue"));

            final HorizontalLayout hl = new HorizontalLayout();
            hl.setSpacing(true);

            if(showSourceParamNames())
            {
                nameTextField.setValue(this.sourceContextParameterNames.get(i).getName());
                nameTextField.setReadOnly(true);
                neverToBeEdited.add(nameTextField);
                hl.addComponent(nameTextField);
            }
            hl.addComponent(tf);

            if(mappingConfiguration.getIsManyToMany() && !this.mappingConfiguration.getConstrainParameterListSizes())
            {
                Button addSourceValueButton = new Button();
                addSourceValueButton.setIcon(VaadinIcons.PLUS);
                addSourceValueButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
                addSourceValueButton.setDescription("Add new source value.");
                addSourceValueButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);

                addSourceValueButton.addClickListener(new Button.ClickListener()
                {
                    @Override
                    public void buttonClick(ClickEvent clickEvent)
                    {
                        logger.info("Attempting to add text field");
                        final SourceConfigurationValue sourceConfigurationValue = new SourceConfigurationValue();
                        sourceConfigurationValue.setSourceSystemValue("Add source system value");
                        sourceConfigurationValue.setSourceConfigGroupId(fsourceSystemGroupId);
                        sourceConfigurationValue.setMappingConfigurationId(mappingConfiguration.getId());

                        mappingConfiguration.getSourceConfigurationValues().add(sourceConfigurationValue);

                        Item item = new BeanItem<SourceConfigurationValue>(sourceConfigurationValue);
                        final TextField nameTextField = new TextField(item.getItemProperty("name"));
                        nameTextField.setWidth(300, Unit.PIXELS);
                        final TextField tf = new TextField(item.getItemProperty("sourceSystemValue"));
                        tf.setReadOnly(false);
                        tf.setWidth(300, Unit.PIXELS);

                        final HorizontalLayout hl = new HorizontalLayout();
                        hl.addComponent(tf);

                        final Button minusTargetValueButton = new Button();
                        minusTargetValueButton.setIcon(VaadinIcons.MINUS);
                        minusTargetValueButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
                        minusTargetValueButton.setDescription("Add new source value.");
                        minusTargetValueButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
                        minusTargetValueButton.setVisible(true);

                        minusTargetValueButton.addClickListener(new Button.ClickListener()
                        {
                            @Override
                            public void buttonClick(ClickEvent clickEvent)
                            {
                                hl.removeComponent(tf);
                                hl.removeComponent(minusTargetValueButton);
                                hl.setImmediate(true);
                                hl.markAsDirty();
                                sourceValueTableCellLayout.removeComponent(hl);
                                sourceValueTableCellLayout.markAsDirty();

                                mappingConfiguration.getSourceConfigurationValues().remove(sourceConfigurationValue);
                            }
                        });

                        hl.addComponent(minusTargetValueButton);
                        sourceValueTableCellLayout.addComponent(hl);
                    }
                });

                hl.addComponent(addSourceValueButton);
            }

            sourceValueTableCellLayout.addComponent(hl);
            tf.setReadOnly(false);
            tf.setWidth(300, Unit.PIXELS);
        }

        final VerticalLayout targetValueTableCellLayout = new VerticalLayout();
        targetValueTableCellLayout.setSpacing(true);
        targetValueTableCellLayout.setMargin(true);
        targetValueTableCellLayout.setWidth("100%");

        if(this.showTargetParamNames())
        {
            HorizontalLayout hl = new HorizontalLayout();
            Label nameLabel = new Label("Name");
            nameLabel.setWidth(300, Unit.PIXELS);
            hl.addComponent(nameLabel);

            Label valueLabel = new Label("Value");
            valueLabel.setWidth(300, Unit.PIXELS);
            hl.addComponent(valueLabel);

            targetValueTableCellLayout.addComponent(hl);
        }

        if(this.mappingConfiguration.getIsManyToMany())
        {
            for(int i=0; i<this.mappingConfiguration.getNumTargetValues(); i++)
            {
                ManyToManyTargetConfigurationValue manyToManyTargetConfigurationValue = new ManyToManyTargetConfigurationValue();
                manyToManyTargetConfigurationValue.setGroupId(fsourceSystemGroupId);
                manyToManyTargetConfigurationValue.setTargetSystemValue("Add target system value");

                manyToManyTargetConfigurationValues.add(manyToManyTargetConfigurationValue);

                Item item = new BeanItem<ManyToManyTargetConfigurationValue>(manyToManyTargetConfigurationValue);
                final TextField nameTextField = new TextField(item.getItemProperty("name"));
                nameTextField.setWidth(300, Unit.PIXELS);
                TextField tf = new TextField(item.getItemProperty("targetSystemValue"));
                tf.setReadOnly(false);
                tf.setWidth(300, Unit.PIXELS);

                final HorizontalLayout hl = new HorizontalLayout();
                hl.setSpacing(true);

                if(showTargetParamNames())
                {
                    nameTextField.setValue(this.targetContextParameterNames.get(i).getName());
                    nameTextField.setReadOnly(true);
                    neverToBeEdited.add(nameTextField);
                    hl.addComponent(nameTextField);
                }
                hl.addComponent(tf);

                if(!this.mappingConfiguration.getConstrainParameterListSizes())
                {
                    Button addTargetValueButton = new Button();
                    addTargetValueButton.setIcon(VaadinIcons.PLUS);
                    addTargetValueButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
                    addTargetValueButton.setDescription("Add new target value.");
                    addTargetValueButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
                    addTargetValueButton.setVisible(false);

                    addTargetValueButton.addClickListener(new Button.ClickListener()
                    {
                        @Override
                        public void buttonClick(ClickEvent clickEvent)
                        {
                            final ManyToManyTargetConfigurationValue targetConfigurationValue = new ManyToManyTargetConfigurationValue();
                            targetConfigurationValue.setGroupId(fsourceSystemGroupId);
                            targetConfigurationValue.setTargetSystemValue("Add source system value");

                            manyToManyTargetConfigurationValues.add(targetConfigurationValue);

                            Item item = new BeanItem<ManyToManyTargetConfigurationValue>(targetConfigurationValue);
                            final TextField nameTextField = new TextField(item.getItemProperty("name"));
                            nameTextField.setWidth(300, Unit.PIXELS);
                            final TextField tf = new TextField(item.getItemProperty("targetSystemValue"));
                            tf.setReadOnly(false);
                            tf.setWidth(300, Unit.PIXELS);

                            final HorizontalLayout hl = new HorizontalLayout();
                            hl.addComponent(tf);

                            final Button minusTargetValueButton = new Button();
                            minusTargetValueButton.setIcon(VaadinIcons.MINUS);
                            minusTargetValueButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
                            minusTargetValueButton.setDescription("Add new source value.");
                            minusTargetValueButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
                            minusTargetValueButton.setVisible(true);

                            minusTargetValueButton.addClickListener(new Button.ClickListener()
                            {
                                @Override
                                public void buttonClick(ClickEvent clickEvent)
                                {
                                    hl.removeComponent(tf);
                                    hl.removeComponent(minusTargetValueButton);
                                    hl.setImmediate(true);
                                    hl.markAsDirty();
                                    targetValueTableCellLayout.removeComponent(hl);
                                    targetValueTableCellLayout.markAsDirty();

                                    deletedManyToManyTargetConfigurationValues.add(targetConfigurationValue);
                                }
                            });
    
                            hl.addComponent(minusTargetValueButton);

                            targetValueTableCellLayout.addComponent(hl);
                        }
                    });

                    hl.addComponent(addTargetValueButton);
                }

                targetValueTableCellLayout.addComponent(hl);
            }
        }
        else
        {
            BeanItem<TargetConfigurationValue> targetConfigurationItem = new BeanItem<TargetConfigurationValue>(targetConfigurationValue);
            final TextField targetConfigurationTextField = new TextField(targetConfigurationItem.getItemProperty("targetSystemValue"));
            targetConfigurationTextField.setReadOnly(true);
            targetConfigurationTextField.setWidth(300, Unit.PIXELS);

            targetValueTableCellLayout.addComponent(targetConfigurationTextField);
        }

        final DeleteRowAction action = new DeleteRowAction(sourceConfigurationValues, this.mappingConfiguration
            , this, this.mappingConfigurationService, this.systemEventService);

        deleteButton.setIcon(VaadinIcons.TRASH);
        deleteButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
        deleteButton.setDescription("Delete this record");
        deleteButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        deleteButton.addClickListener(new Button.ClickListener() 
        {
            public void buttonClick(ClickEvent event) 
            {
                IkasanMessageDialog dialog = new IkasanMessageDialog("Delete record", 
                    "This mapping configuration record will be permanently removed. " +
                    "Are you sure you wish to proceed?.", action);

                UI.getCurrent().addWindow(dialog);
            }
        });
        
        final IkasanAuthentication authentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
 	        	.getAttribute(DashboardSessionValueConstants.USER);
    	 
    	if(authentication != null 
    			&& (authentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY)
    					|| authentication.hasGrantedAuthority(SecurityConstants.EDIT_MAPPING_AUTHORITY))
    					|| authentication.canAccessLinkedItem(PolicyLinkTypeConstants.MAPPING_CONFIGURATION_LINK_TYPE
    							, mappingConfiguration.getId()))
    	{
    		deleteButton.setVisible(true);
    	}
    	else
    	{
    		deleteButton.setVisible(false);
    	}


        Item item = this.container.addItemAt(0, sourceConfigurationValue);
        Property<Layout> sourceProperty = item.getItemProperty("Source Configuration Value");
        sourceProperty.setValue(sourceValueTableCellLayout);
        Property<Layout> targetProperty = item.getItemProperty("Target Configuration Value");
        targetProperty.setValue(targetValueTableCellLayout);
        Property<Button> deleteProperty = item.getItemProperty("Delete");
        deleteProperty.setValue(deleteButton);

        this.mappingConfigurationService.saveMappingConfiguration(mappingConfiguration);

        this.setEditable(true);

        IkasanAuthentication principal = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
                .getAttribute(DashboardSessionValueConstants.USER);

        logger.info("User: " + principal.getName()
            + " added new mapping configuration value for Mapping Configuration " 
                + this.mappingConfiguration.toStringLite());
        
        systemEventService.logSystemEvent(MappingConfigurationConstants.MAPPING_CONFIGURATION_SERVICE, 
        		"Added new mapping configuration value for Mapping Configuration: " + this.mappingConfiguration.toStringLite(), principal.getName());
    }

    /**
     * Method to help populate the table with values associated with the MappingConfiguration.
     * 
     * @param mappingConfiguration
     */
    public void populateTable(final MappingConfiguration mappingConfiguration)
    {
        neverToBeEdited = new ArrayList<Component>();
        this.mappingConfiguration = mappingConfiguration;
        this.loadParameterNames();
        manyToManyTargetConfigurationValues = new ArrayList<ManyToManyTargetConfigurationValue>();
        deletedManyToManyTargetConfigurationValues = new ArrayList<ManyToManyTargetConfigurationValue>();

        Set<SourceConfigurationValue> sourceConfigurationValues 
            = mappingConfiguration.getSourceConfigurationValues();

        super.removeAllItems();

        Iterator<SourceConfigurationValue> sourceConfigurationValueItr = sourceConfigurationValues.iterator();

        ArrayList<SourceConfigurationValue> usedSourceConfigurationValues = new ArrayList<SourceConfigurationValue>();

        ArrayList<SourceConfigurationValue> groupedSourceSystemValues = new ArrayList<SourceConfigurationValue>();

        while(sourceConfigurationValueItr.hasNext())
        {
            final SourceConfigurationValue value = sourceConfigurationValueItr.next();

            final VerticalLayout tableCellLayout = new VerticalLayout();
            tableCellLayout.setWidth("100%");
            tableCellLayout.setSpacing(true);
            tableCellLayout.setMargin(true);

            if(this.showSourceParamNames())
            {
                HorizontalLayout hl = new HorizontalLayout();
                Label nameLabel = new Label("Name");
                nameLabel.setWidth(300, Unit.PIXELS);
                hl.addComponent(nameLabel);

                Label valueLabel = new Label("Value");
                valueLabel.setWidth(300, Unit.PIXELS);
                hl.addComponent(valueLabel);

                tableCellLayout.addComponent(hl);
            }

            for(int i=0; i<this.mappingConfiguration.getNumberOfParams(); i++)
            {
                if(!usedSourceConfigurationValues.contains(value))
                {
                    logger.info("Adding source value, should be adding button");
                    groupedSourceSystemValues.add(value);

                    BeanItem<SourceConfigurationValue> item = new BeanItem<SourceConfigurationValue>(value);
                    final TextField sourceSystemValueTextField = new TextField(item.getItemProperty("sourceSystemValue"));
                    sourceSystemValueTextField.setWidth(300, Unit.PIXELS);

                    sourceSystemValueTextField.setReadOnly(true);
                    usedSourceConfigurationValues.add(value);

                    final TextField nameTextField = new TextField(item.getItemProperty("name"));
                    nameTextField.setWidth(300, Unit.PIXELS);

                    nameTextField.setReadOnly(true);

                    neverToBeEdited.add(nameTextField);

                    if (mappingConfiguration.getIsManyToMany())
                    {
                        final HorizontalLayout hl = new HorizontalLayout();
                        hl.setSpacing(true);
                        if(this.showSourceParamNames())
                        {
                            hl.addComponent(nameTextField);
                        }
                        hl.addComponent(sourceSystemValueTextField);

                        if(!mappingConfiguration.getConstrainParameterListSizes())
                        {
                            Button addSourceValueButton = new Button();
                            addSourceValueButton.setIcon(VaadinIcons.PLUS);
                            addSourceValueButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
                            addSourceValueButton.setDescription("Add new source value.");
                            addSourceValueButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
                            addSourceValueButton.setVisible(false);

                            addSourceValueButton.addClickListener(new Button.ClickListener()
                            {
                                @Override
                                public void buttonClick(ClickEvent clickEvent)
                                {
                                    final SourceConfigurationValue sourceConfigurationValue = new SourceConfigurationValue();
                                    sourceConfigurationValue.setSourceSystemValue("Add source system value");
                                    sourceConfigurationValue.setSourceConfigGroupId(value.getSourceConfigGroupId());
                                    sourceConfigurationValue.setMappingConfigurationId(mappingConfiguration.getId());

                                    mappingConfiguration.getSourceConfigurationValues().add(sourceConfigurationValue);

                                    Item item = new BeanItem<SourceConfigurationValue>(sourceConfigurationValue);
                                    final TextField tf = new TextField(item.getItemProperty("sourceSystemValue"));
                                    tf.setReadOnly(false);
                                    tf.setWidth(300, Unit.PIXELS);

                                    final TextField nameTextField = new TextField(item.getItemProperty("name"));
                                    nameTextField.setWidth(300, Unit.PIXELS);

                                    nameTextField.setReadOnly(true);


                                    final HorizontalLayout hl = new HorizontalLayout();

                                    final Button minusTargetValueButton = new Button();
                                    minusTargetValueButton.setIcon(VaadinIcons.MINUS);
                                    minusTargetValueButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
                                    minusTargetValueButton.setDescription("Add new source value.");
                                    minusTargetValueButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
                                    minusTargetValueButton.setVisible(true);

                                    minusTargetValueButton.addClickListener(new Button.ClickListener()
                                    {
                                        @Override
                                        public void buttonClick(ClickEvent clickEvent)
                                        {
                                            hl.removeComponent(tf);
                                            hl.removeComponent(minusTargetValueButton);
                                            hl.setImmediate(true);
                                            hl.markAsDirty();
                                            tableCellLayout.removeComponent(hl);
                                            tableCellLayout.markAsDirty();

                                            mappingConfiguration.getSourceConfigurationValues().remove(sourceConfigurationValue);
                                        }
                                    });

                                    hl.setSpacing(true);

                                    if(showSourceParamNames())
                                    {
                                        hl.addComponent(nameTextField);
                                    }
                                    hl.addComponent(tf);
                                    hl.addComponent(minusTargetValueButton);

                                    tableCellLayout.addComponent(hl);
                                }
                            });

                            hl.addComponent(addSourceValueButton);
                        }
                        tableCellLayout.addComponent(hl);
                    }
                    else
                    {
                        logger.info("Adding source value, should NOT be adding button");
                        final HorizontalLayout hl = new HorizontalLayout();
                        hl.setSpacing(true);

                        if(this.showSourceParamNames())
                        {
                            hl.addComponent(nameTextField);
                        }
                        hl.addComponent(sourceSystemValueTextField);
                        tableCellLayout.addComponent(hl);
                    }

                    Iterator<SourceConfigurationValue> partnerSourceConfigurationValueItr = sourceConfigurationValues.iterator();

                    while(partnerSourceConfigurationValueItr.hasNext())
                    {
                        final SourceConfigurationValue partnerSourceConfigurationValue = partnerSourceConfigurationValueItr.next();

                        if(partnerSourceConfigurationValue.getSourceConfigGroupId() != null &&
                                !usedSourceConfigurationValues.contains(partnerSourceConfigurationValue) && 
                                partnerSourceConfigurationValue.getId().compareTo(value.getId()) != 0
                                && partnerSourceConfigurationValue.getSourceConfigGroupId().compareTo(value.getSourceConfigGroupId()) == 0)
                        {
                            groupedSourceSystemValues.add(partnerSourceConfigurationValue);
                            item = new BeanItem<SourceConfigurationValue>(partnerSourceConfigurationValue);
                            final TextField sourceSystemValueTextField2 = new TextField(item.getItemProperty("sourceSystemValue"));
                            sourceSystemValueTextField2.setWidth(300, Unit.PIXELS);

                            sourceSystemValueTextField2.setReadOnly(true);

                            final TextField nameTextField2 = new TextField(item.getItemProperty("name"));
                            nameTextField2.setWidth(300, Unit.PIXELS);

                            nameTextField2.setReadOnly(true);

                            neverToBeEdited.add(nameTextField2);


                            usedSourceConfigurationValues.add(partnerSourceConfigurationValue);
                            tableCellLayout.addComponent(sourceSystemValueTextField2);

                            logger.info("Adding source value as partner group");

                            final HorizontalLayout hl = new HorizontalLayout();
                            hl.setSpacing(true);

                            final Button minusTargetValueButton = new Button();
                            minusTargetValueButton.setIcon(VaadinIcons.MINUS);
                            minusTargetValueButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
                            minusTargetValueButton.setDescription("Add new source value.");
                            minusTargetValueButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
                            minusTargetValueButton.setVisible(false);

                            minusTargetValueButton.addClickListener(new Button.ClickListener()
                            {
                                @Override
                                public void buttonClick(ClickEvent clickEvent)
                                {
                                    hl.removeComponent(sourceSystemValueTextField2);
                                    hl.removeComponent(minusTargetValueButton);
                                    hl.setImmediate(true);
                                    hl.markAsDirty();
                                    tableCellLayout.removeComponent(hl);
                                    tableCellLayout.markAsDirty();

                                    mappingConfiguration.getSourceConfigurationValues().remove(partnerSourceConfigurationValue);
                                }
                            });

                            if(this.showSourceParamNames())
                            {
                                hl.addComponent(nameTextField2);
                            }
                            hl.addComponent(sourceSystemValueTextField2);

                            if(mappingConfiguration.getIsManyToMany() && !mappingConfiguration.getConstrainParameterListSizes())
                            {
                                hl.addComponent(minusTargetValueButton);
                            }
                            
                            tableCellLayout.addComponent(hl);
                        }
                    }

                    final VerticalLayout targetValueTableCellLayout = new VerticalLayout();
                    targetValueTableCellLayout.setMargin(true);
                    targetValueTableCellLayout.setWidth("100%");
                    targetValueTableCellLayout.setSizeUndefined();
                    targetValueTableCellLayout.setImmediate(true);
                    targetValueTableCellLayout.setSpacing(true);

                    if(this.mappingConfiguration.getIsManyToMany())
                    {
                        if(this.showTargetParamNames())
                        {
                            HorizontalLayout labelHl = new HorizontalLayout();
                            Label nameLabel = new Label("Name");
                            nameLabel.setWidth(300, Unit.PIXELS);
                            labelHl.addComponent(nameLabel);

                            Label valueLabel = new Label("Value");
                            valueLabel.setWidth(300, Unit.PIXELS);
                            labelHl.addComponent(valueLabel);

                            targetValueTableCellLayout.addComponent(labelHl);
                        }

                        List<ManyToManyTargetConfigurationValue> targetValues
                                = this.mappingConfigurationService.getManyToManyTargetConfigurationValues(value.getSourceConfigGroupId());

                        boolean buttonAdded = false;

                        if(targetValues.size() == 0)
                        {
                            ManyToManyTargetConfigurationValue targetConfigurationValue = new ManyToManyTargetConfigurationValue();
                            targetConfigurationValue.setGroupId(value.getSourceConfigGroupId());
                            targetConfigurationValue.setTargetSystemValue("Add target system value");

                            manyToManyTargetConfigurationValues.add(targetConfigurationValue);

                            Item tItem = new BeanItem<ManyToManyTargetConfigurationValue>(targetConfigurationValue);
                            final TextField tvf = new TextField(tItem.getItemProperty("targetSystemValue"));
                            tvf.setReadOnly(false);
                            tvf.setWidth(300, Unit.PIXELS);

                            final HorizontalLayout hl = new HorizontalLayout();
                            hl.setSpacing(true);
                            hl.addComponent(tvf);

                            if(!mappingConfiguration.getConstrainParameterListSizes())
                            {
                                Button addTargetValueButton = new Button();
                                addTargetValueButton.setIcon(VaadinIcons.PLUS);
                                addTargetValueButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
                                addTargetValueButton.setDescription("Add new target value.");
                                addTargetValueButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
                                addTargetValueButton.setVisible(false);

                                addTargetValueButton.addClickListener(new Button.ClickListener()
                                {
                                    @Override
                                    public void buttonClick(ClickEvent clickEvent)
                                    {
                                        final HorizontalLayout hl = new HorizontalLayout();
                                        hl.setSpacing(true);
                                        hl.setSizeUndefined();

                                        final ManyToManyTargetConfigurationValue targetConfigurationValue = new ManyToManyTargetConfigurationValue();
                                        targetConfigurationValue.setGroupId(value.getSourceConfigGroupId());
                                        targetConfigurationValue.setTargetSystemValue("Add target system value");

                                        manyToManyTargetConfigurationValues.add(targetConfigurationValue);

                                        Item tItem = new BeanItem<ManyToManyTargetConfigurationValue>(targetConfigurationValue);
                                        final TextField tvf = new TextField(tItem.getItemProperty("targetSystemValue"));
                                        tvf.setReadOnly(false);
                                        tvf.setWidth(300, Unit.PIXELS);

                                        final Button minusTargetValueButton = new Button();
                                        minusTargetValueButton.setIcon(VaadinIcons.MINUS);
                                        minusTargetValueButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
                                        minusTargetValueButton.setDescription("Add new target value.");
                                        minusTargetValueButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
                                        minusTargetValueButton.setVisible(true);

                                        minusTargetValueButton.addClickListener(new Button.ClickListener()
                                        {
                                            @Override
                                            public void buttonClick(ClickEvent clickEvent)
                                            {
                                                hl.removeComponent(tvf);
                                                hl.removeComponent(minusTargetValueButton);
                                                hl.setImmediate(true);
                                                hl.markAsDirty();
                                                targetValueTableCellLayout.removeComponent(hl);
                                                targetValueTableCellLayout.markAsDirty();

                                                deletedManyToManyTargetConfigurationValues.add(targetConfigurationValue);
                                            }
                                        });

                                        hl.addComponent(tvf);
                                        hl.addComponent(minusTargetValueButton);
                                        targetValueTableCellLayout.addComponent(hl);
                                    }
                                });

                                hl.addComponent(addTargetValueButton);
                            }
                            targetValueTableCellLayout.addComponent(hl);
                        }
                        else
                        {
                            for(final ManyToManyTargetConfigurationValue targetValue: targetValues)
                            {
                                manyToManyTargetConfigurationValues.add(targetValue);

                                Item tItem = new BeanItem<ManyToManyTargetConfigurationValue>(targetValue);
                                final TextField nameTf = new TextField(tItem.getItemProperty("name"));
                                nameTf.setReadOnly(true);
                                nameTf.setWidth(300, Unit.PIXELS);
                                final TextField tvf = new TextField(tItem.getItemProperty("targetSystemValue"));
                                tvf.setReadOnly(true);
                                tvf.setWidth(300, Unit.PIXELS);

                                neverToBeEdited.add(nameTf);

                                if (!buttonAdded)
                                {
                                    buttonAdded = true;
                                    final HorizontalLayout hl = new HorizontalLayout();
                                    hl.setSpacing(true);
                                    hl.setSizeUndefined();
                                    if(this.showTargetParamNames())
                                    {
                                        hl.addComponent(nameTf);
                                    }
                                    hl.addComponent(tvf);

                                    Button addTargetValueButton = new Button();
                                    addTargetValueButton.setIcon(VaadinIcons.PLUS);
                                    addTargetValueButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
                                    addTargetValueButton.setDescription("Add new target value.");
                                    addTargetValueButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
                                    addTargetValueButton.setVisible(false);

                                    addTargetValueButton.addClickListener(new Button.ClickListener()
                                    {
                                        @Override
                                        public void buttonClick(ClickEvent clickEvent)
                                        {
                                            final ManyToManyTargetConfigurationValue targetConfigurationValue = new ManyToManyTargetConfigurationValue();
                                            targetConfigurationValue.setGroupId(value.getSourceConfigGroupId());
                                            targetConfigurationValue.setTargetSystemValue("Add target system value");

                                            manyToManyTargetConfigurationValues.add(targetConfigurationValue);

                                            Item tItem = new BeanItem<ManyToManyTargetConfigurationValue>(targetConfigurationValue);
                                            final TextField nameTf = new TextField(tItem.getItemProperty("name"));
                                            nameTf.setReadOnly(false);
                                            nameTf.setWidth(300, Unit.PIXELS);
                                            final TextField tvf = new TextField(tItem.getItemProperty("targetSystemValue"));
                                            tvf.setReadOnly(false);
                                            tvf.setWidth(300, Unit.PIXELS);

                                            final HorizontalLayout hl = new HorizontalLayout();
                                            hl.setSpacing(true);

                                            final Button minusTargetValueButton = new Button();
                                            minusTargetValueButton.setIcon(VaadinIcons.MINUS);
                                            minusTargetValueButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
                                            minusTargetValueButton.setDescription("Add new target value.");
                                            minusTargetValueButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
                                            minusTargetValueButton.setVisible(true);

                                            minusTargetValueButton.addClickListener(new Button.ClickListener()
                                            {
                                                @Override
                                                public void buttonClick(ClickEvent clickEvent)
                                                {
                                                    hl.removeComponent(tvf);
                                                    hl.removeComponent(minusTargetValueButton);
                                                    hl.setImmediate(true);
                                                    hl.markAsDirty();
                                                    targetValueTableCellLayout.removeComponent(hl);
                                                    targetValueTableCellLayout.markAsDirty();

                                                    deletedManyToManyTargetConfigurationValues.add(targetConfigurationValue);
                                                }
                                            });

                                            if(showTargetParamNames())
                                            {
                                                hl.addComponent(nameTf);
                                            }
                                            hl.addComponent(tvf);
                                            hl.addComponent(minusTargetValueButton);

                                            targetValueTableCellLayout.addComponent(hl);
                                        }
                                    });

                                    final Button minusTargetValueButton = new Button();
                                    minusTargetValueButton.setIcon(VaadinIcons.MINUS);
                                    minusTargetValueButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
                                    minusTargetValueButton.setDescription("Remove target value.");
                                    minusTargetValueButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
                                    minusTargetValueButton.setVisible(false);

                                    minusTargetValueButton.addClickListener(new Button.ClickListener()
                                    {
                                        @Override
                                        public void buttonClick(ClickEvent clickEvent)
                                        {
                                            hl.removeComponent(tvf);
                                            hl.removeComponent(minusTargetValueButton);
                                            hl.setImmediate(true);
                                            hl.markAsDirty();
                                            targetValueTableCellLayout.removeComponent(hl);
                                            targetValueTableCellLayout.markAsDirty();

                                            deletedManyToManyTargetConfigurationValues.add(targetValue);
                                        }
                                    });

                                    if (!mappingConfiguration.getConstrainParameterListSizes())
                                    {
                                        hl.addComponent(addTargetValueButton);
                                    }
                                    
                                    targetValueTableCellLayout.addComponent(hl);
                                } 
                                else
                                {
                                    final HorizontalLayout hl = new HorizontalLayout();
                                    hl.setSpacing(true);
                                    hl.setSizeUndefined();

                                    final Button minusTargetValueButton = new Button();
                                    minusTargetValueButton.setIcon(VaadinIcons.MINUS);
                                    minusTargetValueButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
                                    minusTargetValueButton.setDescription("Remove target value.");
                                    minusTargetValueButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
                                    minusTargetValueButton.setVisible(false);

                                    minusTargetValueButton.addClickListener(new Button.ClickListener()
                                    {
                                        @Override
                                        public void buttonClick(ClickEvent clickEvent)
                                        {
                                            hl.removeComponent(nameTf);
                                            hl.removeComponent(tvf);
                                            hl.removeComponent(minusTargetValueButton);
                                            hl.setImmediate(true);
                                            hl.markAsDirty();
                                            targetValueTableCellLayout.removeComponent(hl);
                                            targetValueTableCellLayout.markAsDirty();

                                            deletedManyToManyTargetConfigurationValues.add(targetValue);
                                        }
                                    });

                                    if(showTargetParamNames())
                                    {
                                        hl.addComponent(nameTf);
                                    }

                                    hl.addComponent(tvf);

                                    if(!mappingConfiguration.getConstrainParameterListSizes())
                                    {
                                        hl.addComponent(minusTargetValueButton);
                                    }

                                    targetValueTableCellLayout.addComponent(hl);
                                }
                            }
                        }
                    }
                    else
                    {
                        BeanItem<TargetConfigurationValue> targetConfigurationItem = new BeanItem<TargetConfigurationValue>(value.getTargetConfigurationValue());
                        final TextField targetConfigurationTextField = new TextField(targetConfigurationItem.getItemProperty("targetSystemValue"));
                        targetConfigurationTextField.setReadOnly(true);
                        targetConfigurationTextField.setWidth(300, Unit.PIXELS);

                        targetValueTableCellLayout.addComponent(targetConfigurationTextField);
                    }

                    final DeleteRowAction action = new DeleteRowAction(groupedSourceSystemValues
                        , this.mappingConfiguration, this, this.mappingConfigurationService, this.systemEventService);

                    final Button deleteButton = new Button("Delete");
                    deleteButton.setData(value);
                    deleteButton.setIcon(VaadinIcons.TRASH);
                    deleteButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
                    deleteButton.setDescription("Delete this record");
                    deleteButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
                    deleteButton.addClickListener(new Button.ClickListener() {
                        public void buttonClick(ClickEvent event) {
                            IkasanMessageDialog dialog = new IkasanMessageDialog("Delete record", 
                                "This mapping configuration record will be permanently removed. " +
                                "Are you sure you wish to proceed?.", action);

                            UI.getCurrent().addWindow(dialog);
                        }
                    });
                    
                    final IkasanAuthentication authentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
             	        	.getAttribute(DashboardSessionValueConstants.USER);
                	 
                	if(authentication != null 
                			&& (authentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY)
                					|| authentication.hasGrantedAuthority(SecurityConstants.EDIT_MAPPING_AUTHORITY))
                					|| authentication.canAccessLinkedItem(PolicyLinkTypeConstants.MAPPING_CONFIGURATION_LINK_TYPE
                							, mappingConfiguration.getId()))
                	{
                		deleteButton.setVisible(true);
                	}
                	else
                	{
                		deleteButton.setVisible(false);
                	}

                    this.addItem(new Object[] {tableCellLayout,
                            targetValueTableCellLayout, deleteButton}, value);

                    groupedSourceSystemValues = new ArrayList<SourceConfigurationValue>();
                }
            }

        }
    }

    private boolean showSourceParamNames()
    {
        if(!this.mappingConfiguration.getIsManyToMany() && this.sourceContextParameterNames.size() > 0)
        {
            return true;
        }
        else if(this.mappingConfiguration.getIsManyToMany() && this.sourceContextParameterNames.size() > 0
                && this.targetContextParameterNames.size() > 0)
        {
            return true;
        }

        return false;
    }

    private boolean showTargetParamNames()
    {
        if(this.mappingConfiguration.getIsManyToMany() && this.sourceContextParameterNames.size() > 0
                && this.targetContextParameterNames.size() > 0)
        {
            return true;
        }

        return false;
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

    /* (non-Javadoc)
     * @see com.vaadin.ui.Table#removeAllItems()
     */
    @Override
    public boolean removeAllItems()
    {
        for(SourceConfigurationValue value: this.mappingConfiguration.getSourceConfigurationValues())
        {
            this.mappingConfigurationService.deleteSourceConfigurationValue(value);

            Long numberOfSourceConfigurationValues = this.mappingConfigurationService
                    .getNumberOfSourceConfigurationValuesReferencingTargetConfigurationValue(value.getTargetConfigurationValue());

            if(numberOfSourceConfigurationValues == 0)
            {
                this.mappingConfigurationService.deleteTargetConfigurationValue(value.getTargetConfigurationValue());
            }
        }

        IkasanAuthentication principal = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
                .getAttribute(DashboardSessionValueConstants.USER);

        logger.debug("User: " + principal.getName()
            + " deleted all records for Mapping Configuration " + this.mappingConfiguration);
        
        String message = "[Client=" + mappingConfiguration.getConfigurationServiceClient().getName()
        		+"] [Source Context=" + mappingConfiguration.getSourceContext().getName() + "] [Target Context=" 
        		+ mappingConfiguration.getTargetContext().getName() + "] [Type=" + mappingConfiguration.getConfigurationType().getName()
        		+ "] [Description=" + mappingConfiguration.getDescription() +"] [Number of source params=" 
        		+ mappingConfiguration.getNumberOfParams() + "]";
        
        systemEventService.logSystemEvent(MappingConfigurationConstants.MAPPING_CONFIGURATION_SERVICE, 
        		"Deleted all configuration values for Mapping Configuration " + message, principal.getName());
        
        return super.removeAllItems();
    }

    public ArrayList<ManyToManyTargetConfigurationValue> getManyToManyTargetConfigurationValues()
    {
        return manyToManyTargetConfigurationValues;
    }

    public ArrayList<ManyToManyTargetConfigurationValue> getDeletedManyToManyTargetConfigurationValues()
    {
        return deletedManyToManyTargetConfigurationValues;
    }

    private int manageTableSorting(Object o1, Object o2)
    {
        logger.info("Sorting: " + o1 + " and " + o2);

        if (o1 instanceof CheckBox && o2 instanceof CheckBox)
        {
            Boolean b1 = ((CheckBox) o1).booleanValue();
            return b1.compareTo(((CheckBox) o2).booleanValue());
        }
        else if (o1 instanceof Button && o2 instanceof Button)
        {
            String caption1 = ((Button) o1).getCaption().toLowerCase();
            String caption2 = ((Button) o2).getCaption().toLowerCase();
            return caption1.compareTo(caption2);

        }
        else if (o1 instanceof String && o2 instanceof String)
        {
            return ((String) o1).toLowerCase().compareTo(
                    ((String) o2).toLowerCase());
        }
        else if (o1 instanceof TextField && o2 instanceof TextField)
        {
            return ((TextField) o1).getValue().toLowerCase().compareTo(
                    ((TextField) o2).getValue().toLowerCase());
        }
        else if (o1 instanceof VerticalLayout && o2 instanceof VerticalLayout)
        {
            logger.info("Sorting vertical: " + o1 + " and " + o2);

            List<HorizontalLayout> hl1 = new ArrayList<HorizontalLayout>();
            List<HorizontalLayout> h12 = new ArrayList<HorizontalLayout>();

            TextField t1 = null;
            TextField t2 = null;

            for (int i=0; i< ((VerticalLayout) o1).getComponentCount() ; i++)
            {
                if(((VerticalLayout) o1).getComponent(i) instanceof HorizontalLayout)
                {
                    hl1.add((HorizontalLayout)((VerticalLayout) o1).getComponent(i));

                }
                else if(((VerticalLayout) o1).getComponent(i) instanceof TextField)
                {
                    t1 = (TextField)((VerticalLayout    ) o1).getComponent(i);
                    break;
                }
            }

            for (int i=0; i< ((VerticalLayout) o2).getComponentCount() ; i++)
            {
                if(((VerticalLayout) o2).getComponent(i) instanceof HorizontalLayout)
                {
                    h12.add((HorizontalLayout)((VerticalLayout) o2).getComponent(i));

                }
                if(((VerticalLayout) o2).getComponent(i) instanceof TextField)
                {
                    t2 = (TextField)((VerticalLayout) o2).getComponent(i);
                    break;
                }
            }

            if(t1 != null && t2 != null)
            {
                logger.info("t1: " + t1.getValue());
                logger.info("t2: " + t2.getValue());

                return t1.getValue().toLowerCase().compareTo(
                        (t2.getValue().toLowerCase()));
            }

            for(int i=0; i<hl1.size() && i<h12.size(); i++)
            {
                int result =  sortHorizontalLayout(hl1.get(i), h12.get(i));

                if(result != 0)
                {
                    return result;
                }
            }
        }
        else if (o1 instanceof HorizontalLayout && o2 instanceof HorizontalLayout)
        {
            return sortHorizontalLayout((HorizontalLayout)o1, (HorizontalLayout)o2);
        }

        return 0;
    }

    private int sortHorizontalLayout(HorizontalLayout o1, HorizontalLayout o2)
    {
        TextField t1 = null;
        TextField t2 = null;

        logger.info("Sorting horizontal: " + o1 + " and " + o2);

        for (int i=0; i< ((HorizontalLayout) o1).getComponentCount() ; i++)
        {
            if(((HorizontalLayout) o1).getComponent(i) instanceof TextField)
            {
                t1 = (TextField)((HorizontalLayout) o1).getComponent(i);
                break;
            }
        }

        for (int i=0; i< ((HorizontalLayout) o2).getComponentCount() ; i++)
        {
            if(((HorizontalLayout) o2).getComponent(i) instanceof TextField)
            {
                t2 = (TextField)((HorizontalLayout) o2).getComponent(i);
                break;
            }
        }

        logger.info("t1: " + t1);
        logger.info("t2: " + t2);

        if(t1 != null && t2 != null)
        {
            logger.info("t1: " + t1.getValue());
            logger.info("t2: " + t2.getValue());

            return t1.getValue().toLowerCase().compareTo(
                    (t2.getValue().toLowerCase()));
        }
        else
        {
            return 0;
        }
    }
}
