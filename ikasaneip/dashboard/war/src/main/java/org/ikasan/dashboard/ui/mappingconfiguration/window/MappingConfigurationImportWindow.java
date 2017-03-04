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
package org.ikasan.dashboard.ui.mappingconfiguration.window;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;
import org.ikasan.dashboard.ui.framework.util.DashboardSessionValueConstants;
import org.ikasan.dashboard.ui.framework.util.DocumentValidator;
import org.ikasan.dashboard.ui.framework.util.SchemaValidationErrorHandler;
import org.ikasan.dashboard.ui.mappingconfiguration.component.MappingConfigurationConfigurationValuesTable;
import org.ikasan.dashboard.ui.mappingconfiguration.model.MappingConfigurationValue;
import org.ikasan.dashboard.ui.mappingconfiguration.panel.MappingConfigurationPanel;
import org.ikasan.dashboard.ui.mappingconfiguration.util.MappingConfigurationConstants;
import org.ikasan.dashboard.ui.mappingconfiguration.util.MappingConfigurationDocumentHelper;
import org.ikasan.dashboard.ui.mappingconfiguration.util.MappingConfigurationImportException;
import org.ikasan.mapping.model.*;
import org.ikasan.mapping.service.MappingConfigurationService;
import org.ikasan.mapping.service.MappingConfigurationServiceException;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.ikasan.systemevent.service.SystemEventService;
import org.springframework.dao.DataIntegrityViolationException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.vaadin.server.VaadinService;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FinishedEvent;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.StartedEvent;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

/**
 * @author Ikasan Development Team
 *
 */
public class MappingConfigurationImportWindow extends Window
{
    private static final long serialVersionUID = 4798260539109852939L;

    private Logger logger = Logger.getLogger(MappingConfigurationImportWindow.class);

    private MappingConfigurationService mappingConfigurationService;
    private MappingConfiguration mappingConfiguration;
    private List<MappingConfigurationValue> mappingConfigurationValues;

    private FileUploader receiver = new FileUploader();
    private HorizontalLayout progressLayout = new HorizontalLayout();
    private Label uploadLabel = new Label();
    private MappingConfigurationConfigurationValuesTable mappingConfigurationConfigurationValuesTable;
    private MappingConfigurationPanel mappingConfigurationPanel;
    private List<KeyLocationQuery> keyLocationQueries;
    private SystemEventService systemEventService;
    
    /**
     * Constructor
     * 
     * @param mappingConfigurationService
     * @param mappingConfiguration
     * @param mappingConfigurationConfigurationValuesTable
     */
    public MappingConfigurationImportWindow(MappingConfigurationService mappingConfigurationService,
            MappingConfigurationConfigurationValuesTable mappingConfigurationConfigurationValuesTable,
            MappingConfigurationPanel mappingConfigurationPanel, SystemEventService systemEventService)
    {
        this.mappingConfigurationService = mappingConfigurationService;
        this.mappingConfigurationConfigurationValuesTable = mappingConfigurationConfigurationValuesTable;
        this.mappingConfigurationPanel = mappingConfigurationPanel;
        this.systemEventService = systemEventService;
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
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    e.printStackTrace(pw);
                    
                    Notification.show("Caught exception trying to import a Mapping Configuration!\n", sw.toString()
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
        importButton.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                try
                {
                    saveImportedMappingConfiguration();
                    progressLayout.setVisible(false);
                    upload.setVisible(true);
                    
                    IkasanAuthentication authentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
                        	.getAttribute(DashboardSessionValueConstants.USER);

                    systemEventService.logSystemEvent(MappingConfigurationConstants.MAPPING_CONFIGURATION_SERVICE, 
                    		"Imported mapping configuration: [Client=" + mappingConfiguration.getConfigurationServiceClient().getName()
                    		+"] [Source Context=" + mappingConfiguration.getSourceContext().getName() + "] [Target Context=" 
                    		+ mappingConfiguration.getTargetContext().getName() + "] [Type=" + mappingConfiguration.getConfigurationType().getName()
                    		+ "]", authentication.getName());

                    mappingConfiguration = null;
                }
                catch (MappingConfigurationServiceException e)
                {
                    if(e.getCause() instanceof DataIntegrityViolationException)
                    {
                        Notification.show("Caught exception trying to save an imported Mapping Configuration!", "This is due to the fact " +
                        		"that, combined, the client, type, source and target context values are unique for a Mapping Configuration. The values" +
                        		" that are being imported and not unique." 
                            , Notification.Type.ERROR_MESSAGE);
                    }
                    else
                    {
                        StringWriter sw = new StringWriter();
                        PrintWriter pw = new PrintWriter(sw);
                        e.printStackTrace(pw);
                        
                        Notification.show("Caught exception trying to save an imported Mapping Configuration!\n", sw.toString()
                            , Notification.Type.ERROR_MESSAGE);
                    }
                }

                close();
            }
        });

        progressLayout.addComponent(importButton);
        
        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        layout.addComponent(new Label("Select file to upload mapping configurations."));
        layout.addComponent(upload);

        layout.addComponent(progressLayout);


        Button cancelButton = new Button("Cancel");
        cancelButton.setStyleName(ValoTheme.BUTTON_SMALL);

        cancelButton.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                mappingConfiguration = null;
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
            MappingConfigurationDocumentHelper helper = new MappingConfigurationDocumentHelper();

            try
            {
                this.mappingConfiguration = helper.getMappingConfiguration(receiver.file.toByteArray());
            }
            catch(MappingConfigurationImportException e)
            {
                Notification.show("An error has occurred importing a mapping configuration!\n",
                        e.getMessage(),
                        Notification.Type.ERROR_MESSAGE);

                return;
            }

            this.mappingConfigurationValues = helper.getMappingConfigurationValues(receiver.file.toByteArray(), mappingConfiguration.getIsManyToMany());

            if(!mappingConfiguration.getIsManyToMany())
            {
                this.keyLocationQueries = helper.getKeyLocationQueries(receiver.file.toByteArray());
            }
    
            this.uploadLabel.setValue("Importing " + mappingConfigurationValues.size()
                + " configuration values. Press import to procede.");
            progressLayout.setVisible(true);
        }

    }

    /**
     * Method to save the imported mapping configuration values.
     * 
     * @throws MappingConfigurationServiceException
     */
    protected void saveImportedMappingConfiguration() throws MappingConfigurationServiceException
    {
        ConfigurationServiceClient client = this.mappingConfiguration.getConfigurationServiceClient();
        StringBuffer errorMessage = new StringBuffer();
        
        client = this.mappingConfigurationService.getAllConfigurationClientByName(client.getName());
        if(client == null)
        {
            errorMessage.append("No matching configuration client found.\n");
        }
        else
        {
            this.mappingConfiguration.setConfigurationServiceClient(client);
        }

        ConfigurationType type = this.mappingConfiguration.getConfigurationType();
        type = this.mappingConfigurationService.getAllConfigurationTypeByName(type.getName());

        if(type == null)
        {
            errorMessage.append("No matching configuration type found.\n");
        }
        else
        {
            this.mappingConfiguration.setConfigurationType(type);
        }

        ConfigurationContext sourceContext = this.mappingConfiguration.getSourceContext();
        sourceContext = this.mappingConfigurationService.getAllConfigurationContextByName(sourceContext.getName());
        if(sourceContext == null)
        {
            errorMessage.append("No source configuration context found.\n");
        }
        else
        {
            this.mappingConfiguration.setSourceContext(sourceContext);
        }

        ConfigurationContext targetContext = this.mappingConfiguration.getTargetContext();
        targetContext = this.mappingConfigurationService.getAllConfigurationContextByName(targetContext.getName());
        if(targetContext == null)
        {
            errorMessage.append("No target configuration context found.\n");
        }
        else
        {
            this.mappingConfiguration.setTargetContext(targetContext);
        }

        if(errorMessage.length() > 0)
        {            
            Notification.show("Caught exception trying to import a Mapping Configuration!\n", errorMessage.toString()
                , Notification.Type.ERROR_MESSAGE);
        }
        else
        {
            Long id = null;
            try
            {
                id = this.mappingConfigurationService.saveMappingConfiguration(this.mappingConfiguration);
            }
            catch(Exception e)
            {
                Notification.show("An error has occurred imporint a mapping configuration!\n",
                        " It appears that the mapping configuration you are importing already exists.",
                        Notification.Type.ERROR_MESSAGE);

                return;
            }

            ArrayList<ManyToManyTargetConfigurationValue> manyToManyTargetConfigurationValues = new ArrayList<ManyToManyTargetConfigurationValue>();

            if(mappingConfiguration.getIsManyToMany())
            {
                for (MappingConfigurationValue mappingConfigurationValue : this.mappingConfigurationValues)
                {
                    Long  sourceConfigurationGroupId = this.mappingConfigurationService.getNextSequenceNumber();

                    for (SourceConfigurationValue value : mappingConfigurationValue.getSourceConfigurationValues())
                    {
                        logger.debug("Source value: " + value);
                        value.setMappingConfigurationId(id);
                        value.setSourceConfigGroupId(sourceConfigurationGroupId);
                    }

                    for(ManyToManyTargetConfigurationValue value: mappingConfigurationValue.getTargetConfigurationValues())
                    {
                        value.setGroupId(sourceConfigurationGroupId);

                        manyToManyTargetConfigurationValues.add(value);
                    }

                    this.mappingConfiguration.getSourceConfigurationValues().addAll(mappingConfigurationValue.getSourceConfigurationValues());


                }
            }
            else
            {
                for (MappingConfigurationValue mappingConfigurationValue : this.mappingConfigurationValues)
                {
                    this.mappingConfigurationService.saveTargetConfigurationValue(mappingConfigurationValue.getTargetConfigurationValue());

                    Long sourceConfigurationGroupId = null;

                    if (this.mappingConfiguration.getNumberOfParams() > 1)
                    {
                        sourceConfigurationGroupId = this.mappingConfigurationService.getNextSequenceNumber();
                    }

                    for (SourceConfigurationValue value : mappingConfigurationValue.getSourceConfigurationValues())
                    {
                        logger.debug("Source value: " + value);
                        value.setMappingConfigurationId(id);
                        value.setSourceConfigGroupId(sourceConfigurationGroupId);
                    }

                    this.mappingConfiguration.getSourceConfigurationValues().addAll(mappingConfigurationValue.getSourceConfigurationValues());
                }

                for(KeyLocationQuery query: this.keyLocationQueries)
                {
                    query.setMappingConfigurationId(id);

                    this.mappingConfigurationService.saveKeyLocationQuery(query);
                }
            }

            this.mappingConfigurationService.saveMappingConfiguration(this.mappingConfiguration);

            mappingConfiguration = this.mappingConfigurationService.getMappingConfigurationById(id);

            if(mappingConfiguration.getIsManyToMany())
            {
                for(ManyToManyTargetConfigurationValue value: manyToManyTargetConfigurationValues)
                {
                    this.mappingConfigurationService.storeManyToManyTargetConfigurationValue(value);
                }
            }

            UI.getCurrent().getNavigator().navigateTo("existingMappingConfigurationPanel");

            this.mappingConfigurationPanel.setMappingConfiguration(this.mappingConfiguration);
            this.mappingConfigurationPanel.populateMappingConfigurationForm();

            this.mappingConfigurationConfigurationValuesTable.populateTable(mappingConfiguration);
        }
    }

    /**
     * Helper method to return a composite mapping configuration value.
     * 
     * @param mappingConfigurationValue
     * @return
     */
    protected MappingConfigurationValue getMappingConfigurationValue(Element mappingConfigurationValue)
    {
        TargetConfigurationValue targetConfigurationValue = getTargetConfigurationValue
                (mappingConfigurationValue.getElementsByTagName("targetConfigurationValue").item(0));

        ArrayList<SourceConfigurationValue> sourceConfigurationValues = getSourceConfigurationValues(mappingConfigurationValue
            .getElementsByTagName("sourceConfigurationValue"));

        for(SourceConfigurationValue sourceConfigurationValue: sourceConfigurationValues)
        {
            logger.debug("Source value: " + sourceConfigurationValue.getSourceSystemValue());
            sourceConfigurationValue.setTargetConfigurationValue(targetConfigurationValue);
            sourceConfigurationValue.setMappingConfigurationId(this.mappingConfiguration.getId());
        }

        return new MappingConfigurationValue(targetConfigurationValue, sourceConfigurationValues);
    }

    /**
     * Gets a list of source configuration values from an XML node list.
     * @param sourceConfigurationValues
     * @return
     */
    protected ArrayList<SourceConfigurationValue> getSourceConfigurationValues(NodeList sourceConfigurationValues)
    {
        ArrayList<SourceConfigurationValue> returnValue = new ArrayList<SourceConfigurationValue>();

        Long sourceConfigurationGroupId = null;
        
        if(this.mappingConfiguration.getNumberOfParams() > 1)
        {
            sourceConfigurationGroupId = this.mappingConfigurationService.getNextSequenceNumber();
        }

        for(int i=0; i<sourceConfigurationValues.getLength(); i++)
        {
            logger.debug("Source value: " + sourceConfigurationValues.item(i).getTextContent());
            SourceConfigurationValue value = new SourceConfigurationValue();
            value.setSourceSystemValue(sourceConfigurationValues.item(i).getTextContent());
            value.setSourceConfigGroupId(sourceConfigurationGroupId);

            returnValue.add(value);
        }

        return returnValue;
    }

    /**
     * Gets a target configuration value from an XML node.
     * @param targetConfigurationValue
     * @return
     */
    protected TargetConfigurationValue getTargetConfigurationValue(Node targetConfigurationValue)
    {
        logger.debug("Target value: " + targetConfigurationValue.getTextContent());
        TargetConfigurationValue value = new TargetConfigurationValue();
        value.setTargetSystemValue(targetConfigurationValue.getTextContent());
        return value;
    }
}
