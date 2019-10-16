package org.ikasan.dashboard.ui.util;

import java.time.LocalTime;

public class DateTimeUtil
{
    /**
     * Helper method to get the hour and minute milliseconds from a local time.
     * @param localTime
     * @return
     */
    public static long getMilliFromTime(LocalTime localTime)
    {
        long milli = 0;
        if(localTime.getMinute() > 0)
        {
            milli += localTime.getMinute()  * 60 * 1000;
        }
        if(localTime.getHour() > 0)
        {
            milli += localTime.getHour() * 60  * 60 * 1000;
        }

        return milli;
    }
}
