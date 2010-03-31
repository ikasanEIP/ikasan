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
package org.ikasan.common;

import java.util.List;

/**
 * Envelope providing the generic facade for all data to be moved around in an encapsulated object.
 * 
 * @author Ikasan Development Team
 */
public interface Envelope extends MetaDataInterface
{
    /** Primary payload is always at the first position in any Payload List */
    public static final int PRIMARY_PAYLOAD = 0;

    /** Root name of the envelope */
    public static final String ENVELOPE_ROOT_NAME = "envelope"; //$NON-NLS-1$

    /**
     * Setter for envelope payloads
     * 
     * @param payloads payloads to set
     */
    public void setPayloads(List<Payload> payloads);

    /**
     * Getter for envelope payloads
     * 
     * @return payloads
     */
    public List<Payload> getPayloads();

    /**
     * Test the equality of two envelope instances
     * 
     * @param envelope Envelope to test against
     * @return boolean
     */
    public boolean equals(Envelope envelope);
}
