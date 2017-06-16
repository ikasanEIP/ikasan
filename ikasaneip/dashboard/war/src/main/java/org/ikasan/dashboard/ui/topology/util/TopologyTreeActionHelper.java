package org.ikasan.dashboard.ui.topology.util;

import com.vaadin.event.Action;
import com.vaadin.server.VaadinService;
import org.apache.log4j.Logger;
import org.ikasan.dashboard.ui.framework.constants.SecurityConstants;
import org.ikasan.dashboard.ui.framework.util.DashboardSessionValueConstants;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.ikasan.topology.model.Component;

/**
 * Created by stewmi on 07/06/2017.
 */
public class TopologyTreeActionHelper
{
    private Logger logger = Logger.getLogger(TopologyTreeActionHelper.class);

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
    public static final Action VIEW_DIAGRAM = new Action("View Diagram");
    public static final Action CONFIGURE = new Action("Configure");
    public static final Action CONFIGURE_METRICS = new Action("Configure Metrics");
    public static final Action PAUSE = new Action("Pause");
    public static final Action START_PAUSE = new Action("Start/Pause");
    public static final Action RESUME = new Action("Resume");
    public static final Action RESTART = new Action("Re-start");
    public static final Action DISABLE = new Action("Disable");
    public static final Action DETAILS = new Action("Details");
    public static final Action WIRETAP = new Action("Wiretap");
    public static final Action ERROR_CATEGORISATION = new Action("Categorise Error");
    public static final Action EDIT = new Action("Edit");
    public static final Action MANAGE_COMPONENT_CONFIGURATIONS = new Action("Manage Component Configurations");
    public static final Action STARTUP_CONTROL = new Action("Startup Type");

    // Server Actions
    private final Action[] serverActionsWriteAdmin = new Action[] { DETAILS, ERROR_CATEGORISATION, EDIT };
    private final Action[] serverActionsRead = new Action[] { DETAILS };

    // Module Actions
    private final Action[] moduleActionsWriteAdmin = new Action[] { DETAILS, VIEW_DIAGRAM, ERROR_CATEGORISATION, MANAGE_COMPONENT_CONFIGURATIONS};
    private final Action[] moduleActionsRead = new Action[] { DETAILS, VIEW_DIAGRAM};

    // Flow Actions
    private final Action[] flowActionsStoppedAdmin = new Action[] { START, START_PAUSE, STARTUP_CONTROL, ERROR_CATEGORISATION, MANAGE_COMPONENT_CONFIGURATIONS};
    private final Action[] flowActionsStartedAdmin = new Action[] { STOP, PAUSE, STARTUP_CONTROL, ERROR_CATEGORISATION, MANAGE_COMPONENT_CONFIGURATIONS};
    private final Action[] flowActionsPausedAdmin = new Action[] { STOP, RESUME, STARTUP_CONTROL, ERROR_CATEGORISATION, MANAGE_COMPONENT_CONFIGURATIONS};
    private final Action[] flowActionsStoppedConfigurableAdmin = new Action[] { START, START_PAUSE, STARTUP_CONTROL, ERROR_CATEGORISATION, CONFIGURE, MANAGE_COMPONENT_CONFIGURATIONS};
    private final Action[] flowActionsStartedConfigurableAdmin = new Action[] { STOP, PAUSE, STARTUP_CONTROL, ERROR_CATEGORISATION, CONFIGURE, MANAGE_COMPONENT_CONFIGURATIONS};
    private final Action[] flowActionsPausedConfigurableAdmin = new Action[] { STOP, RESUME, STARTUP_CONTROL, ERROR_CATEGORISATION, CONFIGURE, MANAGE_COMPONENT_CONFIGURATIONS};

    private final Action[] flowActionsConfigurableWrite = new Action[] { ERROR_CATEGORISATION, CONFIGURE, MANAGE_COMPONENT_CONFIGURATIONS};
    private final Action[] flowActionsWrite = new Action[] { ERROR_CATEGORISATION, MANAGE_COMPONENT_CONFIGURATIONS};

    // Component Actions
    private final Action[] componentActionsConfigurableConfigureMetrics = new Action[] { CONFIGURE, CONFIGURE_METRICS, WIRETAP, ERROR_CATEGORISATION };
    private final Action[] componentActionsConfigurableConfigure = new Action[] { CONFIGURE, WIRETAP, ERROR_CATEGORISATION };
    private final Action[] componentActionsConfigureMetrics = new Action[] { CONFIGURE_METRICS, WIRETAP, ERROR_CATEGORISATION };
    private final Action[] componentActions = new Action[] { WIRETAP, ERROR_CATEGORISATION };

    //Miscellaneous Actions
    private final Action[] actionsEmpty = new Action[]{};

    private IkasanAuthentication authentication;

    public TopologyTreeActionHelper(IkasanAuthentication authentication)
    {
        this.authentication = authentication;
    }

    public Action[] getServerActions()
    {
        if(authentication.hasGrantedAuthority(SecurityConstants.TOPOLOGY_WRITE)
                || authentication.hasGrantedAuthority(SecurityConstants.TOPOLOGY_ADMIN)
                || authentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
        {
            return serverActionsWriteAdmin;
        }
        else if(authentication.hasGrantedAuthority(SecurityConstants.TOPOLOGY_READ))
        {
            return serverActionsRead;
        }

        return actionsEmpty;
    }

    public Action[] getModuleActions()
    {
        if(authentication.hasGrantedAuthority(SecurityConstants.TOPOLOGY_WRITE)
                || authentication.hasGrantedAuthority(SecurityConstants.TOPOLOGY_ADMIN)
                || authentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
        {
            return moduleActionsWriteAdmin;
        }
        else if(authentication.hasGrantedAuthority(SecurityConstants.TOPOLOGY_READ))
        {
            return moduleActionsRead;
        }

        return actionsEmpty;
    }

    public Action[] getFlowActions(String state, boolean isConfigurable)
    {
        if(authentication.hasGrantedAuthority(SecurityConstants.TOPOLOGY_ADMIN)
                || authentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
        {
            if(state == null)
            {
                if(isConfigurable)
                {
                    return this.flowActionsConfigurableWrite;
                }
                else
                {
                    return this.flowActionsWrite;
                }
            }

            if((state.equals(RUNNING) || state.equals(RECOVERING)))
            {
                if(isConfigurable)
                {
                    return this.flowActionsStartedConfigurableAdmin;
                }
                else
                {
                    return this.flowActionsStartedAdmin;
                }
            }
            else if ((state.equals(STOPPED) || state.equals(STOPPED_IN_ERROR)))
            {
                if(isConfigurable)
                {
                    return this.flowActionsStoppedConfigurableAdmin;
                }
                else
                {
                    return this.flowActionsStoppedAdmin;
                }
            }
            else if (state.equals(PAUSED))
            {
                if(isConfigurable)
                {
                    return this.flowActionsPausedConfigurableAdmin;
                }
                else
                {
                    return this.flowActionsPausedAdmin;
                }
            }
        }
        else if(authentication.hasGrantedAuthority(SecurityConstants.TOPOLOGY_WRITE))
        {
            if(isConfigurable)
            {
                return this.flowActionsConfigurableWrite;
            }
            else
            {
                return this.flowActionsWrite;
            }
        }

        return actionsEmpty;

    }

    public Action[] getComponentActions(boolean componentIsConfigurable, boolean flowIsConfigurable)
    {
        if(authentication.hasGrantedAuthority(SecurityConstants.TOPOLOGY_WRITE)
                || authentication.hasGrantedAuthority(SecurityConstants.TOPOLOGY_ADMIN)
                || authentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
        {
            if (componentIsConfigurable && flowIsConfigurable)
            {
                return componentActionsConfigurableConfigureMetrics;
            }
            else if (componentIsConfigurable && !flowIsConfigurable)
            {
                return componentActionsConfigurableConfigure;
            }
            else if (!componentIsConfigurable && flowIsConfigurable)
            {
                return componentActionsConfigureMetrics;
            }
        }

        return actionsEmpty;
    }

    public Action[] getActionsEmpty()
    {
        return actionsEmpty;
    }
}
