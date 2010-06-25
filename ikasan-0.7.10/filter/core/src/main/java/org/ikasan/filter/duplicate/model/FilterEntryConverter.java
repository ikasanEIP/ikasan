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

/**
 * Base interface for converting any message to a {@link FilterEntry}. It defines
 * the contract for evaluating the Criteria of a message.
 * 
 * @author Ikasan Development Team
 *
 * @param <T> Type of message to convert
 */
public interface FilterEntryConverter<T>
{
    /**
     * Convert any object to a {@link FilterEntry} instance
     * @param object The instance to convert
     * @return A {@link FilterEntry} representation of an object
     * @throws FilterEntryConverterException allow configuration of the flow to 
     * skip filter entry issues caused by data
     */
    public FilterEntry convert(T message) throws FilterEntryConverterException;
}
