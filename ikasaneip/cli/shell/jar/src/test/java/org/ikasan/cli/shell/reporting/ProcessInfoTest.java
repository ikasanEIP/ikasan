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
package org.ikasan.cli.shell.reporting;

import org.ikasan.cli.shell.operation.model.ProcessType;
import org.jmock.Mockery;
import org.jmock.imposters.ByteBuddyClassImposteriser;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * This test class supports the <code>ProcessInfo</code> class.
 * 
 * @author Ikasan Development Team
 */
class ProcessInfoTest
{
    /**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ByteBuddyClassImposteriser.INSTANCE);
        }
    };

    @Test
    void successful_new_h2_processInfo()
    {
        ProcessInfo processInfo = new ProcessInfo();
        processInfo.setPid(12345L);
        processInfo.setRunning(true);
        processInfo.setProcessType(ProcessType.getH2Instance());
        processInfo.setUsername("username");
        processInfo.setCommand("command");
        processInfo.setCommandLine("commandLine");
        processInfo.setException(new Exception("test"));
        processInfo.setProcess(null);
        processInfo.setName("name");
        processInfo.setPsOperation();

        assertTrue(processInfo.getPid() == 12345L);
        assertTrue(processInfo.isRunning());
        assertEquals(processInfo.getProcessType(), ProcessType.getH2Instance());
        assertEquals("username", processInfo.getUsername());
        assertEquals("command", processInfo.getCommand());
        assertEquals("commandLine", processInfo.getCommandLine());
        assertNotNull(processInfo.getException());
        assertEquals("name", processInfo.getName());
        assertEquals("ps", processInfo.getOperation());
    }

    @Test
    void successful_new_module_processInfo()
    {
        ProcessInfo processInfo = new ProcessInfo();
        processInfo.setPid(12345L);
        processInfo.setRunning(true);
        processInfo.setProcessType(ProcessType.getModuleInstance());
        processInfo.setUsername("username");
        processInfo.setCommand("command");
        processInfo.setCommandLine("commandLine");
        processInfo.setException(new Exception("test"));
        processInfo.setProcess(null);
        processInfo.setName("name");
        processInfo.setPsOperation();

        assertTrue(processInfo.getPid() == 12345L);
        assertTrue(processInfo.isRunning());
        assertEquals(processInfo.getProcessType(), ProcessType.getModuleInstance());
        assertEquals("username", processInfo.getUsername());
        assertEquals("command", processInfo.getCommand());
        assertEquals("commandLine", processInfo.getCommandLine());
        assertNotNull(processInfo.getException());
        assertEquals("name", processInfo.getName());
        assertEquals("ps", processInfo.getOperation());
    }

    @Test
    void successful_new_generic_processInfo()
    {
        ProcessInfo processInfo = new ProcessInfo();
        processInfo.setPid(12345L);
        processInfo.setRunning(true);
        processInfo.setProcessType(ProcessType.getGenericInstance());
        processInfo.setUsername("username");
        processInfo.setCommand("command");
        processInfo.setCommandLine("commandLine");
        processInfo.setException(new Exception("test"));
        processInfo.setProcess(null);
        processInfo.setName("name");
        processInfo.setPsOperation();

        assertTrue(processInfo.getPid() == 12345L);
        assertTrue(processInfo.isRunning());
        assertEquals(processInfo.getProcessType(), ProcessType.getGenericInstance());
        assertEquals("username", processInfo.getUsername());
        assertEquals("command", processInfo.getCommand());
        assertEquals("commandLine", processInfo.getCommandLine());
        assertNotNull(processInfo.getException());
        assertEquals("name", processInfo.getName());
        assertEquals("ps", processInfo.getOperation());
    }

    @Test
    void successful_toJson() throws JSONException
    {
        ProcessInfo processInfo = new ProcessInfo();
        processInfo.setPid(12345L);
        processInfo.setRunning(true);
        processInfo.setProcessType(ProcessType.getH2Instance());
        processInfo.setUsername("username");
        processInfo.setCommand("command");
        processInfo.setCommandLine("commandLine");
        processInfo.setException(new Exception("test"));
        processInfo.setProcess(null);
        processInfo.setName("name");
        processInfo.setPsOperation();

        JSONObject json = processInfo.toJSON();
        assertEquals(Boolean.TRUE, json.get("running"));
        assertEquals("command", json.get("command"));
        assertEquals(12345L, json.get("pid"));
        assertEquals("H2", json.get("type"));
        assertEquals("username", json.get("username"));
        assertEquals("command", json.get("command"));
        assertEquals("commandLine", json.get("commandLine"));
        assertEquals("name", json.get("name"));
        assertEquals("ps", json.get("operation"));
    }
}

