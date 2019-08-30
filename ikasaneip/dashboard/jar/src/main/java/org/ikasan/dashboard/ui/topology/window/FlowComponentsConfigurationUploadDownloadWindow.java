package org.ikasan.dashboard.ui.topology.window;

import com.vaadin.server.ConnectorResource;
import com.vaadin.server.DownloadStream;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ikasan.configurationService.util.FlowConfigurationExportHelper;
import org.ikasan.dashboard.ui.framework.util.XmlFormatter;
import org.ikasan.spec.configuration.Configuration;
import org.ikasan.spec.configuration.ConfigurationManagement;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.configuration.PlatformConfigurationService;
import org.ikasan.topology.model.*;
import org.vaadin.teemu.VaadinIcons;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Ikasan Development Team on 20/12/2016.
 */
public class FlowComponentsConfigurationUploadDownloadWindow extends Window
{
    private Logger logger = LoggerFactory.getLogger(FlowComponentsConfigurationUploadDownloadWindow.class);

    private ConfigurationManagement<ConfiguredResource, Configuration> configurationService;
    private FlowConfigurationExportHelper flowConfigurationExportHelper;
    private GridLayout layout;
    private Flow flow;
    private FlowConfigurationImportWindow flowConfigurationImportWindow;
    private PlatformConfigurationService platformConfigurationService = null;

    public FlowComponentsConfigurationUploadDownloadWindow(ConfigurationManagement<ConfiguredResource, Configuration> configurationService,
                                                           FlowConfigurationExportHelper flowConfigurationExportHelper, FlowConfigurationImportWindow flowConfigurationImportWindow,
                                                           PlatformConfigurationService platformConfigurationService)
    {
        this.configurationService = configurationService;
        if(this.configurationService == null)
        {
            throw new IllegalArgumentException("configurationService cannot be null!");
        }
        this.flowConfigurationExportHelper = flowConfigurationExportHelper;
        if(this.flowConfigurationExportHelper == null)
        {
            throw new IllegalArgumentException("flowConfigurationExportHelper cannot be null!");
        }
        this.flowConfigurationImportWindow = flowConfigurationImportWindow;
        if(this.flowConfigurationImportWindow == null)
        {
            throw new IllegalArgumentException("flowConfigurationImportWindow cannot be null!");
        }
        this.platformConfigurationService = platformConfigurationService;
        if(this.platformConfigurationService == null)
        {
            throw new IllegalArgumentException("platformConfigurationService cannot be null!");
        }
        init();
    }

    private void init()
    {
        setModal(true);
        setHeight("150px");
        setWidth("300px");
        this.layout = new GridLayout(1, 2);

        this.layout.setSpacing(true);
        this.layout.setWidth("95%");
        this.layout.setMargin(true);
        this.setContent(this.layout);
    }

    public void populate(final Flow flow)
    {
        this.flow = flow;

        this.layout.removeAllComponents();

        Label configurationParametersLabel = new Label("Flow Configured Resources Import/Export");
        configurationParametersLabel.setStyleName(ValoTheme.LABEL_HUGE);
        this.layout.addComponent(configurationParametersLabel, 0, 0);

        Button exportMappingConfigurationButton = new Button();
        exportMappingConfigurationButton.setIcon(VaadinIcons.DOWNLOAD_ALT);
        exportMappingConfigurationButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
        exportMappingConfigurationButton.setDescription("Export the current flow component configurations");
        exportMappingConfigurationButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);

        FileDownloader fd = new FileDownloader(new ConnectorResource() {
            private final String filename = String.format("flowConfigurationExport-%s.xml", new SimpleDateFormat("yyyy-MM-dd-HH-mm").format(new Date()));

            @Override
            public String getFilename() {
                return filename;
            }

            @Override
            public String getMIMEType() {
                return com.google.common.net.MediaType.ZIP.toString();
            }

            @Override
            public DownloadStream getStream() {
                try {
                    // generate data .... => inputstream
                    InputStream data  = new ByteArrayInputStream(getFlowConfigurationExport(flow).toByteArray());
                    final DownloadStream stream = new DownloadStream(data, getMIMEType(), filename);
                    stream.setParameter("Content-Disposition", "attachment;filename=" + filename);
                    // This magic incantation should prevent anyone from caching the data
                    stream.setParameter("Cache-Control", "private,no-cache,no-store");
                    // In theory <=0 disables caching. In practice Chrome, Safari (and, apparently, IE) all ignore <=0. Set to 1s
                    stream.setCacheTime(1000);
                    return stream;
                } catch (final IOException e) {
                    logger.error("Can't download " + filename, e);
                }
                return null;
            }
        });
        fd.extend(exportMappingConfigurationButton);

        Button importMappingConfigurationButton = new Button();

        importMappingConfigurationButton.setIcon(VaadinIcons.UPLOAD_ALT);
        importMappingConfigurationButton.setDescription("Import flow component configurations");
        importMappingConfigurationButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
        importMappingConfigurationButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        importMappingConfigurationButton.addClickListener(new Button.ClickListener()
        {
            public void buttonClick(Button.ClickEvent event)
            {
                flowConfigurationImportWindow.setFlow(flow);
                UI.getCurrent().addWindow(flowConfigurationImportWindow);
            }
        });

        HorizontalLayout uploadDownloadLayout = new HorizontalLayout();
        uploadDownloadLayout.setSpacing(true);
        uploadDownloadLayout.setWidth("100px");

        uploadDownloadLayout.addComponent(exportMappingConfigurationButton);
        uploadDownloadLayout.addComponent(importMappingConfigurationButton);

        this.layout.addComponent(uploadDownloadLayout, 0 ,1);
        this.layout.setComponentAlignment(uploadDownloadLayout, Alignment.MIDDLE_CENTER);
    }

    /**
     * Helper method to get the ByteArrayOutputStream associated with the export.
     *
     * @return
     * @throws IOException
     */
    private ByteArrayOutputStream getFlowConfigurationExport(final Flow flow) throws IOException
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

		String schemaLocation = (String)this.platformConfigurationService.getConfigurationValue("flowConfigurationSchemaLocation");

        if(schemaLocation == null || schemaLocation.length() == 0)
        {
            throw new RuntimeException("Cannot resolve the platform configuration flowConfigurationSchemaLocation!");
        }

		logger.debug("Resolved schemaLocation " + schemaLocation);

        this.flowConfigurationExportHelper.setSchemaLocation(schemaLocation);

        String exportXml = this.flowConfigurationExportHelper.getFlowConfigurationExportXml(flow);

        out.write(XmlFormatter.format(exportXml).getBytes());

        return out;
    }

}
