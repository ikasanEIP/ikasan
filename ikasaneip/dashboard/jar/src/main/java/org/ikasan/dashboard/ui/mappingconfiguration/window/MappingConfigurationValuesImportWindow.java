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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ikasan.dashboard.ui.framework.util.DashboardSessionValueConstants;
import org.ikasan.dashboard.ui.mappingconfiguration.component.MappingConfigurationConfigurationValuesTable;
import org.ikasan.dashboard.ui.mappingconfiguration.model.MappingConfigurationValue;
import org.ikasan.dashboard.ui.mappingconfiguration.util.MappingConfigurationConstants;
import org.ikasan.dashboard.ui.mappingconfiguration.util.MappingConfigurationDocumentHelper;
import org.ikasan.mapping.model.*;
import org.ikasan.mapping.service.MappingManagementService;
import org.ikasan.mapping.service.MappingConfigurationServiceException;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.ikasan.systemevent.service.SystemEventService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.vaadin.server.VaadinService;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
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
public class MappingConfigurationValuesImportWindow extends Window
{
    private static final long serialVersionUID = 4798260539109852939L;

    private Logger logger = LoggerFactory.getLogger(MappingConfigurationValuesImportWindow.class);

    private MappingManagementService mappingConfigurationService;
    private MappingConfiguration mappingConfiguration;
    private MappingConfigurationConfigurationValuesTable mappingConfigurationConfigurationValuesTable;

    private FileUploader receiver = new FileUploader();
    private HorizontalLayout progressLayout = new HorizontalLayout();
    private Label uploadLabel = new Label();
    private List<MappingConfigurationValue> mappingConfigurationValues;
    private SystemEventService systemEventService;
    
    /**
     * Constructor
     * 
     * @param mappingConfigurationService
     * @param mappingConfiguration
     * @param mappingConfigurationConfigurationValuesTable
     */
    public MappingConfigurationValuesImportWindow(MappingManagementService mappingConfigurationService
            , MappingConfiguration mappingConfiguration, MappingConfigurationConfigurationValuesTable 
            mappingConfigurationConfigurationValuesTable, SystemEventService systemEventService)
    {
        super();
        this.mappingConfigurationService = mappingConfigurationService;
        this.mappingConfiguration = mappingConfiguration;
        this.mappingConfigurationConfigurationValuesTable = mappingConfigurationConfigurationValuesTable;
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
                parseUploadFile();
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
                try
                {

                    saveImportedMappingConfigurationValues();
                    
                    IkasanAuthentication authentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
                        	.getAttribute(DashboardSessionValueConstants.USER);

                    systemEventService.logSystemEvent(MappingConfigurationConstants.MAPPING_CONFIGURATION_SERVICE, 
                    		"Imported mapping configuration values for mapping configuration: [Client=" + mappingConfiguration.getConfigurationServiceClient().getName()
                    		+"] [Source Context=" + mappingConfiguration.getSourceContext().getName() + "] [Target Context=" 
                    		+ mappingConfiguration.getTargetContext().getName() + "] [Type=" + mappingConfiguration.getConfigurationType().getName()
                    		+ "]", authentication.getName());

                    logger.debug("User: " + authentication.getName() 
                        + " successfully imported the following Mapping Configuration: " 
                            + mappingConfiguration);
                }
                catch (Exception e)
                {
                	logger.error("An error occurred trying to import a mapping configuration!", e); 
                	
                    Notification.show("An error occurred trying to import a mapping configuration: " + e.getMessage()
                    		, Notification.Type.ERROR_MESSAGE);
                }
                mappingConfigurationConfigurationValuesTable.populateTable(mappingConfiguration);
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

        cancelButton.addClickListener(new Button.ClickListener() 
        {
            public void buttonClick(ClickEvent event) 
            {
                close();
            }
        });

        HorizontalLayout hlayout = new HorizontalLayout();
        hlayout.addComponent(cancelButton);

        layout.addComponent(hlayout);
        
        super.setContent(layout);
    }

    /**
     * Helper method to parse the upload file.
     */
    protected void parseUploadFile()
    {
        DocumentBuilderFactory builderFactory =
                DocumentBuilderFactory.newInstance();
        builderFactory.setNamespaceAware(true);
        builderFactory.setValidating(true);
        builderFactory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage", 
                "http://www.w3.org/2001/XMLSchema");

        DocumentBuilder builder = null;

        this.mappingConfigurationValues = new ArrayList<MappingConfigurationValue>();

        try
        {
            builder = builderFactory.newDocumentBuilder();
            SimpleErrorHandler errorHandler = new SimpleErrorHandler();
            builder.setErrorHandler(errorHandler);
            Document document = builder.parse(
                new ByteArrayInputStream(this.receiver.file.toByteArray()));

            logger.debug("Uploaded document = " + document);

            logger.debug("Document element = " + document.getDocumentElement().getNodeName());

            if(errorHandler.errors.size() > 0 || errorHandler.fatal.size() > 0)
            {
                StringBuffer errors = new StringBuffer();

                for(SAXParseException exception: errorHandler.errors)
                {
                    errors.append(exception.getMessage()).append("\n");
                }

                for(SAXParseException exception: errorHandler.fatal)
                {
                    errors.append(exception.getMessage()).append("\n");
                }

                logger.error("An error occured parsing the uploaded XML document!\n" + errors.toString()); 
                
                Notification.show("An error occured parsing the uploaded XML document!\n", errors.toString()
                    , Notification.Type.ERROR_MESSAGE);
            }
            else
            {
                Element documentRoot = document.getDocumentElement();
    
                NodeList mappingConfigurationValues = documentRoot.getElementsByTagName("mappingConfigurationValue");
                
                logger.debug("Number of mapping configuration values = " + mappingConfigurationValues.getLength());
    
                this.uploadLabel.setValue("Importing " + mappingConfigurationValues.getLength() 
                    + " configuration values. Press import to proceed.");
                progressLayout.setVisible(true);

                MappingConfigurationDocumentHelper helper = new MappingConfigurationDocumentHelper();

                this.mappingConfigurationValues = helper.getMappingConfigurationValues(this.receiver.file.toByteArray(), this.mappingConfiguration.getIsManyToMany());

            }

        }
        catch (Exception e)
        {
            Notification.show("An error has occurred attempting to import mapping values:" + e.getMessage(), Notification.Type.ERROR_MESSAGE);
        }
    }

    /**
     * Method to save the imported mapping configuration values.
     * 
     * @throws MappingConfigurationServiceException
     */
    protected void saveImportedMappingConfigurationValues() throws MappingConfigurationServiceException
    {
        if(mappingConfiguration.getIsManyToMany())
        {
            for (MappingConfigurationValue mappingConfigurationValue : this.mappingConfigurationValues)
            {
                Long  sourceConfigurationGroupId = this.mappingConfigurationService.getNextSequenceNumber();

                for (SourceConfigurationValue value : mappingConfigurationValue.getSourceConfigurationValues())
                {
                    logger.debug("Source value: " + value);
                    value.setMappingConfigurationId(mappingConfiguration.getId());
                    value.setSourceConfigGroupId(sourceConfigurationGroupId);
                }

                for(ManyToManyTargetConfigurationValue value: mappingConfigurationValue.getTargetConfigurationValues())
                {
                    value.setGroupId(sourceConfigurationGroupId);

                    this.mappingConfigurationService.storeManyToManyTargetConfigurationValue(value);
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
                    value.setMappingConfigurationId(mappingConfiguration.getId());
                    value.setSourceConfigGroupId(sourceConfigurationGroupId);
                }

                this.mappingConfiguration.getSourceConfigurationValues().addAll(mappingConfigurationValue.getSourceConfigurationValues());
            }
        }

        this.mappingConfiguration.setNumberOfMappings(this.getNumberOfMappings(mappingConfiguration.getSourceConfigurationValues()));

        this.mappingConfigurationService.saveMappingConfiguration(this.mappingConfiguration);
    }

    private int getNumberOfMappings(Set<SourceConfigurationValue> values)
    {
        int num = 0;

        Set<Long> groupKeys = new HashSet<Long>();

        for(SourceConfigurationValue value: values)
        {
            if(value.getSourceConfigGroupId() == null)
            {
                num++;
            }
            else
            {
                groupKeys.add(value.getSourceConfigGroupId());
            }
        }

        if(num > 0)
        {
            return num;
        }
        else
        {
            return groupKeys.size();
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
        TargetConfigurationValue value = new TargetConfigurationValue();
        value.setTargetSystemValue(targetConfigurationValue.getTextContent());
        return value;
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

    public class SimpleErrorHandler implements ErrorHandler 
    {
        List<SAXParseException> warnings = new ArrayList<SAXParseException>();
        List<SAXParseException> errors = new ArrayList<SAXParseException>();
        List<SAXParseException> fatal = new ArrayList<SAXParseException>();
        
        public void warning(SAXParseException e) throws SAXException {
            logger.debug(e.getMessage());
            warnings.add(e);
        }

        public void error(SAXParseException e) throws SAXException {
            logger.debug(e.getMessage());
            errors.add(e);
        }

        public void fatalError(SAXParseException e) throws SAXException {
            logger.debug(e.getMessage());
            fatal.add(e);
        }
    }
}
