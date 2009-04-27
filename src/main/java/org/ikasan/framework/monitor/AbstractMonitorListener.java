/* 
 * $Id: AbstractMonitorListener.java 16808 2009-04-27 07:28:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/framework/src/main/java/org/ikasan/framework/monitor/AbstractMonitorListener.java $
 *
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * Copyright (c) 2003-2008 Mizuho International plc. and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the 
 * Free Software Foundation Europe e.V. Talstrasse 110, 40217 Dusseldorf, Germany 
 * or see the FSF site: http://www.fsfeurope.org/.
 * ====================================================================
 */
package org.ikasan.framework.monitor;

/**
 * Ikasan Abstract monitor listener.
 * 
 * @author Ikasan Development Team
 */
public abstract class AbstractMonitorListener implements MonitorListener
{
    /** Monitor listener name */
    private String name;

    /**
     * Constructor
     * 
     * @param name The name of the monitor
     */
    public AbstractMonitorListener(final String name)
    {
        this.name = name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ikasan.framework.initiator.monitor.MonitorListener#getName()
     */
    public String getName()
    {
        return this.name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ikasan.framework.initiator.monitor.MonitorListener#notify(java.lang.String)
     */
    public abstract void notify(String state);
}
