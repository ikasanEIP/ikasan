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
import org.ikasan.mapping.model.*;
import org.w3c.dom.*;
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
     * @param fileContents
     * @return
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws XPathExpressionException
     * @throws MappingConfigurationImportException
     */
    public MappingConfiguration getMappingConfiguration(byte[] fileContents) throws SAXException
        , IOException, ParserConfigurationException, XPathExpressionException, MappingConfigurationImportException
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
        String isManyToMany = (String) xpath.evaluate(MappingConfigurationImportXpathConstants.IS_MANY_TO_MANY_XPATH, document, XPathConstants.STRING);
        String isFixedParameterListSize = (String) xpath.evaluate(MappingConfigurationImportXpathConstants.IS_FIXED_PARAMETER_LIST_SIZE_XPATH, document, XPathConstants.STRING);
        String numberOfParams = (String) xpath.evaluate(MappingConfigurationImportXpathConstants.NUMBER_OF_SOURCE_PARAMS_XPATH, document, XPathConstants.STRING);
        String numberOfTargetParams = (String) xpath.evaluate(MappingConfigurationImportXpathConstants.NUMBER_OF_TARGET_PARAMS_XPATH, document, XPathConstants.STRING);

        StringBuffer errorMessage = new StringBuffer();

        if(client == null || client.isEmpty())
        {
            errorMessage.append("Mapping Configuration Client is missing\n");
        }

        if(type == null || type.isEmpty())
        {
            errorMessage.append("Mapping Configuration Type is missing\n");
        }

        if(sourceContext == null || sourceContext.isEmpty())
        {
            errorMessage.append("Mapping Configuration Source Context is missing\n");
        }

        if(description == null || description.isEmpty())
        {
            errorMessage.append("Mapping Configuration Description is missing\n");
        }

        if((numberOfParams == null || numberOfParams.isEmpty()))
        {
            errorMessage.append("Mapping Configuration Number of Source Parameters is missing\n");
        }

        if((numberOfTargetParams == null || numberOfTargetParams.isEmpty()))
        {
            errorMessage.append("Mapping Configuration Number of Target Parameters is missing\n");
        }

        if(errorMessage.length() > 0)
        {
            throw new MappingConfigurationImportException("An error has occurred trying to import a Mapping Configuration\n"
                + errorMessage.toString());
        }

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
        mappingConfiguration.setIsManyToMany((isManyToMany.equals("true")) ? true : false);
        mappingConfiguration.setConstrainParameterListSizes((isFixedParameterListSize.equals("true")) ? true : false);

        logger.info("Setting is many to many to: " + isManyToMany);

        mappingConfiguration.setNumberOfParams(new Integer(numberOfParams));
        mappingConfiguration.setNumTargetValues(new Integer(numberOfTargetParams));


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
    public List<MappingConfigurationValue> getMappingConfigurationValues(byte[] fileContents, boolean isManyToMany) throws SAXException
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
            this.mappingConfigurationValues.add(getMappingConfigurationValue((Element)mappingConfigurationValues.item(i), isManyToMany));
        }

        return this.mappingConfigurationValues;
    }

    /**
     * Get source parameter names.
     *
     * @param fileContents
     * @return
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     */
    public List<ParameterName> getSourceParameterNames(byte[] fileContents) throws SAXException
        , IOException, ParserConfigurationException
    {
        DocumentBuilderFactory builderFactory =
                DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;

        builder = builderFactory.newDocumentBuilder();
        Document document = builder.parse(
            new ByteArrayInputStream(fileContents));

        Element documentRoot = document.getDocumentElement();

        NodeList parameterNames = documentRoot.getElementsByTagName("sourceParameterName");

        return this.getSourceParameterNames(parameterNames, ParameterName.SOURCE_CONTEXT);
    }

    /**
     * Get target parameter names.
     *
     * @param fileContents
     * @return
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     */
    public List<ParameterName> getTargetParameterNames(byte[] fileContents) throws SAXException
            , IOException, ParserConfigurationException
    {
        DocumentBuilderFactory builderFactory =
                DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;

        builder = builderFactory.newDocumentBuilder();
        Document document = builder.parse(
                new ByteArrayInputStream(fileContents));

        Element documentRoot = document.getDocumentElement();

        NodeList parameterNames = documentRoot.getElementsByTagName("targetParameterName");

        return this.getSourceParameterNames(parameterNames, ParameterName.TARGET_CONTEXT);
    }

    /**
     * Helper method to return a composite mapping configuration value.
     * 
     * @param mappingConfigurationValue
     * @return
     */
    protected MappingConfigurationValue getMappingConfigurationValue(Element mappingConfigurationValue, boolean isManyToMany)
    {
        if(isManyToMany)
        {
            ArrayList<SourceConfigurationValue> sourceConfigurationValues = getSourceConfigurationValues(mappingConfigurationValue
                    .getElementsByTagName("sourceConfigurationValue"));

            ArrayList<ManyToManyTargetConfigurationValue> targetConfigurationValues = getTargetConfigurationValues(mappingConfigurationValue
                    .getElementsByTagName("targetConfigurationValue"));

            return new MappingConfigurationValue(sourceConfigurationValues, targetConfigurationValues);

        }
        else
        {
            TargetConfigurationValue targetConfigurationValue = getTargetConfigurationValue
                    (mappingConfigurationValue.getElementsByTagName("targetConfigurationValue").item(0));

            ArrayList<SourceConfigurationValue> sourceConfigurationValues = getSourceConfigurationValues(mappingConfigurationValue
                    .getElementsByTagName("sourceConfigurationValue"));

            for (SourceConfigurationValue sourceConfigurationValue : sourceConfigurationValues)
            {
                logger.debug("Source value: " + sourceConfigurationValue.getSourceSystemValue());
                sourceConfigurationValue.setTargetConfigurationValue(targetConfigurationValue);
            }

            return new MappingConfigurationValue(targetConfigurationValue, sourceConfigurationValues);
        }
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

            NamedNodeMap attributes = sourceConfigurationValues.item(i).getAttributes();

            Node nameAttribute = attributes.getNamedItem("name");

            if(nameAttribute != null)
            {
                value.setName(nameAttribute.getTextContent());
            }

            returnValue.add(value);
        }

        return returnValue;
    }

    /**
     *
     * @param targetConfigurationValues
     * @return
     */
    protected ArrayList<ManyToManyTargetConfigurationValue> getTargetConfigurationValues(NodeList targetConfigurationValues)
    {
        ArrayList<ManyToManyTargetConfigurationValue> returnValue = new ArrayList<ManyToManyTargetConfigurationValue>();

        for(int i = 0; i< targetConfigurationValues.getLength(); i++)
        {
            logger.debug("Target value: " + targetConfigurationValues.item(i).getTextContent());
            ManyToManyTargetConfigurationValue value = new ManyToManyTargetConfigurationValue();
            value.setTargetSystemValue(targetConfigurationValues.item(i).getTextContent());

            NamedNodeMap attributes = targetConfigurationValues.item(i).getAttributes();

            Node nameAttribute = attributes.getNamedItem("name");

            if(nameAttribute != null)
            {
                value.setName(nameAttribute.getTextContent());
            }

            returnValue.add(value);
        }

        return returnValue;
    }
    
    protected ArrayList<ParameterName> getSourceParameterNames(NodeList keyLocationQueries, String context)
    {
        ArrayList<ParameterName> returnValue = new ArrayList<ParameterName>();

        for(int i=0; i<keyLocationQueries.getLength(); i++)
        {
            logger.debug("KeyLocationQuery: " + keyLocationQueries.item(i).getTextContent());
            ParameterName value = new ParameterName();
            value.setName(keyLocationQueries.item(i).getTextContent());
            value.setContext(context);

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
