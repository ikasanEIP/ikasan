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
package org.ikasan.common.component;

/**
 * Enumeration of specs used within the Payload object.
 * 
 * @author Ikasan Development Team
 */
public enum Spec
{
    /** XML */
    TEXT_XML("text/xml"), //$NON-NLS-1$
    /** HTML */
    TEXT_HTML("text/html"), //$NON-NLS-1$
    /** CSV */
    TEXT_CSV("text/csv"), //$NON-NLS-1$
    /** TXT */
    TEXT_PLAIN("text/plain"), //$NON-NLS-1$
    /** JAR */
    BYTE_JAR("byte/jar"), //$NON-NLS-1$
    /** ZIP */
    BYTE_ZIP("byte/zip"), //$NON-NLS-1$
    /** plain binary - default */
    BYTE_PLAIN("byte/plain"); //$NON-NLS-1$

    /** Serialize ID */
    private static final long serialVersionUID = 1L;

    /** Spec MIME type */
    private final String mime_type;

    /**
     * Creates a new instance of <code>Spec</code> with the specified MIME type.
     * 
     * @param mime_type - The spec MIME type
     */
    private Spec(final String mime_type)
    {
        this.mime_type = mime_type;
    }

    /**
     * Utility method for returning spec as String
     */
    @Override
    public String toString()
    {
        return this.mime_type;
    }

}
