package org.ikasan.dashboard.ui.administration.util;

import org.ikasan.spec.systemevent.SystemEvent;

public class SystemEventFormatter {

    public static final String getContext(SystemEvent systemEvent) {
        if (systemEvent.getAction().equals("Create Wiretap") || systemEvent.getAction().equals("Delete Wiretap") || systemEvent.getAction().equals("Replaying Event")) {
            return  systemEvent.getSubject().substring(0, systemEvent.getSubject().indexOf(":"));
        }

        return systemEvent.getSubject();
    }

    public static final String getEvent(SystemEvent systemEvent) {
        if (systemEvent.getAction().equals("Create Wiretap") || systemEvent.getAction().equals("Delete Wiretap") || systemEvent.getAction().equals("Replaying Event")) {
             return systemEvent.getAction() + "\r\n" + systemEvent.getSubject().substring(systemEvent.getSubject().indexOf(":") + 1);
        }

        return systemEvent.getAction();
    }
}
