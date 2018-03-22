package org.ikasan.dashboard.ui.control.util;

import com.vaadin.event.Action;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ikasan.dashboard.ui.framework.constants.SecurityConstants;
import org.ikasan.security.service.authentication.IkasanAuthentication;

/**
 * Created by Ikasan Development Team on 07/06/2017.
 */
public class ModuleControlActionHelper
{
    private Logger logger = LoggerFactory.getLogger(ModuleControlActionHelper.class);

    /** running state string constant */
    private static String RUNNING = "running";

    /** stopped state string constant */
    private static String STOPPED = "stopped";

    /** recovering state string constant */
    private static String RECOVERING = "recovering";

    /** stoppedInError state string constant */
    private static String STOPPED_IN_ERROR = "stoppedInError";

    /** paused state string constant */
    private static String PAUSED = "paused";

    // Individual Actions
    public static final Action START = new Action("Start");
    public static final Action STOP = new Action("Stop");
    public static final Action PAUSE = new Action("Pause");
    public static final Action START_PAUSE = new Action("Start/Pause");
    public static final Action RESUME = new Action("Resume");




    // Flow Actions
    private final Action[] flowActionsStoppedAdmin = new Action[] { START, START_PAUSE};
    private final Action[] flowActionsStartedAdmin = new Action[] { STOP, PAUSE};
    private final Action[] flowActionsPausedAdmin = new Action[] { STOP, RESUME};


    //Miscellaneous Actions
    private final Action[] actionsEmpty = new Action[]{};

    private IkasanAuthentication authentication;

    public ModuleControlActionHelper(IkasanAuthentication authentication)
    {
        this.authentication = authentication;
    }


    public Action[] getFlowActions(String state, boolean isConfigurable)
    {
        if(authentication.hasGrantedAuthority(SecurityConstants.TOPOLOGY_ADMIN)
                || authentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
        {
            if(state == null)
            {
                return actionsEmpty;
            }

            if((state.equals(RUNNING) || state.equals(RECOVERING)))
            {
                return this.flowActionsStartedAdmin;
            }
            else if ((state.equals(STOPPED) || state.equals(STOPPED_IN_ERROR)))
            {
                return this.flowActionsStoppedAdmin;
            }
            else if (state.equals(PAUSED))
            {
                return this.flowActionsPausedAdmin;
            }
        }


        return actionsEmpty;

    }


    public Action[] getActionsEmpty()
    {
        return actionsEmpty;
    }
}
