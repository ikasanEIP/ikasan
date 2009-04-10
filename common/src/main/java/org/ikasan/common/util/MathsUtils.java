/*
 * $Id: MathsUtils.java 16606 2009-04-09 08:07:33Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/common/src/main/java/org/ikasan/common/util/MathsUtils.java $
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
package org.ikasan.common.util;

// Imported java classes
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;

/**
 * This class provides miscellaneous maths specific utilities.
 *
 * @author Jun Suetake
 */
public class MathsUtils
{

    /**
     * Don't let anyone instantiate this class.
     */
    private MathsUtils()
    {
        // Do Nothing
    }

    /**
     * Sets the number of decimal places of the specified number string to
     * the given value. For example, <code>formatDecimal("123456", 2, 6)</code>
     * becomes "<code>1234.56</code>". Available rounding modes are as follows
     * (see <code>java.math.BigDecimal</code> for full explanation):-<br>
     * <ul>
     *   <li>0 : ROUND_UP</li>
     *   <li>1 : ROUND_DOWN</li>
     *   <li>2 : ROUND_CEILING</li>
     *   <li>3 : ROUND_FLOOR</li>
     *   <li>4 : ROUND_HALF_UP</li>
     *   <li>5 : ROUND_HALF_DOWN</li>
     *   <li>6 : ROUND_HALF_EVEN</li>
     *   <li>7 : ROUND_UNNECESSARY</li>
     * </ul>
     *
     * @param numberStr      is the big decimal value.
     * @param numOfDecPlaces is the number of decimal places.
     * @param roundingMode   is the rounding mode to apply.
     * @return The decimal as a string
     */
    public static String setDecimalPlaces(String numberStr, int numOfDecPlaces,
                                          int roundingMode)
    {
        if (numberStr == null)
            throw new NullPointerException(
                "BigDecimal value can't be null");
        if (numberStr.trim().length() == 0)
            throw new IllegalArgumentException(
                "BigDecimal value can't be empty");
        if (numOfDecPlaces < 0)
            throw new ArithmeticException(
                "Negative number of decimal places: '" + numOfDecPlaces + "'");
        if (roundingMode < BigDecimal.ROUND_UP
         || roundingMode > BigDecimal.ROUND_UNNECESSARY)
            throw new IllegalArgumentException(
                "Invalid rounding mode: '" + roundingMode + "'");

        BigDecimal bigDec = new BigDecimal(numberStr);
        bigDec = bigDec.setScale(numOfDecPlaces, roundingMode);

        return bigDec.toString();
    }

    /**
     * Sets the number of decimal places of the specified number string to
     * the given value.
     *
     * @param numberStr      is the big decimal value.
     * @param numOfDecPlaces is the number of decimal places.
     * @return the decimal as a string
     */
    public static String setDecimalPlaces(String numberStr, int numOfDecPlaces)
    {
        return setDecimalPlaces(numberStr, numOfDecPlaces,
                                BigDecimal.ROUND_UNNECESSARY);
    }

    /**
     * Sets the number of decimal places of the specified number string to
     * the given value.
     *
     * @param numberStr      is the big decimal value.
     * @param numOfDecPlaces is the number of decimal places.
     * @param roundingMode   is the rounding mode to apply.
     * @return the decimal as a string
     */
    public static String setDecimalPlaces(String numberStr,
                                          String numOfDecPlaces,
                                          String roundingMode)
    {
        if (numOfDecPlaces == null)
            throw new NullPointerException(
                "Number of decimal places can't be null");
        if (numOfDecPlaces.trim().length() == 0)
            throw new IllegalArgumentException(
                "Number of decimal places can't be empty");
        if (roundingMode == null)
            throw new NullPointerException("Rounding mode can't be null");
        if (roundingMode.trim().length() == 0)
            throw new IllegalArgumentException("Rounding mode can't be empty");

        return setDecimalPlaces(numberStr, Integer.parseInt(numOfDecPlaces),
                                Integer.parseInt(roundingMode));
    }

    /**
     * Sets the number of decimal places of the specified number string to
     * the given value.
     *
     * @param numberStr      is the big decimal value.
     * @param numOfDecPlaces is the number of decimal places.
     * @return the decimal as a string
     */
    public static String setDecimalPlaces(String numberStr,
                                          String numOfDecPlaces)
    {
        return setDecimalPlaces(numberStr, numOfDecPlaces,
                                String.valueOf(BigDecimal.ROUND_UNNECESSARY));
    }

    /**
     * Returns the resultant string of (numberStr / divisorStr).
     *
     * @param numberStr      is the big decimal value.
     * @param divisorStr     is the divisor string.
     * @param numOfDecPlaces is the number of decimal places.
     * @param roundingMode   is the rounding mode to apply.
     * @return the decimal as a string
     */
    public static String divide(String numberStr, String divisorStr,
                                int numOfDecPlaces, int roundingMode)
    {
        if (numberStr == null)
            throw new NullPointerException(
                "BigDecimal value can't be null");
        if (numberStr.trim().length() == 0)
            throw new IllegalArgumentException(
                "BigDecimal value can't be empty");
        if (divisorStr == null)
            throw new NullPointerException(
                "Divisor value can't be null");
        if (divisorStr.trim().length() == 0)
            throw new IllegalArgumentException(
                "Divisor value can't be empty");
        if (numOfDecPlaces < 0)
            throw new ArithmeticException(
                "Negative number of decimal places: '" + numOfDecPlaces + "'");
        if (roundingMode < BigDecimal.ROUND_UP
         || roundingMode > BigDecimal.ROUND_UNNECESSARY)
            throw new IllegalArgumentException(
                "Invalid rounding mode: '" + roundingMode + "'");

        BigDecimal bigDec  = new BigDecimal(numberStr);
        BigDecimal divisor = new BigDecimal(divisorStr);
        BigDecimal rv = bigDec.divide(divisor, numOfDecPlaces, roundingMode);

        return rv.toString();
    }

    /**
     * Returns the resultant string of (numberStr / divisorStr).
     *
     * @param numberStr    is the big decimal value.
     * @param divisorStr   is the divisor string.
     * @param roundingMode is the rounding mode to apply.
     * @return the result as a string
     */
    public static String divide(String numberStr, String divisorStr,
                                int roundingMode)
    {
        return divide(numberStr, divisorStr,
                      new BigDecimal(numberStr).scale(), roundingMode);
    }

    /**
     * Returns the resultant string of (numberStr / divisorStr).
     *
     * @param numberStr      is the big decimal value.
     * @param divisorStr     is the divisor string.
     * @param numOfDecPlaces is the number of decimal places.
     * @param roundingMode   is the rounding mode to apply.
     * @return the result as a string
     */
    public static String divide(String numberStr, String divisorStr,
                                String numOfDecPlaces, String roundingMode)
    {
        if (numOfDecPlaces == null)
            throw new NullPointerException(
                "Number of decimal places can't be null");
        if (numOfDecPlaces.trim().length() == 0)
            throw new IllegalArgumentException(
                "Number of decimal places can't be empty");
        if (roundingMode == null)
            throw new NullPointerException("Rounding mode can't be null");
        if (roundingMode.trim().length() == 0)
            throw new IllegalArgumentException("Rounding mode can't be empty");

        return divide(numberStr, divisorStr,
                      Integer.parseInt(numOfDecPlaces),
                      Integer.parseInt(roundingMode));
    }

    /**
     * Returns the resultant string of (numberStr / divisorStr).
     *
     * @param numberStr    is the big decimal value.
     * @param divisorStr   is the divisor string.
     * @param roundingMode is the rounding mode to apply.
     * @return the result as a string
     */
    public static String divide(String numberStr, String divisorStr,
                                String roundingMode)
    {
        return divide(numberStr, divisorStr,
                      String.valueOf(new BigDecimal(numberStr).scale()),
                      roundingMode);
    }

    /**
     * Returns the boolean flag indicating whether the specified set bit is set
     * on the given string that represents integer value.
     *
     * @param value is the string that represents integer value
     *                 between 0 and 255 inclusive.
     * @param bit   is the string that represents bit.
     * @return true if the bit is set
     */
    public static boolean isBitSet(int value, int bit)
    {
        if (value > 255 || value < 0)
            throw new IllegalArgumentException(
                "The value must be between 0 and 255 inclusive: '" +
                value + "'");
        if (bit > 7 || bit < 0)
            throw new IllegalArgumentException(
                "The bit must be between 0 and 7 inclusive: '" + bit + "'");

        int mask = 1 << bit;
        return ((value & mask) > 0);
    }

    /**
     * Returns the boolean flag indicating whether the specified set bit is set
     * on the given string that represents integer value.
     *
     * @param value is the string that represents integer value
     *                 between 0 and 255 inclusive.
     * @param bit   is the string that represents bit.
     * @return &quot;true&quot; if the bit is set
     */
    public static String isBitSet(String value, String bit)
    {
        if (value == null)
            throw new NullPointerException("Value can't be null");
        if (value.trim().length() == 0)
            throw new IllegalArgumentException("Value can't be empty");
        if (bit == null)
            throw new NullPointerException("Bit can't be null");
        if (bit.trim().length() == 0)
            throw new IllegalArgumentException("Bit can't be empty");

        return String.valueOf(isBitSet(Integer.parseInt(value),
                                       Integer.parseInt(bit)));
    }

// main() method
///////////////////////////////////////////////////////////////////////////////

    /**
     * Runs this class for testing.
     * 
     * TODO Unit Test
     * 
     * @param args 
     */
    public static void main(String args[])
    {
        String value        = "1234567890";
        String decPlaces    = "2";
        String roundingMode = "" + BigDecimal.ROUND_HALF_EVEN;
        String divisor      = "1.2";
        String bitStr       = "1";

        // Parse the command-line parameters
        for (int i = 0; i < args.length; i++)
        {
            // Display usage then get outta here
            if (args[i].equalsIgnoreCase("-help"))
            {
                usage();
                System.exit(1);
            }
            // BigDecimal value to be formatted
            else if (args[i].equalsIgnoreCase("-value"))
            {
                value = args[++i].trim();
            }
            // Number of decimal places
            else if (args[i].equalsIgnoreCase("-dps"))
            {
                decPlaces = args[++i].trim();
            }
            // Rounding mode
            else if (args[i].equalsIgnoreCase("-round"))
            {
                roundingMode = args[++i].trim();
            }
            // Divisor
            else if (args[i].equalsIgnoreCase("-divisor"))
            {
                divisor = args[++i].trim();
            }
            // Bit
            else if (args[i].equalsIgnoreCase("-bit"))
            {
                bitStr = args[++i].trim();
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
        System.out.println("Value       =[" + value + "].");
        System.out.println("Decimal pls =[" + decPlaces + "].");
        System.out.println("Divisor     =[" + divisor + "].");
        System.out.println("Bit         =[" + bitStr + "].");
        System.out.println("");

        try
        {
            // Keep on asking until we've got zero "0" (to exit) as answer
            String question = "", answer = "";
            while (!answer.equalsIgnoreCase("0"))
            {
                // Which method would you like to test?
                question = "Which method do you want to test?\n"
                    + "  0. exit(Get outta here!!)\n"
                    + "  1. Set decimal places\n"
                    + "  2. Division\n"
                    + "  3. Check bitSet\n";
                answer = askQuestion(question);

                if (!answer.matches("[1-9][0-9]*")) continue;

                // We've got one of the above numbers
                // ask more questions
                // then call the selected method and return the result
                try
                {
                    // Set decimal places
                    if (answer.equals("1"))
                    {
                        question = "Big decimal value (" + value + ")?";
                        value = getString(value, question);
                        question = "Number of decimal places ("
                                 + decPlaces + ")?";
                        decPlaces = getString(decPlaces, question);
                        question = "Rounding mode (" + roundingMode + ")?";
                        roundingMode = getString(roundingMode, question);

                        System.out.println("Result =["
                            + MathsUtils.setDecimalPlaces(value, decPlaces,
                                                          roundingMode) + "]");
                    }
                    // Division
                    else
                    if (answer.equals("2"))
                    {
                        question = "Big decimal value (" + value + ")?";
                        value = getString(value, question);
                        question = "Divisor value (" + divisor + ")?";
                        divisor = getString(divisor, question);
                        question = "Number of decimal places ("
                                 + decPlaces + ")?";
                        decPlaces = getString(decPlaces, question);
                        question = "Rounding mode (" + roundingMode + ")?";
                        roundingMode = getString(roundingMode, question);

                        System.out.println("Result =["
                            + MathsUtils.divide(value, divisor,
                                                decPlaces, roundingMode) + "]");
                    }
                    // Check bit set
                    else
                    if (answer.equals("3"))
                    {
                        question = "Value (" + value + ")?";
                        value = getString(value, question);
                        question = "Bit (" + bitStr + ")?";
                        bitStr = getString(bitStr, question);

                        System.out.println("Result =["
                            + MathsUtils.isBitSet(value, bitStr) + "]");
                    }
                    else
                    {
                        System.out.println("Invalid number! "
                            + "Please select one of the following numbers.");
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
        String fqClassName = MathsUtils.class.getName();
        System.err.println("");
        System.err.println("Usage:");
        System.err.println("java " + fqClassName + " [-options]");
        System.err.println("");
        System.err.println("where options include:");
        System.err.println("    -value <value> to specify a BigDecimal value to be formatted (1234567890)");
        System.err.println("    -dps <decimal places>");
        System.err.println("                   to specify a number of decimal places (2)");
        System.err.println("    -round <mode>  to specify rounding mode (" + BigDecimal.ROUND_HALF_EVEN + ")");
        System.err.println("                   Available modes are:-");
        System.err.println("                     ROUND_UP (" + BigDecimal.ROUND_UP + ")");
        System.err.println("                     ROUND_DOWN (" + BigDecimal.ROUND_DOWN + ")");
        System.err.println("                     ROUND_CEILING (" + BigDecimal.ROUND_CEILING + ")");
        System.err.println("                     ROUND_FLOOR (" + BigDecimal.ROUND_FLOOR + ")");
        System.err.println("                     ROUND_HALF_UP (" + BigDecimal.ROUND_HALF_UP + ")");
        System.err.println("                     ROUND_HALF_DOWN (" + BigDecimal.ROUND_HALF_DOWN + ")");
        System.err.println("                     ROUND_HALF_EVEN (" + BigDecimal.ROUND_HALF_EVEN + ")");
        System.err.println("                     ROUND_UNNECESSARY (" + BigDecimal.ROUND_UNNECESSARY + ")");
        System.err.println("    -divisor <divisor>");
        System.err.println("                   to specify a divisor (1.2)");
        System.err.println("    -bit <bit>     to specify bit to check whether the specified bit is set");
        System.err.println("");
    }

    /**
     * Asks a simple question, waits for response and
     * finally returns an answer as string. It returns the default value
     * if the answer is empty provided the default value is not null.
     * This is ONLY used in main() method.
     * 
     * @param str 
     * @param question 
     * @return response 
     */
    private static String getString(String str, String question)
    {
        return (str != null)
            ? askQuestion(question, str) : askQuestion(question);
    }

    /**
     * Asks a simple question, waits for response and
     * finally returns an answer. It returns the default value
     * if the answer is empty provided the default value is not null.
     * This is ONLY used in main() method.
     * 
     * @param question 
     * @param defValue 
     * @return response 
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
     * This is ONLY used in main() method.
     * 
     * @param question 
     * @return response
     */
    private static String askQuestion(String question)
    {
        return askQuestion(question, null);
    }

}
