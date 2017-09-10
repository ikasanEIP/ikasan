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
import org.ikasan.configurationService.model.*;
import org.ikasan.spec.configuration.Configuration;
import org.ikasan.spec.configuration.ConfigurationParameter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Ikasan Development Team
 *
 */
public class ComponentConfigurationImportHelper
{
    private static Logger logger = LoggerFactory.getLogger(ComponentConfigurationImportHelper.class);

    public static final String ID_XPATH = "/componentConfiguration/id";
    public static final String DESCRIPTION_XPATH = "/componentConfiguration/description";
    public static final String NAME = "name";
    public static final String VALUE = "value";
    public static final String DESCRIPTION = "description";
    public static final String ITEM = "item";


    private final XPathFactory xpathFactory = XPathFactory.newInstance();

    private Map<String, ConfigurationParameter> configurationParameters;

    private StringBuffer errorMessage;

    /**
     *
     * @param configuration
     * @param fileContents
     * @return
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws XPathExpressionException
     */
    public void updateComponentConfiguration(Configuration configuration, byte[] fileContents) throws SAXException
        , IOException, ParserConfigurationException, XPathExpressionException
    {
        DocumentBuilderFactory builderFactory =
                DocumentBuilderFactory.newInstance();

        DocumentBuilder builder = builderFactory.newDocumentBuilder();

        Document document = builder.parse(new ByteArrayInputStream(fileContents));

        this.updateComponentConfiguration(configuration, document);
    }

    /**
     *
     * @param configuration
     * @param document
     * @return
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws XPathExpressionException
     */
    public void updateComponentConfiguration(Configuration configuration, Document document) throws SAXException
            , IOException, ParserConfigurationException, XPathExpressionException
    {
        List<ConfigurationParameter> parameters = (List<ConfigurationParameter>)configuration.getParameters();

        this.configurationParameters = new HashMap<String, ConfigurationParameter>();

        for(ConfigurationParameter parameter: parameters)
        {
            this.configurationParameters.put(parameter.getName(), parameter);
        }

        XPath xpath = this.xpathFactory.newXPath();
        String id = (String) xpath.evaluate(ID_XPATH, document, XPathConstants.STRING);
        String description = (String) xpath.evaluate(DESCRIPTION_XPATH, document, XPathConstants.STRING);

        this.errorMessage = new StringBuffer();

        if(id == null || id.isEmpty())
        {
            errorMessage.append("Component Configuration id is missing!\n");
        }
        else if(!id.equals(configuration.getConfigurationId()))
        {
            errorMessage.append("This configuration id of the imported document must match the id of the configuration we are importing to!\n");
        }

        configuration.setDescription(description);
        this.updateStringParameters(document);
        this.updateBooleanParameters(document);
        this.updateIntegerParameters(document);
        this.updateLongParameters(document);
        this.updateMaskedStringParameters(document);
        this.updateMapParameters(document);
        this.updateListParameters(document);

        if(this.errorMessage.length() > 0)
        {
            logger.error("Error importing component configuration: " + errorMessage.toString());
            throw new RuntimeException(errorMessage.toString());
        }
    }

    /**
     * Update all of the configuration string parameters.
     *
     * @param document uploaded configuration document
     */
    protected void updateStringParameters(Document document)
    {
        Element documentRoot = document.getDocumentElement();

        NodeList parameters = documentRoot.getElementsByTagName("stringParameter");
        
        logger.debug("Number of string parameters = " + parameters.getLength());

        for(int i=0; i<parameters.getLength(); i++)
        {
            String paramName = ((Element)parameters.item(i)).getElementsByTagName(NAME).item(0).getTextContent();
            String paramValue = ((Element)parameters.item(i)).getElementsByTagName(VALUE).item(0).getTextContent();
            String paramDescription = ((Element)parameters.item(i)).getElementsByTagName(DESCRIPTION).item(0).getTextContent();

            ConfigurationParameterStringImpl param = (ConfigurationParameterStringImpl)this.configurationParameters.get(paramName);

            if(param == null)
            {
                errorMessage.append("Could not find underlying string configuration parameter for [")
                        .append(paramName)
                        .append("]. This is a valid configuration parameter name.\r\n");
            }
            else
            {
                param.setValue(paramValue);
                param.setDescription(paramDescription);
            }
        }
    }

    /**
     * Update all of the configuration string parameters.
     *
     * @param document uploaded configuration document
     */
    protected void updateMaskedStringParameters(Document document)
    {
        Element documentRoot = document.getDocumentElement();

        NodeList parameters = documentRoot.getElementsByTagName("maskedStringParameter");

        logger.debug("Number of masked string parameters = " + parameters.getLength());

        for(int i=0; i<parameters.getLength(); i++)
        {
            String paramName = ((Element)parameters.item(i)).getElementsByTagName(NAME).item(0).getTextContent();
            String paramValue = ((Element)parameters.item(i)).getElementsByTagName(VALUE).item(0).getTextContent();
            String paramDescription = ((Element)parameters.item(i)).getElementsByTagName(DESCRIPTION).item(0).getTextContent();

            ConfigurationParameterMaskedStringImpl param = (ConfigurationParameterMaskedStringImpl)this.configurationParameters.get(paramName);

            if(param == null)
            {
                errorMessage.append("Could not find underlying masked string configuration parameter for [")
                        .append(paramName)
                        .append("]. This is a valid configuration parameter name.\r\n");
            }
            else
            {
                param.setValue(paramValue);
                param.setDescription(paramDescription);
            }
        }
    }

    /**
     * Update all of the configuration string parameters.
     *
     * @param document uploaded configuration document
     */
    protected void updateIntegerParameters(Document document)
    {
        Element documentRoot = document.getDocumentElement();

        NodeList parameters = documentRoot.getElementsByTagName("integerParameter");

        logger.debug("Number of integer parameters = " + parameters.getLength());

        for(int i=0; i<parameters.getLength(); i++)
        {
            String paramName = ((Element)parameters.item(i)).getElementsByTagName(NAME).item(0).getTextContent();
            String paramValue = ((Element)parameters.item(i)).getElementsByTagName(VALUE).item(0).getTextContent();
            String paramDescription = ((Element)parameters.item(i)).getElementsByTagName(DESCRIPTION).item(0).getTextContent();

            ConfigurationParameterIntegerImpl param = (ConfigurationParameterIntegerImpl)this.configurationParameters.get(paramName);

            if(param == null)
            {
                errorMessage.append("Could not find underlying integer configuration parameter for [")
                        .append(paramName)
                        .append("]. This is not a valid configuration parameter name for the configuration.\r\n");
                continue;
            }


            if(!paramValue.isEmpty())
            {
                try
                {
                    param.setValue(Integer.parseInt(paramValue));
                } catch (NumberFormatException ex)
                {
                    this.errorMessage.append("Integer parameter [")
                            .append(paramName)
                            .append("] must be a Integer value. Received ")
                            .append(paramValue)
                            .append("\r\n");
                }
            }

            param.setDescription(paramDescription);
        }
    }

    /**
     * Update all of the configuration string parameters.
     *
     * @param document uploaded configuration document
     */
    protected void updateLongParameters(Document document)
    {
        Element documentRoot = document.getDocumentElement();

        NodeList parameters = documentRoot.getElementsByTagName("longParameter");

        logger.debug("Number of long parameters = " + parameters.getLength());

        for(int i=0; i<parameters.getLength(); i++)
        {
            String paramName = ((Element)parameters.item(i)).getElementsByTagName(NAME).item(0).getTextContent();
            String paramValue = ((Element)parameters.item(i)).getElementsByTagName(VALUE).item(0).getTextContent();
            String paramDescription = ((Element)parameters.item(i)).getElementsByTagName(DESCRIPTION).item(0).getTextContent();

            ConfigurationParameterLongImpl param = (ConfigurationParameterLongImpl)this.configurationParameters.get(paramName);

            if(param == null)
            {
                errorMessage.append("Could not find underlying long configuration parameter for [")
                        .append(paramName)
                        .append("]. This is not a valid configuration parameter name for the configuration\r\n");
                continue;
            }

            if(!paramValue.isEmpty()) {
                try {
                    param.setValue(Long.parseLong(paramValue));
                } catch (NumberFormatException ex) {
                    this.errorMessage.append("Long parameter [")
                            .append(paramName)
                            .append("] must be a Long value. Received ")
                            .append(paramValue)
                            .append("\r\n");
                }
            }

            param.setDescription(paramDescription);
        }
    }

    /**
     * Update all of the configuration string parameters.
     *
     * @param document uploaded configuration document
     */
    protected void updateBooleanParameters(Document document)
    {
        Element documentRoot = document.getDocumentElement();

        NodeList parameters = documentRoot.getElementsByTagName("booleanParameter");

        logger.debug("Number of boolean parameters = " + parameters.getLength());

        for(int i=0; i<parameters.getLength(); i++)
        {
            String paramName = ((Element)parameters.item(i)).getElementsByTagName(NAME).item(0).getTextContent();
            String paramValue = ((Element)parameters.item(i)).getElementsByTagName(VALUE).item(0).getTextContent();
            String paramDescription = ((Element)parameters.item(i)).getElementsByTagName(DESCRIPTION).item(0).getTextContent();

            ConfigurationParameterBooleanImpl param = (ConfigurationParameterBooleanImpl)this.configurationParameters.get(paramName);

            if(param == null)
            {
                errorMessage.append("Could not find underlying boolean configuration parameter for [")
                        .append(paramName)
                        .append("]. This is not a valid configuration parameter name for the configuration\r\n");
                continue;
            }

            if(!paramValue.equals("") && !paramValue.equals("true") && !paramValue.equals("false"))
            {
                this.errorMessage.append("Boolean parameter [")
                        .append(paramName)
                        .append("] must be a boolean value. Received ")
                        .append(paramValue)
                        .append("\r\n");
            }
            else if(!paramValue.equals(""))
            {
                param.setValue(Boolean.parseBoolean(paramValue));
            }

            param.setDescription(paramDescription);
        }
    }

    /**
     * Update all of the configuration string parameters.
     *
     * @param document uploaded configuration document
     */
    protected void updateMapParameters(Document document)
    {
        Element documentRoot = document.getDocumentElement();

        NodeList parameters = documentRoot.getElementsByTagName("mapParameter");

        logger.debug("Number of map parameters = " + parameters.getLength());

        for(int i=0; i<parameters.getLength(); i++)
        {
            String paramName = ((Element)parameters.item(i)).getElementsByTagName(NAME).item(0).getTextContent();
            String paramDescription = ((Element)parameters.item(i)).getElementsByTagName(DESCRIPTION).item(0).getTextContent();

            ConfigurationParameterMapImpl param = (ConfigurationParameterMapImpl)this.configurationParameters.get(paramName);

            if(param == null)
            {
                errorMessage.append("Could not find underlying map configuration parameter for [")
                        .append(paramName)
                        .append("]. This is not a valid configuration parameter name for the configuration\r\n");
                continue;
            }

            param.setDescription(paramDescription);

            NodeList items = ((Element)parameters.item(i)).getElementsByTagName(ITEM);

            for(int j=0; j<items.getLength(); j++)
            {
                String itemName = ((Element)items.item(j)).getElementsByTagName(NAME).item(0).getTextContent();
                String itemValue = ((Element)items.item(j)).getElementsByTagName(VALUE).item(0).getTextContent();

                param.getValue().put(itemName, itemValue);
            }
        }
    }


    protected void updateListParameters(Document document)
    {
        Element documentRoot = document.getDocumentElement();

        NodeList parameters = documentRoot.getElementsByTagName("listParameter");

        logger.info("Number of list parameters = " + parameters.getLength());

        for(int i=0; i<parameters.getLength(); i++)
        {
            String paramName = ((Element)parameters.item(i)).getElementsByTagName(NAME).item(0).getTextContent();
            String paramDescription = ((Element)parameters.item(i)).getElementsByTagName(DESCRIPTION).item(0).getTextContent();

            logger.info("List paramName = " + paramName);

            ConfigurationParameterListImpl param = (ConfigurationParameterListImpl)this.configurationParameters.get(paramName);

            if(param == null)
            {
                errorMessage.append("Could not find underlying list configuration parameter for [")
                        .append(paramName)
                        .append("]. This is not a valid configuration parameter name for the configuration\r\n");
                continue;
            }

            param.setDescription(paramDescription);

            NodeList items = ((Element)parameters.item(i)).getElementsByTagName(VALUE);

            logger.info("Number of list values = " + items.getLength());

            param.getValue().clear();

            for(int j=0; j<items.getLength(); j++)
            {
                String itemValue = items.item(j).getTextContent();

                logger.info("Adding list value = " + itemValue);
                param.getValue().add(itemValue);
            }
        }
    }

    public String getErrorMessage()
    {
        return errorMessage.toString();
    }
}
