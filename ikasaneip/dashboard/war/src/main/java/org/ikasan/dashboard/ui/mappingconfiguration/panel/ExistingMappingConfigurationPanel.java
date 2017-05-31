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

import org.ikasan.dashboard.ui.framework.group.Editable;
import org.ikasan.dashboard.ui.framework.group.FunctionalGroup;
import org.ikasan.dashboard.ui.framework.navigation.IkasanUINavigator;
import org.ikasan.dashboard.ui.framework.navigation.MenuLayout;
import org.ikasan.dashboard.ui.framework.util.SaveRequiredMonitor;
import org.ikasan.dashboard.ui.mappingconfiguration.component.ClientComboBox;
import org.ikasan.dashboard.ui.mappingconfiguration.component.MappingConfigurationConfigurationValuesTable;
import org.ikasan.dashboard.ui.mappingconfiguration.component.SourceContextComboBox;
import org.ikasan.dashboard.ui.mappingconfiguration.component.TargetContextComboBox;
import org.ikasan.dashboard.ui.mappingconfiguration.component.TypeComboBox;
import org.ikasan.dashboard.ui.mappingconfiguration.util.MappingConfigurationExportHelper;
import org.ikasan.dashboard.ui.mappingconfiguration.util.MappingConfigurationValuesExportHelper;
import org.ikasan.mapping.service.MappingManagementService;
import org.ikasan.spec.configuration.Configuration;
import org.ikasan.spec.configuration.ConfigurationManagement;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.configuration.PlatformConfigurationService;
import org.ikasan.systemevent.service.SystemEventService;

import com.vaadin.navigator.View;
import com.vaadin.ui.Button;

/**
 * @author Ikasan Development Team
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
            TargetContextComboBox targetContextComboBox, MappingManagementService mappingConfigurationService,
            SaveRequiredMonitor saveRequiredMonitor, Button editButton, Button saveButton, Button addNewRecordButton, 
            Button deleteAllRecordsButton, Button importMappingConfigurationButton, Button exportMappingConfigurationValuesButton,
            Button exportMappingConfigurationButton, Button cancelButton, FunctionalGroup existingMappingConfigurationFunctionalGroup,
            MappingConfigurationExportHelper mappingConfigurationExportHelper, MappingConfigurationValuesExportHelper 
            mappingConfigurationValuesExportHelper, SystemEventService systemEventService, IkasanUINavigator topLevelNavigator
            , IkasanUINavigator uiNavigator, MenuLayout menuLayout, ConfigurationManagement<ConfiguredResource, Configuration> configurationManagement,
            PlatformConfigurationService platformConfigurationService)
    {
        super(mappingConfigurationConfigurationValuesTable, clientComboBox, typeComboBox, sourceContextComboBox,
            targetContextComboBox, "Mapping Configuration", mappingConfigurationService, saveRequiredMonitor, editButton,
            saveButton, addNewRecordButton, deleteAllRecordsButton, importMappingConfigurationButton, exportMappingConfigurationValuesButton,
            exportMappingConfigurationButton, cancelButton, existingMappingConfigurationFunctionalGroup, mappingConfigurationExportHelper,
            mappingConfigurationValuesExportHelper, systemEventService, topLevelNavigator, uiNavigator, menuLayout, configurationManagement,
            platformConfigurationService);

        super.init();
    }

}
