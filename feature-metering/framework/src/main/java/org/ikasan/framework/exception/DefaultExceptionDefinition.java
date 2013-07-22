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
package org.ikasan.framework.exception;

import org.ikasan.common.ExceptionType;

/**
 * Default implementation of an Ikasan Exception Definition as utilised by the IkasanExceptionResolver concrete
 * implementation.
 * 
 * @author Ikasan Development Team
 */
public class DefaultExceptionDefinition
{
    /** Resolution of this exception definition */
    private IkasanExceptionResolution resolution;

    /** ClassName of this exception definition */
    private String className;

    /** Optional type of this exception definition */
    private ExceptionType type;

    /**
     * Constructor
     * 
     * @param resolution The resolution
     * @param className The class name of this definition
     */
    public DefaultExceptionDefinition(final IkasanExceptionResolution resolution, final String className)
    {
        this.resolution = resolution;
        this.className = className;
    }

    /**
     * Constructor
     * 
     * @param resolution The resolution
     * @param className The class name of this definition
     * @param type The type of this definition
     */
    public DefaultExceptionDefinition(final IkasanExceptionResolution resolution, final String className,
            final ExceptionType type)
    {
        this.resolution = resolution;
        this.className = className;
        this.type = type;
    }

    /**
     * @return the resolution
     */
    public IkasanExceptionResolution getResolution()
    {
        return this.resolution;
    }

    /**
     * @param resolution the resolution to set
     */
    public void setResolution(IkasanExceptionResolution resolution)
    {
        this.resolution = resolution;
    }

    /**
     * @return the className
     */
    public String getClassName()
    {
        return this.className;
    }

    /**
     * @param className the className to set
     */
    public void setClassName(String className)
    {
        this.className = className;
    }

    /**
     * @return the type
     */
    public ExceptionType getType()
    {
        return this.type;
    }

    /**
     * @param type the type to set
     */
    public void setType(ExceptionType type)
    {
        this.type = type;
    }
}
