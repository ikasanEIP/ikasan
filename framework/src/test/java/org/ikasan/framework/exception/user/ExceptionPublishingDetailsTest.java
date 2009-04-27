 /* 
 * $Id: ExceptionPublishingDetailsTest.java 16808 2009-04-27 07:28:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/framework/src/test/java/org/ikasan/framework/exception/user/ExceptionPublishingDetailsTest.java $
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
public class ExceptionPublishingDetailsTest
{
    
    /**
     * Test method for {@link org.ikasan.framework.exception.user.ExceptionPublishingDetails#isFilterDuplicates()}.
     */
    @Test
    public void testIsFilterDuplicates()
    {
        assertTrue(new ExceptionPublishingDetails(false, true, 0).isFilterDuplicates());
        assertFalse(new ExceptionPublishingDetails(false, false, 0).isFilterDuplicates());
    }

    /**
     * Test method for {@link org.ikasan.framework.exception.user.ExceptionPublishingDetails#getMaximumDuplicateAge()}.
     */
    @Test
    public void testGetMaximumDuplicateAge()
    {
        assertEquals(99, new ExceptionPublishingDetails(false, false, 99).getMaximumDuplicateAge());
    }

    /**
     * Test method for {@link org.ikasan.framework.exception.user.ExceptionPublishingDetails#isPublishable()}.
     */
    @Test
    public void testIsPublishable()
    {
        assertTrue(new ExceptionPublishingDetails(true, false, 0).isPublishable());
        assertFalse(new ExceptionPublishingDetails(false, false, 0).isPublishable());
    }
}
