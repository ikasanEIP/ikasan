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
package org.ikasan.error.reporting.model;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class ErrorCategorisation
{
	public static final String TRIVIAL = "Trivial";
	public static final String MAJOR = "Major";
	public static final String CRITICAL = "Critical";
	public static final String BLOCKER = "Blocker";
	
	private Long id;
	private String errorCategory;
	private String errorDescription;

	/**
	 * Hibernate likes this. 
	 */
	public ErrorCategorisation()
	{
		super();
	}

	/**
	 * @param moduleName
	 * @param flowName
	 * @param componentName
	 * @param errorCategory
	 * @param errorDescription
	 */
	public ErrorCategorisation(String errorCategory, String errorDescription)
	{
		super();

		this.errorCategory = errorCategory;
		this.errorDescription = errorDescription;
	}
	

	
	/**
	 * @return the id
	 */
	public Long getId()
	{
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id)
	{
		this.id = id;
	}

	/**
	 * @return the errorCategory
	 */
	public String getErrorCategory()
	{
		return errorCategory;
	}

	/**
	 * @param errorCategory the errorCategory to set
	 */
	public void setErrorCategory(String errorCategory)
	{
		this.errorCategory = errorCategory;
	}

	/**
	 * @return the errorDescription
	 */
	public String getErrorDescription()
	{
		return errorDescription;
	}

	/**
	 * @param errorDescription the errorDescription to set
	 */
	public void setErrorDescription(String errorDescription)
	{
		this.errorDescription = errorDescription;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((errorCategory == null) ? 0 : errorCategory.hashCode());
		result = prime
				* result
				+ ((errorDescription == null) ? 0 : errorDescription.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ErrorCategorisation other = (ErrorCategorisation) obj;
		if (errorCategory == null)
		{
			if (other.errorCategory != null)
				return false;
		} else if (!errorCategory.equals(other.errorCategory))
			return false;
		if (errorDescription == null)
		{
			if (other.errorDescription != null)
				return false;
		} else if (!errorDescription.equals(other.errorDescription))
			return false;
		if (id == null)
		{
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "ErrorCategorisation [id=" + id + ", errorCategory="
				+ errorCategory + ", errorDescription=" + errorDescription
				+ "]";
	}
}
