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
 * Represents a link between a {@link Policy} and a specific resource.
 *
 * <p>Policy links enable fine-grained access control by associating policies with specific
 * resources such as modules, flows, or job plans. The {@link PolicyLinkType} defines the
 * type of resource being linked, while the target ID references the specific resource instance.
 *
 * @author Ikasan Development Team
 * @since 1.0
 */
public interface PolicyLink
{
	/**
	 * Retrieves the unique identifier of this policy link.
	 *
	 * @return the unique identifier, or {@code null} if not yet persisted
	 */
	Long getId();

	/**
	 * Retrieves the type of this policy link, defining what kind of resource it targets.
	 *
	 * @return the {@link PolicyLinkType} for this link, never {@code null}
	 */
	PolicyLinkType getPolicyLinkType();

	/**
	 * Sets the type of this policy link.
	 *
	 * @param policyLinkType the {@link PolicyLinkType} to set, must not be {@code null}
	 */
	void setPolicyLinkType(PolicyLinkType policyLinkType);

	/**
	 * Retrieves the identifier of the target resource this policy link references.
	 *
	 * @return the target resource identifier, or {@code null} if not set
	 */
	Long getTargetId();

	/**
	 * Sets the identifier of the target resource this policy link references.
	 *
	 * @param targetId the target resource identifier to set
	 */
	void setTargetId(Long targetId);

	/**
	 * Retrieves the name of the target resource this policy link references.
	 *
	 * @return the target resource name, or {@code null} if not set
	 */
	String getName();

	/**
	 * Sets the name of the target resource this policy link references.
	 *
	 * @param name the target resource name to set
	 */
	void setName(String name);

	/**
	 * Retrieves the date and time when this policy link was created.
	 *
	 * @return the creation timestamp, or {@code null} if not set
	 */
	Date getCreatedDateTime();

	/**
	 * Sets the date and time when this policy link was created.
	 *
	 * @param createdDateTime the creation timestamp to set
	 */
	void setCreatedDateTime(Date createdDateTime);

	/**
	 * Retrieves the date and time when this policy link was last updated.
	 *
	 * @return the last update timestamp, or {@code null} if not set
	 */
	Date getUpdatedDateTime();

	/**
	 * Sets the date and time when this policy link was last updated.
	 *
	 * @param updatedDateTime the last update timestamp to set
	 */
	void setUpdatedDateTime(Date updatedDateTime);

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
