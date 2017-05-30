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

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import org.apache.log4j.Logger;
import org.ikasan.dashboard.ui.framework.constants.DashboardConstants;
import org.ikasan.dashboard.ui.framework.constants.SecurityConstants;
import org.ikasan.dashboard.ui.framework.group.VisibilityGroup;
import org.ikasan.dashboard.ui.framework.util.SaveRequiredMonitor;
import org.ikasan.dashboard.ui.framework.window.IkasanMessageDialog;
import org.ikasan.dashboard.ui.mappingconfiguration.action.DeleteMappingConfigurationAction;
import org.ikasan.dashboard.ui.mappingconfiguration.component.ClientComboBox;
import org.ikasan.dashboard.ui.mappingconfiguration.component.MappingConfigurationSearchResultsTable;
import org.ikasan.dashboard.ui.mappingconfiguration.component.SourceContextComboBox;
import org.ikasan.dashboard.ui.mappingconfiguration.component.TargetContextComboBox;
import org.ikasan.dashboard.ui.mappingconfiguration.component.TypeComboBox;
import org.ikasan.dashboard.ui.mappingconfiguration.util.MappingConfigurationExportHelper;
import org.ikasan.dashboard.ui.mappingconfiguration.util.MappingConfigurationValuesExportHelper;
import org.ikasan.mapping.model.*;
import org.ikasan.mapping.service.MappingManagementService;
import org.ikasan.spec.configuration.PlatformConfigurationService;
import org.ikasan.systemevent.service.SystemEventService;
import org.vaadin.teemu.VaadinIcons;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;

/**
 * @author Ikasan Development Team
 *
 */
public class MappingSearchButtonClickListener implements ClickListener
{
    private static final long serialVersionUID = -9077141163243070560L;

    /** Logger instance */
    private static Logger logger = Logger.getLogger(MappingSearchButtonClickListener.class);

    private ClientComboBox clientComboBox;
    private TypeComboBox typeComboBox;
    private SourceContextComboBox sourceContextComboBox;
    private TargetContextComboBox targetContextComboBox;
    private MappingManagementService mappingConfigurationService;
    private MappingConfigurationSearchResultsTable searchResultsTable;
    private SaveRequiredMonitor saveRequiredMonitor;
    private VisibilityGroup visibilityGroup;
    private SystemEventService systemEventService;
    private MappingConfigurationExportHelper mappingConfigurationExportHelper;
    private PlatformConfigurationService platformConfigurationService;

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
    public MappingSearchButtonClickListener(MappingManagementService mappingConfigurationService,
            ClientComboBox clientComboBox, TypeComboBox typeComboBox, SourceContextComboBox sourceContextComboBox,
            TargetContextComboBox targetContextComboBox, MappingConfigurationSearchResultsTable searchResultsTable,
            SaveRequiredMonitor saveRequiredMonitor, VisibilityGroup visibilityGroup, SystemEventService systemEventService,
            MappingConfigurationExportHelper mappingConfigurationExportHelper, PlatformConfigurationService platformConfigurationService)
    {
        this.mappingConfigurationService = mappingConfigurationService;
        this.clientComboBox = clientComboBox;
        this.typeComboBox = typeComboBox;
        this.sourceContextComboBox = sourceContextComboBox;
        this.targetContextComboBox = targetContextComboBox;
        this.searchResultsTable = searchResultsTable;
        this.saveRequiredMonitor = saveRequiredMonitor;
        this.visibilityGroup = visibilityGroup;
        this.systemEventService = systemEventService;
        this.mappingConfigurationExportHelper = mappingConfigurationExportHelper;
        this.platformConfigurationService = platformConfigurationService;
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

        List<MappingConfigurationLite> mappingConfigurations = this.mappingConfigurationService.getMappingConfigurationLites(clientName
            , typeName, sourceContextName, targetContextName);

        this.searchResultsTable.removeAllItems();

        for(final MappingConfigurationLite mappingConfiguration : mappingConfigurations)
        {
            final DeleteMappingConfigurationAction action = new DeleteMappingConfigurationAction( mappingConfiguration.getId()
                , this.searchResultsTable, this.mappingConfigurationService, this.systemEventService);

            final Button deleteButton = new Button();
            deleteButton.setIcon(VaadinIcons.TRASH);
            deleteButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
            deleteButton.setDescription("Delete this mapping configuration");
            deleteButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
            deleteButton.addClickListener(new Button.ClickListener() {
                public void buttonClick(ClickEvent event) {
                    IkasanMessageDialog dialog = new IkasanMessageDialog("Delete record", 
                        "This mapping configuration record will be permanently removed. " +
                        "Are you sure you wish to proceed?.", action);

                    UI.getCurrent().addWindow(dialog);
                }
            });

            SimpleDateFormat format = new SimpleDateFormat(DashboardConstants.DATE_FORMAT_TABLE_VIEWS);
            String timestamp = format.format(mappingConfiguration.getUpdatedDateTime());

            String numberOfMappings = "Not Available";
            if(mappingConfiguration.getNumberOfMappings() > 0)
            {
                numberOfMappings = new Integer(mappingConfiguration.getNumberOfMappings()).toString();
            }

            String lastUpdatedBy = "Not Available";
            if(mappingConfiguration.getLastUpdatedBy() != null && mappingConfiguration.getLastUpdatedBy() != "")
            {
                lastUpdatedBy = mappingConfiguration.getLastUpdatedBy();
            }

            final Button exportMappingConfigurationButton = new Button();
            FileDownloader fd = new FileDownloader(getMappingConfigurationExportStream(mappingConfiguration));
            fd.extend(exportMappingConfigurationButton);

            exportMappingConfigurationButton.setIcon(VaadinIcons.DOWNLOAD_ALT);
            exportMappingConfigurationButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
            exportMappingConfigurationButton.setDescription("Export the current mapping configuration");
            exportMappingConfigurationButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);

            this.visibilityGroup.registerComponent(SecurityConstants.ALL_AUTHORITY, deleteButton);
            this.searchResultsTable.addItem(new Object[] {mappingConfiguration.getConfigurationServiceClient().getName(),
                    mappingConfiguration.getConfigurationType().getName(), mappingConfiguration.getSourceContext().getName(),
                    mappingConfiguration.getTargetContext().getName(), numberOfMappings
                    , lastUpdatedBy, timestamp, deleteButton, exportMappingConfigurationButton}, mappingConfiguration.getId());
        }
        
    }

    /**
     * Helper method to get the stream associated with the export of the file.
     *
     * @return the StreamResource associated with the export.
     */
    private StreamResource getMappingConfigurationExportStream(final MappingConfigurationLite lite)
    {
        StreamResource.StreamSource source = new StreamResource.StreamSource()
        {

            public InputStream getStream() {
                ByteArrayOutputStream stream = null;
                try
                {
                    logger.info("downloading mapping configuration: " + lite.getId());
                    final MappingConfiguration mappingConfiguration = mappingConfigurationService.getMappingConfigurationById(lite.getId());
                    stream = getMappingConfigurationExport(mappingConfiguration);
                }
                catch (IOException e)
                {
                    logger.error(e.getMessage(), e);
                }
                InputStream input = new ByteArrayInputStream(stream.toByteArray());
                return input;

            }
        };

        StringBuffer fileName = new StringBuffer();
        fileName.append(lite.getConfigurationServiceClient().getName()).append("_");
        fileName.append(lite.getConfigurationType().getName()).append("_");
        fileName.append(lite.getSourceContext().getName()).append("_");
        fileName.append(lite.getTargetContext().getName()).append("_mappingExport.xml");

        StreamResource resource = new StreamResource ( source, fileName.toString());
        return resource;
    }

    /**
     * Helper method to get the ByteArrayOutputStream associated with the export.
     *
     * @return
     * @throws IOException
     */
    private ByteArrayOutputStream getMappingConfigurationExport(MappingConfiguration mappingConfiguration) throws IOException
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        String schemaLocation = (String)this.platformConfigurationService.getConfigurationValue("mappingExportSchemaLocation");

        if(schemaLocation == null || schemaLocation.length() == 0)
        {
            throw new RuntimeException("Cannot resolve the platform configuration mappingExportSchemaLocation!");
        }

        logger.debug("Resolved schemaLocation " + schemaLocation);


        String exportXml = this.mappingConfigurationExportHelper.getMappingConfigurationExportXml(mappingConfiguration,
                loadSourceParameterNames(mappingConfiguration), loadTargetParameterNames(mappingConfiguration), schemaLocation);

        out.write(exportXml.getBytes());

        return out;
    }

    private ArrayList<ParameterName> loadSourceParameterNames(MappingConfiguration mappingConfiguration)
    {
        List<ParameterName> parameterNames = this.mappingConfigurationService.getParameterNamesByMappingConfigurationId
                (mappingConfiguration.getId());

        ArrayList<ParameterName> sourceContextParameterNames = new ArrayList<ParameterName>();

        for(ParameterName parameterName: parameterNames)
        {
            if(parameterName.getContext().equals(ParameterName.SOURCE_CONTEXT))
            {
                sourceContextParameterNames.add(parameterName);
            }
        }

        return sourceContextParameterNames;
    }

    private ArrayList<ParameterName> loadTargetParameterNames(MappingConfiguration mappingConfiguration)
    {
        List<ParameterName> parameterNames = this.mappingConfigurationService.getParameterNamesByMappingConfigurationId
                (mappingConfiguration.getId());

        ArrayList<ParameterName> targetContextParameterNames = new ArrayList<ParameterName>();

        for(ParameterName parameterName: parameterNames)
        {
            if(parameterName.getContext().equals(ParameterName.TARGET_CONTEXT))
            {
                targetContextParameterNames.add(parameterName);
            }
        }

        return targetContextParameterNames;
    }
}
