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
import org.ikasan.dashboard.ui.framework.util.SaveRequiredMonitor;
import org.ikasan.dashboard.ui.framework.window.IkasanMessageDialog;
import org.ikasan.dashboard.ui.mappingconfiguration.action.DeleteMappingConfigurationAction;
import org.ikasan.dashboard.ui.mappingconfiguration.component.ClientComboBox;
import org.ikasan.dashboard.ui.mappingconfiguration.component.SearchResultsTable;
import org.ikasan.dashboard.ui.mappingconfiguration.component.SourceContextComboBox;
import org.ikasan.dashboard.ui.mappingconfiguration.component.TargetContextComboBox;
import org.ikasan.dashboard.ui.mappingconfiguration.component.TypeComboBox;

import com.mizuho.cmi2.mappingConfiguration.model.ConfigurationContext;
import com.mizuho.cmi2.mappingConfiguration.model.ConfigurationServiceClient;
import com.mizuho.cmi2.mappingConfiguration.model.ConfigurationType;
import com.mizuho.cmi2.mappingConfiguration.model.MappingConfiguration;
import com.mizuho.cmi2.mappingConfiguration.service.MappingConfigurationService;
import com.mizuho.cmi2.mappingConfiguration.model.MappingConfigurationLite;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.Reindeer;

/**
 * @author CMI2 Development Team
 *
 */
public class SearchButtonClickListener implements ClickListener
{
    private static final long serialVersionUID = -9077141163243070560L;

    /** Logger instance */
    private static Logger logger = Logger.getLogger(SearchButtonClickListener.class);

    private ClientComboBox clientComboBox;
    private TypeComboBox typeComboBox;
    private SourceContextComboBox sourceContextComboBox;
    private TargetContextComboBox targetContextComboBox;
    private MappingConfigurationService mappingConfigurationService;
    private SearchResultsTable searchResultsTable;
    protected SaveRequiredMonitor saveRequiredMonitor;
    private VisibilityGroup visibilityGroup;

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
    public SearchButtonClickListener(MappingConfigurationService mappingConfigurationService,
            ClientComboBox clientComboBox, TypeComboBox typeComboBox, SourceContextComboBox sourceContextComboBox,
            TargetContextComboBox targetContextComboBox, SearchResultsTable searchResultsTable,
            SaveRequiredMonitor saveRequiredMonitor, VisibilityGroup visibilityGroup)
    {
        this.mappingConfigurationService = mappingConfigurationService;
        this.clientComboBox = clientComboBox;
        this.typeComboBox = typeComboBox;
        this.sourceContextComboBox = sourceContextComboBox;
        this.targetContextComboBox = targetContextComboBox;
        this.searchResultsTable = searchResultsTable;
        this.saveRequiredMonitor = saveRequiredMonitor;
        this.visibilityGroup = visibilityGroup;
    }

    /* (non-Javadoc)
     * @see com.vaadin.ui.Button.ClickListener#buttonClick(com.vaadin.ui.Button.ClickEvent)
     */
    @Override
    public void buttonClick(ClickEvent event)
    {
        saveRequiredMonitor.manageSaveRequired("searchResultsPanel");

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
        long queryTime = time2 - time1;

        logger.info("Mapping configuration query time: " + queryTime);

        this.searchResultsTable.removeAllItems();

        for(MappingConfigurationLite mappingConfiguration : mappingConfigurations)
        {
            final DeleteMappingConfigurationAction action = new DeleteMappingConfigurationAction( mappingConfiguration.getId()
                , this.searchResultsTable, this.mappingConfigurationService);

            final Button deleteButton = new Button("Delete");
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
            this.searchResultsTable.addItem(new Object[] {mappingConfiguration.getConfigurationServiceClient().getName(),
                    mappingConfiguration.getConfigurationType().getName(), mappingConfiguration.getSourceContext().getName(),
                    mappingConfiguration.getTargetContext().getName(), deleteButton}
                    , mappingConfiguration.getId());
        }
        
    }
}
