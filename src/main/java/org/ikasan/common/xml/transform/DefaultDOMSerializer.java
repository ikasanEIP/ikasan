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
package org.ikasan.common.xml.transform;

// Imported ikasan classes
import org.ikasan.common.CommonXMLTransformer;
import org.ikasan.common.xml.parser.DefaultXMLParser;

// Imported java classes
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Properties;

// Imported jaxp classes
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

// Imported trax classes
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

// Imported w3c classes
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * This class serialises the specified DOM tree
 * with the given object holding the result.
 *
 * <pre>
 *    Usage Example:
 *
 *    Document doc = createMyXmlDocument(xml);
 *
 *    DefaultDOMSerializer serializer = new DefaultDOMSerializer();
 *    serializer.seDefaultOutputProperties();
 *    String xmlString = serializer.toString(doc);
 *    System.out.println("===== XML OUTPUT =====");
 *    System.out.println(xmlString);
 *
 * </pre>
 *
 * @author Jun Suetake
 * @version $Id$
 */
public class DefaultDOMSerializer
    implements CommonXMLTransformer
{
    /**
     * The transformer is configured to produce indented XML output.
     */
    protected static final String XALAN_INDENT_AMOUNT
        = "{http://xml.apache.org/xalan}indent-amount";

    /**
     * The output properties for the transformation.
     */
    private Properties outputProps = new Properties();

    /**
     * Creates a new <code>DefaultDOMSerializer</code> instance
     * with the default trace level.
     */
    public DefaultDOMSerializer()
    {
        // Do Nothing
    }

    /**
     * Convenient method to set the output result
     * to be XML with indentation amount of 2.
     *
     */
    public void seDefaultOutputProperties()
    {
        this.outputProps.setProperty(OutputKeys.METHOD, "xml");
        this.outputProps.setProperty(OutputKeys.INDENT, "yes");
        this.outputProps.setProperty(XALAN_INDENT_AMOUNT, "2");
    }

    /**
     * Sets an output property that will be in effect for the transformation.
     * 
     * @param name 
     * @param value 
     */
    public void setOutputProperty(String name, String value)
    {
        if (name == null || name.trim().length() == 0) return;
        if (value == null || value.trim().length() == 0) return;
        this.outputProps.setProperty(name, value);
    }

    /**
     * Adds the new output properties for the transformation to the current
     * ones.
     * 
     * @param props 
     */
    public void setOutputProperties(Properties props)
    {
        if (props == null) return;
        this.outputProps.putAll(props);
    }

    /**
     * Clears the current output properties.
     *
     */
    public void clearOutputProperties()
    {
        this.outputProps.clear();
    }

    /**
     * Serialises the specified DOM tree with the given object holding the result.
     * 
     * @param document 
     * @param obj 
     * @throws TransformerException 
     */
    private void serialize(Document document, Object obj)
        throws TransformerException
    {
        if (document == null)
            throw new NullPointerException("XML document can't be null");
        if (obj == null)
            throw new NullPointerException("Result instance can't be null");

        Transformer serializer =
            TransformerFactory.newInstance().newTransformer();
        serializer.setOutputProperties(this.outputProps);

        StreamResult result = null;
        if (obj instanceof File)
            result = new StreamResult((File)obj);
        else
        if (obj instanceof Writer)
            result = new StreamResult((Writer)obj);
        else
            throw new IllegalArgumentException("Unknown result object: "
                                             + obj.getClass().getName());

        serializer.transform(new DOMSource(document), result);
    }

    /**
     * Writes the specified DOM tree to the specified file.
     * 
     * @param document 
     * @param xmlFile 
     * @throws TransformerException 
     */
    public void toFile(Document document, File xmlFile)
        throws TransformerException
    {
        this.serialize(document, xmlFile);
    }

    /**
     * Writes the specified DOM tree to the specified file.
     * @param document 
     * @param xmlFileName 
     * @throws IOException 
     * @throws TransformerException 
     *
     */
    public void toFile(Document document, String xmlFileName)
        throws IOException, TransformerException
    {
        if (xmlFileName == null)
            throw new NullPointerException("XML file name can't be null");
        if (xmlFileName.trim().length() == 0)
            throw new IllegalArgumentException("XML file name can't be empty");

        this.toFile(document, new File(xmlFileName));
    }

    /**
     * Writes the specified DOM tree to the default file.
     * @param document 
     * @throws IOException 
     * @throws TransformerException 
     *
     */
    public void toFile(Document document)
        throws IOException, TransformerException
    {
        String rootName = "out";
        if (document != null)
            rootName = ((Node)document.getDocumentElement()).getNodeName();
        this.toFile(document, rootName + ".xml");
    }

    /**
     * Serialises the specified DOM tree and
     * returns the result as <code>String</code>.
     * @param document 
     * @return String
     * @throws IOException 
     * @throws TransformerException 
     *
     */
    public String toString(Document document)
        throws IOException, TransformerException
    {
        StringWriter out = new StringWriter();
        this.serialize(document, out);
        out.flush();
        out.close();
        return out.getBuffer().toString();
    }

// main() method
///////////////////////////////////////////////////////////////////////////////

    /**
     * Runs this class for testing.  TODO Unit Test
     * 
     * @param args 
     */
    public static void main(String[] args)
    {
        String defXmlLabel = "default";
        String xmlInURI  = null;
        String xmlOutURI = null;
        boolean outProps = true;

        // Parse the command-line parameters
        for (int i = 0; i < args.length; i++)
        {
            // Display usage then get outta here
            if (args[i].equalsIgnoreCase("-help"))
            {
                usage();
                System.exit(1);
            }
            // Input XML URI
            else if (args[i].equalsIgnoreCase("-in"))
            {
                xmlInURI = args[++i].trim();
            }
            // Output XML URI
            else if (args[i].equalsIgnoreCase("-out"))
            {
                xmlOutURI = args[++i].trim();
            }
            // Set default output properties
            else if (args[i].equalsIgnoreCase("-defprops"))
            {
                outProps = true;
            }
            // Don't know what to do
            else
            {
                System.err.println("Invalid option - [" + args[i] + "].");
                usage();
                System.exit(1);
            }
        }

        System.out.println("");
        System.out.println("XML input file   =[" + xmlInURI + "].");
        System.out.println("XML output file  =[" + xmlOutURI + "].");
        System.out.println("Def output props =[" + outProps + "].");
        System.out.println("");

        try
        {
            // Instantiate this class with trace level
            DefaultDOMSerializer serializer = new DefaultDOMSerializer();

            // Keep on asking until we've got "0" (to exit) as answer
            String question = "";
            String answer = "";
            Document document = defaultDoc;
            while (!answer.equalsIgnoreCase("0"))
            {
                // Which method would you like to test?
                question = "Which method do you want to test?\n"
                    + "  0. exit(Get outta here!!)\n"
                    + "  1. toFile(Document document, String fileName)\n"
                    + "  2. toString(Document document)\n";
                answer = askQuestion(question);

                if (!answer.matches("[1-9][0-9]*")) continue;

                // We've got one of the above numbers
                // ask more questions
                // then call the selected method and return the result
                try
                {
                    xmlInURI = (xmlInURI != null && !xmlInURI.equals(defXmlLabel))
                             ? xmlInURI : defXmlLabel;
                    question = "XML URI (" + xmlInURI + ")?";
                    xmlInURI = getString(xmlInURI, question);
                    question = "Use default ouput properties (" + outProps
                             + ")?";
                    outProps = getBoolean(outProps, question);

                    if (xmlInURI != null && !xmlInURI.equals(defXmlLabel))
                    {
                        DefaultXMLParser parser = new DefaultXMLParser();
                        document = parser.parse(xmlInURI);
                    }
                    else
                    {
                        document = defaultDoc;
                    }
                    if (outProps)
                        serializer.seDefaultOutputProperties();
                    else
                        serializer.clearOutputProperties();

                    // Call the selected method and return the result
                    if (answer.equals("1"))
                    {
                        question = "XML file name (" + xmlOutURI + ")?";
                        xmlOutURI = getString(xmlOutURI, question);
                        System.out.println("Generating XML file...");
                        serializer.toFile(document, xmlOutURI);
                        System.out.println("Successful!");
                    }
                    else
                    if (answer.equals("2"))
                    {
                        System.out.println("===== XML OUTPUT =====");
                        System.out.println(serializer.toString(document));
                    }
                    else
                    {
                        System.out.println("Invalid number! "
                            + "Please select one of the following numbers.");
                    }
                }
                catch (Exception e)
                {
                    // Log it and carry on
                    e.printStackTrace(System.err);
                }

                System.out.println("");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace(System.err);
        }

        System.exit(0);
    }

    /**
     * Asks a simple question, waits for response and
     * finally returns an answer as string. It returns the default value
     * if the answer is empty provided the default value is not null.
     * This is used in main() method.
     * 
     * @param str 
     * @param question 
     * @return answer 
     */
    private static String getString(String str, String question)
    {
        return (str != null)
            ? askQuestion(question, str) : askQuestion(question);
    }

    /**
     * Asks a simple question, waits for response and
     * finally returns an answer as boolean. It returns the default value
     * if the answer is empty provided the default value is not null.
     * This is used in main() method.
     * 
     * @param flag 
     * @param question 
     * @return boolean answer 
     */
    private static boolean getBoolean(boolean flag, String question)
    {
        String str = askQuestion(question, "" + flag);
        return (new Boolean(str)).booleanValue();
    }

    /**
     * Asks a simple question, waits for response and
     * finally returns an answer. It returns the default value
     * if the answer is empty provided the default value is not null.
     * This is used in main() method.
     * 
     * @param question 
     * @param defValue 
     * @return The response 
     */
    private static String askQuestion(String question, String defValue)
    {
        String response = "";
        do
        {
            System.out.println(question);
            try
            {
                BufferedReader br = new BufferedReader(
                                      new InputStreamReader(System.in));
                response = br.readLine();
                response = (response != null) ? response : "";
            }
            catch (IOException e)
            {
                System.err.println("Error while reading answer: '" + e + "'");
                response = null;
            }
        }
        while (defValue == null && response != null && response.trim().length() == 0);

        return (response != null && response.trim().length() > 0) ? response : defValue;
    }

    /**
     * Asks a simple question, waits for response and
     * finally returns an answer. It keeps asking until it gets something.
     * This is used in main() method.
     * 
     * @param question 
     * @return answer 
     */
    private static String askQuestion(String question)
    {
        return askQuestion(question, null);
    }

    /**
     * Displays the usage for main() method.
     *
     */
    private static void usage()
    {
        String fqClassName = DefaultDOMSerializer.class.getName();
        System.err.println("");
        System.err.println("Usage:");
        System.err.println("java " + fqClassName + " [-options]");
        System.err.println("");
        System.err.println("where options include:");
        System.err.println("    -in <input XML>");
        System.err.println("                   to specify input XML file name (empty XML)");
        System.err.println("    -out <output>  to specify output file name (System.out)");
        System.err.println("    -param <name> <value>");
        System.err.println("                   to set a name-value paired parameter");
        System.err.println("                   The parameter must be delimited by a space");
        System.err.println("                   Set parameters as many as you can");
        System.err.println("    -defprops      to set the default output properties");
        System.err.println("                   that are XMl with indentation amount of 2 (false)");
        System.err.println("    -indent        to generate output XML with indentation (false)");
        System.err.println("");
    }

    /** Default XML document used in main() method */
    protected static Document defaultDoc = null;

    static
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try
        {
            DocumentBuilder builder = factory.newDocumentBuilder();
            defaultDoc = builder.newDocument();

            Element root = defaultDoc.createElement("architectureTeam");

            root.appendChild(defaultDoc.createComment("Actually we need "
                                                    + "some hottiiiies!!"));

            Element element = defaultDoc.createElement("member");
            element.appendChild(defaultDoc.createTextNode("Jeff Mitchell"));
            element.setAttribute("AKA1", "Poof-san");
            element.setAttribute("EXT_NO", "6152");
            root.appendChild(element);

            element = defaultDoc.createElement("member");
            element.appendChild(defaultDoc.createTextNode("Herodotos Koukkides"));
            element.setAttribute("EXT_NO", "6548");
            root.appendChild(element);

            element = defaultDoc.createElement("member");
            element.appendChild(defaultDoc.createTextNode("Madhu Konda"));
            element.setAttribute("EXT_NO", "6213");
            root.appendChild(element);

            element = defaultDoc.createElement("member");
            element.appendChild(defaultDoc.createTextNode("Jun Suetake"));
            element.setAttribute("NICK_NAME", "Fine Fellow");
            element.setAttribute("EXT_NO", "6756");
            root.appendChild(element);

            defaultDoc.appendChild(root);
        }
        catch (Exception e)
        {
            // Do nothing
            // e.printStackTrace(System.err);
        }
    }

}
