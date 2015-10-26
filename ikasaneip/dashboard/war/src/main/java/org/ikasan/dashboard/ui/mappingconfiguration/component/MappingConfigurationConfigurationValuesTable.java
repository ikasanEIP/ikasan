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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;
import org.ikasan.dashboard.ui.framework.constants.SecurityConstants;
import org.ikasan.dashboard.ui.framework.group.VisibilityGroup;
import org.ikasan.dashboard.ui.framework.util.DashboardSessionValueConstants;
import org.ikasan.dashboard.ui.framework.util.PolicyLinkTypeConstants;
import org.ikasan.dashboard.ui.framework.window.IkasanMessageDialog;
import org.ikasan.dashboard.ui.mappingconfiguration.action.DeleteRowAction;
import org.ikasan.dashboard.ui.mappingconfiguration.util.MappingConfigurationConstants;
import org.ikasan.mapping.model.MappingConfiguration;
import org.ikasan.mapping.model.SourceConfigurationValue;
import org.ikasan.mapping.model.TargetConfigurationValue;
import org.ikasan.mapping.service.MappingConfigurationService;
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
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
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
    
    private MappingConfigurationService mappingConfigurationService;
    private MappingConfiguration mappingConfiguration;
    private IndexedContainer container;
    private VisibilityGroup visibilityGroup;
    private SystemEventService systemEventService;

    /**
     * Constructor
     * 
     * @param mappingConfigurationService
     * @param visibilityGroup
     */
    public MappingConfigurationConfigurationValuesTable(MappingConfigurationService mappingConfigurationService,
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
            	IkasanAuthentication authentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
                        .getAttribute(DashboardSessionValueConstants.USER);

                logger.debug("User: " + authentication.getName() + " saving Target Configuration Value: " +
                        value);
                this.mappingConfigurationService.saveTargetConfigurationValue(value.getTargetConfigurationValue());
                
                systemEventService.logSystemEvent(MappingConfigurationConstants.MAPPING_CONFIGURATION_SERVICE, 
                		"Saving Target Configuration Value: " + value, authentication.getName());
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

            tableCellLayout.addComponent(tf);
            tf.setReadOnly(true);
            tf.setWidth(300, Unit.PIXELS);
        }

        BeanItem<TargetConfigurationValue> targetConfigurationItem = new BeanItem<TargetConfigurationValue>(targetConfigurationValue);
        final TextField targetConfigurationTextField = new TextField(targetConfigurationItem.getItemProperty("targetSystemValue"));
        targetConfigurationTextField.setReadOnly(true);
        targetConfigurationTextField.setWidth(300, Unit.PIXELS);

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
        sourceProperty.setValue(tableCellLayout);
        Property<TextField> targetProperty = item.getItemProperty("Target Configuration Value");
        targetProperty.setValue(targetConfigurationTextField);
        Property<Button> deleteProperty = item.getItemProperty("Delete");
        deleteProperty.setValue(deleteButton);

        this.mappingConfigurationService.saveMappingConfiguration(mappingConfiguration);

        this.setEditable(true);

        IkasanAuthentication principal = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
                .getAttribute(DashboardSessionValueConstants.USER);

        logger.debug("User: " + principal.getName() 
            + " added new mapping configuration value for Mapping Configuration " 
                + this.mappingConfiguration);
        
        systemEventService.logSystemEvent(MappingConfigurationConstants.MAPPING_CONFIGURATION_SERVICE, 
        		"Added new mapping configuration value for Mapping Configuration: " + this.mappingConfiguration, principal.getName());
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
}
