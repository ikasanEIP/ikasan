/*
 * $Id: DefaultXMLParser.java 16606 2009-04-09 08:07:33Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/common/src/main/java/org/ikasan/common/xml/parser/DefaultXMLParser.java $
 * 
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * Copyright (c) 2003-2008 Mizuho International plc. and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the 
 * Free Software Foundation Europe e.V. Talstrasse 110, 40217 Dusseldorf, Germany 
 * or see the FSF site: http://www.fsfeurope.org/.
 * ====================================================================
 */
package org.ikasan.common.xml.parser;

// Imported ikasan classes
import org.ikasan.common.xml.parser.DefaultEntityResolver;
import org.ikasan.common.xml.parser.DefaultErrorHandler;
import org.ikasan.common.CommonXMLParser;

// Imported java classes
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.IOException;

// Imported jaxp classes
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

// Imported sax classes
import org.xml.sax.Attributes;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

// Imported w3c classes
import org.w3c.dom.Document;
import org.w3c.dom.Node;

// Imported log4j classes
import org.apache.log4j.Logger;

/**
 * This class wraps JAXP API for ease of XML well-formedness, validation and DOM document creation. Basically, it parses
 * (and validates if the flag is turned on) a specified DTD or XML-Schema based XML document via
 * <code>javax.xml.parsers.DocumentBuilder</code> and returns a DOM Document instance, <code>org.w3c.dom.Document</code>
 * object.
 * <p>
 * 
 * <pre>
 *    Usage Example:
 * 
 *    String xmlURI = &quot;http://www.abc.com/data/foo.xml&quot;;
 * 
 *    Document doc = null;
 *    try
 *    {
 *        DefaultXMLParser parser = new DefaultXMLParser();
 *        parser.setValidation(true, XMLConstants.W3C_XML_SCHEMA_NS_URI);
 *        doc = parser.parse(xmlURI);
 * 
 *        // Get the root name
 *        String rootName = parser.getRootName(doc);
 *        System.out.println(&quot;Root name: [&quot; + rootName + &quot;].&quot;);
 * 
 *        // Do something else with this Document
 * 
 *        // Remove indentation
 *        // Then normalise the DOM tree to combine all adjacent text nodes
 *        parser.removeIndent(doc);
 *        doc.normalize();
 *    }
 *    catch (Exception e)
 *    {
 *        e.printStackTrace(System.err);
 *    }
 * </pre>
 * 
 * @author Ikasan Development Team
 */
public class DefaultXMLParser implements CommonXMLParser
{
    /** The logger instance. */
    private static Logger logger = Logger.getLogger(DefaultXMLParser.class);

    /** The document builder factory */
    private DocumentBuilderFactory factory;

    /** default entity resolver is null */
    private EntityResolver entityResolver;

    /** The XML schema types are based on imported XMLConstants. */
    private String schemaType;

    /**
     * Creates a new instance of <code>DefaultXMLParser</code>. This is created with the following defaults, validation
     * defaulted to false namespaceAware defaulted to true schemaType defaults to XML_DTD_NS_URI
     * 
     */
    public DefaultXMLParser()
    {
        this.factory = DocumentBuilderFactory.newInstance();
        this.factory.setNamespaceAware(true);
        this.factory.setValidating(false);
        this.schemaType = XMLConstants.XML_DTD_NS_URI;
        this.entityResolver = null;
    }

    /**
     * Sets a flag indicating whether to validate an incoming XML document as it is parsed. By default the value of this
     * is set to <code>false</code>.
     * 
     * @deprecated - use the two individual setter methods of setValidation(Boolean) and setSchemaType(String).
     * 
     * @param validation - true if the parser produced will validate a document as it is parsed.
     * @param schemaType - XML document schema type. Currently, <code>DTD</code> and <code>XSD</code> are supported.
     */
    @Deprecated
    public void setValidation(Boolean validation, String schemaType)
    {
        this.setValidation(validation);
        this.setSchemaType(schemaType);
    }

    /**
     * Sets a flag indicating whether to validate an incoming XML document as it is parsed. By default the value of this
     * is set to <code>false</code>.
     * 
     * @param validation - true if the parser produced will validate a document as it is parsed.
     */
    public void setValidation(final Boolean validation)
    {
        this.factory.setValidating(validation.booleanValue());
    }

    /**
     * Sets the XML schema type for validation.
     * 
     * @param schemaType - XML document schema type. Currently, <code>DTD</code> and <code>XSD</code> are supported.
     */
    public void setSchemaType(final String schemaType)
    {
        this.schemaType = schemaType;
    }

    /**
     * Set the flag for this document to be namespace aware.
     * 
     * @param namespaceAware namespace aware flag to set
     */
    public void setNamespaceAware(Boolean namespaceAware)
    {
        this.factory.setNamespaceAware(namespaceAware.booleanValue());
    }

    /**
     * Set a specific entityResolver.
     * 
     * @param entityResolver entity resolver to set
     */
    public void setEntityResolver(EntityResolver entityResolver)
    {
        this.entityResolver = entityResolver;
    }

    /**
     * Set the default entity resolver.
     */
    public void setEntityResolver()
    {
        this.entityResolver = new DefaultEntityResolver();
    }

    /*
     * Returns a flag indicating whether to validate an incoming XML document. Currently, <code>DTD</code> and
     * <code>XSD</code> are supported.
     */
    public Boolean isValidating()
    {
        return new Boolean(this.factory.isValidating());
    }

    /*
     * Returns a flag indicating whether to validate an incoming XML document. Currently, <code>DTD</code> and
     * <code>XSD</code> are supported.
     */
    public Boolean isNamspaceAware()
    {
        return new Boolean(this.factory.isNamespaceAware());
    }

    /*
     * Returns the schema type of the incoming XML document.
     */
    public String getXMLSchemaType()
    {
        return this.schemaType;
    }

    /**
     * Parses (and validates if the flag is turned on) the specified DTD or XML-Schema based XML document using JAXP API
     * and returns <code>org.w3c.dom.Document</code> object.
     * 
     * @param xmlObject - XML document to be parsed.
     * 
     * @return Parsed document
     * 
     * @throws ParserConfigurationException Exception if we could not configure the parser
     * @throws SAXException Exception from a SAX related problem
     * @throws IOException Exception from a File i/O problem
     */
    private Document doParse(Object xmlObject) throws ParserConfigurationException, SAXException, IOException
    {
        if (xmlObject == null)
        {
            throw new NullPointerException("Object that is holding XML document can't be null"); //$NON-NLS-1$
        }
        if (this.schemaType == XMLConstants.W3C_XML_SCHEMA_NS_URI)
        {
            logger.debug("Setting attribute for XMLSchema validation..."); //$NON-NLS-1$
            this.factory.setAttribute(org.apache.xerces.jaxp.JAXPConstants.JAXP_SCHEMA_LANGUAGE,
                org.apache.xerces.jaxp.JAXPConstants.W3C_XML_SCHEMA);
        }
        DocumentBuilder builder = this.factory.newDocumentBuilder();
        builder.setErrorHandler(new DefaultErrorHandler());
        // set entity resolver if defined
        if (this.entityResolver != null) builder.setEntityResolver(entityResolver);
        if (xmlObject instanceof String)
        {
            logger.debug("Parsing XML doc as URI [" + xmlObject + "]..."); //$NON-NLS-1$ //$NON-NLS-2$
            return builder.parse((String) xmlObject);
        }
        else if (xmlObject instanceof byte[])
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Parsing XML doc as XML string..."); //$NON-NLS-1$
                logger.debug("XML document content ="); //$NON-NLS-1$
                logger.debug("[\n" + String.valueOf(xmlObject) + "\n]"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            InputStream is = new ByteArrayInputStream((byte[]) xmlObject);
            return builder.parse(is);
        }
        else if (xmlObject instanceof File)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Parsing XML doc as file..."); //$NON-NLS-1$
                logger.debug("XML document URI =[" //$NON-NLS-1$
                        + ((File) xmlObject).toString() + "]."); //$NON-NLS-1$
            }
            return builder.parse((File) xmlObject);
        }
        else if (xmlObject instanceof InputStream)
        {
            logger.debug("Parsing XML doc as input stream..."); //$NON-NLS-1$
            return builder.parse((InputStream) xmlObject);
        }
        else if (xmlObject instanceof InputSource)
        {
            logger.debug("Parsing XML doc as input source..."); //$NON-NLS-1$
            return builder.parse((InputSource) xmlObject);
        }
        else if (xmlObject instanceof Document)
        {
            logger.debug("Already Docuent object, returning..."); //$NON-NLS-1$
            return (Document) xmlObject;
        }
        else
        {
            throw new IllegalArgumentException("Unsupported object '" + xmlObject.getClass().getName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    /*
     * Parses (and validates if the flag is turned on) the specified DTD or XML-Schema based XML document using JAXP API
     * and returns <code>org.w3c.dom.Document</code> object.
     * 
     * @param uri - location of XML document to be parsed.
     */
    public Document parse(String uri) throws ParserConfigurationException, SAXException, IOException
    {
        return this.doParse(uri);
    }

    /*
     * Parses (and validates if the flag is turned on) the specified DTD or XML-Schema based XML document using JAXP API
     * and returns <code>org.w3c.dom.Document</code> object.
     * 
     * @param xmlDoc - array of byte containing XML document to be parsed.
     */
    public Document parse(byte[] xmlDoc) throws ParserConfigurationException, SAXException, IOException
    {
        return this.doParse(xmlDoc);
    }

    /*
     * Parses (and validates if the flag is turned on) the specified DTD or XML-Schema based XML document using JAXP API
     * and returns <code>org.w3c.dom.Document</code> object.
     * 
     * @param file - XML document file to be parsed.
     */
    public Document parse(File file) throws ParserConfigurationException, SAXException, IOException
    {
        return this.doParse(file);
    }

    /*
     * Parses (and validates if the flag is turned on) the specified DTD or XML-Schema based XML document using JAXP API
     * and returns <code>org.w3c.dom.Document</code> object.
     * 
     * @param is - <code>InputSource</code> containing XML document to be parsed.
     */
    public Document parse(InputSource is) throws ParserConfigurationException, SAXException, IOException
    {
        return this.doParse(is);
    }

    /*
     * Parses (and validates if the flag is turned on) the specified DTD or XML-Schema based XML document using JAXP API
     * and returns <code>org.w3c.dom.Document</code> object.
     * 
     * @param is - <code>InputStream</code> containing XML document to be parsed.
     */
    public Document parse(InputStream is) throws ParserConfigurationException, SAXException, IOException
    {
        return this.doParse(is);
    }

    /**
     * Returns the root name of the specified XML document without validation.
     * 
     * @param xmlObject is the XML document as <code>String</code> or <code>InputSource</code> or <code>File</code> or
     *            <code>InputStream</code> or <code>Document</code> being parsed.
     * 
     * @return the root name.
     * @throws ParserConfigurationException Exception if we could not configure the parser
     * @throws SAXException Exception from a SAX related problem
     * @throws IOException Exception from a File i/O problem
     */
    private static String doGetRootName(Object xmlObject) throws ParserConfigurationException, IOException,
            SAXException
    {
        if (xmlObject == null)
        {
            throw new NullPointerException("Object that is holding XML document can't be null"); //$NON-NLS-1$
        }
        else if (xmlObject instanceof Document)
        {
            Document doc = (Document) xmlObject;
            Node node = doc.getDocumentElement();
            node.normalize();
            return node.getNodeName();
        }
        // Custom DefaultHandler
        // to obtain a root name
        class MyDefaultHandler extends DefaultHandler
        {
            boolean isFirstTime = false;

            String rootName = null;

            DefaultErrorHandler errHandler = new DefaultErrorHandler();

            /** Constructor */
            public MyDefaultHandler()
            {
                // Do Nothing
            }

            @Override
            public void startElement(String namespaceUri, String localName, String qname, Attributes attrs)
            {
                if (isFirstTime == false)
                {
                    this.rootName = qname;
                    this.isFirstTime = true;
                }
            }

            @Override
            public void warning(SAXParseException e) throws SAXException
            {
                this.errHandler.warning(e);
            }

            @Override
            public void error(SAXParseException e) throws SAXException
            {
                this.errHandler.error(e);
            }

            @Override
            public void fatalError(SAXParseException e) throws SAXException
            {
                this.errHandler.fatalError(e);
            }
        }
        MyDefaultHandler handler = new MyDefaultHandler();
        SAXParserFactory factory = SAXParserFactory.newInstance();
        // TODO - defaulting to true maybe an issue for certain XML documents.
        factory.setNamespaceAware(true);
        // jun No need to validate an XML document at the moment
        // factory.setValidating(true);
        SAXParser saxParser = factory.newSAXParser();
        if (xmlObject instanceof String)
        {
            saxParser.parse((String) xmlObject, handler);
        }
        else if (xmlObject instanceof byte[])
        {
            InputStream is = new ByteArrayInputStream((byte[]) xmlObject);
            saxParser.parse(is, handler);
        }
        else if (xmlObject instanceof File)
        {
            saxParser.parse((File) xmlObject, handler);
        }
        else if (xmlObject instanceof InputStream)
        {
            saxParser.parse((InputStream) xmlObject, handler);
        }
        else if (xmlObject instanceof InputSource)
        {
            saxParser.parse((InputSource) xmlObject, handler);
        }
        else
        {
            throw new IllegalArgumentException("Unsupported object [" + xmlObject.getClass().getName() + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        return handler.rootName;
    }

    /**
     * Returns the root name of the specified XML document without validation.
     * 
     * @param uri - location of XML document to be parsed.
     * 
     * @return the root name.
     * @throws ParserConfigurationException Exception if we could not configure the parser
     * @throws SAXException Exception from a SAX related problem
     * @throws IOException Exception from a File i/O problem
     */
    public String getRootName(String uri) throws ParserConfigurationException, IOException, SAXException
    {
        return doGetRootName(uri);
    }

    /**
     * Returns the root name of the specified XML document without validation.
     * 
     * @param xmlDoc - array of byte containing XML document to be parsed.
     * 
     * @return the root name.
     * @throws ParserConfigurationException Exception if we could not configure the parser
     * @throws SAXException Exception from a SAX related problem
     * @throws IOException Exception from a File i/O problem
     */
    public String getRootName(byte xmlDoc[]) throws ParserConfigurationException, IOException, SAXException
    {
        return doGetRootName(xmlDoc);
    }

    /**
     * Returns the root name of the specified XML document without validation.
     * 
     * @param file - XML document file to be parsed.
     * 
     * @return the root name.
     * @throws ParserConfigurationException Exception if we could not configure the parser
     * @throws SAXException Exception from a SAX related problem
     * @throws IOException Exception from a File i/O problem
     */
    public String getRootName(File file) throws ParserConfigurationException, IOException, SAXException
    {
        return doGetRootName(file);
    }

    /**
     * Returns the root name of the specified XML document without validation.
     * 
     * @param is - <code>InputSource</code> containing XML document to be parsed.
     * 
     * @return the root name.
     * @throws ParserConfigurationException Exception if we could not configure the parser
     * @throws SAXException Exception from a SAX related problem
     * @throws IOException Exception from a File i/O problem
     */
    public String getRootName(InputSource is) throws ParserConfigurationException, IOException, SAXException
    {
        return doGetRootName(is);
    }

    /**
     * Returns the root name of the specified XML document without validation.
     * 
     * @param is - <code>InputStream</code> containing XML document to be parsed.
     * 
     * @return the root name.
     * @throws ParserConfigurationException Exception if we could not configure the parser
     * @throws SAXException Exception from a SAX related problem
     * @throws IOException Exception from a File i/O problem
     */
    public String getRootName(InputStream is) throws ParserConfigurationException, IOException, SAXException
    {
        return doGetRootName(is);
    }

    /**
     * Returns the root name of the specified XML document without validation.
     * 
     * @param doc - <code>Document</code> containing XML document to be parsed.
     * 
     * @return the root name.
     * @throws ParserConfigurationException Exception if we could not configure the parser
     * @throws SAXException Exception from a SAX related problem
     * @throws IOException Exception from a File i/O problem
     */
    public String getRootName(Document doc) throws ParserConfigurationException, IOException, SAXException
    {
        return doGetRootName(doc);
    }

    /**
     * Walks the document and removes all text nodes used for indentation.
     * 
     * @param node - <code>Node</code> containing XML document to be examined.
     * 
     */
    public void removeIndent(Node node)
    {
        // Is there anything to do?
        if (node == null) return;
        short type = node.getNodeType();
        switch (type)
        {
        // It's show time!
        case Node.DOCUMENT_NODE:
        {
            Document document = (Document) node;
            removeIndent(document.getDocumentElement());
            break;
        }
            // Remove all text nodes, but the one that is the only one child
        case Node.ELEMENT_NODE:
        {
            int numOfChildren = (node.getChildNodes() != null) ? node.getChildNodes().getLength() : 0;
            Node child = node.getFirstChild();
            while (child != null)
            {
                // We've got TEXT node
                if (child.getNodeType() == Node.TEXT_NODE)
                {
                    // It's got only whitespaces and brothers & sisters
                    // so it should be just indentation, remove this node
                    if (child.getNodeValue() != null && child.getNodeValue().trim().length() == 0 && numOfChildren > 1)
                    {
                        Node nextSibling = child.getNextSibling();
                        child = node.removeChild(child);
                        child = nextSibling;
                        continue;
                    }
                }
                removeIndent(child);
                child = child.getNextSibling();
            }
            break;
        }
        case Node.ENTITY_REFERENCE_NODE:
        {
            Node child = node.getFirstChild();
            while (child != null)
            {
                removeIndent(child);
                child = child.getNextSibling();
            }
            break;
        }
        case Node.DOCUMENT_TYPE_NODE:
            break;
        case Node.CDATA_SECTION_NODE:
            break;
        case Node.TEXT_NODE:
            break;
        case Node.PROCESSING_INSTRUCTION_NODE:
            break;
        case Node.COMMENT_NODE:
            break;
        default:
            break;
        }
    }

    // main() method
    // /////////////////////////////////////////////////////////////////////////////
    /**
     * Runs this class for testing.
     * 
     * TODO Unit Test
     * 
     * @param args arguments
     */
    public static void main(String args[])
    {
        String xmlURI = null;
        String xmlStr = defaultXmlStr;
        boolean validate = false;
        boolean xmlSchema = false;
        // Parse the command-line parameters
        for (int i = 0; i < args.length; i++)
        {
            // Display usage then get outta here
            if (args[i].equalsIgnoreCase("-help"))
            {
                usage();
                System.exit(1);
            }
            // XML URI
            else if (args[i].equalsIgnoreCase("-xml"))
            {
                xmlURI = args[++i].trim();
            }
            // Validation flag for parse() method
            else if (args[i].equalsIgnoreCase("-validate"))
            {
                String value = args[++i].trim();
                validate = (value.equalsIgnoreCase("true")) ? true : false;
            }
            // XML Schema flag for parse() method
            else if (args[i].equalsIgnoreCase("-xmlschema"))
            {
                String value = args[++i].trim();
                xmlSchema = (value.equalsIgnoreCase("true")) ? true : false;
            }
            else
            {
                System.err.println("Invalid option - [" + args[i] + "].");
                usage();
                System.exit(1);
            }
        }
        System.out.println("");
        System.out.println("XML URI    =[" + xmlURI + "].");
        System.out.println("validation =[" + validate + "].");
        System.out.println("XML schema =[" + xmlSchema + "].");
        if (xmlURI == null || xmlURI.trim().length() == 0)
        {
            System.out.println("XML doc string (default) =");
            System.out.println(xmlStr);
        }
        System.out.println("");
        try
        {
            DefaultXMLParser parser = new DefaultXMLParser();
            String schemaType = xmlSchema ? XMLConstants.W3C_XML_SCHEMA_NS_URI : XMLConstants.XML_DTD_NS_URI;
            parser.setValidation(validate, schemaType);
            if (xmlURI != null && xmlURI.length() > 0)
            {
                System.out.println("Parsing URI [" + xmlURI + "]...");
                parser.parse(xmlURI);
            }
            else
            {
                System.out.println("Parsing the default XML string...");
                parser.parse(xmlStr.getBytes());
            }
            System.out.println("=> Successful.");
        }
        catch (Exception e)
        {
            e.printStackTrace(System.err);
        }
        System.exit(0);
    }

    /**
     * Displays the usage for main() method.
     * 
     * TODO Unit Test
     */
    private static void usage()
    {
        String fqClassName = DefaultXMLParser.class.getName();
        System.err.println("");
        System.err.println("Usage:");
        System.err.println("java " + fqClassName + " [-options]");
        System.err.println("");
        System.err.println("where options include:");
        System.err.println("    -xml <XML URI> to specify input XML document location (See below for default XML)");
        System.err.println("    -validate <true|false>");
        System.err.println("                   to validate the XML document (false)");
        System.err.println("    -xmlschema <true|false>");
        System.err.println("                   to indicate whether the XML document is XML Schema based (false)");
        System.err.println("");
        System.err.println("Note that the following default XML string will be used if XML URI is not specified:-");
        System.err.println(defaultXmlStr);
    }

    /**
     * Default XML string used in main() method
     * 
     * TODO Unit Test
     */
    private static String defaultXmlStr = "<?xml version='1.0'?>\n" + "<doc>\n" + "  <a>data_a1\n"
            + "    <b>data_b1</b>\n" + "    <c>data_c1</c>\n" + "  </a>\n" + "</doc>";
}
