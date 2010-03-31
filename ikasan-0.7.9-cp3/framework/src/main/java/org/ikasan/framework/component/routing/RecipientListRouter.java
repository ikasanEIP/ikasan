/* 
 * $Id$
 * $URL$
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
package org.ikasan.framework.component.routing;

import java.util.ArrayList;
import java.util.List;

import org.ikasan.framework.component.Event;

/**
 * Implementation of EIP 'RecipientList' Pattern as a <code>Router</code>
 * 
 * This class is configured with a <code>List<String></code> of recipient names to which we route, regardless of the
 * <code>Event</code>
 * 
 * @author Ikasan Development Team
 */
public class RecipientListRouter implements Router
{
    /** List of recipients to which we intend to route */
    private List<String> recipients;

    /**
     * Constructor
     * 
     * @param recipients List of recipients to route to
     */
    public RecipientListRouter(List<String> recipients)
    {
        super();
        if (recipients == null)
        {
            throw new IllegalArgumentException("recipients cannot be null");
        }
        this.recipients = recipients;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ikasan.framework.component.routing.Router#onEvent(org.ikasan.framework.component.Event)
     */
    public List<String> onEvent(Event event)
    {
        return new ArrayList<String>(recipients);
    }
}
