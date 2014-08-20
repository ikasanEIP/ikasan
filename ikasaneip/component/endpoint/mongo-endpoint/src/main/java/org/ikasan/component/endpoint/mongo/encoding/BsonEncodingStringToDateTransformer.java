package org.ikasan.component.endpoint.mongo.encoding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.bson.Transformer;

/**
 * Transforms String values that match a date or date time regex to a java.util.Date type
 * 
 * @author Ikasan Development Team
 */
public class BsonEncodingStringToDateTransformer implements Transformer
{
    /** logger instance */
    private static Logger logger = Logger.getLogger(BsonEncodingStringToDateTransformer.class);

    private Pattern dateRegExp;

    /** the default date regular expression **/
    public static final String BSON_TRANSFORMER_DEFAULT_DATE_REG_EXP_STR = "\\d\\d\\d\\d-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])";

    private Pattern dateTimeRegExp;

    /** the default date time regular expression **/
    public static final String BSON_TRANSFORMER_DEFAULT_DATE_TIME_REG_EXP_STR = "\\d\\d\\d\\d-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])T([01]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]";

    /** the default dateFormat string **/
    private String dateFormatString = "yyyy-MM-dd";

    /** the default dateTimeFormat string **/
    private String dateTimeFormatString = "yyyy-MM-dd'T'HH:mm:ss";

    public BsonEncodingStringToDateTransformer()
    {
        dateRegExp = Pattern.compile(BSON_TRANSFORMER_DEFAULT_DATE_REG_EXP_STR);
        dateTimeRegExp = Pattern.compile(BSON_TRANSFORMER_DEFAULT_DATE_TIME_REG_EXP_STR);
    }

    @Override
    public Object transform(Object o)
    {
        Object transformed = o;
        if (o instanceof String)
        {
            String str = (String) o;
            if (dateTimeRegExp.matcher(str).matches())
            {
                transformed = transformToDate(str, dateTimeFormatString);
            }
            else if (dateRegExp.matcher(str).matches())
            {
                transformed = transformToDate(str, dateFormatString);
            }
        } 
        return transformed;
    }

    private Object transformToDate(String str, String dateTimeFormat)
    {
        try
        {
            return new SimpleDateFormat(dateTimeFormat).parse(str);
        }
        catch (ParseException e)
        {
            logger.error(String.format(
                "Unable to transform to date string [%1$s] using format [%2$s] will remain a string", str,
                dateTimeFormatString));
            logger.error("Error thrown", e);
        }
        return str;
    }

    public void setDateRegExp(Pattern dateRegExp)
    {
        this.dateRegExp = dateRegExp;
    }

    public void setDateTimeRegExp(Pattern dateTimeRegExp)
    {
        this.dateTimeRegExp = dateTimeRegExp;
    }

    public void setDateFormatString(String dateFormatString)
    {
        this.dateFormatString = dateFormatString;
    }

    public void setDateTimeFormatString(String dateTimeFormatString)
    {
        this.dateTimeFormatString = dateTimeFormatString;
    }
    
    @Override
    public String toString()
    {
        return "BsonEncodingStringToDateTransformer [dateRegExp=" + dateRegExp + ", dateTimeRegExp=" + dateTimeRegExp
                + ", dateFormatString=" + dateFormatString + ", dateTimeFormatString=" + dateTimeFormatString + "]";
    }
}
