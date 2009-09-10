/**
 * 
 */
package org.ikasan.framework.management.search;

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
public interface PagedSearchResult<T> extends List<T> {


	/**
	 * Accessor for first result index
	 * 
	 * @return index of the first result shown here into the larger super set of results
	 */
	public int getFirstResultIndex();

	/** 
	 * @return true if this represents the last page in the super result set
	 */
	public boolean isLastPage();

	/**
	 * Accessor for resultSize
	 * 
	 * @return size of the larger super result set
	 */
	public int getResultSize();
}
