package org.ikasan.dashboard.ui.topology.window;

import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ikasan.configurationService.util.ModuleConfigurationImportHelper;
import org.ikasan.dashboard.ui.framework.util.DocumentValidator;
import org.ikasan.dashboard.ui.framework.util.SchemaValidationErrorHandler;
import org.ikasan.dashboard.ui.mappingconfiguration.util.MappingConfigurationImportException;
import org.ikasan.spec.configuration.Configuration;
import org.ikasan.spec.configuration.ConfigurationManagement;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.topology.model.Module;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Ikasan Development Team on 20/12/2016.
 */
public class ModuleConfigurationImportWindow extends Window
{
    private Logger logger = LoggerFactory.getLogger(ModuleConfigurationImportWindow.class);

    private ModuleConfigurationImportWindow.FileUploader receiver = new ModuleConfigurationImportWindow.FileUploader();
    private HorizontalLayout progressLayout = new HorizontalLayout();
    private Label uploadLabel = new Label();
    private ConfigurationManagement<ConfiguredResource, Configuration> configurationService;
    private Module module;

    private ModuleConfigurationImportHelper moduleImportHelper = null;

    public ModuleConfigurationImportWindow(ConfigurationManagement<ConfiguredResource, Configuration> configurationManagement,
                                           ModuleConfigurationImportHelper moduleImportHelper)
    {
        this.configurationService = configurationManagement;
        if(this.configurationService == null)
        {
            throw new IllegalArgumentException("configurationService cannot be null!");
        }
        this.moduleImportHelper = moduleImportHelper;
        if(this.moduleImportHelper == null)
        {
            throw new IllegalArgumentException("moduleImportHelper cannot be null!");
        }
        init();
    }

    /**
     * Helper method to initialise this object.
     */
    protected void init()
    {
        this.setModal(true);
        super.setHeight(40.0f, Unit.PERCENTAGE);
        super.setWidth(40.0f, Unit.PERCENTAGE);
        super.center();
        super.setStyleName("ikasan");

        progressLayout.setSpacing(true);
        progressLayout.setVisible(false);
        progressLayout.addComponent(uploadLabel);

        final Upload upload = new Upload("", receiver);
        upload.addSucceededListener(receiver);

        upload.addFinishedListener(new Upload.FinishedListener() {
            public void uploadFinished(Upload.FinishedEvent event) {
                upload.setVisible(false);
                try
                {
                    parseUploadFile();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    Notification.show("Caught exception trying to import a Module Configuration!\n", e.getMessage()
                            , Notification.Type.ERROR_MESSAGE);
                }
            }
        });

        final Button importButton = new Button("Import");
        importButton.addClickListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                upload.interruptUpload();
            }
        });

        upload.addStartedListener(new Upload.StartedListener() {
            public void uploadStarted(Upload.StartedEvent event) {
                // This method gets called immediately after upload is started
                upload.setVisible(false);
            }
        });

        upload.addProgressListener(new Upload.ProgressListener() {
            public void updateProgress(long readBytes, long contentLength) {
                // This method gets called several times during the update

            }

        });

        importButton.setStyleName(ValoTheme.BUTTON_SMALL);
        importButton.addClickListener(new Button.ClickListener()
        {
            public void buttonClick(Button.ClickEvent event)
            {
                moduleImportHelper.save();
                progressLayout.setVisible(false);
                upload.setVisible(true);
                close();

                Notification.show("Module component configurations successfully imported!"
                        , Notification.Type.HUMANIZED_MESSAGE);
            }
        });

        progressLayout.addComponent(importButton);

        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        layout.addComponent(new Label("Select file to upload module configuration."));
        layout.addComponent(upload);

        layout.addComponent(progressLayout);

        Button cancelButton = new Button("Cancel");
        cancelButton.setStyleName(ValoTheme.BUTTON_SMALL);

        cancelButton.addClickListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                progressLayout.setVisible(false);
                upload.setVisible(true);
                close();
            }
        });

        HorizontalLayout hlayout = new HorizontalLayout();
        hlayout.addComponent(cancelButton);

        layout.addComponent(hlayout);

        super.setContent(layout);
    }

     /**
     * Inner class to help with file uploads.
     */
    class FileUploader implements Upload.Receiver, Upload.SucceededListener
    {
        private static final long serialVersionUID = 5176770080834995716L;

        public ByteArrayOutputStream file;

        public OutputStream receiveUpload(String filename,
                                          String mimeType) {
            this.file = new ByteArrayOutputStream();
            return file;// Return the output stream to write to
        }

        public void uploadSucceeded(Upload.SucceededEvent event) {
            logger.debug("File = " + new String(file.toByteArray()));
        }
    };

    /**
     * Helper method to parse the upload file.
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     * @throws XPathExpressionException
     */
    protected void parseUploadFile() throws XPathExpressionException, SAXException, IOException, ParserConfigurationException
    {

         SchemaValidationErrorHandler errorHandler = DocumentValidator.validateUploadedDocument(receiver.file.toByteArray());

         if(errorHandler.isInError())
         {
             StringBuffer errors = new StringBuffer();

             for(SAXParseException exception: errorHandler.getErrors())
             {
                 errors.append(exception.getMessage()).append("\n");
             }

             for(SAXParseException exception: errorHandler.getFatal())
             {
                 errors.append(exception.getMessage()).append("\n");
             }

             Notification.show("An error occured parsing the uploaded XML document!\n", errors.toString()
                 , Notification.Type.ERROR_MESSAGE);
         }
         else
         {
             moduleImportHelper.updateModuleConfiguration(module, receiver.file.toByteArray());

             this.uploadLabel.setValue("Importing module configuration"
                    + ". Press import to proceed.");
             progressLayout.setVisible(true);
             
         }

    }

    public void setModule(Module module)
    {
        this.module = module;
    }
}
