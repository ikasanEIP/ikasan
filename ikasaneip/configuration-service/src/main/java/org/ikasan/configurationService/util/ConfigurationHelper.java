package org.ikasan.configurationService.util;

import org.apache.log4j.Logger;
import org.ikasan.spec.configuration.Configuration;
import org.ikasan.spec.configuration.ConfigurationManagement;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.topology.model.Component;
import org.ikasan.topology.model.Flow;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by stewmi on 21/12/2016.
 */
public abstract class ConfigurationHelper
{
    private Logger logger = Logger.getLogger(ConfigurationHelper.class);

    protected ConfigurationManagement<ConfiguredResource, Configuration> configurationService;

    protected StringBuffer errorMessage;

    protected final XPathFactory xpathFactory = XPathFactory.newInstance();

    ConfigurationCreationHelper helper = null;

    /**
     * Constructor
     *
     * @param configurationService
     * @param helper
     */
    protected ConfigurationHelper(ConfigurationManagement<ConfiguredResource, Configuration> configurationService,
                               ConfigurationCreationHelper helper)
    {
        this.configurationService = configurationService;
        this.helper = helper;
    }

    protected List<Configuration> getFlowConfigurations(Flow flow)
    {
        List<Configuration> configurations = new ArrayList<Configuration>();

        logger.info("Getting configurations for flow: " + flow.getName() + " with " + flow.getComponents().size() + " components");

        for(org.ikasan.topology.model.Component component: flow.getComponents())
        {
            if(component.isConfigurable() && component.getConfigurationId() != null)
            {
                logger.info("Component is configurable: " + component.getName());

                Configuration configuration = configurationService
                        .getConfiguration(component.getConfigurationId());

                if(configuration == null)
                {
                    logger.info("Creating configuration for component: " + component.getName());
                    configuration = helper.createConfiguration(component);
                }

                configurations.add(configuration);
            }
        }

        return configurations;
    }

    protected Document getSubDocument(Node documentNode) throws ParserConfigurationException
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document newDocument = builder.newDocument();
        Node importedNode = newDocument.importNode(documentNode, true);
        newDocument.appendChild(importedNode);

        return newDocument;
    }

    protected String getValueFromDocument(Document document, String xpathString) throws XPathExpressionException
    {
        XPath xpath = this.xpathFactory.newXPath();
        return (String) xpath.evaluate(xpathString, document, XPathConstants.STRING);
    }

    protected Component confirmFlowConfiguredComponentExists(Flow flow, String configureResourceId)
    {
        boolean exists = false;

        Component returnComponent = null;

        for(Component component: flow.getComponents())
        {
            if(component.isConfigurable() && component.getConfigurationId().equals(configureResourceId))
            {
                returnComponent = component;
                exists = true;
                break;
            }
        }

        if(!exists)
        {
            errorMessage.append("Configured resource [" + configureResourceId
                    + "] does not exist in Flow [" + flow.getName() + "]\n");
        }

        return returnComponent;
    }
}
