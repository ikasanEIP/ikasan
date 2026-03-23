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
package org.ikasan.spec.security.model;

import java.util.Date;

/**
 * Defines the type of resource that can be linked to a {@link Policy} through a {@link PolicyLink}.
 *
 * <p>Policy link types categorize the different kinds of resources that policies can be applied to,
 * such as modules, flows, components, or job plans. Each type includes metadata about the associated
 * database table for ORM mapping purposes.
 *
 * @author Ikasan Development Team
 * @since 1.0
 */
public interface PolicyLinkType
{
	/**
	 * Retrieves the unique identifier of this policy link type.
	 *
	 * @return the unique identifier, or {@code null} if not yet persisted
	 */
	Long getId();

	/**
	 * Retrieves the name of this policy link type.
	 *
	 * @return the type name (e.g., "Module", "Flow", "JobPlan"), never {@code null}
	 */
	String getName();

	/**
	 * Sets the name of this policy link type.
	 *
	 * @param name the type name to set, must not be {@code null}
	 */
	void setName(String name);

	/**
	 * Retrieves the date and time when this policy link type was created.
	 *
	 * @return the creation timestamp, or {@code null} if not set
	 */
	Date getCreatedDateTime();

	/**
	 * Sets the date and time when this policy link type was created.
	 *
	 * @param createdDateTime the creation timestamp to set
	 */
	void setCreatedDateTime(Date createdDateTime);

	/**
	 * Retrieves the date and time when this policy link type was last updated.
	 *
	 * @return the last update timestamp, or {@code null} if not set
	 */
	Date getUpdatedDateTime();

	/**
	 * Sets the date and time when this policy link type was last updated.
	 *
	 * @param updatedDateTime the last update timestamp to set
	 */
	void setUpdatedDateTime(Date updatedDateTime);

	/**
	 * Retrieves the name of the database table associated with this policy link type.
	 *
	 * <p>Used for ORM mapping to identify which table contains the target resources
	 * referenced by policy links of this type.
	 *
	 * @return the database table name, or {@code null} if not set
	 */
	String getTableName();

	/**
	 * Sets the name of the database table associated with this policy link type.
	 *
	 * @param tableName the database table name to set
	 */
	void setTableName(String tableName);

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	int hashCode();

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	boolean equals(Object obj);

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	String toString();
}
