package org.ikasan.dashboard.ui.util;

import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.ikasan.systemevent.service.SystemEventService;
import org.springframework.security.core.context.SecurityContextHolder;

public class SystemEventLogger
{
    private SystemEventService systemEventService;

    public SystemEventLogger(SystemEventService systemEventService)
    {
        this.systemEventService = systemEventService;
        if(this.systemEventService == null)
        {
            throw new IllegalArgumentException("The system event service cannot be null!");
        }
    }

    public void logEvent(String subject, String action)
    {
        IkasanAuthentication ikasanAuthentication = (IkasanAuthentication) SecurityContextHolder.getContext().getAuthentication();
        systemEventService.logSystemEvent(subject, action, ikasanAuthentication.getName());
    }
}
