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
package org.ikasan.framework.exception.user;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple in memory cache that recalls the last time an exception resolution Id was added and can determine if an
 * incoming resolution Id is already in the cache
 * 
 * @author Ikasan Development Team
 */
public class ExceptionCacheImpl implements ExceptionCache
{
    /** Map to store resolutionIds and last added timestamp */
    private Map<String, Long> cache = new HashMap<String, Long>();

    public void notify(String exceptionResoultionId)
    {
        cache.put(exceptionResoultionId, System.currentTimeMillis());
    }

    public boolean notifiedSince(String exceptionResoultionId, long publicationPeriod)
    {
        boolean result = false;
        Long lastAddedTimestamp = cache.get(exceptionResoultionId);
        if (lastAddedTimestamp != null)
        {
            if ((System.currentTimeMillis() - lastAddedTimestamp) < publicationPeriod)
            {
                result = true;
            }
        }
        return result;
    }
}
