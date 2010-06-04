/**
 * 
 */
package org.ikasan.framework.management.search;

import java.util.ArrayList;
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
public class ArrayListPagedSearchResult<T> extends ArrayList<T> implements PagedSearchResult<T> {



	/**
	 * generated serial version UID
	 */
	private static final long serialVersionUID = -6291822622891056813L;

	/**
	 * index of the first result shown here into the larger super set of results
	 */
	private int firstResultIndex;
	
	/**
	 * size of the larger super set of results
	 */
	private int resultSize;
	
	/**
	 * Constructor
	 * 
	 * @param pagedResults - search results comprising a single page of results from a larger super set
	 * @param firstResult - index of the first result shown here into the larger super set of results
	 * @param resultSize - size of the larger super set of results
	 */
	public ArrayListPagedSearchResult(List<T> pagedResults, int firstResultIndex,
			int resultSize) {
		super(pagedResults);
		this.firstResultIndex = firstResultIndex;
		this.resultSize = resultSize;
	}
	

	/**
	 * Accessor for first result
	 * 
	 * @return index of the first result shown here into the larger super set of results
	 */
	public int getFirstResultIndex() {
		return firstResultIndex;
	}

	/**
	 * Accessor for resultSize
	 * 
	 * @return size of the larger super result set
	 */
	public int getResultSize() {
		return resultSize;
	}


	/* (non-Javadoc)
	 * @see org.ikasan.framework.management.search.PagedSearchResult#isLastPage()
	 */
	public boolean isLastPage() {
		return resultSize==(firstResultIndex+size());
	}


}
