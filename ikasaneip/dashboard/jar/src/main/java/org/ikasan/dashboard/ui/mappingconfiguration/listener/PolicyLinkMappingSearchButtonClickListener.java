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
package org.ikasan.dashboard.ui.mappingconfiguration.listener;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ikasan.dashboard.ui.mappingconfiguration.component.ClientComboBox;
import org.ikasan.dashboard.ui.mappingconfiguration.component.PolicyLinkMappingConfigurationSearchResultsTable;
import org.ikasan.dashboard.ui.mappingconfiguration.component.SourceContextComboBox;
import org.ikasan.dashboard.ui.mappingconfiguration.component.TargetContextComboBox;
import org.ikasan.dashboard.ui.mappingconfiguration.component.TypeComboBox;
import org.ikasan.mapping.model.ConfigurationContext;
import org.ikasan.mapping.model.ConfigurationServiceClient;
import org.ikasan.mapping.model.ConfigurationType;
import org.ikasan.mapping.model.MappingConfigurationLite;
import org.ikasan.mapping.service.MappingManagementService;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

/**
 * @author Ikasan Development Team
 *
 */
public class PolicyLinkMappingSearchButtonClickListener implements ClickListener
{
    private static final long serialVersionUID = -9077141163243070560L;

    /** Logger instance */
    private static Logger logger = LoggerFactory.getLogger(PolicyLinkMappingSearchButtonClickListener.class);

    private ClientComboBox clientComboBox;
    private TypeComboBox typeComboBox;
    private SourceContextComboBox sourceContextComboBox;
    private TargetContextComboBox targetContextComboBox;
    private MappingManagementService mappingConfigurationService;
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
    public PolicyLinkMappingSearchButtonClickListener(MappingManagementService mappingConfigurationService,
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
