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

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author Ikasan Development Team
 *
 */
public class ExceptionCacheImplTest
{

    /** One minute in millisec */
    private static final int ONE_MINUTE = 60000;

    /**
     * Test method for {@link org.ikasan.framework.exception.user.ExceptionCacheImpl#notifiedSince(java.lang.String, long)}.
     */
    @Test
    public void testPublishedSince_cacheHit()
    {
        ExceptionCacheImpl publicationCacheImpl = new ExceptionCacheImpl();
        String exceptionResolutionId = "exceptionResoultionId";
        publicationCacheImpl.notify(exceptionResolutionId);
        assertTrue("publishedSince should return true for a exception we just published", publicationCacheImpl.notifiedSince(exceptionResolutionId, ONE_MINUTE));
    }
    
    /**
     * Test method for {@link org.ikasan.framework.exception.user.ExceptionCacheImpl#notifiedSince(java.lang.String, long)}.
     */
    @Test
    public void testPublishedSince_cacheMiss()
    {
        ExceptionCacheImpl publicationCacheImpl = new ExceptionCacheImpl();
        String exceptionResolutionId = "exceptionResoultionId";
        assertFalse("publishedSince should return false for a previously unpublished exception", publicationCacheImpl.notifiedSince(exceptionResolutionId, ONE_MINUTE));

    }
}
