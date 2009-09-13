/*
 * $Id$
 * $URL$
 * 
 * =============================================================================
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
 * =============================================================================
 */
package org.ikasan.common.util;

// Imported java classes
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

// Imported log4j classes
import org.apache.log4j.Logger;

import org.ikasan.common.CommonEnvironment;

/**
 * This class captures all environment variables set in Java system property option.
 * 
 * @author Ikasan Development Team
 */
public class Env implements CommonEnvironment
{
    /** Serial ID */
    private static final long serialVersionUID = 1L;

    /** The logger instance. */
    private static Logger logger = Logger.getLogger(Env.class);

    /** The default start variable marker <code>${</code>. */
    public static final String START_MARKER = "${";

    /** The default start variable marker <code>}</code>. */
    public static final String END_MARKER = "}";

    /** Must be public default constructor for the ResourceLoader. */
    public Env()
    {
        // Do Nothing
    }

    /**
     * Replaces any occurrences within the string of the form <code>startMarker&lt;variable.name&gt;endMarker</code>
     * with the value from the current system properties or environment and returns the string after performing all
     * substitutions.
     * <p>
     * Commonly, the variable markers are "${" and "}", in which case variables are indicated by ${variable.name} in the
     * string.
     * 
     * @param originalStr - a string (that may contain system properties or system environment) to be parsed and
     *            substituted. It may be <code>null</code>, in which case this method returns immediately with no
     *            effect.
     * @param startMarker - a start variable marker string.
     * @param endMarker - an end variable marker string.
     * @param screamOnError - a boolean value indicating whether to throw an exception if one of pre-defined exceptions
     *            occurs.
     * 
     * @exception IllegalArgumentException if the string contains a <code>startMarker</code> without a
     *                <code>endMarker</code>, or if the variable name contains whitespace(s) or starts with a digit, or
     *                if the variable name is empty.
     * 
     * @return the original string with the variables replaced, or <code>null</code> if the original string is
     *         <code>null</code>.
     */
    // Note: It may be possible to utilise JDK 1.5 java.util.regex package:-
    // - to validate the string passed in;
    // - to replace matches with appropriate system properties.
    // However, for now we use the good old way as solid regex construction and
    // testing may take a while.
    public String expandEnvVar(String originalStr, String startMarker, String endMarker, boolean screamOnError)
    {
        // Nothing to do, get out of here
        if (originalStr == null)
        {
            return null;
        }
        // Check both start and end variable markers
        if (startMarker == null || startMarker.length() == 0)
        {
            throw new NullPointerException("Start variable marker can't be empty");
        }
        if (endMarker == null || endMarker.length() == 0)
        {
            throw new NullPointerException("End variable marker can't be empty");
        }
        String newStr = new String(originalStr);
        int startMkrLen = startMarker.length();
        // Search for the next instance of 'startMarker'
        // from the 'prev' position
        int prev = 0, pos = -1;
        while ((pos = newStr.indexOf(startMarker, prev)) >= 0)
        {
            logger.debug("Original str =[" + newStr + "].");
            logger.debug("Tracking pos. =[" + prev + "].");
            logger.debug("StartMkr pos. =[" + pos + "].");
            // Search for the next instance of 'endMarker' and 'startMarker'
            int endMkrPos = newStr.indexOf(endMarker, pos);
            int startMkrPos = newStr.indexOf(startMarker, pos + startMkrLen);
            // No 'endMarker', scream!
            if (endMkrPos < 0 || (startMkrPos > 0 && startMkrPos < endMkrPos))
            {
                if (screamOnError)
                {
                    throw new IllegalArgumentException("Syntax error, make sure to close variable name with '"
                            + endMarker + "': [" + originalStr + "].");
                }
                // Default else
                prev = pos + 1;
                continue;
            }
            // Default Else, We've got the variable name
            String varName = newStr.substring(pos + startMkrLen, endMkrPos);
            logger.debug("Variable name =[" + varName + "].");
            // Validate the variable name
            if (varName == null || varName.length() == 0)
            {
                if (screamOnError)
                {
                    throw new IllegalArgumentException("Variable name can't be empty: [" + originalStr + "]");
                }
                // Default else
                prev = pos + 1;
                continue;
            }
            else if (Pattern.matches(".*\\s+.*", varName))
            {
                if (screamOnError)
                {
                    throw new IllegalArgumentException("No white space(s) allowed in variable name: [" + originalStr
                            + "]");
                }
                // Default else
                prev = pos + 1;
                continue;
            }
            else if (Character.isDigit(varName.charAt(0)))
            {
                if (screamOnError)
                {
                    throw new IllegalArgumentException("The first variable character can't be a digit: [" + originalStr
                            + "]");
                }
                // Default else
                prev = pos + 1;
                continue;
            }
            String variable = startMarker + varName + endMarker;
            // Try to find its property value from system properties
            // first and system environment, then perform substitution
            if (System.getProperty(varName) != null || System.getenv(varName) != null)
            {
                String varValue = (System.getProperty(varName) != null) ? System.getProperty(varName) : System
                    .getenv(varName);
                logger.debug("Var =[" + varName + " -> " + varValue + "].");
                newStr = newStr.replace(variable, varValue);
                logger.debug("New str =[" + newStr + "].");
                prev = pos;
            }
            else
            {
                logger.warn("No value for [" + variable + "].");
                prev = pos + 1;
            }
        }
        return newStr;
    }

    /**
     * Replaces any occurrences within the string of the form <code>startMarker&lt;variable.name&gt;endMarker</code>
     * with the value from the current system properties or environment and returns the string after performing all
     * substitutions.
     * <p>
     * Commonly, the variable markers are "${" and "}", in which case variables are indicated by ${variable.name} in the
     * string.
     * 
     * @param originalStr - a string (that may contain system properties or system environment) to be parsed and
     *            substituted. It may be <code>null</code>, in which case this method returns immediately with no
     *            effect.
     * @param startMarker - a start variable marker string.
     * @param endMarker - an end variable marker string.
     * 
     * @exception IllegalArgumentException if the string contains a <code>startMarker</code> without a
     *                <code>endMarker</code>, or if the variable name contains whitespace(s) or starts with a digit, or
     *                if the variable name is empty.
     * 
     * @return the original string with the variables replaced, or <code>null</code> if the original string is
     *         <code>null</code>.
     */
    public String expandEnvVar(String originalStr, String startMarker, String endMarker)
    {
        return expandEnvVar(originalStr, startMarker, endMarker, true);
    }

    /**
     * Replaces any occurrences within the string of the form <code>${variable.name}</code> with the value from the
     * current system properties or environment and returns the string after performing all substitutions.
     * 
     * @param originalStr - a string (that may contain system properties or system environment) to be parsed and
     *            substituted. It may be <code>null</code>, in which case this method returns immediately with no
     *            effect.
     * @param screamOnError - a boolean value indicating whether to throw an exception if one of pre-defined exceptions
     *            occurs.
     * 
     * @exception IllegalArgumentException if the string contains a <code>startMarker</code> without a
     *                <code>endMarker</code>, or if the variable name contains whitespace(s) or starts with a digit, or
     *                if the variable name is empty.
     * 
     * @return the original string with the properties replaced, or <code>null</code> if the original string is
     *         <code>null</code>.
     */
    public String expandEnvVar(String originalStr, boolean screamOnError)
    {
        return expandEnvVar(originalStr, START_MARKER, END_MARKER, screamOnError);
    }

    /**
     * Replaces any occurrences within the string of the form <code>${variable.name}</code> with the value from the
     * current system properties or environment and returns the string after performing all substitutions.
     * 
     * @param originalStr - a string (that may contain system properties or system environment) to be parsed and
     *            substituted. It may be <code>null</code>, in which case this method returns immediately with no
     *            effect.
     * 
     * @exception IllegalArgumentException if the string contains a <code>startMarker</code> without a
     *                <code>endMarker</code>, or if the variable name contains whitespace(s) or starts with a digit, or
     *                if the variable name is empty.
     * 
     * @return the original string with the properties replaced, or <code>null</code> if the original string is
     *         <code>null</code>.
     */
    public String expandEnvVar(String originalStr)
    {
        return expandEnvVar(originalStr, START_MARKER, END_MARKER, true);
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
        String testString = "Hello, ${user.name}! " + "You are running on '${os.name}'. " + "Nice to see you. Ta!";
        Properties props = new Properties();
        // Parse the command-line parameters
        for (int i = 0; i < args.length; i++)
        {
            // Display usage then get outta here
            if (args[i].equalsIgnoreCase("-help"))
            {
                usage();
                System.exit(1);
            }
            // String to be examined
            else if (args[i].equalsIgnoreCase("-str"))
            {
                testString = args[++i].trim();
            }
            // Set system properties if any
            else if (args[i].equalsIgnoreCase("-prop"))
            {
                String name = new String(args[++i].trim());
                String value = new String(args[++i].trim());
                props.setProperty(name, value);
            }
            // Dunno what to do
            else
            {
                System.err.println("Invalid option - [" + args[i] + "].");
                usage();
                System.exit(1);
            }
        }
        System.out.println("");
        System.out.println("Test string =[" + testString + "].");
        System.out.println("Properties =[" + props + "].");
        System.out.println("");
        // Set system properties if any
        if (props.size() > 0)
        {
            for (Map.Entry<Object, Object> entry : props.entrySet())
            {
                System.setProperty((String) entry.getKey(), (String) entry.getValue());
            }
        }
        try
        {
            Env env = new Env();
            // Keep on asking until we've got zero "0" (to exit) as answer
            String question = "", answer = "";
            while (!answer.equalsIgnoreCase("0"))
            {
                // Which method would you like to test?
                question = "Which method do you want to test?\n" + "  0. exit(Get outta here!!)\n"
                        + "  1. expandEnvVar(String originalStr)\n"
                        + "  2. expandEnvVar(String originalStr, boolean screamOnError)\n"
                        + "  3. expandEnvVar(String originalStr, String startMarker, String endMarker)\n"
                        + "  4. displaySystemProps()\n" + "  5. displaySystemEnv()\n";
                answer = askQuestion(question);
                if (!answer.matches("[1-9][0-9]*")) continue;
                // We've got one of the above numbers
                // ask more questions
                // then call the selected method and return the result
                try
                {
                    // expandEnvVar(String originalStr)
                    if (answer.equals("1"))
                    {
                        String prop = null, name = null, value = null;
                        question = "Want to set more properties (y/n)?";
                        prop = getString(prop, question);
                        while (prop.equalsIgnoreCase("y"))
                        {
                            question = "Proprty name (" + name + ")?";
                            name = getString(name, question);
                            question = "Property value (" + value + ")?";
                            value = getString(value, question);
                            System.setProperty(name, value);
                            question = "Want to set more properties (y/n)?";
                            prop = getString(prop, question);
                        }
                        question = "Test string (" + testString + ")?";
                        testString = getString(testString, question);
                        System.out.println("Result =[" + env.expandEnvVar(testString) + "].");
                    }
                    // expandEnvVar(String originalStr, boolean screamOnError)
                    else if (answer.equals("2"))
                    {
                        String prop = null, name = null, value = null;
                        boolean screamOnError = true;
                        question = "Want to set more properties (y/n)?";
                        prop = getString(prop, question);
                        while (prop.equalsIgnoreCase("y"))
                        {
                            question = "Proprty name (" + name + ")?";
                            name = getString(name, question);
                            question = "Property value (" + value + ")?";
                            value = getString(value, question);
                            System.setProperty(name, value);
                            question = "Want to set more properties (y/n)?";
                            prop = getString(prop, question);
                        }
                        question = "Test string (" + testString + ")?";
                        testString = getString(testString, question);
                        question = "Scream on error (" + screamOnError + ")?";
                        screamOnError = getBoolean(screamOnError, question);
                        System.out.println("Result =[" + env.expandEnvVar(testString, screamOnError) + "].");
                    }
                    // expandEnvVar(String originalStr, String startMarker,
                    // String endMarker)
                    else if (answer.equals("3"))
                    {
                        String startMarker = START_MARKER;
                        String endMarker = END_MARKER;
                        String prop = null, name = null, value = null;
                        question = "Start marker (" + startMarker + ")?";
                        startMarker = getString(startMarker, question);
                        question = "End marker (" + endMarker + ")?";
                        endMarker = getString(endMarker, question);
                        question = "Want to set more properties (y/n)?";
                        prop = getString(prop, question);
                        while (prop.equalsIgnoreCase("y"))
                        {
                            question = "Proprty name (" + name + ")?";
                            name = getString(name, question);
                            question = "Property value (" + value + ")?";
                            value = getString(value, question);
                            System.setProperty(name, value);
                            question = "Want to set more properties (y/n)?";
                            prop = getString(prop, question);
                        }
                        question = "Test string (" + testString + ")?";
                        testString = getString(testString, question);
                        System.out.println("Result =[" + env.expandEnvVar(testString, startMarker, endMarker) + "].");
                    }
                    // displaySystemProps()
                    else if (answer.equals("4"))
                    {
                        displaySystemProps();
                    }
                    // displaySystemEnv()
                    else if (answer.equals("5"))
                    {
                        displaySystemEnv();
                    }
                    else
                    {
                        System.out.println("Invalid number! " + "Please select one of the following numbers.");
                    }
                }
                catch (Exception e)
                {
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
     * Displays the usage for main() method.
     * 
     */
    private static void usage()
    {
        String fqClassName = Env.class.getName();
        System.err.println("");
        System.err.println("Usage:");
        System.err.println("java " + fqClassName + " [-options]");
        System.err.println("");
        System.err.println("where options include:");
        System.err.println("    -str <string>  to specify a test string to be parsed");
        System.err.println("    -prop <name> <value>");
        System.err.println("                   to set additional system property.");
        System.err.println("                   Property name and value must be delimited by a space.");
        System.err.println("                   For example, 'ikasan.home here'");
        System.err.println("");
    }

    /**
     * Asks a simple question, waits for response and finally returns an answer as a String. It returns the default
     * value if the answer is empty provided the default value is not null. This is ONLY used in main() method.
     * 
     * @param str The default value
     * @param question The question
     * @return The response
     */
    private static String getString(String str, String question)
    {
        return (str != null) ? askQuestion(question, str) : askQuestion(question);
    }

    /**
     * Asks a simple question, waits for response and finally returns an answer as boolean. It returns the default value
     * if the answer is empty provided the default value is not null. This is used in main() method.
     * 
     * @param flag The default value
     * @param question The question
     * @return true or false response
     */
    private static boolean getBoolean(boolean flag, String question)
    {
        String str = askQuestion(question, "" + flag);
        return (new Boolean(str)).booleanValue();
    }

    /**
     * Asks a simple question, waits for response and finally returns an answer. It returns the default value if the
     * answer is empty provided the default value is not null. This is ONLY used in main() method.
     * 
     * @param question The question
     * @param defValue The default value
     * @return the response
     */
    private static String askQuestion(String question, String defValue)
    {
        String response = "";
        do
        {
            System.out.println(question);
            try
            {
                java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(System.in));
                response = br.readLine();
                response = (response != null) ? response : "";
            }
            catch (java.io.IOException e)
            {
                System.err.println("Error while reading answer: '" + e + "'");
                response = null;
            }
        }
        while (defValue == null && response != null && response.trim().length() == 0);
        return (response != null && response.trim().length() > 0) ? response : defValue;
    }

    /**
     * Asks a simple question, waits for response and finally returns an answer. It keeps asking until it gets
     * something. This is ONLY used in main() method.
     * 
     * @param question The question
     * @return the response
     */
    private static String askQuestion(String question)
    {
        return askQuestion(question, null);
    }

    /**
     * Displays all values from the current system properties. Very convenient method. Run it through main().
     */
    private static void displaySystemProps()
    {
        for (Map.Entry<Object, Object> entry : System.getProperties().entrySet())
        {
            System.out.println("[" + entry.getKey() + "]" + " -> " + "[" + entry.getValue() + "]");
        }
    }

    /**
     * Displays all values from the current system environment. Very convenient method. Run it through main().
     */
    private static void displaySystemEnv()
    {
        for (Map.Entry<String, String> entry : System.getenv().entrySet())
        {
            System.out.println("[" + entry.getKey() + "]" + " -> " + "[" + entry.getValue() + "]");
        }
    }
}
