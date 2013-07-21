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
package org.ikasan.module;

import java.util.ArrayList;
import java.util.List;

import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;

/**
 * A simple representation of a Module
 * 
 * @author Ikasan Development Team
 */
public class SimpleModule implements Module
{
    /** Flows within this module */
    private List<Flow> flows;

    /** Module name */
    protected String name;

    /** Human readable description of this module */
    private String description;

    /**
     * Constructor
     * 
     * @param name The name of the module
     * @param flows A list of Flows for the module
     */
    public SimpleModule(String name, List<Flow> flows)
    {
        this(name);
        this.flows = new ArrayList<Flow>(flows);
    }

    /**
     * Constructor
     * 
     * @param name Name of the module
     */
    public SimpleModule(String name)
    {
        this.name = name;
        if(name == null)
        {
            throw new IllegalArgumentException("name cannot be 'null'");
        }
    }

    /**
     * Accessor for name
     * 
     * @return module name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @return the flows
     */
    public List<Flow> getFlows()
    {
        return new ArrayList<Flow>(this.flows);
    }

    /**
     * @return the flow matching the given name
     */
    public Flow getFlow(String name)
    {
        for(Flow flow:this.flows)
        {
            if(flow.getName().equals(name))
            {
                return flow;
            }
        }

        return null;
    }

    /**
     * @see org.ikasan.framework.module.Module#getDescription()
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Set the description. 
     * 
     * @param description - description to set
     */
    public void setDescription(String description)
    {
        this.description = description;
    }

}
