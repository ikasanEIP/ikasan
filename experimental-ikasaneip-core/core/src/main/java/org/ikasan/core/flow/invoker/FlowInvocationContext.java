/* 
 * $Id: 
 * $URL: 
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
 
package org.ikasan.core.flow.invoker;

import java.util.ArrayList;
import java.util.List;

/**
 * This class acts as a transfer object holding flow invocation time data relevent only
 * to a single invocation of an Event down a Flow. 
 * 
 * At time of writing, the only data item that we are interested in is the name of the last
 * component invoked, and only then when dealing with an error scenario.
 * 
 * Unlike the Event object, the same FlowInvocation object will be present immediately prior
 * to the invocation of any component in a flow. The Events of course may be split, aggregated, etc.
 * 
 * It remains to be seen if we will need to transport any other data in this object, of if at some
 * later stage, the FlowComponents themselves will need access to this information
 * 
 * @author Ikasan Development Team
 *
 */
public class FlowInvocationContext
{
    /**
     * a stack of the names of all components invoked so far
     */
    private List<String> invokedComponents = new ArrayList<String>();

    /**
     * Accessor for the name of the last component invoked
     * 
     * @return name of the last component invoked, or null if none exists yet
     */
    public String getLastComponentName()
    {
        String lastComponentName = null;
        if (!this.invokedComponents.isEmpty())
        {
            lastComponentName = this.invokedComponents.get(this.invokedComponents.size()-1);
        }
        return lastComponentName;
    }

    /**
     * Allows a new componentName to be added to the stack of invoked components
     * 
     * @param componentName componentName
     */
    public void addInvokedComponentName(String componentName)
    {
        this.invokedComponents.add(componentName);
    }

    /**
     * Safe accessor for the entire stack of invoked components
     * 
     * @return List of componentNames of all invoked components
     */
    public List<String> getInvokedComponents()
    {
        return new ArrayList<String>(this.invokedComponents);
    }

}
