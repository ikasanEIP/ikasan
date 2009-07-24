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
package org.ikasan.connector.basefiletransfer.net;

import java.util.Comparator;

/**
 * <code>OlderFirstClientListEntryComparator</code> is an implementation of
 * {@link Comparator} for <code>ClientListEntry</code> that compares two such
 * objects based on their last modified dates.
 * 
 * @author Ikasan Development Team 
 * 
 */
public class OlderFirstClientListEntryComparator implements Comparator<ClientListEntry>
{
    public int compare(ClientListEntry cle1, ClientListEntry cle2)
    {
        boolean older = cle1.getMtime() < cle2.getMtime();
        boolean newer = cle1.getMtime() > cle2.getMtime();
        if (older && newer)
        {
            return 0;
        }
        else if (newer)
        {
            return 1;
        }
        else
        {
            return -1;
        }
    }
}