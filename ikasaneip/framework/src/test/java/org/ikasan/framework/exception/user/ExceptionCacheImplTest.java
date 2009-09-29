 /* 
 * $Id$
 * $URL$
 *
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * 
 * Distributed under the Modified BSD License.
 * Copyright notice: The copyright for this software and a full listing 
 * of individual contributors are as shown in the packaged copyright.txt 
 * file. 
 * 
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 *
 *  - Neither the name of the ORGANIZATION nor the names of its contributors may
 *    be used to endorse or promote products derived from this software without 
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE 
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE 
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 */
package org.ikasan.framework.exception.user;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
