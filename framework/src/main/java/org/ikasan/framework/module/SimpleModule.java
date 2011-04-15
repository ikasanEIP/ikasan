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
package org.ikasan.framework.module;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.ikasan.spec.flow.Flow;

/**
 * A simple representation of a Module
 * 
 * @author Ikasan Development Team
 */
public class SimpleModule implements Module
{
    /** Initiators of flows within this module */
    private List<Flow> flows;

    /** Module name */
    protected String name;

    /** Human readable description of this module */
    private String description;

    /**
     * Constructor
     * 
     * @param name The name of the module
     * @param flows a set of flows making the module
     */
    public SimpleModule(final String name, final List<Flow> flows)
    {
        this.name = name;
        this.flows = new ArrayList<Flow>(flows);
    }

    /**
     * Accessor for name
     * 
     * @return module name
     */
    public String getName()
    {
        return this.name;
    }

    /* (non-Javadoc)
     * @see org.ikasan.framework.module.Module#getFlows()
     */
    public Map<String, Flow> getFlows()
    {
        Map<String, Flow> result = new LinkedHashMap<String, Flow>();
        for (Flow flow : this.flows)
        {
            result.put(flow.getName(), flow);
        }
        return result;
    }

    /**
     * @see org.ikasan.framework.module.Module#getDescription()
     */
    public String getDescription()
    {
        return this.description;
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

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuffer sb = new StringBuffer(getClass().getName() + " [");
        sb.append("name=");
        sb.append(this.name);
        sb.append(",");
        sb.append("description=");
        sb.append(this.description);
        sb.append(",");
        sb.append("flows=");
        sb.append(this.flows);
        sb.append("]");
        return sb.toString();
    }
}
