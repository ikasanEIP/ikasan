package org.ikasan.rest.module.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeConverter
{
    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    public Date getDate(String text) throws ParseException
    {
        if (text == null)
        {
            return null;
        }
        else
        {
            return formatter.parse(text);
        }
    }
}
