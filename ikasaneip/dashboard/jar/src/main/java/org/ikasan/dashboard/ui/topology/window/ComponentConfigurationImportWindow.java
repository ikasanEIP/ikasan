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
package org.ikasan.dashboard.ui.topology.window;

 import com.vaadin.ui.*;
 import com.vaadin.ui.Button.ClickEvent;
 import com.vaadin.ui.Upload.*;
 import com.vaadin.ui.themes.ValoTheme;
 import org.apache.log4j.Logger;
 import org.ikasan.configurationService.util.ComponentConfigurationImportHelper;
 import org.ikasan.dashboard.ui.framework.util.DocumentValidator;
 import org.ikasan.dashboard.ui.framework.util.SchemaValidationErrorHandler;
 import org.ikasan.dashboard.ui.mappingconfiguration.util.MappingConfigurationImportException;
 import org.ikasan.spec.configuration.Configuration;
 import org.xml.sax.SAXException;
 import org.xml.sax.SAXParseException;

 import javax.xml.parsers.ParserConfigurationException;
 import javax.xml.xpath.XPathExpressionException;
 import java.io.*;

 /**
  * @author Ikasan Development Team
  *
  */
 public class ComponentConfigurationImportWindow extends Window
 {
     private static final long serialVersionUID = 4798260539109852939L;

     private Logger logger = Logger.getLogger(ComponentConfigurationImportWindow.class);

     private Configuration configuration;

     private ComponentConfigurationImportWindow.FileUploader receiver = new ComponentConfigurationImportWindow.FileUploader();
     private HorizontalLayout progressLayout = new HorizontalLayout();
     private Label uploadLabel = new Label();

     /**
      * Constructor
      *
      * @param configuration
      */
     public ComponentConfigurationImportWindow(Configuration configuration)
     {
         this.configuration = configuration;
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
             public void uploadFinished(FinishedEvent event) {
                 upload.setVisible(false);
                 try
                 {
                     parseUploadFile();
                 }
                 catch (Exception e)
                 {
                     e.printStackTrace();

                     Notification.show("Caught exception trying to import a Component Configuration!\n", e.getMessage()
                         , Notification.Type.ERROR_MESSAGE);
                 }
             }
         });

         final Button importButton = new Button("Import");
         importButton.addClickListener(new Button.ClickListener() {
             public void buttonClick(ClickEvent event) {
                 upload.interruptUpload();
             }
         });

         upload.addStartedListener(new Upload.StartedListener() {
             public void uploadStarted(StartedEvent event) {
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
             public void buttonClick(ClickEvent event)
             {
                progressLayout.setVisible(false);
                upload.setVisible(true);
                close();

                 Notification.show("Module component configurations successfully imported! Please save congiuration to apply changes."
                         , Notification.Type.HUMANIZED_MESSAGE);
             }
         });

         progressLayout.addComponent(importButton);

         VerticalLayout layout = new VerticalLayout();
         layout.setMargin(true);
         layout.addComponent(new Label("Select file to upload component configuration."));
         layout.addComponent(upload);

         layout.addComponent(progressLayout);

         Button cancelButton = new Button("Cancel");
         cancelButton.setStyleName(ValoTheme.BUTTON_SMALL);

         cancelButton.addClickListener(new Button.ClickListener() {
             public void buttonClick(ClickEvent event) {
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
     class FileUploader implements Receiver, SucceededListener
     {
         private static final long serialVersionUID = 5176770080834995716L;

         public ByteArrayOutputStream file;

         public OutputStream receiveUpload(String filename,
                                           String mimeType) {
             this.file = new ByteArrayOutputStream();
             return file;// Return the output stream to write to
         }

         public void uploadSucceeded(SucceededEvent event) {
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
             ComponentConfigurationImportHelper helper = new ComponentConfigurationImportHelper();

             helper.updateComponentConfiguration(this.configuration, receiver.file.toByteArray());

             this.uploadLabel.setValue("Importing component configuration"
                 + ". Press import to proceed.");
             progressLayout.setVisible(true);
         }

     }
 }
