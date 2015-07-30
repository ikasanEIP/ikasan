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
package org.ikasan.component.endpoint.filesystem.messageprovider;

import org.ikasan.spec.event.ManagedEventIdentifierService;

import java.io.File;
import java.util.List;

/**
 * Implementation of the ManagedIdentifierService for FileLine events.
 * Uses filename and line number.
 *
 * @author Ikasan Development Team
 */
public class FileLineEventIdentifierServiceImpl implements ManagedEventIdentifierService<String, List<File>>
{
    /** module name and flow name prefix */
    String prefix;

    /**
     * Constructor
     * @param prefix
     */
    public FileLineEventIdentifierServiceImpl(String prefix)
    {
        this.prefix = prefix;
    }

    /*
     * (non-Javadoc)
     * @see org.ikasan.spec.event.EventLifeIdentifierService#getLifeIdentifier(java.lang.Object)
     */
    public String getEventIdentifier(List<File> files)
    {
        StringBuffer sb = new StringBuffer(prefix);
        for(File file:files)
        {
            sb.append("_");
            sb.append(file.getName());
        }
        return sb.toString();
    }

    /*
     * (non-Javadoc)
     * @see org.ikasan.spec.event.EventLifeIdentifierService#setLifeIdentifier(java.lang.Object, java.lang.Object)
     */
    public void setEventIdentifier(String identifier, List<File> files)
    {
        // nothing to do here
    }
}
