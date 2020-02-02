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
package org.ikasan.spec.trigger;

import org.ikasan.spec.trigger.Trigger;
import org.ikasan.spec.trigger.TriggerRelationship;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public interface TriggerService
{

    /**
     * Returns a Map all the triggers associated with a particular flow
     *
     * @param moduleName - THe name of the module
     * @param flowName - The name of the flow
     *
     * @return - List of triggers that apply in flow specified by the parameters
     */
    Map<String,List<Trigger>> getTriggers(String moduleName, String flowName);
    /**
     * Returns a safe List of all the triggers associated with a particular point in a particular flow
     *
     * @param moduleName - THe name of the module
     * @param flowName - The name of the flow
     * @param relationship - The Trigger relationship (before or after)
     * @param flowElementName - The flow element name
     *
     * @return - List of triggers that apply at the point in flow specified by the parameters
     */
    List<Trigger> getTriggers(String moduleName, String flowName, TriggerRelationship relationship, String flowElementName);

    /**
     * Deletes a dynamic trigger, specified by trigger id. This has the effect of:<br>
     * <br>
     *  1) de-registering the trigger from the mapped triggers, so that it no longer takes effect
     *  2) deleting the trigger so that it is not reloaded next time
     *
     * @param triggerId - The dynamic Trigger to deregister
     */
    void deleteDynamicTrigger(Long triggerId);

}
