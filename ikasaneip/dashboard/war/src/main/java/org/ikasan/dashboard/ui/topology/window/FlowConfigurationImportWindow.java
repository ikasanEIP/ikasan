package org.ikasan.dashboard.ui.topology.window;

import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.apache.log4j.Logger;
import org.ikasan.dashboard.configurationManagement.util.ComponentConfigurationImportHelper;
import org.ikasan.dashboard.ui.mappingconfiguration.util.MappingConfigurationImportException;
import org.ikasan.spec.configuration.Configuration;
import org.ikasan.spec.configuration.ConfigurationManagement;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.topology.model.Flow;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * Created by stewmi on 20/12/2016.
 */
public class FlowConfigurationImportWindow extends Window
{
    private Logger logger = Logger.getLogger(FlowConfigurationImportWindow.class);

    private FlowConfigurationImportWindow.FileUploader receiver = new FlowConfigurationImportWindow.FileUploader();
    private HorizontalLayout progressLayout = new HorizontalLayout();
    private Label uploadLabel = new Label();

    public FlowConfigurationImportWindow(Flow flow, List<Configuration> configurationList, ConfigurationManagement<ConfiguredResource, Configuration> configurationManagement)
    {
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
                    Notification.show("Caught exception trying to import a Flow Configuration!\n", e.getMessage()
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
                progressLayout.setVisible(false);
                upload.setVisible(true);
                close();
            }
        });

        progressLayout.addComponent(importButton);

        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        layout.addComponent(new Label("Select file to upload flow configuration."));
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
//
//         SchemaValidationErrorHandler errorHandler = DocumentValidator.validateUploadedDocument(receiver.file.toByteArray());
//
//         if(errorHandler.isInError())
//         {
//             StringBuffer errors = new StringBuffer();
//
//             for(SAXParseException exception: errorHandler.getErrors())
//             {
//                 errors.append(exception.getMessage()).append("\n");
//             }
//
//             for(SAXParseException exception: errorHandler.getFatal())
//             {
//                 errors.append(exception.getMessage()).append("\n");
//             }
//
//             Notification.show("An error occured parsing the uploaded XML document!\n", errors.toString()
//                 , Notification.Type.ERROR_MESSAGE);
//         }
//         else
//         {
//        ComponentConfigurationImportHelper helper = new ComponentConfigurationImportHelper();
//
//        try
//        {
//            helper.updateComponentConfiguration(this.configuration, receiver.file.toByteArray());
//        }
//        catch(MappingConfigurationImportException e)
//        {
//            Notification.show("An error has occurred importing a component configuration!\n",
//                    e.getMessage(),
//                    Notification.Type.ERROR_MESSAGE);
//
//            return;
//        }

        this.uploadLabel.setValue("Importing component configuration"
                + ". Press import to proceed.");
        progressLayout.setVisible(true);
//         }

    }
}
