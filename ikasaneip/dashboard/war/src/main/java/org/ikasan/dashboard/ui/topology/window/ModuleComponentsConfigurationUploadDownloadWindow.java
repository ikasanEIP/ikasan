package org.ikasan.dashboard.ui.topology.window;

import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.apache.log4j.Logger;
import org.ikasan.configurationService.util.ModuleConfigurationExportHelper;
import org.ikasan.dashboard.ui.framework.util.XmlFormatter;
import org.ikasan.spec.configuration.Configuration;
import org.ikasan.spec.configuration.ConfigurationManagement;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.configuration.PlatformConfigurationService;
import org.ikasan.topology.model.Flow;
import org.ikasan.topology.model.Module;
import org.vaadin.teemu.VaadinIcons;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ikasan Development Team on 20/12/2016.
 */
public class ModuleComponentsConfigurationUploadDownloadWindow extends Window
{
    private Logger logger = Logger.getLogger(ModuleComponentsConfigurationUploadDownloadWindow.class);

    private ConfigurationManagement<ConfiguredResource, Configuration> configurationService;
    private GridLayout layout;
    private Module module;
    private ModuleConfigurationImportWindow moduleConfigurationImportWindow;
    private ModuleConfigurationExportHelper exportHelper;
    private PlatformConfigurationService platformConfigurationService = null;

    public ModuleComponentsConfigurationUploadDownloadWindow(ConfigurationManagement<ConfiguredResource, Configuration> configurationService,
                                                             ModuleConfigurationImportWindow moduleConfigurationImportWindow, ModuleConfigurationExportHelper exportHelper,
                                                             PlatformConfigurationService platformConfigurationService)
    {
        this.configurationService = configurationService;
        if(this.configurationService == null)
        {
            throw new IllegalArgumentException("configurationService cannot be null!");
        }
        this.moduleConfigurationImportWindow = moduleConfigurationImportWindow;
        if(this.moduleConfigurationImportWindow == null)
        {
            throw new IllegalArgumentException("moduleConfigurationImportWindow cannot be null!");
        }
        this.exportHelper = exportHelper;
        if(this.exportHelper == null)
        {
            throw new IllegalArgumentException("exportHelper cannot be null!");
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


    public void populate(Module module)
    {
        this.module = module;

        this.layout.removeAllComponents();

        Label configurationParametersLabel = new Label("Module Configured Resources Import/Export");
        configurationParametersLabel.setStyleName(ValoTheme.LABEL_HUGE);
        this.layout.addComponent(configurationParametersLabel, 0, 0);

        Button exportMappingConfigurationButton = new Button();
        exportMappingConfigurationButton.setIcon(VaadinIcons.DOWNLOAD_ALT);
        exportMappingConfigurationButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
        exportMappingConfigurationButton.setDescription("Export the current component configuration");
        exportMappingConfigurationButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);

        FileDownloader fd = new FileDownloader(this.getModuleConfigurationExportStream(module));
        fd.extend(exportMappingConfigurationButton);

        Button importMappingConfigurationButton = new Button();

        importMappingConfigurationButton.setIcon(VaadinIcons.UPLOAD_ALT);
        importMappingConfigurationButton.setDescription("Import a component configuration");
        importMappingConfigurationButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
        importMappingConfigurationButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        importMappingConfigurationButton.addClickListener(new Button.ClickListener()
        {
            public void buttonClick(Button.ClickEvent event)
            {
                moduleConfigurationImportWindow.setModule(ModuleComponentsConfigurationUploadDownloadWindow.this.module);
                UI.getCurrent().addWindow(moduleConfigurationImportWindow);
            }
        });

        HorizontalLayout uploadDownloadLayout = new HorizontalLayout();
        uploadDownloadLayout.setSpacing(true);
        uploadDownloadLayout.setWidth("100px");

        uploadDownloadLayout.addComponent(exportMappingConfigurationButton);
        uploadDownloadLayout.addComponent(importMappingConfigurationButton);

        this.layout.addComponent(uploadDownloadLayout, 0 ,1 );
        this.layout.setComponentAlignment(uploadDownloadLayout, Alignment.MIDDLE_CENTER);
    }

    /**
     * Helper method to get the stream associated with the export of the file.
     *
     * @return the StreamResource associated with the export.
     */
    private StreamResource getModuleConfigurationExportStream(final Module module)
    {
        StreamResource.StreamSource source = new StreamResource.StreamSource()
        {

            public InputStream getStream() {
                ByteArrayOutputStream stream = null;
                try
                {
                    stream = getModuleConfigurationExport(module);
                }
                catch (IOException e)
                {
                    logger.error(e.getMessage(), e);
                }
                InputStream input = new ByteArrayInputStream(stream.toByteArray());
                return input;

            }
        };
        StreamResource resource = new StreamResource ( source,"moduleConfigurationExport1.xml");
        return resource;
    }

    /**
     * Helper method to get the ByteArrayOutputStream associated with the export.
     *
     * @return
     * @throws IOException
     */
    private ByteArrayOutputStream getModuleConfigurationExport(final Module module) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

		String schemaLocation = (String)this.platformConfigurationService.getConfigurationValue("moduleConfigurationSchemaLocation");

		if(schemaLocation == null || schemaLocation.length() == 0)
		{
			throw new RuntimeException("Cannot resolve the platform configuration mappingExportSchemaLocation!");
		}

		logger.debug("Resolved schemaLocation " + schemaLocation);

        this.exportHelper.setSchemaLocation(schemaLocation);

        String exportXml = exportHelper.getModuleConfigurationExportXml(this.module);

        out.write(XmlFormatter.format(exportXml).getBytes());

        return out;
    }
}
