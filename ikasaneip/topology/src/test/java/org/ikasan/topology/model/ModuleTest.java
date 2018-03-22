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
package org.ikasan.topology.model;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author Ikasan Development Team
 *
 */
public class ModuleTest
{

    @Test
    public void addFlowWhenExistingFlowsDontExist(){

        Module uut = new Module();

        Flow flow = new Flow("testFlow","desctiption",null);
        Set<Flow> newSet = new HashSet<>();
        newSet.add(flow);
        //do test
        uut.addFlows(newSet);

        // assert
        assertTrue(uut.getFlows().contains(flow));
        assertEquals(1,uut.getFlows().size());


    }

    @Test
    public void addFlowWhenExistingFlowIsSameAsNewFlow(){

        Module uut = new Module();

        Flow flow = new Flow("testFlow","desctiption",null);
        Set<Flow> newSet = new HashSet<>();
        newSet.add(flow);

        Flow exitingFlow = new Flow("testFlow","desctiption",null);
        Set<Flow> exitingSet = new HashSet<>();
        exitingSet.add(exitingFlow);
        uut.setFlows(exitingSet);

        //do test
        uut.addFlows(newSet);

        // assert
        assertTrue(uut.getFlows().contains(flow));
        assertEquals(1,uut.getFlows().size());

    }

    @Test
    public void addFlowWhenExistingFlowIsSameAsNewFlow2(){

        Module uut = new Module();

        Flow flow = new Flow("testFlow","desctiption",null);
        flow.setId(1l);
        Set<Flow> newSet = new HashSet<>();
        newSet.add(flow);

        Flow exitingFlow = new Flow("testFlow","desctiption",null);
        exitingFlow.setId(1l);
        Set<Flow> exitingSet = new HashSet<>();
        exitingSet.add(exitingFlow);
        uut.setFlows(exitingSet);

        //do test
        uut.addFlows(newSet);

        // assert
        assertTrue(uut.getFlows().contains(flow));
        assertEquals(1,uut.getFlows().size());

    }
}
