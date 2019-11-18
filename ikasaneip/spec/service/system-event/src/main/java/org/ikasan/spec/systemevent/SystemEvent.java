package org.ikasan.spec.systemevent;

import java.util.Date;

public interface SystemEvent
{
    String getAction();

    String getActor();

    Long getId();

    String getSubject();

    Date getTimestamp();

    Date getExpiry();
}
