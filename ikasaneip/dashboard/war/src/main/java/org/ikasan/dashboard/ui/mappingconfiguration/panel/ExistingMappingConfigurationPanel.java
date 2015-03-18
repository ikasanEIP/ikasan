/*
 * $Id: ExistingMappingConfigurationPanel.java 40648 2014-11-07 11:12:53Z stewmi $
 * $URL: https://svc-vcs-prd.uk.mizuho-sc.com:18080/svn/architecture/cmi2/trunk/projects/mappingConfigurationUI/war/src/main/java/org/ikasan/mapping/configuration/ui/panel/ExistingMappingConfigurationPanel.java $
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

import com.mizuho.cmi2.mappingConfiguration.service.MappingConfigurationService;
import com.vaadin.navigator.View;
import com.vaadin.ui.Button;

/**
 * @author CMI2 Development Team
 *
 */
public class ExistingMappingConfigurationPanel extends MappingConfigurationPanel implements View, Editable
{
    private static final long serialVersionUID = -9039289194412332063L;

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
     * @param exportMappingConfigurationButton
     * @param cancelButton
     * @param existingMappingConfigurationFunctionalGroup
     */
    public ExistingMappingConfigurationPanel(
            MappingConfigurationConfigurationValuesTable mappingConfigurationConfigurationValuesTable,
            ClientComboBox clientComboBox, TypeComboBox typeComboBox, SourceContextComboBox sourceContextComboBox,
            TargetContextComboBox targetContextComboBox, MappingConfigurationService mappingConfigurationService,
            SaveRequiredMonitor saveRequiredMonitor, Button editButton, Button saveButton, Button addNewRecordButton, 
            Button deleteAllRecordsButton, Button importMappingConfigurationButton, Button exportMappingConfigurationValuesButton,
            Button exportMappingConfigurationButton, Button cancelButton, FunctionalGroup existingMappingConfigurationFunctionalGroup,
            MappingConfigurationExportHelper mappingConfigurationExportHelper, MappingConfigurationValuesExportHelper 
            mappingConfigurationValuesExportHelper)
    {
        super(mappingConfigurationConfigurationValuesTable, clientComboBox, typeComboBox, sourceContextComboBox,
            targetContextComboBox, "Mapping Configuration", mappingConfigurationService, saveRequiredMonitor, editButton,
            saveButton, addNewRecordButton, deleteAllRecordsButton, importMappingConfigurationButton, exportMappingConfigurationValuesButton,
            exportMappingConfigurationButton, cancelButton, existingMappingConfigurationFunctionalGroup, mappingConfigurationExportHelper,
            mappingConfigurationValuesExportHelper);

        super.init();
    }

}
