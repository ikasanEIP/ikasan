package org.ikasan.dashboard.ui.topology.window;

import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.apache.log4j.Logger;
import org.ikasan.configurationService.service.ConfiguredResourceConfigurationService;
import org.ikasan.dashboard.configurationManagement.util.ConfigurationCreationHelper;
import org.ikasan.dashboard.configurationManagement.util.FlowConfigurationExportHelper;
import org.ikasan.spec.configuration.Configuration;
import org.ikasan.spec.configuration.ConfigurationManagement;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.topology.model.*;
import org.vaadin.teemu.VaadinIcons;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by stewmi on 20/12/2016.
 */
public class FlowComponentsConfigurationUploadDownloadWindow extends Window
{
    private Logger logger = Logger.getLogger(FlowComponentsConfigurationUploadDownloadWindow.class);

    private ConfigurationManagement<ConfiguredResource, Configuration> configurationService;
    private GridLayout layout;
    private Flow flow;

    public FlowComponentsConfigurationUploadDownloadWindow(ConfigurationManagement<ConfiguredResource, Configuration> configurationService)
    {
        this.configurationService = configurationService;
        init();
    }

    private void init()
    {
        setModal(true);
        setHeight("300px");
        setWidth("500px");
        this.layout = new GridLayout(2, 2);

        this.layout.setSpacing(true);
        this.layout.setColumnExpandRatio(0, .25f);
        this.layout.setColumnExpandRatio(1, .75f);

        this.layout.setWidth("95%");
        this.layout.setMargin(true);

        this.setContent(this.layout);
    }

    public void populate(Flow flow)
    {
        this.flow = flow;

        this.layout.removeAllComponents();

        Label configurationParametersLabel = new Label("Flow Configured Resources Import/Export");
        configurationParametersLabel.setStyleName(ValoTheme.LABEL_HUGE);
        this.layout.addComponent(configurationParametersLabel, 0, 0);

        Button exportMappingConfigurationButton = new Button();
        exportMappingConfigurationButton.setIcon(VaadinIcons.DOWNLOAD_ALT);
        exportMappingConfigurationButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
        exportMappingConfigurationButton.setDescription("Export the current component configuration");
        exportMappingConfigurationButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);

        FileDownloader fd = new FileDownloader(this.getFlowConfigurationExportStream(flow));
        fd.extend(exportMappingConfigurationButton);

        Button importMappingConfigurationButton = new Button();

        final FlowConfigurationImportWindow flowConfigurationImportWindow
                = new FlowConfigurationImportWindow(this.flow, this.getFlowConfigurations(flow), this.configurationService);

        importMappingConfigurationButton.setIcon(VaadinIcons.UPLOAD_ALT);
        importMappingConfigurationButton.setDescription("Import a component configuration");
        importMappingConfigurationButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
        importMappingConfigurationButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        importMappingConfigurationButton.addClickListener(new Button.ClickListener()
        {
            public void buttonClick(Button.ClickEvent event)
            {
                UI.getCurrent().addWindow(flowConfigurationImportWindow);
            }
        });

        HorizontalLayout uploadDownloadLayout = new HorizontalLayout();
        uploadDownloadLayout.setSpacing(true);
        uploadDownloadLayout.setWidth("100px");

        uploadDownloadLayout.addComponent(exportMappingConfigurationButton);
        uploadDownloadLayout.addComponent(importMappingConfigurationButton);

        this.layout.addComponent(uploadDownloadLayout, 1 ,1);

    }

    /**
     * Helper method to get the stream associated with the export of the file.
     *
     * @return the StreamResource associated with the export.
     */
    private StreamResource getFlowConfigurationExportStream(final Flow flow)
    {
        logger.info("Getting export stream for flow: " + flow.getName() + " with " + flow.getComponents().size() + " components");

        StreamResource.StreamSource source = new StreamResource.StreamSource()
        {

            public InputStream getStream() {
                ByteArrayOutputStream stream = null;
                try
                {
                    stream = getFlowConfigurationExport(flow);
                }
                catch (IOException e)
                {
                    logger.error(e.getMessage(), e);
                }
                InputStream input = new ByteArrayInputStream(stream.toByteArray());
                return input;

            }
        };
        StreamResource resource = new StreamResource ( source,"flowConfigurationExport1.xml");
        return resource;
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

//		String schemaLocation = (String)this.platformConfigurationService.getConfigurationValue("mappingExportSchemaLocation");

//		if(schemaLocation == null || schemaLocation.length() == 0)
//		{
//			throw new RuntimeException("Cannot resolve the platform configuration mappingExportSchemaLocation!");
//		}
//
//		logger.debug("Resolved schemaLocation " + schemaLocation);

        FlowConfigurationExportHelper exportHelper = new FlowConfigurationExportHelper(flow, this.configurationService);

        String exportXml = exportHelper.getFlowConfigurationExportXml();

        out.write(exportXml.getBytes());

        return out;
    }

    protected List<Configuration> getFlowConfigurations(Flow flow)
    {
        List<Configuration> configurations = new ArrayList<Configuration>();

        ConfigurationCreationHelper helper = new ConfigurationCreationHelper(configurationService);

        logger.info("Getting configurations for flow: " + flow.getName() + " with " + flow.getComponents().size() + " components");

        for(org.ikasan.topology.model.Component component: flow.getComponents())
        {
            if(component.isConfigurable() && component.getConfigurationId() != null)
            {
                logger.info("Component is configurable: " + component.getName());

                Configuration configuration = configurationService
                        .getConfiguration(component.getConfigurationId());

                if(configuration == null)
                {
                    logger.info("Creating configuration for component: " + component.getName());
                    configuration = helper.createConfiguration(component);
                }

                configurations.add(configuration);
            }
        }

        return configurations;
    }
}
