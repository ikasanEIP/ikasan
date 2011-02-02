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
package org.ikasan.spec.flow;


/**
 * Interface representing a business path for a <code>FlowEvent<code>
 * 
 * Invocation represents the traversal of that business path.
 * 
 * @author Ikasan Development Team
 */
public interface Flow
{
    /**
     * Invocation of this method represents the handling of the <code>FlowEvent<code>
     * with respect to some business path
     * 
     * @param flowInvocationContext invocation context
     * @param flowEvent The business event we're dealing with
     */
    public void invoke(FlowInvocationContext flowInvocationContext, FlowEvent flowEvent);

    /**
     * Returns the name of this flow
     * 
     * @return String name of this flow
     */
    public String getName();

    /**
     * Accessor for moduleName
     * 
     * @return name of the module this flow exist for
     */
    public String getModuleName();

    /**
     * Invoke all start operations for the flow that are required prior to an event invocation.
     * For instance, this could include setting any flow component configurations,
     * or starting any flow managed resources.
     */
    public void start();
    
    /**
     * Invoke all stop operations for the flow that are required on shutdown of the invoking client.
     * For instance, this could include stopping any flow managed resources.
     */
    public void stop();
}
