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
package org.ikasan.dashboard.ui.mappingconfiguration.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.ikasan.dashboard.ui.mappingconfiguration.model.MappingConfigurationValue;
import org.ikasan.mapping.model.ConfigurationContext;
import org.ikasan.mapping.model.ConfigurationServiceClient;
import org.ikasan.mapping.model.ConfigurationType;
import org.ikasan.mapping.model.KeyLocationQuery;
import org.ikasan.mapping.model.MappingConfiguration;
import org.ikasan.mapping.model.SourceConfigurationValue;
import org.ikasan.mapping.model.TargetConfigurationValue;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * @author Ikasan Development Team
 *
 */
public class MappingConfigurationDocumentHelper
{
    private Logger logger = Logger.getLogger(MappingConfigurationDocumentHelper.class);

    private List<MappingConfigurationValue> mappingConfigurationValues;

    private final XPathFactory xpathFactory = XPathFactory.newInstance();

    /**
     * Helper method to return a composite mapping configuration value.
     * 
     * @param mappingConfigurationValue
     * @return
     * @throws XPathExpressionException 
     */
    public MappingConfiguration getMappingConfiguration(byte[] fileContents) throws SAXException
        , IOException, ParserConfigurationException, XPathExpressionException
    {
        DocumentBuilderFactory builderFactory =
                DocumentBuilderFactory.newInstance();

        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        
        Document document = builder.parse(new ByteArrayInputStream(fileContents));
        MappingConfiguration mappingConfiguration = new MappingConfiguration();
        XPath xpath = this.xpathFactory.newXPath();
        String client = (String) xpath.evaluate(MappingConfigurationImportXpathConstants.CLIENT_XPATH, document, XPathConstants.STRING);
        String type = (String) xpath.evaluate(MappingConfigurationImportXpathConstants.TYPE_XPATH, document, XPathConstants.STRING);
        String sourceContext = (String) xpath.evaluate(MappingConfigurationImportXpathConstants.SOURCE_CONTEXT_XPATH, document, XPathConstants.STRING);
        String targetContext = (String) xpath.evaluate(MappingConfigurationImportXpathConstants.TARGET_CONTEXT_XPATH, document, XPathConstants.STRING);
        String description = (String) xpath.evaluate(MappingConfigurationImportXpathConstants.DESCRIPTION_XPATH, document, XPathConstants.STRING);
        String numberOfParams = (String) xpath.evaluate(MappingConfigurationImportXpathConstants.NUMBER_OF_SOURCE_PARAMS_XPATH, document, XPathConstants.STRING);

        ConfigurationServiceClient configurationServiceClient = new ConfigurationServiceClient();
        configurationServiceClient.setName(client);

        ConfigurationType configurationType = new ConfigurationType();
        configurationType.setName(type);

        ConfigurationContext sourceConfigurationContext = new ConfigurationContext();
        sourceConfigurationContext.setName(sourceContext);

        ConfigurationContext targetConfigurationContext = new ConfigurationContext();
        targetConfigurationContext.setName(targetContext);

        mappingConfiguration.setConfigurationServiceClient(configurationServiceClient);
        mappingConfiguration.setConfigurationType(configurationType);
        mappingConfiguration.setSourceContext(sourceConfigurationContext);
        mappingConfiguration.setTargetContext(targetConfigurationContext);
        mappingConfiguration.setDescription(description);
        mappingConfiguration.setNumberOfParams(new Long(numberOfParams));

        return mappingConfiguration;
    }

    /**
     * 
     * @param fileContents
     * @return
     * @throws IOException 
     * @throws SAXException 
     * @throws ParserConfigurationException 
     */
    public List<MappingConfigurationValue> getMappingConfigurationValues(byte[] fileContents) throws SAXException
        , IOException, ParserConfigurationException
    {
        DocumentBuilderFactory builderFactory =
                DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;

        this.mappingConfigurationValues = new ArrayList<MappingConfigurationValue>();

        builder = builderFactory.newDocumentBuilder();
        Document document = builder.parse(
            new ByteArrayInputStream(fileContents));

        logger.debug("Uploaded document = " + document);

        logger.debug("Document element = " + document.getDocumentElement().getNodeName());

        Element documentRoot = document.getDocumentElement();

        NodeList mappingConfigurationValues = documentRoot.getElementsByTagName("mappingConfigurationValue");
        
        logger.debug("Number of mapping configuration values = " + mappingConfigurationValues.getLength());

        for(int i=0; i<mappingConfigurationValues.getLength(); i++)
        {
            this.mappingConfigurationValues.add(getMappingConfigurationValue((Element)mappingConfigurationValues.item(i)));
        }

        return this.mappingConfigurationValues;
    }

    /**
     * 
     * @param fileContents
     * @return
     * @throws IOException 
     * @throws SAXException 
     * @throws ParserConfigurationException 
     */
    public List<KeyLocationQuery> getKeyLocationQueries(byte[] fileContents) throws SAXException
        , IOException, ParserConfigurationException
    {
        DocumentBuilderFactory builderFactory =
                DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;

        builder = builderFactory.newDocumentBuilder();
        Document document = builder.parse(
            new ByteArrayInputStream(fileContents));

        Element documentRoot = document.getDocumentElement();

        NodeList keyLocationQueries = documentRoot.getElementsByTagName("sourceConfigurationValueQuery");

        return this.getKeyLocationQueries(keyLocationQueries);
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

        for(int i=0; i<sourceConfigurationValues.getLength(); i++)
        {
            logger.debug("Source value: " + sourceConfigurationValues.item(i).getTextContent());
            SourceConfigurationValue value = new SourceConfigurationValue();
            value.setSourceSystemValue(sourceConfigurationValues.item(i).getTextContent());

            returnValue.add(value);
        }

        return returnValue;
    }

    /**
     * Gets a list of source configuration values from an XML node list.
     * @param sourceConfigurationValues
     * @return
     */
    protected ArrayList<KeyLocationQuery> getKeyLocationQueries(NodeList keyLocationQueries)
    {
        ArrayList<KeyLocationQuery> returnValue = new ArrayList<KeyLocationQuery>();

        for(int i=0; i<keyLocationQueries.getLength(); i++)
        {
            logger.debug("KeyLocationQuery: " + keyLocationQueries.item(i).getTextContent());
            KeyLocationQuery value = new KeyLocationQuery();
            value.setValue(keyLocationQueries.item(i).getTextContent());

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
