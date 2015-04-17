/*
 * $Id: SearchButtonClickListener.java 44073 2015-03-17 10:38:20Z stewmi $
 * $URL: https://svc-vcs-prd.uk.mizuho-sc.com:18080/svn/architecture/cmi2/trunk/projects/mappingConfigurationUI/war/src/main/java/org/ikasan/mapping/configuration/ui/listener/SearchButtonClickListener.java $
 *
 * ====================================================================
 *
 * Copyright (c) 2000-2011 by Mizuho International plc.
 * All Rights Reserved.
 *
 * ====================================================================
 *
 */
package org.ikasan.dashboard.ui.mappingconfiguration.listener;

import java.util.List;

import org.apache.log4j.Logger;
import org.ikasan.dashboard.ui.framework.group.VisibilityGroup;
import org.ikasan.dashboard.ui.mappingconfiguration.component.ClientComboBox;
import org.ikasan.dashboard.ui.mappingconfiguration.component.PolicyLinkMappingConfigurationSearchResultsTable;
import org.ikasan.dashboard.ui.mappingconfiguration.component.SourceContextComboBox;
import org.ikasan.dashboard.ui.mappingconfiguration.component.TargetContextComboBox;
import org.ikasan.dashboard.ui.mappingconfiguration.component.TypeComboBox;
import org.ikasan.mapping.model.ConfigurationContext;
import org.ikasan.mapping.model.ConfigurationServiceClient;
import org.ikasan.mapping.model.ConfigurationType;
import org.ikasan.mapping.model.MappingConfigurationLite;
import org.ikasan.mapping.service.MappingConfigurationService;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

/**
 * @author CMI2 Development Team
 *
 */
public class PolicyLinkMappingSearchButtonClickListener implements ClickListener
{
    private static final long serialVersionUID = -9077141163243070560L;

    /** Logger instance */
    private static Logger logger = Logger.getLogger(PolicyLinkMappingSearchButtonClickListener.class);

    private ClientComboBox clientComboBox;
    private TypeComboBox typeComboBox;
    private SourceContextComboBox sourceContextComboBox;
    private TargetContextComboBox targetContextComboBox;
    private MappingConfigurationService mappingConfigurationService;
    private PolicyLinkMappingConfigurationSearchResultsTable searchResultsTable;

    /**
     * Constructor
     * 
     * @param mappingConfigurationService
     * @param clientComboBox
     * @param typeComboBox
     * @param sourceContextComboBox
     * @param targetContextComboBox
     * @param searchResultsTable
     * @param saveRequiredMonitor
     */
    public PolicyLinkMappingSearchButtonClickListener(MappingConfigurationService mappingConfigurationService,
            ClientComboBox clientComboBox, TypeComboBox typeComboBox, SourceContextComboBox sourceContextComboBox,
            TargetContextComboBox targetContextComboBox, PolicyLinkMappingConfigurationSearchResultsTable searchResultsTable)
    {
        this.mappingConfigurationService = mappingConfigurationService;
        this.clientComboBox = clientComboBox;
        this.typeComboBox = typeComboBox;
        this.sourceContextComboBox = sourceContextComboBox;
        this.targetContextComboBox = targetContextComboBox;
        this.searchResultsTable = searchResultsTable;
    }

    /* (non-Javadoc)
     * @see com.vaadin.ui.Button.ClickListener#buttonClick(com.vaadin.ui.Button.ClickEvent)
     */
    @Override
    public void buttonClick(ClickEvent event)
    {
        String clientName = null;
        if(this.clientComboBox.getValue() != null)
        {
            clientName = ((ConfigurationServiceClient)this.clientComboBox.getValue()).getName();
        }

        String typeName = null;
        if(this.typeComboBox.getValue() != null)
        {
            typeName = ((ConfigurationType)this.typeComboBox.getValue()).getName();
        }

        String sourceContextName = null;
        if(this.sourceContextComboBox.getValue() != null)
        {
            sourceContextName = ((ConfigurationContext)this.sourceContextComboBox.getValue()).getName();
        }

        String targetContextName = null;
        if(this.targetContextComboBox.getValue() != null)
        {
            targetContextName = ((ConfigurationContext)this.targetContextComboBox.getValue()).getName();
        }

        long time1 = System.currentTimeMillis();

        List<MappingConfigurationLite> mappingConfigurations = this.mappingConfigurationService.getMappingConfigurationLites(clientName
            , typeName, sourceContextName, targetContextName);

        long time2 = System.currentTimeMillis();

        this.searchResultsTable.removeAllItems();

        for(MappingConfigurationLite mappingConfiguration : mappingConfigurations)
        {
            this.searchResultsTable.addItem(new Object[] {mappingConfiguration.getConfigurationServiceClient().getName(),
                    mappingConfiguration.getConfigurationType().getName(), mappingConfiguration.getSourceContext().getName(),
                    mappingConfiguration.getTargetContext().getName()}
                    , mappingConfiguration);
        }
        
    }
}
