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
package org.ikasan.console.module;

/**
 * Console representation of a Module
 * 
 * @author Ikasan Development Team
 */
public class Module
{

    /** Unique Id */
    private long id;

    /** Module Name */
    private String name;
    
    /** Module Description */
    private String description;
    
    /** The URL to the resource that shows the design of this module */
    private String designDiagramURL;
    
    /**
     * Get the id
     * 
     * @return id
     */
    public long getId()
    {
        return id;
    }

    /**
     * Set the id
     * 
     * @param id - id to set
     */
    public void setId(long id)
    {
        this.id = id;
    }

    /**
     * Get the name
     * 
     * @return name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Set the name
     * 
     * @param name - name to set
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Get the description
     * 
     * @return description
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Set the description
     * 
     * @param description - name to set
     */
    public void setDescription(String description)
    {
        this.description = description;
    }

    /**
     * Get the design diagram URL
     * 
     * @return the design diagram URL
     */
    public String getDesignDiagramURL()
    {
        return designDiagramURL;
    }

    /**
     * Set the design diagram URL
     * 
     * @param designDiagramURL - The design diagram URL to set
     */
    public void setDesignDiagramURL(String designDiagramURL)
    {
        this.designDiagramURL = designDiagramURL;
    }
    
}
