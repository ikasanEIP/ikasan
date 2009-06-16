/*
 * $Id$
 * $URL$
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
package org.ikasan.common.transform;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.apache.log4j.Logger;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.ikasan.common.xml.transform.DefaultDOMSerializer;

/**
 * Provides functionality to parse a delimited string of single values or
 * name-value pairs into a flat XML structure. <u>Example 1:</u> Delimited
 * string of single value: <code>|a|b|c|d|</code> Resulting xml:
 * 
 * <pre>
 * &lt;root&gt;
 *   &lt;a/&gt;
 *   &lt;b/&gt;
 *   &lt;c/&gt;
 *   &lt;d/&gt;
 * &lt;/root&gt;
 * </pre>
 * 
 * <u>Example 2:</u> Delimited string of name-value pair:
 * <code>|a=1|b=2|c=3|d=4|</code> Resulting xml:
 * 
 * <pre>
 *  
 * &lt;root&gt;
 *    &lt;a&gt;1&lt;/a&gt;
 *    &lt;b&gt;2&lt;/b&gt;
 *    &lt;c&gt;3&lt;/c&gt;
 *    &lt;d&gt;4&lt;/d&gt;
 * &lt;/root&gt;
 * </pre>
 * 
 * TODO: Add the ability to escape '=' characters as part of the value a
 * name-value pair
 * 
 * @author Ikasan Development Team
 * 
 */
public class DelimitedStringToFlatXMLTransformer
{
    /**
     * The logger instance.
     */
    private static Logger logger = Logger.getLogger(DelimitedStringToFlatXMLTransformer.class);

    /** The scanner */
    private Scanner scanner = null;

    /** List of message delimiters */
    private ArrayList<String> messageDelimiters = null;

    /** List of tokens */
    private ArrayList<String> tokens = null;

    /** Token Separator */
    private String tokenSeparator = null;

    /** The name of the root element */
    private String rootElementName = null;

    /** Default constructor */
    DelimitedStringToFlatXMLTransformer()
    {
        // Do Nothing
    }

    /**
     * Parameterised constructor for name-value pair input
     * 
     * @param source The delimited string to convert to flat xml
     * @param messageDelimiter The
     * @param rootElementName
     * @param tokenSeparator
     */
    DelimitedStringToFlatXMLTransformer(String source, String messageDelimiter, String rootElementName,
            String tokenSeparator)
    {
        this.setSource(source);
        this.setMessageDelimiters(messageDelimiter);
        this.setRootElementName(rootElementName);
        this.setTokenSeparator(tokenSeparator);
    }

    /**
     * Parameterised constructor for name only pair input
     * 
     * @param source
     * @param messageDelimiter
     * @param rootElementName
     */
    DelimitedStringToFlatXMLTransformer(String source, String messageDelimiter, String rootElementName)
    {
        this.setSource(source);
        this.setMessageDelimiters(messageDelimiter);
        this.setRootElementName(rootElementName);
        this.setTokenSeparator(null);
    }

    /**
     * Set the root element name
     * 
     * @param rootElementName
     */
    public void setRootElementName(String rootElementName)
    {
        logger.debug("Setting root element name to [" + rootElementName + "]");
        this.rootElementName = new String(rootElementName);
    }

    /**
     * Get the root element name
     * 
     * @return the root element name
     */
    public String getRootElementName()
    {
        logger.debug("Getting root element name [" + this.rootElementName + "]");
        return this.rootElementName;
    }

    /**
     * Set the message delimiters
     * 
     * @param pattern
     */
    public void setMessageDelimiters(String pattern)
    {
        if (this.messageDelimiters != null)
            this.messageDelimiters.add(pattern);
        else
        {
            this.messageDelimiters = new ArrayList<String>();
            this.messageDelimiters.add(pattern);
        }
    }

    /**
     * Set the message delimiters
     * 
     * @param patterns
     */
    public void setMessageDelimiters(List<String> patterns)
    {
        if (this.messageDelimiters != null)
            this.messageDelimiters.addAll(patterns);
        else
            this.messageDelimiters = new ArrayList<String>(patterns);
    }

    /**
     * Get the list of message delimiters
     * 
     * @return list of message delimiters
     */
    public ArrayList<String> getMessageDelimiters()
    {
        return this.messageDelimiters;
    }

    /**
     * Sets the name-value pair separator pattern
     * 
     * @param pattern
     */
    public void setTokenSeparator(String pattern)
    {
        logger.debug("Setting name-value pair separator pattern to [" + pattern + "]");
        this.tokenSeparator = pattern;
    }

    /**
     * @return the name-value pair separator pattern currently used
     */
    public String getTokenSeparator()
    {
        logger.debug("Getting current name-value pair separator [" + this.tokenSeparator + "]");
        return this.tokenSeparator;
    }

    /**
     * Set the source of the delimited string
     * 
     * @param in
     * @throws FileNotFoundException
     */
    public void setSource(File in) throws FileNotFoundException
    {
        this.scanner = new Scanner(in);
    }

    /**
     * Set the source of the delimited string
     * 
     * @param in
     * @param charsetName
     * @throws FileNotFoundException
     */
    public void setSource(File in, String charsetName) throws FileNotFoundException
    {
        this.scanner = new Scanner(in, charsetName);
    }

    /**
     * Set the source of the delimted string
     * 
     * @param in
     */
    public void setSource(InputStream in)
    {
        this.scanner = new Scanner(in);
    }

    /**
     * Set the source of the delimited string
     * 
     * @param in
     * @param charsetName
     */
    public void setSource(InputStream in, String charsetName)
    {
        this.scanner = new Scanner(in, charsetName);
    }

    /**
     * Set the source of the delimited string
     * 
     * @param in
     */
    public void setSource(ReadableByteChannel in)
    {
        this.scanner = new Scanner(in);
    }

    /**
     * Set the source of the delimited string
     * 
     * @param in
     * @param charsetName
     */
    public void setSource(ReadableByteChannel in, String charsetName)
    {
        this.scanner = new Scanner(in, charsetName);
    }

    /**
     * Set the source of the delimited string
     * 
     * @param in
     */
    public void setSource(String in)
    {
        this.scanner = new Scanner(in.trim());
    }

    /**
     * Reset the transformer
     */
    public void reset()
    {
        this.messageDelimiters = null;
        this.rootElementName = null;
        this.scanner.close();
        this.scanner = null;
        this.tokens = null;
        this.tokenSeparator = null;
    }

    /**
     * Will apply the list of messageTokenizers on the source, sequentially. I.e
     * the second tokenization will apply on the results of the first one, the
     * third tokenization will apply to the results of the second one, and so
     * forth.
     * 
     */
    private void tokenizeSource()
    {
        for (String delimiter : this.messageDelimiters)
        {
            if (this.tokens == null)
            {
                this.tokens = new ArrayList<String>();
                scanner.useDelimiter(delimiter);
                while (scanner.hasNext())
                    tokens.add(scanner.next());

                scanner.close();
            }
            // Source has already been tokenised, so this is the delimiter to
            // use on the tokens TODO: Improve array copy etc
            else
            {
                ArrayList<String> tmp = new ArrayList<String>();
                for (String token : this.tokens)
                {
                    Scanner tokenScanner = new Scanner(token);
                    tokenScanner.useDelimiter(delimiter);
                    while (tokenScanner.hasNext())
                        tmp.add(tokenScanner.next());

                    tokenScanner.close();
                }
                this.tokens = new ArrayList<String>(tmp.size());
                this.tokens.addAll(tmp);
            }
        }
        this.tokens.trimToSize();
    }

    /**
     * 
     * @return A Document
     * @throws TransformerException
     * @throws ParserConfigurationException
     */
    private Document parseToDoc() throws ParserConfigurationException, TransformerException
    {
        // Create a new document and append the root element
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.newDocument();
        Element root = null;
        if (this.rootElementName != null)
        {
            root = doc.createElement(this.rootElementName);
        }
        else
        {
            root = doc.createElement("default");
        }
        doc.appendChild(root);
        // If the scanner has been initialised, tokenize the source
        if (this.scanner != null)
        {
            this.tokenizeSource();
        }
        else
        {
            logger.warn("No input to tokenize!");
        }
        // If tokens were found
        if (this.tokens != null)
        {
            // Create a new element
            Element el = null;
            // For each identified token
            for (String token : this.tokens)
            {
                // Split the token based on the pattern
                if (this.tokenSeparator != null)
                {
                    String[] fields = token.split(this.tokenSeparator);
                    if (fields.length > 2)
                    {
                        throw new TransformerException("Unexpected result from splitting token [" + token + "]");
                    }
                    // Default else
                    // create an element from the first split string
                    try
                    {
                        el = doc.createElement(fields[0]);
                    }
                    catch (DOMException e)
                    {
                        StringBuilder msg = new StringBuilder(128);
                        msg.append("Cannot create a valid XML element out of token [");
                        msg.append(token);
                        msg.append(']');
                        logger.warn(msg.toString(), e);
                        throw new TransformerException(msg.toString(), e);
                    }
                    // if the second does not exist or is size 0, create
                    // empty element
                    if (fields.length > 1) el.appendChild(doc.createTextNode(fields[1]));
                }
                else
                {
                    // Treat each token as an empty element. This will throw a
                    // org.w3c.dom.DOMException if the token is invalid as an
                    // element name
                    try
                    {
                        el = doc.createElement(token);
                    }
                    catch (DOMException e)
                    {
                        StringBuilder msg = new StringBuilder(128);
                        msg.append("Cannot create a valid XML element out of token [");
                        msg.append(token);
                        msg.append(']');
                        logger.warn(msg.toString(), e);
                        throw new TransformerException(msg.toString(), e);
                    }

                }
                root.appendChild(el);
            }
        }
        else
        {
            // No tokens were found, return the empty root element
            // NOTE: Returning a single element names after all the input can be
            // dangerous
            logger.warn("No tokens to create elements. Returning empty root!");
        }
        return doc;
    }

    /**
     * Get the XML String result
     * 
     * @return the XML string
     * @throws TransformerException
     * @throws ParserConfigurationException
     * @throws IOException
     */
    public String getXMLString() throws TransformerException, ParserConfigurationException, IOException
    {
        Document document = this.parseToDoc();
        DefaultDOMSerializer serializer = new DefaultDOMSerializer();
        return serializer.toString(document);
    }

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        DelimitedStringToFlatXMLTransformer p = new DelimitedStringToFlatXMLTransformer();

        String singleDelimiterCase = new String("|a=1|b=2|c=|d|e=1|         \n           ");
        p.setMessageDelimiters("\\|");
        p.setTokenSeparator("\\=");
        p.setSource(singleDelimiterCase);
        p.setRootElementName("test1");
        try
        {
            logger.info(p.getXMLString());
        }
        catch (Exception e)
        {
            logger.error(e);
        }

        p.reset();
        String multiDelimiterCase = new String("{|a=1|b=2}{|c=3|d=4|}");
        ArrayList<String> delimiters = new ArrayList<String>(3);
        delimiters.add("\\{");
        delimiters.add("\\}");
        delimiters.add("\\|");
        p.setMessageDelimiters(delimiters);
        p.setTokenSeparator("\\=");
        p.setSource(multiDelimiterCase);
        p.setRootElementName("test2");
        try
        {
            logger.info(p.getXMLString());
        }
        catch (Exception e)
        {
            logger.error(e);
        }

        String msg = new String(
            "|PayloadLength=21|MessageType=1|Timestamp=20070511064530567|Timezone=BST|Id=0|SourceSystem=cmi|DataSource=|DataType=0|Priority=0|Comment=AckMessage|ReasonCode=0|Reason=|");
        p = new DelimitedStringToFlatXMLTransformer(msg, "\\|", "test3", "\\=");
        try
        {
            logger.info(p.getXMLString());
        }
        catch (Exception e)
        {
            logger.error(e);
        }

        p = new DelimitedStringToFlatXMLTransformer("", "\\|", "test4", "\\=");
        try
        {
            logger.info(p.getXMLString());
        }
        catch (Exception e)
        {
            logger.error(e);
        }
    }
}
