package org.ikasan.dashboard.ui.util;

import org.ikasan.dashboard.ui.administration.view.UserManagementView;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.ikasan.spec.systemevent.SystemEventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;

public class SystemEventLogger
{
    private Logger logger = LoggerFactory.getLogger(SystemEventLogger.class);

    private SystemEventService systemEventService;

    public SystemEventLogger(SystemEventService systemEventService)
    {
        this.systemEventService = systemEventService;
        if(this.systemEventService == null)
        {
            throw new IllegalArgumentException("The system event listener cannot be null!");
        }
    }

    public void logEvent(String subject, String action, String actor)
    {
        IkasanAuthentication ikasanAuthentication = (IkasanAuthentication) SecurityContextHolder.getContext().getAuthentication();
        action += " - Performed by [" + ikasanAuthentication.getName() + "]";
        if(actor == null)
        {
            actor = ikasanAuthentication.getName();
        }
        logger.debug("Logging system event [{}], [{}], [{}]", subject, action, actor);
        systemEventService.logSystemEvent(subject, action, actor);
        logger.debug("Sucessfully logged system event [{}], [{}], [{}]", subject, action, actor);
    }
}
