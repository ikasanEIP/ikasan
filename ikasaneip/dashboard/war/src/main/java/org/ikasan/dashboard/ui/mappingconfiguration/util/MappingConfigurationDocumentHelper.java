/*
 * $Id: MappingConfigurationDocumentHelper.java 40648 2014-11-07 11:12:53Z stewmi $
 * $URL: https://svc-vcs-prd.uk.mizuho-sc.com:18080/svn/architecture/cmi2/trunk/projects/mappingConfigurationUI/war/src/main/java/org/ikasan/mapping/configuration/ui/util/MappingConfigurationDocumentHelper.java $
 *
 * ====================================================================
 *
 * Copyright (c) 2000-2011 by Mizuho International plc.
 * All Rights Reserved.
 *
 * ====================================================================
 *
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
import org.ikasan.dashboard.ui.mappingconfiguration.window.MappingConfigurationValuesImportWindow.SimpleErrorHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.mizuho.cmi2.mappingConfiguration.model.ConfigurationContext;
import com.mizuho.cmi2.mappingConfiguration.model.ConfigurationServiceClient;
import com.mizuho.cmi2.mappingConfiguration.model.ConfigurationType;
import com.mizuho.cmi2.mappingConfiguration.model.KeyLocationQuery;
import com.mizuho.cmi2.mappingConfiguration.model.MappingConfiguration;
import com.mizuho.cmi2.mappingConfiguration.model.SourceConfigurationValue;
import com.mizuho.cmi2.mappingConfiguration.model.TargetConfigurationValue;

/**
 * @author CMI2 Development Team
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
            logger.info(e.getMessage());
            warnings.add(e);
        }

        public void error(SAXParseException e) throws SAXException {
            logger.info(e.getMessage());
            errors.add(e);
        }

        public void fatalError(SAXParseException e) throws SAXException {
            logger.info(e.getMessage());
            fatal.add(e);
        }
    }
}
