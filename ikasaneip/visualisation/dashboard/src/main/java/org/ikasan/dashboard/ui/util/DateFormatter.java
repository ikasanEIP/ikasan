package org.ikasan.dashboard.ui.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatter
{
    public static final String DATE_FORMAT_TABLE_VIEWS = "dd/MM/yyyy HH:mm:ss.SSS";
    public static final String DATE_FORMAT_CALENDAR_VIEWS = "dd/MM/yyyy HH:mm:ss";

    private static SimpleDateFormat tableFormatter;

    static
    {
        tableFormatter = new SimpleDateFormat(DATE_FORMAT_TABLE_VIEWS);
    }

    public static String getFormattedDate(long timestamp)
    {
        if(timestamp == 0)
        {
            return "N/A";
        }

        Date date = new Date(timestamp);

        return tableFormatter.format(date);
    }
}
