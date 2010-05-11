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
package org.ikasan.filter;

/**
 * Default implementation of {@link MessageFilter} that delegates to a
 * {@link FilterRule} to evaluate the incoming message.
 * 
 * @author Summer
 *
 */
public class DefaultMessageFilter implements MessageFilter
{
    /** The {@link FilterRule} evaluating the incoming message */
    private final FilterRule filterRule;

    /*
     * optimistic place holder for future time where we can
     * specify a discarded message channel as part of MessageFilter
     * creation.
     */
    //DiscaredMessageChannel

    /**
     * Constructor
     * @param filterRule The {@link FilterRule} instance evaluating incoming message.
     */
    public DefaultMessageFilter(final FilterRule filterRule)
    {
        this.filterRule = filterRule;
    }

    /*
     * (non-Javadoc)
     * @see org.ikasan.filter.MessageFilter#filter(java.lang.String)
     */
    public String filter(String message)
    {
        if (this.filterRule.accept(message))
        {
            return message;
        }
        else
        {
            return null;
        }
    }

}
