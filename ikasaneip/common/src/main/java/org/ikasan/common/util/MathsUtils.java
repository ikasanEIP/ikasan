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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;

/**
 * This class provides miscellaneous maths specific utilities.
 * 
 * @author Ikasan Development Team
 */
public class MathsUtils
{
    /** Default result */
    private static String ERROR_STR = "NaN";

    /**
     * Don't let anyone instantiate this class.
     */
    private MathsUtils()
    {
        // Do Nothing
    }

    /**
     * This method returns a string, which will be the result of multiplying one
     * number by another.
     * 
     * @param number The number to multiply.
     * @param multiplier The number to multiply by.
     * @return Result of multiplication as a String.
     */
    public static String multiply(String number, String multiplier)
    {
        String result = ERROR_STR;
        try
        {
            int firstParamLength = 0;
            int secondParamLength = 0;
            int decPlaces = 0;
            String decPl = "0";
            if (number.indexOf(".") > 0)
            {
                firstParamLength = number.substring(number.indexOf(".") + 1).length();
            }
            if (multiplier.indexOf(".") > 0)
            {
                secondParamLength = multiplier.substring(multiplier.indexOf(".") + 1).length();
            }
            decPlaces = firstParamLength + secondParamLength;
            decPl = Integer.toString(decPlaces);
            result = multiply(number, multiplier, decPl);
            return result;
        }
        catch (NumberFormatException e)
        {
            return result;
        }
    }

    /**
     * This method returns a string, which will be the result of multiplying one
     * number by another.
     * 
     * @param number The number to multiply.
     * @param multiplier The number to multiply by.
     * @param maxDecPl The number of decimal places to round the result at.
     * @return Result of multiplication as a String.
     * */
    public static String multiply(String number, String multiplier, String maxDecPl)
    {
        BigDecimal bdNumber = null;
        BigDecimal bdMultiplier = null;
        int iDecPl = 0;
        BigDecimal rv = null;
        String result = ERROR_STR;
        try
        {
            bdNumber = new BigDecimal(number);
            bdMultiplier = new BigDecimal(multiplier);
            iDecPl = Integer.parseInt(maxDecPl);
            // This method will use BigDecimals to multiply a number by another,
            // then round to the specified decimal places.
            // The number of decimal places to round at is parameterized.
            // The calculated value is returned as a string
            rv = bdNumber.multiply(bdMultiplier);
            rv = rv.setScale(iDecPl, BigDecimal.ROUND_HALF_EVEN);
        }
        catch (NumberFormatException e)
        {
            return result;
        }
        result = rv.toString();
        return result;
    }

    /**
     * This method returns a string, which will be the result of calculating the
     * proportion a changed base represents of n original, multiplying a
     * supplied value by that proportion.
     * 
     * @param originalBase The original base value.
     * @param changedBase The change base value.
     * @param toBeProportioned The number to be changed by the calculated
     *            proportion
     * @param maxDecPl The number of decimal places to round the result at.
     * @return The result value as a string.
     */
    public static String proportion(String originalBase, String changedBase, String toBeProportioned, String maxDecPl)
    {
        BigDecimal bdOriginal = null;
        BigDecimal bdChanged = null;
        BigDecimal bdToBeProportioned = null;
        int iDecPl = 0;
        int calcDecPl = 15;
        BigDecimal rv = null;
        String result = ERROR_STR;
        BigDecimal ZERO = new BigDecimal("0");
        try
        {
            bdOriginal = new BigDecimal(originalBase);
            bdChanged = new BigDecimal(changedBase);
            bdToBeProportioned = new BigDecimal(toBeProportioned);
            iDecPl = Integer.parseInt(maxDecPl);
            // This method will use BigDecimals to multply a number by another,
            // then round to the specified decimal places.
            // The number of decimal places to round at is parameterized.
            // The calculated value is returned as a string
            if (bdOriginal.compareTo(ZERO) == 0 || bdChanged.compareTo(ZERO) == 0)
            {
                rv = ZERO;
            }
            else
            {
                rv = bdChanged.divide(bdOriginal, calcDecPl, BigDecimal.ROUND_HALF_EVEN);
                rv = rv.multiply(bdToBeProportioned);
                rv = rv.setScale(iDecPl, BigDecimal.ROUND_HALF_EVEN);
            }
        }
        catch (NumberFormatException e)
        {
            return result;
        }
        result = rv.toString();
        return result;
    }

    /**
     * This method will subtract one number from another, protecting precision.
     * 
     * @param baseNumber Base value.
     * @param numberToSubtract Amount to be subtracted from baseNumber.
     * @return Result of subtraction as a string.
     */
    public static String subtract(String baseNumber, String numberToSubtract)
    {
        String result = ERROR_STR;
        try
        {
            BigDecimal bdBase = new BigDecimal(baseNumber);
            BigDecimal bdSubtract = new BigDecimal(numberToSubtract);
            BigDecimal bdResult = null;
            bdResult = bdBase.subtract(bdSubtract);
            result = bdResult.toString();
        }
        catch (Exception e)
        {
            return result;
        }
        return result;
    }

    /**
     * Sets the number of decimal places of the specified number string to the
     * given value. For example, <code>formatDecimal("123456", 2, 6)</code>
     * becomes "<code>1234.56</code>". Available rounding modes are as follows
     * (see <code>java.math.BigDecimal</code> for full explanation):-<br>
     * <ul>
     * <li>0 : ROUND_UP</li>
     * <li>1 : ROUND_DOWN</li>
     * <li>2 : ROUND_CEILING</li>
     * <li>3 : ROUND_FLOOR</li>
     * <li>4 : ROUND_HALF_UP</li>
     * <li>5 : ROUND_HALF_DOWN</li>
     * <li>6 : ROUND_HALF_EVEN</li>
     * <li>7 : ROUND_UNNECESSARY</li>
     * </ul>
     * 
     * @param numberStr is the big decimal value.
     * @param numOfDecPlaces is the number of decimal places.
     * @param roundingMode is the rounding mode to apply.
     * @return The decimal as a string
     */
    public static String setDecimalPlaces(String numberStr, int numOfDecPlaces, int roundingMode)
    {
        if (numberStr == null) throw new NullPointerException("BigDecimal value can't be null");
        if (numberStr.trim().length() == 0) throw new IllegalArgumentException("BigDecimal value can't be empty");
        if (numOfDecPlaces < 0) throw new ArithmeticException("Negative number of decimal places: '" + numOfDecPlaces + "'");
        if (roundingMode < BigDecimal.ROUND_UP || roundingMode > BigDecimal.ROUND_UNNECESSARY)
            throw new IllegalArgumentException("Invalid rounding mode: '" + roundingMode + "'");
        BigDecimal bigDec = new BigDecimal(numberStr);
        bigDec = bigDec.setScale(numOfDecPlaces, roundingMode);
        return bigDec.toString();
    }

    /**
     * Sets the number of decimal places of the specified number string to the
     * given value.
     * 
     * @param numberStr is the big decimal value.
     * @param numOfDecPlaces is the number of decimal places.
     * @return the decimal as a string
     */
    public static String setDecimalPlaces(String numberStr, int numOfDecPlaces)
    {
        return setDecimalPlaces(numberStr, numOfDecPlaces, BigDecimal.ROUND_UNNECESSARY);
    }

    /**
     * Sets the number of decimal places of the specified number string to the
     * given value.
     * 
     * @param numberStr is the big decimal value.
     * @param numOfDecPlaces is the number of decimal places.
     * @param roundingMode is the rounding mode to apply.
     * @return the decimal as a string
     */
    public static String setDecimalPlaces(String numberStr, String numOfDecPlaces, String roundingMode)
    {
        if (numOfDecPlaces == null) throw new NullPointerException("Number of decimal places can't be null");
        if (numOfDecPlaces.trim().length() == 0) throw new IllegalArgumentException("Number of decimal places can't be empty");
        if (roundingMode == null) throw new NullPointerException("Rounding mode can't be null");
        if (roundingMode.trim().length() == 0) throw new IllegalArgumentException("Rounding mode can't be empty");
        return setDecimalPlaces(numberStr, Integer.parseInt(numOfDecPlaces), Integer.parseInt(roundingMode));
    }

    /**
     * Sets the number of decimal places of the specified number string to the
     * given value.
     * 
     * @param numberStr is the big decimal value.
     * @param numOfDecPlaces is the number of decimal places.
     * @return the decimal as a string
     */
    public static String setDecimalPlaces(String numberStr, String numOfDecPlaces)
    {
        return setDecimalPlaces(numberStr, numOfDecPlaces, String.valueOf(BigDecimal.ROUND_UNNECESSARY));
    }

    /**
     * Returns the resultant string of (numberStr / divisorStr).
     * 
     * @param numberStr is the big decimal value.
     * @param divisorStr is the divisor string.
     * @param numOfDecPlaces is the number of decimal places.
     * @param roundingMode is the rounding mode to apply.
     * @return the decimal as a string
     */
    public static String divide(String numberStr, String divisorStr, String numOfDecPlaces, int roundingMode)
    {
        BigDecimal rv = null;
        String result = ERROR_STR;
        try
        {
            BigDecimal bdNumber = new BigDecimal(numberStr);
            BigDecimal bdDivisor = new BigDecimal(divisorStr);
            int iDecPl;
            if (numOfDecPlaces != null)
            {
                iDecPl = Integer.parseInt(numOfDecPlaces);
            }
            else
            {
                iDecPl = bdNumber.scale();
            }
            // This method will use BigDecimals to divide one number by another.
            // The number of decimal places to round at is parameterized.
            // The calculated value is returned as a string
            rv = bdNumber.divide(bdDivisor, iDecPl, roundingMode);
            return rv.toString();
        }
        catch (Exception e)
        {
            return result;
        }
    }

    /**
     * Returns a padded fixed string in the specified alignment.
     *
     * @param number   is the string only containing the actual data.
     * @param divisor  is the divisor to be applied to the number parameter.
     * @param maxDecPl is the precision to be applied to the result
     * @return Division result
     */
    public static String divide(String number, String divisor, String maxDecPl)
    {
        return divide(number, divisor, maxDecPl, BigDecimal.ROUND_HALF_EVEN);
    }


    /**
     * Returns the resultant string of (numberStr / divisorStr).
     * 
     * @param numberStr is the big decimal value.
     * @param divisorStr is the divisor string.
     * @param roundingMode is the rounding mode to apply.
     * @return the result as a string
     */
    public static String divide(String numberStr, String divisorStr, int roundingMode)
    {
        return divide(numberStr, divisorStr, null , roundingMode);
    }

    /**
     * Returns the resultant string of (numberStr / divisorStr).
     * 
     * @param numberStr is the big decimal value.
     * @param divisorStr is the divisor string.
     * @param numOfDecPlaces is the number of decimal places.
     * @param roundingMode is the rounding mode to apply.
     * @return the result as a string
     */
    public static String divide(String numberStr, String divisorStr, String numOfDecPlaces, String roundingMode)
    {
        if (numOfDecPlaces == null) throw new NullPointerException("Number of decimal places can't be null");
        if (numOfDecPlaces.trim().length() == 0) throw new IllegalArgumentException("Number of decimal places can't be empty");
        if (roundingMode == null) throw new NullPointerException("Rounding mode can't be null");
        if (roundingMode.trim().length() == 0) throw new IllegalArgumentException("Rounding mode can't be empty");
        return divide(numberStr, divisorStr, numOfDecPlaces, Integer.parseInt(roundingMode));
    }

    /**
     * Returns the boolean flag indicating whether the specified set bit is set
     * on the given string that represents integer value.
     * 
     * @param value is the string that represents integer value between 0 and
     *            255 inclusive.
     * @param bit is the string that represents bit.
     * @return true if the bit is set
     */
    public static boolean isBitSet(int value, int bit)
    {
        if (value > 255 || value < 0) throw new IllegalArgumentException("The value must be between 0 and 255 inclusive: '" + value + "'");
        if (bit > 7 || bit < 0) throw new IllegalArgumentException("The bit must be between 0 and 7 inclusive: '" + bit + "'");
        int mask = 1 << bit;
        return ((value & mask) > 0);
    }

    /**
     * Returns the boolean flag indicating whether the specified set bit is set
     * on the given string that represents integer value.
     * 
     * @param value is the string that represents integer value between 0 and
     *            255 inclusive.
     * @param bit is the string that represents bit.
     * @return &quot;true&quot; if the bit is set
     */
    public static String isBitSet(String value, String bit)
    {
        if (value == null) throw new NullPointerException("Value can't be null");
        if (value.trim().length() == 0) throw new IllegalArgumentException("Value can't be empty");
        if (bit == null) throw new NullPointerException("Bit can't be null");
        if (bit.trim().length() == 0) throw new IllegalArgumentException("Bit can't be empty");
        return String.valueOf(isBitSet(Integer.parseInt(value), Integer.parseInt(bit)));
    }

    /**
     * This method returns a string, which will be the result of multiplying the
     * input value by a par price.
     * 
     * @param number The number to multiply
     * @param price The par price value.
     * @param maxDecPl The number of decimal places to round the result at.
     * @return Result as a string.
     */
    public static String multiplyNumberByParPrice(String number, String price, String maxDecPl)
    {
        BigDecimal bdNumber = null;
        BigDecimal bdPrice = null;
        int iDecPl = 0;
        BigDecimal rv = null;
        String result = ERROR_STR;
        try
        {
            bdNumber = new BigDecimal(number);
            bdPrice = new BigDecimal(price);
            iDecPl = Integer.parseInt(maxDecPl);
            BigDecimal ONE_HUNDRED = new BigDecimal("100");
            // This method will use BigDecimals to multply a number by a
            // percentage.
            // The number of decimal places to round at is parameterized.
            // The calculated value is returned as a string
            rv = bdNumber.multiply(bdPrice);
            rv = rv.divide(ONE_HUNDRED, iDecPl, BigDecimal.ROUND_HALF_EVEN);
        }
        catch (Exception e)
        {
            return result;
        }
        return result;
    }

    // main() method
    // /////////////////////////////////////////////////////////////////////////////
    /**
     * Runs this class for testing.
     * 
     * @param args Arguments
     */
    public static void main(String args[])
    {
        String value = "1234567890";
        String decPlaces = "2";
        String roundingMode = "" + BigDecimal.ROUND_HALF_EVEN;
        String divisor = "1.2";
        String bitStr = "1";
        String multiplier = "2";
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
                question = "Which method do you want to test?\n" + "  0. exit(Get outta here!!)\n" + "  1. Set decimal places\n" + "  2. Division\n"
                        + "  3. Check bitSet\n" + "  4. Multiply two numbers\n";
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
                        question = "Number of decimal places (" + decPlaces + ")?";
                        decPlaces = getString(decPlaces, question);
                        question = "Rounding mode (" + roundingMode + ")?";
                        roundingMode = getString(roundingMode, question);
                        System.out.println("Result =[" + MathsUtils.setDecimalPlaces(value, decPlaces, roundingMode) + "]");
                    }
                    // Division
                    else if (answer.equals("2"))
                    {
                        question = "Big decimal value (" + value + ")?";
                        value = getString(value, question);
                        question = "Divisor value (" + divisor + ")?";
                        divisor = getString(divisor, question);
                        question = "Number of decimal places (" + decPlaces + ")?";
                        decPlaces = getString(decPlaces, question);
                        question = "Rounding mode (" + roundingMode + ")?";
                        roundingMode = getString(roundingMode, question);
                        System.out.println("Result =[" + MathsUtils.divide(value, divisor, decPlaces, roundingMode) + "]");
                    }
                    // Check bit set
                    else if (answer.equals("3"))
                    {
                        question = "Value (" + value + ")?";
                        value = getString(value, question);
                        question = "Bit (" + bitStr + ")?";
                        bitStr = getString(bitStr, question);
                        System.out.println("Result =[" + MathsUtils.isBitSet(value, bitStr) + "]");
                    }
                    else if (answer.equals("4"))
                    {
                        question = "Value (" + value + ")?";
                        value = getString(value, question);
                        question = "Multiplier (" + multiplier + ")?";
                        multiplier = getString(multiplier, question);
                        System.out.println("Result =[" + MathsUtils.divide(MathsUtils.multiply("92.4764289", "100", "8"),"100.000000" , "8") + "]");
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
     * Asks a simple question, waits for response and finally returns an answer
     * as string. It returns the default value if the answer is empty provided
     * the default value is not null. This is ONLY used in main() method.
     * 
     * @param str Default value override
     * @param question Choice of arithmatic operation
     * @return response The result of operation
     */
    private static String getString(String str, String question)
    {
        return (str != null) ? askQuestion(question, str) : askQuestion(question);
    }

    /**
     * Asks a simple question, waits for response and finally returns an answer.
     * It returns the default value if the answer is empty provided the default
     * value is not null. This is ONLY used in main() method.
     * 
     * @param question Choice of arithmatic operation
     * @param defValue Value for operation variable
     * @return response Result
     */
    private static String askQuestion(String question, String defValue)
    {
        String response = "";
        do
        {
            System.out.println(question);
            try
            {
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
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
     * Asks a simple question, waits for response and finally returns an answer.
     * It keeps asking until it gets something. This is ONLY used in main()
     * method.
     * 
     * @param question Choice of math operation
     * @return response
     */
    private static String askQuestion(String question)
    {
        return askQuestion(question, null);
    }
}
