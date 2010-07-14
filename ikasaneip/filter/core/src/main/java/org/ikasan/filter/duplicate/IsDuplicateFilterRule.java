/*
 * $Id$
 * $URL$
 * 
 * =============================================================================
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
 * =============================================================================
 */

package org.ikasan.filter.duplicate;

import org.ikasan.filter.FilterException;
import org.ikasan.filter.FilterRule;
import org.ikasan.filter.duplicate.model.FilterEntry;
import org.ikasan.filter.duplicate.model.FilterEntryConverter;
import org.ikasan.filter.duplicate.service.DuplicateFilterService;

/**
 * A {@link FilterRule} determining if a message has been "seen" before, or not.
 * 
 * @author Ikasan Development Team
 *
 */
public class IsDuplicateFilterRule<T> implements FilterRule<T>
{
    /** Service to access previous encountered messages*/
    private final DuplicateFilterService filterService;
    private final FilterEntryConverter<T> converter;

    /**
     * Constructor 
     * @param filterService
     */
    public IsDuplicateFilterRule(final DuplicateFilterService filterService,
            final FilterEntryConverter<T> converter)
    {
        this.filterService = filterService;
        this.converter = converter;
    }

    /*
     * (non-Javadoc)
     * @see org.ikasan.filter.FilterRule#accept(java.lang.String)
     */
    public boolean accept(T message) throws FilterException
    {
        FilterEntry messageToFilter = this.converter.convert(message);
        boolean messageFound = this.filterService.isDuplicate(messageToFilter);
        if (!messageFound)
        {
            this.filterService.persistMessage(messageToFilter);
            return true;
        }
        else
        {
            return false;
        }
    }
}
