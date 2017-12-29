package org.ikasan.dashboard.ui.control.util;

import com.vaadin.event.Action;

/**
 * Created by Ikasan Development Team on 20/11/2017.
 */
public class FlowActions
{
    public static final String START = "start";
    public static final String STOP = "stop";
    public static final String PAUSE = "pause";
    public static final String START_PAUSE = "startPause";
    public static final String RESUME = "resume";

    public static String mapPostActionSucessState(String action)
    {
        if(action.equals(START))
        {
            return FlowStates.RUNNING;
        }
        else if(action.equals(STOP))
        {
            return FlowStates.STOPPED;
        }
        else if(action.equals(PAUSE))
        {
            return FlowStates.PAUSED;
        }
        else if(action.equals(START_PAUSE))
        {
            return FlowStates.PAUSED;
        }
        else if(action.equals(RESUME))
        {
            return FlowStates.RUNNING;
        }

        return "";
    }
}
