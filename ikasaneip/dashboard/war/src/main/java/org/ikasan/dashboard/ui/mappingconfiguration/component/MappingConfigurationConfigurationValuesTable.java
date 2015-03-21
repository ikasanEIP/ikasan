/*
 * $Id: MappingConfigurationConfigurationValuesTable.java 44073 2015-03-17 10:38:20Z stewmi $
 * $URL: https://svc-vcs-prd.uk.mizuho-sc.com:18080/svn/architecture/cmi2/trunk/projects/mappingConfigurationUI/war/src/main/java/org/ikasan/mapping/configuration/ui/component/MappingConfigurationConfigurationValuesTable.java $
 *
 * ====================================================================
 *
 * Copyright (c) 2000-2011 by Mizuho International plc.
 * All Rights Reserved.
 *
 * ====================================================================
 *
 */
package org.ikasan.dashboard.ui.mappingconfiguration.component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;
import org.ikasan.dashboard.ui.framework.group.VisibilityGroup;
import org.ikasan.dashboard.ui.framework.util.UserDetailsHelper;
import org.ikasan.dashboard.ui.framework.window.IkasanMessageDialog;
import org.ikasan.dashboard.ui.mappingconfiguration.action.DeleteRowAction;
import org.ikasan.dashboard.ui.mappingconfiguration.util.MappingConfigurationUISessionValueConstants;
import org.ikasan.mapping.model.MappingConfiguration;
import org.ikasan.mapping.model.SourceConfigurationValue;
import org.ikasan.mapping.model.TargetConfigurationValue;
import org.ikasan.mapping.service.MappingConfigurationService;
import org.ikasan.mapping.service.MappingConfigurationServiceException;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.DefaultItemSorter;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

/**
 * @author CMI2 Development Team
 *
 */
public class MappingConfigurationConfigurationValuesTable extends Table
{
    private static final long serialVersionUID = -3565819620819253906L;

    /** Logger instance */
    private static Logger logger = Logger.getLogger(MappingConfigurationConfigurationValuesTable.class);
    
    private MappingConfigurationService mappingConfigurationService;
    private MappingConfiguration mappingConfiguration;
    private IndexedContainer container;
    private VisibilityGroup visibilityGroup;

    /**
     * Constructor
     * 
     * @param mappingConfigurationService
     * @param visibilityGroup
     */
    public MappingConfigurationConfigurationValuesTable(MappingConfigurationService mappingConfigurationService,
            VisibilityGroup visibilityGroup)
    {
        this.mappingConfigurationService = mappingConfigurationService;
        this.visibilityGroup = visibilityGroup;
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
                    return ((TextField)((VerticalLayout) o1).getComponent(0)).getValue().toLowerCase().compareTo(
                        ((TextField)((VerticalLayout) o2).getComponent(0)).getValue().toLowerCase());
                }

                return 0;

            }
        }));

        container.addContainerProperty("Source Configuration Value", VerticalLayout.class,  null);
        container.addContainerProperty("Target Configuration Value", TextField.class,  null);
        container.addContainerProperty("Delete", Button.class,  null);

        this.setCellStyleGenerator(new IkasanCellStyleGenerator());
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
                ((TextField)layout.getComponent(i)).setReadOnly(!editable);
            }

            Property<TextField> targetProperty = item.getItemProperty("Target Configuration Value");
            targetProperty.getValue().setReadOnly(!editable);
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

        for(SourceConfigurationValue value: this.mappingConfiguration.getSourceConfigurationValues())
        {
           Long numberOfSourceConfigurationValues = this.mappingConfigurationService
                    .getNumberOfSourceConfigurationValuesReferencingTargetConfigurationValue(value.getTargetConfigurationValue());

            if(numberOfSourceConfigurationValues == 0)
            {
                this.mappingConfigurationService.deleteTargetConfigurationValue(value.getTargetConfigurationValue());
            }
            else
            {
                UserDetailsHelper userDetailHelper = (UserDetailsHelper)VaadinService.getCurrentRequest().getWrappedSession()
                        .getAttribute(MappingConfigurationUISessionValueConstants.USER);

                logger.info("User: " + userDetailHelper.getUserDetails().getUsername() + " saving Target Configuration Value: " +
                        value);
                this.mappingConfigurationService.saveTargetConfigurationValue(value.getTargetConfigurationValue());
            }
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
        
        if(this.mappingConfiguration.getNumberOfParams() > 1)
        {
            sourceSystemGroupId = this.mappingConfigurationService.getNextSequenceNumber();
        }
        TargetConfigurationValue targetConfigurationValue = new TargetConfigurationValue();
        targetConfigurationValue.setTargetSystemValue("Add targetSystemValue");

        this.mappingConfigurationService.saveTargetConfigurationValue(targetConfigurationValue);

        VerticalLayout tableCellLayout = new VerticalLayout();

        SourceConfigurationValue sourceConfigurationValue = null;
        final Button deleteButton = new Button("Delete");

        ArrayList<SourceConfigurationValue> sourceConfigurationValues = new ArrayList<SourceConfigurationValue>();

        for(int i=0; i<this.mappingConfiguration.getNumberOfParams(); i++)
        {
            sourceConfigurationValue = new SourceConfigurationValue();
            sourceConfigurationValue.setSourceSystemValue("Add source system value");
            sourceConfigurationValue.setSourceConfigGroupId(sourceSystemGroupId);
            sourceConfigurationValue.setTargetConfigurationValue(targetConfigurationValue);
            sourceConfigurationValue.setMappingConfigurationId(this.mappingConfiguration.getId());

            sourceConfigurationValues.add(sourceConfigurationValue);

            this.mappingConfiguration.getSourceConfigurationValues().add(sourceConfigurationValue);
            
            BeanItem<SourceConfigurationValue> item = new BeanItem<SourceConfigurationValue>(sourceConfigurationValue);
            final TextField tf = new TextField(item.getItemProperty("sourceSystemValue"));

//            tf.addFocusListener(new FieldEvents.FocusListener() {
//                @Override
//                public void focus(com.vaadin.event.FieldEvents.FocusEvent event)
//                {
//                    tf.selectAll();
//                }
//            });

            tableCellLayout.addComponent(tf);
            tf.setReadOnly(true);
            tf.setWidth(300, Unit.PIXELS);
        }

        BeanItem<TargetConfigurationValue> targetConfigurationItem = new BeanItem<TargetConfigurationValue>(targetConfigurationValue);
        final TextField targetConfigurationTextField = new TextField(targetConfigurationItem.getItemProperty("targetSystemValue"));
        targetConfigurationTextField.setReadOnly(true);
        targetConfigurationTextField.setWidth(300, Unit.PIXELS);
//        targetConfigurationTextField.addFocusListener(new FieldEvents.FocusListener() {
//            @Override
//            public void focus(com.vaadin.event.FieldEvents.FocusEvent event)
//            {
//                targetConfigurationTextField.selectAll();
//            }
//        });

        final DeleteRowAction action = new DeleteRowAction(sourceConfigurationValues, this.mappingConfiguration
            , this, this.mappingConfigurationService);

        deleteButton.setStyleName(Reindeer.BUTTON_LINK);
        deleteButton.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                IkasanMessageDialog dialog = new IkasanMessageDialog("Delete record", 
                    "This mapping configuration record will be permanently removed. " +
                    "Are you sure you wish to proceed?.", action);

                UI.getCurrent().addWindow(dialog);
            }
        });
        this.visibilityGroup.registerComponent(deleteButton);


        Item item = this.container.addItemAt(0, sourceConfigurationValue);
        Property<Layout> sourceProperty = item.getItemProperty("Source Configuration Value");
        sourceProperty.setValue(tableCellLayout);
        Property<TextField> targetProperty = item.getItemProperty("Target Configuration Value");
        targetProperty.setValue(targetConfigurationTextField);
        Property<Button> deleteProperty = item.getItemProperty("Delete");
        deleteProperty.setValue(deleteButton);

        this.mappingConfigurationService.saveMappingConfiguration(mappingConfiguration);

        this.setEditable(true);

        UserDetailsHelper userDetailsHelper = (UserDetailsHelper)VaadinService.getCurrentRequest().getWrappedSession()
                .getAttribute(MappingConfigurationUISessionValueConstants.USER);

        logger.info("User: " + userDetailsHelper.getUserDetails().getUsername() 
            + " added new mapping configuration value for Mapping Configuration " 
                + this.mappingConfiguration);
    }

    /**
     * Method to help populate the table with values associated with the MappingConfiguration.
     * 
     * @param mappingConfiguration
     */
    public void populateTable(final MappingConfiguration mappingConfiguration)
    {
        this.mappingConfiguration = mappingConfiguration;

        Set<SourceConfigurationValue> sourceConfigurationValues 
            = mappingConfiguration.getSourceConfigurationValues();

        super.removeAllItems();

        Iterator<SourceConfigurationValue> sourceConfigurationValueItr = sourceConfigurationValues.iterator();

        ArrayList<SourceConfigurationValue> usedSourceConfigurationValues = new ArrayList<SourceConfigurationValue>();

        ArrayList<SourceConfigurationValue> groupedSourceSystemValues = new ArrayList<SourceConfigurationValue>();

        while(sourceConfigurationValueItr.hasNext())
        {
            SourceConfigurationValue value = sourceConfigurationValueItr.next();

            VerticalLayout tableCellLayout = new VerticalLayout();

            for(int i=0; i<this.mappingConfiguration.getNumberOfParams(); i++)
            {
                if(!usedSourceConfigurationValues.contains(value))
                {
                    groupedSourceSystemValues.add(value);

                    BeanItem<SourceConfigurationValue> item = new BeanItem<SourceConfigurationValue>(value);
                    final TextField tf = new TextField(item.getItemProperty("sourceSystemValue"));
                    tf.setWidth(300, Unit.PIXELS);
//                    tf.addFocusListener(new FieldEvents.FocusListener() {
//                        @Override
//                        public void focus(com.vaadin.event.FieldEvents.FocusEvent event)
//                        {
//                            tf.selectAll();
//                        }
//                    });

                    tableCellLayout.addComponent(tf);
                    tf.setReadOnly(true);
                    usedSourceConfigurationValues.add(value);
    
                    Iterator<SourceConfigurationValue> partnerSourceConfigurationValueItr = sourceConfigurationValues.iterator();
    
                    while(partnerSourceConfigurationValueItr.hasNext())
                    {
                        SourceConfigurationValue partnerSourceConfigurationValue = partnerSourceConfigurationValueItr.next();

                        if(partnerSourceConfigurationValue.getSourceConfigGroupId() != null &&
                                !usedSourceConfigurationValues.contains(partnerSourceConfigurationValue) && 
                                partnerSourceConfigurationValue.getId().compareTo(value.getId()) != 0
                                && partnerSourceConfigurationValue.getSourceConfigGroupId().compareTo(value.getSourceConfigGroupId()) == 0)
                        {
                            groupedSourceSystemValues.add(partnerSourceConfigurationValue);
                            item = new BeanItem<SourceConfigurationValue>(partnerSourceConfigurationValue);
                            final TextField stf = new TextField(item.getItemProperty("sourceSystemValue"));
                            stf.setWidth(300, Unit.PIXELS);

//                            stf.addFocusListener(new FieldEvents.FocusListener() {
//                                @Override
//                                public void focus(com.vaadin.event.FieldEvents.FocusEvent event)
//                                {
//                                    stf.selectAll();
//                                }
//                            });

                            tableCellLayout.addComponent(stf);
                            stf.setReadOnly(true);
                            usedSourceConfigurationValues.add(partnerSourceConfigurationValue);
                        }
                    }

                    TargetConfigurationValue targetConfigurationValue = value.getTargetConfigurationValue();
                    BeanItem<TargetConfigurationValue> targetConfigurationItem = new BeanItem<TargetConfigurationValue>(targetConfigurationValue);
                    final TextField targetConfigurationTextField = new TextField(targetConfigurationItem.getItemProperty("targetSystemValue"));
                    targetConfigurationTextField.setReadOnly(true);
                    targetConfigurationTextField.setWidth(300, Unit.PIXELS);
//                    targetConfigurationTextField.addFocusListener(new FieldEvents.FocusListener() {
//                        @Override
//                        public void focus(com.vaadin.event.FieldEvents.FocusEvent event)
//                        {
//                            targetConfigurationTextField.selectAll();
//                        }
//                    });

                    final DeleteRowAction action = new DeleteRowAction(groupedSourceSystemValues
                        , this.mappingConfiguration, this, this.mappingConfigurationService);

                    final Button deleteButton = new Button("Delete");
                    deleteButton.setData(value);
                    deleteButton.setStyleName(Reindeer.BUTTON_LINK);
                    deleteButton.addClickListener(new Button.ClickListener() {
                        public void buttonClick(ClickEvent event) {
                            IkasanMessageDialog dialog = new IkasanMessageDialog("Delete record", 
                                "This mapping configuration record will be permanently removed. " +
                                "Are you sure you wish to proceed?.", action);

                            UI.getCurrent().addWindow(dialog);
                        }
                    });
                    this.visibilityGroup.registerComponent(deleteButton);

                    this.addItem(new Object[] {tableCellLayout,
                            targetConfigurationTextField, deleteButton}, value);

                    groupedSourceSystemValues = new ArrayList<SourceConfigurationValue>();
                }
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

        UserDetailsHelper userDetailsHelper = (UserDetailsHelper)VaadinService.getCurrentRequest().getWrappedSession()
                .getAttribute(MappingConfigurationUISessionValueConstants.USER);

        logger.info("User: " + userDetailsHelper.getUserDetails().getUsername() 
            + " deleted all records for Mapping Configuration " + this.mappingConfiguration);
        
        return super.removeAllItems();
    }
}
