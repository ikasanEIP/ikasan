package org.ikasan.dashboard.ui.administration.component;

import com.vaadin.flow.component.dialog.Dialog;
import org.ikasan.security.service.SecurityService;
import org.ikasan.security.service.UserService;
import org.ikasan.systemevent.service.SystemEventService;

public class RoleSelectDialog extends Dialog
{
    private UserService userService;
    private SecurityService securityService;
    private SystemEventService systemEventService;

    /**
     * Constructor
     *
     * @param userService
     * @param securityService
     * @param systemEventService
     */
    public RoleSelectDialog(UserService userService, SecurityService securityService, SystemEventService systemEventService)
    {
        this.userService = userService;
        this.securityService = securityService;
        this.systemEventService = systemEventService;
    }
}
