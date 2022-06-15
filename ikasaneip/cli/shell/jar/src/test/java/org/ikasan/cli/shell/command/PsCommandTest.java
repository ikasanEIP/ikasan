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
package org.ikasan.cli.shell.command;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * This test class supports the <code>PsCommand</code> class.
 * 
 * @author Ikasan Development Team
 */
class PsCommandTest
{
    List<Process> processes = new ArrayList<Process>();

    @Test
    void successful_ps_h2_and_module_match() throws IOException, JSONException
    {
        List<String> firstProcess = new ArrayList<String>();
        firstProcess.add("java");
        firstProcess.add("-classpath");
        firstProcess.add("cli/shell/target/test-classes:target/test-classes");
        firstProcess.add("org.ikasan.cli.sample.process.SampleProcess");
        firstProcess.add("-Dmodule.name=sampleProcess");
        firstProcess.add("-DfakeH2Signature=org.h2.tools.Server");

        ProcessBuilder processBuilder = new ProcessBuilder(firstProcess);
        Process process = processBuilder.start();
        processes.add(process);

        List<String> secondProcess = new ArrayList<String>();
        secondProcess.add("java");
        secondProcess.add("-classpath");
        secondProcess.add("cli/shell/target/test-classes:target/test-classes");
        secondProcess.add("org.ikasan.cli.sample.process.SampleProcess");
        secondProcess.add("-Dmodule.name=sampleProcess");
        secondProcess.add("-DfakeModuleSignature=spring.jta.logDir");

        processBuilder = new ProcessBuilder(secondProcess);
        process = processBuilder.start();
        processes.add(process);

        // give process time to start
        pause(1000);

        PsCommand command = new PsCommand();

        // test all match
        JSONObject result = command._ps("sampleProcess", null);
        JSONArray processes = (JSONArray)result.get("Processes");
        Assert.assertTrue(processes.length() == 2);

        JSONObject resultProcess = (JSONObject)processes.get(0);
        Assert.assertTrue("running should be true", resultProcess.get("running").equals(true));
        Assert.assertTrue("type should be H2, but returned " + resultProcess.get("type"), resultProcess.get("type").equals("H2"));
        Assert.assertTrue("operation should be ps", resultProcess.get("operation").equals("ps"));
        Assert.assertNotNull("username should be not null", resultProcess.get("username"));
        Assert.assertNotNull("pid should be not null", resultProcess.get("pid"));

        resultProcess = (JSONObject)processes.get(1);
        Assert.assertTrue("running should be true", resultProcess.get("running").equals(true));
        Assert.assertTrue("type should be H2", resultProcess.get("type").equals("Module"));
        Assert.assertTrue("operation should be ps", resultProcess.get("operation").equals("ps"));
        Assert.assertNotNull("username should be not null", resultProcess.get("username"));
        Assert.assertNotNull("pid should be not null", resultProcess.get("pid"));
    }

    @Test
    void successful_ps_h2_match_no_module_match() throws IOException, JSONException
    {
        List<String> firstProcess = new ArrayList<String>();
        firstProcess.add("java");
        firstProcess.add("-classpath");
        firstProcess.add("cli/shell/target/test-classes:target/test-classes");
        firstProcess.add("org.ikasan.cli.sample.process.SampleProcess");
        firstProcess.add("-Dmodule.name=sampleProcess");
        firstProcess.add("-DfakeH2Signature=org.h2.tools.Server");

        ProcessBuilder processBuilder = new ProcessBuilder(firstProcess);
        Process process = processBuilder.start();
        processes.add(process);

        List<String> secondProcess = new ArrayList<String>();
        secondProcess.add("java");
        secondProcess.add("-classpath");
        secondProcess.add("cli/shell/target/test-classes:target/test-classes");
        secondProcess.add("org.ikasan.cli.sample.process.SampleProcess");
        secondProcess.add("-Dmodule.name=sampleProcess");

        processBuilder = new ProcessBuilder(secondProcess);
        process = processBuilder.start();
        processes.add(process);

        // give process time to start
        pause(1000);

        PsCommand command = new PsCommand();

        // test all match
        JSONObject result = command._ps("sampleProcess", null);
        JSONArray processes = (JSONArray)result.get("Processes");
        Assert.assertTrue(processes.length() == 1);

        JSONObject resultProcess = (JSONObject)processes.get(0);
        Assert.assertTrue("running should be true", resultProcess.get("running").equals(true));
        Assert.assertTrue("type should be H2", resultProcess.get("type").equals("H2"));
        Assert.assertTrue("operation should be ps", resultProcess.get("operation").equals("ps"));
        Assert.assertNotNull("username should be not null", resultProcess.get("username"));
        Assert.assertNotNull("pid should be not null", resultProcess.get("pid"));
    }

    @Test
    void successful_ps_no_h2_match_module_match() throws IOException, JSONException
    {
        List<String> firstProcess = new ArrayList<String>();
        firstProcess.add("java");
        firstProcess.add("-classpath");
        firstProcess.add("cli/shell/target/test-classes:target/test-classes");
        firstProcess.add("org.ikasan.cli.sample.process.SampleProcess");
        firstProcess.add("-Dmodule.name=sampleProcess");

        ProcessBuilder processBuilder = new ProcessBuilder(firstProcess);
        Process process = processBuilder.start();
        processes.add(process);

        List<String> secondProcess = new ArrayList<String>();
        secondProcess.add("java");
        secondProcess.add("-classpath");
        secondProcess.add("cli/shell/target/test-classes:target/test-classes");
        secondProcess.add("org.ikasan.cli.sample.process.SampleProcess");
        secondProcess.add("-Dmodule.name=sampleProcess");
        secondProcess.add("-DfakeModuleSignature=spring.jta.logDir");

        processBuilder = new ProcessBuilder(secondProcess);
        process = processBuilder.start();
        processes.add(process);

        // give process time to start
        pause(1000);

        PsCommand command = new PsCommand();

        // test all match
        JSONObject result = command._ps("sampleProcess", null);
        JSONArray processes = (JSONArray)result.get("Processes");
        System.out.println( processes.toString() );

        Assert.assertTrue("Returned length of " + processes.length(), processes.length() == 1);

        JSONObject resultProcess = (JSONObject)processes.get(0);
        Assert.assertTrue("running should be true", resultProcess.get("running").equals(true));
        Assert.assertTrue("type should be H2", resultProcess.get("type").equals("Module"));
        Assert.assertTrue("operation should be ps", resultProcess.get("operation").equals("ps"));
        Assert.assertNotNull("username should be not null", resultProcess.get("username"));
        Assert.assertNotNull("pid should be not null", resultProcess.get("pid"));
    }

    @Test
    void successful_ps_no_match() throws IOException, JSONException
    {
        List<String> firstProcess = new ArrayList<String>();
        firstProcess.add("java");
        firstProcess.add("-classpath");
        firstProcess.add("cli/shell/target/test-classes:target/test-classes");
        firstProcess.add("org.ikasan.cli.sample.process.SampleProcess");
        firstProcess.add("-Dmodule.name=sampleProcess");

        ProcessBuilder processBuilder = new ProcessBuilder(firstProcess);
        Process process = processBuilder.start();
        processes.add(process);

        List<String> secondProcess = new ArrayList<String>();
        secondProcess.add("java");
        secondProcess.add("-classpath");
        secondProcess.add("cli/shell/target/test-classes:target/test-classes");
        secondProcess.add("org.ikasan.cli.sample.process.SampleProcess");
        secondProcess.add("-Dmodule.name=sampleProcess");

        processBuilder = new ProcessBuilder(secondProcess);
        process = processBuilder.start();
        processes.add(process);

        // give process time to start
        pause(1000);

        PsCommand command = new PsCommand();

        // test all match
        JSONObject result = command._ps("sampleProcess", null);
        JSONArray processes = (JSONArray)result.get("Processes");
        Assert.assertTrue(processes.length() == 0);
    }

    @AfterEach
    void teardown() throws IOException
    {
        for(Process process:processes)
        {
            if(process.isAlive())
            {
                process.destroyForcibly();
            }
        }

        FileUtils.deleteDirectory(new File("./pid"));
    }

    void pause(long millis)
    {
        try
        {
            Thread.sleep(millis);
        }
        catch(InterruptedException e)
        {
            Assert.fail("Sleep interrupted" + e.getMessage());
        }
    }
}

