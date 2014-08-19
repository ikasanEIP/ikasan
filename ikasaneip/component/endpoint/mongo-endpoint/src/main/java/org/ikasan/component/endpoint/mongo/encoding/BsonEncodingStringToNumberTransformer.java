package org.ikasan.component.endpoint.mongo.encoding;

import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.bson.Transformer;

/**
 * Transforms String values that match a number regex to the appropriate java.lang.Integer, java.lang.Long or
 * java.lang.Double type
 * 
 * @author Ikasan Development Team
 */
public class BsonEncodingStringToNumberTransformer implements Transformer
{
    /** logger instance */
    private static Logger logger = Logger.getLogger(BsonEncodingStringToNumberTransformer.class);

    private Pattern numberRegExp;

    /** the default regex used to match a +ve, -ve whole or decimal number **/
    public static final String BSON_TRANSFORMER_DEFAULT_NUMBER_REG_EXP_STR = "-?\\d+(\\.\\d+)?";

    public BsonEncodingStringToNumberTransformer()
    {
        numberRegExp = Pattern.compile(BSON_TRANSFORMER_DEFAULT_NUMBER_REG_EXP_STR);
    }

    @Override
    public Object transform(Object o)
    {
        Object transformed = o;
        if (o instanceof String)
        {
            String str = (String) o;
            if (numberRegExp.matcher(str).matches())
            {
                transformed = transformToNumber(str);
            }
        } else {
                logger.warn(String.format("The object to be transformed [%1$s] is not a java.lang.String class ensure this transformer is mapped to this class", o));
        }
        return transformed;
    }

    private Object transformToNumber(String str)
    {
        Object transformed = str;
        try
        {
            return Integer.parseInt(str);
        }
        catch (NumberFormatException nfe)
        {
        }
        try
        {
            return Long.parseLong(str);
        }
        catch (NumberFormatException nfe)
        {
        }
        try
        {
            return Double.parseDouble(str);
        }
        catch (NumberFormatException nfe)
        {
        }
        logger.error(String.format("Unable to parse number [%1%s] into a Long or a Double will remain a string", str));
        return transformed;
    }

    public void setNumberRegExp(Pattern numberRegExp)
    {
        this.numberRegExp = numberRegExp;
    }
}
