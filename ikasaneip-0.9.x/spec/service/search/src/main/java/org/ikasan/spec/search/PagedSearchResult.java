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
package org.ikasan.spec.search;

import java.util.List;

/**
 * This class is intended to be used as a DTO for transporting a subset of search results for
 * some domain object <T> from a larger set of searched results. This is intended to support
 * paging.
 * 
 * For example we may be performing a search for some domain entities that would return 1000 results
 * if not paged. For performance and usability reasons, a search result of 1000 entries may not be
 * desired. This class allows for sub result set to be returned, including enough information to
 * establish its position within the superset, as well as the size of the super set
 * 
 * 
 * @author Ikasan Development Team
 *
 */
public interface PagedSearchResult<T>
{

    /**
     * Accessor for the current page content
     * @return List<T>
     */
    public List<T> getPagedResults();
    
    /**
     * Accessor for first result index
     * 
     * @return index of the first result of the paged results
     */
    public int getFirstResultIndex();

    /**
     * Accessor for last result index
     * 
     * @return index of the last result of the paged results
     */
    public long getLastResultIndex();

	/** 
	 * @return true if this represents the last page in the super result set
	 */
	public boolean isLastPage();

	/**
	 * Accessor for complete resultSize
	 * 
	 * @return size of the larger super result set
	 */
	public long getResultSize();
}
