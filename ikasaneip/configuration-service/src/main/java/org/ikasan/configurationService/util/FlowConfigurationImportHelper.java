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
package org.ikasan.configurationService.util;

import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.ikasan.spec.configuration.Configuration;
import org.ikasan.spec.configuration.ConfigurationManagement;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.topology.model.Component;
import org.ikasan.topology.model.Flow;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Ikasan Development Team
 *
 */
public class FlowConfigurationImportHelper extends ConfigurationHelper
{
    private static Logger logger = LoggerFactory.getLogger(FlowConfigurationImportHelper.class);

    public static final String NAME_XPATH = "/flowConfiguration/name";

    public static final String CONFIGURATION_ID_NAME_XPATH = "/componentConfiguration/id";

    public static final String NAME = "name";

    private ArrayList<Configuration> configurations = null;

    public FlowConfigurationImportHelper(ConfigurationManagement<ConfiguredResource, Configuration> configurationService,
                                         ConfigurationCreationHelper helper)
    {
        super(configurationService, helper);
    }

    /**
     *
     * @param flow
     * @param fileContents
     * @return
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws XPathExpressionException
     */
    public void updateFlowConfiguration(Flow flow, byte[] fileContents) throws SAXException
        , IOException, ParserConfigurationException, XPathExpressionException
    {
        DocumentBuilderFactory builderFactory =
                DocumentBuilderFactory.newInstance();

        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        
        Document document = builder.parse(new ByteArrayInputStream(fileContents));

        this.updateFlowConfiguration(flow, document);
    }

    /**
     *
     * @param flow
     * @param document
     * @return
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws XPathExpressionException
     */
    public void updateFlowConfiguration(Flow flow, Document document) throws SAXException
            , IOException, ParserConfigurationException, XPathExpressionException
    {
        String name = this.getValueFromDocument(document, NAME_XPATH);

        this.errorMessage = new StringBuffer();

        if(name == null || name.isEmpty())
        {
            errorMessage.append("Flow Configuration name is missing!\n");
        }
        else if (!name.equals(flow.getName()))
        {
            errorMessage.append("Flow name is not equal! Expected ["
                    + flow.getName() + "] Received [" + name + "]\n");
        }

        Element documentRoot = document.getDocumentElement();

        NodeList componentConfigurations = documentRoot.getElementsByTagName("componentConfiguration");

        Node componentConfiguration = null;

        configurations = new ArrayList<Configuration>();

        logger.info("Attempting to update flow component configurations: " + componentConfigurations.getLength());

        for(int i=0; i < componentConfigurations.getLength(); i++)
        {
            componentConfiguration = componentConfigurations.item(i);

            Document componentConfigurationDocument = this.getSubDocument(componentConfiguration);

            String configurationId = this.getValueFromDocument(componentConfigurationDocument, CONFIGURATION_ID_NAME_XPATH);

            logger.info("Dealing wtih configuration whose configured resource id is: " + configurationId);

            Component component = this.confirmFlowConfiguredComponentExists(flow,configurationId);

            logger.info("Component instance: " + component);

            if(component == null)
            {
                continue;
            }

            Configuration configuration = this.configurationService.getConfiguration(configurationId);

            if(configuration == null)
            {
                configuration = helper.createConfiguration(component);
            }

            logger.info("Updating configuration instance: " + configuration);

            ComponentConfigurationImportHelper importHelper = new ComponentConfigurationImportHelper();

            importHelper.updateComponentConfiguration(configuration, componentConfigurationDocument);

            if(!importHelper.getErrorMessage().isEmpty())
            {
                this.errorMessage.append(importHelper.getErrorMessage() + "\n");
            }

            configurations.add(configuration);
        }

        if(this.errorMessage.length() > 0)
        {
            logger.error("Error importing flow configuration: " + errorMessage.toString());
            throw new RuntimeException(errorMessage.toString());
        }
    }

    public void save()
    {
        for(Configuration configuration: configurations)
        {
            logger.info("Saving configuration: " + configuration);
            this.configurationService.saveConfiguration(configuration);
        }
    }
}
