/*
 * $Id$
 * $URL$
 * 
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * Copyright (c) 2003-2010 Mizuho International plc. and individual contributors as indicated
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

package org.ikasan.filter.duplicate.model;

import java.util.Date;

/**
 * Interface defining what a persisted message should look like.
 * 
 * @author Summer
 *
 */
public interface FilterEntry
{
    /** 
     * Getter for a clientId variable. Together with the criteria,
     * it identifies a persisted {@link FilterEntry}
     * 
     * @return The client id
     */
    public String getClientId();

    /** Getter for a criteria variable: object unique about a message.
     * Together with clientId, it identifies a persisted {@link FilterEntry}
     * 
     * @return criteria object whatever it might be.
     */
    public Integer getCriteria();

    /**
     * Getter for {@link Date} object representing the date/time a {@link FilterEntry}
     * was persisted.
     * @return a creation date.
     */
    public Date getCreatedDateTime();

    /**
     * Getter for {@link Date} object representing the date/time a {@link FilterEntry}
     * is expired and can be removed from persistance.
     * @return an expiry date
     */
    public Date getExpiry();
}
